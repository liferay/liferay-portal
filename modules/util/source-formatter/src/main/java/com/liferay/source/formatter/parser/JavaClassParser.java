/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.checkstyle.util.DetailASTUtil;

import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JavaClassParser {

	public static List<JavaClass> parseAnonymousClasses(
			String fileName, String content, String packageName,
			List<String> importNames, DetailAST detailAST,
			FileContents fileContents)
		throws IOException, ParseException {

		String absolutePath = SourceUtil.getAbsolutePath(fileName);

		File file = new File(absolutePath);

		FileText fileText = new FileText(
			file, CheckstyleUtil.getLines(content));

		if (detailAST == null) {
			try {
				detailAST = JavaParser.parseFileText(
					fileText, JavaParser.Options.WITH_COMMENTS);
			}
			catch (CheckstyleException checkstyleException) {
				throw new RuntimeException(checkstyleException);
			}
		}

		DetailAST siblingDetailAST = detailAST.getNextSibling();

		while ((siblingDetailAST != null) &&
			   (siblingDetailAST.getType() != TokenTypes.CLASS_DEF) &&
			   (siblingDetailAST.getType() != TokenTypes.ENUM_DEF) &&
			   (siblingDetailAST.getType() != TokenTypes.INTERFACE_DEF)) {

			siblingDetailAST = siblingDetailAST.getNextSibling();
		}

		if (siblingDetailAST == null) {
			return Collections.emptyList();
		}

		List<JavaClass> anonymousClasses = new ArrayList<>();

		List<DetailAST> leteralNewDetailASTs = DetailASTUtil.getAllChildTokens(
			siblingDetailAST, true, TokenTypes.LITERAL_NEW);

		JavaClass parentJavaClass = null;

		if (!leteralNewDetailASTs.isEmpty() &&
			(siblingDetailAST.getType() == TokenTypes.CLASS_DEF)) {

			parentJavaClass = parseJavaClass(content, detailAST, fileContents);
		}

		for (DetailAST leteralNewDetailAST : leteralNewDetailASTs) {
			DetailAST objBlockDetailAST = leteralNewDetailAST.findFirstToken(
				TokenTypes.OBJBLOCK);

			if (objBlockDetailAST == null) {
				continue;
			}

			String classContent = _getJavaTermContent(
				fileContents, leteralNewDetailAST);

			if (classContent == null) {
				throw new ParseException(
					"Parsing error at line \"" +
						leteralNewDetailAST.getLineNo() + "\"");
			}

			classContent = classContent.substring(0, classContent.length() - 1);

			JavaClass anonymousClass = _parseJavaClass(
				JavaTerm.ACCESS_MODIFIER_PRIVATE, true, classContent,
				leteralNewDetailAST.getLineNo(), StringPool.BLANK, importNames,
				false, false, false, false, false, false, packageName, false,
				fileContents, leteralNewDetailAST, parentJavaClass);

			anonymousClasses.add(anonymousClass);
		}

		return anonymousClasses;
	}

	public static JavaClass parseJavaClass(
			String content, DetailAST detailAST, FileContents fileContents)
		throws IOException, ParseException {

		DetailAST siblingDetailAST = detailAST.getNextSibling();

		while ((siblingDetailAST != null) &&
			   (siblingDetailAST.getType() != TokenTypes.ANNOTATION_DEF) &&
			   (siblingDetailAST.getType() != TokenTypes.CLASS_DEF) &&
			   (siblingDetailAST.getType() != TokenTypes.ENUM_DEF) &&
			   (siblingDetailAST.getType() != TokenTypes.INTERFACE_DEF) &&
			   (siblingDetailAST.getType() != TokenTypes.RECORD_DEF)) {

			siblingDetailAST = siblingDetailAST.getNextSibling();
		}

		if (siblingDetailAST == null) {
			throw new ParseException(
				"Parsing error at line \"" + detailAST.getLineNo() + "\"");
		}

		String accessModifier = JavaTerm.ACCESS_MODIFIER_DEFAULT;
		boolean isAbstract = false;
		boolean isFinal = false;

		boolean isInterface = false;

		if (siblingDetailAST.getType() == TokenTypes.INTERFACE_DEF) {
			isInterface = true;
		}

		boolean isStrictfp = false;
		boolean nonsealed = false;
		boolean sealed = false;

		DetailAST modifiersDetailAST = siblingDetailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (modifiersDetailAST != null) {
			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE)) {
				accessModifier = JavaTerm.ACCESS_MODIFIER_PRIVATE;
			}
			else if (modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_PROTECTED)) {

				accessModifier = JavaTerm.ACCESS_MODIFIER_PROTECTED;
			}
			else if (modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_PUBLIC)) {

				accessModifier = JavaTerm.ACCESS_MODIFIER_PUBLIC;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.ABSTRACT)) {
				isAbstract = true;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.FINAL)) {
				isFinal = true;
			}

			if (modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_NON_SEALED)) {

				nonsealed = true;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.STRICTFP)) {
				isStrictfp = true;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_SEALED)) {
				sealed = true;
			}
		}

		DetailAST nameDetailAST = siblingDetailAST.findFirstToken(
			TokenTypes.IDENT);

		String classContent = _getJavaTermContent(
			fileContents, siblingDetailAST);

		if (classContent == null) {
			throw new ParseException(
				"Parsing error at line \"" + siblingDetailAST.getLineNo() +
					"\"");
		}

		JavaClass javaClass = _parseJavaClass(
			accessModifier, false, classContent, siblingDetailAST.getLineNo(),
			nameDetailAST.getText(), JavaSourceUtil.getImportNames(content),
			isAbstract, isFinal, isInterface, false, isStrictfp, nonsealed,
			JavaSourceUtil.getPackageName(content), sealed, fileContents,
			siblingDetailAST, null);

		_parseExtendsImplementsPermits(javaClass, siblingDetailAST);

		return javaClass;
	}

	public static JavaClass parseJavaClass(String fileName, String content)
		throws IOException, ParseException {

		String absolutePath = SourceUtil.getAbsolutePath(fileName);

		File file = new File(absolutePath);

		FileText fileText = new FileText(
			file, CheckstyleUtil.getLines(content));

		DetailAST detailAST;

		try {
			detailAST = JavaParser.parseFileText(
				fileText, JavaParser.Options.WITH_COMMENTS);
		}
		catch (CheckstyleException checkstyleException) {
			throw new RuntimeException(checkstyleException);
		}

		return parseJavaClass(content, detailAST, new FileContents(fileText));
	}

	private static Position _getEndPosition(DetailAST detailAST) {
		if ((detailAST.getType() == TokenTypes.ANNOTATION_DEF) ||
			(detailAST.getType() == TokenTypes.CLASS_DEF) ||
			(detailAST.getType() == TokenTypes.ENUM_DEF) ||
			(detailAST.getType() == TokenTypes.INTERFACE_DEF) ||
			(detailAST.getType() == TokenTypes.LITERAL_NEW) ||
			(detailAST.getType() == TokenTypes.RECORD_DEF)) {

			DetailAST objBlockDetailAST = detailAST.findFirstToken(
				TokenTypes.OBJBLOCK);

			if (objBlockDetailAST == null) {
				return null;
			}

			DetailAST lastChildDetailAST = objBlockDetailAST.getLastChild();

			if ((lastChildDetailAST == null) ||
				(lastChildDetailAST.getType() != TokenTypes.RCURLY)) {

				return null;
			}

			return new Position(
				lastChildDetailAST.getColumnNo(),
				lastChildDetailAST.getLineNo());
		}

		if ((detailAST.getType() == TokenTypes.CTOR_DEF) ||
			(detailAST.getType() == TokenTypes.METHOD_DEF)) {

			DetailAST lastChildDetailAST = detailAST.getLastChild();

			if (lastChildDetailAST == null) {
				return null;
			}

			if (lastChildDetailAST.getType() == TokenTypes.SLIST) {
				lastChildDetailAST = lastChildDetailAST.getLastChild();

				if ((lastChildDetailAST == null) ||
					(lastChildDetailAST.getType() != TokenTypes.RCURLY)) {

					return null;
				}

				return new Position(
					lastChildDetailAST.getColumnNo(),
					lastChildDetailAST.getLineNo());
			}

			if (lastChildDetailAST.getType() == TokenTypes.SEMI) {
				return new Position(
					lastChildDetailAST.getColumnNo(),
					lastChildDetailAST.getLineNo());
			}

			return null;
		}

		if (detailAST.getType() == TokenTypes.STATIC_INIT) {
			DetailAST slistBlockDetailAST = detailAST.findFirstToken(
				TokenTypes.SLIST);

			if (slistBlockDetailAST == null) {
				return null;
			}

			DetailAST lastChildDetailAST = slistBlockDetailAST.getLastChild();

			if ((lastChildDetailAST == null) ||
				(lastChildDetailAST.getType() != TokenTypes.RCURLY)) {

				return null;
			}

			return new Position(
				lastChildDetailAST.getColumnNo(),
				lastChildDetailAST.getLineNo());
		}

		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			DetailAST lastChildDetailAST = detailAST.getLastChild();

			if ((lastChildDetailAST == null) ||
				(lastChildDetailAST.getType() != TokenTypes.SEMI)) {

				return null;
			}

			return new Position(
				lastChildDetailAST.getColumnNo(),
				lastChildDetailAST.getLineNo());
		}

		return null;
	}

	private static JavaTerm _getJavaTerm(
			String packageName, List<String> importNames,
			String javaTermContent, DetailAST detailAST,
			FileContents fileContents)
		throws IOException, ParseException {

		String accessModifier = JavaTerm.ACCESS_MODIFIER_DEFAULT;
		boolean isAbstract = false;
		boolean isFinal = false;

		boolean isInterface = false;

		if (detailAST.getType() == TokenTypes.INTERFACE_DEF) {
			isInterface = true;
		}

		boolean isStatic = false;
		boolean isStrictfp = false;
		boolean nonsealed = false;
		boolean sealed = false;

		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (modifiersDetailAST != null) {
			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE)) {
				accessModifier = JavaTerm.ACCESS_MODIFIER_PRIVATE;
			}
			else if (modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_PROTECTED)) {

				accessModifier = JavaTerm.ACCESS_MODIFIER_PROTECTED;
			}
			else if (modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_PUBLIC)) {

				accessModifier = JavaTerm.ACCESS_MODIFIER_PUBLIC;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.ABSTRACT)) {
				isAbstract = true;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.FINAL)) {
				isFinal = true;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_STATIC)) {
				isStatic = true;
			}

			if (modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_NON_SEALED)) {

				nonsealed = true;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.STRICTFP)) {
				isStrictfp = true;
			}

			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_SEALED)) {
				sealed = true;
			}
		}

		if ((detailAST.getType() == TokenTypes.ANNOTATION_DEF) ||
			(detailAST.getType() == TokenTypes.CLASS_DEF) ||
			(detailAST.getType() == TokenTypes.ENUM_DEF) ||
			(detailAST.getType() == TokenTypes.INTERFACE_DEF) ||
			(detailAST.getType() == TokenTypes.RECORD_DEF)) {

			DetailAST nameDetailAST = detailAST.findFirstToken(
				TokenTypes.IDENT);

			String className = nameDetailAST.getText();

			JavaClass javaClass = _parseJavaClass(
				accessModifier, false, javaTermContent, detailAST.getLineNo(),
				className, importNames, isAbstract, isFinal, isInterface,
				isStatic, isStrictfp, nonsealed, packageName, sealed,
				fileContents, detailAST, null);

			_parseExtendsImplementsPermits(javaClass, detailAST);

			return javaClass;
		}

		if (detailAST.getType() == TokenTypes.STATIC_INIT) {
			return new JavaStaticBlock(javaTermContent, detailAST.getLineNo());
		}

		String name = _getName(detailAST.findFirstToken(TokenTypes.IDENT));

		if (detailAST.getType() == TokenTypes.CTOR_DEF) {
			return new JavaConstructor(
				accessModifier, javaTermContent, isAbstract, isFinal, isStatic,
				detailAST.getLineNo(), name);
		}

		if (detailAST.getType() == TokenTypes.METHOD_DEF) {
			return new JavaMethod(
				accessModifier, javaTermContent, isAbstract, isFinal, isStatic,
				detailAST.getLineNo(), name);
		}

		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return new JavaVariable(
				accessModifier, javaTermContent, isAbstract, isFinal, isStatic,
				detailAST.getLineNo(), name);
		}

		return null;
	}

	private static String _getJavaTermContent(
		FileContents fileContents, DetailAST detailAST) {

		Position endPosition = _getEndPosition(detailAST);

		if (endPosition == null) {
			return null;
		}

		Position startPosition = new Position(
			detailAST.getColumnNo(), _getStartLineNumber(detailAST));

		String javaTermContent = _getJavaTermContent(
			fileContents, endPosition, startPosition);

		if (startPosition.getColumnNumber() != 0) {
			javaTermContent = javaTermContent + "\n";
		}

		return javaTermContent;
	}

	private static String _getJavaTermContent(
		FileContents fileContents, Position endPosition,
		Position startPosition) {

		int endLineNumber = endPosition.getLineNumber();
		int startLineNumber = startPosition.getLineNumber();

		if (endLineNumber == startLineNumber) {
			String line = fileContents.getLine(startLineNumber - 1);

			return line.substring(0, endPosition.getColumnNumber() + 1);
		}

		StringBundler sb = new StringBundler();

		sb.append(fileContents.getLine(startLineNumber - 1));
		sb.append("\n");

		for (int i = startLineNumber + 1; i <= (endLineNumber - 1); i++) {
			sb.append(fileContents.getLine(i - 1));
			sb.append("\n");
		}

		String line = fileContents.getLine(endLineNumber - 1);

		sb.append(line.substring(0, endPosition.getColumnNumber() + 1));

		return sb.toString();
	}

	private static String _getName(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(detailAST);

			return fullIdent.getText();
		}

		if (detailAST.getType() == TokenTypes.IDENT) {
			return detailAST.getText();
		}

		return null;
	}

	private static String[] _getNames(DetailAST detailAST) {
		List<String> names = new ArrayList<>();

		DetailAST childDetailAST = detailAST.getFirstChild();

		while (childDetailAST != null) {
			String name = _getName(childDetailAST);

			if (name != null) {
				names.add(name);
			}

			childDetailAST = childDetailAST.getNextSibling();
		}

		return names.toArray(new String[0]);
	}

	private static int _getStartLineNumber(DetailAST detailAST) {
		int startLineNumber = detailAST.getLineNo();

		List<DetailAST> childDetailASTs = DetailASTUtil.getAllChildTokens(
			detailAST, true, DetailASTUtil.ALL_TYPES);

		for (DetailAST childDetailAST : childDetailASTs) {
			if ((childDetailAST.getColumnNo() == detailAST.getColumnNo()) &&
				(childDetailAST.getLineNo() < startLineNumber)) {

				startLineNumber = childDetailAST.getLineNo();
			}
		}

		return startLineNumber;
	}

	private static void _parseExtendsImplementsPermits(
		JavaClass javaClass, DetailAST detailAST) {

		DetailAST extendsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST != null) {
			String[] extendedClassNames = _getNames(extendsClauseDetailAST);

			javaClass.addExtendedClassNames(extendedClassNames);
		}

		DetailAST implementsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.IMPLEMENTS_CLAUSE);

		if (implementsClauseDetailAST != null) {
			String[] implementedClassNames = _getNames(
				implementsClauseDetailAST);

			javaClass.addImplementedClassNames(implementedClassNames);
		}

		DetailAST permitsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.PERMITS_CLAUSE);

		if (permitsClauseDetailAST != null) {
			String[] permitsClassNames = _getNames(permitsClauseDetailAST);

			javaClass.addPermittedClassNames(permitsClassNames);
		}
	}

	private static JavaClass _parseJavaClass(
			String accessModifier, boolean anonymous, String classContent,
			int classLineNumber, String className, List<String> importNames,
			boolean isAbstract, boolean isFinal, boolean isInterface,
			boolean isStatic, boolean isStrictfp, boolean nonsealed,
			String packageName, boolean sealed, FileContents fileContents,
			DetailAST detailAST, JavaClass parentJavaClass)
		throws IOException, ParseException {

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		if (objBlockDetailAST == null) {
			return null;
		}

		JavaClass javaClass = new JavaClass(
			accessModifier, anonymous, classContent, importNames, isAbstract,
			isFinal, isInterface, isStatic, isStrictfp, classLineNumber,
			className, nonsealed, packageName, sealed);

		List<DetailAST> childDetailASTs = DetailASTUtil.getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.ANNOTATION_DEF,
			TokenTypes.CLASS_DEF, TokenTypes.CTOR_DEF, TokenTypes.ENUM_DEF,
			TokenTypes.INTERFACE_DEF, TokenTypes.METHOD_DEF,
			TokenTypes.RECORD_DEF, TokenTypes.STATIC_INIT,
			TokenTypes.VARIABLE_DEF);

		for (DetailAST childDetailAST : childDetailASTs) {
			String javaTermContent = _getJavaTermContent(
				fileContents, childDetailAST);

			if (javaTermContent == null) {
				throw new ParseException(
					"Parsing error at line \"" + childDetailAST.getLineNo() +
						"\"");
			}

			JavaTerm javaTerm = _getJavaTerm(
				packageName, importNames, javaTermContent, childDetailAST,
				fileContents);

			if (javaTerm == null) {
				throw new ParseException(
					"Parsing error at line \"" + childDetailAST.getLineNo() +
						"\"");
			}

			javaClass.addChildJavaTerm(javaTerm);
		}

		if (!anonymous) {
			return javaClass;
		}

		javaClass.setParentJavaClass(parentJavaClass);

		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			if (parentDetailAST == null) {
				return javaClass;
			}

			if (parentDetailAST.getType() == TokenTypes.METHOD_DEF) {
				break;
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		String javaTermContent = _getJavaTermContent(
			fileContents, parentDetailAST);

		if (javaTermContent == null) {
			throw new ParseException(
				"Parsing error at line \"" + detailAST.getLineNo() + "\"");
		}

		JavaTerm javaTerm = _getJavaTerm(
			packageName, importNames, javaTermContent, parentDetailAST,
			fileContents);

		if (javaTerm == null) {
			throw new ParseException(
				"Parsing error at line \"" + detailAST.getLineNo() + "\"");
		}

		JavaMethod javaMethod = (JavaMethod)javaTerm;

		javaClass.setParentJavaMethod(javaMethod);

		return javaClass;
	}

	private static class Position {

		public Position(int columnNumber, int lineNumber) {
			_columnNumber = columnNumber;
			_lineNumber = lineNumber;
		}

		public int getColumnNumber() {
			return _columnNumber;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		private final int _columnNumber;
		private final int _lineNumber;

	}

}