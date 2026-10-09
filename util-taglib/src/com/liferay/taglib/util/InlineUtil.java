/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class InlineUtil {

	public static String buildDynamicAttributes(
		Map<String, Object> dynamicAttributes) {

		if ((dynamicAttributes == null) || dynamicAttributes.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(dynamicAttributes.size() * 4);

		for (Map.Entry<String, Object> entry : dynamicAttributes.entrySet()) {
			String attributeName = entry.getKey();

			if (attributeName.equals("class")) {
				continue;
			}

			String attributeValue = String.valueOf(entry.getValue());

			if (_attributeNames.contains(attributeName)) {
				if (!Boolean.valueOf(attributeValue)) {
					continue;
				}

				attributeValue = attributeName;
			}

			sb.append(attributeName);
			sb.append("=\"");
			sb.append(attributeValue);
			sb.append("\" ");
		}

		return sb.toString();
	}

	private static final Set<String> _attributeNames = SetUtil.fromArray(
		"allowfullscreen", "allowpaymentrequest", "async", "autofocus",
		"autoplay", "checked", "controls", "default", "disabled",
		"formnovalidate", "hidden", "ismap", "itemscope", "loop", "multiple",
		"muted", "nomodule", "novalidate", "open", "playsinline", "readonly",
		"required", "reversed", "selected", "truespeed");

}