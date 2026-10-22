/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Iván Zaera Avellón
 */
public class CompressionStrategyTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFromValue() {
		for (CompressionStrategy compressionStrategy :
				CompressionStrategy.values()) {

			Assert.assertEquals(
				compressionStrategy,
				CompressionStrategy.fromValue(
					compressionStrategy.getValue(),
					CompressionStrategy.DO_NOT_COMPRESS));
		}
	}

	@Test
	public void testFromValueWhenValueIsInvalid() {
		for (String value :
				new String[] {
					null, StringPool.BLANK, RandomTestUtil.randomString()
				}) {

			Assert.assertEquals(
				CompressionStrategy.USE_SYSTEM_DEFAULTS,
				CompressionStrategy.fromValue(
					value, CompressionStrategy.USE_SYSTEM_DEFAULTS));
		}
	}

}