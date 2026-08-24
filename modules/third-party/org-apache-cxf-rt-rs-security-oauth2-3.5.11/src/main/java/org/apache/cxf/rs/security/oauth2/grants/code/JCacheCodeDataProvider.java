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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.cache.Cache;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.provider.JCacheOAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;

public class JCacheCodeDataProvider extends JCacheOAuthDataProvider
    implements AuthorizationCodeDataProvider {
    public static final String CODE_GRANT_CACHE_KEY = "cxf.oauth2.codegrant.cache";

    private long codeLifetime = 10 * 60;
    private Cache<String, ServerAuthorizationCodeGrant> grantCache;

    protected JCacheCodeDataProvider() {
        this(DEFAULT_CONFIG_URL, BusFactory.getThreadDefaultBus(true));
    }

    protected JCacheCodeDataProvider(String configFileURL, Bus bus) {
        this(configFileURL, bus, CLIENT_CACHE_KEY, CODE_GRANT_CACHE_KEY,
             ACCESS_TOKEN_CACHE_KEY, REFRESH_TOKEN_CACHE_KEY);
    }

    protected JCacheCodeDataProvider(String configFileURL,
                                     Bus bus,
                                     String clientCacheKey,
                                     String codeCacheKey,
                                     String accessTokenKey,
                                     String refreshTokenKey) {
        this(configFileURL, bus, clientCacheKey, codeCacheKey, accessTokenKey, refreshTokenKey, false);
    }

    protected JCacheCodeDataProvider(String configFileURL,
                                     Bus bus,
                                     String clientCacheKey,
                                     String codeCacheKey,
                                     String accessTokenKey,
                                     String refreshTokenKey,
                                     boolean storeJwtTokenKeyOnly) {
        super(configFileURL, bus, clientCacheKey, accessTokenKey, refreshTokenKey, storeJwtTokenKeyOnly);
        grantCache = createCache(cacheManager, codeCacheKey, String.class, ServerAuthorizationCodeGrant.class);
    }


    @Override
    protected void doRemoveClient(Client c) {
        for (ServerAuthorizationCodeGrant grant : getCodeGrants(c, null)) {
            removeCodeGrant(grant.getCode());
        }

        super.doRemoveClient(c);
    }

    @Override
    public ServerAuthorizationCodeGrant createCodeGrant(AuthorizationCodeRegistration reg)
        throws OAuthServiceException {
        ServerAuthorizationCodeGrant grant = AbstractCodeDataProvider.initCodeGrant(reg, codeLifetime);
        grantCache.put(grant.getCode(), grant);

        return grant;
    }

    @Override
    public List<ServerAuthorizationCodeGrant> getCodeGrants(Client c, UserSubject sub) {
        final Set<String> toRemove = new HashSet<>();
        final List<ServerAuthorizationCodeGrant> grants = new ArrayList<>();

        for (Iterator<Cache.Entry<String, ServerAuthorizationCodeGrant>> it = grantCache.iterator(); it.hasNext();) {
            Cache.Entry<String, ServerAuthorizationCodeGrant> entry = it.next();
            ServerAuthorizationCodeGrant grant = entry.getValue();

            if (isExpired(grant)) {
                toRemove.add(entry.getKey());
            } else if (AbstractCodeDataProvider.isCodeMatched(grant, c, sub)) {
                grants.add(grant);
            }
        }

        grantCache.removeAll(toRemove);

        return grants;
    }

    @Override
    public ServerAuthorizationCodeGrant removeCodeGrant(String code) throws OAuthServiceException {
        ServerAuthorizationCodeGrant grant = getCodeGrant(code);
        if (grant != null) {
            grantCache.remove(code);
        }
        return grant;
    }

    public void setCodeLifetime(long codeLifetime) {
        this.codeLifetime = codeLifetime;
    }

    protected ServerAuthorizationCodeGrant getCodeGrant(String code) throws OAuthServiceException {
        ServerAuthorizationCodeGrant grant = grantCache.get(code);
        if (grant != null && isExpired(grant)) {
            grantCache.remove(code);
            grant = null;
        }

        return grant;
    }

    protected static boolean isExpired(ServerAuthorizationCodeGrant grant) {
        return System.currentTimeMillis() < (grant.getIssuedAt() + grant.getExpiresIn());
    }

    @Override
    public void close() {
        grantCache.close();
        super.close();
    }
}
