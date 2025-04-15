/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.object.admin.rest.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayoutBox;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayoutColumn;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayoutRow;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayoutTab;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectLayoutUtil;
import com.liferay.object.admin.rest.internal.odata.entity.v1_0.ObjectLayoutEntityModel;
import com.liferay.object.admin.rest.resource.v1_0.ObjectLayoutResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.persistence.ObjectLayoutBoxPersistence;
import com.liferay.object.service.persistence.ObjectLayoutColumnPersistence;
import com.liferay.object.service.persistence.ObjectLayoutRowPersistence;
import com.liferay.object.service.persistence.ObjectLayoutTabPersistence;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
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
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-layout.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ObjectLayoutResource.class
)
public class ObjectLayoutResourceImpl extends BaseObjectLayoutResourceImpl {

	@Override
	public void deleteObjectLayout(Long objectLayoutId) throws Exception {
		_objectLayoutService.deleteObjectLayout(objectLayoutId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ObjectLayout>
			getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
				String externalReferenceCode, String search,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getObjectDefinitionObjectLayoutsPage(
			objectDefinition.getObjectDefinitionId(), search, pagination,
			sorts);
	}

	@NestedField(
		parentClass = com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition.class,
		value = "objectLayouts"
	)
	@Override
	public Page<ObjectLayout> getObjectDefinitionObjectLayoutsPage(
			Long objectDefinitionId, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectLayout",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectLayoutBatch",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectLayoutBatch",
					ObjectDefinition.class.getName(), null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectDefinitionObjectLayoutsPage",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectLayoutBatch",
					ObjectDefinition.class.getName(), null)
			).build(),
			booleanQuery -> {
			},
			null, com.liferay.object.model.ObjectLayout.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setAttribute(
					"objectDefinitionId", objectDefinitionId);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toObjectLayout(
				_objectLayoutService.getObjectLayout(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectLayout getObjectLayout(Long objectLayoutId) throws Exception {
		return _toObjectLayout(
			_objectLayoutService.getObjectLayout(objectLayoutId));
	}

	@Override
	public ObjectLayout postObjectDefinitionByExternalReferenceCodeObjectLayout(
			String externalReferenceCode, ObjectLayout objectLayout)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return postObjectDefinitionObjectLayout(
			objectDefinition.getObjectDefinitionId(), objectLayout);
	}

	@Override
	public ObjectLayout postObjectDefinitionObjectLayout(
			Long objectDefinitionId, ObjectLayout objectLayout)
		throws Exception {

		return _toObjectLayout(
			_objectLayoutService.addObjectLayout(
				objectDefinitionId,
				GetterUtil.getBoolean(objectLayout.getDefaultObjectLayout()),
				LocalizedMapUtil.populateLocalizedMap(objectLayout.getName()),
				transformToList(
					objectLayout.getObjectLayoutTabs(),
					objectLayoutTab -> _toObjectLayoutTab(
						objectDefinitionId, objectLayoutTab))));
	}

	@Override
	public ObjectLayout putObjectLayout(
			Long objectLayoutId, ObjectLayout objectLayout)
		throws Exception {

		if (Validator.isNotNull(
				objectLayout.getObjectDefinitionExternalReferenceCode())) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						objectLayout.getObjectDefinitionExternalReferenceCode(),
						contextCompany.getCompanyId());

			objectLayout.setObjectDefinitionId(
				objectDefinition::getObjectDefinitionId);
		}

		return _toObjectLayout(
			_objectLayoutService.updateObjectLayout(
				objectLayoutId, objectLayout.getDefaultObjectLayout(),
				LocalizedMapUtil.populateLocalizedMap(objectLayout.getName()),
				transformToList(
					objectLayout.getObjectLayoutTabs(),
					objectLayoutTab -> _toObjectLayoutTab(
						objectLayout.getObjectDefinitionId(),
						objectLayoutTab))));
	}

	private ObjectLayout _toObjectLayout(
			com.liferay.object.model.ObjectLayout serviceBuilderObjectLayout)
		throws PortalException {

		return ObjectLayoutUtil.toObjectLayout(
			HashMapBuilder.put(
				"delete",
				addAction(
					ActionKeys.DELETE, "deleteObjectLayout",
					ObjectDefinition.class.getName(),
					serviceBuilderObjectLayout.getObjectDefinitionId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectLayout",
					ObjectDefinition.class.getName(),
					serviceBuilderObjectLayout.getObjectDefinitionId())
			).put(
				"update",
				addAction(
					ActionKeys.UPDATE, "putObjectLayout",
					ObjectDefinition.class.getName(),
					serviceBuilderObjectLayout.getObjectDefinitionId())
			).build(),
			_objectDefinitionLocalService, _objectFieldLocalService,
			_objectRelationshipLocalService, serviceBuilderObjectLayout);
	}

	private com.liferay.object.model.ObjectLayoutBox _toObjectLayoutBox(
			long objectDefinitionId, ObjectLayoutBox objectLayoutBox)
		throws PortalException {

		com.liferay.object.model.ObjectLayoutBox serviceBuilderObjectLayoutBox =
			_objectLayoutBoxPersistence.create(0L);

		serviceBuilderObjectLayoutBox.setCollapsable(
			objectLayoutBox.getCollapsable());
		serviceBuilderObjectLayoutBox.setNameMap(
			LocalizedMapUtil.populateLocalizedMap(objectLayoutBox.getName()));
		serviceBuilderObjectLayoutBox.setObjectLayoutRows(
			transformToList(
				objectLayoutBox.getObjectLayoutRows(),
				objectLayoutRow -> _toObjectLayoutRow(
					objectDefinitionId, objectLayoutRow)));
		serviceBuilderObjectLayoutBox.setPriority(
			objectLayoutBox.getPriority());
		serviceBuilderObjectLayoutBox.setType(
			objectLayoutBox.getTypeAsString());

		return serviceBuilderObjectLayoutBox;
	}

	private com.liferay.object.model.ObjectLayoutColumn _toObjectLayoutColumn(
			long objectDefinitionId, ObjectLayoutColumn objectLayoutColumn)
		throws PortalException {

		com.liferay.object.model.ObjectLayoutColumn
			serviceBuilderObjectLayoutColumn =
				_objectLayoutColumnPersistence.create(0L);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, objectLayoutColumn.getObjectFieldName());

		serviceBuilderObjectLayoutColumn.setObjectFieldId(
			objectField.getObjectFieldId());

		serviceBuilderObjectLayoutColumn.setPriority(
			objectLayoutColumn.getPriority());
		serviceBuilderObjectLayoutColumn.setSize(
			GetterUtil.getInteger(objectLayoutColumn.getSize(), 12));

		return serviceBuilderObjectLayoutColumn;
	}

	private com.liferay.object.model.ObjectLayoutRow _toObjectLayoutRow(
			long objectDefinitionId, ObjectLayoutRow objectLayoutRow)
		throws PortalException {

		com.liferay.object.model.ObjectLayoutRow serviceBuilderObjectLayoutRow =
			_objectLayoutRowPersistence.create(0L);

		serviceBuilderObjectLayoutRow.setPriority(
			objectLayoutRow.getPriority());
		serviceBuilderObjectLayoutRow.setObjectLayoutColumns(
			transformToList(
				objectLayoutRow.getObjectLayoutColumns(),
				objectLayoutColumn -> _toObjectLayoutColumn(
					objectDefinitionId, objectLayoutColumn)));

		return serviceBuilderObjectLayoutRow;
	}

	private com.liferay.object.model.ObjectLayoutTab _toObjectLayoutTab(
			long objectDefinitionId, ObjectLayoutTab objectLayoutTab)
		throws PortalException {

		com.liferay.object.model.ObjectLayoutTab serviceBuilderObjectLayoutTab =
			_objectLayoutTabPersistence.create(0L);

		serviceBuilderObjectLayoutTab.setNameMap(
			LocalizedMapUtil.populateLocalizedMap(objectLayoutTab.getName()));
		serviceBuilderObjectLayoutTab.setObjectLayoutBoxes(
			transformToList(
				objectLayoutTab.getObjectLayoutBoxes(),
				objectLayoutBox -> _toObjectLayoutBox(
					objectDefinitionId, objectLayoutBox)));

		if (Validator.isNotNull(
				objectLayoutTab.getObjectRelationshipExternalReferenceCode())) {

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					getObjectRelationshipByExternalReferenceCode(
						objectLayoutTab.
							getObjectRelationshipExternalReferenceCode(),
						contextCompany.getCompanyId(), objectDefinitionId);

			serviceBuilderObjectLayoutTab.setObjectRelationshipId(
				objectRelationship.getObjectRelationshipId());
		}
		else {
			serviceBuilderObjectLayoutTab.setObjectRelationshipId(
				GetterUtil.getLong(objectLayoutTab.getObjectRelationshipId()));
		}

		serviceBuilderObjectLayoutTab.setPriority(
			objectLayoutTab.getPriority());

		return serviceBuilderObjectLayoutTab;
	}

	private static final EntityModel _entityModel =
		new ObjectLayoutEntityModel();

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectLayoutBoxPersistence _objectLayoutBoxPersistence;

	@Reference
	private ObjectLayoutColumnPersistence _objectLayoutColumnPersistence;

	@Reference
	private ObjectLayoutRowPersistence _objectLayoutRowPersistence;

	@Reference
	private ObjectLayoutService _objectLayoutService;

	@Reference
	private ObjectLayoutTabPersistence _objectLayoutTabPersistence;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}