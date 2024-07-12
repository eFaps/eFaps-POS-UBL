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
package org.efaps.pos.ubl.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = EInvoiceDto.Builder.class)
public class EInvoiceDto
{

    private final String ubl;

    private final String hash;

    private final String docOid;

    private EInvoiceDto(final Builder builder)
    {
        ubl = builder.ubl;
        hash = builder.hash;
        docOid = builder.docOid;
    }

    public String getUbl()
    {
        return ubl;
    }

    public String getHash()
    {
        return hash;
    }

    public String getDocOid()
    {
        return docOid;
    }

    /**
     * Creates builder to build {@link EInvoiceDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link EInvoiceDto}.
     */
    public static final class Builder
    {

        private String ubl;
        private String hash;
        private String docOid;

        private Builder()
        {
        }

        public Builder withUbl(final String ubl)
        {
            this.ubl = ubl;
            return this;
        }

        public Builder withHash(final String hash)
        {
            this.hash = hash;
            return this;
        }

        public Builder withDocOid(final String docOid)
        {
            this.docOid = docOid;
            return this;
        }

        public EInvoiceDto build()
        {
            return new EInvoiceDto(this);
        }
    }

}
