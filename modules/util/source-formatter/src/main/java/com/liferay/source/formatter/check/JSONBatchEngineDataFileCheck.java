/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.JSONSourceUtil;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.checkstyle.util.DetailASTUtil;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;

import java.util.Comparator;
import java.util.List;

/**
 * @author Qi Zhang
 */
public class JSONBatchEngineDataFileCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!absolutePath.endsWith(".batch-engine-data.json")) {
			return content;
		}

		JSONObject jsonObject = new JSONObjectImpl(content);

		jsonObject.remove("actions");
		jsonObject.remove("facets");

		jsonObject = _checkConfiguration(jsonObject);
		jsonObject = _checkItems(fileName, absolutePath, jsonObject);

		return JSONUtil.toString(jsonObject);
	}

	private boolean _appendString(DetailAST detailAST, StringBundler sb) {
		if (detailAST == null) {
			return false;
		}

		if (detailAST.getType() == TokenTypes.STRING_LITERAL) {
			String text = detailAST.getText();

			sb.append(text.substring(1, text.length() - 1));

			return true;
		}

		if (detailAST.getType() != TokenTypes.PLUS) {
			return false;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return false;
		}

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if (_appendString(firstChildDetailAST, sb) &&
			_appendString(nextSiblingDetailAST, sb)) {

			return true;
		}

		return false;
	}

	private void _checkClassName(
			String fileName, String absolutePath, JSONObject jsonObject)
		throws Exception {

		String className = jsonObject.getString("className");

		if (!className.startsWith(
				"com.liferay.object.model.ObjectDefinition#")) {

			return;
		}

		List<String> allowedUnmatchedClassNameFileNames = getAttributeValues(
			_ALLOWED_UNMATCHED_CLASS_NAME_FILE_NAMES_KEY, absolutePath);

		for (String allowedUnmatchedClassNameFileName :
				allowedUnmatchedClassNameFileNames) {

			if (absolutePath.endsWith(allowedUnmatchedClassNameFileName)) {
				return;
			}
		}

		String classNameRegex = _getClassNameRegex();

		if ((classNameRegex == null) || className.matches(classNameRegex)) {
			return;
		}

		addMessage(
			fileName,
			"\"className\" does not match the pattern specified by \"_class" +
				"NamePattern\" in \"ObjectDefinitionClassNameProcessorImpl\"");
	}

	private JSONObject _checkConfiguration(JSONObject jsonObject) {
		JSONObject configurationJSONObject = jsonObject.getJSONObject(
			"configuration");

		if (configurationJSONObject == null) {
			return jsonObject;
		}

		configurationJSONObject.remove("companyId");

		boolean multiCompany = configurationJSONObject.getBoolean(
			"multiCompany");

		if (!multiCompany) {
			configurationJSONObject.remove("multiCompany");
		}

		configurationJSONObject.remove("userId");
		configurationJSONObject.remove("version");

		jsonObject.put("configuration", configurationJSONObject);

		return jsonObject;
	}

	private JSONObject _checkItems(
			String fileName, String absolutePath, JSONObject jsonObject)
		throws Exception {

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		if (itemsJSONArray == null) {
			return jsonObject;
		}

		List<Object> objects = JSONUtil.toObjectList(itemsJSONArray);

		itemsJSONArray = new JSONArrayImpl();

		List<String> allItemsDefaultValues = getAttributeValues(
			_ALL_ITEMS_DEFAULT_VALUES_KEY, absolutePath);
		List<String> objectDefinitionItemsDefaultValues = getAttributeValues(
			_OBJECT_DEFINITION_ITEMS_DEFAULT_VALUES_KEY, absolutePath);

		boolean objectDefinition = _isObjectDefinition(jsonObject);

		for (Object object : objects) {
			JSONObject itemJSONObject = (JSONObject)object;

			_removeDefaultValues(allItemsDefaultValues, itemJSONObject);

			if (objectDefinition) {
				_checkClassName(fileName, absolutePath, itemJSONObject);
				_removeDefaultValues(
					objectDefinitionItemsDefaultValues, itemJSONObject);
				_sortObjectFields(itemJSONObject);
			}

			itemsJSONArray.put(itemJSONObject);
		}

		jsonObject.put("items", itemsJSONArray);

		return jsonObject;
	}

	private synchronized String _getClassNameRegex() throws Exception {
		if (_classNameRegex != null) {
			return _classNameRegex;
		}

		File portalDir = getPortalDir();

		if (portalDir == null) {
			return null;
		}

		File file = new File(
			portalDir,
			"/modules/apps/object/object-service/src/main/java/com/liferay" +
				"/object/internal/definition/processor" +
					"/ObjectDefinitionClassNameProcessorImpl.java");

		if (!file.exists()) {
			return null;
		}

		String content = FileUtil.read(file);

		FileText fileText = new FileText(
			file, CheckstyleUtil.getLines(content));

		FileContents fileContents = new FileContents(fileText);

		DetailAST rootDetailAST = JavaParser.parse(fileContents);

		DetailAST childDetailAST = rootDetailAST.getFirstChild();

		while (childDetailAST != null) {
			if (childDetailAST.getType() != TokenTypes.CLASS_DEF) {
				childDetailAST = childDetailAST.getNextSibling();

				continue;
			}

			DetailAST objBlockDetailAST = childDetailAST.findFirstToken(
				TokenTypes.OBJBLOCK);

			List<DetailAST> variableDefinitionDetailASTs =
				DetailASTUtil.getAllChildTokens(
					objBlockDetailAST, false, TokenTypes.VARIABLE_DEF);

			for (DetailAST variableDefinitionDetailAST :
					variableDefinitionDetailASTs) {

				DetailAST identDetailAST =
					variableDefinitionDetailAST.findFirstToken(
						TokenTypes.IDENT);

				if (identDetailAST == null) {
					continue;
				}

				String variableName = identDetailAST.getText();

				if (!variableName.equals("_classNamePattern")) {
					continue;
				}

				DetailAST assignDetailAST =
					variableDefinitionDetailAST.findFirstToken(
						TokenTypes.ASSIGN);

				if (assignDetailAST == null) {
					return null;
				}

				DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

				if ((firstChildDetailAST == null) ||
					(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

					return null;
				}

				firstChildDetailAST = firstChildDetailAST.getFirstChild();

				if ((firstChildDetailAST == null) ||
					(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

					return null;
				}

				FullIdent fullIdent = FullIdent.createFullIdentBelow(
					firstChildDetailAST);

				if (!StringUtil.equals(
						fullIdent.getText(), "Pattern.compile")) {

					return null;
				}

				_classNameRegex = _getClassNameRegex(firstChildDetailAST);

				return _classNameRegex;
			}
		}

		return null;
	}

	private String _getClassNameRegex(DetailAST detailAST) {
		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if ((elistDetailAST == null) || (elistDetailAST.getChildCount() == 0)) {
			return null;
		}

		DetailAST exprDetailAST = elistDetailAST.findFirstToken(
			TokenTypes.EXPR);

		if (exprDetailAST == null) {
			return null;
		}

		StringBundler sb = new StringBundler();

		if (_appendString(exprDetailAST.getFirstChild(), sb)) {
			return StringUtil.replace(sb.toString(), "\\\\", "\\");
		}

		return null;
	}

	private boolean _isObjectDefinition(JSONObject jsonObject) {
		JSONObject configurationJSONObject = jsonObject.getJSONObject(
			"configuration");

		if ((configurationJSONObject != null) &&
			StringUtil.equals(
				configurationJSONObject.getString("className"),
				"com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition")) {

			return true;
		}

		return false;
	}

	private void _removeDefaultValues(
		List<String> defaultValues, JSONObject jsonObject) {

		for (String defaultValue : defaultValues) {
			String[] parts = StringUtil.split(defaultValue, CharPool.COLON);

			if (parts.length != 2) {
				continue;
			}

			String key = parts[0];

			if (!jsonObject.has(key)) {
				continue;
			}

			String value = parts[1];

			if (value.equals(StringPool.STAR) ||
				value.equals(String.valueOf(jsonObject.get(key)))) {

				jsonObject.remove(key);
			}
		}
	}

	private void _sortObjectFields(JSONObject jsonObject) {
		JSONArray objectFieldsJSONArray = jsonObject.getJSONArray(
			"objectFields");

		if (objectFieldsJSONArray == null) {
			return;
		}

		jsonObject.put(
			"objectFields",
			JSONSourceUtil.sortJSONArray(
				objectFieldsJSONArray, new ObjectFieldComparator()));
	}

	private static final String _ALL_ITEMS_DEFAULT_VALUES_KEY =
		"allItemsDefaultValues";

	private static final String _ALLOWED_UNMATCHED_CLASS_NAME_FILE_NAMES_KEY =
		"allowedUnmatchedClassNameFileNames";

	private static final String _OBJECT_DEFINITION_ITEMS_DEFAULT_VALUES_KEY =
		"objectDefinitionItemsDefaultValues";

	private String _classNameRegex;

	private static class ObjectFieldComparator implements Comparator<Object> {

		@Override
		public int compare(Object object1, Object object2) {
			JSONObject jsonObject1 = (JSONObject)object1;
			JSONObject jsonObject2 = (JSONObject)object2;

			boolean relationship1 = _isRelationship(jsonObject1);
			boolean relationship2 = _isRelationship(jsonObject2);

			if (relationship1 != relationship2) {
				if (relationship1) {
					return 1;
				}

				return -1;
			}

			String name1 = jsonObject1.getString("name");
			String name2 = jsonObject2.getString("name");

			return name1.compareTo(name2);
		}

		private boolean _isRelationship(JSONObject jsonObject) {
			String businessType = jsonObject.getString("businessType");

			if (businessType.equals("Relationship")) {
				return true;
			}

			String name = jsonObject.getString("name");

			if (Validator.isNotNull(name) && name.startsWith("r_")) {
				return true;
			}

			return false;
		}

	}

}