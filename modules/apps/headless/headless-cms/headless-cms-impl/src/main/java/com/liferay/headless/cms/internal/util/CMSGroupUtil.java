/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.vulcan.util.GroupUtil;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class CMSGroupUtil {

	public static Long[] getSelectedSpaceGroupIds(
		Long assetLibraryId, long companyId,
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService, Long[] spaceGroupIds) {

		if (assetLibraryId == null) {
			return spaceGroupIds;
		}

		Long groupId = GroupUtil.getDepotGroupId(
			String.valueOf(assetLibraryId), companyId, depotEntryLocalService,
			groupLocalService);

		if ((groupId == null) || !ArrayUtil.contains(spaceGroupIds, groupId)) {
			return new Long[0];
		}

		return new Long[] {groupId};
	}

	public static Long[] getSpaceGroupIds(
		long companyId, DepotEntryService depotEntryService, long userId) {

		List<Long> depotEntryGroupIds = depotEntryService.getDepotEntryGroupIds(
			companyId, userId, DepotConstants.TYPE_SPACE);

		return depotEntryGroupIds.toArray(new Long[0]);
	}

}