/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.dynamic.registration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.client.test.BaseClientTestCase;
import com.liferay.oauth2.provider.client.test.BaseTestPreparatorBundleActivator;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;

import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Álvaro Saugar
 */
@FeatureFlag("LPD-63416")
@RunWith(Arquillian.class)
public class DynamicRegistrationServiceJWTAccessTokenTest
	extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpdateClientRegistration() throws Exception {
		String jwtAccessToken = _getJWTAccessToken();

		String clientName = RandomTestUtil.randomString();

		Response response = _updateClientRegistration(
			clientName, jwtAccessToken);

		Assert.assertEquals(200, response.getStatus());

		JSONObject jsonObject = parseJSONObject(response);

		Assert.assertEquals(clientName, jsonObject.getString("client_name"));

		revokeOAuth2AuthorizationByAccessToken(jwtAccessToken);

		response = _updateClientRegistration(
			RandomTestUtil.randomString(), jwtAccessToken);

		Assert.assertEquals(401, response.getStatus());

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				TestPropsValues.getCompanyId(), _CLIENT_ID);

		Assert.assertEquals(clientName, oAuth2Application.getName());
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new JWTAccessTokenTestPreparatorBundleActivator();
	}

	private String _getJWTAccessToken() {
		String jwtAccessToken = getToken(
			_CLIENT_ID, null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseTokenString);

		JwsJwtCompactConsumer jwsJwtCompactConsumer = new JwsJwtCompactConsumer(
			jwtAccessToken);

		JwtToken jwtToken = jwsJwtCompactConsumer.getJwtToken();

		Assert.assertEquals(
			_CLIENT_ID, jwtToken.getClaim(OAuthConstants.CLIENT_ID));

		return jwtAccessToken;
	}

	private Response _updateClientRegistration(
		String clientName, String jwtAccessToken) {

		WebTarget webTarget = getOAuth2WebTarget();

		WebTarget registerWebTarget = webTarget.path("register/" + _CLIENT_ID);

		Invocation.Builder invocationBuilder = authorize(
			registerWebTarget.request(), jwtAccessToken);

		return invocationBuilder.method(
			"put",
			Entity.json(
				JSONUtil.put(
					"client_name", clientName
				).toString()));
	}

	private static final String _CLIENT_ID =
		"oauthDynamicRegisterJWTApplication";

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	private User _user;

	private class JWTAccessTokenTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			autoCloseables.add(
				new ConfigurationTemporarySwapper(
					"com.liferay.oauth2.provider.rest.internal.configuration." +
						"OAuth2AuthorizationServerConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"oauth2.authorization.server.issue.jwt.access.token",
						true
					).put(
						"oauth2.authorization.server.jwt.access.token." +
							"signing.json.web.key",
						JWTAssertionUtil.JWK
					).build()));

			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, _user, _CLIENT_ID,
				Collections.singletonList(GrantType.RESOURCE_OWNER_PASSWORD),
				Arrays.asList("everything", "everything.read"));
		}

	}

}