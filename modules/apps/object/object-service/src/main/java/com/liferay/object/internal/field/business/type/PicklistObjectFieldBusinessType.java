/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Collections;
import java.util.HashMap;
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
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
	service = ObjectFieldBusinessType.class
)
public class PicklistObjectFieldBusinessType
	implements ObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
			ObjectFieldSettingConstants.NAME_STATE_FLOW);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_STRING;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.SELECT;
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
		return _language.get(locale, "picklist");
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
		return ObjectFieldConstants.BUSINESS_TYPE_PICKLIST;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"listTypeDefinitionId", objectField.getListTypeDefinitionId()
		).put(
			"options",
			_getDDMFormFieldOptions(objectField, objectFieldRenderingContext)
		).put(
			"predefinedValue",
			() -> {
				LocalizedValue localizedValue = new LocalizedValue(
					objectFieldRenderingContext.getLocale());

				Locale defaultLocale = objectFieldRenderingContext.getLocale();
				String defaultValue = String.valueOf(
					ObjectFieldSettingUtil.getDefaultValue(
						null, objectField, null));

				if (objectField.isLocalized() &&
					Validator.isNotNull(defaultValue)) {

					localizedValue.addString(
						defaultLocale,
						_jsonFactory.createJSONObject(
							HashMapBuilder.put(
								defaultLocale, defaultValue
							).build()
						).toJSONString());
				}
				else {
					localizedValue.addString(defaultLocale, defaultValue);
				}

				return localizedValue;
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
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		if (!objectField.isState()) {
			return Collections.emptySet();
		}

		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE);
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		return _getValue(
			objectField.getName(),
			ObjectFieldBusinessType.super.getValue(
				groupId, objectField, userId, values),
			values);
	}

	@Override
	public void predefineObjectFieldSettings(
			ObjectField newObjectField, ObjectField oldObjectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		for (ObjectFieldSetting objectFieldSetting : objectFieldSettings) {
			if (!StringUtil.equals(
					objectFieldSetting.getName(),
					ObjectFieldSettingConstants.NAME_STATE_FLOW) ||
				(objectFieldSetting.getObjectStateFlow() == null)) {

				continue;
			}

			ObjectStateFlow newObjectStateFlow =
				objectFieldSetting.getObjectStateFlow();

			if (oldObjectField != null) {
				ObjectStateFlow oldObjectStateFlow =
					_objectStateFlowLocalService.
						fetchObjectFieldObjectStateFlow(
							oldObjectField.getObjectFieldId());

				_objectStateFlowLocalService.updateObjectStateFlow(
					newObjectField.getUserId(),
					oldObjectStateFlow.getObjectStateFlowId(),
					newObjectStateFlow.getObjectStates());

				_objectStateFlowLocalService.updateDefaultObjectStateFlow(
					newObjectField, oldObjectField);
			}
			else {
				_objectStateFlowLocalService.addObjectStateFlow(
					newObjectField.getUserId(),
					newObjectField.getObjectFieldId(),
					newObjectStateFlow.getObjectStates());
			}

			return;
		}

		_objectStateFlowLocalService.addDefaultObjectStateFlow(newObjectField);
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

		String defaultValueType = objectFieldSettingsValuesMap.get(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE);

		if (StringUtil.equals(
				defaultValueType,
				ObjectFieldSettingConstants.VALUE_EXPRESSION_BUILDER)) {

			if (objectField.isState()) {
				throw new ObjectFieldSettingValueException.InvalidValue(
					objectField.getName(),
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
					defaultValueType);
			}

			return;
		}

		String defaultValue = objectFieldSettingsValuesMap.get(
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE);

		if (defaultValue == null) {
			return;
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), defaultValue);

		if (listTypeEntry == null) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE, defaultValue);
		}
	}

	private DDMFormFieldOptions _getDDMFormFieldOptions(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions(
			objectFieldRenderingContext.getLocale());

		for (ListTypeEntry listTypeEntry :
				_getListTypeEntries(objectField, objectFieldRenderingContext)) {

			Map<Locale, String> nameMap = listTypeEntry.getNameMap();

			for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
				ddmFormFieldOptions.addOptionLabel(
					listTypeEntry.getKey(), entry.getKey(),
					GetterUtil.getString(entry.getValue()));
			}
		}

		return ddmFormFieldOptions;
	}

	private List<ListTypeEntry> _getListTypeEntries(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		if (!objectField.isState()) {
			return _listTypeEntryLocalService.getListTypeEntries(
				objectField.getListTypeDefinitionId());
		}

		String listEntryKey = String.valueOf(
			ObjectFieldSettingUtil.getDefaultValue(null, objectField, null));

		if (MapUtil.isNotEmpty(objectFieldRenderingContext.getProperties())) {
			ListEntry listEntry =
				(ListEntry)objectFieldRenderingContext.getProperty(
					objectField.getName());

			if ((listEntry == null) || Validator.isNull(listEntry.getKey())) {
				return _listTypeEntryLocalService.getListTypeEntries(
					objectField.getListTypeDefinitionId());
			}

			listEntryKey = listEntry.getKey();
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), listEntryKey);

		if (listTypeEntry == null) {
			return Collections.emptyList();
		}

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		ObjectState objectState =
			_objectStateLocalService.getObjectStateFlowObjectState(
				listTypeEntry.getListTypeEntryId(),
				objectStateFlow.getObjectStateFlowId());

		List<ListTypeEntry> listTypeEntries = TransformUtil.transform(
			_objectStateLocalService.getNextObjectStates(
				objectState.getObjectStateId()),
			nextObjectState -> _listTypeEntryLocalService.getListTypeEntry(
				nextObjectState.getListTypeEntryId()));

		listTypeEntries.add(
			_listTypeEntryLocalService.getListTypeEntry(
				objectState.getListTypeEntryId()));

		return listTypeEntries;
	}

	private Object _getValue(
		String objectFieldName, Object value, Map<String, Object> values) {

		if (value instanceof ListEntry) {
			ListEntry listEntry = (ListEntry)value;

			values.put(objectFieldName, listEntry.getKey());

			return listEntry.getKey();
		}
		else if (value instanceof Map) {
			String key = MapUtil.getString((Map<String, String>)value, "key");

			values.put(objectFieldName, key);

			return key;
		}

		return value;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Reference
	private ObjectStateLocalService _objectStateLocalService;

}