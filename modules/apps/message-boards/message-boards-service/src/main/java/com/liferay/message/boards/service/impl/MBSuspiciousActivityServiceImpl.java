/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.model.MBSuspiciousActivity;
import com.liferay.message.boards.service.base.MBSuspiciousActivityServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;

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

		// TODO Add permission checks for remote methods

		return mbSuspiciousActivityLocalService.deleteSuspiciousActivity(
			suspiciousActivityId);
	}

	@Override
	public List<MBSuspiciousActivity> getMessageSuspiciousActivities(
		long messageId) {

		return mbSuspiciousActivityPersistence.findByMessageId(messageId);
	}

	@Override
	public MBSuspiciousActivity getSuspiciousActivity(long suspiciousActivityId)
		throws PortalException {

		return mbSuspiciousActivityPersistence.findByPrimaryKey(
			suspiciousActivityId);
	}

	@Override
	public List<MBSuspiciousActivity> getThreadSuspiciousActivities(
		long threadId) {

		return mbSuspiciousActivityPersistence.findByThreadId(threadId);
	}

	@Override
	public MBSuspiciousActivity updateValidated(long suspiciousActivityId)
		throws PortalException {

		return mbSuspiciousActivityLocalService.updateValidated(
			suspiciousActivityId);
	}

}