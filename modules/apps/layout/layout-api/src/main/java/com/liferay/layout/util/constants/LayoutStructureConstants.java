/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.constants;

/**
 * @author Eudaldo Alonso
 */
public class LayoutStructureConstants {

	public static final int[][] COLUMN_SIZES = {
		{12}, {6, 6}, {4, 4, 4}, {3, 3, 3, 3}, {2, 2, 4, 2, 2},
		{2, 2, 2, 2, 2, 2}, {1, 1, 1, 6, 1, 1, 1}, {1, 1, 1, 3, 3, 1, 1, 1},
		{1, 1, 1, 1, 4, 1, 1, 1, 1}, {1, 1, 1, 1, 2, 2, 1, 1, 1, 1},
		{1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
	};

	public static final double LATEST_PAGE_DEFINITION_VERSION = 1.1;

	public static final String[] LAYOUT_ELEMENT_FRAGMENT_ENTRY_KEYS = {
		"collection-display", "container", "form", "formRelationship", "row"
	};

}