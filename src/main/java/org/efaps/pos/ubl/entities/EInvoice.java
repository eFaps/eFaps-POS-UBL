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
