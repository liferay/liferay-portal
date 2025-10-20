/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.constants;

import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Selton Guedes
 */
public class ObjectLayoutBoxConstants {

	public static final String TYPE_CATEGORIZATION = "categorization";

	public static final String TYPE_REGULAR = "regular";

	public static final String TYPE_SEO = "seo";

	public static String getTypeLabel(String type) {
		if (StringUtil.equals(type, TYPE_CATEGORIZATION)) {
			return "Categorization";
		}
		else if (StringUtil.equals(type, TYPE_SEO)) {
			return "SEO";
		}

		return null;
	}

}