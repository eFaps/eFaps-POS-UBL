/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
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
