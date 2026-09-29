/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.upgrade.v1_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.security.script.management.test.util.ScriptManagementConfigurationTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Closeable;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Sousa
 */
@RunWith(Arquillian.class)
public class ScriptManagementConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		ScriptManagementConfigurationTestUtil.delete();
	}

	@Test
	public void testUpgradeDoesNotDowngradeEnabledFlag() throws Exception {
		try (Closeable closeable =
				ScriptManagementConfigurationTestUtil.saveWithCloseable(true)) {

			Assert.assertTrue(
				_scriptManagementConfigurationHelper.
					isAllowScriptContentToBeExecutedOrIncluded());

			_runUpgrade();

			Assert.assertTrue(
				_scriptManagementConfigurationHelper.
					isAllowScriptContentToBeExecutedOrIncluded());
		}
	}

	@Test
	public void testUpgradeDoesNotRestoreFlagForBuiltInWorkflowDefinition()
		throws Exception {

		Assert.assertFalse(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());

		_runUpgrade();

		Assert.assertFalse(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());
	}

	@Test
	public void testUpgradeRestoresFlagForPublishedDRLWorkflowDefinition()
		throws Exception {

		try (Closeable closeable =
				ScriptManagementConfigurationTestUtil.saveWithCloseable(true)) {

			_workflowDefinitionManager.deployWorkflowDefinition(
				_getContentBytes("workflow-definition-4.json"),
				TestPropsValues.getCompanyId(), null, StringUtil.randomId(),
				StringUtil.randomId(), TestPropsValues.getUserId());
		}

		Assert.assertFalse(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());

		_runUpgrade();

		Assert.assertTrue(
			_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded());
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

	private static final String _CLASS_NAME =
		"com.liferay.portal.security.script.management.web.internal.upgrade." +
			"v1_2_0.ScriptManagementConfigurationUpgradeProcess";

	@Inject
	private MultiVMPool _multiVMPool;

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