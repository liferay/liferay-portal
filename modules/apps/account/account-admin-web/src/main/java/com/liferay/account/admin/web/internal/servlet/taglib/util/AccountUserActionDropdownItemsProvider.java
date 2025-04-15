/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.servlet.taglib.util;

import com.liferay.account.admin.web.internal.display.AccountEntryDisplay;
import com.liferay.account.admin.web.internal.display.AccountUserDisplay;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountEntryPermission;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountUserPermission;
import com.liferay.account.constants.AccountActionKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Albert Lee
 */
public class AccountUserActionDropdownItemsProvider {

	public AccountUserActionDropdownItemsProvider(
		AccountEntryDisplay accountEntryDisplay,
		AccountUserDisplay accountUserDisplay,
		PermissionChecker permissionChecker, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_accountEntryDisplay = accountEntryDisplay;
		_accountUserDisplay = accountUserDisplay;
		_permissionChecker = permissionChecker;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.add(
			() -> AccountUserPermission.hasEditUserPermission(
				_permissionChecker,
				PortalUtil.getPortletId(_httpServletRequest),
				_accountEntryDisplay, _accountUserDisplay.getUser()),
			dropdownItem -> {
				dropdownItem.setHref(getEditAccountUserURL());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() ->
				AccountEntryPermission.contains(
					_permissionChecker,
					_accountEntryDisplay.getAccountEntryId(),
					ActionKeys.MANAGE_USERS) &&
				AccountEntryPermission.contains(
					_permissionChecker,
					_accountEntryDisplay.getAccountEntryId(),
					AccountActionKeys.VIEW_ACCOUNT_ROLES),
			dropdownItem -> {
				dropdownItem.putData("action", "assignRoleAccountUsers");
				dropdownItem.putData(
					"assignRoleAccountUsersURL",
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCPath(
						"/account_entries_admin/select_account_roles.jsp"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"accountEntryId",
						_accountEntryDisplay.getAccountEntryId()
					).setParameter(
						"accountUserIds", _accountUserDisplay.getUserId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.putData(
					"editRoleAccountUsersURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/account_admin/set_user_account_roles"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"accountEntryId",
						_accountEntryDisplay.getAccountEntryId()
					).setParameter(
						"accountUserId", _accountUserDisplay.getUserId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "assign-roles"));
			}
		).add(
			() -> AccountEntryPermission.contains(
				_permissionChecker, _accountEntryDisplay.getAccountEntryId(),
				ActionKeys.MANAGE_USERS),
			dropdownItem -> {
				dropdownItem.putData("action", "removeAccountUsers");
				dropdownItem.putData(
					"removeAccountUsersURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/account_admin/remove_account_users"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"accountEntryId",
						_accountEntryDisplay.getAccountEntryId()
					).setParameter(
						"accountUserIds", _accountUserDisplay.getUserId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "remove"));
			}
		).build();
	}

	public String getEditAccountUserURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/account_admin/edit_account_user"
		).setBackURL(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"accountEntryId", _accountEntryDisplay.getAccountEntryId()
		).setParameter(
			"accountUserId", _accountUserDisplay.getUserId()
		).buildString();
	}

	private final AccountEntryDisplay _accountEntryDisplay;
	private final AccountUserDisplay _accountUserDisplay;
	private final HttpServletRequest _httpServletRequest;
	private final PermissionChecker _permissionChecker;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}