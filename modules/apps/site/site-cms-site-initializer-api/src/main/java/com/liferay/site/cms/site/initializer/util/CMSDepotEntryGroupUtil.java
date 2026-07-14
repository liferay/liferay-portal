/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Pedro Leite
 */
public class CMSDepotEntryGroupUtil {

	public static String getFilterString() {
		List<Long> depotEntryGroupIds =
			DepotEntryLocalServiceUtil.getDepotEntryGroupIds(
				CompanyThreadLocal.getCompanyId(), DepotConstants.TYPE_SPACE);

		String filterString =
			"groupIds in (" + StringUtil.merge(depotEntryGroupIds) + ")";

		Set<Long> depotEntryUserGroupIds = new HashSet<>();

		for (long depotEntryGroupId : depotEntryGroupIds) {
			depotEntryUserGroupIds.addAll(
				TransformUtil.transform(
					UserGroupLocalServiceUtil.getGroupUserGroups(
						depotEntryGroupId),
					UserGroup::getUserGroupId));
		}

		if (SetUtil.isEmpty(depotEntryUserGroupIds)) {
			return filterString;
		}

		return StringBundler.concat(
			"(", filterString, " or userGroupIds in ('",
			StringUtil.merge(depotEntryUserGroupIds, "', '"), "'))");
	}

}