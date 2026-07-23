/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * @author Lucas Miranda
 */
public class FIPSSelfTestException extends Exception {

	public FIPSSelfTestException(
		String providerName, String failedTest, String fipsState,
		String providerMessage) {

		super(providerMessage);

		_providerName = providerName;
		_failedTest = failedTest;
		_fipsState = fipsState;
		_providerMessage = providerMessage;
	}

	public String getFailedTest() {
		return _failedTest;
	}

	public String getFipsState() {
		return _fipsState;
	}

	public String getProviderMessage() {
		return _providerMessage;
	}

	public String getProviderName() {
		return _providerName;
	}

	private final String _failedTest;
	private final String _fipsState;
	private final String _providerMessage;
	private final String _providerName;

}