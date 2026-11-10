/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionLinkSearchUtil {

	public static SearchContext getCPDefinitionLinkSearchContext(
		AccountEntry accountEntry,
		AccountGroupLocalService accountGroupLocalService, long companyId,
		long cpDefinitionId, String definitionLinkType) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"accountEntryId",
				() -> {
					if (accountEntry == null) {
						return null;
					}

					return accountEntry.getAccountEntryId();
				}
			).put(
				"commerceAccountGroupIds",
				() -> {
					if (accountEntry == null) {
						return null;
					}

					return accountGroupLocalService.getAccountGroupIds(
						accountEntry.getAccountEntryId());
				}
			).put(
				"definitionLinkCPDefinitionId", cpDefinitionId
			).put(
				"definitionLinkType", definitionLinkType
			).put(
				"excludedCPDefinitionId", cpDefinitionId
			).build());
		searchContext.setCompanyId(companyId);

		return searchContext;
	}

}