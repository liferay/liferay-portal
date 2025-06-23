/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.filter;

import com.liferay.frontend.js.web.internal.frontend.resource.FrontendResource;
import com.liferay.frontend.js.web.internal.frontend.resource.handler.FrontendResourceRequestHandler;
import com.liferay.frontend.js.web.internal.frontend.resource.handler.HashedFileFrontendResourceRequestHandler;
import com.liferay.frontend.js.web.internal.hashed.files.HashedFilesRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"before-filter=Header Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Frontend Resource Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class FrontendResourceFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		for (FrontendResourceRequestHandler frontendResourceRequestHandler :
				_frontendResourceRequestHandlers) {

			if (frontendResourceRequestHandler.canHandleRequest(
					httpServletRequest)) {

				return true;
			}
		}

		return false;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_hashedFilesRegistry = new HashedFilesRegistry(bundleContext);

		HashedFilesRegistry.setHashedFilesRegistry(_hashedFilesRegistry);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ServletContext.class, null,
			(serviceReference, emitter) -> {
				ServletContext servletContext = bundleContext.getService(
					serviceReference);

				try {
					emitter.emit(servletContext.getContextPath());
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});

		_frontendResourceRequestHandlers.add(
			new HashedFileFrontendResourceRequestHandler(
				ContentTypes.APPLICATION_JSON, ".map", _hashedFilesRegistry,
				86400, "esModulesMaxAge", _portal, false,
				"sendNoCacheForESModules", _serviceTrackerMap));
		_frontendResourceRequestHandlers.add(
			new HashedFileFrontendResourceRequestHandler(
				ContentTypes.TEXT_CSS, ".css", _hashedFilesRegistry, 86400,
				"cssStyleSheetsMaxAge", _portal, false,
				"sendNoCacheForCSSStyleSheets", _serviceTrackerMap));
		_frontendResourceRequestHandlers.add(
			new HashedFileFrontendResourceRequestHandler(
				ContentTypes.TEXT_JAVASCRIPT, ".js", _hashedFilesRegistry,
				86400, "esModulesMaxAge", _portal, false,
				"sendNoCacheForESModules", _serviceTrackerMap));
	}

	@Deactivate
	protected void deactivate() {
		_frontendResourceRequestHandlers.clear();

		HashedFilesRegistry.setHashedFilesRegistry(null);

		_hashedFilesRegistry.close();

		_hashedFilesRegistry = null;

		_serviceTrackerMap.close();

		_serviceTrackerMap = null;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		for (FrontendResourceRequestHandler frontendResourceRequestHandler :
				_frontendResourceRequestHandlers) {

			if (frontendResourceRequestHandler.canHandleRequest(
					httpServletRequest)) {

				FrontendResource frontendResource =
					frontendResourceRequestHandler.handleRequest(
						httpServletRequest);

				send(frontendResource, httpServletRequest, httpServletResponse);

				return;
			}
		}

		super.processFilter(
			httpServletRequest, httpServletResponse, filterChain);
	}

	protected void send(
			FrontendResource frontendResource,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (frontendResource == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		String eTag = frontendResource.getETag();

		if (eTag != null) {
			if (StringUtil.equals(
					httpServletRequest.getHeader(HttpHeaders.IF_NONE_MATCH),
					eTag)) {

				httpServletResponse.setStatus(
					HttpServletResponse.SC_NOT_MODIFIED);

				return;
			}

			httpServletResponse.setHeader(HttpHeaders.ETAG, eTag);
		}

		if (frontendResource.isImmutable()) {
			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL,
				"immutable, max-age=31536000, public");
		}
		else {
			StringBuilder sb = new StringBuilder();

			sb.append("max-age=");
			sb.append(frontendResource.getMaxAge());

			if (frontendResource.isSendNoCache()) {
				sb.append(", no-cache");
			}
			else {
				sb.append(", must-revalidate");
			}

			sb.append(", public");

			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL, sb.toString());
		}

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(frontendResource.getContentType());

		try (InputStream inputStream = frontendResource.getInputStream()) {
			StreamUtil.transfer(
				inputStream, httpServletResponse.getOutputStream(), false);
		}
	}

	private final List<FrontendResourceRequestHandler>
		_frontendResourceRequestHandlers = new ArrayList<>();
	private HashedFilesRegistry _hashedFilesRegistry;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, ServletContext> _serviceTrackerMap;

}