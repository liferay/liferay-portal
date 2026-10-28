/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.servlet;

import com.liferay.portal.kernel.test.TestInfo;
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
	@TestInfo({"LPD-103742", "LPD-112108"})
	public void testGetAttribute() {
		_testGetAttribute();
		_testGetAttributeWithReplacedRequest();
		_testGetAttributeWithRequestDispatcherAttribute();
		_testGetAttributeWithRestoredRequest();
	}

	@Test
	@TestInfo("LPD-103742")
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
	@TestInfo("LPD-103742")
	public void testRemoveAttribute() {
		_testRemoveAttribute();
		_testRemoveAttributeWithRequestDispatcherAttribute();
	}

	@Test
	@TestInfo("LPD-103742")
	public void testSetAttribute() {
		_testSetAttribute();
		_testSetAttributeWithRequestDispatcherAttribute();
	}

	private void _testGetAttribute() {
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

	private void _testGetAttributeWithReplacedRequest() {
		HttpServletRequest httpServletRequest1 = new MockHttpServletRequest();

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest =
				new IsolatedAttributesHttpServletRequest(httpServletRequest1);

		String value1 = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH, value1);

		HttpServletRequest httpServletRequest2 = new MockHttpServletRequest();
		String value2 = RandomTestUtil.randomString();

		httpServletRequest2.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH, value2);

		isolatedAttributesHttpServletRequest.setRequest(httpServletRequest2);

		Assert.assertEquals(
			value2,
			isolatedAttributesHttpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));

		isolatedAttributesHttpServletRequest.setRequest(httpServletRequest1);

		Assert.assertEquals(
			value1,
			isolatedAttributesHttpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));

		isolatedAttributesHttpServletRequest.removeAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH);

		isolatedAttributesHttpServletRequest.setRequest(httpServletRequest2);

		Assert.assertEquals(
			value2,
			isolatedAttributesHttpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));
	}

	private void _testGetAttributeWithRequestDispatcherAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest1 =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);
		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest2 =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		String value1 = RandomTestUtil.randomString();
		String value2 = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest1.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH, value1);
		isolatedAttributesHttpServletRequest2.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH, value2);

		Assert.assertEquals(
			value1,
			isolatedAttributesHttpServletRequest1.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));
		Assert.assertEquals(
			value2,
			httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));
		Assert.assertEquals(
			value2,
			isolatedAttributesHttpServletRequest2.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));
	}

	private void _testGetAttributeWithRestoredRequest() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest1 =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		String value = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest1.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH, value);

		isolatedAttributesHttpServletRequest1.setRequest(
			new MockHttpServletRequest());
		isolatedAttributesHttpServletRequest1.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH,
			RandomTestUtil.randomString());
		isolatedAttributesHttpServletRequest1.setRequest(httpServletRequest);

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest2 =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		isolatedAttributesHttpServletRequest2.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH,
			RandomTestUtil.randomString());

		Assert.assertEquals(
			value,
			isolatedAttributesHttpServletRequest1.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));
	}

	private void _testRemoveAttribute() {
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

	private void _testRemoveAttributeWithRequestDispatcherAttribute() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest1 =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);
		IsolatedAttributesHttpServletRequest
			isolatedAttributesHttpServletRequest2 =
				new IsolatedAttributesHttpServletRequest(httpServletRequest);

		String value = RandomTestUtil.randomString();

		isolatedAttributesHttpServletRequest1.setAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH, value);

		isolatedAttributesHttpServletRequest2.removeAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH);

		Assert.assertEquals(
			value,
			isolatedAttributesHttpServletRequest1.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));
		Assert.assertNull(
			httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH));
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