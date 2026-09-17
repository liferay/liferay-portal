/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.internal.events.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

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
 * @author Christian Moura
 */
@FeatureFlag("LPD-6417")
@RunWith(Arquillian.class)
public class ImpersonationActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_impersonatedUser = UserTestUtil.addUser();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", EventTypes.IMPERSONATE);

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage), properties);
	}

	@After
	public void tearDown() throws Exception {
		AuditRequestContextThreadLocal.removeAuditRequestContext();

		PrincipalThreadLocal.setName(null);

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testRun() throws Exception {
		_testRun();
		_testRunWhenFeatureFlagIsDisabled();
	}

	private AuditMessage _run() throws Exception {
		_auditMessages.clear();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setDoAsUserId(RandomTestUtil.randomString());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setUser(_impersonatedUser);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"p_l_id", String.valueOf(RandomTestUtil.randomLong()));

		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		auditRequestContext.setRealUserId(TestPropsValues.getUserId());

		PrincipalThreadLocal.setName(_impersonatedUser.getUserId());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			_impersonationAction.run(
				mockHttpServletRequest, new MockHttpServletResponse());
		}

		Assert.assertEquals(
			_auditMessages.toString(), 1, _auditMessages.size());

		return _auditMessages.get(0);
	}

	private void _testRun() throws Exception {
		AuditMessage auditMessage = _run();

		Assert.assertTrue(auditMessage.isImpersonated());
		Assert.assertEquals(
			_impersonatedUser.getEmailAddress(),
			auditMessage.getImpersonatedUserEmailAddress());
		Assert.assertEquals(
			_impersonatedUser.getUserId(),
			auditMessage.getImpersonatedUserId());
		Assert.assertEquals(
			_impersonatedUser.getFullName(),
			auditMessage.getImpersonatedUserName());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertFalse(additionalInfoJSONObject.has("userEmailAddress"));
		Assert.assertFalse(additionalInfoJSONObject.has("userId"));
		Assert.assertFalse(additionalInfoJSONObject.has("userName"));
	}

	private void _testRunWhenFeatureFlagIsDisabled() throws Exception {
		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-6417")) {

			AuditMessage auditMessage = _run();

			Assert.assertFalse(auditMessage.isImpersonated());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			Assert.assertEquals(
				_impersonatedUser.getEmailAddress(),
				additionalInfoJSONObject.getString("userEmailAddress"));
			Assert.assertEquals(
				_impersonatedUser.getUserId(),
				additionalInfoJSONObject.getLong("userId"));
			Assert.assertEquals(
				_impersonatedUser.getFullName(),
				additionalInfoJSONObject.getString("userName"));
		}
	}

	private final List<AuditMessage> _auditMessages = new ArrayList<>();

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private User _impersonatedUser;

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.event.generators.internal.events.ImpersonationAction",
		type = LifecycleAction.class
	)
	private Action _impersonationAction;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}