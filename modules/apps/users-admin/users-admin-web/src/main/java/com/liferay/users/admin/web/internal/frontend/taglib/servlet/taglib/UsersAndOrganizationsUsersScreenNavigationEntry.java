/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class UsersAndOrganizationsUsersScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	@Override
	public String getCategoryKey() {
		return UserScreenNavigationEntryConstants.CATEGORY_KEY_USERS;
	}

	@Override
	public String getEntryKey() {
		return UserScreenNavigationEntryConstants.CATEGORY_KEY_USERS;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "users");
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_USERS_AND_ORGANIZATIONS;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			"view.jsp-backURL",
			ParamUtil.getString(
				httpServletRequest, "backURL",
				ParamUtil.getString(httpServletRequest, "redirect")));
		httpServletRequest.setAttribute(
			"view.jsp-status",
			ParamUtil.getInteger(
				httpServletRequest, "status",
				WorkflowConstants.STATUS_APPROVED));
		httpServletRequest.setAttribute(
			"view.jsp-usersListView", UserConstants.LIST_VIEW_FLAT_USERS);
		httpServletRequest.setAttribute(
			"view.jsp-viewUsersRedirect",
			ParamUtil.getString(httpServletRequest, "viewUsersRedirect"));

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse, "/view_flat_users.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

}