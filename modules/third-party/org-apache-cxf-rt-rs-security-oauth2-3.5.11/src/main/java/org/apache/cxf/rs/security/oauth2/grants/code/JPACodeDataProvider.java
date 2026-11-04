/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.rs.security.oauth2.grants.code;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.JPAOAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;

public class JPACodeDataProvider extends JPAOAuthDataProvider implements AuthorizationCodeDataProvider {
    private long codeLifetime = 10 * 60;

    @Override
    public ServerAuthorizationCodeGrant createCodeGrant(AuthorizationCodeRegistration reg)
            throws OAuthServiceException {
        ServerAuthorizationCodeGrant grant = doCreateCodeGrant(reg);
        saveCodeGrant(grant);
        return grant;
    }

    protected ServerAuthorizationCodeGrant doCreateCodeGrant(AuthorizationCodeRegistration reg)
            throws OAuthServiceException {
        return AbstractCodeDataProvider.initCodeGrant(reg, codeLifetime);
    }

    protected void saveCodeGrant(final ServerAuthorizationCodeGrant grant) {
        executeInTransaction(em -> {
            if (grant.getSubject() != null) {
                UserSubject sub = em.find(UserSubject.class, grant.getSubject().getId());
                if (sub == null) {
                    em.persist(grant.getSubject());
                } else {
                    sub = em.merge(grant.getSubject());
                    grant.setSubject(sub);
                }
            }
            // ensure we have a managed association
            // (needed for OpenJPA : InvalidStateException: Encountered unmanaged object)
            if (grant.getClient() != null) {
                grant.setClient(em.find(Client.class, grant.getClient().getClientId()));
            }
            em.persist(grant);
            return null;
        });
    }

    @Override
    protected void doRemoveClient(final Client c) {
        executeInTransaction(em -> {
            removeClientCodeGrants(c, em);
            Client clientToRemove = em.getReference(Client.class, c.getClientId());
            em.remove(clientToRemove);
            return null;
        });
    }

    protected void removeClientCodeGrants(final Client c) {
        executeInTransaction(em -> {
            removeClientCodeGrants(c, em);
            return null;
        });
    }

    protected void removeClientCodeGrants(final Client c, EntityManager em) {
        for (ServerAuthorizationCodeGrant grant : getCodeGrants(c, null, em)) {
            removeCodeGrant(grant.getCode(), em);
        }
    }

    @Override
    public ServerAuthorizationCodeGrant removeCodeGrant(final String code) throws OAuthServiceException {
        return executeInTransaction(em -> {
            return removeCodeGrant(code, em);
        });
    }

    private ServerAuthorizationCodeGrant removeCodeGrant(String code, EntityManager em) throws OAuthServiceException {
        return removeCodeGrant(code, em, LockModeType.PESSIMISTIC_WRITE);
    }

    protected ServerAuthorizationCodeGrant removeCodeGrant(String code, EntityManager em,
                                                           LockModeType lockModeType) throws OAuthServiceException {
        ServerAuthorizationCodeGrant grant = em.find(ServerAuthorizationCodeGrant.class, code, lockModeType);
        try {
            if (grant != null) {
                em.remove(grant);
            }
        } catch (EntityNotFoundException e) {
        }
        return grant;
    }

    @Override
    public List<ServerAuthorizationCodeGrant> getCodeGrants(final Client c, final UserSubject subject)
            throws OAuthServiceException {
        return execute(em -> {
            return getCodeGrants(c, subject, em);
        });
    }

    private List<ServerAuthorizationCodeGrant> getCodeGrants(final Client c, final UserSubject subject,
                                                             EntityManager em)
            throws OAuthServiceException {
        return getCodesQuery(c, subject, em).getResultList();
    }

    public void setCodeLifetime(long codeLifetime) {
        this.codeLifetime = codeLifetime;
    }

    protected TypedQuery<ServerAuthorizationCodeGrant> getCodesQuery(Client c, UserSubject resourceOwnerSubject,
                                                                     EntityManager em) {
        if (c == null && resourceOwnerSubject == null) {
            return em.createQuery("SELECT c FROM ServerAuthorizationCodeGrant c",
                    ServerAuthorizationCodeGrant.class);
        } else if (c == null && resourceOwnerSubject != null) {
            return em.createQuery(
                    "SELECT c FROM ServerAuthorizationCodeGrant"
                            + " c JOIN c.subject s"
                            + " WHERE s.login = :login", ServerAuthorizationCodeGrant.class)
                    .setParameter("login", resourceOwnerSubject.getLogin());
        } else if (c != null && resourceOwnerSubject == null) {
            return em.createQuery(
                    "SELECT code FROM ServerAuthorizationCodeGrant code"
                            + " JOIN code.client c"
                            + " WHERE c.clientId = :clientId", ServerAuthorizationCodeGrant.class)
                    .setParameter("clientId", c.getClientId());
        } else {
            return em.createQuery(
                    "SELECT code FROM ServerAuthorizationCodeGrant code"
                            + " JOIN code.subject s"
                            + " JOIN code.client c"
                            + " WHERE s.login = :login"
                            + " AND c.clientId = :clientId", ServerAuthorizationCodeGrant.class)
                    .setParameter("clientId", c.getClientId())
                    .setParameter("login", resourceOwnerSubject.getLogin());
        }
    }
}
/* @generated */
