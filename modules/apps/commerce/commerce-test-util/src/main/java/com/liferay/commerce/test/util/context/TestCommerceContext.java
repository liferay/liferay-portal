/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalServiceUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
public class TestCommerceContext implements CommerceContext {

	public TestCommerceContext(
		AccountEntry accountEntry, CommerceCurrency commerceCurrency,
		CommerceChannel commerceChannel, User contextUser, Group contextGroup,
		CommerceOrder commerceOrder) {

		_accountEntry = accountEntry;
		_commerceCurrency = commerceCurrency;
		_commerceChannel = commerceChannel;
		_contextUser = contextUser;
		_contextGroup = contextGroup;
		_commerceOrder = commerceOrder;
	}

	@Override
	public AccountEntry getAccountEntry() {
		return _accountEntry;
	}

	@Override
	public String[] getAccountEntryAllowedTypes() throws PortalException {
		return new String[0];
	}

	@Override
	public long getCPConfigurationListId(long groupId) {
		return 0;
	}

	@Override
	public long[] getCPConfigurationListIds() {
		return new long[0];
	}

	@Override
	public long[] getCommerceAccountGroupIds() {
		if (_accountEntry == null) {
			return new long[0];
		}

		return AccountGroupLocalServiceUtil.getAccountGroupIds(
			_accountEntry.getAccountEntryId());
	}

	@Override
	public long getCommerceChannelGroupId() throws PortalException {
		if (_commerceChannel == null) {
			return 0;
		}

		return _commerceChannel.getGroupId();
	}

	@Override
	public long getCommerceChannelId() throws PortalException {
		if (_commerceChannel == null) {
			return 0;
		}

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	public CommerceCurrency getCommerceCurrency() {
		return _commerceCurrency;
	}

	@Override
	public CommerceOrder getCommerceOrder() {
		return _commerceOrder;
	}

	@Override
	public int getCommerceSiteType() {
		return 0;
	}

	private final AccountEntry _accountEntry;
	private final CommerceChannel _commerceChannel;
	private final CommerceCurrency _commerceCurrency;
	private final CommerceOrder _commerceOrder;
	private final Group _contextGroup;
	private final User _contextUser;

}