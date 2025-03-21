/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletParameterUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Sampsa Sohlman
 */
public class DynamicServletRequestTest {

	@Test
	public void testAddQueryStringToParameterMapWithEmptyMap1() {
		String queryString = PortletParameterUtil.addNamespace(
			"15", StringPool.BLANK);

		HttpServletRequest httpServletRequest =
			DynamicServletRequest.addQueryString(
				new MockHttpServletRequest(),
				Collections.<String, String[]>emptyMap(), queryString, false);

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		Assert.assertEquals(parameterMap.toString(), 1, parameterMap.size());
		Assert.assertArrayEquals(
			new String[] {"15"}, parameterMap.get("p_p_id"));
	}

	@Test
	public void testAddQueryStringToParameterMapWithEmptyMap2() {
		String queryString = PortletParameterUtil.addNamespace(
			"15", "param1=value1&param2=value2&param3=value3");

		HttpServletRequest httpServletRequest =
			DynamicServletRequest.addQueryString(
				new MockHttpServletRequest(),
				Collections.<String, String[]>emptyMap(), queryString, false);

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		Assert.assertEquals(parameterMap.toString(), 4, parameterMap.size());
		Assert.assertArrayEquals(
			new String[] {"15"}, parameterMap.get("p_p_id"));
		Assert.assertArrayEquals(
			new String[] {"value1"}, parameterMap.get("_15_param1"));
		Assert.assertArrayEquals(
			new String[] {"value2"}, parameterMap.get("_15_param2"));
		Assert.assertArrayEquals(
			new String[] {"value3"}, parameterMap.get("_15_param3"));
	}

}