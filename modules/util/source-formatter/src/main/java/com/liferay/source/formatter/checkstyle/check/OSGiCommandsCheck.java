/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Lee
 */
public class OSGiCommandsCheck extends BaseCheck {

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

		DetailAST implementsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.IMPLEMENTS_CLAUSE);

		if (implementsClauseDetailAST == null) {
			return;
		}

		List<String> implementedClassNames = getNames(
			implementsClauseDetailAST, false);

		if (!implementedClassNames.contains("OSGiCommands")) {
			return;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		if (objBlockDetailAST == null) {
			return;
		}

		List<String> importNames = getImportNames(detailAST);

		if (!importNames.contains(
				"org.osgi.service.component.annotations.Component")) {

			return;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Component");

		if (annotationDetailAST == null) {
			return;
		}

		DetailAST annotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "property");

		if (annotationMemberValuePairDetailAST == null) {
			return;
		}

		DetailAST annotationArrayInitDetailAST =
			annotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.ANNOTATION_ARRAY_INIT);

		if (annotationArrayInitDetailAST == null) {
			return;
		}

		List<String> osgiCommandScopes = _getProperties(
			annotationArrayInitDetailAST, "osgi.command.scope");

		if (osgiCommandScopes.isEmpty() || (osgiCommandScopes.size() != 1)) {
			return;
		}

		_checkClassName(detailAST, osgiCommandScopes.get(0));

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.METHOD_DEF);

		methodDefinitionDetailASTs = ListUtil.filter(
			methodDefinitionDetailASTs,
			methodDefinitionDetailAST -> {
				DetailAST modifiersDetailAST =
					methodDefinitionDetailAST.findFirstToken(
						TokenTypes.MODIFIERS);

				return modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_PUBLIC);
			});

		List<String> osgiCommandFunctions = _getProperties(
			annotationArrayInitDetailAST, "osgi.command.function");

		_checkIncorrectPublicMethodName(
			methodDefinitionDetailASTs, osgiCommandFunctions);
		_checkMissingUnimplementedMethod(
			detailAST, methodDefinitionDetailASTs, osgiCommandFunctions);

		if (importNames.contains(
				"org.osgi.service.component.annotations.Reference")) {

			_checkVariableDeclaration(objBlockDetailAST);
		}
	}

	private void _checkClassName(DetailAST detailAST, String osgiCommandScope) {
		String className = getName(detailAST);

		if (!StringUtil.equalsIgnoreCase(
				className, osgiCommandScope + "OSGiCommands")) {

			log(detailAST, _MSG_INCORRECT_CLASS_NAME);
		}
	}

	private void _checkIncorrectPublicMethodName(
		List<DetailAST> methodDefinitionDetailASTs,
		List<String> osgiCommandFunctions) {

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			String methodName = getName(methodDefinitionDetailAST);

			if (osgiCommandFunctions.contains(methodName)) {
				continue;
			}

			log(methodDefinitionDetailAST, _MSG_INCORRECT_PUBLIC_METHOD_NAME);
		}
	}

	private void _checkMissingUnimplementedMethod(
		DetailAST detailAST, List<DetailAST> methodDefinitionDetailASTs,
		List<String> osgiCommandFunctions) {

		List<String> methodNames = new ArrayList<>();

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			String methodName = getName(methodDefinitionDetailAST);

			if (methodNames.contains(methodName)) {
				continue;
			}

			methodNames.add(methodName);
		}

		for (String osgiCommandFunction : osgiCommandFunctions) {
			if (methodNames.contains(osgiCommandFunction)) {
				continue;
			}

			log(
				detailAST, _MSG_MISSING_IMPLEMENTED_COMMAND_FUNCTION,
				osgiCommandFunction);
		}
	}

	private void _checkVariableDeclaration(DetailAST detailAST) {
		List<DetailAST> variableDefinitionDetailASTs = getAllChildTokens(
			detailAST, false, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefinitionDetailAST :
				variableDefinitionDetailASTs) {

			if (!AnnotationUtil.containsAnnotation(
					variableDefinitionDetailAST, "Reference")) {

				continue;
			}

			String typeName = getTypeName(variableDefinitionDetailAST, false);

			if (!typeName.endsWith("OSGiCommands")) {
				continue;
			}

			log(variableDefinitionDetailAST, _MSG_AVOID_OSGI_REFERENCE);
		}
	}

	private List<String> _getProperties(DetailAST detailAST, String name) {
		List<String> osgiCommandProperties = new ArrayList<>();

		for (DetailAST expressionDetailAST :
				getAllChildTokens(detailAST, false, TokenTypes.EXPR)) {

			DetailAST firstChildDetailAST = expressionDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String[] property = StringUtil.split(
				StringUtil.unquote(firstChildDetailAST.getText()),
				CharPool.EQUAL);

			if (!property[0].equals(name)) {
				continue;
			}

			osgiCommandProperties.add(property[1]);
		}

		return osgiCommandProperties;
	}

	private static final String _MSG_AVOID_OSGI_REFERENCE =
		"osgi.reference.avoid";

	private static final String _MSG_INCORRECT_CLASS_NAME =
		"class.name.incorrect";

	private static final String _MSG_INCORRECT_PUBLIC_METHOD_NAME =
		"public.method.name.incorrect";

	private static final String _MSG_MISSING_IMPLEMENTED_COMMAND_FUNCTION =
		"implemented.command.function.missing";

}