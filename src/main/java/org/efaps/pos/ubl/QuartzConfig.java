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
        obj.setConcurrent(false);
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
