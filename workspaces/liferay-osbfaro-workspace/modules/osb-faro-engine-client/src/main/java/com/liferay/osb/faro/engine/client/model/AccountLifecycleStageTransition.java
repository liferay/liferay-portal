/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.Date;

/**
 * @author Leslie Wong
 */
public class AccountLifecycleStageTransition {

	public String getAccountId() {
		return _accountId;
	}

	public String getAccountName() {
		return _accountName;
	}

	public AccountLifecycleStage getFromAccountLifecycleStage() {
		return _fromAccountLifecycleStage;
	}

	public AccountLifecycleStage getToAccountLifecycleStage() {
		return _toAccountLifecycleStage;
	}

	public Date getTransitionDate() {
		if (_transitionDate == null) {
			return null;
		}

		return new Date(_transitionDate.getTime());
	}

	public void setAccountId(String accountId) {
		_accountId = accountId;
	}

	public void setAccountName(String accountName) {
		_accountName = accountName;
	}

	public void setFromAccountLifecycleStage(
		AccountLifecycleStage fromAccountLifecycleStage) {

		_fromAccountLifecycleStage = fromAccountLifecycleStage;
	}

	public void setToAccountLifecycleStage(
		AccountLifecycleStage toAccountLifecycleStage) {

		_toAccountLifecycleStage = toAccountLifecycleStage;
	}

	public void setTransitionDate(Date transitionDate) {
		if (transitionDate != null) {
			_transitionDate = new Date(transitionDate.getTime());
		}
	}

	private String _accountId;
	private String _accountName;
	private AccountLifecycleStage _fromAccountLifecycleStage;
	private AccountLifecycleStage _toAccountLifecycleStage;
	private Date _transitionDate;

}