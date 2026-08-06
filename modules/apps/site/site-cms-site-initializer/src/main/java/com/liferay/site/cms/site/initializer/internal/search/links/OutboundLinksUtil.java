/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.links;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jürgen Kappler
 */
public class OutboundLinksUtil {

	public static Set<String> getObjectEntryExternalReferenceCodes(
		String content) {

		if (Validator.isNull(content)) {
			return Collections.emptySet();
		}

		Matcher matcher = _pattern.matcher(
			StringUtil.replace(
				content, StringPool.AMPERSAND_ENCODED, StringPool.AMPERSAND));

		Set<String> objectEntryExternalReferenceCodes = new LinkedHashSet<>();

		while (matcher.find()) {
			String objectEntryExternalReferenceCode = _decodeURL(
				matcher.group(1));

			if (Validator.isNotNull(objectEntryExternalReferenceCode)) {
				objectEntryExternalReferenceCodes.add(
					objectEntryExternalReferenceCode);
			}
		}

		return objectEntryExternalReferenceCodes;
	}

	private static String _decodeURL(String value) {
		if (!value.contains(StringPool.PERCENT)) {
			return value;
		}

		try {
			return URLCodec.decodeURL(value);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return value;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OutboundLinksUtil.class);

	private static final Pattern _pattern = Pattern.compile(
		"objectEntryExternalReferenceCode=([^&\"'\\s>]+)");

}