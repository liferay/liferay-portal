/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.object.admin.rest.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.dto.v1_0.ObjectViewColumn;
import com.liferay.object.admin.rest.dto.v1_0.ObjectViewFilterColumn;
import com.liferay.object.admin.rest.dto.v1_0.ObjectViewSortColumn;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.admin.rest.internal.odata.entity.v1_0.ObjectViewEntityModel;
import com.liferay.object.admin.rest.resource.v1_0.ObjectViewResource;
import com.liferay.object.admin.rest.resource.v1_0.util.NameMapUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectViewService;
import com.liferay.object.service.persistence.ObjectViewColumnPersistence;
import com.liferay.object.service.persistence.ObjectViewFilterColumnPersistence;
import com.liferay.object.service.persistence.ObjectViewSortColumnPersistence;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-view.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ObjectViewResource.class
)
public class ObjectViewResourceImpl extends BaseObjectViewResourceImpl {

	@Override
	public void deleteObjectView(Long objectViewId) throws Exception {
		_objectViewService.deleteObjectView(objectViewId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ObjectView>
			getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
				String externalReferenceCode, String search,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextUser.getCompanyId());

		return getObjectDefinitionObjectViewsPage(
			objectDefinition.getObjectDefinitionId(), search, pagination,
			sorts);
	}

	@NestedField(
		parentClass = com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition.class,
		value = "objectViews"
	)
	@Override
	public Page<ObjectView> getObjectDefinitionObjectViewsPage(
			Long objectDefinitionId, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectView",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectViewBatch",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectViewBatch",
					ObjectDefinition.class.getName(), null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectDefinitionObjectViewsPage",
					ObjectDefinition.class.getName(), objectDefinitionId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectViewBatch",
					ObjectDefinition.class.getName(), null)
			).build(),
			booleanQuery -> {
			},
			null, com.liferay.object.model.ObjectView.class.getName(), search,
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
			document -> _toObjectView(
				_objectViewService.getObjectView(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectView getObjectView(Long objectViewId) throws Exception {
		return _toObjectView(_objectViewService.getObjectView(objectViewId));
	}

	@Override
	public ObjectView postObjectDefinitionByExternalReferenceCodeObjectView(
			String externalReferenceCode, ObjectView objectView)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return postObjectDefinitionObjectView(
			objectDefinition.getObjectDefinitionId(), objectView);
	}

	@Override
	public ObjectView postObjectDefinitionObjectView(
			Long objectDefinitionId, ObjectView objectView)
		throws Exception {

		return _toObjectView(
			_objectViewService.addObjectView(
				objectDefinitionId,
				GetterUtil.getBoolean(objectView.getDefaultObjectView()),
				LocalizedMapUtil.populateLocalizedMap(objectView.getName()),
				transformToList(
					objectView.getObjectViewColumns(),
					this::_toObjectViewColumn),
				transformToList(
					objectView.getObjectViewFilterColumns(),
					this::_toObjectViewFilterColumn),
				transformToList(
					objectView.getObjectViewSortColumns(),
					this::_toObjectViewSortColumn)));
	}

	@Override
	public ObjectView postObjectViewCopy(Long objectViewId) throws Exception {
		com.liferay.object.model.ObjectView objectView =
			_objectViewService.getObjectView(objectViewId);

		return _toObjectView(
			_objectViewService.addObjectView(
				objectView.getObjectDefinitionId(), false,
				NameMapUtil.copy(objectView.getNameMap()),
				objectView.getObjectViewColumns(),
				objectView.getObjectViewFilterColumns(),
				objectView.getObjectViewSortColumns()));
	}

	@Override
	public ObjectView putObjectView(Long objectViewId, ObjectView objectView)
		throws Exception {

		return _toObjectView(
			_objectViewService.updateObjectView(
				objectViewId, objectView.getDefaultObjectView(),
				LocalizedMapUtil.populateLocalizedMap(objectView.getName()),
				transformToList(
					objectView.getObjectViewColumns(),
					this::_toObjectViewColumn),
				transformToList(
					objectView.getObjectViewFilterColumns(),
					this::_toObjectViewFilterColumn),
				transformToList(
					objectView.getObjectViewSortColumns(),
					this::_toObjectViewSortColumn)));
	}

	private ObjectView _toObjectView(
			com.liferay.object.model.ObjectView serviceBuilderObjectView)
		throws Exception {

		return _objectViewDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				false,
				HashMapBuilder.put(
					"copy",
					addAction(
						ActionKeys.UPDATE, "postObjectViewCopy",
						ObjectDefinition.class.getName(),
						serviceBuilderObjectView.getObjectDefinitionId())
				).put(
					"delete",
					addAction(
						ActionKeys.DELETE, "deleteObjectView",
						ObjectDefinition.class.getName(),
						serviceBuilderObjectView.getObjectDefinitionId())
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, "getObjectView",
						ObjectDefinition.class.getName(),
						serviceBuilderObjectView.getObjectDefinitionId())
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, "putObjectView",
						ObjectDefinition.class.getName(),
						serviceBuilderObjectView.getObjectDefinitionId())
				).build(),
				null, null, contextAcceptLanguage.getPreferredLocale(), null,
				null),
			serviceBuilderObjectView);
	}

	private com.liferay.object.model.ObjectViewColumn _toObjectViewColumn(
		ObjectViewColumn objectViewColumn) {

		com.liferay.object.model.ObjectViewColumn
			serviceBuilderObjectViewColumn =
				_objectViewColumnPersistence.create(0L);

		serviceBuilderObjectViewColumn.setLabelMap(
			LocalizedMapUtil.populateLocalizedMap(objectViewColumn.getLabel()));
		serviceBuilderObjectViewColumn.setObjectFieldName(
			objectViewColumn.getObjectFieldName());
		serviceBuilderObjectViewColumn.setPriority(
			objectViewColumn.getPriority());

		return serviceBuilderObjectViewColumn;
	}

	private com.liferay.object.model.ObjectViewFilterColumn
		_toObjectViewFilterColumn(
			ObjectViewFilterColumn objectViewFilterColumn) {

		com.liferay.object.model.ObjectViewFilterColumn
			serviceBuilderObjectViewFilterColumn =
				_objectViewFilterColumnPersistence.create(0L);

		serviceBuilderObjectViewFilterColumn.setFilterType(
			objectViewFilterColumn.getFilterTypeAsString());
		serviceBuilderObjectViewFilterColumn.setJSON(
			objectViewFilterColumn.getJson());
		serviceBuilderObjectViewFilterColumn.setObjectFieldName(
			objectViewFilterColumn.getObjectFieldName());

		return serviceBuilderObjectViewFilterColumn;
	}

	private com.liferay.object.model.ObjectViewSortColumn
		_toObjectViewSortColumn(ObjectViewSortColumn objectViewSortColumn) {

		com.liferay.object.model.ObjectViewSortColumn
			serviceBuilderObjectViewSortColumn =
				_objectViewSortColumnPersistence.create(0L);

		serviceBuilderObjectViewSortColumn.setObjectFieldName(
			objectViewSortColumn.getObjectFieldName());
		serviceBuilderObjectViewSortColumn.setPriority(
			objectViewSortColumn.getPriority());
		serviceBuilderObjectViewSortColumn.setSortOrder(
			objectViewSortColumn.getSortOrderAsString());

		return serviceBuilderObjectViewSortColumn;
	}

	private static final EntityModel _entityModel = new ObjectViewEntityModel();

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectViewColumnPersistence _objectViewColumnPersistence;

	@Reference(target = DTOConverterConstants.OBJECT_VIEW_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectView, ObjectView>
		_objectViewDTOConverter;

	@Reference
	private ObjectViewFilterColumnPersistence
		_objectViewFilterColumnPersistence;

	@Reference
	private ObjectViewService _objectViewService;

	@Reference
	private ObjectViewSortColumnPersistence _objectViewSortColumnPersistence;

}