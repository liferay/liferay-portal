/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.exception;

/**
 * @author Zoltán Takács
 */
public class URIPathException extends RuntimeException {

	public URIPathException() {
	}

	public URIPathException(String message) {
		super(message);
	}

	public URIPathException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public URIPathException(Throwable throwable) {
		super(throwable);
	}

}