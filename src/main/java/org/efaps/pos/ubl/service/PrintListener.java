/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
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
 */
package org.efaps.pos.ubl.service;

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
    public void addAdditionalInfo2Document(final AbstractDocument<?> _document2print,
                                           final Map<String, Object> additionalInfo)
    {
        if (_document2print instanceof Invoice || _document2print instanceof Receipt) {
            final Optional<EInvoice> eInvoice = eInvoiceRepository.findByDocId(_document2print.getId());
            if (eInvoice.isPresent()) {
                additionalInfo.put("EInvoice-Hash", eInvoice.get().getHash());
            }
        }
    }
}
