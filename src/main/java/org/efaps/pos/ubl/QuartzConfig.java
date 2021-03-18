/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl;

import org.efaps.pos.ubl.service.SyncService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration("UBL-QuartzConfig")
public class QuartzConfig
{

    @Value("${org.quartz.jobs.syncEInvoices.interval:300}")
    private Integer syncEInvoice;

    private final SyncService syncService;

    public QuartzConfig(final SyncService syncService)
    {
        this.syncService = syncService;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncEInvoices.interval:300} > 0}")
    public MethodInvokingJobDetailFactoryBean syncEInvoicesFactoryBean()
    {
        final MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetObject(syncService);
        obj.setTargetMethod("runSyncJob");
        return obj;
    }

    @Bean
    @ConditionalOnExpression(value = "#{${org.quartz.jobs.syncEInvoices.interval:300} > 0}")
    public SimpleTriggerFactoryBean syncEInvoicesTriggerFactoryBean()
        throws SchedulerException
    {
        final SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(syncEInvoicesFactoryBean().getObject());
        stFactory.setStartDelay(180 * 1000);
        stFactory.setRepeatInterval(Math.abs(syncEInvoice) * 1000);
        stFactory.afterPropertiesSet();
        return stFactory;
    }
}
