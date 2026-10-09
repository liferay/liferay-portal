/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration;
import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.internal.resource.JavaScriptFrontendResource;
import com.liferay.frontend.js.web.internal.util.FrontendJSWebUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletConfigFactory;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.language.LanguageResources;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.net.URL;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Iván Zaera Avellón
 */
public class JavaScriptFrontendResourceRequestHandler
	implements FrontendResourceRequestHandler {

	public static String getBundleHashedFileURIPrefix(Portal portal) {
		return FrontendJSWebUtil.getPortalContextPath(portal) + "/o/js/-/";
	}

	public JavaScriptFrontendResourceRequestHandler(
		ConfigurationProvider configurationProvider,
		HashedFilesRegistry hashedFilesRegistry, Language language,
		Portal portal, PortletConfigFactory portletConfigFactory) {

		_configurationProvider = configurationProvider;
		_hashedFilesRegistry = hashedFilesRegistry;
		_language = language;
		_portal = portal;
		_portletConfigFactory = portletConfigFactory;
	}

	@Override
	public boolean canHandleRequest(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (!requestURI.endsWith(".js")) {
			return false;
		}

		String resourceURI = requestURI;

		if (requestURI.startsWith(getBundleHashedFileURIPrefix(_portal))) {
			resourceURI = _getBundleHashedFileResourceURI(requestURI);
		}

		if (_hashedFilesRegistry.getResource(resourceURI) != null) {
			return true;
		}

		return false;
	}

	@Override
	public FrontendResource handleRequest(HttpServletRequest httpServletRequest)
		throws IOException, ServletException {

		boolean requestContainsHash = true;

		String requestURI = httpServletRequest.getRequestURI();

		String resourceURI = requestURI;

		if (requestURI.startsWith(getBundleHashedFileURIPrefix(_portal))) {
			resourceURI = _getBundleHashedFileResourceURI(requestURI);
		}
		else {
			requestContainsHash = HashedFilesUtil.getHash(requestURI) != null;
		}

		if (resourceURI == null) {
			return null;
		}

		URL url = _hashedFilesRegistry.getResource(resourceURI);

		if (url == null) {
			return null;
		}

		ResourceBundle resourceBundle = _getResourceBundle(
			httpServletRequest, resourceURI);

		String eTag =
			(resourceBundle == null) ? HashedFilesUtil.getHash(url.getFile()) :
				null;

		boolean immutable = false;

		if (requestContainsHash && (resourceBundle == null)) {
			immutable = true;
		}

		FrontendCachingConfiguration frontendCachingConfiguration =
			FrontendJSWebUtil.getFrontendCachingConfiguration(
				_portal.getCompanyId(httpServletRequest),
				_configurationProvider);

		long maxAge = frontendCachingConfiguration.jsFilesMaxAge();
		boolean sendNoCache =
			frontendCachingConfiguration.sendNoCacheForJSFiles();

		if (immutable) {
			maxAge = 31536000;
			sendNoCache = false;
		}
		else if (resourceBundle != null) {
			maxAge = frontendCachingConfiguration.translatedJSFilesMaxAge();
			sendNoCache =
				frontendCachingConfiguration.sendNoCacheForTranslatedJSFiles();
		}

		return new JavaScriptFrontendResource(
			eTag, immutable, resourceBundle != null, _language, maxAge,
			resourceBundle, sendNoCache, url);
	}

	private String _getBundleHashedFileResourceURI(String requestURI) {
		int openParethensisIndex = requestURI.indexOf(
			StringPool.OPEN_PARENTHESIS);

		if (openParethensisIndex == -1) {
			return null;
		}

		int closeParethensisIndex = requestURI.indexOf(
			StringPool.CLOSE_PARENTHESIS);

		if (closeParethensisIndex == -1) {
			return null;
		}

		String requestHash = requestURI.substring(
			openParethensisIndex + 1, closeParethensisIndex);

		String bundleHashedFileURIPrefix = getBundleHashedFileURIPrefix(
			_portal);

		String servletContextName = requestURI.substring(
			bundleHashedFileURIPrefix.length(), openParethensisIndex);

		if (!Objects.equals(
				requestHash,
				_hashedFilesRegistry.getServletContextHash(
					servletContextName))) {

			return null;
		}

		String unhashedResourceURI = StringBundler.concat(
			FrontendJSWebUtil.getPortalContextPath(_portal), Portal.PATH_MODULE,
			StringPool.SLASH, servletContextName,
			requestURI.substring(closeParethensisIndex + 1));

		return _hashedFilesRegistry.getHashedFileURI(unhashedResourceURI);
	}

	private ResourceBundle _getResourceBundle(
		HttpServletRequest httpServletRequest, String resourceURI) {

		if (!_isTranslatable(resourceURI)) {
			return null;
		}

		Locale locale = LocaleUtil.fromLanguageId(
			_language.getLanguageId(httpServletRequest));

		ResourceBundle resourceBundle = LanguageResources.getResourceBundle(
			locale);

		PortletConfig portletConfig = null;

		Enumeration<String> enumeration =
			httpServletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String parameterName = enumeration.nextElement();

			int index = parameterName.indexOf(CharPool.COLON);

			if (index <= 0) {
				continue;
			}

			portletConfig = _portletConfigFactory.get(
				parameterName.substring(0, index));
		}

		if (portletConfig != null) {
			resourceBundle = new AggregateResourceBundle(
				portletConfig.getResourceBundle(locale), resourceBundle);
		}

		final ResourceBundle finalResourceBundle = resourceBundle;

		return new ResourceBundle() {

			@Override
			public Enumeration<String> getKeys() {
				return finalResourceBundle.getKeys();
			}

			@Override
			public Locale getLocale() {
				return locale;
			}

			@Override
			protected Object handleGetObject(String s) {
				return finalResourceBundle.getObject(s);
			}

		};
	}

	private boolean _isTranslatable(String resourceURI) {
		if (!_translatable.containsKey(resourceURI)) {
			try {
				URL url = _hashedFilesRegistry.getResource(resourceURI);

				String content = StreamUtil.toString(url.openStream());

				if (!content.contains("Liferay.Language.get(")) {
					return false;
				}

				URL languageJsonURL = _hashedFilesRegistry.getResource(
					StringBundler.concat(
						FrontendJSWebUtil.getPortalContextPath(_portal),
						Portal.PATH_MODULE,
						FrontendJSWebUtil.getWebContextPathFromFileURI(
							_portal, resourceURI),
						"/language.json"));

				if (languageJsonURL == null) {
					_translatable.putIfAbsent(resourceURI, true);
				}
				else {
					_translatable.putIfAbsent(resourceURI, false);
				}
			}
			catch (IOException ioException) {
				_log.error(
					"Unable to read translatable resource " + resourceURI,
					ioException);

				_translatable.putIfAbsent(resourceURI, false);
			}
		}

		return _translatable.get(resourceURI);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JavaScriptFrontendResourceRequestHandler.class);

	private final ConfigurationProvider _configurationProvider;
	private final HashedFilesRegistry _hashedFilesRegistry;
	private final Language _language;
	private final Portal _portal;
	private final PortletConfigFactory _portletConfigFactory;
	private final Map<String, Boolean> _translatable =
		new ConcurrentHashMap<>();

}