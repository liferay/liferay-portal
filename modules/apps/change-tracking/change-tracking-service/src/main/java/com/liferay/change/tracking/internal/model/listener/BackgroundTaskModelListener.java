/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.model.listener;

import com.liferay.change.tracking.internal.background.task.CTPublishBackgroundTaskExecutor;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = ModelListener.class)
public class BackgroundTaskModelListener
	extends BaseModelListener<BackgroundTask> {

	@Override
	public void onAfterUpdate(
		BackgroundTask originalBackgroundTask, BackgroundTask backgroundTask) {

		if ((backgroundTask == null) ||
			!Objects.equals(
				backgroundTask.getTaskExecutorClassName(),
				CTPublishBackgroundTaskExecutor.class.getName())) {

			return;
		}

		try {
			if ((backgroundTask.getStatus() ==
					BackgroundTaskConstants.STATUS_CANCELLED) ||
				(backgroundTask.getStatus() ==
					BackgroundTaskConstants.STATUS_FAILED)) {

				CTCollection ctCollection =
					_ctCollectionLocalService.fetchCTCollection(
						Long.valueOf(backgroundTask.getName()));

				if (ctCollection != null) {
					if (ctCollection.getStatus() ==
							WorkflowConstants.STATUS_APPROVED) {

						backgroundTask.setStatus(
							BackgroundTaskConstants.STATUS_SUCCESSFUL);

						backgroundTask =
							_backgroundTaskLocalService.updateBackgroundTask(
								backgroundTask);

						return;
					}

					int status = WorkflowConstants.STATUS_DRAFT;

					if (!_ctSchemaVersionLocalService.isLatestCTSchemaVersion(
							ctCollection.getSchemaVersionId())) {

						status = WorkflowConstants.STATUS_EXPIRED;
					}
					else if (_ctCollectionLocalService.hasUnapprovedChanges(
								ctCollection.getCtCollectionId())) {

						status = WorkflowConstants.STATUS_INCOMPLETE;
					}

					ctCollection.setStatus(status);

					_ctCollectionLocalService.updateCTCollection(ctCollection);
				}
			}

			if (backgroundTask.getStatus() !=
					originalBackgroundTask.getStatus()) {

				_reindexCTProcess(backgroundTask);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _reindexCTProcess(BackgroundTask backgroundTask) {
		long ctProcessId = MapUtil.getLong(
			backgroundTask.getTaskContextMap(), "ctProcessId");

		if (ctProcessId <= 0) {
			return;
		}

		try {
			Indexer<CTProcess> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				CTProcess.class);

			indexer.reindex(CTProcess.class.getName(), ctProcessId);
		}
		catch (SearchException searchException) {
			throw new ModelListenerException(searchException);
		}
	}

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

}