/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.display.context.MBListDisplayContext;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBThreadServiceUtil;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.message.boards.web.internal.util.MBRequestUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Roberto Díaz
 */
public class DefaultMBListDisplayContext implements MBListDisplayContext {

	public DefaultMBListDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long categoryId,
		String mvcRenderCommandName) {

		_httpServletRequest = httpServletRequest;
		_categoryId = categoryId;

		boolean showMyPosts = false;
		boolean showRecentPosts = false;
		boolean showSearch = false;

		if (mvcRenderCommandName.equals("/message_boards/view_my_posts")) {
			showMyPosts = true;
		}
		else if (_isShowRecentPosts(mvcRenderCommandName)) {
			showRecentPosts = true;
		}
		else if (_isShowSearch(mvcRenderCommandName)) {
			showSearch = true;
		}

		_showMyPosts = showMyPosts;
		_showRecentPosts = showRecentPosts;
		_showSearch = showSearch;
	}

	@Override
	public int getCategoryEntriesDelta() {
		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_httpServletRequest);

		return GetterUtil.getInteger(
			portalPreferences.getValue(
				MBPortletKeys.MESSAGE_BOARDS, "categoryEntriesDelta"),
			SearchContainer.DEFAULT_DELTA);
	}

	@Override
	public String getEmptyResultsMessage() {
		if (isShowSearch()) {
			return "there-are-no-threads";
		}

		return "there-are-no-threads-or-categories";
	}

	@Override
	public int getThreadEntriesDelta() {
		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_httpServletRequest);

		return GetterUtil.getInteger(
			portalPreferences.getValue(
				MBPortletKeys.MESSAGE_BOARDS, "threadEntriesDelta"),
			SearchContainer.DEFAULT_DELTA);
	}

	@Override
	public UUID getUuid() {
		return _UUID;
	}

	@Override
	public boolean isShowMyPosts() {
		return _showMyPosts;
	}

	@Override
	public boolean isShowRecentPosts() {
		return _showRecentPosts;
	}

	@Override
	public boolean isShowSearch() {
		return _showSearch;
	}

	@Override
	public void populateCategoriesResultsAndTotal(
			SearchContainer searchContainer)
		throws PortalException {

		if (isShowSearch()) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		int status = _getStatus(themeDisplay);

		try {
			searchContainer.setResultsAndTotal(
				() -> MBCategoryServiceUtil.getCategories(
					themeDisplay.getScopeGroupId(), _categoryId,
					_getQueryDefinition(
						searchContainer, status, themeDisplay.getUserId())),
				MBCategoryServiceUtil.getCategoriesCount(
					themeDisplay.getScopeGroupId(), _categoryId,
					_getQueryDefinition(
						searchContainer, status, themeDisplay.getUserId())));
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
	}

	@Override
	public void populateThreadsResultsAndTotal(SearchContainer searchContainer)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (isShowSearch()) {
			long searchCategoryId = ParamUtil.getLong(
				_httpServletRequest, "searchCategoryId");

			List<Long> categoryIds = new ArrayList<Long>() {
				{
					add(Long.valueOf(searchCategoryId));
				}
			};

			MBCategoryServiceUtil.getSubcategoryIds(
				categoryIds, themeDisplay.getScopeGroupId(), searchCategoryId);

			Indexer<MBMessage> indexer = IndexerRegistryUtil.getIndexer(
				MBMessage.class);

			SearchContext searchContext = SearchContextFactory.getInstance(
				_httpServletRequest);

			searchContext.setAttribute("paginationType", "more");
			searchContext.setCategoryIds(
				StringUtil.split(StringUtil.merge(categoryIds), 0L));
			searchContext.setEnd(searchContainer.getEnd());
			searchContext.setIncludeAttachments(true);
			searchContext.setKeywords(
				ParamUtil.getString(_httpServletRequest, "keywords"));
			searchContext.setStart(searchContainer.getStart());

			Hits hits = indexer.search(searchContext);

			try {
				searchContainer.setResultsAndTotal(
					() -> SearchResultUtil.getSearchResults(
						hits, _httpServletRequest.getLocale()),
					hits.getLength());
			}
			catch (Throwable throwable) {
				throw new PortalException(throwable);
			}
		}
		else if (isShowRecentPosts()) {
			searchContainer.setEmptyResultsMessage("there-are-no-recent-posts");

			long groupThreadsUserId = ParamUtil.getLong(
				_httpServletRequest, "groupThreadsUserId");

			Calendar calendar = Calendar.getInstance();

			MBGroupServiceSettings mbGroupServiceSettings =
				MBRequestUtil.getMBGroupServiceSettings(
					_httpServletRequest, themeDisplay.getSiteGroupId());

			int offset = GetterUtil.getInteger(
				mbGroupServiceSettings.getRecentPostsDateOffset());

			calendar.add(Calendar.DATE, -offset);

			boolean includeAnonymous = false;

			if (groupThreadsUserId == themeDisplay.getUserId()) {
				includeAnonymous = true;
			}

			boolean mbIncludeAnonymous = includeAnonymous;

			try {
				searchContainer.setResultsAndTotal(
					() -> MBThreadServiceUtil.getGroupThreads(
						themeDisplay.getScopeGroupId(), groupThreadsUserId,
						calendar.getTime(), mbIncludeAnonymous,
						WorkflowConstants.STATUS_APPROVED,
						searchContainer.getStart(), searchContainer.getEnd()),
					MBThreadServiceUtil.getGroupThreadsCount(
						themeDisplay.getScopeGroupId(), groupThreadsUserId,
						calendar.getTime(), mbIncludeAnonymous,
						WorkflowConstants.STATUS_APPROVED));
			}
			catch (Throwable throwable) {
				throw new PortalException(throwable);
			}
		}
		else if (isShowMyPosts()) {
			searchContainer.setEmptyResultsMessage("you-do-not-have-any-posts");

			if (!themeDisplay.isSignedIn()) {
				try {
					searchContainer.setResultsAndTotal(
						Collections::emptyList, 0);
				}
				catch (Throwable throwable) {
					throw new PortalException(throwable);
				}

				return;
			}

			int status = WorkflowConstants.STATUS_ANY;

			try {
				searchContainer.setResultsAndTotal(
					() -> MBThreadServiceUtil.getGroupThreads(
						themeDisplay.getScopeGroupId(),
						themeDisplay.getUserId(), status,
						searchContainer.getStart(), searchContainer.getEnd()),
					MBThreadServiceUtil.getGroupThreadsCount(
						themeDisplay.getScopeGroupId(),
						themeDisplay.getUserId(), status));
			}
			catch (Throwable throwable) {
				throw new PortalException(throwable);
			}
		}
		else {
			int status = _getStatus(themeDisplay);

			try {
				searchContainer.setResultsAndTotal(
					() -> MBThreadServiceUtil.getThreads(
						themeDisplay.getScopeGroupId(), _categoryId,
						_getQueryDefinition(
							searchContainer, status, themeDisplay.getUserId())),
					MBThreadServiceUtil.getThreadsCount(
						themeDisplay.getScopeGroupId(), _categoryId,
						_getQueryDefinition(
							searchContainer, status,
							themeDisplay.getUserId())));
			}
			catch (Throwable throwable) {
				throw new PortalException(throwable);
			}
		}
	}

	@Override
	public void setCategoryEntriesDelta(SearchContainer searchContainer) {
		int categoryEntriesDelta = ParamUtil.getInteger(
			_httpServletRequest, searchContainer.getDeltaParam());

		if (categoryEntriesDelta > 0) {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					_httpServletRequest);

			portalPreferences.setValue(
				MBPortletKeys.MESSAGE_BOARDS, "categoryEntriesDelta",
				String.valueOf(categoryEntriesDelta));
		}
	}

	@Override
	public void setThreadEntriesDelta(SearchContainer searchContainer) {
		int threadEntriesDelta = ParamUtil.getInteger(
			_httpServletRequest, searchContainer.getDeltaParam());

		if (threadEntriesDelta > 0) {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					_httpServletRequest);

			portalPreferences.setValue(
				MBPortletKeys.MESSAGE_BOARDS, "threadEntriesDelta",
				String.valueOf(threadEntriesDelta));
		}
	}

	private QueryDefinition _getQueryDefinition(
		SearchContainer searchContainer, int status, long userId) {

		return new QueryDefinition<>(
			status, userId, true, searchContainer.getStart(),
			searchContainer.getEnd(), searchContainer.getOrderByComparator());
	}

	private int _getStatus(ThemeDisplay themeDisplay) {
		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId())) {

			return WorkflowConstants.STATUS_ANY;
		}

		return WorkflowConstants.STATUS_APPROVED;
	}

	private boolean _isShowRecentPosts(String mvcRenderCommandName) {
		if (mvcRenderCommandName.equals("/message_boards/view_recent_posts")) {
			return true;
		}

		String entriesNavigation = ParamUtil.getString(
			_httpServletRequest, "entriesNavigation");

		return entriesNavigation.equals("recent");
	}

	private boolean _isShowSearch(String mvcRenderCommandName) {
		if (Validator.isNotNull(
				ParamUtil.getString(_httpServletRequest, "keywords")) ||
			mvcRenderCommandName.equals("/message_boards/search")) {

			return true;
		}

		return false;
	}

	private static final UUID _UUID = UUID.fromString(
		"c29b2669-a9ce-45e3-aa4e-9ec766a4ffad");

	private final long _categoryId;
	private final HttpServletRequest _httpServletRequest;
	private final boolean _showMyPosts;
	private final boolean _showRecentPosts;
	private final boolean _showSearch;

}