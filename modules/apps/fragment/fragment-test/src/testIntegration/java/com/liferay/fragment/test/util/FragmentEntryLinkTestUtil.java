/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.test.util;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.persistence.FragmentEntryLinkUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

/**
 * @author Shuyang Zhou
 */
public class FragmentEntryLinkTestUtil {

	public static FragmentEntryLink getFragmentEntryLink(
			long groupId, String originalFragmentEntryLinkERC, long plid)
		throws Exception {

		return TransactionTestUtil.invoke(
			FragmentEntryLinkUtil.getPersistence(),
			fragmentEntryLinkPersistence ->
				fragmentEntryLinkPersistence.findByG_OFELERC_P_First(
					groupId, originalFragmentEntryLinkERC, plid, null));
	}

}