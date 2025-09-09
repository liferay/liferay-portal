/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.trash.TrashHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Pedro Leite
 */
public class ViewRecycleBinSectionDisplayContext
	extends BaseSectionDisplayContext {

	public ViewRecycleBinSectionDisplayContext(
		AssetLibraryResource.Factory assetLibraryResourceFactory,
		DepotEntryLocalService depotEntryLocalService, long groupId,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal, TrashHelper trashHelper) {

		super(
			depotEntryLocalService, null, groupLocalService, httpServletRequest,
			language, objectDefinitionService,
			objectDefinitionSettingLocalService,
			objectEntryFolderModelResourcePermission, portal);

		_assetLibraryResourceFactory = assetLibraryResourceFactory;
		_groupId = groupId;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_trashHelper = trashHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
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
			"cmsRoot eq true and (cmsSection eq 'contents' or cmsSection eq " +
				"'files') and status eq ";

		Long[] groupIds;

		try {
			groupIds = _getDepotGroupIds();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get depot group ids for group " + _groupId,
					exception);
			}

			return filter + WorkflowConstants.STATUS_ANY;
		}

		if (groupIds.length == 0) {
			return filter + WorkflowConstants.STATUS_ANY;
		}

		return filter + WorkflowConstants.STATUS_IN_TRASH +
			_buildGroupIdsAnyClause(groupIds);
	}

	private String _buildGroupIdsAnyClause(Long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return "";
		}

		StringBundler sb = new StringBundler(3);

		sb.append(" and groupIds/any(g:g in (");
		sb.append(StringUtil.merge(groupIds, ","));
		sb.append("))");

		return sb.toString();
	}

	private Long[] _getDepotGroupIds() throws Exception {
		AssetLibraryResource.Builder builder =
			_assetLibraryResourceFactory.create();

		AssetLibraryResource assetLibraryResource = builder.user(
			_themeDisplay.getUser()
		).build();

		return TransformUtil.transformToArray(
			assetLibraryResource.getAssetLibrariesPage(
				null, null, assetLibraryResource.toFilter("type eq 'Space'"),
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null
			).getItems(),
			assetLibrary -> {
				Group group = groupLocalService.fetchGroup(
					assetLibrary.getSiteId());

				if ((group == null) || !_trashHelper.isTrashEnabled(group)) {
					return null;
				}

				return group.getGroupId();
			},
			Long.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewRecycleBinSectionDisplayContext.class);

	private final AssetLibraryResource.Factory _assetLibraryResourceFactory;
	private final long _groupId;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}