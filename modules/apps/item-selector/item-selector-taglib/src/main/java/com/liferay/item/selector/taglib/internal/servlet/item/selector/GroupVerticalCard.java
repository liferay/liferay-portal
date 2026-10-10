/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.servlet.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.taglib.internal.display.context.GroupSelectorDisplayContext;
import com.liferay.portal.kernel.model.Group;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Georgel Pop
 */
public class GroupVerticalCard extends BaseGroupCard implements VerticalCard {

	public GroupVerticalCard(
		Group group, GroupSelectorDisplayContext groupSelectorDisplayContext,
		HttpServletRequest httpServletRequest) {

		super(group, groupSelectorDisplayContext, httpServletRequest);
	}

	@Override
	public String getCssClass() {
		return "group-selector-" + groupSelectorDisplayContext.getGroupType();
	}

}