/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
	service = ObjectFieldBusinessType.class
)
public class IntegerObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_INTEGER;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.NUMERIC;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "add-an-integer-up-to-nine-digits");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "integer");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_INTEGER;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			FieldConstants.DATA_TYPE, FieldConstants.INTEGER
		).putAll(
			super.getProperties(objectField, objectFieldRenderingContext)
		).build();
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.INTEGER;
	}

	@Override
	public Set<String> getUnmodifiableObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_UNIQUE_VALUES);
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		validateBooleanObjectFieldSetting(
			objectField.getName(),
			ObjectFieldSettingConstants.NAME_UNIQUE_VALUES,
			getObjectFieldSettingsValues(objectFieldSettings));
	}

	@Reference
	private Language _language;

}