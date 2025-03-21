/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Minhchau Dang
 */
public class SharedSessionServletRequestTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testGetSharedSession() {
		_testGetSharedSession(false);
		_testGetSharedSession(true);
	}

	@Test
	public void testInvalidateSession() {
		_testInvalidateSession(false);
		_testInvalidateSession(true);
	}

	private void _testGetSharedSession(boolean shared) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		SharedSessionServletRequest sharedSessionServletRequest =
			new SharedSessionServletRequest(mockHttpServletRequest, shared);

		Assert.assertSame(
			sharedSessionServletRequest.getSharedSession(),
			sharedSessionServletRequest.getSharedSession());
	}

	private void _testInvalidateSession(boolean shared) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		SharedSessionServletRequest sharedSessionServletRequest =
			new SharedSessionServletRequest(mockHttpServletRequest, shared);

		HttpSession httpSession = sharedSessionServletRequest.getSession();

		httpSession.invalidate();

		Assert.assertNull(sharedSessionServletRequest.getSession(false));

		HttpSession newHttpSession = sharedSessionServletRequest.getSession(
			true);

		Assert.assertNotNull(newHttpSession);
		Assert.assertNotSame(httpSession, newHttpSession);
	}

}