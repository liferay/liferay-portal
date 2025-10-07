/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.feature.flag;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Dictionary;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Drew Brokke
 */
public class FeatureFlagManagerUtil {

	public static void checkEnabled(long companyId, String key) {
		if (!isEnabled(companyId, key)) {
			throw new UnsupportedOperationException(
				StringBundler.concat(
					"Feature flag ", key, " is disabled for company ",
					companyId));
		}
	}

	public static void checkEnabled(String key) {
		if (!isEnabled(key)) {
			throw new UnsupportedOperationException(
				"Feature flag " + key + " is disabled");
		}
	}

	public static String getJSON(long companyId) {
		FeatureFlagManager featureFlagManager =
			_featureFlagManagerSnapshot.get();

		if (featureFlagManager != null) {
			return featureFlagManager.getJSON(companyId);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"No feature flag manager service found. Returning the " +
					"default value.");
		}

		return _JSON;
	}

	public static boolean isEnabled(long companyId, String key) {
		FeatureFlagManager featureFlagManager =
			_featureFlagManagerSnapshot.get();

		if (featureFlagManager != null) {
			return featureFlagManager.isEnabled(companyId, key);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"No feature flag manager service found. Returning the " +
					"default value.");
		}

		return GetterUtil.getBoolean(PropsUtil.get("feature.flag." + key));
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *		#isEnabled(long, String)}
	 */
	@Deprecated
	public static boolean isEnabled(String key) {
		return isEnabled(CompanyThreadLocal.getCompanyId(), key);
	}

	public static <T> ServiceRegistration<T> registerService(
		BundleContext bundleContext, String featureFlagKey,
		Class<T> serviceClass, Function<Boolean, T> serviceFunction,
		Function<Boolean, Dictionary<String, ?>> servicePropertiesFunction) {

		return new FeatureFlaggedServiceRegistration<>(
			bundleContext, featureFlagKey, serviceClass, serviceFunction,
			servicePropertiesFunction);
	}

	private static final String _JSON = String.valueOf(
		JSONFactoryUtil.createJSONObject(
			PropsUtil.getProperties("feature.flag.", true)));

	private static final Log _log = LogFactoryUtil.getLog(
		FeatureFlagManagerUtil.class);

	private static final Snapshot<FeatureFlagManager>
		_featureFlagManagerSnapshot = new Snapshot<>(
			FeatureFlagManagerUtil.class, FeatureFlagManager.class);

	private static class FeatureFlaggedServiceRegistration<T>
		implements ServiceRegistration<T> {

		public FeatureFlaggedServiceRegistration(
			BundleContext bundleContext, String featureFlagKey,
			Class<T> serviceClass, Function<Boolean, T> serviceFunction,
			Function<Boolean, Dictionary<String, ?>>
				servicePropertiesFunction) {

			_featureFlagListenerServiceRegistration =
				bundleContext.registerService(
					FeatureFlagListener.class,
					(companyId, currentFeatureFlagKey, enabled) -> {
						if (_serviceRegistration != null) {
							_serviceRegistration.unregister();
						}

						enabled = FeatureFlagManagerUtil.isEnabled(
							companyId, currentFeatureFlagKey);

						setServiceRegistration(
							bundleContext.registerService(
								serviceClass, serviceFunction.apply(enabled),
								servicePropertiesFunction.apply(enabled)));
					},
					MapUtil.singletonDictionary(
						"feature.flag.key", featureFlagKey));
		}

		@Override
		public ServiceReference<T> getReference() {
			return _serviceRegistration.getReference();
		}

		@Override
		public void setProperties(Dictionary<String, ?> dictionary) {
			_serviceRegistration.setProperties(dictionary);
		}

		public void setServiceRegistration(
			ServiceRegistration<T> serviceRegistration) {

			_serviceRegistration = serviceRegistration;
		}

		@Override
		public void unregister() {
			_featureFlagListenerServiceRegistration.unregister();

			if (_serviceRegistration != null) {
				_serviceRegistration.unregister();
			}
		}

		private final ServiceRegistration<FeatureFlagListener>
			_featureFlagListenerServiceRegistration;
		private ServiceRegistration<T> _serviceRegistration;

	}

}