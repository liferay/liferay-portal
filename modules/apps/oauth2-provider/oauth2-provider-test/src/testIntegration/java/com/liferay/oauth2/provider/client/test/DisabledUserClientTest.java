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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
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
			webTarget.request(), getToken(_CLIENT_ID));

		Response response = builder.get();

		Assert.assertEquals(200, response.getStatus());

		webTarget = getWebTarget("/users");

		builder = authorize(
			webTarget.request(), getToken("oauthTestApplicationDisabled"));

		response = builder.get();

		Assert.assertEquals(403, response.getStatus());
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new DisabledUserTestPreparatorBundleActivator();
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private class DisabledUserTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			User disabledUser = addUser(
				CompanyLocalServiceUtil.getCompany(companyId));

			UserLocalServiceUtil.updateStatus(
				disabledUser.getUserId(), WorkflowConstants.STATUS_INACTIVE,
				new ServiceContext());

			User user = UserTestUtil.getAdminUser(companyId);

			registerJaxRsApplication(new TestApplication(), "users", null);

			createOAuth2Application(
				companyId, user, _CLIENT_ID, Arrays.asList("GET"));
			createOAuth2Application(
				companyId, disabledUser, "oauthTestApplicationDisabled",
				Arrays.asList("GET"));
		}

	}

}