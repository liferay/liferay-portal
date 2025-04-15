/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portal.url.builder.facet.BuildableAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.facet.CDNAwareAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.facet.CacheAwareAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.facet.PathProxyAwareAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.internal.util.CacheHelper;
import com.liferay.portal.url.builder.internal.util.URLUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;

/**
 * @author Iván Zaera Avellón
 */
public abstract class BaseBundleResourceAbsolutePortalURLBuilderImpl<T>
	implements BuildableAbsolutePortalURLBuilder,
			   CacheAwareAbsolutePortalURLBuilder<T>,
			   CDNAwareAbsolutePortalURLBuilder<T>,
			   PathProxyAwareAbsolutePortalURLBuilder<T> {

	public BaseBundleResourceAbsolutePortalURLBuilderImpl(
		Bundle bundle, CacheHelper cacheHelper, String cdnHost,
		HttpServletRequest httpServletRequest, String pathModule,
		String pathProxy, String relativeURL) {

		if (!relativeURL.startsWith(StringPool.SLASH)) {
			relativeURL = StringPool.SLASH + relativeURL;
		}

		_bundle = bundle;
		_cacheHelper = cacheHelper;
		_cdnHost = cdnHost;
		_httpServletRequest = httpServletRequest;
		_pathProxy = pathProxy;
		_relativeURL = relativeURL;

		_bundlePathPrefix = URLUtil.getBundlePathPrefix(bundle, pathModule);
	}

	@Override
	public String build() {
		StringBundler sb = new StringBundler();

		URLUtil.appendURL(
			sb, _cdnHost, _ignoreCDNHost, _ignorePathProxy, _bundlePathPrefix,
			_pathProxy, _relativeURL);

		sb.append(StringPool.QUESTION);

		_cacheHelper.appendCacheParam(sb, _bundle, _cachePolicy, _relativeURL);

		URLUtil.appendParam(
			sb, "browserId",
			BrowserSnifferUtil.getBrowserId(_httpServletRequest));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		URLUtil.appendParam(sb, "languageId", themeDisplay.getLanguageId());

		addSpecificParams(_httpServletRequest, sb);

		return sb.toString();
	}

	@Override
	public T cache(CachePolicy cachePolicy) {
		_cachePolicy = cachePolicy;

		return (T)this;
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

	protected abstract void addSpecificParams(
		HttpServletRequest httpServletRequest, StringBundler sb);

	private final Bundle _bundle;
	private final String _bundlePathPrefix;
	private final CacheHelper _cacheHelper;
	private CachePolicy _cachePolicy = CachePolicy.UNTIL_CHANGED;
	private final String _cdnHost;
	private final HttpServletRequest _httpServletRequest;
	private boolean _ignoreCDNHost;
	private boolean _ignorePathProxy;
	private final String _pathProxy;
	private final String _relativeURL;

}