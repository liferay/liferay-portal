/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.display.context.MBAdminListDisplayContext;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBThreadServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Sergio González
 */
public class DefaultMBAdminListDisplayContext
	implements MBAdminListDisplayContext {

	public DefaultMBAdminListDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long categoryId) {

		_httpServletRequest = httpServletRequest;
		_categoryId = categoryId;
	}

	@Override
	public String getEmptyResultsMessage() {
		if (isShowSearch()) {
			return "there-are-no-threads";
		}

		return "there-are-no-threads-or-categories";
	}

	@Override
	public int getEntriesDelta() {
		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				_httpServletRequest);

		return GetterUtil.getInteger(
			portalPreferences.getValue(
				MBPortletKeys.MESSAGE_BOARDS_ADMIN, "entriesDelta"),
			SearchContainer.DEFAULT_DELTA);
	}

	@Override
	public UUID getUuid() {
		return _UUID;
	}

	@Override
	public boolean isShowSearch() {
		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			return true;
		}

		String mvcRenderCommandName = ParamUtil.getString(
			_httpServletRequest, "mvcRenderCommandName");

		return mvcRenderCommandName.equals("/message_boards/search");
	}

	@Override
	public void populateResultsAndTotal(SearchContainer searchContainer)
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
			searchContext.setIncludeInternalAssetCategories(true);
			searchContext.setKeywords(
				ParamUtil.getString(_httpServletRequest, "keywords"));

			String orderByCol = searchContainer.getOrderByCol();
			String orderByType = searchContainer.getOrderByType();

			boolean orderByAsc = false;

			if (Objects.equals(orderByType, "asc")) {
				orderByAsc = true;
			}

			Sort sort = null;

			if (Objects.equals(orderByCol, "modified-date")) {
				sort = new Sort(
					Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
			}
			else if (Objects.equals(orderByCol, "title")) {
				sort = new Sort(
					Field.getSortableFieldName(
						"localized_title_".concat(
							themeDisplay.getLanguageId())),
					Sort.STRING_TYPE, !orderByAsc);
			}

			searchContext.setSorts(sort);

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
		else {
			String entriesNavigation = ParamUtil.getString(
				_httpServletRequest, "entriesNavigation", "all");

			if (Objects.equals(entriesNavigation, "all")) {
				int status = _getStatus(themeDisplay);

				try {
					searchContainer.setResultsAndTotal(
						() -> MBCategoryServiceUtil.getCategoriesAndThreads(
							themeDisplay.getScopeGroupId(), _categoryId,
							_getQueryDefinition(
								searchContainer, status,
								themeDisplay.getUserId())),
						MBCategoryServiceUtil.getCategoriesAndThreadsCount(
							themeDisplay.getScopeGroupId(), _categoryId,
							_getQueryDefinition(
								searchContainer, status,
								themeDisplay.getUserId())));
				}
				catch (Throwable throwable) {
					throw new PortalException(throwable);
				}
			}
			else if (Objects.equals(entriesNavigation, "threads")) {
				int status = _getStatus(themeDisplay);

				try {
					searchContainer.setResultsAndTotal(
						() -> MBThreadServiceUtil.getThreads(
							themeDisplay.getScopeGroupId(), _categoryId,
							_getQueryDefinition(
								searchContainer, status,
								themeDisplay.getUserId())),
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
			else if (Objects.equals(entriesNavigation, "categories")) {
				int status = _getStatus(themeDisplay);

				try {
					searchContainer.setResultsAndTotal(
						() -> MBCategoryServiceUtil.getCategories(
							themeDisplay.getScopeGroupId(), _categoryId,
							_getQueryDefinition(
								searchContainer, status,
								themeDisplay.getUserId())),
						MBCategoryServiceUtil.getCategoriesCount(
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
	}

	@Override
	public void setEntriesDelta(SearchContainer searchContainer) {
		int entriesDelta = ParamUtil.getInteger(
			_httpServletRequest, searchContainer.getDeltaParam());

		if (entriesDelta > 0) {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					_httpServletRequest);

			portalPreferences.setValue(
				MBPortletKeys.MESSAGE_BOARDS_ADMIN, "entriesDelta",
				String.valueOf(entriesDelta));
		}
	}

	private QueryDefinition _getQueryDefinition(
		SearchContainer searchContainer, int status, long userId) {

		return new QueryDefinition<>(
			status, userId, true, searchContainer.getStart(),
			searchContainer.getEnd(), searchContainer.getOrderByComparator());
	}

	private int _getStatus(ThemeDisplay themeDisplay) {
		int status = WorkflowConstants.STATUS_APPROVED;

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId())) {

			status = WorkflowConstants.STATUS_ANY;
		}

		return status;
	}

	private static final UUID _UUID = UUID.fromString(
		"f3efa0bd-ca31-43c5-bdfe-164ee683b39e");

	private final long _categoryId;
	private final HttpServletRequest _httpServletRequest;

}