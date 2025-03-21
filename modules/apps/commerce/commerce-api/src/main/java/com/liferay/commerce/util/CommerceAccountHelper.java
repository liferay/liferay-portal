/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface CommerceAccountHelper {

	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long userId, long[] roleIds,
			ServiceContext serviceContext)
		throws PortalException;

	public AccountEntryUserRel addAccountEntryUserRel(
			long commerceAccountId, long commerceAccountUserId,
			ServiceContext serviceContext)
		throws PortalException;

	public void addAccountEntryUserRels(
			long accountEntryId, long[] userIds, String[] emailAddresses,
			long[] roleIds, ServiceContext serviceContext)
		throws PortalException;

	public void addDefaultRoles(long userId) throws PortalException;

	public int countUserCommerceAccounts(
			long userId, long commerceChannelGroupId)
		throws PortalException;

	public String[] getAccountEntryTypes(long commerceChannelGroupId)
		throws ConfigurationException;

	public String getAccountManagementPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public int getCommerceSiteType(long commerceChannelGroupId)
		throws ConfigurationException;

	public AccountEntry getCurrentAccountEntry(
			long groupId, HttpServletRequest httpServletRequest)
		throws PortalException;

	public long[] getUserCommerceAccountIds(
			long userId, long commerceChannelGroupId)
		throws PortalException;

	public void setCurrentCommerceAccount(
			HttpServletRequest httpServletRequest, long groupId,
			long commerceAccountId)
		throws PortalException;

	public Integer toAccountEntryStatus(Boolean commerceAccountActive);

	public String[] toAccountEntryTypes(int commerceSiteType);

}