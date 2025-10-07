/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.entry.util;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.extension.EntityExtensionThreadLocal;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryUtil {

	public static JSONObject getPayloadJSONObject(
			BaseModel<?> baseModel, DTOConverterRegistry dtoConverterRegistry,
			JSONFactory jsonFactory, String objectActionTriggerKey,
			ObjectDefinition objectDefinition, BaseModel<?> originalBaseModel,
			SystemObjectDefinitionManager systemObjectDefinitionManager,
			long userId)
		throws PortalException {

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			systemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		Class<?> modelClass = systemObjectDefinitionManager.getModelClass();

		DTOConverter<BaseModel<?>, ?> dtoConverter =
			(DTOConverter<BaseModel<?>, ?>)dtoConverterRegistry.getDTOConverter(
				jaxRsApplicationDescriptor.getApplicationName(),
				modelClass.getName(), jaxRsApplicationDescriptor.getVersion());

		String dtoConverterType = null;

		if (dtoConverter == null) {
			dtoConverterType = modelClass.getSimpleName();
		}
		else {
			dtoConverterType = dtoConverter.getContentType();
		}

		Map<String, Serializable> originalExtendedProperties =
			ObjectEntryLocalServiceUtil.
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition,
					GetterUtil.getLong(baseModel.getPrimaryKeyObj()));

		Map<String, Object> extendedProperties =
			HashMapBuilder.<String, Object>putAll(
				originalExtendedProperties
			).putAll(
				EntityExtensionThreadLocal.getExtendedProperties()
			).build();

		return JSONUtil.put(
			"classPK", baseModel.getPrimaryKeyObj()
		).put(
			"extendedProperties", extendedProperties
		).put(
			"model" + modelClass.getSimpleName(), baseModel.getModelAttributes()
		).put(
			"modelDTO" + dtoConverterType,
			_toDTO(
				baseModel, dtoConverter, dtoConverterRegistry,
				extendedProperties, jsonFactory, modelClass, userId)
		).put(
			"objectActionTriggerKey", objectActionTriggerKey
		).put(
			"original" + modelClass.getSimpleName(),
			() -> {
				if (originalBaseModel == null) {
					return null;
				}

				return originalBaseModel.getModelAttributes();
			}
		).put(
			"originalDTO" + dtoConverterType,
			() -> {
				if (originalBaseModel == null) {
					return null;
				}

				Map<String, Object> originalDTOMap = _toDTO(
					originalBaseModel, dtoConverter, dtoConverterRegistry,
					Collections.emptyMap(), jsonFactory, modelClass, userId);

				if (MapUtil.isEmpty(
						ObjectEntryThreadLocal.getExpandoBridgeAttributes())) {

					return originalDTOMap;
				}

				User user = UserLocalServiceUtil.fetchUser(userId);

				originalDTOMap.put(
					"customFields",
					CustomFieldsUtil.toCustomFields(
						false, modelClass.getName(),
						GetterUtil.getLong(
							originalBaseModel.getPrimaryKeyObj()),
						objectDefinition.getCompanyId(),
						ObjectEntryThreadLocal.getExpandoBridgeAttributes(),
						user.getLocale()));

				return originalDTOMap;
			}
		).put(
			"originalExtendedProperties", originalExtendedProperties
		);
	}

	public static JSONObject getPayloadJSONObject(
			DTOConverterRegistry dtoConverterRegistry, JSONFactory jsonFactory,
			ObjectDefinition objectDefinition, long primaryKey,
			SystemObjectDefinitionManagerRegistry
				systemObjectDefinitionManagerRegistry,
			long userId)
		throws PortalException {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition.getName());

			return getPayloadJSONObject(
				systemObjectDefinitionManager.
					fetchBaseModelByExternalReferenceCode(
						systemObjectDefinitionManager.
							getBaseModelExternalReferenceCode(primaryKey),
						objectDefinition.getCompanyId()),
				dtoConverterRegistry, jsonFactory, null, objectDefinition, null,
				systemObjectDefinitionManager, userId);
		}

		return getPayloadJSONObject(
			dtoConverterRegistry, jsonFactory, null, objectDefinition,
			ObjectEntryLocalServiceUtil.fetchObjectEntry(primaryKey), null,
			null, UserLocalServiceUtil.fetchUser(userId));
	}

	public static JSONObject getPayloadJSONObject(
		DTOConverterRegistry dtoConverterRegistry, JSONFactory jsonFactory,
		String objectActionTriggerKey, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry, ObjectEntry originalObjectEntry,
		String preferredLanguageId, User user) {

		return JSONUtil.put(
			"classPK", objectEntry.getObjectEntryId()
		).put(
			"objectActionTriggerKey", objectActionTriggerKey
		).put(
			"objectEntry",
			HashMapBuilder.putAll(
				objectEntry.getModelAttributes()
			).put(
				"creator", user.getFullName()
			).put(
				"id", objectEntry.getObjectEntryId()
			).put(
				"values", objectEntry.getValues()
			).build()
		).put(
			"objectEntryDTO" + objectDefinition.getShortName(),
			_toDTO(dtoConverterRegistry, jsonFactory, objectEntry, user)
		).put(
			"originalObjectEntry",
			() -> {
				if (originalObjectEntry == null) {
					return null;
				}

				return HashMapBuilder.putAll(
					originalObjectEntry.getModelAttributes()
				).put(
					"values", originalObjectEntry.getValues()
				).build();
			}
		).put(
			"originalObjectEntryDTO" + objectDefinition.getShortName(),
			() -> {
				if (originalObjectEntry == null) {
					return null;
				}

				return _toDTO(
					dtoConverterRegistry, jsonFactory, originalObjectEntry,
					user);
			}
		).put(
			"preferredLanguageId", preferredLanguageId
		).put(
			"userExternalReferenceCode", user.getExternalReferenceCode()
		).put(
			"userId", user.getUserId()
		);
	}

	private static Map<String, Object> _toDTO(
		BaseModel<?> baseModel, DTOConverter<BaseModel<?>, ?> dtoConverter,
		DTOConverterRegistry dtoConverterRegistry,
		Map<String, Object> extendedProperties, JSONFactory jsonFactory,
		Class<?> modelClass, long userId) {

		Map<String, Object> modelAttributes = baseModel.getModelAttributes();

		ExpandoBridge expandoBridge = baseModel.getExpandoBridge();

		modelAttributes.putAll(expandoBridge.getAttributes());

		if (dtoConverter == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No DTO converter found for " + modelClass.getName());
			}

			return modelAttributes;
		}

		User user = UserLocalServiceUtil.fetchUser(userId);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No user found with user ID " + userId);
			}

			return modelAttributes;
		}

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), dtoConverterRegistry,
				baseModel.getPrimaryKeyObj(), user.getLocale(), null, user);

		try {
			Object object = dtoConverter.toDTO(
				defaultDTOConverterContext, baseModel);

			if (object == null) {
				return modelAttributes;
			}

			JSONObject jsonObject = jsonFactory.createJSONObject(
				jsonFactory.looseSerializeDeep(object));

			jsonObject.put(
				"createDate", modelAttributes.get("createDate")
			).put(
				"modifiedDate", modelAttributes.get("modifiedDate")
			).put(
				"status", modelAttributes.get("status")
			).put(
				"userName", user.getFullName()
			).put(
				"uuid", modelAttributes.get("uuid")
			);

			for (Map.Entry<String, Object> entry :
					extendedProperties.entrySet()) {

				jsonObject.put(entry.getKey(), entry.getValue());
			}

			return jsonObject.toMap();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return baseModel.getModelAttributes();
	}

	private static Map<String, Object> _toDTO(
		DTOConverterRegistry dtoConverterRegistry, JSONFactory jsonFactory,
		ObjectEntry objectEntry, User user) {

		DTOConverter<ObjectEntry, ?> dtoConverter =
			(DTOConverter<ObjectEntry, ?>)dtoConverterRegistry.getDTOConverter(
				ObjectEntry.class.getName());

		if (dtoConverter == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No DTO converter found for " +
						ObjectEntry.class.getName());
			}

			return objectEntry.getModelAttributes();
		}

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), dtoConverterRegistry, null,
				user.getLocale(), null, user);

		try {
			JSONObject jsonObject = jsonFactory.createJSONObject(
				jsonFactory.looseSerializeDeep(
					dtoConverter.toDTO(
						defaultDTOConverterContext, objectEntry)));

			return jsonObject.toMap();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return objectEntry.getModelAttributes();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryUtil.class);

}