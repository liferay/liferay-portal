/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.message.listener;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.site.dsr.site.initializer.constants.DSRFolderConstants;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;

import java.io.Serializable;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = {
		"destination.name=" + DestinationNames.BACKGROUND_TASK_STATUS,
		"service.ranking:Integer=100"
	},
	service = MessageListener.class
)
public class BackgroundTaskStatusMessageListener implements MessageListener {

	@Override
	public void receive(Message message) throws MessageListenerException {
		long companyId = message.getLong("companyId");

		if ((companyId == CompanyConstants.SYSTEM) ||
			(message.getInteger("status") !=
				BackgroundTaskConstants.STATUS_SUCCESSFUL) ||
			!Objects.equals(
				message.getString("taskExecutorClassName"),
				"com.liferay.exportimport.internal.background.task." +
					"LayoutSetPrototypeImportBackgroundTaskExecutor")) {

			return;
		}

		BackgroundTask backgroundTask =
			_backgroundTaskManager.fetchBackgroundTask(
				message.getLong("backgroundTaskId"));

		if (backgroundTask == null) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(
			backgroundTask.getGroupId());

		if ((group == null) ||
			Objects.equals(group.getClassName(), Group.class.getName())) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", companyId);

		if ((objectDefinition == null) ||
			!Objects.equals(
				objectDefinition.getClassName(), group.getClassName())) {

			return;
		}

		try {
			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				group.getClassPK());

			if ((objectEntry != null) &&
				!MapUtil.getBoolean(objectEntry.getValues(), "initialized")) {

				_objectEntryLocalService.partialUpdateObjectEntry(
					objectEntry.getUserId(), objectEntry.getObjectEntryId(),
					objectEntry.getObjectEntryFolderId(),
					HashMapBuilder.<String, Serializable>put(
						"initialized", true
					).build(),
					new ServiceContext());
			}

			_setDLFolderResourcePermissions(companyId, group);

			if (group.getDefaultPublicPlid() == 0) {
				return;
			}

			Role role = _roleLocalService.fetchRoleByExternalReferenceCode(
				DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_SELLER, companyId);

			if (role == null) {
				return;
			}

			_resourcePermissionLocalService.removeResourcePermission(
				companyId, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getDefaultPublicPlid()), role.getRoleId(),
				ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			throw new MessageListenerException(portalException);
		}
	}

	private void _setDLFolderResourcePermissions(long companyId, Group group)
		throws PortalException {

		DLFolder dlFolder =
			_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				group.getGroupId());

		if (dlFolder == null) {
			return;
		}

		_setResourcePermissions(
			companyId, String.valueOf(dlFolder.getFolderId()),
			_roleLocalService.fetchRoleByExternalReferenceCode(
				DSRRoleConstants.
					EXTERNAL_REFERENCE_CODE_DSR_CONTENT_CONTRIBUTOR,
				companyId),
			new String[] {
				ActionKeys.ADD_DOCUMENT, ActionKeys.SUBSCRIBE, ActionKeys.VIEW
			});
		_setResourcePermissions(
			companyId, String.valueOf(dlFolder.getFolderId()),
			_roleLocalService.fetchRoleByExternalReferenceCode(
				DSRRoleConstants.EXTERNAL_REFERENCE_CODE_DSR_ROOM_COLLABORATOR,
				companyId),
			new String[] {
				ActionKeys.ADD_DOCUMENT, ActionKeys.SUBSCRIBE, ActionKeys.VIEW
			});
		_setResourcePermissions(
			companyId, String.valueOf(dlFolder.getFolderId()),
			_roleLocalService.fetchRole(companyId, RoleConstants.SITE_MEMBER),
			new String[] {ActionKeys.SUBSCRIBE, ActionKeys.VIEW});
	}

	private void _setResourcePermissions(
			long companyId, String primKey, Role role, String[] actionIds)
		throws PortalException {

		if (role == null) {
			return;
		}

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, primKey, role.getRoleId(),
			actionIds);
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}