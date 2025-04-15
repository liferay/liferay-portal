/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.users.admin.management.toolbar.FilterContributor;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;
import com.liferay.users.admin.web.internal.constants.UsersAdminWebKeys;
import com.liferay.users.admin.web.internal.util.DisplayStyleUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class ViewFlatUsersDisplayContextFactory {

	public static ViewFlatUsersDisplayContext create(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		ViewFlatUsersDisplayContext viewFlatUsersDisplayContext =
			new ViewFlatUsersDisplayContext();

		viewFlatUsersDisplayContext.setDisplayStyle(
			DisplayStyleUtil.getDisplayStyle(renderRequest, "list"));

		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(renderRequest);
		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(renderResponse);

		UserSearch searchContainer = _createSearchContainer(
			renderRequest, renderResponse);

		UserSearchTerms userSearchTerms =
			(UserSearchTerms)searchContainer.getSearchTerms();

		ManagementToolbarDisplayContext managementToolbarDisplayContext;

		if (Objects.equals(
				UsersAdminPortletKeys.SERVICE_ACCOUNTS,
				PortalUtil.getPortletId(renderRequest))) {

			managementToolbarDisplayContext =
				new ViewServiceAccountUsersManagementToolbarDisplayContext(
					liferayPortletRequest, liferayPortletResponse,
					searchContainer, _isShowDeleteButton(userSearchTerms),
					_isShowRestoreButton(userSearchTerms));
		}
		else {
			managementToolbarDisplayContext =
				new ViewFlatUsersManagementToolbarDisplayContext(
					liferayPortletRequest, liferayPortletResponse,
					searchContainer, _isShowDeleteButton(userSearchTerms),
					_isShowRestoreButton(userSearchTerms));
		}

		FilterContributor[] filterContributors = _getFilterContributors(
			httpServletRequest);

		if (filterContributors != null) {
			managementToolbarDisplayContext =
				new FiltersManagementToolbarDisplayContextWrapper(
					filterContributors, httpServletRequest,
					liferayPortletRequest, liferayPortletResponse,
					managementToolbarDisplayContext);
		}

		viewFlatUsersDisplayContext.setManagementToolbarDisplayContext(
			managementToolbarDisplayContext);

		viewFlatUsersDisplayContext.setScreenNavigationCategoryKey(
			ParamUtil.getString(
				httpServletRequest, "screenNavigationCategoryKey",
				UserScreenNavigationEntryConstants.CATEGORY_KEY_USERS));
		viewFlatUsersDisplayContext.setSearchContainer(searchContainer);
		viewFlatUsersDisplayContext.setStatus(userSearchTerms.getStatus());
		viewFlatUsersDisplayContext.setUsersListView(
			GetterUtil.getString(
				httpServletRequest.getAttribute("view.jsp-usersListView")));
		viewFlatUsersDisplayContext.setViewUsersRedirect(
			GetterUtil.getString(
				httpServletRequest.getAttribute("view.jsp-viewUsersRedirect")));

		return viewFlatUsersDisplayContext;
	}

	private static UserSearch _createSearchContainer(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		int status = GetterUtil.getInteger(
			httpServletRequest.getAttribute("view.jsp-status"));

		String navigation = ParamUtil.getString(
			httpServletRequest, "navigation", "active");

		if (navigation.equals("active")) {
			status = WorkflowConstants.STATUS_APPROVED;
		}
		else if (navigation.equals("all")) {
			status = WorkflowConstants.STATUS_ANY;
		}
		else if (navigation.equals("inactive")) {
			status = WorkflowConstants.STATUS_INACTIVE;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			(PortletURL)httpServletRequest.getAttribute("view.jsp-portletURL")
		).setNavigation(
			navigation
		).buildPortletURL();

		UserSearch userSearch = new UserSearch(
			renderRequest, "cur2", portletURL);

		userSearch.setId("users");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		searchTerms.setStatus(status);

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		FilterContributor[] filterContributors = _getFilterContributors(
			httpServletRequest);

		if (filterContributors != null) {
			for (FilterContributor filterContributor : filterContributors) {
				params.putAll(
					filterContributor.getSearchParameters(
						ParamUtil.getString(
							httpServletRequest,
							filterContributor.getParameter(),
							filterContributor.getDefaultValue())));
			}
		}

		userSearch.setResultsAndTotal(
			() -> UserLocalServiceUtil.search(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), params, userSearch.getStart(),
				userSearch.getEnd(), userSearch.getOrderByComparator()),
			UserLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), params));

		if (ListUtil.isNotEmpty(userSearch.getResults()) &&
			(_isShowDeleteButton(searchTerms) ||
			 _isShowRestoreButton(searchTerms))) {

			userSearch.setRowChecker(
				new EmptyOnClickRowChecker(renderResponse) {
					{
						setRowIds("rowIdsUser");
					}
				});
		}

		return userSearch;
	}

	private static FilterContributor[] _getFilterContributors(
		HttpServletRequest httpServletRequest) {

		return (FilterContributor[])httpServletRequest.getAttribute(
			UsersAdminWebKeys.MANAGEMENT_TOOLBAR_FILTER_CONTRIBUTORS);
	}

	private static boolean _isShowDeleteButton(
		UserSearchTerms userSearchTerms) {

		if ((userSearchTerms.getStatus() != WorkflowConstants.STATUS_ANY) &&
			(userSearchTerms.isActive() ||
			 (!userSearchTerms.isActive() && PropsValues.USERS_DELETE))) {

			return true;
		}

		return false;
	}

	private static boolean _isShowRestoreButton(
		UserSearchTerms userSearchTerms) {

		if ((userSearchTerms.getStatus() != WorkflowConstants.STATUS_ANY) &&
			!userSearchTerms.isActive()) {

			return true;
		}

		return false;
	}

}