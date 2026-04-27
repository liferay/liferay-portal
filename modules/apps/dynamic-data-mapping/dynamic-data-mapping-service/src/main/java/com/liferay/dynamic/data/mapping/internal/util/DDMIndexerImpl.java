/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.dynamic.data.mapping.configuration.DDMIndexerConfiguration;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesConverterUtil;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.FieldArray;
import com.liferay.portal.kernel.search.NestedQuery;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.NestedSort;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortBuilderFactory;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.configuration.DDMIndexerConfiguration",
	service = DDMIndexer.class
)
public class DDMIndexerImpl implements DDMIndexer {

	@Override
	public void addAttributes(
		Document document, DDMStructure ddmStructure,
		DDMFormValues ddmFormValues) {

		boolean legacyDDMIndexFieldsEnabled = isLegacyDDMIndexFieldsEnabled();

		FieldArray fieldArray = (FieldArray)document.getField(DDM_FIELD_ARRAY);

		if ((fieldArray == null) && !legacyDDMIndexFieldsEnabled) {
			fieldArray = new FieldArray(DDM_FIELD_ARRAY);

			document.add(fieldArray);
		}

		Set<Locale> locales = ddmFormValues.getAvailableLocales();

		for (FieldPair fieldPair : _toFieldPairs(ddmStructure, ddmFormValues)) {
			try {
				DDMFormField ddmFormField = fieldPair._ddmFormField;

				String indexType = ddmFormField.getIndexType();

				String name = null;
				Serializable value = null;

				if (ddmFormField.isLocalizable()) {
					for (Locale locale : locales) {
						value = _getValue(
							fieldPair._field, ddmFormField, locale);

						if ((value != null) || legacyDDMIndexFieldsEnabled) {
							name = encodeName(
								ddmStructure.getStructureId(),
								ddmFormField.getFieldReference(), locale,
								indexType);
						}

						if (legacyDDMIndexFieldsEnabled) {
							_addToDocument(
								document, indexType, name,
								ddmFormField.getType(), value);
						}
						else if (value != null) {
							fieldArray.addField(
								createField(
									ddmFormField, indexType, locale, name,
									value));
						}
					}
				}
				else {
					value = _getValue(
						fieldPair._field, ddmFormField,
						ddmFormValues.getDefaultLocale());

					if ((value != null) || legacyDDMIndexFieldsEnabled) {
						name = encodeName(
							ddmStructure.getStructureId(),
							ddmFormField.getFieldReference(), null, indexType);
					}

					if (legacyDDMIndexFieldsEnabled) {
						_addToDocument(
							document, indexType, name, ddmFormField.getType(),
							value);
					}
					else if (value != null) {
						fieldArray.addField(
							createField(
								ddmFormField, indexType, null, name, value));
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
	}

	@Override
	public Sort createDDMStructureFieldSort(
			DDMStructure ddmStructure, String fieldReference, Locale locale,
			SortOrder sortOrder)
		throws PortalException {

		DDMFormField ddmFormField =
			ddmStructure.getDDMFormFieldByFieldReference(fieldReference);

		if (GetterUtil.getBoolean(ddmFormField.getProperty("localizable"))) {
			if (locale == null) {
				throw new IllegalArgumentException(
					"Locale cannot be null if the dynamic data mapping form " +
						"field is localizable");
			}
		}
		else {
			locale = null;
		}

		StringBundler sb = new StringBundler(5);

		if (isLegacyDDMIndexFieldsEnabled()) {
			sb.append(
				encodeName(
					ddmStructure.getStructureId(), fieldReference, locale));
		}
		else {
			sb.append(DDMIndexer.DDM_FIELD_ARRAY);
			sb.append(StringPool.PERIOD);

			String indexType = ddmStructure.getFieldPropertyByFieldReference(
				fieldReference, "indexType");

			sb.append(getValueFieldName(indexType, locale));
		}

		sb.append(StringPool.UNDERLINE);

		String ddmFormFieldType = ddmFormField.getType();

		if (Objects.equals(ddmFormFieldType, DDMFormFieldType.DECIMAL) ||
			Objects.equals(ddmFormFieldType, DDMFormFieldType.INTEGER) ||
			Objects.equals(ddmFormFieldType, DDMFormFieldType.NUMBER) ||
			Objects.equals(ddmFormFieldType, DDMFormFieldType.NUMERIC)) {

			sb.append("Number");
		}
		else {
			sb.append("String");
		}

		FieldSort fieldSort = sorts.field(
			com.liferay.portal.kernel.search.Field.getSortableFieldName(
				sb.toString()),
			sortOrder);

		if (isLegacyDDMIndexFieldsEnabled()) {
			return fieldSort;
		}

		NestedSort nestedSort = sorts.nested(DDMIndexer.DDM_FIELD_ARRAY);

		nestedSort.setFilterQuery(
			QueriesUtil.term(
				StringBundler.concat(
					DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
					DDMIndexer.DDM_FIELD_NAME),
				encodeName(
					ddmStructure.getStructureId(), fieldReference, locale)));

		fieldSort.setNestedSort(nestedSort);

		return fieldSort;
	}

	@Override
	public Sort createDDMStructureFieldSort(
			String ddmStructureFieldName, Locale locale, SortOrder sortOrder)
		throws PortalException {

		String[] ddmStructureFieldNameParts = StringUtil.split(
			ddmStructureFieldName, DDM_FIELD_SEPARATOR);

		long ddmStructureId = GetterUtil.getLong(ddmStructureFieldNameParts[2]);

		String fieldReference = StringUtil.replaceLast(
			ddmStructureFieldNameParts[3],
			StringPool.UNDERLINE.concat(LocaleUtil.toLanguageId(locale)),
			StringPool.BLANK);

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		return createDDMStructureFieldSort(
			ddmStructure, fieldReference, locale, sortOrder);
	}

	@Override
	public QueryFilter createFieldValueQueryFilter(
			DDMStructure ddmStructure, String fieldReference, Locale locale,
			Serializable value)
		throws Exception {

		String indexType = ddmStructure.getFieldPropertyByFieldReference(
			fieldReference, "indexType");

		return createFieldValueQueryFilter(
			ddmStructure,
			encodeName(ddmStructure.getStructureId(), fieldReference, locale),
			value, fieldReference, indexType, locale);
	}

	@Override
	public QueryFilter createFieldValueQueryFilter(
			String ddmStructureFieldName, Serializable ddmStructureFieldValue,
			Locale locale)
		throws Exception {

		String[] ddmStructureFieldNameParts = StringUtil.split(
			ddmStructureFieldName, DDM_FIELD_SEPARATOR);

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			GetterUtil.getLong(ddmStructureFieldNameParts[2]));

		String fieldReference = StringUtil.replaceLast(
			ddmStructureFieldNameParts[3],
			StringPool.UNDERLINE.concat(LocaleUtil.toLanguageId(locale)),
			StringPool.BLANK);

		return createFieldValueQueryFilter(
			ddmStructure, ddmStructureFieldName, ddmStructureFieldValue,
			fieldReference, ddmStructureFieldNameParts[1], locale);
	}

	@Override
	public String encodeName(long ddmStructureId, String fieldReference) {
		return encodeName(ddmStructureId, fieldReference, null);
	}

	@Override
	public String encodeName(
		long ddmStructureId, String fieldReference, Locale locale) {

		String indexType = StringPool.BLANK;
		boolean localizable = true;

		if (ddmStructureId > 0) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchDDMStructure(ddmStructureId);

			if (ddmStructure != null) {
				try {
					indexType = ddmStructure.getFieldPropertyByFieldReference(
						fieldReference, "indexType");
					localizable = GetterUtil.getBoolean(
						ddmStructure.getFieldPropertyByFieldReference(
							fieldReference, "localizable"));
				}
				catch (PortalException portalException) {
					throw new IllegalArgumentException(
						StringBundler.concat(
							"Unable to obtain index type for field ",
							fieldReference, " and DDM structure ID ",
							ddmStructureId),
						portalException);
				}
			}
		}

		if (localizable) {
			return encodeName(
				ddmStructureId, fieldReference, locale, indexType);
		}

		return encodeName(ddmStructureId, fieldReference, null, indexType);
	}

	@Override
	public String extractIndexableAttributes(
		DDMStructure ddmStructure, DDMFormValues ddmFormValues, Locale locale) {

		StringBundler sb = new StringBundler();

		_extractIndexableAttributes(
			ddmFormValues.getDDMFormFieldValues(), ddmStructure,
			ddmFormValues.getDefaultLocale(), locale, sb);

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	@Override
	public String getValueFieldName(String indexType) {
		return getValueFieldName(indexType, null);
	}

	@Override
	public String getValueFieldName(String indexType, Locale locale) {
		String valueFieldName = DDM_VALUE_FIELD_NAME_PREFIX;

		if (indexType != null) {
			valueFieldName = valueFieldName.concat(
				StringUtil.upperCaseFirstLetter(indexType));
		}

		if (locale != null) {
			valueFieldName = StringBundler.concat(
				valueFieldName, StringPool.UNDERLINE,
				LocaleUtil.toLanguageId(locale));
		}

		return valueFieldName;
	}

	@Override
	public boolean isLegacyDDMIndexFieldsEnabled() {
		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return true;
		}

		return _ddmIndexerConfiguration.enableLegacyDDMIndexFields();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddmIndexerConfiguration = ConfigurableUtil.createConfigurable(
			DDMIndexerConfiguration.class, properties);
	}

	protected com.liferay.portal.kernel.search.Field createField(
			DDMFormField ddmFormField, String indexType, Locale locale,
			String name, Serializable value)
		throws PortalException {

		com.liferay.portal.kernel.search.Field ddmField =
			new com.liferay.portal.kernel.search.Field(StringPool.BLANK);

		List<com.liferay.portal.kernel.search.Field> sortedFields =
			new SortedArrayList<>(
				Comparator.comparing(
					com.liferay.portal.kernel.search.Field::getName));

		Document document = new DocumentImpl();

		String valueFieldName = getValueFieldName(indexType, locale);

		Serializable sortableValue = null;

		if (value instanceof Serializable[] serializables) {
			List<String> sortableValues = new ArrayList<>();

			for (Serializable serializable : serializables) {
				sortableValues.add(
					_getSortableValue(ddmFormField, locale, serializable));
			}

			sortableValue = sortableValues.toString();
		}
		else {
			sortableValue = _getSortableValue(ddmFormField, locale, value);
		}

		_addToDocument(
			document, indexType, valueFieldName, sortableValue,
			ddmFormField.getType(), value);

		Map<String, com.liferay.portal.kernel.search.Field> documentFields =
			document.getFields();

		sortedFields.addAll(documentFields.values());

		sortedFields.add(
			new com.liferay.portal.kernel.search.Field(DDM_FIELD_NAME, name));

		sortedFields.add(
			new com.liferay.portal.kernel.search.Field(
				DDM_VALUE_FIELD_NAME, valueFieldName));

		sortedFields.forEach(ddmField::addField);

		return ddmField;
	}

	protected QueryFilter createFieldValueQueryFilter(
			DDMStructure ddmStructure, String ddmStructureFieldName,
			Serializable ddmStructureFieldValue, String fieldReference,
			String indexType, Locale locale)
		throws Exception {

		boolean localizable = false;

		if (ddmStructure.hasFieldByFieldReference(fieldReference)) {
			ddmStructureFieldValue = _ddm.getIndexedFieldValue(
				ddmStructureFieldValue,
				ddmStructure.getFieldPropertyByFieldReference(
					fieldReference, "type"));

			localizable = GetterUtil.getBoolean(
				ddmStructure.getFieldPropertyByFieldReference(
					fieldReference, "localizable"));
		}

		if (!localizable) {
			locale = null;
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		if (isLegacyDDMIndexFieldsEnabled()) {
			_addFieldValueRequiredTerm(
				booleanQuery, ddmStructureFieldName, ddmStructureFieldValue);

			return new QueryFilter(booleanQuery);
		}

		booleanQuery.addRequiredTerm(
			StringBundler.concat(
				DDM_FIELD_ARRAY, StringPool.PERIOD, DDM_FIELD_NAME),
			ddmStructureFieldName);

		_addFieldValueRequiredTerm(
			booleanQuery,
			StringBundler.concat(
				DDM_FIELD_ARRAY, StringPool.PERIOD,
				getValueFieldName(indexType, locale)),
			ddmStructureFieldValue);

		return new QueryFilter(new NestedQuery(DDM_FIELD_ARRAY, booleanQuery));
	}

	protected String encodeName(
		long ddmStructureId, String fieldReference, Locale locale,
		String indexType) {

		StringBundler sb = new StringBundler(8);

		sb.append(DDM_FIELD_PREFIX);

		if (Validator.isNotNull(indexType)) {
			sb.append(indexType);
			sb.append(DDM_FIELD_SEPARATOR);
		}

		sb.append(ddmStructureId);
		sb.append(DDM_FIELD_SEPARATOR);
		sb.append(fieldReference);

		if (locale != null) {
			sb.append(StringPool.UNDERLINE);
			sb.append(LocaleUtil.toLanguageId(locale));
		}
		else if (isLegacyDDMIndexFieldsEnabled() &&
				 StringUtil.equals(fieldReference, "date")) {

			sb.append(StringPool.UNDERLINE);
		}

		return sb.toString();
	}

	@Reference
	protected SearchEngineInformation searchEngineInformation;

	@Reference
	protected SortBuilderFactory sortBuilderFactory;

	@Reference
	protected Sorts sorts;

	private void _addField(
			DDMFormField ddmFormField, DDMFormFieldValue ddmFormFieldValue,
			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
			long ddmStructureId, Locale defaultLocale,
			Map<String, FieldPair> fieldPairs)
		throws PortalException {

		if ((ddmFormField == null) || ddmFormField.isTransient() ||
			(ddmFormFieldValue.getValue() == null)) {

			return;
		}

		String indexType = ddmFormField.getIndexType();

		if (Validator.isNull(indexType) || indexType.equals("none")) {
			return;
		}

		Field field = _createField(
			ddmFormField, ddmFormFieldValuesMap, ddmFormFieldValue,
			ddmStructureId, defaultLocale);

		FieldPair fieldPair = fieldPairs.get(field.getName());

		if (fieldPair == null) {
			fieldPairs.put(field.getName(), new FieldPair(ddmFormField, field));

			return;
		}

		fieldPair._mergeField(field);
	}

	private void _addFields(
			DDMForm ddmForm, DDMFormFieldValue ddmFormFieldValue,
			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
			long ddmStructureId, Locale defaultLocale,
			Map<String, FieldPair> fieldPairs)
		throws PortalException {

		_addField(
			ddmForm.getDDMFormField(ddmFormFieldValue.getName(), true),
			ddmFormFieldValue, ddmFormFieldValuesMap, ddmStructureId,
			defaultLocale, fieldPairs);

		for (DDMFormFieldValue nestedDDMFormFieldValue :
				ddmFormFieldValue.getNestedDDMFormFieldValues()) {

			_addFields(
				ddmForm, nestedDDMFormFieldValue, ddmFormFieldValuesMap,
				ddmStructureId, defaultLocale, fieldPairs);
		}
	}

	private void _addFieldValue(
			StringBundler sb, String type, String valueString)
		throws Exception {

		if (type.equals(DDMFormFieldTypeConstants.DOCUMENT_LIBRARY) ||
			type.equals(DDMFormFieldTypeConstants.LINK_TO_LAYOUT)) {

			JSONObject jsonObject = _jsonFactory.createJSONObject(valueString);

			if ((jsonObject != null) && jsonObject.has("title")) {
				sb.append(jsonObject.getString("title"));
			}
		}
		else if (type.equals(DDMFormFieldTypeConstants.IMAGE)) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(valueString);

			if (jsonObject == null) {
				return;
			}

			if (jsonObject.has("description") &&
				Validator.isNotNull(jsonObject.getString("description"))) {

				sb.append(jsonObject.getString("description"));
			}
			else if (jsonObject.has("alt") &&
					 Validator.isNotNull(jsonObject.getString("alt"))) {

				sb.append(jsonObject.getString("alt"));
			}
			else if (jsonObject.has("title")) {
				sb.append(jsonObject.getString("title"));
			}
		}
		else if (type.equals(DDMFormFieldTypeConstants.JOURNAL_ARTICLE)) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(valueString);

			if (jsonObject == null) {
				return;
			}

			if (jsonObject.has("titleMap")) {
				JSONObject titleMapJSONObject = jsonObject.getJSONObject(
					"titleMap");

				Iterator<String> iterator = titleMapJSONObject.keys();

				while (iterator.hasNext()) {
					sb.append(titleMapJSONObject.getString(iterator.next()));

					if (iterator.hasNext()) {
						sb.append(StringPool.SPACE);
					}
				}
			}
			else if (jsonObject.has("title")) {
				sb.append(jsonObject.getString("title"));
			}
		}
		else if (type.equals(DDMFormFieldTypeConstants.RICH_TEXT)) {
			sb.append(_htmlParser.extractText(valueString));
		}
		else if (type.equals(DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE) ||
				 type.equals(DDMFormFieldTypeConstants.SELECT)) {

			JSONArray jsonArray = _jsonFactory.createJSONArray(valueString);

			sb.append(ArrayUtil.toStringArray(jsonArray));
		}
		else {
			sb.append(valueString);
		}
	}

	private void _addFieldValueRequiredTerm(
		BooleanQuery booleanQuery, String fieldName, Serializable fieldValue) {

		if (fieldValue instanceof String[]) {
			String[] fieldValueArray = (String[])fieldValue;

			for (String fieldValueString : fieldValueArray) {
				booleanQuery.addRequiredTerm(
					fieldName,
					StringPool.QUOTE + fieldValueString + StringPool.QUOTE);
			}
		}
		else {
			booleanQuery.addRequiredTerm(
				fieldName,
				StringPool.QUOTE + String.valueOf(fieldValue) +
					StringPool.QUOTE);
		}
	}

	private void _addToDocument(
			Document document, String indexType, String name,
			Serializable sortableValue, String type, Serializable value)
		throws PortalException {

		if (value == null) {
		}
		else if (value instanceof BigDecimal) {
			document.addNumberSortable(name, (BigDecimal)value);
		}
		else if (value instanceof BigDecimal[]) {
			document.addNumberSortable(name, (BigDecimal[])value);
		}
		else if (value instanceof Boolean) {
			document.addKeywordSortable(name, (Boolean)value);
		}
		else if (value instanceof Boolean[]) {
			document.addKeywordSortable(name, (Boolean[])value);
		}
		else if (value instanceof Date) {
			document.addDateSortable(name, (Date)value);
		}
		else if (value instanceof Date[]) {
			document.addDateSortable(name, (Date[])value);
		}
		else if (value instanceof Double) {
			document.addNumberSortable(name, (Double)value);
		}
		else if (value instanceof Double[]) {
			document.addNumberSortable(name, (Double[])value);
		}
		else if (value instanceof Integer) {
			document.addNumberSortable(name, (Integer)value);
		}
		else if (value instanceof Integer[]) {
			document.addNumberSortable(name, (Integer[])value);
		}
		else if (value instanceof Long) {
			document.addNumberSortable(name, (Long)value);
		}
		else if (value instanceof Long[]) {
			document.addNumberSortable(name, (Long[])value);
		}
		else if (value instanceof Float) {
			document.addNumberSortable(name, (Float)value);
		}
		else if (value instanceof Float[]) {
			document.addNumberSortable(name, (Float[])value);
		}
		else if (value instanceof Number[]) {
			Number[] numbers = (Number[])value;

			Double[] doubles = new Double[numbers.length];

			for (int i = 0; i < numbers.length; i++) {
				doubles[i] = numbers[i].doubleValue();
			}

			document.addNumberSortable(name, doubles);
		}
		else if (value instanceof Object[]) {
			String[] valueStringArray = ArrayUtil.toStringArray(
				(Object[])value);

			String[] truncatedValueStringArray = valueStringArray;

			if (type.equals(DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE) ||
				type.equals(DDMFormFieldTypeConstants.SELECT)) {

				String[] sortableValueStringArray = _toStringArray(
					sortableValue);

				document.addKeyword(
					_getFieldName(name), sortableValueStringArray);

				document.addKeyword(name, valueStringArray);

				truncatedValueStringArray = TransformUtil.transform(
					sortableValueStringArray, StringUtil::toLowerCase,
					String.class);
			}
			else if (type.equals(DDMFormFieldTypeConstants.DATE) ||
					 type.equals(DDMFormFieldTypeConstants.DATE_TIME)) {

				Date[] dateValues = _getDateValues(type, valueStringArray);

				if (dateValues.length > 0) {
					document.addDate(name.concat("_date"), dateValues);
				}
			}
			else if (type.equals(DDMFormFieldTypeConstants.RICH_TEXT)) {
				List<String> richTextValues = new ArrayList<>(
					valueStringArray.length);
				List<String> truncatedValues = new ArrayList<>(
					valueStringArray.length);

				for (String valueString : valueStringArray) {
					String richTextValue = _htmlParser.extractText(valueString);

					richTextValues.add(richTextValue);

					truncatedValues.add(_truncate(richTextValue));
				}

				valueStringArray = richTextValues.toArray(new String[0]);
				truncatedValueStringArray = truncatedValues.toArray(
					new String[0]);
			}
			else if (type.equals(DDMFormFieldTypeConstants.TEXT)) {
				truncatedValueStringArray = TransformUtil.transform(
					valueStringArray, valueString -> _truncate(valueString),
					String.class);
			}

			if (indexType.equals("keyword")) {
				document.addKeywordSortable(name, valueStringArray);

				document.addKeyword(
					_getSortableFieldName(name), truncatedValueStringArray);
			}
			else {
				document.addTextSortable(name, valueStringArray);

				document.addText(
					_getSortableFieldName(name), truncatedValueStringArray);
			}
		}
		else {
			String sortableValueString = StringUtil.toLowerCase(
				String.valueOf(sortableValue));
			String valueString = String.valueOf(value);

			if (type.equals(DDMFormFieldTypeConstants.GEOLOCATION)) {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					valueString);

				double latitude = jsonObject.getDouble("lat", 0);
				double longitude = jsonObject.getDouble("lng", 0);

				document.addGeoLocation(
					name.concat("_geolocation"), latitude, longitude);
			}
			else if (type.equals(DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE) ||
					 type.equals(DDMFormFieldTypeConstants.SELECT)) {

				document.addKeyword(
					_getFieldName(name), _toStringArray(sortableValue));
				document.addKeyword(
					_getSortableFieldName(name),
					_toStringArray(sortableValueString));
				document.addKeyword(name, _toStringArray(valueString));
			}
			else {
				if ((type.equals(DDMFormFieldTypeConstants.DATE) ||
					 type.equals(DDMFormFieldTypeConstants.DATE_TIME)) &&
					Validator.isNotNull(valueString)) {

					Date[] dateValues = _getDateValues(
						type, new String[] {valueString});

					if (dateValues.length > 0) {
						document.addDate(name.concat("_date"), dateValues);
					}
				}
				else if (type.equals(DDMFormFieldTypeConstants.RICH_TEXT)) {
					valueString = _htmlParser.extractText(valueString);
					sortableValueString = _htmlParser.extractText(
						sortableValueString);
				}

				_createSortableTextField(document, name, sortableValueString);

				if (indexType.equals("keyword")) {
					document.addKeyword(name, valueString);
				}
				else {
					document.addText(name, valueString);
				}
			}
		}
	}

	private void _addToDocument(
			Document document, String indexType, String name, String type,
			Serializable value)
		throws PortalException {

		_addToDocument(document, indexType, name, value, type, value);
	}

	private Field _createField(
			DDMFormField ddmFormField,
			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
			DDMFormFieldValue ddmFormFieldValue, long ddmStructureId,
			Locale defaultLocale)
		throws PortalException {

		Field field = new Field();

		field.setDDMStructureId(ddmStructureId);
		field.setDefaultLocale(defaultLocale);
		field.setName(ddmFormFieldValue.getName());

		Value value = ddmFormFieldValue.getValue();

		boolean addValueLocales = false;

		if (MapUtil.isEmpty(value.getValues())) {
			LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

			Map<Locale, String> predefinedValuesMap =
				predefinedValue.getValues();

			if (predefinedValuesMap.isEmpty()) {
				LocalizedValue localizedValue = new LocalizedValue(
					defaultLocale);

				localizedValue.addString(defaultLocale, StringPool.BLANK);

				ddmFormField.setPredefinedValue(localizedValue);
			}

			value = ddmFormField.getPredefinedValue();

			addValueLocales = true;
		}

		if (!value.isLocalized()) {
			field.addValue(
				defaultLocale,
				FieldConstants.getSerializable(
					defaultLocale, LocaleUtil.ROOT, ddmFormField.getDataType(),
					value.getString(LocaleUtil.ROOT)));

			return field;
		}

		Set<Locale> availableLocales = _getAvailableLocales(
			ddmFormFieldValuesMap, ddmFormField.getName());

		if (addValueLocales) {
			availableLocales.addAll(value.getAvailableLocales());
		}

		for (Locale availableLocale : availableLocales) {
			field.addValue(
				availableLocale,
				FieldConstants.getSerializable(
					availableLocale, availableLocale,
					ddmFormField.getDataType(),
					value.getString(availableLocale)));
		}

		return field;
	}

	private void _createSortableTextField(
		Document document, String name, String sortableValueString) {

		if (Validator.isNull(sortableValueString)) {
			return;
		}

		document.addKeyword(
			_getSortableFieldName(name), _truncate(sortableValueString));
	}

	private void _extractIndexableAttribute(
			DDMFormField ddmFormField, Locale defaultLocale, String indexType,
			Locale locale, StringBundler sb, Value value)
		throws Exception {

		if (Validator.isNull(indexType) || indexType.equals("none") ||
			(value == null)) {

			return;
		}

		Serializable serializable = FieldConstants.getSerializable(
			defaultLocale, locale, ddmFormField.getDataType(),
			value.getString(locale));

		if ((serializable == null) ||
			Validator.isBlank(String.valueOf(serializable))) {

			return;
		}

		if (serializable instanceof Boolean || serializable instanceof Number) {
			sb.append(serializable);
		}
		else if (serializable instanceof Date) {
			Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
				PropsUtil.get(PropsKeys.INDEX_DATE_FORMAT_PATTERN));

			sb.append(dateFormat.format(serializable));
		}
		else {
			_addFieldValue(
				sb, ddmFormField.getType(),
				_getSortableValue(ddmFormField, locale, serializable));
		}

		sb.append(StringPool.SPACE);
	}

	private void _extractIndexableAttributes(
		List<DDMFormFieldValue> ddmFormFieldValues, DDMStructure ddmStructure,
		Locale defaultLocale, Locale locale, StringBundler sb) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

			if (ddmFormField == null) {
				continue;
			}

			try {
				Locale ddmFormFieldLocale = locale;

				if (!ddmFormField.isLocalizable()) {
					ddmFormFieldLocale = LocaleUtil.ROOT;
				}

				_extractIndexableAttribute(
					ddmFormField, defaultLocale, ddmFormField.getIndexType(),
					ddmFormFieldLocale, sb, ddmFormFieldValue.getValue());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to index ", ddmFormField.getName(),
							" because it was deleted from the dynamic data ",
							"mapping structure ID",
							ddmStructure.getStructureId()));
				}
			}

			if (ListUtil.isNotEmpty(
					ddmFormFieldValue.getNestedDDMFormFieldValues())) {

				_extractIndexableAttributes(
					ddmFormFieldValue.getNestedDDMFormFieldValues(),
					ddmStructure, defaultLocale, locale, sb);
			}
		}
	}

	private Set<Locale> _getAvailableLocales(
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		String name) {

		Set<Locale> availableLocales = new HashSet<>();

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			name);

		if (ddmFormFieldValues == null) {
			return availableLocales;
		}

		List<DDMFormFieldValue> matchedDDMFormFieldValues = new ArrayList<>();

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			Value value = ddmFormFieldValue.getValue();

			if (value == null) {
				continue;
			}

			availableLocales.addAll(value.getAvailableLocales());

			ddmFormFieldValue.populateNestedDDMFormFieldValues(
				name, matchedDDMFormFieldValues);
		}

		for (DDMFormFieldValue ddmFormFieldValue : matchedDDMFormFieldValues) {
			Value value = ddmFormFieldValue.getValue();

			if (value == null) {
				continue;
			}

			availableLocales.addAll(value.getAvailableLocales());
		}

		return availableLocales;
	}

	private Date[] _getDateValues(String type, String[] values) {
		String pattern = "yyyy-MM-dd";

		if (type.equals(DDMFormFieldTypeConstants.DATE_TIME)) {
			pattern = "yyyy-MM-dd hh:mm";
		}

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			pattern);

		return TransformUtil.transform(
			values,
			value -> {
				if (Validator.isNull(value)) {
					return null;
				}

				try {
					return dateFormat.parse(value);
				}
				catch (ParseException parseException) {
					if (_log.isWarnEnabled()) {
						_log.warn(parseException);
					}

					return null;
				}
			},
			Date.class);
	}

	private String _getFieldName(String name) {
		return name + "_String";
	}

	private String _getSortableFieldName(String name) {
		return com.liferay.portal.kernel.search.Field.getSortableFieldName(
			_getFieldName(name));
	}

	private String _getSortableValue(
		DDMFormField ddmFormField, Locale locale, Serializable value) {

		if (value == null) {
			return null;
		}

		String sortableValue = String.valueOf(value);

		DDMFormFieldOptions ddmFormFieldOptions =
			(DDMFormFieldOptions)ddmFormField.getProperty("options");

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();

		if (MapUtil.isEmpty(options)) {
			return sortableValue;
		}

		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray();

			JSONArray sortableValueJSONArray = _jsonFactory.createJSONArray(
				sortableValue);

			for (int i = 0; i < sortableValueJSONArray.length(); i++) {
				LocalizedValue localizedValue = options.get(
					sortableValueJSONArray.getString(i));

				if (localizedValue == null) {
					jsonArray.put(sortableValueJSONArray.getString(i));
				}
				else {
					jsonArray.put(localizedValue.getString(locale));
				}
			}

			return jsonArray.toString();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		for (Map.Entry<String, LocalizedValue> entry : options.entrySet()) {
			LocalizedValue localizedValue = entry.getValue();

			sortableValue = StringUtil.replace(
				sortableValue, entry.getKey(),
				localizedValue.getString(locale));
		}

		return sortableValue;
	}

	private Serializable _getValue(
		Field field, DDMFormField ddmFormField, Locale locale) {

		List<Serializable> values = field.getValues(locale);

		if (values.isEmpty()) {
			return null;
		}

		try {
			if ((values.size() > 1) || ddmFormField.isRepeatable()) {
				return FieldConstants.getSerializable(
					ddmFormField.getDataType(), values);
			}

			return values.get(0);
		}
		catch (Exception exception) {
			_log.error("Unable to extract field value", exception);
		}

		return null;
	}

	private Collection<FieldPair> _toFieldPairs(
		DDMStructure ddmStructure, DDMFormValues ddmFormValues) {

		try {
			DDMForm ddmForm = ddmStructure.getFullHierarchyDDMForm(false);

			List<DDMFormFieldValue> ddmFormFieldValues =
				DDMFormValuesConverterUtil.addMissingDDMFormFieldValues(
					ddmForm.getDDMFormFields(),
					ddmFormValues.getDDMFormFieldValuesMap(true));

			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
				new HashMap<>();

			for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
				List<DDMFormFieldValue> curDDMFormFieldValues =
					ddmFormFieldValuesMap.computeIfAbsent(
						ddmFormFieldValue.getName(), key -> new ArrayList<>());

				curDDMFormFieldValues.add(ddmFormFieldValue);

				ddmFormFieldValue.populateNestedDDMFormFieldValuesMap(
					ddmFormFieldValuesMap);
			}

			Map<String, FieldPair> fieldPairs = new HashMap<>();

			for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
				_addFields(
					ddmForm, ddmFormFieldValue, ddmFormFieldValuesMap,
					ddmStructure.getStructureId(),
					ddmFormValues.getDefaultLocale(), fieldPairs);
			}

			return fieldPairs.values();
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to convert DDMFormValues to Fields", portalException);
		}

		return Collections.emptySet();
	}

	private String[] _toStringArray(Object value) throws PortalException {
		return ArrayUtil.toStringArray(
			_jsonFactory.createJSONArray(String.valueOf(value)));
	}

	private String _truncate(String string) {
		if (string.length() > _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH) {
			return string.substring(0, _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH);
		}

		return string;
	}

	private static final int _SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH =
		GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.INDEX_SORTABLE_TEXT_FIELDS_TRUNCATED_LENGTH));

	private static final Log _log = LogFactoryUtil.getLog(DDMIndexerImpl.class);

	@Reference
	private DDM _ddm;

	private volatile DDMIndexerConfiguration _ddmIndexerConfiguration;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private JSONFactory _jsonFactory;

	private static class FieldPair {

		private FieldPair(DDMFormField ddmFormField, Field field) {
			_ddmFormField = ddmFormField;
			_field = field;
		}

		private void _mergeField(Field field) {
			for (Locale locale : field.getAvailableLocales()) {
				_field.addValues(locale, field.getValues(locale));
			}
		}

		private final DDMFormField _ddmFormField;
		private final Field _field;

	}

}