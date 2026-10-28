/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.action.LayoutAction;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.struts.model.ActionMapping;
import com.liferay.portal.struts.model.ModuleConfig;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
public class LayoutActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_impersonatedUser = UserTestUtil.addUser();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", ActionKeys.VIEW);

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage), properties);
	}

	@After
	public void tearDown() throws Exception {
		AuditRequestContextThreadLocal.removeAuditRequestContext();

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testExecute() throws Exception {
		_testExecute();
		_testExecuteWhenFeatureFlagIsDisabled();
	}

	private AuditMessage _execute(long realUserId) throws Exception {
		_auditMessages.clear();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setDoAsUserId(RandomTestUtil.randomString());
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setSignedIn(true);
		themeDisplay.setUser(_impersonatedUser);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"p_l_id", String.valueOf(_layout.getPlid()));

		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		auditRequestContext.setRealUserId(realUserId);

		PrincipalThreadLocal.setName(_impersonatedUser.getUserId());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"AUDIT_MESSAGE_COM_LIFERAY_PORTAL_MODEL_LAYOUT_VIEW",
					true)) {

			LayoutAction layoutAction = new LayoutAction();

			layoutAction.execute(
				new ActionMapping(null, null, true, new ModuleConfig(), null),
				mockHttpServletRequest, new MockHttpServletResponse());
		}

		Assert.assertEquals(
			_auditMessages.toString(), 1, _auditMessages.size());

		return _auditMessages.get(0);
	}

	private void _testExecute() throws Exception {
		AuditMessage auditMessage = _execute(TestPropsValues.getUserId());

		Assert.assertTrue(auditMessage.isImpersonated());
		Assert.assertEquals(
			_impersonatedUser.getUserId(),
			auditMessage.getImpersonatedUserId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertFalse(
			additionalInfoJSONObject.has("doAsUserEmailAddress"));
		Assert.assertFalse(additionalInfoJSONObject.has("doAsUserId"));
		Assert.assertFalse(additionalInfoJSONObject.has("doAsUserName"));
	}

	private void _testExecuteWhenFeatureFlagIsDisabled() throws Exception {
		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-6417")) {

			AuditMessage auditMessage = _execute(0);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			Assert.assertEquals(
				_impersonatedUser.getEmailAddress(),
				additionalInfoJSONObject.getString("doAsUserEmailAddress"));
			Assert.assertEquals(
				_impersonatedUser.getUserId(),
				additionalInfoJSONObject.getLong("doAsUserId"));
			Assert.assertEquals(
				_impersonatedUser.getFullName(),
				additionalInfoJSONObject.getString("doAsUserName"));
		}
	}

	private final List<AuditMessage> _auditMessages = new ArrayList<>();

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private User _impersonatedUser;

	private Layout _layout;
	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}