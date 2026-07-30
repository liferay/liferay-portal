/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

/**
 * @author Guilherme Camacho
 */
public class CMSObjectEntryUtil {

	public static boolean isCMSObjectEntry(ObjectEntry objectEntry) {
		if (objectEntry.getGroupId() == 0) {
			return false;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(
			objectEntry.getGroupId());

		if ((group == null) || !group.isDepot()) {
			return false;
		}

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchDepotEntry(
			group.getClassPK());

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_SPACE)) {

			return false;
		}

		return true;
	}

}