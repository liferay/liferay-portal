/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.selection.internal;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.bulk.selection.internal.constants.BulkSelectionBackgroundTaskConstants;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = BulkSelectionRunner.class)
public class BulkSelectionSelectionRunnerImpl implements BulkSelectionRunner {

	@Override
	public boolean isBusy(User user) {
		long userId = user.getUserId();

		for (BackgroundTask backgroundTask :
				_backgroundTaskLocalService.getBackgroundTasks(
					BulkSelectionBackgroundTaskExecutor.class.getName(),
					BackgroundTaskConstants.STATUS_IN_PROGRESS)) {

			if (backgroundTask.getUserId() == userId) {
				return true;
			}
		}

		return false;
	}

	@Override
	public <T> void run(
			User user, BulkSelection<T> bulkSelection,
			BulkSelectionAction<T> bulkSelectionAction,
			Map<String, Serializable> inputMap)
		throws PortalException {

		Class<? extends BulkSelectionAction> bulkSelectionActionClass =
			bulkSelectionAction.getClass();

		_backgroundTaskLocalService.addBackgroundTask(
			user.getUserId(), BackgroundTaskConstants.GROUP_ID_DEFAULT,
			bulkSelectionActionClass.getName(),
			BulkSelectionBackgroundTaskExecutor.class.getName(),
			HashMapBuilder.<String, Serializable>put(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
			).put(
				BulkSelectionBackgroundTaskConstants.
					BULK_SELECTION_ACTION_CLASS_NAME,
				bulkSelectionActionClass.getName()
			).put(
				BulkSelectionBackgroundTaskConstants.
					BULK_SELECTION_ACTION_INPUT_MAP,
				new HashMap<>(inputMap)
			).put(
				BulkSelectionBackgroundTaskConstants.
					BULK_SELECTION_FACTORY_CLASS_NAME,
				() -> {
					Class<? extends BulkSelectionFactory>
						bulkSelectionFactoryClass =
							bulkSelection.getBulkSelectionFactoryClass();

					return bulkSelectionFactoryClass.getName();
				}
			).put(
				BulkSelectionBackgroundTaskConstants.
					BULK_SELECTION_PARAMETER_MAP,
				new HashMap<>(bulkSelection.getParameterMap())
			).build(),
			new ServiceContext());
	}

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

}