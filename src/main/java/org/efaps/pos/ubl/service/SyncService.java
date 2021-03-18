/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl.service;

import java.util.Optional;

import org.efaps.pos.ConfigProperties;
import org.efaps.pos.ConfigProperties.Company;
import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.context.Context;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.ubl.ConfigProps;
import org.efaps.pos.ubl.dtos.EInvoiceDto;
import org.efaps.pos.ubl.entities.EInvoice;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
import org.efaps.pos.util.IdentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service("UBL-SyncService")
public class SyncService
{

    private static final Logger LOG = LoggerFactory.getLogger(SyncService.class);

    private final ConfigProps localConfigProps;
    private final ConfigProperties configProperties;
    private final EInvoiceRepository eInvoiceRepository;
    private final EFapsClient eFapsClient;
    private final DocumentService documentService;

    public SyncService(final ConfigProps localConfigProps,
                       final ConfigProperties configProperties,
                       final EInvoiceRepository eInvoiceRepository,
                       final EFapsClient eFapsClient,
                       final DocumentService documentService)
    {
        this.localConfigProps = localConfigProps;
        this.configProperties = configProperties;
        this.eInvoiceRepository = eInvoiceRepository;
        this.eFapsClient = eFapsClient;
        this.documentService = documentService;
    }

    public void runSyncJob()
    {
        if (configProperties.getCompanies().size() > 0) {
            for (final Company company : configProperties.getCompanies()) {
                Context.get().setCompany(company);
                MDC.put("company", company.getTenant());
                sync();
            }
        } else {
            sync();
        }
    }

    private void sync()
    {
        LOG.info("Syncing Bizlinks-EInvoices");
        for (final var eInvoice : eInvoiceRepository.findByOidIsNull()) {
            try {
                final var docOidOpt = getDocOid(eInvoice);
                if (docOidOpt.isPresent()) {
                    final var dto = EInvoiceDto.builder()
                                    .withDocOid(docOidOpt.get())
                                    .withHash(eInvoice.getHash())
                                    .withUbl(eInvoice.getUbl())
                                    .build();
                    final RequestEntity<EInvoiceDto> requestEntity = eFapsClient
                                    .post(localConfigProps.getUblPath(), dto);
                    final ResponseEntity<String> response = eFapsClient.getRestTemplate().exchange(requestEntity,
                                    String.class);
                    final var oid = response.getBody();
                    if (oid != null) {
                        eInvoice.setOid(oid);
                        eInvoiceRepository.save(eInvoice);
                    }
                }
            } catch (final RestClientException | IdentException e) {
                LOG.error("Catched error during post for Bizlinks-EInvoices", e);
            }
        }
    }

    private Optional<String> getDocOid(final EInvoice eInvoice)
    {
        final var doc = documentService.getDocument(eInvoice.getDocId());
        return doc == null ? Optional.empty() : Optional.ofNullable(doc.getOid());
    }
}
