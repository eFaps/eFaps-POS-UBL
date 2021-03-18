/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
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
