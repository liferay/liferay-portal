/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.impl;

import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.exception.DuplicateKBFolderNameException;
import com.liferay.knowledge.base.exception.InvalidKBFolderNameException;
import com.liferay.knowledge.base.exception.KBFolderParentException;
import com.liferay.knowledge.base.exception.NoSuchFolderException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.base.KBFolderLocalServiceBaseImpl;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.knowledge.base.model.KBFolder",
	service = AopService.class
)
public class KBFolderLocalServiceImpl extends KBFolderLocalServiceBaseImpl {

	@Override
	public KBFolder addKBFolder(
			String externalReferenceCode, long userId, long groupId,
			long parentResourceClassNameId, long parentResourcePrimKey,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		// KB folder

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		_validateName(groupId, parentResourcePrimKey, name);
		_validateParent(parentResourceClassNameId, parentResourcePrimKey);

		long kbFolderId = counterLocalService.increment();

		KBFolder kbFolder = kbFolderPersistence.create(kbFolderId);

		kbFolder.setUuid(serviceContext.getUuid());
		kbFolder.setExternalReferenceCode(externalReferenceCode);
		kbFolder.setGroupId(groupId);
		kbFolder.setCompanyId(user.getCompanyId());
		kbFolder.setUserId(userId);
		kbFolder.setUserName(user.getFullName());
		kbFolder.setCreateDate(date);
		kbFolder.setModifiedDate(date);
		kbFolder.setParentKBFolderId(parentResourcePrimKey);
		kbFolder.setName(name);
		kbFolder.setUrlTitle(
			_getUniqueUrlTitle(
				groupId, parentResourcePrimKey, kbFolderId, name));
		kbFolder.setDescription(description);
		kbFolder.setStatus(WorkflowConstants.STATUS_APPROVED);
		kbFolder.setStatusByUserId(userId);
		kbFolder.setStatusByUserName(user.getFullName());
		kbFolder.setStatusDate(date);
		kbFolder.setExpandoBridgeAttributes(serviceContext);

		kbFolder = kbFolderPersistence.update(kbFolder);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			_addKBFolderResources(
				kbFolder, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			_addKBFolderResources(
				kbFolder, serviceContext.getModelPermissions());
		}

		return kbFolder;
	}

	@Override
	public KBFolder deleteKBFolder(KBFolder kbFolder) throws PortalException {
		return kbFolderLocalService.deleteKBFolder(kbFolder, true);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBFolder deleteKBFolder(
			KBFolder kbFolder, boolean includeTrashedEntries)
		throws PortalException {

		_kbArticleLocalService.deleteKBArticles(
			kbFolder.getGroupId(), kbFolder.getKbFolderId(),
			includeTrashedEntries);

		List<KBFolder> childKBFolders = kbFolderPersistence.findByG_P(
			kbFolder.getGroupId(), kbFolder.getKbFolderId());

		for (KBFolder childKBFolder : childKBFolders) {
			if (!includeTrashedEntries &&
				_trashHelper.isInTrashExplicitly(childKBFolder)) {

				continue;
			}

			deleteKBFolder(childKBFolder.getKbFolderId());
		}

		_resourceLocalService.deleteResource(
			kbFolder.getCompanyId(), KBFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, kbFolder.getKbFolderId());

		_expandoRowLocalService.deleteRows(kbFolder.getKbFolderId());

		if (_trashHelper.isInTrashExplicitly(kbFolder)) {
			_trashEntryLocalService.deleteEntry(
				KBFolder.class.getName(), kbFolder.getKbFolderId());
		}
		else {
			_trashVersionLocalService.deleteTrashVersion(
				KBFolder.class.getName(), kbFolder.getKbFolderId());
		}

		return kbFolderPersistence.remove(kbFolder);
	}

	@Override
	public KBFolder deleteKBFolder(long kbFolderId) throws PortalException {
		return deleteKBFolder(kbFolderId, true);
	}

	@Override
	public KBFolder deleteKBFolder(
			long kbFolderId, boolean includeTrashedEntries)
		throws PortalException {

		KBFolder kbFolder = kbFolderPersistence.findByPrimaryKey(kbFolderId);

		return kbFolderLocalService.deleteKBFolder(
			kbFolder, includeTrashedEntries);
	}

	@Override
	public void deleteKBFolders(long groupId) throws PortalException {
		List<KBFolder> kbFolders = getKBFolders(
			groupId, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (KBFolder kbFolder : kbFolders) {
			deleteKBFolder(kbFolder.getKbFolderId());
		}
	}

	@Override
	public KBFolder fetchFirstChildKBFolder(long groupId, long kbFolderId)
		throws PortalException {

		return fetchFirstChildKBFolder(groupId, kbFolderId, null);
	}

	@Override
	public KBFolder fetchFirstChildKBFolder(
			long groupId, long kbFolderId,
			OrderByComparator<KBFolder> orderByComparator)
		throws PortalException {

		return kbFolderPersistence.fetchByG_P_First(
			groupId, kbFolderId, orderByComparator);
	}

	@Override
	public KBFolder fetchKBFolder(long kbFolderId) {
		return kbFolderPersistence.fetchByPrimaryKey(kbFolderId);
	}

	@Override
	public KBFolder fetchKBFolder(String uuid, long groupId) {
		return kbFolderPersistence.fetchByUUID_G(uuid, groupId);
	}

	@Override
	public KBFolder fetchKBFolderByUrlTitle(
			long groupId, long parentKbFolderId, String urlTitle)
		throws PortalException {

		return kbFolderPersistence.fetchByG_P_UT(
			groupId, parentKbFolderId, urlTitle);
	}

	@Override
	public KBFolder getKBFolderByUrlTitle(
			long groupId, long parentKbFolderId, String urlTitle)
		throws PortalException {

		return kbFolderPersistence.findByG_P_UT(
			groupId, parentKbFolderId, urlTitle);
	}

	@Override
	public List<KBFolder> getKBFolders(
			long groupId, long parentKBFolderId, int start, int end)
		throws PortalException {

		return kbFolderPersistence.findByG_P_S(
			groupId, parentKBFolderId, WorkflowConstants.STATUS_APPROVED, start,
			end);
	}

	@Override
	public List<Object> getKBFoldersAndKBArticles(
		long groupId, long parentResourcePrimKey, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return kbFolderFinder.findF_A_ByG_P(
			groupId, parentResourcePrimKey, queryDefinition);
	}

	@Override
	public List<Object> getKBFoldersAndKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return kbFolderFinder.findF_A_ByG_P(
			groupId, parentResourcePrimKey, queryDefinition);
	}

	@Override
	public int getKBFoldersAndKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return kbFolderFinder.countF_A_ByG_P(
			groupId, parentResourcePrimKey, queryDefinition);
	}

	@Override
	public int getKBFoldersCount(long groupId, long parentKBFolderId)
		throws PortalException {

		return kbFolderPersistence.countByG_P(groupId, parentKBFolderId);
	}

	@Override
	public int getKBFoldersCount(
			long groupId, long parentKBFolderId, int status)
		throws PortalException {

		return kbFolderPersistence.countByG_P_S(
			groupId, parentKBFolderId, status);
	}

	@Override
	public KBFolder moveKBFolder(long kbFolderId, long parentKBFolderId)
		throws PortalException {

		KBFolder kbFolder = kbFolderPersistence.findByPrimaryKey(kbFolderId);

		if (parentKBFolderId != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			KBFolder parentKBFolder = kbFolderPersistence.findByPrimaryKey(
				parentKBFolderId);

			_validateParent(kbFolder, parentKBFolder);

			parentKBFolderId = parentKBFolder.getKbFolderId();
		}

		kbFolder.setParentKBFolderId(parentKBFolderId);

		kbFolder = kbFolderPersistence.update(kbFolder);

		LinkedList<Object> kbFoldersAndArticles = new LinkedList<>(
			kbFolderLocalService.getKBFoldersAndKBArticles(
				kbFolder.getGroupId(), kbFolder.getKbFolderId(),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		while (!kbFoldersAndArticles.isEmpty()) {
			Object kbObject = kbFoldersAndArticles.pop();

			if (kbObject instanceof KBArticle) {
				KBArticle childKBArticle = (KBArticle)kbObject;

				_kbArticleLocalService.updateKBArticle(childKBArticle);
			}
			else if (kbObject instanceof KBFolder) {
				KBFolder childKBFolder = (KBFolder)kbObject;

				kbFoldersAndArticles.addAll(
					kbFolderLocalService.getKBFoldersAndKBArticles(
						childKBFolder.getGroupId(),
						childKBFolder.getKbFolderId(),
						WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null));
			}
		}

		return kbFolder;
	}

	@Override
	public KBFolder moveKBFolderFromTrash(
			long userId, long kbFolderId, long parentKBFolderId)
		throws PortalException {

		KBFolder kbFolder = kbFolderPersistence.findByPrimaryKey(kbFolderId);

		if (!kbFolder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(kbFolder)) {
			restoreKBFolderFromTrash(userId, kbFolderId);
		}
		else {

			// KB folder

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				KBFolder.class.getName(), kbFolderId);

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			updateStatus(userId, kbFolder, status);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// KB folders and articles

			_restoreDependentsFromTrash(kbFolder);
		}

		return moveKBFolder(kbFolderId, parentKBFolderId);
	}

	@Override
	public KBFolder moveKBFolderToTrash(long userId, long kbFolderId)
		throws PortalException {

		// KB folder

		KBFolder kbFolder = kbFolderPersistence.findByPrimaryKey(kbFolderId);

		if (kbFolder.isInTrash()) {
			throw new TrashEntryException();
		}

		int oldStatus = kbFolder.getStatus();

		kbFolder = updateStatus(
			userId, kbFolder, WorkflowConstants.STATUS_IN_TRASH);

		// Trash

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, kbFolder.getGroupId(), KBFolder.class.getName(),
			kbFolder.getKbFolderId(), kbFolder.getUuid(), null, oldStatus, null,
			null);

		// KB folders and articles

		_moveDependentsToTrash(kbFolder, trashEntry.getEntryId());

		return kbFolder;
	}

	@Override
	public KBFolder restoreKBFolderFromTrash(long userId, long kbFolderId)
		throws PortalException {

		// KB folder

		KBFolder kbFolder = kbFolderPersistence.findByPrimaryKey(kbFolderId);

		if (!kbFolder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			KBFolder.class.getName(), kbFolderId);

		kbFolder = updateStatus(userId, kbFolder, trashEntry.getStatus());

		// KB folders and articles

		_restoreDependentsFromTrash(kbFolder);

		// Trash

		_trashEntryLocalService.deleteEntry(trashEntry.getEntryId());

		return kbFolder;
	}

	@Override
	public KBFolder updateKBFolder(
			long parentResourceClassNameId, long parentResourcePrimKey,
			long kbFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		_validateParent(parentResourceClassNameId, parentResourcePrimKey);

		KBFolder kbFolder = kbFolderPersistence.findByPrimaryKey(kbFolderId);

		if (!StringUtil.equals(name, kbFolder.getName())) {
			_validateName(kbFolder.getGroupId(), parentResourcePrimKey, name);
		}

		kbFolder.setModifiedDate(new Date());
		kbFolder.setParentKBFolderId(parentResourcePrimKey);
		kbFolder.setName(name);
		kbFolder.setDescription(description);
		kbFolder.setExpandoBridgeAttributes(serviceContext);

		return kbFolderPersistence.update(kbFolder);
	}

	@Override
	public KBFolder updateStatus(long userId, KBFolder kbFolder, int status)
		throws PortalException {

		// KB folder

		User user = _userLocalService.getUser(userId);

		kbFolder.setStatus(status);
		kbFolder.setStatusByUserId(userId);
		kbFolder.setStatusByUserName(user.getFullName());
		kbFolder.setStatusDate(new Date());

		kbFolder = kbFolderPersistence.update(kbFolder);

		// Indexer

		Indexer<KBFolder> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			KBFolder.class);

		indexer.reindex(kbFolder);

		return kbFolder;
	}

	private void _addKBFolderResources(
			KBFolder kbFolder, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			kbFolder.getCompanyId(), kbFolder.getGroupId(),
			kbFolder.getUserId(), KBFolder.class.getName(),
			kbFolder.getKbFolderId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	private void _addKBFolderResources(
			KBFolder kbFolder, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			kbFolder.getCompanyId(), kbFolder.getGroupId(),
			kbFolder.getUserId(), KBFolder.class.getName(),
			kbFolder.getKbFolderId(), modelPermissions);
	}

	private String _getUniqueUrlTitle(
		long groupId, long parentKbFolderId, long kbFolderId, String name) {

		String urlTitle = KnowledgeBaseUtil.getUrlTitle(kbFolderId, name);

		String uniqueUrlTitle = urlTitle;

		KBFolder kbFolder = kbFolderPersistence.fetchByG_P_UT(
			groupId, parentKbFolderId, uniqueUrlTitle);

		for (int i = 1; kbFolder != null; i++) {
			uniqueUrlTitle = urlTitle + StringPool.DASH + i;

			kbFolder = kbFolderPersistence.fetchByG_P_UT(
				groupId, parentKbFolderId, uniqueUrlTitle);
		}

		return uniqueUrlTitle;
	}

	private void _moveDependentsToTrash(
			KBFolder parentKBFolder, long trashEntryId)
		throws PortalException {

		List<Object> objects = getKBFoldersAndKBArticles(
			parentKBFolder.getGroupId(), parentKBFolder.getKbFolderId(),
			WorkflowConstants.STATUS_ANY);

		for (Object object : objects) {
			if (object instanceof KBArticle) {
				_kbArticleLocalService.moveDependentKBArticleToTrash(
					(KBArticle)object, trashEntryId);
			}
			else {

				// KB folder

				KBFolder kbFolder = (KBFolder)object;

				if (kbFolder.isInTrash()) {
					continue;
				}

				int oldStatus = kbFolder.getStatus();

				kbFolder.setStatus(WorkflowConstants.STATUS_IN_TRASH);

				kbFolder = kbFolderPersistence.update(kbFolder);

				// Trash

				if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
					_trashVersionLocalService.addTrashVersion(
						trashEntryId, KBFolder.class.getName(),
						kbFolder.getKbFolderId(), oldStatus, null);
				}

				// KB folders and articles

				_moveDependentsToTrash(kbFolder, trashEntryId);

				// Indexer

				Indexer<KBFolder> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(KBFolder.class);

				indexer.reindex(kbFolder);
			}
		}
	}

	private void _restoreDependentsFromTrash(KBFolder parentKBFolder)
		throws PortalException {

		List<Object> objects = getKBFoldersAndKBArticles(
			parentKBFolder.getGroupId(), parentKBFolder.getKbFolderId(),
			WorkflowConstants.STATUS_IN_TRASH);

		for (Object object : objects) {
			if (object instanceof KBArticle) {
				_kbArticleLocalService.restoreDependentKBArticleFromTrash(
					(KBArticle)object);
			}
			else {

				// KB folder

				KBFolder kbFolder = (KBFolder)object;

				if (!_trashHelper.isInTrashImplicitly(kbFolder)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						KBFolder.class.getName(), kbFolder.getKbFolderId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				kbFolder.setStatus(oldStatus);

				kbFolder = kbFolderPersistence.update(kbFolder);

				// KB folders and articles

				_restoreDependentsFromTrash(kbFolder);

				// Trash

				if (trashVersion != null) {
					_trashVersionLocalService.deleteTrashVersion(trashVersion);
				}

				// Indexer

				Indexer<KBFolder> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(KBFolder.class);

				indexer.reindex(kbFolder);
			}
		}
	}

	private void _validateName(long groupId, long parentKBFolderId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new InvalidKBFolderNameException("KB folder name is null");
		}

		KBFolder kbFolder = kbFolderPersistence.fetchByG_P_N(
			groupId, parentKBFolderId, name);

		if (kbFolder != null) {
			throw new DuplicateKBFolderNameException(
				String.format("A KB folder with name %s already exists", name));
		}
	}

	private void _validateParent(KBFolder kbFolder, KBFolder parentKBFolder)
		throws PortalException {

		if (kbFolder.getGroupId() != parentKBFolder.getGroupId()) {
			throw new NoSuchFolderException(
				String.format(
					"No KB folder with KB folder ID %s found in group %s",
					parentKBFolder.getKbFolderId(), kbFolder.getGroupId()));
		}

		List<Long> ancestorKBFolderIds =
			parentKBFolder.getAncestorKBFolderIds();

		if (ancestorKBFolderIds.contains(kbFolder.getKbFolderId())) {
			throw new KBFolderParentException(
				String.format(
					"Cannot move KBFolder %s inside its descendant KBFolder %s",
					kbFolder.getKbFolderId(), parentKBFolder.getKbFolderId()));
		}
	}

	private void _validateParent(
			long parentResourceClassNameId, long parentResourcePrimKey)
		throws PortalException {

		long kbFolderClassNameId = _classNameLocalService.getClassNameId(
			KBFolderConstants.getClassName());

		KBFolder parentKBFolder = null;

		if (parentResourceClassNameId == kbFolderClassNameId) {
			if (parentResourcePrimKey ==
					KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				return;
			}

			parentKBFolder = kbFolderPersistence.fetchByPrimaryKey(
				parentResourcePrimKey);
		}

		if (parentKBFolder == null) {
			throw new NoSuchFolderException(
				String.format(
					"No KB folder found with KB folder ID %",
					parentResourcePrimKey));
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}