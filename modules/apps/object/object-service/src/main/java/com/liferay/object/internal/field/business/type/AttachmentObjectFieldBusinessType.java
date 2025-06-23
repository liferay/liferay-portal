/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
	service = ObjectFieldBusinessType.class
)
public class AttachmentObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_DOCS_AND_MEDIA,
			ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_LONG;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.ATTACHMENT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale, "upload-files-or-select-from-documents-and-media");
	}

	@Override
	public Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		if (objectField.isLocalized()) {
			return getLocalizedValues(objectField, userId, values);
		}

		return super.getDisplayContextValue(objectField, userId, values);
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "attachment");
	}

	@Override
	public Map<String, Object> getLocalizedValues(
			ObjectField objectField, Long userId, Map<String, Object> values)
		throws PortalException {

		Map<String, Object> localizedValues = super.getLocalizedValues(
			objectField, userId, values);

		if (localizedValues == null) {
			return null;
		}

		for (Map.Entry<String, Object> entry : localizedValues.entrySet()) {
			localizedValues.put(
				entry.getKey(), _getFileEntryId(entry.getValue()));
		}

		return localizedValues;
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		Map<String, Object> properties = super.getProperties(
			objectField, objectFieldRenderingContext);

		properties.remove(
			ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_DOCS_AND_MEDIA);
		properties.remove(
			ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH);

		return HashMapBuilder.<String, Object>put(
			"groupAware",
			() -> {
				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.getObjectDefinition(
						objectField.getObjectDefinitionId());

				ObjectScopeProvider objectScopeProvider =
					_objectScopeProviderRegistry.getObjectScopeProvider(
						objectDefinition.getScope());

				return objectScopeProvider.isGroupAware();
			}
		).put(
			"objectFieldId", objectField.getObjectFieldId()
		).put(
			"portletId", objectFieldRenderingContext.getPortletId()
		).putAll(
			properties
		).build();
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.LONG;
	}

	@Override
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS,
			ObjectFieldSettingConstants.NAME_FILE_SOURCE,
			ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE);
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		return _getFileEntryId(
			super.getValue(groupId, objectField, userId, values));
	}

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		return objectDefinition.isDefaultStorageType();
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		Map<String, String> objectFieldSettingsValues =
			getObjectFieldSettingsValues(objectFieldSettings);

		String fileSource = objectFieldSettingsValues.get(
			ObjectFieldSettingConstants.NAME_FILE_SOURCE);

		if (Objects.equals(
				fileSource, ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA)) {

			validateNotAllowedObjectFieldSettingNames(
				SetUtil.fromArray(
					ObjectFieldSettingConstants.
						NAME_SHOW_FILES_IN_DOCS_AND_MEDIA,
					ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH),
				objectField.getName(), objectFieldSettingsValues);
		}
		else if (Objects.equals(
					fileSource,
					ObjectFieldSettingConstants.VALUE_USER_COMPUTER)) {

			validateRelatedObjectFieldSettings(
				objectField,
				ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_DOCS_AND_MEDIA,
				ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH,
				objectFieldSettingsValues);
		}
		else {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_FILE_SOURCE, fileSource);
		}

		BigDecimal bigDecimal = null;

		try {
			bigDecimal = new BigDecimal(
				objectFieldSettingsValues.get(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE));
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}
		}

		if ((bigDecimal == null) || (bigDecimal.signum() == -1)) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE,
				objectFieldSettingsValues.get(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE));
		}
	}

	private Object _getFileEntryId(Object value) throws PortalException {
		long fileEntryId = GetterUtil.getLong(value);

		if (fileEntryId > 0) {
			return fileEntryId;
		}

		if (value instanceof Map) {
			fileEntryId = MapUtil.getLong((Map<String, Object>)value, "id");
		}
		else {
			String valueString = String.valueOf(value);

			if (JSONUtil.isJSONObject(valueString)) {
				JSONObject jsonObject = jsonFactory.createJSONObject(
					valueString);

				fileEntryId = GetterUtil.getLong(jsonObject.get("id"));
			}
		}

		if (fileEntryId > 0) {
			return fileEntryId;
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AttachmentObjectFieldBusinessType.class);

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}