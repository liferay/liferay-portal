/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser;

import antlr.CommonHiddenStreamToken;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ImportsFormatter;
import com.liferay.portal.tools.JavaImportsFormatter;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.portal.tools.java.parser.util.DetailASTUtil;
import com.liferay.portal.tools.java.parser.util.FileUtil;
import com.liferay.portal.tools.java.parser.util.JavaParserUtil;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class JavaParser {

	public static String parse(File file, int maxLineLength)
		throws CheckstyleException, IOException {

		return parse(file, maxLineLength, true);
	}

	public static String parse(File file, int maxLineLength, boolean writeFile)
		throws CheckstyleException, IOException {

		return parse(file, FileUtil.read(file), maxLineLength, writeFile);
	}

	public static String parse(File file, String content, int maxLineLength)
		throws CheckstyleException, IOException {

		return parse(file, content, maxLineLength, true);
	}

	public static String parse(
			File file, String content, int maxLineLength, boolean writeFile)
		throws CheckstyleException, IOException {

		return parse(
			file, ToolsUtil.getPackagePath(file), content, maxLineLength,
			writeFile);
	}

	public static String parse(
			File file, String packagePath, String content, int maxLineLength,
			boolean writeFile)
		throws CheckstyleException, IOException {

		String newContent = _parse(
			file, packagePath, content, maxLineLength, false);

		if (writeFile && !newContent.equals(content)) {
			FileUtil.write(file, newContent);
		}

		return newContent;
	}

	public static String parseSnippet(String content, String indent) {
		int level = ToolsUtil.getLevel(
			content, StringPool.OPEN_CURLY_BRACE, StringPool.CLOSE_CURLY_BRACE);

		int additionalOpenCurlyBracesCount = Math.max(0, -level);
		int additionalCloseCurlyBracesCount = Math.max(0, level);

		int failureCount = 0;

		while (true) {
			StringBundler sb = new StringBundler();

			sb.append("public class Test {\n\n");
			sb.append("\tpublic void method() {\n");

			for (int i = 0; i < additionalOpenCurlyBracesCount; i++) {
				sb.append("if (true) {\n");
			}

			sb.append(content);
			sb.append("\n");

			for (int i = 0; i < additionalCloseCurlyBracesCount; i++) {
				sb.append("}\n");
			}

			sb.append("\t}\n\n");
			sb.append("}");

			String javaContent = sb.toString();

			String newJavaContent = null;

			try {
				String fileName = StringBundler.concat(
					SystemProperties.get(SystemProperties.TMP_DIR),
					StringPool.SLASH, System.currentTimeMillis(),
					PwdGenerator.getPassword(8, PwdGenerator.KEY2), ".java");

				File file = new File(fileName);

				newJavaContent = _parse(
					file, ToolsUtil.getPackagePath(file), javaContent,
					JavaParserUtil.NO_MAX_LINE_LENGTH, true);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				failureCount++;

				if (failureCount > 5) {
					return content;
				}

				additionalOpenCurlyBracesCount++;
				additionalCloseCurlyBracesCount++;

				continue;
			}

			if (javaContent.equals(newJavaContent)) {
				return content;
			}

			String[] lines = StringUtil.splitLines(newJavaContent);

			lines = ArrayUtil.subset(
				lines, 3 + additionalOpenCurlyBracesCount,
				lines.length - 3 - additionalCloseCurlyBracesCount);

			sb = new StringBundler(lines.length - 6);

			for (String line : lines) {
				sb.append(_adjustIndent(line, indent));
				sb.append(CharPool.NEW_LINE);
			}

			sb.setIndex(sb.index() - 1);

			return sb.toString();
		}
	}

	private static ParsedJavaClass _addClosingJavaTerm(
		ParsedJavaClass parsedJavaClass, DetailAST closingDetailAST,
		FileContents fileContents, String className, int maxLineLength) {

		DetailAST rcurlyDetailAST = null;

		if (closingDetailAST.getType() == TokenTypes.LCURLY) {
			DetailAST parentDetailAST = closingDetailAST.getParent();

			rcurlyDetailAST = parentDetailAST.getLastChild();
		}
		else if (closingDetailAST.getType() == TokenTypes.SLIST) {
			rcurlyDetailAST = closingDetailAST.getLastChild();
		}

		if (rcurlyDetailAST != null) {
			JavaClosingBrace javaClosingBrace = new JavaClosingBrace();

			String curlyExpectedIndent = _getExpectedIndent(
				rcurlyDetailAST, fileContents);

			String content = javaClosingBrace.toString(
				curlyExpectedIndent, StringPool.BLANK, maxLineLength);

			parsedJavaClass.addJavaTerm(
				content, DetailASTUtil.getStartPosition(rcurlyDetailAST),
				DetailASTUtil.getEndPosition(rcurlyDetailAST, fileContents),
				className);
		}

		return parsedJavaClass;
	}

	private static ContentModifications _addCommentContentModifications(
		ContentModifications contentModifications,
		ParsedJavaTerm parsedJavaTerm, String indent,
		FileContents fileContents) {

		CommonHiddenStreamToken precedingCommentToken =
			parsedJavaTerm.getPrecedingCommentToken();

		if (precedingCommentToken == null) {
			return contentModifications;
		}

		String expectedCommentIndent = indent;

		String trimmedJavaTermContent = StringUtil.trim(
			parsedJavaTerm.getContent());

		if (trimmedJavaTermContent.startsWith("}") ||
			trimmedJavaTermContent.startsWith(")")) {

			expectedCommentIndent += "\t";
		}

		while (true) {
			if (precedingCommentToken == null) {
				return contentModifications;
			}

			String line = fileContents.getLine(
				precedingCommentToken.getLine() - 1);

			if (!_isAtLineStart(line, precedingCommentToken.getColumn() - 1)) {
				return contentModifications;
			}

			String actualCommentIndent = _getIndent(line);

			if (!actualCommentIndent.equals(expectedCommentIndent)) {
				contentModifications.addReplaceContent(
					expectedCommentIndent + StringUtil.trim(line),
					precedingCommentToken.getLine());
			}

			if (precedingCommentToken.getType() ==
					TokenTypes.SINGLE_LINE_COMMENT) {

				precedingCommentToken = precedingCommentToken.getHiddenBefore();

				continue;
			}

			String text = precedingCommentToken.getText();

			boolean javadoc = false;

			if (StringUtil.startsWith(StringUtil.trim(text), CharPool.STAR)) {
				javadoc = true;
			}
			else if (actualCommentIndent.equals(expectedCommentIndent)) {
				precedingCommentToken = precedingCommentToken.getHiddenBefore();

				continue;
			}

			int end =
				precedingCommentToken.getLine() + StringUtil.count(text, "\n");

			for (int i = precedingCommentToken.getLine() + 1; i <= end; i++) {
				line = fileContents.getLine(i - 1);

				if (Validator.isNull(line)) {
					contentModifications.addRemoveLineLineNumber(i);
				}

				if (javadoc) {
					String actualIndent = _getIndent(line);

					if (!actualIndent.equals(
							expectedCommentIndent + StringPool.SPACE)) {

						contentModifications.addReplaceContent(
							expectedCommentIndent + StringPool.SPACE +
								StringUtil.trim(line),
							i);
					}
				}
				else if (line.startsWith(actualCommentIndent)) {
					contentModifications.addReplaceContent(
						StringUtil.replaceFirst(
							line, actualCommentIndent, expectedCommentIndent),
						i);
				}
			}

			precedingCommentToken = precedingCommentToken.getHiddenBefore();
		}
	}

	private static ContentModifications _addContentModifications(
		ContentModifications contentModifications,
		ParsedJavaTerm parsedJavaTerm, FileContents fileContents) {

		if (parsedJavaTerm.getContent() == null) {
			return contentModifications;
		}

		Position endPosition = parsedJavaTerm.getEndPosition();

		int endLineNumber = endPosition.getLineNumber();

		String endLine = fileContents.getLine(endLineNumber - 1);

		if (!_isAtLineEnd(endLine, endPosition.getLinePosition())) {
			return contentModifications;
		}

		int followingLineAction = parsedJavaTerm.getFollowingLineAction();

		if (followingLineAction != ParsedJavaTerm.NO_ACTION_REQUIRED) {
			String trimmedFollowingLine = StringUtil.trim(
				fileContents.getLine(endLineNumber));

			if ((followingLineAction ==
					ParsedJavaTerm.DOUBLE_LINE_BREAK_REQUIRED) &&
				Validator.isNotNull(trimmedFollowingLine)) {

				contentModifications.addInsertLineBreakLineNumber(
					endLineNumber + 1);
			}
			else if ((followingLineAction ==
						ParsedJavaTerm.SINGLE_LINE_BREAK_REQUIRED) &&
					 Validator.isNull(trimmedFollowingLine)) {

				contentModifications.addRemoveLineLineNumber(endLineNumber + 1);
			}
		}

		String expectedJavaTermContent = parsedJavaTerm.getContent();

		Position startPosition = parsedJavaTerm.getStartPosition();

		int startLineNumber = startPosition.getLineNumber();

		String actualJavaTermContent = _getContent(
			fileContents, startLineNumber, endLineNumber);

		if (!actualJavaTermContent.startsWith(expectedJavaTermContent) &&
			!_isExcludedJavaTerm(parsedJavaTerm) &&
			(!actualJavaTermContent.contains("\n\n") ||
			 !Objects.equals(
				 parsedJavaTerm.getClassName(),
				 JavaTryStatement.class.getName()))) {

			contentModifications.addReplaceContent(
				parsedJavaTerm.getContent(), startPosition.getLineNumber(),
				endPosition.getLineNumber());
		}

		int precedingLineAction = parsedJavaTerm.getPrecedingLineAction();

		if (precedingLineAction != ParsedJavaTerm.NO_ACTION_REQUIRED) {
			String trimmedPrecedingLine = StringUtil.trim(
				fileContents.getLine(startLineNumber - 2));

			if ((precedingLineAction ==
					ParsedJavaTerm.DOUBLE_LINE_BREAK_REQUIRED) &&
				Validator.isNotNull(trimmedPrecedingLine)) {

				contentModifications.addInsertLineBreakLineNumber(
					startLineNumber);
			}
			else if ((precedingLineAction ==
						ParsedJavaTerm.SINGLE_LINE_BREAK_REQUIRED) &&
					 Validator.isNull(trimmedPrecedingLine)) {

				contentModifications.addRemoveLineLineNumber(
					startLineNumber - 1);
			}
		}

		return _addCommentContentModifications(
			contentModifications, parsedJavaTerm,
			_getIndent(expectedJavaTermContent), fileContents);
	}

	private static ParsedJavaClass _addJavaTerm(
			ParsedJavaClass parsedJavaClass, DetailAST detailAST,
			JavaTerm javaTerm, FileContents fileContents, int maxLineLength)
		throws IOException {

		if (javaTerm == null) {
			return parsedJavaClass;
		}

		Class<?> clazz = javaTerm.getClass();

		String className = clazz.getName();

		DetailAST closingDetailAST = DetailASTUtil.getClosingDetailAST(
			detailAST);

		if (closingDetailAST != null) {
			parsedJavaClass = _addClosingJavaTerm(
				parsedJavaClass, closingDetailAST, fileContents, className,
				maxLineLength);
		}

		Position startPosition = DetailASTUtil.getStartPosition(detailAST);

		String javaTermContent = javaTerm.toString(
			_getExpectedIndent(detailAST, fileContents), StringPool.BLANK,
			maxLineLength);

		if (javaTermContent.contains(
				"\n" + JavaClassCall.NESTED_CODE_BLOCK + "\n") ||
			javaTermContent.contains(
				"\n" + JavaEnumConstantDefinition.NESTED_CODE_BLOCK + "\n") ||
			javaTermContent.contains(
				"\n" + JavaLambdaExpression.NESTED_CODE_BLOCK + "\n") ||
			javaTermContent.contains(
				"\n" + JavaSwitchExpression.NESTED_CODE_BLOCK + "\n")) {

			return _addJavaTermWithNestedCodeBlocks(
				parsedJavaClass, detailAST, javaTermContent, className,
				startPosition, fileContents);
		}

		Position endPosition = null;

		if (detailAST.getType() == TokenTypes.ENUM_CONSTANT_DEF) {
			endPosition = DetailASTUtil.getEndPosition(
				_getLastEnumConstantDefinitionDetailAST(detailAST),
				fileContents);
		}
		else {
			endPosition = DetailASTUtil.getEndPosition(detailAST, fileContents);
		}

		parsedJavaClass.addJavaTerm(
			javaTermContent, startPosition, endPosition, className);

		return parsedJavaClass;
	}

	private static ParsedJavaClass _addJavaTermWithNestedCodeBlocks(
			ParsedJavaClass parsedJavaClass, DetailAST detailAST,
			String javaTermContent, String className, Position startPosition,
			FileContents fileContents)
		throws IOException {

		String followingNestedCodeBlockClassName = null;
		String precedingNestedCodeBlockClassName = null;

		List<Position> curlyBracePositionList = _getCurlyBracePositionList(
			new ArrayList<>(), detailAST);

		Collections.sort(curlyBracePositionList);

		int count = 0;

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new UnsyncStringReader(javaTermContent))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.equals(JavaClassCall.NESTED_CODE_BLOCK)) {
					followingNestedCodeBlockClassName =
						JavaClassCall.class.getName();
				}
				else if (line.equals(
							JavaEnumConstantDefinition.NESTED_CODE_BLOCK)) {

					followingNestedCodeBlockClassName =
						JavaEnumConstantDefinition.class.getName();
				}
				else if (line.equals(JavaLambdaExpression.NESTED_CODE_BLOCK)) {
					followingNestedCodeBlockClassName =
						JavaLambdaExpression.class.getName();
				}
				else if (line.equals(JavaSwitchExpression.NESTED_CODE_BLOCK)) {
					followingNestedCodeBlockClassName =
						JavaSwitchExpression.class.getName();
				}
				else {
					sb.append(line);
					sb.append("\n");

					continue;
				}

				sb.setIndex(sb.index() - 1);

				Position partStartPosition = null;

				if (count == 0) {
					partStartPosition = startPosition;
				}
				else {
					partStartPosition = curlyBracePositionList.get(
						(count * 2) - 1);
				}

				Position partEndPosition = curlyBracePositionList.get(
					count * 2);

				parsedJavaClass.addJavaTerm(
					sb.toString(), partStartPosition, partEndPosition,
					className, precedingNestedCodeBlockClassName,
					followingNestedCodeBlockClassName);

				sb.setIndex(0);

				precedingNestedCodeBlockClassName =
					followingNestedCodeBlockClassName;

				count++;
			}
		}

		sb.setIndex(sb.index() - 1);

		Position partEndPosition = DetailASTUtil.getEndPosition(
			detailAST, fileContents);
		Position partStartPosition = curlyBracePositionList.get(
			(count * 2) - 1);

		parsedJavaClass.addJavaTerm(
			sb.toString(), partStartPosition, partEndPosition, className,
			precedingNestedCodeBlockClassName, null);

		return parsedJavaClass;
	}

	private static String _adjustIndent(String line, String indent) {
		if (Validator.isNull(line) || (indent.length() == 2)) {
			return line;
		}

		if (indent.length() < 2) {
			for (int i = 0; i < (2 - indent.length()); i++) {
				line = StringUtil.replaceFirst(
					line, CharPool.TAB, StringPool.BLANK);
			}

			return line;
		}

		for (int i = 0; i < (indent.length() - 2); i++) {
			line = StringPool.TAB + line;
		}

		return line;
	}

	private static String _fixContent(
		String content, String javaTermContent, Position startPosition,
		Position endPosition) {

		int x = _getLineStartPos(content, startPosition.getLineNumber());
		int y =
			_getLineStartPos(content, endPosition.getLineNumber()) +
				endPosition.getLinePosition();

		return StringUtil.replaceFirst(
			content, content.substring(x, y), javaTermContent, x - 1);
	}

	private static String _fixIncorrectStartOrEndPositions(
		String content, ParsedJavaClass parsedJavaClass,
		FileContents fileContents) {

		ParsedJavaTerm parsedJavaTerm = parsedJavaClass.getLastParsedJavaTerm();

		while (true) {
			if (parsedJavaTerm == null) {
				return content;
			}

			if (parsedJavaTerm.containsCommentToken() ||
				(parsedJavaTerm.getContent() == null)) {

				parsedJavaTerm = parsedJavaTerm.getPreviousParsedJavaTerm();

				continue;
			}

			Position startPosition = parsedJavaTerm.getStartPosition();

			int startLineNumber = startPosition.getLineNumber();

			if (!_isAtLineStart(
					fileContents.getLine(startLineNumber - 1),
					startPosition.getLinePosition())) {

				int lineStartPos = _getLineStartPos(
					content, startPosition.getLineNumber());

				content = StringUtil.insert(
					content, "\n",
					lineStartPos + startPosition.getLinePosition());

				parsedJavaTerm = parsedJavaTerm.getPreviousParsedJavaTerm();

				continue;
			}

			Position endPosition = parsedJavaTerm.getEndPosition();

			int endLineNumber = endPosition.getLineNumber();

			String endLine = fileContents.getLine(endLineNumber - 1);

			int endLinePosition = endPosition.getLinePosition();

			if (!_isAtLineEnd(endLine, endLinePosition)) {
				String remainder = StringUtil.trim(
					endLine.substring(endLinePosition));

				if (Validator.isNull(remainder)) {
					content = _trimLine(content, endLine, endLineNumber);
				}
				else if (remainder.startsWith("//") ||
						 remainder.startsWith("/*")) {

					content = _fixContent(
						content, parsedJavaTerm.getContent(), startPosition,
						endPosition);
				}
			}

			parsedJavaTerm = parsedJavaTerm.getPreviousParsedJavaTerm();
		}
	}

	private static String _getContent(
		FileContents fileContents, int start, int end) {

		StringBundler sb = new StringBundler();

		for (int i = start; i <= end; i++) {
			sb.append(fileContents.getLine(i - 1));
			sb.append("\n");
		}

		return sb.toString();
	}

	private static List<Position> _getCurlyBracePositionList(
		List<Position> curlyBracePositionList, DetailAST detailAST) {

		if (detailAST == null) {
			return curlyBracePositionList;
		}

		if ((detailAST.getType() == TokenTypes.ENUM_CONSTANT_DEF) ||
			(detailAST.getType() == TokenTypes.LITERAL_NEW)) {

			DetailAST objblockDetailAST = detailAST.findFirstToken(
				TokenTypes.OBJBLOCK);

			if ((objblockDetailAST != null) &&
				(objblockDetailAST.getChildCount() > 2)) {

				DetailAST leftCurlyDetailAST =
					objblockDetailAST.getFirstChild();

				curlyBracePositionList.add(
					new Position(
						leftCurlyDetailAST.getLineNo(),
						leftCurlyDetailAST.getColumnNo() + 1));

				DetailAST rightCurlyDetailAST =
					objblockDetailAST.getLastChild();

				curlyBracePositionList.add(
					new Position(
						rightCurlyDetailAST.getLineNo(),
						rightCurlyDetailAST.getColumnNo()));
			}
		}
		else if (detailAST.getType() == TokenTypes.LAMBDA) {
			DetailAST lastChildDetailAST = detailAST.getLastChild();

			if ((lastChildDetailAST != null) &&
				(lastChildDetailAST.getType() == TokenTypes.SLIST)) {

				curlyBracePositionList.add(
					new Position(
						lastChildDetailAST.getLineNo(),
						lastChildDetailAST.getColumnNo() + 1));

				lastChildDetailAST = lastChildDetailAST.getLastChild();

				curlyBracePositionList.add(
					new Position(
						lastChildDetailAST.getLineNo(),
						lastChildDetailAST.getColumnNo()));
			}
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_SWITCH) {
			DetailAST switchRuleDetailAST = detailAST.findFirstToken(
				TokenTypes.SWITCH_RULE);

			if (switchRuleDetailAST != null) {
				DetailAST previousSiblingDetailAST =
					switchRuleDetailAST.getPreviousSibling();

				curlyBracePositionList.add(
					new Position(
						previousSiblingDetailAST.getLineNo(),
						previousSiblingDetailAST.getColumnNo() + 1));

				DetailAST lastChildDetailAST = detailAST.getLastChild();

				curlyBracePositionList.add(
					new Position(
						lastChildDetailAST.getLineNo(),
						lastChildDetailAST.getColumnNo()));
			}
		}

		if ((detailAST.getType() != TokenTypes.OBJBLOCK) &&
			(detailAST.getType() != TokenTypes.SLIST)) {

			curlyBracePositionList = _getCurlyBracePositionList(
				curlyBracePositionList, detailAST.getFirstChild());
		}

		return _getCurlyBracePositionList(
			curlyBracePositionList, detailAST.getNextSibling());
	}

	private static String _getExpectedIndent(
		DetailAST detailAST, FileContents fileContents) {

		String indent = StringPool.BLANK;

		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			if (parentDetailAST == null) {
				break;
			}

			if ((parentDetailAST.getType() == TokenTypes.ELIST) ||
				(parentDetailAST.getType() == TokenTypes.LITERAL_SWITCH) ||
				(parentDetailAST.getType() == TokenTypes.OBJBLOCK) ||
				(parentDetailAST.getType() == TokenTypes.SLIST)) {

				indent += StringPool.TAB;
			}

			if (parentDetailAST.getType() == TokenTypes.OBJBLOCK) {
				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() == TokenTypes.LITERAL_NEW) {
					String expectedIndent = _getExpectedIndent(
						parentDetailAST, fileContents);

					String actualIndent = _getIndent(
						fileContents.getLine(parentDetailAST.getLineNo() - 1));

					if (actualIndent.startsWith(expectedIndent)) {
						indent += StringUtil.replaceFirst(
							actualIndent, expectedIndent, StringPool.BLANK);
					}
				}

				continue;
			}

			if (parentDetailAST.getType() ==
					TokenTypes.RESOURCE_SPECIFICATION) {

				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() == TokenTypes.LITERAL_TRY) {
					indent += "\t";
				}

				continue;
			}

			if (parentDetailAST.getType() == TokenTypes.SLIST) {
				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() == TokenTypes.LAMBDA) {
					int lambdaLineNumber = parentDetailAST.getLineNo();

					DetailAST firstChildDetailAST =
						parentDetailAST.getFirstChild();

					if (firstChildDetailAST.getType() == TokenTypes.LPAREN) {
						lambdaLineNumber = firstChildDetailAST.getLineNo();
					}

					String actualIndent = StringUtil.removeChar(
						_getIndent(fileContents.getLine(lambdaLineNumber - 1)),
						CharPool.SPACE);

					String expectedIndent = _getExpectedIndent(
						parentDetailAST, fileContents);

					if (actualIndent.startsWith(expectedIndent)) {
						indent += StringUtil.replaceFirst(
							actualIndent, expectedIndent, StringPool.BLANK);
					}
				}

				continue;
			}

			if ((parentDetailAST.getType() != TokenTypes.LITERAL_ELSE) &&
				(parentDetailAST.getType() != TokenTypes.LITERAL_IF) &&
				(parentDetailAST.getType() != TokenTypes.SLIST)) {

				DetailAST grandParentDetailAST = parentDetailAST.getParent();

				if ((grandParentDetailAST != null) &&
					((grandParentDetailAST.getType() ==
						TokenTypes.LITERAL_ELSE) ||
					 (grandParentDetailAST.getType() ==
						 TokenTypes.LITERAL_IF) ||
					 (grandParentDetailAST.getType() ==
						 TokenTypes.LITERAL_WHILE))) {

					indent += "\t";
				}
			}

			if (((parentDetailAST.getType() == TokenTypes.LITERAL_FOR) ||
				 (parentDetailAST.getType() == TokenTypes.LITERAL_IF) ||
				 (parentDetailAST.getType() == TokenTypes.LITERAL_WHILE)) &&
				(parentDetailAST.findFirstToken(TokenTypes.SLIST) == null)) {

				indent += "\t";
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		if (detailAST.getType() != TokenTypes.RCURLY) {
			return indent;
		}

		return StringUtil.replaceFirst(indent, "\t", "");
	}

	private static String _getIndent(String s) {
		while (s.startsWith("\n")) {
			s = s.substring(1);
		}

		StringBundler sb = new StringBundler(s.length());

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (!Character.isWhitespace(c)) {
				break;
			}

			sb.append(c);
		}

		return sb.toString();
	}

	private static DetailAST _getLastEnumConstantDefinitionDetailAST(
		DetailAST enumConstantDefinitionDetailAST) {

		DetailAST lastEnumConstantDefinitionDetailAST =
			enumConstantDefinitionDetailAST;

		DetailAST nextSiblingDetailAST =
			enumConstantDefinitionDetailAST.getNextSibling();

		while (nextSiblingDetailAST != null) {
			if (nextSiblingDetailAST.getType() ==
					TokenTypes.ENUM_CONSTANT_DEF) {

				lastEnumConstantDefinitionDetailAST = nextSiblingDetailAST;
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();
		}

		return lastEnumConstantDefinitionDetailAST;
	}

	private static List<String> _getLines(String s) throws IOException {
		List<String> lines = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(s))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	private static int _getLineStartPos(String content, int lineNumber) {
		if (lineNumber <= 0) {
			return -1;
		}

		if (lineNumber == 1) {
			return 0;
		}

		int x = -1;

		for (int i = 1; i < lineNumber; i++) {
			x = content.indexOf(CharPool.NEW_LINE, x + 1);

			if (x == -1) {
				return x;
			}
		}

		return x + 1;
	}

	private static ParsedJavaClass _getParsedJavaClass(
			DetailAST rootDetailAST, FileContents fileContents,
			int maxLineLength)
		throws IOException {

		ParsedJavaClass parsedJavaClass = _walk(
			new ParsedJavaClass(), rootDetailAST, fileContents, maxLineLength);

		parsedJavaClass.processCommentTokens();

		return parsedJavaClass;
	}

	private static boolean _isAtLineEnd(String line, int x) {
		if (line.length() == x) {
			return true;
		}

		return false;
	}

	private static boolean _isAtLineStart(String line, int x) {
		return Validator.isNull(StringUtil.trim(line.substring(0, x)));
	}

	private static boolean _isExcludedJavaTerm(ParsedJavaTerm parsedJavaTerm) {
		CommonHiddenStreamToken precedingCommentToken =
			parsedJavaTerm.getPrecedingCommentToken();

		while (true) {
			if (precedingCommentToken == null) {
				return false;
			}

			if ((precedingCommentToken.getType() ==
					TokenTypes.SINGLE_LINE_COMMENT) &&
				StringUtil.startsWith(
					StringUtil.trim(precedingCommentToken.getText()),
					"Skip JavaParser")) {

				return true;
			}

			precedingCommentToken = precedingCommentToken.getHiddenBefore();
		}
	}

	private static String _parse(
			File file, String packagePath, String content, int maxLineLength,
			boolean abortOnNestedCommentToken)
		throws CheckstyleException, IOException {

		List<String> lines = _getLines(content);

		FileText fileText = new FileText(file, lines);

		FileContents fileContents = new FileContents(fileText);

		DetailAST rootDetailAST =
			com.puppycrawl.tools.checkstyle.JavaParser.parse(fileContents);

		ParsedJavaClass parsedJavaClass = _getParsedJavaClass(
			rootDetailAST, fileContents, maxLineLength);

		if (abortOnNestedCommentToken &&
			parsedJavaClass.containsNestedCommentToken()) {

			return content;
		}

		String newContent = _fixIncorrectStartOrEndPositions(
			content, parsedJavaClass, fileContents);

		if (!newContent.equals(content)) {
			return _parse(file, packagePath, newContent, maxLineLength, false);
		}

		newContent = _parseContent(parsedJavaClass, fileContents, lines);

		if (!newContent.equals(content)) {
			return _parse(file, packagePath, newContent, maxLineLength, false);
		}

		ImportsFormatter importsFormatter = new JavaImportsFormatter();

		newContent = importsFormatter.format(
			_trimContent(newContent), packagePath,
			StringUtil.replaceLast(file.getName(), ".java", StringPool.BLANK));

		if (!newContent.equals(content)) {
			return _parse(file, packagePath, newContent, maxLineLength, false);
		}

		return newContent;
	}

	private static String _parseContent(
		ParsedJavaClass parsedJavaClass, FileContents fileContents,
		List<String> lines) {

		ContentModifications contentModifications = new ContentModifications();

		ParsedJavaTerm parsedJavaTerm = parsedJavaClass.getLastParsedJavaTerm();

		while (true) {
			if (parsedJavaTerm == null) {
				break;
			}

			if (!parsedJavaTerm.containsCommentToken()) {
				contentModifications = _addContentModifications(
					contentModifications, parsedJavaTerm, fileContents);
			}

			parsedJavaTerm = parsedJavaTerm.getPreviousParsedJavaTerm();
		}

		StringBundler sb = new StringBundler();

		for (int i = 0; i < lines.size(); i++) {
			if (contentModifications.isRemoveLineLineNumber(i + 1)) {
				continue;
			}

			if (contentModifications.isInsertLineLineNumber(i + 1)) {
				sb.append("\n");
			}

			Tuple replaceContentTuple =
				contentModifications.getReplaceContentTuple(i + 1);

			if (replaceContentTuple == null) {
				sb.append(lines.get(i));
				sb.append("\n");

				continue;
			}

			sb.append((String)replaceContentTuple.getObject(0));
			sb.append("\n");

			i = (int)replaceContentTuple.getObject(1) - 1;
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static ParsedJavaClass _parseDetailAST(
			ParsedJavaClass parsedJavaClass, DetailAST detailAST,
			FileContents fileContents, int maxLineLength)
		throws IOException {

		if (detailAST == null) {
			return parsedJavaClass;
		}

		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			DetailAST previousSiblingDetailAST = detailAST.getPreviousSibling();

			if ((previousSiblingDetailAST != null) &&
				(previousSiblingDetailAST.getType() == TokenTypes.COMMA)) {

				return parsedJavaClass;
			}
		}

		JavaTerm javaTerm = JavaParserUtil.parseJavaTerm(detailAST);

		if (javaTerm != null) {
			parsedJavaClass = _addJavaTerm(
				parsedJavaClass, detailAST, javaTerm, fileContents,
				maxLineLength);
		}

		if (detailAST.getType() == TokenTypes.LITERAL_DO) {
			DetailAST doWhileDetailAST = detailAST.findFirstToken(
				TokenTypes.DO_WHILE);

			parsedJavaClass = _parseDetailAST(
				parsedJavaClass, doWhileDetailAST, fileContents, maxLineLength);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_ELSE) {
			DetailAST firstChildDetailAST = detailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.LITERAL_IF) {
				if (firstChildDetailAST.findFirstToken(TokenTypes.SLIST) ==
						null) {

					DetailAST rparentDetailAST =
						firstChildDetailAST.findFirstToken(TokenTypes.RPAREN);

					if (rparentDetailAST != null) {
						parsedJavaClass = _parseDetailAST(
							parsedJavaClass, rparentDetailAST.getNextSibling(),
							fileContents, maxLineLength);
					}
				}
			}
			else if (firstChildDetailAST.getType() != TokenTypes.SLIST) {
				parsedJavaClass = _parseDetailAST(
					parsedJavaClass, firstChildDetailAST, fileContents,
					maxLineLength);
			}
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_IF) {
			DetailAST literalIfDetailAST = detailAST;

			while (true) {
				DetailAST literalElseDetailAST =
					literalIfDetailAST.findFirstToken(TokenTypes.LITERAL_ELSE);

				if (literalElseDetailAST == null) {
					break;
				}

				parsedJavaClass = _parseDetailAST(
					parsedJavaClass, literalElseDetailAST, fileContents,
					maxLineLength);

				literalIfDetailAST = literalElseDetailAST.findFirstToken(
					TokenTypes.LITERAL_IF);

				if (literalIfDetailAST == null) {
					break;
				}
			}
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_SWITCH) {
			List<DetailAST> caseGroupDetailASTs =
				DetailASTUtil.getAllChildTokens(
					detailAST, false, TokenTypes.CASE_GROUP);

			for (DetailAST caseGroupDetailAST : caseGroupDetailASTs) {
				parsedJavaClass = _parseDetailAST(
					parsedJavaClass, caseGroupDetailAST, fileContents,
					maxLineLength);
			}

			List<DetailAST> switchRuleDetailASTs =
				DetailASTUtil.getAllChildTokens(
					detailAST, false, TokenTypes.SWITCH_RULE);

			for (DetailAST switchRuleDetailAST : switchRuleDetailASTs) {
				parsedJavaClass = _parseDetailAST(
					parsedJavaClass, switchRuleDetailAST, fileContents,
					maxLineLength);
			}
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_TRY) {
			List<DetailAST> literalCatchDetailASTs =
				DetailASTUtil.getAllChildTokens(
					detailAST, false, TokenTypes.LITERAL_CATCH);

			for (DetailAST literalCatchDetailAST : literalCatchDetailASTs) {
				parsedJavaClass = _parseDetailAST(
					parsedJavaClass, literalCatchDetailAST, fileContents,
					maxLineLength);
			}

			DetailAST literalFinallyDetailAST = detailAST.findFirstToken(
				TokenTypes.LITERAL_FINALLY);

			if (literalFinallyDetailAST != null) {
				parsedJavaClass = _parseDetailAST(
					parsedJavaClass, literalFinallyDetailAST, fileContents,
					maxLineLength);
			}
		}

		if (((detailAST.getType() == TokenTypes.LITERAL_FOR) ||
			 (detailAST.getType() == TokenTypes.LITERAL_IF) ||
			 (detailAST.getType() == TokenTypes.LITERAL_WHILE)) &&
			(detailAST.findFirstToken(TokenTypes.SLIST) == null)) {

			DetailAST rparentDetailAST = detailAST.findFirstToken(
				TokenTypes.RPAREN);

			if (rparentDetailAST != null) {
				parsedJavaClass = _parseDetailAST(
					parsedJavaClass, rparentDetailAST.getNextSibling(),
					fileContents, maxLineLength);
			}
		}

		return parsedJavaClass;
	}

	private static String _trimContent(String content) throws IOException {
		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				sb.append(StringUtil.trimTrailing(line));
				sb.append("\n");
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		String newContent = sb.toString();

		while (true) {
			if (!newContent.contains("\n\n\n")) {
				return newContent;
			}

			newContent = StringUtil.replace(newContent, "\n\n\n", "\n\n");
		}
	}

	private static String _trimLine(
		String content, String line, int lineNumber) {

		int x = _getLineStartPos(content, lineNumber);

		return StringUtil.replaceFirst(
			content, line, StringUtil.trimTrailing(line), x);
	}

	private static ParsedJavaClass _walk(
			ParsedJavaClass parsedJavaClass, DetailAST detailAST,
			FileContents fileContents, int maxLineLength)
		throws IOException {

		if (detailAST == null) {
			return parsedJavaClass;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (((detailAST.getType() == TokenTypes.ANNOTATION_DEF) ||
			 (detailAST.getType() == TokenTypes.CLASS_DEF) ||
			 (detailAST.getType() == TokenTypes.ENUM_DEF) ||
			 (detailAST.getType() == TokenTypes.INTERFACE_DEF) ||
			 (detailAST.getType() == TokenTypes.RECORD_DEF)) &&
			((parentDetailAST == null) ||
			 (parentDetailAST.getType() != TokenTypes.OBJBLOCK))) {

			parsedJavaClass = _parseDetailAST(
				parsedJavaClass, detailAST, fileContents, maxLineLength);
		}
		else if ((detailAST.getType() == TokenTypes.IMPORT) ||
				 (detailAST.getType() == TokenTypes.PACKAGE_DEF) ||
				 (detailAST.getType() == TokenTypes.STATIC_IMPORT) ||
				 (detailAST.getType() == TokenTypes.SWITCH_RULE)) {

			parsedJavaClass = _parseDetailAST(
				parsedJavaClass, detailAST, fileContents, maxLineLength);
		}

		if ((parentDetailAST != null) &&
			((parentDetailAST.getType() == TokenTypes.OBJBLOCK) ||
			 (parentDetailAST.getType() == TokenTypes.SLIST))) {

			parsedJavaClass = _parseDetailAST(
				parsedJavaClass, detailAST, fileContents, maxLineLength);
		}

		CommonHiddenStreamToken commonHiddenStreamToken =
			DetailASTUtil.getHiddenBefore(detailAST);

		if (commonHiddenStreamToken != null) {
			parsedJavaClass.addPrecedingCommentToken(
				commonHiddenStreamToken,
				DetailASTUtil.getStartPosition(detailAST));
		}

		parsedJavaClass = _walk(
			parsedJavaClass, detailAST.getFirstChild(), fileContents,
			maxLineLength);
		parsedJavaClass = _walk(
			parsedJavaClass, detailAST.getNextSibling(), fileContents,
			maxLineLength);

		return parsedJavaClass;
	}

	private static final Log _log = LogFactoryUtil.getLog(JavaParser.class);

	private static class ContentModifications {

		public void addInsertLineBreakLineNumber(int lineNumber) {
			_insertLineBreakLineNumbers.add(lineNumber);
		}

		public void addRemoveLineLineNumber(int lineNumber) {
			_removeLineLineNumbers.add(lineNumber);
		}

		public void addReplaceContent(String content, int lineNumber) {
			_replaceContentMap.put(lineNumber, new Tuple(content, lineNumber));
		}

		public void addReplaceContent(
			String content, int startLineNumber, int endLineNumber) {

			_replaceContentMap.put(
				startLineNumber, new Tuple(content, endLineNumber));
		}

		public Tuple getReplaceContentTuple(int lineNumber) {
			return _replaceContentMap.get(lineNumber);
		}

		public boolean isInsertLineLineNumber(int lineNumber) {
			return _insertLineBreakLineNumbers.contains(lineNumber);
		}

		public boolean isRemoveLineLineNumber(int lineNumber) {
			return _removeLineLineNumbers.contains(lineNumber);
		}

		private final List<Integer> _insertLineBreakLineNumbers =
			new ArrayList<>();
		private final List<Integer> _removeLineLineNumbers = new ArrayList<>();
		private final Map<Integer, Tuple> _replaceContentMap = new HashMap<>();

	}

}