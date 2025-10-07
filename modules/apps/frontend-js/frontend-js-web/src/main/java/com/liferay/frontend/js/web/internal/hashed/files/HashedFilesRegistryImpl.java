/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.hashed.files;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = HashedFilesRegistry.class)
public class HashedFilesRegistryImpl implements HashedFilesRegistry {

	public void forEach(BiConsumer<String, String> biConsumer) {
		_lazyActivate();

		for (Map.Entry<String, String> entry : _map.entrySet()) {
			biConsumer.accept(entry.getKey(), entry.getValue());
		}
	}

	public String getHashedFileURI(String unhashedFileURI) {
		_lazyActivate();

		return _map.get(unhashedFileURI);
	}

	@Override
	public URL getResource(String path) {
		_lazyActivate();

		List<String> pathParts = Arrays.asList(path.split(StringPool.SLASH));

		ServletContext servletContext = _serviceTrackerMap.getService(
			StringUtil.merge(pathParts.subList(0, 3), StringPool.SLASH));

		if (servletContext == null) {
			return null;
		}

		String subpath = StringUtil.merge(
			pathParts.subList(3, pathParts.size()), StringPool.SLASH);

		subpath = StringPool.SLASH + subpath;

		try {
			return servletContext.getResource(subpath);
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		_map.clear();

		if (_serviceTracker != null) {
			_serviceTracker.close();

			_serviceTracker = null;
		}

		if (_serviceTrackerMap != null) {
			_serviceTrackerMap.close();

			_serviceTrackerMap = null;
		}
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
					Set<String> hashedResourcePaths;

					URL url = servletContext.getResource(
						"/WEB-INF/liferay-look-and-feel.xml");

					if (url != null) {
						hashedResourcePaths = _getHashedResourcePaths(
							servletContext, "/css/");

						hashedResourcePaths.addAll(
							_getHashedResourcePaths(servletContext, "/js/"));
					}
					else {
						Set<String> completeHashedResourcePaths =
							_getHashedResourcePaths(
								servletContext, "/META-INF/resources/");

						hashedResourcePaths = new HashSet<>();

						for (String completeHashedResourcePath :
								completeHashedResourcePaths) {

							hashedResourcePaths.add(
								completeHashedResourcePath.substring(19));
						}
					}

					Map<String, String> map = new HashMap<>();

					String contextPath = servletContext.getContextPath();

					for (String hashedResourcePath : hashedResourcePaths) {
						map.put(
							contextPath +
								HashedFilesUtil.removeHash(hashedResourcePath),
							contextPath + hashedResourcePath);
					}

					_map.putAll(map);

					return map;
				}
				catch (MalformedURLException malformedURLException) {
					_log.error(malformedURLException);

					return Collections.emptyMap();
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
			else if (HashedFilesUtil.containsHash(resourcePath)) {
				hashedResourcePaths.add(resourcePath);
			}
		}

		return hashedResourcePaths;
	}

	private void _lazyActivate() {
		if (_serviceTracker == null) {
			synchronized (this) {
				if (_serviceTracker == null) {
					_serviceTracker = new ServiceTracker<>(
						_bundleContext, ServletContext.class,
						_createServiceTrackerCustomizer());

					_serviceTracker.open();
				}
			}
		}

		if (_serviceTrackerMap == null) {
			synchronized (this) {
				if (_serviceTrackerMap == null) {
					_serviceTrackerMap =
						ServiceTrackerMapFactory.openSingleValueMap(
							_bundleContext, ServletContext.class, null,
							(serviceReference, emitter) -> {
								ServletContext servletContext =
									_bundleContext.getService(serviceReference);

								try {
									emitter.emit(
										servletContext.getContextPath());
								}
								finally {
									_bundleContext.ungetService(
										serviceReference);
								}
							});
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HashedFilesRegistryImpl.class);

	private BundleContext _bundleContext;
	private final Map<String, String> _map = new ConcurrentHashMap<>();
	private volatile ServiceTracker<ServletContext, Map<String, String>>
		_serviceTracker;
	private volatile ServiceTrackerMap<String, ServletContext>
		_serviceTrackerMap;

}