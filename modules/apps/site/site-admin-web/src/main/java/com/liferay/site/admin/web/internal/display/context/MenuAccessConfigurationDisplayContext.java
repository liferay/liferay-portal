/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.item.selector.RegularRoleItemSelectorCriterion;
import com.liferay.roles.item.selector.SiteRoleItemSelectorCriterion;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mikel Lorza
 */
public class MenuAccessConfigurationDisplayContext {

	public MenuAccessConfigurationDisplayContext(
		HttpServletRequest httpServletRequest, ItemSelector itemSelector,
		MenuAccessConfigurationManager menuAccessConfigurationManager,
		Portal portal, RoleLocalService roleLocalService) {

		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;
		_menuAccessConfigurationManager = menuAccessConfigurationManager;
		_roleLocalService = roleLocalService;

		_liferayPortletRequest = portal.getLiferayPortletRequest(
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST));
		_liferayPortletResponse = portal.getLiferayPortletResponse(
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE));
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getEventName() {
		return _liferayPortletResponse.getNamespace() + "selectRole";
	}

	public String getRoleItemSelectorURL() throws Exception {
		List<ItemSelectorCriterion> itemSelectorCriteria = new ArrayList<>();

		RegularRoleItemSelectorCriterion regularRoleItemSelectorCriterion =
			new RegularRoleItemSelectorCriterion();

		String[] roleIds =
			_menuAccessConfigurationManager.getAccessToControlMenuRoleIds(
				_themeDisplay.getScopeGroupId());

		long[] checkedRoleIds = new long[roleIds.length];

		for (int i = 0; i < roleIds.length; i++) {
			Role role = _roleLocalService.fetchRole(
				GetterUtil.getLong(roleIds[i]));

			if (role != null) {
				checkedRoleIds[i] = role.getRoleId();
			}
		}

		regularRoleItemSelectorCriterion.setCheckedRoleIds(checkedRoleIds);

		regularRoleItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.singletonList(new UUIDItemSelectorReturnType()));
		regularRoleItemSelectorCriterion.setExcludedRoleNames(
			new String[] {
				RoleConstants.ADMINISTRATOR, RoleConstants.GUEST,
				RoleConstants.OWNER
			});

		itemSelectorCriteria.add(regularRoleItemSelectorCriterion);

		SiteRoleItemSelectorCriterion siteRoleItemSelectorCriterion =
			new SiteRoleItemSelectorCriterion();

		siteRoleItemSelectorCriterion.setCheckedRoleIds(checkedRoleIds);
		siteRoleItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.singletonList(new UUIDItemSelectorReturnType()));
		siteRoleItemSelectorCriterion.setExcludedRoleNames(
			new String[] {
				RoleConstants.SITE_ADMINISTRATOR, RoleConstants.SITE_OWNER
			});

		itemSelectorCriteria.add(siteRoleItemSelectorCriterion);

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				getEventName(),
				itemSelectorCriteria.toArray(new ItemSelectorCriterion[0]))
		).buildString();
	}

	public SearchContainer<Role> getSearchContainer() throws Exception {
		PortletURL currentURL = PortletURLUtil.getCurrent(
			_liferayPortletRequest, _liferayPortletResponse);

		SearchContainer<Role> searchContainer = new RoleSearch(
			_liferayPortletRequest, currentURL);

		searchContainer.setEmptyResultsMessage("no-roles-selected");

		List<Role> roles = new ArrayList<>();

		String[] accessToControlMenuRoleIds =
			_menuAccessConfigurationManager.getAccessToControlMenuRoleIds(
				_themeDisplay.getScopeGroupId());

		for (String roleId : accessToControlMenuRoleIds) {
			Role role = _roleLocalService.fetchRole(GetterUtil.getLong(roleId));

			if (role != null) {
				roles.add(role);
			}
		}

		Role administratorRole = _roleLocalService.getRole(
			_themeDisplay.getCompanyId(), RoleConstants.ADMINISTRATOR);

		if (!ArrayUtil.contains(
				accessToControlMenuRoleIds,
				String.valueOf(administratorRole.getRoleId()))) {

			roles.add(administratorRole);
		}

		Role siteAdministratorRole = _roleLocalService.getRole(
			_themeDisplay.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		if (!ArrayUtil.contains(
				accessToControlMenuRoleIds,
				String.valueOf(siteAdministratorRole.getRoleId()))) {

			roles.add(siteAdministratorRole);
		}

		searchContainer.setResultsAndTotal(roles);

		return searchContainer;
	}

	public boolean isShowControlMenuByRole() throws Exception {
		return _menuAccessConfigurationManager.isShowControlMenuByRole(
			_themeDisplay.getScopeGroupId());
	}

	public boolean isShowDeleteButton(Role role) throws Exception {
		if (role == null) {
			return true;
		}

		if (RoleConstants.ADMINISTRATOR.equals(role.getName()) ||
			RoleConstants.SITE_ADMINISTRATOR.equals(role.getName())) {

			return false;
		}

		return true;
	}

	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final MenuAccessConfigurationManager
		_menuAccessConfigurationManager;
	private final RoleLocalService _roleLocalService;
	private final ThemeDisplay _themeDisplay;

}