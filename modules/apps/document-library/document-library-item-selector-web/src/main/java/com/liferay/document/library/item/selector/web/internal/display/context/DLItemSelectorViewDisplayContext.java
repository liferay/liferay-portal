/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.item.selector.web.internal.DLItemSelectorView;
import com.liferay.document.library.item.selector.web.internal.criterion.DLItemSelectorCriterionCreationMenuRestrictionUtil;
import com.liferay.document.library.item.selector.web.internal.file.DLCustomFileItemSelectorView;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.criteria.file.criterion.CustomFileItemSelectorCriterion;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.item.selector.taglib.servlet.taglib.util.RepositoryEntryBrowserTagUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.FileEntryTypeCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Roberto Díaz
 */
public class DLItemSelectorViewDisplayContext<T extends ItemSelectorCriterion> {

	public DLItemSelectorViewDisplayContext(
		AssetVocabularyService assetVocabularyService,
		ClassNameLocalService classNameLocalService,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
		DLItemSelectorView<T> dlItemSelectorView,
		ModelResourcePermission<Folder> folderModelResourcePermission,
		HttpServletRequest httpServletRequest, T itemSelectorCriterion,
		String itemSelectedEventName,
		ItemSelectorReturnTypeResolverHandler
			itemSelectorReturnTypeResolverHandler,
		PortletURL portletURL, boolean search,
		StagingGroupHelper stagingGroupHelper) {

		_assetVocabularyService = assetVocabularyService;
		_classNameLocalService = classNameLocalService;
		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
		_dlItemSelectorView = dlItemSelectorView;
		_folderModelResourcePermission = folderModelResourcePermission;
		_httpServletRequest = httpServletRequest;
		_itemSelectorCriterion = itemSelectorCriterion;
		_itemSelectedEventName = itemSelectedEventName;
		_itemSelectorReturnTypeResolverHandler =
			itemSelectorReturnTypeResolverHandler;
		_portletURL = portletURL;
		_search = search;
		_stagingGroupHelper = stagingGroupHelper;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Set<String> getAllowedCreationMenuUIItemKeys() {
		return DLItemSelectorCriterionCreationMenuRestrictionUtil.
			getAllowedCreationMenuUIItemKeys(_itemSelectorCriterion);
	}

	public PortletURL getEditImageURL(
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, PortletKeys.DOCUMENT_LIBRARY
		).setActionName(
			"/document_library/image_editor"
		).setParameter(
			"folderId", getFolderId()
		).buildPortletURL();
	}

	public String[] getExtensions() {
		if (_itemSelectorCriterion instanceof CustomFileItemSelectorCriterion) {
			CustomFileItemSelectorCriterion customFileItemSelectorCriterion =
				(CustomFileItemSelectorCriterion)_itemSelectorCriterion;

			return customFileItemSelectorCriterion.getExtensions();
		}

		return _dlItemSelectorView.getExtensions();
	}

	public long getFolderId() throws PortalException {
		if (_folderId == null) {
			_folderId = _getFolderId(_httpServletRequest);
		}

		return _folderId;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public ItemSelectorReturnTypeResolver<?, ?>
		getItemSelectorReturnTypeResolver() {

		return _itemSelectorReturnTypeResolverHandler.
			getItemSelectorReturnTypeResolver(
				_itemSelectorCriterion, _dlItemSelectorView, FileEntry.class);
	}

	public long getMaxFileSize() {
		if (_itemSelectorCriterion instanceof DLCustomFileItemSelectorView) {
			CustomFileItemSelectorCriterion customFileItemSelectorCriterion =
				(CustomFileItemSelectorCriterion)_itemSelectorCriterion;

			return DLValidatorUtil.getMaxAllowableSize(
				_themeDisplay.getScopeGroupId(), null,
				customFileItemSelectorCriterion.getMaxFileSize());
		}

		return DLValidatorUtil.getMaxAllowableSize(
			_themeDisplay.getScopeGroupId(), null);
	}

	public String getMimeTypeRestriction() {
		return _itemSelectorCriterion.getMimeTypeRestriction();
	}

	public PortletURL getPortletURL(
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException, PortletException {

		return PortletURLBuilder.create(
			PortletURLUtil.clone(_portletURL, liferayPortletResponse)
		).setParameter(
			"folderId", getFolderId()
		).setParameter(
			"selectedTab", getTitle()
		).buildPortletURL();
	}

	public List<Object> getRepositoryEntries() throws Exception {
		if (_isSearch()) {
			Hits hits = _getHits();

			Document[] docs = hits.getDocs();

			List<Object> repositoryEntries = new ArrayList<>(docs.length);

			List<SearchResult> searchResults =
				SearchResultUtil.getSearchResults(
					hits, _themeDisplay.getLocale());

			for (SearchResult searchResult : searchResults) {
				String className = searchResult.getClassName();

				if (className.equals(DLFileEntryConstants.getClassName())) {
					repositoryEntries.add(
						DLAppServiceUtil.getFileEntry(
							searchResult.getClassPK()));
				}
				else if (className.equals(
							DLFileShortcutConstants.getClassName())) {

					repositoryEntries.add(
						DLAppServiceUtil.getFileShortcut(
							searchResult.getClassPK()));
				}
				else if (className.equals(DLFolderConstants.getClassName())) {
					repositoryEntries.add(
						DLAppServiceUtil.getFolder(searchResult.getClassPK()));
				}
			}

			return repositoryEntries;
		}

		OrderByComparator<Object> repositoryModelOrderByComparator =
			DLUtil.getRepositoryModelOrderByComparator(
				RepositoryEntryBrowserTagUtil.getOrderByCol(
					_httpServletRequest, _portalPreferences),
				RepositoryEntryBrowserTagUtil.getOrderByType(
					_httpServletRequest, _portalPreferences),
				true);

		int[] startAndEnd = _getStartAndEnd();

		Repository repository = _getRepository();

		if (_isFilterByFileEntryType() &&
			repository.isCapabilityProvided(FileEntryTypeCapability.class)) {

			FileEntryTypeCapability fileEntryTypeCapability =
				repository.getCapability(FileEntryTypeCapability.class);

			Long fileEntryTypeId = _getFileEntryTypeId();

			if (fileEntryTypeId == null) {
				fileEntryTypeId =
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL;
			}

			return (List)
				fileEntryTypeCapability.
					getFoldersAndFileEntriesAndFileShortcuts(
						_getStagingAwareGroupId(), getFolderId(),
						_getMimeTypes(), fileEntryTypeId, false,
						WorkflowConstants.STATUS_APPROVED, startAndEnd[0],
						startAndEnd[1], repositoryModelOrderByComparator);
		}

		return DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcuts(
			_repository.getRepositoryId(), getFolderId(),
			WorkflowConstants.STATUS_APPROVED, _getMimeTypes(), true, false,
			startAndEnd[0], startAndEnd[1], repositoryModelOrderByComparator);
	}

	public int getRepositoryEntriesCount() throws PortalException {
		if (_isSearch()) {
			Hits hits = _getHits();

			return hits.getLength();
		}

		Repository repository = _getRepository();

		if (_isFilterByFileEntryType() &&
			repository.isCapabilityProvided(FileEntryTypeCapability.class)) {

			FileEntryTypeCapability fileEntryTypeCapability =
				repository.getCapability(FileEntryTypeCapability.class);

			Long fileEntryTypeId = _getFileEntryTypeId();

			if (fileEntryTypeId == null) {
				fileEntryTypeId =
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL;
			}

			return fileEntryTypeCapability.
				getFoldersAndFileEntriesAndFileShortcutsCount(
					_getStagingAwareGroupId(), getFolderId(), _getMimeTypes(),
					fileEntryTypeId, false, WorkflowConstants.STATUS_APPROVED);
		}

		return DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcutsCount(
			_repository.getRepositoryId(), getFolderId(),
			WorkflowConstants.STATUS_APPROVED, _getMimeTypes(), true, false);
	}

	public String getTitle() {
		return _dlItemSelectorView.getTitle(_themeDisplay.getLocale());
	}

	public PortletURL getUploadURL(
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		if (!isShowDragAndDropZone()) {
			return null;
		}

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					_themeDisplay.getScopeGroupId()));

		if (!assetVocabularies.isEmpty()) {
			long classNameId = _classNameLocalService.getClassNameId(
				DLFileEntryConstants.getClassName());
			long defaultFileEntryTypeId =
				DLFileEntryTypeLocalServiceUtil.getDefaultFileEntryTypeId(
					getFolderId());

			for (AssetVocabulary assetVocabulary : assetVocabularies) {
				if (assetVocabulary.isRequired(
						classNameId, defaultFileEntryTypeId,
						_themeDisplay.getScopeGroupId())) {

					return null;
				}
			}
		}

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, PortletKeys.DOCUMENT_LIBRARY
		).setActionName(
			"/document_library/upload_file_entry"
		).setParameter(
			"folderId", getFolderId()
		).buildPortletURL();
	}

	public boolean isShowDragAndDropZone() throws PortalException {
		if (_showDragAndDropZone != null) {
			return _showDragAndDropZone;
		}

		if (!ModelResourcePermissionUtil.contains(
				_folderModelResourcePermission,
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), getFolderId(),
				ActionKeys.ADD_DOCUMENT)) {

			_showDragAndDropZone = false;
		}
		else {
			if (RepositoryUtil.isExternalRepository(
					_repository.getRepositoryId()) ||
				DLUtil.hasWorkflowDefinitionLink(
					_themeDisplay.getCompanyId(),
					_themeDisplay.getScopeGroupId(), getFolderId(),
					DLFileEntryTypeLocalServiceUtil.getDefaultFileEntryTypeId(
						getFolderId()))) {

				_showDragAndDropZone = false;
			}
			else {
				_showDragAndDropZone = true;
			}
		}

		return _showDragAndDropZone;
	}

	public void setShowDragAndDropZone(boolean showDragAndDropZone) {
		_showDragAndDropZone = showDragAndDropZone;
	}

	private Long _getFileEntryTypeId() {
		if (!(_itemSelectorCriterion instanceof
				InfoItemItemSelectorCriterion)) {

			return null;
		}

		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
			(InfoItemItemSelectorCriterion)_itemSelectorCriterion;

		Long fileEntryTypeId = _getFileEntryTypeId(
			infoItemItemSelectorCriterion.getItemSubtype());

		if (fileEntryTypeId != null) {
			return fileEntryTypeId;
		}

		return GetterUtil.getLong(
			infoItemItemSelectorCriterion.getItemSubtype());
	}

	private Long _getFileEntryTypeId(String fileEntryTypeKey) {
		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchFileEntryType(
				GetterUtil.getLong(fileEntryTypeKey, -1));

		if (dlFileEntryType == null) {
			dlFileEntryType = _dlFileEntryTypeLocalService.fetchFileEntryType(
				_themeDisplay.getScopeGroupId(), fileEntryTypeKey);
		}

		if (dlFileEntryType == null) {
			dlFileEntryType = _dlFileEntryTypeLocalService.fetchFileEntryType(
				_themeDisplay.getCompanyGroupId(), fileEntryTypeKey);
		}

		if (dlFileEntryType != null) {
			return dlFileEntryType.getFileEntryTypeId();
		}

		return null;
	}

	private long _getFolderId(HttpServletRequest httpServletRequest)
		throws PortalException {

		if (httpServletRequest.getParameter("folderId") != null) {
			return ParamUtil.getLong(
				httpServletRequest, "folderId",
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		long selectedFileEntryId = ParamUtil.getLong(
			PortalUtil.getOriginalServletRequest(httpServletRequest),
			"selectedItemIds");

		if (selectedFileEntryId != 0) {
			FileEntry fileEntry = DLAppServiceUtil.getFileEntry(
				selectedFileEntryId);

			return fileEntry.getFolderId();
		}

		return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}

	private long[] _getGroupIds() throws PortalException {
		if (_isEverywhereScopeFilter()) {
			return SiteConnectedGroupGroupProviderUtil.
				getCurrentAndAncestorSiteAndDepotGroupIds(
					_themeDisplay.getScopeGroupId());
		}

		return PortalUtil.getCurrentAndAncestorSiteGroupIds(
			_themeDisplay.getScopeGroupId());
	}

	private Hits _getHits() throws PortalException {
		if (_hits != null) {
			return _hits;
		}

		_hits = DLAppServiceUtil.search(
			_getStagingAwareGroupId(), _getSearchContext());

		return _hits;
	}

	private String[] _getMimeTypes() {
		if (_mimeTypes != null) {
			return _mimeTypes;
		}

		String[] mimeTypes = _dlItemSelectorView.getMimeTypes();

		ItemSelectorCriterion itemSelectorCriterion = _itemSelectorCriterion;

		if (itemSelectorCriterion instanceof CustomFileItemSelectorCriterion) {
			String[] customFileItemSelectorMimeTypes = new String[0];

			CustomFileItemSelectorCriterion customFileItemSelectorCriterion =
				(CustomFileItemSelectorCriterion)itemSelectorCriterion;

			for (String extension :
					customFileItemSelectorCriterion.getExtensions()) {

				customFileItemSelectorMimeTypes = ArrayUtil.append(
					customFileItemSelectorMimeTypes,
					MimeTypesUtil.getExtensionContentType(extension));
			}

			if (ArrayUtil.isNotEmpty(customFileItemSelectorMimeTypes)) {
				mimeTypes = customFileItemSelectorMimeTypes;
			}
		}
		else if (itemSelectorCriterion instanceof
					InfoItemItemSelectorCriterion) {

			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
				(InfoItemItemSelectorCriterion)itemSelectorCriterion;

			String[] infoItemSelectorMimeTypes =
				infoItemItemSelectorCriterion.getMimeTypes();

			if (ArrayUtil.isNotEmpty(infoItemSelectorMimeTypes)) {
				mimeTypes = infoItemItemSelectorCriterion.getMimeTypes();
			}
		}

		_mimeTypes = mimeTypes;

		return _mimeTypes;
	}

	private Repository _getRepository() throws PortalException {
		if (_repository != null) {
			return _repository;
		}

		Repository repository = null;

		if (getFolderId() != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = DLFolderLocalServiceUtil.fetchDLFolder(
				getFolderId());

			if ((dlFolder != null) && dlFolder.isMountPoint()) {
				repository = RepositoryProviderUtil.getRepository(
					dlFolder.getRepositoryId());
			}
			else {
				repository = RepositoryProviderUtil.getFolderRepository(
					getFolderId());
			}
		}
		else {
			repository = RepositoryProviderUtil.getRepository(
				_getStagingAwareGroupId());
		}

		_repository = repository;

		return _repository;
	}

	private SearchContext _getSearchContext() throws PortalException {
		if (_searchContext != null) {
			return _searchContext;
		}

		int[] startAndEnd = _getStartAndEnd();

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		Repository repository = _getRepository();

		if (_isFilterByFileEntryType() &&
			repository.isCapabilityProvided(FileEntryTypeCapability.class)) {

			Long fileEntryTypeId = _getFileEntryTypeId();

			if (fileEntryTypeId != null) {
				searchContext.setAttribute("fileEntryTypeId", fileEntryTypeId);
			}
		}

		searchContext.setAttribute("mimeTypes", _getMimeTypes());
		searchContext.setEnd(startAndEnd[1]);
		searchContext.setFolderIds(new long[] {getFolderId()});
		searchContext.setGroupIds(_getGroupIds());
		searchContext.setStart(startAndEnd[0]);

		_searchContext = searchContext;

		return _searchContext;
	}

	private long _getStagingAwareGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = _stagingGroupHelper.getStagedPortletGroupId(
			_themeDisplay.getScopeGroupId(), DLPortletKeys.DOCUMENT_LIBRARY);

		return _groupId;
	}

	private int[] _getStartAndEnd() {
		if (_startAndEnd != null) {
			return _startAndEnd;
		}

		int cur = ParamUtil.getInteger(
			_httpServletRequest, SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_CUR);
		int delta = ParamUtil.getInteger(
			_httpServletRequest, SearchContainer.DEFAULT_DELTA_PARAM,
			SearchContainer.DEFAULT_DELTA);

		_startAndEnd = SearchPaginationUtil.calculateStartAndEnd(cur, delta);

		return _startAndEnd;
	}

	private boolean _isEverywhereScopeFilter() {
		return Objects.equals(
			ParamUtil.getString(_httpServletRequest, "scope"), "everywhere");
	}

	private boolean _isFilterByFileEntryType() {
		if (_filterByFileEntryType != null) {
			return _filterByFileEntryType;
		}

		boolean filterByFileEntryType = false;

		ItemSelectorCriterion itemSelectorCriterion = _itemSelectorCriterion;

		if (itemSelectorCriterion instanceof InfoItemItemSelectorCriterion) {
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
				(InfoItemItemSelectorCriterion)itemSelectorCriterion;

			if (Validator.isNotNull(
					infoItemItemSelectorCriterion.getItemSubtype())) {

				filterByFileEntryType = true;
			}
		}

		_filterByFileEntryType = filterByFileEntryType;

		return _filterByFileEntryType;
	}

	private boolean _isSearch() {
		if (_isEverywhereScopeFilter()) {
			return true;
		}

		return _search;
	}

	private final AssetVocabularyService _assetVocabularyService;
	private final ClassNameLocalService _classNameLocalService;
	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;
	private final DLItemSelectorView<T> _dlItemSelectorView;
	private Boolean _filterByFileEntryType;
	private Long _folderId;
	private final ModelResourcePermission<Folder>
		_folderModelResourcePermission;
	private Long _groupId;
	private Hits _hits;
	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private final T _itemSelectorCriterion;
	private final ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;
	private String[] _mimeTypes;
	private final PortalPreferences _portalPreferences;
	private final PortletURL _portletURL;
	private Repository _repository;
	private final boolean _search;
	private SearchContext _searchContext;
	private Boolean _showDragAndDropZone;
	private final StagingGroupHelper _stagingGroupHelper;
	private int[] _startAndEnd;
	private final ThemeDisplay _themeDisplay;

}