/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.UTF8Control;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Kevin Lee
 */
public class LanguageResourcesExtension {

	public LanguageResourcesExtension(
		BundleContext bundleContext, Bundle bundle,
		List<BundleCapability> bundleCapabilities) {

		_bundleContext = bundleContext;
		_bundle = bundle;
		_bundleCapabilities = bundleCapabilities;
	}

	public void start() {
		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		for (BundleCapability bundleCapability : _bundleCapabilities) {
			Map<String, Object> attributes = bundleCapability.getAttributes();

			Object baseName = attributes.get("resource.bundle.base.name");

			if (baseName instanceof String) {
				_registerResourceBundles(
					bundleWiring, (String)baseName,
					GetterUtil.getInteger(
						attributes.get(Constants.SERVICE_RANKING)));
			}
		}
	}

	public void stop() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private void _registerResourceBundles(
		BundleWiring bundleWiring, String baseName, int serviceRanking) {

		int index = baseName.lastIndexOf(StringPool.PERIOD);

		String path = StringPool.SLASH;
		String name = baseName;

		if (index > 0) {
			path = baseName.substring(0, index);

			path =
				StringPool.SLASH +
					StringUtil.replace(path, CharPool.PERIOD, CharPool.SLASH);

			name = baseName.substring(index + 1);
		}

		Enumeration<URL> enumeration = _bundle.findEntries(
			path, name.concat("*.properties"), false);

		if (enumeration == null) {
			return;
		}

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String urlPath = url.getPath();

			String languageId = StringPool.BLANK;

			index = urlPath.indexOf(StringPool.UNDERLINE, path.length());

			if (index > -1) {
				languageId = urlPath.substring(
					index + 1, urlPath.length() - ".properties".length());
			}

			Locale locale = LocaleUtil.fromLanguageId(languageId, false);

			ServiceRegistration<?> serviceRegistration =
				_bundleContext.registerService(
					ResourceBundle.class,
					new ServiceFactory<ResourceBundle>() {

						@Override
						public ResourceBundle getService(
							Bundle bundle,
							ServiceRegistration<ResourceBundle>
								serviceRegistration) {

							return ResourceBundle.getBundle(
								baseName, locale, bundleWiring.getClassLoader(),
								UTF8Control.INSTANCE);
						}

						@Override
						public void ungetService(
							Bundle bundle,
							ServiceRegistration<ResourceBundle>
								serviceRegistration,
							ResourceBundle resourceBundle) {
						}

					},
					HashMapDictionaryBuilder.<String, Object>put(
						Constants.SERVICE_RANKING, serviceRanking
					).put(
						"language.id", languageId
					).build());

			_serviceRegistrations.add(serviceRegistration);
		}
	}

	private final Bundle _bundle;
	private final List<BundleCapability> _bundleCapabilities;
	private final BundleContext _bundleContext;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}