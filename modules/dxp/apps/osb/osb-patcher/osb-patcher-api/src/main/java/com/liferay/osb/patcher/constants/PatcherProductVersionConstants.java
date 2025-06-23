/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.constants;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Zsolt Balogh
 */
public class PatcherProductVersionConstants {

	public static final String LABEL_FIX_DELIVERY_METHOD_FIX_PACK_20 =
		"Fix Pack 2.0";

	public static final String LABEL_FIX_DELIVERY_METHOD_FIX_PACK_30 =
		"Fix Pack 3.0";

	public static final String LABEL_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE =
		"Marketplace Release";

	public static final String LABEL_PRODUCT_VERSION_PORTAL_6X = "Portal 6.x";

	public static final String LABEL_PRODUCT_VERSION_PORTAL_70 = "DXP 7.0";

	public static final String LABEL_PRODUCT_VERSION_QUARTERLY_RELEASES =
		"Quarterly Releases";

	public static final int TYPE_FIX_DELIVERY_METHOD_FIX_PACK_20 = 1;

	public static final int TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30 = 2;

	public static final int TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE = 0;

	public static int getLabelType(String label) {
		if (StringUtil.equalsIgnoreCase(
				label, LABEL_FIX_DELIVERY_METHOD_FIX_PACK_20)) {

			return TYPE_FIX_DELIVERY_METHOD_FIX_PACK_20;
		}
		else if (StringUtil.equalsIgnoreCase(
					label, LABEL_FIX_DELIVERY_METHOD_FIX_PACK_30)) {

			return TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30;
		}
		else if (StringUtil.equalsIgnoreCase(
					label, LABEL_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE)) {

			return TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE;
		}

		return -1;
	}

	public static String getTypeLabel(int type) {
		if (type == TYPE_FIX_DELIVERY_METHOD_FIX_PACK_20) {
			return LABEL_FIX_DELIVERY_METHOD_FIX_PACK_20;
		}
		else if (type == TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30) {
			return LABEL_FIX_DELIVERY_METHOD_FIX_PACK_30;
		}
		else if (type == TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE) {
			return LABEL_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE;
		}

		return StringPool.BLANK;
	}

}