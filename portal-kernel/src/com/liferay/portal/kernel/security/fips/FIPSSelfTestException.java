/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Lucas Miranda
 */
public class FIPSSelfTestException extends PortalException {

	public FIPSSelfTestException(
		String providerName, String failedTest, String fipsState,
		String providerMessage) {

		this(providerName, failedTest, fipsState, providerMessage, null);
	}

	public FIPSSelfTestException(
		String providerName, String failedTest, String fipsState,
		String providerMessage, Throwable throwable) {

		super(providerMessage, throwable);

		_providerName = providerName;
		_failedTest = failedTest;
		_fipsState = fipsState;
	}

	public String getFailedTest() {
		return _failedTest;
	}

	public String getFipsState() {
		return _fipsState;
	}

	public String getProviderName() {
		return _providerName;
	}

	private final String _failedTest;
	private final String _fipsState;
	private final String _providerName;

}