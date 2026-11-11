/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.setting.util;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Carolina Barbosa
 */
public class ObjectFieldSettingUtil {

	public static Set<String> getAllowedFileSources() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT,
			ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA,
			ObjectFieldSettingConstants.
				VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT,
			ObjectFieldSettingConstants.VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA);
	}

	public static Object getDefaultValue(
		DDMExpressionFactory ddmExpressionFactory, ObjectField objectField,
		Map<String, Object> values) {

		List<ObjectFieldSetting> objectFieldSettings =
			objectField.getObjectFieldSettings();

		ObjectFieldSetting defaultValueObjectFieldSetting =
			_getObjectFieldSetting(
				objectFieldSettings,
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE);

		if (defaultValueObjectFieldSetting == null) {
			return null;
		}

		ObjectFieldSetting defaultValueTypeObjectFieldSetting =
			_getObjectFieldSetting(
				objectFieldSettings,
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE);

		if ((defaultValueTypeObjectFieldSetting == null) ||
			StringUtil.equals(
				defaultValueTypeObjectFieldSetting.getValue(),
				ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE)) {

			return _parseValue(
				objectField.getDBType(),
				defaultValueObjectFieldSetting.getValue());
		}

		if (ddmExpressionFactory == null) {
			return StringPool.BLANK;
		}

		try {
			DDMExpression<?> ddmExpression =
				ddmExpressionFactory.createExpression(
					CreateExpressionRequest.Builder.newBuilder(
						defaultValueObjectFieldSetting.getValue()
					).build());

			ddmExpression.setVariables(
				HashMapBuilder.<String, Object>put(
					"currentDate",
					() -> {
						DateFormat dateFormat =
							DateFormatFactoryUtil.getSimpleDateFormat(
								"yyyy-MM-dd HH:mm");

						return dateFormat.format(new Date());
					}
				).put(
					"currentUserId", PrincipalThreadLocal.getUserId()
				).putAll(
					values
				).build());

			Object defaultValue = ddmExpression.evaluate();

			if (defaultValue == null) {
				return null;
			}

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_DATE)) {

				return _getDateString(defaultValue, "yyyy-MM-dd");
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

				return _getDateString(defaultValue, "yyyy-MM-dd HH:mm");
			}

			return defaultValue;
		}
		catch (DDMExpressionException ddmExpressionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ddmExpressionException);
			}
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(parseException);
			}
		}

		return StringPool.BLANK;
	}

	public static Map<String, Object> getDefaultValues(
		DDMExpressionFactory ddmExpressionFactory, long objectDefinitionId) {

		Map<String, Object> defaultValues = new HashMap<>();

		for (ObjectField objectField :
				ObjectFieldLocalServiceUtil.getObjectFields(
					objectDefinitionId)) {

			defaultValues.put(
				objectField.getName(),
				getDefaultValue(ddmExpressionFactory, objectField, null));
		}

		return defaultValues;
	}

	public static String getTimeZoneId(
		List<ObjectFieldSetting> objectFieldSettings, User user) {

		if ((user == null) ||
			(ListUtil.isNotEmpty(objectFieldSettings) &&
			 !StringUtil.equals(
				 getValue(
					 ObjectFieldSettingConstants.NAME_TIME_STORAGE,
					 objectFieldSettings),
				 ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC))) {

			return null;
		}

		return user.getTimeZoneId();
	}

	public static String getValue(
		String name, List<ObjectFieldSetting> objectFieldSettings) {

		for (ObjectFieldSetting objectFieldSetting : objectFieldSettings) {
			if (Objects.equals(objectFieldSetting.getName(), name)) {
				return objectFieldSetting.getValue();
			}
		}

		return null;
	}

	public static String getValue(String name, ObjectField objectField) {
		return getValue(name, objectField.getObjectFieldSettings());
	}

	public static boolean isUnique(
		List<ObjectFieldSetting> objectFieldSetting) {

		if (ListUtil.isEmpty(objectFieldSetting)) {
			return false;
		}

		return GetterUtil.getBoolean(
			getValue(
				ObjectFieldSettingConstants.NAME_UNIQUE_VALUES,
				objectFieldSetting));
	}

	private static String _getDateString(Object defaultValue, String pattern)
		throws ParseException {

		return DateUtil.getDate(
			DateUtil.parseDate(
				ObjectFieldUtil.getDateTimePattern(defaultValue.toString()),
				defaultValue.toString(), LocaleUtil.getSiteDefault()),
			pattern, LocaleUtil.getSiteDefault());
	}

	private static ObjectFieldSetting _getObjectFieldSetting(
		List<ObjectFieldSetting> objectFieldSettings, String name) {

		for (ObjectFieldSetting objectFieldSetting : objectFieldSettings) {
			if (Objects.equals(objectFieldSetting.getName(), name)) {
				return objectFieldSetting;
			}
		}

		return null;
	}

	private static Object _parseValue(String dbType, String value) {
		Class<?> clazz = DynamicObjectDefinitionTableUtil.getJavaClass(dbType);

		if (clazz == Boolean.class) {
			return Boolean.parseBoolean(value);
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFieldSettingUtil.class);

}