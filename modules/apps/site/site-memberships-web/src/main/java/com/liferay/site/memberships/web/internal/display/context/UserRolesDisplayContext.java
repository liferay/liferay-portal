/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.display.context;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.admin.search.RoleSearchTerms;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;
import com.liferay.site.memberships.web.internal.util.DepotRolesUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class UserRolesDisplayContext {

	public UserRolesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
			"display-style-roles", "icon");

		return _displayStyle;
	}

	public String getEventName() {
		if (Validator.isNotNull(_eventName)) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_httpServletRequest, "eventName",
			_renderResponse.getNamespace() + "selectUsersRoles");

		return _eventName;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
			"order-by-col-roles", "title");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
			"order-by-type-roles", "asc");

		return _orderByType;
	}

	public SearchContainer<Role> getRoleSearchSearchContainer()
		throws PortalException {

		if (_roleSearch != null) {
			return _roleSearch;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		RoleSearch roleSearch = new RoleSearch(
			_renderRequest, _getPortletURL());

		Group group = GroupLocalServiceUtil.fetchGroup(_getGroupId());

		RoleSearchTerms searchTerms =
			(RoleSearchTerms)roleSearch.getSearchTerms();

		List<Role> roles = RoleLocalServiceUtil.search(
			themeDisplay.getCompanyId(), searchTerms.getKeywords(),
			new Integer[] {_getRoleType()}, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, roleSearch.getOrderByComparator());

		List<Role> selectedRoles = _getSelectedRoles();

		roles = ListUtil.filter(
			roles,
			role ->
				(_isAssignRoles() && !selectedRoles.contains(role)) ||
				(!_isAssignRoles() && selectedRoles.contains(role)));

		if (group.isDepot()) {
			roles = DepotRolesUtil.filterGroupRoles(
				themeDisplay.getPermissionChecker(), _getGroupId(), roles);
		}
		else {
			roles = UsersAdminUtil.filterGroupRoles(
				themeDisplay.getPermissionChecker(), _getGroupId(), roles);
		}

		roleSearch.setResultsAndTotal(roles);
		roleSearch.setRowChecker(new EmptyOnClickRowChecker(_renderResponse));

		_roleSearch = roleSearch;

		return _roleSearch;
	}

	private long _getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_groupId = ParamUtil.getLong(
			_httpServletRequest, "groupId",
			themeDisplay.getSiteGroupIdOrLiveGroupId());

		return _groupId;
	}

	private PortletURL _getPortletURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/users_roles.jsp"
		).setKeywords(
			() -> {
				String keywords = ParamUtil.getString(
					_renderRequest, "keywords");

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"displayStyle",
			() -> {
				String displayStyle = getDisplayStyle();

				if (Validator.isNotNull(displayStyle)) {
					return displayStyle;
				}

				return null;
			}
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = getOrderByCol();

				if (Validator.isNotNull(orderByCol)) {
					return orderByCol;
				}

				return null;
			}
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = getOrderByType();

				if (Validator.isNotNull(orderByType)) {
					return orderByType;
				}

				return null;
			}
		).setParameter(
			"p_u_i_d", _getUserId()
		).setParameter(
			"roleType",
			() -> {
				int roleType = _getRoleType();

				if (roleType > 0) {
					return roleType;
				}

				return null;
			}
		).buildPortletURL();
	}

	private int _getRoleType() {
		if (_roleType != null) {
			return _roleType;
		}

		_roleType = ParamUtil.getInteger(
			_httpServletRequest, "roleType", RoleConstants.TYPE_SITE);

		return _roleType;
	}

	private List<Role> _getSelectedRoles() throws PortalException {
		return TransformUtil.transform(
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(
				_getUserId(), _getGroupId()),
			userGroupRole -> RoleLocalServiceUtil.fetchRole(
				userGroupRole.getRoleId()));
	}

	private long _getUserId() throws PortalException {
		User selUser = PortalUtil.getSelectedUser(_httpServletRequest, false);

		if (selUser != null) {
			return selUser.getUserId();
		}

		return 0;
	}

	private boolean _isAssignRoles() {
		if (_assignRoles != null) {
			return _assignRoles;
		}

		_assignRoles = ParamUtil.getBoolean(
			_httpServletRequest, "assignRoles", true);

		return _assignRoles;
	}

	private Boolean _assignRoles;
	private String _displayStyle;
	private String _eventName;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private RoleSearch _roleSearch;
	private Integer _roleType;

}