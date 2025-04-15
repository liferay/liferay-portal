/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.servlet;

import com.liferay.adaptive.media.handler.AMRequestHandler;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class AMServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_amServlet, "_amRequestHandlerLocator", _amRequestHandlerLocator);
	}

	@Test
	public void testMiscellaneousError() throws Exception {
		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			(AMRequestHandler<Object>)_amRequestHandlerLocator.locateForPattern(
				Mockito.anyString())
		).thenReturn(
			(AMRequestHandler<Object>)_amRequestHandler
		);

		Mockito.when(
			_amRequestHandler.handleRequest(_httpServletRequest)
		).thenThrow(
			new IllegalArgumentException()
		);

		_amServlet.doGet(_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse
		).sendError(
			Mockito.eq(HttpServletResponse.SC_BAD_REQUEST),
			Mockito.nullable(String.class)
		);
	}

	@Test
	public void testNoMediaFound() throws Exception {
		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			(AMRequestHandler<Object>)_amRequestHandlerLocator.locateForPattern(
				Mockito.anyString())
		).thenReturn(
			(AMRequestHandler<Object>)_amRequestHandler
		);

		Mockito.when(
			_amRequestHandler.handleRequest(_httpServletRequest)
		).thenReturn(
			null
		);

		_amServlet.doGet(_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse
		).sendError(
			Mockito.eq(HttpServletResponse.SC_NOT_FOUND),
			Mockito.nullable(String.class)
		);
	}

	@Test
	public void testNoMediaFoundWithException() throws Exception {
		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			(AMRequestHandler<Object>)_amRequestHandlerLocator.locateForPattern(
				Mockito.anyString())
		).thenReturn(
			(AMRequestHandler<Object>)_amRequestHandler
		);

		Mockito.when(
			_amRequestHandler.handleRequest(_httpServletRequest)
		).thenReturn(
			null
		);

		_amServlet.doGet(_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse
		).sendError(
			Mockito.eq(HttpServletResponse.SC_NOT_FOUND),
			Mockito.nullable(String.class)
		);
	}

	@Test
	public void testNoPermissionError() throws Exception {
		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			(AMRequestHandler<Object>)_amRequestHandlerLocator.locateForPattern(
				Mockito.anyString())
		).thenReturn(
			(AMRequestHandler<Object>)_amRequestHandler
		);

		Mockito.when(
			_amRequestHandler.handleRequest(_httpServletRequest)
		).thenThrow(
			new ServletException(new PrincipalException())
		);

		_amServlet.doGet(_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse
		).sendError(
			Mockito.eq(HttpServletResponse.SC_FORBIDDEN),
			Mockito.nullable(String.class)
		);
	}

	@Test
	public void testNoRequestHandlerFound() throws Exception {
		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_amRequestHandlerLocator.locateForPattern(Mockito.anyString())
		).thenReturn(
			null
		);

		_amServlet.doGet(_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse
		).sendError(
			Mockito.eq(HttpServletResponse.SC_NOT_FOUND),
			Mockito.nullable(String.class)
		);
	}

	private final AMRequestHandler<?> _amRequestHandler = Mockito.mock(
		AMRequestHandler.class);
	private final AMRequestHandlerLocator _amRequestHandlerLocator =
		Mockito.mock(AMRequestHandlerLocator.class);
	private final AMServlet _amServlet = new AMServlet();
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final HttpServletResponse _httpServletResponse = Mockito.mock(
		HttpServletResponse.class);

}