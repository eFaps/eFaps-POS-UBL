/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.efaps.pos.entity.AbstractDocument;
import org.efaps.pos.entity.Invoice;
import org.efaps.pos.entity.Receipt;
import org.efaps.pos.listener.IPrintListener;
import org.efaps.pos.ubl.entities.EInvoice;
import org.efaps.pos.ubl.repository.EInvoiceRepository;

public class PrintListener
    implements IPrintListener
{

    private final EInvoiceRepository eInvoiceRepository;

    public PrintListener(final EInvoiceRepository eInvoiceRepository)
    {
        this.eInvoiceRepository = eInvoiceRepository;
    }

    @Override
    public Map<String, Object> getAdditionalInfo(final AbstractDocument<?> _document2print)
    {
        final var ret = new HashMap<String, Object>();
        if (_document2print instanceof Invoice || _document2print instanceof Receipt) {
            final Optional<EInvoice> eInvoice = eInvoiceRepository.findByDocId(_document2print.getId());
            if (eInvoice.isPresent()) {
                ret.put("EInvoice-Hash", eInvoice.get().getHash());
            }
        }
        return ret;
    }

}
