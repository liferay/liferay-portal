/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.internal.events.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilterChain;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Álvaro Saugar
 */
@RunWith(Arquillian.class)
public class LoginPostActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", "*");

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage), properties);
	}

	@After
	public void tearDown() throws Exception {
		AuditRequestThreadLocal.removeAuditThreadLocal();

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testRunGeneratesOpaqueAuditSessionId() throws Exception {
		MockHttpServletRequest mockHttpServletRequest1 =
			_createMockHttpServletRequest();

		_run(mockHttpServletRequest1);

		MockHttpServletRequest mockHttpServletRequest2 =
			_createMockHttpServletRequest();

		_run(mockHttpServletRequest2);

		String auditSessionId1 = _getAuditSessionId(mockHttpServletRequest1);
		String auditSessionId2 = _getAuditSessionId(mockHttpServletRequest2);

		HttpSession httpSession1 = mockHttpServletRequest1.getSession();
		HttpSession httpSession2 = mockHttpServletRequest2.getSession();

		Assert.assertNotEquals(auditSessionId1, auditSessionId2);
		Assert.assertNotEquals(httpSession1.getId(), auditSessionId1);
		Assert.assertNotEquals(httpSession2.getId(), auditSessionId2);
	}

	@Test
	public void testRunKeepsAuditSessionIdAcrossRequests() throws Exception {
		MockHttpServletRequest mockHttpServletRequest1 =
			_createMockHttpServletRequest();

		_run(mockHttpServletRequest1);

		AuditMessage auditMessage = _fetchAuditMessage(EventTypes.LOGIN);

		String auditSessionId = auditMessage.getSessionID();

		Assert.assertNotNull(auditSessionId);

		MockHttpServletRequest mockHttpServletRequest2 =
			new MockHttpServletRequest();

		mockHttpServletRequest2.setSession(
			mockHttpServletRequest1.getSession());

		_testDoFilter(mockHttpServletRequest2);

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		Assert.assertEquals(
			auditSessionId, auditRequestThreadLocal.getSessionID());
	}

	@Test
	public void testRunKeepsAuditSessionIdAcrossSessionRenewal()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_createMockHttpServletRequest();

		_run(mockHttpServletRequest);

		String auditSessionId = _getAuditSessionId(mockHttpServletRequest);

		HttpSession httpSession = mockHttpServletRequest.getSession();

		String containerSessionId = httpSession.getId();

		AuthenticatedSessionManagerUtil.renewSession(
			mockHttpServletRequest, httpSession);

		_run(mockHttpServletRequest);

		HttpSession renewedHttpSession = mockHttpServletRequest.getSession();

		Assert.assertNotEquals(containerSessionId, renewedHttpSession.getId());

		Assert.assertEquals(
			auditSessionId, _getAuditSessionId(mockHttpServletRequest));

		AuditMessage auditMessage = _fetchAuditMessage(EventTypes.LOGIN);

		Assert.assertEquals(auditSessionId, auditMessage.getSessionID());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"SESSION_PHISHING_PROTECTED_ATTRIBUTES",
					ArrayUtil.remove(
						PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES,
						WebKeys.AUDIT_SESSION_ID))) {

			MockHttpServletRequest unprotectedMockHttpServletRequest =
				_createMockHttpServletRequest();

			_run(unprotectedMockHttpServletRequest);

			String unprotectedAuditSessionId = _getAuditSessionId(
				unprotectedMockHttpServletRequest);

			AuthenticatedSessionManagerUtil.renewSession(
				unprotectedMockHttpServletRequest,
				unprotectedMockHttpServletRequest.getSession());

			_run(unprotectedMockHttpServletRequest);

			Assert.assertNotEquals(
				unprotectedAuditSessionId,
				_getAuditSessionId(unprotectedMockHttpServletRequest));
		}
	}

	private MockHttpServletRequest _createMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockHttpServletRequest.setAttribute(WebKeys.USER, _user);

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.USER_ID, _user.getUserId());

		return mockHttpServletRequest;
	}

	private AuditMessage _fetchAuditMessage(String eventType) {
		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(auditMessage.getEventType(), eventType)) {
				return auditMessage;
			}
		}

		return null;
	}

	private String _getAuditSessionId(
		MockHttpServletRequest mockHttpServletRequest) {

		HttpSession httpSession = mockHttpServletRequest.getSession();

		return (String)httpSession.getAttribute(WebKeys.AUDIT_SESSION_ID);
	}

	private void _run(MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		_auditMessages.clear();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			_loginPostAction.run(
				mockHttpServletRequest, new MockHttpServletResponse());
		}
	}

	private void _testDoFilter(MockHttpServletRequest mockHttpServletRequest)
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

	private final List<AuditMessage> _auditMessages = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.wiring.internal.servlet.filter.AuditFilter"
	)
	private Filter _filter;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.event.generators.internal.events.LoginPostAction",
		type = LifecycleAction.class
	)
	private Action _loginPostAction;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

}