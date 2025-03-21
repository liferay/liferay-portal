/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.helper;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.display.context.logic.PersonalMenuEntryHelper;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntryRegistry;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;
import com.liferay.roles.admin.panel.category.role.type.mapper.PanelCategoryRoleTypeMapperRegistry;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = AccountRoleRequestHelper.class)
public class AccountRoleRequestHelper {

	public void setRequestAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY, _panelAppRegistry);
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_HELPER,
			new PanelCategoryHelper(_panelAppRegistry));
		httpServletRequest.setAttribute(
			ApplicationListWebKeys.PERSONAL_MENU_ENTRY_HELPER,
			new PersonalMenuEntryHelper(
				_personalMenuEntryRegistry.getPersonalMenuEntries()));
		httpServletRequest.setAttribute(
			RolesAdminWebKeys.CURRENT_ROLE_TYPE, _accountRoleTypeContributor);
		httpServletRequest.setAttribute(
			RolesAdminWebKeys.PANEL_CATEGORY_KEYS,
			_panelCategoryRoleTypeMapperRegistry.getPanelCategoryKeys(
				RoleConstants.TYPE_ACCOUNT));
		httpServletRequest.setAttribute(
			RolesAdminWebKeys.SHOW_NAV_TABS, Boolean.FALSE);
	}

	public void setRequestAttributes(PortletRequest portletRequest) {
		setRequestAttributes(_portal.getHttpServletRequest(portletRequest));
	}

	@Reference(
		target = "(component.name=com.liferay.account.internal.roles.admin.role.type.contributor.AccountRoleTypeContributor)"
	)
	private RoleTypeContributor _accountRoleTypeContributor;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private PanelCategoryRoleTypeMapperRegistry
		_panelCategoryRoleTypeMapperRegistry;

	@Reference
	private PersonalMenuEntryRegistry _personalMenuEntryRegistry;

	@Reference
	private Portal _portal;

}