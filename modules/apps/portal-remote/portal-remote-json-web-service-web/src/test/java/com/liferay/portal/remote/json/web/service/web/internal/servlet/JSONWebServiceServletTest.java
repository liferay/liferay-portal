/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal.servlet;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rafael Praxedes
 */
public class JSONWebServiceServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_portal = Mockito.mock(Portal.class);

		ReflectionTestUtil.setFieldValue(
			_jsonWebServiceServlet, "_portal", _portal);
	}

	@Test
	public void testGetPathInfo() throws Exception {
		_setCurrentURL(
			"http://localhost:8080/api/jsonws/methodName/paramName/paramValue");

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		_jsonWebServiceServlet.service(
			httpServletRequest, new MockHttpServletResponse());

		String pathInfo = (String)httpServletRequest.getAttribute(
			WebKeys.ORIGINAL_PATH_INFO);

		Assert.assertEquals("/methodName/paramName/paramValue", pathInfo);
	}

	@Test
	public void testGetPathInfoWithPathParameterValueEncoded()
		throws Exception {

		_setCurrentURL(
			"http://localhost:8080/api/jsonws/methodName/paramName/user%27ea1");

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		_jsonWebServiceServlet.service(
			httpServletRequest, new MockHttpServletResponse());

		String pathInfo = (String)httpServletRequest.getAttribute(
			WebKeys.ORIGINAL_PATH_INFO);

		Assert.assertEquals("/methodName/paramName/user'ea1", pathInfo);
	}

	private void _setCurrentURL(String currentURL) {
		Mockito.when(
			_portal.getCurrentURL(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			currentURL
		);
	}

	private static final JSONWebServiceServlet _jsonWebServiceServlet =
		new JSONWebServiceServlet();
	private static Portal _portal;

}