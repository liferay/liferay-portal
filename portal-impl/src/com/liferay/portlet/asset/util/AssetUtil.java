/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author     Brian Wing Shun Chan
 * @author     Jorge Ferrer
 * @deprecated As of Judson (7.1.x), replaced by {@link
 *             com.liferay.asset.util.impl.AssetUtil}
 */
@Deprecated
public class AssetUtil {

	public static long[] filterCategoryIds(
			PermissionChecker permissionChecker, long[] categoryIds)
		throws PortalException {

		if (permissionChecker == null) {
			return categoryIds;
		}

		List<Long> viewableCategoryIds = new ArrayList<>();

		for (long categoryId : categoryIds) {
			AssetCategory category =
				AssetCategoryLocalServiceUtil.fetchCategory(categoryId);

			if ((category == null) ||
				!AssetCategoryPermission.contains(
					permissionChecker, category, ActionKeys.VIEW)) {

				continue;
			}

			viewableCategoryIds.add(categoryId);
		}

		return ArrayUtil.toArray(viewableCategoryIds.toArray(new Long[0]));
	}

}