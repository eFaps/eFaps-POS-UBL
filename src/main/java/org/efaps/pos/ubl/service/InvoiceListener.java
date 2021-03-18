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
