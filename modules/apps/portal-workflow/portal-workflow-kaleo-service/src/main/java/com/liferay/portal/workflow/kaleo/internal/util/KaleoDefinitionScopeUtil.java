/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;

import java.util.List;
import java.util.Objects;

/**
 * @author Shuyang Zhou
 */
public class KaleoDefinitionScopeUtil {

	public static long getGroupId(
		String scope, ServiceContext serviceContext,
		AccountEntryUserRelLocalService accountEntryUserRelLocalService) {

		if (!Objects.equals(WorkflowDefinitionConstants.SCOPE_AI, scope)) {
			return 0L;
		}

		List<AccountEntryUserRel> accountEntryUserRels =
			accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountUserId(
					serviceContext.getUserId());

		if (accountEntryUserRels.size() != 2) {
			return 0L;
		}

		try {
			for (AccountEntryUserRel accountEntryUserRel :
					accountEntryUserRels) {

				AccountEntry accountEntry =
					accountEntryUserRel.getAccountEntry();

				if (!Objects.equals(
						accountEntry.getExternalReferenceCode(), "L_AI_HUB")) {

					return accountEntry.getAccountEntryGroupId();
				}
			}
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}

		return 0L;
	}

}