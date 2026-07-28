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
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
public abstract class BaseAccountEntryValidator
	implements AccountEntryValidator {

	@Override
	public Set<String> getResultMessages() {
		return Collections.singleton("account-validation-failed");
	}

	@Override
	public final AccountEntryValidatorResult validate(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException {

		if (accountEntry == null) {
			return null;
		}

		AccountEntryValidatorConfiguration accountEntryValidatorConfiguration =
			getAccountEntryValidatorConfiguration(accountEntry.getCompanyId());

		if ((accountEntryValidatorConfiguration == null) ||
			!accountEntryValidatorConfiguration.enabled()) {

			return null;
		}

		String classPK = getClassPK(accountEntry, jsonObject);

		if (Validator.isNull(classPK)) {
			return doValidate(accountEntry, jsonObject);
		}

		Class<?> clazz = getClass();

		AccountEntryValidatorResult accountEntryValidatorResult =
			accountEntryValidatorResultManager.
				getValidAccountEntryValidatorResult(
					accountEntry,
					accountEntryValidatorConfiguration.checkInterval(),
					clazz.getName(), classPK);

		if (accountEntryValidatorResult != null) {
			return accountEntryValidatorResult;
		}

		accountEntryValidatorResult = doValidate(accountEntry, jsonObject);

		if (accountEntryValidatorResult != null) {
			accountEntryValidatorResultManager.addAccountEntryValidatorResult(
				accountEntry, accountEntryValidatorResult, clazz.getName());
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