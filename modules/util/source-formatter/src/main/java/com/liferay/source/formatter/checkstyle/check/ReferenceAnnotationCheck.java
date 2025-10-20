/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class ReferenceAnnotationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		List<String> importNames = getImportNames(detailAST);

		if (!importNames.contains(
				"org.osgi.service.component.annotations.Reference")) {

			return;
		}

		List<DetailAST> detailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_DEF, TokenTypes.VARIABLE_DEF);

		for (DetailAST curDetailAST : detailASTs) {
			_checkReferenceAnnotation(curDetailAST);
		}
	}

	private void _checkCardinality(
		DetailAST annotationDetailAST, String cardinalityName) {

		if ((cardinalityName != null) &&
			cardinalityName.endsWith(_CARDINALITY_OPTIONAL)) {

			log(annotationDetailAST, _MSG_USE_SNAPSHOT);
		}
	}

	private void _checkDynamicMethod(
		DetailAST classDefinitionDetailAST, DetailAST methodDefinitionDetailAST,
		String methodName, String defaultUnbindMethodName) {

		Matcher matcher = _referenceMethodContentPattern.matcher(
			StringUtil.trim(_getMethodBody(methodDefinitionDetailAST)));

		if (!matcher.find()) {
			if (!_containsMethod(
					classDefinitionDetailAST, defaultUnbindMethodName)) {

				log(
					methodDefinitionDetailAST,
					_MSG_MISSING_DYNAMIC_POLICY_UNBIND);
			}

			return;
		}

		String variableName = matcher.group(1);

		List<DetailAST> variableDefinitionDetailASTs = getAllChildTokens(
			classDefinitionDetailAST, true, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefinitionDetailAST :
				variableDefinitionDetailASTs) {

			if (!variableName.equals(getName(variableDefinitionDetailAST))) {
				continue;
			}

			if (AnnotationUtil.containsAnnotation(
					variableDefinitionDetailAST, "Reference")) {

				return;
			}

			DetailAST modifiersDetailAST =
				variableDefinitionDetailAST.findFirstToken(
					TokenTypes.MODIFIERS);

			if (!modifiersDetailAST.branchContains(TokenTypes.LITERAL_STATIC)) {
				log(
					methodDefinitionDetailAST, _MSG_MOVE_REFERENCE, methodName,
					variableName);
			}
		}
	}

	private void _checkDynamicOption(
		DetailAST annotationDetailAST, String cardinalityName,
		String policyName) {

		if (policyName.endsWith(_POLICY_DYNAMIC) || (cardinalityName == null) ||
			!cardinalityName.endsWith(_CARDINALITY_OPTIONAL)) {

			return;
		}

		String policyOptionName = _getAnnotationMemberValue(
			annotationDetailAST, "policyOption", null);

		if ((policyOptionName == null) ||
			!policyOptionName.endsWith(_POLICY_OPTION_GREEDY)) {

			return;
		}

		log(annotationDetailAST, _MSG_MISSING_DYNAMIC_POLICY);
	}

	private void _checkReferenceAnnotation(DetailAST detailAST) {
		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Reference");

		if (annotationDetailAST == null) {
			return;
		}

		String cardinalityName = _getAnnotationMemberValue(
			annotationDetailAST, "cardinality", null);

		if (isAttributeValue(_CHECK_REFERENCE_CARDINALITY_OPTIONAL_KEY)) {
			_checkCardinality(annotationDetailAST, cardinalityName);
		}

		String policyName = _getAnnotationMemberValue(
			annotationDetailAST, "policy", _POLICY_STATIC);

		_checkDynamicOption(annotationDetailAST, cardinalityName, policyName);

		_checkTarget(annotationDetailAST);

		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			_checkVolatileVariable(detailAST, policyName);

			return;
		}

		DetailAST classDefinitionDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CLASS_DEF);

		if (classDefinitionDetailAST == null) {
			return;
		}

		String unbindName = _getAnnotationMemberValue(
			annotationDetailAST, "unbind", null);

		String methodName = getName(detailAST);

		String defaultUnbindMethodName = _getDefaultUnbindMethodName(
			methodName);

		_checkUnbind(
			annotationDetailAST, classDefinitionDetailAST,
			defaultUnbindMethodName, unbindName, policyName);

		if (policyName.endsWith(_POLICY_DYNAMIC) && (unbindName == null)) {
			_checkDynamicMethod(
				classDefinitionDetailAST, detailAST, methodName,
				defaultUnbindMethodName);
		}
	}

	private void _checkTarget(DetailAST annotationDetailAST) {
		String targetValue = _getAnnotationMemberValue(
			annotationDetailAST, "target", null);

		if (targetValue == null) {
			return;
		}

		List<String> allowedFileNames = getAttributeValues(_ALLOWED_FILE_NAMES);

		String absolutePath = getAbsolutePath();

		for (String allowedFileName : allowedFileNames) {
			if (absolutePath.endsWith(allowedFileName)) {
				return;
			}
		}

		List<String> forbiddenReferenceTargetValues = getAttributeValues(
			_FORBIDDEN_REFERENCE_TARGET_VALUES);

		if (forbiddenReferenceTargetValues.contains(targetValue)) {
			log(annotationDetailAST, _MSG_INCORRECT_TARGET_VALUE, targetValue);
		}
	}

	private void _checkUnbind(
		DetailAST annotationDetailAST, DetailAST classDefinitionDetailAST,
		String defaultUnbindMethodName, String unbindName, String policyName) {

		if (unbindName == null) {
			if (policyName.endsWith(_POLICY_STATIC) &&
				!_containsMethod(
					classDefinitionDetailAST, defaultUnbindMethodName)) {

				log(
					annotationDetailAST, _MSG_MISSING_STATIC_POLICY_UNBIND,
					_NO_UNBIND);
			}
		}
		else if (unbindName.equals("\"" + defaultUnbindMethodName + "\"")) {
			log(annotationDetailAST, _MSG_REDUNDANT_DEFAULT_UNBIND);
		}
		else if (unbindName.equals(_NO_UNBIND) &&
				 policyName.endsWith(_POLICY_DYNAMIC)) {

			log(annotationDetailAST, _MSG_MISSING_DYNAMIC_POLICY_UNBIND);
		}
	}

	private void _checkVolatileVariable(
		DetailAST variableDefinitionDetailAST, String policyName) {

		if (!policyName.endsWith(_POLICY_DYNAMIC)) {
			return;
		}

		DetailAST modifiersDetailAST =
			variableDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

		if (!modifiersDetailAST.branchContains(TokenTypes.LITERAL_VOLATILE)) {
			DetailAST identDetailAST =
				variableDefinitionDetailAST.findFirstToken(TokenTypes.IDENT);

			log(
				identDetailAST, _MSG_MISSING_VOLATILE,
				identDetailAST.getText());
		}
	}

	private boolean _containsMethod(
		DetailAST classDefinitionDetailAST, String methodName) {

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			classDefinitionDetailAST, true, TokenTypes.METHOD_DEF);

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			if (methodName.equals(getName(methodDefinitionDetailAST))) {
				return true;
			}
		}

		return false;
	}

	private String _getAnnotationMemberValue(
		DetailAST anontationDetailAST, String name, String defaultValue) {

		List<DetailAST> annotationMemberValuePairDetailASTs = getAllChildTokens(
			anontationDetailAST, false,
			TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR);

		for (DetailAST annotationMemberValuePairDetailAST :
				annotationMemberValuePairDetailASTs) {

			String annotationMemberName = getName(
				annotationMemberValuePairDetailAST);

			if (!annotationMemberName.equals(name)) {
				continue;
			}

			DetailAST expressionDetailAST =
				annotationMemberValuePairDetailAST.findFirstToken(
					TokenTypes.EXPR);

			if (expressionDetailAST == null) {
				return null;
			}

			FullIdent expressionFullIdent = FullIdent.createFullIdentBelow(
				expressionDetailAST);

			return expressionFullIdent.getText();
		}

		return defaultValue;
	}

	private String _getDefaultUnbindMethodName(String methodName) {
		if (methodName.startsWith("add")) {
			return StringUtil.replaceFirst(methodName, "add", "remove");
		}

		return "un" + methodName;
	}

	private String _getMethodBody(DetailAST methodDefinitionDetailAST) {
		DetailAST slistDetailAST = methodDefinitionDetailAST.findFirstToken(
			TokenTypes.SLIST);

		int startLineNumber = getStartLineNumber(slistDetailAST);
		int endLineNumber = getEndLineNumber(slistDetailAST);

		StringBundler sb = new StringBundler(
			(endLineNumber - startLineNumber - 1) * 2);

		for (int i = startLineNumber + 1; i < endLineNumber; i++) {
			sb.append(getLine(i - 1));
			sb.append("\n");
		}

		return sb.toString();
	}

	private static final String _ALLOWED_FILE_NAMES = "allowedFileNames";

	private static final String _CARDINALITY_OPTIONAL = "OPTIONAL";

	private static final String _CHECK_REFERENCE_CARDINALITY_OPTIONAL_KEY =
		"checkReferenceCardinalityOptional";

	private static final String _FORBIDDEN_REFERENCE_TARGET_VALUES =
		"forbiddenReferenceTargetValues";

	private static final String _MSG_INCORRECT_TARGET_VALUE =
		"target.value.incorrect";

	private static final String _MSG_MISSING_DYNAMIC_POLICY =
		"dynamic.policy.missing";

	private static final String _MSG_MISSING_DYNAMIC_POLICY_UNBIND =
		"unbind.dynamic.policy.missing";

	private static final String _MSG_MISSING_STATIC_POLICY_UNBIND =
		"unbind.static.policy.missing";

	private static final String _MSG_MISSING_VOLATILE = "volatile.missing";

	private static final String _MSG_MOVE_REFERENCE = "reference.move";

	private static final String _MSG_REDUNDANT_DEFAULT_UNBIND =
		"default.unbind.redundant";

	private static final String _MSG_USE_SNAPSHOT = "snapshot.use";

	private static final String _NO_UNBIND = "\"-\"";

	private static final String _POLICY_DYNAMIC = "DYNAMIC";

	private static final String _POLICY_OPTION_GREEDY = "GREEDY";

	private static final String _POLICY_STATIC = "STATIC";

	private static final Pattern _referenceMethodContentPattern =
		Pattern.compile("^(\\w+) =\\s+\\w+;$");

}