/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ubl")
public class EInvoice
{

    @Id
    private String id;

    private String oid;

    private String ubl;

    private String hash;

    private String docId;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getOid()
    {
        return oid;
    }

    public void setOid(final String oid)
    {
        this.oid = oid;
    }

    public String getUbl()
    {
        return ubl;
    }

    public EInvoice setUbl(final String ubl)
    {
        this.ubl = ubl;
        return this;
    }

    public String getHash()
    {
        return hash;
    }

    public EInvoice setHash(final String hash)
    {
        this.hash = hash;
        return this;
    }

    public String getDocId()
    {
        return docId;
    }

    public EInvoice setDocId(final String docId)
    {
        this.docId = docId;
        return this;
    }
}
