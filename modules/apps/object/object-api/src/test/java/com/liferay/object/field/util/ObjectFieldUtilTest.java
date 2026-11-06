/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.util;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ObjectFieldUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDateTimePattern() {
		String value = "2026-11-05T07:24:33";

		Assert.assertEquals(
			LocalDateTime.of(2026, 11, 5, 7, 24, 33),
			LocalDateTime.parse(
				value,
				DateTimeFormatter.ofPattern(
					ObjectFieldUtil.getDateTimePattern(value))));
	}

}