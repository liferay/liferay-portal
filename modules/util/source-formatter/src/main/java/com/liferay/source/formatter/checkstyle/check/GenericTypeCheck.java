/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Hugo Huijser
 */
public class GenericTypeCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.EXTENDS_CLAUSE, TokenTypes.IMPLEMENTS_CLAUSE,
			TokenTypes.METHOD_DEF, TokenTypes.PARAMETER_DEF,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if ((detailAST.getType() == TokenTypes.EXTENDS_CLAUSE) ||
			(detailAST.getType() == TokenTypes.IMPLEMENTS_CLAUSE)) {

			List<DetailAST> childDetailASTs = getAllChildTokens(
				detailAST, false, TokenTypes.DOT, TokenTypes.IDENT);

			for (DetailAST childDetailAST : childDetailASTs) {
				_checkType(detailAST, childDetailAST);
			}
		}

		_checkType(detailAST, detailAST.findFirstToken(TokenTypes.TYPE));
	}

	private void _checkType(DetailAST detailAST, DetailAST childDetailAST) {
		if ((childDetailAST == null) ||
			(detailAST.findFirstToken(TokenTypes.ELLIPSIS) != null)) {

			return;
		}

		DetailAST typeArgumentsDetailAST = getTypeArgumentsDetailAST(
			childDetailAST);

		if (typeArgumentsDetailAST != null) {
			List<DetailAST> typeArgumentDetailASTs = getAllChildTokens(
				typeArgumentsDetailAST, false, TokenTypes.TYPE_ARGUMENT);

			if (isAttributeValue(_POPULATE_TYPE_NAMES_KEY)) {
				_populateGenericTypeNames(
					childDetailAST, typeArgumentDetailASTs);
			}

			for (DetailAST typeArgumentDetailAST : typeArgumentDetailASTs) {
				_checkType(detailAST, typeArgumentDetailAST);
			}

			return;
		}

		String genericTypeName = _getGenericTypeName(childDetailAST);

		if (genericTypeName == null) {
			return;
		}

		if (!genericTypeName.startsWith("com.liferay.")) {
			if ((detailAST.getType() == TokenTypes.METHOD_DEF) &&
				_overridesUnknownTerm(detailAST)) {

				return;
			}

			if (detailAST.getType() == TokenTypes.PARAMETER_DEF) {
				DetailAST parentDetailAST = getParentWithTokenType(
					detailAST, TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF);

				if ((parentDetailAST != null) &&
					_overridesUnknownTerm(parentDetailAST)) {

					return;
				}
			}
		}

		DetailAST parentDetailAST = childDetailAST;

		while (true) {
			if (parentDetailAST == null) {
				break;
			}

			if (_hasSuppressWarningsAnnotation(parentDetailAST, "rawtypes")) {
				return;
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		Map<String, Integer> genericTypeNamesMap = _getGenericTypeNamesMap();

		int genericTypeCount = GetterUtil.getInteger(
			genericTypeNamesMap.get(genericTypeName));

		if (genericTypeCount == 1) {
			log(
				childDetailAST, _MSG_PARAMETERIZE_GENERIC_TYPE, "type",
				genericTypeName);
		}
		else {
			log(
				childDetailAST, _MSG_PARAMETERIZE_GENERIC_TYPE, "types",
				genericTypeName);
		}
	}

	private String _getGenericTypeName(DetailAST detailAST) {
		Map<String, Integer> genericTypeNamesMap = _getGenericTypeNamesMap();

		String typeName = _getTypeName(detailAST);

		String fullyQualifiedTypeName = getFullyQualifiedTypeName(
			typeName, detailAST, false);

		if ((fullyQualifiedTypeName != null) &&
			genericTypeNamesMap.containsKey(fullyQualifiedTypeName)) {

			return fullyQualifiedTypeName;
		}

		if (typeName.equals(fullyQualifiedTypeName)) {
			return null;
		}

		if (genericTypeNamesMap.containsKey("java.lang." + typeName)) {
			return "java.lang." + typeName;
		}

		FileContents fileContents = getFileContents();

		FileText fileText = fileContents.getText();

		typeName =
			JavaSourceUtil.getPackageName((String)fileText.getFullText()) +
				StringPool.PERIOD + typeName;

		if (genericTypeNamesMap.containsKey(typeName)) {
			return typeName;
		}

		return null;
	}

	private synchronized Map<String, Integer> _getGenericTypeNamesMap() {
		if (_genericTypeNamesMap != null) {
			return _genericTypeNamesMap;
		}

		Tuple genericTypeNamesTuple = _getGenericTypeNamesTuple();

		JSONObject jsonObject = (JSONObject)genericTypeNamesTuple.getObject(0);

		JSONArray jsonArray = (JSONArray)jsonObject.get(
			_GENERIC_TYPE_NAMES_CATEGORY);

		_genericTypeNamesMap = new TreeMap<>();

		for (Object object : JSONUtil.toObjectList(jsonArray)) {
			jsonObject = (JSONObject)object;

			_genericTypeNamesMap.put(
				jsonObject.getString("name"),
				jsonObject.getInt("genericTypeCount"));
		}

		return _genericTypeNamesMap;
	}

	private synchronized Tuple _getGenericTypeNamesTuple() {
		if (_genericTypeNamesTuple != null) {
			return _genericTypeNamesTuple;
		}

		_genericTypeNamesTuple = getTypeNamesTuple(
			_GENERIC_TYPE_NAMES_FILE_NAME, _GENERIC_TYPE_NAMES_CATEGORY);

		return _genericTypeNamesTuple;
	}

	private String _getTypeName(DetailAST detailAST) {
		if ((detailAST.getType() == TokenTypes.TYPE) ||
			(detailAST.getType() == TokenTypes.TYPE_ARGUMENT)) {

			return getTypeName(detailAST, false);
		}

		if (detailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(detailAST);

			return fullIdent.getText();
		}

		return detailAST.getText();
	}

	private boolean _hasSuppressWarningsAnnotation(
		DetailAST detailAST, String warning) {

		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (modifiersDetailAST == null) {
			return false;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "SuppressWarnings");

		if (annotationDetailAST == null) {
			return false;
		}

		List<DetailAST> literalStringDetailASTs = getAllChildTokens(
			annotationDetailAST, true, TokenTypes.STRING_LITERAL);

		for (DetailAST literalStringDetailAST : literalStringDetailASTs) {
			String s = literalStringDetailAST.getText();

			if (s.equals("\"" + warning + "\"")) {
				return true;
			}
		}

		return false;
	}

	private boolean _overridesUnknownTerm(DetailAST detailAST) {
		if (!AnnotationUtil.containsAnnotation(detailAST, "Override")) {
			return false;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		parentDetailAST = parentDetailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.CLASS_DEF) ||
			(parentDetailAST.findFirstToken(TokenTypes.EXTENDS_CLAUSE) !=
				null)) {

			return true;
		}

		DetailAST implementsClauseDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.IMPLEMENTS_CLAUSE);

		if (implementsClauseDetailAST == null) {
			return false;
		}

		List<String> importNames = getImportNames(detailAST);

		DetailAST childDetailAST = implementsClauseDetailAST.getFirstChild();

		while (true) {
			if (childDetailAST == null) {
				return false;
			}

			if (childDetailAST.getType() == TokenTypes.IDENT) {
				String implementedClassName = childDetailAST.getText();

				if (ArrayUtil.contains(
						_JAVA_LANG_INTERFACE_NAMES, implementedClassName)) {

					return true;
				}

				for (String importName : importNames) {
					if (importName.endsWith("." + implementedClassName)) {
						if (!importName.startsWith("com.liferay.")) {
							return true;
						}

						break;
					}
				}
			}
			else if (childDetailAST.getType() == TokenTypes.DOT) {
				FullIdent fullIdent = FullIdent.createFullIdent(childDetailAST);

				String implementedClassName = fullIdent.getText();

				if (!implementedClassName.startsWith("com.liferay.")) {
					return true;
				}
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private void _populateGenericTypeNames(
		DetailAST childDetailAST, List<DetailAST> typeArgumentDetailASTs) {

		Tuple genericTypeNamesTuple = _getGenericTypeNamesTuple();

		File genericTypeNamesFile = (File)genericTypeNamesTuple.getObject(1);

		if (genericTypeNamesFile == null) {
			return;
		}

		for (DetailAST typeArgumentDetailAST : typeArgumentDetailASTs) {
			DetailAST firstChildDetailAST =
				typeArgumentDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.WILDCARD_TYPE) {
				return;
			}

			if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				String name = firstChildDetailAST.getText();

				if (name.length() == 1) {
					return;
				}
			}
		}

		Map<String, Integer> genericTypeNamesMap = _getGenericTypeNamesMap();

		String fullyQualifiedTypeName = getFullyQualifiedTypeName(
			_getTypeName(childDetailAST), childDetailAST, false);

		if ((fullyQualifiedTypeName == null) ||
			genericTypeNamesMap.containsKey(fullyQualifiedTypeName)) {

			return;
		}

		genericTypeNamesMap.put(
			fullyQualifiedTypeName, typeArgumentDetailASTs.size());

		try {
			JSONObject jsonObject = new JSONObjectImpl();

			JSONArray jsonArray = new JSONArrayImpl();

			for (Map.Entry<String, Integer> entry :
					genericTypeNamesMap.entrySet()) {

				JSONObject curJSONObject = new JSONObjectImpl();

				curJSONObject.put(
					"genericTypeCount", entry.getValue()
				).put(
					"name", entry.getKey()
				);

				jsonArray.put(curJSONObject);
			}

			jsonObject.put(_GENERIC_TYPE_NAMES_CATEGORY, jsonArray);

			FileUtil.write(genericTypeNamesFile, JSONUtil.toString(jsonObject));

			System.out.println(
				StringBundler.concat(
					"Added \"", fullyQualifiedTypeName, "\" to \"",
					_GENERIC_TYPE_NAMES_FILE_NAME, "\""));

			_genericTypeNamesMap = null;
			_genericTypeNamesTuple = null;
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}
	}

	private static final String _GENERIC_TYPE_NAMES_CATEGORY =
		"genericTypeNames";

	private static final String _GENERIC_TYPE_NAMES_FILE_NAME =
		"generic-type-names.json";

	private static final String[] _JAVA_LANG_INTERFACE_NAMES = {
		"Appendable", "AutoCloseable", "CharSequence", "Cloneable",
		"Comparable", "Iterable", "Readable", "Runnable"
	};

	private static final String _MSG_PARAMETERIZE_GENERIC_TYPE =
		"generic.type.parameterize";

	private static final String _POPULATE_TYPE_NAMES_KEY = "populateTypeNames";

	private static final Log _log = LogFactoryUtil.getLog(
		GenericTypeCheck.class);

	private Map<String, Integer> _genericTypeNamesMap;
	private Tuple _genericTypeNamesTuple;

}