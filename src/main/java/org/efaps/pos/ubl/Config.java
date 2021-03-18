/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl;

import org.efaps.pos.interfaces.IInvoiceListener;
import org.efaps.pos.interfaces.IReceiptListener;
import org.efaps.pos.listener.IPrintListener;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
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
@EnableMongoRepositories("org.efaps.pos.ubl")
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

    @Bean(name = { "UBL-PrintListener" })
    public IPrintListener printListener()
    {
        return new PrintListener(eInvoiceRepository);
    }

}
