/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class StringUtilTest {

	@Test
	public void testStripPrefix() {
		Set<String> testSet = new HashSet<>(
			Arrays.asList(
				"prefixTest1", "prefixTest2", "prefixTest3", "Test4"));

		testSet = StringUtil.stripPrefix("prefix", testSet);

		Assert.assertEquals(
			"Transformed string collection size", 4, testSet.size());

		Assert.assertThat(
			testSet, Matchers.hasItems("Test1", "Test2", "Test3", "Test4"));
	}

}