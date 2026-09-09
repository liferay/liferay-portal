/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.authorization;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.ai.hub.cell.rest.internal.web.cache.AIHubCellAccessTokenWebCacheItem;
import com.liferay.ai.hub.cell.rest.internal.web.cache.AIHubCellUserTokenWebCacheItem;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Rafael Uen
 */
public class AIHubCellAuthorizationTokenProviderImplTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_aiHubCellConfiguration.serviceURL()
		).thenReturn(
			_SERVICE_URL
		);

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				AIHubCellConfiguration.class, _COMPANY_ID)
		).thenReturn(
			_aiHubCellConfiguration
		);

		OAuth2ApplicationLocalService oAuth2ApplicationLocalService =
			Mockito.mock(OAuth2ApplicationLocalService.class);

		Mockito.when(
			oAuth2ApplicationLocalService.
				getOAuth2ApplicationByExternalReferenceCode(
					"AI-HUB-CELL", _COMPANY_ID)
		).thenReturn(
			_oAuth2Application
		);

		ReflectionTestUtil.setFieldValue(
			_aiHubCellAuthorizationTokenProviderImpl, "_configurationProvider",
			configurationProvider);
		ReflectionTestUtil.setFieldValue(
			_aiHubCellAuthorizationTokenProviderImpl, "_localOAuthClient",
			_localOAuthClient);
		ReflectionTestUtil.setFieldValue(
			_aiHubCellAuthorizationTokenProviderImpl,
			"_oAuth2ApplicationLocalService", oAuth2ApplicationLocalService);
	}

	@Test
	public void testGetAuthorizationTokenJSONObject() throws Exception {
		String accessToken = RandomTestUtil.randomString();
		String scope = RandomTestUtil.randomString();
		String userToken = RandomTestUtil.randomString();

		try (MockedStatic<AIHubCellAccessTokenWebCacheItem>
				aiHubCellAccessTokenWebCacheItemMockedStatic =
					Mockito.mockStatic(AIHubCellAccessTokenWebCacheItem.class);
			MockedStatic<AIHubCellUserTokenWebCacheItem>
				aiHubCellUserTokenWebCacheItemMockedStatic = Mockito.mockStatic(
					AIHubCellUserTokenWebCacheItem.class)) {

			aiHubCellAccessTokenWebCacheItemMockedStatic.when(
				() -> AIHubCellAccessTokenWebCacheItem.get(
					_aiHubCellConfiguration, _COMPANY_ID)
			).thenReturn(
				JSONUtil.put(
					"access_token", accessToken
				).put(
					"scope", scope
				)
			);

			aiHubCellUserTokenWebCacheItemMockedStatic.when(
				() -> AIHubCellUserTokenWebCacheItem.get(
					_localOAuthClient, _oAuth2Application, _USER_ID)
			).thenReturn(
				userToken
			);

			JSONObject jsonObject =
				_aiHubCellAuthorizationTokenProviderImpl.
					getAuthorizationTokenJSONObject(_COMPANY_ID, _USER_ID);

			Assert.assertEquals(
				accessToken, jsonObject.getString("accessToken"));
			Assert.assertEquals(scope, jsonObject.getString("scope"));
			Assert.assertEquals(
				_SERVICE_URL, jsonObject.getString("serviceURL"));
			Assert.assertEquals(userToken, jsonObject.getString("userToken"));
		}
	}

	@Test(expected = PortalException.class)
	public void testGetAuthorizationTokenJSONObjectWhenAccessTokenIsMissing()
		throws Exception {

		try (MockedStatic<AIHubCellAccessTokenWebCacheItem>
				aiHubCellAccessTokenWebCacheItemMockedStatic =
					Mockito.mockStatic(
						AIHubCellAccessTokenWebCacheItem.class)) {

			aiHubCellAccessTokenWebCacheItemMockedStatic.when(
				() -> AIHubCellAccessTokenWebCacheItem.get(
					_aiHubCellConfiguration, _COMPANY_ID)
			).thenReturn(
				null
			);

			_aiHubCellAuthorizationTokenProviderImpl.
				getAuthorizationTokenJSONObject(_COMPANY_ID, _USER_ID);
		}
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _SERVICE_URL = RandomTestUtil.randomString();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private final AIHubCellAuthorizationTokenProviderImpl
		_aiHubCellAuthorizationTokenProviderImpl =
			new AIHubCellAuthorizationTokenProviderImpl();
	private final AIHubCellConfiguration _aiHubCellConfiguration = Mockito.mock(
		AIHubCellConfiguration.class);
	private final LocalOAuthClient _localOAuthClient = Mockito.mock(
		LocalOAuthClient.class);
	private final OAuth2Application _oAuth2Application = Mockito.mock(
		OAuth2Application.class);

}