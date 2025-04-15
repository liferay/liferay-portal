/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.servlet.taglib;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Julio Camarero
 */
public class PanelCategoryBodyTag extends BasePanelTag {

	public PanelCategory getPanelCategory() {
		return _panelCategory;
	}

	public void setPanelApps(List<PanelApp> panelApps) {
		_panelApps = panelApps;
	}

	public void setPanelCategory(PanelCategory panelCategory) {
		_panelCategory = panelCategory;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_panelApps = null;
		_panelCategory = null;
	}

	@Override
	protected String getPage() {
		return "/panel_category_body/page.jsp";
	}

	protected List<PanelApp> getPanelApps() {
		if (_panelApps != null) {
			return _panelApps;
		}

		HttpServletRequest httpServletRequest = getRequest();

		PanelAppRegistry panelAppRegistry =
			(PanelAppRegistry)httpServletRequest.getAttribute(
				ApplicationListWebKeys.PANEL_APP_REGISTRY);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return panelAppRegistry.getPanelApps(
			_panelCategory, themeDisplay.getPermissionChecker(), getGroup());
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		String id = StringUtil.replace(
			_panelCategory.getKey(), CharPool.PERIOD, CharPool.UNDERLINE);

		id = "panel-manage-" + id;

		httpServletRequest.setAttribute(
			"liferay-application-list:panel-category-body:id", id);

		httpServletRequest.setAttribute(
			"liferay-application-list:panel-category-body:panelApps",
			getPanelApps());
		httpServletRequest.setAttribute(
			"liferay-application-list:panel-category-body:panelCategory",
			_panelCategory);
	}

	private List<PanelApp> _panelApps;
	private PanelCategory _panelCategory;

}