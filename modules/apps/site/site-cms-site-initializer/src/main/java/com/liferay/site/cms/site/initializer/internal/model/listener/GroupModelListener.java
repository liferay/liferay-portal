/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;
import com.liferay.trash.TrashHelper;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onAfterCreate(Group group) throws ModelListenerException {
		try {
			_onAfterCreate(group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(Group group) throws ModelListenerException {
		try {
			_onAfterRemove(group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(Group originalGroup, Group group)
		throws ModelListenerException {

		try {
			_onAfterUpdate(originalGroup, group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeRemove(Group group) throws ModelListenerException {
		try {
			_onBeforeRemove(group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private Long[] _getDepotGroupIds(long companyId) {
		return TransformUtil.transformToArray(
			_depotEntryLocalService.getDepotEntries(
				companyId, DepotConstants.TYPE_SPACE),
			depotEntry -> {
				Group group = _groupLocalService.fetchGroup(
					depotEntry.getGroupId());

				if ((group == null) || !_trashHelper.isTrashEnabled(group)) {
					return null;
				}

				return group.getGroupId();
			},
			Long.class);
	}

	private JSONObject _getObjectEntryDefaultPermissionJSONObject(
			long companyId, String externalReferenceCode)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		String[] actionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				objectDefinition.getClassName()),
			ResourceAction::getActionId, String.class);

		return JSONUtil.put(
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
			new String[] {
				ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
				ActionKeys.DELETE_DISCUSSION, ActionKeys.PERMISSIONS,
				ActionKeys.UPDATE, ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW
			}
		).put(
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
			new String[] {
				ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
				ActionKeys.DELETE_DISCUSSION, ActionKeys.PERMISSIONS,
				ActionKeys.UPDATE, ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW
			}
		).put(
			DepotRolesConstants.ASSET_LIBRARY_MEMBER,
			new String[] {ActionKeys.VIEW}
		).put(
			RoleConstants.CMS_ADMINISTRATOR, actionIds
		).put(
			RoleConstants.OWNER, actionIds
		).put(
			RoleConstants.USER, new String[] {ActionKeys.VIEW}
		);
	}

	private void _onAfterCreate(Group group) throws PortalException {
		if ((group.getType() != GroupConstants.TYPE_DEPOT) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-17564")) {

			return;
		}

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
						ActionKeys.ADD_ENTRY, ActionKeys.DELETE,
						ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
						ActionKeys.SUBSCRIBE, ActionKeys.VIEW
					}
				).put(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
					new String[] {
						ActionKeys.ADD_ENTRY, ActionKeys.DELETE,
						ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
						ActionKeys.SUBSCRIBE, ActionKeys.VIEW
					}
				).put(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER,
					new String[] {ActionKeys.VIEW, ActionKeys.SUBSCRIBE}
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

	private void _onAfterRemove(Group group) throws PortalException {
		if ((group.getType() != GroupConstants.TYPE_DEPOT) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-17564")) {

			return;
		}

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

	private void _onAfterUpdate(Group originalGroup, Group group)
		throws Exception {

		if (group.isDepot()) {
			DepotEntry depotEntry =
				_depotEntryLocalService.fetchGroupDepotEntry(
					group.getGroupId());

			if ((depotEntry == null) ||
				!(depotEntry.getType() == DepotConstants.TYPE_SPACE) ||
				Objects.equals(
					originalGroup.getTypeSettingsProperty("trashEnabled"),
					group.getTypeSettingsProperty("trashEnabled")) ||
				!FeatureFlagManagerUtil.isEnabled(
					group.getCompanyId(), "LPD-17564")) {

				return;
			}

			Group siteGroup = _groupLocalService.fetchGroup(
				group.getCompanyId(), GroupConstants.CMS);

			if (siteGroup == null) {
				return;
			}

			Long[] groupIds = _getDepotGroupIds(group.getCompanyId());

			if (groupIds.length > 0) {
				_updateRecycleBinLayout(siteGroup, false);
			}
			else {
				_updateRecycleBinLayout(siteGroup, true);
			}
		}
	}

	private void _onBeforeRemove(Group group) throws Exception {
		if ((group.getType() != GroupConstants.TYPE_DEPOT) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-17564")) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					group.getGroupId(), group.getCompanyId());
			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
					group.getGroupId(), group.getCompanyId());
		}
	}

	private void _updateRecycleBinLayout(Group group, boolean hidden)
		throws Exception {

		Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
			group.getGroupId(), false, "/recycle-bin");

		if (layout.isHidden() == hidden) {
			return;
		}

		layout.setHidden(hidden);

		_layoutLocalService.updateLayout(layout);
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
	private LayoutLocalService _layoutLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private TrashHelper _trashHelper;

}