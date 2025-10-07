/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.url.builder.ESModuleAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.internal.util.URLUtil;

/**
 * @author Iván Zaera Avellón
 */
public class ESModuleAbsolutePortalURLBuilderImpl
	implements ESModuleAbsolutePortalURLBuilder {

	public ESModuleAbsolutePortalURLBuilderImpl(
		String esModulePath, String cdnHost, String pathModule,
		String pathProxy, String webContextPath) {

		if (!esModulePath.startsWith(StringPool.SLASH)) {
			esModulePath = StringPool.SLASH + esModulePath;
		}

		if (!webContextPath.startsWith(StringPool.SLASH)) {
			webContextPath = StringPool.SLASH + webContextPath;
		}

		_esModulePath = esModulePath;
		_cdnHost = cdnHost;
		_pathModule = pathModule;
		_pathProxy = pathProxy;
		_webContextPath = webContextPath;
	}

	@Override
	public String build() {
		StringBundler sb = new StringBundler();

		URLUtil.appendURL(
			sb, _cdnHost, _ignoreCDNHost, _ignorePathProxy,
			_pathModule + _webContextPath + "/__liferay__", _pathProxy,
			_esModulePath);

		return sb.toString();
	}

	@Override
	public ESModuleAbsolutePortalURLBuilder ignoreCDNHost() {
		_ignoreCDNHost = true;

		return this;
	}

	@Override
	public ESModuleAbsolutePortalURLBuilder ignorePathProxy() {
		_ignorePathProxy = true;

		return this;
	}

	private final String _cdnHost;
	private final String _esModulePath;
	private boolean _ignoreCDNHost;
	private boolean _ignorePathProxy;
	private final String _pathModule;
	private final String _pathProxy;
	private final String _webContextPath;

}