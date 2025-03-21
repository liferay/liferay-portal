/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.servlet.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.NavigationCard;
import com.liferay.item.selector.taglib.internal.display.context.GroupSelectorDisplayContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class GroupNavigationCard implements NavigationCard {

	public GroupNavigationCard(
		Group group, GroupSelectorDisplayContext groupSelectorDisplayContext,
		HttpServletRequest httpServletRequest) {

		_group = group;
		_groupSelectorDisplayContext = groupSelectorDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getHref() {
		return _groupSelectorDisplayContext.getViewGroupURL(_group);
	}

	@Override
	public String getIcon() {
		return _groupSelectorDisplayContext.getGroupItemSelectorIcon();
	}

	@Override
	public String getImageSrc() {
		return _group.getLogoURL(_themeDisplay, false);
	}

	@Override
	public String getTitle() {
		try {
			return _group.getDescriptiveName(_themeDisplay.getLocale());
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return _group.getName(_themeDisplay.getLocale());
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupNavigationCard.class);

	private final Group _group;
	private final GroupSelectorDisplayContext _groupSelectorDisplayContext;
	private final ThemeDisplay _themeDisplay;

}