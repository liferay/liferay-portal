/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Arthur Chan
 */
public class NoSuchUserException extends NoSuchModelException {

	public NoSuchUserException() {
	}

	public NoSuchUserException(String msg) {
		super(msg);
	}

	public NoSuchUserException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchUserException(Throwable throwable) {
		super(throwable);
	}

}