/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.tunnel;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Key;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class TunnelAuthenticationManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetSharedSecretKey() throws Exception {
		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_ENCRYPTION_ALGORITHM", "Blowfish");
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET", _SHARED_SECRET);
			SafeCloseable safeCloseable4 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", false)) {

			Key key = _getSharedSecretKey();

			Assert.assertEquals("Blowfish", key.getAlgorithm());
		}

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable("FIPS_ENABLED", true);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET", _SHARED_SECRET);
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", false)) {

			try (SafeCloseable safeCloseable4 =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"TUNNELING_SERVLET_ENCRYPTION_ALGORITHM", "AES")) {

				Key key = _getSharedSecretKey();

				Assert.assertEquals("AES", key.getAlgorithm());
			}
		}
	}

	private Key _getSharedSecretKey() {
		return ReflectionTestUtil.invoke(
			new TunnelAuthenticationManagerImpl(), "getSharedSecretKey",
			new Class<?>[0]);
	}

	private static final String _SHARED_SECRET = RandomTestUtil.randomString(
		16);

}