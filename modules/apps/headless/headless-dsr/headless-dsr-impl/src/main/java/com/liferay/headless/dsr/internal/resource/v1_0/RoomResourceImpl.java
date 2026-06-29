/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.internal.resource.v1_0;

import com.liferay.headless.dsr.dto.v1_0.Room;
import com.liferay.headless.dsr.resource.v1_0.RoomResource;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.definition.security.permission.resource.ObjectDefinitionPortletResourcePermissionRegistryUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.site.dsr.site.initializer.thread.local.DSRRoomThreadLocal;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/room.properties",
	scope = ServiceScope.PROTOTYPE, service = RoomResource.class
)
public class RoomResourceImpl extends BaseRoomResourceImpl {

	@Override
	public Room postRoomDuplicate(Long roomId, Room room) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(roomId);

		_checkPermission(objectEntry);

		DSRRoomThreadLocal.setFileEntryIds(
			GetterUtil.getLongValues(room.getFileEntryIds()));
		DSRRoomThreadLocal.setObjectEntryId(objectEntry.getObjectEntryId());

		try {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(contextCompany.getCompanyId());
			serviceContext.setUserId(contextUser.getUserId());

			return _toRoom(
				_objectEntryLocalService.addObjectEntry(
					0, contextUser.getUserId(),
					objectEntry.getObjectDefinitionId(), 0, null,
					HashMapBuilder.<String, Serializable>put(
						"description",
						MapUtil.getString(
							objectEntry.getValues(), "description")
					).put(
						"name",
						GetterUtil.getString(
							room.getName(),
							MapUtil.getString(objectEntry.getValues(), "name"))
					).put(
						"r_accountToDSRRooms_accountEntryId",
						MapUtil.getLong(
							objectEntry.getValues(),
							"r_accountToDSRRooms_accountEntryId")
					).build(),
					serviceContext));
		}
		finally {
			DSRRoomThreadLocal.setFileEntryIds(new long[0]);
			DSRRoomThreadLocal.setObjectEntryId(0);
		}
	}

	private void _checkPermission(ObjectEntry objectEntry) throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return;
		}

		DSRRoomUtil.checkPermission(
			objectEntry, permissionChecker, ActionKeys.UPDATE);

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		PortletResourcePermission portletResourcePermission =
			ObjectDefinitionPortletResourcePermissionRegistryUtil.getService(
				objectDefinition.getResourceName());

		if ((objectEntry.getUserId() == permissionChecker.getUserId()) &&
			portletResourcePermission.contains(
				permissionChecker, objectEntry.getGroupId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY)) {

			return;
		}

		throw new PrincipalException.MustHavePermission(
			permissionChecker, objectDefinition.getResourceName(),
			objectEntry.getGroupId(), ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	private Room _toRoom(ObjectEntry objectEntry) throws Exception {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		Group group = _groupLocalService.fetchGroup(
			objectEntry.getCompanyId(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		return new Room() {
			{
				setFriendlyURL(group::getFriendlyURL);
				setGroupId(group::getGroupId);
				setId(objectEntry::getObjectEntryId);
				setName(
					() -> MapUtil.getString(objectEntry.getValues(), "name"));
			}
		};
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}