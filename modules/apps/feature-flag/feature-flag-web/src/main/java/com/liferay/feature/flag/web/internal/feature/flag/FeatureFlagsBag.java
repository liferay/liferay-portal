/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.feature.flag;

import com.liferay.feature.flag.web.internal.model.FeatureFlagWrapper;
import com.liferay.feature.flag.web.internal.model.PreferenceAwareFeatureFlag;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public class FeatureFlagsBag {

	public FeatureFlagsBag(
		long companyId, Map<String, FeatureFlag> featureFlagsMap) {

		_companyId = companyId;
		_featureFlagsMap = featureFlagsMap;
	}

	public FeatureFlag getFeatureFlag(String key) {
		return _featureFlagsMap.get(key);
	}

	public List<FeatureFlag> getFeatureFlags(Predicate<FeatureFlag> predicate) {
		if (predicate == null) {
			return new ArrayList<>(_featureFlagsMap.values());
		}

		List<FeatureFlag> featureFlags = new ArrayList<>();

		for (FeatureFlag featureFlag : _featureFlagsMap.values()) {
			if (predicate.test(featureFlag)) {
				featureFlags.add(featureFlag);
			}
		}

		return featureFlags;
	}

	public String getJSON() {
		if (_featureFlagsMap.isEmpty()) {
			return _FEATURE_FLAGS_JSON;
		}

		String json = _json;

		if (json == null) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			for (FeatureFlag featureFlag : _featureFlagsMap.values()) {
				jsonObject.put(featureFlag.getKey(), featureFlag.isEnabled());
			}

			json = jsonObject.toString();

			_json = json;
		}

		return json;
	}

	public boolean isEnabled(String key) {
		FeatureFlag featureFlag = _featureFlagsMap.get(key);

		if (featureFlag != null) {
			return featureFlag.isEnabled();
		}

		throw new IllegalStateException(
			StringBundler.concat(
				"Feature flag ", key, " is not available for company ",
				_companyId));
	}

	public void setEnabled(String key, boolean enabled) {
		FeatureFlag featureFlag = _featureFlagsMap.get(key);

		if ((featureFlag == null) || (enabled == featureFlag.isEnabled())) {
			return;
		}

		while (featureFlag instanceof FeatureFlagWrapper) {
			if (featureFlag instanceof PreferenceAwareFeatureFlag) {
				PreferenceAwareFeatureFlag preferenceAwareFeatureFlag =
					(PreferenceAwareFeatureFlag)featureFlag;

				preferenceAwareFeatureFlag.setEnabled(enabled);

				_json = null;

				break;
			}

			FeatureFlagWrapper featureFlagWrapper =
				(FeatureFlagWrapper)featureFlag;

			featureFlag = featureFlagWrapper.getFeatureFlag();
		}
	}

	private static final String _FEATURE_FLAGS_JSON = String.valueOf(
		JSONFactoryUtil.createJSONObject(
			PropsUtil.getProperties("feature.flag.", true)));

	private final long _companyId;
	private final Map<String, FeatureFlag> _featureFlagsMap;
	private volatile String _json;

}