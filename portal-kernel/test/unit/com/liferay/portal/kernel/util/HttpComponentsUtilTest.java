/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alberto Chaparro
 */
public class HttpComponentsUtilTest {

	@Test
	@TestInfo("LPD-106200")
	public void testHasHttpProtocol() {
		String host = RandomTestUtil.randomString() + ".com";

		Assert.assertFalse(HttpComponentsUtil.hasHttpProtocol("/" + host));
		Assert.assertTrue(HttpComponentsUtil.hasHttpProtocol("HTTP://" + host));
		Assert.assertTrue(
			HttpComponentsUtil.hasHttpProtocol("HTTPS://" + host));
		Assert.assertFalse(
			HttpComponentsUtil.hasHttpProtocol("file:///" + host));
		Assert.assertFalse(HttpComponentsUtil.hasHttpProtocol("ftp://" + host));
		Assert.assertTrue(HttpComponentsUtil.hasHttpProtocol("http://" + host));
		Assert.assertTrue(
			HttpComponentsUtil.hasHttpProtocol("https://" + host));
		Assert.assertFalse(
			HttpComponentsUtil.hasHttpProtocol("httpx://" + host));
		Assert.assertFalse(HttpComponentsUtil.hasHttpProtocol(host));

		Assert.assertFalse(
			HttpComponentsUtil.hasHttpProtocol(StringPool.BLANK));
		Assert.assertFalse(HttpComponentsUtil.hasHttpProtocol(null));
	}

}