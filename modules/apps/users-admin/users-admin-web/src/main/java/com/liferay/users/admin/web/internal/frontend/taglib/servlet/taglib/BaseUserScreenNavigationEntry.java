/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.web.internal.constants.UsersAdminWebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
public abstract class BaseUserScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	public abstract String getActionCommandName();

	public abstract String getJspPath();

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(_getResourceBundle(locale), getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.SCREEN_NAVIGATION_KEY_USERS;
	}

	public boolean isEditable(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return true;
	}

	public boolean isShowControls() {
		return true;
	}

	public boolean isShowTitle() {
		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (getJspPath() == null) {
			return;
		}

		httpServletRequest.setAttribute(
			UsersAdminWebKeys.ACTION_COMMAND_NAME, getActionCommandName());
		httpServletRequest.setAttribute(
			UsersAdminWebKeys.EDITABLE,
			isEditable(httpServletRequest, httpServletResponse));
		httpServletRequest.setAttribute(
			UsersAdminWebKeys.FORM_LABEL,
			getLabel(httpServletRequest.getLocale()));
		httpServletRequest.setAttribute(
			UsersAdminWebKeys.JSP_PATH, getJspPath());
		httpServletRequest.setAttribute(
			UsersAdminWebKeys.SHOW_CONTROLS, isShowControls());
		httpServletRequest.setAttribute(
			UsersAdminWebKeys.SHOW_TITLE, isShowTitle());

		jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/edit_user_navigation.jsp");
	}

	@Reference
	protected JSPRenderer jspRenderer;

	@Reference
	protected Portal portal;

	private ResourceBundle _getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			resourceBundle, PortalUtil.getResourceBundle(locale));
	}

}