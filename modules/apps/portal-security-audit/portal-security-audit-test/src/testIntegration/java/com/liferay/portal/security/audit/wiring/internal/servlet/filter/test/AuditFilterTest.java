/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.wiring.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.LiferayFilter;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterChain;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpSession;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		CompanyLocalServiceUtil.deleteCompany(_company.getCompanyId());
	}

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

		AuditRequestThreadLocal auditRequestThreadLocal = _testDoFilter(
			mockHttpServletRequest);

		Assert.assertEquals(
			auditSessionId, auditRequestThreadLocal.getSessionID());
		Assert.assertNotEquals(
			httpSession.getId(), auditRequestThreadLocal.getSessionID());
	}

	@Test
	public void testDoFilterCapturesNoAuditSessionIdBeforeAuthentication()
		throws Exception {

		AuditRequestThreadLocal auditRequestThreadLocal = _testDoFilter(
			new MockHttpServletRequest());

		Assert.assertNotNull(auditRequestThreadLocal.getRequestURL());
		Assert.assertNull(auditRequestThreadLocal.getSessionID());
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testDoFilterDoesNotResolveRequestIdWhenFeatureFlagIsDisabled()
		throws Exception {

		AuditRequestThreadLocal auditRequestThreadLocal = _testDoFilter(
			PortalUUIDUtil.generate());

		Assert.assertNull(auditRequestThreadLocal.getRequestId());
		Assert.assertFalse(auditRequestThreadLocal.isRequestIdGenerated());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testDoFilterResolvesRequestId() throws Exception {
		AuditRequestThreadLocal auditRequestThreadLocal = _testDoFilter(
			new MockHttpServletRequest());

		Assert.assertNotNull(auditRequestThreadLocal.getRequestId());
		Assert.assertTrue(auditRequestThreadLocal.isRequestIdGenerated());

		auditRequestThreadLocal = _testDoFilter("invalid");

		Assert.assertNotEquals(
			"invalid", auditRequestThreadLocal.getRequestId());
		Assert.assertNotNull(auditRequestThreadLocal.getRequestId());
		Assert.assertTrue(auditRequestThreadLocal.isRequestIdGenerated());

		String xRequestId = PortalUUIDUtil.generate();

		auditRequestThreadLocal = _testDoFilter(xRequestId);

		Assert.assertEquals(xRequestId, auditRequestThreadLocal.getRequestId());
		Assert.assertFalse(auditRequestThreadLocal.isRequestIdGenerated());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testIsFilterEnabled() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						AuditConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			Assert.assertTrue(_isFilterEnabled(TestPropsValues.getCompanyId()));
			Assert.assertFalse(_isFilterEnabled(_company.getCompanyId()));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper1 =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						AuditConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build());
			CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper2 =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AuditConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			Assert.assertFalse(
				_isFilterEnabled(TestPropsValues.getCompanyId()));
			Assert.assertTrue(_isFilterEnabled(_company.getCompanyId()));
		}
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testIsFilterEnabledWhenFeatureFlagIsDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						AuditConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			Assert.assertTrue(_isFilterEnabled(_company.getCompanyId()));
		}
	}

	private boolean _isFilterEnabled(long companyId) {
		LiferayFilter liferayFilter = (LiferayFilter)_filter;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			return liferayFilter.isFilterEnabled(
				new MockHttpServletRequest(), new MockHttpServletResponse());
		}
	}

	private AuditRequestThreadLocal _testDoFilter(
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

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		AuditRequestThreadLocal.removeAuditThreadLocal();

		return auditRequestThreadLocal;
	}

	private AuditRequestThreadLocal _testDoFilter(String xRequestId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(HttpHeaders.X_REQUEST_ID, xRequestId);

		return _testDoFilter(mockHttpServletRequest);
	}

	private static Company _company;

	@Inject(filter = "servlet-filter-name=Audit Filter")
	private Filter _filter;

}