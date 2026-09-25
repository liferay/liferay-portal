/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carlos Correa
 */
public class FeatureFlagTestUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSetFeatureFlagsWithSafeCloseable() {
		long companyId = RandomTestUtil.randomLong();

		Assert.assertFalse(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-123"));
		Assert.assertFalse(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-456"));

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					true, "METHOD-123", "METHOD-456")) {

			Assert.assertTrue(
				FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-123"));
			Assert.assertTrue(
				FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-456"));
		}

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					true, "METHOD-456")) {

			Assert.assertFalse(
				FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-123"));
			Assert.assertTrue(
				FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-456"));
		}

		Assert.assertFalse(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-123"));
		Assert.assertFalse(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-456"));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("METHOD-123"), @FeatureFlag("METHOD-456")}
	)
	@Test
	public void testSetFeatureFlagsWithSafeCloseableDisable() {
		long companyId = RandomTestUtil.randomLong();

		Assert.assertTrue(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-123"));
		Assert.assertTrue(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-456"));

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "METHOD-123")) {

			Assert.assertFalse(
				FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-123"));
			Assert.assertTrue(
				FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-456"));
		}

		Assert.assertTrue(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-123"));
		Assert.assertTrue(
			FeatureFlagManagerUtil.isEnabled(companyId, "METHOD-456"));
	}

}