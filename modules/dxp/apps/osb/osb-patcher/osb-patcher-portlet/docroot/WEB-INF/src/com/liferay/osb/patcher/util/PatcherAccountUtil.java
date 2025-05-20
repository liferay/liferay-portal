/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.AlloyServiceInvoker;
import com.liferay.osb.patcher.model.PatcherAccount;

import java.util.List;

/**
 * @author Zsolt Balogh
 */
public class PatcherAccountUtil {

	public static PatcherAccount fetchPatcherAccount(String accountEntryCode) {
		try {
			return getPatcherAccount(accountEntryCode);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static PatcherAccount getPatcherAccount(String accountEntryCode)
		throws Exception {

		AlloyServiceInvoker patcherAccountAlloyServiceInvoker =
			new AlloyServiceInvoker(PatcherAccount.class.getName());

		List<PatcherAccount> patcherAccounts =
			patcherAccountAlloyServiceInvoker.executeDynamicQuery(
				new Object[] {"accountEntryCode", accountEntryCode});

		return patcherAccounts.get(0);
	}

}