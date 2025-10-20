/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class CamelCaseNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (AnnotationUtil.containsAnnotation(detailAST, "Deprecated") ||
			AnnotationUtil.containsAnnotation(detailAST, "DisplayName") ||
			AnnotationUtil.containsAnnotation(detailAST, "Meta.AD") ||
			((detailAST.getType() == TokenTypes.METHOD_DEF) &&
			 AnnotationUtil.containsAnnotation(detailAST, "Override"))) {

			return;
		}

		DetailAST nameDetailAST = detailAST.findFirstToken(TokenTypes.IDENT);

		_checkIncorrectCamelCase(
			detailAST, nameDetailAST, "non", "nonProxyHost",
			"nonSerializableObjectHandler", "nonSpringServlet");
		_checkIncorrectCamelCase(detailAST, nameDetailAST, "re", "reCaptcha");
		_checkIncorrectCamelCase(
			detailAST, nameDetailAST, "sub", "subDSLQuery", "subSelect");

		_checkRequiredCamelCase(
			detailAST, nameDetailAST, "name", "filenameFilter", "hostname",
			"nickname", "rename", "subname");
	}

	private void _checkIncorrectCamelCase(
		DetailAST detailAST, DetailAST nameDetailAST, String s,
		String... allowedNames) {

		String name = nameDetailAST.getText();

		if (_isAllowedName(name, allowedNames)) {
			return;
		}

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"(^_?", s, "|", TextFormatter.format(s, TextFormatter.G),
				")([A-Z]([a-z]*|[A-Z]*))"));

		Matcher matcher = pattern.matcher(name);

		if (matcher.find()) {
			if (_containsNameInAssignStatement(
					detailAST, s + matcher.group(2)) ||
				_containsMatchingTypeName(detailAST, name, pattern)) {

				return;
			}

			if (detailAST.getType() == TokenTypes.METHOD_DEF) {
				log(
					nameDetailAST, _MSG_INCORRECT_FOLLOWING_UPPERCASE, s,
					"method", name);
			}
			else if (detailAST.getType() == TokenTypes.PARAMETER_DEF) {
				log(
					nameDetailAST, _MSG_INCORRECT_FOLLOWING_UPPERCASE, s,
					"parameter", name);
			}
			else {
				log(
					nameDetailAST, _MSG_INCORRECT_FOLLOWING_UPPERCASE, s,
					"variable", name);
			}
		}

		if (detailAST.getType() != TokenTypes.VARIABLE_DEF) {
			return;
		}

		pattern = Pattern.compile(
			StringBundler.concat(
				"(\\A|_)(", StringUtil.toUpperCase(s), "_[A-Z]+)"));

		matcher = pattern.matcher(name);

		if (matcher.find() &&
			!_containsNameInAssignStatement(detailAST, matcher.group(2))) {

			log(
				nameDetailAST, _MSG_INCORRECT_FOLLOWING_UNDERSCORE,
				StringUtil.toUpperCase(s), name);
		}
	}

	private void _checkRequiredCamelCase(
		DetailAST detailAST, DetailAST nameDetailAST, String s,
		String... allowedNames) {

		String name = nameDetailAST.getText();

		if (_isAllowedName(name, allowedNames)) {
			return;
		}

		Pattern pattern = Pattern.compile(
			"(((\\A|_)[a-z]+|[A-Z]([A-Z]+|[a-z]+))" + s + ")");

		Matcher matcher = pattern.matcher(name);

		if (matcher.find()) {
			if (_containsNameInAssignStatement(detailAST, matcher.group(1)) ||
				_containsMatchingTypeName(detailAST, name, pattern)) {

				return;
			}

			if (detailAST.getType() == TokenTypes.METHOD_DEF) {
				log(
					nameDetailAST, _MSG_REQUIRED_STARTING_UPPERCASE, s,
					"method", name);
			}
			else if (detailAST.getType() == TokenTypes.PARAMETER_DEF) {
				log(
					nameDetailAST, _MSG_REQUIRED_STARTING_UPPERCASE, s,
					"parameter", name);
			}
			else {
				log(
					nameDetailAST, _MSG_REQUIRED_STARTING_UPPERCASE, s,
					"variable", name);
			}
		}

		if (detailAST.getType() != TokenTypes.VARIABLE_DEF) {
			return;
		}

		pattern = Pattern.compile(
			StringBundler.concat(
				"(\\A|_)([A-Z]+", StringUtil.toUpperCase(s), ").*"));

		matcher = pattern.matcher(name);

		if (matcher.find() &&
			!_containsNameInAssignStatement(detailAST, matcher.group(2))) {

			log(
				nameDetailAST, _MSG_REQUIRED_PRECEDING_UNDERSCORE,
				StringUtil.toUpperCase(s), name);
		}
	}

	private boolean _containsMatchingTypeName(
		DetailAST detailAST, String name, Pattern pattern) {

		if ((detailAST.getType() == TokenTypes.PARAMETER_DEF) ||
			(detailAST.getType() == TokenTypes.VARIABLE_DEF)) {

			Matcher matcher = pattern.matcher(
				getVariableTypeName(detailAST, name, false));

			return matcher.find();
		}

		String returnTypeName = getTypeName(detailAST, false);

		if (!returnTypeName.equals("void")) {
			Matcher matcher = pattern.matcher(returnTypeName);

			if (matcher.find()) {
				return true;
			}
		}

		DetailAST parametersDetailAST = detailAST.findFirstToken(
			TokenTypes.PARAMETERS);

		List<DetailAST> parameterDetailASTs = getAllChildTokens(
			parametersDetailAST, false, TokenTypes.PARAMETER_DEF);

		for (DetailAST parameterDetailAST : parameterDetailASTs) {
			Matcher matcher = pattern.matcher(
				getTypeName(parameterDetailAST, false));

			if (matcher.find()) {
				return true;
			}
		}

		return false;
	}

	private boolean _containsNameInAssignStatement(
		DetailAST detailAST, String name) {

		DetailAST assignDetailAST = detailAST.findFirstToken(TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return false;
		}

		String constantFormatName = _getConstantFormatName(name);

		String lowerCaseCamelCaseFormatName = _getCamelCaseFormatName(name);

		String upperCaseCamelCaseFormatName = TextFormatter.format(
			lowerCaseCamelCaseFormatName, TextFormatter.G);

		List<DetailAST> stringLiteralDetailASTs = getAllChildTokens(
			assignDetailAST, true, TokenTypes.STRING_LITERAL);

		if (!stringLiteralDetailASTs.isEmpty()) {
			if (lowerCaseCamelCaseFormatName == null) {
				return false;
			}

			String propertyFormatName = _getPropertyFormatName(
				lowerCaseCamelCaseFormatName);

			for (DetailAST stringLiteralDetailAST : stringLiteralDetailASTs) {
				String text = stringLiteralDetailAST.getText();

				if (text.contains(constantFormatName) ||
					text.contains(lowerCaseCamelCaseFormatName) ||
					text.contains(name) || text.contains(propertyFormatName) ||
					text.contains(upperCaseCamelCaseFormatName)) {

					return true;
				}
			}
		}

		for (String text : getNames(assignDetailAST, true)) {
			if (text.contains(constantFormatName) ||
				text.contains(lowerCaseCamelCaseFormatName) ||
				text.contains(name) ||
				text.contains(upperCaseCamelCaseFormatName)) {

				return true;
			}
		}

		return false;
	}

	private String _getCamelCaseFormatName(String name) {
		if (name.startsWith(StringPool.UNDERLINE)) {
			name = name.substring(1);
		}

		if (!StringUtil.isUpperCase(name)) {
			return TextFormatter.format(name, TextFormatter.I);
		}

		name = TextFormatter.format(name, TextFormatter.O);

		name = StringUtil.toLowerCase(name);

		return TextFormatter.format(name, TextFormatter.M);
	}

	private String _getConstantFormatName(String name) {
		if (name.startsWith(StringPool.UNDERLINE)) {
			name = name.substring(1);
		}

		if (StringUtil.isUpperCase(name)) {
			return name;
		}

		name = TextFormatter.format(name, TextFormatter.K);

		name = TextFormatter.format(name, TextFormatter.N);

		return StringUtil.toUpperCase(name);
	}

	private String _getPropertyFormatName(String camelCaseName) {
		String propertyFormatName = TextFormatter.format(
			camelCaseName, TextFormatter.K);

		return StringUtil.replace(
			propertyFormatName, CharPool.DASH, CharPool.PERIOD);
	}

	private boolean _isAllowedName(String name, String[] allowedNames) {
		for (String allowedName : allowedNames) {
			if (name.startsWith(allowedName) ||
				name.startsWith("_" + allowedName) ||
				name.contains(
					TextFormatter.format(allowedName, TextFormatter.G))) {

				return true;
			}

			String constantFormatName = _getConstantFormatName(allowedName);

			if (name.startsWith(constantFormatName) ||
				name.contains("_" + constantFormatName)) {

				return true;
			}
		}

		return false;
	}

	private static final String _MSG_INCORRECT_FOLLOWING_UNDERSCORE =
		"following.underscore.incorrect";

	private static final String _MSG_INCORRECT_FOLLOWING_UPPERCASE =
		"following.uppercase.incorrect";

	private static final String _MSG_REQUIRED_PRECEDING_UNDERSCORE =
		"preceding.underscore.required";

	private static final String _MSG_REQUIRED_STARTING_UPPERCASE =
		"starting.uppercase.required";

}