/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.resource.v1_0;

import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolderItem;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.object.admin.rest.internal.dto.v1_0.util.ObjectDefinitionUtil;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.exception.ObjectFolderItemObjectDefinitionIdException;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderItemLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectFolderService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Murilo Stodolni
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-folder.properties",
	scope = ServiceScope.PROTOTYPE, service = ObjectFolderResource.class
)
public class ObjectFolderResourceImpl extends BaseObjectFolderResourceImpl {

	@Override
	public void deleteObjectFolder(Long objectFolderId) throws Exception {
		_objectFolderService.deleteObjectFolder(objectFolderId);
	}

	@Override
	public ObjectFolder getObjectFolder(Long objectFolderId) throws Exception {
		return _toObjectFolder(
			_objectFolderService.getObjectFolder(objectFolderId));
	}

	@Override
	public ObjectFolder getObjectFolderByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toObjectFolder(
			_objectFolderService.getObjectFolderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Page<ObjectFolder> getObjectFoldersPage(
			String search, Pagination pagination)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ObjectActionKeys.ADD_OBJECT_FOLDER, "postObjectFolder",
					ObjectConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).put(
				"createBatch",
				addAction(
					ObjectActionKeys.ADD_OBJECT_FOLDER, "postObjectFolderBatch",
					ObjectConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteObjectFolderBatch",
					com.liferay.object.model.ObjectFolder.class.getName(), null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getObjectFoldersPage",
					com.liferay.object.model.ObjectFolder.class.getName(), null)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putObjectFolderBatch",
					com.liferay.object.model.ObjectFolder.class.getName(), null)
			).build(),
			booleanQuery -> {
			},
			null, com.liferay.object.model.ObjectFolder.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			null,
			document -> _toObjectFolder(
				_objectFolderService.getObjectFolder(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectFolder postObjectFolder(ObjectFolder objectFolder)
		throws Exception {

		com.liferay.object.model.ObjectFolder serviceBuilderObjectFolder =
			_objectFolderService.addObjectFolder(
				objectFolder.getExternalReferenceCode(),
				LocalizedMapUtil.populateLocalizedMap(objectFolder.getLabel()),
				objectFolder.getName());

		_addObjectFolderResources(
			serviceBuilderObjectFolder.getExternalReferenceCode(),
			serviceBuilderObjectFolder.getObjectFolderId(),
			ListUtil.fromArray(objectFolder.getObjectFolderItems()),
			Collections.emptyList());

		return _toObjectFolder(serviceBuilderObjectFolder);
	}

	@Override
	public ObjectFolder putObjectFolder(
			Long objectFolderId, ObjectFolder objectFolder)
		throws Exception {

		com.liferay.object.model.ObjectFolder serviceBuilderObjectFolder =
			_objectFolderService.updateObjectFolder(
				objectFolder.getExternalReferenceCode(), objectFolderId,
				LocalizedMapUtil.populateLocalizedMap(objectFolder.getLabel()));

		_addObjectFolderResources(
			objectFolder.getExternalReferenceCode(), objectFolderId,
			ListUtil.fromArray(objectFolder.getObjectFolderItems()),
			transform(
				new ArrayList<>(
					_objectFolderItemLocalService.
						getObjectFolderItemsByObjectFolderId(objectFolderId)),
				com.liferay.object.model.ObjectFolderItem::
					getObjectDefinitionId));

		return _toObjectFolder(serviceBuilderObjectFolder);
	}

	@Override
	public ObjectFolder putObjectFolderByExternalReferenceCode(
			String externalReferenceCode, ObjectFolder objectFolder)
		throws Exception {

		objectFolder.setExternalReferenceCode(() -> externalReferenceCode);

		com.liferay.object.model.ObjectFolder serviceBuilderObjectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderObjectFolder != null) {
			return putObjectFolder(
				serviceBuilderObjectFolder.getObjectFolderId(), objectFolder);
		}

		return postObjectFolder(objectFolder);
	}

	@Override
	protected void preparePatch(
		ObjectFolder objectFolder, ObjectFolder existingObjectFolder) {

		if (objectFolder.getObjectFolderItems() != null) {
			existingObjectFolder.setObjectFolderItems(
				objectFolder::getObjectFolderItems);
		}
	}

	private void _addObjectFolderResources(
			String objectFolderExternalReferenceCode, long objectFolderId,
			List<ObjectFolderItem> objectFolderItems,
			List<Long> serviceBuilderObjectDefinitionIds)
		throws Exception {

		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		ObjectDefinitionResource objectDefinitionResource = builder.user(
			contextUser
		).build();

		List<ObjectFolderItem> unlinkedObjectFolderItems = ListUtil.filter(
			objectFolderItems,
			objectFolderItem -> !objectFolderItem.getLinkedObjectDefinition());

		List<String> failedObjectDefinitionNames = new ArrayList<>();

		for (ObjectFolderItem unlinkedObjectFolderItem :
				unlinkedObjectFolderItems) {

			ObjectDefinition objectDefinition =
				unlinkedObjectFolderItem.getObjectDefinition();

			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition = null;

			if (objectDefinition != null) {
				objectDefinition.setObjectFolderExternalReferenceCode(
					() -> objectFolderExternalReferenceCode);

				try {
					objectDefinition =
						objectDefinitionResource.
							putObjectDefinitionByExternalReferenceCode(
								objectDefinition.getExternalReferenceCode(),
								objectDefinition);

					_objectFolderItemLocalService.updateObjectFolderItem(
						objectDefinition.getId(), objectFolderId,
						unlinkedObjectFolderItem.getPositionX(),
						unlinkedObjectFolderItem.getPositionY());

					serviceBuilderObjectDefinitionIds.remove(
						objectDefinition.getId());
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception);
					}

					failedObjectDefinitionNames.add(objectDefinition.getName());

					serviceBuilderObjectDefinition =
						_objectDefinitionLocalService.
							fetchObjectDefinitionByExternalReferenceCode(
								objectDefinition.getExternalReferenceCode(),
								contextCompany.getCompanyId());

					if (serviceBuilderObjectDefinition != null) {
						serviceBuilderObjectDefinitionIds.remove(
							serviceBuilderObjectDefinition.
								getObjectDefinitionId());
					}
				}

				continue;
			}

			serviceBuilderObjectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						unlinkedObjectFolderItem.
							getObjectDefinitionExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (serviceBuilderObjectDefinition == null) {
				continue;
			}

			_objectDefinitionLocalService.updateObjectFolderId(
				serviceBuilderObjectDefinition.getObjectDefinitionId(),
				objectFolderId);

			_objectFolderItemLocalService.updateObjectFolderItem(
				serviceBuilderObjectDefinition.getObjectDefinitionId(),
				objectFolderId, unlinkedObjectFolderItem.getPositionX(),
				unlinkedObjectFolderItem.getPositionY());

			serviceBuilderObjectDefinitionIds.remove(
				serviceBuilderObjectDefinition.getObjectDefinitionId());
		}

		com.liferay.object.model.ObjectFolder
			defaultServiceBuilderObjectFolder =
				_objectFolderService.getObjectFolderByExternalReferenceCode(
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_DEFAULT,
					contextCompany.getCompanyId());

		for (Long objectDefinitionId : serviceBuilderObjectDefinitionIds) {
			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectDefinitionId);

			if (serviceBuilderObjectDefinition.isLinkedToObjectFolder(
					objectFolderId)) {

				continue;
			}

			_objectDefinitionLocalService.updateObjectFolderId(
				objectDefinitionId,
				defaultServiceBuilderObjectFolder.getObjectFolderId());
		}

		objectFolderItems.removeAll(unlinkedObjectFolderItems);

		for (ObjectFolderItem objectFolderItem : objectFolderItems) {
			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition =
					_objectDefinitionLocalService.
						fetchObjectDefinitionByExternalReferenceCode(
							objectFolderItem.
								getObjectDefinitionExternalReferenceCode(),
							contextCompany.getCompanyId());

			if (serviceBuilderObjectDefinition == null) {
				continue;
			}

			_objectFolderItemLocalService.updateObjectFolderItem(
				serviceBuilderObjectDefinition.getObjectDefinitionId(),
				objectFolderId, objectFolderItem.getPositionX(),
				objectFolderItem.getPositionY());
		}

		if (!failedObjectDefinitionNames.isEmpty()) {
			throw new ObjectFolderItemObjectDefinitionIdException(
				failedObjectDefinitionNames);
		}
	}

	private ObjectFolder _toObjectFolder(
		com.liferay.object.model.ObjectFolder objectFolder) {

		String permissionName =
			com.liferay.object.model.ObjectFolder.class.getName();

		return new ObjectFolder() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"delete",
						() -> {
							if (objectFolder.isDefault()) {
								return null;
							}

							return addAction(
								ActionKeys.DELETE, "deleteObjectFolder",
								permissionName,
								objectFolder.getObjectFolderId());
						}
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, "getObjectFolder", permissionName,
							objectFolder.getObjectFolderId())
					).put(
						"permissions",
						addAction(
							ActionKeys.PERMISSIONS, "patchObjectFolder",
							permissionName, objectFolder.getObjectFolderId())
					).put(
						"update",
						() -> {
							if (objectFolder.isDefault()) {
								return null;
							}

							return addAction(
								ActionKeys.UPDATE, "putObjectFolder",
								permissionName,
								objectFolder.getObjectFolderId());
						}
					).build());
				setDateCreated(objectFolder::getCreateDate);
				setDateModified(objectFolder::getModifiedDate);
				setExternalReferenceCode(
					objectFolder::getExternalReferenceCode);
				setId(objectFolder::getObjectFolderId);
				setLabel(
					() -> LocalizedMapUtil.getLanguageIdMap(
						objectFolder.getLabelMap()));
				setName(objectFolder::getName);
				setObjectFolderItems(
					() -> transformToArray(
						_objectFolderItemLocalService.
							getObjectFolderItemsByObjectFolderId(
								objectFolder.getObjectFolderId()),
						objectFolderItem -> _toObjectFolderItem(
							objectFolder.getObjectFolderId(), objectFolderItem),
						ObjectFolderItem.class));
			}
		};
	}

	private ObjectFolderItem _toObjectFolderItem(
			long objectFolderId,
			com.liferay.object.model.ObjectFolderItem objectFolderItem)
		throws Exception {

		if (objectFolderItem == null) {
			return null;
		}

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionService.getObjectDefinition(
					objectFolderItem.getObjectDefinitionId());

		boolean finalLinkedObjectDefinition = !Objects.equals(
			serviceBuilderObjectDefinition.getObjectFolderId(), objectFolderId);

		return new ObjectFolderItem() {
			{
				setLinkedObjectDefinition(() -> finalLinkedObjectDefinition);
				setObjectDefinition(
					() -> {
						if (finalLinkedObjectDefinition) {
							return null;
						}

						return ObjectDefinitionUtil.toObjectDefinition(
							_groupLocalService,
							contextAcceptLanguage.getPreferredLocale(),
							_notificationTemplateLocalService,
							_objectActionLocalService,
							_objectDefinitionLocalService,
							_objectFieldDTOConverter, _objectFieldLocalService,
							_objectLayoutLocalService,
							_objectRelationshipDTOConverter,
							_objectRelationshipLocalService,
							_objectValidationRuleDTOConverter,
							_objectValidationRuleLocalService,
							_objectViewDTOConverter, _objectViewLocalService,
							_portal, serviceBuilderObjectDefinition,
							_systemObjectDefinitionManagerRegistry,
							_userLocalService,
							_workflowDefinitionLinkLocalService);
					});
				setObjectDefinitionExternalReferenceCode(
					serviceBuilderObjectDefinition::getExternalReferenceCode);
				setPositionX(objectFolderItem::getPositionX);
				setPositionY(objectFolderItem::getPositionY);
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFolderResourceImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference(target = DTOConverterConstants.OBJECT_FIELD_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectField, ObjectField>
		_objectFieldDTOConverter;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderItemLocalService _objectFolderItemLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectFolderService _objectFolderService;

	@Reference
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_RELATIONSHIP_DTO_CONVERTER)
	private DTOConverter
		<com.liferay.object.model.ObjectRelationship, ObjectRelationship>
			_objectRelationshipDTOConverter;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference(
		target = DTOConverterConstants.OBJECT_VALIDATION_RULE_DTO_CONVERTER
	)
	private DTOConverter
		<com.liferay.object.model.ObjectValidationRule, ObjectValidationRule>
			_objectValidationRuleDTOConverter;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Reference(target = DTOConverterConstants.OBJECT_VIEW_DTO_CONVERTER)
	private DTOConverter<com.liferay.object.model.ObjectView, ObjectView>
		_objectViewDTOConverter;

	@Reference
	private ObjectViewLocalService _objectViewLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}