/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.business.type;

import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectFieldSettingNameException;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Marcela Cunha
 */
public interface ObjectFieldBusinessType {

	public default Set<String> getAllowedObjectFieldSettingsNames() {
		return Collections.emptySet();
	}

	public String getDBType();

	public String getDDMFormFieldTypeName();

	public default String getDDMFormFieldTypeName(boolean localized) {
		return getDDMFormFieldTypeName();
	}

	public default String getDescription(Locale locale) {
		return StringPool.BLANK;
	}

	public default Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		if (objectField.isLocalized()) {
			return values.get(objectField.getI18nObjectFieldName());
		}

		return getValue(objectField, userId, values);
	}

	public String getLabel(Locale locale);

	public default Map<String, Object> getLocalizedValues(
			ObjectField objectField, Long userId, Map<String, Object> values)
		throws PortalException {

		Object value = values.get(objectField.getI18nObjectFieldName());

		if (value == null) {
			return null;
		}

		if (!(value instanceof Map<?, ?>)) {
			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getI18nObjectFieldName());
		}

		return (Map<String, Object>)value;
	}

	public String getName();

	public default Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"editOnlyInDefaultLanguage",
			FeatureFlagManagerUtil.isEnabled("LPD-32050") &&
			!GetterUtil.getBoolean(objectField.getReadOnly()) &&
			!objectField.isLocalized()
		).put(
			"isLocalizationSupported", isLocalizationSupported(objectField)
		).put(
			"localizedObjectField", objectField.isLocalized()
		).build();
	}

	public PropertyDefinition.PropertyType getPropertyType();

	public default Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return Collections.emptySet();
	}

	public default Set<String> getUnmodifiableObjectFieldSettingsNames() {
		return Collections.emptySet();
	}

	public default Object getValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		if (!objectField.isLocalized()) {
			return values.get(objectField.getName());
		}

		Object value = values.get(objectField.getI18nObjectFieldName());

		if (value == null) {
			return values.get(objectField.getName());
		}

		if (!(value instanceof Map<?, ?>)) {
			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getI18nObjectFieldName());
		}

		Map<String, Object> localizedValues = (Map<String, Object>)value;

		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		if (locale == null) {
			locale = LocaleThreadLocal.getSiteDefaultLocale();
		}

		if (locale == null) {
			User user = GuestOrUserUtil.getGuestOrUser();

			locale = user.getLocale();
		}

		Object localizedValue = localizedValues.get(
			LocaleUtil.toLanguageId(locale));

		if (localizedValue != null) {
			return localizedValue;
		}

		return StringPool.BLANK;
	}

	public default boolean isLocalizationSupported(ObjectField objectField) {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-32050") ||
			objectField.isMetadata()) {

			return false;
		}

		return true;
	}

	public default boolean isVisible(ObjectDefinition objectDefinition) {
		return true;
	}

	public default void predefineObjectFieldSettings(
			ObjectField newObjectField, ObjectField oldObjectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {
	}

	public default void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		Set<String> missingRequiredObjectFieldSettingsNames = new HashSet<>();

		Map<String, String> objectFieldSettingsValuesMap = new HashMap<>();

		for (ObjectFieldSetting objectFieldSetting : objectFieldSettings) {
			objectFieldSettingsValuesMap.put(
				objectFieldSetting.getName(), objectFieldSetting.getValue());
		}

		for (String requiredObjectFieldSettingName :
				getRequiredObjectFieldSettingsNames(objectField)) {

			if (Validator.isNull(
					objectFieldSettingsValuesMap.get(
						requiredObjectFieldSettingName))) {

				missingRequiredObjectFieldSettingsNames.add(
					requiredObjectFieldSettingName);
			}
		}

		if (!missingRequiredObjectFieldSettingsNames.isEmpty()) {
			throw new ObjectFieldSettingValueException.MissingRequiredValues(
				objectField.getName(), missingRequiredObjectFieldSettingsNames);
		}

		Set<String> notAllowedObjectFieldSettingsNames = new HashSet<>(
			objectFieldSettingsValuesMap.keySet());

		notAllowedObjectFieldSettingsNames.removeAll(
			getAllowedObjectFieldSettingsNames());
		notAllowedObjectFieldSettingsNames.removeAll(
			getRequiredObjectFieldSettingsNames(objectField));

		if (!notAllowedObjectFieldSettingsNames.isEmpty()) {
			throw new ObjectFieldSettingNameException.NotAllowedNames(
				objectField.getName(), notAllowedObjectFieldSettingsNames);
		}

		validateObjectFieldSettingsDefaultValue(
			objectField, objectFieldSettingsValuesMap);
	}

	public default void validateObjectFieldSettingsDefaultValue(
			ObjectField objectField,
			Map<String, String> objectFieldSettingsValuesMap)
		throws PortalException {

		String defaultValueType = objectFieldSettingsValuesMap.get(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE);

		if (defaultValueType == null) {
			return;
		}

		if (!(StringUtil.equals(
				defaultValueType,
				ObjectFieldSettingConstants.VALUE_EXPRESSION_BUILDER) ||
			  StringUtil.equals(
				  defaultValueType,
				  ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE))) {

			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
				defaultValueType);
		}
	}

}