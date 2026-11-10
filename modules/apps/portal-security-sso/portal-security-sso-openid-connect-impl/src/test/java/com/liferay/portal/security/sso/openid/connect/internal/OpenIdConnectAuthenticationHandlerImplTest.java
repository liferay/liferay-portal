/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceException;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.internal.session.manager.OfflineOpenIdConnectSessionManager;
import com.liferay.portal.security.sso.openid.connect.internal.util.OpenIdConnectClientInformationUtil;
import com.liferay.portal.security.sso.openid.connect.internal.util.OpenIdConnectTokenRequestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.net.SocketTimeoutException;
import java.net.URI;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Tamas Biro
 */
public class OpenIdConnectAuthenticationHandlerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetUserInfoClaims() throws Exception {
		Map<String, Object> claims = _getUserInfoClaims(
			JSONUtil.put(
				"email", "exists@test.com"
			).put(
				"name", "test_account"
			).put(
				"sub", "subject"
			).toString());

		Assert.assertEquals("exists@test.com", claims.get("email"));

		claims = _getUserInfoClaims(
			JSONUtil.put(
				"name", "test_account"
			).put(
				"sub", "subject"
			).toString());

		Assert.assertNull(claims.get("email"));
	}

	@Test
	public void testProcessAuthenticationResponse() throws Exception {
		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			Mockito.mock(OfflineOpenIdConnectSessionManager.class);
		long openIdConnectSessionId = RandomTestUtil.randomLong();

		Mockito.when(
			offlineOpenIdConnectSessionManager.startOpenIdConnectSession(
				Mockito.any(), Mockito.any(), Mockito.any(OIDCTokens.class),
				Mockito.anyLong())
		).thenReturn(
			openIdConnectSessionId
		);

		HttpSession httpSession = Mockito.mock(HttpSession.class);

		_processAuthenticationResponse(
			httpSession, offlineOpenIdConnectSessionManager);

		Mockito.verify(
			httpSession
		).setAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION_ID,
			openIdConnectSessionId
		);

		Mockito.when(
			offlineOpenIdConnectSessionManager.startOpenIdConnectSession(
				Mockito.any(), Mockito.any(), Mockito.any(OIDCTokens.class),
				Mockito.anyLong())
		).thenThrow(
			new SystemException(new ORMException(RandomTestUtil.randomString()))
		);

		try {
			_processAuthenticationResponse(
				httpSession, offlineOpenIdConnectSessionManager);

			Assert.fail();
		}
		catch (SystemException systemException) {
			Mockito.verify(
				httpSession
			).invalidate();
		}
	}

	@Test
	public void testRequestUserInfoJSON() throws Exception {
		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";
		String subject = RandomTestUtil.randomString();

		try (MockedStatic<HttpUtil> httpUtilMockedStatic = Mockito.mockStatic(
				HttpUtil.class)) {

			httpUtilMockedStatic.when(
				() -> HttpUtil.URLtoString(Mockito.any(Http.Options.class))
			).thenAnswer(
				invocation -> {
					Http.Options httpOptions = invocation.getArgument(0);

					Http.Response httpResponse = new Http.Response();

					httpResponse.setContentType("application/json");
					httpResponse.setResponseCode(200);

					httpOptions.setResponse(httpResponse);

					return JSONUtil.put(
						"email", emailAddress
					).put(
						"sub", subject
					).toString();
				}
			);

			String userInfoJSON = _requestUserInfoJSON();

			Assert.assertNotNull(userInfoJSON);

			Map<String, Object> userInfo = JSONObjectUtils.parse(userInfoJSON);

			Assert.assertEquals(emailAddress, userInfo.get("email"));
			Assert.assertEquals(subject, userInfo.get("sub"));
		}

		try (MockedStatic<HttpUtil> httpUtilMockedStatic = Mockito.mockStatic(
				HttpUtil.class)) {

			httpUtilMockedStatic.when(
				() -> HttpUtil.URLtoString(Mockito.any(Http.Options.class))
			).thenThrow(
				new SocketTimeoutException("Read timed out")
			);

			try {
				_requestUserInfoJSON();

				Assert.fail();
			}
			catch (OpenIdConnectServiceException.UserInfoException
						userInfoException) {

				Throwable throwable = userInfoException.getCause();

				Assert.assertTrue(throwable instanceof SocketTimeoutException);
				Assert.assertEquals("Read timed out", throwable.getMessage());
			}
		}
	}

	private Map<String, Object> _getUserInfoClaims(String claimSetJSON)
		throws Exception {

		OpenIdConnectAuthenticationHandlerImpl
			openIdConnectAuthenticationHandlerImpl =
				new OpenIdConnectAuthenticationHandlerImpl();

		JWT mockJWT = Mockito.mock(JWT.class);

		Mockito.when(
			mockJWT.getJWTClaimsSet()
		).thenReturn(
			JWTClaimsSet.parse(claimSetJSON)
		);

		return openIdConnectAuthenticationHandlerImpl.getUserInfoClaims(
			mockJWT);
	}

	private void _processAuthenticationResponse(
			HttpSession httpSession,
			OfflineOpenIdConnectSessionManager
				offlineOpenIdConnectSessionManager)
		throws Exception {

		OpenIdConnectAuthenticationHandlerImpl
			openIdConnectAuthenticationHandlerImpl =
				new OpenIdConnectAuthenticationHandlerImpl();

		AuthorizationServerMetadataResolver
			authorizationServerMetadataResolver = Mockito.mock(
				AuthorizationServerMetadataResolver.class);

		Mockito.when(
			authorizationServerMetadataResolver.resolveOIDCProviderMetadata(
				Mockito.any(), Mockito.anyLong(), Mockito.anyInt(),
				Mockito.anyLong())
		).thenReturn(
			Mockito.mock(OIDCProviderMetadata.class)
		);

		ReflectionTestUtil.setFieldValue(
			openIdConnectAuthenticationHandlerImpl,
			"_authorizationServerMetadataResolver",
			authorizationServerMetadataResolver);

		OAuthClientEntryLocalService oAuthClientEntryLocalService =
			Mockito.mock(OAuthClientEntryLocalService.class);

		Mockito.when(
			oAuthClientEntryLocalService.getOAuthClientEntry(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(OAuthClientEntry.class)
		);

		ReflectionTestUtil.setFieldValue(
			openIdConnectAuthenticationHandlerImpl,
			"_oAuthClientEntryLocalService", oAuthClientEntryLocalService);

		ReflectionTestUtil.setFieldValue(
			openIdConnectAuthenticationHandlerImpl,
			"_offlineOpenIdConnectSessionManager",
			offlineOpenIdConnectSessionManager);
		ReflectionTestUtil.setFieldValue(
			openIdConnectAuthenticationHandlerImpl, "_oidcUserInfoProcessor",
			Mockito.mock(OIDCUserInfoProcessor.class));
		ReflectionTestUtil.setFieldValue(
			openIdConnectAuthenticationHandlerImpl, "_portal",
			Mockito.mock(Portal.class));

		HttpSession authenticationHttpSession = Mockito.mock(HttpSession.class);
		State state = new State();

		Mockito.when(
			authenticationHttpSession.getAttribute(Mockito.anyString())
		).thenReturn(
			new OpenIdConnectAuthenticationSession(
				new CodeVerifier(), new Nonce(), RandomTestUtil.randomLong(),
				state)
		);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getQueryString()
		).thenReturn(
			StringBundler.concat(
				"code=", RandomTestUtil.randomString(), "&state=",
				state.getValue())
		);

		Mockito.when(
			httpServletRequest.getRequestURL()
		).thenReturn(
			new StringBuffer("http://" + RandomTestUtil.randomString())
		);

		Mockito.when(
			httpServletRequest.getSession()
		).thenReturn(
			authenticationHttpSession, httpSession
		);

		try (MockedStatic<OpenIdConnectClientInformationUtil>
				openIdConnectClientInformationUtilMockedStatic =
					Mockito.mockStatic(
						OpenIdConnectClientInformationUtil.class);
			MockedStatic<OpenIdConnectTokenRequestUtil>
				openIdConnectTokenRequestUtilMockedStatic = Mockito.mockStatic(
					OpenIdConnectTokenRequestUtil.class);
			MockedStatic<ServiceContextFactory>
				serviceContextFactoryMockedStatic = Mockito.mockStatic(
					ServiceContextFactory.class)) {

			openIdConnectClientInformationUtilMockedStatic.when(
				() ->
					OpenIdConnectClientInformationUtil.getOIDCClientInformation(
						Mockito.any(), Mockito.any(), Mockito.any(),
						Mockito.any())
			).thenReturn(
				Mockito.mock(OIDCClientInformation.class)
			);

			openIdConnectTokenRequestUtilMockedStatic.when(
				() -> OpenIdConnectTokenRequestUtil.request(
					Mockito.any(AuthenticationSuccessResponse.class),
					Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
					Mockito.any(), Mockito.anyInt(), Mockito.any())
			).thenReturn(
				new OIDCTokens(
					new PlainJWT(
						new JWTClaimsSet.Builder(
						).subject(
							RandomTestUtil.randomString()
						).build()),
					new BearerAccessToken(), null)
			);

			openIdConnectAuthenticationHandlerImpl.
				processAuthenticationResponse(
					httpServletRequest, Mockito.mock(HttpServletResponse.class),
					userId -> {
					});
		}
	}

	private String _requestUserInfoJSON() throws Exception {
		OpenIdConnectAuthenticationHandlerImpl
			openIdConnectAuthenticationHandlerImpl =
				new OpenIdConnectAuthenticationHandlerImpl();

		OIDCProviderMetadata oidcProviderMetadata = Mockito.mock(
			OIDCProviderMetadata.class);

		Mockito.when(
			oidcProviderMetadata.getUserInfoEndpointURI()
		).thenReturn(
			new URI("http://" + RandomTestUtil.randomString() + "/")
		);

		AccessToken accessToken = AccessToken.parse(
			JSONObjectUtils.parse(
				JSONUtil.put(
					"access_token", RandomTestUtil.randomString()
				).put(
					"token_type", "Bearer"
				).toString()));

		return ReflectionTestUtil.invoke(
			openIdConnectAuthenticationHandlerImpl, "_requestUserInfoJSON",
			new Class<?>[] {AccessToken.class, OIDCProviderMetadata.class},
			accessToken, oidcProviderMetadata);
	}

}