/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator;

import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Tancredi Covioli
 */
public final class AccountEntryValidatorResult implements Serializable {

	public static Builder builder(String classPK) {
		return new Builder(classPK);
	}

	public String getActionLabel() {
		return _actionLabel;
	}

	public String getActionURL() {
		return _actionURL;
	}

	public JSONObject getAdditionalProps() {
		return _jsonObject;
	}

	public String getClassPK() {
		return _classPK;
	}

	public String getResultMessage() {
		return _resultMessage;
	}

	public String getResultStatus() {
		return _resultStatus;
	}

	public boolean isValid() {
		return !Objects.equals(
			AccountEntryValidatorConstants.RESULT_FAILURE, _resultStatus);
	}

	public static class Builder {

		public Builder actionLabel(String actionLabel) {
			_actionLabel = actionLabel;

			return this;
		}

		public Builder actionURL(String actionURL) {
			_actionURL = actionURL;

			return this;
		}

		public Builder additionalProps(JSONObject jsonObject) {
			_jsonObject = jsonObject;

			return this;
		}

		public AccountEntryValidatorResult build() {
			if ((_resultMessage == null) &&
				Objects.equals(
					_resultStatus,
					AccountEntryValidatorConstants.RESULT_FAILURE)) {

				_resultMessage = "account-validation-failed";
			}

			return new AccountEntryValidatorResult(
				_actionLabel, _actionURL, _classPK, _jsonObject, _resultMessage,
				_resultStatus);
		}

		public Builder resultMessage(String resultMessage) {
			_resultMessage = resultMessage;

			return this;
		}

		public Builder resultStatus(String resultStatus) {
			_resultStatus = resultStatus;

			return this;
		}

		private Builder(String classPK) {
			_classPK = classPK;
		}

		private String _actionLabel = StringPool.BLANK;
		private String _actionURL = StringPool.BLANK;
		private final String _classPK;
		private JSONObject _jsonObject;
		private String _resultMessage = StringPool.BLANK;
		private String _resultStatus =
			AccountEntryValidatorConstants.RESULT_SUCCESS;

	}

	private AccountEntryValidatorResult(
		String actionLabel, String actionURL, String classPK,
		JSONObject jsonObject, String resultMessage, String resultStatus) {

		_actionLabel = actionLabel;
		_actionURL = actionURL;
		_classPK = classPK;
		_jsonObject = jsonObject;
		_resultMessage = resultMessage;
		_resultStatus = resultStatus;
	}

	private final String _actionLabel;
	private final String _actionURL;
	private final String _classPK;
	private final JSONObject _jsonObject;
	private final String _resultMessage;
	private final String _resultStatus;

}