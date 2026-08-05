/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.vulcan.extension.v1_0;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.internal.util.ObjectEntryValuesUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.extension.ExtensionProvider;
import com.liferay.portal.vulcan.extension.PropertyDefinition;
import com.liferay.portal.vulcan.extension.validation.DefaultPropertyValidator;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 * @author Javier de Arcos
 */
@Component(service = ExtensionProvider.class)
public class ObjectEntryExtensionProvider extends BaseObjectExtensionProvider {

	@Override
	public Map<String, Serializable> getExtendedProperties(
		long companyId, long userId, String className, Object entity) {

		try {
			ObjectDefinition objectDefinition = fetchObjectDefinition(
				companyId, className);

			Map<String, Serializable> extendedProperties =
				_objectEntryLocalService.
					getExtensionDynamicObjectDefinitionTableValues(
						objectDefinition,
						getPrimaryKey(entity, objectDefinition));

			List<ObjectField> objectFields =
				_objectFieldLocalService.getObjectFieldsByBusinessType(
					objectDefinition.getObjectDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST);

			if (objectFields.isEmpty()) {
				return extendedProperties;
			}

			Locale locale = LocaleUtil.fromLanguageId(
				objectDefinition.getDefaultLanguageId(), true, false);

			User user = _userLocalService.fetchUser(userId);

			if (user != null) {
				locale = user.getLocale();
			}

			DTOConverterContext dtoConverterContext =
				new DefaultDTOConverterContext(null, null, locale, null, null);

			ObjectFieldBusinessType objectFieldBusinessType =
				_objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST);

			for (ObjectField objectField : objectFields) {
				Serializable value = extendedProperties.get(
					objectField.getName());

				if (value instanceof String) {
					extendedProperties.put(
						objectField.getName(),
						objectFieldBusinessType.getDTOValue(
							dtoConverterContext, objectDefinition, null,
							objectField, value));
				}

				if (!objectField.isLocalized()) {
					continue;
				}

				Serializable i18nValues = extendedProperties.get(
					objectField.getI18nObjectFieldName());

				if (!(i18nValues instanceof Map)) {
					continue;
				}

				Map<String, Serializable> i18nValuesMap =
					(Map<String, Serializable>)i18nValues;

				for (Map.Entry<String, Serializable> entry :
						i18nValuesMap.entrySet()) {

					if (entry.getValue() instanceof String) {
						entry.setValue(
							objectFieldBusinessType.getDTOValue(
								dtoConverterContext, objectDefinition, null,
								objectField, entry.getValue()));
					}
				}
			}

			return extendedProperties;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return Collections.emptyMap();
		}
	}

	@Override
	public Map<String, PropertyDefinition> getExtendedPropertyDefinitions(
		long companyId, String className) {

		Map<String, PropertyDefinition> extendedPropertyDefinitions =
			new HashMap<>();

		ObjectDefinition objectDefinition = fetchObjectDefinition(
			companyId, className);

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId(), false)) {

			ObjectFieldBusinessType objectFieldBusinessType =
				_objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
					objectField.getBusinessType());

			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

				extendedPropertyDefinitions.put(
					objectField.getName(),
					new PropertyDefinition(
						SetUtil.fromArray(ListEntry.class, String.class), null,
						ListEntry.class.getSimpleName(), null,
						objectField.getName(),
						objectFieldBusinessType.getPropertyType(),
						new DefaultPropertyValidator(),
						objectField.isRequired()));
			}
			else {
				extendedPropertyDefinitions.put(
					objectField.getName(),
					new PropertyDefinition(
						null, objectField.getName(),
						objectFieldBusinessType.getPropertyType(),
						objectField.isRequired()));
			}

			if (Objects.equals(
					objectField.getRelationshipType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				String objectRelationshipERCObjectFieldName =
					ObjectFieldSettingUtil.getValue(
						ObjectFieldSettingConstants.
							NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
						objectField);

				extendedPropertyDefinitions.put(
					objectRelationshipERCObjectFieldName,
					new PropertyDefinition(
						null, objectRelationshipERCObjectFieldName,
						PropertyDefinition.PropertyType.TEXT,
						objectField.isRequired()));
			}

			if (!objectField.isLocalized()) {
				continue;
			}

			extendedPropertyDefinitions.put(
				objectField.getI18nObjectFieldName(),
				new PropertyDefinition(
					Collections.singleton(Map.class), null, Map.class.getName(),
					null, objectField.getI18nObjectFieldName(),
					PropertyDefinition.PropertyType.SINGLE_ELEMENT,
					new DefaultPropertyValidator(), objectField.isRequired()));
		}

		return extendedPropertyDefinitions;
	}

	@Override
	public void setExtendedProperties(
		long companyId, long userId, String className, Object entity,
		Map<String, Serializable> extendedProperties) {

		try {
			ObjectDefinition objectDefinition = fetchObjectDefinition(
				companyId, className);

			for (ObjectField objectField :
					_objectFieldLocalService.getObjectFields(
						objectDefinition.getObjectDefinitionId(), false)) {

				Object value = ObjectEntryValuesUtil.getValue(
					null, objectDefinitionLocalService,
					_objectEntryLocalService, objectField,
					_objectFieldBusinessTypeRegistry, userId,
					new HashMap<>(extendedProperties));

				if (value == null) {
					continue;
				}

				extendedProperties.put(
					objectField.getName(), (Serializable)value);
			}

			_objectEntryLocalService.
				addOrUpdateExtensionDynamicObjectDefinitionTableValues(
					userId, objectDefinition,
					getPrimaryKey(entity, objectDefinition), extendedProperties,
					new ServiceContext() {
						{
							setCompanyId(companyId);
							setUserId(userId);
						}
					});
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryExtensionProvider.class);

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private UserLocalService _userLocalService;

}