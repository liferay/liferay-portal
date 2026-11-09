/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.util;

import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;

/**
 * @author Alberto Chaparro
 */
public class LayoutPageTemplateCollectionUtil {

	public static String getName(int type) {
		if (type == LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE) {
			return "display page template folder";
		}

		return "page template set";
	}

}