/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.setting.util;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class ObjectFieldSettingUtil {

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

			ddmExpression.setVariables(values);

			return ddmExpression.evaluate();
		}
		catch (DDMExpressionException ddmExpressionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ddmExpressionException);
			}

			return StringPool.BLANK;
		}
	}

	public static Map<String, Object> getDefaultValues(
		long objectDefinitionId) {

		Map<String, Object> defaultValues = new HashMap<>();

		for (ObjectField objectField :
				ObjectFieldLocalServiceUtil.getObjectFields(
					objectDefinitionId)) {

			defaultValues.put(
				objectField.getName(),
				getDefaultValue(null, objectField, null));
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