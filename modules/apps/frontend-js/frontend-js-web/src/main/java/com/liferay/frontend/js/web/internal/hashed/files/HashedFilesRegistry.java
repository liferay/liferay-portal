/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.hashed.files;

import com.liferay.petra.string.StringPool;

import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFilesRegistry {

	public static HashedFilesRegistry getHashedFilesRegistry() {
		return _hashedFilesRegistry;
	}

	public static void setHashedFilesRegistry(
		HashedFilesRegistry hashedFilesRegistry) {

		_hashedFilesRegistry = hashedFilesRegistry;
	}

	public HashedFilesRegistry(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	public void close() {
		_map.clear();

		if (_serviceTracker != null) {
			_serviceTracker.close();

			_serviceTracker = null;
		}
	}

	public void forEach(BiConsumer<String, String> biConsumer) {
		_openServiceTracker();

		for (Map.Entry<String, String> entry : _map.entrySet()) {
			biConsumer.accept(entry.getKey(), entry.getValue());
		}
	}

	public String get(String unhashedFileURI) {
		_openServiceTracker();

		return _map.get(unhashedFileURI);
	}

	private ServiceTrackerCustomizer<ServletContext, Map<String, String>>
		_createServiceTrackerCustomizer() {

		return new ServiceTrackerCustomizer<>() {

			@Override
			public Map<String, String> addingService(
				ServiceReference<ServletContext> serviceReference) {

				ServletContext servletContext = _bundleContext.getService(
					serviceReference);

				try {
					String contextPath = servletContext.getContextPath();

					Set<String> hashedResourcePaths = _getHashedResourcePaths(
						servletContext, "/META-INF/resources/__liferay__/");

					Map<String, String> map = new HashMap<>();

					for (String hashedResourcePath : hashedResourcePaths) {

						// Remove "/META-INF/resources" from path

						hashedResourcePath = hashedResourcePath.substring(19);

						String baseName = hashedResourcePath.substring(
							0, hashedResourcePath.lastIndexOf(".("));

						String extension = hashedResourcePath.substring(
							hashedResourcePath.lastIndexOf(").") + 1);

						map.put(
							contextPath + baseName + extension,
							contextPath + hashedResourcePath);
					}

					_map.putAll(map);

					return map;
				}
				finally {
					_bundleContext.ungetService(serviceReference);
				}
			}

			@Override
			public void modifiedService(
				ServiceReference<ServletContext> serviceReference,
				Map<String, String> map) {

				removedService(serviceReference, map);

				addingService(serviceReference);
			}

			@Override
			public void removedService(
				ServiceReference<ServletContext> serviceReference,
				Map<String, String> map) {

				for (String key : map.keySet()) {
					_map.remove(key);
				}
			}

		};
	}

	private Set<String> _getHashedResourcePaths(
		ServletContext servletContext, String folderPath) {

		Set<String> resourcePaths = servletContext.getResourcePaths(folderPath);

		if (resourcePaths == null) {
			return Collections.emptySet();
		}

		Set<String> hashedResourcePaths = new HashSet<>();

		for (String resourcePath : resourcePaths) {
			if (resourcePath.endsWith(StringPool.SLASH)) {
				hashedResourcePaths.addAll(
					_getHashedResourcePaths(servletContext, resourcePath));
			}
			else if (resourcePath.contains(".(") &&
					 resourcePath.contains(").")) {

				hashedResourcePaths.add(resourcePath);
			}
		}

		return hashedResourcePaths;
	}

	private void _openServiceTracker() {
		if (_serviceTracker != null) {
			return;
		}

		synchronized (this) {
			if (_serviceTracker != null) {
				return;
			}

			_serviceTracker = new ServiceTracker<>(
				_bundleContext, ServletContext.class,
				_createServiceTrackerCustomizer());

			_serviceTracker.open();
		}
	}

	private static volatile HashedFilesRegistry _hashedFilesRegistry;

	private final BundleContext _bundleContext;
	private final Map<String, String> _map = new ConcurrentHashMap<>();
	private volatile ServiceTracker<ServletContext, Map<String, String>>
		_serviceTracker;

}