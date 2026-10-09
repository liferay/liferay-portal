/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_5;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author István András Dézsi
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

			preparedStatement1.setLong(1, getClassNameId());

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					String definition = resultSet.getString("definition");

					preparedStatement2.setString(
						1, _makeFieldsLocalizable(definition));

					long structureId = resultSet.getLong("structureId");

					preparedStatement2.setLong(2, structureId);

					preparedStatement2.addBatch();

					preparedStatement3.setLong(1, structureId);

					try (ResultSet resultSet2 =
							preparedStatement3.executeQuery()) {

						while (resultSet2.next()) {
							definition = resultSet2.getString("definition");

							preparedStatement4.setString(
								1, _makeFieldsLocalizable(definition));

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

	protected long getClassNameId() {
		return PortalUtil.getClassNameId(
			"com.liferay.dynamic.data.mapping.model.DDMFormInstance");
	}

	private boolean _hasValidation(JSONObject fieldJSONObject) {
		JSONObject validationJSONObject = fieldJSONObject.getJSONObject(
			"validation");

		if (validationJSONObject == null) {
			return false;
		}

		return true;
	}

	private void _makeFieldsLocalizable(
		JSONArray availableLanguageIdsJSONArray, JSONArray fieldsJSONArray) {

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			if (!_hasValidation(jsonObject)) {
				continue;
			}

			JSONObject validationJSONObject = jsonObject.getJSONObject(
				"validation");

			String originalValue = validationJSONObject.getString(
				"errorMessage");

			if (JSONUtil.isJSONObject(originalValue)) {
				continue;
			}

			Map<String, String> localizedValue = new HashMap<>();

			for (int j = 0; j < availableLanguageIdsJSONArray.length(); j++) {
				localizedValue.put(
					availableLanguageIdsJSONArray.getString(j), originalValue);
			}

			validationJSONObject.put("errorMessage", localizedValue);

			JSONArray nestedFieldsJSONArray = jsonObject.getJSONArray(
				"nestedFields");

			if (nestedFieldsJSONArray == null) {
				continue;
			}

			_makeFieldsLocalizable(
				availableLanguageIdsJSONArray, nestedFieldsJSONArray);
		}
	}

	private String _makeFieldsLocalizable(String definition)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(definition);

		JSONArray availableLanguageIdsJSONArray = jsonObject.getJSONArray(
			"availableLanguageIds");

		JSONArray fieldsJSONArray = jsonObject.getJSONArray("fields");

		_makeFieldsLocalizable(availableLanguageIdsJSONArray, fieldsJSONArray);

		return jsonObject.toString();
	}

	private final JSONFactory _jsonFactory;

}