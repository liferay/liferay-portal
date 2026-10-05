/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.test.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalServiceUtil;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

/**
 * @author Georgel Pop
 */
public class DesignLibraryTestUtil {

	public static Group addDesignLibraryGroup(long groupId, boolean connected)
		throws Exception {

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(groupId));

		if (connected) {
			DepotEntryGroupRelLocalServiceUtil.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), groupId);
		}

		return depotEntry.getGroup();
	}

}