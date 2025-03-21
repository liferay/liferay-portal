/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
	service = ObjectFieldBusinessType.class
)
public class BooleanObjectFieldBusinessType implements ObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_BOOLEAN;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.CHECKBOX;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "select-between-true-or-false");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "boolean");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.BOOLEAN;
	}

	@Override
	public void validateObjectFieldSettingsDefaultValue(
			ObjectField objectField,
			Map<String, String> objectFieldSettingsValuesMap)
		throws PortalException {

		if (objectFieldSettingsValuesMap.isEmpty()) {
			return;
		}

		ObjectFieldBusinessType.super.validateObjectFieldSettingsDefaultValue(
			objectField, objectFieldSettingsValuesMap);

		if (Objects.equals(
				objectFieldSettingsValuesMap.get(
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE),
				ObjectFieldSettingConstants.VALUE_EXPRESSION_BUILDER)) {

			return;
		}

		String defaultValue = objectFieldSettingsValuesMap.get(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE);

		if (Objects.equals(defaultValue, "false") ||
			Objects.equals(defaultValue, "true")) {

			return;
		}

		throw new ObjectFieldSettingValueException.InvalidValue(
			objectField.getName(),
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE, defaultValue);
	}

	@Reference
	private Language _language;

}