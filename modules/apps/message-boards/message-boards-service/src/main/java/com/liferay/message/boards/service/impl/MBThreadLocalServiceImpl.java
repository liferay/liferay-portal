/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBThreadConstants;
import com.liferay.message.boards.exception.SplitThreadException;
import com.liferay.message.boards.internal.util.MBMessageUtil;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBTreeWalker;
import com.liferay.message.boards.model.impl.MBTreeWalkerImpl;
import com.liferay.message.boards.service.MBDiscussionLocalService;
import com.liferay.message.boards.service.base.MBThreadLocalServiceBaseImpl;
import com.liferay.message.boards.service.persistence.MBCategoryPersistence;
import com.liferay.message.boards.service.persistence.MBDiscussionPersistence;
import com.liferay.message.boards.service.persistence.MBMessageFinder;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.util.comparator.MessageCreateDateComparator;
import com.liferay.message.boards.util.comparator.MessageThreadComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.DateOverrideIncrement;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ExceptionRetryAcceptor;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBThread",
	service = AopService.class
)
public class MBThreadLocalServiceImpl extends MBThreadLocalServiceBaseImpl {

	@Override
	public MBThread addThread(
			long categoryId, MBMessage message, ServiceContext serviceContext)
		throws PortalException {

		// Thread

		long threadId = message.getThreadId();

		if (!message.isRoot() || (threadId <= 0)) {
			threadId = counterLocalService.increment();
		}

		MBThread thread = mbThreadPersistence.create(threadId);

		thread.setUuid(serviceContext.getUuid());
		thread.setGroupId(message.getGroupId());
		thread.setCompanyId(message.getCompanyId());
		thread.setUserId(message.getUserId());
		thread.setUserName(message.getUserName());
		thread.setCategoryId(categoryId);
		thread.setRootMessageId(message.getMessageId());
		thread.setRootMessageUserId(message.getUserId());
		thread.setTitle(message.getSubject());

		if (message.isAnonymous()) {
			thread.setLastPostByUserId(0);
		}
		else {
			thread.setLastPostByUserId(message.getUserId());
		}

		thread.setLastPostDate(message.getModifiedDate());

		if (message.getPriority() != MBThreadConstants.PRIORITY_NOT_GIVEN) {
			thread.setPriority(message.getPriority());
		}

		thread.setStatus(message.getStatus());
		thread.setStatusByUserId(message.getStatusByUserId());
		thread.setStatusByUserName(message.getStatusByUserName());
		thread.setStatusDate(message.getStatusDate());

		thread = mbThreadPersistence.update(thread);

		// Asset

		if (categoryId >= 0) {
			_assetEntryLocalService.updateEntry(
				message.getUserId(), message.getGroupId(),
				thread.getStatusDate(), thread.getLastPostDate(),
				MBThread.class.getName(), thread.getThreadId(),
				thread.getUuid(), 0, new long[0], new String[0], true, false,
				null, null, thread.getStatusDate(), null, null,
				String.valueOf(thread.getRootMessageId()), null, null, null,
				null, 0, 0, serviceContext.getAssetPriority());
		}

		return thread;
	}

	@Override
	public void deleteThread(long threadId) throws PortalException {
		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		mbThreadLocalService.deleteThread(thread);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deleteThread(MBThread thread) throws PortalException {
		MBMessage rootMessage = _mbMessagePersistence.findByPrimaryKey(
			thread.getRootMessageId());

		// Indexer

		Indexer<MBMessage> messageIndexer =
			IndexerRegistryUtil.nullSafeGetIndexer(MBMessage.class);

		// Attachments

		long folderId = thread.getAttachmentsFolderId();

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			PortletFileRepositoryUtil.deletePortletFolder(folderId);
		}

		// Discussion

		MBDiscussion discussion = _mbDiscussionPersistence.fetchByThreadId(
			thread.getThreadId());

		if (discussion != null) {
			_mbDiscussionLocalService.deleteMBDiscussion(
				discussion.getDiscussionId());
		}

		// Subscriptions

		_subscriptionLocalService.deleteSubscriptions(
			thread.getCompanyId(), MBThread.class.getName(),
			thread.getThreadId());

		// Messages

		List<MBMessage> messages = _mbMessagePersistence.findByThreadId(
			thread.getThreadId());

		for (MBMessage message : messages) {

			// Ratings

			_ratingsStatsLocalService.deleteStats(
				message.getWorkflowClassName(), message.getMessageId());

			// Asset

			_assetEntryLocalService.deleteEntry(
				message.getWorkflowClassName(), message.getMessageId());

			// Expando

			expandoRowLocalService.deleteRows(message.getMessageId());

			// Resources

			if (!message.isDiscussion()) {
				_resourceLocalService.deleteResource(
					message.getCompanyId(), message.getWorkflowClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL, message.getMessageId());
			}

			// Message

			_mbMessagePersistence.remove(message);

			// Indexer

			messageIndexer.delete(message);

			// Workflow

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				message.getCompanyId(), message.getGroupId(),
				message.getWorkflowClassName(), message.getMessageId());
		}

		// Asset

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			MBThread.class.getName(), thread.getThreadId());

		if (assetEntry != null) {
			assetEntry.setTitle(rootMessage.getSubject());

			_assetEntryLocalService.updateAssetEntry(assetEntry);
		}

		_assetEntryLocalService.deleteEntry(
			MBThread.class.getName(), thread.getThreadId());

		// View count

		_viewCountManager.deleteViewCount(
			thread.getCompanyId(),
			_classNameLocalService.getClassNameId(MBThread.class),
			thread.getThreadId());

		// Indexer

		Indexer<MBThread> threadIndexer =
			IndexerRegistryUtil.nullSafeGetIndexer(MBThread.class);

		threadIndexer.delete(thread);

		// Thread

		mbThreadPersistence.remove(thread);
	}

	@Override
	public void deleteThreads(long groupId, long categoryId)
		throws PortalException {

		mbThreadLocalService.deleteThreads(groupId, categoryId, true);
	}

	@Override
	public void deleteThreads(
			long groupId, long categoryId, boolean includeTrashedEntries)
		throws PortalException {

		List<MBThread> threads = mbThreadPersistence.findByG_C(
			groupId, categoryId);

		for (MBThread thread : threads) {
			if (includeTrashedEntries ||
				!_trashHelper.isInTrashExplicitly(thread)) {

				mbThreadLocalService.deleteThread(thread);
			}
		}

		if (mbThreadPersistence.countByGroupId(groupId) == 0) {
			PortletFileRepositoryUtil.deletePortletRepository(
				groupId, MBConstants.SERVICE_NAME);
		}
	}

	@Override
	public MBThread fetchThread(long threadId) {
		return mbThreadPersistence.fetchByPrimaryKey(threadId);
	}

	@Override
	public int getCategoryThreadsCount(
		long groupId, long categoryId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbThreadPersistence.countByG_C(groupId, categoryId);
		}

		return mbThreadPersistence.countByG_C_S(groupId, categoryId, status);
	}

	@Override
	public List<MBThread> getGroupThreads(
		long groupId, long userId, boolean subscribed, boolean includeAnonymous,
		QueryDefinition<MBThread> queryDefinition) {

		if (userId <= 0) {
			return getGroupThreads(groupId, queryDefinition);
		}

		if (subscribed) {
			return mbThreadFinder.findByS_G_U_C(
				groupId, userId, null, queryDefinition);
		}

		if (includeAnonymous) {
			return mbThreadFinder.findByG_U_C(
				groupId, userId, null, queryDefinition);
		}

		return mbThreadFinder.findByG_U_C_A(
			groupId, userId, null, false, queryDefinition);
	}

	@Override
	public List<MBThread> getGroupThreads(
		long groupId, long userId, boolean subscribed,
		QueryDefinition<MBThread> queryDefinition) {

		return getGroupThreads(
			groupId, userId, subscribed, true, queryDefinition);
	}

	@Override
	public List<MBThread> getGroupThreads(
		long groupId, long userId, QueryDefinition<MBThread> queryDefinition) {

		return getGroupThreads(groupId, userId, false, queryDefinition);
	}

	@Override
	public List<MBThread> getGroupThreads(
		long groupId, QueryDefinition<MBThread> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return mbThreadPersistence.findByG_NotC_NotS(
				groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
				queryDefinition.getStatus(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}

		return mbThreadPersistence.findByG_NotC_S(
			groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			queryDefinition.getStatus(), queryDefinition.getStart(),
			queryDefinition.getEnd());
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, long userId, boolean subscribed, boolean includeAnonymous,
		QueryDefinition<MBThread> queryDefinition) {

		if (userId <= 0) {
			return getGroupThreadsCount(groupId, queryDefinition);
		}

		if (subscribed) {
			return mbThreadFinder.countByS_G_U_C(
				groupId, userId, null, queryDefinition);
		}

		if (includeAnonymous) {
			return mbThreadFinder.countByG_U_C(
				groupId, userId, null, queryDefinition);
		}

		return mbThreadFinder.countByG_U_C_A(
			groupId, userId, null, false, queryDefinition);
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, long userId, boolean subscribed,
		QueryDefinition<MBThread> queryDefinition) {

		return getGroupThreadsCount(
			groupId, userId, subscribed, true, queryDefinition);
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, long userId, QueryDefinition<MBThread> queryDefinition) {

		return getGroupThreadsCount(groupId, userId, false, queryDefinition);
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, QueryDefinition<MBThread> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return mbThreadPersistence.countByG_NotC_NotS(
				groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
				queryDefinition.getStatus());
		}

		return mbThreadPersistence.countByG_NotC_S(
			groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			queryDefinition.getStatus());
	}

	@Override
	public int getMessageCount(long threadId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return _mbMessagePersistence.countByThreadId(threadId);
		}

		return _mbMessagePersistence.countByT_S(threadId, status);
	}

	@Override
	public List<MBThread> getPriorityThreads(long categoryId, double priority)
		throws PortalException {

		return getPriorityThreads(categoryId, priority, false);
	}

	@Override
	public List<MBThread> getPriorityThreads(
			long categoryId, double priority, boolean inherit)
		throws PortalException {

		if (!inherit) {
			return mbThreadPersistence.findByC_P(categoryId, priority);
		}

		List<MBThread> threads = new ArrayList<>();

		while ((categoryId != MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
			   (categoryId != MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			threads.addAll(
				0, mbThreadPersistence.findByC_P(categoryId, priority));

			MBCategory category = _mbCategoryPersistence.findByPrimaryKey(
				categoryId);

			categoryId = category.getParentCategoryId();
		}

		return threads;
	}

	@Override
	public MBThread getThread(long threadId) throws PortalException {
		return mbThreadPersistence.findByPrimaryKey(threadId);
	}

	@Override
	public List<MBThread> getThreads(
		long groupId, long categoryId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbThreadPersistence.findByG_C(
				groupId, categoryId, start, end);
		}

		return mbThreadPersistence.findByG_C_S(
			groupId, categoryId, status, start, end);
	}

	@Override
	public int getThreadsCount(long groupId, long categoryId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbThreadPersistence.countByG_C(groupId, categoryId);
		}

		return mbThreadPersistence.countByG_C_S(groupId, categoryId, status);
	}

	@Override
	public boolean hasAnswerMessage(long threadId) {
		int count = _mbMessagePersistence.countByT_A(threadId, true);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void incrementViewCounter(long threadId, int increment) {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		MBThread thread = mbThreadPersistence.fetchByPrimaryKey(threadId);

		if (thread == null) {
			return;
		}

		_viewCountManager.incrementViewCount(
			thread.getCompanyId(),
			_classNameLocalService.getClassNameId(MBThread.class),
			thread.getThreadId(), increment);
	}

	@Override
	public void moveDependentsToTrash(
			long groupId, long threadId, long trashEntryId)
		throws PortalException {

		Set<Long> userIds = new HashSet<>();

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		List<MBMessage> messages = _mbMessagePersistence.findByThreadId(
			threadId);

		for (MBMessage message : messages) {

			// Message

			if (message.isDiscussion()) {
				continue;
			}

			int oldStatus = message.getStatus();

			message.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			message = _mbMessagePersistence.update(message);

			userIds.add(message.getUserId());

			// Trash

			int status = oldStatus;

			if (oldStatus == WorkflowConstants.STATUS_PENDING) {
				status = WorkflowConstants.STATUS_DRAFT;
			}

			if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
				_trashVersionLocalService.addTrashVersion(
					trashEntryId, MBMessage.class.getName(),
					message.getMessageId(), status, null);
			}

			// Asset

			if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
				_assetEntryLocalService.updateVisible(
					MBMessage.class.getName(), message.getMessageId(), false);
			}

			// Attachments

			for (FileEntry fileEntry : message.getAttachmentsFileEntries()) {
				PortletFileRepositoryUtil.movePortletFileEntryToTrash(
					thread.getStatusByUserId(), fileEntry.getFileEntryId());
			}

			// Indexer

			Indexer<MBMessage> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				MBMessage.class);

			indexer.reindex(message);

			// Workflow

			if (oldStatus == WorkflowConstants.STATUS_PENDING) {
				_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
					message.getCompanyId(), message.getGroupId(),
					MBMessage.class.getName(), message.getMessageId());
			}
		}
	}

	@Override
	public MBThread moveThread(long groupId, long categoryId, long threadId)
		throws PortalException {

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		long oldCategoryId = thread.getCategoryId();

		// Thread

		thread.setCategoryId(categoryId);

		thread = mbThreadPersistence.update(thread);

		// Messages

		List<MBMessage> messages = _mbMessagePersistence.findByG_C_T(
			groupId, oldCategoryId, thread.getThreadId());

		for (MBMessage message : messages) {
			message.setCategoryId(categoryId);

			message = _mbMessagePersistence.update(message);

			// Indexer

			if (!message.isDiscussion()) {
				Indexer<MBMessage> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(MBMessage.class);

				indexer.reindex(message);
			}
		}

		// Indexer

		Indexer<MBThread> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			MBThread.class);

		indexer.reindex(thread);

		return thread;
	}

	@Override
	public MBThread moveThreadFromTrash(
			long userId, long categoryId, long threadId)
		throws PortalException {

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		if (!thread.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(thread)) {
			restoreThreadFromTrash(userId, threadId);
		}
		else {

			// Thread

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				MBThread.class.getName(), thread.getThreadId());

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			updateStatus(userId, threadId, status);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Messages

			mbThreadLocalService.restoreDependentsFromTrash(
				thread.getGroupId(), threadId);
		}

		return moveThread(thread.getGroupId(), categoryId, threadId);
	}

	@Override
	public void moveThreadsToTrash(long groupId, long userId)
		throws PortalException {

		List<MBThread> threads = mbThreadPersistence.findByGroupId(groupId);

		for (MBThread thread : threads) {
			moveThreadToTrash(userId, thread);
		}
	}

	@Override
	public MBThread moveThreadToTrash(long userId, long threadId)
		throws PortalException {

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		return moveThreadToTrash(userId, thread);
	}

	@Override
	public MBThread moveThreadToTrash(long userId, MBThread thread)
		throws PortalException {

		// Thread

		if (thread.isInTrash()) {
			throw new TrashEntryException();
		}

		if (thread.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			return thread;
		}

		int oldStatus = thread.getStatus();

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			thread.setStatus(WorkflowConstants.STATUS_DRAFT);

			thread = mbThreadPersistence.update(thread);
		}

		thread = updateStatus(
			userId, thread.getThreadId(), WorkflowConstants.STATUS_IN_TRASH);

		// Trash

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, thread.getGroupId(), MBThread.class.getName(),
			thread.getThreadId(), thread.getUuid(), null, oldStatus, null,
			null);

		// Messages

		mbThreadLocalService.moveDependentsToTrash(
			thread.getGroupId(), thread.getThreadId(), trashEntry.getEntryId());

		// Social

		MBMessage message = _mbMessagePersistence.findByPrimaryKey(
			thread.getRootMessageId());

		JSONObject extraDataJSONObject = JSONUtil.put(
			"rootMessageId", thread.getRootMessageId()
		).put(
			"title", message.getSubject()
		);

		SocialActivityManagerUtil.addActivity(
			userId, thread, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		return thread;
	}

	@Override
	public void restoreDependentsFromTrash(long groupId, long threadId)
		throws PortalException {

		Set<Long> userIds = new HashSet<>();

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		List<MBMessage> messages = _mbMessagePersistence.findByThreadId(
			threadId);

		for (MBMessage message : messages) {

			// Message

			if (message.isDiscussion()) {
				continue;
			}

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				MBMessage.class.getName(), message.getMessageId());

			int oldStatus = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				oldStatus = trashVersion.getStatus();
			}

			message.setStatus(oldStatus);

			message = _mbMessagePersistence.update(message);

			userIds.add(message.getUserId());

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Asset

			if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
				_assetEntryLocalService.updateVisible(
					MBMessage.class.getName(), message.getMessageId(), true);
			}

			// Attachments

			for (FileEntry fileEntry :
					message.getDeletedAttachmentsFileEntries()) {

				PortletFileRepositoryUtil.restorePortletFileEntryFromTrash(
					thread.getStatusByUserId(), fileEntry.getFileEntryId());
			}

			// Indexer

			Indexer<MBMessage> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				MBMessage.class);

			indexer.reindex(message);
		}
	}

	@Override
	public void restoreThreadFromTrash(long userId, long threadId)
		throws PortalException {

		// Thread

		MBThread thread = getThread(threadId);

		if (!thread.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (thread.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			return;
		}

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			MBThread.class.getName(), threadId);

		updateStatus(userId, threadId, trashEntry.getStatus());

		// Messages

		mbThreadLocalService.restoreDependentsFromTrash(
			thread.getGroupId(), threadId);

		// Trash

		_trashEntryLocalService.deleteEntry(trashEntry.getEntryId());

		// Social

		MBMessage message = _mbMessagePersistence.findByPrimaryKey(
			thread.getRootMessageId());

		JSONObject extraDataJSONObject = JSONUtil.put(
			"rootMessageId", thread.getRootMessageId()
		).put(
			"title", message.getSubject()
		);

		SocialActivityManagerUtil.addActivity(
			userId, thread, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);
	}

	@Override
	public Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws PortalException {

		return search(groupId, userId, creatorUserId, 0, 0, status, start, end);
	}

	@Override
	public Hits search(
			long groupId, long userId, long creatorUserId, long startDate,
			long endDate, int status, int start, int end)
		throws PortalException {

		Indexer<MBThread> indexer = IndexerRegistryUtil.getIndexer(
			MBThread.class.getName());

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(Field.STATUS, status);

		if (endDate > 0) {
			searchContext.setAttribute("endDate", endDate);
		}

		searchContext.setAttribute("paginationType", "none");

		if (creatorUserId > 0) {
			searchContext.setAttribute(
				"participantUserId", String.valueOf(creatorUserId));
		}

		if (startDate > 0) {
			searchContext.setAttribute("startDate", startDate);
		}

		Group group = _groupLocalService.getGroup(groupId);

		searchContext.setCompanyId(group.getCompanyId());

		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setSorts(new Sort("lastPostDate", true));
		searchContext.setStart(start);
		searchContext.setUserId(userId);

		return indexer.search(searchContext);
	}

	@Override
	public MBThread splitThread(
			long userId, long messageId, String subject,
			ServiceContext serviceContext)
		throws PortalException {

		MBMessage message = _mbMessagePersistence.findByPrimaryKey(messageId);

		if (message.isRoot()) {
			throw new SplitThreadException(
				"Unable to split message " + messageId +
					" because it is a root message");
		}

		MBCategory category = message.getCategory();

		MBThread oldThread = message.getThread();

		MBMessage rootMessage = _mbMessagePersistence.findByPrimaryKey(
			oldThread.getRootMessageId());

		long oldAttachmentsFolderId = message.getAttachmentsFolderId();

		// Message flags

		MBMessageUtil.updateAnswer(_mbMessagePersistence, message, false, true);

		// Create new thread

		MBThread thread = addThread(
			message.getCategoryId(), message, serviceContext);

		oldThread = mbThreadPersistence.update(oldThread);

		// Update messages

		Indexer<MBMessage> messageIndexer = _indexerRegistry.nullSafeGetIndexer(
			MBMessage.class);

		if (Validator.isNotNull(subject)) {
			List<MBMessage> messages = MBMessageUtil.getThreadMessages(
				_mbMessagePersistence, _mbMessageFinder, userId,
				message.getThreadId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new MessageThreadComparator());

			MBTreeWalker treeWalker = new MBTreeWalkerImpl(messages);

			int[] range = treeWalker.getChildrenRange(message);

			for (int i = range[0]; i < range[1]; i++) {
				MBMessage curMessage = messages.get(i);

				String oldSubject = message.getSubject();
				String curSubject = curMessage.getSubject();

				if (oldSubject.startsWith(
						MBMessageConstants.MESSAGE_SUBJECT_PREFIX_RE)) {

					curSubject = StringUtil.replace(
						curSubject, rootMessage.getSubject(), subject);
				}
				else {
					curSubject = StringUtil.replace(
						curSubject, oldSubject, subject);
				}

				curMessage.setSubject(curSubject);

				messageIndexer.reindex(
					_mbMessagePersistence.update(curMessage));
			}

			message.setSubject(subject);

			thread.setTitle(subject);

			thread = mbThreadLocalService.updateMBThread(thread);
		}

		message.setThreadId(thread.getThreadId());
		message.setRootMessageId(thread.getRootMessageId());
		message.setParentMessageId(0);

		messageIndexer.reindex(_mbMessagePersistence.update(message));

		// Attachments

		_moveAttachmentsFolders(
			message, oldAttachmentsFolderId, oldThread, thread, serviceContext);

		// Indexer

		if (!message.isDiscussion()) {
			messageIndexer.reindex(message);
		}

		// Update children

		_moveChildrenMessages(message, category, oldThread.getThreadId());

		// Indexer

		Indexer<MBThread> threadIndexer =
			IndexerRegistryUtil.nullSafeGetIndexer(MBThread.class);

		threadIndexer.reindex(oldThread);

		threadIndexer.reindex(message.getThread());

		return thread;
	}

	@BufferedIncrement(
		configuration = "MBThread", incrementClass = DateOverrideIncrement.class
	)
	@Override
	@Retry(
		acceptor = ExceptionRetryAcceptor.class,
		properties = {
			@Property(
				name = ExceptionRetryAcceptor.EXCEPTION_NAME,
				value = "org.hibernate.StaleObjectStateException"
			)
		}
	)
	public void updateLastPostDate(long threadId, Date lastPostDate) {
		MBThread thread = mbThreadPersistence.fetchByPrimaryKey(threadId);

		if (thread == null) {
			return;
		}

		MBMessage message = _mbMessagePersistence.fetchByT_S_First(
			threadId, WorkflowConstants.STATUS_APPROVED,
			MessageCreateDateComparator.getInstance(false));

		if ((message == null) || message.isAnonymous()) {
			thread.setLastPostByUserId(0);
		}
		else {
			thread.setLastPostByUserId(message.getUserId());
		}

		thread.setLastPostDate(lastPostDate);

		mbThreadPersistence.update(thread);
	}

	@Override
	public void updateQuestion(long threadId, boolean question)
		throws PortalException {

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		if (thread.isQuestion() == question) {
			return;
		}

		thread.setQuestion(question);

		thread = mbThreadPersistence.update(thread);

		MBMessage message = _mbMessagePersistence.findByPrimaryKey(
			thread.getRootMessageId());

		if (!question) {
			MBMessageUtil.updateAnswer(
				_mbMessagePersistence, message, false, true);
		}

		Indexer<MBMessage> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			MBMessage.class);

		indexer.reindex(message);
	}

	@Override
	public MBThread updateStatus(long userId, long threadId, int status)
		throws PortalException {

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		// Thread

		User user = _userLocalService.getUser(userId);

		thread.setStatus(status);
		thread.setStatusByUserId(user.getUserId());
		thread.setStatusByUserName(user.getFullName());
		thread.setStatusDate(new Date());

		thread = mbThreadPersistence.update(thread);

		// Indexer

		Indexer<MBThread> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			MBThread.class);

		indexer.reindex(thread);

		return thread;
	}

	@Reference
	protected ExpandoRowLocalService expandoRowLocalService;

	private void _moveAttachmentsFolders(
			MBMessage message, long oldAttachmentsFolderId, MBThread oldThread,
			MBThread newThread, ServiceContext serviceContext)
		throws PortalException {

		if (oldAttachmentsFolderId !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			Folder newThreadFolder = newThread.addAttachmentsFolder();

			PortletFileRepositoryUtil.movePortletFolder(
				message.getGroupId(), message.getUserId(),
				oldAttachmentsFolderId, newThreadFolder.getFolderId(),
				serviceContext);
		}

		List<MBMessage> childMessages = _mbMessagePersistence.findByT_P(
			oldThread.getThreadId(), message.getMessageId());

		for (MBMessage childMessage : childMessages) {
			_moveAttachmentsFolders(
				childMessage, childMessage.getAttachmentsFolderId(), oldThread,
				newThread, serviceContext);
		}
	}

	private void _moveChildrenMessages(
			MBMessage parentMessage, MBCategory category, long oldThreadId)
		throws PortalException {

		Indexer<MBMessage> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			MBMessage.class);

		List<MBMessage> messages = _mbMessagePersistence.findByT_P(
			oldThreadId, parentMessage.getMessageId());

		for (MBMessage message : messages) {
			message.setCategoryId(parentMessage.getCategoryId());
			message.setThreadId(parentMessage.getThreadId());
			message.setRootMessageId(parentMessage.getRootMessageId());

			indexer.reindex(_mbMessagePersistence.update(message));

			_moveChildrenMessages(message, category, oldThreadId);
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private MBCategoryPersistence _mbCategoryPersistence;

	@Reference
	private MBDiscussionLocalService _mbDiscussionLocalService;

	@Reference
	private MBDiscussionPersistence _mbDiscussionPersistence;

	@Reference
	private MBMessageFinder _mbMessageFinder;

	@Reference
	private MBMessagePersistence _mbMessagePersistence;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

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

	@Reference
	private ViewCountManager _viewCountManager;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}