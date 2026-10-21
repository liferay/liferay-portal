/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.test.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.function.Supplier;

/**
 * @author Daniel Raposo
 */
public class FeatureFlagManagerTestUtil {

	public static SafeCloseable swapWithSafeCloseable(
		FeatureFlagManager featureFlagManager) {

		Snapshot<FeatureFlagManager> featureFlagManagerSnapshot =
			ReflectionTestUtil.getFieldValue(
				FeatureFlagManagerUtil.class, "_featureFlagManagerSnapshot");

		Supplier<Object> serviceSupplier = ReflectionTestUtil.getFieldValue(
			featureFlagManagerSnapshot, "_serviceSupplier");

		ReflectionTestUtil.setFieldValue(
			featureFlagManagerSnapshot, "_serviceSupplier",
			(Supplier<Object>)() -> featureFlagManager);

		return () -> ReflectionTestUtil.setFieldValue(
			featureFlagManagerSnapshot, "_serviceSupplier", serviceSupplier);
	}

}