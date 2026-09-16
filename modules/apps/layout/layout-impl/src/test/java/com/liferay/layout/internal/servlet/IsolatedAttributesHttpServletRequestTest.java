/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.servlet;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Javier Moral
 */
public class IsolatedAttributesHttpServletRequestTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		String name = RandomTestUtil.randomString();
		String value1 = RandomTestUtil.randomString();

		httpServletRequest.setAttribute(name, value1);

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		Assert.assertEquals(
			value1, isolatedAttributesHttpServletRequest.getAttribute(name));

		String value2 = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest.setAttribute(name, value2);

		Assert.assertEquals(
			value2, isolatedAttributesHttpServletRequest.getAttribute(name));

		Assert.assertEquals(value1, httpServletRequest.getAttribute(name));
	}

	@Test
	public void testGetAttributeNames() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		String name1 = RandomTestUtil.randomString();

		httpServletRequest.setAttribute(name1, RandomTestUtil.randomString());

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		isolatedAttributesHttpServletRequest.removeAttribute(name1);

		String name2 = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest.setAttribute(
			name2, RandomTestUtil.randomString());

		List<String> names = Collections.list(
			isolatedAttributesHttpServletRequest.getAttributeNames());

		Assert.assertEquals(names.toString(), 1, names.size());
		Assert.assertEquals(name2, names.get(0));
	}

	@Test
	public void testRemoveAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		String name = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		httpServletRequest.setAttribute(name, value);

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		isolatedAttributesHttpServletRequest.removeAttribute(name);

		Assert.assertNull(
			isolatedAttributesHttpServletRequest.getAttribute(name));

		Assert.assertEquals(value, httpServletRequest.getAttribute(name));
	}

	@Test
	public void testSetAttribute() {
		_testSetAttribute();

		_testSetAttributeWithRequestDispatcherAttribute();
	}

	private void _testSetAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		String name = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest.setAttribute(name, value);

		Assert.assertEquals(
			value, isolatedAttributesHttpServletRequest.getAttribute(name));

		Assert.assertNull(httpServletRequest.getAttribute(name));
	}

	private void _testSetAttributeWithRequestDispatcherAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		String value = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_REQUEST_URI, value);

		Assert.assertEquals(
			value,
			httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_REQUEST_URI));
	}

}