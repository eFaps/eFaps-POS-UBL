/*
 * Copyright 2003 - 2022 The eFaps Team
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
package org.efaps.pos.ubl.controller;

import org.efaps.pos.config.IApi;
import org.efaps.pos.entity.Config;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.ubl.service.CreditNoteListener;
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
    private final CreditNoteListener creditNoteListener;

    public UBLController(final MongoTemplate _mongoTemplate,
                         final DocumentService _documentService,
                         final InvoiceListener _invoiceListener,
                         final ReceiptListener _receiptListener,
                         final CreditNoteListener _creditNoteListener)
    {
        mongoTemplate = _mongoTemplate;
        documentService = _documentService;
        invoiceListener = _invoiceListener;
        receiptListener = _receiptListener;
        creditNoteListener = _creditNoteListener;
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

    @GetMapping(path = "/creditnote/{id}")
    public void getCreditNote(@PathVariable("id") final String _id)
    {
        final var creditNote = documentService.getCreditNote(_id);
        final var posCreditNote = Converter.toDto(creditNote);
        final Config config = mongoTemplate.findById(Config.KEY, Config.class);
        creditNoteListener.onCreate(null, posCreditNote, config.getProperties());
    }

    @PostMapping(path = "/sign", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String sign(@RequestBody final String ublXml)
    {
        return receiptListener.sign(ublXml).getUbl();
    }
}
