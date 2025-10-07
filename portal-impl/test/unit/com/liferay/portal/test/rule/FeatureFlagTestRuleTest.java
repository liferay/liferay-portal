/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PropsUtil;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("CLASS-123"),
		@FeatureFlag(enable = false, value = "CLASS-456")
	}
)
public class FeatureFlagTestRuleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws PortalException {
		PropsUtil.set(
			FeatureFlagConstants.PORTAL_PROPERTY_KEY_FEATURE_FLAG +
				".METHOD-456",
			"true");
	}

	@Test
	public void testAnnotateClass() throws Exception {
		Assert.assertTrue(FeatureFlagManagerUtil.isEnabled("CLASS-123"));
		Assert.assertFalse(FeatureFlagManagerUtil.isEnabled("CLASS-456"));
	}

	@FeatureFlag("METHOD-123")
	@Test
	public void testAnnotateMethod() throws Exception {
		Assert.assertTrue(FeatureFlagManagerUtil.isEnabled("METHOD-123"));
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag("METHOD-123"),
			@FeatureFlag(enable = false, value = "METHOD-456")
		}
	)
	@Test
	public void testAnnotateMethodWithFeatureFlags() throws Exception {
		Assert.assertTrue(FeatureFlagManagerUtil.isEnabled("METHOD-123"));
		Assert.assertFalse(FeatureFlagManagerUtil.isEnabled("METHOD-456"));
	}

	@FeatureFlag(enable = false, value = "METHOD-456")
	@Test
	public void testDisableFeatureFlag() {
		Assert.assertFalse(FeatureFlagManagerUtil.isEnabled("METHOD-456"));
	}

}