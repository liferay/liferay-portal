/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.form.navigator;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "form.navigator.entry.order:Integer=400",
	service = FormNavigatorEntry.class
)
public class LayoutBasicInfoFormNavigatorEntry
	extends BaseLayoutFormNavigatorEntry {

	@Override
	public String getCategoryKey() {
		return FormNavigatorConstants.CATEGORY_KEY_LAYOUT_GENERAL;
	}

	@Override
	public String getKey() {
		return "basic-info";
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, Layout layout) {
		Group group = layout.getGroup();

		if ((layout.isDraftLayout() || layout.isSystem()) &&
			!group.isLayoutSetPrototype()) {

			return false;
		}

		if (layout.isTypeAssetDisplay() || layout.isTypeEmpty() ||
			layout.isTypeUtility() ||
			(layout.isTypeContent() && (layout.fetchDraftLayout() == null))) {

			return false;
		}

		return true;
	}

	@Override
	protected String getJspPath() {
		return "/layout/basic_info.jsp";
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.admin.web)")
	private ServletContext _servletContext;

}