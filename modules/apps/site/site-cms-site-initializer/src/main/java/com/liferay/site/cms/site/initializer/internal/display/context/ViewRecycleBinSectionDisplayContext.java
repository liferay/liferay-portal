/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.rest.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.contributor.CMSSectionTypeContributor;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;
import com.liferay.trash.TrashHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Pedro Leite
 */
public class ViewRecycleBinSectionDisplayContext
	extends BaseSectionDisplayContext {

	public ViewRecycleBinSectionDisplayContext(
		AssetLibraryResource.Factory assetLibraryResourceFactory,
		List<CMSSectionTypeContributor> cmsSectionTypeContributors,
		DepotEntryLocalService depotEntryLocalService, long groupId,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		Portal portal, SearchResultResource.Factory searchResultResourceFactory,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry,
		TrashHelper trashHelper) {

		super(
			depotEntryLocalService, null, groupLocalService, httpServletRequest,
			language, objectDefinitionService, portal,
			translationInfoItemFieldValuesExporterRegistry);

		_assetLibraryResourceFactory = assetLibraryResourceFactory;
		_cmsSectionTypeContributors = cmsSectionTypeContributors;
		_groupId = groupId;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_searchResultResourceFactory = searchResultResourceFactory;
		_trashHelper = trashHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getBreadcrumbProps() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (objectEntryFolder == null) {
			addBreadcrumbItem(
				jsonArray, false, null,
				language.get(themeDisplay.getLocale(), "recycle-bin"));

			return HashMapBuilder.<String, Object>put(
				"breadcrumbItems", jsonArray
			).put(
				"emptyRecycleBinFilterString", getCMSSectionFilterString()
			).put(
				"hideSpace", true
			).put(
				"showEmptyRecycleBinAction", _isShowEmptyRecycleBinAction()
			).build();
		}

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
			"size", "md"
		).build();
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		List<DropdownItem> bulkActionDropdownItems =
			super.getBulkActionDropdownItems();

		bulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				"#", "restore", "restore",
				LanguageUtil.get(httpServletRequest, "restore"), null, null,
				null));

		return bulkActionDropdownItems;
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
				"get", null,
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
		String filterString = StringBundler.concat(
			"cmsRoot eq true and ",
			SectionDisplayContextUtil.getCMSSectionFilterString(
				_cmsSectionTypeContributors),
			" and rootDescendantNode eq false");

		List<Long> groupIds = _getTrashEnabledDepotEntryGroupIds();

		if (ListUtil.isEmpty(groupIds)) {
			return filterString + " and status eq " +
				WorkflowConstants.STATUS_ANY;
		}

		return StringBundler.concat(
			filterString, " and groupIds/any(g:g in (",
			StringUtil.merge(groupIds, ","), ")) and status eq ",
			WorkflowConstants.STATUS_IN_TRASH);
	}

	private List<Long> _getTrashEnabledDepotEntryGroupIds() {
		return ListUtil.filter(
			DepotEntryServiceUtil.getDepotEntryGroupIds(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				DepotConstants.TYPE_SPACE),
			groupId -> {
				Group group = groupLocalService.fetchGroup(groupId);

				if ((group != null) && _trashHelper.isTrashEnabled(group)) {
					return true;
				}

				return false;
			});
	}

	private boolean _isShowEmptyRecycleBinAction() {
		if (ListUtil.isEmpty(_getTrashEnabledDepotEntryGroupIds())) {
			return false;
		}

		try {
			SearchResultResource searchResultResource =
				_searchResultResourceFactory.create(
				).httpServletRequest(
					httpServletRequest
				).preferredLocale(
					themeDisplay.getLocale()
				).user(
					themeDisplay.getUser()
				).build();

			Page<SearchResult> page = searchResultResource.getSearchPage(
				null, true, null, null, null,
				searchResultResource.toFilter(getCMSSectionFilterString()),
				Pagination.of(1, 0), null);

			if (page.getTotalCount() > 0) {
				return true;
			}

			return false;
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewRecycleBinSectionDisplayContext.class);

	private final AssetLibraryResource.Factory _assetLibraryResourceFactory;
	private final List<CMSSectionTypeContributor> _cmsSectionTypeContributors;
	private final long _groupId;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final SearchResultResource.Factory _searchResultResourceFactory;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}