/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class UnicodeFormatterTest {

	@Test
	public void testCharToHex() {
		Assert.assertEquals("abcd", UnicodeFormatter.charToHex((char)0xABCD));
		Assert.assertEquals(
			"abcd", UnicodeFormatter.charToHex((char)0xABCD, false));
		Assert.assertEquals(
			"ABCD", UnicodeFormatter.charToHex((char)0xABCD, true));
	}

}