/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.security.script.management.test.util.ScriptManagementConfigurationTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Closeable;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class ScriptManagementConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = _createObjectDefinition(
			TestPropsValues.getUserId());
	}

	@After
	public void tearDown() {
		ScriptManagementConfigurationTestUtil.delete();
	}

	@Test
	public void testUpgradeSafeResources() throws Exception {
		try (Closeable closeable =
				ScriptManagementConfigurationTestUtil.saveWithCloseable(true)) {

			_addObjectAction(
				true, "ActiveWebhookObjectAction",
				ObjectActionExecutorConstants.KEY_WEBHOOK, _objectDefinition,
				TestPropsValues.getUserId());
			_addObjectAction(
				false, "InactiveGroovyObjectAction",
				ObjectActionExecutorConstants.KEY_GROOVY, _objectDefinition,
				TestPropsValues.getUserId());

			_addObjectValidationRule(
				true, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				"ActiveDDMObjectValidation", _objectDefinition,
				TestPropsValues.getUserId());
			_addObjectValidationRule(
				false, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
				"InactiveGroovyObjectValidation", _objectDefinition,
				TestPropsValues.getUserId());

			_workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), "PublishedWorkflowDefinition",
				StringUtil.randomId(),
				_getContentBytes("workflow-definition-1.json"));

			_workflowDefinitionManager.saveWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(),
				"UnpublishedGroovyWorkflowDefinition", StringUtil.randomId(),
				_getContentBytes("workflow-definition-2.json"));
			_workflowDefinitionManager.saveWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(),
				"UnpublishedJavaWorkflowDefinition", StringUtil.randomId(),
				_getContentBytes("workflow-definition-3.json"));
		}

		Assert.assertFalse(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());

		_runUpgrade();

		Assert.assertFalse(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());
	}

	@Test
	public void testUpgradeUnsafeResourceActiveGroovyObjectAction()
		throws Exception {

		_testUpgrade(
			() -> _addObjectAction(
				true, StringUtil.randomId(),
				ObjectActionExecutorConstants.KEY_GROOVY, _objectDefinition,
				TestPropsValues.getUserId()));
	}

	@Test
	public void testUpgradeUnsafeResourceActiveGroovyObjectValidation()
		throws Exception {

		_testUpgrade(
			() -> _addObjectValidationRule(
				true, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
				StringUtil.randomId(), _objectDefinition,
				TestPropsValues.getUserId()));
	}

	@Test
	public void testUpgradeUnsafeResourcePublishedGroovyWorkflowDefinition()
		throws Exception {

		_testUpgrade(
			() -> _workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), StringUtil.randomId(),
				StringUtil.randomId(),
				_getContentBytes("workflow-definition-2.json")));
	}

	@Test
	public void testUpgradeUnsafeResourcePublishedJavaWorkflowDefinition()
		throws Exception {

		_testUpgrade(
			() -> _workflowDefinitionManager.deployWorkflowDefinition(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), StringUtil.randomId(),
				StringUtil.randomId(),
				_getContentBytes("workflow-definition-3.json")));
	}

	private ObjectAction _addObjectAction(
			boolean active, String label, String objectActionExecutorKey,
			ObjectDefinition objectDefinition, long userId)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"secret", "onafteradd"
		).put(
			"url", "https://onafteradd.com"
		).build();

		if (Objects.equals(
				ObjectActionExecutorConstants.KEY_GROOVY,
				objectActionExecutorKey)) {

			unicodeProperties = UnicodePropertiesBuilder.put(
				"script", "println \"Hello World \""
			).build();
		}

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), userId,
			objectDefinition.getObjectDefinitionId(), active, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(label),
			RandomTestUtil.randomString(), objectActionExecutorKey,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, unicodeProperties,
			false);
	}

	private ObjectValidationRule _addObjectValidationRule(
			boolean active, String engine, String label,
			ObjectDefinition objectDefinition, long userId)
		throws Exception {

		String script = "isEmailAddress(textObjectField)";

		if (Objects.equals(
				ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY, engine)) {

			script = "println \"Hello World \"";
		}

		return _objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, userId, objectDefinition.getObjectDefinitionId(),
			active, engine,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(label),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION, script,
			false, Collections.emptyList());
	}

	private ObjectDefinition _createObjectDefinition(long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				userId, 0, null, false, false, true, false, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).build()));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private byte[] _getContentBytes(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			StringBundler.concat(
				"com/liferay/portal/security/script/management/web/internal",
				"/portlet/action/test/dependencies/", fileName));

		String content = StringUtil.read(inputStream);

		return content.getBytes();
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private void _testUpgrade(UnsafeSupplier<Object, Exception> unsafeSupplier)
		throws Exception {

		try (Closeable closeable =
				ScriptManagementConfigurationTestUtil.saveWithCloseable(true)) {

			unsafeSupplier.get();
		}

		Assert.assertFalse(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());

		_runUpgrade();

		Assert.assertTrue(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.security.script.management.web.internal.upgrade." +
			"v1_1_0.ScriptManagementConfigurationUpgradeProcess";

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

	@Inject(
		filter = "(&(component.name=com.liferay.portal.security.script.management.web.internal.upgrade.registry.ScriptManagementWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}