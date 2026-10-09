/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_8_0;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Marcos Martins
 */
public class DDMContentUpgradeProcess extends UpgradeProcess {

	public DDMContentUpgradeProcess(
		DDMFormDeserializer ddmFormDeserializer, JSONFactory jsonFactory) {

		_ddmFormDeserializer = ddmFormDeserializer;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DDMContent.ctCollectionId, DDMContent.contentId, ",
					"DDMContent.data_, ",
					"DDMStructureVersion.structureVersionId, ",
					"DDMStructureVersion.definition from DDMContent inner ",
					"join DDMFormInstanceRecordVersion on ",
					"DDMContent.contentId = ",
					"DDMFormInstanceRecordVersion.storageId inner join ",
					"DDMFormInstanceVersion on ",
					"DDMFormInstanceRecordVersion.formInstanceId = ",
					"DDMFormInstanceVersion.formInstanceId and ",
					"DDMFormInstanceRecordVersion.formInstanceVersion = ",
					"DDMFormInstanceVersion.version inner join ",
					"DDMStructureVersion on ",
					"DDMFormInstanceVersion.structureVersionId = ",
					"DDMStructureVersion.structureVersionId"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMContent set data_ = ? where ctCollectionId = " +
						"? and contentId = ?")) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
						_ddmFormDeserializer,
						resultSet.getString("definition"));

					List<DDMFormField> fieldSetDDMFormFields = ListUtil.filter(
						ddmForm.getDDMFormFields(),
						ddmFormField -> Objects.equals(
							ddmFormField.getType(), "fieldset"));

					if (fieldSetDDMFormFields.isEmpty()) {
						continue;
					}

					String data = resultSet.getString("data_");

					String newData = _upgradeDDMContentData(
						data, fieldSetDDMFormFields);

					if (data.equals(newData)) {
						continue;
					}

					preparedStatement2.setString(1, newData);
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("contentId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private boolean _hasDDMFormField(
		DDMFormField ddmFormField, JSONArray fieldValuesJSONArray) {

		String ddmFormFieldName = ddmFormField.getName();

		Iterator<JSONObject> iterator = fieldValuesJSONArray.iterator();

		while (iterator.hasNext()) {
			JSONObject fieldValueJSONObject = iterator.next();

			if (ddmFormFieldName.equals(
					fieldValueJSONObject.getString("name"))) {

				return true;
			}
		}

		return false;
	}

	private String _upgradeDDMContentData(
			String data, List<DDMFormField> fieldSetDDMFormFields)
		throws Exception {

		JSONObject dataJSONObject = _jsonFactory.createJSONObject(data);

		JSONArray fieldValuesJSONArray = dataJSONObject.getJSONArray(
			"fieldValues");

		for (DDMFormField fieldSetDDMFormField : fieldSetDDMFormFields) {
			if (_hasDDMFormField(fieldSetDDMFormField, fieldValuesJSONArray)) {
				continue;
			}

			Map<String, DDMFormField> nestedDDMFormFieldsMap =
				fieldSetDDMFormField.getNestedDDMFormFieldsMap();

			Set<String> nestedDDMFormFieldNames =
				nestedDDMFormFieldsMap.keySet();

			JSONArray nestedFieldValuesJSONArray =
				_jsonFactory.createJSONArray();

			JSONArray newFieldValuesJSONArray = _jsonFactory.createJSONArray();

			Iterator<JSONObject> iterator = fieldValuesJSONArray.iterator();

			while (iterator.hasNext()) {
				JSONObject fieldValueJSONObject = iterator.next();

				if (nestedDDMFormFieldNames.contains(
						fieldValueJSONObject.getString("name"))) {

					nestedFieldValuesJSONArray.put(fieldValueJSONObject);
				}
				else {
					newFieldValuesJSONArray.put(fieldValueJSONObject);
				}
			}

			newFieldValuesJSONArray.put(
				JSONUtil.put(
					"instanceId", StringUtil.randomString(8)
				).put(
					"name", fieldSetDDMFormField.getName()
				).put(
					"nestedFieldValues", nestedFieldValuesJSONArray
				));

			fieldValuesJSONArray = newFieldValuesJSONArray;
		}

		dataJSONObject.put("fieldValues", fieldValuesJSONArray);

		return dataJSONObject.toString();
	}

	private final DDMFormDeserializer _ddmFormDeserializer;
	private final JSONFactory _jsonFactory;

}