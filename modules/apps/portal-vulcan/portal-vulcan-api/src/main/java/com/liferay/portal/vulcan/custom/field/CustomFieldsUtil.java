/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.custom.field;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portlet.expando.model.impl.ExpandoColumnImpl;

import java.io.Serializable;

import java.lang.reflect.Array;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

/**
 * @author Carlos Correa
 */
public class CustomFieldsUtil {

	public static CustomField[] toCustomFields(
		boolean acceptAllLanguages, String className, long classPK,
		long companyId, Locale locale) {

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			companyId, className, classPK);

		return toCustomFields(
			acceptAllLanguages, className, classPK, companyId,
			expandoBridge.getAttributes(), locale);
	}

	public static CustomField[] toCustomFields(
		boolean acceptAllLanguages, String className, long classPK,
		long companyId, Map<String, Serializable> expandoBridgeAttributes,
		Locale locale) {

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			companyId, className, classPK);

		return TransformUtil.transformToArray(
			expandoBridgeAttributes.entrySet(),
			entry -> {
				UnicodeProperties unicodeProperties =
					expandoBridge.getAttributeProperties(entry.getKey());

				if (GetterUtil.getBoolean(
						unicodeProperties.getProperty(
							ExpandoColumnConstants.PROPERTY_HIDDEN))) {

					return null;
				}

				return _toCustomField(
					acceptAllLanguages,
					unicodeProperties.getProperty(
						ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE),
					entry, expandoBridge, locale);
			},
			CustomField.class);
	}

	public static Map<String, Serializable> toMap(
		String className, long companyId, CustomField[] customFields,
		Locale locale) {

		if (customFields == null) {
			return null;
		}

		Map<String, Serializable> map = new HashMap<>();

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			companyId, className);

		for (CustomField customField : customFields) {
			CustomValue customValue = customField.getCustomValue();

			Object data = customValue.getData();

			if ((data == null) && (customValue.getData_i18n() == null) &&
				(customValue.getGeo() == null)) {

				continue;
			}

			String name = customField.getName();

			try {
				EmptyModelManagerUtil.getOrAddEmptyModel(
					ExpandoColumn.class, companyId, name,
					(__, ___) -> {
						if (!expandoBridge.hasAttribute(name)) {
							return null;
						}

						return _expandoColumn;
					},
					(__, ___) -> {
						if (!expandoBridge.hasAttribute(name)) {

							// TODO LPD-65443 Throw exception to handle error

							return null;
						}

						return _expandoColumn;
					},
					() -> {
						expandoBridge.addAttribute(
							name,
							_toAttributeType(customField.getAttributeType()));

						return _expandoColumn;
					});
			}
			catch (PortalException portalException) {
				throw new RuntimeException(portalException);
			}

			int attributeType = expandoBridge.getAttributeType(name);

			if (ExpandoColumnConstants.BOOLEAN == attributeType) {
				map.put(name, GetterUtil.getBoolean(data));
			}
			else if (ExpandoColumnConstants.BOOLEAN_ARRAY == attributeType) {
				map.put(
					name,
					_toArray(boolean.class, data, GetterUtil::getBoolean));
			}
			else if (ExpandoColumnConstants.DATE == attributeType) {
				map.put(name, _toDate(data));
			}
			else if (ExpandoColumnConstants.DATE_ARRAY == attributeType) {
				map.put(
					name,
					_toArray(Date.class, data, CustomFieldsUtil::_toDate));
			}
			else if (ExpandoColumnConstants.DOUBLE == attributeType) {
				map.put(name, GetterUtil.getDouble(data));
			}
			else if (ExpandoColumnConstants.DOUBLE_ARRAY == attributeType) {
				map.put(
					name, _toArray(double.class, data, GetterUtil::getDouble));
			}
			else if (ExpandoColumnConstants.FLOAT == attributeType) {
				map.put(name, GetterUtil.getFloat(data));
			}
			else if (ExpandoColumnConstants.FLOAT_ARRAY == attributeType) {
				map.put(
					name, _toArray(float.class, data, GetterUtil::getFloat));
			}
			else if (ExpandoColumnConstants.GEOLOCATION == attributeType) {
				Geo geo = customValue.getGeo();

				map.put(
					name,
					JSONUtil.put(
						"latitude", geo.getLatitude()
					).put(
						"longitude", geo.getLongitude()
					).toString());
			}
			else if (ExpandoColumnConstants.INTEGER == attributeType) {
				map.put(name, GetterUtil.getInteger(data));
			}
			else if (ExpandoColumnConstants.INTEGER_ARRAY == attributeType) {
				map.put(
					name, _toArray(int.class, data, GetterUtil::getInteger));
			}
			else if (ExpandoColumnConstants.LONG == attributeType) {
				map.put(name, GetterUtil.getLong(data));
			}
			else if (ExpandoColumnConstants.LONG_ARRAY == attributeType) {
				map.put(name, _toArray(long.class, data, GetterUtil::getLong));
			}
			else if (ExpandoColumnConstants.NUMBER == attributeType) {
				map.put(name, GetterUtil.getNumber(data));
			}
			else if (ExpandoColumnConstants.NUMBER_ARRAY == attributeType) {
				map.put(
					name, _toArray(Number.class, data, GetterUtil::getNumber));
			}
			else if (ExpandoColumnConstants.SHORT == attributeType) {
				map.put(name, GetterUtil.getShort(data));
			}
			else if (ExpandoColumnConstants.SHORT_ARRAY == attributeType) {
				map.put(
					name, _toArray(short.class, data, GetterUtil::getShort));
			}
			else if (ExpandoColumnConstants.STRING == attributeType) {
				map.put(name, String.valueOf(data));
			}
			else if (ExpandoColumnConstants.STRING_ARRAY == attributeType) {
				map.put(name, _toArray(String.class, data, String::valueOf));
			}
			else if (ExpandoColumnConstants.STRING_LOCALIZED == attributeType) {
				map.put(
					name,
					(Serializable)LocalizedMapUtil.getLocalizedMap(
						locale, (String)data, customValue.getData_i18n()));
			}
			else {
				map.put(name, (Serializable)data);
			}
		}

		return map;
	}

	private static Map<String, String> _getLocalizedValues(
		boolean acceptAllLanguages, int attributeType, Object value) {

		if (ExpandoColumnConstants.STRING_LOCALIZED != attributeType) {
			return null;
		}

		return LocalizedMapUtil.getI18nMap(
			acceptAllLanguages, (Map<Locale, String>)value);
	}

	private static Object _getValue(
		int attributeType, Locale locale, Object value) {

		if (ExpandoColumnConstants.STRING_LOCALIZED == attributeType) {
			Map<Locale, String> map = (Map<Locale, String>)value;

			return map.get(locale);
		}
		else if (ExpandoColumnConstants.DATE == attributeType) {
			return DateUtil.getDate(
				(Date)value, "yyyy-MM-dd'T'HH:mm:ss'Z'", locale,
				TimeZone.getTimeZone("UTC"));
		}

		return value;
	}

	private static Object _getValue(
		int attributeType, String displayType,
		Map.Entry<String, Serializable> entry, ExpandoBridge expandoBridge,
		String key) {

		Object value = entry.getValue();

		if (!_isEmpty(value)) {
			return value;
		}

		if (!ExpandoColumnConstants.isArray(attributeType)) {
			return expandoBridge.getAttributeDefault(key);
		}

		boolean selectionList = StringUtil.equals(
			displayType,
			ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST);

		if (ExpandoColumnConstants.DOUBLE_ARRAY == attributeType) {
			if (selectionList) {
				return ArrayUtil.subset(
					GetterUtil.getDoubleValues(
						expandoBridge.getAttributeDefault(key)),
					0, 1);
			}

			return new double[] {GetterUtil.DEFAULT_DOUBLE};
		}
		else if (ExpandoColumnConstants.LONG_ARRAY == attributeType) {
			if (selectionList) {
				return ArrayUtil.subset(
					GetterUtil.getLongValues(
						expandoBridge.getAttributeDefault(key)),
					0, 1);
			}

			return new long[] {GetterUtil.DEFAULT_INTEGER};
		}
		else if (ExpandoColumnConstants.STRING_ARRAY == attributeType) {
			if (selectionList) {
				return ArrayUtil.subset(
					GetterUtil.getStringValues(
						expandoBridge.getAttributeDefault(key)),
					0, 1);
			}

			return new String[] {String.valueOf(GetterUtil.DEFAULT_BOOLEAN)};
		}

		return value;
	}

	private static boolean _isEmpty(Object value) {
		if (value == null) {
			return true;
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray() && (Array.getLength(value) == 0)) {
			return true;
		}

		if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)value;

			if (map.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private static <T, U> Serializable _toArray(
		Class<? extends U> clazz, Object data, Function<Object, U> function) {

		Serializable serializable = (Serializable)data;

		Class<?> dataClass = data.getClass();

		if (dataClass.isArray()) {
			if (clazz.equals(dataClass.getComponentType())) {
				return (Serializable)data;
			}

			int length = Array.getLength(data);

			serializable = (Serializable)Array.newInstance(clazz, length);

			for (int i = 0; i < length; i++) {
				Array.set(serializable, i, function.apply(Array.get(data, i)));
			}
		}
		else if (data instanceof Collection) {
			Collection<?> collection = (Collection<?>)data;

			serializable = (Serializable)Array.newInstance(
				clazz, collection.size());

			int i = 0;

			Iterator<T> iterator = (Iterator<T>)collection.iterator();

			while (iterator.hasNext()) {
				Array.set(serializable, i++, function.apply(iterator.next()));
			}
		}

		return serializable;
	}

	private static int _toAttributeType(
		CustomField.AttributeType attributeType) {

		for (Map.Entry<Integer, CustomField.AttributeType> entry :
				_attributeTypes.entrySet()) {

			if (attributeType == entry.getValue()) {
				return entry.getKey();
			}
		}

		throw new IllegalArgumentException(
			"Invalid attribute type: " + attributeType);
	}

	private static CustomField _toCustomField(
		boolean acceptAllLanguages, String displayType,
		Map.Entry<String, Serializable> entry, ExpandoBridge expandoBridge,
		Locale locale) {

		String key = entry.getKey();

		int type = expandoBridge.getAttributeType(key);

		if (ExpandoColumnConstants.GEOLOCATION == type) {
			return new CustomField() {
				{
					setAttributeType(
						() -> NestedFieldsSupplier.supply(
							"customFields.attributeType",
							nestedField -> _attributeTypes.get(type)));
					setCustomValue(
						() -> {
							JSONObject jsonObject =
								JSONFactoryUtil.createJSONObject(
									String.valueOf(entry.getValue()));

							return new CustomValue() {
								{
									setGeo(
										() -> new Geo() {
											{
												setLatitude(
													() -> jsonObject.getDouble(
														"latitude"));
												setLongitude(
													() -> jsonObject.getDouble(
														"longitude"));
											}
										});
								}
							};
						});
					setDataType(() -> "Geolocation");
					setName(entry::getKey);
				}
			};
		}

		return new CustomField() {
			{
				setAttributeType(
					() -> NestedFieldsSupplier.supply(
						"customFields.attributeType",
						nestedField -> _attributeTypes.get(type)));
				setCustomValue(
					() -> new CustomValue() {
						{
							setData(
								() -> _getValue(
									type, locale,
									_getValue(
										type, displayType, entry, expandoBridge,
										key)));
							setData_i18n(
								() -> _getLocalizedValues(
									acceptAllLanguages, type,
									_getValue(
										type, displayType, entry, expandoBridge,
										key)));
						}
					});
				setDataType(() -> ExpandoColumnConstants.getDataType(type));
				setName(entry::getKey);
			}
		};
	}

	private static Date _toDate(Object data) {
		if (data instanceof Date) {
			return (Date)data;
		}

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		try {
			return dateFormat.parse(String.valueOf(data));
		}
		catch (ParseException parseException) {
			throw new IllegalArgumentException(
				"Unable to parse date from " + data, parseException);
		}
	}

	private static final Map<Integer, CustomField.AttributeType>
		_attributeTypes = HashMapBuilder.put(
			ExpandoColumnConstants.BOOLEAN, CustomField.AttributeType.BOOLEAN
		).put(
			ExpandoColumnConstants.BOOLEAN_ARRAY,
			CustomField.AttributeType.BOOLEAN_ARRAY
		).put(
			ExpandoColumnConstants.DATE, CustomField.AttributeType.DATE
		).put(
			ExpandoColumnConstants.DATE_ARRAY,
			CustomField.AttributeType.DATE_ARRAY
		).put(
			ExpandoColumnConstants.DOUBLE, CustomField.AttributeType.DOUBLE
		).put(
			ExpandoColumnConstants.DOUBLE_ARRAY,
			CustomField.AttributeType.DOUBLE_ARRAY
		).put(
			ExpandoColumnConstants.FLOAT, CustomField.AttributeType.FLOAT
		).put(
			ExpandoColumnConstants.FLOAT_ARRAY,
			CustomField.AttributeType.FLOAT_ARRAY
		).put(
			ExpandoColumnConstants.GEOLOCATION,
			CustomField.AttributeType.GEOLOCATION
		).put(
			ExpandoColumnConstants.INTEGER, CustomField.AttributeType.INTEGER
		).put(
			ExpandoColumnConstants.INTEGER_ARRAY,
			CustomField.AttributeType.INTEGER_ARRAY
		).put(
			ExpandoColumnConstants.LONG, CustomField.AttributeType.LONG
		).put(
			ExpandoColumnConstants.LONG_ARRAY,
			CustomField.AttributeType.LONG_ARRAY
		).put(
			ExpandoColumnConstants.NUMBER, CustomField.AttributeType.NUMBER
		).put(
			ExpandoColumnConstants.NUMBER_ARRAY,
			CustomField.AttributeType.NUMBER_ARRAY
		).put(
			ExpandoColumnConstants.SHORT, CustomField.AttributeType.SHORT
		).put(
			ExpandoColumnConstants.SHORT_ARRAY,
			CustomField.AttributeType.SHORT_ARRAY
		).put(
			ExpandoColumnConstants.STRING, CustomField.AttributeType.STRING
		).put(
			ExpandoColumnConstants.STRING_ARRAY,
			CustomField.AttributeType.STRING_ARRAY
		).put(
			ExpandoColumnConstants.STRING_ARRAY_LOCALIZED,
			CustomField.AttributeType.STRING_ARRAY_LOCALIZED
		).put(
			ExpandoColumnConstants.STRING_LOCALIZED,
			CustomField.AttributeType.STRING_LOCALIZED
		).build();
	private static final ExpandoColumn _expandoColumn = new ExpandoColumnImpl();

}