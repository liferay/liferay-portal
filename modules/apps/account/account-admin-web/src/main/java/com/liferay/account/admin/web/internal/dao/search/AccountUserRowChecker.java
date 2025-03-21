/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.admin.web.internal.display.AccountUserDisplay;
import com.liferay.account.admin.web.internal.util.AccountEntryEmailAddressValidatorFactoryUtil;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountEntryUserRelLocalServiceUtil;
import com.liferay.account.validator.AccountEntryEmailAddressValidator;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;

import jakarta.portlet.PortletResponse;

/**
 * @author Erick Monteiro
 */
public class AccountUserRowChecker extends EmptyOnClickRowChecker {

	public AccountUserRowChecker(
		long accountEntryId, PortletResponse portletResponse) {

		super(portletResponse);

		_accountEntryId = accountEntryId;
	}

	@Override
	public boolean isChecked(Object object) {
		AccountUserDisplay accountUserDisplay = (AccountUserDisplay)object;

		return AccountEntryUserRelLocalServiceUtil.hasAccountEntryUserRel(
			_accountEntryId, accountUserDisplay.getUserId());
	}

	@Override
	public boolean isDisabled(Object object) {
		if (isChecked(object)) {
			return true;
		}

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.fetchAccountEntry(_accountEntryId);

		if ((accountEntry == null) || !accountEntry.isRestrictMembership()) {
			return false;
		}

		AccountEntryEmailAddressValidator accountEntryEmailAddressValidator =
			AccountEntryEmailAddressValidatorFactoryUtil.create(
				accountEntry.getCompanyId(), accountEntry.getDomainsArray());
		AccountUserDisplay accountUserDisplay = (AccountUserDisplay)object;

		return !accountEntryEmailAddressValidator.isValidDomain(
			accountUserDisplay.getEmailAddress());
	}

	private final long _accountEntryId;

}