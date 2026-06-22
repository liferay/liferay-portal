/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.converter;

import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "dto.class.name=com.liferay.object.model.ObjectRelationship",
	service = DTOConverter.class
)
public class ObjectRelationshipDTOConverter
	implements DTOConverter
		<com.liferay.object.model.ObjectRelationship, ObjectRelationship> {

	@Override
	public String getContentType() {
		return ObjectRelationship.class.getSimpleName();
	}

	@Override
	public ObjectRelationship toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.object.model.ObjectRelationship
				serviceBuilderObjectRelationship)
		throws Exception {

		if (serviceBuilderObjectRelationship == null) {
			return null;
		}

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.getObjectDefinition(
				serviceBuilderObjectRelationship.getObjectDefinitionId1());

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				serviceBuilderObjectRelationship.getObjectDefinitionId2());

		return new ObjectRelationship() {
			{
				setActions(dtoConverterContext::getActions);
				setDeletionType(
					() -> ObjectRelationship.DeletionType.create(
						serviceBuilderObjectRelationship.getDeletionType()));
				setEdge(serviceBuilderObjectRelationship::isEdge);
				setExternalReferenceCode(
					() ->
						serviceBuilderObjectRelationship.
							getExternalReferenceCode());
				setId(
					() ->
						serviceBuilderObjectRelationship.
							getObjectRelationshipId());
				setLabel(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectRelationship.getLabelMap()));
				setName(serviceBuilderObjectRelationship::getName);
				setObjectDefinitionExternalReferenceCode1(
					objectDefinition1::getExternalReferenceCode);
				setObjectDefinitionExternalReferenceCode2(
					objectDefinition2::getExternalReferenceCode);
				setObjectDefinitionId1(
					() ->
						serviceBuilderObjectRelationship.
							getObjectDefinitionId1());
				setObjectDefinitionId2(
					() ->
						serviceBuilderObjectRelationship.
							getObjectDefinitionId2());
				setObjectDefinitionModifiable2(objectDefinition2::isModifiable);
				setObjectDefinitionName2(objectDefinition2::getShortName);
				setObjectDefinitionScope2(objectDefinition2::getScope);
				setObjectDefinitionSystem2(objectDefinition2::isSystem);
				setObjectField(
					() -> {
						ObjectField objectField =
							_objectFieldLocalService.fetchObjectField(
								serviceBuilderObjectRelationship.
									getObjectFieldId2());

						if (objectField == null) {
							return null;
						}

						return _objectFieldDTOConverter.toDTO(
							new DefaultDTOConverterContext(
								false, null, null, null,
								dtoConverterContext.getLocale(), null, null),
							objectField);
					});
				setParameterObjectFieldId(
					() ->
						serviceBuilderObjectRelationship.
							getParameterObjectFieldId());
				setParameterObjectFieldName(
					() -> {
						if (Validator.isNull(
								serviceBuilderObjectRelationship.
									getParameterObjectFieldId())) {

							return StringPool.BLANK;
						}

						ObjectField objectField =
							_objectFieldLocalService.getObjectField(
								serviceBuilderObjectRelationship.
									getParameterObjectFieldId());

						return objectField.getName();
					});
				setReverse(serviceBuilderObjectRelationship::isReverse);
				setSystem(serviceBuilderObjectRelationship::isSystem);
				setType(
					() -> ObjectRelationship.Type.create(
						serviceBuilderObjectRelationship.getType()));
			}
		};
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_FIELD_DTO_CONVERTER)
	private DTOConverter
		<ObjectField, com.liferay.object.admin.rest.dto.v1_0.ObjectField>
			_objectFieldDTOConverter;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}