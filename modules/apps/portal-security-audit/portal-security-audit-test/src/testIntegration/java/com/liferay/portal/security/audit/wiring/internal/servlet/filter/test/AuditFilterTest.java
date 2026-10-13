/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.wiring.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterChain;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpSession;

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

	@Test
	public void testDoFilterCapturesAuditSessionIdWhenAuthenticated()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		HttpSession httpSession = mockHttpServletRequest.getSession();

		String auditSessionId = RandomTestUtil.randomString();

		httpSession.setAttribute(WebKeys.AUDIT_SESSION_ID, auditSessionId);

		httpSession.setAttribute(WebKeys.USER_ID, TestPropsValues.getUserId());

		AuditRequestContext auditRequestContext = _testDoFilter(
			mockHttpServletRequest);

		Assert.assertEquals(auditSessionId, auditRequestContext.getSessionID());
		Assert.assertNotEquals(
			httpSession.getId(), auditRequestContext.getSessionID());
	}

	@Test
	public void testDoFilterCapturesNoAuditSessionIdBeforeAuthentication()
		throws Exception {

		AuditRequestContext auditRequestContext = _testDoFilter(
			new MockHttpServletRequest());

		Assert.assertNotNull(auditRequestContext.getRequestURL());
		Assert.assertNull(auditRequestContext.getSessionID());
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testDoFilterDoesNotResolveRequestIdWhenFeatureFlagIsDisabled()
		throws Exception {

		AuditRequestContext auditRequestContext = _testDoFilter(
			PortalUUIDUtil.generate());

		Assert.assertNull(auditRequestContext.getRequestId());
		Assert.assertFalse(auditRequestContext.isRequestIdGenerated());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testDoFilterResolvesRequestId() throws Exception {
		AuditRequestContext auditRequestContext = _testDoFilter(
			new MockHttpServletRequest());

		Assert.assertNotNull(auditRequestContext.getRequestId());
		Assert.assertTrue(auditRequestContext.isRequestIdGenerated());

		auditRequestContext = _testDoFilter("invalid");

		Assert.assertNotEquals("invalid", auditRequestContext.getRequestId());
		Assert.assertNotNull(auditRequestContext.getRequestId());
		Assert.assertTrue(auditRequestContext.isRequestIdGenerated());

		String xRequestId = PortalUUIDUtil.generate();

		auditRequestContext = _testDoFilter(xRequestId);

		Assert.assertEquals(xRequestId, auditRequestContext.getRequestId());
		Assert.assertFalse(auditRequestContext.isRequestIdGenerated());
	}

	private AuditRequestContext _testDoFilter(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			InvokerFilterChain invokerFilterChain = new InvokerFilterChain(
				(servletRequest, servletResponse) -> {
				});

			invokerFilterChain.addFilter(_filter);

			invokerFilterChain.doFilter(
				mockHttpServletRequest, new MockHttpServletResponse());
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

	@Inject(filter = "servlet-filter-name=Audit Filter")
	private Filter _filter;

}