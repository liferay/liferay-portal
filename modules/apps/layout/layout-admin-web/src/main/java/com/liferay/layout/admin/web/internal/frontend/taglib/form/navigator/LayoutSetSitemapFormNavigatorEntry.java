/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.form.navigator;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants;
import com.liferay.layout.admin.web.internal.constants.LayoutAdminFormNavigatorConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = "form.navigator.entry.order:Integer=160",
	service = FormNavigatorEntry.class
)
public class LayoutSetSitemapFormNavigatorEntry
	extends BaseLayoutSetFormNavigatorEntry {

	@Override
	public String getCategoryKey() {
		return FormNavigatorConstants.CATEGORY_KEY_LAYOUT_SET_ADVANCED;
	}

	@Override
	public String getFormNavigatorId() {
		return LayoutAdminFormNavigatorConstants.
			FORM_NAVIGATOR_ID_LAYOUT_SET_ADVANCED;
	}

	@Override
	public String getKey() {
		return "sitemap";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, LayoutSet layoutSet) {
		if (layoutSet.isPrivateLayout()) {
			return false;
		}

		return super.isVisible(user, layoutSet);
	}

	@Override
	protected String getJspPath() {
		return "/layout_set/sitemap.jsp";
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.admin.web)")
	private ServletContext _servletContext;

}