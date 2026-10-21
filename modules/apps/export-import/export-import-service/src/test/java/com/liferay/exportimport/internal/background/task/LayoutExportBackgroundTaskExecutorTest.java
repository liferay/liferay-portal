/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.io.File;
import java.io.Serializable;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Magdalena Jedraszak
 */
public class LayoutExportBackgroundTaskExecutorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_timestamp = String.valueOf(System.currentTimeMillis());

		ReflectionTestUtil.setFieldValue(
			_layoutExportBackgroundTaskExecutor, "_backgroundTaskManager",
			_backgroundTaskManager);
		ReflectionTestUtil.setFieldValue(
			_layoutExportBackgroundTaskExecutor, "_exportImportLocalService",
			_exportImportLocalService);

		_timeMockedStatic.when(
			Time::getTimestamp
		).thenReturn(
			_timestamp
		);
	}

	@After
	public void tearDown() {
		_backgroundTaskStatusRegistryUtilMockedStatic.close();
		_timeMockedStatic.close();
	}

	@Test
	public void test() throws Exception {
		ExportImportConfiguration exportImportConfiguration = Mockito.mock(
			ExportImportConfiguration.class);

		LayoutExportBackgroundTaskExecutor layoutExportBackgroundTaskExecutor =
			_spy(exportImportConfiguration);

		long userId = RandomTestUtil.randomLong();

		Map<String, Serializable> settingsMap =
			HashMapBuilder.<String, Serializable>put(
				"userId", userId
			).build();

		Mockito.when(
			exportImportConfiguration.getSettingsMap()
		).thenReturn(
			settingsMap
		);

		String name = RandomTestUtil.randomString();

		Mockito.when(
			exportImportConfiguration.getName()
		).thenReturn(
			name
		);

		BackgroundTask backgroundTask = Mockito.mock(BackgroundTask.class);

		long backgroundTaskId = RandomTestUtil.randomLong();

		Mockito.when(
			backgroundTask.getBackgroundTaskId()
		).thenReturn(
			backgroundTaskId
		);

		File file = new File(
			StringBundler.concat(
				RandomTestUtil.randomString(), StringPool.PERIOD, _PROTOCOL));

		Mockito.when(
			_exportImportLocalService.exportLayoutsAsFile(
				exportImportConfiguration)
		).thenReturn(
			file
		);

		Assert.assertEquals(
			BackgroundTaskResult.SUCCESS,
			layoutExportBackgroundTaskExecutor.execute(backgroundTask));

		ArgumentCaptor<String> sourceFileNameArgumentCaptor =
			ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			_backgroundTaskManager
		).addBackgroundTaskAttachment(
			Mockito.eq(userId), Mockito.eq(backgroundTaskId),
			sourceFileNameArgumentCaptor.capture(),
			titleArgumentCaptor.capture(), Mockito.eq(file)
		);

		Assert.assertEquals(
			StringBundler.concat(
				name, StringPool.DASH, _timestamp, StringPool.PERIOD,
				_PROTOCOL),
			sourceFileNameArgumentCaptor.getValue());
		Assert.assertEquals(
			StringBundler.concat(name, StringPool.PERIOD, _PROTOCOL),
			titleArgumentCaptor.getValue());
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

		LayoutExportBackgroundTaskExecutor layoutExportBackgroundTaskExecutor =
			_spy(exportImportConfiguration);

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

			layoutExportBackgroundTaskExecutor.execute(backgroundTask);
		}

		Assert.assertEquals(
			3, backgroundTaskStatus.getAttribute("phasesTotal"));
	}

	private LayoutExportBackgroundTaskExecutor _spy(
		ExportImportConfiguration exportImportConfiguration) {

		LayoutExportBackgroundTaskExecutor layoutExportBackgroundTaskExecutor =
			Mockito.spy(_layoutExportBackgroundTaskExecutor);

		Mockito.doReturn(
			exportImportConfiguration
		).when(
			layoutExportBackgroundTaskExecutor
		).getExportImportConfiguration(
			Mockito.any(BackgroundTask.class)
		);

		return layoutExportBackgroundTaskExecutor;
	}

	private static final String _PROTOCOL = "lar";

	private final BackgroundTaskManager _backgroundTaskManager = Mockito.mock(
		BackgroundTaskManager.class);
	private final MockedStatic<BackgroundTaskStatusRegistryUtil>
		_backgroundTaskStatusRegistryUtilMockedStatic = Mockito.mockStatic(
			BackgroundTaskStatusRegistryUtil.class);
	private final ExportImportLocalService _exportImportLocalService =
		Mockito.mock(ExportImportLocalService.class);
	private final LayoutExportBackgroundTaskExecutor
		_layoutExportBackgroundTaskExecutor =
			new LayoutExportBackgroundTaskExecutor();
	private final MockedStatic<Time> _timeMockedStatic = Mockito.mockStatic(
		Time.class);
	private String _timestamp;

}