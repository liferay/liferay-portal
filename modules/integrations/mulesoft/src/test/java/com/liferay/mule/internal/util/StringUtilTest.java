/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Matija Petanjek
 */
public class StringUtilTest {

	@Test
	public void testReplace() {
		String template = "HelloTPL1WorldTPL2";

		template = StringUtil.replace(
			template, "TPL2", "World", "TPL1", "Hello");

		Assert.assertEquals("HelloHelloWorldWorld", template);
	}

}