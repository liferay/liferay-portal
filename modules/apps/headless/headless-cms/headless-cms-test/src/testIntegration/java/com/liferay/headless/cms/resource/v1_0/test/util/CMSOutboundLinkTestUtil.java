/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Jürgen Kappler
 */
public class CMSOutboundLinkTestUtil {

	public static String getImageHTML(String externalReferenceCode) {
		return StringBundler.concat(
			"<img src=\"/documents/", RandomTestUtil.randomLong(),
			"/0/image.jpg/", StringUtil.randomId(),
			"?download=true&amp;objectDefinitionExternalReferenceCode=",
			"L_CMS_BASIC_DOCUMENT&amp;objectEntryExternalReferenceCode=",
			externalReferenceCode,
			"&amp;objectFieldExternalReferenceCode=FILE\">");
	}

}