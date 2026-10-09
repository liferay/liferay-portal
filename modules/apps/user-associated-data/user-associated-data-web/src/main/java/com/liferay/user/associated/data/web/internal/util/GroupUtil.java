/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.user.associated.data.web.internal.constants.UADConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class GroupUtil {

	public static long[] getGroupIds(
		GroupLocalService groupLocalService, User user, String scope) {

		try {
			if (scope.equals(UADConstants.SCOPE_PERSONAL_SITE)) {
				Group userGroup = groupLocalService.getUserGroup(
					user.getCompanyId(), user.getUserId());

				return new long[] {userGroup.getGroupId()};
			}

			if (scope.equals(UADConstants.SCOPE_REGULAR_SITES)) {
				List<Group> allGroups = new ArrayList<>();

				List<Group> liveGroups = groupLocalService.getGroups(
					user.getCompanyId(), GroupConstants.ANY_PARENT_GROUP_ID,
					true);

				allGroups.addAll(liveGroups);

				for (Group group : liveGroups) {
					Group stagingGroup = group.getStagingGroup();

					if (stagingGroup == null) {
						continue;
					}

					allGroups.add(stagingGroup);
				}

				return ListUtil.toLongArray(allGroups, Group.GROUP_ID_ACCESSOR);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(GroupUtil.class);

}