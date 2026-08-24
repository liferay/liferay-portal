/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.portal.kernel.test.util.DependenciesTestUtil;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;

/**
 * @author Caio Farias
 */
public class FIPSModeTestUtil {

	public static final String AUTH_CLASS_NAME = "org.jgroups.auth.X509Token";

	public static void assertSecurityException(
		String expectedMessage, ThrowingRunnable throwingRunnable) {

		SecurityException securityException = Assert.assertThrows(
			SecurityException.class, throwingRunnable);

		String message = securityException.getMessage();

		Assert.assertTrue(message, message.contains(expectedMessage));
	}

	public static String getChannelPropertiesLocation(String fileName)
		throws Exception {

		return String.valueOf(
			DependenciesTestUtil.getDependencyAsFile(
				FIPSModeTestUtil.class, fileName));
	}

}