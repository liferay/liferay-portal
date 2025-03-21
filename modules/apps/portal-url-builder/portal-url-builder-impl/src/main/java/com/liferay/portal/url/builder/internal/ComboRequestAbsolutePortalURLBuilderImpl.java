/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.ComboRequestAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.internal.util.URLUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Iván Zaera Avellón
 */
public class ComboRequestAbsolutePortalURLBuilderImpl
	implements ComboRequestAbsolutePortalURLBuilder {

	public ComboRequestAbsolutePortalURLBuilderImpl(
		String cdnHost, HttpServletRequest httpServletRequest,
		String pathContext, String pathProxy, Portal portal) {

		_cdnHost = cdnHost;
		_httpServletRequest = httpServletRequest;
		_pathContext = pathContext;
		_pathProxy = pathProxy;
		_portal = portal;
	}

	@Override
	public ComboRequestAbsolutePortalURLBuilder addFile(String filePath) {
		_filePaths.add(URLUtil.removeParams(filePath));

		return this;
	}

	@Override
	public String build() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		boolean cdnDynamicResourcesEnabled =
			_portal.isCDNDynamicResourcesEnabled(themeDisplay.getCompanyId());

		String comboPath = _portal.getStaticResourceURL(
			_httpServletRequest, "/combo", "minifierType=js", _timestamp);

		StringBundler sb = new StringBundler();

		URLUtil.appendURL(
			sb, _cdnHost, !cdnDynamicResourcesEnabled, _pathContext, _pathProxy,
			comboPath);

		for (String filePath : _filePaths) {
			sb.append("&");
			sb.append(filePath);
		}

		return sb.toString();
	}

	@Override
	public ComboRequestAbsolutePortalURLBuilder setTimestamp(long timestamp) {
		_timestamp = timestamp;

		return this;
	}

	private final String _cdnHost;
	private final Set<String> _filePaths = new LinkedHashSet<>();
	private final HttpServletRequest _httpServletRequest;
	private final String _pathContext;
	private final String _pathProxy;
	private final Portal _portal;
	private long _timestamp;

}