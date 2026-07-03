/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.dynamic.registration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.client.test.BaseClientTestCase;
import com.liferay.oauth2.provider.client.test.BaseTestPreparatorBundleActivator;
import com.liferay.oauth2.provider.constants.OAuth2ApplicationConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jorge García Jiménez
 */
@FeatureFlag("LPD-63416")
@RunWith(Arquillian.class)
public class DynamicRegistrationServiceTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_auditMessages = new ArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(
			DynamicRegistrationServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", "*");

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage), properties);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		super.tearDown();
	}

	@Test
	public void testDeleteClientRegistration() throws Exception {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				TestPropsValues.getCompanyId(), "oauthDeleteMeApplication");

		WebTarget registerWebTarget = getRegisterWebTarget(
			oAuth2Application.getClientId());

		Invocation.Builder invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					TestPropsValues.getCompanyId(),
					"oauthDynamicRegisterTestApplication")));

		Response response = invocationBuilder.delete();

		Assert.assertEquals(403, response.getStatus());

		invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(_getDynamicRegistratorOAuth2Application()));

		response = invocationBuilder.delete();

		Assert.assertEquals(204, response.getStatus());

		response = invocationBuilder.delete();

		Assert.assertEquals(401, response.getStatus());
	}

	@Test
	public void testRegister() throws Exception {
		WebTarget registerWebTarget = getRegisterWebTarget();

		Invocation.Builder invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(
				_oAuth2ApplicationLocalService.fetchOAuth2Application(
					TestPropsValues.getCompanyId(),
					"oauthDynamicRegisterTestApplication")));

		Response response = invocationBuilder.method(
			"post",
			Entity.json(
				JSONUtil.put(
					"client_name", RandomTestUtil.randomString()
				).toString()));

		Assert.assertEquals(401, response.getStatus());

		String clientName = RandomTestUtil.randomString();

		String scope =
			RandomTestUtil.randomString() + StringPool.SPACE +
				RandomTestUtil.randomString();

		JSONObject jsonObject = _createAuthenticatedRegistrationJSONObject(
			clientName, scope);

		response = invocationBuilder.method(
			"post", Entity.json(jsonObject.toString()));

		Assert.assertEquals(401, response.getStatus());

		OAuth2Application oAuth2Application =
			_getDynamicRegistratorOAuth2Application();

		invocationBuilder = authorize(
			registerWebTarget.request(), _getToken(oAuth2Application));

		response = invocationBuilder.method(
			"post", Entity.json(jsonObject.toString()));

		Assert.assertEquals(201, response.getStatus());

		JSONObject responseJSONObject = parseJSONObject(response);

		Assert.assertEquals(
			clientName, responseJSONObject.getString("client_name"));

		String[] expectedScopes = StringUtil.split(scope, CharPool.SPACE);

		Arrays.sort(expectedScopes);

		String[] actualScopes = StringUtil.split(
			responseJSONObject.getString("scope"), CharPool.SPACE);

		Arrays.sort(actualScopes);

		Assert.assertArrayEquals(expectedScopes, actualScopes);

		String clientId = responseJSONObject.getString(
			OAuthConstants.CLIENT_ID);

		jsonObject.put(
			"response_types",
			Collections.singletonList(OAuthConstants.CODE_RESPONSE_TYPE));

		response = invocationBuilder.method(
			"post", Entity.json(jsonObject.toString()));

		Assert.assertEquals(400, response.getStatus());

		Assert.assertEquals("invalid_client_metadata", parseError(response));

		registerWebTarget = getRegisterWebTarget(clientId);

		invocationBuilder = authorize(
			registerWebTarget.request(), _getToken(oAuth2Application));

		invocationBuilder.header("Origin", RandomTestUtil.randomString());

		response = invocationBuilder.get();

		Assert.assertEquals(200, response.getStatus());

		responseJSONObject = parseJSONObject(response);

		Assert.assertEquals(
			clientName, responseJSONObject.getString("client_name"));

		Assert.assertNull(
			response.getHeaderString("Access-Control-Allow-Origin"));

		AuditMessage addAuditMessage = _fetchAuditMessage(
			"DYNAMIC_REGISTRATION_ADD");

		Assert.assertEquals(
			OAuth2Application.class.getName(), addAuditMessage.getClassName());
		Assert.assertEquals(clientId, addAuditMessage.getClassPK());

		JSONObject addAdditionalInfoJSONObject =
			addAuditMessage.getAdditionalInfo();

		Assert.assertEquals(
			clientName, addAdditionalInfoJSONObject.getString("clientName"));

		JSONArray grantTypesJSONArray =
			addAdditionalInfoJSONObject.getJSONArray("grantTypes");

		Assert.assertEquals(1, grantTypesJSONArray.length());
		Assert.assertEquals(
			OAuthConstants.CLIENT_CREDENTIALS_GRANT,
			grantTypesJSONArray.getString(0));

		Assert.assertEquals(
			"authenticated", addAdditionalInfoJSONObject.getString("mode"));

		String[] auditScopes = StringUtil.split(
			addAdditionalInfoJSONObject.getString("scope"), CharPool.SPACE);

		Arrays.sort(auditScopes);

		Assert.assertArrayEquals(expectedScopes, auditScopes);

		AuditMessage rejectAuditMessage = _fetchAuditMessage(
			"DYNAMIC_REGISTRATION_REJECT");

		Assert.assertEquals(
			OAuth2Application.class.getName(),
			rejectAuditMessage.getClassName());

		JSONObject rejectAdditionalInfoJSONObject =
			rejectAuditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"invalid_client_metadata",
			rejectAdditionalInfoJSONObject.getString("error"));
	}

	@Test
	public void testRegisterWithInvalidBearerToken() throws Exception {
		WebTarget registerWebTarget = getRegisterWebTarget();

		Invocation.Builder invocationBuilder = registerWebTarget.request();

		invocationBuilder.header(
			"Authorization", "Bearer " + RandomTestUtil.randomString());

		Response response = invocationBuilder.method(
			"post",
			Entity.json(
				JSONUtil.put(
					"client_name", RandomTestUtil.randomString()
				).toString()));

		Assert.assertEquals(401, response.getStatus());

		AuditMessage auditMessage = _fetchAuditMessage(
			"DYNAMIC_REGISTRATION_REJECT");

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"invalid_token", additionalInfoJSONObject.getString("error"));
		Assert.assertEquals(
			"authenticated", additionalInfoJSONObject.getString("mode"));
	}

	@Test
	public void testUpdateClientRegistration() throws Exception {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				TestPropsValues.getCompanyId(), "oauthDeleteMeApplication");

		WebTarget registerWebTarget = getRegisterWebTarget(
			oAuth2Application.getClientId());

		Invocation.Builder invocationBuilder = authorize(
			registerWebTarget.request(),
			_getToken(_getDynamicRegistratorOAuth2Application()));

		String clientName = RandomTestUtil.randomString();

		Response response = invocationBuilder.method(
			"put",
			Entity.json(
				_createAuthenticatedRegistrationJSONObject(
					clientName, RandomTestUtil.randomString()
				).toString()));

		Assert.assertEquals(200, response.getStatus());

		JSONObject jsonObject = parseJSONObject(response);

		Assert.assertEquals(clientName, jsonObject.getString("client_name"));
	}

	protected static WebTarget getRegisterWebTarget() {
		WebTarget webTarget = getOAuth2WebTarget();

		return webTarget.path("register");
	}

	protected static WebTarget getRegisterWebTarget(String target) {
		WebTarget webTarget = getRegisterWebTarget();

		return webTarget.path(target);
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new DynamicRegistrationServiceTestPreparatorBundleActivator();
	}

	private JSONObject _createAuthenticatedRegistrationJSONObject(
		String clientName, String scope) {

		return JSONUtil.put(
			"client_name", clientName
		).put(
			"grant_types",
			new String[] {OAuthConstants.CLIENT_CREDENTIALS_GRANT}
		).put(
			"logo_uri", RandomTestUtil.randomString()
		).put(
			"redirect_uris",
			new String[] {
				StringBundler.concat(
					Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(),
					StringPool.SLASH, RandomTestUtil.randomString()),
				StringBundler.concat(
					Http.HTTPS_WITH_SLASH, RandomTestUtil.randomString(),
					StringPool.SLASH, RandomTestUtil.randomString())
			}
		).put(
			"scope", scope
		);
	}

	private AuditMessage _fetchAuditMessage(String eventType) {
		for (AuditMessage auditMessage : _auditMessages) {
			if (eventType.equals(auditMessage.getEventType())) {
				return auditMessage;
			}
		}

		return null;
	}

	private OAuth2Application _getDynamicRegistratorOAuth2Application()
		throws Exception {

		DynamicQuery dynamicQuery =
			_oAuth2ApplicationLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(TestPropsValues.getCompanyId()));

		Property nameProperty = PropertyFactoryUtil.forName("name");

		dynamicQuery.add(
			nameProperty.eq(
				OAuth2ApplicationConstants.NAME_DYNAMIC_REGISTRATOR));

		List<OAuth2Application> oAuth2Applications =
			_oAuth2ApplicationLocalService.dynamicQuery(dynamicQuery);

		Assert.assertFalse(oAuth2Applications.isEmpty());

		return oAuth2Applications.get(0);
	}

	private String _getToken(OAuth2Application oAuth2Application) {
		WebTarget tokenWebTarget = getTokenWebTarget();

		Invocation.Builder invocationBuilder = tokenWebTarget.request();

		String tokenString = parseTokenString(
			invocationBuilder.post(
				Entity.form(
					new MultivaluedHashMap<>(
						HashMapBuilder.put(
							OAuthConstants.CLIENT_ID,
							oAuth2Application.getClientId()
						).put(
							OAuthConstants.CLIENT_SECRET,
							oAuth2Application.getClientSecret()
						).put(
							OAuthConstants.GRANT_TYPE,
							OAuthConstants.CLIENT_CREDENTIALS_GRANT
						).build()))));

		Assert.assertNotNull(tokenString);

		return tokenString;
	}

	private List<AuditMessage> _auditMessages;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	private class DynamicRegistrationServiceTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			User user = UserTestUtil.getAdminUser(companyId);

			createOAuth2Application(
				companyId, user, "oauthDynamicRegisterTestApplication");
			createOAuth2Application(
				companyId, user, "oauthDeleteMeApplication");
		}

	}

}