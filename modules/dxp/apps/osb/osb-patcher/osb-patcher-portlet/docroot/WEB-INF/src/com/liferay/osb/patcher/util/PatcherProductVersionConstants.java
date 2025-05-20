/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

import com.liferay.portal.kernel.util.StringPool;
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