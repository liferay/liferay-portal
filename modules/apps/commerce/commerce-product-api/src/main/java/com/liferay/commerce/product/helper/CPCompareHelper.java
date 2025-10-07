/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.helper;

import aQute.bnd.annotation.ProviderType;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

/**
 * @author Alec Sloan
 */
@ProviderType
public interface CPCompareHelper {

	public List<CPCatalogEntry> getCPCatalogEntries(
			long groupId, long commerceAccountId,
			String cpDefinitionIdsCookieValue)
		throws PortalException;

	public List<Long> getCPDefinitionIds(
			long groupId, long commerceAccountId,
			String cpDefinitionIdsCookieValue)
		throws PortalException;

	public List<Long> getCPDefinitionIds(
			long groupId, long commerceAccountId,
			String cpDefinitionIdsCookieValue, boolean secure)
		throws PortalException;

	public String getCPDefinitionIdsCookieKey(long commerceChannelGroupId);

}