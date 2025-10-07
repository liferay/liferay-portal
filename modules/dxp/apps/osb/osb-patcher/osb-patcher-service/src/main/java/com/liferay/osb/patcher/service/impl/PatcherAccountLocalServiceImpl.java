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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
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

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherAccount addPatcherAccount(
			long userId, long accountEntryId, String accountEntryCode)
		throws PortalException {

		PatcherAccount patcherAccount = patcherAccountPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		patcherAccount.setCompanyId(user.getCompanyId());
		patcherAccount.setUserId(user.getUserId());
		patcherAccount.setUserName(user.getFullName());

		patcherAccount.setCreateDate(new Date());
		patcherAccount.setModifiedDate(new Date());
		patcherAccount.setAccountEntryId(accountEntryId);
		patcherAccount.setAccountEntryCode(accountEntryCode);

		return patcherAccount;
	}

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

		if (Validator.isNull(keyword)) {
			return patcherAccountPersistence.findByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return patcherAccountPersistence.findByC_LikeA(
			companyId,
			_customSQL.keywords(keyword, false, WildcardMode.SURROUND)[0],
			start, end, orderByComparator);
	}

	@Override
	public int getPatcherAccountsCount(long companyId, String keyword) {
		if (Validator.isNull(keyword)) {
			return patcherAccountPersistence.countByCompanyId(companyId);
		}

		return patcherAccountPersistence.countByC_LikeA(
			companyId,
			_customSQL.keywords(keyword, false, WildcardMode.SURROUND)[0]);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}