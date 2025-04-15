/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectActionUtil;
import com.liferay.object.admin.rest.internal.odata.entity.v1_0.ObjectActionEntityModel;
import com.liferay.object.admin.rest.resource.v1_0.ObjectActionResource;
import com.liferay.object.service.ObjectActionService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
 * @author Marco Leo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-action.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ObjectActionResource.class
)
public class ObjectActionResourceImpl extends BaseObjectActionResourceImpl {

	@Override
	public void deleteObjectAction(Long objectActionId) throws Exception {
		_objectActionService.deleteObjectAction(objectActionId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ObjectAction getObjectAction(Long objectActionId) throws Exception {
		return _toObjectAction(
			_objectActionService.getObjectAction(objectActionId));
	}

	@Override
	public Page<ObjectAction>
			getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
				String externalReferenceCode, String search,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		com.liferay.object.model.ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getObjectDefinitionObjectActionsPage(
			objectDefinition.getObjectDefinitionId(), search, pagination,
			sorts);
	}

	@NestedField(parentClass = ObjectDefinition.class, value = "objectActions")
	@Override
	public Page<ObjectAction> getObjectDefinitionObjectActionsPage(
			Long objectDefinitionId, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectAction",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					objectDefinitionId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postObjectDefinitionObjectActionBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					objectDefinitionId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectActionBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectDefinitionObjectActionsPage",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					objectDefinitionId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectActionBatch",
					com.liferay.object.model.ObjectDefinition.class.getName(),
					null)
			).build(),
			booleanQuery -> {
			},
			null, com.liferay.object.model.ObjectAction.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setAttribute("label", search);
				searchContext.setAttribute(
					"objectDefinitionId", objectDefinitionId);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toObjectAction(
				_objectActionService.getObjectAction(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectAction postObjectDefinitionByExternalReferenceCodeObjectAction(
			String externalReferenceCode, ObjectAction objectAction)
		throws Exception {

		com.liferay.object.model.ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return postObjectDefinitionObjectAction(
			objectDefinition.getObjectDefinitionId(), objectAction);
	}

	@Override
	public ObjectAction postObjectDefinitionObjectAction(
			Long objectDefinitionId, ObjectAction objectAction)
		throws Exception {

		return _toObjectAction(
			_objectActionService.addObjectAction(
				objectAction.getExternalReferenceCode(), objectDefinitionId,
				objectAction.getActive(), objectAction.getConditionExpression(),
				objectAction.getDescription(),
				LocalizedMapUtil.populateLocalizedMap(
					objectAction.getErrorMessage()),
				LocalizedMapUtil.populateLocalizedMap(objectAction.getLabel()),
				objectAction.getName(),
				objectAction.getObjectActionExecutorKey(),
				objectAction.getObjectActionTriggerKey(),
				ObjectActionUtil.toParametersUnicodeProperties(
					objectAction.getParameters()),
				GetterUtil.getBoolean(objectAction.getSystem())));
	}

	@Override
	public ObjectAction putObjectAction(
			Long objectActionId, ObjectAction objectAction)
		throws Exception {

		return _toObjectAction(
			_objectActionService.updateObjectAction(
				objectAction.getExternalReferenceCode(), objectActionId,
				objectAction.getActive(), objectAction.getConditionExpression(),
				objectAction.getDescription(),
				LocalizedMapUtil.populateLocalizedMap(
					objectAction.getErrorMessage()),
				LocalizedMapUtil.populateLocalizedMap(objectAction.getLabel()),
				objectAction.getName(),
				objectAction.getObjectActionExecutorKey(),
				objectAction.getObjectActionTriggerKey(),
				ObjectActionUtil.toParametersUnicodeProperties(
					objectAction.getParameters())));
	}

	private ObjectAction _toObjectAction(
		com.liferay.object.model.ObjectAction objectAction) {

		if (objectAction == null) {
			return null;
		}

		String permissionName =
			com.liferay.object.model.ObjectDefinition.class.getName();

		return ObjectActionUtil.toObjectAction(
			HashMapBuilder.put(
				"delete",
				() -> {
					if (objectAction.isSystem()) {
						return null;
					}

					return addAction(
						ActionKeys.DELETE, "deleteObjectAction", permissionName,
						objectAction.getObjectDefinitionId());
				}
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectAction", permissionName,
					objectAction.getObjectDefinitionId())
			).put(
				"update",
				addAction(
					ActionKeys.UPDATE, "putObjectAction", permissionName,
					objectAction.getObjectDefinitionId())
			).build(),
			contextAcceptLanguage.getPreferredLocale(),
			_notificationTemplateLocalService, _objectDefinitionLocalService,
			objectAction);
	}

	private static final EntityModel _entityModel =
		new ObjectActionEntityModel();

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private ObjectActionService _objectActionService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}