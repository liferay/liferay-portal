/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.resource.v1_0.test;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.net.HttpURLConnection;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class AuthorizationTokenResourceTest
	extends BaseAuthorizationTokenResourceTestCase {

	@After
	public void tearDown() {
		try {
			ConfigurationTestUtil.deleteConfiguration(
				AIHubCellConfiguration.class.getName());

			WebCachePoolUtil.clear();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	@Test
	public void testPostAuthorizationToken() throws Exception {
		User user = TestPropsValues.getUser();

		String portalURL =
			"http://localhost:" + PortalUtil.getPortalServerPort(false);

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.addOAuth2Application(
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				List.of(GrantType.CLIENT_CREDENTIALS), "client_secret_post",
				user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.WEB_APPLICATION.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), "",
				List.of(), portalURL, 0, null, "AI Hub", "", List.of(portalURL),
				false, Arrays.asList("Liferay.AI.Hub.REST.everything"), false,
				new ServiceContext());

		ConfigurationTestUtil.saveConfiguration(
			AIHubCellConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"clientId", oAuth2Application.getClientId()
			).put(
				"clientSecret", oAuth2Application.getClientSecret()
			).put(
				"companyId", user.getCompanyId()
			).put(
				"serviceURL", portalURL
			).build());

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			null, "ai-hub-cell/v1.0/authorization-tokens", Http.Method.POST);

		Assert.assertTrue(jsonObject1.has("accessToken"));
		Assert.assertTrue(jsonObject1.has("scope"));
		Assert.assertTrue(jsonObject1.has("userToken"));

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			null, "ai-hub-cell/v1.0/authorization-tokens", Http.Method.POST);

		Assert.assertEquals(
			jsonObject1.getString("accessToken"),
			jsonObject2.getString("accessToken"));

		HTTPTestUtil.customize(
		).withGuest(
		).apply(
			() -> Assert.assertEquals(
				HttpURLConnection.HTTP_FORBIDDEN,
				HTTPTestUtil.invokeToHttpCode(
					null, "ai-hub-cell/v1.0/authorization-tokens",
					Http.Method.POST))
		);
	}

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}