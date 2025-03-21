/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.exception.NoSuchObjectRelationshipException;
import com.liferay.object.exception.ObjectFieldSettingNameException;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFilter;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION,
	service = ObjectFieldBusinessType.class
)
public class AggregationObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray(
			"filters", ObjectFieldSettingConstants.NAME_FUNCTION,
			ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME,
			ObjectFieldSettingConstants.NAME_OBJECT_RELATIONSHIP_NAME);
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_STRING;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.TEXT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "summarize-data-values");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "aggregation");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"readOnly", true
		).putAll(
			super.getProperties(objectField, objectFieldRenderingContext)
		).build();
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_FUNCTION,
			ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME,
			ObjectFieldSettingConstants.NAME_OBJECT_RELATIONSHIP_NAME);
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
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

		Map<String, Object> objectFieldSettingsValuesMap = new HashMap<>();

		for (ObjectFieldSetting objectFieldSetting : objectFieldSettings) {
			String name = objectFieldSetting.getName();

			if (Objects.equals(name, "filters")) {
				objectFieldSettingsValuesMap.put(
					name, objectFieldSetting.getObjectFilters());

				continue;
			}

			objectFieldSettingsValuesMap.put(
				name, objectFieldSetting.getValue());
		}

		String function = GetterUtil.getString(
			objectFieldSettingsValuesMap.get(
				ObjectFieldSettingConstants.NAME_FUNCTION));

		Set<String> requiredObjectFieldSettingsNames =
			getRequiredObjectFieldSettingsNames(objectField);

		if (!ArrayUtil.contains(_FUNCTION, function)) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_FUNCTION, function);
		}
		else if (Objects.equals(
					function, ObjectFieldSettingConstants.VALUE_COUNT)) {

			requiredObjectFieldSettingsNames.remove(
				ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME);
		}

		Set<String> missingRequiredObjectFieldSettingsNames = new HashSet<>();

		for (String requiredObjectFieldSettingName :
				requiredObjectFieldSettingsNames) {

			if (Validator.isNull(
					objectFieldSettingsValuesMap.get(
						requiredObjectFieldSettingName))) {

				missingRequiredObjectFieldSettingsNames.add(
					requiredObjectFieldSettingName);
			}
		}

		if (!missingRequiredObjectFieldSettingsNames.isEmpty()) {
			throw new ObjectFieldSettingValueException.MissingRequiredValues(
				objectField.getName(), missingRequiredObjectFieldSettingsNames);
		}

		Set<String> notAllowedObjectFieldSettingsNames = new HashSet<>(
			objectFieldSettingsValuesMap.keySet());

		notAllowedObjectFieldSettingsNames.removeAll(
			getAllowedObjectFieldSettingsNames());
		notAllowedObjectFieldSettingsNames.removeAll(
			requiredObjectFieldSettingsNames);

		if (!notAllowedObjectFieldSettingsNames.isEmpty()) {
			throw new ObjectFieldSettingNameException.NotAllowedNames(
				objectField.getName(), notAllowedObjectFieldSettingsNames);
		}

		try {
			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.getObjectRelationship(
					objectField.getObjectDefinitionId(),
					GetterUtil.getString(
						objectFieldSettingsValuesMap.get(
							ObjectFieldSettingConstants.
								NAME_OBJECT_RELATIONSHIP_NAME)));

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId2());

			if (!Objects.equals(
					function, ObjectFieldSettingConstants.VALUE_COUNT)) {

				ObjectField objectField1 =
					_objectFieldLocalService.fetchObjectField(
						objectDefinition.getObjectDefinitionId(),
						GetterUtil.getString(
							objectFieldSettingsValuesMap.get(
								ObjectFieldSettingConstants.
									NAME_OBJECT_FIELD_NAME)));

				if ((objectField1 != null) &&
					!ArrayUtil.contains(
						_NUMERIC_BUSINESS_TYPES,
						objectField1.getBusinessType())) {

					throw new ObjectFieldSettingValueException.InvalidValue(
						objectField.getName(),
						ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME,
						GetterUtil.getString(
							objectFieldSettingsValuesMap.get(
								ObjectFieldSettingConstants.
									NAME_OBJECT_FIELD_NAME)));
				}
			}

			_validateObjectFilters(
				objectDefinition, objectField.getName(),
				(List<ObjectFilter>)objectFieldSettingsValuesMap.get(
					"filters"));
		}
		catch (NoSuchObjectFieldException noSuchObjectFieldException) {
			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME,
				GetterUtil.getString(
					objectFieldSettingsValuesMap.get(
						ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME)),
				noSuchObjectFieldException);
		}
		catch (NoSuchObjectRelationshipException
					noSuchObjectRelationshipException) {

			throw new ObjectFieldSettingValueException.InvalidValue(
				objectField.getName(),
				ObjectFieldSettingConstants.NAME_OBJECT_RELATIONSHIP_NAME,
				GetterUtil.getString(
					objectFieldSettingsValuesMap.get(
						ObjectFieldSettingConstants.
							NAME_OBJECT_RELATIONSHIP_NAME)),
				noSuchObjectRelationshipException);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private void _validateObjectFilters(
			ObjectDefinition objectDefinition, String objectFieldName,
			List<ObjectFilter> objectFilters)
		throws PortalException {

		if (ListUtil.isEmpty(objectFilters)) {
			return;
		}

		for (ObjectFilter objectFilter : objectFilters) {
			Set<String> missingObjectFilterValues = new HashSet<>();

			_validateObjectFilterValue(
				missingObjectFilterValues, "filterBy",
				objectFilter.getFilterBy());
			_validateObjectFilterValue(
				missingObjectFilterValues, "filterType",
				objectFilter.getFilterType());
			_validateObjectFilterValue(
				missingObjectFilterValues, "json", objectFilter.getJSON());

			if (!missingObjectFilterValues.isEmpty()) {
				throw new ObjectFieldSettingValueException.
					MissingRequiredValues(
						objectFieldName, missingObjectFilterValues);
			}

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(),
				GetterUtil.getString(objectFilter.getFilterBy()));

			if ((objectField != null) &&
				(objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION) ||
				 objectField.compareBusinessType(
					 ObjectFieldConstants.BUSINESS_TYPE_FORMULA))) {

				throw new ObjectFieldSettingValueException.InvalidValue(
					objectFieldName, "filterBy",
					GetterUtil.getString(objectFilter.getFilterBy()));
			}
		}
	}

	private void _validateObjectFilterValue(
		Set<String> missingObjectFilterValues, String objectFilterName,
		String objectFilterValue) {

		if (Validator.isNull(objectFilterValue)) {
			missingObjectFilterValues.add(objectFilterName);
		}
	}

	private static final String[] _FUNCTION = {
		ObjectFieldSettingConstants.VALUE_AVERAGE,
		ObjectFieldSettingConstants.VALUE_COUNT,
		ObjectFieldSettingConstants.VALUE_MAX,
		ObjectFieldSettingConstants.VALUE_MIN,
		ObjectFieldSettingConstants.VALUE_SUM
	};

	private static final String[] _NUMERIC_BUSINESS_TYPES = {
		ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
		ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
		ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
		ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL
	};

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}