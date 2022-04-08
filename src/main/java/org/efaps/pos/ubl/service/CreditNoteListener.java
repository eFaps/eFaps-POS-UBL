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
package org.efaps.pos.ubl.service;

import java.util.Map;

import org.efaps.pos.dto.DocType;
import org.efaps.pos.interfaces.ICreditNote;
import org.efaps.pos.interfaces.ICreditNoteListener;
import org.efaps.pos.interfaces.IPos;
import org.efaps.pos.ubl.ConfigProps;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
import org.efaps.ubl.documents.CreditNote;
import org.efaps.ubl.documents.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreditNoteListener
    extends AbstractDocumentListener
    implements ICreditNoteListener
{

    private static final Logger LOG = LoggerFactory.getLogger(CreditNoteListener.class);

    public CreditNoteListener(final ConfigProps configProps,
                              final EInvoiceRepository eInvoiceRepository)
    {
        super(configProps, eInvoiceRepository);
    }

    @Override
    public ICreditNote onCreate(final IPos _pos, final ICreditNote creditNote, final Map<String, String> _properties)
    {
        final var ublCreditNote = new CreditNote();
        ublCreditNote.withReference(new Reference()
                        .setNumber(creditNote.getReference().getNumber())
                        .setDate(creditNote.getReference().getDate())
                        .setDocType(DocType.INVOICE.equals(creditNote.getReference().getDocType()) ? "01" : "03"));
        final var ublXml = getUBL(creditNote, creditNote.getCreditNoteItems(), ublCreditNote, _properties);
        LOG.info("UBL: {}", ublXml);
        final var signResponse = sign(ublXml);
        store(creditNote, signResponse, _properties);
        return creditNote;
    }
}
