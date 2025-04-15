/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelModifiedDateComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelSizeComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelTitleComparator;
import com.liferay.document.library.taglib.internal.frontend.taglib.clay.servlet.FileEntryVerticalCard;
import com.liferay.document.library.taglib.internal.frontend.taglib.clay.servlet.FileShortcutVerticalCard;
import com.liferay.document.library.taglib.internal.frontend.taglib.clay.servlet.FolderHorizontalCard;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.HorizontalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.RelatedSearchResult;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Adolfo Pérez
 */
public class RepositoryBrowserTagDisplayContext {

	public RepositoryBrowserTagDisplayContext(
		Set<String> actions, DLAppService dlAppService,
		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission,
		ModelResourcePermission<FileShortcut>
			fileShortcutModelResourcePermission,
		ModelResourcePermission<Folder> folderModelResourcePermission,
		long folderId, HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		PortletRequest portletRequest, long repositoryId, long rootFolderId,
		boolean viewableByGuest) {

		_actions = actions;
		_dlAppService = dlAppService;
		_fileEntryModelResourcePermission = fileEntryModelResourcePermission;
		_fileShortcutModelResourcePermission =
			fileShortcutModelResourcePermission;
		_folderModelResourcePermission = folderModelResourcePermission;
		_folderId = folderId;
		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portletRequest = portletRequest;
		_repositoryId = repositoryId;
		_rootFolderId = rootFolderId;
		_viewableByGuest = viewableByGuest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems(
		RepositoryEntry repositoryEntry) {

		if (repositoryEntry instanceof FileEntry) {
			return _getActionDropdownItems((FileEntry)repositoryEntry);
		}

		if (repositoryEntry instanceof FileShortcut) {
			return _getActionDropdownItems((FileShortcut)repositoryEntry);
		}

		if (repositoryEntry instanceof Folder) {
			return _getActionDropdownItems((Folder)repositoryEntry);
		}

		throw new IllegalArgumentException(
			"Invalid repository model " + repositoryEntry);
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"repositoryBrowserURL", _getRepositoryBrowserURL()
		).build();
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() throws PortalException {
		LinkedList<BreadcrumbEntry> breadcrumbEntries = new LinkedList<>();

		long folderId = _folderId;

		while (folderId != _rootFolderId) {
			Folder folder = _dlAppService.getFolder(folderId);

			if (folder.isMountPoint()) {
				break;
			}

			breadcrumbEntries.addFirst(
				_createBreadcrumbEntry(folderId, folder.getName()));

			folderId = folder.getParentFolderId();
		}

		breadcrumbEntries.addFirst(
			_createBreadcrumbEntry(
				folderId, LanguageUtil.get(_httpServletRequest, "home")));

		return breadcrumbEntries;
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle", "icon");

		return _displayStyle;
	}

	public String getFolderURL(Folder folder) {
		return _getFolderURL(folder.getFolderId());
	}

	public HorizontalCard getHorizontalCard(RepositoryEntry repositoryEntry)
		throws PortalException {

		if (!(repositoryEntry instanceof Folder)) {
			throw new IllegalArgumentException(
				"Invalid repository model " + repositoryEntry);
		}

		return new FolderHorizontalCard(
			_actions, (Folder)repositoryEntry, this);
	}

	public ManagementToolbarDisplayContext getManagementToolbarDisplayContext()
		throws PortalException {

		return new RepositoryBrowserManagementToolbarDisplayContext(
			_actions, _folderId, _folderModelResourcePermission,
			_httpServletRequest, _liferayPortletRequest,
			_liferayPortletResponse, _repositoryId, getSearchContainer(),
			_viewableByGuest);
	}

	public Map<String, Object> getRepositoryBrowserComponentContext() {
		return HashMapBuilder.<String, Object>put(
			"parentFolderId", String.valueOf(_folderId)
		).put(
			"repositoryBrowserURL", _getRepositoryBrowserURL()
		).put(
			"repositoryId", String.valueOf(_repositoryId)
		).put(
			"viewableByGuest", String.valueOf(_viewableByGuest)
		).build();
	}

	public String getRepositoryEntryIcon(RepositoryEntry repositoryEntry) {
		if (repositoryEntry instanceof FileEntry) {
			return "documents-and-media";
		}

		if (repositoryEntry instanceof FileShortcut) {
			return "shortcut";
		}

		if (repositoryEntry instanceof Folder) {
			return "folder";
		}

		throw new IllegalArgumentException(
			"Repository entry model not a file entry, file shortcut or folder" +
				repositoryEntry);
	}

	public String getRepositoryEntryModifiedDateDescription(
		RepositoryEntry repositoryEntry) {

		Date modifiedDate = repositoryEntry.getModifiedDate();

		return LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - modifiedDate.getTime(), true);
	}

	public String getRepositoryEntrySizeValue(RepositoryEntry repositoryEntry)
		throws PortalException {

		if (repositoryEntry instanceof Folder) {
			return StringPool.BLANK;
		}

		FileEntry fileEntry = _getFileEntry(repositoryEntry);

		return LanguageUtil.formatStorageSize(
			fileEntry.getSize(), _themeDisplay.getLocale());
	}

	public String getRepositoryEntryThumbnailSrc(
			RepositoryEntry repositoryEntry)
		throws Exception {

		return DLURLHelperUtil.getThumbnailSrc(
			(FileEntry)repositoryEntry, _themeDisplay);
	}

	public String getRepositoryEntryTitle(RepositoryEntry repositoryEntry) {
		if (repositoryEntry instanceof FileEntry) {
			FileEntry fileEntry = (FileEntry)repositoryEntry;

			return fileEntry.getTitle();
		}

		if (repositoryEntry instanceof FileShortcut) {
			FileShortcut fileShortcut = (FileShortcut)repositoryEntry;

			return fileShortcut.getToTitle();
		}

		if (repositoryEntry instanceof Folder) {
			Folder folder = (Folder)repositoryEntry;

			return folder.getName();
		}

		throw new IllegalArgumentException(
			"Repository entry model not a file entry, file shortcut or folder" +
				repositoryEntry);
	}

	public String getRepositoryEntryURL(RepositoryEntry repositoryEntry) {
		return getFolderURL((Folder)repositoryEntry);
	}

	public ResultRowSplitter getResultRowSplitter() {
		return resultRows -> {
			List<ResultRowSplitterEntry> resultRowSplitterEntries =
				new ArrayList<>();

			List<ResultRow> fileEntryResultRows = new ArrayList<>();
			List<ResultRow> folderResultRows = new ArrayList<>();

			for (ResultRow resultRow : resultRows) {
				if (resultRow.getObject() instanceof Folder) {
					folderResultRows.add(resultRow);
				}
				else {
					fileEntryResultRows.add(resultRow);
				}
			}

			if (!folderResultRows.isEmpty()) {
				resultRowSplitterEntries.add(
					new ResultRowSplitterEntry("folders", folderResultRows));
			}

			if (!fileEntryResultRows.isEmpty()) {
				resultRowSplitterEntries.add(
					new ResultRowSplitterEntry(
						"documents", fileEntryResultRows));
			}

			return resultRowSplitterEntries;
		};
	}

	public SearchContainer<Object> getSearchContainer() throws PortalException {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		String keywords = ParamUtil.getString(
			_liferayPortletRequest, "keywords");

		if (Validator.isNull(keywords)) {
			_searchContainer = _getDLSearchContainer();
		}
		else {
			_searchContainer = _getSearchSearchContainer();
		}

		return _searchContainer;
	}

	public VerticalCard getVerticalCard(RepositoryEntry repositoryEntry)
		throws PortalException {

		if (repositoryEntry instanceof FileEntry) {
			return new FileEntryVerticalCard(
				_actions, (FileEntry)repositoryEntry, _httpServletRequest,
				this);
		}

		if (repositoryEntry instanceof FileShortcut) {
			return new FileShortcutVerticalCard(
				_actions, (FileShortcut)repositoryEntry, _httpServletRequest,
				this);
		}

		throw new IllegalArgumentException(
			"Invalid repository model " + repositoryEntry);
	}

	public boolean isDescriptiveDisplayStyle() {
		return Objects.equals(getDisplayStyle(), "descriptive");
	}

	public boolean isIconDisplayStyle() {
		return Objects.equals(getDisplayStyle(), "icon");
	}

	public boolean isRepositoryEntryNavigable(RepositoryEntry repositoryEntry) {
		if (repositoryEntry instanceof Folder) {
			return true;
		}

		return false;
	}

	public boolean isRepositoryEntryThumbnailAvailable(
			RepositoryEntry repositoryEntry)
		throws Exception {

		if (!(repositoryEntry instanceof FileEntry) ||
			Validator.isNull(getRepositoryEntryThumbnailSrc(repositoryEntry))) {

			return false;
		}

		return true;
	}

	public boolean isVerticalCard(RepositoryEntry repositoryEntry) {
		if (repositoryEntry instanceof Folder) {
			return false;
		}

		return true;
	}

	private BreadcrumbEntry _createBreadcrumbEntry(
		long folderId, String title) {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(title);
		breadcrumbEntry.setURL(_getFolderURL(folderId));

		return breadcrumbEntry;
	}

	private List<DropdownItem> _getActionDropdownItems(FileEntry fileEntry) {
		return DropdownItemListBuilder.add(
			() ->
				_actions.contains("rename") &&
				_fileEntryModelResourcePermission.contains(
					_themeDisplay.getPermissionChecker(), fileEntry,
					ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.putData("action", "rename");
				dropdownItem.putData(
					"renameURL", _getRenameFileEntryURL(fileEntry));
				dropdownItem.putData("value", fileEntry.getTitle());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "rename"));
			}
		).add(
			() ->
				_actions.contains("delete") &&
				_fileEntryModelResourcePermission.contains(
					_themeDisplay.getPermissionChecker(), fileEntry,
					ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"deleteURL", _getDeleteFileEntryURL(fileEntry));
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
	}

	private List<DropdownItem> _getActionDropdownItems(
		FileShortcut fileShortcut) {

		return DropdownItemListBuilder.add(
			() ->
				_actions.contains("delete") &&
				_fileShortcutModelResourcePermission.contains(
					_themeDisplay.getPermissionChecker(), fileShortcut,
					ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"deleteURL", _getDeleteFileShortcutURL(fileShortcut));
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
	}

	private List<DropdownItem> _getActionDropdownItems(Folder folder) {
		return DropdownItemListBuilder.add(
			() ->
				_actions.contains("rename") &&
				_folderModelResourcePermission.contains(
					_themeDisplay.getPermissionChecker(), folder,
					ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.putData("action", "rename");
				dropdownItem.putData("renameURL", _getRenameFolderURL(folder));
				dropdownItem.putData("value", folder.getName());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "rename"));
			}
		).add(
			() ->
				_actions.contains("delete") &&
				_folderModelResourcePermission.contains(
					_themeDisplay.getPermissionChecker(), folder,
					ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData("deleteURL", _getDeleteFolderURL(folder));
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
	}

	private String _getDeleteFileEntryURL(FileEntry fileEntry) {
		return HttpComponentsUtil.addParameter(
			_getRepositoryBrowserURL(), "fileEntryId",
			fileEntry.getFileEntryId());
	}

	private String _getDeleteFileShortcutURL(FileShortcut fileShortcut) {
		return HttpComponentsUtil.addParameter(
			_getRepositoryBrowserURL(), "fileShortcutId",
			fileShortcut.getFileShortcutId());
	}

	private String _getDeleteFolderURL(Folder folder) {
		return HttpComponentsUtil.addParameter(
			_getRepositoryBrowserURL(), "folderId", folder.getFolderId());
	}

	private SearchContainer<Object> _getDLSearchContainer()
		throws PortalException {

		SearchContainer<Object> searchContainer = new SearchContainer<>(
			_portletRequest,
			PortletURLUtil.getCurrent(
				_liferayPortletRequest, _liferayPortletResponse),
			null, "there-are-no-documents-in-this-folder");

		searchContainer.setOrderByCol(
			ParamUtil.getString(
				_httpServletRequest, searchContainer.getOrderByColParam(),
				"title"));
		searchContainer.setOrderByType(
			ParamUtil.getString(
				_httpServletRequest, searchContainer.getOrderByTypeParam(),
				"asc"));

		searchContainer.setResultsAndTotal(
			() -> DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcuts(
				_repositoryId, _folderId, WorkflowConstants.STATUS_APPROVED,
				null, false, false, searchContainer.getStart(),
				searchContainer.getEnd(),
				_getOrderByComparator(searchContainer)),
			DLAppServiceUtil.getFoldersAndFileEntriesAndFileShortcutsCount(
				_repositoryId, _folderId, WorkflowConstants.STATUS_APPROVED,
				null, false, false));

		if (!_actions.isEmpty()) {
			searchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_liferayPortletResponse));
		}

		return searchContainer;
	}

	private FileEntry _getFileEntry(RepositoryEntry repositoryEntry)
		throws PortalException {

		if (repositoryEntry instanceof FileEntry) {
			return (FileEntry)repositoryEntry;
		}

		FileShortcut fileShortcut = (FileShortcut)repositoryEntry;

		FileVersion fileVersion = fileShortcut.getFileVersion();

		return fileVersion.getFileEntry();
	}

	private String _getFolderURL(long folderId) {
		return PortletURLBuilder.create(
			PortletURLUtil.getCurrent(
				_liferayPortletRequest, _liferayPortletResponse)
		).setParameter(
			"folderId", folderId
		).buildString();
	}

	private Hits _getHits(SearchContainer<Object> searchContainer)
		throws PortalException {

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setAttribute("paginationType", "regular");
		searchContext.setAttribute("searchRepositoryId", _repositoryId);
		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setFolderIds(new long[] {_rootFolderId});
		searchContext.setKeywords(
			ParamUtil.getString(_httpServletRequest, "keywords"));
		searchContext.setLocale(_themeDisplay.getSiteDefaultLocale());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSearchSubfolders(true);

		searchContext.setStart(searchContainer.getStart());

		return DLAppServiceUtil.search(_repositoryId, searchContext);
	}

	private OrderByComparator<?> _getOrderByComparator(
		SearchContainer<Object> searchContainer) {

		boolean ascending = false;

		if (Objects.equals(searchContainer.getOrderByType(), "asc")) {
			ascending = true;
		}

		if (Objects.equals(searchContainer.getOrderByCol(), "modified-date")) {
			return new RepositoryModelModifiedDateComparator<>(ascending);
		}

		if (Objects.equals(searchContainer.getOrderByCol(), "size")) {
			return new RepositoryModelSizeComparator<>(ascending);
		}

		if (Objects.equals(searchContainer.getOrderByCol(), "title")) {
			return new RepositoryModelTitleComparator<>(ascending);
		}

		return null;
	}

	private String _getRenameFileEntryURL(FileEntry fileEntry) {
		return HttpComponentsUtil.addParameter(
			_getRepositoryBrowserURL(), "fileEntryId",
			fileEntry.getFileEntryId());
	}

	private String _getRenameFolderURL(Folder folder) {
		return HttpComponentsUtil.addParameter(
			_getRepositoryBrowserURL(), "folderId", folder.getFolderId());
	}

	private String _getRepositoryBrowserURL() {
		if (_repositoryBrowserURL != null) {
			return _repositoryBrowserURL;
		}

		_repositoryBrowserURL = StringBundler.concat(
			PortalUtil.getPortalURL(_httpServletRequest),
			PortalUtil.getPathContext(), Portal.PATH_MODULE,
			"/repository_browser");

		return _repositoryBrowserURL;
	}

	private List<Object> _getSearchResults(Hits hits) throws PortalException {
		List<Object> searchResults = new ArrayList<>();

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

	private SearchContainer<Object> _getSearchSearchContainer()
		throws PortalException {

		SearchContainer<Object> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, _liferayPortletResponse.createRenderURL(),
			null, null);

		Hits hits = _getHits(searchContainer);

		searchContainer.setResultsAndTotal(
			() -> _getSearchResults(hits), hits.getLength());

		return searchContainer;
	}

	private final Set<String> _actions;
	private String _displayStyle;
	private final DLAppService _dlAppService;
	private final ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;
	private final ModelResourcePermission<FileShortcut>
		_fileShortcutModelResourcePermission;
	private final long _folderId;
	private final ModelResourcePermission<Folder>
		_folderModelResourcePermission;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PortletRequest _portletRequest;
	private String _repositoryBrowserURL;
	private final long _repositoryId;
	private final long _rootFolderId;
	private SearchContainer<Object> _searchContainer;
	private final ThemeDisplay _themeDisplay;
	private final boolean _viewableByGuest;

}