/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategory;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategoryProvider;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntryProvider;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.layout.admin.constants.LayoutScreenNavigationEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseLayoutScreenNavigationEntry
	implements ScreenNavigationEntry<Layout> {

	@Override
	public String getCategoryKey() {
		return LayoutScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL;
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return LayoutScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_LAYOUT;
	}

	@Override
	public boolean isVisible(User user, Layout layout) {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryLocalService.
				fetchLayoutUtilityPageEntryByPlid(layout.getPlid());

		if (layoutUtilityPageEntry != null) {
			return false;
		}

		List<FormNavigatorCategory> formNavigatorCategories =
			formNavigatorCategoryProvider.getFormNavigatorCategories(
				FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT);

		for (FormNavigatorCategory formNavigatorCategory :
				formNavigatorCategories) {

			if (ListUtil.isNotEmpty(
					formNavigatorEntryProvider.getFormNavigatorEntries(
						FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT,
						formNavigatorCategory.getKey(), user, layout))) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse, getJspPath());
	}

	protected abstract String getJspPath();

	@Reference
	protected FormNavigatorCategoryProvider formNavigatorCategoryProvider;

	@Reference
	protected FormNavigatorEntryProvider formNavigatorEntryProvider;

	@Reference
	protected JSPRenderer jspRenderer;

	@Reference
	protected Language language;

	@Reference
	protected LayoutUtilityPageEntryLocalService
		layoutUtilityPageEntryLocalService;

}