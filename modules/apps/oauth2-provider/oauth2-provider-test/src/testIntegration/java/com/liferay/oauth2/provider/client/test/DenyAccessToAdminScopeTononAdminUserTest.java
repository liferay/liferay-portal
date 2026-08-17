/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class DenyAccessToAdminScopeTononAdminUserTest
	extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() {
		WebTarget webTarget = getWebTarget();

		webTarget = webTarget.path(
			"o/headless-admin-workflow/v1.0/workflow-definitions");

		String tokenString = getToken(
			_CLIENT_ID_ADMIN, null, this::getClientCredentialsResponse,
			this::parseTokenString);

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), tokenString);

		Response response = invocationBuilder.get();

		Assert.assertEquals(200, response.getStatus());

		tokenString = getToken(_CLIENT_ID_NONADMIN);

		invocationBuilder = authorize(webTarget.request(), tokenString);

		response = invocationBuilder.get();

		Assert.assertEquals(400, response.getStatus());
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new DenyAccessToAdminScopeTononAdminUserTestPreparatorBundleActivator();
	}

	private static final String _CLIENT_ID_ADMIN =
		RandomTestUtil.randomString();

	private static final String _CLIENT_ID_NONADMIN =
		RandomTestUtil.randomString();

	@Inject
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	private class
		DenyAccessToAdminScopeTononAdminUserTestPreparatorBundleActivator
			extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long companyId = TestPropsValues.getCompanyId();

			OAuth2Application oauth2AdminApp = createOAuth2Application(
				companyId, UserTestUtil.getAdminUser(companyId),
				_CLIENT_ID_ADMIN,
				Collections.singletonList(
					"Liferay.Headless.Admin.Workflow.everything"));

			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				oauth2AdminApp.getCompanyId(),
				oauth2AdminApp.getOAuth2ApplicationScopeAliasesId(),
				"Liferay.Headless.Admin.Workflow",
				"com.liferay.headless.admin.workflow.impl", "GET",
				Collections.singletonList(
					"Liferay.Headless.Admin.Workflow.everything"));

			OAuth2Application oauth2RegularUserApp = createOAuth2Application(
				companyId, UserTestUtil.addUser(), _CLIENT_ID_NONADMIN,
				Collections.singletonList(
					"Liferay.Headless.Admin.Workflow.everything"));

			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				oauth2RegularUserApp.getCompanyId(),
				oauth2RegularUserApp.getOAuth2ApplicationScopeAliasesId(),
				"Liferay.Headless.Admin.Workflow",
				"com.liferay.headless.admin.workflow.impl", "GET",
				Collections.singletonList(
					"Liferay.Headless.Admin.Workflow.everything"));
		}

	}

}