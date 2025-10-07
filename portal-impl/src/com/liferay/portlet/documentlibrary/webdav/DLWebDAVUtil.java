/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.webdav;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;

/**
 * @author Iván Zaera
 */
public class DLWebDAVUtil {

	public static String escapeRawTitle(String title) {
		return StringUtil.replace(
			title, CharPool.SLASH, PropsValues.DL_WEBDAV_SUBSTITUTION_CHAR);
	}

	public static String escapeURLTitle(String title) {
		return URLCodec.encodeURL(escapeRawTitle(title), true);
	}

	public static boolean isRepresentableTitle(String title) {
		return !title.contains(PropsValues.DL_WEBDAV_SUBSTITUTION_CHAR);
	}

	public static String unescapeRawTitle(String escapedTitle) {
		return StringUtil.replace(
			escapedTitle, PropsValues.DL_WEBDAV_SUBSTITUTION_CHAR,
			StringPool.SLASH);
	}

}