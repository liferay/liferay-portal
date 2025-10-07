/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.test.util;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Objects;
import java.util.Properties;

import org.junit.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Drew Brokke
 */
public class FeatureFlagTestHelper {

	public static final String FEATURE_FLAG_KEY_1 = "FAKE-123";

	public static final String FEATURE_FLAG_KEY_2 = "FAKE-456";

	public static final String FEATURE_FLAG_KEY_SYSTEM = "FAKE-000";

	public FeatureFlagTestHelper() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(FeatureFlagTestHelper.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Bundle featureFlagWebBundle = null;

		for (Bundle curBundle : bundleContext.getBundles()) {
			if (Objects.equals(
					curBundle.getSymbolicName(),
					"com.liferay.feature.flag.web")) {

				featureFlagWebBundle = curBundle;
			}
		}

		Assert.assertNotNull(featureFlagWebBundle);

		_featureFlagsBagProviderObject = bundleContext.getService(
			bundleContext.getServiceReference(
				featureFlagWebBundle.loadClass(
					"com.liferay.feature.flag.web.internal.feature.flag." +
						"FeatureFlagsBagProvider")));

		Assert.assertNotNull(_featureFlagsBagProviderObject);

		_featureFlagManager = bundleContext.getService(
			bundleContext.getServiceReference(FeatureFlagManager.class));

		Assert.assertNotNull(_featureFlagManager);

		Properties properties = PropsUtil.getProperties();

		properties.setProperty(
			FeatureFlagConstants.getKey(FEATURE_FLAG_KEY_1),
			Boolean.FALSE.toString());
		properties.setProperty(
			FeatureFlagConstants.getKey(FEATURE_FLAG_KEY_2),
			Boolean.FALSE.toString());
		properties.setProperty(
			FeatureFlagConstants.getKey(FEATURE_FLAG_KEY_SYSTEM),
			Boolean.FALSE.toString());
		properties.setProperty(
			FeatureFlagConstants.getKey(FEATURE_FLAG_KEY_SYSTEM, "system"),
			Boolean.TRUE.toString());

		_clearCache();
	}

	public boolean getFeatureFlagValue(long companyId, String featureFlagKey) {
		return _featureFlagManager.isEnabled(companyId, featureFlagKey);
	}

	public void setFeatureFlagValue(
		long companyId, String featureFlagKey, boolean enabled) {

		ReflectionTestUtil.invoke(
			_featureFlagsBagProviderObject, "setEnabled",
			new Class<?>[] {long.class, String.class, boolean.class}, companyId,
			featureFlagKey, enabled);
	}

	public void tearDown() {
		_clearCache();
	}

	private void _clearCache() {
		ReflectionTestUtil.invoke(
			_featureFlagsBagProviderObject, "clearCache", new Class<?>[0]);
	}

	private final FeatureFlagManager _featureFlagManager;
	private final Object _featureFlagsBagProviderObject;

}