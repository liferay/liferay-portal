/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
public class SecretException extends PortalException {

	public SecretException() {
	}

	public SecretException(String msg) {
		super(msg);
	}

	public SecretException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public SecretException(Throwable throwable) {
		super(throwable);
	}

}