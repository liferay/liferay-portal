/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.servlet.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.NavigationCard;
import com.liferay.item.selector.taglib.internal.display.context.GroupSelectorDisplayContext;

/**
 * @author Georgel Pop
 */
public class GroupTypeNavigationCard implements NavigationCard {

	public GroupTypeNavigationCard(
		GroupSelectorDisplayContext groupSelectorDisplayContext,
		String groupType) {

		_groupSelectorDisplayContext = groupSelectorDisplayContext;
		_groupType = groupType;
	}

	@Override
	public String getCardType() {
		return "template";
	}

	@Override
	public String getHref() {
		return String.valueOf(
			_groupSelectorDisplayContext.getGroupItemSelectorURL(_groupType));
	}

	@Override
	public String getIcon() {
		return _groupSelectorDisplayContext.getGroupItemSelectorIcon(
			_groupType);
	}

	@Override
	public String getTitle() {
		return _groupSelectorDisplayContext.getGroupItemSelectorLabel(
			_groupType);
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final GroupSelectorDisplayContext _groupSelectorDisplayContext;
	private final String _groupType;

}