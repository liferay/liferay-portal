/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private JSONObject _getCMSDefaultPermissionJSONObject(
			ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition cmsDefaultPermissionObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", objectEntry.getCompanyId());

		if (cmsDefaultPermissionObjectDefinition == null) {
			return null;
		}

		if (objectEntry.getObjectEntryFolderId() != 0) {
			ObjectEntryFolder parentObjectEntryFolder =
				_objectEntryFolderLocalService.getObjectEntryFolder(
					objectEntry.getObjectEntryFolderId());

			JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
				parentObjectEntryFolder.getCompanyId(),
				parentObjectEntryFolder.getUserId(),
				parentObjectEntryFolder.getExternalReferenceCode(),
				parentObjectEntryFolder.getModelClassName(), _filterFactory);

			if ((jsonObject != null) && !JSONUtil.isEmpty(jsonObject)) {
				return jsonObject;
			}
		}

		Group group = _groupLocalService.getGroup(objectEntry.getGroupId());

		return CMSDefaultPermissionUtil.getJSONObject(
			group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			_filterFactory);
	}

	private ObjectEntryFolder _getRootObjectEntryFolder(
		ObjectEntry objectEntry) {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntry.getObjectEntryFolderId());

		if (objectEntryFolder == null) {
			return null;
		}

		if (Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return objectEntryFolder;
		}

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		if (parts.length <= 2) {
			return null;
		}

		return _objectEntryFolderLocalService.fetchObjectEntryFolder(
			GetterUtil.getLong(parts[1]));
	}

	private void _onAfterCreate(ObjectEntry objectEntry) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-17564") ||
			(objectEntry.getGroupId() == 0)) {

			return;
		}

		Group group = _groupLocalService.fetchGroup(objectEntry.getGroupId());

		if ((group == null) || !group.isDepot()) {
			return;
		}

		DepotEntry depotEntry = _depotEntryLocalService.getDepotEntry(
			group.getClassPK());

		if (depotEntry.getType() != DepotConstants.TYPE_SPACE) {
			return;
		}

		JSONObject defaultPermissionsJSONObject =
			_getCMSDefaultPermissionJSONObject(objectEntry);

		if ((defaultPermissionsJSONObject == null) ||
			JSONUtil.isEmpty(defaultPermissionsJSONObject)) {

			return;
		}

		ObjectEntryFolder rootObjectEntryFolder = _getRootObjectEntryFolder(
			objectEntry);

		if (rootObjectEntryFolder == null) {
			return;
		}

		JSONObject objectEntryJSONObject =
			defaultPermissionsJSONObject.getJSONObject(
				rootObjectEntryFolder.getExternalReferenceCode());

		if (objectEntryJSONObject == null) {
			return;
		}

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			objectEntry.getModelClassName());

		Iterator<String> iterator = objectEntryJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			JSONArray jsonArray = objectEntryJSONObject.getJSONArray(key);

			if ((jsonArray == null) || JSONUtil.isEmpty(jsonArray)) {
				continue;
			}

			Role role = _roleLocalService.fetchRole(
				objectEntry.getCompanyId(), key);

			if (role == null) {
				continue;
			}

			_resourcePermissionLocalService.setResourcePermissions(
				objectEntry.getCompanyId(), objectEntry.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId(),
				ArrayUtil.filter(
					JSONUtil.toStringArray(jsonArray),
					action -> resourceActions.contains(action)));
		}
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

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
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}