/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.servlet;

import com.liferay.frontend.js.loader.modules.extender.internal.resolution.BrowserModulesResolution;
import com.liferay.frontend.js.loader.modules.extender.internal.resolution.BrowserModulesResolver;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
			_JSON
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

		_jsResolveModulesServlet.activate(Collections.emptyMap());
	}

	@Test
	public void testServiceWhenModuleNamesAreDuplicated() throws Exception {
		List<String> moduleNames1 = Arrays.asList(
			_MODULE_NAMES[0], _MODULE_NAMES[1], _MODULE_NAMES[1],
			_MODULE_NAMES[0]);
		List<String> moduleNames2 = Arrays.asList(
			_MODULE_NAMES[0], _MODULE_NAMES[1]);

		// GET request

		_assertResolved(moduleNames2, _serviceGET(moduleNames1));

		// POST request

		_assertResolved(
			moduleNames2, _servicePOST(_getModulesParameter(moduleNames1)));
	}

	@Test
	public void testServiceWithGETRequest() throws Exception {
		List<String> moduleNames = Arrays.asList(
			_MODULE_NAMES[0], _MODULE_NAMES[1]);

		_assertResolved(moduleNames, _serviceGET(moduleNames));
	}

	@Test
	public void testServiceWithGETRequestWhenQueryStringExceedsLimit()
		throws Exception {

		List<String> moduleNames = Arrays.asList(
			_MODULE_NAMES[0], _MODULE_NAMES[1]);

		String queryString = _getModulesParameter(moduleNames);

		// Above the configured limit

		_activate(queryString.length() - 1);

		_assertRequestURITooLong(queryString);

		// Above the configured limit with multibyte characters

		String multibyteQueryString = "modules=é";

		_activate(multibyteQueryString.length());

		_assertRequestURITooLong(multibyteQueryString);

		// Above the default limit

		_jsResolveModulesServlet.activate(Collections.emptyMap());

		_assertRequestURITooLong(_getRandomModulesParameter((256 * 1024) + 1));

		// Above the hard limit

		_activate(0);

		_assertRequestURITooLong(_getRandomModulesParameter((1024 * 1024) + 1));

		_activate(Long.MAX_VALUE);

		_assertRequestURITooLong(_getRandomModulesParameter((1024 * 1024) + 1));

		// Within the configured limit

		_activate(queryString.length());

		_assertResolved(moduleNames, _serviceGET(moduleNames));

		// Within the hard limit

		_activate(0);

		String moduleName = RandomTestUtil.randomString(
			(1024 * 1024) - "modules=".length());

		_assertResolved(
			Collections.singletonList(moduleName),
			_serviceGET(Collections.singletonList(moduleName)));
	}

	@Test
	public void testServiceWithPOSTRequest() throws Exception {

		// Empty body

		_assertResolved(
			Collections.emptyList(), _servicePOST(StringPool.BLANK));

		// Module names

		List<String> moduleNames = Arrays.asList(
			_MODULE_NAMES[0], _MODULE_NAMES[1], _MODULE_NAMES[2]);

		_assertResolved(
			moduleNames, _servicePOST(_getModulesParameter(moduleNames)));
	}

	@Test
	public void testServiceWithPOSTRequestWhenBodyExceedsLimit()
		throws Exception {

		// Above the configured limit

		List<String> moduleNames = Arrays.asList(
			_MODULE_NAMES[0], _MODULE_NAMES[1]);

		String body = _getModulesParameter(moduleNames);

		_activate(body.length() - 1);

		_assertRequestEntityTooLarge(body);

		// Above the default limit

		_jsResolveModulesServlet.activate(Collections.emptyMap());

		_assertRequestEntityTooLarge(
			_getRandomModulesParameter((256 * 1024) + 1));

		// Above the hard limit

		_activate(0);

		_assertRequestEntityTooLarge(
			_getRandomModulesParameter((1024 * 1024) + 1));

		_activate(Long.MAX_VALUE);

		_assertRequestEntityTooLarge(
			_getRandomModulesParameter((1024 * 1024) + 1));

		// Within the configured limit

		_activate(body.length());

		_assertResolved(moduleNames, _servicePOST(body));

		// Within the hard limit

		_activate(0);

		String moduleName = RandomTestUtil.randomString(
			(1024 * 1024) - "modules=".length());

		_assertResolved(
			Collections.singletonList(moduleName),
			_servicePOST(
				_getModulesParameter(Collections.singletonList(moduleName))));
	}

	@Test
	public void testServiceWithPOSTRequestWhenBodyIsInvalid() throws Exception {

		// Malformed percent encoding

		_assertRejected(
			HttpServletResponse.SC_BAD_REQUEST,
			_servicePOST("modules=" + _MODULE_NAMES[0] + "%2"));

		// Missing modules prefix

		_assertRejected(
			HttpServletResponse.SC_BAD_REQUEST, _servicePOST(_MODULE_NAMES[0]));
	}

	@Test
	public void testServiceWithPOSTRequestWhenContentLengthIsMissing()
		throws Exception {

		// Above the configured limit

		String body = _getRandomModulesParameter(4 * 1024 * 1024);

		_activate(1024);

		_assertChunkedRequestEntityTooLarge(body, 1024);

		// Above the default limit

		_jsResolveModulesServlet.activate(Collections.emptyMap());

		_assertChunkedRequestEntityTooLarge(body, 256 * 1024);

		// Above the hard limit

		_activate(0);

		_assertChunkedRequestEntityTooLarge(body, 1024 * 1024);

		_activate(Long.MAX_VALUE);

		_assertChunkedRequestEntityTooLarge(body, 1024 * 1024);

		// Within the default limit

		_jsResolveModulesServlet.activate(Collections.emptyMap());

		List<String> moduleNames = Arrays.asList(
			_MODULE_NAMES[0], _MODULE_NAMES[1]);

		_assertResolved(
			moduleNames,
			_service(
				_getChunkedPOSTMockHttpServletRequest(
					_getModulesParameter(moduleNames))));
	}

	@Test
	public void testServiceWithPUTRequest() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _service(
			new MockHttpServletRequest("PUT", RandomTestUtil.randomString()));

		Assert.assertEquals(
			HttpServletResponse.SC_METHOD_NOT_ALLOWED,
			mockHttpServletResponse.getStatus());

		Mockito.verify(
			_browserModulesResolver, Mockito.never()
		).resolve(
			Mockito.anyList(), Mockito.any(MockHttpServletRequest.class)
		);
	}

	private void _activate(long maxRequestLength) {
		_jsResolveModulesServlet.activate(
			HashMapBuilder.<String, Object>put(
				"maxRequestLength", maxRequestLength
			).build());
	}

	private void _assertChunkedRequestEntityTooLarge(
			String body, long maxRequestLength)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getChunkedPOSTMockHttpServletRequest(body);

		_assertRejected(
			HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
			_service(mockHttpServletRequest));

		ServletInputStream servletInputStream =
			mockHttpServletRequest.getInputStream();

		Assert.assertEquals(
			body.length() - maxRequestLength - 1,
			servletInputStream.available());
	}

	private void _assertRejected(
		int status, MockHttpServletResponse mockHttpServletResponse) {

		Assert.assertFalse(mockHttpServletResponse.isCommitted());
		Assert.assertEquals(status, mockHttpServletResponse.getStatus());

		Mockito.verify(
			_browserModulesResolver, Mockito.never()
		).resolve(
			Mockito.anyList(), Mockito.any(MockHttpServletRequest.class)
		);
	}

	private void _assertRequestEntityTooLarge(String body) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getPOSTMockHttpServletRequest(body);

		_assertRejected(
			HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
			_service(mockHttpServletRequest));

		ServletInputStream servletInputStream =
			mockHttpServletRequest.getInputStream();

		Assert.assertEquals(body.length(), servletInputStream.available());
	}

	private void _assertRequestURITooLong(String queryString) throws Exception {
		_assertRejected(
			HttpServletResponse.SC_REQUEST_URI_TOO_LONG,
			_service(_getGETMockHttpServletRequest(queryString)));
	}

	private void _assertResolved(
			List<String> moduleNames,
			MockHttpServletResponse mockHttpServletResponse)
		throws Exception {

		Assert.assertEquals(
			_JSON, mockHttpServletResponse.getContentAsString());
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		Mockito.verify(
			_browserModulesResolver
		).resolve(
			Mockito.eq(moduleNames), Mockito.any(MockHttpServletRequest.class)
		);

		Mockito.clearInvocations(_browserModulesResolver);
	}

	private MockHttpServletRequest _getChunkedPOSTMockHttpServletRequest(
		String body) {

		MockHttpServletRequest mockHttpServletRequest = Mockito.spy(
			_getPOSTMockHttpServletRequest(body));

		Mockito.doReturn(
			-1L
		).when(
			mockHttpServletRequest
		).getContentLengthLong();

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _getGETMockHttpServletRequest(
		String queryString) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("GET", RandomTestUtil.randomString());

		mockHttpServletRequest.setQueryString(queryString);

		return mockHttpServletRequest;
	}

	private String _getModulesParameter(List<String> moduleNames) {
		return "modules=" +
			URLCodec.encodeURL(StringUtil.merge(moduleNames), true);
	}

	private MockHttpServletRequest _getPOSTMockHttpServletRequest(String body) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("POST", RandomTestUtil.randomString());

		mockHttpServletRequest.setContent(
			body.getBytes(StandardCharsets.UTF_8));

		return mockHttpServletRequest;
	}

	private String _getRandomModulesParameter(int length) {
		return "modules=" +
			RandomTestUtil.randomString(length - "modules=".length());
	}

	private MockHttpServletResponse _service(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_jsResolveModulesServlet.service(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private MockHttpServletResponse _serviceGET(List<String> moduleNames)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getGETMockHttpServletRequest(_getModulesParameter(moduleNames));

		mockHttpServletRequest.addParameter(
			"modules", StringUtil.merge(moduleNames));

		return _service(mockHttpServletRequest);
	}

	private MockHttpServletResponse _servicePOST(String body) throws Exception {
		return _service(_getPOSTMockHttpServletRequest(body));
	}

	private static final String _JSON = RandomTestUtil.randomString();

	private static final String[] _MODULE_NAMES = {
		RandomTestUtil.randomString(), RandomTestUtil.randomString(),
		RandomTestUtil.randomString()
	};

	private final BrowserModulesResolver _browserModulesResolver = Mockito.mock(
		BrowserModulesResolver.class);
	private final JSResolveModulesServlet _jsResolveModulesServlet =
		new JSResolveModulesServlet();

}