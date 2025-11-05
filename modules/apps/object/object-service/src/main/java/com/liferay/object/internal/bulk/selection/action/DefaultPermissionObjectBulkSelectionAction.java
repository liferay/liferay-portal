/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection.action;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=default.permission.object",
	service = BulkSelectionAction.class
)
public class DefaultPermissionObjectBulkSelectionAction
	implements BulkSelectionAction<Object> {

	@Override
	public void execute(
			User user, BulkSelection<Object> bulkSelection,
			Map<String, Serializable> inputMap)
		throws Exception {

		ObjectEntry bulkActionTask = _objectEntryLocalService.getObjectEntry(
			GetterUtil.getLong(inputMap.get("bulkActionTaskId")));

		Map<String, Serializable> bulkActionTaskValues =
			bulkActionTask.getValues();

		bulkActionTaskValues.put("numberOfItems", bulkSelection.getSize());

		String status = "completed";

		AtomicInteger numberOfSuccessfulItems = new AtomicInteger(0);

		AtomicInteger numberOfFailedItems = new AtomicInteger(0);

		try {
			bulkActionTaskValues.put("executionStatus", "started");

			bulkActionTask = _partialUpdateObjectEntry(
				bulkActionTask, bulkActionTaskValues);

			bulkActionTaskValues = bulkActionTask.getValues();

			bulkSelection.forEach(
				object -> {
					try {
						ObjectEntry objectEntry = (ObjectEntry)object;

						Map<String, Serializable> values =
							objectEntry.getValues();

						values.put(
							"defaultPermissions",
							MapUtil.getString(inputMap, "defaultPermissions"));

						_partialUpdateObjectEntry(objectEntry, values);

						numberOfSuccessfulItems.getAndIncrement();
					}
					catch (PortalException portalException) {
						if (_log.isWarnEnabled()) {
							_log.warn(portalException);
						}

						numberOfFailedItems.getAndIncrement();
					}
				});
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			status = "failed";
		}
		finally {
			bulkActionTaskValues.put("completionDate", new Date());
			bulkActionTaskValues.put("executionStatus", status);
			bulkActionTaskValues.put(
				"numberOfFailedItems", numberOfFailedItems.get());
			bulkActionTaskValues.put(
				"numberOfSuccessfulItems", numberOfSuccessfulItems.get());

			_partialUpdateObjectEntry(bulkActionTask, bulkActionTaskValues);
		}
	}

	private ObjectEntry _partialUpdateObjectEntry(
			ObjectEntry objectEntry, Map<String, Serializable> values)
		throws PortalException {

		return _objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), values, new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultPermissionObjectBulkSelectionAction.class);

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}