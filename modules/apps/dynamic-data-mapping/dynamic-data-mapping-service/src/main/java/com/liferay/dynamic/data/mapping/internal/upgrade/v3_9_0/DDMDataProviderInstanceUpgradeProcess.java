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
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

		Map<Locale, String> valuesMap = new HashMap<>(value.getValues());

		for (Map.Entry<Locale, String> entry : valuesMap.entrySet()) {
			String[] valueStringParts = StringUtil.split(
				entry.getValue(), CharPool.SEMICOLON);

			if ((valueStringParts.length != 2) ||
				!_legacyOutputPathTypes.contains(valueStringParts[1])) {

				continue;
			}

			String outputPathValue = valueStringParts[0];

			if (valueStringParts[1].equals("List")) {
				outputPathValue = StringBundler.concat(
					StringPool.DOLLAR, StringPool.DOUBLE_PERIOD,
					outputPathValue);
			}

			value.addString(entry.getKey(), outputPathValue);
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

	private static final Set<String> _legacyOutputPathTypes = SetUtil.fromArray(
		"List", "Number", "Text");

	private final DDMFormValuesDeserializer _ddmFormValuesDeserializer;
	private final DDMFormValuesSerializer _ddmFormValuesSerializer;
	private final ServiceTrackerMap<String, DDMDataProviderSettingsProvider>
		_serviceTrackerMap;

}