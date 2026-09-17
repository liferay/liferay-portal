/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Calum Ragan
 */
public class ThrowableUtil {

	public static <T extends Throwable> T getNestedThrowable(
		Throwable throwable, Class<T> throwableClass) {

		List<Throwable> throwables = new ArrayList<>();

		if (throwable != null) {
			throwables.add(throwable);
		}

		for (int i = 0; (i < throwables.size()) && (i < _MAX_THROWABLE_COUNT);
			 i++) {

			Throwable curThrowable = throwables.get(i);

			if (throwableClass.isInstance(curThrowable)) {
				return throwableClass.cast(curThrowable);
			}

			Throwable causeThrowable = curThrowable.getCause();

			if ((causeThrowable != null) &&
				!throwables.contains(causeThrowable)) {

				throwables.add(causeThrowable);
			}

			for (Throwable suppressedThrowable : curThrowable.getSuppressed()) {
				if (!throwables.contains(suppressedThrowable)) {
					throwables.add(suppressedThrowable);
				}
			}
		}

		return null;
	}

	private static final int _MAX_THROWABLE_COUNT = 100;

}