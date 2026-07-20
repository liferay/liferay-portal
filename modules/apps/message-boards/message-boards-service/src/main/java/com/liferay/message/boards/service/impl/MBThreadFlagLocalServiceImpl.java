/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadFlag;
import com.liferay.message.boards.service.base.MBThreadFlagLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.DateUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBThreadFlag",
	service = AopService.class
)
public class MBThreadFlagLocalServiceImpl
	extends MBThreadFlagLocalServiceBaseImpl {

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public MBThreadFlag addThreadFlag(
			long userId, MBThread thread, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		if (user.isGuestUser()) {
			return null;
		}

		long threadId = thread.getThreadId();

		MBThreadFlag threadFlag = mbThreadFlagPersistence.fetchByU_T(
			userId, threadId);

		if ((threadFlag != null) &&
			DateUtil.equals(
				threadFlag.getModifiedDate(), thread.getLastPostDate())) {

			return threadFlag;
		}

		if (threadFlag == null) {
			long threadFlagId = counterLocalService.increment();

			threadFlag = mbThreadFlagPersistence.create(threadFlagId);

			threadFlag.setUuid(serviceContext.getUuid());
			threadFlag.setGroupId(thread.getGroupId());
			threadFlag.setCompanyId(user.getCompanyId());
			threadFlag.setUserId(userId);
			threadFlag.setUserName(user.getFullName());
			threadFlag.setModifiedDate(
				serviceContext.getModifiedDate(thread.getLastPostDate()));
			threadFlag.setThreadId(threadId);
		}
		else {
			threadFlag.setModifiedDate(thread.getLastPostDate());
		}

		return mbThreadFlagLocalService.updateMBThreadFlag(threadFlag);
	}

	@Override
	public void deleteThreadFlag(long threadFlagId) throws PortalException {
		MBThreadFlag threadFlag = mbThreadFlagPersistence.findByPrimaryKey(
			threadFlagId);

		mbThreadFlagLocalService.deleteThreadFlag(threadFlag);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteThreadFlag(MBThreadFlag threadFlag) {
		mbThreadFlagPersistence.remove(threadFlag);
	}

	@Override
	public void deleteThreadFlagsByThreadId(long threadId) {
		List<MBThreadFlag> threadFlags = mbThreadFlagPersistence.findByThreadId(
			threadId);

		for (MBThreadFlag threadFlag : threadFlags) {
			mbThreadFlagLocalService.deleteThreadFlag(threadFlag);
		}
	}

	@Override
	public void deleteThreadFlagsByUserId(long userId) {
		List<MBThreadFlag> threadFlags = mbThreadFlagPersistence.findByUserId(
			userId);

		for (MBThreadFlag threadFlag : threadFlags) {
			mbThreadFlagLocalService.deleteThreadFlag(threadFlag);
		}
	}

	@Override
	public MBThreadFlag fetchThreadFlag(long userId, MBThread thread) {
		User user = _userLocalService.fetchUser(userId);

		if ((user == null) || user.isGuestUser()) {
			return null;
		}

		return mbThreadFlagPersistence.fetchByU_T(userId, thread.getThreadId());
	}

	@Override
	public MBThreadFlag getThreadFlag(long userId, MBThread thread)
		throws PortalException {

		return mbThreadFlagPersistence.findByU_T(userId, thread.getThreadId());
	}

	@Override
	public boolean hasThreadFlag(long userId, MBThread thread)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		if (user.isGuestUser()) {
			return true;
		}

		MBThreadFlag threadFlag = mbThreadFlagPersistence.fetchByU_T(
			userId, thread.getThreadId());

		if ((threadFlag != null) &&
			DateUtil.equals(
				threadFlag.getModifiedDate(), thread.getLastPostDate())) {

			return true;
		}

		return false;
	}

	@Reference
	private UserLocalService _userLocalService;

}