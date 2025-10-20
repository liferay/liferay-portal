/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.headless.cms.dto.v1_0.AssetPermissionAction;
import com.liferay.headless.cms.resource.v1_0.AssetPermissionActionResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 * @author Balazs Breier
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-permission-action.properties",
	scope = ServiceScope.PROTOTYPE,
	service = AssetPermissionActionResource.class
)
public class AssetPermissionActionResourceImpl
	extends BaseAssetPermissionActionResourceImpl {

	@Override
	public AssetPermissionAction postAssetPermission(
			AssetPermissionAction assetPermissionAction)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-17564")) {

			throw new UnsupportedOperationException();
		}

		if (AssetPermissionAction.Type.RESET_ASSET_PERMISSION_ACTION.equals(
				assetPermissionAction.getType())) {

			_executeResetAssetPermissionAction(assetPermissionAction);
		}

		return assetPermissionAction;
	}

	private void _executeResetAssetPermissionAction(
			AssetPermissionAction assetPermissionAction)
		throws Exception {

		String className = assetPermissionAction.getClassName();

		if (className.startsWith(ObjectDefinition.class.getName())) {
			ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
				assetPermissionAction.getClassPK());

			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				_objectEntryService.getModelResourcePermission(
					objectEntry.getObjectDefinitionId());

			modelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(), objectEntry,
				ActionKeys.PERMISSIONS);

			JSONObject jsonObject = _getCMSDefaultPermissionJSONObject(
				objectEntry.getGroupId(), objectEntry.getObjectEntryFolderId());

			ObjectEntryFolder rootObjectEntryFolder = _getRootObjectEntryFolder(
				objectEntry);

			if (rootObjectEntryFolder == null) {
				return;
			}

			JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
				rootObjectEntryFolder.getExternalReferenceCode());

			if (objectEntryJSONObject == null) {
				return;
			}

			_setResourcePermissions(
				objectEntry.getModelClassName(), objectEntry.getCompanyId(),
				objectEntry.getObjectEntryId(), objectEntryJSONObject);
		}
		else if (className.equals(ObjectEntryFolder.class.getName())) {
			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderService.getObjectEntryFolder(
					assetPermissionAction.getClassPK());

			_objectEntryFolderModelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(),
				objectEntryFolder.getObjectEntryFolderId(),
				ActionKeys.PERMISSIONS);

			JSONObject jsonObject = _getCMSDefaultPermissionJSONObject(
				objectEntryFolder.getGroupId(),
				objectEntryFolder.getParentObjectEntryFolderId());

			_setResourcePermissions(
				objectEntryFolder.getModelClassName(),
				objectEntryFolder.getCompanyId(),
				objectEntryFolder.getObjectEntryFolderId(),
				jsonObject.getJSONObject("OBJECT_ENTRY_FOLDERS"));
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	private JSONObject _getCMSDefaultPermissionJSONObject(
			long groupId, long objectEntryFolderId)
		throws Exception {

		if (objectEntryFolderId != 0) {
			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderService.getObjectEntryFolder(
					objectEntryFolderId);

			JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
				objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
				objectEntryFolder.getExternalReferenceCode(),
				objectEntryFolder.getModelClassName(), _filterFactory);

			if ((jsonObject != null) && !JSONUtil.isEmpty(jsonObject)) {
				return jsonObject;
			}
		}

		Group group = _groupLocalService.getGroup(groupId);

		return CMSDefaultPermissionUtil.getJSONObject(
			group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			_filterFactory);
	}

	private ObjectEntryFolder _getRootObjectEntryFolder(ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderService.fetchObjectEntryFolder(
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

		return _objectEntryFolderService.fetchObjectEntryFolder(
			GetterUtil.getLong(parts[1]));
	}

	private void _setResourcePermissions(
			String className, long companyId, long id, JSONObject jsonObject)
		throws Exception {

		_resourcePermissionLocalService.deleteResourcePermissions(
			companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(id));

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			className);

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			JSONArray jsonArray = jsonObject.getJSONArray(key);

			if ((jsonArray == null) || JSONUtil.isEmpty(jsonArray)) {
				continue;
			}

			Role role = _roleLocalService.fetchRole(companyId, key);

			if (role == null) {
				continue;
			}

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(id), role.getRoleId(),
				ArrayUtil.filter(
					JSONUtil.toStringArray(jsonArray),
					action -> resourceActions.contains(action)));
		}
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}