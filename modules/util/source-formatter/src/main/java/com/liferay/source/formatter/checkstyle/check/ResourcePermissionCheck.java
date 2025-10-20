/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Kevin Lee
 */
public class ResourcePermissionCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String className = getName(detailAST);

		if (className.endsWith("ModelResourcePermission")) {
			_checkModelResourcePermission(detailAST);
		}
	}

	private void _checkModelResourcePermission(DetailAST detailAST) {
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

		DetailAST propertyAnnotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "property");

		if (propertyAnnotationMemberValuePairDetailAST == null) {
			if (isAttributeValue(_CHECK_MODEL_CLASS_NAME_KEY)) {
				log(annotationDetailAST, _MSG_MODEL_CLASS_NAME_MISSING);
			}

			return;
		}

		DetailAST annotationArrayInitDetailAST =
			propertyAnnotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.ANNOTATION_ARRAY_INIT);

		List<DetailAST> expressionDetailASTs = new ArrayList<>();

		if (annotationArrayInitDetailAST != null) {
			expressionDetailASTs.addAll(
				getAllChildTokens(
					annotationArrayInitDetailAST, false, TokenTypes.EXPR));
		}
		else {
			expressionDetailASTs.add(
				propertyAnnotationMemberValuePairDetailAST.findFirstToken(
					TokenTypes.EXPR));
		}

		boolean hasModelClassName = false;

		for (DetailAST expressionDetailAST : expressionDetailASTs) {
			DetailAST firstChildDetailAST = expressionDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String value = StringUtil.unquote(firstChildDetailAST.getText());

			if (value.startsWith("service.ranking:")) {
				log(
					expressionDetailAST, _MSG_REMOVE_SERVICE_RANKING_PROPERTY,
					value);
			}
			else if (value.startsWith("model.class.name=") &&
					 isAttributeValue(_CHECK_MODEL_CLASS_NAME_KEY)) {

				hasModelClassName = true;

				String modelClassName = value.substring(
					value.indexOf(CharPool.EQUAL) + 1);

				String modelResourcePermissionTypeArgument =
					_getModelResourcePermissionTypeArgument(detailAST);

				if (!Objects.equals(
						modelClassName, modelResourcePermissionTypeArgument)) {

					log(
						expressionDetailAST, _MSG_MODEL_CLASS_NAME_MISMATCH,
						modelResourcePermissionTypeArgument, modelClassName);
				}
			}
		}

		if (!hasModelClassName &&
			isAttributeValue(_CHECK_MODEL_CLASS_NAME_KEY)) {

			log(annotationDetailAST, _MSG_MODEL_CLASS_NAME_MISSING);
		}
	}

	private String _getModelResourcePermissionTypeArgument(
		DetailAST detailAST) {

		DetailAST implementsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.IMPLEMENTS_CLAUSE);

		for (DetailAST childDetailAST :
				getAllChildTokens(
					implementsClauseDetailAST, false, TokenTypes.IDENT)) {

			if (!Objects.equals(
					childDetailAST.getText(), "ModelResourcePermission")) {

				continue;
			}

			DetailAST typeArgumentsDetailAST = getTypeArgumentsDetailAST(
				childDetailAST);

			if (typeArgumentsDetailAST == null) {
				break;
			}

			return getTypeName(
				typeArgumentsDetailAST.findFirstToken(TokenTypes.TYPE_ARGUMENT),
				false, false, true);
		}

		return null;
	}

	private static final String _CHECK_MODEL_CLASS_NAME_KEY =
		"checkModelClassName";

	private static final String _MSG_MODEL_CLASS_NAME_MISMATCH =
		"model.class.name.mismatch";

	private static final String _MSG_MODEL_CLASS_NAME_MISSING =
		"model.class.name.missing";

	private static final String _MSG_REMOVE_SERVICE_RANKING_PROPERTY =
		"remove.service.ranking.property";

}