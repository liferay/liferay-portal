/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.engine;

/**
 * @author Alejandro Tardín
 */
public class RedactTimeoutException extends RuntimeException {

	public RedactTimeoutException(long timeout) {
		super("Redaction exceeded the timeout of " + timeout + " milliseconds");
	}

	public RedactTimeoutException(String message, Throwable throwable) {
		super(message, throwable);
	}

}