/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util.validator;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

/**
 * @author Crescenzo Rega
 */
public class TestAccountEntryValidator implements AccountEntryValidator {

	public TestAccountEntryValidator() {
		this(RandomTestUtil.randomString(), null, null);
	}

	public TestAccountEntryValidator(
		String classPK, String resultMessage, String resultStatus) {

		_classPK = classPK;
		_resultMessage = resultMessage;
		_resultStatus = resultStatus;
	}

	@Override
	public AccountEntryValidatorConfiguration
		getAccountEntryValidatorConfiguration(long companyId) {

		return new AccountEntryValidatorConfiguration() {

			@Override
			public int checkInterval() {
				return 0;
			}

			@Override
			public boolean enabled() {
				return true;
			}

		};
	}

	@Override
	public String getClassPK(AccountEntry accountEntry, JSONObject jsonObject) {
		_jsonObject = jsonObject;

		return _classPK;
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public int getValidateCount() {
		return _validateCount;
	}

	@Override
	public boolean isSkipped(AccountEntry accountEntry, JSONObject jsonObject) {
		return _skipped;
	}

	public void setResultStatus(String resultStatus) {
		_resultStatus = resultStatus;
	}

	public void setSkipped(boolean skipped) {
		_skipped = skipped;
	}

	@Override
	public AccountEntryValidatorResult validate(
		AccountEntry accountEntry, JSONObject jsonObject) {

		_jsonObject = jsonObject;

		_validateCount++;

		return AccountEntryValidatorResult.builder(
			_classPK
		).resultMessage(
			_resultMessage
		).resultStatus(
			_resultStatus
		).build();
	}

	private final String _classPK;
	private volatile JSONObject _jsonObject;
	private final String _resultMessage;
	private String _resultStatus;
	private volatile boolean _skipped;
	private volatile int _validateCount;

}