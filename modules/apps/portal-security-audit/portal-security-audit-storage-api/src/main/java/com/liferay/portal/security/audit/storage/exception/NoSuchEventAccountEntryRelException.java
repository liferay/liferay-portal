/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Brian Wing Shun Chan
 */
public class NoSuchEventAccountEntryRelException extends NoSuchModelException {

	public NoSuchEventAccountEntryRelException() {
	}

	public NoSuchEventAccountEntryRelException(String msg) {
		super(msg);
	}

	public NoSuchEventAccountEntryRelException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public NoSuchEventAccountEntryRelException(Throwable throwable) {
		super(throwable);
	}

}