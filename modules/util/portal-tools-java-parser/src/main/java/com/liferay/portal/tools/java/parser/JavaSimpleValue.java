/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.tools.java.parser.util.StringUtil;
import com.liferay.portal.tools.java.parser.util.ToolsUtil;

/**
 * @author Hugo Huijser
 */
public class JavaSimpleValue extends BaseJavaExpression {

	public JavaSimpleValue(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	@Override
	protected String getString(
		String indent, String prefix, String suffix, int maxLineLength,
		boolean forceLineBreak) {

		String s = StringBundler.concat(indent, prefix, _name, suffix);

		if ((maxLineLength == NO_MAX_LINE_LENGTH) ||
			(getLineLength(s) <= maxLineLength) ||
			(_name.endsWith("\"\"\"") && _name.startsWith("\"\"\""))) {

			return s;
		}

		int x = _name.length() + 1;

		while (true) {
			x = _name.lastIndexOf(".", x - 1);

			if (x == -1) {
				return StringBundler.concat(indent, prefix, _name, suffix);
			}

			if (ToolsUtil.isInsideQuotes(_name, x)) {
				continue;
			}

			String firstLine = StringBundler.concat(
				indent, prefix, _name.substring(0, x + 1));

			if (getLineLength(firstLine) > maxLineLength) {
				continue;
			}

			String secondLineIndent = "\t" + indent;

			String trimmedFirstLine = StringUtil.trim(firstLine);

			if (trimmedFirstLine.startsWith("catch (")) {
				secondLineIndent += "\t\t";
			}
			else if (trimmedFirstLine.startsWith("else if (")) {
				secondLineIndent += "\t\t";
			}
			else if (trimmedFirstLine.startsWith("extends ")) {
				secondLineIndent += "\t\t";
			}
			else if (trimmedFirstLine.startsWith("for (") &&
					 !trimmedFirstLine.endsWith(";")) {

				secondLineIndent += "\t";
			}
			else if (trimmedFirstLine.startsWith("if (")) {
				secondLineIndent += "\t";
			}
			else if (trimmedFirstLine.startsWith("implements ")) {
				secondLineIndent += "\t\t   ";
			}
			else if (trimmedFirstLine.startsWith("try (") &&
					 !trimmedFirstLine.endsWith(";")) {

				secondLineIndent += "\t";
			}
			else if (trimmedFirstLine.startsWith("while (")) {
				secondLineIndent += "\t\t";
			}

			return StringBundler.concat(
				firstLine, "\n", secondLineIndent, _name.substring(x + 1),
				suffix);
		}
	}

	private final String _name;

}