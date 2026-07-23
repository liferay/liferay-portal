/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.converter;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFieldSetting;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectFieldSettingUtil;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectStateFlowUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = "dto.class.name=com.liferay.object.model.ObjectField",
	service = DTOConverter.class
)
public class ObjectFieldDTOConverter
	implements DTOConverter<com.liferay.object.model.ObjectField, ObjectField> {

	@Override
	public String getContentType() {
		return ObjectField.class.getSimpleName();
	}

	@Override
	public ObjectField toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.object.model.ObjectField objectField)
		throws Exception {

		if (objectField == null) {
			return null;
		}

		ObjectRelationship objectRelationship = null;

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());
		}

		ObjectRelationship finalObjectRelationship = objectRelationship;

		return new ObjectField() {
			{
				setActions(dtoConverterContext::getActions);
				setBusinessType(
					() -> ObjectField.BusinessType.create(
						objectField.getBusinessType()));
				setDBType(
					() -> ObjectField.DBType.create(objectField.getDBType()));
				setDefaultValue(
					() -> String.valueOf(
						com.liferay.object.field.setting.util.
							ObjectFieldSettingUtil.getDefaultValue(
								null, objectField, null)));
				setExternalReferenceCode(objectField::getExternalReferenceCode);
				setId(objectField::getObjectFieldId);
				setIndexed(objectField::isIndexed);
				setIndexedAsKeyword(objectField::isIndexedAsKeyword);
				setIndexedLanguageId(objectField::getIndexedLanguageId);
				setLabel(
					() -> LocalizedMapUtil.getLanguageIdMap(
						objectField.getLabelMap()));
				setListTypeDefinitionExternalReferenceCode(
					() -> {
						if (objectField.getListTypeDefinitionId() == 0) {
							return null;
						}

						ListTypeDefinition listTypeDefinition =
							_listTypeDefinitionLocalService.
								fetchListTypeDefinition(
									objectField.getListTypeDefinitionId());

						return listTypeDefinition.getExternalReferenceCode();
					});
				setListTypeDefinitionId(objectField::getListTypeDefinitionId);
				setLocalized(objectField::isLocalized);
				setName(objectField::getName);
				setObjectDefinitionExternalReferenceCode1(
					() -> {
						if (finalObjectRelationship == null) {
							return null;
						}

						ObjectDefinition objectDefinition =
							_objectDefinitionLocalService.fetchObjectDefinition(
								finalObjectRelationship.
									getObjectDefinitionId1());

						return objectDefinition.getExternalReferenceCode();
					});
				setObjectFieldSettings(
					() -> TransformUtil.transformToArray(
						objectField.getObjectFieldSettings(),
						objectFieldSetting -> _toObjectFieldSetting(
							objectFieldSetting),
						ObjectFieldSetting.class));
				setObjectRelationshipExternalReferenceCode(
					() -> {
						if (finalObjectRelationship == null) {
							return null;
						}

						return finalObjectRelationship.
							getExternalReferenceCode();
					});
				setReadOnly(
					() -> ObjectField.ReadOnly.create(
						objectField.getReadOnly()));
				setReadOnlyConditionExpression(
					objectField::getReadOnlyConditionExpression);
				setRelationshipType(
					() -> ObjectField.RelationshipType.create(
						objectField.getRelationshipType()));
				setRequired(objectField::isRequired);
				setState(objectField::isState);
				setSystem(objectField::isSystem);
				setType(() -> ObjectField.Type.create(objectField.getDBType()));
				setUnique(
					() ->
						com.liferay.object.field.setting.util.
							ObjectFieldSettingUtil.isUnique(
								objectField.getObjectFieldSettings()));
			}
		};
	}

	private ObjectFieldSetting _toObjectFieldSetting(
		com.liferay.object.model.ObjectFieldSetting
			serviceBuilderObjectFieldSetting) {

		if (serviceBuilderObjectFieldSetting == null) {
			return null;
		}

		return new ObjectFieldSetting() {
			{
				setName(serviceBuilderObjectFieldSetting::getName);
				setValue(
					() -> {
						if (serviceBuilderObjectFieldSetting.compareName(
								ObjectFieldSettingConstants.NAME_STATE_FLOW)) {

							return ObjectStateFlowUtil.toObjectStateFlow(
								_objectStateFlowLocalService.
									fetchObjectStateFlow(
										GetterUtil.getLong(
											serviceBuilderObjectFieldSetting.
												getValue())));
						}

						if (serviceBuilderObjectFieldSetting.compareName(
								ObjectFieldSettingConstants.
									NAME_STORAGE_DEPOT_GROUP)) {

							Group group = _groupLocalService.fetchGroup(
								GetterUtil.getLong(
									serviceBuilderObjectFieldSetting.
										getValue()));

							if (group != null) {
								return group.getExternalReferenceCode();
							}

							return null;
						}

						return ObjectFieldSettingUtil.getValue(
							serviceBuilderObjectFieldSetting);
					});
			}
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

}