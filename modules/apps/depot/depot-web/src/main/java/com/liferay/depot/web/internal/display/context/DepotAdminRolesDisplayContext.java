/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.admin.kernel.util.PortalMyAccountApplicationType;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.UserGroupRolePermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.WindowStateException;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Cristina González
 */
public class DepotAdminRolesDisplayContext {

	public DepotAdminRolesDisplayContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_permissionChecker = _themeDisplay.getPermissionChecker();

		_user = PortalUtil.getSelectedUser(liferayPortletRequest);
	}

	public String getAssetLibraryLabel() {
		return ResourceBundleUtil.getString(
			ResourceBundleUtil.getBundle(_themeDisplay.getLocale(), getClass()),
			"asset-library");
	}

	public String getDepotRoleSyncEntitiesEventName() {
		return PortalUtil.getPortletNamespace(DepotPortletKeys.DEPOT_ADMIN) +
			"syncDepotRoles";
	}

	public String getLabel() {
		return ResourceBundleUtil.getString(
			ResourceBundleUtil.getBundle(_themeDisplay.getLocale(), getClass()),
			"asset-library-roles");
	}

	public String getSelectDepotRolesEventName() {
		return PortalUtil.getPortletNamespace(DepotPortletKeys.DEPOT_ADMIN) +
			"selectDepotRole";
	}

	public String getSelectDepotRolesURL() throws WindowStateException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				DepotPortletKeys.DEPOT_ADMIN)
		).setMVCRenderCommandName(
			"/depot/select_depot_role"
		).setParameter(
			"p_u_i_d",
			() -> {
				if (_user == null) {
					return "0";
				}

				return String.valueOf(_user.getUserId());
			}
		).setParameter(
			"step", "1"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<UserGroupRole> getUserGroupRoles(int start, int end)
		throws PortalException {

		return ListUtil.subList(_getUserGroupRoles(), start, end);
	}

	public int getUserGroupRolesCount() throws PortalException {
		List<UserGroupRole> userGroupRoles = _getUserGroupRoles();

		return userGroupRoles.size();
	}

	public boolean isDeletable() {
		return isSelectable();
	}

	public boolean isSelectable() {
		String myAccountPortletId = PortletProviderUtil.getPortletId(
			PortalMyAccountApplicationType.MyAccount.CLASS_NAME,
			PortletProvider.Action.VIEW);

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return !Objects.equals(
			portletDisplay.getPortletName(), myAccountPortletId);
	}

	private boolean _contains(UserGroupRole userGroupRole) {
		try {
			Group group = userGroupRole.getGroup();
			Role role = userGroupRole.getRole();

			if (!_permissionChecker.isCompanyAdmin() &&
				!_permissionChecker.isGroupOwner(group.getGroupId())) {

				String roleName = role.getName();

				if (Objects.equals(
						roleName,
						DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR) ||
					Objects.equals(
						roleName, DepotRolesConstants.ASSET_LIBRARY_OWNER)) {

					return false;
				}
			}

			return UserGroupRolePermissionUtil.contains(
				_permissionChecker, group, role);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return false;
	}

	private List<UserGroupRole> _getUserGroupRoles() throws PortalException {
		if (_userGroupRoles != null) {
			return _userGroupRoles;
		}

		if (_user == null) {
			_userGroupRoles = Collections.emptyList();

			return _userGroupRoles;
		}

		List<UserGroupRole> userGroupRoles = ListUtil.filter(
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(_user.getUserId()),
			this::_isDepotRole);

		if (!_isFilterManageableUserGroups()) {
			_userGroupRoles = userGroupRoles;

			return _userGroupRoles;
		}

		List<UserGroupRole> filteredUserGroupRoles = ListUtil.copy(
			userGroupRoles);

		Iterator<UserGroupRole> iterator = filteredUserGroupRoles.iterator();

		while (iterator.hasNext()) {
			UserGroupRole userGroupRole = iterator.next();

			Role role = userGroupRole.getRole();

			if (Objects.equals(
					role.getName(), DepotRolesConstants.ASSET_LIBRARY_MEMBER)) {

				iterator.remove();
			}
		}

		if (_permissionChecker.isCompanyAdmin()) {
			return filteredUserGroupRoles;
		}

		iterator = filteredUserGroupRoles.iterator();

		while (iterator.hasNext()) {
			if (!_contains(iterator.next())) {
				iterator.remove();
			}
		}

		_userGroupRoles = filteredUserGroupRoles;

		return _userGroupRoles;
	}

	private boolean _isDepotRole(UserGroupRole userGroupRole) {
		try {
			Group group = userGroupRole.getGroup();
			Role role = userGroupRole.getRole();

			if ((group != null) && group.isDepot() && (role != null) &&
				(role.getType() == RoleConstants.TYPE_DEPOT)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return false;
	}

	private boolean _isFilterManageableUserGroups() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String myAccountPortletId = PortletProviderUtil.getPortletId(
			PortalMyAccountApplicationType.MyAccount.CLASS_NAME,
			PortletProvider.Action.VIEW);

		if (Objects.equals(
				portletDisplay.getPortletName(), myAccountPortletId)) {

			return false;
		}
		else if (_permissionChecker.isCompanyAdmin()) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotAdminRolesDisplayContext.class);

	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final PermissionChecker _permissionChecker;
	private final ThemeDisplay _themeDisplay;
	private final User _user;
	private List<UserGroupRole> _userGroupRoles;

}