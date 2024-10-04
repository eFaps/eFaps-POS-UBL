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
package org.efaps.pos.ubl.service;

import java.util.Optional;

import org.efaps.pos.client.EFapsClient;
import org.efaps.pos.config.ConfigProperties;
import org.efaps.pos.config.ConfigProperties.Company;
import org.efaps.pos.context.Context;
import org.efaps.pos.service.DocumentService;
import org.efaps.pos.ubl.ConfigProps;
import org.efaps.pos.ubl.dtos.EInvoiceDto;
import org.efaps.pos.ubl.entities.EInvoice;
import org.efaps.pos.ubl.repository.EInvoiceRepository;
import org.efaps.pos.util.IdentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service("UBL-SyncService")
public class SyncService
{

    private static final Logger LOG = LoggerFactory.getLogger(SyncService.class);

    private final ConfigProps localConfigProps;
    private final ConfigProperties configProperties;
    private final EInvoiceRepository eInvoiceRepository;
    private final EFapsClient eFapsClient;
    private final DocumentService documentService;

    public SyncService(final ConfigProps localConfigProps,
                       final ConfigProperties configProperties,
                       final EInvoiceRepository eInvoiceRepository,
                       final EFapsClient eFapsClient,
                       final DocumentService documentService)
    {
        this.localConfigProps = localConfigProps;
        this.configProperties = configProperties;
        this.eInvoiceRepository = eInvoiceRepository;
        this.eFapsClient = eFapsClient;
        this.documentService = documentService;
    }

    public void runSyncJob()
    {
        if (configProperties.getCompanies().size() > 0) {
            for (final Company company : configProperties.getCompanies()) {
                Context.get().setCompany(company);
                MDC.put("company", company.getTenant());
                sync();
            }
        } else {
            sync();
        }
    }

    private void sync()
    {
        LOG.info("Syncing UBL-EInvoices");
        for (final var eInvoice : eInvoiceRepository.findByOidIsNull()) {
            try {
                final var docOidOpt = getDocOid(eInvoice);
                if (docOidOpt.isPresent()) {
                    final var dto = EInvoiceDto.builder()
                                    .withDocOid(docOidOpt.get())
                                    .withHash(eInvoice.getHash())
                                    .withUbl(eInvoice.getUbl())
                                    .build();
                    final RequestEntity<EInvoiceDto> requestEntity = eFapsClient
                                    .post(localConfigProps.getUblPath(), dto);
                    final ResponseEntity<String> response = eFapsClient.getRestTemplate().exchange(requestEntity,
                                    String.class);
                    final var oid = response.getBody();
                    if (oid != null) {
                        eInvoice.setOid(oid);
                        eInvoiceRepository.save(eInvoice);
                    }
                }
            } catch (final RestClientException | IdentException e) {
                LOG.error("Catched error during post for UBL-EInvoices", e);
            }
        }
    }

    private Optional<String> getDocOid(final EInvoice eInvoice)
    {
        final var doc = documentService.getDocument(eInvoice.getDocId());
        return doc == null ? Optional.empty() : Optional.ofNullable(doc.getOid());
    }
}
