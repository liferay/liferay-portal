/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * @author Lucas Miranda
 */
public class FIPSHealthCheckResult {

	public static FIPSHealthCheckResult failed(
		String providerName, String failedTest, String fipsState,
		String providerMessage) {

		return new FIPSHealthCheckResult(
			Status.FAILED, providerName, failedTest, fipsState,
			providerMessage);
	}

	public static FIPSHealthCheckResult healthy(String providerName) {
		return new FIPSHealthCheckResult(
			Status.HEALTHY, providerName, null, null, null);
	}

	public static FIPSHealthCheckResult notApplicable() {
		return new FIPSHealthCheckResult(
			Status.NOT_APPLICABLE, null, null, null, null);
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

	public Status getStatus() {
		return _status;
	}

	public enum Status {

		FAILED, HEALTHY, NOT_APPLICABLE

	}

	private FIPSHealthCheckResult(
		Status status, String providerName, String failedTest, String fipsState,
		String providerMessage) {

		_status = status;
		_providerName = providerName;
		_failedTest = failedTest;
		_fipsState = fipsState;
		_providerMessage = providerMessage;
	}

	private final String _failedTest;
	private final String _fipsState;
	private final String _providerMessage;
	private final String _providerName;
	private final Status _status;

}