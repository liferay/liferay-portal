/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.document;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.geolocation.GeoLocationPoint;
import com.liferay.portal.search.opensearch2.internal.geolocation.GeoTranslator;

import java.io.IOException;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.GeoLocation;
import org.opensearch.client.opensearch._types.LatLonGeoLocation;

/**
 * @author Michael C. Han
 * @author Milen Dyankov
 * @author Petteri Karttunen
 */
public class OpenSearchDocumentFactoryUtil {

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	public static JsonData getOpenSearchDocument(
		com.liferay.portal.kernel.search.Document legacyDocument) {

		try {
			return _translateLegacyDocument(legacyDocument);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static JsonData getOpenSearchDocument(Document document) {
		try {
			return _translateDocument(document);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static void _addField(
			Field field, Map<String, Object> translatedFields)
		throws IOException {

		List<Object> values = field.getValues();

		if (values.isEmpty()) {
			_addFieldValueless(field, translatedFields);
		}

		if (values.size() == 1) {
			_addFieldValue(field, translatedFields, values.get(0));

			return;
		}

		_addFieldValues(field, translatedFields, values);
	}

	private static void _addFieldValue(
			Field field, Map<String, Object> translatedFields, Object value)
		throws IOException {

		translatedFields.put(field.getName(), _translateValue(value));
	}

	private static void _addFieldValueless(
			Field field, Map<String, Object> translatedFields)
		throws IOException {

		translatedFields.put(field.getName(), null);
	}

	private static void _addFieldValues(
			Field field, Map<String, Object> translatedFields,
			List<Object> values)
		throws IOException {

		Object[] fieldValues = new Object[values.size()];

		for (int i = 0; i < values.size(); i++) {
			fieldValues[i] = _translateValue(values.get(i));
		}

		translatedFields.put(field.getName(), fieldValues);
	}

	private static void _addLegacyField(
			com.liferay.portal.kernel.search.Field field,
			Map<String, Object> translatedFields)
		throws IOException {

		String name = field.getName();

		if (!field.isLocalized()) {
			String[] values = field.getValues();

			if (ArrayUtil.isEmpty(values)) {
				return;
			}

			List<String> fieldValues = new ArrayList<>(values.length);

			for (String value : values) {
				if (value == null) {
					continue;
				}

				fieldValues.add(value.trim());
			}

			if (fieldValues.isEmpty()) {
				return;
			}

			values = fieldValues.toArray(new String[0]);

			_addLegacyField(field, name, translatedFields, values);

			if (field.isSortable()) {
				_addLegacyField(
					field, _getSortableLegacyFieldName(name), translatedFields,
					values);
			}
		}
		else {
			Map<Locale, String> localizedValues = field.getLocalizedValues();

			for (Map.Entry<Locale, String> entry : localizedValues.entrySet()) {
				String value = entry.getValue();

				if (Validator.isNull(value)) {
					continue;
				}

				String languageId = LocaleUtil.toLanguageId(entry.getKey());

				String defaultLanguageId = LocaleUtil.toLanguageId(
					LocaleUtil.getDefault());

				value = value.trim();

				if (languageId.equals(defaultLanguageId)) {
					_addLegacyField(field, name, translatedFields, value);
				}

				String localizedName =
					com.liferay.portal.kernel.search.Field.getLocalizedName(
						languageId, name);

				_addLegacyField(field, localizedName, translatedFields, value);

				if (!field.isSortable()) {
					continue;
				}

				_addLegacyField(
					field, _getSortableLegacyFieldName(localizedName),
					translatedFields, value);
			}
		}
	}

	private static void _addLegacyField(
			com.liferay.portal.kernel.search.Field field, String fieldName,
			Map<String, Object> translatedFields, String... values)
		throws IOException {

		com.liferay.portal.kernel.search.geolocation.GeoLocationPoint
			geoLocationPoint = field.getGeoLocationPoint();

		List<Object> fieldValues = new ArrayList<>();

		if (geoLocationPoint != null) {
			fieldValues.add(_translateGeoLocationPoint(geoLocationPoint));
		}
		else if (field.isDate()) {
			Collections.addAll(fieldValues, field.getValues());
		}
		else {
			for (String value : values) {
				fieldValues.add(_translateLegacyValue(field, value));
			}
		}

		if (values.length == 1) {
			translatedFields.put(fieldName, fieldValues.get(0));
		}
		else {
			translatedFields.put(fieldName, fieldValues);
		}
	}

	private static void _addLegacyFields(
			Collection<com.liferay.portal.kernel.search.Field> fields,
			Map<String, Object> translatedFields)
		throws IOException {

		for (com.liferay.portal.kernel.search.Field field : fields) {
			if (!field.hasChildren()) {
				_addLegacyField(field, translatedFields);
			}
			else {
				_addLegacyNestedField(field, translatedFields);
			}
		}
	}

	private static void _addLegacyNestedField(
			com.liferay.portal.kernel.search.Field field,
			Map<String, Object> translatedFields)
		throws IOException {

		List<Map<String, Object>> nestedFields = new ArrayList<>();

		for (com.liferay.portal.kernel.search.Field nestedField :
				field.getFields()) {

			Map<String, Object> nestedFieldsMap = new HashMap<>();

			_addLegacyFields(nestedField.getFields(), nestedFieldsMap);

			nestedFields.add(nestedFieldsMap);
		}

		translatedFields.put(field.getName(), nestedFields);
	}

	private static String _getSortableLegacyFieldName(String localizedName) {
		return com.liferay.portal.kernel.search.Field.getSortableFieldName(
			localizedName);
	}

	private static JsonData _translateDocument(Document document)
		throws IOException {

		Map<String, Field> fields = document.getFields();

		Map<String, Object> translatedFields = new HashMap<>();

		for (Field field : fields.values()) {
			_addField(field, translatedFields);
		}

		return JsonData.of(translatedFields);
	}

	private static Double[] _translateGeoLocationPoint(Object value) {
		GeoLocation geoLocation;

		if (value instanceof GeoLocationPoint) {
			geoLocation = _geoTranslator.translateGeoLocationPoint(
				(GeoLocationPoint)value);
		}
		else {
			geoLocation = _geoTranslator.translateGeoLocationPoint(
				(com.liferay.portal.kernel.search.geolocation.GeoLocationPoint)
					value);
		}

		LatLonGeoLocation latLonGeoLocation = geoLocation.latlon();

		return new Double[] {latLonGeoLocation.lon(), latLonGeoLocation.lat()};
	}

	private static JsonData _translateLegacyDocument(
			com.liferay.portal.kernel.search.Document document)
		throws IOException {

		Map<String, com.liferay.portal.kernel.search.Field> fields =
			document.getFields();
		Map<String, Object> translatedFields = new HashMap<>();

		_addLegacyFields(fields.values(), translatedFields);

		return JsonData.of(translatedFields);
	}

	private static Object _translateLegacyValue(
		com.liferay.portal.kernel.search.Field field, String value) {

		if (!field.isNumeric()) {
			return value;
		}

		Class<? extends Number> clazz = field.getNumericClass();

		if (clazz.equals(BigDecimal.class)) {
			return new BigDecimal(value);
		}
		else if (clazz.equals(Double.class)) {
			return Double.valueOf(value);
		}
		else if (clazz.equals(Float.class)) {
			return Float.valueOf(value);
		}
		else if (clazz.equals(Integer.class)) {
			return Integer.valueOf(value);
		}
		else if (clazz.equals(Long.class)) {
			return Long.valueOf(value);
		}
		else if (clazz.equals(Short.class)) {
			return Short.valueOf(value);
		}

		throw new IllegalArgumentException(
			"Invalid number class " + clazz.getName());
	}

	private static Object _translateValue(Object value) {
		if (value instanceof GeoLocationPoint) {
			return _translateGeoLocationPoint(value);
		}

		return value;
	}

	private static final GeoTranslator _geoTranslator = new GeoTranslator();

}