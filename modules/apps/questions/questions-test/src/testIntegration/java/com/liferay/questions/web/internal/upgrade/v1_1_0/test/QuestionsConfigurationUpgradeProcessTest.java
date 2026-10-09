/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class QuestionsConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Configuration systemConfiguration = _getSystemConfiguration();

		if (systemConfiguration != null) {
			_originalSystemConfigurationProperties =
				systemConfiguration.getProperties();

			systemConfiguration.delete();
		}

		Configuration[] scopedConfigurations = _getScopedConfigurations();

		_originalScopedConfigurationsProperties = new ArrayList<>();

		for (Configuration scopedConfiguration : scopedConfigurations) {
			if (scopedConfiguration == null) {
				continue;
			}

			_originalScopedConfigurationsProperties.add(
				scopedConfiguration.getProperties());

			scopedConfiguration.delete();
		}
	}

	@After
	public void tearDown() throws Exception {
		_deleteScopedConfigurations();
		_deleteSystemConfiguration();

		if (_originalSystemConfigurationProperties != null) {
			_createSystemConfiguration(_originalSystemConfigurationProperties);
		}

		for (Dictionary<String, Object> properties :
				_originalScopedConfigurationsProperties) {

			_createScopedConfiguration(properties);
		}
	}

	@Test
	public void testUpgradeWithDefaultTopicRootId() throws Exception {
		_testUpgrade(
			StringPool.BLANK, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
	}

	@Test
	public void testUpgradeWithInvalidTopicRootId() throws Exception {
		_testUpgrade(StringPool.BLANK, RandomTestUtil.randomLong());
	}

	@Test
	public void testUpgradeWithValidTopicRootId() throws Exception {
		MBCategory mbCategory = _mbCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_testUpgrade(
			mbCategory.getExternalReferenceCode(), mbCategory.getCategoryId());
	}

	private void _assertConfigurationValueEquals(
		Configuration configuration, String rootTopicExternalReferenceCode) {

		Assert.assertNotNull(configuration);

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertNotNull(properties);
		Assert.assertEquals(
			rootTopicExternalReferenceCode,
			properties.get("rootTopicExternalReferenceCode"));
	}

	private void _createCompanyConfiguration(
			long companyId, Dictionary<String, Object> properties)
		throws Exception {

		properties.put(
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			companyId);

		_createScopedConfiguration(properties);
	}

	private HashMapDictionary<String, Object> _createDictionary(
		long rootTopicId) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"rootTopicId", rootTopicId
		).build();
	}

	private void _createScopedConfiguration(
			Dictionary<String, Object> properties)
		throws Exception {

		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				_CLASS_NAME_QUESTIONS_CONFIGURATION + ".scoped",
				StringPool.QUESTION);

		configuration.update(properties);
	}

	private void _createSystemConfiguration(
			Dictionary<String, Object> properties)
		throws Exception {

		Configuration configuration = _configurationAdmin.getConfiguration(
			_CLASS_NAME_QUESTIONS_CONFIGURATION, StringPool.QUESTION);

		configuration.update(properties);
	}

	private void _deleteScopedConfigurations() throws Exception {
		Configuration[] configurations = _getScopedConfigurations();

		for (Configuration configuration : configurations) {
			if (configuration == null) {
				continue;
			}

			configuration.delete();
		}
	}

	private void _deleteSystemConfiguration() throws Exception {
		Configuration configuration = _getSystemConfiguration();

		if (configuration == null) {
			return;
		}

		configuration.delete();
	}

	private Configuration[] _getScopedConfigurations() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(%s=%s)", ConfigurationAdmin.SERVICE_FACTORYPID,
				_CLASS_NAME_QUESTIONS_CONFIGURATION + ".scoped"));

		if (configurations == null) {
			return new Configuration[0];
		}

		return configurations;
	}

	private Configuration _getSystemConfiguration() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(%s=%s)", Constants.SERVICE_PID,
				_CLASS_NAME_QUESTIONS_CONFIGURATION));

		if (configurations == null) {
			return null;
		}

		return configurations[0];
	}

	private UpgradeProcess _getUpgradeProcess() {
		UpgradeProcess[] upgradeProcesses = new UpgradeProcess[1];

		_upgradeStepRegistrator.register(
			(fromSchemaVersionString, toSchemaVersionString, upgradeSteps) -> {
				for (UpgradeStep upgradeStep : upgradeSteps) {
					Class<? extends UpgradeStep> clazz = upgradeStep.getClass();

					if (Objects.equals(
							clazz.getName(), _CLASS_NAME_UPGRADE_PROCESS)) {

						upgradeProcesses[0] = (UpgradeProcess)upgradeStep;

						break;
					}
				}
			});

		return upgradeProcesses[0];
	}

	private void _testUpgrade(
			String rootTopicExternalReferenceCode, long rootTopicId)
		throws Exception {

		_createCompanyConfiguration(
			TestPropsValues.getCompanyId(), _createDictionary(rootTopicId));
		_createSystemConfiguration(_createDictionary(rootTopicId));

		try {
			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_CLASS_NAME_UPGRADE_PROCESS, LoggerTestUtil.OFF)) {

				UpgradeProcess upgradeProcess = _getUpgradeProcess();

				upgradeProcess.upgrade();
			}

			_assertConfigurationValueEquals(
				_getSystemConfiguration(), rootTopicExternalReferenceCode);

			for (Configuration configuration : _getScopedConfigurations()) {
				_assertConfigurationValueEquals(
					configuration, rootTopicExternalReferenceCode);
			}
		}
		finally {
			_deleteScopedConfigurations();
			_deleteSystemConfiguration();
		}
	}

	private static final String _CLASS_NAME_QUESTIONS_CONFIGURATION =
		"com.liferay.questions.web.internal.configuration." +
			"QuestionsConfiguration";

	private static final String _CLASS_NAME_UPGRADE_PROCESS =
		"com.liferay.questions.web.internal.upgrade.v1_1_0." +
			"QuestionsConfigurationUpgradeProcess";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private MBCategoryLocalService _mbCategoryLocalService;

	private List<Dictionary<String, Object>>
		_originalScopedConfigurationsProperties;
	private Dictionary<String, Object> _originalSystemConfigurationProperties;

	@Inject(
		filter = "(&(component.name=com.liferay.questions.web.internal.upgrade.registry.QuestionsWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}