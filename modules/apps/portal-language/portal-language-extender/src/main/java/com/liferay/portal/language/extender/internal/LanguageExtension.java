/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.CacheResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ClassResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Carlos Sierra Andrés
 */
public class LanguageExtension {

	public LanguageExtension(
		BundleContext bundleContext, Bundle bundle,
		List<BundleCapability> bundleCapabilities) {

		_bundleContext = bundleContext;
		_bundle = bundle;
		_bundleCapabilities = bundleCapabilities;
	}

	public void destroy() {
		for (ServiceTrackerResourceBundleLoader
				serviceTrackerResourceBundleLoader :
					_serviceTrackerResourceBundleLoaders) {

			serviceTrackerResourceBundleLoader.close();
		}

		for (ServiceRegistration<ResourceBundleLoader> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	public void start() throws InvalidSyntaxException {
		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		for (BundleCapability bundleCapability : _bundleCapabilities) {
			_registerResourceBundleLoader(bundleWiring, bundleCapability);
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

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageExtension.class);

	private static final AtomicInteger _atomicInteger = new AtomicInteger();

	private final Bundle _bundle;
	private final List<BundleCapability> _bundleCapabilities;
	private final BundleContext _bundleContext;
	private final Collection<ServiceRegistration<ResourceBundleLoader>>
		_serviceRegistrations = new ArrayList<>();
	private final List<ServiceTrackerResourceBundleLoader>
		_serviceTrackerResourceBundleLoaders = new ArrayList<>();

}