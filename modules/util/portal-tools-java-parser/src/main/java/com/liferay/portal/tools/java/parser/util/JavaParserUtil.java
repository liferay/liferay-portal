/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.java.parser.JavaAnnotation;
import com.liferay.portal.tools.java.parser.JavaAnnotationFieldDefinition;
import com.liferay.portal.tools.java.parser.JavaAnnotationMemberValuePair;
import com.liferay.portal.tools.java.parser.JavaArray;
import com.liferay.portal.tools.java.parser.JavaArrayDeclarator;
import com.liferay.portal.tools.java.parser.JavaArrayElement;
import com.liferay.portal.tools.java.parser.JavaBreakStatement;
import com.liferay.portal.tools.java.parser.JavaCatchStatement;
import com.liferay.portal.tools.java.parser.JavaClassCall;
import com.liferay.portal.tools.java.parser.JavaClassDefinition;
import com.liferay.portal.tools.java.parser.JavaConstructorCall;
import com.liferay.portal.tools.java.parser.JavaConstructorDefinition;
import com.liferay.portal.tools.java.parser.JavaContinueStatement;
import com.liferay.portal.tools.java.parser.JavaDoStatement;
import com.liferay.portal.tools.java.parser.JavaElseStatement;
import com.liferay.portal.tools.java.parser.JavaEnhancedForStatement;
import com.liferay.portal.tools.java.parser.JavaEnumConstantDefinition;
import com.liferay.portal.tools.java.parser.JavaEnumConstantDefinitions;
import com.liferay.portal.tools.java.parser.JavaExpression;
import com.liferay.portal.tools.java.parser.JavaFinallyStatement;
import com.liferay.portal.tools.java.parser.JavaForStatement;
import com.liferay.portal.tools.java.parser.JavaIfStatement;
import com.liferay.portal.tools.java.parser.JavaImport;
import com.liferay.portal.tools.java.parser.JavaInstanceInitialization;
import com.liferay.portal.tools.java.parser.JavaInstanceofStatement;
import com.liferay.portal.tools.java.parser.JavaLambdaExpression;
import com.liferay.portal.tools.java.parser.JavaLambdaParameter;
import com.liferay.portal.tools.java.parser.JavaLoopStatement;
import com.liferay.portal.tools.java.parser.JavaMethodCall;
import com.liferay.portal.tools.java.parser.JavaMethodDefinition;
import com.liferay.portal.tools.java.parser.JavaMethodReference;
import com.liferay.portal.tools.java.parser.JavaNewArrayInstantiation;
import com.liferay.portal.tools.java.parser.JavaNewClassInstantiation;
import com.liferay.portal.tools.java.parser.JavaOperator;
import com.liferay.portal.tools.java.parser.JavaOperatorExpression;
import com.liferay.portal.tools.java.parser.JavaPackageDefinition;
import com.liferay.portal.tools.java.parser.JavaParameter;
import com.liferay.portal.tools.java.parser.JavaRecordComponent;
import com.liferay.portal.tools.java.parser.JavaReturnStatement;
import com.liferay.portal.tools.java.parser.JavaSignature;
import com.liferay.portal.tools.java.parser.JavaSimpleValue;
import com.liferay.portal.tools.java.parser.JavaStaticInitialization;
import com.liferay.portal.tools.java.parser.JavaSwitchCaseStatement;
import com.liferay.portal.tools.java.parser.JavaSwitchExpression;
import com.liferay.portal.tools.java.parser.JavaSwitchRuleStatement;
import com.liferay.portal.tools.java.parser.JavaSwitchStatement;
import com.liferay.portal.tools.java.parser.JavaSynchronizedStatement;
import com.liferay.portal.tools.java.parser.JavaTerm;
import com.liferay.portal.tools.java.parser.JavaTernaryOperator;
import com.liferay.portal.tools.java.parser.JavaTextBlock;
import com.liferay.portal.tools.java.parser.JavaThrowStatement;
import com.liferay.portal.tools.java.parser.JavaTryStatement;
import com.liferay.portal.tools.java.parser.JavaType;
import com.liferay.portal.tools.java.parser.JavaTypeCast;
import com.liferay.portal.tools.java.parser.JavaVariableDefinition;
import com.liferay.portal.tools.java.parser.JavaWhileStatement;
import com.liferay.portal.tools.java.parser.Position;
import com.liferay.portal.tools.java.parser.util.comparator.ModifierComparator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class JavaParserUtil {

	public static final int NO_MAX_LINE_LENGTH = -1;

	public static String getLastLine(String s) {
		int x = s.lastIndexOf("\n");

		if (x != -1) {
			return s.substring(x + 1);
		}

		return s;
	}

	public static JavaTerm parseJavaTerm(DetailAST detailAST) {
		JavaTerm javaTerm = null;

		if ((detailAST.getType() == TokenTypes.ANNOTATION_DEF) ||
			(detailAST.getType() == TokenTypes.CLASS_DEF) ||
			(detailAST.getType() == TokenTypes.ENUM_DEF) ||
			(detailAST.getType() == TokenTypes.INTERFACE_DEF) ||
			(detailAST.getType() == TokenTypes.RECORD_DEF)) {

			javaTerm = _parseJavaClassDefinition(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.CASE_GROUP) {
			javaTerm = _parseJavaSwitchCaseStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.ANNOTATION_FIELD_DEF) {
			javaTerm = _parseJavaAnnotationFieldDefinition(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.COMPACT_CTOR_DEF) {
			javaTerm = _parseJavaConstructorDefinition(detailAST, true);
		}
		else if ((detailAST.getType() == TokenTypes.CTOR_CALL) ||
				 (detailAST.getType() == TokenTypes.SUPER_CTOR_CALL)) {

			javaTerm = _parseJavaConstructorCall(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.CTOR_DEF) {
			javaTerm = _parseJavaConstructorDefinition(detailAST, false);
		}
		else if (detailAST.getType() == TokenTypes.DO_WHILE) {
			javaTerm = _parseJavaWhileStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.ENUM_CONSTANT_DEF) {
			DetailAST previousSiblingDetailAST = detailAST.getPreviousSibling();

			if (previousSiblingDetailAST.getType() == TokenTypes.LCURLY) {
				javaTerm = _parseJavaEnumConstantDefinitions(detailAST);
			}
		}
		else if (detailAST.getType() == TokenTypes.EXPR) {
			javaTerm = _parseJavaExpression(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.IMPORT) {
			javaTerm = _parseJavaImport(detailAST, false);
		}
		else if (detailAST.getType() == TokenTypes.INSTANCE_INIT) {
			javaTerm = new JavaInstanceInitialization();
		}
		else if (detailAST.getType() == TokenTypes.LABELED_STAT) {
			javaTerm = _parseJavaLabeledStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_BREAK) {
			javaTerm = _parseJavaBreakStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_CATCH) {
			javaTerm = _parseJavaCatchStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_CONTINUE) {
			javaTerm = _parseJavaContinueStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_DO) {
			javaTerm = new JavaDoStatement();
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_ELSE) {
			javaTerm = _parseJavaElseStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_FINALLY) {
			javaTerm = new JavaFinallyStatement();
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_FOR) {
			javaTerm = _parseJavaForStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_IF) {
			javaTerm = _parseJavaIfStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_RETURN) {
			javaTerm = _parseJavaReturnStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_SYNCHRONIZED) {
			javaTerm = _parseJavaSynchronizedStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_SWITCH) {
			DetailAST switchRuleDetailAST = detailAST.findFirstToken(
				TokenTypes.SWITCH_RULE);

			if (switchRuleDetailAST != null) {
				javaTerm = _parseJavaExpression(detailAST);
			}
			else {
				javaTerm = _parseJavaSwitchStatement(detailAST);
			}
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_THROW) {
			javaTerm = _parseJavaThrowStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_TRY) {
			javaTerm = _parseJavaTryStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_WHILE) {
			javaTerm = _parseJavaWhileStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.METHOD_DEF) {
			javaTerm = _parseJavaMethodDefinition(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.PACKAGE_DEF) {
			javaTerm = _parseJavaPackageDefinition(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.STATIC_IMPORT) {
			javaTerm = _parseJavaImport(detailAST, true);
		}
		else if (detailAST.getType() == TokenTypes.STATIC_INIT) {
			javaTerm = new JavaStaticInitialization();
		}
		else if (detailAST.getType() == TokenTypes.SWITCH_RULE) {
			javaTerm = _parseJavaSwitchRuleStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			javaTerm = _parseJavaVariableDefinition(detailAST);
		}

		if (javaTerm != null) {
			javaTerm.setSuffix(_getSuffix(detailAST));
		}

		return javaTerm;
	}

	private static int _getArrayDimension(DetailAST detailAST) {
		int arrayDimension = 0;

		DetailAST childDetailAST = detailAST.getFirstChild();

		while (childDetailAST.getType() == TokenTypes.ARRAY_DECLARATOR) {
			arrayDimension++;

			childDetailAST = childDetailAST.getFirstChild();
		}

		// Checkstyle parses the following two types as identical DetailASTs:
		// 'Map<Long, List<String>[]>' and 'Map<Long, List<String>>[]'. The
		// following logic is to 'correct' misplaced array declarators.

		if (arrayDimension > 0) {
			DetailAST parentDetailAST = detailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.TYPE_ARGUMENT) {
				parentDetailAST = parentDetailAST.getParent();
			}

			if ((parentDetailAST.getType() == TokenTypes.TYPE_ARGUMENTS) &&
				_isMisplacedArrayDeclarator(
					parentDetailAST.getLastChild(),
					detailAST.getFirstChild())) {

				return 0;
			}

			return arrayDimension;
		}

		DetailAST typeInfoDetailAST = detailAST;

		if (childDetailAST.getType() == TokenTypes.DOT) {
			typeInfoDetailAST = childDetailAST;
		}

		DetailAST typeArgumentsDetailAST = typeInfoDetailAST.findFirstToken(
			TokenTypes.TYPE_ARGUMENTS);

		if (typeArgumentsDetailAST == null) {
			return arrayDimension;
		}

		List<DetailAST> arrayDeclaratorDetailASTs =
			DetailASTUtil.getAllChildTokens(
				typeInfoDetailAST, true, TokenTypes.ARRAY_DECLARATOR);

		for (DetailAST arrayDeclaratorDetailAST : arrayDeclaratorDetailASTs) {
			if (_isMisplacedArrayDeclarator(
					typeArgumentsDetailAST.getLastChild(),
					arrayDeclaratorDetailAST)) {

				arrayDimension++;
			}
		}

		return arrayDimension;
	}

	private static Tuple _getChainTuple(DetailAST dotDetailAST) {
		String name = StringPool.BLANK;

		DetailAST detailAST = dotDetailAST;

		while (true) {
			if (detailAST.getType() == TokenTypes.DOT) {
				DetailAST lastChildDetailAST = detailAST.getLastChild();

				if (Validator.isNull(name)) {
					name = lastChildDetailAST.getText();
				}
				else {
					name = lastChildDetailAST.getText() + "." + name;
				}

				detailAST = detailAST.getFirstChild();

				continue;
			}

			JavaExpression javaExpression = null;

			if (ArrayUtil.contains(_SIMPLE_TYPES, detailAST.getType()) &&
				(detailAST.getFirstChild() == null)) {

				name = detailAST.getText() + "." + name;
			}
			else {
				javaExpression = _parseJavaExpression(detailAST);
			}

			return new Tuple(name, javaExpression);
		}
	}

	private static String _getName(DetailAST detailAST) {
		DetailAST identDetailAST = detailAST.findFirstToken(TokenTypes.IDENT);

		if (identDetailAST != null) {
			return identDetailAST.getText();
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (ArrayUtil.contains(_SIMPLE_TYPES, firstChildDetailAST.getType())) {
			return firstChildDetailAST.getText();
		}

		DetailAST dotDetailAST = detailAST.findFirstToken(TokenTypes.DOT);

		FullIdent fullIdent = FullIdent.createFullIdent(dotDetailAST);

		return fullIdent.getText();
	}

	private static String _getSuffix(DetailAST detailAST) {
		DetailAST closingDetailAST = DetailASTUtil.getClosingDetailAST(
			detailAST);

		if (closingDetailAST == null) {
			return StringPool.BLANK;
		}

		if ((closingDetailAST.getType() == TokenTypes.LCURLY) ||
			(closingDetailAST.getType() == TokenTypes.SLIST)) {

			return " {";
		}

		return closingDetailAST.getText();
	}

	private static boolean _isMisplacedArrayDeclarator(
		DetailAST genericEndDetailAST, DetailAST arrayDeclaratorDetailAST) {

		Position genericEndPosition = new Position(
			genericEndDetailAST.getLineNo(), genericEndDetailAST.getColumnNo());

		Position arrayDeclaratorPosition = new Position(
			arrayDeclaratorDetailAST.getLineNo(),
			arrayDeclaratorDetailAST.getColumnNo());

		if (arrayDeclaratorPosition.compareTo(genericEndPosition) > 0) {
			return true;
		}

		return false;
	}

	private static List<JavaExpression> _parseArrayValueJavaExpressions(
		DetailAST detailAST) {

		int bracketType = detailAST.getType();

		List<JavaExpression> arrayValueJavaExpressions = new ArrayList<>();

		DetailAST firstChildDetailAST = detailAST;

		while (true) {
			if (firstChildDetailAST.getType() != bracketType) {
				if (arrayValueJavaExpressions.size() > 1) {
					Collections.reverse(arrayValueJavaExpressions);
				}

				return arrayValueJavaExpressions;
			}

			DetailAST closeBracketDetailAST =
				firstChildDetailAST.findFirstToken(TokenTypes.RBRACK);

			DetailAST previousSiblingDetailAST =
				closeBracketDetailAST.getPreviousSibling();

			if ((previousSiblingDetailAST == null) ||
				(previousSiblingDetailAST.getType() == bracketType)) {

				arrayValueJavaExpressions.add(
					new JavaSimpleValue(StringPool.BLANK));
			}
			else {
				arrayValueJavaExpressions.add(
					_parseJavaExpression(previousSiblingDetailAST));
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();
		}
	}

	private static List<JavaType> _parseExceptionJavaTypes(
		DetailAST throwsDetailAST) {

		List<JavaType> exceptionJavaTypes = new ArrayList<>();

		if (throwsDetailAST == null) {
			return exceptionJavaTypes;
		}

		DetailAST childDetailAST = throwsDetailAST.getFirstChild();

		while (true) {
			if (childDetailAST == null) {
				return exceptionJavaTypes;
			}

			if (childDetailAST.getType() != TokenTypes.COMMA) {
				FullIdent fullIdent = FullIdent.createFullIdent(childDetailAST);

				exceptionJavaTypes.add(new JavaType(0, fullIdent.getText()));
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private static List<JavaType>
		_parseExtendedOrImplementedOrPermittedClassJavaTypes(
			DetailAST clauseDetailAST) {

		List<JavaType> classJavaTypes = new ArrayList<>();

		DetailAST childDetailAST = clauseDetailAST.getFirstChild();

		while (true) {
			if (childDetailAST == null) {
				return classJavaTypes;
			}

			if (childDetailAST.getType() == TokenTypes.IDENT) {
				JavaType javaType = new JavaType(0, childDetailAST.getText());

				DetailAST nextSiblingDetailAST =
					childDetailAST.getNextSibling();

				if ((nextSiblingDetailAST != null) &&
					(nextSiblingDetailAST.getType() ==
						TokenTypes.TYPE_ARGUMENTS)) {

					javaType.setGenericJavaTypes(
						_parseGenericJavaTypes(
							nextSiblingDetailAST, TokenTypes.TYPE_ARGUMENT));
				}

				classJavaTypes.add(javaType);
			}
			else if (childDetailAST.getType() == TokenTypes.DOT) {
				FullIdent fullIdent = FullIdent.createFullIdent(childDetailAST);

				JavaType javaType = new JavaType(0, fullIdent.getText());

				DetailAST typeArgumentsDetailAST =
					childDetailAST.findFirstToken(TokenTypes.TYPE_ARGUMENTS);

				if (typeArgumentsDetailAST != null) {
					javaType.setGenericJavaTypes(
						_parseGenericJavaTypes(
							typeArgumentsDetailAST, TokenTypes.TYPE_ARGUMENT));
				}

				classJavaTypes.add(javaType);
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private static JavaType _parseGenericBoundJavaType(
		DetailAST detailAST, int arrayDimension) {

		FullIdent fullIdent = FullIdent.createFullIdent(detailAST);

		JavaType genericBoundJavaType = new JavaType(
			arrayDimension, fullIdent.getText());

		DetailAST typeArgumentsDetailAST = null;

		if (detailAST.getType() != TokenTypes.DOT) {
			typeArgumentsDetailAST = detailAST.getNextSibling();
		}
		else {
			typeArgumentsDetailAST = detailAST.getLastChild();
		}

		if ((typeArgumentsDetailAST != null) &&
			(typeArgumentsDetailAST.getType() == TokenTypes.TYPE_ARGUMENTS)) {

			genericBoundJavaType.setGenericJavaTypes(
				_parseGenericJavaTypes(
					typeArgumentsDetailAST, TokenTypes.TYPE_ARGUMENT));
		}

		return genericBoundJavaType;
	}

	private static List<JavaType> _parseGenericBoundJavaTypes(
		DetailAST detailAST, int genericBoundType) {

		List<DetailAST> typeGenericBoundsDetailASTs =
			DetailASTUtil.getAllChildTokens(detailAST, true, genericBoundType);

		int arrayDimension = 0;
		DetailAST typeGenericBoundsDetailAST = null;

		outerLoop:
		for (DetailAST curTypeGenericBoundsDetailAST :
				typeGenericBoundsDetailASTs) {

			DetailAST parentDetailAST =
				curTypeGenericBoundsDetailAST.getParent();

			while (true) {
				if (DetailASTUtil.equals(detailAST, parentDetailAST)) {
					typeGenericBoundsDetailAST = curTypeGenericBoundsDetailAST;

					break outerLoop;
				}

				if (parentDetailAST.getType() != TokenTypes.ARRAY_DECLARATOR) {
					continue outerLoop;
				}

				arrayDimension++;

				parentDetailAST = parentDetailAST.getParent();
			}
		}

		if (typeGenericBoundsDetailAST == null) {
			return null;
		}

		List<JavaType> genericBoundJavaTypes = new ArrayList<>();

		DetailAST childDetailAST = typeGenericBoundsDetailAST.getFirstChild();

		while (true) {
			if (childDetailAST == null) {
				return genericBoundJavaTypes;
			}

			if ((childDetailAST.getType() != TokenTypes.TYPE_ARGUMENTS) &&
				(childDetailAST.getType() != TokenTypes.TYPE_EXTENSION_AND)) {

				genericBoundJavaTypes.add(
					_parseGenericBoundJavaType(childDetailAST, arrayDimension));
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private static List<JavaType> _parseGenericJavaTypes(
		DetailAST groupDetailAST, int type) {

		if (groupDetailAST == null) {
			return null;
		}

		List<JavaType> genericJavaTypes = new ArrayList<>();

		List<DetailAST> detailASTs = DetailASTUtil.getAllChildTokens(
			groupDetailAST, false, type);

		for (DetailAST currentDetailAST : detailASTs) {
			DetailAST childDetailAST = currentDetailAST.getFirstChild();

			if (childDetailAST.getType() == TokenTypes.TYPE) {
				genericJavaTypes.add(_parseJavaType(childDetailAST));
			}
			else {
				genericJavaTypes.add(_parseJavaType(currentDetailAST));
			}
		}

		return genericJavaTypes;
	}

	private static JavaAnnotation _parseJavaAnnotation(
		DetailAST annotationDetailAST) {

		JavaAnnotation javaAnnotation = new JavaAnnotation(
			_getName(annotationDetailAST));

		DetailAST lparenDetailAST = annotationDetailAST.findFirstToken(
			TokenTypes.LPAREN);

		if (lparenDetailAST == null) {
			return javaAnnotation;
		}

		List<JavaAnnotationMemberValuePair> javaAnnotationMemberValuePairs =
			_parseJavaAnnotationMemberValuePairs(annotationDetailAST);

		if (!javaAnnotationMemberValuePairs.isEmpty()) {
			Collections.sort(javaAnnotationMemberValuePairs);

			javaAnnotation.setJavaAnnotationMemberValuePairs(
				javaAnnotationMemberValuePairs);
		}
		else {
			DetailAST nextSiblingDetailAST = lparenDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.RPAREN) {
				javaAnnotation.setValueJavaExpression(
					_parseJavaExpression(lparenDetailAST.getNextSibling()));
			}
		}

		return javaAnnotation;
	}

	private static JavaAnnotationFieldDefinition
		_parseJavaAnnotationFieldDefinition(
			DetailAST annotationFieldDefinitionDetailAST) {

		JavaAnnotationFieldDefinition javaAnnotationFieldDefinition =
			new JavaAnnotationFieldDefinition(
				_parseJavaAnnotations(
					annotationFieldDefinitionDetailAST.findFirstToken(
						TokenTypes.MODIFIERS)),
				_parseJavaSignature(annotationFieldDefinitionDetailAST, false));

		DetailAST literalDefaultDetailAST =
			annotationFieldDefinitionDetailAST.findFirstToken(
				TokenTypes.LITERAL_DEFAULT);

		if (literalDefaultDetailAST != null) {
			javaAnnotationFieldDefinition.setDefaultJavaExpression(
				_parseJavaExpression(literalDefaultDetailAST.getFirstChild()));
		}

		return javaAnnotationFieldDefinition;
	}

	private static JavaAnnotationMemberValuePair
		_parseJavaAnnotationMemberValuePair(
			DetailAST annotationMemberValuePairDetailAST) {

		DetailAST identDetailAST =
			annotationMemberValuePairDetailAST.findFirstToken(TokenTypes.IDENT);

		return new JavaAnnotationMemberValuePair(
			identDetailAST.getText(),
			_parseJavaExpression(
				annotationMemberValuePairDetailAST.getLastChild()));
	}

	private static List<JavaAnnotationMemberValuePair>
		_parseJavaAnnotationMemberValuePairs(DetailAST annotationDetailAST) {

		List<JavaAnnotationMemberValuePair> javaAnnotationMemberValuePairs =
			new ArrayList<>();

		List<DetailAST> annotationMemberValuePairDetailASTs =
			DetailASTUtil.getAllChildTokens(
				annotationDetailAST, false,
				TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR);

		for (DetailAST annotationMemberValuePairDetailAST :
				annotationMemberValuePairDetailASTs) {

			javaAnnotationMemberValuePairs.add(
				_parseJavaAnnotationMemberValuePair(
					annotationMemberValuePairDetailAST));
		}

		return javaAnnotationMemberValuePairs;
	}

	private static List<JavaAnnotation> _parseJavaAnnotations(
		DetailAST detailAST) {

		List<JavaAnnotation> javaAnnotations = new ArrayList<>();

		List<DetailAST> annotationDetailASTs = DetailASTUtil.getAllChildTokens(
			detailAST, false, TokenTypes.ANNOTATION);

		for (DetailAST annotationDetailAST : annotationDetailASTs) {
			javaAnnotations.add(_parseJavaAnnotation(annotationDetailAST));
		}

		Collections.sort(javaAnnotations);

		return javaAnnotations;
	}

	private static JavaArray _parseJavaArray(DetailAST arrayDetailAST) {
		JavaArray javaArray = new JavaArray();

		DetailAST childDetailAST = arrayDetailAST.getFirstChild();

		while (true) {
			if ((childDetailAST == null) ||
				(childDetailAST.getType() == TokenTypes.RCURLY)) {

				return javaArray;
			}

			javaArray.addValueJavaExpression(
				_parseJavaExpression(childDetailAST));

			childDetailAST = childDetailAST.getNextSibling();
			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private static JavaArrayDeclarator _parseJavaArrayDeclarator(
		DetailAST arrayDeclaratorDetailAST) {

		List<JavaExpression> dimensionValueJavaExpressions = new ArrayList<>();

		dimensionValueJavaExpressions.add(
			new JavaSimpleValue(StringPool.BLANK));

		DetailAST childDetailAST = arrayDeclaratorDetailAST.getFirstChild();

		while (true) {
			if (childDetailAST.getType() != TokenTypes.ARRAY_DECLARATOR) {
				FullIdent fullIdent = FullIdent.createFullIdent(childDetailAST);

				return new JavaArrayDeclarator(
					fullIdent.getText(), dimensionValueJavaExpressions);
			}

			dimensionValueJavaExpressions.add(
				new JavaSimpleValue(StringPool.BLANK));

			childDetailAST = childDetailAST.getFirstChild();
		}
	}

	private static JavaArrayElement _parseJavaArrayElement(
		DetailAST indexOpDetailAST) {

		JavaExpression arrayJavaExpression = null;

		DetailAST firstChildDetailAST = indexOpDetailAST.getFirstChild();

		while (true) {
			if (firstChildDetailAST.getType() != TokenTypes.INDEX_OP) {
				arrayJavaExpression = _parseJavaExpression(firstChildDetailAST);

				break;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();
		}

		return new JavaArrayElement(
			arrayJavaExpression,
			_parseArrayValueJavaExpressions(indexOpDetailAST));
	}

	private static JavaBreakStatement _parseJavaBreakStatement(
		DetailAST literalBreakDetailAST) {

		JavaBreakStatement javaBreakStatement = new JavaBreakStatement();

		DetailAST firstChildDetailAST = literalBreakDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			javaBreakStatement.setIdentifierName(firstChildDetailAST.getText());
		}

		return javaBreakStatement;
	}

	private static JavaCatchStatement _parseJavaCatchStatement(
		DetailAST literalCatchDetailAST) {

		DetailAST parameterDefinitionDetailAST =
			literalCatchDetailAST.findFirstToken(TokenTypes.PARAMETER_DEF);

		List<JavaSimpleValue> modifiers = _parseModifiers(
			parameterDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS));

		DetailAST identDetailAST = parameterDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		String parameterName = identDetailAST.getText();

		List<JavaType> parameterJavaTypes = new ArrayList<>();

		DetailAST typeDetailAST = parameterDefinitionDetailAST.findFirstToken(
			TokenTypes.TYPE);

		DetailAST childDetailAST = typeDetailAST.getFirstChild();

		while (true) {
			DetailAST nextSiblingDetailAST = childDetailAST.getNextSibling();

			if (nextSiblingDetailAST != null) {
				FullIdent fullIdent = FullIdent.createFullIdent(
					nextSiblingDetailAST);

				parameterJavaTypes.add(new JavaType(0, fullIdent.getText()));
			}

			if (childDetailAST.getType() != TokenTypes.BOR) {
				FullIdent fullIdent = FullIdent.createFullIdent(childDetailAST);

				parameterJavaTypes.add(new JavaType(0, fullIdent.getText()));

				break;
			}

			childDetailAST = childDetailAST.getFirstChild();
		}

		if (parameterJavaTypes.size() > 1) {
			Collections.sort(parameterJavaTypes);
		}

		return new JavaCatchStatement(
			modifiers, parameterName, parameterJavaTypes);
	}

	private static JavaClassCall _parseJavaClassCall(
		DetailAST literalNewDetailAST) {

		DetailAST typeArgumentDetailAST = literalNewDetailAST.findFirstToken(
			TokenTypes.TYPE_ARGUMENTS);

		if (typeArgumentDetailAST == null) {
			DetailAST firstChildDetailAST = literalNewDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.DOT) {
				typeArgumentDetailAST = firstChildDetailAST.findFirstToken(
					TokenTypes.TYPE_ARGUMENTS);
			}
		}

		JavaClassCall javaClassCall = new JavaClassCall(
			_getName(literalNewDetailAST),
			_parseGenericJavaTypes(
				typeArgumentDetailAST, TokenTypes.TYPE_ARGUMENT),
			_parseParameterValueJavaExpressions(
				literalNewDetailAST.findFirstToken(TokenTypes.ELIST)));

		javaClassCall.setStatementCondition(
			DetailASTUtil.hasParentWithTokenType(
				literalNewDetailAST, TokenTypes.LITERAL_FOR,
				TokenTypes.LITERAL_IF, TokenTypes.LITERAL_WHILE));

		DetailAST objBlockDetailAST = literalNewDetailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		if (objBlockDetailAST != null) {
			javaClassCall.setHasBody(true);

			if (objBlockDetailAST.getChildCount() == 2) {
				javaClassCall.setEmptyBody(true);
			}
		}

		return javaClassCall;
	}

	private static JavaClassDefinition _parseJavaClassDefinition(
		DetailAST definitionDetailAST) {

		DetailAST modifiersDetailAST = definitionDetailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		String type = StringPool.BLANK;

		DetailAST nextSiblingDetailAST = modifiersDetailAST.getNextSibling();

		while (nextSiblingDetailAST.getType() != TokenTypes.IDENT) {
			type += nextSiblingDetailAST.getText();

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();
		}

		JavaType classJavaType = new JavaType(0, _getName(definitionDetailAST));

		DetailAST typeParametersDetailAST = definitionDetailAST.findFirstToken(
			TokenTypes.TYPE_PARAMETERS);

		if (typeParametersDetailAST != null) {
			classJavaType.setGenericJavaTypes(
				_parseGenericJavaTypes(
					typeParametersDetailAST, TokenTypes.TYPE_PARAMETER));
		}

		JavaClassDefinition javaClassDefinition = new JavaClassDefinition(
			classJavaType, _parseJavaAnnotations(modifiersDetailAST),
			_parseModifiers(modifiersDetailAST), type);

		DetailAST extendsClauseDetailAST = definitionDetailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST != null) {
			List<JavaType> extendedClassJavaTypes =
				_parseExtendedOrImplementedOrPermittedClassJavaTypes(
					extendsClauseDetailAST);

			if ((extendedClassJavaTypes.size() > 1) &&
				((definitionDetailAST.getParent() == null) ||
				 !AnnotationUtil.containsAnnotation(definitionDetailAST))) {

				Collections.sort(extendedClassJavaTypes);
			}

			javaClassDefinition.setExtendedClassJavaTypes(
				extendedClassJavaTypes);
		}

		DetailAST implementsClauseDetailAST =
			definitionDetailAST.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);

		if (implementsClauseDetailAST != null) {
			List<JavaType> implementedClassJavaTypes =
				_parseExtendedOrImplementedOrPermittedClassJavaTypes(
					implementsClauseDetailAST);

			if ((implementedClassJavaTypes.size() > 1) &&
				((definitionDetailAST.getParent() == null) ||
				 !AnnotationUtil.containsAnnotation(definitionDetailAST))) {

				Collections.sort(implementedClassJavaTypes);
			}

			javaClassDefinition.setImplementedClassJavaTypes(
				implementedClassJavaTypes);
		}

		DetailAST permitsClauseDetailAST = definitionDetailAST.findFirstToken(
			TokenTypes.PERMITS_CLAUSE);

		if (permitsClauseDetailAST != null) {
			List<JavaType> permittedClassJavaTypes =
				_parseExtendedOrImplementedOrPermittedClassJavaTypes(
					permitsClauseDetailAST);

			if ((permittedClassJavaTypes.size() > 1) &&
				((definitionDetailAST.getParent() == null) ||
				 !AnnotationUtil.containsAnnotation(definitionDetailAST))) {

				Collections.sort(permittedClassJavaTypes);
			}

			javaClassDefinition.setPermittedClassJavaTypes(
				permittedClassJavaTypes);
		}

		DetailAST recordComponentsDetailAST =
			definitionDetailAST.findFirstToken(TokenTypes.RECORD_COMPONENTS);

		if (recordComponentsDetailAST != null) {
			javaClassDefinition.setJavaRecordComponent(
				_parseRecordComponents(definitionDetailAST));
		}

		return javaClassDefinition;
	}

	private static JavaConstructorCall _parseJavaConstructorCall(
		DetailAST detailAST) {

		boolean superCall = false;

		if (detailAST.getType() == TokenTypes.SUPER_CTOR_CALL) {
			superCall = true;
		}

		return new JavaConstructorCall(
			_parseParameterValueJavaExpressions(
				detailAST.findFirstToken(TokenTypes.ELIST)),
			superCall);
	}

	private static JavaConstructorDefinition _parseJavaConstructorDefinition(
		DetailAST constructorDefinitionDetailAST,
		boolean compactRecordConstructor) {

		return new JavaConstructorDefinition(
			_parseJavaAnnotations(
				constructorDefinitionDetailAST.findFirstToken(
					TokenTypes.MODIFIERS)),
			_parseJavaSignature(
				constructorDefinitionDetailAST, compactRecordConstructor));
	}

	private static JavaContinueStatement _parseJavaContinueStatement(
		DetailAST literalContinueDetailAST) {

		JavaContinueStatement javaContinueStatement =
			new JavaContinueStatement();

		DetailAST firstChildDetailAST =
			literalContinueDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			javaContinueStatement.setIdentifierName(
				firstChildDetailAST.getText());
		}

		return javaContinueStatement;
	}

	private static JavaElseStatement _parseJavaElseStatement(
		DetailAST literalElseDetailAST) {

		JavaElseStatement javaElseStatement = new JavaElseStatement();

		DetailAST firstChildDetailAST = literalElseDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_IF) {
			javaElseStatement.setJavaIfStatement(
				_parseJavaIfStatement(firstChildDetailAST));
		}

		return javaElseStatement;
	}

	private static JavaEnhancedForStatement _parseJavaEnhancedForStatement(
		DetailAST forEachClauseDetailAST) {

		return new JavaEnhancedForStatement(
			_parseJavaExpression(
				forEachClauseDetailAST.findFirstToken(TokenTypes.EXPR)),
			_parseJavaVariableDefinition(
				forEachClauseDetailAST.findFirstToken(
					TokenTypes.VARIABLE_DEF)));
	}

	private static JavaEnumConstantDefinition _parseJavaEnumConstantDefinition(
		DetailAST enumConstantDefinitionDetailAST) {

		JavaEnumConstantDefinition javaEnumConstantDefinition =
			new JavaEnumConstantDefinition(
				_getName(enumConstantDefinitionDetailAST),
				_parseJavaAnnotations(
					enumConstantDefinitionDetailAST.findFirstToken(
						TokenTypes.ANNOTATIONS)));

		DetailAST elistDetailAST =
			enumConstantDefinitionDetailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST != null) {
			javaEnumConstantDefinition.setParameterValueJavaExpressions(
				_parseParameterValueJavaExpressions(elistDetailAST));
		}

		DetailAST objBlockDetailAST =
			enumConstantDefinitionDetailAST.findFirstToken(TokenTypes.OBJBLOCK);

		if (objBlockDetailAST != null) {
			javaEnumConstantDefinition.setHasBody(true);
		}

		return javaEnumConstantDefinition;
	}

	private static JavaEnumConstantDefinitions
		_parseJavaEnumConstantDefinitions(
			DetailAST enumConstantDefinitionDetailAST) {

		JavaEnumConstantDefinitions javaEnumConstantDefinitions =
			new JavaEnumConstantDefinitions();

		DetailAST detailAST = enumConstantDefinitionDetailAST;

		while (true) {
			if (detailAST == null) {
				return javaEnumConstantDefinitions;
			}

			if (detailAST.getType() == TokenTypes.ENUM_CONSTANT_DEF) {
				javaEnumConstantDefinitions.addJavaEnumConstantDefinition(
					_parseJavaEnumConstantDefinition(detailAST));
			}
			else if (detailAST.getType() != TokenTypes.COMMA) {
				return javaEnumConstantDefinitions;
			}

			detailAST = detailAST.getNextSibling();
		}
	}

	private static JavaExpression _parseJavaExpression(DetailAST detailAST) {
		return _parseJavaExpression(detailAST, false);
	}

	private static JavaExpression _parseJavaExpression(
		DetailAST detailAST, boolean checkSurroundingParentheses) {

		if (detailAST.getType() == TokenTypes.EXPR) {
			detailAST = detailAST.getFirstChild();
		}

		boolean hasSurroundingParentheses = false;

		while (true) {
			if (detailAST.getType() == TokenTypes.LPAREN) {
				detailAST = detailAST.getNextSibling();

				hasSurroundingParentheses = true;
			}
			else if (detailAST.getType() == TokenTypes.RPAREN) {
				detailAST = detailAST.getPreviousSibling();

				hasSurroundingParentheses = true;
			}
			else {
				break;
			}
		}

		JavaExpression javaExpression = null;

		if (detailAST.getType() == TokenTypes.ANNOTATION) {
			javaExpression = _parseJavaAnnotation(detailAST);
		}
		else if ((detailAST.getType() == TokenTypes.ANNOTATION_ARRAY_INIT) ||
				 (detailAST.getType() == TokenTypes.ARRAY_INIT)) {

			javaExpression = _parseJavaArray(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.ARRAY_DECLARATOR) {
			javaExpression = _parseJavaArrayDeclarator(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.DOT) {
			DetailAST lastChildDetailAST = detailAST.getLastChild();

			if (lastChildDetailAST.getChildCount() > 0) {
				DetailAST firstChildDetailAST = detailAST.getFirstChild();

				javaExpression = new JavaSimpleValue(
					firstChildDetailAST.getText());

				javaExpression.setChainedJavaExpression(
					_parseJavaExpression(lastChildDetailAST));
			}
			else {
				Tuple chainTuple = _getChainTuple(detailAST);

				javaExpression = (JavaExpression)chainTuple.getObject(1);

				if (javaExpression != null) {
					javaExpression.setChainedJavaExpression(
						new JavaSimpleValue((String)chainTuple.getObject(0)));
				}
				else {
					javaExpression = new JavaSimpleValue(
						(String)chainTuple.getObject(0));
				}
			}
		}
		else if (detailAST.getType() == TokenTypes.INDEX_OP) {
			javaExpression = _parseJavaArrayElement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LAMBDA) {
			javaExpression = _parseJavaLambdaExpression(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_INSTANCEOF) {
			javaExpression = _parseJavaInstanceofStatement(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_NEW) {
			DetailAST arrayDeclaratorDetailAST = detailAST.findFirstToken(
				TokenTypes.ARRAY_DECLARATOR);

			if (arrayDeclaratorDetailAST != null) {
				javaExpression = _parseJavaNewArrayInstantiation(detailAST);
			}
			else {
				DetailAST elistDetailAST = detailAST.findFirstToken(
					TokenTypes.ELIST);

				if (elistDetailAST != null) {
					javaExpression = _parseJavaNewClassInstantiation(detailAST);
				}
			}
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_SWITCH) {
			DetailAST switchRuleDetailAST = detailAST.findFirstToken(
				TokenTypes.SWITCH_RULE);

			if (switchRuleDetailAST != null) {
				javaExpression = _parseJavaSwitchExpression(detailAST);
			}
		}
		else if (detailAST.getType() == TokenTypes.METHOD_CALL) {
			return _parseJavaMethodCall(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.METHOD_REF) {
			javaExpression = _parseJavaMethodReference(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.QUESTION) {
			javaExpression = _parseJavaTernaryOperator(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.TEXT_BLOCK_LITERAL_BEGIN) {
			javaExpression = _parseJavaTextBlock(detailAST.getFirstChild());
		}
		else if (detailAST.getType() == TokenTypes.TYPECAST) {
			javaExpression = _parseJavaTypeCast(detailAST);
		}
		else if (ArrayUtil.contains(_SIMPLE_TYPES, detailAST.getType())) {
			javaExpression = new JavaSimpleValue(detailAST.getText());
		}
		else {
			for (JavaOperator operator : JavaOperator.values()) {
				if (operator.getType() == detailAST.getType()) {
					javaExpression = _parseJavaOperatorExpression(
						detailAST, operator);

					if (checkSurroundingParentheses) {
						javaExpression.setHasSurroundingParentheses(
							hasSurroundingParentheses);
					}

					break;
				}
			}
		}

		if (javaExpression != null) {
			javaExpression.setSurroundingParentheses();
		}

		return javaExpression;
	}

	private static JavaLoopStatement _parseJavaForStatement(
		DetailAST literalForDetailAST) {

		DetailAST firstChildDetailAST = literalForDetailAST.getFirstChild();

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() == TokenTypes.FOR_EACH_CLAUSE) {
			return _parseJavaEnhancedForStatement(nextSiblingDetailAST);
		}

		JavaForStatement javaForStatement = new JavaForStatement();

		List<JavaTerm> initializationJavaTerms = new ArrayList<>();

		DetailAST forInitDetailAST = literalForDetailAST.findFirstToken(
			TokenTypes.FOR_INIT);

		firstChildDetailAST = forInitDetailAST.getFirstChild();

		if (firstChildDetailAST != null) {
			if (firstChildDetailAST.getType() == TokenTypes.ELIST) {
				List<DetailAST> exprDetailASTs =
					DetailASTUtil.getAllChildTokens(
						firstChildDetailAST, false, TokenTypes.EXPR);

				for (DetailAST exprDetailAST : exprDetailASTs) {
					initializationJavaTerms.add(
						_parseJavaExpression(exprDetailAST));
				}
			}
			else if (firstChildDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
				initializationJavaTerms.add(
					_parseJavaVariableDefinition(firstChildDetailAST));
			}
		}

		javaForStatement.setInitializationJavaTerms(initializationJavaTerms);

		DetailAST forConditionDetailAST = literalForDetailAST.findFirstToken(
			TokenTypes.FOR_CONDITION);

		DetailAST exprDetailAST = forConditionDetailAST.findFirstToken(
			TokenTypes.EXPR);

		if (exprDetailAST != null) {
			javaForStatement.setConditionJavaExpression(
				_parseJavaExpression(exprDetailAST));
		}

		DetailAST forIteratorDetailAST = literalForDetailAST.findFirstToken(
			TokenTypes.FOR_ITERATOR);

		List<JavaExpression> iteratorJavaExpressions = new ArrayList<>();

		DetailAST elistDetailAST = forIteratorDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if (elistDetailAST != null) {
			List<DetailAST> exprDetailASTs = DetailASTUtil.getAllChildTokens(
				elistDetailAST, false, TokenTypes.EXPR);

			for (DetailAST curExprDetailAST : exprDetailASTs) {
				iteratorJavaExpressions.add(
					_parseJavaExpression(curExprDetailAST));
			}
		}

		javaForStatement.setIteratorJavaExpression(iteratorJavaExpressions);

		return javaForStatement;
	}

	private static JavaIfStatement _parseJavaIfStatement(
		DetailAST literalIfDetailAST) {

		DetailAST firstChildDetailAST = literalIfDetailAST.getFirstChild();

		return new JavaIfStatement(
			_parseJavaExpression(firstChildDetailAST.getNextSibling()));
	}

	private static JavaImport _parseJavaImport(
		DetailAST importDetailAST, boolean isStatic) {

		return new JavaImport(isStatic, _getName(importDetailAST));
	}

	private static JavaInstanceofStatement _parseJavaInstanceofStatement(
		DetailAST literalInstanceofDetailAST) {

		JavaExpression javaExpression = _parseJavaExpression(
			literalInstanceofDetailAST.getFirstChild());

		DetailAST typeDetailAST = literalInstanceofDetailAST.findFirstToken(
			TokenTypes.TYPE);

		if (typeDetailAST != null) {
			return new JavaInstanceofStatement(
				_parseJavaType(typeDetailAST), null, javaExpression);
		}

		return new JavaInstanceofStatement(
			null,
			_parseJavaVariableDefinition(
				literalInstanceofDetailAST.findFirstToken(
					TokenTypes.PATTERN_VARIABLE_DEF)),
			javaExpression);
	}

	private static JavaLoopStatement _parseJavaLabeledStatement(
		DetailAST labeledStatementDetailAST) {

		JavaLoopStatement javaLoopStatement = null;

		DetailAST firstChildDetailAST =
			labeledStatementDetailAST.getFirstChild();

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() == TokenTypes.LITERAL_FOR) {
			javaLoopStatement = _parseJavaForStatement(nextSiblingDetailAST);
		}
		else if (nextSiblingDetailAST.getType() == TokenTypes.LITERAL_WHILE) {
			javaLoopStatement = _parseJavaWhileStatement(nextSiblingDetailAST);
		}

		if (javaLoopStatement != null) {
			javaLoopStatement.setLabelName(firstChildDetailAST.getText());
		}

		return javaLoopStatement;
	}

	private static JavaExpression _parseJavaLambdaExpression(
		DetailAST lambdaDetailAST) {

		JavaLambdaExpression javaLambdaExpression = null;

		DetailAST firstChildDetailAST = lambdaDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			javaLambdaExpression = new JavaLambdaExpression(
				firstChildDetailAST.getText());
		}
		else {
			javaLambdaExpression = new JavaLambdaExpression(
				_parseJavaLambdaParameters(
					lambdaDetailAST.findFirstToken(TokenTypes.PARAMETERS)));
		}

		DetailAST lastChildDetailAST = lambdaDetailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.SLIST) {
			javaLambdaExpression.setLambdaActionJavaExpression(
				_parseJavaExpression(lastChildDetailAST));
		}

		return javaLambdaExpression;
	}

	private static List<JavaLambdaParameter> _parseJavaLambdaParameters(
		DetailAST parametersDetailAST) {

		List<JavaLambdaParameter> javaLambdaParameters = new ArrayList<>();

		List<DetailAST> parameterDefinitionDetailASTs =
			DetailASTUtil.getAllChildTokens(
				parametersDetailAST, false, TokenTypes.PARAMETER_DEF);

		for (DetailAST parameterDefinitionDetailAST :
				parameterDefinitionDetailASTs) {

			JavaLambdaParameter javaLambdaParameter = new JavaLambdaParameter(
				_getName(parameterDefinitionDetailAST));

			DetailAST typeDetailAST =
				parameterDefinitionDetailAST.findFirstToken(TokenTypes.TYPE);

			if (typeDetailAST.getFirstChild() != null) {
				javaLambdaParameter.setJavaType(_parseJavaType(typeDetailAST));
			}

			javaLambdaParameters.add(javaLambdaParameter);
		}

		return javaLambdaParameters;
	}

	private static JavaExpression _parseJavaMethodCall(
		DetailAST methodCallDetailAST) {

		JavaExpression javaExpression = null;
		JavaMethodCall javaMethodCall = null;

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			javaMethodCall = new JavaMethodCall(firstChildDetailAST.getText());

			javaMethodCall.setGenericJavaTypes(
				_parseGenericJavaTypes(
					methodCallDetailAST.findFirstToken(
						TokenTypes.TYPE_ARGUMENTS),
					TokenTypes.TYPE_ARGUMENT));
		}
		else {
			Tuple chainTuple = _getChainTuple(firstChildDetailAST);

			String name = (String)chainTuple.getObject(0);

			javaExpression = (JavaExpression)chainTuple.getObject(1);

			if (javaExpression != null) {
				javaMethodCall = new JavaMethodCall(name);
			}
			else {
				int i = name.lastIndexOf(StringPool.PERIOD);

				if (i == -1) {
					javaMethodCall = new JavaMethodCall(name);
				}
				else {
					javaMethodCall = new JavaMethodCall(name.substring(i + 1));

					javaExpression = new JavaSimpleValue(name.substring(0, i));
				}
			}

			javaMethodCall.setGenericJavaTypes(
				_parseGenericJavaTypes(
					firstChildDetailAST.findFirstToken(
						TokenTypes.TYPE_ARGUMENTS),
					TokenTypes.TYPE_ARGUMENT));
		}

		javaMethodCall.setParameterValueJavaExpressions(
			_parseParameterValueJavaExpressions(
				methodCallDetailAST.findFirstToken(TokenTypes.ELIST)));
		javaMethodCall.setInsideConstructorCall(
			DetailASTUtil.hasParentWithTokenType(
				methodCallDetailAST, TokenTypes.CTOR_CALL,
				TokenTypes.SUPER_CTOR_CALL));

		if (javaExpression == null) {
			javaMethodCall.setMethodCallWithinClass(true);

			return javaMethodCall;
		}

		javaExpression.setChainedJavaExpression(javaMethodCall);

		return javaExpression;
	}

	private static JavaMethodDefinition _parseJavaMethodDefinition(
		DetailAST methodDefinitionDetailAST) {

		return new JavaMethodDefinition(
			_parseJavaAnnotations(
				methodDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS)),
			_parseJavaSignature(methodDefinitionDetailAST, false));
	}

	private static JavaMethodReference _parseJavaMethodReference(
		DetailAST methodReferenceDetailAST) {

		DetailAST lastChildDetailAST = methodReferenceDetailAST.getLastChild();

		JavaExpression referenceJavaExpression = _parseJavaExpression(
			methodReferenceDetailAST.getFirstChild(), true);

		if (referenceJavaExpression instanceof JavaTypeCast) {
			referenceJavaExpression.setHasSurroundingParentheses(true);
		}

		return new JavaMethodReference(
			_parseGenericJavaTypes(
				methodReferenceDetailAST.findFirstToken(
					TokenTypes.TYPE_ARGUMENTS),
				TokenTypes.TYPE_ARGUMENT),
			lastChildDetailAST.getText(), referenceJavaExpression);
	}

	private static JavaNewArrayInstantiation _parseJavaNewArrayInstantiation(
		DetailAST literalNewDetailAST) {

		JavaNewArrayInstantiation javaNewArrayInstantiation =
			new JavaNewArrayInstantiation();

		JavaArrayDeclarator javaArrayDeclarator = new JavaArrayDeclarator(
			_getName(literalNewDetailAST),
			_parseArrayValueJavaExpressions(
				literalNewDetailAST.findFirstToken(
					TokenTypes.ARRAY_DECLARATOR)));

		javaArrayDeclarator.setGenericJavaTypes(
			_parseGenericJavaTypes(
				literalNewDetailAST.findFirstToken(TokenTypes.TYPE_ARGUMENTS),
				TokenTypes.TYPE_ARGUMENT));

		javaNewArrayInstantiation.setJavaArrayDeclarator(javaArrayDeclarator);

		DetailAST arrayInitDetailAST = literalNewDetailAST.findFirstToken(
			TokenTypes.ARRAY_INIT);

		if (arrayInitDetailAST != null) {
			javaNewArrayInstantiation.setInitialJavaArray(
				_parseJavaArray(arrayInitDetailAST));
		}

		return javaNewArrayInstantiation;
	}

	private static JavaNewClassInstantiation _parseJavaNewClassInstantiation(
		DetailAST literalNewDetailAST) {

		return new JavaNewClassInstantiation(
			_parseJavaClassCall(literalNewDetailAST));
	}

	private static JavaOperatorExpression _parseJavaOperatorExpression(
		DetailAST detailAST, JavaOperator javaOperator) {

		JavaOperatorExpression javaOperatorExpression =
			new JavaOperatorExpression(javaOperator);

		if (javaOperator.hasLeftHandExpression()) {
			javaOperatorExpression.setLeftHandJavaExpression(
				_parseJavaExpression(detailAST.getFirstChild(), true));
		}

		if (javaOperator.hasRightHandExpression()) {
			javaOperatorExpression.setRightHandJavaExpression(
				_parseJavaExpression(detailAST.getLastChild(), true));
		}

		return javaOperatorExpression;
	}

	private static JavaPackageDefinition _parseJavaPackageDefinition(
		DetailAST packageDefinitionDetailAST) {

		return new JavaPackageDefinition(
			_parseJavaAnnotations(
				packageDefinitionDetailAST.findFirstToken(
					TokenTypes.ANNOTATIONS)),
			_getName(packageDefinitionDetailAST));
	}

	private static JavaParameter _parseJavaParameter(
		DetailAST parameterDefinitionDetailAST) {

		DetailAST modifiersDetailAST =
			parameterDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

		DetailAST typeDetailAST = parameterDefinitionDetailAST.findFirstToken(
			TokenTypes.TYPE);

		JavaType javaType = _parseJavaType(typeDetailAST);

		DetailAST ellipsisDetailAST =
			parameterDefinitionDetailAST.findFirstToken(TokenTypes.ELLIPSIS);

		if (ellipsisDetailAST != null) {
			javaType.setVarargs(true);
		}

		return new JavaParameter(
			javaType, _parseModifiers(modifiersDetailAST),
			_getName(parameterDefinitionDetailAST));
	}

	private static List<JavaParameter> _parseJavaParameters(
		DetailAST detailAST) {

		List<JavaParameter> javaParameters = new ArrayList<>();

		if (detailAST == null) {
			return javaParameters;
		}

		List<DetailAST> parameterDefinitionDetailASTs =
			DetailASTUtil.getAllChildTokens(
				detailAST, false, TokenTypes.PARAMETER_DEF);

		for (DetailAST parameterDefinitionDetailAST :
				parameterDefinitionDetailASTs) {

			javaParameters.add(
				_parseJavaParameter(parameterDefinitionDetailAST));
		}

		return javaParameters;
	}

	private static JavaReturnStatement _parseJavaReturnStatement(
		DetailAST literalReturnDetailAST) {

		JavaReturnStatement javaReturnStatement = new JavaReturnStatement();

		DetailAST firstChildDetailAST = literalReturnDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.SEMI) {
			javaReturnStatement.setReturnJavaExpression(
				_parseJavaExpression(firstChildDetailAST));
		}

		return javaReturnStatement;
	}

	private static JavaSignature _parseJavaSignature(
		DetailAST detailAST, boolean compactRecordConstructor) {

		DetailAST identDetailAST = detailAST.findFirstToken(TokenTypes.IDENT);
		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		List<JavaType> exceptionJavaTypes = _parseExceptionJavaTypes(
			detailAST.findFirstToken(TokenTypes.LITERAL_THROWS));

		if (exceptionJavaTypes.size() > 1) {
			Collections.sort(exceptionJavaTypes);
		}

		return new JavaSignature(
			compactRecordConstructor, exceptionJavaTypes,
			_parseGenericJavaTypes(
				detailAST.findFirstToken(TokenTypes.TYPE_PARAMETERS),
				TokenTypes.TYPE_PARAMETER),
			_parseJavaParameters(
				detailAST.findFirstToken(TokenTypes.PARAMETERS)),
			_parseModifiers(modifiersDetailAST), identDetailAST.getText(),
			_parseJavaType(detailAST.findFirstToken(TokenTypes.TYPE)));
	}

	private static JavaSwitchCaseStatement _parseJavaSwitchCaseStatement(
		DetailAST caseGroupDetailAST) {

		JavaSwitchCaseStatement javaSwitchCaseStatement =
			new JavaSwitchCaseStatement();

		DetailAST literalDefaultDetailAST = caseGroupDetailAST.findFirstToken(
			TokenTypes.LITERAL_DEFAULT);

		if (literalDefaultDetailAST != null) {
			javaSwitchCaseStatement.addDefault();
		}

		List<DetailAST> literalCaseDetailASTs = DetailASTUtil.getAllChildTokens(
			caseGroupDetailAST, false, TokenTypes.LITERAL_CASE);

		for (DetailAST literalCaseDetailAST : literalCaseDetailASTs) {
			javaSwitchCaseStatement.addSwitchCaseJavaExpression(
				_parseJavaExpression(literalCaseDetailAST.getFirstChild()));
		}

		return javaSwitchCaseStatement;
	}

	private static JavaExpression _parseJavaSwitchExpression(
		DetailAST detailAST) {

		DetailAST lparenDetailAST = detailAST.getFirstChild();

		return new JavaSwitchExpression(
			_parseJavaExpression(lparenDetailAST.getNextSibling()));
	}

	private static JavaSwitchRuleStatement _parseJavaSwitchRuleStatement(
		DetailAST switchRuleDetailAST) {

		JavaSwitchRuleStatement javaSwitchRuleStatement =
			new JavaSwitchRuleStatement();

		DetailAST firstChildDetailAST = switchRuleDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_DEFAULT) {
			javaSwitchRuleStatement.setDefault(true);
		}
		else {
			List<DetailAST> exprCaseDetailASTs =
				DetailASTUtil.getAllChildTokens(
					firstChildDetailAST, false, TokenTypes.EXPR);

			for (DetailAST exprCaseDetailAST : exprCaseDetailASTs) {
				javaSwitchRuleStatement.addSwitchRuleJavaExpression(
					_parseJavaExpression(exprCaseDetailAST));
			}
		}

		DetailAST lambdaDetailAST = switchRuleDetailAST.findFirstToken(
			TokenTypes.LAMBDA);

		DetailAST nextSiblingDetailAST = lambdaDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() == TokenTypes.SLIST) {
			return javaSwitchRuleStatement;
		}

		if (nextSiblingDetailAST.getType() == TokenTypes.EXPR) {
			javaSwitchRuleStatement.setLambdaActionJavaExpression(
				_parseJavaExpression(nextSiblingDetailAST));
		}
		else {
			javaSwitchRuleStatement.setLambdaActionJavaTerm(
				parseJavaTerm(nextSiblingDetailAST));
		}

		return javaSwitchRuleStatement;
	}

	private static JavaSwitchStatement _parseJavaSwitchStatement(
		DetailAST literalSwitchDetailAST) {

		DetailAST lparenDetailAST = literalSwitchDetailAST.getFirstChild();

		return new JavaSwitchStatement(
			_parseJavaExpression(lparenDetailAST.getNextSibling()));
	}

	private static JavaSynchronizedStatement _parseJavaSynchronizedStatement(
		DetailAST literalSynchronizedDetailAST) {

		JavaSynchronizedStatement javaSynchronizedStatement =
			new JavaSynchronizedStatement();

		DetailAST firstChildDetailAST =
			literalSynchronizedDetailAST.getFirstChild();

		javaSynchronizedStatement.setSynchronizedJavaExpression(
			_parseJavaExpression(firstChildDetailAST.getNextSibling()));

		return javaSynchronizedStatement;
	}

	private static JavaTernaryOperator _parseJavaTernaryOperator(
		DetailAST questionDetailAST) {

		DetailAST colonDetailAST = questionDetailAST.findFirstToken(
			TokenTypes.COLON);

		JavaExpression conditionJavaExpression = _parseJavaExpression(
			questionDetailAST.getFirstChild(), true);

		if (conditionJavaExpression instanceof JavaTernaryOperator) {
			conditionJavaExpression.setHasSurroundingParentheses(true);
		}

		JavaExpression falseValueJavaExpression = _parseJavaExpression(
			colonDetailAST.getNextSibling(), true);

		if (falseValueJavaExpression instanceof JavaTernaryOperator) {
			falseValueJavaExpression.setHasSurroundingParentheses(true);
		}

		JavaExpression trueValueJavaExpression = _parseJavaExpression(
			colonDetailAST.getPreviousSibling(), true);

		if (trueValueJavaExpression instanceof JavaTernaryOperator) {
			trueValueJavaExpression.setHasSurroundingParentheses(true);
		}

		return new JavaTernaryOperator(
			conditionJavaExpression, falseValueJavaExpression,
			trueValueJavaExpression);
	}

	private static JavaTextBlock _parseJavaTextBlock(
		DetailAST textBlockContentDetailAST) {

		return new JavaTextBlock(textBlockContentDetailAST.getText());
	}

	private static JavaThrowStatement _parseJavaThrowStatement(
		DetailAST literalThrowDetailAST) {

		return new JavaThrowStatement(
			_parseJavaExpression(literalThrowDetailAST.getFirstChild()));
	}

	private static JavaTryStatement _parseJavaTryStatement(
		DetailAST literalTryDetailAST) {

		JavaTryStatement javaTryStatement = new JavaTryStatement();

		DetailAST firstChildDetailAST = literalTryDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() !=
				TokenTypes.RESOURCE_SPECIFICATION) {

			return javaTryStatement;
		}

		List<JavaVariableDefinition> resourceJavaVariableDefinitions =
			new ArrayList<>();

		DetailAST resourcesDetailAST = firstChildDetailAST.findFirstToken(
			TokenTypes.RESOURCES);

		List<DetailAST> resourceDetailASTs = DetailASTUtil.getAllChildTokens(
			resourcesDetailAST, false, TokenTypes.RESOURCE);

		for (DetailAST resourceDetailAST : resourceDetailASTs) {
			resourceJavaVariableDefinitions.add(
				_parseJavaVariableDefinition(resourceDetailAST));
		}

		javaTryStatement.setResourceJavaVariableDefinitions(
			resourceJavaVariableDefinitions);

		return javaTryStatement;
	}

	private static JavaType _parseJavaType(DetailAST detailAST) {
		if (detailAST == null) {
			return null;
		}

		List<JavaAnnotation> javaAnnotations = new ArrayList<>();

		if (detailAST.getType() == TokenTypes.TYPE) {
			DetailAST parentDetailAST = detailAST.getParent();
			DetailAST previousSiblingDetailAST = detailAST.getPreviousSibling();

			if ((parentDetailAST.getType() == TokenTypes.PARAMETER_DEF) &&
				(previousSiblingDetailAST != null) &&
				(previousSiblingDetailAST.getType() == TokenTypes.MODIFIERS)) {

				javaAnnotations = _parseJavaAnnotations(
					previousSiblingDetailAST);
			}
		}

		DetailAST childDetailAST = detailAST.getFirstChild();

		if (childDetailAST.getType() == TokenTypes.ANNOTATIONS) {
			javaAnnotations = _parseJavaAnnotations(childDetailAST);

			childDetailAST = childDetailAST.getNextSibling();
		}

		int arrayDimension = _getArrayDimension(detailAST);

		while (childDetailAST.getType() == TokenTypes.ARRAY_DECLARATOR) {
			childDetailAST = childDetailAST.getFirstChild();
		}

		FullIdent typeFullIdent = FullIdent.createFullIdent(childDetailAST);

		JavaType javaType = new JavaType(
			arrayDimension, javaAnnotations, typeFullIdent.getText());

		DetailAST typeInfoDetailAST = childDetailAST;

		if (childDetailAST.getType() != TokenTypes.DOT) {
			typeInfoDetailAST = childDetailAST.getParent();
		}

		javaType.setGenericJavaTypes(
			_parseGenericJavaTypes(
				typeInfoDetailAST.findFirstToken(TokenTypes.TYPE_ARGUMENTS),
				TokenTypes.TYPE_ARGUMENT));
		javaType.setLowerBoundJavaTypes(
			_parseGenericBoundJavaTypes(
				typeInfoDetailAST, TokenTypes.TYPE_LOWER_BOUNDS));
		javaType.setUpperBoundJavaTypes(
			_parseGenericBoundJavaTypes(
				typeInfoDetailAST, TokenTypes.TYPE_UPPER_BOUNDS));

		return javaType;
	}

	private static JavaTypeCast _parseJavaTypeCast(
		DetailAST typeCastDetailAST) {

		List<JavaType> javaTypes = new ArrayList<>();

		DetailAST childDetailAST = typeCastDetailAST.getFirstChild();

		javaTypes.add(_parseJavaType(childDetailAST));

		while (true) {
			childDetailAST = childDetailAST.getNextSibling();

			if (childDetailAST.getType() != TokenTypes.TYPE_EXTENSION_AND) {
				return new JavaTypeCast(
					javaTypes,
					_parseJavaExpression(
						typeCastDetailAST.getLastChild(), true));
			}

			childDetailAST = childDetailAST.getNextSibling();

			javaTypes.add(_parseJavaType(childDetailAST));
		}
	}

	private static JavaVariableDefinition _parseJavaVariableDefinition(
		DetailAST detailAST) {

		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		JavaVariableDefinition javaVariableDefinition =
			new JavaVariableDefinition(
				_parseJavaAnnotations(modifiersDetailAST),
				_parseModifiers(modifiersDetailAST));

		javaVariableDefinition.setJavaType(
			_parseJavaType(detailAST.findFirstToken(TokenTypes.TYPE)));

		while (true) {
			String name = _getName(detailAST);

			DetailAST assignDetailAST = detailAST.findFirstToken(
				TokenTypes.ASSIGN);

			if (assignDetailAST == null) {
				javaVariableDefinition.addVariable(name);
			}
			else {
				javaVariableDefinition.addVariable(
					name,
					_parseJavaExpression(assignDetailAST.getFirstChild()));
			}

			detailAST = detailAST.getNextSibling();

			if ((detailAST == null) ||
				(detailAST.getType() != TokenTypes.COMMA)) {

				return javaVariableDefinition;
			}

			detailAST = detailAST.getNextSibling();
		}
	}

	private static JavaWhileStatement _parseJavaWhileStatement(
		DetailAST detailAST) {

		DetailAST lparenDetailAST = null;

		if (detailAST.getType() == TokenTypes.LITERAL_WHILE) {
			lparenDetailAST = detailAST.getFirstChild();
		}
		else {
			lparenDetailAST = detailAST.getNextSibling();
		}

		return new JavaWhileStatement(
			_parseJavaExpression(lparenDetailAST.getNextSibling()));
	}

	private static List<JavaSimpleValue> _parseModifiers(
		DetailAST modifiersDetailAST) {

		List<JavaSimpleValue> modifiers = new ArrayList<>();

		DetailAST childDetailAST = modifiersDetailAST.getFirstChild();

		while (true) {
			if (childDetailAST == null) {
				Collections.sort(modifiers, new ModifierComparator());

				return modifiers;
			}

			if ((childDetailAST.getType() != TokenTypes.ANNOTATION) &&
				(childDetailAST.getType() != TokenTypes.STRICTFP)) {

				modifiers.add(new JavaSimpleValue(childDetailAST.getText()));
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private static List<JavaExpression> _parseParameterValueJavaExpressions(
		DetailAST elistDetailAST) {

		List<JavaExpression> parameterValueJavaExpressions = new ArrayList<>();

		DetailAST childDetailAST = elistDetailAST.getFirstChild();

		if (childDetailAST == null) {
			return parameterValueJavaExpressions;
		}

		while (true) {
			parameterValueJavaExpressions.add(
				_parseJavaExpression(childDetailAST));

			childDetailAST = childDetailAST.getNextSibling();

			if (childDetailAST == null) {
				return parameterValueJavaExpressions;
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private static List<JavaRecordComponent> _parseRecordComponents(
		DetailAST detailAST) {

		List<JavaRecordComponent> javaRecordComponents = new ArrayList<>();

		DetailAST recordComponentsDetailAST = detailAST.findFirstToken(
			TokenTypes.RECORD_COMPONENTS);

		if (recordComponentsDetailAST == null) {
			return javaRecordComponents;
		}

		List<DetailAST> recordComponentDefinitionDetailASTs =
			DetailASTUtil.getAllChildTokens(
				recordComponentsDetailAST, false,
				TokenTypes.RECORD_COMPONENT_DEF);

		for (DetailAST recordComponentDefinitionDetailAST :
				recordComponentDefinitionDetailASTs) {

			DetailAST typeDetailAST =
				recordComponentDefinitionDetailAST.findFirstToken(
					TokenTypes.TYPE);

			JavaType javaType = _parseJavaType(typeDetailAST);

			DetailAST ellipsisDetailAST =
				recordComponentDefinitionDetailAST.findFirstToken(
					TokenTypes.ELLIPSIS);

			if (ellipsisDetailAST != null) {
				javaType.setVarargs(true);
			}

			JavaRecordComponent javaRecordComponent = new JavaRecordComponent(
				javaType, _getName(recordComponentDefinitionDetailAST));

			javaRecordComponents.add(javaRecordComponent);
		}

		return javaRecordComponents;
	}

	private static final int[] _SIMPLE_TYPES = {
		TokenTypes.CHAR_LITERAL, TokenTypes.IDENT, TokenTypes.LITERAL_BOOLEAN,
		TokenTypes.LITERAL_BYTE, TokenTypes.LITERAL_CHAR,
		TokenTypes.LITERAL_CLASS, TokenTypes.LITERAL_DOUBLE,
		TokenTypes.LITERAL_FALSE, TokenTypes.LITERAL_FLOAT,
		TokenTypes.LITERAL_INT, TokenTypes.LITERAL_LONG,
		TokenTypes.LITERAL_NULL, TokenTypes.LITERAL_SHORT,
		TokenTypes.LITERAL_SUPER, TokenTypes.LITERAL_TRUE,
		TokenTypes.LITERAL_THIS, TokenTypes.LITERAL_VOID, TokenTypes.NUM_DOUBLE,
		TokenTypes.NUM_FLOAT, TokenTypes.NUM_INT, TokenTypes.NUM_LONG,
		TokenTypes.STRING_LITERAL
	};

}