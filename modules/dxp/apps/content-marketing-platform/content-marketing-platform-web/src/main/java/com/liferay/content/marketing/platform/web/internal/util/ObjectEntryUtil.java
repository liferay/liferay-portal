/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.GroupConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryUtil {

	public static ObjectEntry getObjectEntry(
		HttpServletRequest httpServletRequest) {

		Object object = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (!(object instanceof ObjectEntry)) {
			return null;
		}

		return (ObjectEntry)object;
	}

	public static long getProjectGroupId(ObjectEntry objectEntry) {
		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-99403")) {

			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			objectEntry.getGroupId());

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_PROJECT)) {

			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		return objectEntry.getGroupId();
	}

}