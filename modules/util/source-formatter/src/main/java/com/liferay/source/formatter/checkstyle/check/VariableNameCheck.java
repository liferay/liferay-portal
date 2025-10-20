/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class VariableNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.PARAMETER_DEF, TokenTypes.RESOURCE,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if ((detailAST.findFirstToken(TokenTypes.ELLIPSIS) != null) ||
			AnnotationUtil.containsAnnotation(detailAST, "Deprecated")) {

			return;
		}

		String name = _getVariableName(detailAST);

		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			_checkClassNameVariable(detailAST, name);
			_checkTypo(detailAST, name);
		}

		_checkIsVariableName(detailAST, name);

		DetailAST typeDetailAST = detailAST.findFirstToken(TokenTypes.TYPE);

		DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return;
		}

		String typeName = getTypeName(typeDetailAST, false);

		if (!typeName.contains("[")) {
			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				_checkCountVariableName(detailAST, name, typeName);
				_checkInstanceVariableName(detailAST, name, typeName);
				_checkTypeName(detailAST, name, typeName);
				_checkTypo(detailAST, name, typeName, true);
			}

			_checkExceptionVariableName(detailAST, name, typeName);
		}

		_checkVariableNameByMethodCall(detailAST, name);
	}

	protected String getExpectedVariableName(String typeName) {
		if (StringUtil.isUpperCase(typeName) || typeName.matches("[A-Z]+s")) {
			return StringUtil.toLowerCase(typeName);
		}

		if (typeName.startsWith("IDf")) {
			return StringUtil.replaceFirst(typeName, "IDf", "idf");
		}

		if (typeName.startsWith("OSGi")) {
			return StringUtil.replaceFirst(typeName, "OSGi", "osgi");
		}

		for (int i = 0; i < typeName.length(); i++) {
			char c = typeName.charAt(i);

			if (!Character.isLowerCase(c)) {
				continue;
			}

			if (i == 0) {
				return typeName;
			}

			if (i == 1) {
				return StringUtil.toLowerCase(typeName.substring(0, 1)) +
					typeName.substring(1);
			}

			return StringUtil.toLowerCase(typeName.substring(0, i - 1)) +
				typeName.substring(i - 1);
		}

		return StringUtil.toLowerCase(typeName);
	}

	protected static final String MSG_RENAME_VARIABLE = "variable.rename";

	private void _checkClassNameVariable(
		DetailAST detailAST, String variableName) {

		Matcher matcher = _classNameVariableNamePattern.matcher(variableName);

		if (!matcher.find()) {
			return;
		}

		String match = matcher.group(1);

		String className = StringUtil.removeChar(match, CharPool.UNDERLINE);

		List<DetailAST> valueDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.IDENT, TokenTypes.STRING_LITERAL);

		for (DetailAST valueDetailAST : valueDetailASTs) {
			String value = StringUtil.removeChar(
				valueDetailAST.getText(), CharPool.QUOTE);

			if (value.matches("(?i)(.*\\.)?" + className)) {
				log(
					detailAST, MSG_RENAME_VARIABLE, variableName,
					"_CLASS_NAME_" + match);

				return;
			}
		}
	}

	private void _checkCountVariableName(
		DetailAST detailAST, String name, String typeName) {

		Matcher matcher = _countVariableNamePattern.matcher(name);

		if (!matcher.find()) {
			return;
		}

		String countlessVariableName = matcher.group(1);

		matcher = _countVariableNamePattern.matcher(typeName);

		if (matcher.find()) {
			return;
		}

		Set<DetailAST> detailASTSet = new TreeSet<>(
			new Comparator<DetailAST>() {

				@Override
				public int compare(DetailAST detailAST1, DetailAST detailAST2) {
					return detailAST1.getLineNo() - detailAST2.getLineNo();
				}

			});

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.OBJBLOCK) {
			detailASTSet.addAll(
				getAllChildTokens(
					parentDetailAST, false, TokenTypes.VARIABLE_DEF));
		}
		else {
			if (parentDetailAST.getType() == TokenTypes.SLIST) {
				detailASTSet.addAll(
					getAllChildTokens(
						parentDetailAST, false, TokenTypes.VARIABLE_DEF));
			}
			else if (parentDetailAST.getType() == TokenTypes.RESOURCES) {
				detailASTSet.addAll(
					getAllChildTokens(
						parentDetailAST, false, TokenTypes.RESOURCE));

				parentDetailAST = parentDetailAST.getParent();

				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() == TokenTypes.LITERAL_TRY) {
					DetailAST slistDetailAST = parentDetailAST.findFirstToken(
						TokenTypes.SLIST);

					if (slistDetailAST != null) {
						detailASTSet.addAll(
							getAllChildTokens(
								slistDetailAST, false,
								TokenTypes.VARIABLE_DEF));
					}
				}
			}

			parentDetailAST = getParentWithTokenType(
				detailAST, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF);

			if (parentDetailAST != null) {
				DetailAST parametersDetailAST = parentDetailAST.findFirstToken(
					TokenTypes.PARAMETERS);

				detailASTSet.addAll(
					getAllChildTokens(
						parametersDetailAST, false, TokenTypes.PARAMETER_DEF));

				DetailAST slistDetailAST = parentDetailAST.findFirstToken(
					TokenTypes.SLIST);

				if (slistDetailAST != null) {
					detailASTSet.addAll(
						getAllChildTokens(
							slistDetailAST, false, TokenTypes.VARIABLE_DEF));
				}
			}
		}

		int endLineNumber = -1;

		DetailAST slistDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.SLIST);

		if (slistDetailAST != null) {
			endLineNumber = getEndLineNumber(slistDetailAST);
		}

		String expectedVariableName = countlessVariableName + "1";

		for (DetailAST curDetailAST : detailASTSet) {
			if ((endLineNumber != -1) &&
				(curDetailAST.getLineNo() > endLineNumber)) {

				break;
			}

			String variableName = _getVariableName(curDetailAST);

			if (variableName.equals(countlessVariableName)) {
				parentDetailAST = detailAST.getParent();

				if (parentDetailAST.getType() == TokenTypes.FOR_EACH_CLAUSE) {
					DetailAST curNameDetailAST = _getDetailAST(
						detailASTSet, "cur" + countlessVariableName);

					if (curNameDetailAST == null) {
						log(detailAST, _MSG_INCORRECT_NAME_FOR_STATEMENT, name);
					}
				}
				else if (curDetailAST.getType() == TokenTypes.PARAMETER_DEF) {
					return;
				}
				else {
					DetailAST expectedVariableNameDetailAST = _getDetailAST(
						detailASTSet, expectedVariableName);

					if (expectedVariableNameDetailAST == null) {
						log(
							curDetailAST, MSG_RENAME_VARIABLE,
							countlessVariableName, expectedVariableName);
					}
					else {
						log(
							curDetailAST, _MSG_INCORRECT_COUNT_VARIABLE,
							countlessVariableName, expectedVariableName);
					}
				}
			}
			else if (variableName.matches(countlessVariableName + "[0-9]+")) {
				int count = GetterUtil.getInteger(
					StringUtil.removeSubstring(
						variableName, countlessVariableName));

				expectedVariableName = countlessVariableName + (count + 1);
			}
		}
	}

	private void _checkExceptionVariableName(
		DetailAST detailAST, String name, String typeName) {

		if (!StringUtil.endsWith(getAbsolutePath(), "ExceptionMapper.java")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST.getType() == TokenTypes.LITERAL_CATCH) ||
			(detailAST.getType() != TokenTypes.PARAMETER_DEF)) {

			return;
		}

		String[] names = StringUtil.split(typeName, StringPool.PERIOD);

		if (names.length > 2) {
			return;
		}

		typeName = names[0];

		if (!StringUtil.endsWith(typeName, "Exception")) {
			return;
		}

		if (names.length == 2) {
			typeName = names[1];
		}

		String expectedVariableName = getExpectedVariableName(typeName);

		if (!name.equals(expectedVariableName)) {
			log(detailAST, MSG_RENAME_VARIABLE, name, expectedVariableName);
		}
	}

	private void _checkInstanceVariableName(
		DetailAST detailAST, String name, String typeName) {

		if (!name.contentEquals("_instance")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			if (parentDetailAST == null) {
				return;
			}

			if (parentDetailAST.getType() != TokenTypes.CLASS_DEF) {
				parentDetailAST = parentDetailAST.getParent();

				continue;
			}

			DetailAST identDetailAST = parentDetailAST.findFirstToken(
				TokenTypes.IDENT);

			if (!typeName.equals(identDetailAST.getText())) {
				return;
			}

			DetailAST grandParentDetailAST = parentDetailAST.getParent();

			if (grandParentDetailAST != null) {
				return;
			}

			String expectedVariableName = _getExpectedVariableName(
				typeName, "_", "");

			List<DetailAST> variableDeclarationDetailASTs = getAllChildTokens(
				parentDetailAST, true, TokenTypes.VARIABLE_DEF);

			for (DetailAST variableDeclarationDetailAST :
					variableDeclarationDetailASTs) {

				identDetailAST = variableDeclarationDetailAST.findFirstToken(
					TokenTypes.IDENT);

				if (expectedVariableName.equals(identDetailAST.getText())) {
					return;
				}
			}

			log(detailAST, MSG_RENAME_VARIABLE, name, expectedVariableName);

			return;
		}
	}

	private void _checkIsVariableName(DetailAST detailAST, String name) {
		if (!_isBooleanType(detailAST.findFirstToken(TokenTypes.TYPE))) {
			return;
		}

		Matcher matcher = _isVariableNamePattern.matcher(name);

		if (!matcher.find()) {
			return;
		}

		String group2 = matcher.group(2);

		String newName = null;

		if (group2.equals("is")) {
			newName =
				StringUtil.toLowerCase(matcher.group(3)) + matcher.group(4);

			if (!Validator.isVariableName(newName)) {
				return;
			}

			newName = matcher.group(1) + newName;
		}
		else {
			newName = matcher.group(1) + matcher.group(3) + matcher.group(4);
		}

		if (!_classHasVariableWithName(detailAST, newName)) {
			log(detailAST, MSG_RENAME_VARIABLE, name, newName);
		}
	}

	private void _checkShortTypeNames(
		DetailAST detailAST, String variableName, String typeName,
		String expectedVariableName) {

		if (StringUtil.equals(variableName, "_" + expectedVariableName) ||
			!detailAST.branchContains(TokenTypes.LITERAL_PRIVATE) ||
			detailAST.branchContains(TokenTypes.LITERAL_STATIC)) {

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return;
		}

		List<DetailAST> variableDeclarationDetailASTs = new ArrayList<>();

		variableDeclarationDetailASTs.addAll(
			getAllChildTokens(parentDetailAST, false, TokenTypes.VARIABLE_DEF));

		int count = 0;

		for (DetailAST variableDeclarationDetailAST :
				variableDeclarationDetailASTs) {

			DetailAST typeDetailAST =
				variableDeclarationDetailAST.findFirstToken(TokenTypes.TYPE);

			DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

			if (firstChildDetailAST == null) {
				continue;
			}

			if ((firstChildDetailAST.getType() != TokenTypes.DOT) &&
				typeName.equals(getTypeName(typeDetailAST, false))) {

				count++;
			}
		}

		if (count == 1) {
			log(
				detailAST, MSG_RENAME_VARIABLE, variableName,
				"_" + expectedVariableName);
		}
	}

	private void _checkTypeName(
		DetailAST detailAST, String variableName, String typeName) {

		if (variableName.matches("_?INSTANCE") ||
			(typeName.equals("Object") &&
			 !variableName.matches("(o|obj|(.*Obj))[0-9]*"))) {

			return;
		}

		List<String> enforceShortTypeNames = getAttributeValues(
			_ENFORCE_SHORT_TYPE_NAMES_KEY);

		if (enforceShortTypeNames.contains(typeName)) {
			String expectedVariableName = getExpectedVariableName(typeName);

			_checkShortTypeNames(
				detailAST, variableName, typeName, expectedVariableName);
		}

		if (variableName.matches("(?i).*" + typeName + "[0-9]*")) {
			List<String> enforceTableSchemaFieldTypeNames = getAttributeValues(
				_ENFORCE_TABLE_SCHEMA_FIELD_TYPE_NAMES_KEY);

			for (String enforceTableSchemaFieldTypeName :
					enforceTableSchemaFieldTypeNames) {

				if (!typeName.matches(
						enforceTableSchemaFieldTypeName + "Field")) {

					continue;
				}

				String expectedVariableName = _getExpectedVariableName(
					detailAST, enforceTableSchemaFieldTypeName);

				if (Validator.isNull(expectedVariableName)) {
					continue;
				}

				if (!variableName.matches(
						"(?i).*" + expectedVariableName + "[0-9]*")) {

					log(
						detailAST, _MSG_INCORRECT_ENDING_VARIABLE_1, typeName,
						expectedVariableName);

					return;
				}
			}

			return;
		}

		String expectedVariableName = getExpectedVariableName(typeName);

		if (StringUtil.isUpperCase(variableName)) {
			expectedVariableName = StringUtil.toUpperCase(
				StringUtil.replace(
					TextFormatter.format(expectedVariableName, TextFormatter.K),
					CharPool.DASH, CharPool.UNDERLINE));

			if (variableName.matches(
					"(.*_)?" + expectedVariableName + "(_[0-9]+)?")) {

				return;
			}

			if (variableName.matches(
					"(.*_)?" + expectedVariableName + "[0-9]+")) {

				log(
					detailAST, MSG_RENAME_VARIABLE, variableName,
					StringUtil.replaceLast(
						variableName, expectedVariableName,
						expectedVariableName + "_"));

				return;
			}
		}

		if (typeName.endsWith("Impl")) {
			log(
				detailAST, _MSG_INCORRECT_ENDING_VARIABLE_1, typeName,
				expectedVariableName);

			return;
		}

		List<String> enforceTypeNames = getAttributeValues(
			_ENFORCE_TYPE_NAMES_KEY);

		for (String enforceTypeName : enforceTypeNames) {
			if (typeName.matches(enforceTypeName)) {
				log(
					detailAST, _MSG_INCORRECT_ENDING_VARIABLE_1, typeName,
					expectedVariableName);

				return;
			}
		}
	}

	private void _checkTypo(DetailAST detailAST, String variableName) {
		List<DetailAST> stringLiteralDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.STRING_LITERAL);

		for (DetailAST stringLiteralDetailAST : stringLiteralDetailASTs) {
			String expectedVariableName = _getExpectedVariableName(
				stringLiteralDetailAST.getText());

			if (SourceUtil.hasTypo(
					StringUtil.toLowerCase(variableName),
					StringUtil.toLowerCase(expectedVariableName))) {

				log(
					detailAST, _MSG_TYPO_VARIABLE, variableName,
					expectedVariableName);
			}
		}
	}

	private void _checkTypo(
		DetailAST detailAST, String variableName, String typeName,
		boolean checkCaseSensitive) {

		if (StringUtil.isUpperCase(variableName) ||
			typeName.contains(StringPool.UNDERLINE)) {

			return;
		}

		if (checkCaseSensitive && StringUtil.endsWith(variableName, typeName) &&
			!variableName.endsWith(typeName)) {

			String variableEnding = variableName.substring(
				variableName.length() - typeName.length());

			if (Character.isUpperCase(typeName.charAt(0)) &&
				Character.isUpperCase(variableEnding.charAt(0))) {

				log(
					detailAST, _MSG_TYPO_VARIABLE, variableName,
					StringUtil.replaceLast(
						variableName, variableEnding, typeName));

				return;
			}
		}

		List<String> allowedVariableNames = getAttributeValues(
			_ALLOWED_VARIABLE_NAMES_KEY);

		if (allowedVariableNames.contains(variableName)) {
			return;
		}

		String nameTrailingDigits = _getTrailingDigits(variableName);

		String trimmedName = StringUtil.replaceLast(
			variableName, nameTrailingDigits, StringPool.BLANK);

		String leadingUnderline = StringPool.BLANK;

		if (variableName.startsWith(StringPool.UNDERLINE)) {
			leadingUnderline = StringPool.UNDERLINE;

			trimmedName = trimmedName.substring(1);
		}

		String typeNameTrailingDigits = _getTrailingDigits(typeName);

		String trimmedTypeName = StringUtil.replaceLast(
			typeName, typeNameTrailingDigits, StringPool.BLANK);

		String expectedVariableName = getExpectedVariableName(trimmedTypeName);

		if (StringUtil.equals(trimmedName, expectedVariableName)) {
			return;
		}

		if (StringUtil.equalsIgnoreCase(trimmedName, trimmedTypeName)) {
			for (int i = expectedVariableName.length() - 1; i >= 0; i--) {
				char c1 = trimmedName.charAt(i);

				if (c1 == expectedVariableName.charAt(i)) {
					continue;
				}

				if (i < (expectedVariableName.length() - 1)) {
					char c2 = trimmedName.charAt(i + 1);

					if (Character.isUpperCase(c1) &&
						(Character.isDigit(c2) || Character.isUpperCase(c2))) {

						return;
					}
				}
			}

			log(
				detailAST, _MSG_TYPO_VARIABLE, variableName,
				StringBundler.concat(
					leadingUnderline, expectedVariableName,
					nameTrailingDigits));

			return;
		}

		String lowerCaseTrimmedTypeName = StringUtil.toLowerCase(
			trimmedTypeName);

		if (SourceUtil.hasTypo(
				StringUtil.toLowerCase(trimmedName),
				lowerCaseTrimmedTypeName)) {

			log(
				detailAST, _MSG_TYPO_VARIABLE, variableName,
				_getExpectedVariableName(
					typeName, leadingUnderline, nameTrailingDigits));
		}

		Matcher matcher = _camelCaseNamePattern.matcher(trimmedName);

		while (matcher.find()) {
			int x = matcher.start() + 1;

			if (SourceUtil.hasTypo(
					StringUtil.toLowerCase(trimmedName.substring(x)),
					lowerCaseTrimmedTypeName)) {

				log(
					detailAST, _MSG_TYPO_VARIABLE, variableName,
					StringBundler.concat(
						leadingUnderline, variableName.substring(0, x),
						typeName, nameTrailingDigits));
			}
		}
	}

	private void _checkVariableNameByMethodCall(
		DetailAST detailAST, String variableName) {

		DetailAST parentDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CLASS_DEF, TokenTypes.CTOR_DEF,
			TokenTypes.METHOD_DEF);

		if (parentDetailAST == null) {
			return;
		}

		List<DetailAST> assignDetailASTs = getAllChildTokens(
			parentDetailAST, true, TokenTypes.ASSIGN);

		for (DetailAST assignDetailAST : assignDetailASTs) {
			DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

			if (firstChildDetailAST == null) {
				continue;
			}

			if (equals(assignDetailAST.getParent(), detailAST)) {
				if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
					continue;
				}

				firstChildDetailAST = firstChildDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
					continue;
				}

				String absolutePath = getAbsolutePath();

				if (absolutePath.contains("/test/")) {
					_checkVariableNameByMethodCall(
						firstChildDetailAST, variableName, "ReflectionTestUtil",
						"getAndSetFieldValue", detailAST);
				}

				String methodName = getMethodName(firstChildDetailAST);

				if (methodName.equals("stream")) {
					firstChildDetailAST = firstChildDetailAST.getFirstChild();

					if (firstChildDetailAST.getType() != TokenTypes.DOT) {
						continue;
					}

					firstChildDetailAST = firstChildDetailAST.getFirstChild();

					_checkTypo(
						detailAST, variableName,
						firstChildDetailAST.getText() + "Stream", false);
				}
			}
			else if ((firstChildDetailAST.getType() == TokenTypes.IDENT) &&
					 variableName.equals(firstChildDetailAST.getText())) {

				DetailAST nextSiblingDetailAST =
					firstChildDetailAST.getNextSibling();

				if (nextSiblingDetailAST.getType() != TokenTypes.METHOD_CALL) {
					continue;
				}

				String absolutePath = getAbsolutePath();

				if (absolutePath.contains("/test/")) {
					_checkVariableNameByMethodCall(
						nextSiblingDetailAST, variableName,
						"ReflectionTestUtil", "getAndSetFieldValue",
						firstChildDetailAST);
				}

				String methodName = getMethodName(nextSiblingDetailAST);

				if (!methodName.matches("get[A-Z].*")) {
					continue;
				}

				_checkTypo(
					detailAST, variableName, methodName.substring(3), false);
			}
		}
	}

	private void _checkVariableNameByMethodCall(
		DetailAST methodCallDetailAST, String variableName, String className,
		String methodName, DetailAST detailAST) {

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.DOT)) {

			return;
		}

		List<String> names = getNames(firstChildDetailAST, false);

		if ((names.size() != 2) ||
			!StringUtil.equals(className, names.get(0)) ||
			!StringUtil.equals(methodName, names.get(1))) {

			return;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			firstChildDetailAST.getParent());

		if (parameterExprDetailASTs.size() < 2) {
			return;
		}

		DetailAST exprDetailAST = parameterExprDetailASTs.get(1);

		firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
			return;
		}

		String expectedVariableName = _getExpectedVariableName(
			firstChildDetailAST.getText());

		if (!variableName.matches("(?i).*" + expectedVariableName + "[0-9]*")) {
			log(
				detailAST, _MSG_INCORRECT_ENDING_VARIABLE_2,
				className + "." + methodName, expectedVariableName);
		}
	}

	private boolean _classHasVariableWithName(
		DetailAST detailAST, String variableName) {

		DetailAST parentDetailAST = detailAST.getParent();

		List<DetailAST> definitionDetailASTs = new ArrayList<>();

		while (true) {
			if (parentDetailAST == null) {
				break;
			}

			if (parentDetailAST.getType() == TokenTypes.METHOD_DEF) {
				definitionDetailASTs.addAll(
					getAllChildTokens(
						parentDetailAST, true, TokenTypes.PARAMETER_DEF,
						TokenTypes.VARIABLE_DEF));
			}

			if (parentDetailAST.getType() == TokenTypes.CLASS_DEF) {
				DetailAST objBlockDetailAST = parentDetailAST.findFirstToken(
					TokenTypes.OBJBLOCK);

				definitionDetailASTs.addAll(
					getAllChildTokens(
						objBlockDetailAST, false, TokenTypes.VARIABLE_DEF));
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		for (DetailAST definitionDetailAST : definitionDetailASTs) {
			if (variableName.equals(getName(definitionDetailAST))) {
				return true;
			}
		}

		return false;
	}

	private DetailAST _getDetailAST(Set<DetailAST> detailASTSet, String name) {
		for (DetailAST detailAST : detailASTSet) {
			if (StringUtil.equalsIgnoreCase(
					name, _getVariableName(detailAST))) {

				return detailAST;
			}
		}

		return null;
	}

	private String _getExpectedVariableName(
		DetailAST detailAST, String enforceTableSchemaFieldTypeName) {

		DetailAST assignDetailAST = detailAST.findFirstToken(TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return null;
		}

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return null;
		}

		List<String> names = getNames(firstChildDetailAST, false);

		if (names.size() != 2) {
			return null;
		}

		String methodCallClassName = names.get(0);
		String methodCallMethodName = names.get(1);

		if (!methodCallClassName.matches(
				"(?i)" + enforceTableSchemaFieldTypeName + "Builder") &&
			!methodCallMethodName.matches(
				"(?i)add" + enforceTableSchemaFieldTypeName)) {

			return null;
		}

		DetailAST firstParameterExprDetailAST = getFirstParameterExprDetailAST(
			firstChildDetailAST.getParent());

		if (firstParameterExprDetailAST == null) {
			return null;
		}

		firstChildDetailAST = firstParameterExprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
			return null;
		}

		String s = firstChildDetailAST.getText();

		s = TextFormatter.format(
			StringUtil.replace(
				TextFormatter.format(s, TextFormatter.Q), '.', '-'),
			TextFormatter.M);

		return s.substring(1, s.length() - 1) +
			enforceTableSchemaFieldTypeName + "Field";
	}

	private String _getExpectedVariableName(String literalString) {
		String s = literalString.substring(1, literalString.length() - 1);

		if (s.matches("_?[a-z][A-Za-z0-9]+")) {
			return StringUtil.removeChar(s, '_');
		}

		if (s.matches("[A-Z0-9_]+")) {
			if (s.startsWith("_")) {
				s = s.substring(1);
			}

			return TextFormatter.format(
				StringUtil.replace(StringUtil.toLowerCase(s), '_', '-'),
				TextFormatter.M);
		}

		if (s.matches("[A-Z][A-Za-z0-9]+")) {
			return TextFormatter.format(s, TextFormatter.L);
		}

		if (s.matches("[a-z0-9 ._-]+")) {
			return TextFormatter.format(
				StringUtil.replace(
					s, new char[] {'_', ' ', '.'}, new char[] {'-', '-', '-'}),
				TextFormatter.M);
		}

		return null;
	}

	private String _getExpectedVariableName(
		String typeName, String leadingUnderline, String trailingDigits) {

		return StringBundler.concat(
			leadingUnderline, getExpectedVariableName(typeName),
			trailingDigits);
	}

	private String _getTrailingDigits(String s) {
		String digits = StringPool.BLANK;

		for (int i = s.length() - 1; i >= 0; i--) {
			if (Character.isDigit(s.charAt(i))) {
				digits = s.charAt(i) + digits;
			}
			else {
				return digits;
			}
		}

		return digits;
	}

	private String _getVariableName(DetailAST variableDefinitionDetailAST) {
		DetailAST nameDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		return nameDetailAST.getText();
	}

	private boolean _isBooleanType(DetailAST typeDetailAST) {
		DetailAST childDetailAST = typeDetailAST.getFirstChild();

		if (childDetailAST == null) {
			return false;
		}

		if (childDetailAST.getType() == TokenTypes.LITERAL_BOOLEAN) {
			return true;
		}

		if (childDetailAST.getType() != TokenTypes.IDENT) {
			return false;
		}

		String name = childDetailAST.getText();

		return name.equals("Boolean");
	}

	private static final String _ALLOWED_VARIABLE_NAMES_KEY =
		"allowedVariableNames";

	private static final String _ENFORCE_SHORT_TYPE_NAMES_KEY =
		"enforceShortTypeNames";

	private static final String _ENFORCE_TABLE_SCHEMA_FIELD_TYPE_NAMES_KEY =
		"enforceTableSchemaFieldTypeNames";

	private static final String _ENFORCE_TYPE_NAMES_KEY = "enforceTypeNames";

	private static final String _MSG_INCORRECT_COUNT_VARIABLE =
		"variable.incorrect.count";

	private static final String _MSG_INCORRECT_ENDING_VARIABLE_1 =
		"variable.incorrect.ending.1";

	private static final String _MSG_INCORRECT_ENDING_VARIABLE_2 =
		"variable.incorrect.ending.2";

	private static final String _MSG_INCORRECT_NAME_FOR_STATEMENT =
		"variable.name.incorrect.for.statement";

	private static final String _MSG_TYPO_VARIABLE = "variable.typo";

	private static final Pattern _camelCaseNamePattern = Pattern.compile(
		"[a-z][A-Z](?=[a-z])");
	private static final Pattern _classNameVariableNamePattern =
		Pattern.compile("^_(.+)_CLASS_NAME$");
	private static final Pattern _countVariableNamePattern = Pattern.compile(
		"^(\\w+?)([1-9][0-9]*)$");
	private static final Pattern _isVariableNamePattern = Pattern.compile(
		"^(_?)(is|IS_)([A-Z])(.*)");

}