/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Marta Medio
 */
public class UserOAuth2ItemSelectorViewDisplayContext {

	public UserOAuth2ItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
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
			themeDisplay.getSiteGroupIdOrLiveGroupId());

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		LinkedHashMap<String, Object> userParams =
			LinkedHashMapBuilder.<String, Object>put(
				"types",
				new long[] {
					UserConstants.TYPE_DEFAULT_SERVICE_ACCOUNT,
					UserConstants.TYPE_REGULAR,
					UserConstants.TYPE_SERVICE_ACCOUNT
				}
			).build();

		if (group.isLimitedToParentSiteMembers()) {
			userParams.put("inherit", Boolean.TRUE);
			userParams.put("usersGroups", group.getParentGroupId());
		}

		List<User> users = UserLocalServiceUtil.search(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			searchTerms.getStatus(), userParams, userSearch.getStart(),
			userSearch.getEnd(), userSearch.getOrderByComparator());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_themeDisplay.getUser());

		users.removeIf(
			user -> {
				try {
					return !UserPermissionUtil.contains(
						permissionChecker, user.getUserId(),
						user.getOrganizationIds(), ActionKeys.IMPERSONATE);
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}

					return false;
				}
			});

		userSearch.setResultsAndTotal(
			() -> users,
			UserLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams));

		_userSearch = userSearch;

		return _userSearch;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserOAuth2ItemSelectorViewDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;
	private UserSearch _userSearch;

}