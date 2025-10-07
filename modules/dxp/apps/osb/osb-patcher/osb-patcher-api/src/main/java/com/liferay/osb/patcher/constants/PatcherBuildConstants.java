/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.constants;

import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Zsolt Balogh
 */
public class PatcherBuildConstants {

	public static final double KEY_VERSION_DEFAULT = 1.0;

	public static final String LABEL_ANY = "any";

	public static final String LABEL_DEBUG = "debug";

	public static final String LABEL_FIX_PACK = "fix-pack";

	public static final String LABEL_HOTFIX = "hotfix";

	public static final String LABEL_IGNORE = "ignore";

	public static final String LABEL_OFFICIAL = "official";

	public static final String PATCHER_BUILD_ACCOUNT_ENTRY_NAME_LIFERAY =
		"LIFERAY";

	public static final String
		PATCHER_BUILD_ACCOUNT_ENTRY_NAME_LIFERAY_SECURITY = "LIFERAYSECURITY";

	public static final double SUPPORT_TICKET_VERSION_DEFAULT = 1.0;

	public static final int TYPE_ANY = -1;

	public static final int TYPE_DEBUG = 3;

	public static final int TYPE_FIX_PACK = 1;

	public static final int TYPE_HOTFIX = 0;

	public static final int TYPE_IGNORE = 4;

	public static final int TYPE_OFFICIAL = 2;

	public static int getLabelType(String label) {
		if (StringUtil.equalsIgnoreCase(label, LABEL_DEBUG)) {
			return TYPE_DEBUG;
		}
		else if (StringUtil.equalsIgnoreCase(label, LABEL_FIX_PACK)) {
			return TYPE_FIX_PACK;
		}
		else if (StringUtil.equalsIgnoreCase(label, LABEL_HOTFIX)) {
			return TYPE_HOTFIX;
		}
		else if (StringUtil.equalsIgnoreCase(label, LABEL_IGNORE)) {
			return TYPE_IGNORE;
		}

		return TYPE_OFFICIAL;
	}

	public static String getTypeLabel(int type) {
		if (type == TYPE_DEBUG) {
			return LABEL_DEBUG;
		}
		else if (type == TYPE_FIX_PACK) {
			return LABEL_FIX_PACK;
		}
		else if (type == TYPE_HOTFIX) {
			return LABEL_HOTFIX;
		}
		else if (type == TYPE_IGNORE) {
			return LABEL_IGNORE;
		}

		return LABEL_OFFICIAL;
	}

}