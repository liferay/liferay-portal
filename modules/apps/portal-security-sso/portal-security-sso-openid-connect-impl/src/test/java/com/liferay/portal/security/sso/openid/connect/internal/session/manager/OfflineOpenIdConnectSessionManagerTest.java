/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.session.manager;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.sso.openid.connect.internal.AuthorizationServerMetadataResolver;
import com.liferay.portal.security.sso.openid.connect.internal.constants.OpenIdConnectDestinationNames;
import com.liferay.portal.security.sso.openid.connect.internal.util.OpenIdConnectTokenRequestUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;

import java.net.URI;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Manuele Castro
 */
public class OfflineOpenIdConnectSessionManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testExtendOpenIdConnectSession() throws Exception {
		Assert.assertEquals(Long.valueOf(0), CompanyThreadLocal.getCompanyId());

		String authServerWellKnownURI = RandomTestUtil.randomString();
		String clientId = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();

		OpenIdConnectSessionLocalService openIdConnectSessionLocalService =
			Mockito.mock(OpenIdConnectSessionLocalService.class);

		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			_createOfflineOpenIdConnectSessionManager(
				authServerWellKnownURI, clientId, companyId,
				openIdConnectSessionLocalService);

		AccessToken accessToken = _createAccessToken();

		OpenIdConnectSession openIdConnectSession = _createOpenIdConnectSession(
			authServerWellKnownURI, clientId, companyId,
			RandomTestUtil.randomString());

		try (MockedStatic<OpenIdConnectTokenRequestUtil>
				openIdConnectTokenRequestUtilMockedStatic = Mockito.mockStatic(
					OpenIdConnectTokenRequestUtil.class)) {

			PlainJWT plainJWT = new PlainJWT(
				new JWTClaimsSet.Builder(
				).issuer(
					RandomTestUtil.randomString()
				).build());

			openIdConnectTokenRequestUtilMockedStatic.when(
				() -> OpenIdConnectTokenRequestUtil.request(
					Mockito.any(OIDCClientInformation.class),
					Mockito.any(OIDCProviderMetadata.class),
					Mockito.any(RefreshToken.class),
					Mockito.eq(_tokenConnectionTimeout), Mockito.anyString())
			).thenReturn(
				new OIDCTokens(plainJWT.serialize(), accessToken, null)
			);

			ReflectionTestUtil.invoke(
				offlineOpenIdConnectSessionManager,
				"_extendOpenIdConnectSession",
				new Class<?>[] {OpenIdConnectSession.class},
				openIdConnectSession);
		}

		Mockito.verify(
			openIdConnectSession
		).setAccessToken(
			accessToken.toJSONString()
		);

		Mockito.verify(
			openIdConnectSessionLocalService, Mockito.never()
		).deleteOpenIdConnectSession(
			openIdConnectSession
		);

		_testExtendOpenIdConnectSessionRefreshesIdToken();

		long mvccVersion = RandomTestUtil.randomLong();

		_testExtendOpenIdConnectSessionWhenUpdateFails(
			mvccVersion, 1, mvccVersion);
		_testExtendOpenIdConnectSessionWhenUpdateFails(
			mvccVersion + 1, 0, mvccVersion);
		_testExtendOpenIdConnectSessionWhenUpdateFails(null, 0, mvccVersion);
	}

	@Test
	public void testGetDestinationName() {
		SchedulerJobConfiguration schedulerJobConfiguration =
			_getSchedulerJobConfiguration(
				new OfflineOpenIdConnectSessionManager());

		Assert.assertEquals(
			OpenIdConnectDestinationNames.OPENID_CONNECT_TOKEN_REFRESH_BATCH,
			schedulerJobConfiguration.getDestinationName());
	}

	@Test
	public void testGetJobExecutorUnsafeRunnable() throws Exception {
		String authServerWellKnownURI = RandomTestUtil.randomString();
		String clientId = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();

		OpenIdConnectSessionLocalService openIdConnectSessionLocalService =
			Mockito.mock(OpenIdConnectSessionLocalService.class);

		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			_createOfflineOpenIdConnectSessionManager(
				authServerWellKnownURI, clientId, companyId,
				openIdConnectSessionLocalService);

		OpenIdConnectSession expiringOpenIdConnectSession =
			_createOpenIdConnectSession(
				authServerWellKnownURI, clientId, companyId,
				RandomTestUtil.randomString());

		Mockito.when(
			expiringOpenIdConnectSession.getAccessTokenExpirationDate()
		).thenReturn(
			new Date()
		);

		long expiringOpenIdConnectSessionId = RandomTestUtil.randomLong();

		Mockito.when(
			openIdConnectSessionLocalService.fetchOpenIdConnectSession(
				expiringOpenIdConnectSessionId)
		).thenReturn(
			expiringOpenIdConnectSession
		);

		OpenIdConnectSession refreshedOpenIdConnectSession =
			_createOpenIdConnectSession(
				authServerWellKnownURI, clientId, companyId,
				RandomTestUtil.randomString());

		Mockito.when(
			refreshedOpenIdConnectSession.getAccessTokenExpirationDate()
		).thenReturn(
			new Date(System.currentTimeMillis() + Time.HOUR)
		);

		long refreshedOpenIdConnectSessionId = RandomTestUtil.randomLong();

		Mockito.when(
			openIdConnectSessionLocalService.fetchOpenIdConnectSession(
				refreshedOpenIdConnectSessionId)
		).thenReturn(
			refreshedOpenIdConnectSession
		);

		List<OpenIdConnectSession> openIdConnectSessions = List.of(
			_createOpenIdConnectSession(expiringOpenIdConnectSessionId),
			_createOpenIdConnectSession(RandomTestUtil.randomLong()),
			_createOpenIdConnectSession(refreshedOpenIdConnectSessionId));

		Mockito.when(
			openIdConnectSessionLocalService.
				getAccessTokenExpirationDateOpenIdConnectSessions(
					Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyInt())
		).thenReturn(
			openIdConnectSessions
		);

		try (MockedStatic<OpenIdConnectTokenRequestUtil>
				openIdConnectTokenRequestUtilMockedStatic = Mockito.mockStatic(
					OpenIdConnectTokenRequestUtil.class)) {

			PlainJWT plainJWT = new PlainJWT(
				new JWTClaimsSet.Builder(
				).issuer(
					RandomTestUtil.randomString()
				).build());

			openIdConnectTokenRequestUtilMockedStatic.when(
				() -> OpenIdConnectTokenRequestUtil.request(
					Mockito.any(OIDCClientInformation.class),
					Mockito.any(OIDCProviderMetadata.class),
					Mockito.any(RefreshToken.class),
					Mockito.eq(_tokenConnectionTimeout), Mockito.anyString())
			).thenReturn(
				new OIDCTokens(plainJWT.serialize(), _createAccessToken(), null)
			);

			SchedulerJobConfiguration schedulerJobConfiguration =
				_getSchedulerJobConfiguration(
					offlineOpenIdConnectSessionManager);

			UnsafeRunnable<Exception> unsafeRunnable =
				schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

			unsafeRunnable.run();
		}

		Mockito.verify(
			openIdConnectSessionLocalService
		).updateOpenIdConnectSession(
			expiringOpenIdConnectSession
		);

		Mockito.verify(
			openIdConnectSessionLocalService, Mockito.never()
		).updateOpenIdConnectSession(
			refreshedOpenIdConnectSession
		);
	}

	@Test
	public void testStartOpenIdConnectSession() {
		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			new OfflineOpenIdConnectSessionManager();

		OpenIdConnectSessionLocalService openIdConnectSessionLocalService =
			Mockito.mock(OpenIdConnectSessionLocalService.class);

		ReflectionTestUtil.setFieldValue(
			offlineOpenIdConnectSessionManager,
			"_openIdConnectSessionLocalService",
			openIdConnectSessionLocalService);

		OpenIdConnectSession openIdConnectSession = Mockito.mock(
			OpenIdConnectSession.class);

		Mockito.when(
			openIdConnectSessionLocalService.fetchOpenIdConnectSession(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			openIdConnectSession
		);

		AccessToken accessToken = _createAccessToken();

		PlainJWT plainJWT = new PlainJWT(
			new JWTClaimsSet.Builder(
			).issuer(
				RandomTestUtil.randomString()
			).build());

		String idTokenString = plainJWT.serialize();

		offlineOpenIdConnectSessionManager.startOpenIdConnectSession(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new OIDCTokens(
				idTokenString, accessToken,
				new RefreshToken(RandomTestUtil.randomString(1500))),
			RandomTestUtil.randomLong());

		Mockito.verify(
			openIdConnectSession
		).setAccessToken(
			accessToken.toJSONString()
		);

		Mockito.verify(
			openIdConnectSession
		).setIdToken(
			idTokenString
		);
	}

	@Test
	public void testUpdateOpenIdConnectSessionIdToken() throws Exception {
		OpenIdConnectSession openIdConnectSession = Mockito.mock(
			OpenIdConnectSession.class);

		ReflectionTestUtil.invoke(
			new OfflineOpenIdConnectSessionManager(),
			"_updateOpenIdConnectSessionIdToken",
			new Class<?>[] {String.class, OpenIdConnectSession.class}, null,
			openIdConnectSession);

		Mockito.verifyNoInteractions(openIdConnectSession);

		String issuer = RandomTestUtil.randomString();
		String sessionId = RandomTestUtil.randomString();

		PlainJWT plainJWT = new PlainJWT(
			new JWTClaimsSet.Builder(
			).claim(
				"sid", sessionId
			).issuer(
				issuer
			).build());

		String idTokenString = plainJWT.serialize();

		ReflectionTestUtil.invoke(
			new OfflineOpenIdConnectSessionManager(),
			"_updateOpenIdConnectSessionIdToken",
			new Class<?>[] {String.class, OpenIdConnectSession.class},
			idTokenString, openIdConnectSession);

		Mockito.verify(
			openIdConnectSession
		).setIdToken(
			idTokenString
		);

		Mockito.verify(
			openIdConnectSession
		).setIssuer(
			issuer
		);

		Mockito.verify(
			openIdConnectSession
		).setSessionId(
			sessionId
		);
	}

	private AccessToken _createAccessToken() {
		return new AccessToken(
			new AccessTokenType("Bearer"), RandomTestUtil.randomString(), 60,
			new Scope("openid")) {

			@Override
			public String toAuthorizationHeader() {
				return null;
			}

		};
	}

	private OAuthClientEntry _createOAuthClientEntry(
		String authServerWellKnownURI, String clientId, long companyId) {

		OAuthClientEntry oAuthClientEntry = Mockito.mock(
			OAuthClientEntry.class);

		Mockito.when(
			oAuthClientEntry.getAuthServerWellKnownURI()
		).thenReturn(
			authServerWellKnownURI
		);

		Mockito.when(
			oAuthClientEntry.getClientId()
		).thenReturn(
			clientId
		);

		Mockito.when(
			oAuthClientEntry.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			oAuthClientEntry.getInfoJSON()
		).thenReturn(
			"{\"client_id\": \"test-client\"}"
		);

		Mockito.when(
			oAuthClientEntry.getMetadataCacheInSeconds()
		).thenReturn(
			3600
		);

		Mockito.when(
			oAuthClientEntry.getOAuthClientEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_tokenConnectionTimeout = RandomTestUtil.randomInt();

		Mockito.when(
			oAuthClientEntry.getTokenConnectionTimeout()
		).thenReturn(
			_tokenConnectionTimeout
		);

		Mockito.when(
			oAuthClientEntry.getTokenRequestParametersJSON()
		).thenReturn(
			"{}"
		);

		return oAuthClientEntry;
	}

	private OIDCProviderMetadata _createOIDCProviderMetadata()
		throws Exception {

		OIDCProviderMetadata oidcProviderMetadata = new OIDCProviderMetadata(
			new Issuer(RandomTestUtil.randomString()),
			List.of(SubjectType.PUBLIC),
			new URI(RandomTestUtil.randomString()));

		oidcProviderMetadata.setTokenEndpointURI(
			new URI(RandomTestUtil.randomString()));

		return oidcProviderMetadata;
	}

	private OfflineOpenIdConnectSessionManager
			_createOfflineOpenIdConnectSessionManager(
				String authServerWellKnownURI, String clientId, long companyId,
				OpenIdConnectSessionLocalService
					openIdConnectSessionLocalService)
		throws Exception {

		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			new OfflineOpenIdConnectSessionManager();

		AuthorizationServerMetadataResolver
			authorizationServerMetadataResolver = Mockito.mock(
				AuthorizationServerMetadataResolver.class);

		Mockito.when(
			authorizationServerMetadataResolver.resolveOIDCProviderMetadata(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(),
				Mockito.anyLong())
		).thenReturn(
			_createOIDCProviderMetadata()
		);

		ReflectionTestUtil.setFieldValue(
			offlineOpenIdConnectSessionManager,
			"_authorizationServerMetadataResolver",
			authorizationServerMetadataResolver);

		OAuthClientEntryLocalService oAuthClientEntryLocalService =
			Mockito.mock(OAuthClientEntryLocalService.class);

		OAuthClientEntry oAuthClientEntry = _createOAuthClientEntry(
			authServerWellKnownURI, clientId, companyId);

		Mockito.when(
			oAuthClientEntryLocalService.fetchOAuthClientEntry(
				companyId, authServerWellKnownURI, clientId)
		).thenReturn(
			oAuthClientEntry
		);

		ReflectionTestUtil.setFieldValue(
			offlineOpenIdConnectSessionManager, "_oAuthClientEntryLocalService",
			oAuthClientEntryLocalService);

		ReflectionTestUtil.setFieldValue(
			offlineOpenIdConnectSessionManager,
			"_openIdConnectSessionLocalService",
			openIdConnectSessionLocalService);

		return offlineOpenIdConnectSessionManager;
	}

	private OpenIdConnectSession _createOpenIdConnectSession(
		long openIdConnectSessionId) {

		OpenIdConnectSession openIdConnectSession = Mockito.mock(
			OpenIdConnectSession.class);

		Mockito.when(
			openIdConnectSession.getOpenIdConnectSessionId()
		).thenReturn(
			openIdConnectSessionId
		);

		return openIdConnectSession;
	}

	private OpenIdConnectSession _createOpenIdConnectSession(
		String authServerWellKnownURI, String clientId, long companyId,
		String refreshToken) {

		OpenIdConnectSession openIdConnectSession = Mockito.mock(
			OpenIdConnectSession.class);

		Mockito.when(
			openIdConnectSession.getAuthServerWellKnownURI()
		).thenReturn(
			authServerWellKnownURI
		);

		Mockito.when(
			openIdConnectSession.getClientId()
		).thenReturn(
			clientId
		);

		Mockito.when(
			openIdConnectSession.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			openIdConnectSession.getRefreshToken()
		).thenReturn(
			refreshToken
		);

		return openIdConnectSession;
	}

	private SchedulerJobConfiguration _getSchedulerJobConfiguration(
		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager) {

		DestinationFactory destinationFactory = Mockito.mock(
			DestinationFactory.class);

		Mockito.when(
			destinationFactory.createDestination(
				Mockito.any(DestinationConfiguration.class))
		).thenReturn(
			Mockito.mock(Destination.class)
		);

		ReflectionTestUtil.setFieldValue(
			offlineOpenIdConnectSessionManager, "_destinationFactory",
			destinationFactory);

		BundleContext bundleContext = Mockito.mock(BundleContext.class);

		offlineOpenIdConnectSessionManager.activate(
			bundleContext,
			HashMapBuilder.<String, Object>put(
				"enabled", true
			).build());

		ArgumentCaptor<SchedulerJobConfiguration> argumentCaptor =
			ArgumentCaptor.forClass(SchedulerJobConfiguration.class);

		Mockito.verify(
			bundleContext
		).registerService(
			Mockito.eq(SchedulerJobConfiguration.class),
			argumentCaptor.capture(), Mockito.isNull()
		);

		return argumentCaptor.getValue();
	}

	private void _testExtendOpenIdConnectSessionRefreshesIdToken()
		throws Exception {

		String authServerWellKnownURI = RandomTestUtil.randomString();
		String clientId = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();

		OpenIdConnectSessionLocalService openIdConnectSessionLocalService =
			Mockito.mock(OpenIdConnectSessionLocalService.class);

		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			_createOfflineOpenIdConnectSessionManager(
				authServerWellKnownURI, clientId, companyId,
				openIdConnectSessionLocalService);

		String refreshedIssuer = RandomTestUtil.randomString();
		String refreshedSessionId = RandomTestUtil.randomString();

		PlainJWT plainJWT = new PlainJWT(
			new JWTClaimsSet.Builder(
			).claim(
				"sid", refreshedSessionId
			).issuer(
				refreshedIssuer
			).build());

		String refreshedIdTokenString = plainJWT.serialize();

		AccessToken refreshedAccessToken = _createAccessToken();
		RefreshToken refreshedRefreshToken = new RefreshToken(
			RandomTestUtil.randomString());

		OpenIdConnectSession openIdConnectSession = _createOpenIdConnectSession(
			authServerWellKnownURI, clientId, companyId,
			RandomTestUtil.randomString());

		try (MockedStatic<OpenIdConnectTokenRequestUtil>
				openIdConnectTokenRequestUtilMockedStatic = Mockito.mockStatic(
					OpenIdConnectTokenRequestUtil.class)) {

			openIdConnectTokenRequestUtilMockedStatic.when(
				() -> OpenIdConnectTokenRequestUtil.request(
					Mockito.any(OIDCClientInformation.class),
					Mockito.any(OIDCProviderMetadata.class),
					Mockito.any(RefreshToken.class),
					Mockito.eq(_tokenConnectionTimeout), Mockito.anyString())
			).thenReturn(
				new OIDCTokens(
					refreshedIdTokenString, refreshedAccessToken,
					refreshedRefreshToken)
			);

			ReflectionTestUtil.invoke(
				offlineOpenIdConnectSessionManager,
				"_extendOpenIdConnectSession",
				new Class<?>[] {OpenIdConnectSession.class},
				openIdConnectSession);
		}

		Mockito.verify(
			openIdConnectSession
		).setAccessToken(
			refreshedAccessToken.toJSONString()
		);

		Mockito.verify(
			openIdConnectSession
		).setIdToken(
			refreshedIdTokenString
		);

		Mockito.verify(
			openIdConnectSession
		).setIssuer(
			refreshedIssuer
		);

		Mockito.verify(
			openIdConnectSession
		).setRefreshToken(
			refreshedRefreshToken.toString()
		);

		Mockito.verify(
			openIdConnectSession
		).setSessionId(
			refreshedSessionId
		);
	}

	private void _testExtendOpenIdConnectSessionWhenUpdateFails(
			Long currentMvccVersion, int expectedTimes, long mvccVersion)
		throws Exception {

		String authServerWellKnownURI = RandomTestUtil.randomString();
		String clientId = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();
		OpenIdConnectSessionLocalService openIdConnectSessionLocalService =
			Mockito.mock(OpenIdConnectSessionLocalService.class);

		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			_createOfflineOpenIdConnectSessionManager(
				authServerWellKnownURI, clientId, companyId,
				openIdConnectSessionLocalService);

		OpenIdConnectSession openIdConnectSession = _createOpenIdConnectSession(
			authServerWellKnownURI, clientId, companyId,
			RandomTestUtil.randomString());

		Mockito.when(
			openIdConnectSession.getMvccVersion()
		).thenReturn(
			mvccVersion
		);

		long openIdConnectSessionId = RandomTestUtil.randomLong();

		Mockito.when(
			openIdConnectSession.getOpenIdConnectSessionId()
		).thenReturn(
			openIdConnectSessionId
		);

		Mockito.when(
			openIdConnectSessionLocalService.updateOpenIdConnectSession(
				openIdConnectSession)
		).thenThrow(
			new SystemException(new ORMException(RandomTestUtil.randomString()))
		);

		if (currentMvccVersion != null) {
			OpenIdConnectSession currentOpenIdConnectSession = Mockito.mock(
				OpenIdConnectSession.class);

			Mockito.when(
				currentOpenIdConnectSession.getMvccVersion()
			).thenReturn(
				currentMvccVersion
			);

			Mockito.when(
				openIdConnectSessionLocalService.fetchOpenIdConnectSession(
					openIdConnectSessionId)
			).thenReturn(
				currentOpenIdConnectSession
			);
		}

		try (MockedStatic<OpenIdConnectTokenRequestUtil>
				openIdConnectTokenRequestUtilMockedStatic = Mockito.mockStatic(
					OpenIdConnectTokenRequestUtil.class)) {

			PlainJWT plainJWT = new PlainJWT(
				new JWTClaimsSet.Builder(
				).issuer(
					RandomTestUtil.randomString()
				).build());

			openIdConnectTokenRequestUtilMockedStatic.when(
				() -> OpenIdConnectTokenRequestUtil.request(
					Mockito.any(OIDCClientInformation.class),
					Mockito.any(OIDCProviderMetadata.class),
					Mockito.any(RefreshToken.class),
					Mockito.eq(_tokenConnectionTimeout), Mockito.anyString())
			).thenReturn(
				new OIDCTokens(plainJWT.serialize(), _createAccessToken(), null)
			);

			ReflectionTestUtil.invoke(
				offlineOpenIdConnectSessionManager,
				"_extendOpenIdConnectSession",
				new Class<?>[] {OpenIdConnectSession.class},
				openIdConnectSession);
		}

		Mockito.verify(
			openIdConnectSessionLocalService, Mockito.times(expectedTimes)
		).deleteOpenIdConnectSession(
			Mockito.any(OpenIdConnectSession.class)
		);
	}

	private int _tokenConnectionTimeout;

}