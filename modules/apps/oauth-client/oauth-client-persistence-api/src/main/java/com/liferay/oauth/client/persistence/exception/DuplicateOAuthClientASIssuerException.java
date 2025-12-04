/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
package com.liferay.oauth.client.persistence.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class DuplicateOAuthClientASIssuerException extends PortalException {

	public DuplicateOAuthClientASIssuerException() {
	}

	public DuplicateOAuthClientASIssuerException(String msg) {
		super(msg);
	}

	public DuplicateOAuthClientASIssuerException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public DuplicateOAuthClientASIssuerException(Throwable throwable) {
		super(throwable);
	}

}