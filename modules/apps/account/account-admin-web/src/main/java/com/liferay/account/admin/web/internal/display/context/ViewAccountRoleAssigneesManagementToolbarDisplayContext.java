/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.admin.web.internal.display.AccountUserDisplay;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountRolePermission;
import com.liferay.account.constants.AccountActionKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class ViewAccountRoleAssigneesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewAccountRoleAssigneesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<AccountUserDisplay> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		if (!_hasAssignUsersPermission()) {
			return null;
		}

		return DropdownItemList.of(
			DropdownItemBuilder.putData(
				"action", "removeUsers"
			).putData(
				"removeUsersURL",
				PortletURLBuilder.createActionURL(
					liferayPortletResponse
				).setActionName(
					"/account_admin/remove_account_role_users"
				).setRedirect(
					currentURLObj
				).setParameter(
					"accountEntryId",
					ParamUtil.getString(httpServletRequest, "accountEntryId")
				).setParameter(
					"accountRoleId",
					ParamUtil.getString(httpServletRequest, "accountRoleId")
				).buildString()
			).setIcon(
				"times-circle"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "remove")
			).setQuickAction(
				true
			).build());
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "selectAccountUsers");
				dropdownItem.putData(
					"assignAccountUsersURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/account_admin/assign_account_role_users"
					).setRedirect(
						currentURLObj
					).buildString());
				dropdownItem.putData(
					"selectAccountUsersURL",
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/account_entries_admin/select_account_users.jsp"
					).setParameter(
						"accountEntryId",
						ParamUtil.getString(
							httpServletRequest, "accountEntryId")
					).setParameter(
						"accountRoleId",
						ParamUtil.getString(httpServletRequest, "accountRoleId")
					).setParameter(
						"showFilter", Boolean.FALSE
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "assign-users"));
			}
		).build();
	}

	@Override
	public PortletURL getPortletURL() {
		try {
			return PortletURLUtil.clone(currentURLObj, liferayPortletResponse);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return liferayPortletResponse.createRenderURL();
		}
	}

	@Override
	public Boolean isShowCreationMenu() {
		return _hasAssignUsersPermission();
	}

	@Override
	protected String getOrderByCol() {
		return ParamUtil.getString(
			liferayPortletRequest, getOrderByColParam(), "last-name");
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"first-name", "last-name", "email-address"};
	}

	private boolean _hasAssignUsersPermission() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return AccountRolePermission.contains(
			themeDisplay.getPermissionChecker(),
			ParamUtil.getLong(httpServletRequest, "accountRoleId"),
			AccountActionKeys.ASSIGN_USERS);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAccountRoleAssigneesManagementToolbarDisplayContext.class);

}