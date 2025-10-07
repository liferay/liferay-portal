/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.document;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.elasticsearch7.internal.geolocation.GeoLocationPointTranslator;
import com.liferay.portal.search.geolocation.GeoLocationPoint;

import java.io.IOException;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;

/**
 * @author Michael C. Han
 */
public class ElasticsearchDocumentFactory {

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	public String getElasticsearchDocument(
		com.liferay.portal.kernel.search.Document legacyDocument) {

		try {
			return Strings.toString(translate(legacyDocument));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public XContentBuilder getElasticsearchDocument(Document document) {
		try {
			return translate(document);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected XContentBuilder translate(
			com.liferay.portal.kernel.search.Document legacyDocument)
		throws IOException {

		XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();

		xContentBuilder.startObject();

		Map<String, com.liferay.portal.kernel.search.Field> fields =
			legacyDocument.getFields();

		_addFields(fields.values(), xContentBuilder);

		xContentBuilder.endObject();

		return xContentBuilder;
	}

	protected XContentBuilder translate(Document document) throws IOException {
		XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();

		Map<String, Field> fields = document.getFields();

		xContentBuilder.startObject();

		for (Field field : fields.values()) {
			_addField(field, xContentBuilder);
		}

		xContentBuilder.endObject();

		return xContentBuilder;
	}

	private void _addDates(
			XContentBuilder xContentBuilder,
			com.liferay.portal.kernel.search.Field field)
		throws IOException {

		for (Date date : field.getDates()) {
			String value;

			if (date.getTime() == Long.MAX_VALUE) {
				value = "99950812133000";
			}
			else {
				Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
					"yyyyMMddHHmmss", null, null);

				value = format.format(date);
			}

			xContentBuilder.value(value);
		}
	}

	private void _addField(Field field, XContentBuilder xContentBuilder)
		throws IOException {

		List<Object> values = field.getValues();

		if (values.isEmpty()) {
			_addFieldValueless(field, xContentBuilder);
		}

		if (values.size() == 1) {
			_addFieldValue(field, values.get(0), xContentBuilder);

			return;
		}

		_addFieldValues(field, values, xContentBuilder);
	}

	private void _addField(
			XContentBuilder xContentBuilder,
			com.liferay.portal.kernel.search.Field field)
		throws IOException {

		String name = field.getName();

		if (!field.isLocalized()) {
			String[] values = field.getValues();

			if (ArrayUtil.isEmpty(values)) {
				return;
			}

			List<String> valuesList = new ArrayList<>(values.length);

			for (String value : values) {
				if (value == null) {
					continue;
				}

				valuesList.add(value.trim());
			}

			if (valuesList.isEmpty()) {
				return;
			}

			values = valuesList.toArray(new String[0]);

			_addField(xContentBuilder, field, name, values);

			if (field.isSortable()) {
				_addField(
					xContentBuilder, field, _getSortableFieldName(name),
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

				Locale locale = entry.getKey();

				String languageId = LocaleUtil.toLanguageId(locale);

				String defaultLanguageId = LocaleUtil.toLanguageId(
					LocaleUtil.getDefault());

				value = value.trim();

				if (languageId.equals(defaultLanguageId)) {
					_addField(xContentBuilder, field, name, value);
				}

				String localizedName =
					com.liferay.portal.kernel.search.Field.getLocalizedName(
						languageId, name);

				_addField(xContentBuilder, field, localizedName, value);

				if (field.isSortable()) {
					_addField(
						xContentBuilder, field,
						_getSortableFieldName(localizedName), value);
				}
			}
		}
	}

	private void _addField(
			XContentBuilder xContentBuilder,
			com.liferay.portal.kernel.search.Field field, String fieldName,
			String... values)
		throws IOException {

		xContentBuilder.field(fieldName);

		if (field.isArray() || (values.length > 1)) {
			xContentBuilder.startArray();
		}

		com.liferay.portal.kernel.search.geolocation.GeoLocationPoint
			geoLocationPoint = field.getGeoLocationPoint();

		if (geoLocationPoint != null) {
			GeoPoint geoPoint = new GeoPoint(
				geoLocationPoint.getLatitude(),
				geoLocationPoint.getLongitude());

			xContentBuilder.value(geoPoint);
		}
		else if (field.isDate()) {
			_addDates(xContentBuilder, field);
		}
		else {
			for (String value : values) {
				xContentBuilder.value(_translateValue(field, value));
			}
		}

		if (field.isArray() || (values.length > 1)) {
			xContentBuilder.endArray();
		}
	}

	private void _addFields(
			Collection<com.liferay.portal.kernel.search.Field> fields,
			XContentBuilder xContentBuilder)
		throws IOException {

		for (com.liferay.portal.kernel.search.Field field : fields) {
			if (!field.hasChildren()) {
				_addField(xContentBuilder, field);
			}
			else {
				_addNestedField(xContentBuilder, field);
			}
		}
	}

	private void _addFieldValue(
			Field field, Object value, XContentBuilder xContentBuilder)
		throws IOException {

		xContentBuilder.field(field.getName(), _toElasticsearchValue(value));
	}

	private void _addFieldValueless(
			Field field, XContentBuilder xContentBuilder)
		throws IOException {

		xContentBuilder.field(field.getName());
	}

	private void _addFieldValues(
			Field field, List<Object> values, XContentBuilder xContentBuilder)
		throws IOException {

		Object[] elasticsearchValues = new Object[values.size()];

		for (int i = 0; i < values.size(); i++) {
			elasticsearchValues[i] = _toElasticsearchValue(values.get(i));
		}

		xContentBuilder.array(field.getName(), elasticsearchValues);
	}

	private void _addNestedField(
			XContentBuilder xContentBuilder,
			com.liferay.portal.kernel.search.Field field)
		throws IOException {

		if (field.isArray()) {
			xContentBuilder.startArray(field.getName());
		}
		else {
			if (Validator.isNull(field.getName())) {
				xContentBuilder.startObject();
			}
			else {
				xContentBuilder.startObject(field.getName());
			}
		}

		_addFields(field.getFields(), xContentBuilder);

		if (field.isArray()) {
			xContentBuilder.endArray();
		}
		else {
			xContentBuilder.endObject();
		}
	}

	private String _getSortableFieldName(String localizedName) {
		return com.liferay.portal.kernel.search.Field.getSortableFieldName(
			localizedName);
	}

	private Object _toElasticsearchValue(Object value) {
		if (value instanceof GeoLocationPoint) {
			return GeoLocationPointTranslator.translate(
				(GeoLocationPoint)value);
		}

		return value;
	}

	private Object _translateValue(
		com.liferay.portal.kernel.search.Field field, String value) {

		if (!field.isNumeric()) {
			return value;
		}

		Class<? extends Number> clazz = field.getNumericClass();

		if (clazz.equals(Float.class)) {
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

		return Double.valueOf(value);
	}

}