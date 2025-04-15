/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.theme.contributor.extender.internal.servlet.taglib;

import com.liferay.frontend.theme.contributor.extender.internal.BundleWebResources;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = DynamicInclude.class)
public class ThemeContributorTopHeadDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		long themeLastModified = PortalWebResourcesUtil.getLastModified(
			PortalWebResourceConstants.RESOURCE_TYPE_THEME_CONTRIBUTOR);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portalCDNURL = themeDisplay.getCDNBaseURL();

		if (!_portal.isCDNDynamicResourcesEnabled(
				themeDisplay.getCompanyId())) {

			portalCDNURL = themeDisplay.getPortalURL();
		}

		ResourceURLsBag resourceURLsBag = _getResourceURLsBag();

		List<String> cssResourceURLs = resourceURLsBag._cssResourceURLs;

		if (!cssResourceURLs.isEmpty()) {
			if (themeDisplay.isThemeCssFastLoad()) {
				_renderComboCSS(
					themeLastModified, httpServletRequest, portalCDNURL,
					httpServletResponse.getWriter());
			}
			else {
				_renderSimpleCSS(
					themeLastModified, httpServletRequest, portalCDNURL,
					httpServletResponse.getWriter(), cssResourceURLs);
			}
		}

		List<String> jsResourceURLs = resourceURLsBag._jsResourceURLs;

		if (jsResourceURLs.isEmpty()) {
			return;
		}

		if (themeDisplay.isThemeJsFastLoad()) {
			_renderComboJS(
				themeLastModified, httpServletRequest, portalCDNURL,
				httpServletResponse.getWriter());
		}
		else {
			_renderSimpleJS(
				themeLastModified, httpServletRequest, portalCDNURL,
				httpServletResponse.getWriter(), jsResourceURLs);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_comboContextPath = _portal.getPathContext() + "/combo";

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, BundleWebResources.class,
			new ServiceTrackerCustomizer
				<BundleWebResources, BundleWebResources>() {

				@Override
				public BundleWebResources addingService(
					ServiceReference<BundleWebResources> serviceReference) {

					synchronized (_bundleWebResourcesServiceReferences) {
						_bundleWebResourcesServiceReferences.add(
							serviceReference);

						_resourceURLsBag = null;
					}

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<BundleWebResources> serviceReference,
					BundleWebResources bundleWebResources) {
				}

				@Override
				public void removedService(
					ServiceReference<BundleWebResources> serviceReference,
					BundleWebResources bundleWebResources) {

					synchronized (_bundleWebResourcesServiceReferences) {
						_bundleWebResourcesServiceReferences.remove(
							serviceReference);

						_resourceURLsBag = null;
					}

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private ResourceURLsBag _getResourceURLsBag() {
		ResourceURLsBag resourceURLsBag = _resourceURLsBag;

		if (resourceURLsBag != null) {
			return resourceURLsBag;
		}

		synchronized (_bundleWebResourcesServiceReferences) {
			if (_resourceURLsBag != null) {
				return _resourceURLsBag;
			}

			_resourceURLsBag = _rebuild();

			return _resourceURLsBag;
		}
	}

	private ResourceURLsBag _rebuild() {
		List<String> cssResourceURLs = new ArrayList<>();
		List<String> jsResourceURLs = new ArrayList<>();

		for (ServiceReference<BundleWebResources>
				bundleWebResourcesServiceReference :
					_bundleWebResourcesServiceReferences) {

			BundleWebResources bundleWebResources = _bundleContext.getService(
				bundleWebResourcesServiceReference);

			try {
				String servletContextPath =
					bundleWebResources.getServletContextPath();

				for (String cssResourcePath :
						bundleWebResources.getCssResourcePaths()) {

					cssResourceURLs.add(
						servletContextPath.concat(cssResourcePath));
				}

				for (String jsResourcePath :
						bundleWebResources.getJsResourcePaths()) {

					jsResourceURLs.add(
						servletContextPath.concat(jsResourcePath));
				}
			}
			finally {
				_bundleContext.ungetService(bundleWebResourcesServiceReference);
			}
		}

		return new ResourceURLsBag(cssResourceURLs, jsResourceURLs);
	}

	private void _renderComboCSS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter) {

		printWriter.write("<link");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonce(
				httpServletRequest));
		printWriter.write(" data-senna-track=\"permanent\" href=\"");

		String staticResourceURL = _portal.getStaticResourceURL(
			httpServletRequest, _comboContextPath, "minifierType=css",
			themeLastModified);

		printWriter.write(portalURL + staticResourceURL);

		ResourceURLsBag resourceURLsBag = _getResourceURLsBag();

		printWriter.write(resourceURLsBag.getMergedCSSResourceURLs());
	}

	private void _renderComboJS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter) {

		printWriter.write("<script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.write(" data-senna-track=\"permanent\" src=\"");

		String staticResourceURL = _portal.getStaticResourceURL(
			httpServletRequest, _comboContextPath, "minifierType=js",
			themeLastModified);

		printWriter.write(portalURL + staticResourceURL);

		ResourceURLsBag resourceURLsBag = _getResourceURLsBag();

		printWriter.write(resourceURLsBag.getMergedJSResourceURLs());
	}

	private void _renderSimpleCSS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter, List<String> resourceURLs) {

		for (String resourceURL : resourceURLs) {
			printWriter.write("<link data-senna-track=\"permanent\" href=\"");
			printWriter.write(
				_portal.getStaticResourceURL(
					httpServletRequest,
					StringBundler.concat(
						portalURL, _portal.getPathProxy(), resourceURL),
					themeLastModified));
			printWriter.write(StringPool.QUOTE);
			printWriter.write(
				ContentSecurityPolicyNonceProviderUtil.getNonce(
					httpServletRequest));
			printWriter.write(" rel=\"stylesheet\" type = \"text/css\" />\n");
		}
	}

	private void _renderSimpleJS(
		long themeLastModified, HttpServletRequest httpServletRequest,
		String portalURL, PrintWriter printWriter, List<String> resourceURLs) {

		for (String resourceURL : resourceURLs) {
			printWriter.write("<script");
			printWriter.write(
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					httpServletRequest));
			printWriter.write(" data-senna-track=\"permanent\" src=\"");
			printWriter.write(
				_portal.getStaticResourceURL(
					httpServletRequest,
					StringBundler.concat(
						portalURL, _portal.getPathProxy(), resourceURL),
					themeLastModified));
			printWriter.write("\" type = \"text/javascript\"></script>\n");
		}
	}

	private BundleContext _bundleContext;
	private final Collection<ServiceReference<BundleWebResources>>
		_bundleWebResourcesServiceReferences = new TreeSet<>();
	private String _comboContextPath;

	@Reference
	private Portal _portal;

	private volatile ResourceURLsBag _resourceURLsBag;
	private ServiceTracker<BundleWebResources, BundleWebResources>
		_serviceTracker;

	private static class ResourceURLsBag {

		public String getMergedCSSResourceURLs() {
			String mergedCSSResourceURLs = _mergedCSSResourceURLs;

			if (mergedCSSResourceURLs == null) {
				mergedCSSResourceURLs =
					_mergeURLs(_cssResourceURLs) +
						"\" rel=\"stylesheet\" type = \"text/css\" />\n";

				_mergedCSSResourceURLs = mergedCSSResourceURLs;
			}

			return mergedCSSResourceURLs;
		}

		public String getMergedJSResourceURLs() {
			String mergedJSResourceURLs = _mergedJSResourceURLs;

			if (mergedJSResourceURLs == null) {
				mergedJSResourceURLs =
					_mergeURLs(_jsResourceURLs) +
						"\" type = \"text/javascript\"></script>\n";

				_mergedJSResourceURLs = mergedJSResourceURLs;
			}

			return mergedJSResourceURLs;
		}

		private ResourceURLsBag(
			List<String> cssResourceURLs, List<String> jsResourceURLs) {

			_cssResourceURLs = cssResourceURLs;
			_jsResourceURLs = jsResourceURLs;
		}

		private String _mergeURLs(List<String> urls) {
			StringBundler sb = new StringBundler(urls.size() * 2);

			for (String url : urls) {
				sb.append("&");
				sb.append(url);
			}

			return sb.toString();
		}

		private final List<String> _cssResourceURLs;
		private final List<String> _jsResourceURLs;
		private volatile String _mergedCSSResourceURLs;
		private volatile String _mergedJSResourceURLs;

	}

}