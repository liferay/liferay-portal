/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.http;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.fips.FIPSModeTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Test;

/**
 * @author Caio Farias
 */
public class TunnelUtilTest {

	@Test
	public void testGetConnection() throws Exception {
		String hostName = RandomTestUtil.randomString();
		int hostPort = RandomTestUtil.randomInt(1, 65535);

		HttpPrincipal httpPrincipal = new HttpPrincipal(
			StringBundler.concat("https://", hostName, ":", hostPort));

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					TunnelUtil.class, "_VERIFY_SSL_HOSTNAME", false);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeTestUtil.assertSecurityException(
				"SSL hostname verification must be enabled in FIPS mode",
				() -> ReflectionTestUtil.invoke(
					TunnelUtil.class, "_getConnection",
					new Class<?>[] {HttpPrincipal.class}, httpPrincipal));
		}
	}

}