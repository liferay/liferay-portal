/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.model;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
	service = AssetRendererFactory.class
)
public class AccountEntryAssetRendererFactory
	extends BaseAssetRendererFactory<AccountEntry> {

	public static final String TYPE = "account";

	public AccountEntryAssetRendererFactory() {
		setCategorizable(false);
		setClassName(AccountEntry.class.getName());
		setPortletId(AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN);
		setSearchable(false);
	}

	@Override
	public AssetRenderer<AccountEntry> getAssetRenderer(long classPK, int type)
		throws PortalException {

		return new AccountEntryAssetRenderer(
			_accountEntryLocalService.getAccountEntry(classPK));
	}

	@Override
	public String getIconCssClass() {
		return "briefcase";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

}