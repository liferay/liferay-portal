/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.manager.AccountEntryValidatorResultManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

import org.osgi.service.component.annotations.Reference;

/**
 * Base class for {@link AccountEntryValidator} implementations that caches
 * validation results. Before running the concrete validation it looks up the
 * latest persisted result; if that result is still valid (successful and
 * younger than the configured check interval) it is returned as is, skipping
 * revalidation. Otherwise the concrete {@link #doValidate} runs and its result
 * is persisted.
 *
 * <p>
 * Concrete validators implement {@link #doValidate}, {@link #getClassPK}, and
 * {@link #getConfiguration}. The bundle declaring the concrete
 * {@code @Component} must set <code>-dsannotations-options: inherit</code> so
 * the inherited {@link AccountEntryValidatorResultManager} reference binds.
 * </p>
 *
 * @author Tancredi Covioli
 */
public abstract class BaseAccountEntryValidator
	implements AccountEntryValidator {

	@Override
	public final AccountEntryValidatorResult validate(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException {

		if (accountEntry == null) {
			return null;
		}

		AccountEntryValidatorConfiguration accountEntryValidatorConfiguration =
			getConfiguration(accountEntry.getCompanyId());

		if ((accountEntryValidatorConfiguration == null) ||
			!accountEntryValidatorConfiguration.enabled()) {

			return null;
		}

		String classPK = getClassPK(accountEntry, jsonObject);

		if (classPK == null) {
			return doValidate(accountEntry, jsonObject);
		}

		String className = getClass().getName();

		AccountEntryValidatorResult accountEntryValidatorResult =
			accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					accountEntry, className, classPK,
					accountEntryValidatorConfiguration.checkInterval());

		if (accountEntryValidatorResult != null) {
			return accountEntryValidatorResult;
		}

		accountEntryValidatorResult = doValidate(accountEntry, jsonObject);

		if (accountEntryValidatorResult != null) {
			accountEntryValidatorResultManager.addAccountEntryValidatorResult(
				accountEntry, className, accountEntryValidatorResult);
		}

		return accountEntryValidatorResult;
	}

	protected abstract AccountEntryValidatorResult doValidate(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException;

	@Reference
	protected AccountEntryValidatorResultManager
		accountEntryValidatorResultManager;

}