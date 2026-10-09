/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_1;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Lino Alves
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
						1, _updateValidation(definition));

					long structureId = resultSet.getLong("structureId");

					preparedStatement2.setLong(2, structureId);

					preparedStatement2.addBatch();

					preparedStatement3.setLong(1, structureId);

					try (ResultSet resultSet2 =
							preparedStatement3.executeQuery()) {

						while (resultSet2.next()) {
							definition = resultSet2.getString("definition");

							preparedStatement4.setString(
								1, _updateValidation(definition));

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

	private void _addParameterValue(
		String value, JSONObject validationJSONObject,
		String defaultLanguageId) {

		JSONObject parameterJSONObject = validationJSONObject.getJSONObject(
			"parameter");

		if (!parameterJSONObject.has(defaultLanguageId)) {
			parameterJSONObject.put(defaultLanguageId, value);
		}
	}

	private String _getExpressionName(String expressionValue) {
		String name = "";

		if (expressionValue.startsWith("contains")) {
			name = "contains";
		}
		else if (expressionValue.startsWith("NOT(contains")) {
			name = "notContains";
		}
		else if (expressionValue.startsWith("isEmailAddress")) {
			name = "email";
		}
		else if (expressionValue.startsWith("match")) {
			name = "regularExpression";
		}
		else if (expressionValue.startsWith("isURL")) {
			name = "url";
		}

		return name;
	}

	private String _getParameterValueFromExpression(String expressionValue) {
		String[] parts = expressionValue.split("\"");

		if (parts.length > 1) {
			return parts[1];
		}

		return "";
	}

	private String _updateValidation(String definition) throws PortalException {
		JSONObject definitionJSONObject = _jsonFactory.createJSONObject(
			definition);

		JSONArray fieldsJSONArray = definitionJSONObject.getJSONArray("fields");

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(i);

			if (!fieldJSONObject.has("validation")) {
				continue;
			}

			JSONObject validationJSONObject = fieldJSONObject.getJSONObject(
				"validation");

			if (validationJSONObject == null) {
				fieldJSONObject.remove("validation");

				continue;
			}

			JSONObject expressionJSONObject =
				validationJSONObject.getJSONObject("expression");

			String expressionValue = expressionJSONObject.getString("value");

			if (Validator.isNull(expressionValue)) {
				fieldJSONObject.remove("validation");

				continue;
			}

			if (Validator.isNotNull(expressionJSONObject.getString("name"))) {
				continue;
			}

			expressionJSONObject.put(
				"name", _getExpressionName(expressionValue));

			String parameterValue = _getParameterValueFromExpression(
				expressionValue);

			_addParameterValue(
				parameterValue, validationJSONObject,
				definitionJSONObject.getString("defaultLanguageId"));

			if (Validator.isNull(parameterValue)) {
				continue;
			}

			expressionJSONObject.put(
				"value",
				StringUtil.replace(
					expressionValue, parameterValue, "{parameter}"));
		}

		return definitionJSONObject.toString();
	}

	private final JSONFactory _jsonFactory;

}