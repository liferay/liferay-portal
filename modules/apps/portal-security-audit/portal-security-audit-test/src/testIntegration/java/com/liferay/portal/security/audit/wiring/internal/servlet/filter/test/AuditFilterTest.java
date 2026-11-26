/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.wiring.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterChain;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Field;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Christian Moura
 * @author Álvaro Saugar
 */
@RunWith(Arquillian.class)
public class AuditFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testDoFilter() throws Exception {
		_testDoFilterCapturesAuditSessionId();
		_testDoFilterCapturesHttpMethod();
		_testDoFilterCapturesUserAgent();
		_testDoFilterResolvesRequestId();
		_testDoFilterWhenAuditLogContextIsEnabled();
	}

	private MockHttpServletResponse _testDoFilter(
			boolean useIncomingXRequestId, String xRequestId)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		Field field = ReflectionTestUtil.getField(
			_filter.getClass(), "_auditLogContextConfiguration");

		Class<?> clazz = field.getType();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					_filter, "_auditLogContextConfiguration",
					ProxyUtil.newProxyInstance(
						clazz.getClassLoader(), new Class<?>[] {clazz},
						(proxy, method, args) -> {
							if (Objects.equals(
									method.getName(),
									"useIncomingXRequestId")) {

								return useIncomingXRequestId;
							}

							return true;
						}))) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.addHeader(
				HttpHeaders.X_REQUEST_ID, xRequestId);

			_testDoFilter(mockHttpServletRequest, mockHttpServletResponse);
		}

		return mockHttpServletResponse;
	}

	private AuditRequestContext _testDoFilter(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		return _testDoFilter(
			mockHttpServletRequest, new MockHttpServletResponse());
	}

	private AuditRequestContext _testDoFilter(
			MockHttpServletRequest mockHttpServletRequest,
			MockHttpServletResponse mockHttpServletResponse)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			InvokerFilterChain invokerFilterChain = new InvokerFilterChain(
				(servletRequest, servletResponse) -> {
				});

			invokerFilterChain.addFilter(_filter);

			invokerFilterChain.doFilter(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		AuditRequestContextThreadLocal.removeAuditRequestContext();

		return auditRequestContext;
	}

	private AuditRequestContext _testDoFilter(String xRequestId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(HttpHeaders.X_REQUEST_ID, xRequestId);

		return _testDoFilter(mockHttpServletRequest);
	}

	private void _testDoFilterCapturesAuditSessionId() throws Exception {
		AuditRequestContext auditRequestContext = _testDoFilter(
			new MockHttpServletRequest());

		Assert.assertNotNull(auditRequestContext.getRequestURL());
		Assert.assertNull(auditRequestContext.getSessionID());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		HttpSession httpSession = mockHttpServletRequest.getSession();

		String auditSessionId = RandomTestUtil.randomString();

		httpSession.setAttribute(WebKeys.AUDIT_SESSION_ID, auditSessionId);

		httpSession.setAttribute(WebKeys.USER_ID, TestPropsValues.getUserId());

		auditRequestContext = _testDoFilter(mockHttpServletRequest);

		Assert.assertEquals(auditSessionId, auditRequestContext.getSessionID());
		Assert.assertNotEquals(
			httpSession.getId(), auditRequestContext.getSessionID());
	}

	private void _testDoFilterCapturesHttpMethod() throws Exception {
		String httpMethod = RandomTestUtil.randomString();

		AuditRequestContext auditRequestContext = _testDoFilter(
			new MockHttpServletRequest(
				httpMethod, RandomTestUtil.randomString()));

		Assert.assertEquals(httpMethod, auditRequestContext.getHttpMethod());

		httpMethod = RandomTestUtil.randomString(76);

		auditRequestContext = _testDoFilter(
			new MockHttpServletRequest(
				httpMethod, RandomTestUtil.randomString()));

		Assert.assertEquals(
			httpMethod.substring(0, 75), auditRequestContext.getHttpMethod());
	}

	private void _testDoFilterCapturesUserAgent() throws Exception {
		AuditRequestContext auditRequestContext = _testDoFilter(
			new MockHttpServletRequest());

		Assert.assertNull(auditRequestContext.getUserAgent());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String userAgent = RandomTestUtil.randomString();

		mockHttpServletRequest.addHeader(HttpHeaders.USER_AGENT, userAgent);

		auditRequestContext = _testDoFilter(mockHttpServletRequest);

		Assert.assertEquals(userAgent, auditRequestContext.getUserAgent());

		mockHttpServletRequest = new MockHttpServletRequest();

		userAgent =
			RandomTestUtil.randomString(128) + StringPool.SPACE +
				RandomTestUtil.randomString(127);

		mockHttpServletRequest.addHeader(HttpHeaders.USER_AGENT, userAgent);

		auditRequestContext = _testDoFilter(mockHttpServletRequest);

		Assert.assertEquals(
			userAgent.substring(0, 255), auditRequestContext.getUserAgent());
	}

	private void _testDoFilterResolvesRequestId() throws Exception {
		AuditRequestContext auditRequestContext = _testDoFilter(
			new MockHttpServletRequest());

		Assert.assertNotNull(auditRequestContext.getRequestId());
		Assert.assertTrue(auditRequestContext.isRequestIdGenerated());

		String xRequestId = RandomTestUtil.randomString();

		auditRequestContext = _testDoFilter(xRequestId);

		Assert.assertNotEquals(xRequestId, auditRequestContext.getRequestId());
		Assert.assertNotNull(auditRequestContext.getRequestId());
		Assert.assertTrue(auditRequestContext.isRequestIdGenerated());

		xRequestId = PortalUUIDUtil.generate();

		auditRequestContext = _testDoFilter(xRequestId);

		Assert.assertEquals(xRequestId, auditRequestContext.getRequestId());
		Assert.assertFalse(auditRequestContext.isRequestIdGenerated());
	}

	private void _testDoFilterWhenAuditLogContextIsEnabled() throws Exception {
		String xRequestId = PortalUUIDUtil.generate();

		MockHttpServletResponse mockHttpServletResponse = _testDoFilter(
			false, xRequestId);

		Assert.assertNotEquals(
			xRequestId,
			mockHttpServletResponse.getHeader(HttpHeaders.X_REQUEST_ID));
		Assert.assertNotNull(
			mockHttpServletResponse.getHeader(HttpHeaders.X_REQUEST_ID));

		mockHttpServletResponse = _testDoFilter(true, xRequestId);

		Assert.assertEquals(
			xRequestId,
			mockHttpServletResponse.getHeader(HttpHeaders.X_REQUEST_ID));
	}

	@Inject(filter = "servlet-filter-name=Audit Filter")
	private Filter _filter;

}