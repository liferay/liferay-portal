/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.tools.java.parser.util.StringUtil;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JavaForStatement extends BaseJavaLoopStatement {

	public void setConditionJavaExpression(
		JavaExpression conditionJavaExpression) {

		_conditionJavaExpression = conditionJavaExpression;
	}

	public void setInitializationJavaTerms(
		List<JavaTerm> initializationJavaTerms) {

		_initializationJavaTerms = initializationJavaTerms;
	}

	public void setIteratorJavaExpression(
		List<JavaExpression> iteratorJavaExpressions) {

		_iteratorJavaExpressions = iteratorJavaExpressions;
	}

	@Override
	public String toString(
		String indent, String prefix, String suffix, int maxLineLength) {

		StringBundler sb = appendLabelName(indent);

		if (sb.index() > 0) {
			sb.append("\n");
		}

		sb.append(indent);

		indent = "\t" + indent;

		if ((_conditionJavaExpression == null) &&
			_initializationJavaTerms.isEmpty() &&
			_iteratorJavaExpressions.isEmpty()) {

			sb.append(prefix);
			sb.append("for (;;)");
			sb.append(suffix);

			return sb.toString();
		}

		if (_initializationJavaTerms.size() == 1) {
			append(
				sb, _initializationJavaTerms.get(0), indent, prefix + "for (",
				_getInitializationJavaTermsSuffix(suffix), maxLineLength);
		}
		else {
			String javaTermSuffix = ", ";

			for (int i = 0; i < _initializationJavaTerms.size(); i++) {
				JavaTerm javaTerm = _initializationJavaTerms.get(i);

				if (i == 0) {
					append(
						sb, javaTerm, indent, prefix + "for (", javaTermSuffix,
						maxLineLength);
				}
				else {
					if (i == (_initializationJavaTerms.size() - 1)) {
						javaTermSuffix = _getInitializationJavaTermsSuffix(
							suffix);
					}

					if (javaTerm instanceof JavaVariableDefinition) {
						JavaVariableDefinition javaVariableDefinition =
							(JavaVariableDefinition)javaTerm;

						javaVariableDefinition.setJavaType(null);

						append(
							sb, javaVariableDefinition, indent, "",
							javaTermSuffix, maxLineLength);
					}
					else {
						append(
							sb, javaTerm, indent, "", javaTermSuffix,
							maxLineLength);
					}
				}
			}
		}

		indent += " ";

		if (_conditionJavaExpression != null) {
			String conditionJavaExpressionSuffix =
				_getConditionJavaExpressionSuffix(suffix);

			if (_initializationJavaTerms.isEmpty()) {
				if (!appendSingleLine(
						sb, _conditionJavaExpression, prefix + "for (; ",
						conditionJavaExpressionSuffix, maxLineLength)) {

					sb.append(prefix);
					sb.append("for (;\n");

					appendNewLine(
						sb, _conditionJavaExpression, indent, "",
						StringUtil.trimTrailing(conditionJavaExpressionSuffix),
						maxLineLength);

					sb.append("\n");
					sb.append(indent);
				}
			}
			else if (getLineLength(getIndent(getLastLine(sb))) > getLineLength(
						indent)) {

				appendNewLine(
					sb, _conditionJavaExpression, indent, "",
					conditionJavaExpressionSuffix, maxLineLength);
			}
			else {
				append(
					sb, _conditionJavaExpression, indent, "",
					conditionJavaExpressionSuffix, maxLineLength);
			}
		}

		if (!_iteratorJavaExpressions.isEmpty()) {
			if (getLineLength(getIndent(getLastLine(sb))) > getLineLength(
					indent)) {

				appendNewLine(
					sb, _iteratorJavaExpressions, indent,
					_getIteratorJavaExpressionsPrefix(prefix), ")" + suffix,
					maxLineLength);
			}
			else {
				append(
					sb, _iteratorJavaExpressions, indent,
					_getIteratorJavaExpressionsPrefix(prefix), ")" + suffix,
					maxLineLength);
			}
		}

		return sb.toString();
	}

	private String _getConditionJavaExpressionSuffix(String suffix) {
		if (!_iteratorJavaExpressions.isEmpty()) {
			return "; ";
		}

		return ";)" + suffix;
	}

	private String _getInitializationJavaTermsSuffix(String suffix) {
		if (_conditionJavaExpression != null) {
			return "; ";
		}

		if (!_iteratorJavaExpressions.isEmpty()) {
			return ";; ";
		}

		return ";;)" + suffix;
	}

	private String _getIteratorJavaExpressionsPrefix(String prefix) {
		if (!_initializationJavaTerms.isEmpty() ||
			(_conditionJavaExpression != null)) {

			return StringPool.BLANK;
		}

		return prefix + "for (;; ";
	}

	private JavaExpression _conditionJavaExpression;
	private List<JavaTerm> _initializationJavaTerms;
	private List<JavaExpression> _iteratorJavaExpressions;

}