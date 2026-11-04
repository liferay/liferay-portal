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
package org.apache.cxf.rs.security.oauth2.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

/**
 * Provides a Jpa BMT implementation for OAuthDataProvider.
 *
 * If your application runs in a container and if you want to use
 * container managed persistence, you'll have to override
 * the following methods :
 * <ul>
 *     <li> {@link #getEntityManager()}</li>
 *     <li> {@link #commitIfNeeded(EntityManager)}</li>
 *     <li> {@link #closeIfNeeded(EntityManager)}</li>
 * </ul>
 */
public class JPAOAuthDataProvider extends AbstractOAuthDataProvider {
    private static final String CLIENT_QUERY = "SELECT client FROM Client client"
            + " INNER JOIN client.resourceOwnerSubject ros";

    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.entityManagerFactory = emf;
    }

    @Override
    public Client doGetClient(final String clientId) throws OAuthServiceException {
        return execute(em -> {
            return em.find(Client.class, clientId);
        });
    }

    protected <T> T execute(EntityManagerOperation<T> operation) {
        EntityManager em = getEntityManager();
        T value;
        try {
            value = operation.apply(em);
        } finally {
            closeIfNeeded(em);
        }
        return value;
    }

    protected <T> T executeInTransaction(EntityManagerOperation<T> operation) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = null;
        T value;
        try {
            transaction = beginIfNeeded(em);
            value = operation.apply(em);
            flushIfNeeded(em);
            commitIfNeeded(em);
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            closeIfNeeded(em);
        }
        return value;
    }

    public void setClient(final Client client) {
        executeInTransaction(em -> {
            if (client.getResourceOwnerSubject() != null) {
                UserSubject sub =
                        em.find(UserSubject.class, client.getResourceOwnerSubject().getId());
                if (sub == null) {
                    em.persist(client.getResourceOwnerSubject());
                } else {
                    client.setResourceOwnerSubject(sub);
                }
            }
            boolean clientExists = em.createQuery("SELECT count(client) from Client client "
                            + "where client.clientId = :id", Long.class)
                    .setParameter("id", client.getClientId())
                    .getSingleResult() > 0;
            if (clientExists) {
                em.merge(client);
            } else {
                em.persist(client);
            }
            return null;
        });
    }

    @Override
    protected void doRemoveClient(final Client c) {
        executeInTransaction(em -> {
            Client clientToRemove = em.getReference(Client.class, c.getClientId());
            em.remove(clientToRemove);
            return null;
        });
    }

    @Override
    public List<Client> getClients(final UserSubject resourceOwner) {
        return execute(em -> {
            return getClientsQuery(resourceOwner, em).getResultList();
        });
    }

    @Override
    public List<ServerAccessToken> getAccessTokens(final Client c, final UserSubject sub) {
        return execute(em -> {
            return CastUtils.cast(getTokensQuery(c, sub, em).getResultList());
        });
    }

    @Override
    public List<RefreshToken> getRefreshTokens(final Client c, final UserSubject sub) {
        return execute(em ->  {
            return getRefreshTokensQuery(c, sub, em).getResultList();
        });
    }

    @Override
    public ServerAccessToken getAccessToken(final String accessToken) throws OAuthServiceException {
        return execute(em -> {
            TypedQuery<BearerAccessToken> query = em.createQuery("SELECT t FROM BearerAccessToken t"
                                  + " WHERE t.tokenKey = :tokenKey", BearerAccessToken.class)
                                  .setParameter("tokenKey", accessToken);
            if (query.getResultList().isEmpty()) {
                return null;
            }
            return query.getSingleResult();
        });
    }

    @Override
    protected void doRevokeAccessToken(final ServerAccessToken at) {
        executeInTransaction(em -> {
            ServerAccessToken tokenToRemove = em.getReference(at.getClass(), at.getTokenKey());
            em.remove(tokenToRemove);
            return null;
        });
    }

    @Override
    protected void linkRefreshTokenToAccessToken(final RefreshToken rt, final ServerAccessToken at) {
        super.linkRefreshTokenToAccessToken(rt, at);
        executeInTransaction(em -> {
            em.merge(at);
            return null;
        });
    }

    @Override
    protected RefreshToken getRefreshToken(final String refreshTokenKey) {
        return execute(em -> {
            return em.find(RefreshToken.class, refreshTokenKey);
        });
    }

    @Override
    protected RefreshToken revokeRefreshToken(Client client, UserSubject callerSubject, String refreshTokenKey) {
        // Atomic find + validate + delete in one transaction prevents concurrent replay of the same refresh token.
        return executeInTransaction(em -> {
            RefreshToken refreshToken = em.find(RefreshToken.class, refreshTokenKey,
                                                LockModeType.PESSIMISTIC_WRITE);
            if (refreshToken != null) {
                if (!refreshToken.getClient().getClientId().equals(client.getClientId())) {
                    throw new OAuthServiceException(OAuthConstants.INVALID_GRANT);
                }
                if (callerSubject != null && refreshToken.getSubject() != null
                    && !callerSubject.getLogin().equals(refreshToken.getSubject().getLogin())) {
                    throw new OAuthServiceException(OAuthConstants.INVALID_GRANT);
                }
                em.remove(refreshToken);
            }
            return refreshToken;
        });
    }

    @Override
    protected void doRevokeRefreshToken(final RefreshToken rt) {
        executeInTransaction(em -> {
            RefreshToken tokentoRemove = em.getReference(RefreshToken.class, rt.getTokenKey());
            em.remove(tokentoRemove);
            return null;
        });
    }

    @Override
    protected ServerAccessToken doCreateAccessToken(AccessTokenRegistration atReg) {
        ServerAccessToken at = super.doCreateAccessToken(atReg);
        // we override this in order to get rid of elementCollections directly injected
        // from another entity
        // this can be the case when using multiple cmt dataProvider operation in a single entityManager
        // lifespan
        if (at.getAudiences() != null) {
            at.setAudiences(new ArrayList<>(at.getAudiences()));
        }
        if (at.getExtraProperties() != null) {
            at.setExtraProperties(new HashMap<String, String>(at.getExtraProperties()));
        }
        if (at.getScopes() != null) {
            at.setScopes(new ArrayList<>(at.getScopes()));
        }
        if (at.getParameters() != null) {
            at.setParameters(new HashMap<String, String>(at.getParameters()));
        }
        return at;
    }

    protected void saveAccessToken(final ServerAccessToken serverToken) {
        executeInTransaction(em -> {
            List<OAuthPermission> perms = new LinkedList<>();
            for (OAuthPermission perm : serverToken.getScopes()) {
                OAuthPermission permSaved = em.find(OAuthPermission.class, perm.getPermission());
                if (permSaved != null) {
                    perms.add(permSaved);
                } else {
                    em.persist(perm);
                    perms.add(perm);
                }
            }
            serverToken.setScopes(perms);

            if (serverToken.getSubject() != null) {
                UserSubject sub = em.find(UserSubject.class, serverToken.getSubject().getId());
                if (sub == null) {
                    em.persist(serverToken.getSubject());
                } else {
                    sub = em.merge(serverToken.getSubject());
                    serverToken.setSubject(sub);
                }
            }
            // ensure we have a managed association
            // (needed for OpenJPA : InvalidStateException: Encountered unmanaged object)
            if (serverToken.getClient() != null) {
                serverToken.setClient(em.find(Client.class, serverToken.getClient().getClientId()));
            }

            em.persist(serverToken);
            return null;
        });
    }

    protected void saveRefreshToken(RefreshToken refreshToken) {
        persistEntity(refreshToken);
    }

    protected void persistEntity(final Object entity) {
        executeInTransaction(em -> {
            em.persist(entity);
            return null;
        });
    }

    protected void removeEntity(final Object entity) {
        executeInTransaction(em -> {
            em.remove(entity);
            return null;
        });
    }

    protected TypedQuery<Client> getClientsQuery(UserSubject resourceOwnerSubject, EntityManager entityManager) {
        if (resourceOwnerSubject == null) {
            return entityManager.createQuery(CLIENT_QUERY, Client.class);
        }
        return entityManager.createQuery(CLIENT_QUERY + " WHERE ros.login = :login", Client.class).
                setParameter("login", resourceOwnerSubject.getLogin());
    }

    protected TypedQuery<BearerAccessToken> getTokensQuery(Client c, UserSubject resourceOwnerSubject,
                                                           EntityManager entityManager) {
        return getQuery("BearerAccessToken", c, resourceOwnerSubject, entityManager, BearerAccessToken.class);
    }

    protected TypedQuery<RefreshToken> getRefreshTokensQuery(Client c, UserSubject resourceOwnerSubject,
                                                             EntityManager entityManager) {
        return getQuery("RefreshToken", c, resourceOwnerSubject, entityManager, RefreshToken.class);
    }

    private static <T> TypedQuery<T> getQuery(String table, Client c, UserSubject resourceOwnerSubject,
            EntityManager entityManager, Class<T> resultClass) {
        StringBuilder query = new StringBuilder("SELECT t FROM ").append(table).append(" t");
        Map<String, Object> parameterMap = new HashMap<>();
        if (c != null || resourceOwnerSubject != null) {
            query.append(" WHERE");
            if (c != null) {
                query.append(" t.client.clientId = :clientId");
                parameterMap.put("clientId", c.getClientId());
            }
            if (resourceOwnerSubject != null) {
                if (!parameterMap.isEmpty()) {
                    query.append(" AND");
                }
                query.append(" t.subject.login = :login");
                parameterMap.put("login", resourceOwnerSubject.getLogin());
            }
        }
        TypedQuery<T> typedQuery = entityManager.createQuery(query.toString(), resultClass);
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            typedQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return typedQuery;
    }

    /**
     * Returns the entityManaged used for the current operation.
     */
    protected EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Begins the current transaction.
     *
     * This method needs to be overridden in a CMT environment.
     */
    protected EntityTransaction beginIfNeeded(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        return tx;
    }

    /**
     * Flush the current transaction.
     */
    protected void flushIfNeeded(EntityManager em) {
        em.flush();
    }

    /**
     * Commits the current transaction.
     *
     * This method needs to be overridden in a CMT environment.
     */
    protected void commitIfNeeded(EntityManager em) {
        em.getTransaction().commit();
    }

    /**
     * Closes the current em.
     *
     * This method needs to be overriden in a CMT environment.
     */
    protected void closeIfNeeded(EntityManager em) {
        em.close();
    }

    public interface EntityManagerOperation<T> extends Function<EntityManager, T> {
    }
}
/* @generated */
