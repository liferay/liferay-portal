/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Alvaro Saugar
 */
public class UrlException extends PortalException {

	public UrlException() {
	}

	public UrlException(String msg) {
		super(msg);
	}

	public UrlException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public UrlException(Throwable throwable) {
		super(throwable);
	}

}