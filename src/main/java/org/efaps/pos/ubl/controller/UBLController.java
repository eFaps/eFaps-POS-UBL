/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl.controller;

import org.efaps.pos.config.IApi;
import org.efaps.pos.entity.Config;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.ubl.service.InvoiceListener;
import org.efaps.pos.ubl.service.ReceiptListener;
import org.efaps.pos.util.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IApi.BASEPATH + "ubl")
public class UBLController
{

    private final MongoTemplate mongoTemplate;
    private final DocumentService documentService;
    private final InvoiceListener invoiceListener;
    private final ReceiptListener receiptListener;

    public UBLController(final MongoTemplate _mongoTemplate,
                         final DocumentService _documentService,
                         final InvoiceListener _invoiceListener,
                         final ReceiptListener _receiptListener)
    {
        mongoTemplate = _mongoTemplate;
        documentService = _documentService;
        invoiceListener = _invoiceListener;
        receiptListener = _receiptListener;
    }

    @GetMapping(path = "/invoice/{id}")
    public void getInvoice(@PathVariable("id") final String _id)
    {
        final var invoice = documentService.getInvoice(_id);
        final var posInvoice = Converter.toDto(invoice);
        final Config config = mongoTemplate.findById(Config.KEY, Config.class);
        invoiceListener.onCreate(null, posInvoice, config.getProperties());
    }

    @GetMapping(path = "/receipt/{id}")
    public void getReceipt(@PathVariable("id") final String _id)
    {
        final var receipt = documentService.getReceipt(_id);
        final var posReceipt = Converter.toDto(receipt);
        final Config config = mongoTemplate.findById(Config.KEY, Config.class);
        receiptListener.onCreate(null, posReceipt, config.getProperties());
    }

    @PostMapping(path = "/sign", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String sign(@RequestBody final String ublXml)
    {
        return receiptListener.sign(ublXml).getUbl();
    }
}
