/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class SaveAuditConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		_configurationProvider.deleteCompanyConfiguration(
			AuditConfiguration.class, TestPropsValues.getCompanyId());
		_configurationProvider.deleteSystemConfiguration(
			AuditConfiguration.class);
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessActionWithCompanyScope() throws Exception {
		_processAction(ExtendedObjectClassDefinition.Scope.COMPANY);

		AuditConfiguration auditConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId());

		Assert.assertFalse(auditConfiguration.enabled());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessActionWithCompanyScopeWhenEnabledIsOverridden()
		throws Exception {

		_testProcessActionWhenEnabledIsOverridden(
			true, ExtendedObjectClassDefinition.Scope.COMPANY);
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testProcessActionWithCompanyScopeWhenFeatureFlagIsDisabled()
		throws Exception {

		_assertProcessActionFails(
			ExtendedObjectClassDefinition.Scope.COMPANY,
			UnsupportedOperationException.class);
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessActionWithCompanyScopeWhenUserIsNotCompanyAdmin()
		throws Exception {

		_assertProcessActionFailsForUser(
			ExtendedObjectClassDefinition.Scope.COMPANY,
			PrincipalException.MustBeCompanyAdmin.class);
	}

	@Test
	public void testProcessActionWithSystemScope() throws Exception {
		_processAction(ExtendedObjectClassDefinition.Scope.SYSTEM);

		AuditConfiguration auditConfiguration =
			_configurationProvider.getSystemConfiguration(
				AuditConfiguration.class);

		Assert.assertFalse(auditConfiguration.enabled());
	}

	@Test
	public void testProcessActionWithSystemScopePersistsAuditMessageMaxQueueSize()
		throws Exception {

		int auditMessageMaxQueueSize = RandomTestUtil.randomInt();

		_processAction(
			auditMessageMaxQueueSize,
			ExtendedObjectClassDefinition.Scope.SYSTEM);

		Dictionary<String, Object> properties = _getProperties(
			ExtendedObjectClassDefinition.Scope.SYSTEM);

		Assert.assertEquals(
			auditMessageMaxQueueSize,
			properties.get("auditMessageMaxQueueSize"));
	}

	@Test
	public void testProcessActionWithSystemScopePreservesAuditMessageMaxQueueSize()
		throws Exception {

		int auditMessageMaxQueueSize = RandomTestUtil.randomInt();

		_configurationProvider.saveSystemConfiguration(
			AuditConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"auditMessageMaxQueueSize", auditMessageMaxQueueSize
			).put(
				"enabled", true
			).build());

		_processAction(ExtendedObjectClassDefinition.Scope.SYSTEM);

		Dictionary<String, Object> properties = _getProperties(
			ExtendedObjectClassDefinition.Scope.SYSTEM);

		Assert.assertEquals(
			auditMessageMaxQueueSize,
			properties.get("auditMessageMaxQueueSize"));
	}

	@Test
	public void testProcessActionWithSystemScopeWhenEnabledIsOverridden()
		throws Exception {

		_testProcessActionWhenEnabledIsOverridden(
			false, ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	@Test
	public void testProcessActionWithSystemScopeWhenUserIsNotOmniadmin()
		throws Exception {

		_assertProcessActionFailsForUser(
			ExtendedObjectClassDefinition.Scope.SYSTEM,
			PrincipalException.MustBeOmniadmin.class);
	}

	private void _assertProcessActionFails(
			ExtendedObjectClassDefinition.Scope scope,
			Class<? extends Throwable> throwableClass)
		throws Exception {

		try {
			_processAction(scope);

			Assert.fail();
		}
		catch (PortletException portletException) {
			Throwable throwable = portletException.getCause();

			Assert.assertEquals(throwableClass, throwable.getClass());
		}
	}

	private void _assertProcessActionFailsForUser(
			ExtendedObjectClassDefinition.Scope scope,
			Class<? extends Throwable> throwableClass)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		try {
			_assertProcessActionFails(scope, throwableClass);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private String _getFilterString(ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		if (ExtendedObjectClassDefinition.Scope.SYSTEM.equals(scope)) {
			return ConfigurationFilterStringUtil.getSystemScopedFilterString(
				AuditConfiguration.class.getName());
		}

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		return ConfigurationFilterStringUtil.getCompanyScopedFilterString(
			TestPropsValues.getCompanyId(), AuditConfiguration.class.getName(),
			company.getWebId());
	}

	private String _getPortletId(ExtendedObjectClassDefinition.Scope scope) {
		if (ExtendedObjectClassDefinition.Scope.SYSTEM.equals(scope)) {
			return ConfigurationAdminPortletKeys.SYSTEM_SETTINGS;
		}

		return ConfigurationAdminPortletKeys.INSTANCE_SETTINGS;
	}

	private Dictionary<String, Object> _getProperties(
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			_getFilterString(scope));

		return configurations[0].getProperties();
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private void _processAction(ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		_processAction(null, scope);
	}

	private void _processAction(
			Integer auditMessageMaxQueueSize,
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			ProxyUtil.newProxyInstance(
				LiferayPortletConfig.class.getClassLoader(),
				new Class<?>[] {LiferayPortletConfig.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getPortletId")) {
						return _getPortletId(scope);
					}

					return null;
				}));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, _getPortletId(scope));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter("enabled", "false");

		if (auditMessageMaxQueueSize != null) {
			mockLiferayPortletActionRequest.setParameter(
				"auditMessageMaxQueueSize",
				String.valueOf(auditMessageMaxQueueSize));
		}

		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private void _processActionWithEnabledOverridden(
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Map<String, Map<String, Object>> overridePropertiesMap =
			ReflectionTestUtil.getFieldValue(
				ConfigurationOverridePropertiesUtil.getOverridePropertiesMap(),
				"m");

		overridePropertiesMap.put(
			AuditConfiguration.class.getName(),
			HashMapBuilder.<String, Object>put(
				"enabled", true
			).build());

		try {
			_processAction(scope);
		}
		finally {
			overridePropertiesMap.remove(AuditConfiguration.class.getName());
		}
	}

	private void _saveConfiguration(
			boolean enabled, ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", enabled
			).build();

		if (ExtendedObjectClassDefinition.Scope.SYSTEM.equals(scope)) {
			_configurationProvider.saveSystemConfiguration(
				AuditConfiguration.class, properties);

			return;
		}

		_configurationProvider.saveCompanyConfiguration(
			AuditConfiguration.class, TestPropsValues.getCompanyId(),
			properties);
	}

	private void _testProcessActionWhenEnabledIsOverridden(
			boolean enabled, ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		_saveConfiguration(enabled, scope);

		_processActionWithEnabledOverridden(scope);

		Dictionary<String, Object> properties = _getProperties(scope);

		Assert.assertEquals(enabled, properties.get("enabled"));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationProvider _configurationProvider;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/portal_security_audit_configuration/save_audit_configuration",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@DeleteAfterTestRun
	private User _user;

}