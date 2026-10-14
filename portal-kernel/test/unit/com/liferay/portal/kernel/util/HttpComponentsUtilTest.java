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
	public void testHasHTTPProtocol() {
		String host = RandomTestUtil.randomString() + ".com";

		Assert.assertFalse(HttpComponentsUtil.hasHTTPProtocol("/" + host));
		Assert.assertTrue(HttpComponentsUtil.hasHTTPProtocol("HTTP://" + host));
		Assert.assertTrue(
			HttpComponentsUtil.hasHTTPProtocol("HTTPS://" + host));
		Assert.assertFalse(
			HttpComponentsUtil.hasHTTPProtocol("file:///" + host));
		Assert.assertFalse(HttpComponentsUtil.hasHTTPProtocol("ftp://" + host));
		Assert.assertTrue(HttpComponentsUtil.hasHTTPProtocol("http://" + host));
		Assert.assertTrue(
			HttpComponentsUtil.hasHTTPProtocol("https://" + host));
		Assert.assertFalse(
			HttpComponentsUtil.hasHTTPProtocol("httpx://" + host));
		Assert.assertFalse(HttpComponentsUtil.hasHTTPProtocol(host));

		Assert.assertFalse(
			HttpComponentsUtil.hasHTTPProtocol(StringPool.BLANK));
		Assert.assertFalse(HttpComponentsUtil.hasHTTPProtocol(null));
	}

}