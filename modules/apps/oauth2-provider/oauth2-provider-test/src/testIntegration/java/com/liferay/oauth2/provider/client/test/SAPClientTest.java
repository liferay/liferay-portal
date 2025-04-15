/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestSAPApplication;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

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
public class SAPClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		WebTarget webTarget = getWebTarget("SAP/AUTHORIZED_OAUTH2_SAP");

		Invocation.Builder builder = authorize(
			webTarget.request(), getToken("oauthTestApplication"));

		Assert.assertTrue(builder.get(Boolean.class));

		webTarget = getWebTarget("SAP/CUSTOM_SAP");

		builder = authorize(
			webTarget.request(), getToken("oauthTestApplication"));

		Assert.assertFalse(builder.get(Boolean.class));

		webTarget = getWebTarget("CUSTOM_SAP/AUTHORIZED_OAUTH2_SAP");

		builder = authorize(
			webTarget.request(), getToken("oauthTestApplication"));

		Assert.assertFalse(builder.get(Boolean.class));

		webTarget = getWebTarget("CUSTOM_SAP/CUSTOM_SAP");

		builder = authorize(
			webTarget.request(), getToken("oauthTestApplication"));

		Assert.assertTrue(builder.get(Boolean.class));
	}

	public static class SAPTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			registerJaxRsApplication(
				new TestSAPApplication(), "SAP",
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.name", TestSAPApplication.class.getName()
				).build());

			registerJaxRsApplication(
				new TestSAPApplication(), "CUSTOM_SAP",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.service.access.policy.name", "CUSTOM_SAP"
				).put(
					"osgi.jaxrs.name", "custom-sap-application"
				).build());

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication",
				Collections.singletonList("GET"));

			createServiceAccessProfile(
				user.getUserId(), "#is*", false, true, "CUSTOM_SAP");
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new SAPTestPreparatorBundleActivator();
	}

}