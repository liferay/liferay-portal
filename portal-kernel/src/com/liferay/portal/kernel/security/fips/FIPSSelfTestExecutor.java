/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * @author Lucas Miranda
 */
public interface FIPSSelfTestExecutor {

	/**
	 * Forces the validated provider to re-run its self-tests and re-verifies
	 * approved mode. Returns the provider name on success. Throws {@link
	 * FIPSSelfTestException} on a detected self-test failure; any other
	 * exception signals an unverifiable state and is treated as failure
	 * (fail-closed) by the caller.
	 */
	public String execute() throws Exception;

}