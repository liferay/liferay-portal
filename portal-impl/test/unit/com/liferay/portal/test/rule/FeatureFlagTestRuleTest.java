/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
@FeatureFlags("CLASS-123")
public class FeatureFlagTestRuleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAnnotateClass() throws Exception {
		Assert.assertTrue(FeatureFlagManagerUtil.isEnabled("CLASS-123"));
	}

	@FeatureFlags("METHOD-123")
	@Test
	public void testAnnotateMethod() throws Exception {
		Assert.assertTrue(FeatureFlagManagerUtil.isEnabled("METHOD-123"));
	}

	@FeatureFlags(enable = false, value = "DISABLED-123")
	@Test
	public void testDisableFeatureFlag() {
		Assert.assertFalse(FeatureFlagManagerUtil.isEnabled("DISABLED-123"));
	}

}