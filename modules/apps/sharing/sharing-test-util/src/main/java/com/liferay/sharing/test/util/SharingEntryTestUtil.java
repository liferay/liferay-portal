/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.test.util;

import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.persistence.SharingEntryUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class SharingEntryTestUtil {

	public static List<SharingEntry> getToUserSharingEntries(long toUserId)
		throws Exception {

		return TransactionTestUtil.invoke(
			SharingEntryUtil.getPersistence(),
			sharingEntryPersistence -> sharingEntryPersistence.findByToUserId(
				toUserId));
	}

}