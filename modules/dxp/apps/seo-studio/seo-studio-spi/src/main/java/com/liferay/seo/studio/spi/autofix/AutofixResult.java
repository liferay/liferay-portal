/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.spi.autofix;

/**
 * @author David Truong
 */
public final class AutofixResult {

	public static AutofixResult failure(String message, int status) {
		return new AutofixResult(message, status, false);
	}

	public static AutofixResult success() {
		return new AutofixResult(null, 0, true);
	}

	public String getMessage() {
		return _message;
	}

	public int getStatus() {
		return _status;
	}

	public boolean isSuccess() {
		return _success;
	}

	private AutofixResult(String message, int status, boolean success) {
		_message = message;
		_status = status;
		_success = success;
	}

	private final String _message;
	private final int _status;
	private final boolean _success;

}