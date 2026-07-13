/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lucas Miranda
 */
public class FIPSAlgorithmTestUtil {

	public static <T> void assertAlgorithmSwitch(
			String algorithm, Class<T> classToMock, String fipsAlgorithm,
			UnsafeConsumer<String, Exception> unsafeConsumer,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (MockedStatic<T> mockedStatic = Mockito.mockStatic(
				classToMock, Mockito.CALLS_REAL_METHODS)) {

			unsafeRunnable.run();

			mockedStatic.verify(
				() -> unsafeConsumer.accept(algorithm), Mockito.atLeastOnce());
			mockedStatic.verify(
				() -> unsafeConsumer.accept(fipsAlgorithm), Mockito.never());
		}

		try (MockedStatic<T> mockedStatic = Mockito.mockStatic(
				classToMock, Mockito.CALLS_REAL_METHODS);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			unsafeRunnable.run();

			mockedStatic.verify(
				() -> unsafeConsumer.accept(algorithm), Mockito.never());
			mockedStatic.verify(
				() -> unsafeConsumer.accept(fipsAlgorithm),
				Mockito.atLeastOnce());
		}
	}

}