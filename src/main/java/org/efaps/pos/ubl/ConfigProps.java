/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ubl")
public class ConfigProps
{

    private final Certificate certificate = new Certificate();

    private Path outputFolder;

    private String ublPath;

    public Certificate getCertificate()
    {
        return certificate;
    }

    public Path getOutputFolder()
    {
        return outputFolder;
    }

    public void setOutputFolder(final Path outputFolder)
    {
        this.outputFolder = outputFolder;
    }

    public String getUblPath()
    {
        return ublPath;
    }

    public void setUblPath(final String ublPath)
    {
        this.ublPath = ublPath;
    }

    public static class Certificate
    {

        private String keyStorePath;
        private String keyStorePwd;
        private String keyAlias;
        private String keyPwd;

        public String getKeyStorePath()
        {
            return keyStorePath;
        }

        public void setKeyStorePath(final String keyStorePath)
        {
            this.keyStorePath = keyStorePath;
        }

        public String getKeyAlias()
        {
            return keyAlias;
        }

        public void setKeyAlias(final String keyAlias)
        {
            this.keyAlias = keyAlias;
        }

        public String getKeyStorePwd()
        {
            return keyStorePwd;
        }

        public void setKeyStorePwd(final String keyStorePwd)
        {
            this.keyStorePwd = keyStorePwd;
        }

        public String getKeyPwd()
        {
            return keyPwd;
        }

        public void setKeyPwd(final String keyPwd)
        {
            this.keyPwd = keyPwd;
        }
    }
}
