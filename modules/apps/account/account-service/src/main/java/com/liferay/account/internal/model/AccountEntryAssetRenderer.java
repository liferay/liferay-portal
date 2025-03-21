/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.model;

import com.liferay.account.model.AccountEntry;
import com.liferay.asset.kernel.model.BaseAssetRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class AccountEntryAssetRenderer extends BaseAssetRenderer<AccountEntry> {

	public AccountEntryAssetRenderer(AccountEntry accountEntry) {
		_accountEntry = accountEntry;
	}

	@Override
	public AccountEntry getAssetObject() {
		return _accountEntry;
	}

	@Override
	public String getClassName() {
		return AccountEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		return _accountEntry.getAccountEntryId();
	}

	@Override
	public long getGroupId() {
		return 0;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _accountEntry.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		return _accountEntry.getName();
	}

	@Override
	public long getUserId() {
		return _accountEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _accountEntry.getUserName();
	}

	@Override
	public String getUuid() {
		return _accountEntry.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private final AccountEntry _accountEntry;

}