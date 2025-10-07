/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Matthew Kong
 */
public class NoSuchFaroProjectUsageException extends NoSuchModelException {

	public NoSuchFaroProjectUsageException() {
	}

	public NoSuchFaroProjectUsageException(String msg) {
		super(msg);
	}

	public NoSuchFaroProjectUsageException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchFaroProjectUsageException(Throwable throwable) {
		super(throwable);
	}

}