/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorReturnType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.service.JournalFolderServiceUtil;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.util.comparator.FolderArticleArticleIdComparator;
import com.liferay.journal.util.comparator.FolderArticleCreateDateComparator;
import com.liferay.journal.util.comparator.FolderArticleDisplayDateComparator;
import com.liferay.journal.util.comparator.FolderArticleModifiedDateComparator;
import com.liferay.journal.util.comparator.FolderArticleTitleComparator;
import com.liferay.journal.web.internal.asset.model.JournalArticleAssetRenderer;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.constants.JournalWebConstants;
import com.liferay.journal.web.internal.dao.search.JournalRecentArticlesResultRowSplitter;
import com.liferay.journal.web.internal.dao.search.JournalResultRowSplitter;
import com.liferay.journal.web.internal.portlet.action.ActionUtil;
import com.liferay.journal.web.internal.search.EntriesChecker;
import com.liferay.journal.web.internal.search.EntriesMover;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission;
import com.liferay.journal.web.internal.servlet.taglib.util.JournalArticleActionDropdownItemsProvider;
import com.liferay.journal.web.internal.servlet.taglib.util.JournalFolderActionDropdownItems;
import com.liferay.journal.web.internal.util.DDMStructureUtil;
import com.liferay.journal.web.internal.util.JournalPortletUtil;
import com.liferay.journal.web.internal.util.JournalSearcherUtil;
import com.liferay.journal.web.internal.util.JournalUtil;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class JournalDisplayContext {

	public static JournalDisplayContext create(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		ItemSelector itemSelector, JournalHelper journalHelper,
		JournalWebConfiguration journalWebConfiguration,
		PortletRequest portletRequest, PortletResponse portletResponse,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService, TrashHelper trashHelper) {

		JournalDisplayContext journalDisplayContext =
			(JournalDisplayContext)portletRequest.getAttribute(
				JournalWebConstants.JOURNAL_DISPLAY_CONTEXT);

		if (journalDisplayContext == null) {
			journalDisplayContext = new JournalDisplayContext(
				assetDisplayPageFriendlyURLProvider, itemSelector,
				journalHelper, journalWebConfiguration, portletRequest,
				portletResponse, resourcePermissionLocalService,
				roleLocalService, trashHelper);

			portletRequest.setAttribute(
				JournalWebConstants.JOURNAL_DISPLAY_CONTEXT,
				journalDisplayContext);
		}

		return journalDisplayContext;
	}

	public long[] getAddMenuFavItems() throws PortalException {
		if (_addMenuFavItems != null) {
			return _addMenuFavItems;
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_httpServletRequest);

		List<Long> addMenuFavItemsList = new ArrayList<>();

		String[] addMenuFavItems = portalPreferences.getValues(
			JournalPortletKeys.JOURNAL,
			JournalPortletUtil.getAddMenuFavItemKey(
				_journalHelper, _liferayPortletRequest),
			new String[0]);

		for (DDMStructure ddmStructure : getDDMStructures()) {
			if (ArrayUtil.contains(
					addMenuFavItems,
					String.valueOf(ddmStructure.getStructureId()))) {

				addMenuFavItemsList.add(ddmStructure.getStructureId());
			}
		}

		_addMenuFavItems = ArrayUtil.toLongArray(addMenuFavItemsList);

		return _addMenuFavItems;
	}

	public int getAddMenuFavItemsLength() throws PortalException {
		long[] addMenuFavItems = getAddMenuFavItems();

		return addMenuFavItems.length;
	}

	public JournalArticle getArticle() throws PortalException {
		if (_article != null) {
			return _article;
		}

		_article = ActionUtil.getArticle(_httpServletRequest);

		return _article;
	}

	public List<DropdownItem> getArticleActionDropdownItems(
			JournalArticle article)
		throws Exception {

		JournalArticleActionDropdownItemsProvider
			articleActionDropdownItemsProvider =
				new JournalArticleActionDropdownItemsProvider(
					article, _liferayPortletRequest, _liferayPortletResponse,
					_assetDisplayPageFriendlyURLProvider, _trashHelper);

		return articleActionDropdownItemsProvider.getActionDropdownItems();
	}

	public JournalArticleDisplay getArticleDisplay() throws Exception {
		if (_articleDisplay != null) {
			return _articleDisplay;
		}

		JournalArticle article = JournalArticleLocalServiceUtil.fetchArticle(
			ParamUtil.getLong(_httpServletRequest, "groupId"),
			ParamUtil.getString(_httpServletRequest, "articleId"),
			ParamUtil.getDouble(_httpServletRequest, "version"));

		if (article == null) {
			return _articleDisplay;
		}

		_articleDisplay = JournalArticleLocalServiceUtil.getArticleDisplay(
			article, article.getDDMTemplateKey(), null,
			_themeDisplay.getLanguageId(),
			ParamUtil.getInteger(_httpServletRequest, "page"),
			new PortletRequestModel(
				_liferayPortletRequest, _liferayPortletResponse),
			_themeDisplay);

		return _articleDisplay;
	}

	public List<DropdownItem> getArticleHistoryActionDropdownItems(
			JournalArticle article)
		throws Exception {

		JournalArticleActionDropdownItemsProvider
			articleActionDropdownItemsProvider =
				new JournalArticleActionDropdownItemsProvider(
					article, _liferayPortletRequest, _liferayPortletResponse,
					_assetDisplayPageFriendlyURLProvider, _trashHelper);

		return articleActionDropdownItemsProvider.
			getArticleHistoryActionDropdownItems();
	}

	public List<DropdownItem> getArticleInfoPanelDropdownItems(
			JournalArticle article)
		throws Exception {

		return getArticleActionDropdownItems(article);
	}

	public String getArticleSubtitle(JournalArticle article) {
		if (isNavigationMine() || isNavigationRecent()) {
			return _getSubtitle(
				article.getCreateDate(), "created-x-ago-by-x",
				article.getUserName());
		}

		return _getSubtitle(
			article.getModifiedDate(), "modified-x-ago-by-x",
			article.getStatusByUserName());
	}

	public List<DropdownItem> getArticleVersionActionDropdownItems(
			JournalArticle article)
		throws Exception {

		JournalArticleActionDropdownItemsProvider
			articleActionDropdownItemsProvider =
				new JournalArticleActionDropdownItemsProvider(
					article, _liferayPortletRequest, _liferayPortletResponse,
					_assetDisplayPageFriendlyURLProvider, _trashHelper);

		return articleActionDropdownItemsProvider.
			getArticleVersionActionDropdownItems();
	}

	public String getAvailableActions(JournalArticle article)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (JournalArticlePermission.contains(
				permissionChecker, article, ActionKeys.DELETE)) {

			availableActions.add("deleteEntries");
		}

		if (JournalArticlePermission.contains(
				permissionChecker, article, ActionKeys.PERMISSIONS)) {

			availableActions.add("changePermissions");
		}

		long scopeGroupId = _themeDisplay.getScopeGroupId();
		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		try {
			if (GroupPermissionUtil.contains(
					permissionChecker, scopeGroupId,
					ActionKeys.EXPORT_IMPORT_PORTLET_INFO) &&
				stagingGroupHelper.isStagingGroup(scopeGroupId) &&
				stagingGroupHelper.isStagedPortlet(
					scopeGroupId, JournalPortletKeys.JOURNAL)) {

				availableActions.add("publishEntriesToLive");
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"An exception occured when checking if the publish " +
						"action should be displayed",
					portalException);
			}
		}

		if (JournalArticlePermission.contains(
				permissionChecker, article, ActionKeys.EXPIRE) &&
			(article.getStatus() == WorkflowConstants.STATUS_APPROVED)) {

			availableActions.add("expireEntries");
		}

		if (JournalArticlePermission.contains(
				permissionChecker, article, ActionKeys.UPDATE)) {

			availableActions.add("moveEntries");
		}

		if (JournalArticlePermission.contains(
				permissionChecker, article, ActionKeys.VIEW)) {

			availableActions.add("exportTranslation");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	public String getAvailableActions(JournalFolder folder)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (JournalFolderPermission.contains(
				_themeDisplay.getPermissionChecker(), folder,
				ActionKeys.UPDATE)) {

			availableActions.add("deleteEntries");
		}

		if (JournalFolderPermission.contains(
				_themeDisplay.getPermissionChecker(), folder,
				ActionKeys.DELETE)) {

			availableActions.add("moveEntries");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	public String[] getCharactersBlacklist() throws PortalException {
		JournalServiceConfiguration journalServiceConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				JournalServiceConfiguration.class,
				_themeDisplay.getCompanyId());

		return journalServiceConfiguration.charactersblacklist();
	}

	public int getCommentsTotal() throws PortalException {
		SearchContainer<MBMessage> commentsSearchContainer =
			_getCommentsSearchContainer();

		return commentsSearchContainer.getTotal();
	}

	public Map<String, Object> getComponentContext() throws Exception {
		return Collections.singletonMap(
			"trashEnabled",
			_trashHelper.isTrashEnabled(_themeDisplay.getScopeGroupId()));
	}

	public List<TabsItem> getConfigurationTabsItems() {
		TabsItemList tabsItemList = TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "email-from"));
			}
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "web-content-added-email"))
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "web-content-expired-email"))
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "web-content-moved-from-folder-email"))
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "web-content-moved-to-folder-email"))
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "web-content-review-email"))
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "web-content-updated-email"))
		).build();

		if (JournalUtil.hasWorkflowDefinitionsLinks(_themeDisplay)) {
			tabsItemList.add(
				tabsItem -> tabsItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest,
						"web-content-approval-denied-email")));
			tabsItemList.add(
				tabsItem -> tabsItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest,
						"web-content-approval-granted-email")));
			tabsItemList.add(
				tabsItem -> tabsItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest,
						"web-content-approval-requested-email")));
		}

		return tabsItemList;
	}

	public long getDDMStructureId() {
		if (_ddmStructureId != null) {
			return _ddmStructureId;
		}

		_ddmStructureId = ParamUtil.getLong(
			_httpServletRequest, "ddmStructureId");

		return _ddmStructureId;
	}

	public String getDDMStructureName() {
		if (_ddmStructureName != null) {
			return _ddmStructureName;
		}

		_ddmStructureName = LanguageUtil.get(
			_httpServletRequest, "basic-web-content");

		if (getDDMStructureId() <= 0) {
			return _ddmStructureName;
		}

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
			getDDMStructureId());

		if (ddmStructure != null) {
			_ddmStructureName = ddmStructure.getName(_themeDisplay.getLocale());
		}

		return _ddmStructureName;
	}

	public List<DDMStructure> getDDMStructures() throws PortalException {
		return getDDMStructures(getRestrictionType());
	}

	public List<DDMStructure> getDDMStructures(Integer restrictionType)
		throws PortalException {

		if (_ddmStructures != null) {
			return _ddmStructures;
		}

		if (restrictionType == null) {
			restrictionType = getRestrictionType();
		}

		_ddmStructures = JournalFolderServiceUtil.getDDMStructures(
			SiteConnectedGroupGroupProviderUtil.
				getCurrentAndAncestorSiteAndDepotGroupIds(
					_themeDisplay.getScopeGroupId(), false, true),
			getFolderId(), restrictionType);

		if (_journalWebConfiguration.journalBrowseByStructuresSortedByName()) {
			Locale locale = _themeDisplay.getLocale();

			_ddmStructures.sort(
				(ddmStructure1, ddmStructure2) -> {
					String name1 = ddmStructure1.getName(locale);
					String name2 = ddmStructure2.getName(locale);

					return name1.compareTo(name2);
				});
		}

		return _ddmStructures;
	}

	public VerticalNavItemList getDDMStructureVerticalNavItemList() {
		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (DDMStructure ddmStructure :
				DDMStructureUtil.getHighlightedDDMStructures(_themeDisplay)) {

			verticalNavItemList.add(
				verticalNavItem -> {
					verticalNavItem.setActive(
						getHighlightedDDMStructureId() ==
							ddmStructure.getStructureId());
					verticalNavItem.setHref(
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setParameter(
							"highlightedDDMStructureId",
							ddmStructure.getStructureId()
						).buildString());

					String name = ddmStructure.getName(
						_themeDisplay.getLocale());

					verticalNavItem.setId(name);
					verticalNavItem.setLabel(name);
				});
		}

		return verticalNavItemList;
	}

	public int getDefaultStatus() {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				_themeDisplay.getCompanyId(),
				_themeDisplay.getScopeGroupId()) ||
			isNavigationMine()) {

			return WorkflowConstants.STATUS_ANY;
		}

		return WorkflowConstants.STATUS_APPROVED;
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_httpServletRequest);

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		if (Validator.isNull(_displayStyle)) {
			_displayStyle = portalPreferences.getValue(
				JournalPortletKeys.JOURNAL, "display-style", "descriptive");
		}

		portalPreferences.setValue(
			JournalPortletKeys.JOURNAL, "display-style", _displayStyle);

		return _displayStyle;
	}

	public JournalFolder getFolder() {
		if (_folder != null) {
			return _folder;
		}

		_folder = (JournalFolder)_httpServletRequest.getAttribute(
			WebKeys.JOURNAL_FOLDER);

		if (_folder != null) {
			return _folder;
		}

		_folder = JournalFolderLocalServiceUtil.fetchFolder(
			ParamUtil.getLong(_httpServletRequest, "folderId"));

		return _folder;
	}

	public List<DropdownItem> getFolderActionDropdownItems(JournalFolder folder)
		throws Exception {

		JournalFolderActionDropdownItems folderActionDropdownItems =
			new JournalFolderActionDropdownItems(
				folder, _liferayPortletRequest, _liferayPortletResponse,
				_trashHelper);

		return folderActionDropdownItems.getActionDropdownItems();
	}

	public long getFolderId() {
		if (_folderId != null) {
			return _folderId;
		}

		_folderId = BeanParamUtil.getLong(
			getFolder(), _httpServletRequest, "folderId",
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return _folderId;
	}

	public List<DropdownItem> getFolderInfoPanelDropdownItems(
			JournalFolder folder)
		throws Exception {

		JournalFolderActionDropdownItems folderActionDropdownItems =
			new JournalFolderActionDropdownItems(
				folder, _liferayPortletRequest, _liferayPortletResponse,
				_trashHelper);

		return folderActionDropdownItems.getInfoPanelActionDropdownItems();
	}

	public JSONArray getFoldersJSONArray() {
		return JSONUtil.put(
			JSONUtil.put(
				"children",
				_getFoldersJSONArray(
					_themeDisplay.getScopeGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID)
			).put(
				"id", JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID
			).put(
				"name", LanguageUtil.get(_themeDisplay.getLocale(), "home")
			));
	}

	public String getFolderSubtitle(JournalFolder folder) {
		if (isNavigationMine() || isNavigationRecent()) {
			return _getSubtitle(
				folder.getCreateDate(), "created-x-ago-by-x",
				folder.getUserName());
		}

		return _getSubtitle(
			folder.getModifiedDate(), "modified-x-ago-by-x",
			folder.getStatusByUserName());
	}

	public long getHighlightedDDMStructureId() {
		if (_highlightedDDMStructureId != null) {
			return _highlightedDDMStructureId;
		}

		_highlightedDDMStructureId = ParamUtil.getLong(
			_httpServletRequest, "highlightedDDMStructureId");

		return _highlightedDDMStructureId;
	}

	public List<TabsItem> getInfoPanelTabsItems(boolean journalArticle) {
		return TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "details"));
			}
		).add(
			() -> journalArticle,
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "versions"))
		).build();
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public JournalArticle getLatestArticle(JournalArticle journalArticle) {
		JournalArticle latestArticle =
			JournalArticleLocalServiceUtil.fetchLatestArticle(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				WorkflowConstants.STATUS_ANY);

		if (latestArticle != null) {
			return latestArticle;
		}

		return journalArticle;
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	public List<NavigationItem> getNavigationItems(String currentItem) {
		Group group = _themeDisplay.getScopeGroup();

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(currentItem.equals("web-content"));
				navigationItem.setHref(
					_liferayPortletResponse.createRenderURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "web-content"));
			}
		).add(
			() -> !group.isLayout(),
			navigationItem -> {
				navigationItem.setActive(currentItem.equals("structures"));
				navigationItem.setHref(
					_liferayPortletResponse.createRenderURL(), "mvcPath",
					"/view_ddm_structures.jsp");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "structures"));
			}
		).add(
			() -> !group.isLayout(),
			navigationItem -> {
				navigationItem.setActive(currentItem.equals("templates"));
				navigationItem.setHref(
					_liferayPortletResponse.createRenderURL(), "mvcPath",
					"/view_ddm_templates.jsp");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "templates"));
			}
		).add(
			() ->
				_journalWebConfiguration.showFeeds() &&
				PortalUtil.isRSSFeedsEnabled(),
			navigationItem -> {
				navigationItem.setActive(currentItem.equals("feeds"));
				navigationItem.setHref(_getFeedsURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "feeds"));
			}
		).build();
	}

	public String getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(_httpServletRequest, "orderByCol");

		if (Validator.isNull(_orderByCol)) {
			if (isSearch()) {
				_orderByCol = _portalPreferences.getValue(
					JournalPortletKeys.JOURNAL, "order-by-col", "relevance");
			}
			else {
				_orderByCol = _portalPreferences.getValue(
					JournalPortletKeys.JOURNAL, "order-by-col",
					"modified-date");

				if (Objects.equals(_orderByCol, "relevance")) {
					_orderByCol = "modified-date";

					_portalPreferences.setValue(
						JournalPortletKeys.JOURNAL, "order-by-col", null);
				}
			}
		}
		else {
			_portalPreferences.setValue(
				JournalPortletKeys.JOURNAL, "order-by-col", _orderByCol);
		}

		return _orderByCol;
	}

	public String getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		if (isNavigationRecent() ||
			Objects.equals(getOrderByCol(), "relevance")) {

			return "desc";
		}

		_orderByType = ParamUtil.getString(_httpServletRequest, "orderByType");

		if (Validator.isNull(_orderByType)) {
			String defaultOrderByType = "asc";

			if (Objects.equals(getOrderByCol(), "create-date") ||
				Objects.equals(getOrderByCol(), "modified-date")) {

				defaultOrderByType = "desc";
			}

			_orderByType = _portalPreferences.getValue(
				JournalPortletKeys.JOURNAL, "order-by-type",
				defaultOrderByType);
		}
		else {
			_portalPreferences.setValue(
				JournalPortletKeys.JOURNAL, "order-by-type", _orderByType);
		}

		return _orderByType;
	}

	public String[] getOrderColumns() {
		String[] orderColumns = {
			"display-date", "modified-date", "create-date", "title"
		};

		if (isSearch()) {
			orderColumns = ArrayUtil.append(orderColumns, "relevance");
		}

		if (!_journalWebConfiguration.journalArticleForceAutogenerateId() ||
			_journalWebConfiguration.journalArticleShowId()) {

			orderColumns = ArrayUtil.append(orderColumns, "id");
		}

		return orderColumns;
	}

	public String getOriginalAuthorUserName(JournalArticle article) {
		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			JournalArticle.class.getName(),
			JournalArticleAssetRenderer.getClassPK(article));

		if (assetEntry != null) {
			return assetEntry.getUserName();
		}

		return article.getUserName();
	}

	public long getParentFolderId() {
		if (_parentFolderId != null) {
			return _parentFolderId;
		}

		_parentFolderId = ParamUtil.getLong(
			_httpServletRequest, "parentFolderId",
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return _parentFolderId;
	}

	public PortletURL getPortletURL(String tab) {
		PortletURL portletURL = _getBasePortletURL(tab);

		String searchIn = _getSearchIn();

		if (Validator.isNotNull(searchIn)) {
			portletURL.setParameter("searchIn", searchIn);
		}

		String searchLocation = _getSearchLocation();

		if (Validator.isNotNull(searchLocation)) {
			portletURL.setParameter("searchLocation", searchLocation);
		}

		return portletURL;
	}

	public int getRestrictionType() {
		if (_restrictionType != null) {
			return _restrictionType;
		}

		JournalFolder folder = getFolder();

		if (folder != null) {
			_restrictionType = folder.getRestrictionType();
		}
		else {
			_restrictionType = JournalFolderConstants.RESTRICTION_TYPE_INHERIT;
		}

		return _restrictionType;
	}

	public ResultRowSplitter getResultRowSplitter() {
		if (isNavigationRecent()) {
			return new JournalRecentArticlesResultRowSplitter(_themeDisplay);
		}

		return new JournalResultRowSplitter();
	}

	public String getScheduledArticleMessage(JournalArticle journalArticle) {
		int count = JournalArticleServiceUtil.getArticlesCountByArticleId(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			WorkflowConstants.STATUS_SCHEDULED);

		if (count > 1) {
			return LanguageUtil.get(
				_themeDisplay.getLocale(),
				"multiple-publications-were-scheduled");
		}

		return _dateTimeFormat.format(journalArticle.getDisplayDate());
	}

	public SearchContainer<?> getSearchContainer() throws PortalException {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		if (isShowComments()) {
			_searchContainer = _getCommentsSearchContainer();

			return _searchContainer;
		}

		if (isIndexAllArticleVersions() && isShowVersions()) {
			_searchContainer = _getVersionsSearchContainer();

			return _searchContainer;
		}

		_searchContainer = _getArticlesSearchContainer();

		return _searchContainer;
	}

	public String getSearchInLabel() {
		if (Objects.equals(_getSearchIn(), "title")) {
			return LanguageUtil.get(_themeDisplay.getLocale(), "title-only");
		}

		if (Objects.equals(_getSearchIn(), "comments")) {
			return LanguageUtil.get(_themeDisplay.getLocale(), "comments");
		}

		return LanguageUtil.get(_themeDisplay.getLocale(), "all-fields");
	}

	public String getSearchLocationLabel() {
		if (getFolderId() == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return null;
		}

		if (_isSearchLocationCurrentFolder()) {
			JournalFolder folder = getFolder();

			return folder.getName();
		}

		return LanguageUtil.get(_themeDisplay.getLocale(), "everywhere");
	}

	public Map<String, Object> getSearchProps() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"searchIn", _getSearchIn()
		).put(
			"searchInCommentsURL",
			String.valueOf(_getSearchInCommentsPortletURL())
		).put(
			"searchInOptions",
			() -> {
				if (isSearch()) {
					return _getSearchInOptionsJSONArray();
				}

				return null;
			}
		).put(
			"searchLocation", _getSearchLocation()
		).put(
			"searchLocationOptions",
			() -> {
				if ((getFolderId() ==
						JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) ||
					!isSearch()) {

					return null;
				}

				JournalFolder folder = getFolder();

				return JSONUtil.putAll(
					JSONUtil.put(
						"label", folder.getName()
					).put(
						"value", "current-folder"
					),
					JSONUtil.put(
						"label",
						LanguageUtil.get(
							_themeDisplay.getLocale(), "everywhere")
					).put(
						"value", "everywhere"
					));
			}
		).put(
			"searchResults", getTab()
		).put(
			"searchURL", String.valueOf(_getBasePortletURL(getTab()))
		).build();
	}

	public String getSearchResultsLabel() throws PortalException {
		String searchResults = getTab();

		if (Objects.equals(searchResults, "comments")) {
			return LanguageUtil.get(_themeDisplay.getLocale(), "comments");
		}
		else if (Objects.equals(searchResults, "versions")) {
			return LanguageUtil.get(_themeDisplay.getLocale(), "versions");
		}

		return LanguageUtil.get(_themeDisplay.getLocale(), "web-content");
	}

	public String getSelectDDMStructureURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest);

		DDMStructureItemSelectorCriterion ddmStructureItemSelectorCriterion =
			new DDMStructureItemSelectorCriterion();

		ddmStructureItemSelectorCriterion.setClassNameId(
			PortalUtil.getClassNameId(JournalArticle.class));
		ddmStructureItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new DDMStructureItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				_liferayPortletResponse.getNamespace() + "selectDDMStructure",
				ddmStructureItemSelectorCriterion));
	}

	public int getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			_httpServletRequest, "status", getDefaultStatus());

		return _status;
	}

	public String getTab() {
		if (Objects.equals(_getSearchIn(), "comments")) {
			return "comments";
		}

		return getType();
	}

	public String getTitle() throws PortalException {
		if (isFilterApplied() || isSearch()) {
			return LanguageUtil.get(_httpServletRequest, "search-results");
		}

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
			getHighlightedDDMStructureId());

		if (ddmStructure != null) {
			return ddmStructure.getName(_themeDisplay.getLocale());
		}

		ddmStructure = DDMStructureLocalServiceUtil.fetchStructure(
			getDDMStructureId());

		if (ddmStructure != null) {
			return ddmStructure.getName(_themeDisplay.getLocale());
		}

		return LanguageUtil.get(_httpServletRequest, "content-library");
	}

	public int getTotalItems() throws PortalException {
		SearchContainer<?> articleSearchContainer =
			_getArticlesSearchContainer();

		return articleSearchContainer.getTotal();
	}

	public String getType() {
		if (_type != null) {
			return _type;
		}

		_type = ParamUtil.getString(_httpServletRequest, "type", "web-content");

		return _type;
	}

	public int getVersionsTotal() throws PortalException {
		SearchContainer<JournalArticle> articleSearchContainer =
			_getVersionsSearchContainer();

		return articleSearchContainer.getTotal();
	}

	public VerticalNavItemList getVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			verticalNavItem -> {
				verticalNavItem.setActive(!isHighlightedDDMStructure());
				verticalNavItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).buildString());

				String name = LanguageUtil.get(
					_httpServletRequest, "content-library");

				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).build();
	}

	public boolean hasAdvancedUpdateDLFolderPermission()
		throws PortalException {

		if (getFolder() == null) {
			return true;
		}

		if (_advancedUpdateDLFolderPermission != null) {
			return _advancedUpdateDLFolderPermission;
		}

		_advancedUpdateDLFolderPermission = JournalFolderPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), getFolderId(),
			ActionKeys.ADVANCED_UPDATE);

		return _advancedUpdateDLFolderPermission;
	}

	public boolean hasAssetFilter() {
		if (ArrayUtil.isEmpty(_getAssetCategoryIds()) &&
			ArrayUtil.isEmpty(_getAssetTagNames())) {

			return false;
		}

		return true;
	}

	public boolean hasCommentsResults() throws PortalException {
		if (getCommentsTotal() > 0) {
			return true;
		}

		return false;
	}

	public boolean hasGuestViewPermission(JournalArticle journalArticle)
		throws PortalException {

		if (_guestRole == null) {
			_guestRole = _roleLocalService.getRole(
				journalArticle.getCompanyId(), RoleConstants.GUEST);
		}

		return _resourcePermissionLocalService.hasResourcePermission(
			journalArticle.getCompanyId(), JournalArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(journalArticle.getResourcePrimKey()),
			_guestRole.getRoleId(), ActionKeys.VIEW);
	}

	public boolean hasHighlightedDDMStructure() {
		return ListUtil.isNotEmpty(
			DDMStructureUtil.getHighlightedDDMStructures(_themeDisplay));
	}

	public boolean hasResults() throws PortalException {
		if (getTotalItems() > 0) {
			return true;
		}

		return false;
	}

	public boolean hasUpdateDLFolderPermission() throws PortalException {
		if (getFolder() == null) {
			return true;
		}

		if (_updateJournalFolderPermission != null) {
			return _updateJournalFolderPermission;
		}

		_updateJournalFolderPermission = JournalFolderPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), getFolderId(), ActionKeys.UPDATE);

		return _updateJournalFolderPermission;
	}

	public boolean hasVersionsResults() throws PortalException {
		if (getVersionsTotal() > 0) {
			return true;
		}

		return false;
	}

	public boolean isCommentsTabSelected() throws PortalException {
		return Objects.equals(getTab(), "comments");
	}

	public boolean isFilterApplied() {
		if ((getStatus() != WorkflowConstants.STATUS_ANY) ||
			isNavigationMine() || isNavigationRecent() || isTypeVersions()) {

			return true;
		}

		for (String parameterName : _PARAMETER_NAMES) {
			if (Validator.isNotNull(
					ParamUtil.getString(_httpServletRequest, parameterName))) {

				return true;
			}
		}

		return false;
	}

	public boolean isHighlightedDDMStructure() {
		if (getHighlightedDDMStructureId() > 0) {
			return true;
		}

		return false;
	}

	public boolean isIndexAllArticleVersions() {
		try {
			JournalServiceConfiguration journalServiceConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					_themeDisplay.getCompanyId());

			return journalServiceConfiguration.indexAllArticleVersionsEnabled();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	public boolean isNavigationHome() {
		return Objects.equals(getNavigation(), "all");
	}

	public boolean isNavigationMine() {
		if (_navigationMine != null) {
			return _navigationMine;
		}

		_navigationMine = ParamUtil.getBoolean(
			_httpServletRequest, "navigationMine");

		return _navigationMine;
	}

	public boolean isNavigationRecent() {
		if (_navigationRecent != null) {
			return _navigationRecent;
		}

		_navigationRecent = ParamUtil.getBoolean(
			_httpServletRequest, "navigationRecent");

		return _navigationRecent;
	}

	public boolean isNavigationStructure() {
		return Objects.equals(getNavigation(), "structure");
	}

	public boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	public boolean isShowBreadcrumb(JournalFolder journalFolder)
		throws PortalException {

		if (isSearch() && (journalFolder != null) &&
			((journalFolder.getFolderId() <= 0) ||
			 JournalFolderPermission.contains(
				 _themeDisplay.getPermissionChecker(), journalFolder,
				 ActionKeys.VIEW))) {

			return true;
		}

		return false;
	}

	public boolean isShowComments() throws PortalException {
		if (Objects.equals(_getSearchIn(), "comments")) {
			return true;
		}

		if ((isTypeWebContent() && !hasResults() && !hasVersionsResults() &&
			 hasCommentsResults()) ||
			(isTypeVersions() && !hasVersionsResults() &&
			 hasCommentsResults())) {

			_searchIn = "comments";

			return true;
		}

		return false;
	}

	public boolean isShowInfoButton() throws PortalException {
		if (hasAssetFilter() || isNavigationMine() || isNavigationRecent() ||
			isSearch() || isTypeVersions()) {

			return false;
		}

		return true;
	}

	public boolean isShowVersions() throws PortalException {
		if (isTypeVersions()) {
			return true;
		}

		if (isIndexAllArticleVersions() && isTypeWebContent() &&
			!hasResults() && hasVersionsResults()) {

			_type = "versions";

			return true;
		}

		return false;
	}

	public boolean isShowWebContent() throws PortalException {
		if (isTypeWebContent() && !isShowComments() && !isShowVersions()) {
			return true;
		}

		return false;
	}

	public boolean isTypeVersions() {
		return Objects.equals(getType(), "versions");
	}

	public boolean isTypeWebContent() {
		return Objects.equals(getType(), "web-content");
	}

	public boolean isVersionsTabSelected() throws PortalException {
		return Objects.equals(getTab(), "versions");
	}

	public boolean isWebContentTabSelected() throws PortalException {
		return Objects.equals(getTab(), "web-content");
	}

	private JournalDisplayContext(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		ItemSelector itemSelector, JournalHelper journalHelper,
		JournalWebConfiguration journalWebConfiguration,
		PortletRequest portletRequest, PortletResponse portletResponse,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService, TrashHelper trashHelper) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_itemSelector = itemSelector;
		_journalHelper = journalHelper;
		_journalWebConfiguration = journalWebConfiguration;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_trashHelper = trashHelper;

		_httpServletRequest = PortalUtil.getHttpServletRequest(portletRequest);
		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
			portletRequest);
		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			portletResponse);
		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			_httpServletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			_themeDisplay.getLocale(), _themeDisplay.getTimeZone());
	}

	private SearchContainer<Object> _getArticleAndFolderSearchContainer()
		throws PortalException {

		SearchContainer<Object> articleAndFolderSearchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, getPortletURL("web-content"), null,
				null);

		articleAndFolderSearchContainer.setOrderByCol(getOrderByCol());
		articleAndFolderSearchContainer.setOrderByComparator(
			_getFolderOrderByComparator());
		articleAndFolderSearchContainer.setOrderByType(getOrderByType());
		articleAndFolderSearchContainer.setRowChecker(_getEntriesChecker());

		if (!BrowserSnifferUtil.isMobile(_httpServletRequest)) {
			articleAndFolderSearchContainer.setRowMover(
				new EntriesMover(
					_trashHelper.isTrashEnabled(
						_themeDisplay.getScopeGroupId())));
		}

		return articleAndFolderSearchContainer;
	}

	private SearchContainer<JournalArticle> _getArticleSearchContainer()
		throws PortalException {

		SearchContainer<JournalArticle> articleSearchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, getPortletURL("web-content"), null,
				null);

		articleSearchContainer.setOrderByCol(getOrderByCol());
		articleSearchContainer.setOrderByComparator(
			JournalPortletUtil.getArticleOrderByComparator(
				getOrderByCol(), getOrderByType()));
		articleSearchContainer.setOrderByType(getOrderByType());
		articleSearchContainer.setRowChecker(_getEntriesChecker());

		if (!BrowserSnifferUtil.isMobile(_httpServletRequest)) {
			articleSearchContainer.setRowMover(
				new EntriesMover(
					_trashHelper.isTrashEnabled(
						_themeDisplay.getScopeGroupId())));
		}

		if (isNavigationRecent()) {
			articleSearchContainer.setOrderByCol("create-date");
		}

		return articleSearchContainer;
	}

	private SearchContainer<?> _getArticlesSearchContainer()
		throws PortalException {

		if (_articleSearchContainer != null) {
			return _articleSearchContainer;
		}

		if (!hasAssetFilter() && !isHighlightedDDMStructure() &&
			!isNavigationMine() && !isNavigationRecent() && !isSearch() &&
			!isTypeVersions() && (getDDMStructureId() <= 0) &&
			(getStatus() == WorkflowConstants.STATUS_ANY) &&
			CTCollectionThreadLocal.isProductionMode()) {

			SearchContainer<Object> articleAndFolderSearchContainer =
				_getArticleAndFolderSearchContainer();

			articleAndFolderSearchContainer.setResultsAndTotal(
				() -> JournalFolderServiceUtil.getFoldersAndArticles(
					_themeDisplay.getScopeGroupId(), 0, getFolderId(),
					WorkflowConstants.STATUS_ANY, _themeDisplay.getLocale(),
					articleAndFolderSearchContainer.getStart(),
					articleAndFolderSearchContainer.getEnd(),
					articleAndFolderSearchContainer.getOrderByComparator()),
				JournalFolderServiceUtil.getFoldersAndArticlesCount(
					_themeDisplay.getScopeGroupId(), 0, getFolderId(),
					WorkflowConstants.STATUS_ANY));

			_articleSearchContainer = articleAndFolderSearchContainer;

			return _articleSearchContainer;
		}

		if (!hasAssetFilter() &&
			(isHighlightedDDMStructure() || isNavigationStructure())) {

			SearchContainer<JournalArticle> articleSearchContainer =
				_getArticleSearchContainer();

			SearchResponse searchResponse =
				JournalSearcherUtil.searchJournalArticles(
					searchContext -> _populateSearchContext(
						articleSearchContainer.getStart(),
						articleSearchContainer.getEnd(), searchContext, false));

			articleSearchContainer.setResultsAndTotal(
				() -> JournalSearcherUtil.transformJournalArticles(
					searchResponse.getDocuments71()),
				searchResponse.getTotalHits());

			_articleSearchContainer = articleSearchContainer;

			return _articleSearchContainer;
		}

		List<Document> documents = new ArrayList<>();

		SearchContainer<Object> articleAndFolderSearchContainer =
			_getArticleAndFolderSearchContainer();

		int end = articleAndFolderSearchContainer.getEnd();
		int start = articleAndFolderSearchContainer.getStart();

		SearchResponse journalFolderSearchResponse =
			JournalSearcherUtil.searchJournalFolders(
				searchContext -> _populateSearchContext(
					start, end, searchContext, false));

		int foldersCount = journalFolderSearchResponse.getTotalHits();

		int journalArticlesStart = start - foldersCount;

		int delta = end - start;

		int journalArticlesEnd = delta + journalArticlesStart;

		if (start < foldersCount) {
			documents.addAll(journalFolderSearchResponse.getDocuments71());

			journalArticlesEnd = Math.max(1, delta - documents.size());

			journalArticlesStart = 0;
		}

		SearchResponse articleSearchResponse = _getJournalArticleSearchResponse(
			journalArticlesStart, journalArticlesEnd);

		if (documents.size() < delta) {
			documents.addAll(articleSearchResponse.getDocuments71());
		}

		articleAndFolderSearchContainer.setResultsAndTotal(
			() -> JournalSearcherUtil.transformJournalArticleAndFolders(
				documents),
			articleSearchResponse.getTotalHits() + foldersCount);

		_articleSearchContainer = articleAndFolderSearchContainer;

		return _articleSearchContainer;
	}

	private long[] _getAssetCategoryIds() {
		if (_assetCategoryIds != null) {
			return _assetCategoryIds;
		}

		_assetCategoryIds = ParamUtil.getLongValues(
			_httpServletRequest, "assetCategoryId");

		return _assetCategoryIds;
	}

	private Filter _getAssetCategoryIdsFilter() {
		BooleanFilter booleanFilter = new BooleanFilter();

		for (long assetCategoryId : _getAssetCategoryIds()) {
			booleanFilter.addTerm(
				Field.ASSET_CATEGORY_IDS, String.valueOf(assetCategoryId),
				BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private String[] _getAssetTagNames() {
		if (_assetTagNames != null) {
			return _assetTagNames;
		}

		_assetTagNames = ParamUtil.getStringValues(
			_httpServletRequest, "assetTagId");

		return _assetTagNames;
	}

	private Filter _getAssetTagNamesFilter() {
		BooleanFilter booleanFilter = new BooleanFilter();

		for (String assetTagName : _getAssetTagNames()) {
			booleanFilter.addTerm(
				Field.ASSET_TAG_NAMES + ".raw", assetTagName,
				BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private PortletURL _getBasePortletURL(String tab) {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		if (ArrayUtil.isNotEmpty(_getAssetCategoryIds())) {
			for (long assetCategoryId : _getAssetCategoryIds()) {
				portletURL.setParameter(
					"assetCategoryId", String.valueOf(assetCategoryId));
			}
		}

		if (ArrayUtil.isNotEmpty(_getAssetTagNames())) {
			for (String assetTagName : _getAssetTagNames()) {
				portletURL.setParameter("assetTagId", assetTagName);
			}
		}

		if (isNavigationStructure()) {
			portletURL.setParameter(
				"ddmStructureId", String.valueOf(getDDMStructureId()));
		}

		String delta = ParamUtil.getString(_httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			_httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", getDisplayStyle());
		}

		portletURL.setParameter("folderId", String.valueOf(getFolderId()));

		long highlightedDDMStructureId = getHighlightedDDMStructureId();

		if (highlightedDDMStructureId > 0) {
			portletURL.setParameter(
				"highlightedDDMStructureId",
				String.valueOf(highlightedDDMStructureId));
		}

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation");

		if (Validator.isNotNull(navigation)) {
			portletURL.setParameter(
				"navigation", HtmlUtil.escapeJS(getNavigation()));
		}

		portletURL.setParameter(
			"navigationMine", String.valueOf(isNavigationMine()));
		portletURL.setParameter(
			"navigationRecent", String.valueOf(isNavigationRecent()));

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		String status = ParamUtil.getString(_httpServletRequest, "status");

		if (Validator.isNotNull(status)) {
			portletURL.setParameter("status", String.valueOf(getStatus()));
		}

		if (Validator.isNotNull(tab)) {
			portletURL.setParameter("tab", tab);
		}

		portletURL.setParameter("type", getType());

		return portletURL;
	}

	private SearchContainer<MBMessage> _getCommentsSearchContainer()
		throws PortalException {

		SearchContainer<MBMessage> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, getPortletURL("comments"), null, null);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_liferayPortletRequest.getHttpServletRequest());

		searchContext.setAttribute(
			Field.CLASS_NAME_ID,
			PortalUtil.getClassNameId(JournalArticle.class));
		searchContext.setAttribute("discussion", Boolean.TRUE);
		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setStart(searchContainer.getStart());

		List<MBMessage> mbMessages = new ArrayList<>();

		Indexer<MBMessage> indexer = IndexerRegistryUtil.getIndexer(
			MBMessage.class);

		Hits hits = indexer.search(searchContext);

		for (Document document : hits.getDocs()) {
			mbMessages.add(
				MBMessageLocalServiceUtil.fetchMBMessage(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
		}

		searchContainer.setResultsAndTotal(() -> mbMessages, hits.getLength());

		return searchContainer;
	}

	private Filter _getCurrentFolderFilter() {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.addTerm(
			Field.ENTRY_CLASS_NAME, JournalFolder.class.getName(),
			BooleanClauseOccur.MUST);
		booleanFilter.addTerm(
			Field.ENTRY_CLASS_PK, String.valueOf(getFolderId()),
			BooleanClauseOccur.MUST);

		return booleanFilter;
	}

	private EntriesChecker _getEntriesChecker() {
		EntriesChecker entriesChecker = new EntriesChecker(
			_liferayPortletRequest, _liferayPortletResponse);

		entriesChecker.setCssClass("entry-selector");
		entriesChecker.setRememberCheckBoxStateURLRegex(
			StringBundler.concat(
				"^(?!.*", _liferayPortletResponse.getNamespace(),
				"redirect).*(folderId=", getFolderId(), ")"));

		return entriesChecker;
	}

	private String _getFeedsURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view_feeds.jsp"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).buildString();
	}

	private OrderByComparator<Object> _getFolderOrderByComparator() {
		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		if (Objects.equals(getOrderByCol(), "create-date")) {
			return FolderArticleCreateDateComparator.getInstance(orderByAsc);
		}
		else if (Objects.equals(getOrderByCol(), "display-date")) {
			return FolderArticleDisplayDateComparator.getInstance(orderByAsc);
		}
		else if (Objects.equals(getOrderByCol(), "id")) {
			return FolderArticleArticleIdComparator.getInstance(orderByAsc);
		}
		else if (Objects.equals(getOrderByCol(), "modified-date")) {
			return FolderArticleModifiedDateComparator.getInstance(orderByAsc);
		}
		else if (Objects.equals(getOrderByCol(), "title")) {
			return FolderArticleTitleComparator.getInstance(orderByAsc);
		}

		return null;
	}

	private JSONArray _getFoldersJSONArray(long groupId, long folderId) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<JournalFolder> folders = JournalFolderServiceUtil.getFolders(
			groupId, folderId);

		for (JournalFolder folder : folders) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			JSONArray childrenJSONArray = _getFoldersJSONArray(
				groupId, folder.getFolderId());

			if (childrenJSONArray.length() > 0) {
				jsonObject.put("children", childrenJSONArray);
			}

			jsonObject.put(
				"id", folder.getFolderId()
			).put(
				"name", folder.getName()
			);

			if (folder.getFolderId() == getParentFolderId()) {
				jsonObject.put("selected", true);
			}

			if (folder.getFolderId() == getFolderId()) {
				jsonObject.put("disabled", true);
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private SearchResponse _getJournalArticleSearchResponse(
		int start, int end) {

		return JournalSearcherUtil.searchJournalArticles(
			searchContext -> _populateSearchContext(
				start, end, searchContext, false));
	}

	private String _getSearchIn() {
		if (_searchIn != null) {
			return _searchIn;
		}

		_searchIn = ParamUtil.getString(
			_httpServletRequest, "searchIn", "all-fields");

		return _searchIn;
	}

	private PortletURL _getSearchInCommentsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setKeywords(
			() -> {
				String keywords = ParamUtil.getString(
					_httpServletRequest, "keywords");

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).buildPortletURL();
	}

	private JSONArray _getSearchInOptionsJSONArray() {
		return JSONUtil.putAll(
			JSONUtil.put(
				"label",
				LanguageUtil.get(_themeDisplay.getLocale(), "all-fields")
			).put(
				"value", "all-fields"
			)
		).put(
			JSONUtil.put(
				"label",
				LanguageUtil.get(_themeDisplay.getLocale(), "title-only")
			).put(
				"value", "title"
			)
		).put(
			JSONUtil.put(
				"label", LanguageUtil.get(_themeDisplay.getLocale(), "comments")
			).put(
				"value", "comments"
			)
		);
	}

	private String _getSearchLocation() {
		if (_searchLocation != null) {
			return _searchLocation;
		}

		_searchLocation = ParamUtil.getString(
			_httpServletRequest, "searchLocation", "current-folder");

		return _searchLocation;
	}

	private Sort _getSort() {
		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		if (Objects.equals(getOrderByCol(), "create-date")) {
			return new Sort("createDate", Sort.LONG_TYPE, !orderByAsc);
		}

		if (Objects.equals(getOrderByCol(), "display-date")) {
			return new Sort("displayDate", Sort.LONG_TYPE, !orderByAsc);
		}

		if (Objects.equals(getOrderByCol(), "id")) {
			return new Sort(
				Field.getSortableFieldName(Field.ARTICLE_ID), Sort.STRING_TYPE,
				!orderByAsc);
		}

		if (Objects.equals(getOrderByCol(), "modified-date")) {
			return new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
		}

		if (Objects.equals(getOrderByCol(), "relevance")) {
			return new Sort(null, Sort.SCORE_TYPE, false);
		}

		if (!Objects.equals(getOrderByCol(), "title")) {
			return null;
		}

		return new Sort(
			Field.getSortableFieldName(
				"localized_title_" + _themeDisplay.getLanguageId()),
			!orderByAsc);
	}

	private String _getSubtitle(
		Date date, String languageKey, String userName) {

		String dateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest, System.currentTimeMillis() - date.getTime(),
			true);

		return LanguageUtil.format(
			_httpServletRequest, languageKey,
			new String[] {dateDescription, HtmlUtil.escape(userName)});
	}

	private SearchContainer<JournalArticle> _getVersionsSearchContainer() {
		if (_articleVersionsSearchContainer != null) {
			return _articleVersionsSearchContainer;
		}

		SearchContainer<JournalArticle> articleVersionsSearchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, getPortletURL("versions"), null, null);

		articleVersionsSearchContainer.setOrderByCol(getOrderByCol());
		articleVersionsSearchContainer.setOrderByComparator(
			JournalPortletUtil.getArticleOrderByComparator(
				getOrderByCol(), getOrderByType()));
		articleVersionsSearchContainer.setOrderByType(getOrderByType());

		SearchResponse searchResponse =
			JournalSearcherUtil.searchJournalArticles(
				searchContext -> _populateSearchContext(
					articleVersionsSearchContainer.getStart(),
					articleVersionsSearchContainer.getEnd(), searchContext,
					true));

		articleVersionsSearchContainer.setResultsAndTotal(
			() -> JournalSearcherUtil.transformJournalArticles(
				searchResponse.getDocuments71()),
			searchResponse.getTotalHits());

		_articleVersionsSearchContainer = articleVersionsSearchContainer;

		return _articleVersionsSearchContainer;
	}

	private boolean _isSearchInAllFields() {
		return Objects.equals(_getSearchIn(), "all-fields");
	}

	private boolean _isSearchLocationCurrentFolder() {
		return Objects.equals(_getSearchLocation(), "current-folder");
	}

	private void _populateSearchContext(
		int start, int end, SearchContext searchContext, boolean showVersions) {

		searchContext.setAndSearch(false);

		Map<String, Serializable> attributes = searchContext.getAttributes();

		if (_isSearchInAllFields()) {
			attributes.put(Field.ARTICLE_ID, getKeywords());
			attributes.put(Field.CONTENT, getKeywords());
			attributes.put(Field.DESCRIPTION, getKeywords());
			attributes.put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"expandoAttributes", getKeywords()
				).put(
					"keywords", getKeywords()
				).build());
		}

		attributes.put(
			Field.CLASS_NAME_ID, JournalArticleConstants.CLASS_NAME_ID_DEFAULT);
		attributes.put(Field.STATUS, getStatus());
		attributes.put(Field.TITLE, getKeywords());

		if (isIndexAllArticleVersions()) {
			attributes.put("head", !showVersions);
			attributes.put("latest", !showVersions);
			attributes.put("showNonindexable", !showVersions);
		}
		else {
			attributes.put("head", false);
		}

		searchContext.setAttributes(attributes);

		_setBooleanClauses(searchContext);

		long ddmStructureId = ParamUtil.getLong(
			_httpServletRequest, "ddmStructureId");

		if (isHighlightedDDMStructure()) {
			searchContext.setClassTypeIds(
				new long[] {getHighlightedDDMStructureId()});
		}
		else if (ddmStructureId > 0) {
			searchContext.setClassTypeIds(new long[] {ddmStructureId});
		}

		searchContext.setCompanyId(_themeDisplay.getCompanyId());
		searchContext.setEnd(end);

		if (isHighlightedDDMStructure() || isNavigationStructure() ||
			isNavigationRecent()) {

			searchContext.setEntryClassNames(
				new String[] {JournalArticle.class.getName()});
		}

		if (!isHighlightedDDMStructure() && _isSearchLocationCurrentFolder()) {
			searchContext.setFolderIds(
				Collections.singletonList(getFolderId()));
		}

		searchContext.setGroupIds(new long[] {_themeDisplay.getScopeGroupId()});
		searchContext.setIncludeInternalAssetCategories(true);

		if (_isSearchInAllFields()) {
			searchContext.setKeywords(getKeywords());
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		Sort sort = _getSort();

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);
	}

	private void _setBooleanClauses(SearchContext searchContext) {
		BooleanFilter booleanFilter = new BooleanFilter();

		if (ArrayUtil.isNotEmpty(_getAssetCategoryIds())) {
			booleanFilter.add(
				_getAssetCategoryIdsFilter(), BooleanClauseOccur.MUST);
		}

		if (ArrayUtil.isNotEmpty(_getAssetTagNames())) {
			booleanFilter.add(
				_getAssetTagNamesFilter(), BooleanClauseOccur.MUST);
		}

		if (_isSearchLocationCurrentFolder()) {
			booleanFilter.add(
				_getCurrentFolderFilter(), BooleanClauseOccur.MUST_NOT);
		}

		if (!isHighlightedDDMStructure() && !isSearch()) {
			booleanFilter.addTerm(
				Field.FOLDER_ID, String.valueOf(getFolderId()),
				BooleanClauseOccur.MUST);
		}

		if ((isNavigationMine() || isNavigationRecent()) &&
			(_themeDisplay.getUserId() > 0)) {

			booleanFilter.addTerm(
				Field.USER_ID, String.valueOf(_themeDisplay.getUserId()),
				BooleanClauseOccur.MUST);
		}

		if (!booleanFilter.hasClauses()) {
			return;
		}

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		booleanQuery.setPreBooleanFilter(booleanFilter);

		searchContext.setBooleanClauses(
			new BooleanClause[] {
				BooleanClauseFactoryUtil.create(
					booleanQuery, BooleanClauseOccur.MUST.getName())
			});
	}

	private static final String[] _PARAMETER_NAMES = {
		"assetCategoryId", "assetTagId", "ddmStructureId"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		JournalDisplayContext.class);

	private long[] _addMenuFavItems;
	private Boolean _advancedUpdateDLFolderPermission;
	private JournalArticle _article;
	private JournalArticleDisplay _articleDisplay;
	private SearchContainer<?> _articleSearchContainer;
	private SearchContainer<JournalArticle> _articleVersionsSearchContainer;
	private long[] _assetCategoryIds;
	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private String[] _assetTagNames;
	private final Format _dateTimeFormat;
	private Long _ddmStructureId;
	private String _ddmStructureName;
	private List<DDMStructure> _ddmStructures;
	private String _displayStyle;
	private JournalFolder _folder;
	private Long _folderId;
	private Role _guestRole;
	private Long _highlightedDDMStructureId;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final JournalHelper _journalHelper;
	private final JournalWebConfiguration _journalWebConfiguration;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _navigation;
	private Boolean _navigationMine;
	private Boolean _navigationRecent;
	private String _orderByCol;
	private String _orderByType;
	private Long _parentFolderId;
	private final PortalPreferences _portalPreferences;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private Integer _restrictionType;
	private final RoleLocalService _roleLocalService;
	private SearchContainer<?> _searchContainer;
	private String _searchIn;
	private String _searchLocation;
	private Integer _status;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;
	private String _type;
	private Boolean _updateJournalFolderPermission;

}