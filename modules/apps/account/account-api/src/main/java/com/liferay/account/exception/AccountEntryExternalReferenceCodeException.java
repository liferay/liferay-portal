/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Tancredi Covioli
 */
public class AccountEntryExternalReferenceCodeException
	extends PortalException {

	public AccountEntryExternalReferenceCodeException() {
	}

	public AccountEntryExternalReferenceCodeException(String msg) {
		super(msg);
	}

	public AccountEntryExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public AccountEntryExternalReferenceCodeException(Throwable throwable) {
		super(throwable);
	}

}