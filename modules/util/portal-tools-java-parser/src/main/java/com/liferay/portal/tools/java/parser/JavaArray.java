/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.tools.java.parser.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JavaArray extends BaseJavaExpression {

	public void addValueJavaExpression(JavaExpression valueJavaExpression) {
		_valueJavaExpressions.add(valueJavaExpression);
	}

	public void setBreakJavaValueExpressions(
		boolean breakJavaValueExpressions) {

		_breakJavaValueExpressions = breakJavaValueExpressions;
	}

	@Override
	protected String getString(
		String indent, String prefix, String suffix, int maxLineLength,
		boolean forceLineBreak) {

		StringBundler sb = new StringBundler();

		sb.append(indent);

		indent = "\t" + indent;

		sb.append(prefix);
		sb.append("{");

		if (!forceLineBreak &&
			appendSingleLine(
				sb, _valueJavaExpressions, "", "}" + suffix, maxLineLength)) {

			return sb.toString();
		}

		if (!_valueJavaExpressions.isEmpty()) {
			appendNewLine(
				sb, _valueJavaExpressions, ", ", indent, "", "", maxLineLength,
				_breakJavaValueExpressions);

			sb.append("\n");
			sb.append(StringUtil.replaceFirst(indent, "\t", ""));
		}

		sb.append("}");
		sb.append(suffix);

		return sb.toString();
	}

	private boolean _breakJavaValueExpressions = true;
	private final List<JavaExpression> _valueJavaExpressions =
		new ArrayList<>();

}