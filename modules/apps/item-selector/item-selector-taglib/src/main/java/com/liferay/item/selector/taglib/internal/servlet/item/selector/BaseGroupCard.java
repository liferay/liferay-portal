/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.servlet.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseClayCard;
import com.liferay.item.selector.taglib.internal.display.context.GroupSelectorDisplayContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Georgel Pop
 */
public abstract class BaseGroupCard implements BaseClayCard {

	public BaseGroupCard(
		Group group, GroupSelectorDisplayContext groupSelectorDisplayContext,
		HttpServletRequest httpServletRequest) {

		this.group = group;
		this.groupSelectorDisplayContext = groupSelectorDisplayContext;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getHref() {
		return groupSelectorDisplayContext.getViewGroupURL(group);
	}

	public String getIcon() {
		return groupSelectorDisplayContext.getGroupItemSelectorIcon(
			groupSelectorDisplayContext.getGroupType());
	}

	public String getImageAlt() {
		return HtmlUtil.escapeAttribute(getTitle());
	}

	public String getImageSrc() {
		return group.getLogoURL(themeDisplay, false);
	}

	public String getTitle() {
		try {
			return group.getDescriptiveName(themeDisplay.getLocale());
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return group.getName(themeDisplay.getLocale());
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	protected final Group group;
	protected final GroupSelectorDisplayContext groupSelectorDisplayContext;
	protected final ThemeDisplay themeDisplay;

	private static final Log _log = LogFactoryUtil.getLog(BaseGroupCard.class);

}