/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.exportimport.internal.util.RemoteImportHttpUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.exportimport.internal.background.task.RemoteImportBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class RemoteImportBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public RemoteImportBackgroundTaskExecutor() {
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		BackgroundTask exportBackgroundTask =
			_backgroundTaskManager.getBackgroundTask(
				MapUtil.getLong(taskContextMap, "exportProcessId"));

		ObjectEntry remoteInstanceObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				MapUtil.getLong(taskContextMap, "remoteInstanceId"));

		Map<String, Serializable> remoteInstanceValues =
			remoteInstanceObjectEntry.getValues();

		JSONObject importPreviewJSONObject =
			RemoteImportHttpUtil.postImportPreview(
				backgroundTask.getCompanyId(),
				_getExportFileEntry(exportBackgroundTask),
				remoteInstanceValues);

		JSONObject importProcessJSONObject =
			RemoteImportHttpUtil.postImportProcess(
				backgroundTask.getCompanyId(), importPreviewJSONObject,
				exportBackgroundTask.getName(), remoteInstanceValues);

		_updateRemoteImportProcess(
			backgroundTask,
			HashMapBuilder.<String, Serializable>put(
				"importStatus", BackgroundTaskConstants.STATUS_IN_PROGRESS
			).put(
				"remoteImportProcessId", importProcessJSONObject.getLong("id")
			).put(
				"remoteImportURL",
				RemoteImportHttpUtil.getImportURL(remoteInstanceValues)
			).build());

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public String generateLockKey(BackgroundTask backgroundTask) {
		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		return StringBundler.concat(
			RemoteImportBackgroundTaskExecutor.class.getName(),
			StringPool.POUND, taskContextMap.get("remoteInstanceId"));
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	@Override
	public String handleException(
		BackgroundTask backgroundTask, Exception exception1) {

		try {
			_updateRemoteImportProcess(
				backgroundTask,
				HashMapBuilder.<String, Serializable>put(
					"errorMessage", exception1.getMessage()
				).put(
					"importStatus", BackgroundTaskConstants.STATUS_FAILED
				).build());
		}
		catch (Exception exception2) {
			_log.error(
				"Unable to update the remote import process of background " +
					"task " + backgroundTask.getBackgroundTaskId(),
				exception2);
		}

		return exception1.getMessage();
	}

	private FileEntry _getExportFileEntry(BackgroundTask exportBackgroundTask)
		throws Exception {

		List<FileEntry> fileEntries =
			exportBackgroundTask.getAttachmentsFileEntries();

		if (fileEntries.isEmpty()) {
			throw new PortalException(
				"Export process " + exportBackgroundTask.getBackgroundTaskId() +
					" has no file");
		}

		return fileEntries.get(0);
	}

	private void _updateRemoteImportProcess(
			BackgroundTask backgroundTask, Map<String, Serializable> values)
		throws Exception {

		_objectEntryLocalService.partialUpdateObjectEntry(
			backgroundTask.getUserId(),
			MapUtil.getLong(
				backgroundTask.getTaskContextMap(), "objectEntryId"),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			values, new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RemoteImportBackgroundTaskExecutor.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}