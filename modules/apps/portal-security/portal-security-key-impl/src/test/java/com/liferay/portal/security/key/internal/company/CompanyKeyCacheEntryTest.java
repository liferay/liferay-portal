/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class CompanyKeyCacheEntryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDestroy() {
		CompanyKeyCacheEntry companyKeyCacheEntry = new CompanyKeyCacheEntry(
			Long.MAX_VALUE, _KEY_BYTES, RandomTestUtil.randomString());

		Assert.assertNotNull(companyKeyCacheEntry.getKeyBytes());

		companyKeyCacheEntry.destroy();

		Assert.assertNull(companyKeyCacheEntry.getKeyBytes());
	}

	@Test
	public void testGetKeyBytes() {
		byte[] keyBytes = _KEY_BYTES.clone();

		CompanyKeyCacheEntry companyKeyCacheEntry = new CompanyKeyCacheEntry(
			Long.MAX_VALUE, keyBytes, RandomTestUtil.randomString());

		keyBytes[0]++;

		Assert.assertArrayEquals(
			_KEY_BYTES, companyKeyCacheEntry.getKeyBytes());

		byte[] returnedKeyBytes = companyKeyCacheEntry.getKeyBytes();

		returnedKeyBytes[0]++;

		Assert.assertArrayEquals(
			_KEY_BYTES, companyKeyCacheEntry.getKeyBytes());
	}

	@Test
	public void testGetKeyString() {
		String keyString = RandomTestUtil.randomString();

		CompanyKeyCacheEntry companyKeyCacheEntry = new CompanyKeyCacheEntry(
			Long.MAX_VALUE, _KEY_BYTES, keyString);

		Assert.assertEquals(keyString, companyKeyCacheEntry.getKeyString());
	}

	@Test
	public void testIsExpired() {
		CompanyKeyCacheEntry companyKeyCacheEntry = new CompanyKeyCacheEntry(
			1000, _KEY_BYTES, RandomTestUtil.randomString());

		Assert.assertFalse(companyKeyCacheEntry.isExpired(999));
		Assert.assertTrue(companyKeyCacheEntry.isExpired(1000));
		Assert.assertTrue(companyKeyCacheEntry.isExpired(1001));
	}

	private static final byte[] _KEY_BYTES = RandomTestUtil.randomBytes();

}