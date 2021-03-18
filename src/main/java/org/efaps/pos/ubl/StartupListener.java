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

import org.efaps.ubl.Signing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component("UBL-StartupListener")
@PropertySource(value = "classpath:ubl.properties")
public class StartupListener
    implements ApplicationListener<ApplicationReadyEvent>
{
    @Value("${ubl.version}")
    private String version;

    private final ConfigProps configProps;

    public StartupListener(final ConfigProps configProps)
    {
        this.configProps = configProps;
    }

    private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event)
    {
        LOG.info("UBL-Integration '{}' found", version);
        if (new Signing().withKeyStorePath(configProps.getCertificate().getKeyStorePath())
                        .withKeyAlias(configProps.getCertificate().getKeyAlias())
                        .withKeyStorePwd(configProps.getCertificate().getKeyStorePwd())
                        .withKeyPwd(configProps.getCertificate().getKeyPwd())
                        .verify()) {
            LOG.info("Certificate verified");
        } else {
            LOG.warn("Problems with the Certificate");
        }
    }
}
