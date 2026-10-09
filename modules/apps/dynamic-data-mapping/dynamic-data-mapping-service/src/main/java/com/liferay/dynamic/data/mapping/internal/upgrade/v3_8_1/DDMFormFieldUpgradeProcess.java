/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_8_1;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Iterator;

/**
 * @author Rodrigo Paulino
 */
public class DDMFormFieldUpgradeProcess extends UpgradeProcess {

	public DDMFormFieldUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, structureId, definition from " +
					"DDMStructure where classNameId = ? ");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"ctCollectionId = ? and structureId = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"select ctCollectionId, structureVersionId, definition from " +
					"DDMStructureVersion where structureId = ?");
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"ctCollectionId = ? and structureVersionId = ?")) {

			preparedStatement1.setLong(
				1,
				PortalUtil.getClassNameId(
					"com.liferay.dynamic.data.mapping.model.DDMFormInstance"));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					preparedStatement2.setString(
						1,
						_upgradeDefinition(resultSet.getString("definition")));
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));

					long structureId = resultSet.getLong("structureId");

					preparedStatement2.setLong(3, structureId);

					preparedStatement2.addBatch();

					preparedStatement3.setLong(1, structureId);

					try (ResultSet resultSet2 =
							preparedStatement3.executeQuery()) {

						while (resultSet2.next()) {
							preparedStatement4.setString(
								1,
								_upgradeDefinition(
									resultSet2.getString("definition")));
							preparedStatement4.setLong(
								2, resultSet2.getLong("ctCollectionId"));
							preparedStatement4.setLong(
								3, resultSet2.getLong("structureVersionId"));

							preparedStatement4.addBatch();
						}
					}
				}
			}

			preparedStatement2.executeBatch();

			preparedStatement4.executeBatch();
		}
	}

	private void _convertStringToJSONArray(JSONObject jsonObject, String key) {
		Object value = jsonObject.get(key);

		if (value instanceof String) {
			String valueString = (String)value;

			if (!valueString.startsWith(StringPool.OPEN_BRACKET) ||
				!valueString.endsWith(StringPool.CLOSE_BRACKET)) {

				jsonObject.put(
					key,
					JSONUtil.putAll(
						StringUtil.split(valueString)
					).toString());
			}
		}
	}

	private String _upgradeDefinition(String definition)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(definition);

		_upgradeFields(jsonObject.getJSONArray("fields"));

		return jsonObject.toString();
	}

	private void _upgradeFields(JSONArray fieldsJSONArray) {
		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			String type = jsonObject.getString("type");

			if (type.equals("checkbox_multiple") || type.equals("radio") ||
				type.equals("select")) {

				JSONObject predefinedValueJSONObject = jsonObject.getJSONObject(
					"predefinedValue");

				Iterator<String> iterator = predefinedValueJSONObject.keys();

				while (iterator.hasNext()) {
					String languageKey = iterator.next();

					_convertStringToJSONArray(
						predefinedValueJSONObject, languageKey);
				}
			}

			if (type.equals("select")) {
				_convertStringToJSONArray(jsonObject, "dataSourceType");
			}

			JSONArray nestedFieldsJSONArray = jsonObject.getJSONArray(
				"nestedFields");

			if (nestedFieldsJSONArray == null) {
				continue;
			}

			_upgradeFields(nestedFieldsJSONArray);
		}
	}

	private final JSONFactory _jsonFactory;

}