/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeSCSSNodeSassPatternsCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
		String fileName, String absolutePath, String content) {

		String newContent = content;

		Matcher divisionMatcher = _divisionPattern.matcher(content);

		boolean replaced = false;

		while (divisionMatcher.find()) {
			String line = divisionMatcher.group();

			if (ToolsUtil.isInsideQuotes(content, divisionMatcher.start()) ||
				SourceUtil.containsUnquoted(line, "hsl") ||
				SourceUtil.containsUnquoted(line, "hsla") ||
				SourceUtil.containsUnquoted(line, "rgb") ||
				SourceUtil.containsUnquoted(line, "rgba")) {

				continue;
			}

			StringBuilder sb = new StringBuilder();

			sb.append("math.div(");
			sb.append(divisionMatcher.group(2));
			sb.append(StringPool.COMMA_AND_SPACE);
			sb.append(divisionMatcher.group(3));
			sb.append(StringPool.CLOSE_PARENTHESIS);

			newContent = StringUtil.replace(
				newContent, divisionMatcher.group(1), sb.toString());

			replaced = true;
		}

		if (replaced) {
			newContent = "@use \"sass:math\";\n" + newContent;
		}

		Matcher interpolationMatcher = _interpolationPattern.matcher(content);

		while (interpolationMatcher.find()) {
			String newInterpolation = _formatInterpolation(
				interpolationMatcher);

			newContent = StringUtil.replace(
				newContent, interpolationMatcher.group(), newInterpolation);
		}

		return newContent;
	}

	@Override
	protected String[] getValidExtensions() {
		return new String[] {"scss"};
	}

	private String _formatInterpolation(Matcher matcher) {
		StringBuilder sb = new StringBuilder();

		sb.append("#{'");
		sb.append(matcher.group(1));
		sb.append("' + ");
		sb.append(matcher.group(2));

		String interpolation = matcher.group(3);

		if (!interpolation.isEmpty()) {
			String[] interpolationParts = interpolation.split(
				"((\\#\\{)|(\\}))");

			for (String interpolationPart : interpolationParts) {
				if (interpolationPart.contains("$")) {
					sb.append(" + ");
					sb.append(interpolationPart);
				}
				else if (!interpolationPart.isEmpty()) {
					sb.append(" + '");
					sb.append(interpolationPart);
					sb.append("'");
				}
			}
		}

		sb.append(StringPool.CLOSE_CURLY_BRACE);

		return sb.toString();
	}

	private static final Pattern _divisionPattern = Pattern.compile(
		"^.*?((\\w+\\([^)]+,[^)]+\\)|\\$[-\\w]+|-?(?:\\d+(?:\\.\\d+)?|" +
			"\\.\\d+)[a-zA-Z%]*)\\s*/\\s*(\\w+\\([^)]+,[^)]+\\)|\\$[-\\w]+|-?" +
				"(?:\\d+(?:\\.\\d+)?|\\.\\d+)[a-zA-Z%]*)).*$",
		Pattern.MULTILINE);
	private static final Pattern _interpolationPattern = Pattern.compile(
		"([\\w-\\.]+)\\#\\{([\\w\\.\\$\\(\\), \\&]+)" +
			"\\}([\\w-\\.\\#\\{\\.\\$\\}]*)");

}