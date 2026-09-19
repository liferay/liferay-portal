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
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.io.Serializable;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_DATE,
	service = ObjectFieldBusinessType.class
)
public class DateObjectFieldBusinessType extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_DATE;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.DATE;
	}

	@Override
	public Serializable getDTOValue(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ObjectField objectField, Serializable serializable)
		throws Exception {

		if (Validator.isNull(serializable)) {
			return null;
		}

		if (serializable instanceof String) {
			Date date = DateUtil.parseDate(
				"yyyy-MM-dd", (String)serializable,
				LocaleUtil.getSiteDefault());

			serializable = new Timestamp(date.getTime());
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		return simpleDateFormat.format((Timestamp)serializable);
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "add-a-date");
	}

	@Override
	public Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		if (objectField.isLocalized()) {
			Map<String, Object> localizedValues = super.getLocalizedValues(
				objectField, userId, values);

			if (localizedValues == null) {
				return null;
			}

			for (Map.Entry<String, Object> entry : localizedValues.entrySet()) {
				localizedValues.put(
					entry.getKey(),
					_getValue(GetterUtil.getString(entry.getValue())));
			}

			return localizedValues;
		}

		return _getValue(MapUtil.getString(values, objectField.getName()));
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "date");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_DATE;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.DATE_TIME;
	}

	@Override
	public boolean isAllowedObjectFieldSettingValue(
		String objectFieldSettingName, String objectFieldSettingValue) {

		if (super.isAllowedObjectFieldSettingValue(
				objectFieldSettingName, objectFieldSettingValue) ||
			(objectFieldSettingName.equals(
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE) &&
			 objectFieldSettingValue.equals(
				 ObjectFieldSettingConstants.VALUE_EXPRESSION_BUILDER))) {

			return true;
		}

		return false;
	}

	private String _getValue(String value) {
		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		return value.replaceAll(".[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}.*", "");
	}

	@Reference
	private Language _language;

}