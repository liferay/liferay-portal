/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.account.admin.web.internal.constants.AccountScreenNavigationEntryConstants;
import com.liferay.account.admin.web.internal.constants.AccountWebKeys;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Lee
 */
public abstract class BaseAccountUserScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	public abstract String getActionCommandName();

	public abstract String getJspPath();

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(getResourceBundle(locale), getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return AccountScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_ACCOUNT_USER;
	}

	@Override
	public boolean isVisible(User user, User selUser) {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		if (UserPermissionUtil.contains(
				permissionChecker, selUser.getUserId(), ActionKeys.VIEW) &&
			UserPermissionUtil.contains(
				permissionChecker, selUser.getUserId(), ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			AccountWebKeys.ACTION_COMMAND_NAME, getActionCommandName());
		httpServletRequest.setAttribute(AccountWebKeys.EDITABLE, Boolean.TRUE);
		httpServletRequest.setAttribute(
			AccountWebKeys.FORM_LABEL,
			getLabel(httpServletRequest.getLocale()));
		httpServletRequest.setAttribute(AccountWebKeys.JSP_PATH, getJspPath());
		httpServletRequest.setAttribute(
			AccountWebKeys.SHOW_CONTROLS, isShowControls());
		httpServletRequest.setAttribute(
			AccountWebKeys.SHOW_TITLE, isShowTitle());

		DynamicServletRequest dynamicServletRequest = new DynamicServletRequest(
			httpServletRequest);

		dynamicServletRequest.appendParameter(
			"redirect", portal.getCurrentURL(httpServletRequest));

		jspRenderer.renderJSP(
			servletContext, dynamicServletRequest, httpServletResponse,
			"/edit_user_navigation.jsp");
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			resourceBundle, portal.getResourceBundle(locale));
	}

	protected boolean isShowControls() {
		return true;
	}

	protected boolean isShowTitle() {
		return true;
	}

	@Reference
	protected JSPRenderer jspRenderer;

	@Reference
	protected Portal portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.users.admin.web)")
	protected ServletContext servletContext;

}