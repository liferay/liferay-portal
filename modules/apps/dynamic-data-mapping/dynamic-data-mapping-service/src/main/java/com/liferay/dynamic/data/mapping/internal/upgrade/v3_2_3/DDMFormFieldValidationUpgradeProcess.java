/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_2_3;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
					long structureId = resultSet.getLong("structureId");

					String definition = resultSet.getString("definition");

					JSONObject jsonObject1 = _jsonFactory.createJSONObject(
						definition);

					if (_upgradeValidation(
							jsonObject1.getJSONArray("fields"))) {

						preparedStatement2.setString(1, jsonObject1.toString());
						preparedStatement2.setLong(2, structureId);

						preparedStatement2.addBatch();
					}

					preparedStatement3.setLong(1, structureId);

					try (ResultSet resultSet2 =
							preparedStatement3.executeQuery()) {

						while (resultSet2.next()) {
							definition = resultSet2.getString("definition");

							JSONObject jsonObject2 =
								_jsonFactory.createJSONObject(definition);

							if (!_upgradeValidation(
									jsonObject2.getJSONArray("fields"))) {

								continue;
							}

							preparedStatement4.setString(
								1, jsonObject2.toString());

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

	private String _replaceFieldName(
		String expression, String fieldName, String regex) {

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(expression);

		matcher.find();

		String oldFieldName = matcher.group(1);

		return StringUtil.replace(expression, oldFieldName, fieldName);
	}

	private String _upgradeExpression(String expression, String fieldName) {
		if (expression.matches("NOT\\(contains\\((.+), \"(.*)\"\\)\\)")) {
			expression = _replaceFieldName(
				expression, fieldName, "NOT\\(contains\\((.+), \"(.*)\"\\)\\)");
		}
		else if (expression.matches("contains\\((.+), \"(.*)\"\\)")) {
			expression = _replaceFieldName(
				expression, fieldName, "contains\\((.+), \"(.*)\"\\)");
		}
		else if (expression.matches("isURL\\((.+)\\)")) {
			expression = _replaceFieldName(
				expression, fieldName, "isURL\\((.+)\\)");
		}
		else if (expression.matches("isEmailAddress\\((.+)\\)")) {
			expression = _replaceFieldName(
				expression, fieldName, "isEmailAddress\\((.+)\\)");
		}
		else if (expression.matches("match\\((.+), \"(.*)\"\\)")) {
			expression = _replaceFieldName(
				expression, fieldName, "match\\((.+), \"(.*)\"\\)");
		}
		else if (expression.matches("(.+)<(\\d+\\.?\\d*)?")) {
			expression = _replaceFieldName(
				expression, fieldName, "(.+)<(\\d+\\.?\\d*)?");
		}
		else if (expression.matches("(.+)<=(\\d+\\.?\\d*)?")) {
			expression = _replaceFieldName(
				expression, fieldName, "(.+)<=(\\d+\\.?\\d*)?");
		}
		else if (expression.matches("(.+)==(\\d+\\.?\\d*)?")) {
			expression = _replaceFieldName(
				expression, fieldName, "(.+)==(\\d+\\.?\\d*)?");
		}
		else if (expression.matches("(.+)>(\\d+\\.?\\d*)?")) {
			expression = _replaceFieldName(
				expression, fieldName, "(.+)>(\\d+\\.?\\d*)?");
		}
		else if (expression.matches("(.+)>=(\\d+\\.?\\d*)?")) {
			expression = _replaceFieldName(
				expression, fieldName, "(.+)>=(\\d+\\.?\\d*)?");
		}

		return expression;
	}

	private boolean _upgradeValidation(JSONArray fieldsJSONArray) {
		boolean upgraded = false;

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			JSONObject validationJSONObject = jsonObject.getJSONObject(
				"validation");

			if (validationJSONObject != null) {
				JSONObject expressionJSONObject =
					validationJSONObject.getJSONObject("expression");

				String value = expressionJSONObject.getString("value");

				String upgradedValue = _upgradeExpression(
					value, jsonObject.getString("name"));

				expressionJSONObject.put("value", upgradedValue);

				upgraded = upgraded || !StringUtil.equals(value, upgradedValue);
			}

			JSONArray nestedFieldsJSONArray = jsonObject.getJSONArray(
				"nestedFields");

			if (nestedFieldsJSONArray == null) {
				continue;
			}

			upgraded = _upgradeValidation(nestedFieldsJSONArray) || upgraded;
		}

		return upgraded;
	}

	private final JSONFactory _jsonFactory;

}