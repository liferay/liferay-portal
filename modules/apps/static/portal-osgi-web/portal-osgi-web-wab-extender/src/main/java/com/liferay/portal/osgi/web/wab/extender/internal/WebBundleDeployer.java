/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal;

import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.osgi.web.servlet.JSPServletFactory;
import com.liferay.portal.profile.PortalProfile;

import java.io.IOException;

import java.net.URL;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleRevision;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class WebBundleDeployer {

	public WebBundleDeployer(
		BundleContext bundleContext, JSPServletFactory jspServletFactory,
		Dictionary<String, Object> properties) {

		_bundleContext = bundleContext;
		_jspServletFactory = jspServletFactory;
		_properties = properties;
	}

	public void close() {
		for (Bundle bundle : _wabBundleProcessors.keySet()) {
			doStop(bundle);
		}
	}

	public ServiceRegistration<?> doStart(Bundle bundle) {
		Enumeration<URL> enumeration = bundle.findEntries(
			"/WEB-INF", "liferay-plugin-package.properties", false);

		if ((enumeration == null) || !enumeration.hasMoreElements()) {
			_initWabBundle(bundle);

			return null;
		}

		try {
			Properties properties = PropertiesUtil.load(
				enumeration.nextElement());

			Set<String> featureFlagKeys = SetUtil.fromArray(
				StringUtil.split(
					properties.getProperty("liferay-feature-flag-keys")));

			if (!featureFlagKeys.isEmpty()) {
				return _bundleContext.registerService(
					FeatureFlagListener.class,
					new WarFeatureFlagListener(bundle, featureFlagKeys),
					HashMapDictionaryBuilder.<String, Object>put(
						"feature.flag.key",
						featureFlagKeys.toArray(new String[0])
					).build());
			}

			Set<String> portalProfileNames = SetUtil.fromArray(
				StringUtil.split(
					properties.getProperty("liferay-portal-profile-names")));

			if (portalProfileNames.isEmpty()) {
				_initWabBundle(bundle);

				return null;
			}

			portalProfileNames.add(bundle.getSymbolicName());

			return _bundleContext.registerService(
				PortalProfile.class,
				new WarModuleProfile(bundle, portalProfileNames), null);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return null;
		}
	}

	public void doStop(Bundle bundle) {
		WabBundleProcessor wabBundleProcessor = _wabBundleProcessors.remove(
			bundle);

		if (wabBundleProcessor == null) {
			return;
		}

		try {
			wabBundleProcessor.destroy();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	public boolean isFragmentBundle(Bundle bundle) {
		BundleRevision bundleRevision = bundle.adapt(BundleRevision.class);

		if ((bundleRevision.getTypes() & BundleRevision.TYPE_FRAGMENT) == 0) {
			return false;
		}

		return true;
	}

	private void _initWabBundle(Bundle bundle) {
		try {
			WabBundleProcessor wabBundleProcessor = new WabBundleProcessor(
				bundle, _jspServletFactory);

			wabBundleProcessor.init(_properties);

			_wabBundleProcessors.put(bundle, wabBundleProcessor);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebBundleDeployer.class);

	private final BundleContext _bundleContext;
	private final JSPServletFactory _jspServletFactory;
	private final Dictionary<String, Object> _properties;
	private final ConcurrentMap<Bundle, WabBundleProcessor>
		_wabBundleProcessors = new ConcurrentHashMap<>();

	private class WarFeatureFlagListener implements FeatureFlagListener {

		@Override
		public void onValue(
			long companyId, String featureFlagKey, boolean enabled) {

			if (enabled && _featureFlagKeys.contains(featureFlagKey) &&
				!_wabBundleProcessors.containsKey(_bundle)) {

				_initWabBundle(_bundle);
			}
		}

		private WarFeatureFlagListener(
			Bundle bundle, Set<String> featureFlagKeys) {

			_bundle = bundle;
			_featureFlagKeys = featureFlagKeys;
		}

		private final Bundle _bundle;
		private final Set<String> _featureFlagKeys;

	}

	private class WarModuleProfile implements PortalProfile {

		@Override
		public void activate() {
			_initWabBundle(_bundle);
		}

		@Override
		public Set<String> getPortalProfileNames() {
			return _portalProfileNames;
		}

		private WarModuleProfile(
			Bundle bundle, Set<String> portalProfileNames) {

			_bundle = bundle;
			_portalProfileNames = portalProfileNames;
		}

		private final Bundle _bundle;
		private final Set<String> _portalProfileNames;

	}

}