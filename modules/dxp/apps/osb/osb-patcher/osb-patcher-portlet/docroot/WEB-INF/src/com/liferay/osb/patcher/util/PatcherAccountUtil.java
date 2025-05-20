/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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