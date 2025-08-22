/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Pedro Leite
 */
public class ViewRecycleBinSectionDisplayContext
	extends BaseSectionDisplayContext {

	public ViewRecycleBinSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal) {

		super(
			depotEntryLocalService, null, groupLocalService, httpServletRequest,
			language, objectDefinitionService,
			objectDefinitionSettingLocalService,
			objectEntryFolderModelResourcePermission, portal);

		_objectEntryFolderLocalService = objectEntryFolderLocalService;
	}

	public Map<String, Object> getBreadcrumbProps() {
		if (objectEntryFolder == null) {
			return Collections.emptyMap();
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		addBreadcrumbItem(
			jsonArray, false, ActionUtil.getRecycleBinURL(themeDisplay),
			language.get(themeDisplay.getLocale(), "recycle-bin"));

		for (String objectEntryFolderId :
				StringUtil.split(
					objectEntryFolder.getTreePath(), CharPool.SLASH)) {

			ObjectEntryFolder parentObjectEntryFolder =
				_objectEntryFolderLocalService.fetchObjectEntryFolder(
					GetterUtil.getLong(objectEntryFolderId));

			if (parentObjectEntryFolder.getStatus() !=
					WorkflowConstants.STATUS_IN_TRASH) {

				continue;
			}

			if (objectEntryFolder.getObjectEntryFolderId() ==
					parentObjectEntryFolder.getObjectEntryFolderId()) {

				addBreadcrumbItem(
					jsonArray, true, null, parentObjectEntryFolder.getName());

				continue;
			}

			addBreadcrumbItem(
				jsonArray, false,
				ActionUtil.getViewFolderRecycleBinURL(
					parentObjectEntryFolder.getObjectEntryFolderId(),
					themeDisplay),
				parentObjectEntryFolder.getName());
		}

		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", jsonArray
		).put(
			"displayType",
			() -> {
				Group group = groupLocalService.fetchGroup(
					objectEntryFolder.getGroupId());

				UnicodeProperties unicodeProperties =
					group.getTypeSettingsProperties();

				return GetterUtil.get(
					unicodeProperties.get("logoColor"), "outline-0");
			}
		).put(
			"size", "sm"
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(httpServletRequest, "the-recycle-bin-is-empty")
		).put(
			"image", "/states/cms_empty_state_files.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-assets-yet")
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				ActionUtil.getBaseViewFolderRecycleBinURL(themeDisplay) +
					"{embedded.id}",
				"view", "actionLinkFolder",
				LanguageUtil.get(httpServletRequest, "view-folder"), "get",
				"update", null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", ObjectEntryFolder.class.getName()
				).build()),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				language.get(httpServletRequest, "delete"), "delete", "delete",
				null),
			new FDSActionDropdownItem(
				null, "restore", "restore",
				language.get(httpServletRequest, "restore"), "restore",
				"restore", null));
	}

	@Override
	protected String getCMSSectionFilterString() {
		String filter =
			"cmsRoot eq true and (cmsSection eq 'contents' or cmsSection " +
			"eq 'files') and status eq " + WorkflowConstants.STATUS_IN_TRASH;

		List<Long> eligible = new ArrayList<>();

		try {
			eligible = _getEligibleGroupIds(httpServletRequest);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to resolve eligible group ids", portalException);
			}

			return filter;
		}

		if (eligible.isEmpty()) {
			return "cmsKind eq 'object' and (cmsSection eq 'contents' or " +
				"cmsSection eq 'files') and status eq -1";
		}

		return filter + _buildGroupIdsAnyClause(eligible);
	}

	private String _buildGroupIdsAnyClause(List<Long> groupIds) {
		if (groupIds.isEmpty()) {
			return "";
		}

		StringBundler sb = new StringBundler();

		if (groupIds.size() == 1) {
			sb.append(" and groupIds/any(g:g eq ");
			sb.append(groupIds.get(0));
			sb.append(")");

			return sb.toString();
		}

		sb.append(" and (");

		boolean first = true;

		for (Long id : groupIds) {
			if (!first) {
				sb.append(" or ");
			}

			sb.append("groupIds/any(g:g eq ");
			sb.append(id);
			sb.append(")");

			first = false;
		}

		sb.append(")");

		return sb.toString();
	}

	private List<Long> _getEligibleGroupIds(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long scopeGroupId = portal.getScopeGroupId(httpServletRequest);
		List<Long> ids = new ArrayList<>();

		List<DepotEntry> depots =
			depotEntryLocalService.getGroupConnectedDepotEntries(
				scopeGroupId, -1, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (DepotEntry depot : depots) {
			Group depotGroup = groupLocalService.fetchGroup(depot.getGroupId());

			if ((depotGroup != null) && _isTrashEnabled(depotGroup)) {
				ids.add(depotGroup.getGroupId());
			}
		}

		Group scopeGroup = groupLocalService.fetchGroup(scopeGroupId);

		if ((scopeGroup != null) && scopeGroup.isDepot() &&
			_isTrashEnabled(scopeGroup)) {

			ids.add(scopeGroupId);
		}

		return ids;
	}

	private boolean _isTrashEnabled(Group group) {
		return Boolean.parseBoolean(
			group.getTypeSettingsProperty("trashEnabled"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewRecycleBinSectionDisplayContext.class);

	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
}