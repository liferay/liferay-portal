/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.data.engine.service.DEDataDefinitionFieldLinkLocalService;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
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
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.util.DDMDataDefinitionConverter;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = DDMDataDefinitionConverter.class)
public class DDMDataDefinitionConverterImpl
	implements DDMDataDefinitionConverter {

	@Override
	public DDMForm convertDDMFormDataDefinition(
		DDMForm ddmForm, long parentStructureId, long parentStructureLayoutId) {

		if (Objects.equals(ddmForm.getDefinitionSchemaVersion(), "2.0")) {
			return ddmForm;
		}

		ddmForm.setDefinitionSchemaVersion("2.0");

		_upgradeParentStructure(
			ddmForm, parentStructureId, parentStructureLayoutId);

		_upgradeFields(ddmForm.getDDMFormFields(), ddmForm.getDefaultLocale());

		return _upgradeNestedFields(ddmForm);
	}

	@Override
	public String convertDDMFormDataDefinition(
			String dataDefinition, long parentStructureId,
			long parentStructureLayoutId)
		throws Exception {

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormDeserializer, dataDefinition);

		ddmForm = convertDDMFormDataDefinition(
			ddmForm, parentStructureId, parentStructureLayoutId);

		return DDMFormSerializeUtil.serialize(ddmForm, _ddmFormSerializer);
	}

	@Override
	public String convertDDMFormDataDefinition(
			String dataDefinition, long groupId, long parentStructureId,
			long parentStructureLayoutId, long structureId)
		throws Exception {

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormDeserializer, dataDefinition);

		ddmForm = convertDDMFormDataDefinition(
			ddmForm, parentStructureId, parentStructureLayoutId);

		_addDataDefinitionFieldLinks(
			_portal.getClassNameId(DDMStructure.class), structureId,
			ddmForm.getDDMFormFields(), groupId);

		return DDMFormSerializeUtil.serialize(ddmForm, _ddmFormSerializer);
	}

	@Override
	public DDMFormLayout convertDDMFormLayoutDataDefinition(
		DDMForm ddmForm, DDMFormLayout ddmFormLayout) {

		ddmFormLayout.setDefinitionSchemaVersion("2.0");
		ddmFormLayout.setPaginationMode(DDMFormLayout.SINGLE_PAGE_MODE);

		for (DDMFormLayoutPage ddmFormLayoutPage :
				ddmFormLayout.getDDMFormLayoutPages()) {

			ddmFormLayoutPage.setDDMFormLayoutRows(
				TransformUtil.transform(
					ddmForm.getDDMFormFields(),
					ddmFormField -> {
						DDMFormLayoutRow ddmFormLayoutRow =
							new DDMFormLayoutRow();

						ddmFormLayoutRow.addDDMFormLayoutColumn(
							new DDMFormLayoutColumn(
								DDMFormLayoutColumn.FULL,
								ddmFormField.getName()));

						return ddmFormLayoutRow;
					}));

			LocalizedValue localizedValue = _upgradeLocalizedValue(
				ddmFormLayout.getAvailableLocales(),
				ddmFormLayout.getDefaultLocale(), "page",
				ddmFormLayoutPage.getTitle());

			ddmFormLayoutPage.setTitle(localizedValue);

			localizedValue = _upgradeLocalizedValue(
				ddmFormLayout.getAvailableLocales(),
				ddmFormLayout.getDefaultLocale(), "description",
				ddmFormLayoutPage.getDescription());

			ddmFormLayoutPage.setDescription(localizedValue);
		}

		return ddmFormLayout;
	}

	@Override
	public String convertDDMFormLayoutDataDefinition(
			long groupId, long structureId,
			String structureLayoutDataDefinition, long structureLayoutId,
			String structureVersionDataDefinition)
		throws Exception {

		DDMFormLayout ddmFormLayout = DDMFormLayoutDeserializeUtil.deserialize(
			_ddmFormLayoutDeserializer, structureLayoutDataDefinition);

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormDeserializer, structureVersionDataDefinition);

		ddmFormLayout = convertDDMFormLayoutDataDefinition(
			ddmForm, ddmFormLayout);

		DDMFormLayoutSerializerSerializeResponse
			ddmFormLayoutSerializerSerializeResponse =
				_ddmFormLayoutSerializer.serialize(
					DDMFormLayoutSerializerSerializeRequest.Builder.newBuilder(
						ddmFormLayout
					).build());

		String content = ddmFormLayoutSerializerSerializeResponse.getContent();

		_addDataDefinitionFieldLinks(
			structureId, structureLayoutId, ddmForm, _getFieldNames(content),
			groupId);

		return content;
	}

	private void _addDataDefinitionFieldLinks(
			long dataDefinitionId, long dataLayoutId, DDMForm ddmForm,
			List<String> fieldNames, long groupId)
		throws Exception {

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (String fieldName : fieldNames) {
			long classNameId = _portal.getClassNameId(DDMStructureLayout.class);

			DEDataDefinitionFieldLink deDataDefinitionFieldLink =
				_deDataDefinitionFieldLinkLocalService.
					fetchDEDataDefinitionFieldLinks(
						classNameId, dataLayoutId, dataDefinitionId, fieldName);

			if (deDataDefinitionFieldLink == null) {
				_deDataDefinitionFieldLinkLocalService.
					addDEDataDefinitionFieldLink(
						groupId, classNameId, dataLayoutId, dataDefinitionId,
						fieldName);
			}

			DDMFormField ddmFormField = ddmFormFieldsMap.get(fieldName);

			if (ddmFormField != null) {
				_addDataDefinitionFieldLinks(
					classNameId, dataLayoutId,
					Collections.singletonList(ddmFormField), groupId);
			}
		}
	}

	private void _addDataDefinitionFieldLinks(
			long classNameId, long dataDefinitionId,
			List<DDMFormField> ddmFormFields, long groupId)
		throws Exception {

		for (DDMFormField ddmFormField : ddmFormFields) {
			long fieldSetDDMStructureId = GetterUtil.getLong(
				ddmFormField.getProperty("ddmStructureId"));

			if (fieldSetDDMStructureId == 0) {
				continue;
			}

			DEDataDefinitionFieldLink deDataDefinitionFieldLink =
				_deDataDefinitionFieldLinkLocalService.
					fetchDEDataDefinitionFieldLinks(
						classNameId, dataDefinitionId, fieldSetDDMStructureId,
						ddmFormField.getName());

			if (deDataDefinitionFieldLink == null) {
				_deDataDefinitionFieldLinkLocalService.
					addDEDataDefinitionFieldLink(
						groupId, classNameId, dataDefinitionId,
						fieldSetDDMStructureId, ddmFormField.getName());
			}

			_addDataDefinitionFieldLinks(
				classNameId, dataDefinitionId,
				ddmFormField.getNestedDDMFormFields(), groupId);
		}
	}

	private DDMFormField _createFieldSetDDMFormField(
		Set<Locale> availableLocales, String name,
		List<DDMFormField> nestedDDMFormFields, boolean repeatable) {

		return _createFieldSetDDMFormField(
			availableLocales, StringPool.BLANK, StringPool.BLANK, name,
			nestedDDMFormFields, repeatable, false);
	}

	private DDMFormField _createFieldSetDDMFormField(
		Set<Locale> availableLocales, String ddmStructureId,
		String ddmStructureLayoutId, String name,
		List<DDMFormField> nestedDDMFormFields, boolean repeatable,
		boolean upgradedStructure) {

		return new DDMFormField(name, "fieldset") {
			{
				setLabel(
					new LocalizedValue() {
						{
							for (Locale locale : availableLocales) {
								addString(
									locale,
									_language.get(locale, "fields-group"));
							}
						}
					});
				setLocalizable(true);
				setNestedDDMFormFields(nestedDDMFormFields);
				setProperty("collapsible", false);
				setProperty("ddmStructureId", ddmStructureId);
				setProperty("ddmStructureLayoutId", ddmStructureLayoutId);
				setProperty("labelAtStructureLevel", true);
				setProperty("upgradedStructure", upgradedStructure);
				setReadOnly(false);
				setRepeatable(repeatable);
				setRequired(false);
				setShowLabel(true);
			}
		};
	}

	private String _getDDMFormFieldsRows(DDMFormField fieldSetDDMFormField) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (DDMFormField ddmFormField :
				fieldSetDDMFormField.getNestedDDMFormFields()) {

			jsonArray.put(
				JSONUtil.put(
					"columns",
					JSONUtil.putAll(
						JSONUtil.put(
							"fields", JSONUtil.putAll(ddmFormField.getName())
						).put(
							"size", 12
						))));
		}

		return jsonArray.toString();
	}

	private LocalizedValue _getEmptyLocalizedValue(Locale defaultLocale) {
		LocalizedValue localizedValue = new LocalizedValue(defaultLocale);

		localizedValue.addString(defaultLocale, StringPool.BLANK);

		return localizedValue;
	}

	private List<String> _getFieldNames(String content) {
		DocumentContext documentContext = JsonPath.parse(content);

		return documentContext.read(
			"$[\"pages\"][*][\"rows\"][*][\"columns\"][*][\"fieldNames\"][*]");
	}

	private DDMFormField _getFieldSetDDMFormField(
		Set<Locale> availableLocales, DDMFormField ddmFormField,
		Locale defaultLocale) {

		DDMFormField fieldSetDDMFormField = _createFieldSetDDMFormField(
			availableLocales, ddmFormField.getName() + "FieldSet",
			ListUtil.fromArray(ddmFormField), ddmFormField.isRepeatable());

		_upgradeNestedFields(
			availableLocales, ddmFormField.getNestedDDMFormFields(),
			defaultLocale, fieldSetDDMFormField);

		fieldSetDDMFormField.setProperty(
			"rows", _getDDMFormFieldsRows(fieldSetDDMFormField));

		ddmFormField.setNestedDDMFormFields(Collections.emptyList());
		ddmFormField.setRepeatable(false);

		return fieldSetDDMFormField;
	}

	private boolean _hasNestedFields(DDMForm ddmForm) {
		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			if (ListUtil.isNotEmpty(ddmFormField.getNestedDDMFormFields())) {
				return true;
			}
		}

		return false;
	}

	private void _upgradeColorField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("string");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("color");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeDDMFormFieldOptionsReferences(
		DDMFormFieldOptions ddmFormFieldOptions) {

		if (ddmFormFieldOptions == null) {
			return;
		}

		Set<String> ddmFormFieldOptionsValues =
			ddmFormFieldOptions.getOptionsValues();

		ddmFormFieldOptionsValues.forEach(
			ddmFormFieldOptionsValue -> ddmFormFieldOptions.addOptionReference(
				ddmFormFieldOptionsValue, ddmFormFieldOptionsValue));
	}

	private void _upgradeDateField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("date");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("date");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeDecimalField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("double");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("numeric");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeDocumentLibraryField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("document-library");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("document_library");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeField(
		DDMFormField ddmFormField, Locale defaultLocale) {

		if (ddmFormField.hasProperty("validation")) {
			Object object = ddmFormField.getProperty("validation");

			if (!(object instanceof DDMFormFieldValidation)) {
				ddmFormField.removeProperty("validation");
			}
			else {
				DDMFormFieldValidation ddmFormFieldValidation =
					(DDMFormFieldValidation)object;

				DDMFormFieldValidationExpression
					ddmFormFieldValidationExpression =
						ddmFormFieldValidation.
							getDDMFormFieldValidationExpression();

				if ((ddmFormFieldValidationExpression == null) ||
					Validator.isNull(
						ddmFormFieldValidationExpression.getValue())) {

					ddmFormField.removeProperty("validation");
				}
			}
		}

		if (!StringUtil.equals(ddmFormField.getType(), "fieldset")) {
			_upgradeDDMFormFieldOptionsReferences(
				ddmFormField.getDDMFormFieldOptions());
			_upgradeDDMFormFieldOptionsReferences(
				(DDMFormFieldOptions)ddmFormField.getProperty("columns"));
			_upgradeDDMFormFieldOptionsReferences(
				(DDMFormFieldOptions)ddmFormField.getProperty("rows"));
		}

		if (Objects.equals(ddmFormField.getType(), "ddm-color")) {
			_upgradeColorField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-date")) {
			_upgradeDateField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-decimal")) {
			_upgradeDecimalField(ddmFormField);
		}
		else if (Objects.equals(
					ddmFormField.getType(), "ddm-documentlibrary")) {

			_upgradeDocumentLibraryField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-geolocation")) {
			_upgradeGeolocation(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-image")) {
			_upgradeImageField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-integer")) {
			_upgradeIntegerField(ddmFormField);
		}
		else if (Objects.equals(
					ddmFormField.getType(), "ddm-journal-article")) {

			_upgradeJournalArticleField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-link-to-page")) {
			_upgradeLinkToPageField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-number")) {
			_upgradeNumberField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-separator")) {
			_upgradeSeparatorField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "ddm-text-html")) {
			_upgradeHTMLField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "select")) {
			_upgradeSelectField(ddmFormField);
		}
		else if (Objects.equals(ddmFormField.getType(), "text")) {
			_upgradeTextField(ddmFormField, defaultLocale);
		}
		else if (Objects.equals(ddmFormField.getType(), "textarea")) {
			_upgradeTextArea(ddmFormField, defaultLocale);
		}

		if (!Objects.equals(ddmFormField.getType(), "separator") &&
			Validator.isNull(ddmFormField.getIndexType())) {

			ddmFormField.setIndexType("none");
		}

		_upgradeFields(ddmFormField.getNestedDDMFormFields(), defaultLocale);
	}

	private void _upgradeFields(
		List<DDMFormField> ddmFormFields, Locale defaultLocale) {

		if (ddmFormFields.isEmpty()) {
			return;
		}

		for (DDMFormField ddmFormField : ddmFormFields) {
			_upgradeField(ddmFormField, defaultLocale);
		}
	}

	private void _upgradeGeolocation(DDMFormField ddmFormField) {
		ddmFormField.setDataType("geolocation");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("geolocation");
	}

	private void _upgradeHTMLField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("string");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("rich_text");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeImageField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("image");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("image");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeIntegerField(DDMFormField ddmFormField) {
		ddmFormField.setType("numeric");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeJournalArticleField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("journal-article");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("journal_article");
	}

	private void _upgradeLinkToPageField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("link-to-page");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("link_to_layout");
	}

	private LocalizedValue _upgradeLocalizedValue(
		Set<Locale> availableLocales, Locale defaultLocale, String key,
		LocalizedValue localizedValue) {

		if (localizedValue == null) {
			localizedValue = new LocalizedValue();

			localizedValue.addString(
				defaultLocale, _language.get(defaultLocale, key));

			for (Locale locale : availableLocales) {
				localizedValue.addString(locale, _language.get(locale, key));
			}

			return localizedValue;
		}

		if (Validator.isNull(localizedValue.getString(defaultLocale))) {
			localizedValue.addString(
				defaultLocale, _language.get(defaultLocale, key));
		}

		return localizedValue;
	}

	private DDMForm _upgradeNestedFields(DDMForm ddmForm) {
		if (!_hasNestedFields(ddmForm)) {
			return ddmForm;
		}

		DDMForm newDDMForm = new DDMForm();

		newDDMForm.setAvailableLocales(ddmForm.getAvailableLocales());
		newDDMForm.setDefaultLocale(ddmForm.getDefaultLocale());
		newDDMForm.setDefinitionSchemaVersion(
			ddmForm.getDefinitionSchemaVersion());

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			if (ListUtil.isEmpty(ddmFormField.getNestedDDMFormFields())) {
				newDDMForm.addDDMFormField(ddmFormField);

				continue;
			}

			newDDMForm.addDDMFormField(
				_getFieldSetDDMFormField(
					ddmForm.getAvailableLocales(), ddmFormField,
					ddmForm.getDefaultLocale()));
		}

		return newDDMForm;
	}

	private void _upgradeNestedFields(
		Set<Locale> availableLocales, List<DDMFormField> ddmFormFields,
		Locale defaultLocale, DDMFormField parentFieldSetDDMFormField) {

		List<DDMFormField> nestedDDMFormFields = TransformUtil.transform(
			ddmFormFields,
			ddmFormField -> {
				if (ListUtil.isEmpty(ddmFormField.getNestedDDMFormFields())) {
					return ddmFormField;
				}

				return _getFieldSetDDMFormField(
					availableLocales, ddmFormField, defaultLocale);
			});

		if (nestedDDMFormFields.size() == 1) {
			DDMFormField nestedDDMFormField = nestedDDMFormFields.get(0);

			if (Objects.equals(
					nestedDDMFormField.getType(),
					DDMFormFieldTypeConstants.FIELDSET)) {

				parentFieldSetDDMFormField.addNestedDDMFormField(
					nestedDDMFormField);

				return;
			}
		}

		DDMFormField fieldSetDDMFormField = _createFieldSetDDMFormField(
			availableLocales, parentFieldSetDDMFormField.getName() + "FieldSet",
			nestedDDMFormFields, false);

		fieldSetDDMFormField.setProperty(
			"rows", _getDDMFormFieldsRows(fieldSetDDMFormField));

		parentFieldSetDDMFormField.addNestedDDMFormField(fieldSetDDMFormField);
	}

	private void _upgradeNumberField(DDMFormField ddmFormField) {
		ddmFormField.setDataType("double");
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("numeric");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeParentStructure(
		DDMForm ddmForm, long parentStructureId, long parentStructureLayoutId) {

		if (parentStructureId <= 0) {
			return;
		}

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		ddmFormFields.add(
			0,
			_createFieldSetDDMFormField(
				ddmForm.getAvailableLocales(),
				String.valueOf(parentStructureId),
				String.valueOf(parentStructureLayoutId),
				"parentStructureFieldSet" + parentStructureId,
				Collections.emptyList(), false, true));

		ddmForm.setDDMFormFields(ddmFormFields);
	}

	private void _upgradeSelectField(DDMFormField ddmFormField) {
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setProperty("dataSourceType", "[manual]");
		ddmFormField.setProperty("ddmDataProviderInstanceId", "[]");
		ddmFormField.setProperty("ddmDataProviderInstanceOutput", "[]");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeSeparatorField(DDMFormField ddmFormField) {
		ddmFormField.setDataType(StringPool.BLANK);
		ddmFormField.setFieldNamespace(StringPool.BLANK);
		ddmFormField.setType("separator");
	}

	private void _upgradeTextArea(
		DDMFormField ddmFormField, Locale defaultLocale) {

		ddmFormField.setFieldNamespace(StringPool.BLANK);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel("Option", defaultLocale, "Option");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmFormField.setProperty("autocomplete", false);
		ddmFormField.setProperty("dataSourceType", "manual");
		ddmFormField.setProperty("ddmDataProviderInstanceId", "[]");
		ddmFormField.setProperty("ddmDataProviderInstanceOutput", "[]");
		ddmFormField.setProperty("displayStyle", "multiline");
		ddmFormField.setProperty(
			"placeholder", _getEmptyLocalizedValue(defaultLocale));
		ddmFormField.setProperty(
			"tooltip", _getEmptyLocalizedValue(defaultLocale));
		ddmFormField.setType("text");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	private void _upgradeTextField(
		DDMFormField ddmFormField, Locale defaultLocale) {

		ddmFormField.setFieldNamespace(StringPool.BLANK);

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel("Option", defaultLocale, "Option");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmFormField.setProperty("autocomplete", false);
		ddmFormField.setProperty("dataSourceType", "manual");
		ddmFormField.setProperty("ddmDataProviderInstanceId", "[]");
		ddmFormField.setProperty("ddmDataProviderInstanceOutput", "[]");
		ddmFormField.setProperty("displayStyle", "singleline");
		ddmFormField.setProperty(
			"placeholder", _getEmptyLocalizedValue(defaultLocale));
		ddmFormField.setProperty(
			"tooltip", _getEmptyLocalizedValue(defaultLocale));
		ddmFormField.setType("text");
		ddmFormField.setVisibilityExpression(StringPool.BLANK);
	}

	@Reference(target = "(ddm.form.deserializer.type=json)")
	private DDMFormDeserializer _ddmFormDeserializer;

	@Reference
	private DDMFormLayoutDeserializer _ddmFormLayoutDeserializer;

	@Reference(target = "(ddm.form.layout.serializer.type=json)")
	private DDMFormLayoutSerializer _ddmFormLayoutSerializer;

	@Reference
	private DDMFormSerializer _ddmFormSerializer;

	@Reference
	private DEDataDefinitionFieldLinkLocalService
		_deDataDefinitionFieldLinkLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}