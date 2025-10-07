/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AbstractTestRule;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.runner.Description;

/**
 * @author Alejandro Tardín
 */
public class FeatureFlagTestRule
	extends AbstractTestRule<Map<String, String>, Map<String, String>> {

	public static final FeatureFlagTestRule INSTANCE =
		new FeatureFlagTestRule();

	@Override
	protected void afterClass(
			Description description, Map<String, String> previousValues)
		throws Throwable {

		_restoreFeatureFlags(previousValues);

		ReflectionTestUtil.setFieldValue(
			FeatureFlagManagerUtil.class, "_featureFlagManagerSnapshot",
			new Snapshot<>(
				FeatureFlagManagerUtil.class, FeatureFlagManager.class));
	}

	@Override
	protected void afterMethod(
			Description description, Map<String, String> previousValues,
			Object target)
		throws Throwable {

		_restoreFeatureFlags(previousValues);
	}

	@Override
	protected Map<String, String> beforeClass(Description description)
		throws Throwable {

		Snapshot<FeatureFlagManager> featureFlagManagerSnapshot =
			ReflectionTestUtil.getFieldValue(
				FeatureFlagManagerUtil.class, "_featureFlagManagerSnapshot");

		FeatureFlagManager featureFlagManager =
			featureFlagManagerSnapshot.get();

		if (featureFlagManager != null) {
			ReflectionTestUtil.setFieldValue(
				featureFlagManagerSnapshot, "_serviceSupplier",
				(Supplier<Object>)() -> new MockFeatureFlagManager(
					featureFlagManager));
		}

		return _updateFeatureFlags(description);
	}

	@Override
	protected Map<String, String> beforeMethod(
			Description description, Object target)
		throws Throwable {

		return _updateFeatureFlags(description);
	}

	private void _restoreFeatureFlags(Map<String, String> previousValues) {
		for (Map.Entry<String, String> entry : previousValues.entrySet()) {
			PropsUtil.set(entry.getKey(), entry.getValue());
		}
	}

	private KeyValuePair _updateFeatureFlag(FeatureFlag featureFlag) {
		String featureFlagKey = FeatureFlagConstants.getKey(
			featureFlag.value());

		KeyValuePair previousKeyValuePair = new KeyValuePair(
			featureFlagKey, PropsUtil.get(featureFlagKey));

		PropsUtil.set(featureFlagKey, String.valueOf(featureFlag.enable()));

		return previousKeyValuePair;
	}

	private Map<String, String> _updateFeatureFlags(Description description) {
		Map<String, String> previousValues = new HashMap<>();

		FeatureFlags featureFlags = description.getAnnotation(
			FeatureFlags.class);

		if (featureFlags != null) {
			for (FeatureFlag featureFlag : featureFlags.featureFlags()) {
				if (featureFlag == null) {
					continue;
				}

				KeyValuePair previousKeyValuePair = _updateFeatureFlag(
					featureFlag);

				previousValues.put(
					previousKeyValuePair.getKey(),
					previousKeyValuePair.getValue());
			}
		}

		FeatureFlag featureFlag = description.getAnnotation(FeatureFlag.class);

		if (featureFlag != null) {
			KeyValuePair previousKeyValuePair = _updateFeatureFlag(featureFlag);

			previousValues.put(
				previousKeyValuePair.getKey(), previousKeyValuePair.getValue());
		}

		return previousValues;
	}

	private static class MockFeatureFlagManager implements FeatureFlagManager {

		@Override
		public List<com.liferay.portal.kernel.feature.flag.FeatureFlag>
			getFeatureFlags(
				long companyId,
				Predicate<com.liferay.portal.kernel.feature.flag.FeatureFlag>
					predicate) {

			return _featureFlagManager.getFeatureFlags(companyId, predicate);
		}

		@Override
		public String getJSON(long companyId) {
			return ReflectionTestUtil.getFieldValue(
				FeatureFlagManagerUtil.class, "_JSON");
		}

		@Override
		public boolean isEnabled(long companyId, String key) {
			return GetterUtil.getBoolean(PropsUtil.get("feature.flag." + key));
		}

		@Override
		public boolean isEnabled(String key) {
			return GetterUtil.getBoolean(PropsUtil.get("feature.flag." + key));
		}

		private MockFeatureFlagManager(FeatureFlagManager featureFlagManager) {
			_featureFlagManager = featureFlagManager;
		}

		private final FeatureFlagManager _featureFlagManager;

	}

}