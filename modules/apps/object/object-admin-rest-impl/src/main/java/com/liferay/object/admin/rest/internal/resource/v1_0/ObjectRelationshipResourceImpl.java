/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectFieldUtil;
import com.liferay.object.admin.rest.internal.odata.entity.v1_0.ObjectRelationshipEntityModel;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFilterLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 * @author Marco Leo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-relationship.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ObjectRelationshipResource.class
)
public class ObjectRelationshipResourceImpl
	extends BaseObjectRelationshipResourceImpl {

	@Override
	public void deleteObjectRelationship(Long objectRelationshipId)
		throws Exception {

		_objectRelationshipService.deleteObjectRelationship(
			objectRelationshipId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ObjectRelationship>
			getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
				String externalReferenceCode, String search, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		com.liferay.object.model.ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getObjectDefinitionObjectRelationshipsPage(
			objectDefinition.getObjectDefinitionId(), search, filter,
			pagination, sorts);
	}

	@NestedField(
		parentClass = ObjectDefinition.class, value = "objectRelationships"
	)
	@Override
	public Page<ObjectRelationship> getObjectDefinitionObjectRelationshipsPage(
			Long objectDefinitionId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE,
					"postObjectDefinitionObjectRelationshipBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					objectDefinitionId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectRelationshipBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					null)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectRelationshipBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					null)
			).build(),
			booleanQuery -> {
			},
			filter, com.liferay.object.model.ObjectRelationship.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setAttribute(
					"objectDefinitionId", objectDefinitionId);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toObjectRelationship(
				_objectRelationshipService.getObjectRelationship(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectRelationship getObjectRelationship(Long objectRelationshipId)
		throws Exception {

		return _toObjectRelationship(
			_objectRelationshipService.getObjectRelationship(
				objectRelationshipId));
	}

	@Override
	public ObjectRelationship
			postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				String externalReferenceCode,
				ObjectRelationship objectRelationship)
		throws Exception {

		com.liferay.object.model.ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		com.liferay.object.model.ObjectDefinition objectDefinition2 =
			_getObjectDefinition2(objectRelationship);

		objectRelationship.setParameterObjectFieldId(
			() -> {
				if (Validator.isNull(
						objectRelationship.getParameterObjectFieldName())) {

					return 0L;
				}

				ObjectField objectField =
					_objectFieldLocalService.getObjectField(
						objectDefinition2.getObjectDefinitionId(),
						objectRelationship.getParameterObjectFieldName());

				return objectField.getObjectFieldId();
			});

		return _toObjectRelationship(
			_objectRelationshipService.addObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(),
				objectRelationship.getParameterObjectFieldId(),
				objectRelationship.getDeletionTypeAsString(),
				GetterUtil.getBoolean(objectRelationship.getEdge()),
				LocalizedMapUtil.populateLocalizedMap(
					objectRelationship.getLabel()),
				objectRelationship.getName(),
				GetterUtil.getBoolean(objectRelationship.getSystem()),
				objectRelationship.getTypeAsString(), null));
	}

	@Override
	public ObjectRelationship postObjectDefinitionObjectRelationship(
			Long objectDefinitionId, ObjectRelationship objectRelationship)
		throws Exception {

		long objectDefinitionId2 = GetterUtil.getLong(
			objectRelationship.getObjectDefinitionId2());

		if (objectRelationship.getObjectDefinitionExternalReferenceCode2() !=
				null) {

			com.liferay.object.model.ObjectDefinition objectDefinition2 =
				_getObjectDefinition2(objectRelationship);

			objectDefinitionId2 = objectDefinition2.getObjectDefinitionId();
		}

		return _toObjectRelationship(
			_objectRelationshipService.addObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectDefinitionId, objectDefinitionId2,
				GetterUtil.getLong(
					objectRelationship.getParameterObjectFieldId()),
				objectRelationship.getDeletionTypeAsString(),
				GetterUtil.getBoolean(objectRelationship.getEdge()),
				LocalizedMapUtil.populateLocalizedMap(
					objectRelationship.getLabel()),
				objectRelationship.getName(),
				GetterUtil.getBoolean(objectRelationship.getSystem()),
				objectRelationship.getTypeAsString(),
				ObjectFieldUtil.toObjectField(
					LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
					_listTypeDefinitionLocalService,
					objectRelationship.getObjectField(),
					_objectFieldLocalService, _objectFieldSettingLocalService,
					_objectFilterLocalService)));
	}

	@Override
	public ObjectRelationship putObjectRelationship(
			Long objectRelationshipId, ObjectRelationship objectRelationship)
		throws Exception {

		if (Validator.isNotNull(objectRelationship.getEdge()) &&
			!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-34594")) {

			throw new UnsupportedOperationException();
		}

		if (Validator.isNotNull(
				objectRelationship.getParameterObjectFieldName())) {

			objectRelationship.setParameterObjectFieldId(
				() -> {
					com.liferay.object.model.ObjectDefinition objectDefinition =
						_objectDefinitionLocalService.
							getObjectDefinitionByExternalReferenceCode(
								objectRelationship.
									getObjectDefinitionExternalReferenceCode2(),
								contextCompany.getCompanyId());

					ObjectField objectField =
						_objectFieldLocalService.getObjectField(
							objectDefinition.getObjectDefinitionId(),
							objectRelationship.getParameterObjectFieldName());

					return objectField.getObjectFieldId();
				});
		}

		return _toObjectRelationship(
			_objectRelationshipService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationshipId,
				GetterUtil.getLong(
					objectRelationship.getParameterObjectFieldId()),
				objectRelationship.getDeletionTypeAsString(),
				GetterUtil.getBoolean(objectRelationship.getEdge()),
				LocalizedMapUtil.populateLocalizedMap(
					objectRelationship.getLabel()),
				ObjectFieldUtil.toObjectField(
					LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
					_listTypeDefinitionLocalService,
					objectRelationship.getObjectField(),
					_objectFieldLocalService, _objectFieldSettingLocalService,
					_objectFilterLocalService)));
	}

	@Override
	public ObjectRelationship putObjectRelationshipByExternalReferenceCode(
			String externalReferenceCode, ObjectRelationship objectRelationship)
		throws Exception {

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition = null;

		if (Validator.isNotNull(
				objectRelationship.
					getObjectDefinitionExternalReferenceCode1())) {

			serviceBuilderObjectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						objectRelationship.
							getObjectDefinitionExternalReferenceCode1(),
						contextCompany.getCompanyId());
		}

		if (serviceBuilderObjectDefinition == null) {
			serviceBuilderObjectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					GetterUtil.getLong(
						objectRelationship.getObjectDefinitionId1()));
		}

		long objectDefinitionId =
			serviceBuilderObjectDefinition.getObjectDefinitionId();

		objectRelationship.setObjectDefinitionId1(() -> objectDefinitionId);

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship =
				_objectRelationshipService.
					fetchObjectRelationshipByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId(),
						objectRelationship.getObjectDefinitionId1());

		objectRelationship.setExternalReferenceCode(
			() -> externalReferenceCode);

		if (serviceBuilderObjectRelationship != null) {
			return putObjectRelationship(
				serviceBuilderObjectRelationship.getObjectRelationshipId(),
				objectRelationship);
		}

		return postObjectDefinitionObjectRelationship(
			objectRelationship.getObjectDefinitionId1(), objectRelationship);
	}

	private com.liferay.object.model.ObjectDefinition _getObjectDefinition2(
			ObjectRelationship objectRelationship)
		throws Exception {

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition2 =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						objectRelationship.
							getObjectDefinitionExternalReferenceCode2(),
						contextCompany.getCompanyId());

		if (serviceBuilderObjectDefinition2 != null) {
			return serviceBuilderObjectDefinition2;
		}

		ObjectFolder defaultObjectFolder =
			_objectFolderLocalService.getOrAddDefaultObjectFolder(
				contextCompany.getCompanyId());

		return _objectDefinitionLocalService.addObjectDefinition(
			objectRelationship.getObjectDefinitionExternalReferenceCode2(),
			contextUser.getUserId(), defaultObjectFolder.getObjectFolderId(),
			GetterUtil.get(
				objectRelationship.getObjectDefinitionModifiable2(), true),
			GetterUtil.get(
				objectRelationship.getObjectDefinitionScope2(),
				ObjectDefinitionConstants.SCOPE_COMPANY),
			GetterUtil.get(
				objectRelationship.getObjectDefinitionSystem2(), false));
	}

	private ObjectRelationship _toObjectRelationship(
			com.liferay.object.model.ObjectRelationship objectRelationship)
		throws Exception {

		return _objectRelationshipDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false,
				HashMapBuilder.put(
					"delete",
					() -> {
						if (objectRelationship.isSystem()) {
							return null;
						}

						return addAction(
							ActionKeys.DELETE, "deleteObjectRelationship",
							com.liferay.object.model.ObjectDefinition.class.
								getName(),
							objectRelationship.getObjectDefinitionId1());
					}
				).build(),
				null, null, contextAcceptLanguage.getPreferredLocale(), null,
				null),
			objectRelationship);
	}

	private static final EntityModel _entityModel =
		new ObjectRelationshipEntityModel();

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFilterLocalService _objectFilterLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_RELATIONSHIP_DTO_CONVERTER)
	private DTOConverter
		<com.liferay.object.model.ObjectRelationship, ObjectRelationship>
			_objectRelationshipDTOConverter;

	@Reference
	private ObjectRelationshipService _objectRelationshipService;

}