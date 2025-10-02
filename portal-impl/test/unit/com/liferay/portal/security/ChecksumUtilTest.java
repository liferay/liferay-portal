/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security;

import com.liferay.portal.kernel.security.ChecksumUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class ChecksumUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAppendChecksum() throws Exception {
		Assert.assertNull(ChecksumUtil.appendChecksum(null));
		Assert.assertTrue(
			Arrays.equals(
				new byte[0], ChecksumUtil.appendChecksum(new byte[0])));

		for (byte[] valid : _VALID_BYTES_WITH_CHECKSUM) {
			byte[] result = ChecksumUtil.appendChecksum(
				Arrays.copyOf(valid, valid.length - 1));

			Assert.assertTrue(Arrays.equals(valid, result));
		}
	}

	@Test
	public void testIsValid() {
		Assert.assertFalse(ChecksumUtil.isValid(null));
		Assert.assertFalse(ChecksumUtil.isValid(new byte[0]));

		for (byte[] valid : _VALID_BYTES_WITH_CHECKSUM) {
			Assert.assertTrue(ChecksumUtil.isValid(valid));
		}

		for (byte[] valid : _VALID_BYTES_WITH_CHECKSUM) {
			byte[] bytes = ChecksumUtil.appendChecksum(valid);

			byte validChecksum = bytes[bytes.length - 1];

			for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
				bytes[bytes.length - 1] = b;

				if (b == validChecksum) {
					Assert.assertTrue(ChecksumUtil.isValid(bytes));
				}
				else {
					Assert.assertFalse(ChecksumUtil.isValid(bytes));
				}
			}
		}
	}

	@Test
	public void testRemoveChecksum() {
		Assert.assertNull(ChecksumUtil.removeChecksum(null));
		Assert.assertNull(ChecksumUtil.removeChecksum(new byte[0]));

		for (byte[] valid : _VALID_BYTES_WITH_CHECKSUM) {
			byte[] result = ChecksumUtil.removeChecksum(valid);

			Assert.assertNotNull(result);
			Assert.assertTrue(result.length == (valid.length - 1));
		}
	}

	private static final byte[][] _VALID_BYTES_WITH_CHECKSUM = {
		{0, 0}, {1, (byte)0xff}, {(byte)0xff, 1}, {1, 0, (byte)0xff},
		{1, (byte)0xff, 0}, {0, 1, (byte)0xff}, {0, (byte)0xff, 1},
		{(byte)0xff, 0, 1}, {(byte)0xff, 1, 0}
	};

}