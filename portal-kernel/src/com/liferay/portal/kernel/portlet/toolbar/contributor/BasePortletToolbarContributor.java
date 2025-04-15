/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.toolbar.contributor;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eduardo García
 */
public abstract class BasePortletToolbarContributor
	implements PortletToolbarContributor {

	@Override
	public List<Menu> getPortletTitleMenus(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		if (group.isLayoutPrototype()) {
			return Collections.emptyList();
		}

		List<MenuItem> portletTitleMenuItems = getPortletTitleMenuItems(
			portletRequest, portletResponse);

		if (ListUtil.isEmpty(portletTitleMenuItems)) {
			return Collections.emptyList();
		}

		List<Menu> menus = new ArrayList<>();

		Menu menu = new Menu();

		menu.setData(
			HashMapBuilder.<String, Object>put(
				"qa-id", "addButton"
			).build());
		menu.setDirection("right");
		menu.setExtended(false);
		menu.setIcon("plus");
		menu.setMarkupView("lexicon");
		menu.setMenuItems(portletTitleMenuItems);
		menu.setMessage("add");
		menu.setScroll(false);
		menu.setShowArrow(false);
		menu.setShowWhenSingleIcon(true);

		menus.add(menu);

		return menus;
	}

	protected abstract List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse);

}