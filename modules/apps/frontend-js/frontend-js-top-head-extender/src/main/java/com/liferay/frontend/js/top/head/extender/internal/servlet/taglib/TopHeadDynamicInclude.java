/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.top.head.extender.internal.servlet.taglib;

import com.liferay.frontend.js.top.head.extender.TopHeadResources;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResources;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.ComboRequestAbsolutePortalURLBuilder;
import com.liferay.portal.util.JavaScriptBundleUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * @author Iván Zaera Avellón
 */
@Component(service = DynamicInclude.class)
public class TopHeadDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ResourceURLsHolder resourceURLsHolder = _getResourceURLsHolder();

		if (themeDisplay.isThemeJsFastLoad()) {
			if (themeDisplay.isThemeJsBarebone()) {
				_renderBundleComboURLs(
					httpServletRequest, httpServletResponse,
					resourceURLsHolder._jsResourceURLs);
			}
			else {
				_renderBundleComboURLs(
					httpServletRequest, httpServletResponse,
					resourceURLsHolder._allJsResourceURLs);
			}
		}
		else {
			if (themeDisplay.isThemeJsBarebone()) {
				_renderBundleURLs(
					httpServletRequest, httpServletResponse,
					resourceURLsHolder._jsResourceURLs);
			}
			else {
				_renderBundleURLs(
					httpServletRequest, httpServletResponse,
					resourceURLsHolder._allJsResourceURLs);
			}
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_topHeadResourcesServiceTracker = ServiceTrackerFactory.open(
			bundleContext, TopHeadResources.class,
			new ServiceTrackerCustomizer<TopHeadResources, TopHeadResources>() {

				@Override
				public TopHeadResources addingService(
					ServiceReference<TopHeadResources> serviceReference) {

					synchronized (_topHeadResourcesServiceReferences) {
						_topHeadResourcesServiceReferences.add(
							serviceReference);

						_resourceURLsHolder = null;
					}

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<TopHeadResources> serviceReference,
					TopHeadResources topHeadResources) {
				}

				@Override
				public void removedService(
					ServiceReference<TopHeadResources> serviceReference,
					TopHeadResources topHeadResources) {

					synchronized (_topHeadResourcesServiceReferences) {
						_topHeadResourcesServiceReferences.remove(
							serviceReference);

						_resourceURLsHolder = null;
					}

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_topHeadResourcesServiceTracker.close();
	}

	private ResourceURLsHolder _getResourceURLsHolder() {
		ResourceURLsHolder resourceURLsHolder = _resourceURLsHolder;

		if (resourceURLsHolder != null) {
			return resourceURLsHolder;
		}

		synchronized (_topHeadResourcesServiceReferences) {
			if (_resourceURLsHolder != null) {
				return _resourceURLsHolder;
			}

			_resourceURLsHolder = _rebuild();

			return _resourceURLsHolder;
		}
	}

	private ResourceURLsHolder _rebuild() {
		PortalWebResources portalWebResources =
			PortalWebResourcesUtil.getPortalWebResources(
				PortalWebResourceConstants.RESOURCE_TYPE_JS);

		if (portalWebResources == null) {
			return null;
		}

		List<String> allJsResourceURLs = new ArrayList<>();
		List<String> jsResourceURLs = new ArrayList<>();

		Collections.addAll(
			allJsResourceURLs,
			JavaScriptBundleUtil.getFileNames(
				PropsKeys.JAVASCRIPT_EVERYTHING_FILES));

		Collections.addAll(
			jsResourceURLs,
			JavaScriptBundleUtil.getFileNames(
				PropsKeys.JAVASCRIPT_BAREBONE_FILES));

		for (ServiceReference<TopHeadResources>
				topHeadResourcesServiceReference :
					_topHeadResourcesServiceReferences) {

			TopHeadResources topHeadResources = _bundleContext.getService(
				topHeadResourcesServiceReference);

			try {
				String bundleContextPath = _portal.getPathContext(
					topHeadResources.getServletContextPath());

				for (String jsResourcePath :
						topHeadResources.getJsResourcePaths()) {

					String url = bundleContextPath + jsResourcePath;

					allJsResourceURLs.add(url);
					jsResourceURLs.add(url);
				}

				for (String jsResourcePath :
						topHeadResources.getAuthenticatedJsResourcePaths()) {

					allJsResourceURLs.add(bundleContextPath + jsResourcePath);
				}
			}
			finally {
				_bundleContext.ungetService(topHeadResourcesServiceReference);
			}
		}

		return new ResourceURLsHolder(allJsResourceURLs, jsResourceURLs);
	}

	private void _renderBundleComboURLs(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, List<String> urls)
		throws IOException {

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		ComboRequestAbsolutePortalURLBuilder
			comboRequestAbsolutePortalURLBuilder =
				absolutePortalURLBuilder.forComboRequest();

		comboRequestAbsolutePortalURLBuilder.setTimestamp(
			PortalWebResourcesUtil.getLastModified(
				PortalWebResourceConstants.RESOURCE_TYPE_JS));

		String comboURL = comboRequestAbsolutePortalURLBuilder.build();

		PrintWriter printWriter = httpServletResponse.getWriter();

		StringBundler sb = new StringBundler();

		for (String url : urls) {
			if ((sb.length() + url.length() + 1) >= 2000) {
				_renderScriptURL(
					httpServletRequest, printWriter, sb.toString());

				sb = new StringBundler();
			}

			if (sb.length() == 0) {
				sb.append(comboURL);
			}

			sb.append(StringPool.AMPERSAND);
			sb.append(url);
		}

		if (sb.length() > 0) {
			_renderScriptURL(httpServletRequest, printWriter, sb.toString());
		}
	}

	private void _renderBundleURLs(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, List<String> urls)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		for (String url : urls) {
			_renderScriptURL(httpServletRequest, printWriter, url);
		}
	}

	private void _renderScriptURL(
		HttpServletRequest httpServletRequest, PrintWriter printWriter,
		String url) {

		printWriter.print("<script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.print(" data-senna-track=\"permanent\" src=\"");
		printWriter.print(url);
		printWriter.println("\" type=\"text/javascript\"></script>");
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private BundleContext _bundleContext;

	@Reference
	private Portal _portal;

	private volatile ResourceURLsHolder _resourceURLsHolder;
	private final Collection<ServiceReference<TopHeadResources>>
		_topHeadResourcesServiceReferences = new TreeSet<>();
	private ServiceTracker<TopHeadResources, TopHeadResources>
		_topHeadResourcesServiceTracker;

	private static class ResourceURLsHolder {

		private ResourceURLsHolder(
			List<String> allJsResourceURLs, List<String> jsResourceURLs) {

			_allJsResourceURLs = allJsResourceURLs;
			_jsResourceURLs = jsResourceURLs;
		}

		private final List<String> _allJsResourceURLs;
		private final List<String> _jsResourceURLs;

	}

}