/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Magdalena Jedraszak
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypeImportBackgroundTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPS-166515")
	public void testCleanUpPreviousBackgroundTasks() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		File larFile = File.createTempFile("corrupt", ".lar");

		try (Writer writer = new FileWriter(larFile)) {
			writer.write(RandomTestUtil.randomString());
		}

		long backgroundTaskId = 0;

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.exportimport.internal.staging.StagingImpl",
				LoggerTestUtil.ERROR);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.background.task.internal.messaging." +
					"BackgroundTaskMessageListener",
				LoggerTestUtil.ERROR)) {

			backgroundTaskId =
				ExportImportLocalServiceUtil.
					importLayoutSetPrototypeInBackground(
						TestPropsValues.getUserId(),
						ExportImportConfigurationLocalServiceUtil.
							addExportImportConfiguration(
								TestPropsValues.getUserId(),
								_group.getGroupId(),
								RandomTestUtil.randomString(),
								RandomTestUtil.randomString(), 0, null,
								WorkflowConstants.STATUS_DRAFT,
								ServiceContextTestUtil.getServiceContext()),
						larFile);
		}

		Thread.sleep(2000);

		BackgroundTask failedBackgroundTask =
			BackgroundTaskManagerUtil.getBackgroundTask(backgroundTaskId);

		Assert.assertEquals(
			BackgroundTaskConstants.STATUS_FAILED,
			failedBackgroundTask.getStatus());

		List<BackgroundTask> backgroundTasks =
			BackgroundTaskManagerUtil.getBackgroundTasks(
				_group.getGroupId(),
				BackgroundTaskExecutorNames.
					LAYOUT_SET_PROTOTYPE_IMPORT_BACKGROUND_TASK_EXECUTOR);

		Assert.assertEquals(
			backgroundTasks.toString(), 1, backgroundTasks.size());

		ExportImportConfiguration exportExportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addExportImportConfiguration(
					TestPropsValues.getUserId(), _group.getGroupId(),
					StringPool.BLANK, StringPool.BLANK,
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							true, new long[0],
							LinkedHashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_CONFIGURATION,
								new String[] {Boolean.TRUE.toString()}
							).build()),
					WorkflowConstants.STATUS_DRAFT,
					ServiceContextTestUtil.getServiceContext());

		larFile = ExportImportServiceUtil.exportLayoutsAsFile(
			exportExportImportConfiguration);

		ExportImportConfiguration importExportExportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addExportImportConfiguration(
					TestPropsValues.getUserId(), _group.getGroupId(),
					StringPool.BLANK, StringPool.BLANK,
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							true, new long[0],
							LinkedHashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_CONFIGURATION,
								new String[] {Boolean.TRUE.toString()}
							).build()),
					WorkflowConstants.STATUS_DRAFT,
					ServiceContextTestUtil.getServiceContext());

		ExportImportLocalServiceUtil.importLayoutSetPrototypeInBackground(
			TestPropsValues.getUserId(), importExportExportImportConfiguration,
			larFile);

		Thread.sleep(2000);

		backgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(
			_group.getGroupId(),
			BackgroundTaskExecutorNames.
				LAYOUT_SET_PROTOTYPE_IMPORT_BACKGROUND_TASK_EXECUTOR);

		Assert.assertEquals(
			backgroundTasks.toString(), 0, backgroundTasks.size());
	}

	@Test
	@TestInfo("LPD-95511")
	public void testImportLayoutSetPrototypeWithLayout() throws Exception {
		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group group = layoutSetPrototype.getGroup();

		long layoutSetPrototypeId =
			layoutSetPrototype.getLayoutSetPrototypeId();

		boolean layoutImportInProcess =
			ExportImportThreadLocal.isLayoutImportInProcess();

		ExportImportThreadLocal.setLayoutImportInProcess(true);

		try {
			TransactionInvokerUtil.invoke(
				_requiredTransactionConfig,
				() -> {

					// Lock the layout set prototype row in the outer
					// transaction, as importing its staged model does

					LayoutSetPrototype lockedLayoutSetPrototype =
						LayoutSetPrototypeLocalServiceUtil.
							getLayoutSetPrototype(layoutSetPrototypeId);

					Date modifiedDate =
						lockedLayoutSetPrototype.getModifiedDate();

					long time = modifiedDate.getTime();

					lockedLayoutSetPrototype.setModifiedDate(
						new Date(time - Time.DAY));

					LayoutSetPrototypeLocalServiceUtil.updateLayoutSetPrototype(
						lockedLayoutSetPrototype);

					// Import a content page in a nested transaction, as the
					// batch engine does for each page

					try {
						TransactionInvokerUtil.invoke(
							_nestedTransactionConfig,
							() -> LayoutTestUtil.addTypeContentLayout(
								group, true, false));
					}
					catch (Throwable throwable) {
						throw new SystemException(throwable);
					}

					return null;
				});
		}
		catch (Throwable throwable) {
			throw new AssertionError(
				"Importing a content page into a layout set prototype " +
					"deadlocked",
				throwable);
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(
				layoutImportInProcess);

			LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
				layoutSetPrototypeId);
		}
	}

	private static final TransactionConfig _nestedTransactionConfig =
		TransactionConfig.Factory.create(
			Propagation.NESTED, new Class<?>[] {Exception.class});
	private static final TransactionConfig _requiredTransactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@DeleteAfterTestRun
	private Group _group;

}