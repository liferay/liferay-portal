/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Paulo Albuquerque
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED,
	service = ObjectFieldBusinessType.class
)
public class EncryptedObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_MAX_LENGTH,
			ObjectFieldSettingConstants.NAME_SHOW_COUNTER);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_CLOB;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.TEXT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "store-content-as-encrypted-text");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "encrypted");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
	}

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		if (objectDefinition.isDefaultStorageType() &&
			PropsValues.OBJECT_ENCRYPTION_ENABLED) {

			return true;
		}

		return false;
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		validateRelatedObjectFieldSettings(
			objectField, ObjectFieldSettingConstants.NAME_SHOW_COUNTER,
			ObjectFieldSettingConstants.NAME_MAX_LENGTH,
			getObjectFieldSettingsValues(objectFieldSettings));
	}

	@Reference
	private Language _language;

}