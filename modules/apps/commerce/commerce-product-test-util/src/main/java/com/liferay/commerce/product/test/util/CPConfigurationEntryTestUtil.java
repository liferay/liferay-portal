/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.test.util;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntryUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class CPConfigurationEntryTestUtil {

	public static List<CPConfigurationEntry> getCPConfigurationEntries(
			long cpConfigurationListId)
		throws Exception {

		return TransactionTestUtil.invoke(
			CPConfigurationEntryUtil.getPersistence(),
			cpConfigurationEntryPersistence ->
				cpConfigurationEntryPersistence.findByCPConfigurationListId(
					cpConfigurationListId));
	}

}