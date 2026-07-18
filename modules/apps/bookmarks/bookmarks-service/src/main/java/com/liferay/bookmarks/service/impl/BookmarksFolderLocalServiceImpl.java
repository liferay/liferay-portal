/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.exception.FolderNameException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.service.base.BookmarksFolderLocalServiceBaseImpl;
import com.liferay.bookmarks.service.persistence.BookmarksEntryPersistence;
import com.liferay.bookmarks.util.comparator.FolderIdComparator;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.tree.TreeModelTasksAdapter;
import com.liferay.portal.kernel.tree.TreePathUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.service.SocialActivityLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
@Component(
	property = "model.class.name=com.liferay.bookmarks.model.BookmarksFolder",
	service = AopService.class
)
public class BookmarksFolderLocalServiceImpl
	extends BookmarksFolderLocalServiceBaseImpl {

	@Override
	public BookmarksFolder addFolder(
			long userId, long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		// Folder

		User user = _userLocalService.getUser(userId);

		long groupId = serviceContext.getScopeGroupId();

		parentFolderId = _getParentFolderId(groupId, parentFolderId);

		_validate(name);

		long folderId = counterLocalService.increment();

		BookmarksFolder folder = bookmarksFolderPersistence.create(folderId);

		folder.setUuid(serviceContext.getUuid());
		folder.setGroupId(groupId);
		folder.setCompanyId(user.getCompanyId());
		folder.setUserId(user.getUserId());
		folder.setUserName(user.getFullName());
		folder.setParentFolderId(parentFolderId);
		folder.setTreePath(folder.buildTreePath());
		folder.setName(name);
		folder.setDescription(description);
		folder.setExpandoBridgeAttributes(serviceContext);

		folder = bookmarksFolderPersistence.update(folder);

		// Resources

		_resourceLocalService.addModelResources(folder, serviceContext);

		// Asset

		updateAsset(
			userId, folder, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		return folder;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public BookmarksFolder deleteFolder(BookmarksFolder folder)
		throws PortalException {

		return bookmarksFolderLocalService.deleteFolder(folder, true);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public BookmarksFolder deleteFolder(
			BookmarksFolder folder, boolean includeTrashedEntries)
		throws PortalException {

		// Folders

		List<BookmarksFolder> folders = bookmarksFolderPersistence.findByG_P(
			folder.getGroupId(), folder.getFolderId());

		for (BookmarksFolder curFolder : folders) {
			if (includeTrashedEntries ||
				!_trashHelper.isInTrashExplicitly(curFolder)) {

				bookmarksFolderLocalService.deleteFolder(curFolder);
			}
		}

		// Folder

		bookmarksFolderPersistence.remove(folder);

		// Resources

		_resourceLocalService.deleteResource(
			folder, ResourceConstants.SCOPE_INDIVIDUAL);

		// Entries

		_bookmarksEntryLocalService.deleteEntries(
			folder.getGroupId(), folder.getFolderId(), includeTrashedEntries);

		// Asset

		_assetEntryLocalService.deleteEntry(
			BookmarksFolder.class.getName(), folder.getFolderId());

		// Expando

		_expandoRowLocalService.deleteRows(
			folder.getCompanyId(),
			_classNameLocalService.getClassNameId(
				BookmarksFolder.class.getName()),
			folder.getFolderId());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			BookmarksFolder.class.getName(), folder.getFolderId());

		// Subscriptions

		_subscriptionLocalService.deleteSubscriptions(
			folder.getCompanyId(), BookmarksFolder.class.getName(),
			folder.getFolderId());

		// Trash

		if (_trashHelper.isInTrashExplicitly(folder)) {
			_trashEntryLocalService.deleteEntry(
				BookmarksFolder.class.getName(), folder.getFolderId());
		}
		else {
			_trashVersionLocalService.deleteTrashVersion(
				BookmarksFolder.class.getName(), folder.getFolderId());
		}

		return folder;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public BookmarksFolder deleteFolder(long folderId) throws PortalException {
		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		return bookmarksFolderLocalService.deleteFolder(folder);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public BookmarksFolder deleteFolder(
			long folderId, boolean includeTrashedEntries)
		throws PortalException {

		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		return bookmarksFolderLocalService.deleteFolder(
			folder, includeTrashedEntries);
	}

	@Override
	public void deleteFolders(long groupId) throws PortalException {
		List<BookmarksFolder> folders =
			bookmarksFolderPersistence.findByGroupId(groupId);

		for (BookmarksFolder folder : folders) {
			bookmarksFolderLocalService.deleteFolder(folder);
		}
	}

	@Override
	public List<BookmarksFolder> getCompanyFolders(
		long companyId, int start, int end) {

		return bookmarksFolderPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public int getCompanyFoldersCount(long companyId) {
		return bookmarksFolderPersistence.countByCompanyId(companyId);
	}

	@Override
	public BookmarksFolder getFolder(long folderId) throws PortalException {
		return bookmarksFolderPersistence.findByPrimaryKey(folderId);
	}

	@Override
	public List<BookmarksFolder> getFolders(long groupId) {
		return bookmarksFolderPersistence.findByGroupId(groupId);
	}

	@Override
	public List<BookmarksFolder> getFolders(long groupId, long parentFolderId) {
		return bookmarksFolderPersistence.findByG_P(groupId, parentFolderId);
	}

	@Override
	public List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int start, int end) {

		return getFolders(
			groupId, parentFolderId, WorkflowConstants.STATUS_APPROVED, start,
			end);
	}

	@Override
	public List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end) {

		return bookmarksFolderPersistence.findByG_P_S(
			groupId, parentFolderId, status, start, end);
	}

	@Override
	public List<Object> getFoldersAndEntries(long groupId, long folderId) {
		return getFoldersAndEntries(
			groupId, folderId, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return bookmarksFolderFinder.findF_E_ByG_F(
			groupId, folderId, queryDefinition);
	}

	@Override
	public List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status, int start, int end) {

		return getFoldersAndEntries(
			groupId, folderId, status, start, end, null);
	}

	@Override
	public List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return bookmarksFolderFinder.findF_E_ByG_F(
			groupId, folderId, queryDefinition);
	}

	@Override
	public int getFoldersAndEntriesCount(
		long groupId, long folderId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return bookmarksFolderFinder.countF_E_ByG_F(
			groupId, folderId, queryDefinition);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return getFoldersCount(
			groupId, parentFolderId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId, int status) {
		return bookmarksFolderPersistence.countByG_P_S(
			groupId, parentFolderId, status);
	}

	@Override
	public List<BookmarksFolder> getNoAssetFolders() {
		return bookmarksFolderFinder.findByNoAssets();
	}

	@Override
	public void getSubfolderIds(
		List<Long> folderIds, long groupId, long folderId) {

		List<BookmarksFolder> folders = bookmarksFolderPersistence.findByG_P(
			groupId, folderId);

		for (BookmarksFolder folder : folders) {
			folderIds.add(folder.getFolderId());

			getSubfolderIds(
				folderIds, folder.getGroupId(), folder.getFolderId());
		}
	}

	@Override
	public void mergeFolders(long folderId, long parentFolderId)
		throws PortalException {

		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		parentFolderId = _getParentFolderId(folder, parentFolderId);

		if (folderId != parentFolderId) {
			_mergeFolders(folder, parentFolderId);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BookmarksFolder moveFolder(long folderId, long parentFolderId)
		throws PortalException {

		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		if (folder.getParentFolderId() == parentFolderId) {
			return folder;
		}

		folder.setParentFolderId(parentFolderId);
		folder.setTreePath(folder.buildTreePath());

		folder = bookmarksFolderPersistence.update(folder);

		rebuildTree(
			folder.getCompanyId(), folderId, folder.getTreePath(), true);

		return folder;
	}

	@Override
	public BookmarksFolder moveFolderFromTrash(
			long userId, long folderId, long parentFolderId)
		throws PortalException {

		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		if (!folder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(folder)) {
			restoreFolderFromTrash(userId, folderId);
		}
		else {

			// Folder

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				BookmarksFolder.class.getName(), folderId);

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			updateStatus(userId, folder, status);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Folders and entries

			_restoreDependentsFromTrash(
				bookmarksFolderLocalService.getFoldersAndEntries(
					folder.getGroupId(), folder.getFolderId(),
					WorkflowConstants.STATUS_IN_TRASH));
		}

		return bookmarksFolderLocalService.moveFolder(folderId, parentFolderId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BookmarksFolder moveFolderToTrash(long userId, long folderId)
		throws PortalException {

		// Folder

		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		if (folder.isInTrash()) {
			throw new TrashEntryException();
		}

		int oldStatus = folder.getStatus();

		folder = updateStatus(
			userId, folder, WorkflowConstants.STATUS_IN_TRASH);

		// Trash

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, folder.getGroupId(), BookmarksFolder.class.getName(),
			folder.getFolderId(), folder.getUuid(), null, oldStatus, null,
			null);

		// Folders and entries

		_moveDependentsToTrash(
			bookmarksFolderLocalService.getFoldersAndEntries(
				folder.getGroupId(), folder.getFolderId()),
			trashEntry.getEntryId());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", folder.getName());

		_socialActivityLocalService.addActivity(
			userId, folder.getGroupId(), BookmarksFolder.class.getName(),
			folder.getFolderId(), SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		return folder;
	}

	@Override
	public void rebuildTree(long companyId) throws PortalException {
		rebuildTree(
			companyId, BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringPool.SLASH, false);
	}

	@Override
	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException {

		TreePathUtil.rebuildTree(
			companyId, parentFolderId, parentTreePath,
			new TreeModelTasksAdapter<BookmarksFolder>() {

				@Override
				public List<BookmarksFolder> findTreeModels(
					long previousId, long companyId, long parentPrimaryKey,
					int size) {

					return bookmarksFolderPersistence.findByGtF_C_P_NotS(
						previousId, companyId, parentPrimaryKey,
						WorkflowConstants.STATUS_IN_TRASH, QueryUtil.ALL_POS,
						size, FolderIdComparator.getInstance(true));
				}

				@Override
				public void rebuildDependentModelsTreePaths(
						long parentPrimaryKey, String treePath)
					throws PortalException {

					_bookmarksEntryLocalService.setTreePaths(
						parentPrimaryKey, treePath, false);
				}

			});
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BookmarksFolder restoreFolderFromTrash(long userId, long folderId)
		throws PortalException {

		// Folder

		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		if (!folder.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			BookmarksFolder.class.getName(), folderId);

		updateStatus(userId, folder, trashEntry.getStatus());

		// Folders and entries

		_restoreDependentsFromTrash(
			bookmarksFolderLocalService.getFoldersAndEntries(
				folder.getGroupId(), folder.getFolderId(),
				WorkflowConstants.STATUS_IN_TRASH));

		// Trash

		_trashEntryLocalService.deleteEntry(trashEntry.getEntryId());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", folder.getName());

		_socialActivityLocalService.addActivity(
			userId, folder.getGroupId(), BookmarksFolder.class.getName(),
			folder.getFolderId(),
			SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);

		return folder;
	}

	@Override
	public void subscribeFolder(long userId, long groupId, long folderId)
		throws PortalException {

		if (folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folderId = groupId;
		}

		_subscriptionLocalService.addSubscription(
			userId, groupId, BookmarksFolder.class.getName(), folderId);
	}

	@Override
	public void unsubscribeFolder(long userId, long groupId, long folderId)
		throws PortalException {

		if (folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folderId = groupId;
		}

		_subscriptionLocalService.deleteSubscription(
			userId, BookmarksFolder.class.getName(), folderId);
	}

	@Override
	public void updateAsset(
			long userId, BookmarksFolder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, folder.getGroupId(), folder.getCreateDate(),
			folder.getModifiedDate(), BookmarksFolder.class.getName(),
			folder.getFolderId(), folder.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, true, null, null, folder.getCreateDate(), null,
			ContentTypes.TEXT_PLAIN, folder.getName(), folder.getDescription(),
			null, null, null, 0, 0, priority);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BookmarksFolder updateFolder(
			long userId, long folderId, long parentFolderId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException {

		BookmarksFolder folder = bookmarksFolderPersistence.findByPrimaryKey(
			folderId);

		parentFolderId = _getParentFolderId(folder, parentFolderId);

		_validate(name);

		long oldParentFolderId = folder.getParentFolderId();

		if (oldParentFolderId != parentFolderId) {
			folder.setParentFolderId(parentFolderId);
			folder.setTreePath(folder.buildTreePath());
		}

		folder.setName(name);
		folder.setDescription(description);
		folder.setExpandoBridgeAttributes(serviceContext);

		folder = bookmarksFolderPersistence.update(folder);

		// Asset

		updateAsset(
			userId, folder, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		if (oldParentFolderId != parentFolderId) {
			rebuildTree(
				folder.getCompanyId(), folderId, folder.getTreePath(), true);
		}

		return folder;
	}

	@Override
	public BookmarksFolder updateStatus(
			long userId, BookmarksFolder folder, int status)
		throws PortalException {

		// Folder

		User user = _userLocalService.getUser(userId);

		folder.setStatus(status);
		folder.setStatusByUserId(userId);
		folder.setStatusByUserName(user.getFullName());
		folder.setStatusDate(new Date());

		folder = bookmarksFolderPersistence.update(folder);

		// Asset

		if (status == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateVisible(
				BookmarksFolder.class.getName(), folder.getFolderId(), true);
		}
		else if (status == WorkflowConstants.STATUS_IN_TRASH) {
			_assetEntryLocalService.updateVisible(
				BookmarksFolder.class.getName(), folder.getFolderId(), false);
		}

		// Indexer

		Indexer<BookmarksFolder> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(BookmarksFolder.class);

		indexer.reindex(folder);

		return folder;
	}

	private long _getParentFolderId(
		BookmarksFolder folder, long parentFolderId) {

		if (parentFolderId ==
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return parentFolderId;
		}

		if (folder.getFolderId() == parentFolderId) {
			return folder.getParentFolderId();
		}

		BookmarksFolder parentFolder =
			bookmarksFolderPersistence.fetchByPrimaryKey(parentFolderId);

		if ((parentFolder == null) ||
			(folder.getGroupId() != parentFolder.getGroupId())) {

			return folder.getParentFolderId();
		}

		List<Long> subfolderIds = new ArrayList<>();

		getSubfolderIds(
			subfolderIds, folder.getGroupId(), folder.getFolderId());

		if (subfolderIds.contains(parentFolderId)) {
			return folder.getParentFolderId();
		}

		return parentFolderId;
	}

	private long _getParentFolderId(long groupId, long parentFolderId) {
		if (parentFolderId !=
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			BookmarksFolder parentFolder =
				bookmarksFolderPersistence.fetchByPrimaryKey(parentFolderId);

			if ((parentFolder == null) ||
				(groupId != parentFolder.getGroupId())) {

				parentFolderId =
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}
		}

		return parentFolderId;
	}

	private void _mergeFolders(BookmarksFolder fromFolder, long toFolderId)
		throws PortalException {

		List<BookmarksFolder> folders = bookmarksFolderPersistence.findByG_P(
			fromFolder.getGroupId(), fromFolder.getFolderId());

		for (BookmarksFolder folder : folders) {
			_mergeFolders(folder, toFolderId);
		}

		List<BookmarksEntry> entries = _bookmarksEntryPersistence.findByG_F(
			fromFolder.getGroupId(), fromFolder.getFolderId());

		for (BookmarksEntry entry : entries) {
			entry.setFolderId(toFolderId);
			entry.setTreePath(entry.buildTreePath());

			entry = _bookmarksEntryPersistence.update(entry);

			Indexer<BookmarksEntry> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(BookmarksEntry.class);

			indexer.reindex(entry);
		}

		bookmarksFolderLocalService.deleteFolder(fromFolder);
	}

	private void _moveDependentsToTrash(
			List<Object> foldersAndEntries, long trashEntryId)
		throws PortalException {

		for (Object object : foldersAndEntries) {
			if (object instanceof BookmarksEntry) {

				// Entry

				BookmarksEntry entry = (BookmarksEntry)object;

				if (entry.isInTrash()) {
					continue;
				}

				int oldStatus = entry.getStatus();

				entry.setStatus(WorkflowConstants.STATUS_IN_TRASH);

				entry = _bookmarksEntryPersistence.update(entry);

				// Trash

				int status = oldStatus;

				if (oldStatus == WorkflowConstants.STATUS_PENDING) {
					status = WorkflowConstants.STATUS_DRAFT;
				}

				if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
					_trashVersionLocalService.addTrashVersion(
						trashEntryId, BookmarksEntry.class.getName(),
						entry.getEntryId(), status, null);
				}

				// Asset

				_assetEntryLocalService.updateVisible(
					BookmarksEntry.class.getName(), entry.getEntryId(), false);

				// Indexer

				Indexer<BookmarksEntry> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						BookmarksEntry.class);

				indexer.reindex(entry);
			}
			else if (object instanceof BookmarksFolder) {

				// Folder

				BookmarksFolder folder = (BookmarksFolder)object;

				if (folder.isInTrash()) {
					continue;
				}

				int oldStatus = folder.getStatus();

				folder.setStatus(WorkflowConstants.STATUS_IN_TRASH);

				folder = bookmarksFolderPersistence.update(folder);

				// Trash

				if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
					_trashVersionLocalService.addTrashVersion(
						trashEntryId, BookmarksFolder.class.getName(),
						folder.getFolderId(), oldStatus, null);
				}

				// Folders and entries

				List<Object> curFoldersAndEntries = getFoldersAndEntries(
					folder.getGroupId(), folder.getFolderId());

				_moveDependentsToTrash(curFoldersAndEntries, trashEntryId);

				// Asset

				_assetEntryLocalService.updateVisible(
					BookmarksFolder.class.getName(), folder.getFolderId(),
					false);

				// Indexer

				Indexer<BookmarksFolder> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						BookmarksFolder.class);

				indexer.reindex(folder);
			}
		}
	}

	private void _restoreDependentsFromTrash(List<Object> foldersAndEntries)
		throws PortalException {

		for (Object object : foldersAndEntries) {
			if (object instanceof BookmarksEntry) {

				// Entry

				BookmarksEntry entry = (BookmarksEntry)object;

				if (!_trashHelper.isInTrashImplicitly(entry)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						BookmarksEntry.class.getName(), entry.getEntryId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				entry.setStatus(oldStatus);

				entry = _bookmarksEntryPersistence.update(entry);

				// Trash

				if (trashVersion != null) {
					_trashVersionLocalService.deleteTrashVersion(trashVersion);
				}

				// Asset

				if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
					_assetEntryLocalService.updateVisible(
						BookmarksEntry.class.getName(), entry.getEntryId(),
						true);
				}

				// Indexer

				Indexer<BookmarksEntry> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						BookmarksEntry.class);

				indexer.reindex(entry);
			}
			else if (object instanceof BookmarksFolder) {

				// Folder

				BookmarksFolder folder = (BookmarksFolder)object;

				if (!_trashHelper.isInTrashImplicitly(folder)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						BookmarksFolder.class.getName(), folder.getFolderId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				folder.setStatus(oldStatus);

				folder = bookmarksFolderPersistence.update(folder);

				// Folders and entries

				List<Object> curFoldersAndEntries = getFoldersAndEntries(
					folder.getGroupId(), folder.getFolderId(),
					WorkflowConstants.STATUS_IN_TRASH);

				_restoreDependentsFromTrash(curFoldersAndEntries);

				// Trash

				if (trashVersion != null) {
					_trashVersionLocalService.deleteTrashVersion(trashVersion);
				}

				// Asset

				_assetEntryLocalService.updateVisible(
					BookmarksFolder.class.getName(), folder.getFolderId(),
					true);

				// Indexer

				Indexer<BookmarksFolder> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						BookmarksFolder.class);

				indexer.reindex(folder);
			}
		}
	}

	private void _validate(String name) throws PortalException {
		if (Validator.isNull(name) || name.contains("\\\\") ||
			name.contains("//")) {

			throw new FolderNameException();
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private BookmarksEntryLocalService _bookmarksEntryLocalService;

	@Reference
	private BookmarksEntryPersistence _bookmarksEntryPersistence;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SocialActivityLocalService _socialActivityLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}