/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.configuration.DLFileOrderConfigurationProvider;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.document.library.web.internal.util.DLFolderUtil;
import com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.RelatedSearchResult;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.util.PropsValues;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class DLAdminDisplayContext {

	public DLAdminDisplayContext(
		AssetAutoTaggerConfiguration assetAutoTaggerConfiguration,
		ConfigurationProvider configurationProvider,
		DLFileOrderConfigurationProvider dlFileOrderConfigurationProvider,
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, TrashHelper trashHelper,
		VersioningStrategy versioningStrategy) {

		_assetAutoTaggerConfiguration = assetAutoTaggerConfiguration;
		_configurationProvider = configurationProvider;
		_dlFileOrderConfigurationProvider = dlFileOrderConfigurationProvider;
		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_trashHelper = trashHelper;
		_versioningStrategy = versioningStrategy;

		_dlRequestHelper = new DLRequestHelper(_httpServletRequest);

		_dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();
		_dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(
			_dlRequestHelper);

		_httpSession = httpServletRequest.getSession();
		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_permissionChecker = _themeDisplay.getPermissionChecker();

		_computeFolders();
	}

	public long[] getAssetCategoryIds() {
		if (_assetCategoryIds == null) {
			_assetCategoryIds = ParamUtil.getLongValues(
				_httpServletRequest, "assetCategoryId");
		}

		return _assetCategoryIds;
	}

	public String[] getAssetTagIds() {
		if (_assetTagIds == null) {
			_assetTagIds = ParamUtil.getStringValues(
				_httpServletRequest, "assetTagId");
		}

		return _assetTagIds;
	}

	public PortletURL getCurrentRenderURL() {
		if (isSearch()) {
			return getSearchRenderURL();
		}

		return getViewRenderURL();
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = _getDisplayStyle(PropsValues.DL_DEFAULT_DISPLAY_VIEW);

		return _displayStyle;
	}

	public String[] getExtensions() {
		if (_extensions == null) {
			_extensions = ParamUtil.getStringValues(
				_httpServletRequest, "extension");
		}

		return _extensions;
	}

	public long getFileEntryTypeId() {
		if (_fileEntryTypeId == null) {
			_fileEntryTypeId = ParamUtil.getLong(
				_httpServletRequest, "fileEntryTypeId", -1);
		}

		return _fileEntryTypeId;
	}

	public Folder getFolder() {
		return _folder;
	}

	public long getFolderId() {
		return _folderId;
	}

	public String getMimeTypeMessageKey() throws PortalException {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		if (StringUtil.equals(
				portletDisplay.getPortletName(),
				DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {

			return "media-files-must-be-one-of-the-following-formats-x";
		}

		return "please-enter-a-file-with-a-valid-mime-type-x";
	}

	public String getMimeTypes() throws PortalException {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		if (StringUtil.equals(
				portletDisplay.getPortletName(),
				DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {

			DLPortletInstanceSettings dlPortletInstanceSettings =
				_dlRequestHelper.getDLPortletInstanceSettings();

			return StringUtil.merge(
				dlPortletInstanceSettings.getMimeTypes(),
				StringPool.COMMA_AND_SPACE);
		}

		DLFileEntryMimeTypeConfiguration dlFileEntryMimeTypeConfiguration =
			_configurationProvider.getCompanyConfiguration(
				DLFileEntryMimeTypeConfiguration.class,
				_themeDisplay.getCompanyId());

		return StringUtil.merge(
			dlFileEntryMimeTypeConfiguration.fileMimeTypes(),
			StringPool.COMMA_AND_SPACE);
	}

	public List<Folder> getMountFolders() throws PortalException {
		if (_mountFolders == null) {
			_mountFolders = DLAppServiceUtil.getMountFolders(
				_themeDisplay.getScopeGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		return _mountFolders;
	}

	public String getNavigation() {
		if (_navigation == null) {
			_navigation = ParamUtil.getString(
				_httpServletRequest, "navigation", "home");
		}

		return _navigation;
	}

	public String getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		if (isNavigationRecent()) {
			return "modifiedDate";
		}

		String orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol");

		if (Objects.equals(orderByCol, "relevance")) {
			return "relevance";
		}

		if (orderByCol.equals("downloads") && (getFileEntryTypeId() >= 0)) {
			orderByCol = "modifiedDate";
		}

		if (Validator.isNull(orderByCol)) {
			orderByCol = _getPortletPreference(
				"order-by-col",
				_dlFileOrderConfigurationProvider.getGroupOrderByColumn(
					_themeDisplay.getScopeGroupId()));
		}
		else {
			_setPortletPreference("order-by-col", orderByCol);
		}

		_orderByCol = orderByCol;

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		if (isNavigationRecent()) {
			return "desc";
		}

		if (Objects.equals(getOrderByCol(), "relevance")) {
			return "asc";
		}

		String orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType");

		if (Validator.isNull(orderByType)) {
			orderByType = _getPortletPreference(
				"order-by-type",
				_dlFileOrderConfigurationProvider.getGroupSortBy(
					_themeDisplay.getScopeGroupId()));
		}
		else {
			_setPortletPreference("order-by-type", orderByType);
		}

		_orderByType = orderByType;

		return _orderByType;
	}

	public String getRememberCheckBoxStateURLRegex() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		if (!DLPortletKeys.DOCUMENT_LIBRARY.equals(
				portletDisplay.getRootPortletId())) {

			return StringBundler.concat(
				"^(?!.*", portletDisplay.getNamespace(),
				"redirect).*(folderId=", _folderId, ")");
		}

		if (_folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return "^[^?]+/" + portletDisplay.getInstanceId() + "\\?";
		}

		return StringBundler.concat(
			"^[^?]+/", portletDisplay.getInstanceId(), "/view/", _folderId,
			"\\?");
	}

	public long getRepositoryGroupId(long scopeGroupId, long repositoryId) {
		Repository repository = RepositoryLocalServiceUtil.fetchRepository(
			repositoryId);

		if (repository != null) {
			return repository.getGroupId();
		}

		Group group = GroupLocalServiceUtil.fetchGroup(repositoryId);

		if (group != null) {
			return group.getGroupId();
		}

		return scopeGroupId;
	}

	public long getRepositoryId() {
		if (_repositoryId != 0) {
			return _repositoryId;
		}

		long repositoryId = 0;

		Folder folder = getFolder();

		if (folder != null) {
			repositoryId = folder.getRepositoryId();
		}
		else {
			repositoryId =
				_dlPortletInstanceSettingsHelper.getSelectedRepositoryId();
		}

		if (repositoryId == 0) {
			repositoryId = ParamUtil.getLong(
				_httpServletRequest, "repositoryId",
				_themeDisplay.getScopeGroupId());
		}

		_repositoryId = repositoryId;

		return _repositoryId;
	}

	public long getRootFolderId() {
		return _rootFolderId;
	}

	public String getRootFolderName() {
		return _rootFolderName;
	}

	public SearchContainer<RepositoryEntry> getSearchContainer() {
		if (_searchContainer == null) {
			try {
				if (isSearch()) {
					_searchContainer = _getSearchSearchContainer();
				}
				else {
					_searchContainer = _getDLSearchContainer();
				}
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		return _searchContainer;
	}

	public String getSearchDisplayStyle() {
		return _getDisplayStyle("descriptive");
	}

	public PortletURL getSearchRenderURL() {
		PortletURL renderURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/document_library/search"
		).setParameter(
			"folderId", ParamUtil.getLong(_httpServletRequest, "folderId")
		).buildPortletURL();

		_setFilterParameters(renderURL);

		_setSearchParameters(renderURL);

		return renderURL;
	}

	public long getSelectedRepositoryId() {
		if (_selectedRepositoryId != 0) {
			return _selectedRepositoryId;
		}

		long repositoryId =
			_dlPortletInstanceSettingsHelper.getSelectedRepositoryId();

		if (repositoryId != 0) {
			_selectedRepositoryId = repositoryId;

			return _selectedRepositoryId;
		}

		_selectedRepositoryId = getRepositoryId();

		return _selectedRepositoryId;
	}

	public String getSelectRootFolderURL() throws PortalException {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		FolderItemSelectorURLProvider folderItemSelectorURLProvider =
			new FolderItemSelectorURLProvider(
				_httpServletRequest, itemSelector);

		return folderItemSelectorURLProvider.getSelectRootFolderURL(
			getSelectedRepositoryId(), getRootFolderId());
	}

	public PortletURL getViewRenderURL() {
		PortletURL renderURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			_getViewMvcRenderCommandName()
		).setParameter(
			"folderId", getFolderId()
		).buildPortletURL();

		_setFilterParameters(renderURL);

		return renderURL;
	}

	public boolean hasFilterParameters() {
		if (ArrayUtil.isNotEmpty(getAssetCategoryIds()) ||
			(getFileEntryTypeId() >= 0) ||
			ArrayUtil.isNotEmpty(getAssetTagIds()) ||
			ArrayUtil.isNotEmpty(getExtensions()) || isNavigationMine() ||
			isNavigationRecent()) {

			return true;
		}

		return false;
	}

	public boolean isAutoTaggingEnabled() {
		return _assetAutoTaggerConfiguration.isEnabled();
	}

	public boolean isDefaultFolderView() {
		return _defaultFolderView;
	}

	public boolean isNavigationHome() {
		return Objects.equals(getNavigation(), "home");
	}

	public boolean isNavigationMine() {
		return Objects.equals(getNavigation(), "mine");
	}

	public boolean isNavigationRecent() {
		return Objects.equals(getNavigation(), "recent");
	}

	public boolean isRootFolderInTrash() {
		return _rootFolderInTrash;
	}

	public boolean isRootFolderNotFound() {
		return _rootFolderNotFound;
	}

	public boolean isSearch() {
		return !Validator.isBlank(_getKeywords());
	}

	public boolean isUpdateAutoTags() {
		return _assetAutoTaggerConfiguration.isUpdateAutoTags();
	}

	public boolean isVersioningStrategyOverridable() {
		return _versioningStrategy.isOverridable();
	}

	private void _computeFolders() {
		try {
			_computeRootFolder();

			_folder = (Folder)_httpServletRequest.getAttribute(
				WebKeys.DOCUMENT_LIBRARY_FOLDER);

			if (_folder == null) {
				_folderId = getRootFolderId();
			}
			else {
				_folderId = _folder.getFolderId();
			}

			_defaultFolderView = false;

			if ((_folder == null) &&
				(_folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

				_defaultFolderView = true;
			}

			if (_defaultFolderView) {
				try {
					_folder = DLAppLocalServiceUtil.getFolder(_folderId);
				}
				catch (NoSuchFolderException noSuchFolderException) {
					_folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to get folder " + _folderId,
							noSuchFolderException);
					}
				}
			}

			if ((_folder != null) && (_folderId != _rootFolderId) &&
				!_isRepositoryRoot()) {

				List<Long> ancestorFolderIds = _folder.getAncestorFolderIds();

				if (!ancestorFolderIds.contains(_rootFolderId)) {
					throw new NoSuchFolderException();
				}
			}
		}
		catch (PortalException portalException) {
			ReflectionUtil.throwException(portalException);
		}
	}

	private void _computeRootFolder() {
		_rootFolderName = StringPool.BLANK;

		try {
			_rootFolder = _dlPortletInstanceSettingsHelper.getRootFolder();

			if (_rootFolder == null) {
				_rootFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
				_rootFolderName = LanguageUtil.get(_httpServletRequest, "home");

				return;
			}

			_rootFolderId = _rootFolder.getFolderId();

			_rootFolderName = _rootFolder.getName();

			if (_rootFolder.isRepositoryCapabilityProvided(
					TrashCapability.class)) {

				TrashCapability trashCapability =
					_rootFolder.getRepositoryCapability(TrashCapability.class);

				_rootFolderInTrash = trashCapability.isInTrash(_rootFolder);

				if (_rootFolderInTrash) {
					_rootFolderName = _trashHelper.getOriginalTitle(
						_rootFolder.getName());
				}
			}

			DLFolderUtil.validateDepotFolder(
				_rootFolderId, _rootFolder.getGroupId(),
				_themeDisplay.getScopeGroupId());
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Could not find folder {folderExternalReferenceCode=",
						_dlPortletInstanceSettings.
							getRootFolderExternalReferenceCode(),
						", groupExternalReferenceCode=",
						_dlPortletInstanceSettings.
							getSelectedGroupExternalReferenceCode(),
						"}"),
					noSuchFolderException);
			}

			_rootFolderNotFound = true;
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private Filter _getAssetCategoryIdsFilter(long[] assetCategoryIds) {
		if (ArrayUtil.isEmpty(assetCategoryIds)) {
			return null;
		}

		BooleanFilter booleanFilter = new BooleanFilter();

		for (long assetCategoryId : assetCategoryIds) {
			booleanFilter.addTerm(
				Field.ASSET_CATEGORY_IDS, String.valueOf(assetCategoryId),
				BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private Filter _getAssetTagNamesFilter(String[] assetTagNames) {
		if (ArrayUtil.isEmpty(assetTagNames)) {
			return null;
		}

		BooleanFilter booleanFilter = new BooleanFilter();

		for (String assetTagName : assetTagNames) {
			booleanFilter.addTerm(
				Field.ASSET_TAG_NAMES + ".raw", assetTagName,
				BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private BooleanClause<Query>[] _getBooleanClauses(
		long[] assetCategoryIds, String[] assetTagNames, String[] extensions,
		long fileEntryTypeId, long userId) {

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		BooleanFilter booleanFilter = new BooleanFilter();

		if (ArrayUtil.isNotEmpty(assetCategoryIds)) {
			booleanFilter.add(
				_getAssetCategoryIdsFilter(assetCategoryIds),
				BooleanClauseOccur.MUST);
		}

		if (ArrayUtil.isNotEmpty(assetTagNames)) {
			booleanFilter.add(
				_getAssetTagNamesFilter(assetTagNames),
				BooleanClauseOccur.MUST);
		}

		if (ArrayUtil.isNotEmpty(extensions)) {
			booleanFilter.add(
				_getExtensionsFilter(extensions), BooleanClauseOccur.MUST);
		}

		if (fileEntryTypeId >= 0) {
			booleanFilter.addTerm(
				"fileEntryTypeId", String.valueOf(fileEntryTypeId),
				BooleanClauseOccur.MUST);
		}

		if (userId > 0) {
			booleanFilter.addTerm(
				Field.USER_ID, String.valueOf(userId), BooleanClauseOccur.MUST);
		}

		booleanQuery.setPreBooleanFilter(booleanFilter);

		return new BooleanClause[] {
			BooleanClauseFactoryUtil.create(
				booleanQuery, BooleanClauseOccur.MUST.getName())
		};
	}

	private String _getDisplayStyle(String defaultValue) {
		String displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		String[] displayViews = _dlPortletInstanceSettings.getDisplayViews();

		if (Validator.isNull(displayStyle)) {
			displayStyle = _getPortletPreference("display-style", defaultValue);
		}
		else {
			if (ArrayUtil.contains(displayViews, displayStyle)) {
				_setPortletPreference("display-style", displayStyle);

				_httpServletRequest.setAttribute(
					WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
			}
		}

		if (!ArrayUtil.contains(displayViews, displayStyle)) {
			displayStyle = displayViews[0];
		}

		return displayStyle;
	}

	private SearchContainer<RepositoryEntry> _getDLSearchContainer()
		throws PortalException {

		SearchContainer<RepositoryEntry> dlSearchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, null, null, "curEntry",
				_dlPortletInstanceSettings.getEntriesPerPage(),
				getViewRenderURL(), null, _getFilterEmptyResultsMessage());

		dlSearchContainer.setHeaderNames(
			ListUtil.fromArray(
				_dlPortletInstanceSettingsHelper.getEntryColumns()));
		dlSearchContainer.setOrderByCol(getOrderByCol());
		dlSearchContainer.setOrderByType(getOrderByType());

		long repositoryId = getRepositoryId();

		if (hasFilterParameters()) {
			SearchContext searchContext = _getSearchContext(
				dlSearchContainer, "none");

			Repository repository = RepositoryLocalServiceUtil.fetchRepository(
				repositoryId);

			if ((repository == null) &&
				(_themeDisplay.getScopeGroupId() != repositoryId) &&
				ArrayUtil.contains(
					SiteConnectedGroupGroupProviderUtil.
						getCurrentAndAncestorSiteAndDepotGroupIds(
							_themeDisplay.getScopeGroupId()),
					repositoryId)) {

				searchContext.setGroupIds(new long[] {repositoryId});
			}

			_initializeFilterSearchContext(searchContext);

			Indexer<?> indexer = IndexerRegistryUtil.getIndexer(
				DLFileEntryConstants.getClassName());

			Hits hits = indexer.search(searchContext);

			dlSearchContainer.setResultsAndTotal(
				() -> _getRepositoryEntries(hits), hits.getLength());

			return dlSearchContainer;
		}

		dlSearchContainer.setOrderByComparator(
			DLUtil.getRepositoryModelOrderByComparator(
				getOrderByCol(), getOrderByType(), true));

		long folderId = getFolderId();

		long categoryId = ParamUtil.getLong(_httpServletRequest, "categoryId");
		String tagName = ParamUtil.getString(_httpServletRequest, "tag");

		if ((categoryId > 0) || Validator.isNotNull(tagName)) {
			long[] classNameIds = {
				PortalUtil.getClassNameId(DLFileEntryConstants.getClassName()),
				PortalUtil.getClassNameId(
					DLFileShortcutConstants.getClassName())
			};

			AssetEntryQuery assetEntryQuery = new AssetEntryQuery(
				classNameIds, dlSearchContainer);

			assetEntryQuery.setEnablePermissions(true);
			assetEntryQuery.setExcludeZeroViewCount(false);

			List<RepositoryEntry> results = new ArrayList<>();

			for (AssetEntry assetEntry :
					AssetEntryServiceUtil.getEntries(assetEntryQuery)) {

				if (Objects.equals(
						assetEntry.getClassName(),
						DLFileEntryConstants.getClassName())) {

					FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
						assetEntry.getClassPK());

					if (_isAncestorFolder(folderId, fileEntry) ||
						((folderId ==
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) &&
						 (fileEntry.getRepositoryId() == repositoryId))) {

						results.add(fileEntry);
					}
				}
				else {
					results.add(
						DLAppLocalServiceUtil.getFileShortcut(
							assetEntry.getClassPK()));
				}
			}

			dlSearchContainer.setResultsAndTotal(
				() -> results,
				AssetEntryServiceUtil.getEntriesCount(assetEntryQuery));
		}
		else {
			int dlAppStatus = _getStatus();

			dlSearchContainer.setResultsAndTotal(
				() ->
					(List)
						DLAppServiceUtil.
							getFoldersAndFileEntriesAndFileShortcuts(
								repositoryId, folderId, dlAppStatus, true,
								dlSearchContainer.getStart(),
								dlSearchContainer.getEnd(),
								dlSearchContainer.getOrderByComparator()),
				DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcutsCount(
					repositoryId, folderId, dlAppStatus, true));
		}

		return dlSearchContainer;
	}

	private Filter _getExtensionsFilter(String[] extensions) {
		if (ArrayUtil.isEmpty(extensions)) {
			return null;
		}

		TermsFilter termsFilter = new TermsFilter("extension");

		for (String extension : extensions) {
			termsFilter.addValue(extension);
		}

		return termsFilter;
	}

	private String _getFilterEmptyResultsMessage() throws PortalException {
		long fileEntryTypeId = getFileEntryTypeId();

		if (fileEntryTypeId < 0) {
			return "there-are-no-documents-or-media-files-in-this-folder";
		}

		String dlFileEntryTypeName = LanguageUtil.get(
			_httpServletRequest, "basic-document");

		if (fileEntryTypeId > 0) {
			DLFileEntryType dlFileEntryType =
				DLFileEntryTypeLocalServiceUtil.getFileEntryType(
					fileEntryTypeId);

			dlFileEntryTypeName = dlFileEntryType.getName(
				_themeDisplay.getLocale());
		}

		return LanguageUtil.format(
			_httpServletRequest,
			"there-are-no-documents-or-media-files-of-type-x",
			HtmlUtil.escape(dlFileEntryTypeName), false);
	}

	private Hits _getHits(SearchContainer<RepositoryEntry> searchContainer)
		throws PortalException {

		SearchContext searchContext = _getSearchContext(
			searchContainer, "regular");

		_initializeFilterSearchContext(searchContext);

		long searchRepositoryId = _getSearchRepositoryId();

		searchContext.setAttribute("searchRepositoryId", searchRepositoryId);

		searchContext.setFolderIds(new long[] {_getSearchFolderId()});

		Group group = GroupLocalServiceUtil.fetchGroup(searchRepositoryId);

		if ((group != null) &&
			GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), group, ActionKeys.VIEW)) {

			searchContext.setGroupIds(new long[] {searchRepositoryId});
		}

		searchContext.setIncludeDiscussions(true);
		searchContext.setIncludeInternalAssetCategories(true);
		searchContext.setKeywords(_getKeywords());
		searchContext.setLocale(_themeDisplay.getSiteDefaultLocale());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSearchSubfolders(true);

		return DLAppServiceUtil.search(searchRepositoryId, searchContext);
	}

	private String _getKeywords() {
		if (_keywords == null) {
			_keywords = ParamUtil.getString(_httpServletRequest, "keywords");
		}

		return _keywords;
	}

	private String _getPortletPreference(String name, String defaultValue) {
		if (_themeDisplay.isSignedIn()) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				PortletPreferences portletPreferences =
					_getPortletPreferences();

				return portletPreferences.getValue(name, defaultValue);
			}
		}

		return GetterUtil.getString(
			_httpSession.getAttribute(
				_dlRequestHelper.getPortletId() + StringPool.UNDERLINE + name),
			defaultValue);
	}

	private PortletPreferences _getPortletPreferences() {
		if (_portletPreferences != null) {
			return _portletPreferences;
		}

		_portletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				_themeDisplay.getCompanyId(), _themeDisplay.getUserId(),
				PortletKeys.PREFS_OWNER_TYPE_USER, _themeDisplay.getPlid(),
				_dlRequestHelper.getPortletId(), StringPool.BLANK);

		return _portletPreferences;
	}

	private List<RepositoryEntry> _getRepositoryEntries(Hits hits) {
		List<RepositoryEntry> results = new ArrayList<>();

		for (Document doc : hits.getDocs()) {
			long fileEntryId = GetterUtil.getLong(
				doc.get(Field.ENTRY_CLASS_PK));

			FileEntry fileEntry = null;

			try {
				fileEntry = DLAppLocalServiceUtil.getFileEntry(fileEntryId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Documents and Media search index is stale and ",
							"contains file entry ", fileEntryId),
						exception);
				}

				continue;
			}

			results.add(fileEntry);
		}

		return results;
	}

	private SearchContext _getSearchContext(
		SearchContainer<RepositoryEntry> searchContainer,
		String paginationType) {

		SearchContext searchContext = SearchContextFactory.getInstance(
			new long[0], new String[0], new HashMap<>(),
			_themeDisplay.getCompanyId(), null, _themeDisplay.getLayout(),
			_themeDisplay.getLocale(), _themeDisplay.getScopeGroupId(),
			_themeDisplay.getTimeZone(), _themeDisplay.getUserId());

		searchContext.setAttribute("paginationType", paginationType);
		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setSorts(
			_getSort(
				searchContainer.getOrderByCol(),
				searchContainer.getOrderByType()));

		searchContext.setStart(searchContainer.getStart());

		return searchContext;
	}

	private String _getSearchEmptyResultsMessage() {
		String message = "no-documents-were-found-that-matched-the-keywords-x";

		if (hasFilterParameters()) {
			if (_isExternalRepositorySearch()) {
				return "there-are-no-documents-or-media-files-that-matched-" +
					"the-filters-in-this-repository";
			}

			message =
				"no-documents-were-found-that-matched-the-filters-and-the-" +
					"keywords-x";
		}

		return LanguageUtil.format(
			_httpServletRequest, message, HtmlUtil.escape(_getKeywords()),
			false);
	}

	private long _getSearchFolderId() {
		if (_searchFolderId == null) {
			_searchFolderId = ParamUtil.getLong(
				_httpServletRequest, "searchFolderId",
				ParamUtil.getLong(_httpServletRequest, "folderId"));

			if ((_rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) &&
				(_searchFolderId ==
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

				_searchFolderId = _rootFolderId;
			}
		}

		return _searchFolderId;
	}

	private long _getSearchRepositoryId() {
		if (_searchRepositoryId == null) {
			_searchRepositoryId = ParamUtil.getLong(
				_httpServletRequest, "searchRepositoryId", getRepositoryId());
		}

		return _searchRepositoryId;
	}

	private List<RepositoryEntry> _getSearchResults(Hits hits)
		throws PortalException {

		List<RepositoryEntry> searchResults = new ArrayList<>();

		for (SearchResult searchResult :
				SearchResultUtil.getSearchResults(
					hits, _httpServletRequest.getLocale())) {

			String className = searchResult.getClassName();

			try {
				List<RelatedSearchResult<FileEntry>>
					fileEntryRelatedSearchResults =
						searchResult.getFileEntryRelatedSearchResults();

				if (!fileEntryRelatedSearchResults.isEmpty()) {
					fileEntryRelatedSearchResults.forEach(
						fileEntryRelatedSearchResult -> searchResults.add(
							fileEntryRelatedSearchResult.getModel()));
				}
				else if (className.equals(DLFileEntry.class.getName()) ||
						 FileEntry.class.isAssignableFrom(
							 Class.forName(className))) {

					searchResults.add(
						DLAppLocalServiceUtil.getFileEntry(
							searchResult.getClassPK()));
				}
				else if (className.equals(DLFolder.class.getName()) ||
						 className.equals(Folder.class.getName())) {

					searchResults.add(
						DLAppLocalServiceUtil.getFolder(
							searchResult.getClassPK()));
				}
			}
			catch (ClassNotFoundException classNotFoundException) {
				throw new PortalException(classNotFoundException);
			}
		}

		return searchResults;
	}

	private SearchContainer<RepositoryEntry> _getSearchSearchContainer()
		throws PortalException {

		SearchContainer<RepositoryEntry> searchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, getSearchRenderURL(), null,
				_getSearchEmptyResultsMessage());

		if (_isExternalRepositorySearch() && hasFilterParameters()) {
			return searchContainer;
		}

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());

		Hits hits = _getHits(searchContainer);

		searchContainer.setResultsAndTotal(
			() -> _getSearchResults(hits), hits.getLength());

		return searchContainer;
	}

	private Sort _getSort(String orderByCol, String orderByType) {
		int type = Sort.STRING_TYPE;
		String fieldName = orderByCol;

		if (Objects.equals(orderByCol, "creationDate")) {
			fieldName = Field.CREATE_DATE;
			type = Sort.LONG_TYPE;
		}
		else if (Objects.equals(orderByCol, "modifiedDate")) {
			fieldName = Field.MODIFIED_DATE;
			type = Sort.LONG_TYPE;
		}
		else if (Objects.equals(orderByCol, "relevance")) {
			type = Sort.SCORE_TYPE;
		}
		else if (Objects.equals(orderByCol, "size")) {
			type = Sort.LONG_TYPE;
		}

		return SortFactoryUtil.create(
			fieldName, type, !StringUtil.equalsIgnoreCase(orderByType, "asc"));
	}

	private int _getStatus() {
		int status = WorkflowConstants.STATUS_APPROVED;

		User user = _themeDisplay.getUser();

		if (_permissionChecker.isContentReviewer(
				user.getCompanyId(), _themeDisplay.getScopeGroupId())) {

			status = WorkflowConstants.STATUS_ANY;
		}

		return status;
	}

	private String _getViewMvcRenderCommandName() {
		if (getFolderId() == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return "/document_library/view";
		}

		return "/document_library/view_folder";
	}

	private void _initializeFilterSearchContext(SearchContext searchContext) {
		int status = _getStatus();
		long userId = 0;

		if (isNavigationMine() && _themeDisplay.isSignedIn()) {
			status = WorkflowConstants.STATUS_ANY;
			userId = _themeDisplay.getUserId();
		}

		searchContext.setAttribute("status", status);
		searchContext.setBooleanClauses(
			_getBooleanClauses(
				getAssetCategoryIds(), getAssetTagIds(), getExtensions(),
				getFileEntryTypeId(), userId));

		long folderId = ParamUtil.getLong(
			_httpServletRequest, "folderId", getFolderId());

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			searchContext.setFolderIds(new long[] {folderId});
		}
	}

	private boolean _isAncestorFolder(long folderId, FileEntry fileEntry) {
		LiferayFileEntry liferayFileEntry = (LiferayFileEntry)fileEntry;

		DLFileEntry dlFileEntry = liferayFileEntry.getDLFileEntry();

		List<String> treePaths = Arrays.asList(
			StringUtil.split(
				dlFileEntry.getTreePath(), CharPool.FORWARD_SLASH));

		return treePaths.contains(String.valueOf(folderId));
	}

	private boolean _isExternalRepositorySearch() {
		if (_getSearchRepositoryId() != _themeDisplay.getScopeGroupId()) {
			return true;
		}

		return false;
	}

	private boolean _isRepositoryRoot() {
		if ((_rootFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ||
			((_rootFolder != null) && _rootFolder.isRoot())) {

			return true;
		}

		return false;
	}

	private void _setFilterParameters(PortletURL portletURL) {
		portletURL.setParameter(
			"assetCategoryId", ArrayUtil.toStringArray(getAssetCategoryIds()));
		portletURL.setParameter(
			"assetTagId", ArrayUtil.toStringArray(getAssetTagIds()));
		portletURL.setParameter("extension", getExtensions());

		long fileEntryTypeId = getFileEntryTypeId();

		if (fileEntryTypeId != -1) {
			portletURL.setParameter(
				"fileEntryTypeId", String.valueOf(fileEntryTypeId));
		}

		portletURL.setParameter(
			"navigation", HtmlUtil.escapeJS(getNavigation()));
	}

	private void _setPortletPreference(String name, String value) {
		if (_themeDisplay.isSignedIn()) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				PortletPreferences portletPreferences =
					_getPortletPreferences();

				try {
					portletPreferences.setValue(name, value);

					portletPreferences.store();
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception);
					}
				}
			}
		}
		else {
			_httpSession.setAttribute(
				_dlRequestHelper.getPortletId() + StringPool.UNDERLINE + name,
				value);
		}
	}

	private void _setSearchParameters(PortletURL portletURL) {
		portletURL.setParameter("keywords", _getKeywords());
		portletURL.setParameter(
			"repositoryId", String.valueOf(getRepositoryId()));
		portletURL.setParameter(
			"searchFolderId", String.valueOf(_getSearchFolderId()));
		portletURL.setParameter(
			"searchRepositoryId", String.valueOf(_getSearchRepositoryId()));
		portletURL.setParameter("showSearchInfo", Boolean.TRUE.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLAdminDisplayContext.class);

	private final AssetAutoTaggerConfiguration _assetAutoTaggerConfiguration;
	private long[] _assetCategoryIds;
	private String[] _assetTagIds;
	private final ConfigurationProvider _configurationProvider;
	private boolean _defaultFolderView;
	private String _displayStyle;
	private final DLFileOrderConfigurationProvider
		_dlFileOrderConfigurationProvider;
	private final DLPortletInstanceSettings _dlPortletInstanceSettings;
	private final DLPortletInstanceSettingsHelper
		_dlPortletInstanceSettingsHelper;
	private final DLRequestHelper _dlRequestHelper;
	private String[] _extensions;
	private Long _fileEntryTypeId;
	private Folder _folder;
	private long _folderId;
	private final HttpServletRequest _httpServletRequest;
	private final HttpSession _httpSession;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private List<Folder> _mountFolders;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final PermissionChecker _permissionChecker;
	private final PortalPreferences _portalPreferences;
	private PortletPreferences _portletPreferences;
	private long _repositoryId;
	private Folder _rootFolder;
	private long _rootFolderId;
	private boolean _rootFolderInTrash;
	private String _rootFolderName;
	private boolean _rootFolderNotFound;
	private SearchContainer<RepositoryEntry> _searchContainer;
	private Long _searchFolderId;
	private Long _searchRepositoryId;
	private long _selectedRepositoryId;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;
	private final VersioningStrategy _versioningStrategy;

}