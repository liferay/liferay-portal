/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.Serializable;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface CommerceContext extends Serializable {

	public AccountEntry getAccountEntry() throws PortalException;

	public String[] getAccountEntryAllowedTypes() throws PortalException;

	public long getCPConfigurationListId(long groupId) throws PortalException;

	public long[] getCPConfigurationListIds() throws PortalException;

	public long[] getCommerceAccountGroupIds() throws PortalException;

	public long getCommerceChannelGroupId() throws PortalException;

	public long getCommerceChannelId() throws PortalException;

	public CommerceCurrency getCommerceCurrency() throws PortalException;

	public CommerceOrder getCommerceOrder();

	public int getCommerceSiteType();

}