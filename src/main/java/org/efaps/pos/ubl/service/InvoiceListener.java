/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl.service;

import java.util.Map;

import org.efaps.pos.interfaces.IInvoice;
import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IPos;
import org.efaps.pos.ubl.ConfigProps;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
import org.efaps.ubl.documents.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoiceListener
    extends AbstractDocumentListener
    implements IInvoiceListener
{
    private static final Logger LOG = LoggerFactory.getLogger(IInvoiceListener.class);

    public InvoiceListener(final ConfigProps configProps,
                           final EInvoiceRepository eInvoiceRepository)
    {
        super(configProps, eInvoiceRepository);
    }

    @Override
    public IInvoice onCreate(final IPos _pos, final IInvoice _invoice, final Map<String, String> _properties)
    {
        final var ublInvoice = new Invoice();
        final var ublXml = getUBL(_invoice, _invoice.getInvoiceItems(), ublInvoice, _properties);
        LOG.info("UBL: {}", ublXml);
        final var signResponse = sign(ublXml);
        store(_invoice, signResponse, _properties);
        return _invoice;
    }
}
