/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
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
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP,
	service = ObjectFieldBusinessType.class
)
public class RelationshipObjectFieldBusinessType
	implements ObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_LONG;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.OBJECT_RELATIONSHIP;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "relationship");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.LONG;
	}

	@Override
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return SetUtil.fromArray(
			ObjectFieldSettingConstants.NAME_OBJECT_DEFINITION_1_SHORT_NAME,
			ObjectFieldSettingConstants.
				NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME);
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		String relationshipName = StringUtil.split(
			objectField.getName(), CharPool.UNDERLINE
		).get(
			1
		);

		Map.Entry<String, Object> relatedElementEntry = MapUtil.getEntry(
			values, relationshipName);

		if (Objects.equals(
				objectField.getRelationshipType(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY) &&
			(relatedElementEntry != null)) {

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectDefinitionId(
						objectField.getObjectDefinitionId(), relationshipName);

			Object relatedElement = relatedElementEntry.getValue();

			if ((objectRelationship == null) ||
				!(relatedElement instanceof Map)) {

				return 0;
			}

			String externalReferenceCode = MapUtil.getString(
				(Map<String, Object>)relatedElement, "externalReferenceCode");

			if (Validator.isNull(externalReferenceCode)) {
				return 0;
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId1());

			try {
				if (objectDefinition.isUnmodifiableSystemObject()) {
					return _getPrimaryKeyObj(
						externalReferenceCode, objectDefinition, 0L, userId);
				}

				ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
					externalReferenceCode, groupId,
					objectDefinition.getObjectDefinitionId());

				if (!Objects.equals(
						objectDefinition.getObjectDefinitionId(),
						objectEntry.getObjectDefinitionId())) {

					throw new ObjectEntryValuesException.InvalidValue(
						objectField.getName());
				}

				return objectEntry.getObjectEntryId();
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			return 0;
		}

		PortalException portalException1 = null;

		Map.Entry<String, Object> valueEntry = MapUtil.getEntry(
			values, objectField.getName());

		if (valueEntry != null) {
			Object value = valueEntry.getValue();

			if (value == null) {
				return 0;
			}

			long valueLong = GetterUtil.getLong(value);

			if (valueLong == 0) {
				return value;
			}

			ObjectDefinition objectDefinition = _getObjectDefinition(
				objectField);

			try {
				if (objectDefinition.isUnmodifiableSystemObject()) {
					return _getPrimaryKeyObj(
						null, objectDefinition, valueLong, userId);
				}

				ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
					valueLong);

				if (!Objects.equals(
						objectDefinition.getObjectDefinitionId(),
						objectEntry.getObjectDefinitionId())) {

					throw new ObjectEntryValuesException.InvalidValue(
						objectField.getName());
				}

				return objectEntry.getObjectEntryId();
			}
			catch (PortalException portalException2) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException2);
				}

				portalException1 = portalException2;

				if (portalException1 instanceof NoSuchModelException) {
					portalException1 =
						new ObjectEntryValuesException.NoSuchRelatedObjectEntry(
							objectField.getName());
				}
			}
		}

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectField);

		Map.Entry<String, Object> externalReferenceCodeEntry = MapUtil.getEntry(
			values, objectRelationshipERCObjectFieldName);

		if (externalReferenceCodeEntry != null) {
			String externalReferenceCode = GetterUtil.getString(
				externalReferenceCodeEntry.getValue());

			if (Validator.isNull(externalReferenceCode)) {
				return 0;
			}

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			ObjectDefinition objectDefinition1 =
				_objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId1());

			if (objectDefinition1.isUnmodifiableSystemObject()) {
				return _getPrimaryKeyObj(
					externalReferenceCode, objectDefinition1, 0L, userId);
			}

			long objectDefinition1GroupId = 0;

			ObjectDefinition objectDefinition2 =
				_objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId2());
			ObjectScopeProvider objectScopeProvider =
				_objectScopeProviderRegistry.getObjectScopeProvider(
					objectDefinition1.getScope());

			if (Objects.equals(
					objectDefinition1.getScope(),
					objectDefinition2.getScope()) &&
				objectScopeProvider.isGroupAware()) {

				objectDefinition1GroupId = GetterUtil.getLong(groupId);
			}

			ObjectEntry objectEntry =
				_objectEntryService.getOrAddEmptyObjectEntry(
					externalReferenceCode, objectDefinition1GroupId,
					objectDefinition1.getObjectDefinitionId());

			return objectEntry.getObjectEntryId();
		}

		if (portalException1 != null) {
			throw portalException1;
		}

		return null;
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
	}

	private ObjectDefinition _getObjectDefinition(ObjectField objectField)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectFieldId2(
					objectField.getObjectFieldId());

		return _objectDefinitionLocalService.getObjectDefinition(
			objectRelationship.getObjectDefinitionId1());
	}

	private Object _getPrimaryKeyObj(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			Long primaryKey, long userId)
		throws PortalException {

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		if (externalReferenceCode == null) {
			externalReferenceCode =
				systemObjectDefinitionManager.getBaseModelExternalReferenceCode(
					primaryKey);
		}

		BaseModel<?> baseModel =
			systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				externalReferenceCode, _userLocalService.getUserById(userId));

		Map<String, Object> modelAttributes = baseModel.getModelAttributes();

		Column<?, Long> primaryKeyColumn =
			systemObjectDefinitionManager.getPrimaryKeyColumn();

		return modelAttributes.get(primaryKeyColumn.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RelationshipObjectFieldBusinessType.class);

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}