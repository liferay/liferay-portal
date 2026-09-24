/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class StyleBookEntryFrontendTokenException extends PortalException {

	public StyleBookEntryFrontendTokenException() {
	}

	public StyleBookEntryFrontendTokenException(String msg) {
		super(msg);
	}

	public StyleBookEntryFrontendTokenException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public StyleBookEntryFrontendTokenException(Throwable throwable) {
		super(throwable);
	}

}