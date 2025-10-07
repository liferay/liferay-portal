/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.exception;

/**
 * @author Igor Beslic
 */
public class BaseComponentException extends RuntimeException {

	public BaseComponentException(String message, int httpStatus) {
		super(message);

		_httpStatus = httpStatus;
	}

	public BaseComponentException(
		String message, int httpStatus, Throwable throwable) {

		super(message, throwable);

		_httpStatus = httpStatus;
	}

	public int getHttpStatus() {
		return _httpStatus;
	}

	private final int _httpStatus;

}