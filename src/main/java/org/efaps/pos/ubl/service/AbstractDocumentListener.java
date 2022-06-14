/*
 * Copyright 2003 - 2021 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.efaps.pos.ubl.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.efaps.pos.dto.AbstractDocumentDto;
import org.efaps.pos.dto.ContactDto;
import org.efaps.pos.dto.PosDocItemDto;
import org.efaps.pos.dto.TaxEntryDto;
import org.efaps.pos.interfaces.IDocument;
import org.efaps.pos.interfaces.IInvoice;
import org.efaps.pos.interfaces.IItem;
import org.efaps.pos.ubl.ConfigProps;
import org.efaps.pos.ubl.entities.AllowanceEntry;
import org.efaps.pos.ubl.entities.ChargeEntry;
import org.efaps.pos.ubl.entities.EInvoice;
import org.efaps.pos.ubl.entities.TaxEntry;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
import org.efaps.ubl.Signing;
import org.efaps.ubl.documents.AbstractDocument;
import org.efaps.ubl.documents.Customer;
import org.efaps.ubl.documents.IAllowanceChargeEntry;
import org.efaps.ubl.documents.ICustomer;
import org.efaps.ubl.documents.IInstallment;
import org.efaps.ubl.documents.ILine;
import org.efaps.ubl.documents.IPaymentTerms;
import org.efaps.ubl.documents.ITaxEntry;
import org.efaps.ubl.documents.Line;
import org.efaps.ubl.documents.Supplier;
import org.efaps.ubl.dto.SignResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDocumentListener
{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDocumentListener.class);

    private final ConfigProps configProps;
    private final EInvoiceRepository eInvoiceRepository;

    public AbstractDocumentListener(final ConfigProps configProps,
                                    final EInvoiceRepository eInvoiceRepository)
    {
        this.configProps = configProps;
        this.eInvoiceRepository = eInvoiceRepository;
    }

    public ConfigProps getConfigProps()
    {
        return configProps;
    }

    protected AbstractDocument<?> fill(final IDocument _document, final Set<? extends IItem> items,
                                       final AbstractDocument<?> ubl,
                                       final Map<String, String> _properties)
    {
        final var allowancesCharges = getCharges(_document.getTaxes(), false, _properties);
        allowancesCharges.addAll(getAllowances(items));
        ubl.withNumber(_document.getNumber())
                        .withCurrency(_document.getCurrency().name())
                        .withDate(_document.getDate())
                        .withCrossTotal(_document.getCrossTotal())
                        .withNetTotal(_document.getNetTotal())
                        .withCustomer(getCustomer(_document.getContact()))
                        .withSupplier(getSupplier(_properties))
                        .withTaxes(getTaxes(_document.getTaxes(), _properties))
                        .withAllowancesCharges(allowancesCharges)
                        .withLines(getLines(items, _properties))
                        .withPaymentTerms(new IPaymentTerms()
                        {

                            @Override
                            public boolean isCredit()
                            {
                                return false;
                            }

                            @Override
                            public BigDecimal getTotal()
                            {
                                return _document.getCrossTotal();
                            }

                            @Override
                            public List<IInstallment> getInstallments()
                            {
                                return null;
                            }
                        });
        return ubl;
    }

    // discounts are added as a line --> convert that into a global discount
    protected List<IAllowanceChargeEntry> getAllowances(final Set<? extends IItem> items)
    {
        final var ret = new ArrayList<IAllowanceChargeEntry>();
        var total = BigDecimal.ZERO;
        var discount = BigDecimal.ZERO;
        for (final var item : items) {
            if (item.getCrossPrice().compareTo(BigDecimal.ZERO) > 0) {
                total = total.add(item.getNetPrice());
            } else {
                discount = discount.add(item.getNetPrice().abs());
            }
        }
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            ret.add(AllowanceEntry.builder()
                            .withAmount(discount)
                            .withBaseAmount(total)
                            // Catalogo 53
                            // Descuentos globales que afectan la base imponible
                            // del IGV/IVAP
                            .withReason("02")
                            .withFactor(discount.divide(total, RoundingMode.HALF_UP))
                            .build());
        }
        return ret;
    }

    protected List<ILine> getLines(final Set<? extends IItem> items, final Map<String, String> _properties)
    {
        final var ret = new ArrayList<ILine>();

        final var docItems = items.stream()
                        .map(item -> {
                            return (PosDocItemDto) item;
                        }).sorted(Comparator.comparingInt(PosDocItemDto::getIndex))
                        .collect(Collectors.toList());

        for (final var item : docItems) {
            if (item.getCrossPrice().compareTo(BigDecimal.ZERO) > 0) {
                ret.add(Line.builder()
                                .withQuantity(item.getQuantity())
                                .withSku(item.getSku())
                                .withDescription(item.getDescription())
                                .withNetUnitPrice(item.getNetUnitPrice())
                                .withCrossUnitPrice(item.getCrossUnitPrice())
                                .withNetPrice(item.getNetPrice())
                                .withCrossPrice(item.getCrossPrice())
                                .withUoMCode(item.getUoMCode())
                                .withTaxEntries(getTaxes(item.getTaxes(), _properties))
                                .withAllowancesCharges(getCharges(item.getTaxes(), true, _properties))
                                .build());
            }
        }
        return ret;
    }

    protected List<ITaxEntry> getTaxes(final Set<TaxEntryDto> taxes, final Map<String, String> _properties)
    {
        final var ret = new ArrayList<ITaxEntry>();
        for (final var entry : taxes) {
            final var taxKey = entry.getTax().getKey();
            if (_properties.containsKey("tax." + taxKey + ".id")) {
                final var code = _properties.get("tax." + taxKey + ".id");
                final var name = _properties.get("tax." + taxKey + ".nombre");
                final var id = _properties.get("tax." + taxKey + ".sunat-id");
                final var taxExemptionReasonCode = _properties.get("tax." + taxKey + ".afectacion-igv");
                org.efaps.ubl.documents.TaxType taxType;
                switch (entry.getTax().getType()) {
                    case PERUNIT:
                        taxType = org.efaps.ubl.documents.TaxType.PERUNIT;
                        break;
                    case ADVALOREM:
                    default:
                        taxType = org.efaps.ubl.documents.TaxType.ADVALOREM;
                        break;
                }
                ret.add(TaxEntry.builder()
                                .withTaxType(taxType)
                                .withTaxExemptionReasonCode(taxExemptionReasonCode)
                                .withAmount(entry.getAmount())
                                .withTaxableAmount(entry.getBase())
                                .withPercent(entry.getTax().getPercent())
                                .withName(name)
                                .withCode(code)
                                .withId(id)
                                .build());
            }
        }
        return ret;
    }

    protected List<IAllowanceChargeEntry> getCharges(final Set<TaxEntryDto> taxes,
                                                     final boolean isItem,
                                                     final Map<String, String> _properties)
    {
        final var ret = new ArrayList<IAllowanceChargeEntry>();
        for (final var entry : taxes) {
            final var taxKey = entry.getTax().getKey();
            if (_properties.containsKey("charge." + taxKey + ".id")) {
                final var id = _properties.get("charge." + taxKey + ".id");
                final var isGlobal = "true".equalsIgnoreCase(_properties.get("charge." + taxKey + ".global"));
                if (!(isGlobal && isItem)) {
                    ret.add(ChargeEntry.builder()
                                    .withAmount(entry.getAmount())
                                    .withBaseAmount(entry.getBase())
                                    .withReason(id)
                                    .withFactor(entry.getTax().getPercent()
                                                    .setScale(2).divide(new BigDecimal(100), RoundingMode.HALF_UP))
                                    .build());
                }
            }
        }
        return ret;
    }

    protected Supplier getSupplier(final Map<String, String> _properties)
    {
        final var ret = new Supplier();
        ret.setDoiType("6");
        ret.setDOI(getProperty(_properties, "TaxNumber"));
        ret.setName(getProperty(_properties, "Name"));
        ret.setStreetName(getProperty(_properties, "Street"));
        ret.setUbigeo(getProperty(_properties, "Ubigeo"));
        ret.setCountry(getProperty(_properties, "Country"));
        ret.setAnexo(getProperty(_properties, "Establecimiento"));
        ret.setDistrict(getProperty(_properties, "District"));
        return ret;
    }

    protected ICustomer getCustomer(final ContactDto _contactDto)
    {
        String doiType;
        switch (_contactDto.getIdType()) {
            case RUC:
                doiType = "6";
                break;
            case DNI:
                doiType = "1";
                break;
            case CE:
                doiType = "4";
                break;
            case PASSPORT:
                doiType = "7";
                break;
            default:
                doiType = "-";
        }

        final Customer ret = new Customer();
        ret.setDOI(StringUtils.isEmpty(_contactDto.getIdNumber())
                        ? "0"
                        : _contactDto.getIdNumber());
        ret.setDoiType(doiType);
        ret.setName(_contactDto.getName());
        return ret;
    }

    protected String getProperty(final Map<String, String> _properties, final String _key)
    {
        String ret;
        switch (_key) {
            case "Establecimiento":
                ret = configProps.getEstablecimiento();
                break;
            default:
                ret = null;
                break;
        }
        if (StringUtils.isEmpty(ret)) {
            ret = _properties.get("org.efaps.commons.Company" + _key);
        }
        return ret;
    }

    public SignResponseDto sign(final String ublXml)
    {
        return new Signing().withKeyStorePath(configProps.getCertificate().getKeyStorePath())
                        .withKeyAlias(configProps.getCertificate().getKeyAlias())
                        .withKeyStorePwd(configProps.getCertificate().getKeyStorePwd())
                        .withKeyPwd(configProps.getCertificate().getKeyPwd())
                        .signDocument(ublXml);
    }

    protected void store(final IDocument _document, final SignResponseDto signResponse,
                         final Map<String, String> _properties)
    {
        if (configProps.getOutputFolder() != null) {
            final var taxnumber = getProperty(_properties, "TaxNumber");
            final var type = _document instanceof IInvoice ? "01" : "03";
            final var fileName = taxnumber + "-" + type + "-" + _document.getNumber() + ".xml";
            try {
                FileUtils.writeStringToFile(new File(configProps.getOutputFolder().toString(), fileName),
                                signResponse.getUbl(), StandardCharsets.UTF_8);
            } catch (final IOException e) {
                LOG.error("Catched", e);
            }
        }
        final var docId = ((AbstractDocumentDto) _document).getId();
        final var eInvoice = new EInvoice()
                        .setDocId(docId)
                        .setUbl(signResponse.getUbl())
                        .setHash(signResponse.getHash());
        eInvoiceRepository.save(eInvoice);
    }
}
