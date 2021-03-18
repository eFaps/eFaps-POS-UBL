/*
 * Copyright 2020 Jan Moxter
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * Jan Moxter. Copyright notice is precautionary only and does not
 * evidence any actual or intended publication of such program.
 */
package org.efaps.pos.ubl.repository;

import java.util.Collection;
import java.util.Optional;

import org.efaps.pos.ubl.entities.EInvoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EInvoiceRepository
    extends MongoRepository<EInvoice, String>
{
    Optional<EInvoice> findByDocId(String _docId);

    Collection<EInvoice> findByOidIsNull();
}
