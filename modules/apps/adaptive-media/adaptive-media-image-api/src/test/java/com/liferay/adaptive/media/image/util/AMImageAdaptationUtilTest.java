/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.util;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Daniel Sanz
 */
@FeatureFlag("LPD-94784")
public class AMImageAdaptationUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsAdaptationDelegated() {
		Assert.assertFalse(
			AMImageAdaptationUtil.isAdaptationDelegated(
				_mockAMImageConfigurationEntry("false"), _COMPANY_ID));
		Assert.assertFalse(
			AMImageAdaptationUtil.isAdaptationDelegated(
				_mockAMImageConfigurationEntry(null), _COMPANY_ID));
		Assert.assertFalse(
			AMImageAdaptationUtil.isAdaptationDelegated(null, _COMPANY_ID));
		Assert.assertTrue(
			AMImageAdaptationUtil.isAdaptationDelegated(
				_mockAMImageConfigurationEntry("true"), _COMPANY_ID));
	}

	@FeatureFlag(enable = false, value = "LPD-94784")
	@Test
	public void testIsAdaptationDelegatedWhenTheFeatureFlagIsDisabled() {
		Assert.assertFalse(
			AMImageAdaptationUtil.isAdaptationDelegated(
				_mockAMImageConfigurationEntry("true"), _COMPANY_ID));
	}

	private AMImageConfigurationEntry _mockAMImageConfigurationEntry(
		String delegateAdaptation) {

		AMImageConfigurationEntry amImageConfigurationEntry = Mockito.mock(
			AMImageConfigurationEntry.class);

		Mockito.when(
			amImageConfigurationEntry.getProperties()
		).thenReturn(
			HashMapBuilder.put(
				"delegate-adaptation", () -> delegateAdaptation
			).build()
		);

		return amImageConfigurationEntry;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

}