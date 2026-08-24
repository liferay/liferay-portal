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

		byte[] clonedBytes = bytes.clone();

		ByteBuffer byteBuffer = ByteBuffer.wrap(clonedBytes);

		byte[] byteBufferBytes = AWSByteBufferUtil.getBytes(byteBuffer);

		Assert.assertArrayEquals(bytes, byteBufferBytes);
		Assert.assertArrayEquals(new byte[bytes.length], clonedBytes);
	}

	@Test
	public void testGetBytesLeavesReadOnlyBuffer() {
		byte[] bytes = RandomTestUtil.randomBytes();

		byte[] clonedBytes = bytes.clone();

		ByteBuffer byteBuffer = ByteBuffer.wrap(clonedBytes);

		byte[] byteBufferBytes = AWSByteBufferUtil.getBytes(
			byteBuffer.asReadOnlyBuffer());

		Assert.assertArrayEquals(bytes, byteBufferBytes);
		Assert.assertArrayEquals(bytes, clonedBytes);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetBytesRejectsNull() {
		AWSByteBufferUtil.getBytes(null);
	}

}