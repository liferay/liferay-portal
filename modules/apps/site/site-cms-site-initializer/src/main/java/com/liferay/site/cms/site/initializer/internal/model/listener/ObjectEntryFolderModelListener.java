/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryFolderModelListener
	extends BaseModelListener<ObjectEntryFolder> {

	@Override
	public void onAfterCreate(ObjectEntryFolder objectEntryFolder)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectEntryFolder);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectEntryFolder objectEntryFolder)
		throws ModelListenerException {

		try {
			_onAfterRemove(objectEntryFolder);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private JSONObject _getCMSDefaultPermissionJSONObject(
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		ObjectDefinition cmsDefaultPermissionObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION",
					objectEntryFolder.getCompanyId());

		if (cmsDefaultPermissionObjectDefinition == null) {
			return null;
		}

		if (objectEntryFolder.getParentObjectEntryFolderId() != 0) {
			ObjectEntryFolder parentObjectEntryFolder =
				_objectEntryFolderLocalService.getObjectEntryFolder(
					objectEntryFolder.getParentObjectEntryFolderId());

			JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
				parentObjectEntryFolder.getCompanyId(),
				parentObjectEntryFolder.getUserId(),
				parentObjectEntryFolder.getExternalReferenceCode(),
				parentObjectEntryFolder.getModelClassName(), _filterFactory);

			if ((jsonObject != null) && !JSONUtil.isEmpty(jsonObject)) {
				return jsonObject;
			}
		}

		Group group = _groupLocalService.getGroup(
			objectEntryFolder.getGroupId());

		return CMSDefaultPermissionUtil.getJSONObject(
			group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			_filterFactory);
	}

	private Role _getOrAddCMSAdministratorRole(long companyId, long userId)
		throws Exception {

		String name = RoleConstants.CMS_ADMINISTRATOR;

		Role role = _roleLocalService.fetchRole(companyId, name);

		if (role != null) {
			return role;
		}

		return _roleLocalService.addRole(
			null, userId, null, 0, name, null, null, RoleConstants.TYPE_REGULAR,
			null, null);
	}

	private void _onAfterCreate(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntryFolder.getCompanyId(), "LPD-17564")) {

			return;
		}

		Role cmsAdministratorRole = _getOrAddCMSAdministratorRole(
			objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId());

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntryFolder.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
			cmsAdministratorRole.getRoleId(),
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					ObjectEntryFolder.class.getName()),
				ResourceAction::getActionId, String.class));

		if (objectEntryFolder.getParentObjectEntryFolderId() == 0) {
			return;
		}

		JSONObject defaultPermissionsJSONObject =
			_getCMSDefaultPermissionJSONObject(objectEntryFolder);

		if ((defaultPermissionsJSONObject == null) ||
			JSONUtil.isEmpty(defaultPermissionsJSONObject)) {

			return;
		}

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			null, objectEntryFolder.getCompanyId(),
			objectEntryFolder.getUserId(),
			objectEntryFolder.getExternalReferenceCode(),
			objectEntryFolder.getModelClassName(), defaultPermissionsJSONObject,
			objectEntryFolder.getGroupId(), objectEntryFolder.getTreePath());

		JSONObject objectEntryFoldersJSONObject =
			defaultPermissionsJSONObject.getJSONObject("OBJECT_ENTRY_FOLDERS");

		if (objectEntryFoldersJSONObject == null) {
			return;
		}

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			ObjectEntryFolder.class.getName());

		Iterator<String> iterator = objectEntryFoldersJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			JSONArray jsonArray = objectEntryFoldersJSONObject.getJSONArray(
				key);

			if ((jsonArray == null) || JSONUtil.isEmpty(jsonArray)) {
				continue;
			}

			Role role = _roleLocalService.fetchRole(
				objectEntryFolder.getCompanyId(), key);

			if (role == null) {
				continue;
			}

			_resourcePermissionLocalService.setResourcePermissions(
				objectEntryFolder.getCompanyId(),
				ObjectEntryFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
				role.getRoleId(),
				ArrayUtil.filter(
					JSONUtil.toStringArray(jsonArray),
					action -> resourceActions.contains(action)));
		}
	}

	private void _onAfterRemove(ObjectEntryFolder objectEntryFolder)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntryFolder.getCompanyId(), "LPD-17564")) {

			return;
		}

		_sharingEntryLocalService.deleteSharingEntries(
			_portal.getClassNameId(ObjectEntryFolder.class.getName()),
			objectEntryFolder.getObjectEntryFolderId());

		ObjectDefinition cmsDefaultPermissionObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION",
					objectEntryFolder.getCompanyId());

		if (cmsDefaultPermissionObjectDefinition == null) {
			return;
		}

		ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
			objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
			objectEntryFolder.getExternalReferenceCode(),
			objectEntryFolder.getModelClassName(), _filterFactory);

		if (objectEntry == null) {
			return;
		}

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

}