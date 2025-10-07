/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.model;

import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
@NewEnv(type = NewEnv.Type.JVM)
public class FeatureFlagImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDependencyKeys() {
		String key = "ABC-123";
		String value = "ABC-456,XYZ-789";

		withFeatureFlag(
			featureFlag -> Assert.assertArrayEquals(
				new String[0], featureFlag.getDependencyKeys()),
			key);

		PropsUtil.set(FeatureFlagConstants.getKey(key, "dependencies"), value);

		withFeatureFlag(
			featureFlag -> Assert.assertArrayEquals(
				StringUtil.split(value), featureFlag.getDependencyKeys()),
			key);
	}

	@Test
	public void testGetDescription() {
		String key = "ABC-123";
		String value = RandomTestUtil.randomString();

		withFeatureFlag(
			featureFlag -> Assert.assertEquals(
				GetterUtil.DEFAULT_STRING, featureFlag.getDescription(null)),
			key);

		PropsUtil.set(FeatureFlagConstants.getKey(key, "description"), value);

		withFeatureFlag(
			featureFlag -> Assert.assertEquals(
				value, featureFlag.getDescription(null)),
			key);
	}

	@Test
	public void testGetTitle() {
		String key = "ABC-123";
		String value = RandomTestUtil.randomString();

		withFeatureFlag(
			featureFlag -> Assert.assertEquals(key, featureFlag.getTitle(null)),
			key);

		PropsUtil.set(FeatureFlagConstants.getKey(key, "title"), value);

		withFeatureFlag(
			featureFlag -> Assert.assertEquals(
				value, featureFlag.getTitle(null)),
			key);
	}

	@Test
	public void testGetType() {
		String betaKey = "BETA-123";
		String devKey = "DEV-123";
		String releaseKey = "RELEASE-123";

		_setType(betaKey, FeatureFlagType.BETA);
		_setType(devKey, FeatureFlagType.DEV);
		_setType(releaseKey, FeatureFlagType.RELEASE);

		withFeatureFlag(
			featureFlag -> Assert.assertEquals(
				FeatureFlagType.BETA, featureFlag.getFeatureFlagType()),
			betaKey);
		withFeatureFlag(
			featureFlag -> Assert.assertEquals(
				FeatureFlagType.DEV, featureFlag.getFeatureFlagType()),
			devKey);
		withFeatureFlag(
			featureFlag -> Assert.assertEquals(
				FeatureFlagType.RELEASE, featureFlag.getFeatureFlagType()),
			releaseKey);

		withFeatureFlag(
			featureFlag -> Assert.assertEquals(
				FeatureFlagType.DEV, featureFlag.getFeatureFlagType()),
			"ABC-123");
	}

	@Test
	public void testIsEnabled() {
		String systemKey1 = "ABC-123";
		String systemKey2 = "ABC-456";
		String systemKey3 = "ABC-789";

		PropsUtil.set(
			FeatureFlagConstants.getKey(systemKey1), Boolean.TRUE.toString());
		PropsUtil.set(
			FeatureFlagConstants.getKey(systemKey2), Boolean.FALSE.toString());

		withFeatureFlag(
			featureFlag -> Assert.assertTrue(featureFlag.isEnabled()),
			systemKey1);
		withFeatureFlag(
			featureFlag -> Assert.assertFalse(featureFlag.isEnabled()),
			systemKey2);
		withFeatureFlag(
			featureFlag -> Assert.assertFalse(featureFlag.isEnabled()),
			systemKey3);
	}

	protected void withFeatureFlag(
		Consumer<FeatureFlag> consumer, String featureFlagKey) {

		consumer.accept(new FeatureFlagImpl(featureFlagKey));
	}

	private void _setType(
		String featureFlagKey, FeatureFlagType featureFlagType) {

		PropsUtil.set(
			FeatureFlagConstants.getKey(featureFlagKey, "type"),
			featureFlagType.toString());
	}

}