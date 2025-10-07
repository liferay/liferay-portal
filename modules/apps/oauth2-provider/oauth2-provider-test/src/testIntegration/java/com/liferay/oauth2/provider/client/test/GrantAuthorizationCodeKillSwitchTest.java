/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.internal.test.TestAnnotatedApplication;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class GrantAuthorizationCodeKillSwitchTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		Assert.assertEquals(
			"unauthorized_client",
			parseErrorParameter(
				getCodeResponse(
					_user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD,
					null,
					getCodeFunction(
						webTarget -> webTarget.queryParam(
							"client_id", "oauthTestApplicationCode"
						).queryParam(
							"response_type", "code"
						)))));
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new GrantKillClientCredentialsSwitchTestPreparatorBundleActivator();
	}

	private User _user;

	private class GrantKillClientCredentialsSwitchTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			updateOAuth2ProviderConfiguration(
				MapUtil.singletonDictionary(
					"oauth2.allow.authorization.code.grant", false));

			long companyId = TestPropsValues.getCompanyId();

			_user = UserTestUtil.getAdminUser(companyId);

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.scope.checker.type", "annotations"
				).build());

			createOAuth2Application(
				companyId, _user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything"));
		}

	}

}