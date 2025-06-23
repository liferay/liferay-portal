/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.service.base.PatcherAccountLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherAccount",
	service = AopService.class
)
public class PatcherAccountLocalServiceImpl
	extends PatcherAccountLocalServiceBaseImpl {

	@Override
	public PatcherAccount fetchPatcherAccount(String accountEntryCode) {
		return patcherAccountPersistence.fetchByAccountEntryCode(
			accountEntryCode);
	}

	@Override
	public PatcherAccount getPatcherAccount(String accountEntryCode)
		throws Exception {

		return patcherAccountPersistence.findByAccountEntryCode(
			accountEntryCode);
	}

	@Override
	public List<PatcherAccount> getPatcherAccounts(
		long companyId, String keyword, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return patcherAccountPersistence.findByC_LikeA(
			companyId,
			_customSQL.keywords(keyword, false, WildcardMode.SURROUND)[0],
			start, end, orderByComparator);
	}

	@Reference
	private CustomSQL _customSQL;

}