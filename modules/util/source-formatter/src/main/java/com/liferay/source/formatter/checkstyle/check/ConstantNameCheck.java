/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class ConstantNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.VARIABLE_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		_checkConstantName(detailAST);
	}

	private void _checkConstantName(DetailAST detailAST) {
		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (!modifiersDetailAST.branchContains(TokenTypes.FINAL) ||
			!modifiersDetailAST.branchContains(TokenTypes.LITERAL_STATIC)) {

			return;
		}

		DetailAST nameDetailAST = detailAST.findFirstToken(TokenTypes.IDENT);

		String name = nameDetailAST.getText();

		if (name.equals("serialPersistentFields") ||
			name.equals("serialVersionUID")) {

			return;
		}

		String regex = null;

		String typeName = getTypeName(detailAST, false);

		List<String> camelCaseTypeNames = getAttributeValues(
			_CAMEL_CASE_TYPE_NAMES_KEY);

		for (String camelCaseTypeName : camelCaseTypeNames) {
			if (!typeName.matches(camelCaseTypeName) &&
				!isCollection(detailAST.findFirstToken(TokenTypes.TYPE))) {

				continue;
			}

			regex = _CAMEL_CASE_REGEX;
		}

		if (regex == null) {
			if (_isImmutableFieldType(typeName)) {
				regex = _UPPER_CASE_REGEX;
			}
			else {
				regex = _CONSTANT_NAME_REGEX;
				typeName = null;
			}
		}

		String accessLevel = null;

		if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE)) {
			accessLevel = JavaTerm.ACCESS_MODIFIER_PRIVATE;

			regex = StringUtil.replaceFirst(regex, '^', "^_");
		}
		else if (modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_PROTECTED)) {

			accessLevel = JavaTerm.ACCESS_MODIFIER_PROTECTED;
		}
		else if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {
			accessLevel = JavaTerm.ACCESS_MODIFIER_PUBLIC;
		}
		else {
			return;
		}

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(name);

		if (!matcher.find()) {
			if (typeName == null) {
				log(
					nameDetailAST, _MSG_INVALID_CONSTANT_NAME, accessLevel,
					name, regex);
			}
			else {
				log(
					nameDetailAST, _MSG_INVALID_CONSTANT_TYPE_NAME, accessLevel,
					name, typeName, regex);
			}
		}
	}

	private boolean _isImmutableFieldType(String typeName) {
		List<String> immutableFieldTypes = getAttributeValues(
			_IMMUTABLE_FIELD_TYPES_KEY);

		for (String immutableFieldType : immutableFieldTypes) {
			if (typeName.equals(immutableFieldType) ||
				typeName.startsWith(immutableFieldType + "[]")) {

				return true;
			}
		}

		return false;
	}

	private static final String _CAMEL_CASE_REGEX = "^[a-z0-9][a-zA-Z0-9]*$";

	private static final String _CAMEL_CASE_TYPE_NAMES_KEY =
		"camelCaseTypeNames";

	private static final String _CONSTANT_NAME_REGEX =
		"^[a-zA-Z0-9][_a-zA-Z0-9]*$";

	private static final String _IMMUTABLE_FIELD_TYPES_KEY =
		"immutableFieldTypes";

	private static final String _MSG_INVALID_CONSTANT_NAME =
		"name.invalidConstantPattern";

	private static final String _MSG_INVALID_CONSTANT_TYPE_NAME =
		"name.invalidConstantTypePattern";

	private static final String _UPPER_CASE_REGEX = "^[A-Z0-9][_A-Z0-9]*$";

}