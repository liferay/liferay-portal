/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
package com.liferay.production.readiness.ignore.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Brian Wing Shun Chan
 */
public class NoSuchProductionReadinessIgnoreException extends NoSuchModelException {

	public NoSuchProductionReadinessIgnoreException() {
	}

	public NoSuchProductionReadinessIgnoreException(String msg) {
		super(msg);
	}

	public NoSuchProductionReadinessIgnoreException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchProductionReadinessIgnoreException(Throwable throwable) {
		super(throwable);
	}

}