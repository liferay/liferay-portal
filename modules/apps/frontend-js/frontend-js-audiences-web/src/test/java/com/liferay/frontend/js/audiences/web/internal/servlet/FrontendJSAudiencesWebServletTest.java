/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences.web.internal.servlet;

import com.liferay.frontend.js.audiences.web.internal.util.BootstrapJavaScriptUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
public class FrontendJSAudiencesWebServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_bootstrapJavaScriptUtilMockedStatic = Mockito.mockStatic(
			BootstrapJavaScriptUtil.class);

		_bootstrapJavaScriptUtilMockedStatic.when(
			BootstrapJavaScriptUtil::getHash
		).thenReturn(
			_TEMPLATE_HASH
		);

		_bootstrapJavaScriptUtilMockedStatic.when(
			() -> BootstrapJavaScriptUtil.getContent(
				Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean())
		).thenReturn(
			_CONTENT
		);

		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			"/bootstrap.(" + _TEMPLATE_HASH + ").js"
		);

		Mockito.when(
			_httpServletResponse.getWriter()
		).thenReturn(
			new PrintWriter(_stringWriter)
		);
	}

	@After
	public void tearDown() {
		_bootstrapJavaScriptUtilMockedStatic.close();
	}

	@Test
	public void testDoGetParsesDetectionTimeout() throws Exception {
		_setUpParameters("5000", "false");

		_frontendJSAudiencesWebServlet.doGet(
			_httpServletRequest, _httpServletResponse);

		_bootstrapJavaScriptUtilMockedStatic.verify(
			() -> BootstrapJavaScriptUtil.getContent(
				Mockito.eq(_AUDIENCES_DEFINITION_HASH), Mockito.eq(5000),
				Mockito.eq(false)));
	}

	@Test
	public void testDoGetWhenDetectionTimeoutIsBlank() throws Exception {
		_setUpParameters(null, "true");

		_frontendJSAudiencesWebServlet.doGet(
			_httpServletRequest, _httpServletResponse);

		_bootstrapJavaScriptUtilMockedStatic.verify(
			() -> BootstrapJavaScriptUtil.getContent(
				Mockito.eq(_AUDIENCES_DEFINITION_HASH), Mockito.eq(0),
				Mockito.eq(true)));
	}

	@Test
	public void testDoGetWhenDetectionTimeoutIsNotANumber() throws Exception {
		_setUpParameters("not-a-number", "true");

		_frontendJSAudiencesWebServlet.doGet(
			_httpServletRequest, _httpServletResponse);

		_bootstrapJavaScriptUtilMockedStatic.verify(
			() -> BootstrapJavaScriptUtil.getContent(
				Mockito.eq(_AUDIENCES_DEFINITION_HASH), Mockito.eq(0),
				Mockito.eq(true)));
	}

	@Test
	public void testDoGetWhenRequestHashIsStale() throws Exception {
		String staleHash = RandomTestUtil.randomString(8);

		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			"/bootstrap.(" + staleHash + ").js"
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/o/audiences/bootstrap.(" + staleHash + ").js"
		);

		_setUpParameters("5000", "true");

		_frontendJSAudiencesWebServlet.doGet(
			_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse
		).sendRedirect(
			Mockito.contains(_TEMPLATE_HASH)
		);
	}

	@Test
	public void testDoGetWritesBootstrapContent() throws Exception {
		_setUpParameters("5000", "true");

		_frontendJSAudiencesWebServlet.doGet(
			_httpServletRequest, _httpServletResponse);

		Assert.assertEquals(_CONTENT, _stringWriter.toString());

		Mockito.verify(
			_httpServletResponse, Mockito.never()
		).sendRedirect(
			Mockito.anyString()
		);
	}

	private void _setUpParameters(String detectionTimeout, String enableLog) {
		Mockito.when(
			_httpServletRequest.getParameter("audiencesDefinitionHash")
		).thenReturn(
			_AUDIENCES_DEFINITION_HASH
		);

		Mockito.when(
			_httpServletRequest.getParameter("detectionTimeout")
		).thenReturn(
			detectionTimeout
		);

		Mockito.when(
			_httpServletRequest.getParameter("enableLog")
		).thenReturn(
			enableLog
		);
	}

	private static final String _AUDIENCES_DEFINITION_HASH =
		RandomTestUtil.randomString(8);

	private static final String _CONTENT = "// bootstrap content";

	private static final String _TEMPLATE_HASH = RandomTestUtil.randomString(8);

	private MockedStatic<BootstrapJavaScriptUtil>
		_bootstrapJavaScriptUtilMockedStatic;
	private final FrontendJSAudiencesWebServlet _frontendJSAudiencesWebServlet =
		new FrontendJSAudiencesWebServlet();
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final HttpServletResponse _httpServletResponse = Mockito.mock(
		HttpServletResponse.class);
	private final StringWriter _stringWriter = new StringWriter();

}