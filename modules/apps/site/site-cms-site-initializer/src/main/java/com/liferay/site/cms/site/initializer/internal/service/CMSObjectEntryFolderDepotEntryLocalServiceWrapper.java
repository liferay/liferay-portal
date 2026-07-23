/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceWrapper;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.cms.site.initializer.internal.util.ObjectEntryFolderUtil;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 * @author Roberto Díaz
 */
@Component(service = ServiceWrapper.class)
public class CMSObjectEntryFolderDepotEntryLocalServiceWrapper
	extends DepotEntryLocalServiceWrapper {

	@Override
	public DepotEntry addDepotEntry(Group group, ServiceContext serviceContext)
		throws PortalException {

		DepotEntry depotEntry = super.addDepotEntry(group, serviceContext);

		if (depotEntry.getType() == DepotConstants.TYPE_SPACE) {
			_addCMSDefaultPermissions(group);

			ObjectEntryFolderUtil.addObjectEntryFolders(
				depotEntry, _attachmentManager);
		}

		return depotEntry;
	}

	@Override
	public DepotEntry addDepotEntry(
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int type, ServiceContext serviceContext)
		throws PortalException {

		DepotEntry depotEntry = super.addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);

		if (depotEntry.getType() == DepotConstants.TYPE_SPACE) {
			_addCMSDefaultPermissions(depotEntry.getGroup());

			ObjectEntryFolderUtil.addObjectEntryFolders(
				depotEntry, _attachmentManager);
		}

		return depotEntry;
	}

	@Override
	public DepotEntry deleteDepotEntry(DepotEntry depotEntry)
		throws PortalException {

		if (depotEntry.getType() == DepotConstants.TYPE_SPACE) {
			ObjectEntryFolderUtil.deleteObjectEntryFolders(depotEntry);

			_deleteCMSDefaultPermissions(depotEntry.getGroup());
		}

		return super.deleteDepotEntry(depotEntry);
	}

	@Override
	public DepotEntry deleteDepotEntry(long depotEntryId)
		throws PortalException {

		DepotEntry depotEntry = getDepotEntry(depotEntryId);

		if (depotEntry.getType() == DepotConstants.TYPE_SPACE) {
			ObjectEntryFolderUtil.deleteObjectEntryFolders(depotEntry);

			_deleteCMSDefaultPermissions(depotEntry.getGroup());
		}

		return super.deleteDepotEntry(depotEntryId);
	}

	private void _addCMSDefaultPermissions(Group group) throws PortalException {
		ObjectDefinition cmsDefaultPermissionObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", group.getCompanyId());

		if (cmsDefaultPermissionObjectDefinition == null) {
			return;
		}

		String[] actionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				ObjectEntryFolder.class.getName()),
			ResourceAction::getActionId, String.class);

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			null, group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			JSONUtil.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				_getObjectEntryDefaultPermissionJSONObject(
					group.getCompanyId(), "L_CMS_BASIC_WEB_CONTENT")
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				_getObjectEntryDefaultPermissionJSONObject(
					group.getCompanyId(), "L_CMS_BASIC_DOCUMENT")
			).put(
				"OBJECT_ENTRY_FOLDERS",
				JSONUtil.put(
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
					new String[] {
						ActionKeys.ADD_ENTRY,
						ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
						ActionKeys.DELETE, ActionKeys.PERMISSIONS,
						ActionKeys.UPDATE, ActionKeys.SUBSCRIBE, ActionKeys.VIEW
					}
				).put(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
					new String[] {
						ActionKeys.ADD_ENTRY,
						ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
						ActionKeys.DELETE, ActionKeys.PERMISSIONS,
						ActionKeys.UPDATE, ActionKeys.SUBSCRIBE, ActionKeys.VIEW
					}
				).put(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER,
					new String[] {
						ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW,
						ActionKeys.SUBSCRIBE
					}
				).put(
					RoleConstants.CMS_ADMINISTRATOR, JSONUtil.putAll(actionIds)
				).put(
					RoleConstants.OWNER, JSONUtil.putAll(actionIds)
				).put(
					RoleConstants.USER,
					new String[] {ActionKeys.VIEW, ActionKeys.SUBSCRIBE}
				)
			),
			group.getGroupId(), StringPool.BLANK);
	}

	private void _deleteCMSDefaultPermissions(Group group)
		throws PortalException {

		ObjectDefinition cmsDefaultPermissionObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", group.getCompanyId());

		if (cmsDefaultPermissionObjectDefinition == null) {
			return;
		}

		ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
			group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			_filterFactory);

		if (objectEntry == null) {
			return;
		}

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	private JSONObject _getObjectEntryDefaultPermissionJSONObject(
			long companyId, String externalReferenceCode)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		String[] assetLibraryMemberObjectEntryActionIds = {
			ActionKeys.ADD_DISCUSSION, ActionKeys.DOWNLOAD, ActionKeys.VIEW
		};
		String[] objectDefinitionActionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				objectDefinition.getClassName()),
			ResourceAction::getActionId, String.class);
		String[] objectEntryActionIds = {
			ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.DOWNLOAD,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW
		};

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFieldsByBusinessType(
				objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);

		if (ListUtil.isNotEmpty(objectFields)) {
			ObjectField objectField = objectFields.get(0);

			assetLibraryMemberObjectEntryActionIds = ArrayUtil.append(
				assetLibraryMemberObjectEntryActionIds,
				objectField.getAttachmentDownloadActionKey());
			objectEntryActionIds = ArrayUtil.append(
				objectEntryActionIds,
				objectField.getAttachmentDownloadActionKey());
		}

		return JSONUtil.put(
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
			objectEntryActionIds
		).put(
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
			objectEntryActionIds
		).put(
			DepotRolesConstants.ASSET_LIBRARY_MEMBER,
			assetLibraryMemberObjectEntryActionIds
		).put(
			RoleConstants.CMS_ADMINISTRATOR, objectDefinitionActionIds
		).put(
			RoleConstants.OWNER, objectDefinitionActionIds
		).put(
			RoleConstants.USER, new String[] {ActionKeys.VIEW}
		);
	}

	@Reference
	private AttachmentManager _attachmentManager;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

}