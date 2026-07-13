/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.test.util.FIPSAlgorithmTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.security.MessageDigest;

import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class DigesterUtilTest {

	@Test
	public void test() throws Exception {
		FIPSAlgorithmTestUtil.assertAlgorithmSwitch(
			DigesterUtil.SHA, MessageDigest.class, DigesterUtil.SHA_256,
			MessageDigest::getInstance,
			() -> DigesterUtil.digestHex(RandomTestUtil.randomString()));
	}

}