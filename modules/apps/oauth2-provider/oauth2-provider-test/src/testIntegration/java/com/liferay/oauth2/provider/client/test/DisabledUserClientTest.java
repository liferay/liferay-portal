/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestApplication;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;

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
public class DisabledUserClientTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		WebTarget webTarget = getWebTarget("/users");

		Invocation.Builder builder = authorize(
			webTarget.request(), getToken("oauthTestApplication"));

		Response response = builder.get();

		Assert.assertEquals(200, response.getStatus());

		webTarget = getWebTarget("/users");

		builder = authorize(
			webTarget.request(), getToken("oauthTestApplicationDisabled"));

		response = builder.get();

		Assert.assertEquals(403, response.getStatus());
	}

	public static class DisabledUserTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User disabledUser = addUser(
				CompanyLocalServiceUtil.getCompany(defaultCompanyId));

			UserLocalServiceUtil.updateStatus(
				disabledUser.getUserId(), WorkflowConstants.STATUS_INACTIVE,
				new ServiceContext());

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			registerJaxRsApplication(new TestApplication(), "users", null);

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication",
				Arrays.asList("GET"));
			createOAuth2Application(
				defaultCompanyId, disabledUser, "oauthTestApplicationDisabled",
				Arrays.asList("GET"));
		}

	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new DisabledUserTestPreparatorBundleActivator();
	}

}