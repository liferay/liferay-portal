/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.account.admin.web.internal.constants.AccountScreenNavigationEntryConstants;
import com.liferay.account.admin.web.internal.helper.AccountRoleRequestHelper;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountRolePermission;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
public abstract class BaseAccountRoleDefinePermissionsScreenNavigationEntry
	extends BaseAccountRoleDefinePermissionsScreenNavigationCategory
	implements ScreenNavigationEntry<AccountRole> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, AccountRole accountRole) {
		if (accountRole == null) {
			return false;
		}

		Role role = roleLocalService.fetchRole(accountRole.getRoleId());

		if ((role != null) && AccountRoleConstants.isSharedRole(role)) {
			return false;
		}

		return AccountRolePermission.contains(
			PermissionCheckerFactoryUtil.create(user),
			accountRole.getAccountRoleId(), ActionKeys.DEFINE_PERMISSIONS);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		accountRoleRequestHelper.setRequestAttributes(httpServletRequest);

		DynamicServletRequest dynamicServletRequest = new DynamicServletRequest(
			httpServletRequest);

		dynamicServletRequest.appendParameter(Constants.CMD, Constants.VIEW);
		dynamicServletRequest.appendParameter("tabs1", doGetTabs1());
		dynamicServletRequest.appendParameter(
			"redirect", _getRedirect(httpServletRequest));
		dynamicServletRequest.appendParameter(
			"backURL", _getBackURL(httpServletRequest));
		dynamicServletRequest.appendParameter(
			"accountRoleGroupScope",
			String.valueOf(doIsAccountRoleGroupScope()));

		AccountRole accountRole = accountRoleLocalService.fetchAccountRole(
			ParamUtil.getLong(httpServletRequest, "accountRoleId"));

		dynamicServletRequest.appendParameter(
			"roleId", String.valueOf(accountRole.getRoleId()));

		jspRenderer.renderJSP(
			servletContext, dynamicServletRequest, httpServletResponse,
			"/edit_role_permissions.jsp");
	}

	protected abstract String doGetTabs1();

	protected abstract boolean doIsAccountRoleGroupScope();

	@Override
	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			resourceBundle, portal.getResourceBundle(locale));
	}

	@Reference
	protected AccountRoleLocalService accountRoleLocalService;

	@Reference
	protected AccountRoleRequestHelper accountRoleRequestHelper;

	@Reference
	protected JSPRenderer jspRenderer;

	@Reference
	protected RoleLocalService roleLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.roles.admin.web)")
	protected ServletContext servletContext;

	private String _getBackURL(HttpServletRequest httpServletRequest) {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				httpServletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/account_admin/edit_account_entry"
		).setParameter(
			"accountEntryId",
			ParamUtil.getString(httpServletRequest, "accountEntryId")
		).setParameter(
			"screenNavigationCategoryKey",
			AccountScreenNavigationEntryConstants.CATEGORY_KEY_ROLES
		).buildString();
	}

	private String _getRedirect(HttpServletRequest httpServletRequest) {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				httpServletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/account_admin/edit_account_role"
		).setParameter(
			"accountEntryId",
			ParamUtil.getString(httpServletRequest, "accountEntryId")
		).setParameter(
			"accountRoleId",
			ParamUtil.getString(httpServletRequest, "accountRoleId")
		).setParameter(
			"screenNavigationCategoryKey", getCategoryKey()
		).buildString();
	}

}