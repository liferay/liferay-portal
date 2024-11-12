/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.test.rule.AbstractTestRule;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

		return _updateFeatureFlags(description);
	}

	@Override
	protected Map<String, String> beforeMethod(
			Description description, Object target)
		throws Throwable {

		return _updateFeatureFlags(description);
	}

	private void _restoreFeatureFlags(Map<String, String> previousValues) {
		PropsUtil.addProperties(
			UnicodePropertiesBuilder.create(
				previousValues, true
			).build());
	}

	private Map<String, String> _updateFeatureFlags(Description description) {
		FeatureFlags featureFlags = description.getAnnotation(
			FeatureFlags.class);

		if (featureFlags == null) {
			return Collections.emptyMap();
		}

		Map<String, String> previousValues = new HashMap<>();

		for (String key : featureFlags.value()) {
			String featureFlagKey = "feature.flag." + key;

			String previousValue = PropsUtil.get(featureFlagKey);

			if (Validator.isNotNull(previousValue)) {
				previousValues.put(featureFlagKey, previousValue);
			}

			PropsUtil.addProperties(
				UnicodePropertiesBuilder.setProperty(
					featureFlagKey, String.valueOf(featureFlags.enable())
				).build());
		}

		return previousValues;
	}

}