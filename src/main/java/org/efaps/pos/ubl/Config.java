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
package org.efaps.pos.ubl;

import org.efaps.pos.interfaces.ICreditNoteListener;
import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.listener.IPrintListener;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
import org.efaps.pos.ubl.service.CreditNoteListener;
import org.efaps.pos.ubl.service.InvoiceListener;
import org.efaps.pos.ubl.service.PrintListener;
import org.efaps.pos.ubl.service.ReceiptListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration("UBL-Config")
@EnableConfigurationProperties(value = { ConfigProps.class })
@ComponentScan("org.efaps.pos.ubl")
@EnableMongoRepositories("org.efaps.pos.ubl.repository")
public class Config
{

    private final ConfigProps configProps;
    private final EInvoiceRepository eInvoiceRepository;

    public Config(final ConfigProps configProps,
                  final EInvoiceRepository eInvoiceRepository)
    {
        this.configProps = configProps;
        this.eInvoiceRepository = eInvoiceRepository;
    }

    @Bean(name = { "UBL-InvoiceListener" })
    public IInvoiceListener invoiceListener()
    {
        return new InvoiceListener(configProps, eInvoiceRepository);
    }

    @Bean(name = { "UBL-ReceiptListener" })
    public IReceiptListener receiptListener()
    {
        return new ReceiptListener(configProps, eInvoiceRepository);
    }

    @Bean(name = { "UBL-CreditNoteListener" })
    public ICreditNoteListener creditNoteListener()
    {
        return new CreditNoteListener(configProps, eInvoiceRepository);
    }


    @Bean(name = { "UBL-PrintListener" })
    public IPrintListener printListener()
    {
        return new PrintListener(eInvoiceRepository);
    }

}
