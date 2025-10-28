/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.DuplicateOpenIdConnectUserException;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.OpenIdConnectUserIssuerException;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.OpenIdConnectUserSubjectException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectUserLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class OpenIdConnectUserLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddOpenIdConnectUser() throws Exception {
		OpenIdConnectUser openIdConnectUser =
			_openIdConnectUserLocalService.addOpenIdConnectUser(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

		_assertPortalException(
			DuplicateOpenIdConnectUserException.class,
			() -> _openIdConnectUserLocalService.addOpenIdConnectUser(
				TestPropsValues.getUserId(), openIdConnectUser.getIssuer(),
				openIdConnectUser.getSubject()));

		_assertPortalException(
			OpenIdConnectUserIssuerException.class,
			() -> _openIdConnectUserLocalService.addOpenIdConnectUser(
				TestPropsValues.getUserId(), StringPool.BLANK,
				RandomTestUtil.randomString()));
		_assertPortalException(
			OpenIdConnectUserSubjectException.class,
			() -> _openIdConnectUserLocalService.addOpenIdConnectUser(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				StringPool.BLANK));
	}

	@Test
	public void testFetchOpenIdConnectUser() throws Exception {
		Assert.assertNull(
			_openIdConnectUserLocalService.fetchOpenIdConnectUser(
				TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()));

		OpenIdConnectUser openIdConnectUser =
			_openIdConnectUserLocalService.addOpenIdConnectUser(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

		Assert.assertEquals(
			openIdConnectUser,
			_openIdConnectUserLocalService.fetchOpenIdConnectUser(
				openIdConnectUser.getCompanyId(), openIdConnectUser.getIssuer(),
				openIdConnectUser.getSubject()));
	}

	private void _assertPortalException(
		Class<? extends PortalException> expectedPortalException,
		UnsafeRunnable<PortalException> unsafeRunnable) {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertEquals(
				expectedPortalException, portalException.getClass());
		}
	}

	@Inject
	private OpenIdConnectUserLocalService _openIdConnectUserLocalService;

}