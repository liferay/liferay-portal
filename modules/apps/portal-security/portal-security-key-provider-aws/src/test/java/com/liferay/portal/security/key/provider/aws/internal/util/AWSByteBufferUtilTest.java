/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christopher Kian
 */
public class AWSByteBufferUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetBytes() {
		byte[] bytes = RandomTestUtil.randomBytes();

		byte[] backingArray = bytes.clone();

		byte[] result = AWSByteBufferUtil.getBytes(
			ByteBuffer.wrap(backingArray));

		Assert.assertArrayEquals(bytes, result);

		Assert.assertArrayEquals(new byte[bytes.length], backingArray);
	}

	@Test
	public void testGetBytesLeavesReadOnlyBuffer() {
		byte[] bytes = RandomTestUtil.randomBytes();

		byte[] backingArray = bytes.clone();

		ByteBuffer byteBuffer = ByteBuffer.wrap(backingArray);

		byte[] result = AWSByteBufferUtil.getBytes(
			byteBuffer.asReadOnlyBuffer());

		Assert.assertArrayEquals(bytes, backingArray);
		Assert.assertArrayEquals(bytes, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetBytesRejectsNull() {
		AWSByteBufferUtil.getBytes(null);
	}

}