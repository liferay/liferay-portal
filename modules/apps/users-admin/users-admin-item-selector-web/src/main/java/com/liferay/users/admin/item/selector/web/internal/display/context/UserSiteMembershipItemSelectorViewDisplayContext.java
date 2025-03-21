/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.item.selector.UserSiteMembershipItemSelectorCriterion;
import com.liferay.users.admin.item.selector.web.internal.search.UserSiteMembershipChecker;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;

/**
 * @author Eudaldo Alonso
 */
public class UserSiteMembershipItemSelectorViewDisplayContext {

	public UserSiteMembershipItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		RenderRequest renderRequest, RenderResponse renderResponse,
		UserSiteMembershipItemSelectorCriterion
			userSiteMembershipItemSelectorCriterion) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_userSiteMembershipItemSelectorCriterion =
			userSiteMembershipItemSelectorCriterion;
	}

	public SearchContainer<User> getUserSearchContainer() {
		if (_userSearch != null) {
			return _userSearch;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		UserSearch userSearch = new UserSearch(_renderRequest, _portletURL);

		Group group = GroupLocalServiceUtil.fetchGroup(
			_userSiteMembershipItemSelectorCriterion.getGroupId());

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		LinkedHashMap<String, Object> userParams = new LinkedHashMap<>();

		if (group.isLimitedToParentSiteMembers()) {
			userParams.put("inherit", Boolean.TRUE);
			userParams.put("usersGroups", group.getParentGroupId());
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			if (permissionChecker.isGroupAdmin(
					_userSiteMembershipItemSelectorCriterion.getGroupId())) {

				PermissionThreadLocal.setPermissionChecker(null);
			}

			userSearch.setResultsAndTotal(
				() -> UserLocalServiceUtil.search(
					themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), userParams, userSearch.getStart(),
					userSearch.getEnd(), userSearch.getOrderByComparator()),
				UserLocalServiceUtil.searchCount(
					themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), userParams));
			userSearch.setRowChecker(
				new UserSiteMembershipChecker(_renderResponse, group));

			_userSearch = userSearch;
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}

		return _userSearch;
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private UserSearch _userSearch;
	private final UserSiteMembershipItemSelectorCriterion
		_userSiteMembershipItemSelectorCriterion;

}