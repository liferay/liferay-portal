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
 * @author Alan Huang
 */
public class NestedFieldAnnotationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if ((detailAST.getParent() != null) ||
			!AnnotationUtil.containsAnnotation(detailAST, "Component")) {

			return;
		}

		String className = getName(detailAST);

		if (!className.endsWith("ResourceImpl")) {
			return;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Component");

		String nestedFieldSupportValue = _getNestedFieldSupportValue(
			annotationDetailAST);

		boolean hasNestedFieldAnnotation = _hasNestedFieldAnnotation(
			getAllChildTokens(detailAST, true, TokenTypes.METHOD_DEF));

		if (hasNestedFieldAnnotation &&
			!Objects.equals(nestedFieldSupportValue, "true")) {

			log(annotationDetailAST, _MSG_NESTED_FIELD_SUPPORT_MISSING);
		}
		else if (!hasNestedFieldAnnotation &&
				 (nestedFieldSupportValue != null)) {

			log(annotationDetailAST, _MSG_NESTED_FIELD_SUPPORT_UNNEEDED);
		}
	}

	private String _getNestedFieldSupportValue(DetailAST annotationDetailAST) {
		if (annotationDetailAST == null) {
			return null;
		}

		DetailAST annotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "property");

		if (annotationMemberValuePairDetailAST == null) {
			return null;
		}

		List<DetailAST> expressionDetailASTs = new ArrayList<>();

		DetailAST annotationArrayInitDetailAST =
			annotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.ANNOTATION_ARRAY_INIT);

		if (annotationArrayInitDetailAST != null) {
			expressionDetailASTs.addAll(
				getAllChildTokens(
					annotationArrayInitDetailAST, false, TokenTypes.EXPR));
		}
		else {
			expressionDetailASTs.add(
				annotationMemberValuePairDetailAST.findFirstToken(
					TokenTypes.EXPR));
		}

		for (DetailAST expressionDetailAST : expressionDetailASTs) {
			DetailAST firstChildDetailAST = expressionDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String[] property = StringUtil.split(
				StringUtil.unquote(firstChildDetailAST.getText()),
				CharPool.EQUAL);

			if (property[0].equals("nested.field.support")) {
				return property[1];
			}
		}

		return null;
	}

	private boolean _hasNestedFieldAnnotation(List<DetailAST> detailASTs) {
		for (DetailAST detailAST : detailASTs) {
			DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
				detailAST, "NestedField");

			if (annotationDetailAST == null) {
				continue;
			}

			return true;
		}

		return false;
	}

	private static final String _MSG_NESTED_FIELD_SUPPORT_MISSING =
		"nested.field.support.missing";

	private static final String _MSG_NESTED_FIELD_SUPPORT_UNNEEDED =
		"nested.field.support.unneeded";

}