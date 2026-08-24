/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.exportimport.internal.background.task.display.LayoutStagingBackgroundTaskDisplay;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManagerUtil;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.trash.service.TrashEntryLocalService;

import java.io.File;
import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.exportimport.internal.background.task.LayoutStagingBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class LayoutStagingBackgroundTaskExecutor
	extends BaseStagingBackgroundTaskExecutor {

	public LayoutStagingBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new LayoutStagingBackgroundTaskStatusMessageTranslator());
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			getExportImportConfiguration(backgroundTask);

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		long userId = MapUtil.getLong(settingsMap, "userId");
		long targetGroupId = MapUtil.getLong(settingsMap, "targetGroupId");
		long sourceGroupId = MapUtil.getLong(settingsMap, "sourceGroupId");

		clearBackgroundTaskStatus(backgroundTask);

		File file = null;
		MissingReferences missingReferences = null;

		try {
			ExportImportThreadLocal.setLayoutStagingInProcess(true);

			Group targetGroup = _groupLocalService.fetchGroup(targetGroupId);

			if (targetGroup == null) {
				throw new NoSuchGroupException(
					"Target group does not exist with the primary key " +
						targetGroupId);
			}

			Group sourceGroup = _groupLocalService.getGroup(sourceGroupId);

			if (sourceGroup.hasStagingGroup()) {
				Group stagingGroup = sourceGroup.getStagingGroup();

				if (stagingGroup.getGroupId() == targetGroupId) {
					_dLAppHelperLocalService.cancelCheckOuts(sourceGroupId);

					ExportImportThreadLocal.setInitialLayoutStagingInProcess(
						true);

					_trashEntryLocalService.deleteEntries(sourceGroupId, true);
				}
			}

			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.
					EVENT_PUBLICATION_LAYOUT_LOCAL_STARTED,
				ExportImportLifecycleConstants.
					PROCESS_FLAG_LAYOUT_STAGING_IN_PROCESS,
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				exportImportConfiguration);

			boolean privateLayout = MapUtil.getBoolean(
				settingsMap, "privateLayout");

			initThreadLocals(sourceGroupId, privateLayout);

			file = _exportImportLocalService.exportLayoutsAsFile(
				exportImportConfiguration);

			markBackgroundTask(
				backgroundTask.getBackgroundTaskId(), "exported");

			missingReferences = TransactionInvokerUtil.invoke(
				transactionConfig,
				new LayoutStagingImportCallable(
					backgroundTask.getBackgroundTaskId(),
					exportImportConfiguration, file, sourceGroupId,
					targetGroupId, userId));

			deleteExportedChangesetEntries();

			ExportImportThreadLocal.setInitialLayoutStagingInProcess(false);
			ExportImportThreadLocal.setLayoutStagingInProcess(false);

			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.
					EVENT_PUBLICATION_LAYOUT_LOCAL_SUCCEEDED,
				ExportImportLifecycleConstants.
					PROCESS_FLAG_LAYOUT_STAGING_IN_PROCESS,
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				exportImportConfiguration);

			_exportImportHelper.processBackgroundTaskManifestSummary(
				userId, sourceGroupId, backgroundTask, file);
		}
		catch (Throwable throwable) {
			ExportImportThreadLocal.setInitialLayoutStagingInProcess(false);
			ExportImportThreadLocal.setLayoutStagingInProcess(false);

			ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
				ExportImportLifecycleConstants.
					EVENT_PUBLICATION_LAYOUT_LOCAL_FAILED,
				ExportImportLifecycleConstants.
					PROCESS_FLAG_LAYOUT_STAGING_IN_PROCESS,
				String.valueOf(
					exportImportConfiguration.getExportImportConfigurationId()),
				exportImportConfiguration, throwable);

			Group sourceGroup = _groupLocalService.getGroup(sourceGroupId);

			if (sourceGroup.hasStagingGroup()) {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setUserId(userId);

				_stagingLocalService.disableStaging(
					sourceGroup, serviceContext);

				List<BackgroundTask> queuedBackgroundTasks =
					_backgroundTaskManager.getBackgroundTasks(
						sourceGroupId,
						LayoutStagingBackgroundTaskExecutor.class.getName(),
						BackgroundTaskConstants.STATUS_QUEUED);

				for (BackgroundTask queuedBackgroundTask :
						queuedBackgroundTasks) {

					_backgroundTaskManager.amendBackgroundTask(
						queuedBackgroundTask.getBackgroundTaskId(), null,
						BackgroundTaskConstants.STATUS_CANCELLED,
						new ServiceContext());
				}
			}

			deleteTempLarOnFailure(file);

			throw new SystemException(throwable);
		}

		deleteTempLarOnSuccess(file);

		return getBackgroundTaskResult(
			backgroundTask.getBackgroundTaskId(), exportImportConfiguration,
			missingReferences);
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return new LayoutStagingBackgroundTaskDisplay(backgroundTask);
	}

	private void _initLayoutSetBranches(
			long userId, long sourceGroupId, long targetGroupId)
		throws PortalException {

		Group sourceGroup = _groupLocalService.getGroup(sourceGroupId);

		if (!sourceGroup.hasStagingGroup()) {
			return;
		}

		_layoutSetBranchLocalService.deleteLayoutSetBranches(
			targetGroupId, false, true);
		_layoutSetBranchLocalService.deleteLayoutSetBranches(
			targetGroupId, true, true);

		UnicodeProperties typeSettingsUnicodeProperties =
			sourceGroup.getTypeSettingsProperties();

		boolean branchingPrivate = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("branchingPrivate"));
		boolean branchingPublic = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("branchingPublic"));

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(userId);

		_stagingLocalService.checkDefaultLayoutSetBranches(
			userId, sourceGroup, branchingPublic, branchingPrivate, false,
			serviceContext);
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private DLAppHelperLocalService _dLAppHelperLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Reference
	private StagingLocalService _stagingLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	private class LayoutStagingImportCallable
		implements Callable<MissingReferences> {

		public LayoutStagingImportCallable(
			long backgroundTaskId,
			ExportImportConfiguration exportImportConfiguration, File file,
			long sourceGroupId, long targetGroupId, long userId) {

			_backgroundTaskId = backgroundTaskId;
			_exportImportConfiguration = exportImportConfiguration;
			_file = file;
			_sourceGroupId = sourceGroupId;
			_targetGroupId = targetGroupId;
			_userId = userId;
		}

		@Override
		public MissingReferences call() throws PortalException {
			_exportImportLocalService.importLayoutsDataDeletions(
				_exportImportConfiguration, _file);

			MissingReferences missingReferences =
				_exportImportLocalService.validateImportLayoutsFile(
					_exportImportConfiguration, _file);

			markBackgroundTask(_backgroundTaskId, "validated");

			_exportImportLocalService.importLayouts(
				_exportImportConfiguration, _file);

			_initLayoutSetBranches(_userId, _sourceGroupId, _targetGroupId);

			return missingReferences;
		}

		private final long _backgroundTaskId;
		private final ExportImportConfiguration _exportImportConfiguration;
		private final File _file;
		private final long _sourceGroupId;
		private final long _targetGroupId;
		private final long _userId;

	}

}