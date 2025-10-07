/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_3_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
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
 * @author Mateus Xavier
 */
@RunWith(Arquillian.class)
public class KaleoDefinitionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		KaleoDefinition kaleoDefinition1 =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				WorkflowDefinitionConstants.
					NAME_MESSAGE_BOARDS_USER_STATS_MODERATION,
				ServiceContextTestUtil.getServiceContext());

		kaleoDefinition1.setExternalReferenceCode(
			RandomTestUtil.randomString());
		kaleoDefinition1.setName("message-boards-user-stats-moderation");

		kaleoDefinition1 = _kaleoDefinitionLocalService.updateKaleoDefinition(
			kaleoDefinition1);

		KaleoDefinition kaleoDefinition2 =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				WorkflowDefinitionConstants.NAME_SINGLE_APPROVER,
				ServiceContextTestUtil.getServiceContext());

		kaleoDefinition2.setExternalReferenceCode(
			RandomTestUtil.randomString());

		kaleoDefinition2 = _kaleoDefinitionLocalService.updateKaleoDefinition(
			kaleoDefinition2);

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
			WorkflowDefinitionConstants.
				EXTERNAL_REFERENCE_CODE_MESSAGE_BOARDS_USER_STATS_MODERATION,
			kaleoDefinition1.getExternalReferenceCode());
		Assert.assertEquals(
			WorkflowDefinitionConstants.
				NAME_MESSAGE_BOARDS_USER_STATS_MODERATION,
			kaleoDefinition1.getName());

		kaleoDefinition2 = _kaleoDefinitionLocalService.fetchKaleoDefinition(
			kaleoDefinition2.getKaleoDefinitionId());

		Assert.assertEquals(
			WorkflowDefinitionConstants.EXTERNAL_REFERENCE_CODE_SINGLE_APPROVER,
			kaleoDefinition2.getExternalReferenceCode());
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.workflow.kaleo.internal.upgrade.v4_3_1." +
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