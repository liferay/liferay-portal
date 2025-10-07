/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.portlet.PortletDependency;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.BrowserModuleAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.BundleScriptAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.BundleStylesheetAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.ComboRequestAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.ESModuleAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.PortalImageAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.PortalMainResourceAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.PortletDependencyAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.ServletAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.WebContextScriptAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.WebContextStylesheetAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.internal.util.CacheHelper;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;

/**
 * @author Iván Zaera Avellón
 */
public class AbsolutePortalURLBuilderImpl implements AbsolutePortalURLBuilder {

	public AbsolutePortalURLBuilderImpl(
		CacheHelper cacheHelper, HashedFilesRegistry hashedFilesRegistry,
		Portal portal, HttpServletRequest httpServletRequest) {

		_cacheHelper = cacheHelper;
		_hashedFilesRegistry = hashedFilesRegistry;
		_portal = portal;
		_httpServletRequest = httpServletRequest;

		String pathContext = portal.getPathContext();

		String pathProxy = portal.getPathProxy();

		_pathContext = pathContext.substring(pathProxy.length());

		_pathImage = _pathContext + Portal.PATH_IMAGE;
		_pathMain = _pathContext + Portal.PATH_MAIN;
		_pathModule = _pathContext + Portal.PATH_MODULE;

		_pathProxy = _computePathProxy();
	}

	@Override
	public BrowserModuleAbsolutePortalURLBuilder forBrowserModule(
		String browserModulePath) {

		return new BrowserModuleAbsolutePortalURLBuilderImpl(
			browserModulePath, _pathContext, _pathProxy);
	}

	@Override
	public BundleScriptAbsolutePortalURLBuilder forBundleScript(
		Bundle bundle, String relativeURL) {

		return new BundleScriptAbsolutePortalURLBuilderImpl(
			bundle, _cacheHelper, _getCDNHost(_httpServletRequest),
			_httpServletRequest, _pathModule, _pathProxy, relativeURL);
	}

	@Override
	public BundleStylesheetAbsolutePortalURLBuilder forBundleStylesheet(
		Bundle bundle, String relativeURL) {

		return new BundleStylesheetAbsolutePortalURLBuilderImpl(
			bundle, _cacheHelper, _getCDNHost(_httpServletRequest),
			_httpServletRequest, _pathModule, _pathProxy, relativeURL);
	}

	@Override
	public ComboRequestAbsolutePortalURLBuilder forComboRequest() {
		return new ComboRequestAbsolutePortalURLBuilderImpl(
			_getCDNHost(_httpServletRequest), _httpServletRequest, _pathContext,
			_pathProxy, _portal);
	}

	@Override
	public ESModuleAbsolutePortalURLBuilder forESModule(
		String webContextPath, String esModulePath) {

		return new ESModuleAbsolutePortalURLBuilderImpl(
			esModulePath, _getCDNHost(_httpServletRequest), _pathModule,
			_pathProxy, webContextPath);
	}

	@Override
	public PortalImageAbsolutePortalURLBuilder forPortalImage(
		String relativeURL) {

		return new PortalImageAbsolutePortalURLBuilderImpl(
			_getCDNHost(_httpServletRequest), _pathImage, _pathProxy,
			relativeURL);
	}

	@Override
	public PortalMainResourceAbsolutePortalURLBuilder forPortalMainResource(
		String relativeURL) {

		return new PortalMainResourceAbsolutePortalURLBuilderImpl(
			_pathMain, _pathProxy, relativeURL);
	}

	@Override
	public PortletDependencyAbsolutePortalURLBuilder forPortletDependency(
		PortletDependency portletDependency, String cssURN,
		String javaScriptURN) {

		return new PortletDependencyAbsolutePortalURLBuilderImpl(
			_getCDNHost(_httpServletRequest), cssURN, javaScriptURN, _pathProxy,
			portletDependency);
	}

	@Override
	public ServletAbsolutePortalURLBuilder forServlet(String requestURL) {
		return new ServletAbsolutePortalURLBuilderImpl(
			_pathModule, _pathProxy, requestURL);
	}

	@Override
	public WebContextScriptAbsolutePortalURLBuilder forWebContextScript(
		String webContextPath, String scriptPath) {

		return new WebContextScriptAbsolutePortalURLBuilderImpl(
			_getCDNHost(_httpServletRequest), _hashedFilesRegistry, _pathModule,
			_pathProxy, scriptPath, webContextPath);
	}

	@Override
	public WebContextStylesheetAbsolutePortalURLBuilder forWebContextStylesheet(
		String webContextPath, String stylesheetPath) {

		if (_portal.isRightToLeft(_httpServletRequest)) {
			int i = stylesheetPath.lastIndexOf(StringPool.PERIOD);

			if (i == -1) {
				stylesheetPath += "_rtl";
			}
			else {
				stylesheetPath =
					stylesheetPath.substring(0, i) + "_rtl" +
						stylesheetPath.substring(i);
			}
		}

		return new WebContextStylesheetAbsolutePortalURLBuilderImpl(
			_getCDNHost(_httpServletRequest), _hashedFilesRegistry, _pathModule,
			_pathProxy, stylesheetPath, webContextPath);
	}

	private String _computePathProxy() {
		String pathProxy = _portal.getPathProxy();

		if (!Validator.isBlank(pathProxy) &&
			!pathProxy.startsWith(StringPool.SLASH)) {

			pathProxy = StringPool.SLASH + pathProxy;
		}

		if (pathProxy.endsWith(StringPool.SLASH)) {
			pathProxy = pathProxy.substring(0, pathProxy.length() - 1);
		}

		return pathProxy;
	}

	private String _getCDNHost(HttpServletRequest httpServletRequest) {
		String cdnHost;

		try {
			cdnHost = _portal.getCDNHost(httpServletRequest);
		}
		catch (PortalException portalException) {
			cdnHost = StringPool.BLANK;

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to retrieve CDN host from request",
					portalException);
			}
		}

		if (cdnHost.endsWith(StringPool.SLASH)) {
			cdnHost = cdnHost.substring(0, cdnHost.length() - 1);
		}

		return cdnHost;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AbsolutePortalURLBuilderImpl.class);

	private final CacheHelper _cacheHelper;
	private final HashedFilesRegistry _hashedFilesRegistry;
	private final HttpServletRequest _httpServletRequest;

	/**
	 * Portal web app's web context path (does not contain the proxy, CDN, or
	 * any other kind of configurable path.
	 */
	private final String _pathContext;

	private final String _pathImage;
	private final String _pathMain;
	private final String _pathModule;
	private final String _pathProxy;
	private final Portal _portal;

}