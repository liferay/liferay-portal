/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.exception.LockedThreadException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.base.MBThreadServiceBaseImpl;
import com.liferay.message.boards.service.persistence.MBMessageFinder;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.service.persistence.impl.constants.MBPersistenceConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Deepak Gothe
 * @author Mika Koivisto
 * @author Shuyang Zhou
 */
@Component(
	property = {
		"json.web.service.context.name=mb",
		"json.web.service.context.path=MBThread"
	},
	service = AopService.class
)
public class MBThreadServiceImpl extends MBThreadServiceBaseImpl {

	@Override
	public void deleteThread(long threadId) throws PortalException {
		if (_lockManager.isLocked(MBThread.class.getName(), threadId)) {
			throw new LockedThreadException(
				StringBundler.concat(
					"Thread is locked for class name ",
					MBThread.class.getName(), " and class PK ", threadId));
		}

		List<MBMessage> messages = _mbMessageLocalService.getThreadMessages(
			threadId, WorkflowConstants.STATUS_ANY, null);

		for (MBMessage message : messages) {
			_messageModelResourcePermission.check(
				getPermissionChecker(), message.getMessageId(),
				ActionKeys.DELETE);
		}

		mbThreadLocalService.deleteThread(threadId);
	}

	@Override
	public List<MBThread> getGroupThreads(
			long groupId, long userId, Date modifiedDate,
			boolean includeAnonymous, int status, int start, int end)
		throws PortalException {

		if (!_inlineSQLHelper.isEnabled(groupId)) {
			QueryDefinition<MBThread> queryDefinition = new QueryDefinition<>(
				status, start, end, null);

			if (includeAnonymous) {
				return mbThreadFinder.findByG_U_LPD(
					groupId, userId, modifiedDate, queryDefinition);
			}

			return mbThreadFinder.findByG_U_LPD_A(
				groupId, userId, modifiedDate, false, queryDefinition);
		}

		long[] categoryIds = _mbCategoryService.getCategoryIds(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		if (categoryIds.length == 0) {
			return Collections.emptyList();
		}

		List<Long> threadIds = null;

		if (includeAnonymous) {
			threadIds = _mbMessageFinder.filterFindByG_U_MD_C_S(
				groupId, userId, modifiedDate, categoryIds, status, start, end);
		}
		else {
			threadIds = _mbMessageFinder.filterFindByG_U_MD_C_A_S(
				groupId, userId, modifiedDate, categoryIds, false, status,
				start, end);
		}

		return TransformUtil.transform(
			threadIds,
			threadId -> mbThreadPersistence.findByPrimaryKey(threadId));
	}

	@Override
	public List<MBThread> getGroupThreads(
			long groupId, long userId, Date modifiedDate, int status, int start,
			int end)
		throws PortalException {

		return mbThreadService.getGroupThreads(
			groupId, userId, modifiedDate, true, status, start, end);
	}

	@Override
	public List<MBThread> getGroupThreads(
			long groupId, long userId, int status, boolean subscribed,
			boolean includeAnonymous, int start, int end)
		throws PortalException {

		if (!_inlineSQLHelper.isEnabled(groupId)) {
			return _getGroupThreads(
				groupId, userId, status, subscribed, includeAnonymous, start,
				end);
		}

		long[] categoryIds = _mbCategoryService.getCategoryIds(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		if (categoryIds.length == 0) {
			return Collections.emptyList();
		}

		List<Long> threadIds = null;

		if (userId <= 0) {
			threadIds = _mbMessageFinder.filterFindByG_U_C_S(
				groupId, 0, categoryIds, status, start, end);
		}
		else {
			if (subscribed) {
				QueryDefinition<MBThread> queryDefinition =
					new QueryDefinition<>(status, start, end, null);

				return mbThreadFinder.filterFindByS_G_U_C(
					groupId, userId, categoryIds, queryDefinition);
			}

			if (includeAnonymous) {
				threadIds = _mbMessageFinder.filterFindByG_U_C_S(
					groupId, userId, categoryIds, status, start, end);
			}
			else {
				threadIds = _mbMessageFinder.filterFindByG_U_C_A_S(
					groupId, userId, categoryIds, false, status, start, end);
			}
		}

		return TransformUtil.transform(
			threadIds,
			threadId -> mbThreadPersistence.findByPrimaryKey(threadId));
	}

	@Override
	public List<MBThread> getGroupThreads(
			long groupId, long userId, int status, boolean subscribed,
			int start, int end)
		throws PortalException {

		return getGroupThreads(
			groupId, userId, status, subscribed, true, start, end);
	}

	@Override
	public List<MBThread> getGroupThreads(
			long groupId, long userId, int status, int start, int end)
		throws PortalException {

		return getGroupThreads(groupId, userId, status, false, start, end);
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, long userId, Date modifiedDate, boolean includeAnonymous,
		int status) {

		if (!_inlineSQLHelper.isEnabled(groupId)) {
			QueryDefinition<MBThread> queryDefinition = new QueryDefinition<>(
				status);

			if (includeAnonymous) {
				return mbThreadFinder.countByG_U_LPD(
					groupId, userId, modifiedDate, queryDefinition);
			}

			return mbThreadFinder.countByG_U_LPD_A(
				groupId, userId, modifiedDate, false, queryDefinition);
		}

		long[] categoryIds = _mbCategoryService.getCategoryIds(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		if (categoryIds.length == 0) {
			return 0;
		}

		if (includeAnonymous) {
			return _mbMessageFinder.filterCountByG_U_MD_C_S(
				groupId, userId, modifiedDate, categoryIds, status);
		}

		return _mbMessageFinder.filterCountByG_U_MD_C_A_S(
			groupId, userId, modifiedDate, categoryIds, false, status);
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, long userId, Date modifiedDate, int status) {

		return mbThreadService.getGroupThreadsCount(
			groupId, userId, modifiedDate, true, status);
	}

	@Override
	public int getGroupThreadsCount(long groupId, long userId, int status) {
		return getGroupThreadsCount(groupId, userId, status, false);
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, long userId, int status, boolean subscribed) {

		return getGroupThreadsCount(groupId, userId, status, subscribed, true);
	}

	@Override
	public int getGroupThreadsCount(
		long groupId, long userId, int status, boolean subscribed,
		boolean includeAnonymous) {

		if (!_inlineSQLHelper.isEnabled(groupId)) {
			return _getGroupThreadsCount(
				groupId, userId, status, subscribed, includeAnonymous);
		}

		long[] categoryIds = _mbCategoryService.getCategoryIds(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		if (categoryIds.length == 0) {
			return 0;
		}

		if (userId <= 0) {
			return _mbMessageFinder.filterCountByG_U_C_S(
				groupId, 0, categoryIds, status);
		}

		if (subscribed) {
			QueryDefinition<MBThread> queryDefinition = new QueryDefinition<>(
				status);

			return mbThreadFinder.filterCountByS_G_U_C(
				groupId, userId, categoryIds, queryDefinition);
		}

		if (includeAnonymous) {
			return _mbMessageFinder.filterCountByG_U_C_S(
				groupId, userId, categoryIds, status);
		}

		return _mbMessageFinder.filterCountByG_U_C_A_S(
			groupId, userId, categoryIds, false, status);
	}

	@Override
	public List<MBThread> getThreads(
		long groupId, long categoryId, int status, int start, int end) {

		QueryDefinition<MBThread> queryDefinition = new QueryDefinition<>(
			status, start, end, null);

		return mbThreadFinder.filterFindByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public List<MBThread> getThreads(
			long groupId, long categoryId,
			QueryDefinition<MBThread> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbThreadFinder.filterFindByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getThreadsCount(long groupId, long categoryId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return mbThreadFinder.filterCountByG_C(groupId, categoryId);
		}

		QueryDefinition<MBThread> queryDefinition = new QueryDefinition<>(
			status);

		return mbThreadFinder.filterCountByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getThreadsCount(
			long groupId, long categoryId,
			QueryDefinition<MBThread> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbThreadFinder.filterCountByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public Lock lockThread(long threadId) throws PortalException {
		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			thread.getGroupId(), thread.getCategoryId(),
			ActionKeys.LOCK_THREAD);

		return _lockManager.lock(
			getUserId(), MBThread.class.getName(), threadId,
			String.valueOf(threadId), false, _mbThreadLockExpirationTime);
	}

	@Override
	public MBThread moveThread(long categoryId, long threadId)
		throws PortalException {

		if (_lockManager.isLocked(MBThread.class.getName(), threadId)) {
			throw new LockedThreadException(
				StringBundler.concat(
					"Thread is locked for class name ",
					MBThread.class.getName(), " and class PK ", threadId));
		}

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			thread.getGroupId(), thread.getCategoryId(),
			ActionKeys.MOVE_THREAD);

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			thread.getGroupId(), categoryId, ActionKeys.MOVE_THREAD);

		return mbThreadLocalService.moveThread(
			thread.getGroupId(), categoryId, threadId);
	}

	@Override
	public MBThread moveThreadFromTrash(long categoryId, long threadId)
		throws PortalException {

		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			thread.getGroupId(), thread.getCategoryId(), ActionKeys.UPDATE);

		return mbThreadLocalService.moveThreadFromTrash(
			getUserId(), categoryId, threadId);
	}

	@Override
	public MBThread moveThreadToTrash(long threadId) throws PortalException {
		if (_lockManager.isLocked(MBThread.class.getName(), threadId)) {
			throw new LockedThreadException(
				StringBundler.concat(
					"Thread is locked for class name ",
					MBThread.class.getName(), " and class PK ", threadId));
		}

		List<MBMessage> messages = _mbMessageLocalService.getThreadMessages(
			threadId, WorkflowConstants.STATUS_ANY, null);

		for (MBMessage message : messages) {
			_messageModelResourcePermission.check(
				getPermissionChecker(), message.getMessageId(),
				ActionKeys.DELETE);
		}

		return mbThreadLocalService.moveThreadToTrash(getUserId(), threadId);
	}

	@Override
	public void restoreThreadFromTrash(long threadId) throws PortalException {
		List<MBMessage> messages = _mbMessageLocalService.getThreadMessages(
			threadId, WorkflowConstants.STATUS_ANY, null);

		for (MBMessage message : messages) {
			_messageModelResourcePermission.check(
				getPermissionChecker(), message.getMessageId(),
				ActionKeys.DELETE);
		}

		mbThreadLocalService.restoreThreadFromTrash(getUserId(), threadId);
	}

	@Override
	public Hits search(
			long groupId, long creatorUserId, int status, int start, int end)
		throws PortalException {

		return mbThreadLocalService.search(
			groupId, getUserId(), creatorUserId, status, start, end);
	}

	@Override
	public Hits search(
			long groupId, long creatorUserId, long startDate, long endDate,
			int status, int start, int end)
		throws PortalException {

		return mbThreadLocalService.search(
			groupId, getUserId(), creatorUserId, startDate, endDate, status,
			start, end);
	}

	@Override
	public MBThread splitThread(
			long messageId, String subject, ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		MBMessage message = _mbMessagePersistence.findByPrimaryKey(messageId);

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			message.getGroupId(), message.getCategoryId(),
			ActionKeys.MOVE_THREAD);

		_messageModelResourcePermission.check(
			permissionChecker, messageId, ActionKeys.VIEW);

		return mbThreadLocalService.splitThread(
			getUserId(), messageId, subject, serviceContext);
	}

	@Override
	public void unlockThread(long threadId) throws PortalException {
		MBThread thread = mbThreadPersistence.findByPrimaryKey(threadId);

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			thread.getGroupId(), thread.getCategoryId(),
			ActionKeys.LOCK_THREAD);

		_lockManager.unlock(MBThread.class.getName(), threadId);
	}

	@Activate
	protected void activate() {
		_mbThreadLockExpirationTime = GetterUtil.getLong(
			_configuration.get(
				"lock.expiration.time.com.liferay.message.boards.model." +
					"MBThread"));
	}

	private List<MBThread> _getGroupThreads(
		long groupId, long userId, int status, boolean subscribed,
		boolean includeAnonymous, int start, int end) {

		if (userId <= 0) {
			if (status == WorkflowConstants.STATUS_ANY) {
				return mbThreadPersistence.findByGroupId(groupId, start, end);
			}

			return mbThreadPersistence.findByG_S(groupId, status, start, end);
		}

		QueryDefinition<MBThread> queryDefinition = new QueryDefinition<>(
			status, start, end, null);

		if (subscribed) {
			return mbThreadFinder.findByS_G_U(groupId, userId, queryDefinition);
		}
		else if (includeAnonymous) {
			return mbThreadFinder.findByG_U(groupId, userId, queryDefinition);
		}

		return mbThreadFinder.findByG_U_A(
			groupId, userId, false, queryDefinition);
	}

	private int _getGroupThreadsCount(
		long groupId, long userId, int status, boolean subscribed,
		boolean includeAnonymous) {

		if (userId <= 0) {
			if (status == WorkflowConstants.STATUS_ANY) {
				return mbThreadPersistence.countByGroupId(groupId);
			}

			return mbThreadPersistence.countByG_S(groupId, status);
		}

		QueryDefinition<MBThread> queryDefinition = new QueryDefinition<>(
			status);

		if (subscribed) {
			return mbThreadFinder.countByS_G_U(
				groupId, userId, queryDefinition);
		}
		else if (includeAnonymous) {
			return mbThreadFinder.countByG_U(groupId, userId, queryDefinition);
		}

		return mbThreadFinder.countByG_U_A(
			groupId, userId, false, queryDefinition);
	}

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference(target = MBPersistenceConstants.SERVICE_CONFIGURATION_FILTER)
	private Configuration _configuration;

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

	@Reference
	private LockManager _lockManager;

	@Reference
	private MBCategoryService _mbCategoryService;

	@Reference
	private MBMessageFinder _mbMessageFinder;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBMessagePersistence _mbMessagePersistence;

	private long _mbThreadLockExpirationTime;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBMessage)"
	)
	private ModelResourcePermission<MBMessage> _messageModelResourcePermission;

}