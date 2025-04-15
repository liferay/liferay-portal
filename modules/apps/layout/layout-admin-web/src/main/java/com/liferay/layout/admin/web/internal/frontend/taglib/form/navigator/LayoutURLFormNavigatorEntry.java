/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.form.navigator;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "form.navigator.entry.order:Integer=200",
	service = FormNavigatorEntry.class
)
public class LayoutURLFormNavigatorEntry extends BaseLayoutFormNavigatorEntry {

	@Override
	public String getCategoryKey() {
		return FormNavigatorConstants.CATEGORY_KEY_LAYOUT_GENERAL;
	}

	@Override
	public String getKey() {
		return "url";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, Layout layout) {
		if (layout.isTypeAssetDisplay() || layout.isTypeUtility()) {
			return true;
		}

		LayoutType layoutType = layout.getLayoutType();

		if (layout.isDraftLayout() || layout.isSystem() ||
			!layoutType.isURLFriendliable()) {

			return false;
		}

		if (layout.isTypeAssetDisplay() ||
			(layout.isTypeContent() && (layout.fetchDraftLayout() == null))) {

			return false;
		}

		return true;
	}

	@Override
	protected String getJspPath() {
		return "/layout/url.jsp";
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.admin.web)")
	private ServletContext _servletContext;

}