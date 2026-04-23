/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import antlr.CommonHiddenStreamToken;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.tools.java.parser.util.JavaParserUtil;
import com.liferay.portal.tools.java.parser.util.StringUtil;
import com.liferay.portal.tools.java.parser.util.ToolsUtil;

import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class ParsedJavaTerm implements Comparable<ParsedJavaTerm> {

	public ParsedJavaTerm(
		String className, String content, Position endPosition,
		String followingNestedCodeBlockClassName,
		String precedingNestedCodeBlockClassName, Position startPosition) {

		_className = className;
		_content = content;
		_endPosition = endPosition;
		_followingNestedCodeBlockClassName = followingNestedCodeBlockClassName;
		_precedingNestedCodeBlockClassName = precedingNestedCodeBlockClassName;
		_startPosition = startPosition;
	}

	@Override
	public int compareTo(ParsedJavaTerm parsedJavaTerm) {
		return _startPosition.compareTo(parsedJavaTerm.getStartPosition());
	}

	public boolean containsCommentToken() {
		return _containsCommentToken;
	}

	public String getAccessModifier() {
		Matcher matcher = _accessModifierPattern.matcher(_content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public String getClassName() {
		return _className;
	}

	public String getContent() {
		return _content;
	}

	public Position getEndPosition() {
		return _endPosition;
	}

	public int getFollowingLineAction() {
		ParsedJavaTerm nextParsedJavaTerm = getNextParsedJavaTerm();

		if (nextParsedJavaTerm == null) {
			return NO_ACTION_REQUIRED;
		}

		CommonHiddenStreamToken precedingCommentToken =
			nextParsedJavaTerm.getPrecedingCommentToken();

		if (precedingCommentToken != null) {
			while (precedingCommentToken.getHiddenBefore() != null) {
				precedingCommentToken = precedingCommentToken.getHiddenBefore();
			}

			if (((precedingCommentToken.getType() ==
					TokenTypes.BLOCK_COMMENT_BEGIN) &&
				 StringUtil.startsWith(
					 StringUtil.trim(precedingCommentToken.getText()),
					 CharPool.STAR)) ||
				((precedingCommentToken.getType() ==
					TokenTypes.SINGLE_LINE_COMMENT) &&
				 StringUtil.startsWith(
					 precedingCommentToken.getText(), CharPool.SPACE))) {

				return DOUBLE_LINE_BREAK_REQUIRED;
			}

			return NO_ACTION_REQUIRED;
		}

		if (nextParsedJavaTerm.getPrecedingLineAction() != NO_ACTION_REQUIRED) {
			return NO_ACTION_REQUIRED;
		}

		if (_content.endsWith(StringPool.OPEN_CURLY_BRACE)) {
			if (_followingNestedCodeBlockClassName != null) {
				return _getOpenCurlyBraceFollowingLineAction(
					_followingNestedCodeBlockClassName);
			}

			return _getOpenCurlyBraceFollowingLineAction(_className);
		}

		if (Objects.equals(
				StringUtil.trim(_content), StringPool.CLOSE_CURLY_BRACE) ||
			_className.equals(JavaConstructorCall.class.getName()) ||
			_className.equals(JavaMethodDefinition.class.getName()) ||
			_className.equals(JavaEnumConstantDefinitions.class.getName()) ||
			_className.equals(JavaAnnotationFieldDefinition.class.getName())) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		return NO_ACTION_REQUIRED;
	}

	public ParsedJavaTerm getNextParsedJavaTerm() {
		return _nextParsedJavaTerm;
	}

	public CommonHiddenStreamToken getPrecedingCommentToken() {
		return _precedingCommentToken;
	}

	public int getPrecedingLineAction() {
		ParsedJavaTerm previousParsedJavaTerm = getPreviousParsedJavaTerm();

		if (_precedingCommentToken != null) {
			if (previousParsedJavaTerm == null) {
				return DOUBLE_LINE_BREAK_REQUIRED;
			}

			if ((_precedingCommentToken.getType() ==
					TokenTypes.SINGLE_LINE_COMMENT) &&
				StringUtil.startsWith(
					_precedingCommentToken.getText(), CharPool.SPACE)) {

				Position previousEndPosition =
					previousParsedJavaTerm.getEndPosition();

				if (previousEndPosition.getLineNumber() ==
						_precedingCommentToken.getLine()) {

					return NO_ACTION_REQUIRED;
				}

				return DOUBLE_LINE_BREAK_REQUIRED;
			}

			if ((_precedingCommentToken.getType() ==
					TokenTypes.BLOCK_COMMENT_BEGIN) &&
				StringUtil.startsWith(
					StringUtil.trim(_precedingCommentToken.getText()),
					CharPool.STAR) &&
				!StringUtil.startsWith(
					StringUtil.trim(_content), StringPool.CLOSE_CURLY_BRACE)) {

				return SINGLE_LINE_BREAK_REQUIRED;
			}

			return NO_ACTION_REQUIRED;
		}

		if (previousParsedJavaTerm == null) {
			return NO_ACTION_REQUIRED;
		}

		if (_className.equals(JavaVariableDefinition.class.getName()) &&
			_className.equals(previousParsedJavaTerm.getClassName())) {

			return _getBetweenVariableDefinitionsLineAction(
				previousParsedJavaTerm);
		}

		if ((_className.equals(JavaConstructorDefinition.class.getName()) ||
			 _className.equals(JavaMethodDefinition.class.getName())) &&
			!StringUtil.startsWith(
				StringUtil.trim(_content), StringPool.CLOSE_CURLY_BRACE)) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		if (StringUtil.endsWith(
				previousParsedJavaTerm.getContent(),
				CharPool.OPEN_CURLY_BRACE) ||
			Objects.equals(
				previousParsedJavaTerm.getClassName(),
				JavaSwitchCaseStatement.class.getName())) {

			return NO_ACTION_REQUIRED;
		}

		if (StringUtil.startsWith(
				StringUtil.trim(_content), StringPool.CLOSE_CURLY_BRACE)) {

			if (_precedingNestedCodeBlockClassName != null) {
				return _getCloseCurlyBracePrecedingLineAction(
					_precedingNestedCodeBlockClassName);
			}

			return _getCloseCurlyBracePrecedingLineAction(_className);
		}

		if (_className.equals(JavaAnnotationFieldDefinition.class.getName()) ||
			_className.equals(JavaBreakStatement.class.getName()) ||
			_className.equals(JavaContinueStatement.class.getName()) ||
			_className.equals(JavaDoStatement.class.getName()) ||
			_className.equals(JavaEnhancedForStatement.class.getName()) ||
			_className.equals(JavaEnumConstantDefinitions.class.getName()) ||
			_className.equals(JavaForStatement.class.getName()) ||
			_className.equals(JavaIfStatement.class.getName()) ||
			_className.equals(JavaReturnStatement.class.getName()) ||
			_className.equals(JavaStaticInitialization.class.getName()) ||
			_className.equals(JavaSynchronizedStatement.class.getName()) ||
			_className.equals(JavaThrowStatement.class.getName()) ||
			_className.equals(JavaTryStatement.class.getName())) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		if (_className.equals(JavaCatchStatement.class.getName()) ||
			_className.equals(JavaElseStatement.class.getName()) ||
			_className.equals(JavaFinallyStatement.class.getName())) {

			return SINGLE_LINE_BREAK_REQUIRED;
		}

		if (_className.equals(JavaWhileStatement.class.getName())) {
			if (_content.endsWith(StringPool.OPEN_CURLY_BRACE)) {
				return DOUBLE_LINE_BREAK_REQUIRED;
			}

			if (Objects.equals(
					previousParsedJavaTerm.getClassName(),
					JavaDoStatement.class.getName())) {

				return SINGLE_LINE_BREAK_REQUIRED;
			}
		}

		return NO_ACTION_REQUIRED;
	}

	public ParsedJavaTerm getPreviousParsedJavaTerm() {
		return _previousParsedJavaTerm;
	}

	public Position getStartPosition() {
		return _startPosition;
	}

	public String getVariableName() {
		Matcher matcher = _variableNamePattern.matcher(_content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public void setContainsCommentToken(boolean containsCommentToken) {
		_containsCommentToken = containsCommentToken;
	}

	public void setNextParsedJavaTerm(ParsedJavaTerm nextParsedJavaTerm) {
		_nextParsedJavaTerm = nextParsedJavaTerm;
	}

	public void setPrecedingCommentToken(
		CommonHiddenStreamToken precedingCommentToken) {

		_precedingCommentToken = precedingCommentToken;
	}

	public void setPreviousParsedJavaTerm(
		ParsedJavaTerm previousParsedJavaTerm) {

		_previousParsedJavaTerm = previousParsedJavaTerm;
	}

	protected static final int DOUBLE_LINE_BREAK_REQUIRED = 0;

	protected static final int NO_ACTION_REQUIRED = 1;

	protected static final int SINGLE_LINE_BREAK_REQUIRED = 2;

	private boolean _containsUnquoted(String s, String text) {
		int x = -1;

		while (true) {
			x = s.indexOf(text, x + 1);

			if (x == -1) {
				return false;
			}

			if (!ToolsUtil.isInsideQuotes(s, x)) {
				return true;
			}
		}
	}

	private int _getBetweenVariableDefinitionsLineAction(
		ParsedJavaTerm previousJavaTerm) {

		if ((_followingNestedCodeBlockClassName != null) ||
			(previousJavaTerm._followingNestedCodeBlockClassName != null) ||
			(previousJavaTerm._precedingNestedCodeBlockClassName != null) ||
			(previousJavaTerm.getPrecedingCommentToken() != null)) {

			return NO_ACTION_REQUIRED;
		}

		String accessModifier = getAccessModifier();
		String previousAccessModifier = previousJavaTerm.getAccessModifier();

		if ((accessModifier == null) && (previousAccessModifier == null)) {
			return NO_ACTION_REQUIRED;
		}

		if (!Objects.equals(accessModifier, previousAccessModifier) ||
			(previousJavaTerm.getPrecedingCommentToken() != null) ||
			StringUtil.startsWith(StringUtil.trim(_content), CharPool.AT) ||
			StringUtil.startsWith(
				StringUtil.trim(previousJavaTerm.getContent()), CharPool.AT)) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		String previousContent = previousJavaTerm.getContent();

		if (previousContent.matches("(?s).*\\sstatic\\s.*") ^
			_content.matches("(?s).*\\sstatic\\s.*")) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		String previousVariableName = previousJavaTerm.getVariableName();
		String variableName = getVariableName();

		if ((StringUtil.isUpperCase(previousVariableName) &&
			 !StringUtil.isLowerCase(previousVariableName)) ||
			(StringUtil.isUpperCase(variableName) &&
			 !StringUtil.isLowerCase(variableName))) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		if (previousContent.matches("(?s).*\\sstatic\\s.*") &&
			(previousVariableName.equals("_instance") ||
			 previousVariableName.equals("_log") ||
			 previousVariableName.equals("_logger"))) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		return SINGLE_LINE_BREAK_REQUIRED;
	}

	private int _getCloseCurlyBracePrecedingLineAction(String className) {
		if (className.equals(JavaEnumConstantDefinition.class.getName()) ||
			className.equals(JavaClassDefinition.class.getName())) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		if (className.equals(JavaClassCall.class.getName())) {
			ParsedJavaTerm previousParsedJavaTerm = getPreviousParsedJavaTerm();

			String previousClassName = previousParsedJavaTerm.getClassName();

			if (!previousClassName.equals(
					JavaInstanceInitialization.class.getName())) {

				return DOUBLE_LINE_BREAK_REQUIRED;
			}
		}

		return SINGLE_LINE_BREAK_REQUIRED;
	}

	private String _getIndent(String s, int lineNumber) {
		int x = -1;

		for (int i = 1; i < lineNumber; i++) {
			x = s.indexOf(CharPool.NEW_LINE, x + 1);
		}

		StringBundler sb = new StringBundler();

		for (int i = x + 1;; i++) {
			if (s.charAt(i) != CharPool.TAB) {
				return sb.toString();
			}

			sb.append(CharPool.TAB);
		}
	}

	private int _getOpenCurlyBraceFollowingLineAction(String className) {
		if (className.equals(JavaLambdaExpression.class.getName())) {
			if (_content.endsWith(") -> {")) {
				String lastLine = StringUtil.trim(
					JavaParserUtil.getLastLine(_content));

				if (!lastLine.contains(StringPool.OPEN_PARENTHESIS)) {
					return DOUBLE_LINE_BREAK_REQUIRED;
				}
			}

			return SINGLE_LINE_BREAK_REQUIRED;
		}

		ParsedJavaTerm nextParsedJavaTerm = getNextParsedJavaTerm();

		String trimmedNextJavaTermContent = StringUtil.trim(
			nextParsedJavaTerm.getContent());

		if (trimmedNextJavaTermContent.startsWith(
				StringPool.CLOSE_CURLY_BRACE)) {

			return SINGLE_LINE_BREAK_REQUIRED;
		}

		if (className.equals(JavaClassDefinition.class.getName()) ||
			className.equals(JavaEnumConstantDefinition.class.getName())) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		if (Objects.equals(
				nextParsedJavaTerm.getClassName(),
				JavaInstanceInitialization.class.getName())) {

			String lastLine = StringUtil.trim(
				JavaParserUtil.getLastLine(_content));

			if (lastLine.startsWith("new ") ||
				_containsUnquoted(lastLine, " new ")) {

				return SINGLE_LINE_BREAK_REQUIRED;
			}

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		int lineCount = StringUtil.count(_content, CharPool.NEW_LINE) + 1;

		if ((_precedingNestedCodeBlockClassName != null) ||
			((lineCount > 1) &&
			 !Objects.equals(
				 _getIndent(_content, 1), _getIndent(_content, lineCount)))) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		String lastLine = StringUtil.trim(JavaParserUtil.getLastLine(_content));

		int x = lastLine.lastIndexOf(" new ");

		if ((x != -1) && !ToolsUtil.isInsideQuotes(lastLine, x) &&
			(ToolsUtil.getLevel(lastLine.substring(x)) == 0)) {

			return DOUBLE_LINE_BREAK_REQUIRED;
		}

		return SINGLE_LINE_BREAK_REQUIRED;
	}

	private static final Pattern _accessModifierPattern = Pattern.compile(
		"\t(private|protected|public)\\s");
	private static final Pattern _variableNamePattern = Pattern.compile(
		"\\s([\\w$]+)( =|;)");

	private final String _className;
	private boolean _containsCommentToken;
	private final String _content;
	private final Position _endPosition;
	private final String _followingNestedCodeBlockClassName;
	private ParsedJavaTerm _nextParsedJavaTerm;
	private CommonHiddenStreamToken _precedingCommentToken;
	private final String _precedingNestedCodeBlockClassName;
	private ParsedJavaTerm _previousParsedJavaTerm;
	private final Position _startPosition;

}