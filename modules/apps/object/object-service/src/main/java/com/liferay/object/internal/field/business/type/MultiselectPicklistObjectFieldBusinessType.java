/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Paulo Albuquerque
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
	service = ObjectFieldBusinessType.class
)
public class MultiselectPicklistObjectFieldBusinessType
	implements ObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_STRING;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.MULTISELECT_PICKLIST;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "choose-from-a-picklist");
	}

	@Override
	public Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		if (objectField.isLocalized()) {
			return getLocalizedValues(objectField, userId, values);
		}

		return ObjectFieldBusinessType.super.getDisplayContextValue(
			objectField, userId, values);
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "multiselect-picklist");
	}

	@Override
	public Map<String, Object> getLocalizedValues(
			ObjectField objectField, Long userId, Map<String, Object> values)
		throws PortalException {

		Map<String, Object> localizedValues =
			ObjectFieldBusinessType.super.getLocalizedValues(
				objectField, userId, values);

		if (localizedValues == null) {
			return null;
		}

		for (Map.Entry<String, Object> entry : localizedValues.entrySet()) {
			localizedValues.put(
				entry.getKey(),
				_getValue(
					objectField.getName(), entry.getValue(), new HashMap<>()));
		}

		return localizedValues;
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"defaultSearch", true
		).put(
			"listTypeDefinitionId", objectField.getListTypeDefinitionId()
		).put(
			"multiple", true
		).put(
			"options",
			() -> {
				DDMFormFieldOptions ddmFormFieldOptions =
					new DDMFormFieldOptions(
						objectFieldRenderingContext.getLocale());

				for (ListTypeEntry listTypeEntry :
						_listTypeEntryLocalService.getListTypeEntries(
							objectField.getListTypeDefinitionId())) {

					Map<Locale, String> nameMap = listTypeEntry.getNameMap();

					for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
						ddmFormFieldOptions.addOptionLabel(
							listTypeEntry.getKey(), entry.getKey(),
							GetterUtil.getString(entry.getValue()));
					}
				}

				return ddmFormFieldOptions;
			}
		).putAll(
			ObjectFieldBusinessType.super.getProperties(
				objectField, objectFieldRenderingContext)
		).build();
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public Object getValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		return _getValue(
			objectField.getName(),
			ObjectFieldBusinessType.super.getValue(objectField, userId, values),
			values);
	}

	private Object _getValue(
		String objectFieldName, Object value, Map<String, Object> values) {

		if (value instanceof List) {
			List<String> keys = new ArrayList<>();

			for (Object object : (List<Object>)value) {
				if (object instanceof ListEntry) {
					ListEntry listEntry = (ListEntry)object;

					keys.add(listEntry.getKey());
				}
				else if (object instanceof Map) {
					keys.add(
						MapUtil.getString((Map<String, String>)object, "key"));
				}
				else {
					keys.add((String)object);
				}
			}

			values.put(objectFieldName, keys);

			return keys;
		}
		else if (value instanceof String) {
			String valueString = GetterUtil.getString(value);

			if (StringUtil.equals(valueString, "[]")) {
				return StringPool.BLANK;
			}
			else if (valueString.contains(StringPool.COMMA_AND_SPACE)) {
				List<String> keys = ListUtil.fromString(
					valueString, StringPool.COMMA_AND_SPACE);

				values.put(objectFieldName, keys);

				return keys;
			}
		}

		return value;
	}

	@Reference
	private Language _language;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

}