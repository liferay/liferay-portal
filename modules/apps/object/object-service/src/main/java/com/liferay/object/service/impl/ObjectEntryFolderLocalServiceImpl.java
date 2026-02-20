/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.folder.subscription.util.ObjectEntryFolderSubscriptionUtil;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.entry.folder.util.ObjectEntryFolderUtil;
import com.liferay.object.exception.DuplicateObjectEntryFolderExternalReferenceCodeException;
import com.liferay.object.exception.ObjectEntryFolderNameException;
import com.liferay.object.exception.ObjectEntryFolderParentObjectEntryFolderIdException;
import com.liferay.object.exception.ObjectEntryFolderScopeException;
import com.liferay.object.exception.RequiredObjectEntryFolderException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectEntryFolderTable;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.base.ObjectEntryFolderLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectEntryPersistence;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.UniqueUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntryFolder",
	service = AopService.class
)
public class ObjectEntryFolderLocalServiceImpl
	extends ObjectEntryFolderLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntryFolder addObjectEntryFolder(
			String externalReferenceCode, long groupId, long userId,
			long parentObjectEntryFolderId, String description,
			Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validateExternalReferenceCode(
			externalReferenceCode, groupId, user.getCompanyId());

		_validateParentObjectEntryFolderId(
			groupId, null, parentObjectEntryFolderId);
		_validateName(
			groupId, user.getCompanyId(), 0, parentObjectEntryFolderId, name);

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			externalReferenceCode, groupId, parentObjectEntryFolderId,
			description, labelMap, name, serviceContext,
			WorkflowConstants.STATUS_APPROVED, user);

		_updateAsset(objectEntryFolder, serviceContext);

		return objectEntryFolder;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntryFolder copyObjectEntryFolder(
			long userId, long objectEntryFolderId,
			long parentObjectEntryFolderId, boolean replace,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder = getObjectEntryFolder(
			objectEntryFolderId);
		ObjectEntryFolder parentObjectEntryFolder = getObjectEntryFolder(
			parentObjectEntryFolderId);

		_validateParentObjectEntryFolderId(
			parentObjectEntryFolder.getGroupId(), objectEntryFolder,
			parentObjectEntryFolderId);

		if (replace &&
			(objectEntryFolder.getParentObjectEntryFolderId() ==
				parentObjectEntryFolderId)) {

			return objectEntryFolder;
		}

		_updateObjectEntryFolderName(
			objectEntryFolder, parentObjectEntryFolderId, replace);

		return _copyObjectEntryFolder(
			userId, objectEntryFolder, parentObjectEntryFolderId,
			serviceContext);
	}

	@Override
	public ObjectEntryFolder deleteObjectEntryFolder(long objectEntryFolderId)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderPersistence.findByPrimaryKey(objectEntryFolderId);

		return objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder);
	}

	@Override
	public ObjectEntryFolder deleteObjectEntryFolder(
			ObjectEntryFolder objectEntryFolder)
		throws PortalException {

		if (!ObjectEntryFolderThreadLocal.
				isForceDeleteSystemObjectEntryFolder() &&
			StringUtil.startsWith(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.
					EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_OBJECT_ENTRY_FOLDER)) {

			throw new RequiredObjectEntryFolderException(
				"System object entry folder " +
					objectEntryFolder.getExternalReferenceCode() +
						" cannot be deleted");
		}

		// Object entries

		ActionableDynamicQuery actionableDynamicQuery =
			_objectEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"groupId", objectEntryFolder.getGroupId()));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"companyId", objectEntryFolder.getCompanyId()));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"objectEntryFolderId",
						objectEntryFolder.getObjectEntryFolderId()));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(ObjectEntry objectEntry) ->
				_objectEntryLocalService.deleteObjectEntry(objectEntry));

		actionableDynamicQuery.performActions();

		// Object entry folders

		actionableDynamicQuery =
			objectEntryFolderLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"groupId", objectEntryFolder.getGroupId()));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"companyId", objectEntryFolder.getCompanyId()));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"parentObjectEntryFolderId",
						objectEntryFolder.getObjectEntryFolderId()));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(ObjectEntryFolder descendantObjectEntryFolder) ->
				objectEntryFolderLocalService.deleteObjectEntryFolder(
					descendantObjectEntryFolder));

		actionableDynamicQuery.performActions();

		_resourceLocalService.deleteResource(
			objectEntryFolder.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			objectEntryFolder.getObjectEntryFolderId());

		_assetEntryLocalService.deleteEntry(
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId());

		_sharingEntryLocalService.deleteSharingEntries(
			_classNameLocalService.getClassNameId(
				ObjectEntryFolder.class.getName()),
			objectEntryFolder.getObjectEntryFolderId());

		if (FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			_subscriptionLocalService.deleteSubscriptions(
				objectEntryFolder.getCompanyId(),
				ObjectEntryFolder.class.getName(),
				_getClassPK(
					objectEntryFolder.getGroupId(),
					objectEntryFolder.getObjectEntryFolderId()));
		}

		if (FeatureFlagManagerUtil.isEnabled("LPD-17564") &&
			(objectEntryFolder.getStatus() ==
				WorkflowConstants.STATUS_IN_TRASH)) {

			_trashEntryLocalService.deleteEntry(
				ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId());
		}

		if (FeatureFlagManagerUtil.isEnabled("LPD-42553")) {
			_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
				objectEntryFolder.getCompanyId(),
				objectEntryFolder.getGroupId(),
				ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId(),
				ObjectDefinitionConstants.OBJECT_DEFINITION_ID_ALL);
		}

		_resourceLocalService.deleteResource(
			objectEntryFolder.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			objectEntryFolder.getObjectEntryFolderId());

		return super.deleteObjectEntryFolder(objectEntryFolder);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntryFolder deleteObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderPersistence.findByERC_G_C(
				externalReferenceCode, groupId, companyId);

		return objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder);
	}

	@Override
	public ObjectEntryFolder fetchObjectEntryFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId, long companyId) {

		return objectEntryFolderPersistence.fetchByERC_G_C(
			externalReferenceCode, groupId, companyId);
	}

	@Override
	public ObjectEntryFolder getObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException {

		return objectEntryFolderPersistence.findByERC_G_C(
			externalReferenceCode, groupId, companyId);
	}

	@Override
	public List<ObjectEntryFolder> getObjectEntryFolders(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end) {

		return objectEntryFolderPersistence.findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end);
	}

	@Override
	public List<ObjectEntryFolder> getObjectEntryFoldersByExternalReferenceCode(
		String externalReferenceCode, List<Long> groupIds, long companyId) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryFolderTable.INSTANCE
			).from(
				ObjectEntryFolderTable.INSTANCE
			).where(
				ObjectEntryFolderTable.INSTANCE.externalReferenceCode.eq(
					externalReferenceCode
				).and(
					ObjectEntryFolderTable.INSTANCE.groupId.in(
						groupIds.toArray(new Long[0]))
				).and(
					ObjectEntryFolderTable.INSTANCE.companyId.eq(companyId)
				)
			));
	}

	@Override
	public int getObjectEntryFoldersCount(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return objectEntryFolderPersistence.countByG_C_P(
			groupId, companyId, parentObjectEntryFolderId);
	}

	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryFolder getOrAddEmptyObjectEntryFolder(
			String externalReferenceCode, long groupId, long companyId,
			long userId, ServiceContext serviceContext)
		throws PortalException {

		return _emptyModelManager.getOrAddEmptyModel(
			ObjectEntryFolder.class.getName(), companyId,
			() -> _addObjectEntryFolder(
				externalReferenceCode, groupId,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				StringPool.BLANK, null, externalReferenceCode, serviceContext,
				WorkflowConstants.STATUS_EMPTY,
				_userLocalService.getUser(userId)),
			externalReferenceCode,
			(_externalReferenceCode, _groupId) ->
				fetchObjectEntryFolderByExternalReferenceCode(
					_externalReferenceCode, _groupId, companyId),
			(_externalReferenceCode, _groupId) ->
				getObjectEntryFolderByExternalReferenceCode(
					_externalReferenceCode, _groupId, companyId),
			groupId, ObjectEntryFolder.class.getName());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntryFolder moveObjectEntryFolder(
			long userId, long objectEntryFolderId,
			long parentObjectEntryFolderId, boolean replace,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder = getObjectEntryFolder(
			objectEntryFolderId);
		ObjectEntryFolder parentObjectEntryFolder = getObjectEntryFolder(
			parentObjectEntryFolderId);

		_validateParentObjectEntryFolderId(
			parentObjectEntryFolder.getGroupId(), objectEntryFolder,
			parentObjectEntryFolderId);

		if (objectEntryFolder.getParentObjectEntryFolderId() ==
				parentObjectEntryFolderId) {

			return objectEntryFolder;
		}

		_updateObjectEntryFolderName(
			objectEntryFolder, parentObjectEntryFolderId, replace);

		return _moveObjectEntryFolder(
			userId, objectEntryFolder, parentObjectEntryFolderId,
			serviceContext);
	}

	@Override
	public void moveObjectEntryFoldersToTrash(
			long userId, ObjectEntryFolder parentObjectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException {

		for (ObjectEntryFolder objectEntryFolder :
				objectEntryFolderPersistence.findByG_C_P(
					parentObjectEntryFolder.getGroupId(),
					parentObjectEntryFolder.getCompanyId(),
					parentObjectEntryFolder.getObjectEntryFolderId())) {

			if (objectEntryFolder.getStatus() ==
					WorkflowConstants.STATUS_IN_TRASH) {

				continue;
			}

			objectEntryFolder = _moveObjectEntryFolderToTrash(
				objectEntryFolder,
				objectEntryFolder.getParentObjectEntryFolderId(),
				serviceContext, userId);

			Indexer<ObjectEntryFolder> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(ObjectEntryFolder.class);

			indexer.reindex(objectEntryFolder);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntryFolder moveObjectEntryFolderToTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException {

		if (objectEntryFolder.getStatus() ==
				WorkflowConstants.STATUS_IN_TRASH) {

			throw new TrashEntryException();
		}

		long parentObjectEntryFolderId =
			objectEntryFolder.getParentObjectEntryFolderId();

		objectEntryFolder.setParentObjectEntryFolderId(
			ObjectEntryFolderUtil.getRootObjectEntryFolderId(
				parentObjectEntryFolderId));

		return _moveObjectEntryFolderToTrash(
			objectEntryFolder, parentObjectEntryFolderId, serviceContext,
			userId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectEntryFolder restoreObjectEntryFolderFromTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException {

		if (objectEntryFolder.getStatus() !=
				WorkflowConstants.STATUS_IN_TRASH) {

			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId());

		objectEntryFolder.setParentObjectEntryFolderId(
			ObjectEntryFolderUtil.getObjectEntryFolderId(
				objectEntryFolder.getParentObjectEntryFolderId(),
				GetterUtil.getLong(
					trashEntry.getTypeSettingsProperty(
						"parentObjectEntryFolderId"))));

		return _restoreObjectEntryFolderFromTrash(
			objectEntryFolder, serviceContext, trashEntry, userId);
	}

	@Override
	public void restoreObjectEntryFoldersFromTrash(
			long userId, ObjectEntryFolder parentObjectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException {

		for (ObjectEntryFolder objectEntryFolder :
				objectEntryFolderPersistence.findByG_C_P(
					parentObjectEntryFolder.getGroupId(),
					parentObjectEntryFolder.getCompanyId(),
					parentObjectEntryFolder.getObjectEntryFolderId())) {

			if (objectEntryFolder.getStatus() !=
					WorkflowConstants.STATUS_IN_TRASH) {

				continue;
			}

			objectEntryFolder = _restoreObjectEntryFolderFromTrash(
				objectEntryFolder, serviceContext,
				_trashEntryLocalService.getEntry(
					ObjectEntryFolder.class.getName(),
					objectEntryFolder.getObjectEntryFolderId()),
				userId);

			Indexer<ObjectEntryFolder> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(ObjectEntryFolder.class);

			indexer.reindex(objectEntryFolder);
		}
	}

	@Override
	public void subscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws PortalException {

		User user = _userLocalService.fetchUser(userId);

		if (ObjectEntryFolderSubscriptionUtil.isSubscribedToObjectEntryFolder(
				user.getCompanyId(), groupId, objectEntryFolderId, userId)) {

			throw new UnsupportedOperationException();
		}

		_subscriptionLocalService.addSubscription(
			userId, groupId, ObjectEntryFolder.class.getName(),
			_getClassPK(groupId, objectEntryFolderId));
	}

	@Override
	public void unsubscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, ObjectEntryFolder.class.getName(),
			_getClassPK(groupId, objectEntryFolderId));
	}

	@Override
	public ObjectEntryFolder updateObjectEntryFolder(
			long userId, long objectEntryFolderId,
			long parentObjectEntryFolderId, String description,
			Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderPersistence.findByPrimaryKey(objectEntryFolderId);

		_validateParentObjectEntryFolderId(
			objectEntryFolder.getGroupId(), objectEntryFolder,
			parentObjectEntryFolderId);
		_validateName(
			objectEntryFolder.getGroupId(), objectEntryFolder.getCompanyId(),
			objectEntryFolderId, parentObjectEntryFolderId, name);

		objectEntryFolder.setParentObjectEntryFolderId(
			parentObjectEntryFolderId);
		objectEntryFolder.setDescription(description);
		objectEntryFolder.setLabelMap(_getLabelMap(labelMap, name));
		objectEntryFolder.setName(name);
		objectEntryFolder.setTreePath(objectEntryFolder.buildTreePath());
		objectEntryFolder.setStatus(
			EmptyModelManagerUtil.solveEmptyModel(
				objectEntryFolder.getExternalReferenceCode(),
				objectEntryFolder.getModelClassName(),
				objectEntryFolder.getCompanyId(),
				objectEntryFolder.getGroupId(), objectEntryFolder.getStatus(),
				() -> WorkflowConstants.STATUS_APPROVED));

		_updateWorkflowDefinitionLinks(objectEntryFolderId, serviceContext);

		objectEntryFolder = objectEntryFolderPersistence.update(
			objectEntryFolder);

		_updateAsset(objectEntryFolder, serviceContext);

		return objectEntryFolder;
	}

	@Override
	public ObjectEntryFolder updateStatus(
			ObjectEntryFolder objectEntryFolder, int status)
		throws PortalException {

		if (objectEntryFolder.getStatus() == status) {
			return objectEntryFolder;
		}

		objectEntryFolder.setStatus(status);

		return objectEntryFolderPersistence.update(objectEntryFolder);
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			String externalReferenceCode, long groupId,
			long parentObjectEntryFolderId, String description,
			Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext, int status, User user)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderPersistence.create(
				counterLocalService.increment());

		objectEntryFolder.setUuid(serviceContext.getUuid());
		objectEntryFolder.setExternalReferenceCode(externalReferenceCode);
		objectEntryFolder.setGroupId(groupId);
		objectEntryFolder.setCompanyId(user.getCompanyId());
		objectEntryFolder.setUserId(user.getUserId());
		objectEntryFolder.setUserName(user.getFullName());
		objectEntryFolder.setParentObjectEntryFolderId(
			parentObjectEntryFolderId);
		objectEntryFolder.setDescription(description);
		objectEntryFolder.setLabelMap(_getLabelMap(labelMap, name));
		objectEntryFolder.setName(name);
		objectEntryFolder.setTreePath(objectEntryFolder.buildTreePath());
		objectEntryFolder.setStatus(status);

		objectEntryFolder = objectEntryFolderPersistence.update(
			objectEntryFolder);

		_addResourcePermission(objectEntryFolder, serviceContext);

		return objectEntryFolder;
	}

	private void _addResourcePermission(
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext)
		throws PortalException {

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			_resourceLocalService.addResources(
				objectEntryFolder.getCompanyId(),
				objectEntryFolder.getGroupId(), objectEntryFolder.getUserId(),
				ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId(), false,
				serviceContext);
		}
		else {
			if (FeatureFlagManagerUtil.isEnabled(
					objectEntryFolder.getCompanyId(), "LPD-17564")) {

				Group group = _groupLocalService.fetchGroup(
					objectEntryFolder.getGroupId());

				if (group.isDepot()) {
					int count =
						_resourcePermissionLocalService.
							getResourcePermissionsCount(
								objectEntryFolder.getCompanyId(),
								ObjectEntryFolder.class.getName(),
								ResourceConstants.SCOPE_INDIVIDUAL,
								String.valueOf(
									objectEntryFolder.
										getObjectEntryFolderId()));

					if (count > 0) {
						return;
					}
				}

				ModelPermissions modelPermissions =
					serviceContext.getModelPermissions();

				if ((modelPermissions == null) ||
					!Objects.equals(
						modelPermissions.getResourceName(),
						ObjectEntryFolder.class.getName())) {

					modelPermissions = ModelPermissionsFactory.create(
						ObjectEntryFolder.class.getName());

					serviceContext.setModelPermissions(modelPermissions);
				}

				modelPermissions.addRolePermissions(
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
					ActionKeys.ADD_ENTRY, ActionKeys.VIEW);
				modelPermissions.addRolePermissions(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
					ActionKeys.ADD_ENTRY, ActionKeys.VIEW);
				modelPermissions.addRolePermissions(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER, ActionKeys.VIEW);

				if (group.isDepot()) {
					DepotEntry depotEntry =
						_depotEntryLocalService.getGroupDepotEntry(
							group.getGroupId());

					if (depotEntry.getType() == DepotConstants.TYPE_SPACE) {
						modelPermissions.addRolePermissions(
							RoleConstants.CMS_ADMINISTRATOR,
							ActionKeys.ADD_ENTRY, ActionKeys.VIEW);
					}
				}
			}

			_resourceLocalService.addModelResources(
				objectEntryFolder.getCompanyId(),
				objectEntryFolder.getGroupId(), objectEntryFolder.getUserId(),
				ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId(),
				serviceContext.getModelPermissions());
		}
	}

	private ObjectEntryFolder _copyObjectEntryFolder(
			long userId, ObjectEntryFolder objectEntryFolder,
			long parentObjectEntryFolderId, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryFolder parentObjectEntryFolder = getObjectEntryFolder(
			parentObjectEntryFolderId);

		ObjectEntryFolder newObjectEntryFolder = addObjectEntryFolder(
			null, parentObjectEntryFolder.getGroupId(), userId,
			parentObjectEntryFolderId, objectEntryFolder.getDescription(),
			objectEntryFolder.getLabelMap(), objectEntryFolder.getName(),
			serviceContext);

		for (ObjectEntry objectEntry :
				_objectEntryPersistence.findByG_C_OEFI(
					objectEntryFolder.getGroupId(),
					objectEntryFolder.getCompanyId(),
					objectEntryFolder.getObjectEntryFolderId())) {

			_objectEntryLocalService.copyObjectEntry(
				userId, objectEntry.getObjectEntryId(),
				newObjectEntryFolder.getObjectEntryFolderId(),
				objectEntry.getValues(), serviceContext);
		}

		for (ObjectEntryFolder childObjectEntryFolder :
				getObjectEntryFolders(
					objectEntryFolder.getGroupId(),
					objectEntryFolder.getCompanyId(),
					objectEntryFolder.getObjectEntryFolderId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			_copyObjectEntryFolder(
				userId, childObjectEntryFolder,
				newObjectEntryFolder.getObjectEntryFolderId(), serviceContext);
		}

		return newObjectEntryFolder;
	}

	private long _getClassPK(long groupId, long objectEntryFolderId) {
		if (objectEntryFolderId ==
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			return groupId;
		}

		return objectEntryFolderId;
	}

	private Map<Locale, String> _getLabelMap(
		Map<Locale, String> labelMap, String name) {

		if (MapUtil.isEmpty(labelMap) ||
			!labelMap.containsKey(LocaleUtil.getSiteDefault())) {

			return HashMapBuilder.putAll(
				labelMap
			).put(
				LocaleUtil.getSiteDefault(), name
			).build();
		}

		return labelMap;
	}

	private String _getUniqueName(
			String name, ObjectEntryFolder parentObjectEntryFolder)
		throws PortalException {

		if (_isUniqueName(name, parentObjectEntryFolder)) {
			return name;
		}

		return UniqueUtil.getUniqueValue(
			"copy",
			uniqueValue -> _isUniqueName(uniqueValue, parentObjectEntryFolder),
			name);
	}

	private boolean _isUniqueName(
		String copyValue, ObjectEntryFolder parentObjectEntryFolder) {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderPersistence.fetchByG_C_P_N(
				parentObjectEntryFolder.getGroupId(),
				parentObjectEntryFolder.getCompanyId(),
				parentObjectEntryFolder.getObjectEntryFolderId(), copyValue);

		if (objectEntryFolder == null) {
			return true;
		}

		return false;
	}

	private ObjectEntryFolder _moveObjectEntryFolder(
			long userId, ObjectEntryFolder objectEntryFolder,
			long parentObjectEntryFolderId, ServiceContext serviceContext)
		throws PortalException {

		long groupId = objectEntryFolder.getGroupId();

		ObjectEntryFolder parentObjectEntryFolder = getObjectEntryFolder(
			parentObjectEntryFolderId);

		if (objectEntryFolder.getGroupId() !=
				parentObjectEntryFolder.getGroupId()) {

			objectEntryFolder.setGroupId(parentObjectEntryFolder.getGroupId());
		}

		objectEntryFolder.setParentObjectEntryFolderId(
			parentObjectEntryFolderId);
		objectEntryFolder.setTreePath(objectEntryFolder.buildTreePath());

		objectEntryFolder = updateObjectEntryFolder(objectEntryFolder);

		if (groupId != parentObjectEntryFolder.getGroupId()) {
			for (ObjectEntry objectEntry :
					_objectEntryPersistence.findByG_C_OEFI(
						groupId, objectEntryFolder.getCompanyId(),
						objectEntryFolder.getObjectEntryFolderId())) {

				_objectEntryLocalService.moveObjectEntry(
					userId, objectEntry.getObjectEntryId(),
					objectEntry.getObjectEntryFolderId(),
					objectEntry.getValues(), serviceContext);
			}

			for (ObjectEntryFolder childObjectEntryFolder :
					getObjectEntryFolders(
						groupId, objectEntryFolder.getCompanyId(),
						objectEntryFolder.getObjectEntryFolderId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				_moveObjectEntryFolder(
					userId, childObjectEntryFolder,
					childObjectEntryFolder.getParentObjectEntryFolderId(),
					serviceContext);
			}
		}

		return objectEntryFolder;
	}

	private ObjectEntryFolder _moveObjectEntryFolderToTrash(
			ObjectEntryFolder objectEntryFolder, long parentObjectEntryFolderId,
			ServiceContext serviceContext, long userId)
		throws PortalException {

		int oldStatus = objectEntryFolder.getStatus();

		objectEntryFolder.setTreePath(objectEntryFolder.buildTreePath());

		objectEntryFolder = updateStatus(
			objectEntryFolder, WorkflowConstants.STATUS_IN_TRASH);

		_trashEntryLocalService.addTrashEntry(
			userId, objectEntryFolder.getGroupId(),
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId(),
			objectEntryFolder.getUuid(), null, oldStatus, null,
			UnicodePropertiesBuilder.put(
				"parentObjectEntryFolderId", parentObjectEntryFolderId
			).put(
				"title", objectEntryFolder.getObjectEntryFolderId()
			).build());

		_objectEntryLocalService.moveObjectEntriesToTrash(
			userId, objectEntryFolder, serviceContext);

		moveObjectEntryFoldersToTrash(
			userId, objectEntryFolder, serviceContext);

		return objectEntryFolder;
	}

	private ObjectEntryFolder _restoreObjectEntryFolderFromTrash(
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext,
			TrashEntry trashEntry, long userId)
		throws PortalException {

		objectEntryFolder.setTreePath(objectEntryFolder.buildTreePath());

		objectEntryFolder = updateStatus(
			objectEntryFolder, trashEntry.getStatus());

		_trashEntryLocalService.deleteEntry(
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId());

		_objectEntryLocalService.restoreObjectEntriesFromTrash(
			userId, objectEntryFolder, serviceContext);

		restoreObjectEntryFoldersFromTrash(
			userId, objectEntryFolder, serviceContext);

		return objectEntryFolder;
	}

	private void _updateAsset(
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext)
		throws PortalException {

		_assetEntryLocalService.updateEntry(
			serviceContext.getUserId(), objectEntryFolder.getGroupId(),
			objectEntryFolder.getCreateDate(),
			objectEntryFolder.getModifiedDate(),
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId(),
			objectEntryFolder.getUuid(), 0,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(), true, true, null, null,
			objectEntryFolder.getCreateDate(), null, null,
			objectEntryFolder.getName(), null, null, null, null, 0, 0, null);
	}

	private void _updateObjectEntryFolderName(
			ObjectEntryFolder objectEntryFolder, long parentObjectEntryFolderId,
			boolean replace)
		throws PortalException {

		ObjectEntryFolder parentObjectEntryFolder =
			objectEntryFolderLocalService.fetchObjectEntryFolder(
				parentObjectEntryFolderId);

		String uniqueName = _getUniqueName(
			objectEntryFolder.getName(), parentObjectEntryFolder);

		if (StringUtil.equals(objectEntryFolder.getName(), uniqueName)) {
			return;
		}

		if (replace) {
			ObjectEntryFolder duplicateObjectEntryFolder =
				objectEntryFolderPersistence.fetchByG_C_P_N(
					parentObjectEntryFolder.getGroupId(),
					parentObjectEntryFolder.getCompanyId(),
					parentObjectEntryFolder.getObjectEntryFolderId(),
					objectEntryFolder.getName());

			if (duplicateObjectEntryFolder != null) {
				duplicateObjectEntryFolder.setName(
					duplicateObjectEntryFolder.getName() + "(replace)");

				duplicateObjectEntryFolder =
					objectEntryFolderLocalService.updateObjectEntryFolder(
						duplicateObjectEntryFolder);

				objectEntryFolderLocalService.deleteObjectEntryFolder(
					duplicateObjectEntryFolder);

				return;
			}
		}

		objectEntryFolder.setLabel(
			uniqueName,
			LocaleUtil.fromLanguageId(
				objectEntryFolder.getDefaultLanguageId()));
		objectEntryFolder.setName(uniqueName);
	}

	private void _updateWorkflowDefinitionLinks(
			long objectEntryFolderId, ServiceContext serviceContext)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-42553") ||
			!GetterUtil.getBoolean(
				serviceContext.getAttribute("updateWorkflowDefinitionLinks"),
				true)) {

			return;
		}

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLinks(
			serviceContext.getUserId(), serviceContext.getCompanyId(),
			serviceContext.getScopeGroupId(), ObjectEntryFolder.class.getName(),
			objectEntryFolderId,
			Collections.singletonList(
				new ObjectValuePair<>(
					ObjectDefinitionConstants.OBJECT_DEFINITION_ID_ALL,
					ParamUtil.getString(
						serviceContext,
						"workflowDefinition" +
							ObjectDefinitionConstants.
								OBJECT_DEFINITION_ID_ALL))));
	}

	private void _validateExternalReferenceCode(
		String externalReferenceCode, long groupId, long companyId) {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderPersistence.fetchByERC_G_C(
				externalReferenceCode, groupId, companyId);

		if (objectEntryFolder != null) {
			throw new DuplicateObjectEntryFolderExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate object entry folder with external reference ",
					"code ", externalReferenceCode));
		}
	}

	private void _validateName(
			long groupId, long companyId, long objectEntryFolderId,
			long parentObjectEntryFolderId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new ObjectEntryFolderNameException.MustNotBeNull();
		}

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderPersistence.fetchByG_C_P_N(
				groupId, companyId, parentObjectEntryFolderId, name);

		if ((objectEntryFolder != null) &&
			(objectEntryFolder.getObjectEntryFolderId() !=
				objectEntryFolderId)) {

			throw new ObjectEntryFolderNameException.MustNotBeDuplicate(name);
		}
	}

	private void _validateParentObjectEntryFolderId(
			long groupId, ObjectEntryFolder objectEntryFolder,
			long parentObjectEntryFolderId)
		throws PortalException {

		if (parentObjectEntryFolderId ==
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			return;
		}

		ObjectEntryFolder parentObjectEntryFolder =
			objectEntryFolderPersistence.findByPrimaryKey(
				parentObjectEntryFolderId);

		if (parentObjectEntryFolder.getGroupId() != groupId) {
			throw new ObjectEntryFolderScopeException(
				StringBundler.concat(
					"Group ID ", groupId,
					" does not match parent object entry folder group ID ",
					parentObjectEntryFolder.getGroupId()));
		}

		if ((objectEntryFolder != null) &&
			StringUtil.startsWith(
				parentObjectEntryFolder.getTreePath(),
				objectEntryFolder.getTreePath())) {

			throw new ObjectEntryFolderParentObjectEntryFolderIdException(
				StringBundler.concat(
					"Object entry folder ",
					objectEntryFolder.getObjectEntryFolderId(),
					" cannot have one of its children or itself as a parent"));
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryPersistence _objectEntryPersistence;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}