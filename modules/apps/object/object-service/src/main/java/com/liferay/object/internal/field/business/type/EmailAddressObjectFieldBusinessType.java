/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nathaly Gomes
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_EMAIL_ADDRESS,
	service = ObjectFieldBusinessType.class
)
public class EmailAddressObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_DOMAINS,
			ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_ENABLED,
			ObjectFieldSettingConstants.NAME_BLOCKED_DOMAINS,
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
			ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_STRING;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.EMAIL_ADDRESS;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "store-and-validate-email-addresses");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "email-address");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_EMAIL_ADDRESS;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public Set<String> getUnmodifiableObjectFieldSettingsNames() {
		return Collections.singleton(
			ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);
	}

	@Override
	public void predefineObjectFieldSettings(
			ObjectField newObjectField, ObjectField oldObjectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		for (ObjectFieldSetting objectFieldSetting : objectFieldSettings) {
			if (Validator.isNull(objectFieldSetting.getValue())) {
				continue;
			}

			if (!objectFieldSetting.compareName(
					ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_DOMAINS) &&
				!objectFieldSetting.compareName(
					ObjectFieldSettingConstants.NAME_BLOCKED_DOMAINS) &&
				!objectFieldSetting.compareName(
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE)) {

				continue;
			}

			objectFieldSetting.setValue(
				StringUtil.toLowerCase(objectFieldSetting.getValue()));
		}
	}

	@Override
	public Serializable processValue(
			ObjectField objectField, Serializable serializable)
		throws PortalException {

		String value = GetterUtil.getString(serializable);

		if (Validator.isNull(value)) {
			return value;
		}

		_validateEmailAddress(
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.NAME_BLOCKED_DOMAINS, objectField),
			value, objectField);

		return StringUtil.toLowerCase(value);
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		Map<String, String> objectFieldSettingsValues =
			getObjectFieldSettingsValues(objectFieldSettings);

		validateBooleanObjectFieldSetting(
			objectField.getName(),
			ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_ENABLED,
			objectFieldSettingsValues);
		validateBooleanObjectFieldSetting(
			objectField.getName(),
			ObjectFieldSettingConstants.NAME_UNIQUE_VALUES,
			objectFieldSettingsValues);
		_validateEmailAddressDomains(
			objectField.getName(),
			ObjectFieldSettingConstants.NAME_BLOCKED_DOMAINS,
			objectFieldSettingsValues);

		if (StringUtil.equalsIgnoreCase(
				objectFieldSettingsValues.get(
					ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_ENABLED),
				StringPool.TRUE)) {

			_validateEmailAddressDomains(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_DOMAINS,
				objectFieldSettingsValues);
		}
		else {
			validateNotAllowedObjectFieldSettingNames(
				SetUtil.fromArray(
					ObjectFieldSettingConstants.NAME_AUTOCOMPLETE_DOMAINS),
				objectField.getName(), objectFieldSettingsValues);
		}
	}

	@Override
	public void validateObjectFieldSettingsDefaultValue(
			ObjectField objectField,
			Map<String, String> objectFieldSettingsValuesMap)
		throws PortalException {

		if (objectFieldSettingsValuesMap.isEmpty()) {
			return;
		}

		super.validateObjectFieldSettingsDefaultValue(
			objectField, objectFieldSettingsValuesMap);

		String defaultValue = objectFieldSettingsValuesMap.get(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE);

		if (Validator.isNull(defaultValue)) {
			return;
		}

		try {
			_validateEmailAddress(
				objectFieldSettingsValuesMap.get(
					ObjectFieldSettingConstants.NAME_BLOCKED_DOMAINS),
				defaultValue, objectField);
		}
		catch (ObjectEntryValuesException objectEntryValuesException) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE, defaultValue,
				objectEntryValuesException);
		}
	}

	private void _validateEmailAddress(
			String blockedDomains, String emailAddress, ObjectField objectField)
		throws PortalException {

		String normalizedEmailAddress = StringUtil.toLowerCase(emailAddress);

		int index = normalizedEmailAddress.lastIndexOf(CharPool.AT);

		if (index > 0) {
			String domain = normalizedEmailAddress.substring(index);

			for (String blockedDomain : StringUtil.split(blockedDomains)) {
				if (StringUtil.equalsIgnoreCase(domain, blockedDomain.trim())) {
					throw new ObjectEntryValuesException.
						BlockedEmailAddressDomain(
							domain, objectField.getName());
				}
			}
		}

		int maxLength = DynamicObjectDefinitionTableUtil.getMaxLength(
			objectField.getBusinessType());

		if (normalizedEmailAddress.length() > maxLength) {
			throw new ObjectEntryValuesException.ExceedsTextMaxLength(
				maxLength, objectField.getName());
		}

		if (!Validator.isEmailAddress(normalizedEmailAddress)) {
			throw new ObjectEntryValuesException.InvalidEmailAddress(
				emailAddress, objectField.getName());
		}
	}

	private void _validateEmailAddressDomains(
			String objectFieldName, String objectFieldSettingName,
			Map<String, String> objectFieldSettingsValues)
		throws PortalException {

		for (String domain :
				StringUtil.split(
					objectFieldSettingsValues.get(objectFieldSettingName))) {

			String normalizedDomain = StringUtil.toLowerCase(domain.trim());

			if (!StringUtil.startsWith(normalizedDomain, CharPool.AT) ||
				!Validator.isDomain(
					StringUtil.extractLast(normalizedDomain, CharPool.AT))) {

				throw new ObjectFieldSettingValueException.InvalidValue(
					objectFieldName, objectFieldSettingName, domain.trim());
			}
		}
	}

	@Reference
	private Language _language;

}