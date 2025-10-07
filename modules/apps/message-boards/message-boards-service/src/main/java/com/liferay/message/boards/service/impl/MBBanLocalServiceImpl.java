/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.exception.BannedUserException;
import com.liferay.message.boards.model.MBBan;
import com.liferay.message.boards.service.base.MBBanLocalServiceBaseImpl;
import com.liferay.message.boards.util.MBUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBBan",
	service = AopService.class
)
public class MBBanLocalServiceImpl extends MBBanLocalServiceBaseImpl {

	@Override
	public MBBan addBan(
			long userId, long banUserId, ServiceContext serviceContext)
		throws PortalException {

		long groupId = serviceContext.getScopeGroupId();

		MBBan ban = mbBanPersistence.fetchByG_B(groupId, banUserId);

		if (ban == null) {
			Date date = new Date();

			long banId = counterLocalService.increment();

			ban = mbBanPersistence.create(banId);

			ban.setUuid(serviceContext.getUuid());
			ban.setGroupId(groupId);

			User user = _userLocalService.getUser(userId);

			ban.setCompanyId(user.getCompanyId());
			ban.setUserId(user.getUserId());
			ban.setUserName(user.getFullName());

			ban.setCreateDate(serviceContext.getCreateDate(date));
			ban.setModifiedDate(serviceContext.getModifiedDate(date));
			ban.setBanUserId(banUserId);
		}

		return mbBanPersistence.update(ban);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkBan(long groupId, long banUserId) throws PortalException {
		if (hasBan(groupId, banUserId)) {
			throw new BannedUserException("Banned user " + banUserId);
		}
	}

	@Override
	public void deleteBan(long banId) throws PortalException {
		MBBan ban = mbBanPersistence.findByPrimaryKey(banId);

		mbBanLocalService.deleteBan(ban);
	}

	@Override
	public void deleteBan(long banUserId, ServiceContext serviceContext) {
		long groupId = serviceContext.getScopeGroupId();

		MBBan ban = mbBanPersistence.fetchByG_B(groupId, banUserId);

		if (ban != null) {
			mbBanLocalService.deleteBan(ban);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteBan(MBBan ban) {
		mbBanPersistence.remove(ban);
	}

	@Override
	public void deleteBansByBanUserId(long banUserId) {
		List<MBBan> bans = mbBanPersistence.findByBanUserId(banUserId);

		for (MBBan ban : bans) {
			mbBanLocalService.deleteBan(ban);
		}
	}

	@Override
	public void deleteBansByGroupId(long groupId) {
		List<MBBan> bans = mbBanPersistence.findByGroupId(groupId);

		for (MBBan ban : bans) {
			mbBanLocalService.deleteBan(ban);
		}
	}

	@Override
	public void expireBans() {
		if (PropsValues.MESSAGE_BOARDS_EXPIRE_BAN_INTERVAL <= 0) {
			return;
		}

		long now = System.currentTimeMillis();

		List<MBBan> bans = mbBanPersistence.findAll();

		for (MBBan ban : bans) {
			Date unbanDate = MBUtil.getUnbanDate(
				ban, PropsValues.MESSAGE_BOARDS_EXPIRE_BAN_INTERVAL);

			long unbanTime = unbanDate.getTime();

			if (now >= unbanTime) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Auto expiring ban ", ban.getBanId(), " on user ",
							ban.getBanUserId()));
				}

				mbBanPersistence.remove(ban);
			}
		}
	}

	@Override
	public List<MBBan> getBans(long groupId, int start, int end) {
		return mbBanPersistence.findByGroupId(groupId, start, end);
	}

	@Override
	public int getBansCount(long groupId) {
		return mbBanPersistence.countByGroupId(groupId);
	}

	@Override
	public boolean hasBan(long groupId, long banUserId) {
		if (mbBanPersistence.fetchByG_B(groupId, banUserId) == null) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBBanLocalServiceImpl.class);

	@Reference
	private UserLocalService _userLocalService;

}