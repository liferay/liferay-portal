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

package com.liferay.osb.patcher.hook.upgrade.v4_3_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherAccount extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherAccount();
	}

	protected void updatePatcherAccount() throws Exception {
		Map<String, Long> patcherAccountEntryCodePatcherAccountIdsMap =
			new HashMap<String, Long>();

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherBuild patcherBuild : patcherBuilds) {
			String accountEntryCode = patcherBuild.getAccountEntryCode();

			if (patcherAccountEntryCodePatcherAccountIdsMap.containsKey(
					accountEntryCode)) {

				long patcherAccountId =
					patcherAccountEntryCodePatcherAccountIdsMap.get(
						accountEntryCode);

				PatcherAccountLocalServiceUtil.addPatcherBuildPatcherAccount(
					patcherBuild.getPatcherBuildId(), patcherAccountId);

				continue;
			}

			long increment = CounterLocalServiceUtil.increment();

			PatcherAccount patcherAccount =
				PatcherAccountLocalServiceUtil.createPatcherAccount(increment);

			patcherAccount.setCompanyId(patcherBuild.getCompanyId());

			User user = UserLocalServiceUtil.getUser(576943);

			patcherAccount.setUserId(user.getUserId());
			patcherAccount.setUserName(user.getFullName());

			Date date = new Date();

			patcherAccount.setCreateDate(date);
			patcherAccount.setModifiedDate(date);

			patcherAccount.setAccountEntryCode(accountEntryCode);

			PatcherAccountLocalServiceUtil.updatePatcherAccount(patcherAccount);

			PatcherAccountLocalServiceUtil.addPatcherBuildPatcherAccount(
				patcherBuild.getPatcherBuildId(),
				patcherAccount.getPatcherAccountId());

			patcherAccountEntryCodePatcherAccountIdsMap.put(
				accountEntryCode, patcherAccount.getPatcherAccountId());
		}
	}

}