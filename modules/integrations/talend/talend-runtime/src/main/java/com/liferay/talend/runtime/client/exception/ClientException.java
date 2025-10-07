/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.client.exception;

import com.liferay.talend.exception.BaseComponentException;

/**
 * @author Igor Beslic
 */
public class ClientException extends BaseComponentException {

	public ClientException(String message) {
		super(message, 0);
	}

	public ClientException(String message, int httpStatus) {
		super(message, httpStatus);
	}

	public ClientException(
		String message, int httpStatus, Throwable throwable) {

		super(message, httpStatus, throwable);
	}

}