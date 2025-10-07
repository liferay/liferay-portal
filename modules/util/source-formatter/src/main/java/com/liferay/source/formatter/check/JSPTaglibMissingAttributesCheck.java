/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.Set;

/**
 * @author Qi Zhang
 */
public class JSPTaglibMissingAttributesCheck extends BaseTagAttributesCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		_checkMissingAttributes(fileName, content);

		return content;
	}

	private void _checkMissingAttributes(String fileName, String content) {
		int x = -1;

		while (true) {
			x = content.indexOf("<clay:dropdown-actions", x + 1);

			if (x == -1) {
				break;
			}

			String tagString = getTag(content, x);

			if (Validator.isNull(tagString)) {
				continue;
			}

			Tag tag = parseTag(tagString, false);

			if (tag == null) {
				continue;
			}

			Map<String, String> attributesMap = tag.getAttributesMap();

			Set<String> attributesSet = attributesMap.keySet();

			if (attributesSet.contains("aria-label") ||
				attributesSet.contains("aria-labelledby") ||
				attributesSet.contains("title")) {

				continue;
			}

			addMessage(
				fileName,
				"When using <clay:dropdown-actions>, always specify one of " +
					"the following attributes: \"aria-label\", " +
						"\"aria-labelledby\", \"title\"",
				getLineNumber(content, x));
		}
	}

}