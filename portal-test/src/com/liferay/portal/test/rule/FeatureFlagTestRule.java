/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AbstractTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.runner.Description;

/**
 * @author Alejandro Tardín
 */
public class FeatureFlagTestRule
	extends AbstractTestRule<SafeCloseable, SafeCloseable> {

	public static final FeatureFlagTestRule INSTANCE =
		new FeatureFlagTestRule();

	@Override
	protected void afterClass(
			Description description, SafeCloseable safeCloseable)
		throws Throwable {

		safeCloseable.close();

		if (_serviceSupplier == null) {
			return;
		}

		Snapshot<FeatureFlagManager> featureFlagManagerSnapshot =
			ReflectionTestUtil.getFieldValue(
				FeatureFlagManagerUtil.class, "_featureFlagManagerSnapshot");

		ReflectionTestUtil.setFieldValue(
			featureFlagManagerSnapshot, "_serviceSupplier", _serviceSupplier);

		_serviceSupplier = null;
	}

	@Override
	protected void afterMethod(
			Description description, SafeCloseable safeCloseable, Object target)
		throws Throwable {

		safeCloseable.close();
	}

	@Override
	protected SafeCloseable beforeClass(Description description)
		throws Throwable {

		Snapshot<FeatureFlagManager> featureFlagManagerSnapshot =
			ReflectionTestUtil.getFieldValue(
				FeatureFlagManagerUtil.class, "_featureFlagManagerSnapshot");

		FeatureFlagManager featureFlagManager =
			featureFlagManagerSnapshot.get();

		if (featureFlagManager != null) {
			if (_serviceSupplier == null) {
				_serviceSupplier = ReflectionTestUtil.getFieldValue(
					featureFlagManagerSnapshot, "_serviceSupplier");
			}

			ReflectionTestUtil.setFieldValue(
				featureFlagManagerSnapshot, "_serviceSupplier",
				(Supplier<Object>)() -> new MockFeatureFlagManager(
					featureFlagManager));
		}

		return _setFeatureFlags(description);
	}

	@Override
	protected SafeCloseable beforeMethod(Description description, Object target)
		throws Throwable {

		return _setFeatureFlags(description);
	}

	private SafeCloseable _setFeatureFlags(Description description) {
		List<SafeCloseable> safeCloseables = new ArrayList<>();

		FeatureFlags featureFlags = description.getAnnotation(
			FeatureFlags.class);

		if (featureFlags != null) {
			for (FeatureFlag featureFlag : featureFlags.featureFlags()) {
				if (featureFlag == null) {
					continue;
				}

				safeCloseables.add(
					FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
						featureFlag.enable(), featureFlag.value()));
			}
		}

		FeatureFlag featureFlag = description.getAnnotation(FeatureFlag.class);

		if (featureFlag != null) {
			safeCloseables.add(
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					featureFlag.enable(), featureFlag.value()));
		}

		return () -> {
			for (SafeCloseable safeCloseable : safeCloseables) {
				safeCloseable.close();
			}
		};
	}

	private Supplier<Object> _serviceSupplier;

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