/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.admin.kernel.util.PortalMyAccountApplicationType;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Cristina González
 */
public class DepotAdminMembershipsDisplayContext {

	public DepotAdminMembershipsDisplayContext(
			ItemSelector itemSelector,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		_itemSelector = itemSelector;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
		_user = PortalUtil.getSelectedUser(liferayPortletRequest);
	}

	public List<Group> getDepotGroups(int start, int end)
		throws PortalException {

		return ListUtil.subList(_getDepotGroups(), start, end);
	}

	public int getDepotGroupsCount() throws PortalException {
		List<Group> groups = _getDepotGroups();

		return groups.size();
	}

	public List<Group> getInheritedDepotGroups() throws PortalException {
		if (_inheritedDepotGroups != null) {
			return _inheritedDepotGroups;
		}

		if (_user == null) {
			_inheritedDepotGroups = Collections.emptyList();

			return _inheritedDepotGroups;
		}

		List<Group> groups = new ArrayList<>();
		List<Organization> organizations = _user.getOrganizations();

		if (!organizations.isEmpty()) {
			groups.addAll(
				ListUtil.filter(
					GroupLocalServiceUtil.getOrganizationsRelatedGroups(
						organizations),
					Group::isDepot));
		}

		List<UserGroup> userGroups = _user.getUserGroups();

		if (!userGroups.isEmpty()) {
			groups.addAll(
				ListUtil.filter(
					GroupLocalServiceUtil.getUserGroupsRelatedGroups(
						userGroups),
					Group::isDepot));
		}

		_inheritedDepotGroups = ListUtil.unique(groups);

		return _inheritedDepotGroups;
	}

	public int getInheritedDepotGroupsCount() throws PortalException {
		List<Group> groups = getInheritedDepotGroups();

		return groups.size();
	}

	public String getItemSelectorEventName() {
		return _liferayPortletResponse.getNamespace() + "selectDepotGroup";
	}

	public PortletURL getItemSelectorURL() {
		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion();

		groupItemSelectorCriterion.setDepotEntryType(
			DepotConstants.TYPE_ASSET_LIBRARY);
		groupItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new GroupItemSelectorReturnType());
		groupItemSelectorCriterion.setIncludeAllVisibleGroups(true);

		return _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest),
			getItemSelectorEventName(), groupItemSelectorCriterion);
	}

	public String getLabel() {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			_themeDisplay.getLocale(), getClass());

		return ResourceBundleUtil.getString(resourceBundle, "asset-libraries");
	}

	public String getRoles(Group group) {
		if (_user == null) {
			return StringPool.BLANK;
		}

		return UsersAdminUtil.getUserColumnText(
			_themeDisplay.getLocale(),
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(
				_user.getUserId(), group.getGroupId(), 0,
				PropsValues.USERS_ADMIN_ROLE_COLUMN_LIMIT),
			UsersAdminUtil.USER_GROUP_ROLE_TITLE_ACCESSOR,
			UserGroupRoleLocalServiceUtil.getUserGroupRolesCount(
				_user.getUserId(), group.getGroupId()));
	}

	public User getUser() {
		return _user;
	}

	public boolean isDeletable() {
		return isSelectable();
	}

	public boolean isInheritedDepotGroupsEmpty() throws PortalException {
		List<Group> groups = getInheritedDepotGroups();

		return groups.isEmpty();
	}

	public boolean isSelectable() {
		String myAccountPortletId = PortletProviderUtil.getPortletId(
			PortalMyAccountApplicationType.MyAccount.CLASS_NAME,
			PortletProvider.Action.VIEW);

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return !Objects.equals(
			portletDisplay.getPortletName(), myAccountPortletId);
	}

	private List<Group> _getDepotGroups() throws PortalException {
		if (_depotGroups != null) {
			return _depotGroups;
		}

		if (_user == null) {
			_depotGroups = Collections.emptyList();

			return _depotGroups;
		}

		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		List<Group> groups = ListUtil.copy(_user.getGroups());

		Iterator<Group> iterator = groups.iterator();

		while (iterator.hasNext()) {
			Group group = iterator.next();

			if (!group.isDepot()) {
				iterator.remove();
			}
			else if (!permissionChecker.isCompanyAdmin() &&
					 !GroupPermissionUtil.contains(
						 permissionChecker, group, ActionKeys.VIEW)) {

				iterator.remove();
			}
		}

		_depotGroups = groups;

		return _depotGroups;
	}

	private List<Group> _depotGroups;
	private List<Group> _inheritedDepotGroups;
	private final ItemSelector _itemSelector;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;
	private final User _user;

}