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
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
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
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.router.configuration.FileSystemAuditMessageProcessorConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
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
		_configurationProvider.deleteCompanyConfiguration(
			FileSystemAuditMessageProcessorConfiguration.class,
			TestPropsValues.getCompanyId());
		_configurationProvider.deleteSystemConfiguration(
			AuditConfiguration.class);
		_configurationProvider.deleteSystemConfiguration(
			FileSystemAuditMessageProcessorConfiguration.class);
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessActionRoutesAuditMessage() throws Exception {
		_testProcessActionRoutesAuditMessage(
			ExtendedObjectClassDefinition.Scope.COMPANY);
		_testProcessActionRoutesAuditMessage(
			ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessActionWithCompanyScope() throws Exception {
		_processAction(ExtendedObjectClassDefinition.Scope.COMPANY);

		AuditConfiguration auditConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AuditConfiguration.class, TestPropsValues.getCompanyId());

		Assert.assertTrue(auditConfiguration.enabled());
		Assert.assertFalse(auditConfiguration.pseudonymizationEnabled());

		_assertFileSystemAuditMessageProcessorConfiguration(
			_configurationProvider.getCompanyConfiguration(
				FileSystemAuditMessageProcessorConfiguration.class,
				TestPropsValues.getCompanyId()));
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessActionWithCompanyScopeDoesNotAffectOtherInstances()
		throws Exception {

		_processAction(ExtendedObjectClassDefinition.Scope.COMPANY);

		FileSystemAuditMessageProcessorConfiguration
			fileSystemAuditMessageProcessorConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FileSystemAuditMessageProcessorConfiguration.class,
					RandomTestUtil.randomLong());

		Assert.assertFalse(
			fileSystemAuditMessageProcessorConfiguration.enabled());
		Assert.assertFalse(
			fileSystemAuditMessageProcessorConfiguration.generateChecksum());
		Assert.assertNotEquals(
			_OUTPUT_DIRECTORY,
			fileSystemAuditMessageProcessorConfiguration.outputDirectory());
		Assert.assertEquals(
			"NDJSON",
			fileSystemAuditMessageProcessorConfiguration.outputFormat());
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
	public void testProcessActionWithCompanyScopeWhenFileSystemProcessorIsOverridden()
		throws Exception {

		_testProcessActionWhenFileSystemProcessorIsOverridden(
			ExtendedObjectClassDefinition.Scope.COMPANY);
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcessActionWithCompanyScopeWhenFileSystemProcessorParametersAreAbsent()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			FileSystemAuditMessageProcessorConfiguration.class,
			TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).put(
				"generateChecksum", true
			).put(
				"outputDirectory", _OUTPUT_DIRECTORY
			).put(
				"outputFormat", _OUTPUT_FORMAT
			).build());

		_processAction(
			null, false, ExtendedObjectClassDefinition.Scope.COMPANY);

		_assertFileSystemAuditMessageProcessorConfiguration(
			_configurationProvider.getCompanyConfiguration(
				FileSystemAuditMessageProcessorConfiguration.class,
				TestPropsValues.getCompanyId()));
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
		Assert.assertFalse(auditConfiguration.pseudonymizationEnabled());

		_assertFileSystemAuditMessageProcessorConfiguration(
			_configurationProvider.getSystemConfiguration(
				FileSystemAuditMessageProcessorConfiguration.class));
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

		_configurationProvider.saveSystemConfiguration(
			AuditConfiguration.class,
			MapUtil.singletonDictionary("enabled", false));

		_processActionWithOverriddenConfiguration(
			AuditConfiguration.class,
			HashMapBuilder.<String, Object>put(
				"enabled", true
			).build(),
			ExtendedObjectClassDefinition.Scope.SYSTEM);

		Dictionary<String, Object> properties = _getProperties(
			ExtendedObjectClassDefinition.Scope.SYSTEM);

		Assert.assertEquals(Boolean.FALSE, properties.get("enabled"));
	}

	@Test
	public void testProcessActionWithSystemScopeWhenFileSystemProcessorIsOverridden()
		throws Exception {

		_testProcessActionWhenFileSystemProcessorIsOverridden(
			ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	@Test
	public void testProcessActionWithSystemScopeWhenUserIsNotOmniadmin()
		throws Exception {

		_assertProcessActionFailsForUser(
			ExtendedObjectClassDefinition.Scope.SYSTEM,
			PrincipalException.MustBeOmniadmin.class);
	}

	private void _assertFileSystemAuditMessageProcessorConfiguration(
		FileSystemAuditMessageProcessorConfiguration
			fileSystemAuditMessageProcessorConfiguration) {

		Assert.assertTrue(
			fileSystemAuditMessageProcessorConfiguration.enabled());
		Assert.assertTrue(
			fileSystemAuditMessageProcessorConfiguration.generateChecksum());
		Assert.assertEquals(
			_OUTPUT_DIRECTORY,
			fileSystemAuditMessageProcessorConfiguration.outputDirectory());
		Assert.assertEquals(
			_OUTPUT_FORMAT,
			fileSystemAuditMessageProcessorConfiguration.outputFormat());
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

	private MockLiferayPortletActionRequest
			_createMockLiferayPortletActionRequest(
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
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private String _getFilterString(
			Class<?> clazz, ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		if (ExtendedObjectClassDefinition.Scope.SYSTEM.equals(scope)) {
			return ConfigurationFilterStringUtil.getSystemScopedFilterString(
				clazz.getName());
		}

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		return ConfigurationFilterStringUtil.getCompanyScopedFilterString(
			TestPropsValues.getCompanyId(), clazz.getName(),
			company.getWebId());
	}

	private String _getPortletId(ExtendedObjectClassDefinition.Scope scope) {
		if (ExtendedObjectClassDefinition.Scope.SYSTEM.equals(scope)) {
			return ConfigurationAdminPortletKeys.SYSTEM_SETTINGS;
		}

		return ConfigurationAdminPortletKeys.INSTANCE_SETTINGS;
	}

	private Dictionary<String, Object> _getProperties(
			Class<?> clazz, ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			_getFilterString(clazz, scope));

		return configurations[0].getProperties();
	}

	private Dictionary<String, Object> _getProperties(
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		return _getProperties(AuditConfiguration.class, scope);
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
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processAction(ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		_processAction(null, true, scope);
	}

	private void _processAction(
			Integer auditMessageMaxQueueSize,
			boolean setFileSystemAuditMessageProcessorParameters,
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_createMockLiferayPortletActionRequest(scope);

		mockLiferayPortletActionRequest.setParameter("enabled", "false");
		mockLiferayPortletActionRequest.setParameter(
			"pseudonymizationEnabled", "false");

		if (auditMessageMaxQueueSize != null) {
			mockLiferayPortletActionRequest.setParameter(
				"auditMessageMaxQueueSize",
				String.valueOf(auditMessageMaxQueueSize));
		}

		if (setFileSystemAuditMessageProcessorParameters) {
			mockLiferayPortletActionRequest.setParameter(
				"fileSystemAuditMessageProcessorEnabled", "true");
			mockLiferayPortletActionRequest.setParameter(
				"fileSystemAuditMessageProcessorGenerateChecksum", "true");
			mockLiferayPortletActionRequest.setParameter(
				"fileSystemAuditMessageProcessorOutputDirectory",
				_OUTPUT_DIRECTORY);
			mockLiferayPortletActionRequest.setParameter(
				"fileSystemAuditMessageProcessorOutputFormat", _OUTPUT_FORMAT);
		}

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private void _processAction(
			Integer auditMessageMaxQueueSize,
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		_processAction(auditMessageMaxQueueSize, true, scope);
	}

	private void _processActionWithOverriddenConfiguration(
			Class<?> clazz, Map<String, Object> overrideProperties,
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Map<String, Map<String, Object>> overridePropertiesMap =
			ReflectionTestUtil.getFieldValue(
				ConfigurationOverridePropertiesUtil.getOverridePropertiesMap(),
				"m");

		overridePropertiesMap.put(clazz.getName(), overrideProperties);

		try {
			_processAction(scope);
		}
		finally {
			overridePropertiesMap.remove(clazz.getName());
		}
	}

	private void _testProcessActionRoutesAuditMessage(
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		List<AuditMessage> auditMessages = new ArrayList<>();

		ServiceRegistration<AuditMessageProcessor> serviceRegistration =
			bundleContext.registerService(
				AuditMessageProcessor.class, auditMessages::add,
				HashMapDictionaryBuilder.<String, Object>put(
					"eventTypes", "*"
				).build());

		try {
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_createMockLiferayPortletActionRequest(scope);

			mockLiferayPortletActionRequest.setParameter(
				"pseudonymizationEnabled", "false");

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			AuditMessage auditMessage = null;

			for (AuditMessage curAuditMessage : auditMessages) {
				if (Objects.equals(
						curAuditMessage.getEventType(),
						"AUDIT_CONFIG_CHANGE")) {

					auditMessage = curAuditMessage;

					break;
				}
			}

			Assert.assertEquals(
				AuditConfiguration.class.getName(),
				auditMessage.getClassName());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			JSONArray attributesJSONArray =
				additionalInfoJSONObject.getJSONArray("attributes");

			JSONObject attributeJSONObject = attributesJSONArray.getJSONObject(
				0);

			Assert.assertEquals(
				"pseudonymizationEnabled",
				attributeJSONObject.getString("name"));
			Assert.assertFalse(attributeJSONObject.getBoolean("newValue"));
			Assert.assertTrue(attributeJSONObject.getBoolean("oldValue"));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testProcessActionWhenFileSystemProcessorIsOverridden(
			ExtendedObjectClassDefinition.Scope scope)
		throws Exception {

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", false
			).put(
				"generateChecksum", false
			).put(
				"outputDirectory", _OUTPUT_DIRECTORY
			).put(
				"outputFormat", _OUTPUT_FORMAT
			).build();

		if (ExtendedObjectClassDefinition.Scope.SYSTEM.equals(scope)) {
			_configurationProvider.saveSystemConfiguration(
				FileSystemAuditMessageProcessorConfiguration.class, properties);
		}
		else {
			_configurationProvider.saveCompanyConfiguration(
				FileSystemAuditMessageProcessorConfiguration.class,
				TestPropsValues.getCompanyId(), properties);
		}

		_processActionWithOverriddenConfiguration(
			FileSystemAuditMessageProcessorConfiguration.class,
			HashMapBuilder.<String, Object>put(
				"enabled", true
			).put(
				"generateChecksum", true
			).put(
				"outputDirectory", "data/other"
			).put(
				"outputFormat", "NDJSON"
			).build(),
			scope);

		Dictionary<String, Object> currentProperties = _getProperties(
			FileSystemAuditMessageProcessorConfiguration.class, scope);

		Assert.assertEquals(Boolean.FALSE, currentProperties.get("enabled"));
		Assert.assertEquals(
			Boolean.FALSE, currentProperties.get("generateChecksum"));
		Assert.assertEquals(
			_OUTPUT_DIRECTORY, currentProperties.get("outputDirectory"));
		Assert.assertEquals(
			_OUTPUT_FORMAT, currentProperties.get("outputFormat"));
	}

	private static final String _OUTPUT_DIRECTORY = "data/audit-test";

	private static final String _OUTPUT_FORMAT = "CSV";

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