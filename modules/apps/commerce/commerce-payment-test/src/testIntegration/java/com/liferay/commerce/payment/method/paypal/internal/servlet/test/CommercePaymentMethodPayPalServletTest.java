/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.paypal.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Servlet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommercePaymentMethodPayPalServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testCrossOriginRedirect() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.commerce.payment.method.mercanet.internal." +
					"servlet.MercanetServlet",
				LoggerTestUtil.OFF)) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest("GET", "/mercanet-payment");

			String redirect = "https://www.google.com";

			mockHttpServletRequest.addParameter("redirect", redirect);

			User user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			PrincipalThreadLocal.setName(user.getUserId());

			mockHttpServletRequest.setAttribute(WebKeys.USER, user);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

			Assert.assertNotEquals(
				redirect, mockHttpServletResponse.getRedirectedUrl());
		}
	}

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.commerce.payment.method.mercanet.internal.servlet.MercanetServlet"
	)
	private Servlet _servlet;

}