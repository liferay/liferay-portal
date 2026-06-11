/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.manager;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * Reads and writes the persisted {@code L_ACCOUNT_VALIDATOR_RESULT} object
 * entries that back account entry validation. Used by
 * {@code BaseAccountEntryValidator} to cache results and skip redundant
 * revalidation.
 *
 * @author Tancredi Covioli
 */
public interface AccountEntryValidatorResultManager {

	/**
	 * Persists the validation result as a new
	 * {@code L_ACCOUNT_VALIDATOR_RESULT} object entry related to the account
	 * entry. The {@code classPK} is taken from
	 * {@link AccountEntryValidatorResult#getKey()}. No-op when the object
	 * definition is absent.
	 */
	public void addAccountEntryValidatorResult(
			AccountEntry accountEntry, String className,
			AccountEntryValidatorResult accountEntryValidatorResult)
		throws PortalException;

	/**
	 * Returns the latest persisted result for the given validator
	 * ({@code className} + {@code classPK}) and account entry, but only when
	 * that result is still valid: its status is {@code SUCCESS} or
	 * {@code MANUAL} and it was created within {@code checkInterval} days.
	 * Returns <code>null</code> otherwise, when no result was ever persisted,
	 * or when the object definition is absent.
	 */
	public AccountEntryValidatorResult getValidAccountEntryValidatorResult(
			AccountEntry accountEntry, String className, String classPK,
			int checkInterval)
		throws PortalException;

}