/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.dto.v2_0.util;

import com.google.gson.Gson;

import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutColumn;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutPage;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutRow;
import com.liferay.data.engine.rest.dto.v2_0.DataRule;
import com.liferay.dynamic.data.mapping.form.builder.rule.DDMFormRuleDeserializer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.spi.converter.SPIDDMFormRuleConverter;
import com.liferay.dynamic.data.mapping.spi.converter.model.SPIDDMFormRule;
import com.liferay.dynamic.data.mapping.util.SettingsDDMFormFieldsUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Jeyvison Nascimento
 */
public class DataLayoutUtil {

	public static String serialize(
			DataLayout dataLayout, DDMForm ddmForm,
			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
			DDMFormLayoutSerializer ddmFormLayoutSerializer,
			DDMFormRuleDeserializer ddmFormRuleDeserializer)
		throws Exception {

		DDMFormLayoutSerializerSerializeRequest.Builder builder =
			DDMFormLayoutSerializerSerializeRequest.Builder.newBuilder(
				toDDMFormLayout(
					dataLayout, ddmForm, ddmFormFieldTypeServicesRegistry,
					ddmFormRuleDeserializer));

		DDMFormLayoutSerializerSerializeResponse
			ddmFormLayoutSerializerSerializeResponse =
				ddmFormLayoutSerializer.serialize(builder.build());

		return ddmFormLayoutSerializerSerializeResponse.getContent();
	}

	public static DDMFormLayout toDDMFormLayout(
			DataLayout dataLayout, DDMForm ddmForm,
			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
			DDMFormRuleDeserializer ddmFormRuleDeserializer)
		throws Exception {

		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		ddmFormLayout.setDDMFormFields(
			_toDDMFormFields(
				dataLayout.getDataLayoutFields(),
				ddmForm.getDDMFormFieldsMap(true),
				ddmFormFieldTypeServicesRegistry));
		ddmFormLayout.setDDMFormLayoutPages(
			_toDDMFormLayoutPages(
				dataLayout.getDataLayoutPages(), ddmForm.getDefaultLocale()));
		ddmFormLayout.setDefaultLocale(ddmForm.getDefaultLocale());
		ddmFormLayout.setPaginationMode(dataLayout.getPaginationMode());
		ddmFormLayout.setDDMFormRules(
			ddmFormRuleDeserializer.deserialize(
				ddmForm,
				JSONUtil.toJSONArray(
					dataLayout.getDataRules(), rule -> _serializeRule(rule))));

		return ddmFormLayout;
	}

	public static DataLayout toDataLayout(
			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
			DDMFormLayout ddmFormLayout,
			SPIDDMFormRuleConverter spiDDMFormRuleConverter)
		throws Exception {

		return new DataLayout() {
			{
				setDataLayoutFields(
					() -> _toDataLayoutFields(
						ddmFormLayout.getDDMFormFields(),
						ddmFormFieldTypeServicesRegistry));
				setDataLayoutPages(
					() -> _toDataLayoutPages(
						ddmFormLayout.getDDMFormLayoutPages()));
				setDataRules(
					() -> _toDataRules(
						ddmFormLayout.getDDMFormRules(),
						spiDDMFormRuleConverter));
				setPaginationMode(ddmFormLayout::getPaginationMode);
			}
		};
	}

	public static DataLayout toDataLayout(
			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
			DDMStructureLayout ddmStructureLayout,
			SPIDDMFormRuleConverter spiDDMFormRuleConverter)
		throws Exception {

		if (ddmStructureLayout == null) {
			return null;
		}

		DataLayout dataLayout = toDataLayout(
			ddmFormFieldTypeServicesRegistry,
			ddmStructureLayout.getDDMFormLayout(), spiDDMFormRuleConverter);

		dataLayout.setDateCreated(ddmStructureLayout::getCreateDate);
		dataLayout.setDataDefinitionId(ddmStructureLayout::getDDMStructureId);
		dataLayout.setDataLayoutKey(ddmStructureLayout::getStructureLayoutKey);
		dataLayout.setDateModified(ddmStructureLayout::getModifiedDate);
		dataLayout.setDescription(
			() -> LocalizedValueUtil.toStringObjectMap(
				ddmStructureLayout.getDescriptionMap()));
		dataLayout.setId(ddmStructureLayout::getStructureLayoutId);
		dataLayout.setName(
			() -> LocalizedValueUtil.toStringObjectMap(
				ddmStructureLayout.getNameMap()));
		dataLayout.setSiteId(ddmStructureLayout::getGroupId);
		dataLayout.setUserId(ddmStructureLayout::getUserId);

		return dataLayout;
	}

	private static JSONObject _serializeRule(DataRule dataRule)
		throws JSONException {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Gson gson = new Gson();

		return jsonObject.put(
			"actions", dataRule.getActions()
		).put(
			"conditions", dataRule.getConditions()
		).put(
			"logical-operator", dataRule.getLogicalOperator()
		).put(
			"name",
			JSONFactoryUtil.createJSONObject(gson.toJson(dataRule.getName()))
		);
	}

	private static List<DDMFormField> _toDDMFormFields(
		Map<String, Object> dataLayoutFields,
		Map<String, DDMFormField> ddmFormFieldsMap,
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry) {

		List<DDMFormField> ddmFormFields = new ArrayList<>();

		if (MapUtil.isEmpty(dataLayoutFields)) {
			return ddmFormFields;
		}

		dataLayoutFields.forEach(
			(key, value) -> {
				DDMFormField ddmFormField = new DDMFormField();

				ddmFormField.setName(key);

				Map<String, Object> properties = ddmFormField.getProperties();

				DDMFormField ddmFormDDMFormField = ddmFormFieldsMap.get(key);

				Map<String, DDMFormField> settingsDDMFormFieldsMap =
					SettingsDDMFormFieldsUtil.getSettingsDDMFormFields(
						ddmFormFieldTypeServicesRegistry,
						ddmFormDDMFormField.getType());

				Map<String, Object> dataLayoutField =
					(Map<String, Object>)value;

				dataLayoutField.forEach(
					(keyProperty, valueProperty) -> {
						DDMFormField settingsDDMFormField =
							settingsDDMFormFieldsMap.get(keyProperty);

						if (settingsDDMFormField.isLocalizable()) {
							properties.put(
								keyProperty,
								LocalizedValueUtil.toLocalizedValue(
									(Map<String, Object>)valueProperty));
						}
						else {
							properties.put(keyProperty, valueProperty);
						}
					});

				ddmFormField.setType(ddmFormDDMFormField.getType());

				ddmFormFields.add(ddmFormField);
			});

		return ddmFormFields;
	}

	private static DDMFormLayoutColumn _toDDMFormLayoutColumn(
		DataLayoutColumn dataLayoutColumn) {

		DDMFormLayoutColumn ddmFormLayoutColumn = new DDMFormLayoutColumn();

		ddmFormLayoutColumn.setDDMFormFieldNames(
			Arrays.asList(dataLayoutColumn.getFieldNames()));
		ddmFormLayoutColumn.setSize(dataLayoutColumn.getColumnSize());

		return ddmFormLayoutColumn;
	}

	private static List<DDMFormLayoutColumn> _toDDMFormLayoutColumns(
		DataLayoutColumn[] dataLayoutColumns) {

		if (ArrayUtil.isEmpty(dataLayoutColumns)) {
			return Collections.emptyList();
		}

		return TransformUtil.transformToList(
			dataLayoutColumns,
			dataLayoutColumn -> _toDDMFormLayoutColumn(dataLayoutColumn));
	}

	private static DDMFormLayoutPage _toDDMFormLayoutPage(
		DataLayoutPage dataLayoutPage, Locale locale) {

		DDMFormLayoutPage ddmFormLayoutPage = new DDMFormLayoutPage();

		ddmFormLayoutPage.setDDMFormLayoutRows(
			_toDDMFormLayoutRows(dataLayoutPage.getDataLayoutRows()));
		ddmFormLayoutPage.setDescription(
			LocalizedValueUtil.toLocalizedValue(
				dataLayoutPage.getDescription(), locale));
		ddmFormLayoutPage.setTitle(
			LocalizedValueUtil.toLocalizedValue(
				dataLayoutPage.getTitle(), locale));

		return ddmFormLayoutPage;
	}

	private static List<DDMFormLayoutPage> _toDDMFormLayoutPages(
		DataLayoutPage[] dataLayoutPages, Locale locale) {

		if (ArrayUtil.isEmpty(dataLayoutPages)) {
			return Collections.emptyList();
		}

		return TransformUtil.transformToList(
			dataLayoutPages,
			dataLayoutPage -> _toDDMFormLayoutPage(dataLayoutPage, locale));
	}

	private static DDMFormLayoutRow _toDDMFormLayoutRow(
		DataLayoutRow dataLayoutRow) {

		DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

		ddmFormLayoutRow.setDDMFormLayoutColumns(
			_toDDMFormLayoutColumns(dataLayoutRow.getDataLayoutColumns()));

		return ddmFormLayoutRow;
	}

	private static List<DDMFormLayoutRow> _toDDMFormLayoutRows(
		DataLayoutRow[] dataLayoutRows) {

		if (ArrayUtil.isEmpty(dataLayoutRows)) {
			return Collections.emptyList();
		}

		return TransformUtil.transformToList(
			dataLayoutRows,
			dataLayoutRow -> _toDDMFormLayoutRow(dataLayoutRow));
	}

	private static DataLayoutColumn _toDataLayoutColumn(
		DDMFormLayoutColumn ddmFormLayoutColumn) {

		return new DataLayoutColumn() {
			{
				setColumnSize(ddmFormLayoutColumn::getSize);
				setFieldNames(
					() -> ArrayUtil.toStringArray(
						ddmFormLayoutColumn.getDDMFormFieldNames()));
			}
		};
	}

	private static DataLayoutColumn[] _toDataLayoutColumns(
		List<DDMFormLayoutColumn> ddmFormLayoutColumns) {

		if (ListUtil.isEmpty(ddmFormLayoutColumns)) {
			return new DataLayoutColumn[0];
		}

		return TransformUtil.transformToArray(
			ddmFormLayoutColumns,
			ddmFormLayoutColumn -> _toDataLayoutColumn(ddmFormLayoutColumn),
			DataLayoutColumn.class);
	}

	private static Map<String, Object> _toDataLayoutFields(
		List<DDMFormField> ddmFormFields,
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry) {

		Map<String, Object> dataLayoutFields = new HashMap<>();

		ddmFormFields.forEach(
			ddmFormField -> {
				Map<String, Object> properties = new HashMap<>();

				Map<String, DDMFormField> settingsDDMFormFieldsMap =
					SettingsDDMFormFieldsUtil.getSettingsDDMFormFields(
						ddmFormFieldTypeServicesRegistry,
						ddmFormField.getType());

				List<DDMFormField> visualPropertiesDDMFormFields =
					ListUtil.filter(
						new ArrayList<>(settingsDDMFormFieldsMap.values()),
						DDMFormField::isVisualProperty);

				visualPropertiesDDMFormFields.forEach(
					visualProperty -> {
						if (visualProperty.isLocalizable()) {
							properties.put(
								visualProperty.getName(),
								LocalizedValueUtil.toLocalizedValuesMap(
									(LocalizedValue)ddmFormField.getProperty(
										visualProperty.getName())));
						}
						else {
							properties.put(
								visualProperty.getName(),
								ddmFormField.getProperty(
									visualProperty.getName()));
						}
					});

				dataLayoutFields.put(ddmFormField.getName(), properties);
			});

		return dataLayoutFields;
	}

	private static DataLayoutPage _toDataLayoutPage(
		DDMFormLayoutPage ddmFormLayoutPage) {

		return new DataLayoutPage() {
			{
				setDataLayoutRows(
					() -> _toDataLayoutRows(
						ddmFormLayoutPage.getDDMFormLayoutRows()));
				setDescription(
					() -> LocalizedValueUtil.toLocalizedValuesMap(
						ddmFormLayoutPage.getDescription()));
				setTitle(
					() -> LocalizedValueUtil.toLocalizedValuesMap(
						ddmFormLayoutPage.getTitle()));
			}
		};
	}

	private static DataLayoutPage[] _toDataLayoutPages(
		List<DDMFormLayoutPage> ddmFormLayoutPages) {

		if (ListUtil.isEmpty(ddmFormLayoutPages)) {
			return new DataLayoutPage[0];
		}

		return TransformUtil.transformToArray(
			ddmFormLayoutPages,
			ddmFormLayoutPage -> _toDataLayoutPage(ddmFormLayoutPage),
			DataLayoutPage.class);
	}

	private static DataLayoutRow _toDataLayoutRow(
		DDMFormLayoutRow ddmFormLayoutRow) {

		return new DataLayoutRow() {
			{
				setDataLayoutColumns(
					() -> _toDataLayoutColumns(
						ddmFormLayoutRow.getDDMFormLayoutColumns()));
			}
		};
	}

	private static DataLayoutRow[] _toDataLayoutRows(
		List<DDMFormLayoutRow> ddmFormLayoutRows) {

		return TransformUtil.transformToArray(
			ddmFormLayoutRows,
			ddmFormLayoutRow -> _toDataLayoutRow(ddmFormLayoutRow),
			DataLayoutRow.class);
	}

	private static DataRule[] _toDataRules(
		List<DDMFormRule> ddmFormRules,
		SPIDDMFormRuleConverter spiDDMFormRuleConverter) {

		DataRule[] dataRules = new DataRule[0];

		for (SPIDDMFormRule spiDDMFormRule :
				spiDDMFormRuleConverter.convert(ddmFormRules)) {

			DataRule dataRule = new DataRule();

			Gson gson = new Gson();

			dataRule.setActions(
				() -> TransformUtil.transformToArray(
					spiDDMFormRule.getSPIDDMFormRuleActions(),
					spiDDMFormRuleAction -> gson.fromJson(
						JSONFactoryUtil.looseSerializeDeep(
							spiDDMFormRuleAction),
						Map.class),
					Map.class));
			dataRule.setConditions(
				() -> TransformUtil.transformToArray(
					spiDDMFormRule.getSPIDDMFormRuleConditions(),
					spiDDMFormRuleCondition -> gson.fromJson(
						JSONFactoryUtil.looseSerializeDeep(
							spiDDMFormRuleCondition),
						Map.class),
					Map.class));

			dataRule.setLogicalOperator(spiDDMFormRule::getLogicalOperator);
			dataRule.setName(
				() -> LocalizedValueUtil.toLocalizedValuesMap(
					spiDDMFormRule.getName()));

			dataRules = ArrayUtil.append(dataRules, dataRule);
		}

		return dataRules;
	}

}