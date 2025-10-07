/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.feature.flag;

import com.liferay.feature.flag.web.internal.model.FeatureFlagImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsBagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_expectedFeatureFlags = new FeatureFlag[] {
			_betaFeatureFlag, _deprecationFeatureFlag, _devFeatureFlag,
			_releaseFeatureFlag
		};

		Map<String, FeatureFlag> featureFlagsMap = new TreeMap<>();

		for (FeatureFlag featureFlag : _expectedFeatureFlags) {
			featureFlagsMap.put(featureFlag.getKey(), featureFlag);
		}

		_featureFlagsBag = new FeatureFlagsBag(_COMPANY_ID, featureFlagsMap);
	}

	@Test
	public void testGetFeatureFlags() {
		List<FeatureFlag> actualFeatureFlags = _featureFlagsBag.getFeatureFlags(
			null);

		Assert.assertEquals(
			actualFeatureFlags.toString(), _expectedFeatureFlags.length,
			actualFeatureFlags.size());

		actualFeatureFlags = _featureFlagsBag.getFeatureFlags(
			FeatureFlagType.BETA.getPredicate());

		Assert.assertEquals(
			actualFeatureFlags.toString(), 1, actualFeatureFlags.size());

		FeatureFlag featureFlag = actualFeatureFlags.get(0);

		Assert.assertEquals(_betaFeatureFlag.getKey(), featureFlag.getKey());
	}

	@Test
	public void testGetJSON() throws JSONException {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_featureFlagsBag.getJSON());

		Set<String> set = jsonObject.keySet();

		Assert.assertEquals(
			set.toString(), _expectedFeatureFlags.length, set.size());

		for (FeatureFlag featureFlag : _expectedFeatureFlags) {
			Assert.assertEquals(
				featureFlag.isEnabled(),
				jsonObject.getBoolean(featureFlag.getKey()));
		}
	}

	@Test
	public void testIsEnabled() {
		for (FeatureFlag expectedFeatureFlag : _expectedFeatureFlags) {
			Assert.assertEquals(
				expectedFeatureFlag.isEnabled(),
				_featureFlagsBag.isEnabled(expectedFeatureFlag.getKey()));
		}

		String randomKey = _createKey();

		try {
			_featureFlagsBag.isEnabled(randomKey);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Feature flag ", randomKey,
					" is not available for company ", _COMPANY_ID),
				illegalStateException.getMessage());
		}
	}

	private FeatureFlag _createFeatureFlag(FeatureFlagType featureFlagType) {
		return new FeatureFlagImpl(
			new String[0], RandomTestUtil.randomString(),
			RandomTestUtil.randomBoolean(), featureFlagType, _createKey(),
			RandomTestUtil.randomString());
	}

	private String _createKey() {
		return RandomTestUtil.randomString(
			5, NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);
	}

	private static final long _COMPANY_ID = 1L;

	private final FeatureFlag _betaFeatureFlag = _createFeatureFlag(
		FeatureFlagType.BETA);
	private final FeatureFlag _deprecationFeatureFlag = _createFeatureFlag(
		FeatureFlagType.DEPRECATION);
	private final FeatureFlag _devFeatureFlag = _createFeatureFlag(
		FeatureFlagType.DEV);
	private FeatureFlag[] _expectedFeatureFlags;
	private FeatureFlagsBag _featureFlagsBag;
	private final FeatureFlag _releaseFeatureFlag = _createFeatureFlag(
		FeatureFlagType.RELEASE);

}