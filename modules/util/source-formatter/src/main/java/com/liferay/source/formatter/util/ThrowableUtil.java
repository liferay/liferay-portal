/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

/**
 * @author Calum Ragan
 */
public class ThrowableUtil {

	public static <T extends Throwable> T getNestedThrowable(
		Throwable throwable, Class<T> throwableClass) {

		Throwable curThrowable = throwable;

		for (int i = 0; (curThrowable != null) && (i < _MAX_CAUSE_DEPTH); i++) {
			if (throwableClass.isInstance(curThrowable)) {
				return throwableClass.cast(curThrowable);
			}

			curThrowable = curThrowable.getCause();
		}

		return null;
	}

	private static final int _MAX_CAUSE_DEPTH = 100;

}