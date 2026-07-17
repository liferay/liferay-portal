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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.rs.security.jose.jwt.JoseJwtConsumer;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.JwtTokenUtils;

import static org.apache.cxf.jaxrs.utils.ResourceUtils.getClasspathResourceURL;

public class JCacheOAuthDataProvider extends AbstractOAuthDataProvider {
    public static final String CLIENT_CACHE_KEY = "cxf.oauth2.client.cache";
    public static final String ACCESS_TOKEN_CACHE_KEY = "cxf.oauth2.accesstoken.cache";
    public static final String REFRESH_TOKEN_CACHE_KEY = "cxf.oauth2.refreshtoken.cache";
    public static final String DEFAULT_CONFIG_URL = "cxf-oauth2-ehcache3.xml";

    protected final CacheManager cacheManager;
    private final Cache<String, Client> clientCache;
    private Cache<String, ServerAccessToken> accessTokenCache;
    private Cache<String, String> jwtAccessTokenCache;
    private final Cache<String, RefreshToken> refreshTokenCache;
    private boolean storeJwtTokenKeyOnly;
    private JoseJwtConsumer jwtTokenConsumer;

    public JCacheOAuthDataProvider() {
        this(false);
    }
    public JCacheOAuthDataProvider(boolean storeJwtTokenKeyOnly) {
        this(DEFAULT_CONFIG_URL, BusFactory.getThreadDefaultBus(true), storeJwtTokenKeyOnly);
    }

    public JCacheOAuthDataProvider(String configFileURL, Bus bus) {
        this(configFileURL, bus, false);
    }

    public JCacheOAuthDataProvider(String configFileURL, Bus bus, boolean storeJwtTokenKeyOnly) {
        this(configFileURL, bus, CLIENT_CACHE_KEY, ACCESS_TOKEN_CACHE_KEY, REFRESH_TOKEN_CACHE_KEY,
             storeJwtTokenKeyOnly);
    }

    public JCacheOAuthDataProvider(String configFileURL,
                                   Bus bus,
                                   String clientCacheKey,
                                   String accessTokenCacheKey,
                                   String refreshTokenCacheKey) {
        this(configFileURL, bus, clientCacheKey, accessTokenCacheKey, refreshTokenCacheKey, false);
    }
    public JCacheOAuthDataProvider(String configFileURL,
                                   Bus bus,
                                   String clientCacheKey,
                                   String accessTokenCacheKey,
                                   String refreshTokenCacheKey,
                                   boolean storeJwtTokenKeyOnly) {

        cacheManager = createCacheManager(configFileURL, bus);
        clientCache = createCache(cacheManager, clientCacheKey, String.class, Client.class);

        this.storeJwtTokenKeyOnly = storeJwtTokenKeyOnly;
        if (storeJwtTokenKeyOnly) {
            jwtAccessTokenCache = createCache(cacheManager, accessTokenCacheKey, String.class, String.class);
        } else {
            accessTokenCache = createCache(cacheManager, accessTokenCacheKey, String.class, ServerAccessToken.class);
        }

        refreshTokenCache = createCache(cacheManager, refreshTokenCacheKey, String.class, RefreshToken.class);
    }

    @Override
    public Client doGetClient(String clientId) throws OAuthServiceException {
        return clientCache.get(clientId);
    }

    public void setClient(Client client) {
        clientCache.put(client.getClientId(), client);
    }

    @Override
    protected void doRemoveClient(Client c) {
        clientCache.remove(c.getClientId());
    }

    @Override
    public List<Client> getClients(UserSubject resourceOwner) {
        List<Client> clients = new ArrayList<>();
        for (Iterator<Cache.Entry<String, Client>> it = clientCache.iterator(); it.hasNext();) {
            Cache.Entry<String, Client> entry = it.next();
            Client client = entry.getValue();

            if (isClientMatched(client, resourceOwner)) {
                clients.add(client);
            }
        }

        return clients;
    }

    @Override
    public List<ServerAccessToken> getAccessTokens(Client c, UserSubject sub) {
        if (isUseJwtFormatForAccessTokens() && isStoreJwtTokenKeyOnly()) {
            return getJwtAccessTokens(c, sub);
        }
        return getTokens(accessTokenCache, c, sub);
    }

    @Override
    public List<RefreshToken> getRefreshTokens(Client c, UserSubject sub) {
        return getTokens(refreshTokenCache, c, sub);
    }

    @Override
    public ServerAccessToken getAccessToken(String accessTokenKey) throws OAuthServiceException {
        if (isUseJwtFormatForAccessTokens() && isStoreJwtTokenKeyOnly()) {
            return getJwtAccessToken(accessTokenKey);
        }
        return getToken(accessTokenCache, accessTokenKey);
    }

    @Override
    protected void doRevokeAccessToken(ServerAccessToken at) {
        if (isUseJwtFormatForAccessTokens() && isStoreJwtTokenKeyOnly()) {
            jwtAccessTokenCache.remove(at.getTokenKey());
        } else {
            accessTokenCache.remove(at.getTokenKey());
        }
    }

    @Override
    protected RefreshToken getRefreshToken(String refreshTokenKey) {
        return getToken(refreshTokenCache, refreshTokenKey);
    }

    @Override
    protected void doRevokeRefreshToken(RefreshToken rt) {
        refreshTokenCache.remove(rt.getTokenKey());
    }

    @Override
    protected void saveAccessToken(ServerAccessToken serverToken) {
        if (isUseJwtFormatForAccessTokens() && isStoreJwtTokenKeyOnly()) {
            jwtAccessTokenCache.put(serverToken.getTokenKey(), serverToken.getTokenKey());
        } else {
            accessTokenCache.put(serverToken.getTokenKey(), serverToken);
        }

    }

    @Override
    protected void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenCache.put(refreshToken.getTokenKey(), refreshToken);
    }

    @Override
    protected void linkRefreshTokenToAccessToken(RefreshToken rt, ServerAccessToken at) {
        super.linkRefreshTokenToAccessToken(rt,  at);
        if (!isStoreJwtTokenKeyOnly()) {
            accessTokenCache.replace(at.getTokenKey(), at);
        }
    }

    @Override
    public void close() {

        clientCache.close();
        refreshTokenCache.close();
        if (accessTokenCache != null) {
            accessTokenCache.close();
        } else {
            jwtAccessTokenCache.close();
        }
        cacheManager.close();
    }

    protected static <V extends ServerAccessToken> V getToken(Cache<String, V> cache, String key) {
        V token = cache.get(key);
        if (token != null && isExpired(token)) {
            cache.remove(key);
            token = null;
        }
        return token;
    }
    protected ServerAccessToken getJwtAccessToken(String key) {
        String jose = jwtAccessTokenCache.get(key);
        ServerAccessToken token = null;
        if (jose != null) {
            JoseJwtConsumer theConsumer = jwtTokenConsumer == null ? new JoseJwtConsumer() : jwtTokenConsumer;
            token = JwtTokenUtils.createAccessTokenFromJwt(theConsumer, jose, this,
                                                                 super.getJwtAccessTokenClaimMap());
            if (isExpired(token)) {
                jwtAccessTokenCache.remove(key);
                token = null;
            }
        }
        return token;
    }


    protected static <K, V extends ServerAccessToken> List<V> getTokens(Cache<K, V> cache,
                                                                      Client client, UserSubject sub) {
        final Set<K> toRemove = new HashSet<>();
        final List<V> tokens = new ArrayList<>();

        for (Iterator<Cache.Entry<K, V>> it = cache.iterator(); it.hasNext();) {
            Cache.Entry<K, V> entry = it.next();
            V token = entry.getValue();

            if (isExpired(token)) {
                toRemove.add(entry.getKey());
            } else if (isTokenMatched(token, client, sub)) {
                tokens.add(token);
            }
        }

        cache.removeAll(toRemove);

        return tokens;
    }

    protected List<ServerAccessToken> getJwtAccessTokens(Client client, UserSubject sub) {
        final Set<String> toRemove = new HashSet<>();
        final List<ServerAccessToken> tokens = new ArrayList<>();

        for (Iterator<Cache.Entry<String, String>> it = jwtAccessTokenCache.iterator(); it.hasNext();) {
            Cache.Entry<String, String> entry = it.next();
            String jose = entry.getValue();

            JoseJwtConsumer theConsumer = jwtTokenConsumer == null ? new JoseJwtConsumer() : jwtTokenConsumer;
            ServerAccessToken token = JwtTokenUtils.createAccessTokenFromJwt(theConsumer, jose, this,
                                                                                   super.getJwtAccessTokenClaimMap());

            if (isExpired(token)) {
                toRemove.add(entry.getKey());
            } else if (isTokenMatched(token, client, sub)) {
                tokens.add(token);
            }
        }

        jwtAccessTokenCache.removeAll(toRemove);

        return tokens;
    }

    protected static boolean isExpired(ServerAccessToken token) {
        return System.currentTimeMillis() < (token.getIssuedAt() + token.getExpiresIn());
    }

    protected static CacheManager createCacheManager(String configFile, Bus bus) {
        if (bus == null) {
            bus = BusFactory.getThreadDefaultBus(true);
        }

        // grab the first provider for now.  Ideally we could look at the
        // config file and try to match it with a provider
        CachingProvider provider = Caching.getCachingProviders().iterator().next();

        URI configFileURI;
        try {
            configFileURI = getClasspathResourceURL(configFile, JCacheOAuthDataProvider.class, bus).toURI();
        } catch (Exception ex) {
            configFileURI = provider.getDefaultURI();
        }

        return provider.getCacheManager(configFileURI, Thread.currentThread().getContextClassLoader());
    }

    protected static <K, V> Cache<K, V> createCache(CacheManager cacheManager,
                                                    String cacheKey, Class<K> keyType, Class<V> valueType) {

        Cache<K, V> cache = cacheManager.getCache(cacheKey, keyType, valueType);
        if (cache == null) {
            cache = cacheManager.createCache(
                cacheKey,
                new MutableConfiguration<K, V>()
                    .setTypes(keyType, valueType)
                    .setStoreByValue(true)
                    .setStatisticsEnabled(false)
            );
        }

        return cache;
    }

    public boolean isStoreJwtTokenKeyOnly() {
        return storeJwtTokenKeyOnly;
    }

    public void setJwtTokenConsumer(JoseJwtConsumer jwtTokenConsumer) {
        this.jwtTokenConsumer = jwtTokenConsumer;
    }

}
