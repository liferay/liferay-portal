/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

/**
 * @author Jürgen Kappler
 */
public class CMSOutboundLinksUtil {

	public static final String FIELD_NAME = "outboundLinks";

	public static String getObjectEntryExternalReferenceCodeToken(
		String externalReferenceCode) {

		return _getToken("objectEntryERC", externalReferenceCode);
	}

	public static String getObjectEntryIdToken(long objectEntryId) {
		return _getToken("objectEntryId", String.valueOf(objectEntryId));
	}

	private static String _getToken(String prefix, String value) {
		return StringBundler.concat(prefix, StringPool.UNDERLINE, value);
	}

}