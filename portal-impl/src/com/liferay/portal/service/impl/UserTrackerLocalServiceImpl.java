/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserTracker;
import com.liferay.portal.kernel.model.UserTrackerPath;
import com.liferay.portal.kernel.service.persistence.UserTrackerPathPersistence;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.service.base.UserTrackerLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class UserTrackerLocalServiceImpl
	extends UserTrackerLocalServiceBaseImpl {

	@Override
	public UserTracker addUserTracker(
		long companyId, long userId, Date modifiedDate, String sessionId,
		String remoteAddr, String remoteHost, String userAgent,
		List<UserTrackerPath> userTrackerPaths) {

		if (PropsValues.SESSION_TRACKER_PERSISTENCE_ENABLED) {
			long userTrackerId = counterLocalService.increment(
				UserTracker.class.getName());

			UserTracker userTracker = userTrackerPersistence.create(
				userTrackerId);

			userTracker.setCompanyId(companyId);
			userTracker.setUserId(userId);
			userTracker.setModifiedDate(modifiedDate);
			userTracker.setSessionId(sessionId);
			userTracker.setRemoteAddr(remoteAddr);
			userTracker.setRemoteHost(remoteHost);
			userTracker.setUserAgent(userAgent);

			userTracker = userTrackerPersistence.update(userTracker);

			for (UserTrackerPath userTrackerPath : userTrackerPaths) {
				long pathId = counterLocalService.increment(
					UserTrackerPath.class.getName());

				userTrackerPath.setUserTrackerPathId(pathId);

				userTrackerPath.setUserTrackerId(userTrackerId);

				_userTrackerPathPersistence.update(userTrackerPath);
			}

			return userTracker;
		}

		return null;
	}

	@Override
	public UserTracker deleteUserTracker(long userTrackerId)
		throws PortalException {

		UserTracker userTracker = userTrackerPersistence.findByPrimaryKey(
			userTrackerId);

		return deleteUserTracker(userTracker);
	}

	@Override
	public UserTracker deleteUserTracker(UserTracker userTracker) {

		// Paths

		_userTrackerPathPersistence.removeByUserTrackerId(
			userTracker.getUserTrackerId());

		// User tracker

		return userTrackerPersistence.remove(userTracker);
	}

	@Override
	public List<UserTracker> getUserTrackers(
		long companyId, int start, int end) {

		return userTrackerPersistence.findByCompanyId(companyId, start, end);
	}

	@BeanReference(type = UserTrackerPathPersistence.class)
	private UserTrackerPathPersistence _userTrackerPathPersistence;

}