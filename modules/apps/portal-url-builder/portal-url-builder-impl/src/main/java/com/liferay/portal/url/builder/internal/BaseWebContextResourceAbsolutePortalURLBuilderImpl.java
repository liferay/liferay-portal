/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.CachingStrategy;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.url.builder.facet.BuildableAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.facet.CDNAwareAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.facet.PathProxyAwareAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.internal.util.URLUtil;

/**
 * @author Iván Zaera Avellón
 */
public abstract class BaseWebContextResourceAbsolutePortalURLBuilderImpl<T>
	implements BuildableAbsolutePortalURLBuilder,
			   CDNAwareAbsolutePortalURLBuilder<T>,
			   PathProxyAwareAbsolutePortalURLBuilder<T> {

	public BaseWebContextResourceAbsolutePortalURLBuilderImpl(
		CachingStrategy cachingStrategy, String cdnHost,
		boolean enableUseOneHashPerWebContext,
		HashedFilesRegistry hashedFilesRegistry, String pathModule,
		String pathProxy, String resourcePath, String webContextName) {

		if (!resourcePath.startsWith(StringPool.SLASH)) {
			resourcePath = StringPool.SLASH + resourcePath;
		}

		String webContextPath = StringPool.SLASH + webContextName;

		if (!enableUseOneHashPerWebContext &&
			(cachingStrategy == CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT)) {

			cachingStrategy = CachingStrategy.USE_ONE_HASH_PER_FILE;
		}

		if (cachingStrategy == CachingStrategy.USE_ONE_HASH_PER_FILE) {
			String prefix = pathModule + webContextPath;

			String hashedFileURI = hashedFilesRegistry.getHashedFileURI(
				prefix + resourcePath);

			if (hashedFileURI != null) {
				resourcePath = hashedFileURI.substring(prefix.length());
			}
		}
		else if (cachingStrategy ==
					CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT) {

			String servletContextHash =
				hashedFilesRegistry.getServletContextHash(webContextName);

			if (servletContextHash != null) {
				webContextPath = StringBundler.concat(
					"/js/-", webContextPath, StringPool.OPEN_PARENTHESIS,
					servletContextHash, StringPool.CLOSE_PARENTHESIS);
			}
		}

		_cdnHost = cdnHost;
		_pathModule = pathModule;
		_pathProxy = pathProxy;
		_resourcePath = resourcePath;

		_webContextPath = webContextPath;
	}

	@Override
	public String build() {
		StringBundler sb = new StringBundler();

		URLUtil.appendURL(
			sb, _cdnHost, _ignoreCDNHost, _ignorePathProxy,
			_pathModule + _webContextPath, _pathProxy, _resourcePath);

		return sb.toString();
	}

	@Override
	public T ignoreCDNHost() {
		_ignoreCDNHost = true;

		return (T)this;
	}

	@Override
	public T ignorePathProxy() {
		_ignorePathProxy = true;

		return (T)this;
	}

	private final String _cdnHost;
	private boolean _ignoreCDNHost;
	private boolean _ignorePathProxy;
	private final String _pathModule;
	private final String _pathProxy;
	private final String _resourcePath;
	private final String _webContextPath;

}