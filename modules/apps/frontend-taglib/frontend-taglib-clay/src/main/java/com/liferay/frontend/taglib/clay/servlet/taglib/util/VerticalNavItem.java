/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eduardo Allegrini
 */
public class VerticalNavItem extends NavigationItem {

	public void addIcon(IconItem iconItem) {
		List<IconItem> iconItems = (List<IconItem>)get("icons");

		if (iconItems == null) {
			iconItems = new ArrayList<>();

			put("icons", iconItems);
		}

		iconItems.add(iconItem);
	}

	public void addLabelItem(LabelItem labelItem) {
		List<LabelItem> labelItems = (List<LabelItem>)get("labelItems");

		if (labelItems == null) {
			labelItems = new ArrayList<>();

			put("labelItems", labelItems);
		}

		labelItems.add(labelItem);
	}

	public void setExpanded(boolean expanded) {
		put("expanded", expanded);
	}

	public void setIcons(List<IconItem> iconItems) {
		put("icons", iconItems);
	}

	public void setId(String id) {
		put("id", id);
	}

	public void setItems(List<VerticalNavItem> verticalNavItems) {
		put("items", verticalNavItems);
	}

	public void setLabelItems(List<LabelItem> labelItems) {
		put("labelItems", labelItems);
	}

}