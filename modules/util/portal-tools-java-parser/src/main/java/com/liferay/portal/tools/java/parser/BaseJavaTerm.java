/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.java.parser.util.JavaParserUtil;
import com.liferay.portal.tools.java.parser.util.StringUtil;
import com.liferay.portal.tools.java.parser.util.ToolsUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public abstract class BaseJavaTerm implements JavaTerm {

	@Override
	public Position getEndPosition() {
		return _endPosition;
	}

	@Override
	public Position getStartPosition() {
		return _startPosition;
	}

	@Override
	public String getSuffix() {
		return _suffix;
	}

	@Override
	public void setEndPosition(Position endPosition) {
		_endPosition = endPosition;
	}

	@Override
	public void setStartPosition(Position startPosition) {
		_startPosition = startPosition;
	}

	@Override
	public void setSuffix(String suffix) {
		_suffix = suffix;
	}

	@Override
	public String toString() {
		return toString(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			_FORCE_SINGLE_LINE);
	}

	@Override
	public String toString(String indent, String prefix, int maxLineLength) {
		return toString(indent, prefix, _suffix, maxLineLength);
	}

	@Override
	public String toString(
		String indent, String prefix, String suffix, int maxLineLength,
		boolean forceLineBreak) {

		return toString(indent, prefix, suffix, maxLineLength);
	}

	protected String adjustIndent(StringBundler sb, String indent) {
		String s = sb.toString();

		String trimmedString = StringUtil.trim(s);

		if (trimmedString.endsWith("&") || trimmedString.endsWith("|") ||
			trimmedString.endsWith("^")) {

			if (ToolsUtil.getLevel(s) == 0) {
				String leadingWhitespace = _getLeadingWhitespace(s);

				if (!trimmedString.startsWith("return ")) {
					return leadingWhitespace;
				}

				return leadingWhitespace + "\t   ";
			}

			int x = -1;

			while (true) {
				x = s.indexOf(CharPool.OPEN_PARENTHESIS, x + 1);

				if (ToolsUtil.getLevel(s.substring(x + 1)) == 0) {
					int y = s.lastIndexOf('\n', x) + 1;

					return _convertToWhitespace(s.substring(y, x + 1));
				}
			}
		}

		String lastLine = JavaParserUtil.getLastLine(s);

		String trimmedLastLine = StringUtil.trim(lastLine);

		if (trimmedLastLine.startsWith("catch (") ||
			trimmedLastLine.startsWith("else if (")) {

			return _getLeadingWhitespace(lastLine) + "\t\t\t";
		}

		if ((trimmedLastLine.startsWith("for (") &&
			 !trimmedLastLine.endsWith(";")) ||
			trimmedLastLine.startsWith("if (")) {

			return _getLeadingWhitespace(lastLine) + "\t\t";
		}

		if (trimmedLastLine.startsWith("try (") &&
			!trimmedLastLine.endsWith(";")) {

			return _getLeadingWhitespace(lastLine) + "\t\t";
		}

		if (trimmedLastLine.startsWith("while (")) {
			return _getLeadingWhitespace(lastLine) + "\t\t\t";
		}

		return indent;
	}

	protected String append(
		StringBundler sb, JavaTerm javaTerm, String indent, int maxLineLength) {

		return append(sb, javaTerm, indent, maxLineLength, true);
	}

	protected String append(
		StringBundler sb, JavaTerm javaTerm, String indent, int maxLineLength,
		boolean newLine) {

		return append(
			sb, javaTerm, indent, StringPool.BLANK, StringPool.BLANK,
			maxLineLength, newLine);
	}

	protected String append(
		StringBundler sb, JavaTerm javaTerm, String indent, String prefix,
		String suffix, int maxLineLength) {

		return append(
			sb, javaTerm, indent, prefix, suffix, maxLineLength, true);
	}

	protected String append(
		StringBundler sb, JavaTerm javaTerm, String indent, String prefix,
		String suffix, int maxLineLength, boolean newLine) {

		if (appendSingleLine(sb, javaTerm, prefix, suffix, maxLineLength)) {
			return indent;
		}

		if (newLine) {
			sb = _stripTrailingWhitespace(sb);

			if (Validator.isNull(sb.toString())) {
				sb.append(
					javaTerm.toString(
						StringUtil.replaceFirst(indent, "\t", ""), prefix,
						suffix, maxLineLength));
			}
			else {
				appendNewLine(
					sb, javaTerm, indent, prefix, suffix, maxLineLength);
			}

			return "\t" + trimTrailingSpaces(getIndent(getLastLine(sb)));
		}

		return appendWithLineBreak(
			sb, javaTerm, indent, prefix, suffix, maxLineLength);
	}

	protected String append(
		StringBundler sb, List<? extends JavaTerm> list, String indent,
		int maxLineLength) {

		return append(
			sb, list, indent, StringPool.BLANK, StringPool.BLANK,
			maxLineLength);
	}

	protected String append(
		StringBundler sb, List<? extends JavaTerm> list, String indent,
		String prefix, String suffix, int maxLineLength) {

		return append(
			sb, list, StringPool.COMMA_AND_SPACE, indent, prefix, suffix,
			maxLineLength);
	}

	protected String append(
		StringBundler sb, List<? extends JavaTerm> list, String delimiter,
		String indent, String prefix, String suffix, int maxLineLength) {

		if ((list.isEmpty() && Validator.isNull(prefix) &&
			 Validator.isNull(suffix)) ||
			appendSingleLine(
				sb, list, delimiter, prefix, suffix, maxLineLength)) {

			return indent;
		}

		String lastLine = getLastLine(sb);

		sb = _stripTrailingWhitespace(sb);

		if (Validator.isNull(StringUtil.trim(lastLine))) {
			appendNewLine(
				sb, list, delimiter, lastLine, prefix, suffix, maxLineLength);
		}
		else {
			appendNewLine(
				sb, list, delimiter, indent, prefix, suffix, maxLineLength);
		}

		return "\t" + getIndent(getLastLine(sb));
	}

	protected void appendAssignValue(
		StringBundler sb, JavaExpression javaExpression, String indent,
		String suffix, int maxLineLength, boolean forceLineBreak) {

		boolean newLine = false;

		if (javaExpression instanceof JavaOperatorExpression) {
			JavaOperatorExpression javaOperatorExpression =
				(JavaOperatorExpression)javaExpression;

			while (true) {
				JavaOperator javaOperator =
					javaOperatorExpression.getJavaOperator();

				if (!javaOperator.equals(
						JavaOperator.LOGICAL_COMPLEMENT_OPERATOR)) {

					newLine = true;

					break;
				}

				JavaExpression rightHandJavaExpression =
					javaOperatorExpression.getRightHandJavaExpression();

				if (!(rightHandJavaExpression instanceof
						JavaOperatorExpression)) {

					break;
				}

				javaOperatorExpression =
					(JavaOperatorExpression)rightHandJavaExpression;
			}
		}
		else if (javaExpression instanceof JavaTypeCast) {
			JavaTypeCast javaTypeCast = (JavaTypeCast)javaExpression;

			if (javaTypeCast.getValueJavaExpression() instanceof
					JavaOperatorExpression) {

				newLine = true;
			}
		}

		if (!newLine && forceLineBreak) {
			appendWithLineBreak(
				sb, javaExpression, indent, "", suffix, maxLineLength);
		}
		else {
			append(
				sb, javaExpression, indent, "", suffix, maxLineLength, newLine);
		}
	}

	protected void appendNewLine(
		StringBundler sb, JavaTerm javaTerm, String indent, int maxLineLength) {

		appendNewLine(
			sb, javaTerm, indent, StringPool.BLANK, StringPool.BLANK,
			maxLineLength);
	}

	protected void appendNewLine(
		StringBundler sb, JavaTerm javaTerm, String indent, String prefix,
		String suffix, int maxLineLength) {

		sb = _stripTrailingWhitespace(sb);

		if (sb.index() > 0) {
			indent = adjustIndent(sb, indent);

			sb.append("\n");
		}

		sb.append(indent);

		if (_appendSingleLine(
				sb, javaTerm.toString(), prefix, suffix, maxLineLength)) {

			return;
		}

		if (indent.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append(javaTerm.toString(indent, prefix, suffix, maxLineLength));
	}

	protected void appendNewLine(
		StringBundler sb, List<? extends JavaTerm> list, String indent,
		int maxLineLength) {

		appendNewLine(
			sb, list, indent, StringPool.BLANK, StringPool.BLANK,
			maxLineLength);
	}

	protected void appendNewLine(
		StringBundler sb, List<? extends JavaTerm> list, String indent,
		String prefix, String suffix, int maxLineLength) {

		appendNewLine(
			sb, list, StringPool.COMMA_AND_SPACE, indent, prefix, suffix,
			maxLineLength);
	}

	protected void appendNewLine(
		StringBundler sb, List<? extends JavaTerm> list, String delimiter,
		String indent, String prefix, String suffix, int maxLineLength) {

		appendNewLine(
			sb, list, delimiter, indent, prefix, suffix, maxLineLength, true);
	}

	protected void appendNewLine(
		StringBundler sb, List<? extends JavaTerm> list, String delimiter,
		String indent, String prefix, String suffix, int maxLineLength,
		boolean breakJavaTerms) {

		sb = _stripTrailingWhitespace(sb);

		if (sb.index() > 0) {
			indent = adjustIndent(sb, indent);

			sb.append("\n");
		}

		sb.append(indent);

		if (list.isEmpty()) {
			sb.append(prefix);
			sb.append(suffix);

			return;
		}

		for (int i = 0;; i++) {
			JavaTerm javaTerm = list.get(i);

			if (i == 1) {
				if (prefix.equals("try (")) {
					indent += "\t";
				}
				else {
					indent += _convertToWhitespace(prefix);
				}

				prefix = StringPool.BLANK;
			}

			if ((sb.index() == 0) ||
				Objects.equals(sb.stringAt(sb.index() - 1), "\n")) {

				sb.append(indent);
			}

			if (i == (list.size() - 1)) {
				if (!appendSingleLine(
						sb, javaTerm, prefix, suffix, maxLineLength)) {

					delimiter = StringUtil.trim(delimiter);

					if (breakJavaTerms) {
						appendNewLine(
							sb, javaTerm, indent, prefix, suffix,
							maxLineLength);
					}
					else {
						appendNewLine(
							sb, javaTerm, indent, prefix, suffix,
							_FORCE_SINGLE_LINE);
					}
				}

				return;
			}

			if (appendSingleLine(
					sb, javaTerm, prefix, delimiter, maxLineLength)) {

				continue;
			}

			sb = _stripTrailingWhitespace(sb);

			sb.append("\n");
			sb.append(indent);

			if (!appendSingleLine(
					sb, javaTerm, prefix, delimiter, maxLineLength)) {

				if (breakJavaTerms) {
					appendNewLine(
						sb, javaTerm, indent, prefix,
						StringUtil.trimTrailing(delimiter), maxLineLength);
				}
				else {
					appendNewLine(
						sb, javaTerm, indent, prefix,
						StringUtil.trimTrailing(delimiter), _FORCE_SINGLE_LINE);
				}

				sb.append("\n");
			}
		}
	}

	protected boolean appendSingleLine(
		StringBundler sb, JavaTerm javaTerm, int maxLineLength) {

		return appendSingleLine(
			sb, javaTerm, StringPool.BLANK, StringPool.BLANK, maxLineLength);
	}

	protected boolean appendSingleLine(
		StringBundler sb, JavaTerm javaTerm, String prefix, String suffix,
		int maxLineLength) {

		return _appendSingleLine(
			sb, javaTerm.toString(), prefix, suffix, maxLineLength);
	}

	protected boolean appendSingleLine(
		StringBundler sb, List<? extends JavaTerm> list, int maxLineLength) {

		return appendSingleLine(
			sb, list, StringPool.BLANK, StringPool.BLANK, maxLineLength);
	}

	protected boolean appendSingleLine(
		StringBundler sb, List<? extends JavaTerm> list, String delimiter,
		int maxLineLength) {

		return appendSingleLine(
			sb, list, delimiter, StringPool.BLANK, StringPool.BLANK,
			maxLineLength);
	}

	protected boolean appendSingleLine(
		StringBundler sb, List<? extends JavaTerm> list, String prefix,
		String suffix, int maxLineLength) {

		return appendSingleLine(
			sb, list, StringPool.COMMA_AND_SPACE, prefix, suffix,
			maxLineLength);
	}

	protected boolean appendSingleLine(
		StringBundler sb, List<? extends JavaTerm> list, String delimiter,
		String prefix, String suffix, int maxLineLength) {

		return _appendSingleLine(
			sb, ListUtil.toString(list, StringPool.BLANK, delimiter), prefix,
			suffix, maxLineLength);
	}

	protected String appendWithLineBreak(
		StringBundler sb, JavaTerm javaTerm, String indent, String prefix,
		String suffix, int maxLineLength) {

		String lastLine = getLastLine(sb);

		if (javaTerm instanceof JavaNewClassInstantiation) {
			indent = StringUtil.removeChar(getIndent(lastLine), CharPool.SPACE);

			String javaTermContent = javaTerm.toString(
				indent, StringUtil.replaceFirst(lastLine, indent, "") + prefix,
				suffix, maxLineLength, true);

			String firstLine = _getFirstLine(javaTermContent);

			if (firstLine.endsWith(StringPool.PERIOD) ||
				(getLineLength(firstLine) > maxLineLength) ||
				javaTermContent.matches(".*\n\\s*<[\\s\\S]+")) {

				appendNewLine(
					sb, javaTerm, "\t" + indent, prefix, suffix, maxLineLength);
			}
			else {
				String s = StringUtil.replaceLast(
					sb.toString(), lastLine, javaTermContent);

				sb.setIndex(0);

				sb.append(s);
			}

			return "\t" + getIndent(getLastLine(sb));
		}

		indent = StringUtil.replaceFirst(adjustIndent(sb, indent), "\t", "");

		if (Validator.isNull(StringUtil.trim(lastLine))) {
			sb = _stripTrailingWhitespace(sb);

			if (sb.index() > 0) {
				sb.append("\n");
			}

			String s = javaTerm.toString(
				indent, prefix, suffix, maxLineLength, true);

			sb.append(s);
		}
		else {
			String javaTermContent = javaTerm.toString(
				indent, "", suffix, maxLineLength, true);

			javaTermContent = StringUtil.replaceFirst(
				javaTermContent, indent, prefix);

			String firstLineJavaTermContent = _getFirstLine(javaTermContent);

			String s = lastLine + firstLineJavaTermContent;

			lastLine = StringUtil.trim(lastLine);

			if ((getLineLength(s) > maxLineLength) &&
				(maxLineLength != NO_MAX_LINE_LENGTH)) {

				appendNewLine(
					sb, javaTerm, "\t" + indent, prefix, suffix, maxLineLength);
			}
			else if ((lastLine.endsWith("=") || lastLine.endsWith(")") ||
					  lastLine.endsWith("->")) &&
					 (firstLineJavaTermContent.endsWith(".") ||
					  firstLineJavaTermContent.matches("\\s*\\([^\\)]+\\)?"))) {

				appendNewLine(
					sb, javaTerm, "\t" + indent, prefix, suffix, maxLineLength);
			}
			else {
				int level = ToolsUtil.getLevel(
					firstLineJavaTermContent, "<", ">");

				if (level > 0) {
					appendNewLine(
						sb, javaTerm, "\t" + indent, prefix, suffix,
						maxLineLength);
				}
				else {
					sb.append(javaTermContent);
				}
			}
		}

		indent = "\t" + getIndent(getLastLine(sb));

		if (javaTerm instanceof JavaType) {
			return trimTrailingSpaces(indent);
		}

		return indent;
	}

	protected String getIndent(String s) {
		StringBundler sb = new StringBundler(s.length());

		for (char c : s.toCharArray()) {
			if (!Character.isWhitespace(c)) {
				return sb.toString();
			}

			sb.append(c);
		}

		return s;
	}

	protected String getLastLine(StringBundler sb) {
		return JavaParserUtil.getLastLine(sb.toString());
	}

	protected int getLineLength(String line) {
		int lineLength = 0;

		int tabLength = 4;

		for (char c : line.toCharArray()) {
			if (c == CharPool.TAB) {
				for (int i = 0; i < tabLength; i++) {
					lineLength++;
				}

				tabLength = 4;
			}
			else {
				lineLength++;

				tabLength--;

				if (tabLength <= 0) {
					tabLength = 4;
				}
			}
		}

		return lineLength;
	}

	protected String trimTrailingSpaces(String s) {
		if (s.length() == 0) {
			return s;
		}

		while (true) {
			if (s.charAt(s.length() - 1) != CharPool.SPACE) {
				return s;
			}

			s = s.substring(0, s.length() - 1);
		}
	}

	protected static final int NO_MAX_LINE_LENGTH =
		JavaParserUtil.NO_MAX_LINE_LENGTH;

	private boolean _appendSingleLine(
		StringBundler sb, String s, String prefix, String suffix,
		int maxLineLength) {

		if (!s.startsWith("\"\"\"") && !s.endsWith("\"\"\"") &&
			s.contains("\n") && (maxLineLength != _FORCE_SINGLE_LINE)) {

			return false;
		}

		int index = sb.index();

		sb.append(prefix);
		sb.append(s);
		sb.append(suffix);

		if ((maxLineLength == _FORCE_SINGLE_LINE) ||
			(maxLineLength == NO_MAX_LINE_LENGTH) ||
			(getLineLength(getLastLine(sb)) <= maxLineLength)) {

			return true;
		}

		if (suffix.length() > 0) {
			sb.setIndex(sb.index() - 1);

			sb.append(StringUtil.trimTrailing(suffix));

			if (getLineLength(getLastLine(sb)) <= maxLineLength) {
				return true;
			}
		}

		sb.setIndex(index);

		return false;
	}

	private String _convertToWhitespace(String s) {
		StringBundler sb = new StringBundler(s.length());

		int i = getLineLength(s);

		while (i >= 4) {
			sb.append(StringPool.TAB);

			i -= 4;
		}

		for (int j = 0; j < i; j++) {
			sb.append(StringPool.SPACE);
		}

		return sb.toString();
	}

	private String _getFirstLine(String s) {
		int x = s.indexOf("\n");

		if (x != -1) {
			return s.substring(0, x);
		}

		return s;
	}

	private String _getLeadingWhitespace(String s) {
		StringBundler sb = new StringBundler();

		for (char c : s.toCharArray()) {
			if (Character.isWhitespace(c)) {
				sb.append(c);
			}
			else {
				return sb.toString();
			}
		}

		return s;
	}

	private StringBundler _stripTrailingWhitespace(StringBundler sb) {
		while (true) {
			if (sb.index() == 0) {
				return sb;
			}

			String trimmedLastAppend = StringUtil.trimTrailing(
				sb.stringAt(sb.index() - 1));

			if (Validator.isNotNull(trimmedLastAppend)) {
				sb.setStringAt(trimmedLastAppend, sb.index() - 1);

				return sb;
			}

			sb.setIndex(sb.index() - 1);
		}
	}

	private static final int _FORCE_SINGLE_LINE = -2;

	private Position _endPosition;
	private Position _startPosition;
	private String _suffix;

}