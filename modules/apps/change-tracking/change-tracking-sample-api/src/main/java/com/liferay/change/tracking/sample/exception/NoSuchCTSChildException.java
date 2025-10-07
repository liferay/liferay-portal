/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Brian Wing Shun Chan
 */
public class NoSuchCTSChildException extends NoSuchModelException {

	public NoSuchCTSChildException() {
	}

	public NoSuchCTSChildException(String msg) {
		super(msg);
	}

	public NoSuchCTSChildException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchCTSChildException(Throwable throwable) {
		super(throwable);
	}

}