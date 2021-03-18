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

import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IPos;
import org.efaps.pos.interfaces.IReceipt;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.ubl.ConfigProps;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
import org.efaps.ubl.documents.Receipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiptListener
    extends AbstractDocumentListener
    implements IReceiptListener
{
    private static final Logger LOG = LoggerFactory.getLogger(IInvoiceListener.class);

    public ReceiptListener(final ConfigProps configProps,
                           final EInvoiceRepository eInvoiceRepository)
    {
        super(configProps, eInvoiceRepository);
    }

    @Override
    public IReceipt onCreate(final IPos _pos, final IReceipt _receipt, final Map<String, String> _properties)
    {
        final var ublReceipt = new Receipt();
        final var ublXml = getUBL(_receipt, _receipt.getReceiptItems(), ublReceipt, _properties);
        LOG.info("UBL: {}", ublXml);
        final var signResponse = sign(ublXml);
        store(_receipt, signResponse, _properties);
        return _receipt;
    }

}
