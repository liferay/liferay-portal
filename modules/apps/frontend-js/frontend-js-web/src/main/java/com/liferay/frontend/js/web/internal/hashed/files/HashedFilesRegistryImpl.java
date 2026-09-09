/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.hashed.files;

import com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration;
import com.liferay.frontend.js.web.internal.util.FrontendJSWebUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.frontend.hashed.files.CachingStrategy;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
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
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = HashedFilesRegistry.class)
public class HashedFilesRegistryImpl implements HashedFilesRegistry {

	public void forEachHashedFileURI(BiConsumer<String, String> biConsumer) {
		_lazyActivate();

		for (DataBag dataBag : _dataBags.values()) {
			Map<String, String> hashedFileURIs = dataBag._hashedFileURIs;

			for (Map.Entry<String, String> entry2 : hashedFileURIs.entrySet()) {
				biConsumer.accept(entry2.getKey(), entry2.getValue());
			}
		}
	}

	public void forEachServletContextHash(
		BiConsumer<String, String> biConsumer) {

		_lazyActivate();

		for (Map.Entry<String, DataBag> entry : _dataBags.entrySet()) {
			DataBag dataBag = entry.getValue();

			biConsumer.accept(
				FrontendJSWebUtil.getServletContextNameFromServletContextPath(
					_portal, entry.getKey()),
				dataBag._servletContextHash);
		}
	}

	@Override
	public CachingStrategy getCachingStrategy(
		HttpServletRequest httpServletRequest) {

		FrontendCachingConfiguration frontendCachingConfiguration =
			FrontendJSWebUtil.getFrontendCachingConfiguration(
				_portal.getCompanyId(httpServletRequest),
				_configurationProvider);

		return CachingStrategy.fromValue(
			frontendCachingConfiguration.cachingStrategy());
	}

	public String getHashedFileURI(String unhashedFileURI) {
		_lazyActivate();

		DataBag dataBag = _dataBags.get(
			FrontendJSWebUtil.getServletContextPathFromFileURI(
				unhashedFileURI, _portal));

		if (dataBag == null) {
			return null;
		}

		Map<String, String> hashedFileURIs = dataBag._hashedFileURIs;

		return hashedFileURIs.get(unhashedFileURI);
	}

	@Override
	public URL getResource(String fileURI) {
		_lazyActivate();

		if (!HashedFilesUtil.containsHash(fileURI)) {
			String hashedFileURI = getHashedFileURI(fileURI);

			if (hashedFileURI != null) {
				fileURI = hashedFileURI;
			}
		}

		DataBag dataBag = _dataBags.get(
			FrontendJSWebUtil.getServletContextPathFromFileURI(
				fileURI, _portal));

		if (dataBag == null) {
			return null;
		}

		try {
			ServletContext servletContext = dataBag._servletContext;

			return servletContext.getResource(
				FrontendJSWebUtil.getServletContextResourcePathFromFileURI(
					fileURI, _portal));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	@Override
	public String getServletContextHash(String servletContextName) {
		_lazyActivate();

		DataBag dataBag = _dataBags.get(
			FrontendJSWebUtil.getServletContextPathFromServletContextName(
				_portal, servletContextName));

		if (dataBag == null) {
			return null;
		}

		return dataBag._servletContextHash;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		_dataBags.clear();

		if (_serviceTracker != null) {
			_serviceTracker.close();

			_serviceTracker = null;
		}
	}

	protected static class DataBag {

		protected DataBag(
			Map<String, String> hashedFileURIs, ServletContext servletContext,
			String servletContextHash) {

			_hashedFileURIs = hashedFileURIs;
			_servletContext = servletContext;
			_servletContextHash = servletContextHash;
		}

		private final Map<String, String> _hashedFileURIs;
		private final ServletContext _servletContext;
		private final String _servletContextHash;

	}

	private Map<String, String> _getHashedFileURIs(
		ServletContext servletContext) {

		boolean theme = false;

		String contextPath = servletContext.getContextPath();

		try {
			URL url = servletContext.getResource(
				"/WEB-INF/liferay-look-and-feel.xml");

			if (url != null) {
				theme = true;
			}
		}
		catch (MalformedURLException malformedURLException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check if context path " + contextPath +
						" contains a theme",
					malformedURLException);
			}
		}

		Set<String> hashedResourcePaths = new HashSet<>();

		if (theme) {
			hashedResourcePaths.addAll(
				_getHashedResourcePaths(servletContext, "/css/"));
			hashedResourcePaths.addAll(
				_getHashedResourcePaths(servletContext, "/js/"));
		}
		else {
			Set<String> hashedResourceFullPaths = _getHashedResourcePaths(
				servletContext, "/META-INF/resources/");

			for (String hashedResourceFullPath : hashedResourceFullPaths) {
				hashedResourcePaths.add(hashedResourceFullPath.substring(19));
			}
		}

		Map<String, String> hashedFileURIs = new HashMap<>();

		for (String hashedResourcePath : hashedResourcePaths) {
			hashedFileURIs.put(
				contextPath + HashedFilesUtil.removeHash(hashedResourcePath),
				contextPath + hashedResourcePath);
		}

		return hashedFileURIs;
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

	private String _getServletContextHash(Map<String, String> hashedFileURIs) {
		Set<String> hashesSet = new HashSet<>();

		for (String hashedFileURI : hashedFileURIs.values()) {
			hashesSet.add(HashedFilesUtil.getHash(hashedFileURI));
		}

		List<String> hashesList = new ArrayList<>(hashesSet);

		Collections.sort(hashesList);

		String hashesString = StringUtil.merge(hashesList, StringPool.PIPE);

		return HashedFilesUtil.computeHash(hashesString);
	}

	private void _lazyActivate() {
		if (_serviceTracker != null) {
			return;
		}

		synchronized (this) {
			if (_serviceTracker != null) {
				return;
			}

			_serviceTracker = new ServiceTracker<>(
				_bundleContext, ServletContext.class,
				new ServiceTrackerCustomizer<>() {

					@Override
					public Void addingService(
						ServiceReference<ServletContext> serviceReference) {

						modifiedService(serviceReference, null);

						return null;
					}

					@Override
					public void modifiedService(
						ServiceReference<ServletContext> serviceReference,
						Void v) {

						ServletContext servletContext =
							_bundleContext.getService(serviceReference);

						try {
							Map<String, String> hashedFileURIs =
								_getHashedFileURIs(servletContext);

							if (hashedFileURIs.isEmpty()) {
								_dataBags.remove(
									servletContext.getContextPath());

								return;
							}

							_dataBags.put(
								servletContext.getContextPath(),
								new DataBag(
									hashedFileURIs, servletContext,
									_getServletContextHash(hashedFileURIs)));
						}
						finally {
							_bundleContext.ungetService(serviceReference);
						}
					}

					@Override
					public void removedService(
						ServiceReference<ServletContext> serviceReference,
						Void v) {

						ServletContext servletContext =
							_bundleContext.getService(serviceReference);

						try {
							_dataBags.remove(servletContext.getContextPath());
						}
						finally {
							_bundleContext.ungetService(serviceReference);
						}
					}

				});

			_serviceTracker.open();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HashedFilesRegistryImpl.class);

	private BundleContext _bundleContext;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final Map<String, DataBag> _dataBags = new ConcurrentHashMap<>();

	@Reference
	private Portal _portal;

	private volatile ServiceTracker<ServletContext, Void> _serviceTracker;

}