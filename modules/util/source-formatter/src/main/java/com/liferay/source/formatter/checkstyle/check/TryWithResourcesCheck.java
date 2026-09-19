/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class TryWithResourcesCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_TRY};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST literalFinallyDetailAST = detailAST.findFirstToken(
			TokenTypes.LITERAL_FINALLY);

		if (literalFinallyDetailAST != null) {
			_checkFinallyStatement(detailAST, literalFinallyDetailAST);
		}

		if (!isAttributeValue(_POPULATE_TYPE_NAMES_KEY)) {
			return;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() ==
				TokenTypes.RESOURCE_SPECIFICATION) {

			_populateCloseableTypeNames(firstChildDetailAST);
		}
	}

	private void _checkFinallyStatement(
		DetailAST literalTryDetailAST, DetailAST literalFinallyDetailAST) {

		Map<Integer, String> cleanUpVariableNamesMap = new HashMap<>();
		Map<Integer, String> closeVariableNamesMap = new HashMap<>();

		DetailAST slistDetailAST = literalFinallyDetailAST.findFirstToken(
			TokenTypes.SLIST);

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			List<String> cleanUpVariableNames = _getCleanUpVariableNames(
				methodCallDetailAST);

			for (String cleanUpVariableName : cleanUpVariableNames) {
				DetailAST typeDetailAST = getVariableTypeDetailAST(
					literalTryDetailAST, cleanUpVariableName, false);

				if (!_useTryWithResources(
						cleanUpVariableName, typeDetailAST,
						literalTryDetailAST)) {

					return;
				}

				cleanUpVariableNamesMap.put(
					methodCallDetailAST.getLineNo(), cleanUpVariableName);
			}

			String closeVariableName = _getCloseVariableName(
				methodCallDetailAST, literalFinallyDetailAST);

			if (closeVariableName == null) {
				continue;
			}

			DetailAST typeDetailAST = getVariableTypeDetailAST(
				literalTryDetailAST, closeVariableName, false);

			if (!_useTryWithResources(
					closeVariableName, typeDetailAST, literalTryDetailAST)) {

				return;
			}

			List<String> closeableTypeNames = _getCloseableTypeNames();

			if (!closeableTypeNames.contains(
					getFullyQualifiedTypeName(typeDetailAST, true))) {

				return;
			}

			closeVariableNamesMap.put(
				methodCallDetailAST.getLineNo(), closeVariableName);
		}

		for (Map.Entry<Integer, String> entry :
				cleanUpVariableNamesMap.entrySet()) {

			log(
				entry.getKey(), _MSG_USE_TRY_WITH_RESOURCES, entry.getValue(),
				"DataAccess.cleanUp");
		}

		for (Map.Entry<Integer, String> entry :
				closeVariableNamesMap.entrySet()) {

			String closeVariableName = entry.getValue();

			log(
				entry.getKey(), _MSG_USE_TRY_WITH_RESOURCES, closeVariableName,
				closeVariableName + ".close");
		}
	}

	private List<String> _getCleanUpVariableNames(
		DetailAST methodCallDetailAST) {

		List<String> variableNames = new ArrayList<>();

		DetailAST dotDetailAST = methodCallDetailAST.getFirstChild();

		if (dotDetailAST.getType() != TokenTypes.DOT) {
			return variableNames;
		}

		DetailAST lastChildDetailAST = dotDetailAST.getLastChild();

		if (!Objects.equals(lastChildDetailAST.getText(), "cleanUp")) {
			return variableNames;
		}

		DetailAST firstChildDetailAST = dotDetailAST.getFirstChild();

		if (!Objects.equals(firstChildDetailAST.getText(), "DataAccess")) {
			return variableNames;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		for (DetailAST exprDetailAST : exprDetailASTs) {
			firstChildDetailAST = exprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				variableNames.add(firstChildDetailAST.getText());
			}
		}

		return variableNames;
	}

	private List<String> _getCloseableTypeNames() {
		Tuple closeableTypeNamesTuple = _getCloseableTypeNamesTuple();

		JSONObject jsonObject = (JSONObject)closeableTypeNamesTuple.getObject(
			0);

		JSONArray jsonArray = (JSONArray)jsonObject.get(
			_CLOSEABLE_TYPE_NAMES_CATEGORY);

		return JSONUtil.toStringList(jsonArray, "name");
	}

	private synchronized Tuple _getCloseableTypeNamesTuple() {
		if (_closeableTypeNamesTuple != null) {
			return _closeableTypeNamesTuple;
		}

		_closeableTypeNamesTuple = getTypeNamesTuple(
			_CLOSEABLE_TYPE_NAMES_FILE_NAME, _CLOSEABLE_TYPE_NAMES_CATEGORY);

		return _closeableTypeNamesTuple;
	}

	private String _getCloseVariableName(
		DetailAST methodCallDetailAST, DetailAST literalFinallyDetailAST) {

		DetailAST dotDetailAST = methodCallDetailAST.getFirstChild();

		if (dotDetailAST.getType() != TokenTypes.DOT) {
			return null;
		}

		DetailAST lastChildDetailAST = dotDetailAST.getLastChild();

		if (!Objects.equals(lastChildDetailAST.getText(), "close")) {
			return null;
		}

		DetailAST firstChildDetailAST = dotDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return null;
		}

		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return null;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return null;
		}

		String variableName = firstChildDetailAST.getText();

		parentDetailAST = parentDetailAST.getParent();

		if (equals(parentDetailAST, literalFinallyDetailAST)) {
			return variableName;
		}

		if (parentDetailAST.getType() != TokenTypes.LITERAL_IF) {
			return null;
		}

		DetailAST exprDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.EXPR);

		firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.NOT_EQUAL) {
			return null;
		}

		lastChildDetailAST = firstChildDetailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.LITERAL_NULL) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (!variableName.equals(firstChildDetailAST.getText())) {
			return null;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return null;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (equals(parentDetailAST, literalFinallyDetailAST)) {
			return variableName;
		}

		return null;
	}

	private void _populateCloseableTypeNames(
		DetailAST resourceSpecificationDetailAST) {

		Tuple closeableTypeNamesTuple = _getCloseableTypeNamesTuple();

		File closeableTypeNamesFile = (File)closeableTypeNamesTuple.getObject(
			1);

		if (closeableTypeNamesFile == null) {
			return;
		}

		DetailAST resourcesDetailAST =
			resourceSpecificationDetailAST.findFirstToken(TokenTypes.RESOURCES);

		if (resourcesDetailAST == null) {
			return;
		}

		List<String> closeableTypeNames = _getCloseableTypeNames();

		List<DetailAST> resourceDetailASTs = getAllChildTokens(
			resourcesDetailAST, false, TokenTypes.RESOURCE);

		for (DetailAST resourceDetailAST : resourceDetailASTs) {
			DetailAST typeDetailAST = resourceDetailAST.findFirstToken(
				TokenTypes.TYPE);

			if (typeDetailAST == null) {
				continue;
			}

			String typeName = getFullyQualifiedTypeName(typeDetailAST, false);

			if ((typeName == null) || closeableTypeNames.contains(typeName)) {
				continue;
			}

			closeableTypeNames.add(typeName);

			Collections.sort(closeableTypeNames);

			try {
				JSONObject jsonObject = new JSONObjectImpl();

				JSONArray jsonArray = new JSONArrayImpl();

				for (String closeableTypeName : closeableTypeNames) {
					jsonArray.put(
						new JSONObjectImpl(
							HashMapBuilder.put(
								"name", closeableTypeName
							).build()));
				}

				jsonObject.put(_CLOSEABLE_TYPE_NAMES_CATEGORY, jsonArray);

				FileUtil.write(
					closeableTypeNamesFile, JSONUtil.toString(jsonObject));

				System.out.println(
					StringBundler.concat(
						"Added \"", typeName, "\" to \"",
						_CLOSEABLE_TYPE_NAMES_FILE_NAME, "\""));

				_closeableTypeNamesTuple = null;
			}
			catch (IOException ioException) {
				if (_log.isDebugEnabled()) {
					_log.debug(ioException);
				}
			}
		}
	}

	private boolean _useTryWithResources(
		String variableName, DetailAST typeDetailAST,
		DetailAST literalTryDetailAST) {

		if (typeDetailAST == null) {
			return false;
		}

		DetailAST parentDetailAST = typeDetailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.VARIABLE_DEF) ||
			hasParentWithTokenType(typeDetailAST, TokenTypes.FOR_EACH_CLAUSE)) {

			return false;
		}

		int endLineNumber = getEndLineNumber(literalTryDetailAST);

		int assignCount = 0;

		DetailAST assignDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST != null) {
			DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.LITERAL_NULL) {
				assignCount++;
			}
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			parentDetailAST, variableName);

		for (DetailAST variableCallerDetailAST : variableCallerDetailASTs) {
			if (hasParentWithTokenType(
					variableCallerDetailAST, TokenTypes.LAMBDA)) {

				return false;
			}

			parentDetailAST = variableCallerDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.ASSIGN) {
				if (assignCount > 0) {
					return false;
				}

				DetailAST literalIfDetailAST = getParentWithTokenType(
					variableCallerDetailAST, TokenTypes.LITERAL_IF);

				if ((literalIfDetailAST != null) &&
					(literalIfDetailAST.getLineNo() >
						typeDetailAST.getLineNo())) {

					return false;
				}

				assignCount++;
			}

			if (variableCallerDetailAST.getLineNo() > endLineNumber) {
				return false;
			}

			DetailAST callerLiteralTryDetailAST = getParentWithTokenType(
				variableCallerDetailAST, TokenTypes.LITERAL_TRY);

			if (callerLiteralTryDetailAST == null) {
				continue;
			}

			if ((callerLiteralTryDetailAST.getLineNo() <
					literalTryDetailAST.getLineNo()) ||
				((callerLiteralTryDetailAST.getLineNo() ==
					literalTryDetailAST.getLineNo()) &&
				 hasParentWithTokenType(
					 variableCallerDetailAST, TokenTypes.LITERAL_CATCH))) {

				return false;
			}
		}

		return true;
	}

	private static final String _CLOSEABLE_TYPE_NAMES_CATEGORY =
		"closeableTypeNames";

	private static final String _CLOSEABLE_TYPE_NAMES_FILE_NAME =
		"closeable-type-names.json";

	private static final String _MSG_USE_TRY_WITH_RESOURCES =
		"try.with.resources.use";

	private static final String _POPULATE_TYPE_NAMES_KEY = "populateTypeNames";

	private static final Log _log = LogFactoryUtil.getLog(
		TryWithResourcesCheck.class);

	private Tuple _closeableTypeNamesTuple;

}