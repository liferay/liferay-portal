/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.display.context;

import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.search.BookmarksSearcher;
import com.liferay.bookmarks.service.BookmarksEntryServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderServiceUtil;
import com.liferay.bookmarks.web.internal.portlet.util.BookmarksUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Mariano Álvaro Sáiz
 */
public class BookmarksDisplayContext {

	public BookmarksDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long folderId) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_folderId = folderId;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		String[] displayViews = getDisplayViews();

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		if (Validator.isNull(_displayStyle)) {
			_displayStyle = _portalPreferences.getValue(
				BookmarksPortletKeys.BOOKMARKS, "display-style", "descriptive");
		}
		else {
			if (ArrayUtil.contains(displayViews, _displayStyle)) {
				_portalPreferences.setValue(
					BookmarksPortletKeys.BOOKMARKS, "display-style",
					_displayStyle);

				_httpServletRequest.setAttribute(
					WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
			}
		}

		if (!ArrayUtil.contains(displayViews, _displayStyle)) {
			_displayStyle = displayViews[0];
		}

		return _displayStyle;
	}

	public String[] getDisplayViews() {
		return new String[] {"descriptive", "list"};
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	public PortletURL getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = _liferayPortletResponse.createRenderURL();

		if (_folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_portletURL.setParameter("mvcRenderCommandName", "/bookmarks/view");
		}
		else {
			_portletURL.setParameter(
				"mvcRenderCommandName", "/bookmarks/view_folder");
			_portletURL.setParameter("folderId", String.valueOf(_folderId));
		}

		_portletURL.setParameter("navigation", getNavigation());

		return _portletURL;
	}

	public SearchContainer<Object> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<Object> searchContainer = new SearchContainer(
			_liferayPortletRequest, null, null, "curEntry",
			SearchContainer.DEFAULT_DELTA, getPortletURL(), null,
			"there-are-no-bookmarks-in-this-folder");

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			Indexer<?> indexer = BookmarksSearcher.getInstance();

			SearchContext searchContext = SearchContextFactory.getInstance(
				_httpServletRequest);

			searchContext.setAttribute("paginationType", "more");
			searchContext.setEnd(searchContainer.getEnd());
			searchContext.setFolderIds(new long[] {_folderId});
			searchContext.setIncludeInternalAssetCategories(true);
			searchContext.setKeywords(keywords);
			searchContext.setStart(searchContainer.getStart());

			Hits hits = indexer.search(searchContext);

			searchContainer.setResultsAndTotal(
				() -> BookmarksUtil.getEntries(hits), hits.getLength());
		}
		else if (isNavigationMine() || isNavigationRecent()) {
			long groupEntriesUserId = 0;

			if (isNavigationMine() && _themeDisplay.isSignedIn()) {
				groupEntriesUserId = _themeDisplay.getUserId();
			}

			long bookmarksGroupEntriesUserId = groupEntriesUserId;

			searchContainer.setResultsAndTotal(
				() -> new ArrayList<>(
					BookmarksEntryServiceUtil.getGroupEntries(
						_themeDisplay.getScopeGroupId(),
						bookmarksGroupEntriesUserId, searchContainer.getStart(),
						searchContainer.getEnd())),
				BookmarksEntryServiceUtil.getGroupEntriesCount(
					_themeDisplay.getScopeGroupId(),
					bookmarksGroupEntriesUserId));
		}
		else if (_isAssetEntryQuery()) {
			AssetEntryQuery assetEntryQuery = new AssetEntryQuery(
				BookmarksEntry.class.getName(), searchContainer);

			assetEntryQuery.setEnablePermissions(true);
			assetEntryQuery.setExcludeZeroViewCount(false);
			assetEntryQuery.setEnd(searchContainer.getEnd());
			assetEntryQuery.setStart(searchContainer.getStart());

			if (Validator.isNotNull(keywords)) {
				assetEntryQuery.setKeywords(keywords);
			}

			searchContainer.setResultsAndTotal(
				() -> new ArrayList<>(
					AssetEntryServiceUtil.getEntries(assetEntryQuery)),
				AssetEntryServiceUtil.getEntriesCount(assetEntryQuery));
		}
		else {
			searchContainer.setResultsAndTotal(
				() -> BookmarksFolderServiceUtil.getFoldersAndEntries(
					_themeDisplay.getScopeGroupId(), _folderId,
					WorkflowConstants.STATUS_APPROVED,
					searchContainer.getStart(), searchContainer.getEnd()),
				BookmarksFolderServiceUtil.getFoldersAndEntriesCount(
					_themeDisplay.getScopeGroupId(), _folderId));
		}

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public boolean isNavigationHome() {
		return Objects.equals(getNavigation(), "all");
	}

	public boolean isNavigationMine() {
		return Objects.equals(getNavigation(), "mine");
	}

	public boolean isNavigationRecent() {
		return Objects.equals(getNavigation(), "recent");
	}

	private Long _getAssetCategoryId() {
		if (_assetCategoryId != null) {
			return _assetCategoryId;
		}

		_assetCategoryId = ParamUtil.getLong(_httpServletRequest, "categoryId");

		return _assetCategoryId;
	}

	private String _getAssetTagName() {
		if (_assetTagName != null) {
			return _assetTagName;
		}

		_assetTagName = ParamUtil.getString(_httpServletRequest, "tag");

		return _assetTagName;
	}

	private boolean _isAssetEntryQuery() {
		if ((_getAssetCategoryId() > 0) ||
			Validator.isNotNull(_getAssetTagName())) {

			return true;
		}

		return false;
	}

	private Long _assetCategoryId;
	private String _assetTagName;
	private String _displayStyle;
	private final long _folderId;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _navigation;
	private final PortalPreferences _portalPreferences;
	private PortletURL _portletURL;
	private SearchContainer<Object> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}