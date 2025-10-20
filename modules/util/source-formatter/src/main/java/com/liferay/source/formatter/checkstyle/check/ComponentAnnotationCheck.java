/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Simon Jiang
 */
public class ComponentAnnotationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<String> importNames = getImportNames(detailAST);

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST != null) ||
			!importNames.contains(
				"org.osgi.service.component.annotations.Component")) {

			return;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Component");

		if (annotationDetailAST == null) {
			return;
		}

		_checkConfigurationPolicy(detailAST, annotationDetailAST);
		_checkOSGiJaxrsName(annotationDetailAST, importNames);
	}

	private void _checkConfigurationPolicy(
		DetailAST detailAST, DetailAST annotationDetailAST) {

		String extendsClassName = null;

		DetailAST extendsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST != null) {
			DetailAST firstChildDetailAST =
				extendsClauseDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.DOT) {
				FullIdent fullIdent = FullIdent.createFullIdent(
					firstChildDetailAST);

				String[] parts = StringUtil.split(fullIdent.getText(), "\\.");

				extendsClassName = parts[parts.length - 1];
			}
			else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				extendsClassName = getName(extendsClauseDetailAST);
			}
		}

		DetailAST annotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "configurationPolicy");

		if (annotationMemberValuePairDetailAST == null) {
			if (Objects.equals(
					extendsClassName, "BaseAuthVerifierPipelineConfigurator")) {

				log(annotationDetailAST, _MSG_INCORRECT_CONFIGURATION_POLICY);
			}

			return;
		}

		DetailAST expressionDetailAST =
			annotationMemberValuePairDetailAST.findFirstToken(TokenTypes.EXPR);

		FullIdent expressionFullIdent = FullIdent.createFullIdentBelow(
			expressionDetailAST);

		String annotationMemberValue = expressionFullIdent.getText();

		if (Objects.equals(
				extendsClassName, "BaseAuthVerifierPipelineConfigurator") &&
			!annotationMemberValue.equals("ConfigurationPolicy.REQUIRE")) {

			log(annotationDetailAST, _MSG_INCORRECT_CONFIGURATION_POLICY);
		}
		else if (annotationMemberValue.equals("ConfigurationPolicy.OPTIONAL")) {
			log(
				annotationDetailAST, _MSG_UNNECESSARY_CONFIGURATION_POLICY,
				annotationMemberValue);
		}
	}

	private void _checkOSGiJaxrsName(
		DetailAST annotationDetailAST, List<String> importNames) {

		if (!importNames.contains("jakarta.ws.rs.ext.ExceptionMapper") ||
			!_isExceptionMapperService(annotationDetailAST)) {

			return;
		}

		DetailAST propertyAnnotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "property");

		if (propertyAnnotationMemberValuePairDetailAST == null) {
			return;
		}

		DetailAST annotationArrayInitDetailAST =
			propertyAnnotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.ANNOTATION_ARRAY_INIT);

		if (annotationArrayInitDetailAST == null) {
			return;
		}

		String osgiJaxrsName = _getOSGiJaxrsName(annotationArrayInitDetailAST);

		if (Validator.isNull(osgiJaxrsName)) {
			return;
		}

		if (!osgiJaxrsName.endsWith(_OSGI_SERVICE_NAME)) {
			log(
				annotationArrayInitDetailAST, _MSG_INCORRECT_OSGI_JAXRS_MAME,
				_OSGI_SERVICE_NAME);
		}
	}

	private String _getOSGiJaxrsName(DetailAST annotationArrayInitDetailAST) {
		List<DetailAST> expressionDetailASTs = getAllChildTokens(
			annotationArrayInitDetailAST, false, TokenTypes.EXPR);

		for (DetailAST expressionDetailAST : expressionDetailASTs) {
			DetailAST firstChildDetailAST = expressionDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String value = firstChildDetailAST.getText();

			if (value.startsWith("\"osgi.jaxrs.name=")) {
				return value.substring(17, value.length() - 1);
			}
		}

		return null;
	}

	private boolean _isExceptionMapperService(DetailAST annotationDetailAST) {
		DetailAST serviceAnnotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "service");

		if (serviceAnnotationMemberValuePairDetailAST == null) {
			return false;
		}

		DetailAST exprDetailAST =
			serviceAnnotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.EXPR);

		if (exprDetailAST == null) {
			return false;
		}

		DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.DOT)) {

			return false;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		return Objects.equals(
			fullIdent.getText(), _OSGI_SERVICE_NAME + ".class");
	}

	private static final String _MSG_INCORRECT_CONFIGURATION_POLICY =
		"configuration.policy.incorrect";

	private static final String _MSG_INCORRECT_OSGI_JAXRS_MAME =
		"osgi.jaxrs.name.incorrect";

	private static final String _MSG_UNNECESSARY_CONFIGURATION_POLICY =
		"configuration.policy.unnecessary";

	private static final String _OSGI_SERVICE_NAME = "ExceptionMapper";

}