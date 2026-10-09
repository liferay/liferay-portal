/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_9_0;

import com.liferay.dynamic.data.mapping.data.provider.settings.DDMDataProviderSettingsProvider;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesSerializeUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Marcos Martins
 */
public class DDMDataProviderInstanceUpgradeProcess extends UpgradeProcess {

	public DDMDataProviderInstanceUpgradeProcess(
		ServiceTrackerMap<String, DDMDataProviderSettingsProvider>
			ddmDataProviderSettingsProviderServiceTracker,
		DDMFormValuesDeserializer ddmFormValuesDeserializer,
		DDMFormValuesSerializer ddmFormValuesSerializer) {

		_ddmFormValuesDeserializer = ddmFormValuesDeserializer;
		_ddmFormValuesSerializer = ddmFormValuesSerializer;

		_serviceTrackerMap = ddmDataProviderSettingsProviderServiceTracker;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, dataProviderInstanceId, definition, " +
					"type_ from DDMDataProviderInstance");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMDataProviderInstance set definition = ? where " +
						"ctCollectionId = ? and dataProviderInstanceId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				preparedStatement2.setString(
					1,
					_upgradeDataProviderInstanceDefinition(
						resultSet.getString("definition"),
						resultSet.getString("type_")));
				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(
					3, resultSet.getLong("dataProviderInstanceId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _updateNestedDDMFormFieldValue(
		DDMFormFieldValue nestedDDMFormFieldValue) {

		Value value = nestedDDMFormFieldValue.getValue();

		Map<Locale, String> valuesMap = value.getValues();

		for (Map.Entry<Locale, String> valueEntry : valuesMap.entrySet()) {
			String valueString = valueEntry.getValue();

			String[] valueStringParts = valueString.split(StringPool.SEMICOLON);

			if (valueStringParts.length <= 1) {
				continue;
			}

			String outputPathType = valueStringParts[1];
			String outputPathValue = valueStringParts[0];

			if (outputPathType.equals("List")) {
				outputPathValue = StringBundler.concat(
					StringPool.DOLLAR, StringPool.DOUBLE_PERIOD,
					outputPathValue);
			}

			valuesMap.put(valueEntry.getKey(), outputPathValue);
		}
	}

	private String _upgradeDataProviderInstanceDefinition(
			String dataProviderInstanceDefinition, String type)
		throws Exception {

		DDMDataProviderSettingsProvider ddmDataProviderSettingsProvider =
			_serviceTrackerMap.getService(type);

		DDMForm ddmForm = DDMFormFactory.create(
			ddmDataProviderSettingsProvider.getSettings());

		DDMFormValues ddmFormValues = DDMFormValuesDeserializeUtil.deserialize(
			dataProviderInstanceDefinition, ddmForm,
			_ddmFormValuesDeserializer);

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(false);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			"outputParameters");

		if (ddmFormFieldValues != null) {
			Map<String, DDMFormField> ddmFormFieldsMap =
				ddmForm.getDDMFormFieldsMap(false);

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				"outputParameters");

			Map<String, DDMFormField> nestedDDMFormFieldsMap =
				ddmFormField.getNestedDDMFormFieldsMap();

			for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
				ddmFormFieldValue.setNestedDDMFormFields(
					TransformUtil.transform(
						ddmFormFieldValue.getNestedDDMFormFieldValues(),
						nestedDDMFormFieldValue -> {
							String nestedDDMFormFieldValueName =
								nestedDDMFormFieldValue.getName();

							DDMFormField nestedDDMFormField =
								nestedDDMFormFieldsMap.get(
									nestedDDMFormFieldValueName);

							if (nestedDDMFormField == null) {
								return null;
							}

							if (nestedDDMFormFieldValueName.equals(
									"outputParameterPath")) {

								_updateNestedDDMFormFieldValue(
									nestedDDMFormFieldValue);
							}

							return nestedDDMFormFieldValue;
						}));
			}
		}

		return DDMFormValuesSerializeUtil.serialize(
			ddmFormValues, _ddmFormValuesSerializer);
	}

	private final DDMFormValuesDeserializer _ddmFormValuesDeserializer;
	private final DDMFormValuesSerializer _ddmFormValuesSerializer;
	private final ServiceTrackerMap<String, DDMDataProviderSettingsProvider>
		_serviceTrackerMap;

}