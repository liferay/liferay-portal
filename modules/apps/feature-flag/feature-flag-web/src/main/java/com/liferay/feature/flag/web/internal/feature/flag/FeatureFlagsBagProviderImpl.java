/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.feature.flag;

import com.liferay.feature.flag.web.internal.manager.FeatureFlagPreferencesManager;
import com.liferay.feature.flag.web.internal.model.DependencyAwareFeatureFlag;
import com.liferay.feature.flag.web.internal.model.FeatureFlagImpl;
import com.liferay.feature.flag.web.internal.model.LanguageAwareFeatureFlag;
import com.liferay.feature.flag.web.internal.model.PreferenceAwareFeatureFlag;
import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = AopService.class)
public class FeatureFlagsBagProviderImpl
	implements AopService, FeatureFlagsBagProvider, IdentifiableOSGiService {

	@Override
	public void clearCache() {
		_featureFlagsBags.clear();

		_initSystemFeatureFlags(true);
	}

	@Override
	public FeatureFlagsBag getOrCreateFeatureFlagsBag(long companyId) {
		FeatureFlagsBag featureFlagsBag = _featureFlagsBags.get(companyId);

		if (featureFlagsBag != null) {
			return featureFlagsBag;
		}

		featureFlagsBag = _createFeatureFlagsBag(companyId);

		FeatureFlagsBag previousFeatureFlagsBag = _featureFlagsBags.putIfAbsent(
			companyId, featureFlagsBag);

		if (previousFeatureFlagsBag != null) {
			return previousFeatureFlagsBag;
		}

		return featureFlagsBag;
	}

	@Override
	public String getOSGiServiceIdentifier() {
		return FeatureFlagsBagProviderImpl.class.getName();
	}

	@Override
	public boolean isSystemKey(String key) {
		return _systemFeatureFlags.contains(key);
	}

	@Clusterable
	@Override
	public void setEnabled(long companyId, String key, boolean enabled) {
		if (ClusterInvokeThreadLocal.isEnabled()) {
			_featureFlagPreferencesManager.setEnabled(companyId, key, enabled);
		}

		FeatureFlagsBag featureFlagsBag = _featureFlagsBags.get(companyId);

		if (featureFlagsBag == null) {
			return;
		}

		featureFlagsBag.setEnabled(key, enabled);

		List<FeatureFlagListener> featureFlagListeners =
			_serviceTrackerMap.getService(key);

		if (featureFlagListeners != null) {
			for (FeatureFlagListener featureFlagListener :
					featureFlagListeners) {

				featureFlagListener.onValue(companyId, key, enabled);
			}
		}

		featureFlagListeners = _serviceTrackerMap.getService("*");

		if (featureFlagListeners != null) {
			for (FeatureFlagListener featureFlagListener :
					featureFlagListeners) {

				featureFlagListener.onValue(companyId, key, enabled);
			}
		}
	}

	@Override
	public FeatureFlagsBagProvider unwrapProxy() {
		return this;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_initSystemFeatureFlags(false);

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, FeatureFlagListener.class, null,
			(serviceReference, emitter) -> {
				List<String> keys = _getFeatureFlagKeys(serviceReference);

				if (keys == null) {
					_log.error(
						"No feature flag keys specified for " +
							serviceReference);

					return;
				}

				for (String key : keys) {
					emitter.emit(key);
				}
			},
			new FeatureFlagListenerEagerServiceTrackerCustomizer(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private FeatureFlagsBag _createFeatureFlagsBag(long companyId) {
		Map<String, FeatureFlag> featureFlags = new TreeMap<>();

		Map<String, FeatureFlag> systemFeatureFlags = new HashMap<>();

		if (companyId != CompanyConstants.SYSTEM) {
			FeatureFlagsBag systemFeatureFlagsBag = getOrCreateFeatureFlagsBag(
				CompanyConstants.SYSTEM);

			for (FeatureFlag featureFlag :
					systemFeatureFlagsBag.getFeatureFlags(null)) {

				systemFeatureFlags.put(featureFlag.getKey(), featureFlag);
			}
		}

		if (companyId == CompanyThreadLocal.getCompanyId()) {
			_populateFeatureFlagsMap(
				companyId, featureFlags, systemFeatureFlags);
		}
		else {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				_populateFeatureFlagsMap(
					companyId, featureFlags, systemFeatureFlags);
			}
		}

		return new FeatureFlagsBag(
			companyId, Collections.unmodifiableMap(featureFlags));
	}

	private List<String> _getFeatureFlagKeys(
		ServiceReference<?> serviceReference) {

		Object value = serviceReference.getProperty("feature.flag.key");

		if (value == null) {
			return null;
		}

		if (value instanceof String[]) {
			return Arrays.asList((String[])value);
		}

		return Arrays.asList(String.valueOf(value));
	}

	private void _initSystemFeatureFlags(boolean reload) {
		if (reload) {
			_featureFlagProperties.clear();

			_featureFlagProperties.putAll(
				PropsUtil.getProperties(
					FeatureFlagConstants.PORTAL_PROPERTY_KEY_FEATURE_FLAG +
						StringPool.PERIOD,
					true));

			_systemFeatureFlags.clear();
		}

		for (String stringPropertyName :
				_featureFlagProperties.stringPropertyNames()) {

			if (stringPropertyName.endsWith(".system") &&
				GetterUtil.getBoolean(
					_featureFlagProperties.getProperty(stringPropertyName))) {

				_systemFeatureFlags.add(
					stringPropertyName.substring(
						0, stringPropertyName.length() - 7));
			}
		}
	}

	private boolean _isFeatureFlagKey(String value) {
		if (value.indexOf(CharPool.PERIOD) != -1) {
			return false;
		}

		int index = value.indexOf(CharPool.DASH);

		if (index <= 0) {
			return false;
		}

		for (int i = 0; i < index; i++) {
			char c = value.charAt(i);

			if ((c < CharPool.UPPER_CASE_A) || (c > CharPool.UPPER_CASE_Z)) {
				return false;
			}
		}

		for (int i = index + 1; i < value.length(); i++) {
			if (!Validator.isDigit(value.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private void _populateFeatureFlagsMap(
		long companyId, Map<String, FeatureFlag> featureFlags,
		Map<String, FeatureFlag> systemFeatureFlags) {

		for (String stringPropertyName :
				_featureFlagProperties.stringPropertyNames()) {

			if (!_isFeatureFlagKey(stringPropertyName)) {
				continue;
			}

			boolean system = _systemFeatureFlags.contains(stringPropertyName);

			if ((system && (companyId == CompanyConstants.SYSTEM)) ||
				(!system && (companyId != CompanyConstants.SYSTEM))) {

				FeatureFlag featureFlag = new FeatureFlagImpl(
					stringPropertyName);

				featureFlag = new LanguageAwareFeatureFlag(
					featureFlag, _language);
				featureFlag = new PreferenceAwareFeatureFlag(
					companyId, featureFlag, _featureFlagPreferencesManager);

				featureFlags.put(featureFlag.getKey(), featureFlag);
			}
		}

		for (Map.Entry<String, FeatureFlag> entry : featureFlags.entrySet()) {
			List<FeatureFlag> dependencyFeatureFlags = new ArrayList<>();

			FeatureFlag featureFlag = entry.getValue();

			for (String dependencyKey : featureFlag.getDependencyKeys()) {
				if (Objects.equals(featureFlag.getKey(), dependencyKey)) {
					_log.error(
						"A feature flag cannot depend on itself: " +
							dependencyKey);

					continue;
				}

				if ((companyId == CompanyConstants.SYSTEM) &&
					!_systemFeatureFlags.contains(dependencyKey)) {

					_log.error(
						StringBundler.concat(
							"The system feature flag ", featureFlag.getKey(),
							" cannot depend on the nonsystem feature flag ",
							dependencyKey));

					continue;
				}

				FeatureFlag dependencyFeatureFlag = featureFlags.get(
					dependencyKey);

				if (dependencyFeatureFlag == null) {
					dependencyFeatureFlag = systemFeatureFlags.get(
						dependencyKey);
				}

				if (dependencyFeatureFlag != null) {
					if (!ArrayUtil.contains(
							dependencyFeatureFlag.getDependencyKeys(),
							featureFlag.getKey())) {

						dependencyFeatureFlags.add(dependencyFeatureFlag);
					}
					else {
						_log.error(
							StringBundler.concat(
								"Skipping circular dependency ", dependencyKey,
								" for feature flag ", featureFlag.getKey()));
					}
				}
			}

			if (ListUtil.isNotEmpty(dependencyFeatureFlags)) {
				entry.setValue(
					new DependencyAwareFeatureFlag(
						featureFlag,
						dependencyFeatureFlags.toArray(new FeatureFlag[0])));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FeatureFlagsBagProviderImpl.class);

	private static final Map<Long, FeatureFlagsBag> _featureFlagsBags =
		new ConcurrentHashMap<>();

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private FeatureFlagPreferencesManager _featureFlagPreferencesManager;

	private final Properties _featureFlagProperties = PropsUtil.getProperties(
		FeatureFlagConstants.PORTAL_PROPERTY_KEY_FEATURE_FLAG +
			StringPool.PERIOD,
		true);

	@Reference
	private Language _language;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	private ServiceTrackerMap<String, List<FeatureFlagListener>>
		_serviceTrackerMap;
	private final Set<String> _systemFeatureFlags = new HashSet<>();

	private class FeatureFlagListenerEagerServiceTrackerCustomizer
		implements EagerServiceTrackerCustomizer
			<FeatureFlagListener, FeatureFlagListener> {

		@Override
		public FeatureFlagListener addingService(
			ServiceReference<FeatureFlagListener> serviceReference) {

			FeatureFlagListener featureFlagListener = _bundleContext.getService(
				serviceReference);

			List<String> featureFlagKeys = _getFeatureFlagKeys(
				serviceReference);

			Predicate<FeatureFlag> predicate = featureFlag ->
				featureFlagKeys.contains(featureFlag.getKey()) ||
				featureFlagKeys.contains("*");

			UnsafeConsumer<Long, Exception> unsafeConsumer = companyId -> {
				FeatureFlagsBag featureFlagsBag = getOrCreateFeatureFlagsBag(
					companyId);

				for (FeatureFlag featureFlag :
						featureFlagsBag.getFeatureFlags(predicate)) {

					featureFlagListener.onValue(
						companyId, featureFlag.getKey(),
						featureFlag.isEnabled());
				}
			};

			try {
				_companyLocalService.forEachCompanyId(unsafeConsumer);

				unsafeConsumer.accept(CompanyConstants.SYSTEM);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return featureFlagListener;
		}

		@Override
		public void modifiedService(
			ServiceReference<FeatureFlagListener> serviceReference,
			FeatureFlagListener featureFlagListener) {
		}

		@Override
		public void removedService(
			ServiceReference<FeatureFlagListener> serviceReference,
			FeatureFlagListener featureFlagListener) {

			_bundleContext.ungetService(serviceReference);
		}

		private FeatureFlagListenerEagerServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

	}

}