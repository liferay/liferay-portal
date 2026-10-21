/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.io.Serializable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jaime Leon
 */
public class LayoutImportBackgroundTaskExecutorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_layoutImportBackgroundTaskExecutor,
			"_exportImportReportEntryLocalService",
			_exportImportReportEntryLocalService);
	}

	@After
	public void tearDown() {
		_backgroundTaskStatusRegistryUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-109895")
	public void testExecuteSetsPhasesTotal() throws Exception {
		BackgroundTask backgroundTask = Mockito.mock(BackgroundTask.class);

		long backgroundTaskId = RandomTestUtil.randomLong();

		Mockito.when(
			backgroundTask.getBackgroundTaskId()
		).thenReturn(
			backgroundTaskId
		);

		BackgroundTaskStatus backgroundTaskStatus = new BackgroundTaskStatus();

		_backgroundTaskStatusRegistryUtilMockedStatic.when(
			() -> BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(
				backgroundTaskId)
		).thenReturn(
			backgroundTaskStatus
		);

		ExportImportConfiguration exportImportConfiguration = Mockito.mock(
			ExportImportConfiguration.class);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			exportImportConfiguration.getCompanyId()
		).thenReturn(
			companyId
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			exportImportConfiguration.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			exportImportConfiguration.getSettingsMap()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"parameterMap",
				HashMapBuilder.put(
					PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
					new String[] {
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString()
					}
				).build()
			).build()
		);

		LayoutImportBackgroundTaskExecutor layoutImportBackgroundTaskExecutor =
			Mockito.spy(_layoutImportBackgroundTaskExecutor);

		Mockito.doReturn(
			exportImportConfiguration
		).when(
			layoutImportBackgroundTaskExecutor
		).getExportImportConfiguration(
			backgroundTask
		);

		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			stagingGroupHelper.isCompanyGroup(companyId, groupId)
		).thenReturn(
			true
		);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<StagingGroupHelperUtil>
				stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
					StagingGroupHelperUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(companyId, "LPD-85946")
			).thenReturn(
				true
			);

			stagingGroupHelperUtilMockedStatic.when(
				StagingGroupHelperUtil::getStagingGroupHelper
			).thenReturn(
				stagingGroupHelper
			);

			Assert.assertEquals(
				BackgroundTaskResult.SUCCESS,
				layoutImportBackgroundTaskExecutor.execute(backgroundTask));
		}

		Assert.assertEquals(
			3, backgroundTaskStatus.getAttribute("phasesTotal"));
	}

	private final MockedStatic<BackgroundTaskStatusRegistryUtil>
		_backgroundTaskStatusRegistryUtilMockedStatic = Mockito.mockStatic(
			BackgroundTaskStatusRegistryUtil.class);
	private final ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService = Mockito.mock(
			ExportImportReportEntryLocalService.class);
	private final LayoutImportBackgroundTaskExecutor
		_layoutImportBackgroundTaskExecutor =
			new LayoutImportBackgroundTaskExecutor();

}