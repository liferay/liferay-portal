/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public abstract class BaseBuilderCheck extends BaseChainedMethodCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.ASSIGN, TokenTypes.INSTANCE_INIT, TokenTypes.METHOD_CALL
		};
	}

	protected abstract boolean allowNullValues();

	protected abstract List<BuilderInformation> doGetBuilderInformationList();

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (isExcludedPath(RUN_OUTSIDE_PORTAL_EXCLUDES) ||
			ListUtil.isEmpty(getAttributeValues(_ENFORCE_BUILDER_NAMES_KEY))) {

			return;
		}

		if (detailAST.getType() == TokenTypes.INSTANCE_INIT) {
			_checkAnonymousClass(detailAST);

			return;
		}

		if (detailAST.getType() == TokenTypes.METHOD_CALL) {
			_checkBuilder(detailAST);

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.EXPR) &&
			(parentDetailAST.getType() != TokenTypes.VARIABLE_DEF)) {

			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		String variableName = getVariableName(detailAST, parentDetailAST);

		if (variableName != null) {
			_checkAssignVariableStatement(
				detailAST, variableName, nextSiblingDetailAST);
		}
	}

	protected abstract String getAssignClassName(DetailAST assignDetailAST);

	protected List<String> getAvoidCastStringMethodNames() {
		return Collections.emptyList();
	}

	protected List<BuilderInformation> getBuilderInformationList() {
		List<BuilderInformation> builderInformationList =
			doGetBuilderInformationList();

		List<String> enforceBuilderNames = getAttributeValues(
			_ENFORCE_BUILDER_NAMES_KEY);

		return ListUtil.filter(
			builderInformationList,
			builderInformation -> enforceBuilderNames.contains(
				builderInformation.getBuilderClassName()));
	}

	protected String getNewInstanceTypeName(DetailAST assignDetailAST) {
		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		DetailAST assignValueDetailAST = null;

		DetailAST parentDetailAST = assignDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			assignValueDetailAST = firstChildDetailAST.getNextSibling();
		}
		else {
			assignValueDetailAST = firstChildDetailAST.getFirstChild();
		}

		if ((assignValueDetailAST == null) ||
			(assignValueDetailAST.getType() != TokenTypes.LITERAL_NEW)) {

			return null;
		}

		return getName(assignValueDetailAST);
	}

	protected Map<String, String[][]> getReservedKeywordsMap() {
		return Collections.emptyMap();
	}

	protected abstract List<String> getSupportsFunctionMethodNames();

	protected abstract boolean isSupportsNestedMethodCalls();

	protected static class BuilderInformation {

		public BuilderInformation(
			String className, String builderClassName, String... methodNames) {

			_className = className;
			_builderClassName = builderClassName;
			_methodNames = methodNames;
		}

		public String getBuilderClassName() {
			return _builderClassName;
		}

		public String getClassName() {
			return _className;
		}

		public String[] getMethodNames() {
			return _methodNames;
		}

		private final String _builderClassName;
		private final String _className;
		private final String[] _methodNames;

	}

	private List<int[]> _addNonfinalVariableRangeList(
		List<int[]> nonfinalVariableRangeList, DetailAST detailAST) {

		if (nonfinalVariableRangeList == null) {
			nonfinalVariableRangeList = new ArrayList<>();
		}

		List<String> variableNames = _getVariableNames(detailAST);

		for (String variableName : variableNames) {
			DetailAST variableTypeDetailAST = getVariableTypeDetailAST(
				detailAST, variableName);

			if (variableTypeDetailAST == null) {
				String[] lines = getLines();

				nonfinalVariableRangeList.add(new int[] {0, lines.length});

				continue;
			}

			DetailAST variableDefinitionDetailAST =
				variableTypeDetailAST.getParent();

			DetailAST parentDetailAST = variableDefinitionDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.OBJBLOCK) {
				if (AnnotationUtil.containsAnnotation(
						variableDefinitionDetailAST, "Reference")) {

					continue;
				}

				DetailAST modifiersDetailAST =
					variableDefinitionDetailAST.findFirstToken(
						TokenTypes.MODIFIERS);

				if ((modifiersDetailAST == null) ||
					!modifiersDetailAST.branchContains(TokenTypes.FINAL)) {

					nonfinalVariableRangeList.add(
						new int[] {
							getStartLineNumber(parentDetailAST),
							getEndLineNumber(parentDetailAST)
						});
				}

				continue;
			}

			boolean isFinal = true;

			int start = getStartLineNumber(variableDefinitionDetailAST);
			int end = getEndLineNumber(variableDefinitionDetailAST);

			List<DetailAST> variableCallerDetailASTs =
				getVariableCallerDetailASTs(
					variableDefinitionDetailAST, variableName);

			for (DetailAST variableCallerDetailAST : variableCallerDetailASTs) {
				parentDetailAST = variableCallerDetailAST.getParent();

				if ((parentDetailAST.getType() == TokenTypes.ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.BAND_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.BOR_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.BXOR_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.DEC) ||
					(parentDetailAST.getType() == TokenTypes.DIV_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.INC) ||
					(parentDetailAST.getType() == TokenTypes.MINUS_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.MOD_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.PLUS_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.POST_DEC) ||
					(parentDetailAST.getType() == TokenTypes.POST_INC) ||
					(parentDetailAST.getType() == TokenTypes.SL_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.SR_ASSIGN) ||
					(parentDetailAST.getType() == TokenTypes.STAR_ASSIGN)) {

					isFinal = false;
				}

				end = Math.max(end, getEndLineNumber(variableCallerDetailAST));
			}

			if (!isFinal) {
				nonfinalVariableRangeList.add(new int[] {start, end});
			}
		}

		return nonfinalVariableRangeList;
	}

	private void _checkAnonymousClass(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.LITERAL_NEW) {
			return;
		}

		String className = getName(parentDetailAST);

		if (className == null) {
			return;
		}

		BuilderInformation builderInformation =
			_findBuilderInformationByClassName(className);

		if (builderInformation == null) {
			return;
		}

		List<DetailAST> childDetailASTs = getAllChildTokens(
			detailAST, true, ALL_TYPES);

		for (DetailAST childDetailAST : childDetailASTs) {
			if (getHiddenBefore(childDetailAST) != null) {
				return;
			}
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			parentDetailAST = methodCallDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.SLIST) {
				continue;
			}

			DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			if (!ArrayUtil.contains(
					builderInformation.getMethodNames(),
					firstChildDetailAST.getText())) {

				return;
			}

			if (isSupportsNestedMethodCalls()) {
				parentDetailAST = getParentWithTokenType(
					methodCallDetailAST, TokenTypes.DO_WHILE, TokenTypes.LAMBDA,
					TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_TRY,
					TokenTypes.LITERAL_WHILE);

				if ((parentDetailAST != null) &&
					(detailAST.getLineNo() <= parentDetailAST.getLineNo())) {

					return;
				}

				parentDetailAST = getParentWithTokenType(
					methodCallDetailAST, TokenTypes.LITERAL_ELSE);

				if ((parentDetailAST != null) &&
					(detailAST.getLineNo() <= parentDetailAST.getLineNo())) {

					firstChildDetailAST = parentDetailAST.getFirstChild();

					if (firstChildDetailAST.getType() ==
							TokenTypes.LITERAL_IF) {

						return;
					}
				}
			}
			else if (!equals(parentDetailAST.getParent(), detailAST)) {
				return;
			}

			if (!allowNullValues()) {
				DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
					TokenTypes.ELIST);

				DetailAST childDetailAST = elistDetailAST.getFirstChild();

				while (true) {
					if (childDetailAST == null) {
						break;
					}

					if (_isNullValueExpression(childDetailAST)) {
						return;
					}

					childDetailAST = childDetailAST.getNextSibling();
				}
			}
		}

		log(
			detailAST, _MSG_USE_BUILDER_INSTEAD,
			builderInformation.getBuilderClassName(), className);
	}

	private void _checkAssignVariableStatement(
		DetailAST assignDetailAST, String variableName,
		DetailAST nextSiblingDetailAST) {

		BuilderInformation builderInformation =
			_findBuilderInformationByClassName(
				getAssignClassName(assignDetailAST));

		if (builderInformation == null) {
			return;
		}

		while (true) {
			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				hasPrecedingPlaceholder(nextSiblingDetailAST)) {

				return;
			}

			FullIdent fullIdent = getMethodCallFullIdent(
				nextSiblingDetailAST, variableName,
				builderInformation.getMethodNames());

			if (fullIdent != null) {
				DetailAST methodCallDetailAST =
					nextSiblingDetailAST.findFirstToken(TokenTypes.METHOD_CALL);

				DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
					TokenTypes.ELIST);

				DetailAST childDetailAST = elistDetailAST.getFirstChild();

				while (true) {
					if (childDetailAST == null) {
						log(
							assignDetailAST, _MSG_USE_BUILDER,
							builderInformation.getBuilderClassName(),
							assignDetailAST.getLineNo(), fullIdent.getLineNo());

						return;
					}

					if ((!allowNullValues() &&
						 _isNullValueExpression(childDetailAST)) ||
						containsVariableName(
							childDetailAST, variableName, true)) {

						return;
					}

					childDetailAST = childDetailAST.getNextSibling();
				}
			}

			if (containsVariableName(
					nextSiblingDetailAST, variableName, true)) {

				return;
			}
		}
	}

	private void _checkBuilder(DetailAST methodCallDetailAST) {
		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String builderClassName = firstChildDetailAST.getText();

		BuilderInformation builderInformation =
			_findBuilderInformationByBuilderClassName(builderClassName);

		if (builderInformation == null) {
			return;
		}

		_checkUnneededCastString(methodCallDetailAST);

		Map<String, List<DetailAST>> expressionDetailASTMap =
			_getExpressionDetailASTMap(methodCallDetailAST, false);

		if (!allowNullValues()) {
			_checkNullValues(expressionDetailASTMap, builderClassName);
		}

		_checkReservedKeywords(expressionDetailASTMap);

		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		int endLineNumber = getEndLineNumber(parentDetailAST);
		int startLineNumber = getStartLineNumber(parentDetailAST);

		while ((parentDetailAST.getType() == TokenTypes.DOT) ||
			   (parentDetailAST.getType() == TokenTypes.EXPR) ||
			   (parentDetailAST.getType() == TokenTypes.METHOD_CALL)) {

			endLineNumber = getEndLineNumber(parentDetailAST);
			startLineNumber = getStartLineNumber(parentDetailAST);

			parentDetailAST = parentDetailAST.getParent();
		}

		if (parentDetailAST.getType() == TokenTypes.ELIST) {
			while (true) {
				DetailAST grandParentDetailAST = parentDetailAST.getParent();

				if (grandParentDetailAST == null) {
					return;
				}

				if (grandParentDetailAST.getType() == TokenTypes.SLIST) {
					_checkInline(
						parentDetailAST, expressionDetailASTMap,
						builderClassName, startLineNumber, endLineNumber);

					return;
				}

				parentDetailAST = grandParentDetailAST;
			}
		}

		if (parentDetailAST.getType() == TokenTypes.LITERAL_RETURN) {
			_checkInline(
				parentDetailAST, expressionDetailASTMap, builderClassName,
				startLineNumber, endLineNumber);

			return;
		}

		if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
			return;
		}

		DetailAST assignDetailAST = parentDetailAST;

		parentDetailAST = assignDetailAST.getParent();

		DetailAST grandParentDetailAST = parentDetailAST.getParent();

		if (grandParentDetailAST.getType() == TokenTypes.OBJBLOCK) {
			DetailAST greatGrandParentDetailAST =
				grandParentDetailAST.getParent();

			if (greatGrandParentDetailAST.getType() == TokenTypes.CLASS_DEF) {
				return;
			}
		}

		_checkInline(
			parentDetailAST, expressionDetailASTMap, builderClassName,
			startLineNumber, endLineNumber);

		if (isJSPFile()) {
			return;
		}

		String variableName = getVariableName(assignDetailAST, parentDetailAST);

		_checkBuildString(
			methodCallDetailAST, assignDetailAST, variableName, startLineNumber,
			endLineNumber);

		_checkInlineIfStatement(
			parentDetailAST, builderClassName, variableName,
			firstChildDetailAST.getLineNo());

		firstChildDetailAST = assignDetailAST.getFirstChild();

		DetailAST assignValueDetailAST = null;

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			assignValueDetailAST = firstChildDetailAST.getNextSibling();
		}
		else {
			assignValueDetailAST = firstChildDetailAST.getFirstChild();
		}

		if ((assignValueDetailAST == null) ||
			(assignValueDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return;
		}

		List<String> variableNames = _getVariableNames(
			parentDetailAST, "get.*");

		String[] builderMethodNames = builderInformation.getMethodNames();

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		while (true) {
			if (nextSiblingDetailAST == null) {
				return;
			}

			FullIdent fullIdent = getMethodCallFullIdent(
				nextSiblingDetailAST, variableName, builderMethodNames);

			if (fullIdent != null) {
				if (_hasVariableNameInMethodCall(
						nextSiblingDetailAST.findFirstToken(
							TokenTypes.METHOD_CALL),
						variableName)) {

					return;
				}

				log(
					assignDetailAST, _MSG_INCLUDE_BUILDER, fullIdent.getText(),
					fullIdent.getLineNo(), builderClassName,
					assignDetailAST.getLineNo());

				return;
			}

			if (containsVariableName(
					nextSiblingDetailAST, variableName, false)) {

				return;
			}

			for (String curVariableName : variableNames) {
				if (containsVariableName(
						nextSiblingDetailAST, curVariableName, true)) {

					return;
				}
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();
		}
	}

	private void _checkBuildString(
		DetailAST methodCallDetailAST, DetailAST assignDetailAST,
		String variableName, int startLineNumber, int endLineNumber) {

		Class<?> clazz = getClass();

		String className = clazz.getName();

		if (!className.endsWith("URLBuilderCheck")) {
			return;
		}

		List<String> chainedMethodNames = getChainedMethodNames(
			methodCallDetailAST);

		String methodName = chainedMethodNames.get(
			chainedMethodNames.size() - 1);

		if (methodName.equals("buildString")) {
			return;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			assignDetailAST, variableName, false);

		if (variableDefinitionDetailAST == null) {
			return;
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			variableDefinitionDetailAST, variableName);

		DetailAST lastDetailAST = variableCallerDetailASTs.get(
			variableCallerDetailASTs.size() - 1);

		if (lastDetailAST.getLineNo() <= endLineNumber) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(
			lastDetailAST.getParent());

		if (!Objects.equals(fullIdent.getText(), variableName + ".toString")) {
			return;
		}

		if (variableCallerDetailASTs.size() > 1) {
			DetailAST secondToLastDetailAST = variableCallerDetailASTs.get(
				variableCallerDetailASTs.size() - 2);

			if (secondToLastDetailAST.getLineNo() > startLineNumber) {
				return;
			}
		}

		if (equals(variableDefinitionDetailAST, assignDetailAST.getParent()) ||
			equals(
				getParentWithTokenType(assignDetailAST, TokenTypes.SLIST),
				getParentWithTokenType(lastDetailAST, TokenTypes.SLIST))) {

			log(
				fullIdent.getLineNo(), _MSG_USE_BUILD_STRING, variableName,
				methodName);
		}
	}

	private void _checkInline(
		DetailAST variableDefinitionDetailAST, DetailAST parentDetailAST,
		String builderClassName, List<String> supportsFunctionMethodNames,
		Map<String, List<DetailAST>> expressionDetailASTMap,
		int startLineNumber, int endLineNumber) {

		DetailAST identDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		String matchingMethodName = _getInlineExpressionMethodName(
			expressionDetailASTMap, ListUtil.fromArray(identDetailAST));

		if (!supportsFunctionMethodNames.contains(matchingMethodName)) {
			return;
		}

		List<DetailAST> dependentIdentDetailASTs = getDependentIdentDetailASTs(
			variableDefinitionDetailAST, startLineNumber);

		if (dependentIdentDetailASTs.isEmpty()) {
			return;
		}

		List<int[]> nonfinalVariableRangeList = _addNonfinalVariableRangeList(
			null, variableDefinitionDetailAST);

		String variableName = identDetailAST.getText();

		for (int i = dependentIdentDetailASTs.size() - 1; i >= 0; i--) {
			DetailAST dependentIdentDetailAST = dependentIdentDetailASTs.get(i);

			if (variableName.equals(dependentIdentDetailAST.getText()) &&
				(dependentIdentDetailAST.getLineNo() > endLineNumber)) {

				return;
			}
		}

		matchingMethodName = _getInlineExpressionMethodName(
			expressionDetailASTMap, dependentIdentDetailASTs);

		if (matchingMethodName == null) {
			return;
		}

		DetailAST literalTryDetailAST = getParentWithTokenType(
			variableDefinitionDetailAST, TokenTypes.LITERAL_TRY);

		if (literalTryDetailAST != null) {
			DetailAST literalCatchDetailAST = getParentWithTokenType(
				variableDefinitionDetailAST, TokenTypes.LITERAL_CATCH);

			if ((literalCatchDetailAST == null) ||
				(literalCatchDetailAST.getLineNo() <
					literalTryDetailAST.getLineNo())) {

				return;
			}
		}

		List<DetailAST> additionalDependentDetailASTs =
			_getAdditionalDependentDetailASTs(
				dependentIdentDetailASTs,
				variableDefinitionDetailAST.getLineNo(), startLineNumber);

		if (additionalDependentDetailASTs.isEmpty()) {
			if (!_hasNonfinalVariableReference(
					nonfinalVariableRangeList,
					getStartLineNumber(variableDefinitionDetailAST),
					getStartLineNumber(variableDefinitionDetailAST))) {

				log(
					identDetailAST, _MSG_INLINE_BUILDER_1,
					identDetailAST.getText(), identDetailAST.getLineNo(),
					builderClassName, startLineNumber);
			}

			return;
		}

		DetailAST lastAdditionalDependentDetailAST =
			additionalDependentDetailASTs.get(
				additionalDependentDetailASTs.size() - 1);

		if (lastAdditionalDependentDetailAST.getLineNo() >=
				parentDetailAST.getLineNo()) {

			return;
		}

		for (DetailAST additionalDependentDetailAST :
				additionalDependentDetailASTs) {

			List<DetailAST> assignDetailASTs = getAllChildTokens(
				additionalDependentDetailAST, true, TokenTypes.ASSIGN);

			for (DetailAST assignDetailAST : assignDetailASTs) {
				String assignVariableName = null;

				DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
					assignVariableName = firstChildDetailAST.getText();
				}
				else {
					DetailAST previousSiblingDetailAST =
						assignDetailAST.getPreviousSibling();

					if ((previousSiblingDetailAST != null) &&
						(previousSiblingDetailAST.getType() ==
							TokenTypes.IDENT)) {

						assignVariableName = previousSiblingDetailAST.getText();
					}
				}

				if (assignVariableName == null) {
					return;
				}

				if (!variableName.equals(assignVariableName)) {
					for (int i = dependentIdentDetailASTs.size() - 1; i >= 0;
						 i--) {

						DetailAST dependentIdentDetailAST =
							dependentIdentDetailASTs.get(i);

						if (assignVariableName.equals(
								dependentIdentDetailAST.getText()) &&
							(dependentIdentDetailAST.getLineNo() >
								endLineNumber)) {

							return;
						}
					}
				}

				nonfinalVariableRangeList = _addNonfinalVariableRangeList(
					nonfinalVariableRangeList, additionalDependentDetailAST);
			}

			if (_hasNonfinalVariableReference(
					nonfinalVariableRangeList,
					getStartLineNumber(variableDefinitionDetailAST),
					getEndLineNumber(lastAdditionalDependentDetailAST))) {

				return;
			}

			List<DetailAST> methodCallDetailASTs = getAllChildTokens(
				additionalDependentDetailAST, true, TokenTypes.METHOD_CALL);

			for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
				DetailAST firstChildDetailAST =
					methodCallDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() == TokenTypes.DOT) {
					FullIdent fullIdent = FullIdent.createFullIdent(
						firstChildDetailAST);

					String methodCall = fullIdent.getText();

					if (!methodCall.startsWith(variableName + ".") &&
						!methodCall.contains(".get")) {

						return;
					}
				}
				else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
					String methodName = firstChildDetailAST.getText();

					if (!methodName.matches("_?get.*")) {
						return;
					}
				}
				else {
					return;
				}
			}
		}

		log(
			identDetailAST, _MSG_INLINE_BUILDER_2, variableName,
			identDetailAST.getLineNo(),
			_getLineNumbers(additionalDependentDetailASTs), builderClassName,
			startLineNumber);
	}

	private void _checkInline(
		DetailAST parentDetailAST,
		Map<String, List<DetailAST>> expressionDetailASTMap,
		String builderClassName, int startLineNumber, int endLineNumber) {

		if (!isAttributeValue(_CHECK_INLINE)) {
			return;
		}

		List<String> supportsFunctionMethodNames =
			getSupportsFunctionMethodNames();

		if (supportsFunctionMethodNames.isEmpty()) {
			return;
		}

		int branchStatementLineNumber = -1;

		List<DetailAST> branchingStatementDetailASTs = getAllChildTokens(
			parentDetailAST.getParent(), true, TokenTypes.LITERAL_BREAK,
			TokenTypes.LITERAL_CONTINUE, TokenTypes.LITERAL_RETURN);

		for (DetailAST branchingStatementDetailAST :
				branchingStatementDetailASTs) {

			int lineNumber = branchingStatementDetailAST.getLineNo();

			if (lineNumber >= startLineNumber) {
				break;
			}

			branchStatementLineNumber = lineNumber;
		}

		List<DetailAST> variableDefinitionDetailASTs = getAllChildTokens(
			parentDetailAST.getParent(), false, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefinitionDetailAST :
				variableDefinitionDetailASTs) {

			int lineNumber = variableDefinitionDetailAST.getLineNo();

			if (lineNumber >= startLineNumber) {
				return;
			}

			if (branchStatementLineNumber < lineNumber) {
				_checkInline(
					variableDefinitionDetailAST, parentDetailAST,
					builderClassName, supportsFunctionMethodNames,
					expressionDetailASTMap, startLineNumber, endLineNumber);
			}
		}
	}

	private void _checkInlineIfStatement(
		DetailAST parentDetailAST, String builderClassName, String variableName,
		int lineNumber) {

		List<String> supportsFunctionMethodNames =
			getSupportsFunctionMethodNames();

		if (supportsFunctionMethodNames.isEmpty()) {
			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() != TokenTypes.SEMI) {
			return;
		}

		DetailAST detailAST = nextSiblingDetailAST.getNextSibling();

		int rangeLineNumber = -1;
		int startLineNumber = -1;

		List<int[]> nonfinalVariableRangeList = new ArrayList<>();

		while (true) {
			if (detailAST.getType() == TokenTypes.SEMI) {
				detailAST = detailAST.getNextSibling();

				continue;
			}

			DetailAST variableDefinitionDetailAST = null;

			if (detailAST.getType() == TokenTypes.EXPR) {
				DetailAST firstChildDetailAST = detailAST.getFirstChild();

				if (firstChildDetailAST.getType() != TokenTypes.ASSIGN) {
					return;
				}

				String name = getName(firstChildDetailAST);

				if (name == null) {
					return;
				}

				variableDefinitionDetailAST = getVariableDefinitionDetailAST(
					detailAST, name, false);
			}
			else if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
				variableDefinitionDetailAST = detailAST;
			}

			if (variableDefinitionDetailAST == null) {
				break;
			}

			List<DetailAST> variableCallerDetailASTs =
				getVariableCallerDetailASTs(variableDefinitionDetailAST);

			if (variableCallerDetailASTs.isEmpty()) {
				return;
			}

			nonfinalVariableRangeList = _addNonfinalVariableRangeList(
				nonfinalVariableRangeList, variableDefinitionDetailAST);

			DetailAST lastDetailAST = variableCallerDetailASTs.get(
				variableCallerDetailASTs.size() - 1);

			if (startLineNumber == -1) {
				startLineNumber = getStartLineNumber(detailAST);
			}

			rangeLineNumber = Math.max(
				rangeLineNumber, lastDetailAST.getLineNo());

			detailAST = detailAST.getNextSibling();
		}

		List<DetailAST> slistDetailASTs = new ArrayList<>();

		int endLineNumber = -1;

		while (true) {
			if ((detailAST == null) ||
				((detailAST.getType() != TokenTypes.LITERAL_ELSE) &&
				 (detailAST.getType() != TokenTypes.LITERAL_IF))) {

				break;
			}

			if (startLineNumber == -1) {
				startLineNumber = getStartLineNumber(detailAST);
			}

			if (endLineNumber == -1) {
				nonfinalVariableRangeList = _addNonfinalVariableRangeList(
					nonfinalVariableRangeList, detailAST);

				endLineNumber = getEndLineNumber(detailAST);

				if (rangeLineNumber > endLineNumber) {
					return;
				}
			}

			if (detailAST.getType() == TokenTypes.LITERAL_ELSE) {
				DetailAST firstChildDetailAST = detailAST.getFirstChild();

				if (firstChildDetailAST.getType() == TokenTypes.SLIST) {
					slistDetailASTs.add(firstChildDetailAST);

					break;
				}

				if (firstChildDetailAST.getType() != TokenTypes.LITERAL_IF) {
					return;
				}

				detailAST = firstChildDetailAST;
			}

			DetailAST slistDetailAST = detailAST.findFirstToken(
				TokenTypes.SLIST);

			if (slistDetailAST == null) {
				return;
			}

			slistDetailASTs.add(slistDetailAST);

			detailAST = slistDetailAST.getNextSibling();
		}

		if (slistDetailASTs.isEmpty()) {
			return;
		}

		String methodKey = null;

		for (DetailAST slistDetailAST : slistDetailASTs) {
			String curMethodKey = _getMethodKey(
				slistDetailAST, variableName, supportsFunctionMethodNames);

			if (curMethodKey == null) {
				return;
			}

			if (methodKey == null) {
				methodKey = curMethodKey;

				continue;
			}

			if (!methodKey.equals(curMethodKey)) {
				return;
			}
		}

		if (!_hasNonfinalVariableReference(
				nonfinalVariableRangeList, startLineNumber, endLineNumber)) {

			log(
				startLineNumber, _MSG_INLINE_IF_STATEMENT, builderClassName,
				lineNumber);
		}
	}

	private void _checkNullValues(
		Map<String, List<DetailAST>> expressionDetailASTMap,
		String builderClassName) {

		for (Map.Entry<String, List<DetailAST>> entry :
				expressionDetailASTMap.entrySet()) {

			for (DetailAST expressionDetailAST : entry.getValue()) {
				if (_isNullValueExpression(expressionDetailAST)) {
					log(
						expressionDetailAST, _MSG_INCORRECT_NULL_VALUE,
						builderClassName);
				}
			}
		}
	}

	private void _checkReservedKeywords(
		Map<String, List<DetailAST>> expressionDetailASTMap) {

		Map<String, String[][]> reservedKeywordsMap = getReservedKeywordsMap();

		for (Map.Entry<String, String[][]> entry :
				reservedKeywordsMap.entrySet()) {

			String methodName = entry.getKey();

			List<DetailAST> expressionDetailASTs = expressionDetailASTMap.get(
				methodName);

			if (expressionDetailASTs == null) {
				continue;
			}

			for (DetailAST expressionDetailAST : expressionDetailASTs) {
				DetailAST previousDetailAST =
					expressionDetailAST.getPreviousSibling();

				if (previousDetailAST != null) {
					continue;
				}

				DetailAST firstChildDetailAST =
					expressionDetailAST.getFirstChild();

				String value = null;

				if (firstChildDetailAST.getType() ==
						TokenTypes.STRING_LITERAL) {

					value = StringUtil.removeChar(
						firstChildDetailAST.getText(), CharPool.QUOTE);
				}
				else if (firstChildDetailAST.getType() == TokenTypes.DOT) {
					FullIdent fullIdent = FullIdent.createFullIdent(
						firstChildDetailAST);

					value = fullIdent.getText();
				}
				else {
					continue;
				}

				String[][] reservedKeywordsArray = entry.getValue();

				for (String[] reservedKeywordArray : reservedKeywordsArray) {
					String reservedKey = reservedKeywordArray[0];

					if (value.equals(reservedKey)) {
						log(
							expressionDetailAST, _MSG_RESERVED_KEYWORD,
							methodName, value, reservedKeywordArray[1]);

						break;
					}
				}
			}
		}
	}

	private void _checkUnneededCastString(DetailAST methodCallDetailAST) {
		List<String> avoidCastStringMethodNames =
			getAvoidCastStringMethodNames();

		if (avoidCastStringMethodNames.isEmpty()) {
			return;
		}

		Map<String, List<DetailAST>> expressionDetailASTMap =
			_getExpressionDetailASTMap(methodCallDetailAST, true);

		for (String avoidCastStringMethodName :
				getAvoidCastStringMethodNames()) {

			List<DetailAST> expressionDetailASTs = expressionDetailASTMap.get(
				avoidCastStringMethodName);

			if (expressionDetailASTs == null) {
				continue;
			}

			for (DetailAST expressionDetailAST : expressionDetailASTs) {
				if (expressionDetailAST.getType() != TokenTypes.EXPR) {
					continue;
				}

				DetailAST childDetailAST = expressionDetailAST.getFirstChild();

				if (childDetailAST.getType() == TokenTypes.METHOD_CALL) {
					FullIdent fullIdent = FullIdent.createFullIdentBelow(
						childDetailAST);

					String methodCall = fullIdent.getText();

					if (methodCall.equals("String.valueOf") ||
						methodCall.endsWith(".toString")) {

						log(
							expressionDetailAST, _MSG_UNNEEDED_STRING_CAST,
							avoidCastStringMethodName);
					}
				}
			}
		}
	}

	private BuilderInformation _findBuilderInformationByBuilderClassName(
		String builderClassName) {

		for (BuilderInformation builderInformation :
				getBuilderInformationList()) {

			if (builderClassName.equals(
					builderInformation.getBuilderClassName())) {

				return builderInformation;
			}
		}

		return null;
	}

	private BuilderInformation _findBuilderInformationByClassName(
		String className) {

		if (className == null) {
			return null;
		}

		for (BuilderInformation builderInformation :
				getBuilderInformationList()) {

			if (className.equals(builderInformation.getClassName())) {
				return builderInformation;
			}
		}

		return null;
	}

	private List<DetailAST> _getAdditionalDependentDetailASTs(
		List<DetailAST> dependentIdentDetailASTs, int startLineNumber,
		int endLineNumber) {

		List<DetailAST> dependentDetailASTs = new ArrayList<>();

		for (DetailAST dependentIdentDetailAST : dependentIdentDetailASTs) {
			if (dependentIdentDetailAST.getLineNo() >= endLineNumber) {
				return dependentDetailASTs;
			}

			DetailAST detailAST = dependentIdentDetailAST;

			while (true) {
				DetailAST parentDetailAST = detailAST.getParent();

				if (parentDetailAST.getLineNo() < startLineNumber) {
					if (!dependentDetailASTs.contains(detailAST)) {
						dependentDetailASTs.add(detailAST);
					}

					break;
				}

				detailAST = parentDetailAST;
			}
		}

		return dependentDetailASTs;
	}

	private Map<String, List<DetailAST>> _getExpressionDetailASTMap(
		DetailAST methodCallDetailAST, boolean lastExpressionOnly) {

		Map<String, List<DetailAST>> expressionDetailASTMap = new HashMap<>();

		while (true) {
			String methodName = getMethodName(methodCallDetailAST);

			List<DetailAST> expressionDetailASTs = expressionDetailASTMap.get(
				methodName);

			if (expressionDetailASTs == null) {
				expressionDetailASTs = new ArrayList<>();
			}

			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			if (lastExpressionOnly) {
				DetailAST childDetailAST = elistDetailAST.getLastChild();

				if (childDetailAST != null) {
					expressionDetailASTs.add(childDetailAST);
				}
			}
			else {
				DetailAST childDetailAST = elistDetailAST.getFirstChild();

				while (true) {
					if (childDetailAST == null) {
						break;
					}

					if (childDetailAST.getType() != TokenTypes.COMMA) {
						expressionDetailASTs.add(childDetailAST);
					}

					childDetailAST = childDetailAST.getNextSibling();
				}
			}

			if (!expressionDetailASTs.isEmpty()) {
				expressionDetailASTMap.put(methodName, expressionDetailASTs);
			}

			DetailAST parentDetailAST = methodCallDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.DOT) {
				return expressionDetailASTMap;
			}

			methodCallDetailAST = parentDetailAST.getParent();
		}
	}

	private String _getInlineExpressionMethodName(
		Map<String, List<DetailAST>> expressionDetailASTMap,
		List<DetailAST> dependentIdentDetailASTs) {

		String methodName = null;

		for (Map.Entry<String, List<DetailAST>> entry :
				expressionDetailASTMap.entrySet()) {

			for (DetailAST expressionDetailAST : entry.getValue()) {
				List<String> variableNames = _getVariableNames(
					expressionDetailAST);

				for (DetailAST dependentIdentDetailAST :
						dependentIdentDetailASTs) {

					if (variableNames.contains(
							dependentIdentDetailAST.getText())) {

						List<int[]> nonfinalVariableRangeList =
							_addNonfinalVariableRangeList(
								null, expressionDetailAST);

						for (int[] array : nonfinalVariableRangeList) {
							if (expressionDetailAST.getLineNo() > array[0]) {
								return null;
							}
						}

						if (methodName != null) {
							return null;
						}

						methodName = entry.getKey();

						break;
					}
				}
			}
		}

		return methodName;
	}

	private String _getLineNumbers(List<DetailAST> detailASTs) {
		StringBundler sb = new StringBundler(detailASTs.size() * 2);

		for (DetailAST detailAST : detailASTs) {
			sb.append(detailAST.getLineNo());
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private String _getMethodKey(
		DetailAST slistDetailAST, String variableName,
		List<String> supportsFunctionMethodNames) {

		DetailAST lastChildDetailAST = slistDetailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.RCURLY) {
			return null;
		}

		DetailAST previousSiblingDetailAST =
			lastChildDetailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return null;
		}

		previousSiblingDetailAST =
			previousSiblingDetailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.EXPR)) {

			return null;
		}

		DetailAST methodCallDetailAST =
			previousSiblingDetailAST.getFirstChild();

		if (methodCallDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return null;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if (elistDetailAST == null) {
			return null;
		}

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		for (DetailAST exprDetailAST : exprDetailASTs) {
			DetailAST exprChildDetailAST = exprDetailAST.getFirstChild();

			if (exprChildDetailAST.getType() == TokenTypes.LITERAL_NULL) {
				return null;
			}
		}

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST.getType() != TokenTypes.IDENT) ||
			!variableName.equals(firstChildDetailAST.getText())) {

			return null;
		}

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() != TokenTypes.IDENT) {
			return null;
		}

		String methodName = nextSiblingDetailAST.getText();

		if (!supportsFunctionMethodNames.contains(methodName)) {
			return null;
		}

		String methodKey = methodName;

		if (exprDetailASTs.size() > 1) {
			FullIdent fullIdent = FullIdent.createFullIdentBelow(
				exprDetailASTs.get(0));

			methodKey += ":" + fullIdent.getText();
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST curMethodCallDetailAST : methodCallDetailASTs) {
			if (equals(curMethodCallDetailAST, methodCallDetailAST)) {
				continue;
			}

			firstChildDetailAST = curMethodCallDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (variableName.equals(firstChildDetailAST.getText())) {
				return null;
			}
		}

		return methodKey;
	}

	private List<String> _getVariableNames(DetailAST detailAST) {
		return _getVariableNames(detailAST, null);
	}

	private List<String> _getVariableNames(
		DetailAST detailAST, String excludeRegex) {

		List<String> variableNames = new ArrayList<>();

		List<DetailAST> identDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.IDENT);

		for (DetailAST identDetailAST : identDetailASTs) {
			String variableName = identDetailAST.getText();

			if (!variableName.matches("[a-z_].*")) {
				continue;
			}

			DetailAST parentDetailAST = identDetailAST.getParent();

			if ((parentDetailAST.getType() == TokenTypes.ASSIGN) ||
				(parentDetailAST.getType() == TokenTypes.EXPR) ||
				(parentDetailAST.getType() == TokenTypes.TYPECAST) ||
				ArrayUtil.contains(
					ARITHMETIC_OPERATOR_TOKEN_TYPES,
					parentDetailAST.getType()) ||
				ArrayUtil.contains(
					CONDITIONAL_OPERATOR_TOKEN_TYPES,
					parentDetailAST.getType()) ||
				ArrayUtil.contains(
					RELATIONAL_OPERATOR_TOKEN_TYPES,
					parentDetailAST.getType()) ||
				ArrayUtil.contains(
					UNARY_OPERATOR_TOKEN_TYPES, parentDetailAST.getType())) {

				variableNames.add(variableName);
			}
			else if (parentDetailAST.getType() == TokenTypes.DOT) {
				DetailAST nextSiblingDetailAST =
					identDetailAST.getNextSibling();

				if (nextSiblingDetailAST != null) {
					if ((nextSiblingDetailAST.getType() != TokenTypes.IDENT) ||
						(excludeRegex == null)) {

						variableNames.add(variableName);
					}
					else {
						String s = nextSiblingDetailAST.getText();

						if (!s.matches(excludeRegex)) {
							variableNames.add(variableName);
						}
					}
				}
			}
		}

		return variableNames;
	}

	private boolean _hasNonfinalVariableReference(
		List<int[]> nonfinalVariableRangeList, int start, int end) {

		for (int[] range : nonfinalVariableRangeList) {
			int rangeStart = range[0];
			int rangeEnd = range[1];

			if ((rangeStart < start) || (rangeEnd > end)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasVariableNameInMethodCall(
		DetailAST methodCallDetailAST, String variableName) {

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		for (String name : getNames(elistDetailAST, true)) {
			if (variableName.equals(name)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isNullValueExpression(DetailAST detailAST) {
		if (detailAST.getType() != TokenTypes.EXPR) {
			return false;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_NULL) {
			return true;
		}

		if (firstChildDetailAST.getType() == TokenTypes.TYPECAST) {
			DetailAST lastChildDetailAST = firstChildDetailAST.getLastChild();

			if (lastChildDetailAST.getType() == TokenTypes.LITERAL_NULL) {
				return true;
			}
		}

		return false;
	}

	private static final String _CHECK_INLINE = "checkInline";

	private static final String _ENFORCE_BUILDER_NAMES_KEY =
		"enforceBuilderNames";

	private static final String _MSG_INCLUDE_BUILDER = "builder.include";

	private static final String _MSG_INCORRECT_NULL_VALUE =
		"null.value.incorrect";

	private static final String _MSG_INLINE_BUILDER_1 = "builder.inline.1";

	private static final String _MSG_INLINE_BUILDER_2 = "builder.inline.2";

	private static final String _MSG_INLINE_IF_STATEMENT =
		"if.statement.inline";

	private static final String _MSG_RESERVED_KEYWORD = "keyword.reserved";

	private static final String _MSG_UNNEEDED_STRING_CAST =
		"string.cast.unneeded";

	private static final String _MSG_USE_BUILD_STRING = "build.string.use";

	private static final String _MSG_USE_BUILDER = "builder.use";

	private static final String _MSG_USE_BUILDER_INSTEAD =
		"builder.use.instead";

}