/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT,
	service = ObjectFieldBusinessType.class
)
public class AutoIncrementObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_PREFIX,
			ObjectFieldSettingConstants.NAME_SUFFIX);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_STRING;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.AUTO_INCREMENT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale,
			"automatically-generate-a-unique-value-when-a-new-entry-is-added");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "auto-increment");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		return super.getProperties(objectField, objectFieldRenderingContext);
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return Collections.singleton(
			ObjectFieldSettingConstants.NAME_INITIAL_VALUE);
	}

	@Override
	public Set<String> getUnmodifiableObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_INITIAL_VALUE,
			ObjectFieldSettingConstants.NAME_PREFIX,
			ObjectFieldSettingConstants.NAME_SUFFIX);
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		Map<String, String> objectFieldSettingsValues =
			getObjectFieldSettingsValues(objectFieldSettings);

		validateMaxLength(
			_MAX_LENGTH, ObjectFieldSettingConstants.NAME_PREFIX,
			objectFieldSettingsValues.get(
				ObjectFieldSettingConstants.NAME_PREFIX));
		validateMaxLength(
			_MAX_LENGTH, ObjectFieldSettingConstants.NAME_SUFFIX,
			objectFieldSettingsValues.get(
				ObjectFieldSettingConstants.NAME_SUFFIX));

		_validatePattern(
			objectField.getName(), ObjectFieldSettingConstants.NAME_PREFIX,
			objectFieldSettingsValues.get(
				ObjectFieldSettingConstants.NAME_PREFIX));
		_validatePattern(
			objectField.getName(), ObjectFieldSettingConstants.NAME_SUFFIX,
			objectFieldSettingsValues.get(
				ObjectFieldSettingConstants.NAME_SUFFIX));

		long initialValue = 0;

		try {
			initialValue = Long.parseUnsignedLong(
				objectFieldSettingsValues.get(
					ObjectFieldSettingConstants.NAME_INITIAL_VALUE));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}
		}

		if (initialValue == 0) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_INITIAL_VALUE,
				objectFieldSettingsValues.get(
					ObjectFieldSettingConstants.NAME_INITIAL_VALUE));
		}
	}

	private void _validatePattern(
			String objectFieldName, String objectFieldSettingName,
			String objectFieldSettingValue)
		throws PortalException {

		if (Validator.isNull(objectFieldSettingValue)) {
			return;
		}

		Matcher matcher = _pattern.matcher(objectFieldSettingValue);

		if (matcher.matches()) {
			return;
		}

		throw new ObjectFieldSettingValueException.InvalidValue(
			objectFieldName, objectFieldSettingName, objectFieldSettingValue);
	}

	private static final int _MAX_LENGTH = 50;

	private static final Log _log = LogFactoryUtil.getLog(
		AutoIncrementObjectFieldBusinessType.class);

	private static final Pattern _pattern = Pattern.compile(
		"^[A-Za-z0-9\\s-\\/:,.\\(\\)\\[\\]\\{\\}#$%+]*$");

	@Reference
	private Language _language;

}