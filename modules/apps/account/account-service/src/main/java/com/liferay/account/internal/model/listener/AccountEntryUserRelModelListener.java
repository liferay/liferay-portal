/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.model.listener;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = ModelListener.class)
public class AccountEntryUserRelModelListener
	extends BaseModelListener<AccountEntryUserRel> {

	@Override
	public void onAfterCreate(AccountEntryUserRel accountEntryUserRel)
		throws ModelListenerException {

		_updateDefaultAccountEntry(accountEntryUserRel);

		_reindexAccountEntry(accountEntryUserRel.getAccountEntryId());
		_reindexUser(accountEntryUserRel.getAccountUserId());
	}

	@Override
	public void onAfterRemove(AccountEntryUserRel accountEntryUserRel)
		throws ModelListenerException {

		if (accountEntryUserRel.getAccountUserId() ==
				UserConstants.USER_ID_DEFAULT) {

			return;
		}

		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryUserRel.getAccountEntryId());

		if (accountEntry != null) {
			_userGroupRoleLocalService.deleteUserGroupRoles(
				accountEntryUserRel.getAccountUserId(),
				new long[] {accountEntry.getAccountEntryGroupId()});
		}

		_updateDefaultAccountEntry(accountEntryUserRel);

		_reindexAccountEntry(accountEntryUserRel.getAccountEntryId());
		_reindexUser(accountEntryUserRel.getAccountUserId());
	}

	@Override
	public void onBeforeCreate(AccountEntryUserRel accountEntryUserRel)
		throws ModelListenerException {

		_checkPersonTypeAccountEntry(accountEntryUserRel.getAccountEntryId());
	}

	private void _checkPersonTypeAccountEntry(long accountEntryId)
		throws ModelListenerException {

		try {
			if (accountEntryId == AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) {
				return;
			}

			AccountEntry accountEntry =
				_accountEntryLocalService.getAccountEntry(accountEntryId);

			if (Objects.equals(
					accountEntry.getType(),
					AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON) &&
				ListUtil.isNotEmpty(
					_accountEntryUserRelLocalService.
						getAccountEntryUserRelsByAccountEntryId(
							accountEntryId))) {

				throw new ModelListenerException();
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _reindexAccountEntry(long accountEntryId) {
		try {
			Indexer<AccountEntry> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(AccountEntry.class);

			indexer.reindex(AccountEntry.class.getName(), accountEntryId);
		}
		catch (SearchException searchException) {
			throw new ModelListenerException(searchException);
		}
	}

	private void _reindexUser(long accountUserId) {
		try {
			Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			indexer.reindex(User.class.getName(), accountUserId);
		}
		catch (SearchException searchException) {
			throw new ModelListenerException(searchException);
		}
	}

	private void _updateDefaultAccountEntry(
			AccountEntryUserRel accountEntryUserRel)
		throws ModelListenerException {

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountUserId(
					accountEntryUserRel.getAccountUserId());

		if (accountEntryUserRels.size() > 1) {
			for (AccountEntryUserRel curAccountEntryUserRel :
					accountEntryUserRels) {

				if (curAccountEntryUserRel.getAccountEntryId() !=
						AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) {

					continue;
				}

				_accountEntryUserRelLocalService.deleteAccountEntryUserRel(
					curAccountEntryUserRel);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryUserRelModelListener.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}