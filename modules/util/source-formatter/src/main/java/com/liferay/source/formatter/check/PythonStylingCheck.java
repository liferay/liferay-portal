/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PythonStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _formatClassDefinitionHeader(absolutePath, content);
	}

	private String _formatClassDefinitionHeader(
		String absolutePath, String content) {

		int maxLineLength = 0;

		try {
			maxLineLength = Integer.parseInt(
				getAttributeValue(_MAX_LINE_LENGTH_KEY, absolutePath));
		}
		catch (NumberFormatException numberFormatException) {
			return content;
		}

		Matcher matcher = _classDefinitionHeaderPattern.matcher(content);

		while (matcher.find()) {
			List<String> parentClassList = ListUtil.fromString(
				matcher.group(4), StringPool.COMMA);

			for (int i = 0; i < parentClassList.size(); i++) {
				parentClassList.set(i, StringUtil.trim(parentClassList.get(i)));
			}

			StringBundler sb = new StringBundler(7);

			String indent = matcher.group(1);

			sb.append(indent);

			sb.append("class ");
			sb.append(matcher.group(2));

			if (ListUtil.isNotEmpty(parentClassList)) {
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(
					ListUtil.toString(
						parentClassList, StringPool.BLANK,
						StringPool.COMMA_AND_SPACE));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}

			sb.append(StringPool.COLON);

			content = StringUtil.replace(
				content, matcher.group(),
				_splitLine(sb.toString(), indent + "\t", maxLineLength) +
					"\n\n");
		}

		return content;
	}

	private String _splitLine(String line, String indent, int maxLineLength) {
		if (line.length() <= maxLineLength) {
			return line;
		}

		int pos = line.indexOf(StringPool.COMMA_AND_SPACE, indent.length());

		if (pos == -1) {
			return line;
		}

		if (pos > maxLineLength) {
			return StringBundler.concat(
				line.substring(0, pos + 1), StringPool.NEW_LINE,
				_splitLine(
					indent + StringUtil.trimLeading(line.substring(pos + 2)),
					indent, maxLineLength));
		}

		pos = line.lastIndexOf(StringPool.COMMA_AND_SPACE, maxLineLength);

		return StringBundler.concat(
			line.substring(0, pos + 1), StringPool.NEW_LINE,
			_splitLine(
				indent + StringUtil.trimLeading(line.substring(pos + 2)),
				indent, maxLineLength));
	}

	private static final String _MAX_LINE_LENGTH_KEY = "maxLineLength";

	private static final Pattern _classDefinitionHeaderPattern =
		Pattern.compile(
			"(?<=\n)(\t*)class (\\w+)(\\((.*?)\\))?:\n+", Pattern.DOTALL);

}