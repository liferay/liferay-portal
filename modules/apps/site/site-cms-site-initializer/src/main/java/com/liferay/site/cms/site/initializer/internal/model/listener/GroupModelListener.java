/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelService;
import com.liferay.depot.service.DepotEntryService;
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
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.List;
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

		ObjectDefinition basicDocumentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_DOCUMENT", group.getCompanyId());
		ObjectDefinition basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_WEB_CONTENT", group.getCompanyId());

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			null, group.getCompanyId(), group.getCreatorUserId(),
			group.getExternalReferenceCode(), DepotEntry.class.getName(),
			JSONUtil.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				JSONUtil.put(
					RoleConstants.CMS_ADMINISTRATOR,
					TransformUtil.transformToArray(
						_resourceActionLocalService.getResourceActions(
							basicWebContentObjectDefinition.getClassName()),
						resourceAction -> resourceAction.getActionId(),
						String.class))
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				JSONUtil.put(
					RoleConstants.CMS_ADMINISTRATOR,
					TransformUtil.transformToArray(
						_resourceActionLocalService.getResourceActions(
							basicDocumentObjectDefinition.getClassName()),
						resourceAction -> resourceAction.getActionId(),
						String.class))
			).put(
				"OBJECT_ENTRY_FOLDERS",
				JSONUtil.put(
					RoleConstants.CMS_ADMINISTRATOR,
					JSONUtil.putAll(
						TransformUtil.transformToArray(
							_resourceActionLocalService.getResourceActions(
								ObjectEntryFolder.class.getName()),
							resourceAction -> resourceAction.getActionId(),
							String.class)))
			));
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

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Override
	public void onBeforeRemove(Group group) throws ModelListenerException {
		try {
			_onBeforeRemove(group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private Long[] _getGroupIds(Group group) throws Exception {
		long groupId = group.getGroupId();

		List<DepotEntry> depotEntries =
			_depotEntryService.getGroupConnectedDepotEntries(
				groupId, DepotConstants.TYPE_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		List<DepotEntry> trashEnabledDepotEntries = ListUtil.filter(
			depotEntries,
			depotEntry -> {
				Group depotGroup = _groupLocalService.fetchGroup(
					depotEntry.getGroupId());

				return (depotGroup != null) && _isTrashEnabled(depotGroup);
			});

		Long[] groupIds = TransformUtil.transformToArray(
			trashEnabledDepotEntries, DepotEntry::getGroupId, Long.class);

		Group scopeGroup = _groupLocalService.fetchGroup(groupId);

		if ((scopeGroup != null) && scopeGroup.isDepot() &&
			_isTrashEnabled(scopeGroup)) {

			groupIds = ArrayUtil.append(groupIds, groupId);
		}

		return groupIds;
	}

	private UnicodeProperties _getUnicodeProperties(
		long companyId, String externalReferenceCode) {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group != null) {
			return group.getTypeSettingsProperties();
		}

		return new UnicodeProperties(true);
	}

	private boolean _isTrashEnabled(Group group) {
		return Boolean.parseBoolean(
			group.getTypeSettingsProperty("trashEnabled"));
	}

	private void _onAfterUpdate(Group originalGroup, Group group)
		throws Exception {

		if (group.isDepot()) {
			UnicodeProperties unicodeProperties = _getUnicodeProperties(
				group.getCompanyId(), group.getExternalReferenceCode());

			if (Objects.equals(
					originalGroup.getTypeSettingsProperty("trashEnabled"),
					group.getTypeSettingsProperty("trashEnabled")) ||
				!unicodeProperties.containsKey("trashEnabled")) {

				return;
			}

			DepotEntry depotEntry = _depotEntryService.fetchGroupDepotEntry(
				group.getGroupId());

			List<DepotEntryGroupRel> depotEntryGroupRels =
				_depotEntryGroupRelService.getDepotEntryGroupRels(
					depotEntry, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Long[] groupIds;

			if (!depotEntryGroupRels.isEmpty()) {
				groupIds = _getGroupIds(
					_groupLocalService.getGroup(
						depotEntryGroupRels.get(
							0
						).getToGroupId()));
			}
			else {
				groupIds = _getGroupIds(depotEntry.getGroup());
			}

			if ((groupIds.length != 0) ||
				(Objects.equals(
					unicodeProperties.getProperty("trashEnabled"), "true") &&
				 !unicodeProperties.getProperty(
					 "trashEnabled"
				 ).isEmpty())) {

				for (DepotEntryGroupRel depotEntryGroupRel :
						depotEntryGroupRels) {

					Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
						depotEntryGroupRel.getToGroupId(), false,
						"/recycle-bin");

					layout.setHidden(false);

					_layoutLocalService.updateLayout(layout);
				}
			}

			if ((groupIds.length == 0) &&
				Objects.equals(
					unicodeProperties.getProperty("trashEnabled"), "false") &&
				!unicodeProperties.getProperty(
					"trashEnabled"
				).isEmpty()) {

				for (DepotEntryGroupRel depotEntryGroupRel :
						depotEntryGroupRels) {

					Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
						depotEntryGroupRel.getToGroupId(), false,
						"/recycle-bin");

					layout.setHidden(true);

					_layoutLocalService.updateLayout(layout);
				}
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

	@Reference
	private DepotEntryGroupRelService _depotEntryGroupRelService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

}