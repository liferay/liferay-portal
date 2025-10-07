/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-07
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v1_4_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mateus Xavier
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class KaleoDefinitionVersionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		String name = StringUtil.randomString();

		_addKaleoDefinition(name, 1);
		_addKaleoDefinition(name, 2);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				name, ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(name, kaleoDefinition.getName());

		KaleoDefinitionVersion kaleoDefinitionVersion1 =
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				TestPropsValues.getCompanyId(), name, _getVersion(1));

		Assert.assertEquals(
			_getVersion(1), kaleoDefinitionVersion1.getVersion());

		KaleoDefinitionVersion kaleoDefinitionVersion2 =
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				TestPropsValues.getCompanyId(), name, _getVersion(2));

		Assert.assertEquals(
			_getVersion(2), kaleoDefinitionVersion2.getVersion());
	}

	private void _addKaleoDefinition(String name, int version)
		throws Exception {

		_kaleoDefinitionLocalService.addKaleoDefinition(
			RandomTestUtil.randomString(), name, StringUtil.randomString(),
			StringUtil.randomString(), StringPool.BLANK,
			WorkflowDefinitionConstants.SCOPE_ALL, version,
			ServiceContextTestUtil.getServiceContext());
	}

	private String _getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.workflow.kaleo.internal.upgrade.v1_4_1." +
			"KaleoDefinitionVersionUpgradeProcess";

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.internal.upgrade.registry.KaleoServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}