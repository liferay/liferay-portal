/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class TSSpecFileStylingCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith(".spec.ts")) {
			return content;
		}

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _descriptionPattern.matcher(content);

		while (matcher.find()) {
			String description = matcher.group(1);

			String unquotedDescription = StringUtil.unquote(description);

			String trimmedDescription = unquotedDescription.trim();

			String newDescription = null;

			if (trimmedDescription.indexOf(CharPool.APOSTROPHE) == -1) {
				newDescription = StringUtil.quote(trimmedDescription);
			}
			else {
				newDescription = StringUtil.quote(
					trimmedDescription, StringPool.QUOTE);
			}

			if (description.equals(newDescription)) {
				continue;
			}

			String replacement = StringUtil.replaceFirst(
				matcher.group(), description, newDescription);

			matcher.appendReplacement(sb, replacement);
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			return sb.toString();
		}

		return content;
	}

	private static final Pattern _descriptionPattern = Pattern.compile(
		"\n\t*test\\(\\s*((['\"]).+\\2),\\s");

}