/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.UTF8Control;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.CacheResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ClassResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
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

	public void start() throws InvalidSyntaxException {
		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		for (BundleCapability bundleCapability : _bundleCapabilities) {
			String namespace = bundleCapability.getNamespace();

			if (namespace.equals("liferay.resource.bundle")) {
				_registerResourceBundleLoader(bundleWiring, bundleCapability);
			}
			else {
				Map<String, Object> attributes =
					bundleCapability.getAttributes();

				Object baseName = attributes.get("resource.bundle.base.name");

				if (baseName instanceof String) {
					_registerResourceBundles(
						bundleWiring, (String)baseName,
						GetterUtil.getInteger(
							attributes.get(Constants.SERVICE_RANKING)));
				}
			}
		}
	}

	public void stop() {
		for (ServiceTrackerResourceBundleLoader
				serviceTrackerResourceBundleLoader :
					_serviceTrackerResourceBundleLoaders) {

			serviceTrackerResourceBundleLoader.close();
		}

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private ResourceBundleLoader _processBaseName(
		ClassLoader classLoader, String baseName,
		boolean excludePortalResource) {

		ResourceBundleLoader resourceBundleLoader =
			new ClassResourceBundleLoader(baseName, classLoader);

		if (excludePortalResource) {
			return new CacheResourceBundleLoader(resourceBundleLoader);
		}

		AggregateResourceBundleLoader aggregateResourceBundleLoader =
			new AggregateResourceBundleLoader(
				resourceBundleLoader,
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader());

		return new CacheResourceBundleLoader(aggregateResourceBundleLoader);
	}

	private void _registerResourceBundleLoader(
			BundleWiring bundleWiring, BundleCapability bundleCapability)
		throws InvalidSyntaxException {

		ResourceBundleLoader resourceBundleLoader = null;

		Dictionary<String, Object> attributes = new HashMapDictionary<>(
			bundleCapability.getAttributes());

		Object aggregate = attributes.get("resource.bundle.aggregate");
		Object baseName = attributes.get("resource.bundle.base.name");
		Object serviceRanking = attributes.get(Constants.SERVICE_RANKING);
		Object servletContextName = attributes.get("servlet.context.name");

		if (aggregate instanceof String) {
			int aggregateId = _atomicInteger.incrementAndGet();

			ServiceTrackerResourceBundleLoader
				serviceTrackerResourceBundleLoader =
					new ServiceTrackerResourceBundleLoader(
						_bundleContext, (String)aggregate, aggregateId,
						GetterUtil.getInteger(serviceRanking));

			attributes.put("aggregateId", String.valueOf(aggregateId));

			_serviceTrackerResourceBundleLoaders.add(
				serviceTrackerResourceBundleLoader);

			resourceBundleLoader = serviceTrackerResourceBundleLoader;
		}
		else if (baseName instanceof String) {
			Object excludePortalResources = attributes.get(
				"exclude.portal.resources");

			if (excludePortalResources == null) {
				excludePortalResources = StringPool.FALSE;
			}

			resourceBundleLoader = _processBaseName(
				bundleWiring.getClassLoader(), (String)baseName,
				GetterUtil.getBoolean(excludePortalResources));
		}
		else {
			attributes.put("resource.bundle.base.name", "content.Language");

			resourceBundleLoader =
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
		}

		if (Validator.isNotNull(serviceRanking)) {
			attributes.put(
				Constants.SERVICE_RANKING,
				GetterUtil.getInteger(serviceRanking));
		}

		if (Validator.isNull(servletContextName)) {
			Dictionary<String, String> headers = _bundle.getHeaders(
				StringPool.BLANK);

			String webContextName = headers.get("Web-ContextName");

			if (Validator.isNotNull(webContextName)) {
				attributes.put("servlet.context.name", webContextName);
			}
			else {
				String webContextPath = headers.get("Web-ContextPath");

				if (Validator.isNotNull(webContextPath)) {
					attributes.put(
						"servlet.context.name", webContextPath.substring(1));
				}
			}
		}

		if (resourceBundleLoader != null) {
			if (Validator.isNull(attributes.get("bundle.symbolic.name"))) {
				attributes.put(
					"bundle.symbolic.name", _bundle.getSymbolicName());
			}

			if (Validator.isNull(attributes.get("service.ranking"))) {
				attributes.put("service.ranking", Integer.MIN_VALUE);
			}

			_serviceRegistrations.add(
				_bundleContext.registerService(
					ResourceBundleLoader.class, resourceBundleLoader,
					attributes));
		}
		else if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unable to handle ", bundleCapability, " in ",
					_bundle.getSymbolicName()));
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

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageResourcesExtension.class);

	private static final AtomicInteger _atomicInteger = new AtomicInteger();

	private final Bundle _bundle;
	private final List<BundleCapability> _bundleCapabilities;
	private final BundleContext _bundleContext;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();
	private final List<ServiceTrackerResourceBundleLoader>
		_serviceTrackerResourceBundleLoaders = new ArrayList<>();

}