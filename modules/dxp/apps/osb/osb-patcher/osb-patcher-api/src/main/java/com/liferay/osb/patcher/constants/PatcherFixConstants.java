/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.constants;

public class PatcherFixConstants {

	public static final double KEY_VERSION_DEFAULT = 1.0;

	public static final String LABEL_EXCLUDED = "excluded";

	public static final String LABEL_FIX_PACK = "fix-pack";

	public static final String LABEL_GENERATED = "generated";

	public static final String LABEL_GENERATED_PRIVATE_PUBLIC =
		"generated-private-public";

	public static final String LABEL_PATCH = "patch";

	public static final String LABEL_REBASE = "rebase";

	public static final String LABEL_WORKAROUND = "workaround";

	public static final int TYPE_ANY = -1;

	public static final int TYPE_EXCLUDED = 3;

	public static final int TYPE_FIX_PACK = 4;

	public static final int TYPE_GENERATED = 2;

	public static final int TYPE_GENERATED_PRIVATE_PUBLIC = 5;

	public static final int TYPE_PATCH = 0;

	public static final int TYPE_REBASE = 6;

	public static final int TYPE_WORKAROUND = 1;

	public static String getTypeLabel(int type) {
		if (type == TYPE_EXCLUDED) {
			return LABEL_EXCLUDED;
		}
		else if (type == TYPE_FIX_PACK) {
			return LABEL_FIX_PACK;
		}
		else if (type == TYPE_GENERATED) {
			return LABEL_GENERATED;
		}
		else if (type == TYPE_GENERATED_PRIVATE_PUBLIC) {
			return LABEL_GENERATED_PRIVATE_PUBLIC;
		}
		else if (type == TYPE_REBASE) {
			return LABEL_REBASE;
		}
		else if (type == TYPE_WORKAROUND) {
			return LABEL_WORKAROUND;
		}

		return LABEL_PATCH;
	}

}