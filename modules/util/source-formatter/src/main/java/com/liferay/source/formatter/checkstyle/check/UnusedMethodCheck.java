/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public class UnusedMethodCheck extends BaseCheck {

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

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_DEF);

		if (methodDefinitionDetailASTs.isEmpty()) {
			return;
		}

		List<String> allowedMethodNames = getAttributeValues(
			_ALLOWED_METHOD_NAMES_KEY);

		Map<String, Set<Integer>> referencedMethodNamesMap =
			_getReferencedMethodNamesMap(detailAST);

		outerLoop:
		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			DetailAST modifiersDetailAST =
				methodDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

			if (!modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_PRIVATE) ||
				AnnotationUtil.containsAnnotation(methodDefinitionDetailAST) ||
				_hasSuppressUnusedWarningsAnnotation(
					methodDefinitionDetailAST)) {

				continue;
			}

			String name = getName(methodDefinitionDetailAST);

			if (allowedMethodNames.contains(name)) {
				continue;
			}

			DetailAST parametersDetailAST =
				methodDefinitionDetailAST.findFirstToken(TokenTypes.PARAMETERS);

			Set<Integer> parameterCountSet = referencedMethodNamesMap.get(name);

			if (parameterCountSet == null) {
				log(methodDefinitionDetailAST, _MSG_UNUSED_METHOD, name);

				continue;
			}

			if (parameterCountSet.contains(-1)) {
				continue;
			}

			List<DetailAST> parameterDefinitionDetailASTs = getAllChildTokens(
				parametersDetailAST, false, TokenTypes.PARAMETER_DEF);

			int parameterCount = parameterDefinitionDetailASTs.size();

			boolean varArgs = false;

			if (parameterCount > 0) {
				DetailAST lastParameterDefinitionDetailAST =
					parameterDefinitionDetailASTs.get(
						parameterDefinitionDetailASTs.size() - 1);

				if (lastParameterDefinitionDetailAST.branchContains(
						TokenTypes.ELLIPSIS)) {

					varArgs = true;
				}
			}

			if (varArgs) {
				for (int curParameterCount : parameterCountSet) {
					if (curParameterCount >= (parameterCount - 1)) {
						continue outerLoop;
					}
				}

				log(methodDefinitionDetailAST, _MSG_UNUSED_METHOD, name);
			}
			else if (!parameterCountSet.contains(parameterCount)) {
				log(methodDefinitionDetailAST, _MSG_UNUSED_METHOD, name);
			}
		}
	}

	private Map<String, Set<Integer>> _addMapEntry(
		Map<String, Set<Integer>> map, String key, int value) {

		Set<Integer> set = map.get(key);

		if (set == null) {
			set = new HashSet<>();
		}

		set.add(value);

		map.put(key, set);

		return map;
	}

	private Map<String, Set<Integer>> _getReferencedMethodNamesMap(
		DetailAST classDefinitionDetailAST) {

		Map<String, Set<Integer>> referencedMethodNamesMap = new HashMap<>();

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			classDefinitionDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST nameDetailAST = methodCallDetailAST.getFirstChild();

			if (nameDetailAST.getType() == TokenTypes.DOT) {
				nameDetailAST = nameDetailAST.getLastChild();
			}

			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			int parameterCount = 0;

			int childCount = elistDetailAST.getChildCount();

			if (childCount > 0) {
				parameterCount = (childCount + 1) / 2;
			}

			referencedMethodNamesMap = _addMapEntry(
				referencedMethodNamesMap, nameDetailAST.getText(),
				parameterCount);
		}

		List<DetailAST> methodReferenceDetailASTs = getAllChildTokens(
			classDefinitionDetailAST, true, TokenTypes.METHOD_REF);

		for (DetailAST methodReferenceDetailAST : methodReferenceDetailASTs) {
			DetailAST lastChildDetailAST =
				methodReferenceDetailAST.getLastChild();

			referencedMethodNamesMap = _addMapEntry(
				referencedMethodNamesMap, lastChildDetailAST.getText(), -1);
		}

		List<DetailAST> literalNewDetailASTs = getAllChildTokens(
			classDefinitionDetailAST, true, TokenTypes.LITERAL_NEW);

		for (DetailAST literalNewDetailAST : literalNewDetailASTs) {
			DetailAST firstChildDetailAST = literalNewDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.IDENT) ||
				!Objects.equals(firstChildDetailAST.getText(), "MethodKey")) {

				continue;
			}

			DetailAST elistDetailAST = literalNewDetailAST.findFirstToken(
				TokenTypes.ELIST);

			List<DetailAST> exprDetailASTs = getAllChildTokens(
				elistDetailAST, false, TokenTypes.EXPR);

			if (exprDetailASTs.size() < 2) {
				continue;
			}

			DetailAST exprDetailAST = exprDetailASTs.get(1);

			firstChildDetailAST = exprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.STRING_LITERAL) {
				String text = firstChildDetailAST.getText();

				referencedMethodNamesMap = _addMapEntry(
					referencedMethodNamesMap,
					text.substring(1, text.length() - 1),
					exprDetailASTs.size() - 2);
			}
		}

		List<DetailAST> annotationDetailASTs = getAllChildTokens(
			classDefinitionDetailAST, true, TokenTypes.ANNOTATION);

		for (DetailAST annotationDetailAST : annotationDetailASTs) {
			DetailAST atDetailAST = annotationDetailAST.findFirstToken(
				TokenTypes.AT);

			FullIdent fullIdent = FullIdent.createFullIdent(
				atDetailAST.getNextSibling());

			String annotationName = fullIdent.getText();

			if (!annotationName.endsWith("Reference")) {
				continue;
			}

			List<DetailAST> annotationMemberValuePairDetailASTs =
				getAllChildTokens(
					annotationDetailAST, false,
					TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR);

			for (DetailAST annotationMemberValuePairDetailAST :
					annotationMemberValuePairDetailASTs) {

				DetailAST firstChildDetailAST =
					annotationMemberValuePairDetailAST.getFirstChild();

				String propertyName = firstChildDetailAST.getText();

				if (!propertyName.equals("unbind")) {
					continue;
				}

				DetailAST nextSiblingDetailAST =
					firstChildDetailAST.getNextSibling();

				fullIdent = FullIdent.createFullIdentBelow(
					nextSiblingDetailAST.getNextSibling());

				String propertyValueName = fullIdent.getText();

				if (propertyValueName.matches("\".*\"")) {
					referencedMethodNamesMap = _addMapEntry(
						referencedMethodNamesMap,
						propertyValueName.substring(
							1, propertyValueName.length() - 1),
						1);
				}
			}
		}

		return referencedMethodNamesMap;
	}

	private boolean _hasSuppressUnusedWarningsAnnotation(
		DetailAST methodDefinitionDetailAST) {

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			methodDefinitionDetailAST, "SuppressWarnings");

		if (annotationDetailAST == null) {
			return false;
		}

		List<DetailAST> literalStringDetailASTs = getAllChildTokens(
			annotationDetailAST, true, TokenTypes.STRING_LITERAL);

		for (DetailAST literalStringDetailAST : literalStringDetailASTs) {
			String s = literalStringDetailAST.getText();

			if (s.equals("\"unused\"")) {
				return true;
			}
		}

		return false;
	}

	private static final String _ALLOWED_METHOD_NAMES_KEY =
		"allowedMethodNames";

	private static final String _MSG_UNUSED_METHOD = "method.unused";

}