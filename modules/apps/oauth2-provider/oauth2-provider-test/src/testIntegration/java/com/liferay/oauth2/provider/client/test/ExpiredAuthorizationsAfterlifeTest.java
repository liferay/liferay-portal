/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Jose L. Bango
 */
@RunWith(Arquillian.class)
public class ExpiredAuthorizationsAfterlifeTest extends BaseClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		_oAuth2AuthorizationLocalService.deleteExpiredOAuth2Authorizations();

		OAuth2Authorization oAuth2Authorization1 =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("accessToken1");

		Assert.assertNull(oAuth2Authorization1);

		OAuth2Authorization oAuth2Authorization2 =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("accessToken2");

		Assert.assertNotNull(oAuth2Authorization2);

		OAuth2Authorization oAuth2Authorization3 =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent("accessToken3");

		Assert.assertNotNull(oAuth2Authorization3);
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new ExpiredAuthorizationsAfterlifeTestPreparatorBundleActivator();
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	private class ExpiredAuthorizationsAfterlifeTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			updateOAuth2ProviderConfiguration(
				MapUtil.singletonDictionary(
					"oauth2.expired.authorizations.afterlife.duration",
					Time.HOUR * 12 / Time.SECOND));

			long companyId = TestPropsValues.getCompanyId();

			User user = UserTestUtil.getAdminUser(companyId);

			OAuth2Application oAuth2Application = createOAuth2Application(
				companyId, user, _CLIENT_ID, Arrays.asList("everything.read"));

			addOAuth2Authorization(
				companyId, user, oAuth2Application, "accessToken1", new Date(),
				new Date(System.currentTimeMillis() - (Time.HOUR * 15)));
			addOAuth2Authorization(
				companyId, user, oAuth2Application, "accessToken2", new Date(),
				new Date(System.currentTimeMillis() - (Time.HOUR * 10)));
			addOAuth2Authorization(
				companyId, user, oAuth2Application, "accessToken3", new Date(),
				new Date(System.currentTimeMillis() + (Time.HOUR * 1)));
		}

	}

}