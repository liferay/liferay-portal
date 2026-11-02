/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBSuspiciousActivity;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.base.MBSuspiciousActivityLocalServiceBaseImpl;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.service.persistence.MBThreadPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBSuspiciousActivity",
	service = AopService.class
)
public class MBSuspiciousActivityLocalServiceImpl
	extends MBSuspiciousActivityLocalServiceBaseImpl {

	@Override
	public MBSuspiciousActivity addOrUpdateMessageSuspiciousActivity(
			long userId, long messageId, String reason)
		throws PortalException {

		MBSuspiciousActivity suspiciousActivity =
			mbSuspiciousActivityPersistence.fetchByU_M(userId, messageId);

		if (suspiciousActivity == null) {
			long suspiciousActivityId = counterLocalService.increment();

			suspiciousActivity = mbSuspiciousActivityPersistence.create(
				suspiciousActivityId);

			MBMessage message = _mbMessagePersistence.findByPrimaryKey(
				messageId);

			MBThread thread = _mbThreadPersistence.findByPrimaryKey(
				message.getThreadId());

			suspiciousActivity.setGroupId(thread.getGroupId());

			User user = _userLocalService.getUser(userId);

			suspiciousActivity.setCompanyId(user.getCompanyId());
			suspiciousActivity.setUserId(user.getUserId());
			suspiciousActivity.setUserName(user.getFullName());

			suspiciousActivity.setMessageId(messageId);
			suspiciousActivity.setThreadId(thread.getThreadId());
		}

		suspiciousActivity.setReason(reason);

		return mbSuspiciousActivityPersistence.update(suspiciousActivity);
	}

	@Override
	public MBSuspiciousActivity addOrUpdateThreadSuspiciousActivity(
			long userId, long threadId, String reason)
		throws PortalException {

		MBSuspiciousActivity suspiciousActivity =
			mbSuspiciousActivityPersistence.fetchByU_T(userId, threadId);

		if (suspiciousActivity == null) {
			long suspiciousActivityId = counterLocalService.increment();

			suspiciousActivity = mbSuspiciousActivityPersistence.create(
				suspiciousActivityId);

			MBThread thread = _mbThreadPersistence.findByPrimaryKey(threadId);

			suspiciousActivity.setGroupId(thread.getGroupId());

			User user = _userLocalService.getUser(userId);

			suspiciousActivity.setCompanyId(user.getCompanyId());
			suspiciousActivity.setUserId(user.getUserId());
			suspiciousActivity.setUserName(user.getFullName());

			suspiciousActivity.setMessageId(0);
			suspiciousActivity.setThreadId(thread.getThreadId());
		}

		suspiciousActivity.setReason(reason);

		return mbSuspiciousActivityPersistence.update(suspiciousActivity);
	}

	@Override
	public MBSuspiciousActivity deleteSuspiciousActivity(
			long suspiciousActivityId)
		throws PortalException {

		return mbSuspiciousActivityPersistence.remove(suspiciousActivityId);
	}

	@Override
	public MBSuspiciousActivity updateValidated(long suspiciousActivityId)
		throws PortalException {

		MBSuspiciousActivity suspiciousActivity =
			mbSuspiciousActivityPersistence.findByPrimaryKey(
				suspiciousActivityId);

		suspiciousActivity.setValidated(!suspiciousActivity.isValidated());

		return mbSuspiciousActivityPersistence.update(suspiciousActivity);
	}

	@Reference
	private MBMessagePersistence _mbMessagePersistence;

	@Reference
	private MBThreadPersistence _mbThreadPersistence;

	@Reference
	private UserLocalService _userLocalService;

}