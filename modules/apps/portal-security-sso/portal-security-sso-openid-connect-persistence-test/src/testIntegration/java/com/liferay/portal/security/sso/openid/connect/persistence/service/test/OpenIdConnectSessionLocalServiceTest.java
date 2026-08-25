/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class OpenIdConnectSessionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddOpenIdConnectSession() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String issuer = RandomTestUtil.randomString();
		long userId1 = RandomTestUtil.randomLong();

		OpenIdConnectSession openIdConnectSession1 = _addOpenIdConnectSession(
			companyId, issuer, null, userId1);

		long userId2 = RandomTestUtil.randomLong();

		OpenIdConnectSession openIdConnectSession2 = _addOpenIdConnectSession(
			companyId, issuer, null, userId2);

		Assert.assertEquals(
			openIdConnectSession1,
			_openIdConnectSessionLocalService.getOpenIdConnectSession(
				userId1, issuer));
		Assert.assertEquals(
			openIdConnectSession2,
			_openIdConnectSessionLocalService.getOpenIdConnectSession(
				userId2, issuer));
	}

	@Test
	public void testFetchCurrentOpenIdConnectSession() throws Exception {
		_addOpenIdConnectSession();

		Assert.assertNull(
			_openIdConnectSessionLocalService.
				fetchCurrentOpenIdConnectSession());

		try {
			ServiceContext serviceContext = new ServiceContext();

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			MockHttpSession mockHttpSession = new MockHttpSession();

			OpenIdConnectSession openIdConnectSession =
				_addOpenIdConnectSession();

			mockHttpSession.setAttribute(
				OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION_ID,
				openIdConnectSession.getOpenIdConnectSessionId());

			mockHttpServletRequest.setSession(mockHttpSession);

			serviceContext.setRequest(mockHttpServletRequest);

			serviceContext.setUserId(TestPropsValues.getUserId());

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			Assert.assertEquals(
				openIdConnectSession,
				_openIdConnectSessionLocalService.
					fetchCurrentOpenIdConnectSession());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetOpenIdConnectSessions() throws Exception {
		long companyId1 = RandomTestUtil.randomLong();
		String issuer = RandomTestUtil.randomString();
		String sessionId = RandomTestUtil.randomString();

		OpenIdConnectSession openIdConnectSession1 = _addOpenIdConnectSession(
			companyId1, issuer, sessionId, RandomTestUtil.randomLong());

		_addOpenIdConnectSession(
			companyId1, issuer, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());

		_addOpenIdConnectSession(
			companyId1, issuer, null, RandomTestUtil.randomLong());

		long companyId2 = RandomTestUtil.randomLong();

		OpenIdConnectSession openIdConnectSession2 = _addOpenIdConnectSession(
			companyId2, issuer, sessionId, RandomTestUtil.randomLong());

		Assert.assertEquals(
			Collections.singletonList(openIdConnectSession1),
			_openIdConnectSessionLocalService.getOpenIdConnectSessions(
				companyId1, issuer, sessionId));
		Assert.assertEquals(
			Collections.emptyList(),
			_openIdConnectSessionLocalService.getOpenIdConnectSessions(
				companyId1, issuer, null));
		Assert.assertEquals(
			Collections.singletonList(openIdConnectSession2),
			_openIdConnectSessionLocalService.getOpenIdConnectSessions(
				companyId2, issuer, sessionId));
	}

	private OpenIdConnectSession _addOpenIdConnectSession() throws Exception {
		return _addOpenIdConnectSession(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), TestPropsValues.getUserId());
	}

	private OpenIdConnectSession _addOpenIdConnectSession(
		long companyId, String issuer, String sessionId, long userId) {

		OpenIdConnectSession openIdConnectSession =
			_openIdConnectSessionLocalService.createOpenIdConnectSession(
				_counterLocalService.increment(
					OpenIdConnectSession.class.getName()));

		openIdConnectSession.setCompanyId(companyId);
		openIdConnectSession.setUserId(userId);
		openIdConnectSession.setAccessToken(RandomTestUtil.randomString());
		openIdConnectSession.setIssuer(issuer);
		openIdConnectSession.setSessionId(sessionId);

		return _openIdConnectSessionLocalService.addOpenIdConnectSession(
			openIdConnectSession);
	}

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

}