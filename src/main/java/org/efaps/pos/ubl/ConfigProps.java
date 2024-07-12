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
package org.efaps.pos.ubl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ubl")
public class ConfigProps
{

    private final Certificate certificate = new Certificate();

    private Path outputFolder;

    private String ublPath;

    private String establecimiento;

    private Charset encoding = StandardCharsets.UTF_8;


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

    public String getEstablecimiento()
    {
        return establecimiento;
    }


    public void setEstablecimiento(final String establecimiento)
    {
        this.establecimiento = establecimiento;
    }

    public void setEncoding(final String encoding)
    {
        this.encoding = Charset.forName(encoding);
    }

    public Charset getEncoding()
    {
        return encoding;
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
