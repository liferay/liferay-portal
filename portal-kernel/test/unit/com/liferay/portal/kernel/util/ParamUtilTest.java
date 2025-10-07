/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Preston Crary
 */
public class ParamUtilTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_propsUtilMockedStatic.when(
			() -> PropsUtil.get(PropsKeys.UNICODE_TEXT_NORMALIZER_FORM)
		).thenReturn(
			"NFC"
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_propsUtilMockedStatic.close();
	}

	@Test
	public void testGetHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"key1", " \t\n\r\u3000value \t\n\r\u3000");

		String defaultString = RandomTestUtil.randomString();

		String value = ParamUtil.get(
			mockHttpServletRequest, "key1", defaultString);

		Assert.assertEquals("value", value);

		value = ParamUtil.get(mockHttpServletRequest, "key2", defaultString);

		Assert.assertSame(defaultString, value);
	}

	@Test
	public void testGetNormalizedString() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter("key1", "\u1004\u103A\u1037");
		mockHttpServletRequest.addParameter("key2", "\u1004\u1037\u103A");

		String value1 = ParamUtil.getString(mockHttpServletRequest, "key1", "");
		String value2 = ParamUtil.getString(mockHttpServletRequest, "key2", "");

		Assert.assertEquals(value1, value2);
	}

	@Test
	public void testGetPortletRequest() {
		MockPortletRequest mockPortletRequest = new MockPortletRequest();

		mockPortletRequest.setParameter(
			"key1", " \t\n\r\u3000value \t\n\r\u3000");

		String defaultString = RandomTestUtil.randomString();

		String value = ParamUtil.get(mockPortletRequest, "key1", defaultString);

		Assert.assertEquals("value", value);

		value = ParamUtil.get(mockPortletRequest, "key2", defaultString);

		Assert.assertSame(defaultString, value);
	}

	@Test
	public void testGetServiceContext() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute("key1", " \t\n\r\u3000value \t\n\r\u3000");

		String defaultString = RandomTestUtil.randomString();

		String value = ParamUtil.get(serviceContext, "key1", defaultString);

		Assert.assertEquals("value", value);

		value = ParamUtil.get(serviceContext, "key2", defaultString);

		Assert.assertSame(defaultString, value);
	}

	private static final MockedStatic<PropsUtil> _propsUtilMockedStatic =
		Mockito.mockStatic(PropsUtil.class);

}