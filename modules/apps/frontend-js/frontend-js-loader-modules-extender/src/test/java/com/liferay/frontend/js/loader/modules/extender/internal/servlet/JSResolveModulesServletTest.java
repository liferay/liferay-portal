/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.servlet;

import com.liferay.frontend.js.loader.modules.extender.internal.resolution.BrowserModulesResolution;
import com.liferay.frontend.js.loader.modules.extender.internal.resolution.BrowserModulesResolver;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Daniel Sanz
 */
@FeatureFlag("LPD-48372")
public class JSResolveModulesServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		BrowserModulesResolution browserModulesResolution = Mockito.mock(
			BrowserModulesResolution.class);

		Mockito.when(
			browserModulesResolution.toJSON()
		).thenReturn(
			_RESPONSE_CONTENT
		);

		Mockito.when(
			_browserModulesResolver.resolve(
				Mockito.anyList(), Mockito.any(MockHttpServletRequest.class))
		).thenReturn(
			browserModulesResolution
		);

		ReflectionTestUtil.setFieldValue(
			_jsResolveModulesServlet, "_browserModulesResolver",
			_browserModulesResolver);
	}

	@Test
	public void testService() throws Exception {
		_testService(false);
		_testService(true);
	}

	private void _assertResponse(
			MockHttpServletResponse mockHttpServletResponse, boolean post,
			ResponseStatus responseStatus)
		throws Exception {

		if (responseStatus == ResponseStatus.INVALID) {
			Mockito.verify(
				_browserModulesResolver, Mockito.never()
			).resolve(
				Mockito.anyList(), Mockito.any(MockHttpServletRequest.class)
			);

			Assert.assertFalse(mockHttpServletResponse.isCommitted());
			Assert.assertEquals(
				HttpServletResponse.SC_BAD_REQUEST,
				mockHttpServletResponse.getStatus());
		}
		else if (responseStatus == ResponseStatus.OK) {
			Mockito.verify(
				_browserModulesResolver
			).resolve(
				Mockito.eq(Arrays.asList(_MODULES)),
				Mockito.any(MockHttpServletRequest.class)
			);

			Assert.assertEquals(
				_RESPONSE_CONTENT,
				mockHttpServletResponse.getContentAsString());
			Assert.assertEquals(
				HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
		}
		else if (responseStatus == ResponseStatus.OVERFLOW) {
			Mockito.verify(
				_browserModulesResolver, Mockito.never()
			).resolve(
				Mockito.anyList(), Mockito.any(MockHttpServletRequest.class)
			);

			Assert.assertFalse(mockHttpServletResponse.isCommitted());
			Assert.assertEquals(
				post ? HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE :
					HttpServletResponse.SC_REQUEST_URI_TOO_LONG,
				mockHttpServletResponse.getStatus());
		}
		else {
			throw new IllegalArgumentException(
				"Unexpected response status: " + responseStatus);
		}
	}

	private MockHttpServletResponse _service(
			Long maxRequestLength, String queryString, boolean post)
		throws Exception {

		Mockito.clearInvocations(_browserModulesResolver);

		if (maxRequestLength == null) {
			_jsResolveModulesServlet.activate(Collections.emptyMap());
		}
		else {
			_jsResolveModulesServlet.activate(
				HashMapBuilder.<String, Object>put(
					"maxRequestLength", maxRequestLength
				).build());
		}

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				post ? "POST" : "GET", RandomTestUtil.randomString());

		if (post) {
			mockHttpServletRequest.setContent(
				queryString.getBytes(StandardCharsets.UTF_8));
		}
		else {
			mockHttpServletRequest.setParameters(
				HttpComponentsUtil.getParameterMap(queryString));
			mockHttpServletRequest.setQueryString(queryString);
		}

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_jsResolveModulesServlet.service(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _testService(boolean post) throws Exception {

		// Invalid request: missing "modules" prefix

		MockHttpServletResponse mockHttpServletResponse = _service(
			_MODULES_PARAMETER.length() + 1L, _MODULES_PARAMETER, post);

		_assertResponse(mockHttpServletResponse, post, ResponseStatus.INVALID);

		// Request above configured limit

		mockHttpServletResponse = _service(
			_MODULES_QUERY_STRING.length() - 1L, _MODULES_QUERY_STRING, post);

		_assertResponse(mockHttpServletResponse, post, ResponseStatus.OVERFLOW);

		// Request above default limit

		mockHttpServletResponse = _service(
			null, _MODULES_QUERY_STRING_OVERFLOW, post);

		_assertResponse(mockHttpServletResponse, post, ResponseStatus.OVERFLOW);

		// Request above hard limit

		mockHttpServletResponse = _service(
			0L, _MODULES_QUERY_STRING_OVERFLOW, post);

		_assertResponse(mockHttpServletResponse, post, ResponseStatus.OVERFLOW);

		// Request within configured limit

		mockHttpServletResponse = _service(
			_MODULES_QUERY_STRING.length() + 1L, _MODULES_QUERY_STRING, post);

		_assertResponse(mockHttpServletResponse, post, ResponseStatus.OK);

		// Request within default limit

		mockHttpServletResponse = _service(null, _MODULES_QUERY_STRING, post);

		_assertResponse(mockHttpServletResponse, post, ResponseStatus.OK);

		// Request within hard limit

		mockHttpServletResponse = _service(0L, _MODULES_QUERY_STRING, post);

		_assertResponse(mockHttpServletResponse, post, ResponseStatus.OK);
	}

	private static final String[] _MODULES = {
		RandomTestUtil.randomString(), RandomTestUtil.randomString(),
		RandomTestUtil.randomString()
	};

	private static final String _MODULES_PARAMETER = StringUtil.merge(_MODULES);

	private static final String _MODULES_QUERY_STRING =
		"modules=" + _MODULES_PARAMETER;

	private static final String _MODULES_QUERY_STRING_OVERFLOW =
		"modules=" + RandomTestUtil.randomString((1024 * 1024) + 1);

	private static final String _RESPONSE_CONTENT =
		RandomTestUtil.randomString();

	private final BrowserModulesResolver _browserModulesResolver = Mockito.mock(
		BrowserModulesResolver.class);
	private final JSResolveModulesServlet _jsResolveModulesServlet =
		new JSResolveModulesServlet();

	private enum ResponseStatus {

		INVALID, OK, OVERFLOW,

	}

}