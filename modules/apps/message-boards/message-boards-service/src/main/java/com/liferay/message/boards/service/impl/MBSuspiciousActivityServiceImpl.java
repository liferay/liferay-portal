/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBSuspiciousActivity;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.base.MBSuspiciousActivityServiceBaseImpl;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.service.persistence.MBThreadPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=mb",
		"json.web.service.context.path=MBSuspiciousActivity"
	},
	service = AopService.class
)
public class MBSuspiciousActivityServiceImpl
	extends MBSuspiciousActivityServiceBaseImpl {

	@Override
	public MBSuspiciousActivity addOrUpdateMessageSuspiciousActivity(
			long messageId, String reason)
		throws PortalException {

		return mbSuspiciousActivityLocalService.
			addOrUpdateMessageSuspiciousActivity(
				getUserId(), messageId, reason);
	}

	@Override
	public MBSuspiciousActivity addOrUpdateThreadSuspiciousActivity(
			long threadId, String reason)
		throws PortalException {

		return mbSuspiciousActivityLocalService.
			addOrUpdateThreadSuspiciousActivity(getUserId(), threadId, reason);
	}

	@Override
	public MBSuspiciousActivity deleteSuspiciousActivity(
			long suspiciousActivityId)
		throws PortalException {

		MBSuspiciousActivity mbSuspiciousActivity =
			mbSuspiciousActivityPersistence.findByPrimaryKey(
				suspiciousActivityId);

		_portletResourcePermission.check(
			getPermissionChecker(), mbSuspiciousActivity.getGroupId(),
			ActionKeys.BAN_USER);

		return mbSuspiciousActivityLocalService.deleteSuspiciousActivity(
			suspiciousActivityId);
	}

	@Override
	public List<MBSuspiciousActivity> getMessageSuspiciousActivities(
			long messageId)
		throws PortalException {

		MBMessage mbMessage = _mbMessagePersistence.findByPrimaryKey(messageId);

		_portletResourcePermission.check(
			getPermissionChecker(), mbMessage.getGroupId(),
			ActionKeys.BAN_USER);

		return mbSuspiciousActivityPersistence.findByMessageId(messageId);
	}

	@Override
	public MBSuspiciousActivity getSuspiciousActivity(long suspiciousActivityId)
		throws PortalException {

		MBSuspiciousActivity mbSuspiciousActivity =
			mbSuspiciousActivityPersistence.findByPrimaryKey(
				suspiciousActivityId);

		_portletResourcePermission.check(
			getPermissionChecker(), mbSuspiciousActivity.getGroupId(),
			ActionKeys.BAN_USER);

		return mbSuspiciousActivity;
	}

	@Override
	public List<MBSuspiciousActivity> getThreadSuspiciousActivities(
			long threadId)
		throws PortalException {

		MBThread mbThread = _mbThreadPersistence.findByPrimaryKey(threadId);

		_portletResourcePermission.check(
			getPermissionChecker(), mbThread.getGroupId(), ActionKeys.BAN_USER);

		return mbSuspiciousActivityPersistence.findByThreadId(threadId);
	}

	@Override
	public MBSuspiciousActivity updateValidated(long suspiciousActivityId)
		throws PortalException {

		MBSuspiciousActivity mbSuspiciousActivity =
			mbSuspiciousActivityPersistence.findByPrimaryKey(
				suspiciousActivityId);

		_portletResourcePermission.check(
			getPermissionChecker(), mbSuspiciousActivity.getGroupId(),
			ActionKeys.BAN_USER);

		return mbSuspiciousActivityLocalService.updateValidated(
			suspiciousActivityId);
	}

	@Reference
	private MBMessagePersistence _mbMessagePersistence;

	@Reference
	private MBThreadPersistence _mbThreadPersistence;

	@Reference(target = "(resource.name=" + MBConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}