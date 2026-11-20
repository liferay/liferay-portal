/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.set.util.comparator;

import com.liferay.launch.set.details.LaunchSetDetails;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carlos Correa
 */
public class LaunchSetDetailsNameComparatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCompare() {
		LaunchSetDetails launchSetDetails1 = new LaunchSetDetails(
			RandomTestUtil.randomLong(), "a");
		LaunchSetDetails launchSetDetails2 = new LaunchSetDetails(
			RandomTestUtil.randomLong(), "B");

		Assert.assertEquals(
			-1,
			Integer.signum(
				LaunchSetDetailsNameComparator.INSTANCE.compare(
					launchSetDetails1, launchSetDetails2)));
		Assert.assertEquals(
			1,
			Integer.signum(
				LaunchSetDetailsNameComparator.INSTANCE.compare(
					launchSetDetails2, launchSetDetails1)));

		Assert.assertEquals(
			0,
			LaunchSetDetailsNameComparator.INSTANCE.compare(
				launchSetDetails1,
				new LaunchSetDetails(RandomTestUtil.randomLong(), "A")));
	}

}