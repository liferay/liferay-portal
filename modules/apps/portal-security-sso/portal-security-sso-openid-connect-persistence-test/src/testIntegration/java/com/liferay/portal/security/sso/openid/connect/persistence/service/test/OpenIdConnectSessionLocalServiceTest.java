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

	private OpenIdConnectSession _addOpenIdConnectSession() throws Exception {
		OpenIdConnectSession openIdConnectSession =
			_openIdConnectSessionLocalService.createOpenIdConnectSession(
				_counterLocalService.increment(
					OpenIdConnectSession.class.getName()));

		openIdConnectSession.setUserId(TestPropsValues.getUserId());
		openIdConnectSession.setAccessToken(RandomTestUtil.randomString());

		return _openIdConnectSessionLocalService.addOpenIdConnectSession(
			openIdConnectSession);
	}

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

}