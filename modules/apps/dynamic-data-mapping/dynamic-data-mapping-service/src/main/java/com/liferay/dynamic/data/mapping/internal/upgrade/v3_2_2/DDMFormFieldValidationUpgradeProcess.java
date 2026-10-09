/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_2_2;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rodrigo Paulino
 */
public class DDMFormFieldValidationUpgradeProcess extends UpgradeProcess {

	public DDMFormFieldValidationUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select structureId, definition from DDMStructure where " +
					"classNameId = ? ");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"select structureVersionId, definition from " +
					"DDMStructureVersion where structureId = ?");
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"structureVersionId = ?")) {

			preparedStatement1.setLong(
				1,
				PortalUtil.getClassNameId(
					"com.liferay.dynamic.data.mapping.model.DDMFormInstance"));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					String definition = resultSet.getString("definition");

					preparedStatement2.setString(
						1, _upgradeDefinition(definition));

					long structureId = resultSet.getLong("structureId");

					preparedStatement2.setLong(2, structureId);

					preparedStatement2.addBatch();

					preparedStatement3.setLong(1, structureId);

					try (ResultSet resultSet2 =
							preparedStatement3.executeQuery()) {

						while (resultSet2.next()) {
							definition = resultSet2.getString("definition");

							preparedStatement4.setString(
								1, _upgradeDefinition(definition));

							long structureVersionId = resultSet2.getLong(
								"structureVersionId");

							preparedStatement4.setLong(2, structureVersionId);

							preparedStatement4.addBatch();
						}
					}
				}
			}

			preparedStatement2.executeBatch();

			preparedStatement4.executeBatch();
		}
	}

	private Map<String, String> _dissect(
		String expression, String name, String regex) {

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(expression);

		matcher.find();

		String parameter = matcher.group(2);

		return HashMapBuilder.put(
			"name", name
		).put(
			"parameter", parameter
		).put(
			"value", StringUtil.replace(expression, parameter, "{parameter}")
		).build();
	}

	private Map<String, String> _dissectExpression(String expression) {
		if (expression.matches("NOT\\(contains\\((.+), \"(.*)\"\\)\\)")) {
			return _dissect(
				expression, "notContains",
				"NOT\\(contains\\((.+), \"(.*)\"\\)\\)");
		}

		if (expression.matches("contains\\((.+), \"(.*)\"\\)")) {
			return _dissect(
				expression, "contains", "contains\\((.+), \"(.*)\"\\)");
		}

		if (expression.matches("isURL\\((.+)\\)")) {
			return HashMapBuilder.put(
				"name", "url"
			).put(
				"parameter", StringPool.BLANK
			).put(
				"value", expression
			).build();
		}

		if (expression.matches("isEmailAddress\\((.+)\\)")) {
			return HashMapBuilder.put(
				"name", "email"
			).put(
				"parameter", StringPool.BLANK
			).put(
				"value", expression
			).build();
		}

		if (expression.matches("match\\((.+), \"(.*)\"\\)")) {
			return _dissect(
				expression, "regularExpression", "match\\((.+), \"(.*)\"\\)");
		}

		if (expression.matches("(.+)<(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "lt", "(.+)<(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)<=(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "lteq", "(.+)<=(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)==(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "eq", "(.+)==(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)>(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "gt", "(.+)>(\\d+\\.?\\d*)?");
		}

		if (expression.matches("(.+)>=(\\d+\\.?\\d*)?")) {
			return _dissect(expression, "gteq", "(.+)>=(\\d+\\.?\\d*)?");
		}

		return HashMapBuilder.put(
			"name", StringPool.BLANK
		).put(
			"parameter", StringPool.BLANK
		).put(
			"value", expression
		).build();
	}

	private String _upgradeDefinition(String definition)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(definition);

		_upgradeFields(
			jsonObject.getJSONArray("availableLanguageIds"),
			jsonObject.getJSONArray("fields"));

		return jsonObject.toString();
	}

	private void _upgradeFields(
		JSONArray availableLanguageIdsJSONArray, JSONArray fieldsJSONArray) {

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			JSONObject validationJSONObject = jsonObject.getJSONObject(
				"validation");

			if (validationJSONObject != null) {
				String expression = validationJSONObject.getString(
					"expression");

				if (Validator.isNull(expression)) {
					jsonObject.remove("validation");
				}
				else {
					JSONObject expressionJSONObject =
						validationJSONObject.getJSONObject("expression");

					if (expressionJSONObject == null) {
						_upgradeValidation(
							availableLanguageIdsJSONArray, expression,
							validationJSONObject);
					}
					else if (expressionJSONObject.isNull("name")) {
						_upgradeValidation(
							availableLanguageIdsJSONArray,
							expressionJSONObject.getString("value"),
							validationJSONObject);
					}
				}
			}

			JSONArray nestedFieldsJSONArray = jsonObject.getJSONArray(
				"nestedFields");

			if (nestedFieldsJSONArray == null) {
				continue;
			}

			_upgradeFields(
				availableLanguageIdsJSONArray, nestedFieldsJSONArray);
		}
	}

	private void _upgradeValidation(
		JSONArray availableLanguageIdsJSONArray, String expression,
		JSONObject validationJSONObject) {

		Map<String, String> expressionParts = _dissectExpression(expression);

		String parameter = expressionParts.remove("parameter");

		validationJSONObject.put("expression", expressionParts);

		Map<String, String> localizedValue = new HashMap<>();

		for (int j = 0; j < availableLanguageIdsJSONArray.length(); j++) {
			localizedValue.put(
				availableLanguageIdsJSONArray.getString(j), parameter);
		}

		validationJSONObject.put("parameter", localizedValue);
	}

	private final JSONFactory _jsonFactory;

}