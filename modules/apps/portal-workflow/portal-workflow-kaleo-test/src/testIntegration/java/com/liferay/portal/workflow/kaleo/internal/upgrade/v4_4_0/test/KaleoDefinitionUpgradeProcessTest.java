/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_4_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Sousa
 */
@RunWith(Arquillian.class)
public class KaleoDefinitionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		KaleoDefinition kaleoDefinition1 = _addKaleoDefinition(false);
		KaleoDefinition kaleoDefinition2 = _addKaleoDefinition(true);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		kaleoDefinition1 = _kaleoDefinitionLocalService.fetchKaleoDefinition(
			kaleoDefinition1.getKaleoDefinitionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, kaleoDefinition1.getStatus());

		kaleoDefinition2 = _kaleoDefinitionLocalService.fetchKaleoDefinition(
			kaleoDefinition2.getKaleoDefinitionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, kaleoDefinition2.getStatus());
	}

	private KaleoDefinition _addKaleoDefinition(boolean active)
		throws Exception {

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.addKaleoDefinition(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringUtil.randomString(), StringUtil.randomString(),
				StringPool.BLANK, WorkflowDefinitionConstants.SCOPE_ALL, 0,
				ServiceContextTestUtil.getServiceContext());

		kaleoDefinition.setActive(active);

		return _kaleoDefinitionLocalService.updateKaleoDefinition(
			kaleoDefinition);
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.workflow.kaleo.internal.upgrade.v4_4_0." +
			"KaleoDefinitionUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.internal.upgrade.registry.KaleoServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

}