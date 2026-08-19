/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.string.StringBundler;

import org.junit.Assert;
import org.junit.function.ThrowingRunnable;

/**
 * @author Caio Farias
 */
public class FIPSModeTestUtil {

	public static final String AUTH_CLASS_NAME = "org.jgroups.auth.X509Token";

	public static final String TRANSFORMATION_SYM = "AES/CBC/PKCS5Padding";

	public static final String XML_ASYM_ENCRYPT = StringBundler.concat(
		"<ASYM_ENCRYPT ",
		"asym_algorithm=\"RSA/ECB/OAEPWithSHA-256AndMGF1Padding\" ",
		"asym_keylength=\"2048\" sym_algorithm=\"", TRANSFORMATION_SYM,
		"\" sym_iv_length=\"16\" sym_keylength=\"128\" />");

	public static final String XML_AUTH = StringBundler.concat(
		"<AUTH auth_class=\"", AUTH_CLASS_NAME, "\" />");

	public static final String XML_SYM_ENCRYPT = StringBundler.concat(
		"<SYM_ENCRYPT sym_algorithm=\"", TRANSFORMATION_SYM,
		"\" sym_iv_length=\"16\" sym_keylength=\"128\" />");

	public static void assertSecurityException(
		String expectedMessage, ThrowingRunnable throwingRunnable) {

		SecurityException securityException = Assert.assertThrows(
			SecurityException.class, throwingRunnable);

		String message = securityException.getMessage();

		Assert.assertTrue(message, message.contains(expectedMessage));
	}

}