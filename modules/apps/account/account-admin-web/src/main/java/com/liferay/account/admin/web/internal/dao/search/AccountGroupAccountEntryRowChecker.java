/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.admin.web.internal.display.AccountEntryDisplay;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.PortletResponse;

/**
 * @author Albert Lee
 */
public class AccountGroupAccountEntryRowChecker extends EmptyOnClickRowChecker {

	public AccountGroupAccountEntryRowChecker(
		PortletResponse portletResponse, long accountGroupId) {

		super(portletResponse);

		_accountGroupId = accountGroupId;
	}

	@Override
	public boolean isChecked(Object object) {
		return isDisabled(object);
	}

	@Override
	public boolean isDisabled(Object object) {
		AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)object;

		try {
			AccountGroupRel accountGroupRel =
				AccountGroupRelLocalServiceUtil.fetchAccountGroupRel(
					_accountGroupId, AccountEntry.class.getName(),
					accountEntryDisplay.getAccountEntryId());

			if (accountGroupRel != null) {
				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountGroupAccountEntryRowChecker.class);

	private final long _accountGroupId;

}