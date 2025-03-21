/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal.display.context;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.user.groups.admin.item.selector.UserGroupItemSelectorCriterion;
import com.liferay.user.groups.admin.item.selector.web.internal.search.UserGroupItemSelectorChecker;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class UserGroupItemSelectorViewDisplayContext {

	public UserGroupItemSelectorViewDisplayContext(
		UserGroupLocalService userGroupLocalService,
		UserGroupItemSelectorCriterion userGroupItemSelectorCriterion,
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_userGroupLocalService = userGroupLocalService;
		_userGroupItemSelectorCriterion = userGroupItemSelectorCriterion;
		_portletURL = portletURL;

		_renderRequest = (RenderRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
		_renderResponse = (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	public String getOrderByCol() {
		return ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM, "name");
	}

	public String getOrderByType() {
		return ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, "asc");
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public SearchContainer<UserGroup> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null, "no-user-groups-were-found");

		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(
			UsersAdminUtil.getUserGroupOrderByComparator(
				getOrderByCol(), getOrderByType()));
		_searchContainer.setOrderByType(getOrderByType());

		String keywords = getKeywords();

		if (_userGroupItemSelectorCriterion.isFilterManageableUserGroups()) {
			_searchContainer.setResultsAndTotal(
				UsersAdminUtil.filterUserGroups(
					_themeDisplay.getPermissionChecker(),
					UserGroupLocalServiceUtil.search(
						_themeDisplay.getCompanyId(), keywords, null,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						_searchContainer.getOrderByComparator())));
		}
		else {
			_searchContainer.setResultsAndTotal(
				() -> _userGroupLocalService.search(
					_themeDisplay.getCompanyId(), keywords, null,
					_searchContainer.getStart(), _searchContainer.getEnd(),
					_searchContainer.getOrderByComparator()),
				_userGroupLocalService.searchCount(
					_themeDisplay.getCompanyId(), keywords, null));
		}

		_searchContainer.setRowChecker(
			new UserGroupItemSelectorChecker(
				_renderResponse, _getCheckedUserGroupIds()));

		return _searchContainer;
	}

	private long[] _getCheckedUserGroupIds() {
		return ParamUtil.getLongValues(_renderRequest, "checkedUserGroupIds");
	}

	private String _keywords;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<UserGroup> _searchContainer;
	private final ThemeDisplay _themeDisplay;
	private final UserGroupItemSelectorCriterion
		_userGroupItemSelectorCriterion;
	private final UserGroupLocalService _userGroupLocalService;

}