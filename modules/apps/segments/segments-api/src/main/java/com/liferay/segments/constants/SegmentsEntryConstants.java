/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.constants;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Eduardo García
 */
public class SegmentsEntryConstants {

	public static final long ID_DEFAULT = 0;

	public static final long ID_MISSING = -1;

	public static final String KEY_DEFAULT = "DEFAULT";

	public static final String SOURCE_ASAH_FARO_BACKEND = "ASAH_FARO_BACKEND";

	public static final String SOURCE_DEFAULT = "DEFAULT";

	public static final String SOURCE_REFERRED = "REFERRED";

	public static final String TYPE_BATCH = "BATCH";

	public static final String TYPE_DEFAULT = "DEFAULT";

	public static final String TYPE_REAL_TIME = "REAL_TIME";

	public static String getDefaultSegmentsEntryName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, SegmentsEntryConstants.class);

		return LanguageUtil.get(resourceBundle, "anyone");
	}

}