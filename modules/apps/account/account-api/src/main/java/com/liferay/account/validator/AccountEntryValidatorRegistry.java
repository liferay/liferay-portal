/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator;

import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.List;

/**
 * @author Tancredi Covioli
 */
public interface AccountEntryValidatorRegistry {

	public AccountEntryValidator getAccountEntryValidator(String key);

	public List<AccountEntryValidator> getAccountEntryValidators();

	public boolean isLastResultSuccess(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException;

	public List<AccountEntryValidatorResult> validate(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException;

}