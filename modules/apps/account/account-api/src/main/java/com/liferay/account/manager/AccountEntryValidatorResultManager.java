/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.manager;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Tancredi Covioli
 */
public interface AccountEntryValidatorResultManager {

	public void addAccountEntryValidatorResult(
			AccountEntry accountEntry,
			AccountEntryValidatorResult accountEntryValidatorResult,
			String className)
		throws PortalException;

	public AccountEntryValidatorResult getValidAccountEntryValidatorResult(
			AccountEntry accountEntry, int checkInterval, String className,
			String classPK)
		throws PortalException;

}