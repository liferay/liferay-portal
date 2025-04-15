/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectFieldSettingUtil;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectFieldUtil;
import com.liferay.object.admin.rest.internal.odata.entity.v1_0.ObjectFieldEntityModel;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFieldResource;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFilterLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-field.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ObjectFieldResource.class
)
public class ObjectFieldResourceImpl extends BaseObjectFieldResourceImpl {

	@Override
	public void deleteObjectField(Long objectFieldId) throws Exception {
		_objectFieldService.deleteObjectField(objectFieldId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ObjectField>
			getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
				String externalReferenceCode, String search, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		com.liferay.object.model.ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return _getObjectFieldsPage(
			objectDefinition, search, filter, pagination, sorts);
	}

	@NestedField(parentClass = ObjectDefinition.class, value = "objectFields")
	@Override
	public Page<ObjectField> getObjectDefinitionObjectFieldsPage(
			@NestedFieldId(value = "id") Long objectDefinitionId, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getObjectFieldsPage(
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinitionId),
			search, filter, pagination, sorts);
	}

	@Override
	public ObjectField getObjectField(Long objectFieldId) throws Exception {
		return _toObjectField(
			_objectFieldService.getObjectField(objectFieldId));
	}

	@Override
	public ObjectField postObjectDefinitionByExternalReferenceCodeObjectField(
			String externalReferenceCode, ObjectField objectField)
		throws Exception {

		com.liferay.object.model.ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return postObjectDefinitionObjectField(
			objectDefinition.getObjectDefinitionId(), objectField);
	}

	@Override
	public ObjectField postObjectDefinitionObjectField(
			Long objectDefinitionId, ObjectField objectField)
		throws Exception {

		com.liferay.object.model.ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinitionId);

		com.liferay.object.model.ObjectField serviceBuilderObjectField =
			_objectFieldService.addCustomObjectField(
				objectField.getExternalReferenceCode(),
				ObjectFieldUtil.getListTypeDefinitionId(
					contextUser.getCompanyId(), _listTypeDefinitionLocalService,
					objectField),
				objectDefinitionId, objectField.getBusinessTypeAsString(),
				ObjectFieldUtil.getDBType(
					objectField.getDBTypeAsString(),
					objectField.getTypeAsString()),
				GetterUtil.getBoolean(objectField.getIndexed()),
				GetterUtil.getBoolean(objectField.getIndexedAsKeyword()),
				objectField.getIndexedLanguageId(),
				LocalizedMapUtil.populateLocalizedMap(objectField.getLabel()),
				GetterUtil.getBoolean(objectField.getLocalized()),
				objectField.getName(), objectField.getReadOnlyAsString(),
				objectField.getReadOnlyConditionExpression(),
				objectField.getRequired(),
				GetterUtil.getBoolean(objectField.getState()),
				ObjectFieldSettingUtil.toObjectFieldSettings(
					ObjectFieldUtil.addListTypeDefinition(
						contextUser.getCompanyId(),
						_listTypeDefinitionLocalService,
						_listTypeEntryLocalService, objectField,
						contextUser.getUserId()),
					objectField, _objectFieldSettingLocalService,
					_objectFilterLocalService));

		if (objectDefinition.isApproved() && objectDefinition.isActive()) {
			_objectDefinitionLocalService.deployObjectDefinition(
				objectDefinition);
		}

		return _toObjectField(serviceBuilderObjectField);
	}

	@Override
	public ObjectField putObjectField(
			Long objectFieldId, ObjectField objectField)
		throws Exception {

		Long listTypeDefinitionId = objectField.getListTypeDefinitionId();

		objectField.setListTypeDefinitionId(
			() -> _getListTypeDefinitionId(
				objectField, objectFieldId, listTypeDefinitionId));

		return _toObjectField(
			_objectFieldService.updateObjectField(
				objectField.getExternalReferenceCode(), objectFieldId,
				objectField.getListTypeDefinitionId(),
				objectField.getBusinessTypeAsString(),
				ObjectFieldUtil.getDBType(
					objectField.getDBTypeAsString(),
					objectField.getTypeAsString()),
				GetterUtil.getBoolean(objectField.getIndexed()),
				GetterUtil.getBoolean(objectField.getIndexedAsKeyword()),
				objectField.getIndexedLanguageId(),
				LocalizedMapUtil.populateLocalizedMap(objectField.getLabel()),
				GetterUtil.getBoolean(objectField.getLocalized()),
				objectField.getName(), objectField.getReadOnlyAsString(),
				objectField.getReadOnlyConditionExpression(),
				objectField.getRequired(),
				GetterUtil.getBoolean(objectField.getState()),
				ObjectFieldSettingUtil.toObjectFieldSettings(
					objectField.getListTypeDefinitionId(), objectField,
					_objectFieldSettingLocalService,
					_objectFilterLocalService)));
	}

	@Override
	protected void preparePatch(
		ObjectField objectField, ObjectField existingObjectField) {

		if (objectField.getObjectFieldSettings() != null) {
			existingObjectField.setObjectFieldSettings(
				objectField::getObjectFieldSettings);
		}
	}

	private Long _getListTypeDefinitionId(
			ObjectField objectField, long objectFieldId,
			Long listTypeDefinitionId)
		throws Exception {

		com.liferay.object.model.ObjectField serviceBuilderObjectField =
			_objectFieldService.getObjectField(objectFieldId);

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					serviceBuilderObjectField.getObjectDefinitionId());

		if (!serviceBuilderObjectDefinition.isApproved()) {
			listTypeDefinitionId = ObjectFieldUtil.addListTypeDefinition(
				contextUser.getCompanyId(), _listTypeDefinitionLocalService,
				_listTypeEntryLocalService, objectField,
				contextUser.getUserId());
		}

		if (Validator.isNull(listTypeDefinitionId)) {
			listTypeDefinitionId =
				serviceBuilderObjectField.getListTypeDefinitionId();
		}

		return listTypeDefinitionId;
	}

	private Page<ObjectField> _getObjectFieldsPage(
			com.liferay.object.model.ObjectDefinition objectDefinition,
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectField",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					objectDefinition.getObjectDefinitionId())
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectFieldBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					objectDefinition.getObjectDefinitionId())
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectFieldBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectDefinitionObjectFieldsPage",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					objectDefinition.getObjectDefinitionId())
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectFieldBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					null)
			).build(),
			booleanQuery -> {
			},
			filter, com.liferay.object.model.ObjectField.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setAttribute("label", search);
				searchContext.setAttribute(
					"objectDefinitionId",
					objectDefinition.getObjectDefinitionId());
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toObjectField(
				objectDefinition,
				_objectFieldService.getObjectField(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private ObjectField _toObjectField(
			com.liferay.object.model.ObjectDefinition objectDefinition,
			com.liferay.object.model.ObjectField objectField)
		throws Exception {

		return _objectFieldDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false,
				HashMapBuilder.put(
					"delete",
					() -> {
						if (!objectField.isDeletionAllowed() ||
							objectField.isSystem()) {

							return null;
						}

						return addAction(
							ActionKeys.UPDATE, "deleteObjectField",
							com.liferay.object.model.ObjectDefinition.class.
								getName(),
							objectField.getObjectDefinitionId());
					}
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, "getObjectField",
						com.liferay.object.model.ObjectDefinition.class.
							getName(),
						objectField.getObjectDefinitionId())
				).put(
					"update",
					() -> {
						if ((objectDefinition.isApproved() ||
							 objectDefinition.isUnmodifiableSystemObject()) &&
							!Objects.equals(
								objectDefinition.getExtensionDBTableName(),
								objectField.getDBTableName())) {

							return null;
						}

						return addAction(
							ActionKeys.UPDATE, "putObjectField",
							com.liferay.object.model.ObjectDefinition.class.
								getName(),
							objectField.getObjectDefinitionId());
					}
				).build(),
				null, null, contextAcceptLanguage.getPreferredLocale(), null,
				null),
			objectField);
	}

	private ObjectField _toObjectField(
			com.liferay.object.model.ObjectField objectField)
		throws Exception {

		return _toObjectField(
			_objectDefinitionLocalService.getObjectDefinition(
				objectField.getObjectDefinitionId()),
			objectField);
	}

	private static final EntityModel _entityModel =
		new ObjectFieldEntityModel();

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_FIELD_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectField, ObjectField>
		_objectFieldDTOConverter;

	@Reference
	private ObjectFieldService _objectFieldService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFilterLocalService _objectFilterLocalService;

}