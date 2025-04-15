/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.display.context;

import com.liferay.admin.kernel.util.PortalMyAccountApplicationType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.user.groups.admin.search.SetUserUserGroupChecker;
import com.liferay.users.admin.item.selector.UserUserGroupItemSelectorCriterion;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;

/**
 * @author Pei-Jung Lan
 */
public class UserUserGroupItemSelectorViewDisplayContext {

	public UserUserGroupItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		RenderRequest renderRequest, RenderResponse renderResponse,
		UserUserGroupItemSelectorCriterion userUserGroupItemSelectorCriterion) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_userUserGroupItemSelectorCriterion =
			userUserGroupItemSelectorCriterion;
	}

	public SearchContainer<User> getSearchContainer() {
		if (_userSearch != null) {
			return _userSearch;
		}

		UserSearch userSearch = new UserSearch(_renderRequest, _portletURL);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		LinkedHashMap<String, Object> userParams = _getParams(themeDisplay);

		userSearch.setResultsAndTotal(
			() -> UserLocalServiceUtil.search(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams, userSearch.getStart(),
				userSearch.getEnd(), userSearch.getOrderByComparator()),
			UserLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams));

		userSearch.setRowChecker(
			new SetUserUserGroupChecker(
				_renderResponse,
				_userUserGroupItemSelectorCriterion.getUserGroupId()));

		_userSearch = userSearch;

		return _userSearch;
	}

	private LinkedHashMap<String, Object> _getParams(
		ThemeDisplay themeDisplay) {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (portletName.equals(
				PortletProviderUtil.getPortletId(
					PortalMyAccountApplicationType.MyAccount.CLASS_NAME,
					PortletProvider.Action.VIEW)) ||
			permissionChecker.hasPermission(
				themeDisplay.getScopeGroup(), Organization.class.getName(),
				Organization.class.getName(), ActionKeys.VIEW) ||
			permissionChecker.hasPermission(
				themeDisplay.getScopeGroup(), User.class.getName(),
				User.class.getName(), ActionKeys.VIEW)) {

			return null;
		}

		LinkedHashMap<String, Object> userParams = new LinkedHashMap<>();

		User user = themeDisplay.getUser();

		try {
			userParams.put("usersOrgsTree", user.getOrganizations());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}

		return userParams;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserUserGroupItemSelectorViewDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private UserSearch _userSearch;
	private final UserUserGroupItemSelectorCriterion
		_userUserGroupItemSelectorCriterion;

}