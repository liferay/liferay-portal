/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection.action;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=permission.object",
	service = BulkSelectionAction.class
)
public class PermissionObjectBulkSelectionAction
	implements BulkSelectionAction<Object> {

	@Override
	public void execute(
			User user, BulkSelection<Object> bulkSelection,
			Map<String, Serializable> inputMap)
		throws Exception {

		ObjectEntry bulkActionTask = _objectEntryLocalService.getObjectEntry(
			GetterUtil.getLong(inputMap.get("bulkActionTaskId")));

		long companyId = GetterUtil.getLong(inputMap.get("companyId"));

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
						long groupId = 0L;
						String resourceName = null;
						long resourceId = 0L;
						String className = null;

						if (object instanceof DepotEntry) {
							DepotEntry depotEntry = (DepotEntry)object;

							groupId = depotEntry.getGroupId();

							resourceName = DepotEntry.class.getName();
							resourceId = depotEntry.getDepotEntryId();
							className = DepotEntry.class.getName();
						}
						else if (object instanceof ObjectEntry) {
							ObjectEntry objectEntry = (ObjectEntry)object;

							groupId = objectEntry.getGroupId();

							resourceName = objectEntry.getModelClassName();
							resourceId = objectEntry.getObjectEntryId();
							className = objectEntry.getModelClassName();
						}
						else {
							ObjectEntryFolder objectEntryFolder =
								(ObjectEntryFolder)object;

							groupId = objectEntryFolder.getGroupId();

							resourceName = ObjectEntryFolder.class.getName();
							resourceId =
								objectEntryFolder.getObjectEntryFolderId();
							className = ObjectEntryFolder.class.getName();
						}

						_permissionService.checkPermission(
							groupId, resourceName, resourceId);

						ModelPermissions modelPermissions =
							ModelPermissionsUtil.toModelPermissions(
								companyId,
								_getPermissions(
									(Map<String, Serializable>)inputMap.get(
										"permissions"),
									className),
								resourceId, resourceName,
								_resourceActionLocalService,
								_resourcePermissionLocalService,
								_roleLocalService);

						Collection<String> roleNames =
							modelPermissions.getRoleNames();

						for (ResourcePermission resourcePermission :
								_resourcePermissionLocalService.
									getResourcePermissions(
										companyId, resourceName,
										ResourceConstants.SCOPE_INDIVIDUAL,
										String.valueOf(resourceId))) {

							Role role = _roleLocalService.fetchRole(
								resourcePermission.getRoleId());

							if ((role == null) ||
								roleNames.contains(role.getName())) {

								continue;
							}

							for (ResourceAction resourceAction :
									_resourceActionLocalService.
										getResourceActions(resourceName)) {

								_resourcePermissionLocalService.
									removeResourcePermission(
										companyId, resourceName,
										ResourceConstants.SCOPE_INDIVIDUAL,
										String.valueOf(resourceId),
										role.getRoleId(),
										resourceAction.getActionId());
							}
						}

						_resourcePermissionLocalService.
							updateResourcePermissions(
								companyId, groupId, resourceName,
								String.valueOf(resourceId), modelPermissions);

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

	private Permission[] _getPermissions(
		Map<String, Serializable> map, String key) {

		try {
			return (Permission[])map.getOrDefault(key, new Permission[0]);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return new Permission[0];
	}

	private ObjectEntry _partialUpdateObjectEntry(
			ObjectEntry objectEntry, Map<String, Serializable> values)
		throws PortalException {

		return _objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), values, new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PermissionObjectBulkSelectionAction.class);

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}