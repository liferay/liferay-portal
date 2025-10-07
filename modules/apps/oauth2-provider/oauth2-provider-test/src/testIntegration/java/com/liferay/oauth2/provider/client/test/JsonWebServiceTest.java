/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Carlos Sierra Andrés
 */
@RunWith(Arquillian.class)
public class JsonWebServiceTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		WebTarget webTarget = getJsonWebTarget(
			"company", "get-company-by-virtual-host");

		Invocation.Builder invocationBuilder = webTarget.request();

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

		formData.putSingle("virtualHost", "testcompany.xyz");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				403,
				invocationBuilder.post(
					Entity.form(formData)
				).getStatus());
		}

		String tokenString = getToken(
			"oauthTestApplicationRO", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD),
			this::parseTokenString);

		invocationBuilder = authorize(webTarget.request(), tokenString);

		Response response = invocationBuilder.post(Entity.form(formData));

		JSONObject jsonObject = new JSONObjectImpl(
			response.readEntity(String.class));

		Assert.assertEquals("testcompany", jsonObject.getString("webId"));

		webTarget = getJsonWebTarget("region", "add-region");

		invocationBuilder = authorize(webTarget.request(), tokenString);

		formData = new MultivaluedHashMap<>();

		formData.putSingle("active", "true");
		formData.putSingle("countryId", "0");
		formData.putSingle("name", "'aName'");
		formData.putSingle("position", "0");
		formData.putSingle("regionCode", "'aRegionCode'");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			response = invocationBuilder.post(Entity.form(formData));

			Assert.assertEquals(403, response.getStatus());
		}

		String token = getToken(
			"oauthTestApplicationRW", null,
			getResourceOwnerPasswordBiFunction(
				_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
				"everything.write"),
			this::parseTokenString);

		invocationBuilder = authorize(webTarget.request(), token);

		response = invocationBuilder.post(Entity.form(formData));

		Assert.assertEquals(404, response.getStatus());

		webTarget = getJsonWebTarget("company", "get-company-by-virtual-host");

		invocationBuilder = authorize(webTarget.request(), token);

		formData = new MultivaluedHashMap<>();

		formData.putSingle("virtualHost", "testcompany.xyz");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"portal_web.docroot.errors.code_jsp", LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				403,
				invocationBuilder.post(
					Entity.form(formData)
				).getStatus());
		}
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new JsonWebServiceTestPreparatorBundleActivator();
	}

	private User _user;

	private class JsonWebServiceTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			createCompany("testcompany");

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationRO",
				Collections.singletonList("everything.read"));

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationRW",
				Arrays.asList("everything.read", "everything.write"));
		}

	}

}