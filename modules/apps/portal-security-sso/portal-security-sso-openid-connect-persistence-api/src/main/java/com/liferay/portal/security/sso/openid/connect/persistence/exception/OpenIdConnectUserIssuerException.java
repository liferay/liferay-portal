/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Arthur Chan
 */
public class OpenIdConnectUserIssuerException extends PortalException {

	public OpenIdConnectUserIssuerException() {
	}

	public OpenIdConnectUserIssuerException(String msg) {
		super(msg);
	}

	public OpenIdConnectUserIssuerException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public OpenIdConnectUserIssuerException(Throwable throwable) {
		super(throwable);
	}

}