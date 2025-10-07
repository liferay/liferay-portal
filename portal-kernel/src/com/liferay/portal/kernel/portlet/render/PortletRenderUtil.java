/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.render;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.frontend.esm.FrontendESMUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistryUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author Iván Zaera Avellón
 */
public class PortletRenderUtil {

	public static PortletRenderParts getPortletRenderParts(
		HttpServletRequest httpServletRequest, String portletHTML,
		Portlet portlet) {

		boolean portletOnLayout = false;

		if (portlet.isInstanceable()) {
			String rootPortletId = _getRootPortletId(portlet);
			String portletId = portlet.getPortletId();

			for (Portlet layoutPortlet : _getAllPortlets(httpServletRequest)) {

				// Check to see if an instance of this portlet is already in the
				// layout, but ignore the portlet that was just added

				String layoutPortletRootPortletId = _getRootPortletId(
					layoutPortlet);

				if (rootPortletId.equals(layoutPortletRootPortletId) &&
					!portletId.equals(layoutPortlet.getPortletId())) {

					portletOnLayout = true;

					break;
				}
			}
		}

		return _getPortletRenderParts(
			httpServletRequest, portletHTML, portlet, portletOnLayout);
	}

	public static void writeFooterCSSPaths(
		HttpServletRequest httpServletRequest, Collection<Portlet> portlets,
		Writer writer) {

		Collection<String> urls = _getURLs(
			httpServletRequest,
			Arrays.asList(_FOOTER_PORTAL_CSS, _FOOTER_PORTLET_CSS), portlets,
			URLType.CSS);

		for (String url : urls) {
			_writeCSSPath(new PrintWriter(writer, true), url, null);
		}
	}

	public static void writeFooterJavaScriptPaths(
		HttpServletRequest httpServletRequest, Collection<Portlet> portlets,
		Writer writer) {

		Collection<String> urls = _getURLs(
			httpServletRequest,
			Arrays.asList(_FOOTER_PORTAL_JS, _FOOTER_PORTLET_JS), portlets,
			URLType.JAVASCRIPT);

		for (String url : urls) {
			_writeJavaScriptPath(new PrintWriter(writer, true), url, null);
		}
	}

	public static void writeFooterPaths(
			HttpServletResponse httpServletResponse,
			PortletRenderParts portletRenderParts)
		throws IOException {

		_writePaths(
			httpServletResponse, portletRenderParts.getFooterCssPaths(),
			portletRenderParts.getFooterJavaScriptPaths());
	}

	public static void writeHeaderCSSPaths(
		HttpServletRequest httpServletRequest, Collection<Portlet> portlets,
		Writer writer) {

		Collection<String> urls = _getURLs(
			httpServletRequest,
			Arrays.asList(_HEADER_PORTAL_CSS, _HEADER_PORTLET_CSS), portlets,
			URLType.CSS);

		for (String url : urls) {
			_writeCSSPath(
				new PrintWriter(writer, true), url,
				HashMapBuilder.put(
					"data-senna-track", "temporary"
				).put(
					"id",
					HtmlUtil.escapeAttribute(
						StringUtil.toHexString(url.hashCode()))
				).build());
		}
	}

	public static void writeHeaderJavaScriptPaths(
		HttpServletRequest httpServletRequest, Collection<Portlet> portlets,
		Writer writer) {

		Collection<String> urls = _getURLs(
			httpServletRequest,
			Arrays.asList(_HEADER_PORTAL_JS, _HEADER_PORTLET_JS), portlets,
			URLType.JAVASCRIPT);

		for (String url : urls) {
			_writeJavaScriptPath(
				new PrintWriter(writer, true), url,
				Collections.singletonMap("data-senna-track", "temporary"));
		}
	}

	public static void writeHeaderPaths(
			HttpServletResponse httpServletResponse,
			PortletRenderParts portletRenderParts)
		throws IOException {

		_writePaths(
			httpServletResponse, portletRenderParts.getHeaderCssPaths(),
			portletRenderParts.getHeaderJavaScriptPaths());
	}

	private static List<Portlet> _getAllPortlets(
		HttpServletRequest httpServletRequest) {

		List<Portlet> allPortlets =
			(List<Portlet>)httpServletRequest.getAttribute(
				WebKeys.ALL_PORTLETS);

		if (ListUtil.isNotEmpty(allPortlets)) {
			return allPortlets;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		allPortlets = layoutTypePortlet.getAllPortlets();

		httpServletRequest.setAttribute(WebKeys.ALL_PORTLETS, allPortlets);

		return allPortlets;
	}

	private static List<String> _getComboServletURLs(
		Collection<PortletResourceAccessor> portletResourceAccessors,
		Collection<Portlet> portlets, Predicate<String> predicate,
		long timestamp, String urlPrefix, Set<String> visitedURLs) {

		if (predicate == null) {
			predicate = s -> true;
		}

		List<String> urls = new ArrayList<>();

		StringBundler comboServletSB = new StringBundler();

		for (Portlet portlet : portlets) {
			for (PortletResourceAccessor portletResourceAccessor :
					portletResourceAccessors) {

				String contextPath = null;

				if (portletResourceAccessor.isPortalResource()) {
					contextPath = PortalUtil.getPathContext();
				}
				else {
					contextPath =
						PortalUtil.getPathProxy() + portlet.getContextPath();
				}

				Collection<String> portletResources =
					portletResourceAccessor.get(portlet);

				for (String portletResource : portletResources) {
					if (!predicate.test(portletResource)) {
						continue;
					}

					String prefix = null;

					for (String specialPrefix : _specialPrefixes) {
						if (portletResource.startsWith(specialPrefix)) {
							portletResource = portletResource.substring(
								specialPrefix.length());

							prefix = specialPrefix;

							break;
						}
					}

					boolean absolute = HttpComponentsUtil.hasProtocol(
						portletResource);

					if (!absolute) {
						portletResource = contextPath + portletResource;
					}

					if (Validator.isNotNull(prefix)) {
						portletResource = prefix + portletResource;
					}

					if (visitedURLs.contains(portletResource)) {
						continue;
					}

					visitedURLs.add(portletResource);

					if (absolute || Validator.isNotNull(prefix)) {
						urls.add(portletResource);
					}
					else {
						comboServletSB.append(StringPool.AMPERSAND);

						if (!portletResourceAccessor.isPortalResource()) {
							comboServletSB.append(portlet.getPortletId());
							comboServletSB.append(StringPool.COLON);
						}

						comboServletSB.append(
							HtmlUtil.escapeURL(portletResource));

						timestamp = Math.max(timestamp, portlet.getTimestamp());
					}
				}
			}
		}

		if (comboServletSB.length() > 0) {
			String url = urlPrefix + comboServletSB;

			url = HttpComponentsUtil.addParameter(url, "t", timestamp);

			urls.add(url);
		}

		return urls;
	}

	private static String _getMinifierType(URLType urlType) {
		if (urlType == URLType.CSS) {
			return "css";
		}

		return "js";
	}

	private static PortletRenderParts _getPortletRenderParts(
		HttpServletRequest httpServletRequest, String portletHTML,
		Portlet portlet, boolean portletOnLayout) {

		Collection<String> footerCssPaths = Collections.emptyList();
		Collection<String> footerJavaScriptPaths = Collections.emptyList();
		Collection<String> headerCssPaths = Collections.emptyList();
		Collection<String> headerJavaScriptPaths = Collections.emptyList();

		if (!portletOnLayout && portlet.isAjaxable()) {
			footerCssPaths = _getURLs(
				httpServletRequest,
				Arrays.asList(_FOOTER_PORTLET_CSS, _FOOTER_PORTAL_CSS),
				List.of(portlet), URLType.CSS);
			footerJavaScriptPaths = _getURLs(
				httpServletRequest,
				Arrays.asList(_FOOTER_PORTLET_JS, _FOOTER_PORTAL_JS),
				List.of(portlet), URLType.JAVASCRIPT);
			headerCssPaths = _getURLs(
				httpServletRequest,
				Arrays.asList(_HEADER_PORTLET_CSS, _HEADER_PORTAL_CSS),
				List.of(portlet), URLType.CSS);
			headerJavaScriptPaths = _getURLs(
				httpServletRequest,
				Arrays.asList(_HEADER_PORTLET_JS, _HEADER_PORTAL_JS),
				List.of(portlet), URLType.JAVASCRIPT);
		}

		return new PortletRenderParts(
			footerCssPaths, footerJavaScriptPaths, headerCssPaths,
			headerJavaScriptPaths, portletHTML, !portlet.isAjaxable());
	}

	private static String _getRootPortletId(Portlet portlet) {

		// Workaround for portlet#getRootPortletId because that does not return
		// the proper root portlet ID for OpenSocial and WSRP portlets

		Portlet rootPortlet = portlet.getRootPortlet();

		return rootPortlet.getPortletId();
	}

	private static String _getStaticCSSResourceURL(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		String unhashedFileURI) {

		String url = unhashedFileURI;

		String hashedFileURI = HashedFilesRegistryUtil.getHashedFileURI(
			unhashedFileURI);

		if (hashedFileURI == null) {
			if (PortalUtil.isRightToLeft(httpServletRequest)) {
				int i = unhashedFileURI.lastIndexOf(StringPool.PERIOD);

				if (i != -1) {
					url =
						unhashedFileURI.substring(0, i) + "_rtl" +
							unhashedFileURI.substring(i);
				}
			}
		}
		else {
			if (PortalUtil.isRightToLeft(httpServletRequest)) {
				url = HashedFilesUtil.addNameSuffix(hashedFileURI, "_rtl");
			}
			else {
				url = hashedFileURI;
			}
		}

		if (_isTokenized(url)) {
			url = HttpComponentsUtil.addParameter(
				url, "themeId", themeDisplay.getThemeId());
			url = HttpComponentsUtil.addParameter(url, "tokenize", true);
		}

		return url;
	}

	private static List<String> _getStaticURLs(
		HttpServletRequest httpServletRequest,
		Collection<PortletResourceAccessor> portletResourceAccessors,
		Collection<Portlet> portlets, URLType urlType,
		Set<String> visitedURLs) {

		List<String> urls = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (Portlet portlet : portlets) {
			for (PortletResourceAccessor portletResourceAccessor :
					portletResourceAccessors) {

				String contextPath = null;

				if (portletResourceAccessor.isPortalResource()) {
					contextPath = PortalUtil.getPathContext();
				}
				else {
					contextPath =
						PortalUtil.getPathProxy() + portlet.getContextPath();
				}

				Collection<String> portletResources =
					portletResourceAccessor.get(portlet);

				for (String portletResource : portletResources) {
					String prefix = null;

					for (String specialProtocol : _specialPrefixes) {
						if (portletResource.startsWith(specialProtocol)) {
							portletResource = portletResource.substring(
								specialProtocol.length());

							prefix = specialProtocol;

							break;
						}
					}

					if (!HttpComponentsUtil.hasProtocol(portletResource)) {
						if (urlType == URLType.CSS) {
							portletResource = _getStaticCSSResourceURL(
								httpServletRequest, themeDisplay,
								contextPath + portletResource);
						}
						else if (urlType == URLType.JAVASCRIPT) {
							Portlet rootPortlet = portlet.getRootPortlet();

							portletResource = PortalUtil.getStaticResourceURL(
								httpServletRequest,
								contextPath + portletResource,
								rootPortlet.getTimestamp());
						}
						else {
							throw new UnsupportedOperationException(
								"Unsupported URL type " + urlType);
						}
					}

					if (!portletResource.contains(Http.PROTOCOL_DELIMITER)) {
						String cdnBaseURL = themeDisplay.getCDNBaseURL();

						portletResource = cdnBaseURL.concat(portletResource);
					}

					if (Validator.isNotNull(prefix)) {
						portletResource = prefix + portletResource;
					}

					if (visitedURLs.contains(portletResource)) {
						continue;
					}

					visitedURLs.add(portletResource);

					urls.add(portletResource);
				}
			}
		}

		return urls;
	}

	private static Collection<String> _getURLs(
		HttpServletRequest httpServletRequest,
		Collection<PortletResourceAccessor> portletResourceAccessors,
		Collection<Portlet> portlets, URLType urlType) {

		Set<String> visitedURLs = (Set<String>)httpServletRequest.getAttribute(
			WebKeys.PORTLET_RESOURCE_STATIC_URLS);

		if (visitedURLs == null) {
			visitedURLs = new LinkedHashSet<>();

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_RESOURCE_STATIC_URLS, visitedURLs);
		}

		List<String> urls;

		if (urlType == URLType.CSS) {
			urls = _getStaticURLs(
				httpServletRequest, portletResourceAccessors, portlets, urlType,
				visitedURLs);
		}
		else if (urlType == URLType.JAVASCRIPT) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			boolean fastLoad = themeDisplay.isThemeJsFastLoad();

			if (fastLoad) {
				Predicate<String> predicate =
					resource -> !themeDisplay.isIncludedJs(resource);

				Theme theme = themeDisplay.getTheme();

				urls = _getComboServletURLs(
					portletResourceAccessors, portlets, predicate,
					theme.getTimestamp(),
					PortalUtil.getStaticResourceURL(
						httpServletRequest,
						themeDisplay.getCDNDynamicResourcesHost() +
							themeDisplay.getPathContext() + "/combo",
						StringBundler.concat(
							"minifierType=", _getMinifierType(urlType),
							"&themeId=", themeDisplay.getThemeId()),
						-1),
					visitedURLs);
			}
			else {
				urls = _getStaticURLs(
					httpServletRequest, portletResourceAccessors, portlets,
					urlType, visitedURLs);
			}
		}
		else {
			throw new UnsupportedOperationException(
				"Unsupported URL type " + urlType);
		}

		for (int i = 0; i < urls.size(); i++) {
			String url = urls.get(i);

			if (url.startsWith("nocombo:")) {
				urls.set(i, url.substring(8));
			}
		}

		return urls;
	}

	private static boolean _isTokenized(String resourceURI) {
		if (!_tokenized.containsKey(resourceURI)) {
			URL resourceURL = HashedFilesRegistryUtil.getResource(resourceURI);

			String content = null;

			try {
				content = StreamUtil.toString(resourceURL.openStream());
			}
			catch (Exception exception) {
				_log.error(
					"Assuming " + resourceURI +
						" has no tokens because it could not be read",
					exception);

				_tokenized.putIfAbsent(resourceURI, false);

				return false;
			}

			_tokenized.putIfAbsent(
				resourceURI,
				content.contains("@base_url@") ||
				content.contains("@portal_ctx@") ||
				content.contains("@theme_image_path@"));
		}

		return _tokenized.get(resourceURI);
	}

	private static void _writeCSSPath(
		PrintWriter printWriter, String cssPath,
		Map<String, String> attributes) {

		printWriter.print("<link href=\"");
		printWriter.print(HtmlUtil.escape(cssPath));
		printWriter.print(StringPool.QUOTE);
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(null));
		printWriter.println(" rel=\"stylesheet\" type=\"text/css\"");

		if (attributes != null) {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				printWriter.print(StringPool.SPACE);
				printWriter.print(entry.getKey());
				printWriter.print("=\"");
				printWriter.print(HtmlUtil.escapeAttribute(entry.getValue()));
				printWriter.print(StringPool.QUOTE);
			}
		}

		printWriter.println(" />");
	}

	private static void _writeJavaScriptPath(
		PrintWriter printWriter, String javaScriptPath,
		Map<String, String> attributes) {

		String type = "text/javascript";

		if (javaScriptPath.startsWith("module:")) {
			javaScriptPath = javaScriptPath.substring(7);
			type = FrontendESMUtil.getScriptType();
		}

		printWriter.print("<script");
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(null));
		printWriter.print(" src=\"");
		printWriter.print(HtmlUtil.escapeAttribute(javaScriptPath));
		printWriter.print("\" type=\"");
		printWriter.print(type);
		printWriter.print(StringPool.QUOTE);

		if (attributes != null) {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				printWriter.print(StringPool.SPACE);
				printWriter.print(entry.getKey());
				printWriter.print("=\"");
				printWriter.print(HtmlUtil.escapeAttribute(entry.getValue()));
				printWriter.print(StringPool.QUOTE);
			}
		}

		printWriter.println("></script>");
	}

	private static void _writePaths(
			HttpServletResponse httpServletResponse,
			Collection<String> cssPaths, Collection<String> javaScriptPaths)
		throws IOException {

		if ((cssPaths == null) || (javaScriptPaths == null) ||
			(cssPaths.isEmpty() && javaScriptPaths.isEmpty())) {

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		for (String cssPath : cssPaths) {
			_writeCSSPath(printWriter, cssPath, null);
		}

		for (String javaScriptPath : javaScriptPaths) {
			_writeJavaScriptPath(printWriter, javaScriptPath, null);
		}
	}

	private static final PortletResourceAccessor _FOOTER_PORTAL_CSS =
		new PortletResourceAccessor(true) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getFooterPortalCss();
			}

		};

	private static final PortletResourceAccessor _FOOTER_PORTAL_JS =
		new PortletResourceAccessor(true) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getFooterPortalJavaScript();
			}

		};

	private static final PortletResourceAccessor _FOOTER_PORTLET_CSS =
		new PortletResourceAccessor(false) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getFooterPortletCss();
			}

		};

	private static final PortletResourceAccessor _FOOTER_PORTLET_JS =
		new PortletResourceAccessor(false) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getFooterPortletJavaScript();
			}

		};

	private static final PortletResourceAccessor _HEADER_PORTAL_CSS =
		new PortletResourceAccessor(true) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getHeaderPortalCss();
			}

		};

	private static final PortletResourceAccessor _HEADER_PORTAL_JS =
		new PortletResourceAccessor(true) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getHeaderPortalJavaScript();
			}

		};

	private static final PortletResourceAccessor _HEADER_PORTLET_CSS =
		new PortletResourceAccessor(false) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getHeaderPortletCss();
			}

		};

	private static final PortletResourceAccessor _HEADER_PORTLET_JS =
		new PortletResourceAccessor(false) {

			@Override
			public Collection<String> get(Portlet portlet) {
				return portlet.getHeaderPortletJavaScript();
			}

		};

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRenderUtil.class);

	private static final Set<String> _specialPrefixes = SetUtil.fromArray(
		"module:", "nocombo:");
	private static final Map<String, Boolean> _tokenized =
		new ConcurrentHashMap<>();

	private enum URLType {

		CSS, JAVASCRIPT

	}

}