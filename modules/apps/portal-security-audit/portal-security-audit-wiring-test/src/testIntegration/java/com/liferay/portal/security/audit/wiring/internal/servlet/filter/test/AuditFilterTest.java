/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.wiring.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterChain;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.WebKeys;
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
		AuditRequestThreadLocal.removeAuditThreadLocal();
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

		_runAuditFilter(mockHttpServletRequest);

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		Assert.assertEquals(
			auditSessionId, auditRequestThreadLocal.getSessionID());
		Assert.assertNotEquals(
			httpSession.getId(), auditRequestThreadLocal.getSessionID());
	}

	@Test
	public void testDoFilterCapturesNoAuditSessionIdBeforeAuthentication()
		throws Exception {

		_runAuditFilter(new MockHttpServletRequest());

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		Assert.assertNull(auditRequestThreadLocal.getSessionID());
	}

	private void _runAuditFilter(MockHttpServletRequest mockHttpServletRequest)
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
	}

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.wiring.internal.servlet.filter.AuditFilter"
	)
	private Filter _filter;

}