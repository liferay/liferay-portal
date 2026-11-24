/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.util;

import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lucas Miranda
 */
public class OAuth2SecureRandomGeneratorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			SecureRandomUtil.nextLong()
		).thenReturn(
			0x0102030405060708L
		);
	}

	@After
	public void tearDown() {
		_secureRandomUtilMockedStatic.close();
	}

	@Test
	public void testGenerateClientId() {
		Assert.assertEquals(
			"id-01020304-0506-0708-0102-030405060708",
			OAuth2SecureRandomGenerator.generateClientId());
	}

	@Test
	public void testGenerateClientSecret() {
		Assert.assertEquals(
			"secret-01020304-0506-0708-0102-030405060708",
			OAuth2SecureRandomGenerator.generateClientSecret());
	}

	private final MockedStatic<SecureRandomUtil> _secureRandomUtilMockedStatic =
		Mockito.mockStatic(SecureRandomUtil.class);

}