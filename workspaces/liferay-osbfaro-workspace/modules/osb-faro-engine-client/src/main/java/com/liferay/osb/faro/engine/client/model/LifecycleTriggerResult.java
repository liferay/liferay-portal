/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Caio Pinheiro
 */
public class LifecycleTriggerResult {

	public String getAccountId() {
		return _accountId;
	}

	public String getAccountName() {
		return _accountName;
	}

	public long getCount() {
		return _count;
	}

	public void setAccountId(String accountId) {
		_accountId = accountId;
	}

	public void setAccountName(String accountName) {
		_accountName = accountName;
	}

	public void setCount(long count) {
		_count = count;
	}

	private String _accountId;
	private String _accountName;
	private long _count;

}