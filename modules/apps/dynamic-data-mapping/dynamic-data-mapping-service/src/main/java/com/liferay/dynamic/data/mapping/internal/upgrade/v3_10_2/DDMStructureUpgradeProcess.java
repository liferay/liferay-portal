/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_10_2;

import com.liferay.dynamic.data.mapping.internal.upgrade.v3_10_2.util.DDMFormFieldUpgradeProcessUtil;
import com.liferay.dynamic.data.mapping.internal.util.ExpressionParameterValueExtractor;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Carolina Barbosa
 * @author Renato Rego
 */
public class DDMStructureUpgradeProcess extends UpgradeProcess {

	public DDMStructureUpgradeProcess(
		DDMFormDeserializer ddmFormDeserializer,
		DDMFormLayoutDeserializer ddmFormLayoutDeserializer,
		DDMFormLayoutSerializer ddmFormLayoutSerializer,
		DDMFormSerializer ddmFormSerializer, JSONFactory jsonFactory) {

		_ddmFormDeserializer = ddmFormDeserializer;
		_ddmFormLayoutDeserializer = ddmFormLayoutDeserializer;
		_ddmFormLayoutSerializer = ddmFormLayoutSerializer;
		_ddmFormSerializer = ddmFormSerializer;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeDDMStructureLayout();

		_upgradeDDMStructureVersion();

		_upgradeDDMStructure();
	}

	protected String upgradeDDMStructureLayoutDefinition(String definition)
		throws Exception {

		DDMFormLayout ddmFormLayout = DDMFormLayoutDeserializeUtil.deserialize(
			_ddmFormLayoutDeserializer, definition);

		_normalizeDDMFormLayout(ddmFormLayout);

		DDMFormLayoutSerializerSerializeResponse
			ddmFormLayoutSerializerSerializeResponse =
				_ddmFormLayoutSerializer.serialize(
					DDMFormLayoutSerializerSerializeRequest.Builder.newBuilder(
						ddmFormLayout
					).build());

		return ddmFormLayoutSerializerSerializeResponse.getContent();
	}

	protected String upgradeDDMStructureVersionDefinition(String definition)
		throws Exception {

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormDeserializer, definition);

		_normalizeDDMFormRules(ddmForm);

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			_normalizeDDMFormField(ddmFormField);
		}

		return DDMFormSerializeUtil.serialize(ddmForm, _ddmFormSerializer);
	}

	private String _getNormalizedDDMFormRuleExpression(
		Map<String, DDMFormField> ddmFormFieldsMap,
		String ddmFormRuleExpression) {

		StringBundler sb = new StringBundler();

		List<String> parameterValues =
			ExpressionParameterValueExtractor.extractParameterValues(
				ddmFormRuleExpression);

		for (int i = 0; i < parameterValues.size(); i++) {
			String parameterValue = parameterValues.get(i);

			String unquotedParameterValue = StringUtil.unquote(parameterValue);

			if (!ddmFormFieldsMap.containsKey(unquotedParameterValue) &&
				((i <= 0) ||
				 !_isDDMFormFieldOptionValue(
					 ddmFormFieldsMap, unquotedParameterValue,
					 StringUtil.unquote(parameterValues.get(i - 1))))) {

				continue;
			}

			int index = ddmFormRuleExpression.indexOf(parameterValue);

			sb.append(ddmFormRuleExpression.substring(0, index));

			sb.append(
				StringUtil.replace(
					parameterValue, unquotedParameterValue,
					DDMFormFieldUpgradeProcessUtil.getNormalizedName(
						unquotedParameterValue)));

			ddmFormRuleExpression = ddmFormRuleExpression.substring(
				index + parameterValue.length());
		}

		sb.append(ddmFormRuleExpression);

		return sb.toString();
	}

	private boolean _isDDMFormFieldOptionValue(
		Map<String, DDMFormField> ddmFormFieldsMap, String operand,
		String previousOperand) {

		DDMFormField ddmFormField = ddmFormFieldsMap.get(previousOperand);

		if ((ddmFormField == null) ||
			!DDMFormFieldUpgradeProcessUtil.isDDMFormFieldWithOptions(
				ddmFormField.getType())) {

			return false;
		}

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		if (ddmFormFieldOptions == null) {
			return false;
		}

		Set<String> ddmFormFieldOptionsValues =
			ddmFormFieldOptions.getOptionsValues();

		return ddmFormFieldOptionsValues.contains(operand);
	}

	private void _normalizeDDMFormField(DDMFormField ddmFormField)
		throws Exception {

		if (StringUtil.equals(ddmFormField.getType(), "fieldset")) {
			_normalizeDDMFormFieldFieldset(ddmFormField);
		}

		_normalizeDDMFormFieldOptions(ddmFormField);
		_normalizeDDMFormFieldValidationExpression(
			ddmFormField.getDDMFormFieldValidation(), ddmFormField.getName());

		if (DDMFormFieldUpgradeProcessUtil.isDDMFormFieldWithOptions(
				ddmFormField.getType())) {

			_normalizePredefinedValue(ddmFormField.getPredefinedValue());
		}

		ddmFormField.setFieldReference(
			DDMFormFieldUpgradeProcessUtil.getNormalizedName(
				ddmFormField.getFieldReference()));
		ddmFormField.setName(
			DDMFormFieldUpgradeProcessUtil.getNormalizedName(
				ddmFormField.getName()));

		for (DDMFormField nestedDDMFormField :
				ddmFormField.getNestedDDMFormFields()) {

			_normalizeDDMFormField(nestedDDMFormField);
		}
	}

	private void _normalizeDDMFormFieldFieldset(DDMFormField ddmFormField)
		throws Exception {

		JSONArray rowsJSONArray = _jsonFactory.createJSONArray(
			GetterUtil.getString(ddmFormField.getProperty("rows")));

		for (int i = 0; i < rowsJSONArray.length(); i++) {
			JSONObject rowJSONObject = rowsJSONArray.getJSONObject(i);

			JSONArray columnsJSONArray = rowJSONObject.getJSONArray("columns");

			for (int j = 0; j < columnsJSONArray.length(); j++) {
				JSONObject columnJSONObject = columnsJSONArray.getJSONObject(j);

				columnJSONObject.put(
					"fields",
					DDMFormFieldUpgradeProcessUtil.getNormalizedJSONArray(
						columnJSONObject.getJSONArray("fields")));
			}
		}

		ddmFormField.setProperty("rows", rowsJSONArray);
	}

	private void _normalizeDDMFormFieldOptions(DDMFormField ddmFormField)
		throws Exception {

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		if (ddmFormFieldOptions == null) {
			return;
		}

		DDMFormFieldOptions normalizedDDMFormFieldOptions =
			new DDMFormFieldOptions(ddmFormFieldOptions.getDefaultLocale());

		Map<String, LocalizedValue> normalizedOptions =
			normalizedDDMFormFieldOptions.getOptions();
		Map<String, String> normalizedOptionsReferences =
			normalizedDDMFormFieldOptions.getOptionsReferences();

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			String normalizedOptionValue =
				DDMFormFieldUpgradeProcessUtil.getNormalizedName(optionValue);

			normalizedOptions.put(
				normalizedOptionValue,
				ddmFormFieldOptions.getOptionLabels(optionValue));
			normalizedOptionsReferences.put(
				normalizedOptionValue,
				DDMFormFieldUpgradeProcessUtil.getNormalizedName(
					ddmFormFieldOptions.getOptionReference(optionValue)));
		}

		ddmFormField.setDDMFormFieldOptions(normalizedDDMFormFieldOptions);
	}

	private void _normalizeDDMFormFieldValidationExpression(
			DDMFormFieldValidation ddmFormFieldValidation,
			String ddmFormFieldName)
		throws Exception {

		if (ddmFormFieldValidation == null) {
			return;
		}

		DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
			ddmFormFieldValidation.getDDMFormFieldValidationExpression();

		String ddmFormFieldValidationExpressionValue =
			ddmFormFieldValidationExpression.getValue();

		if (!ddmFormFieldValidationExpressionValue.contains(ddmFormFieldName)) {
			return;
		}

		ddmFormFieldValidationExpression.setValue(
			StringUtil.replace(
				ddmFormFieldValidationExpressionValue, ddmFormFieldName,
				DDMFormFieldUpgradeProcessUtil.getNormalizedName(
					ddmFormFieldName)));
	}

	private void _normalizeDDMFormLayout(DDMFormLayout ddmFormLayout) {
		for (DDMFormLayoutPage ddmFormLayoutPage :
				ddmFormLayout.getDDMFormLayoutPages()) {

			for (DDMFormLayoutRow ddmFormLayoutRow :
					ddmFormLayoutPage.getDDMFormLayoutRows()) {

				for (DDMFormLayoutColumn ddmFormLayoutColumn :
						ddmFormLayoutRow.getDDMFormLayoutColumns()) {

					List<String> ddmFormFieldNames =
						ddmFormLayoutColumn.getDDMFormFieldNames();

					if (ddmFormFieldNames.isEmpty()) {
						continue;
					}

					ddmFormLayoutColumn.setDDMFormFieldNames(
						TransformUtil.transform(
							ddmFormFieldNames,
							ddmFormFieldName ->
								DDMFormFieldUpgradeProcessUtil.
									getNormalizedName(ddmFormFieldName)));
				}
			}
		}
	}

	private void _normalizeDDMFormRuleActions(
		Map<String, DDMFormField> ddmFormFieldsMap, DDMFormRule ddmFormRule) {

		ddmFormRule.setActions(
			TransformUtil.transform(
				ddmFormRule.getActions(),
				ddmFormRuleAction -> _getNormalizedDDMFormRuleExpression(
					ddmFormFieldsMap, ddmFormRuleAction)));
	}

	private void _normalizeDDMFormRules(DDMForm ddmForm) throws Exception {
		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormRule ddmFormRule : ddmForm.getDDMFormRules()) {
			_normalizeDDMFormRuleActions(ddmFormFieldsMap, ddmFormRule);

			ddmFormRule.setCondition(
				_getNormalizedDDMFormRuleExpression(
					ddmFormFieldsMap, ddmFormRule.getCondition()));
		}
	}

	private void _normalizePredefinedValue(LocalizedValue localizedValue)
		throws Exception {

		if (localizedValue == null) {
			return;
		}

		Set<Locale> availableLocales = new HashSet<>(
			localizedValue.getAvailableLocales());

		for (Locale availableLocale : availableLocales) {
			localizedValue.addString(
				availableLocale,
				String.valueOf(
					DDMFormFieldUpgradeProcessUtil.getNormalizedJSONArray(
						_jsonFactory.createJSONArray(
							localizedValue.getString(availableLocale)))));
		}
	}

	private void _upgradeDDMStructure() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select DDMStructure.structureId, ",
						"DDMStructureVersion.definition from DDMStructure ",
						"inner join DDMStructureVersion on ",
						"DDMStructure.structureId = ",
						"DDMStructureVersion.structureId where ",
						"DDMStructure.version = DDMStructureVersion.version ",
						"and DDMStructure.classNameId = ?"));
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?")) {

			selectPreparedStatement.setLong(
				1, PortalUtil.getClassNameId(DDMFormInstance.class.getName()));

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					updatePreparedStatement.setString(
						1, resultSet.getString("definition"));
					updatePreparedStatement.setLong(
						2, resultSet.getLong("structureId"));

					updatePreparedStatement.addBatch();
				}

				updatePreparedStatement.executeBatch();
			}
		}
	}

	private void _upgradeDDMStructureLayout() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select DDMStructureLayout.structureLayoutId, ",
						"DDMStructureLayout.definition from ",
						"DDMStructureLayout inner join DDMStructureVersion on ",
						"DDMStructureLayout.structureVersionId = ",
						"DDMStructureVersion.structureVersionId inner join ",
						"DDMStructure on DDMStructure.structureId = ",
						"DDMStructureVersion.structureId where ",
						"DDMStructure.classNameId = ?"));
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureLayout set definition = ? where " +
						"structureLayoutId = ?")) {

			selectPreparedStatement.setLong(
				1, PortalUtil.getClassNameId(DDMFormInstance.class.getName()));

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String definition = resultSet.getString("definition");

					String newDefinition = upgradeDDMStructureLayoutDefinition(
						definition);

					if (StringUtil.equals(definition, newDefinition)) {
						continue;
					}

					updatePreparedStatement.setString(1, newDefinition);
					updatePreparedStatement.setLong(
						2, resultSet.getLong("structureLayoutId"));

					updatePreparedStatement.addBatch();
				}

				updatePreparedStatement.executeBatch();
			}
		}
	}

	private void _upgradeDDMStructureVersion() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select DDMStructureVersion.structureVersionId, ",
						"DDMStructureVersion.definition from DDMStructure ",
						"inner join DDMStructureVersion on ",
						"DDMStructure.structureId = ",
						"DDMStructureVersion.structureId where ",
						"DDMStructure.classNameId = ?"));
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"structureVersionId = ?")) {

			selectPreparedStatement.setLong(
				1, PortalUtil.getClassNameId(DDMFormInstance.class.getName()));

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String definition = resultSet.getString("definition");

					String newDefinition = upgradeDDMStructureVersionDefinition(
						definition);

					if (StringUtil.equals(definition, newDefinition)) {
						continue;
					}

					updatePreparedStatement.setString(1, newDefinition);
					updatePreparedStatement.setLong(
						2, resultSet.getLong("structureVersionId"));

					updatePreparedStatement.addBatch();
				}

				updatePreparedStatement.executeBatch();
			}
		}
	}

	private final DDMFormDeserializer _ddmFormDeserializer;
	private final DDMFormLayoutDeserializer _ddmFormLayoutDeserializer;
	private final DDMFormLayoutSerializer _ddmFormLayoutSerializer;
	private final DDMFormSerializer _ddmFormSerializer;
	private final JSONFactory _jsonFactory;

}