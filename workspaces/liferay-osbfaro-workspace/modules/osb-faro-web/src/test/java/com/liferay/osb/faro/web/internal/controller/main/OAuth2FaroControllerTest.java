/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.osb.faro.web.internal.model.display.main.TokenDisplay;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class OAuth2FaroControllerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_oAuth2FaroController, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_oAuth2FaroController, "_localOAuthClient", _localOAuthClient);
		ReflectionTestUtil.setFieldValue(
			_oAuth2FaroController, "_oAuth2ApplicationLocalService",
			_oAuth2ApplicationLocalService);
		ReflectionTestUtil.setFieldValue(
			_oAuth2FaroController, "_oAuth2AuthorizationLocalService",
			_oAuth2AuthorizationLocalService);

		_setUpPermissionChecker();
	}

	@Test
	public void testNewToken() throws Exception {
		Mockito.when(
			_localOAuthClient.requestTokens(_mockOAuth2Application(), 100L)
		).thenReturn(
			"{\"access_token\": \"abc\"}"
		);

		Mockito.when(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("abc")
		).thenReturn(
			_mockOAuth2Authorization("abc")
		);

		TokenDisplay tokenDisplay = _oAuth2FaroController.newToken(
			1, null, "demandbase", null);

		Assert.assertEquals("abc", tokenDisplay.getToken());
	}

	@Test
	public void testNewTokenSetsAccessTokenExpirationDate() throws Exception {
		OAuth2Authorization oAuth2Authorization = _mockOAuth2Authorization(
			"abc");

		Mockito.when(
			_localOAuthClient.requestTokens(_mockOAuth2Application(), 100L)
		).thenReturn(
			"{\"access_token\": \"abc\"}"
		);

		Mockito.when(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("abc")
		).thenReturn(
			oAuth2Authorization
		);

		_oAuth2FaroController.newToken(1, 2592000L, "demandbase", null);

		Mockito.verify(
			oAuth2Authorization
		).setAccessTokenExpirationDate(
			new Date(_ACCESS_TOKEN_CREATE_TIME + (2592000L * 1000))
		);

		Mockito.verify(
			_oAuth2AuthorizationLocalService
		).updateOAuth2Authorization(
			oAuth2Authorization
		);
	}

	@Test
	public void testNewTokenSetsRefreshTokenExpirationDate() throws Exception {
		OAuth2Authorization oAuth2Authorization = _mockOAuth2Authorization(
			"abc");

		Mockito.when(
			oAuth2Authorization.getRefreshTokenCreateDate()
		).thenReturn(
			new Date(_ACCESS_TOKEN_CREATE_TIME)
		);

		Mockito.when(
			_localOAuthClient.requestTokens(_mockOAuth2Application(), 100L)
		).thenReturn(
			"{\"access_token\": \"abc\"}"
		);

		Mockito.when(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("abc")
		).thenReturn(
			oAuth2Authorization
		);

		_oAuth2FaroController.newToken(1, 2592000L, "demandbase", null);

		Mockito.verify(
			oAuth2Authorization
		).setRefreshTokenExpirationDate(
			new Date(_ACCESS_TOKEN_CREATE_TIME + (2592000L * 1000))
		);
	}

	@Test(expected = PortalException.class)
	public void testNewTokenWhenAccessTokenIsNotCreated() throws Exception {
		Mockito.when(
			_localOAuthClient.requestTokens(_mockOAuth2Application(), 100L)
		).thenReturn(
			null
		);

		_oAuth2FaroController.newToken(1, null, "demandbase", null);
	}

	@Test(expected = PortalException.class)
	public void testNewTokenWhenOAuth2AuthorizationIsNotFound()
		throws Exception {

		Mockito.when(
			_localOAuthClient.requestTokens(_mockOAuth2Application(), 100L)
		).thenReturn(
			"{\"access_token\": \"abc\"}"
		);

		Mockito.when(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("abc")
		).thenReturn(
			null
		);

		_oAuth2FaroController.newToken(1, null, "demandbase", null);
	}

	@Test
	public void testRevokeToken() throws Exception {
		OAuth2Authorization oAuth2Authorization = Mockito.mock(
			OAuth2Authorization.class);

		Mockito.when(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("abc")
		).thenReturn(
			oAuth2Authorization
		);

		_oAuth2FaroController.revokeToken(1, "abc");

		Mockito.verify(
			_oAuth2AuthorizationLocalService
		).deleteOAuth2Authorization(
			oAuth2Authorization
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRevokeTokenWhenTokenIsNotFound() throws Exception {
		_oAuth2FaroController.revokeToken(1, "nonexistent");
	}

	private OAuth2Application _mockOAuth2Application() {
		OAuth2Application oAuth2Application = Mockito.mock(
			OAuth2Application.class);

		Mockito.when(
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				Mockito.anyLong(), Mockito.eq("DEMANDBASE"))
		).thenReturn(
			oAuth2Application
		);

		Mockito.when(
			oAuth2Application.getClientCredentialUserId()
		).thenReturn(
			100L
		);

		return oAuth2Application;
	}

	private OAuth2Authorization _mockOAuth2Authorization(String accessToken) {
		OAuth2Authorization oAuth2Authorization = Mockito.mock(
			OAuth2Authorization.class);

		Mockito.when(
			oAuth2Authorization.getAccessTokenContent()
		).thenReturn(
			accessToken
		);

		Mockito.when(
			oAuth2Authorization.getAccessTokenCreateDate()
		).thenReturn(
			new Date(_ACCESS_TOKEN_CREATE_TIME)
		);

		Mockito.when(
			oAuth2Authorization.getExpandoBridge()
		).thenReturn(
			Mockito.mock(ExpandoBridge.class)
		);

		Mockito.when(
			_oAuth2AuthorizationLocalService.updateOAuth2Authorization(
				oAuth2Authorization)
		).thenReturn(
			oAuth2Authorization
		);

		return oAuth2Authorization;
	}

	private void _setUpPermissionChecker() {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.getUser()
		).thenReturn(
			Mockito.mock(User.class)
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	private static final long _ACCESS_TOKEN_CREATE_TIME = 1000000000000L;

	private final LocalOAuthClient _localOAuthClient = Mockito.mock(
		LocalOAuthClient.class);
	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService =
		Mockito.mock(OAuth2ApplicationLocalService.class);
	private final OAuth2AuthorizationLocalService
		_oAuth2AuthorizationLocalService = Mockito.mock(
			OAuth2AuthorizationLocalService.class);
	private final OAuth2FaroController _oAuth2FaroController =
		new OAuth2FaroController();

}