/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaConstantsFileCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith("/JavaConstants.java")) {
			return content;
		}

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _constantPattern.matcher(content);

		while (matcher.find()) {
			String constantValue = TextFormatter.format(
				matcher.group(2), TextFormatter.H);

			StringBundler constantNameSB = new StringBundler(
				constantValue.length());

			for (int i = 0; i < constantValue.length(); i++) {
				char c = constantValue.charAt(i);

				if (!Character.isLetterOrDigit(c)) {
					constantNameSB.append(CharPool.UNDERLINE);
				}
				else if (Character.isLowerCase(c)) {
					constantNameSB.append(Character.toUpperCase(c));
				}
				else {
					constantNameSB.append(c);
				}
			}

			String newConstantName = constantNameSB.toString();

			newConstantName = newConstantName.replaceFirst(
				"_*([A-Z].*([A-Z]|[0-9]))_*", "$1");

			String constantName = matcher.group(1);

			if (constantName.equals(newConstantName)) {
				continue;
			}

			String replacement = StringUtil.replaceFirst(
				matcher.group(), constantName, newConstantName);

			matcher.appendReplacement(sb, replacement);
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			return sb.toString();
		}

		return content;
	}

	private static final Pattern _constantPattern = Pattern.compile(
		"\n\t+public static final String (\\w+) =\\s+\"(.+)\";");

}