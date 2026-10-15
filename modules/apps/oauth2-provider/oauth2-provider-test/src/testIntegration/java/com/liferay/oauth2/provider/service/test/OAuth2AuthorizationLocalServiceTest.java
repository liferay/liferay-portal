/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.util.OAuth2TokenContentUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class OAuth2AuthorizationLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddOAuth2Authorization() throws Exception {
		String accessToken = RandomTestUtil.randomString();
		String refreshToken = RandomTestUtil.randomString();

		_oAuth2Authorization = _addOAuth2Authorization(
			accessToken, refreshToken);

		String accessTokenContent = OAuth2TokenContentUtil.getTokenContent(
			accessToken);

		Assert.assertEquals(
			accessTokenContent, _oAuth2Authorization.getAccessTokenContent());
		Assert.assertEquals(
			accessTokenContent.hashCode(),
			_oAuth2Authorization.getAccessTokenContentHash());

		String refreshTokenContent = OAuth2TokenContentUtil.getTokenContent(
			refreshToken);

		Assert.assertEquals(
			refreshTokenContent, _oAuth2Authorization.getRefreshTokenContent());
		Assert.assertEquals(
			refreshTokenContent.hashCode(),
			_oAuth2Authorization.getRefreshTokenContentHash());
	}

	@Test
	public void testFetchOAuth2AuthorizationByAccessTokenContent()
		throws Exception {

		String accessToken = RandomTestUtil.randomString();

		_oAuth2Authorization = _addOAuth2Authorization(
			accessToken, RandomTestUtil.randomString());

		Assert.assertEquals(
			_oAuth2Authorization,
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(accessToken));

		Assert.assertNull(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(
					RandomTestUtil.randomString()));
	}

	@Test
	public void testFetchOAuth2AuthorizationByRefreshTokenContent()
		throws Exception {

		String refreshToken = RandomTestUtil.randomString();

		_oAuth2Authorization = _addOAuth2Authorization(
			RandomTestUtil.randomString(), refreshToken);

		Assert.assertEquals(
			_oAuth2Authorization,
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByRefreshTokenContent(refreshToken));

		Assert.assertNull(
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByRefreshTokenContent(
					RandomTestUtil.randomString()));
	}

	private OAuth2Authorization _addOAuth2Authorization(
			String accessToken, String refreshToken)
		throws Exception {

		return _oAuth2AuthorizationLocalService.addOAuth2Authorization(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), accessToken, new Date(), new Date(),
			Collections.emptyList(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), refreshToken, new Date(),
			new Date());
	}

	@DeleteAfterTestRun
	private OAuth2Authorization _oAuth2Authorization;

	@Inject
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

}