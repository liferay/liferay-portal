/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.BundleStylesheetAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.internal.util.CacheHelper;
import com.liferay.portal.url.builder.internal.util.URLUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;

/**
 * @author Iván Zaera Avellón
 */
public class BundleStylesheetAbsolutePortalURLBuilderImpl
	extends BaseBundleResourceAbsolutePortalURLBuilderImpl
		<BundleStylesheetAbsolutePortalURLBuilder>
	implements BundleStylesheetAbsolutePortalURLBuilder {

	public BundleStylesheetAbsolutePortalURLBuilderImpl(
		Bundle bundle, CacheHelper cacheHelper, String cdnHost,
		HttpServletRequest httpServletRequest, String pathModule,
		String pathProxy, String relativeURL) {

		super(
			bundle, cacheHelper, cdnHost, httpServletRequest, pathModule,
			pathProxy, relativeURL);
	}

	@Override
	protected void addSpecificParams(
		HttpServletRequest httpServletRequest, StringBundler sb) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isThemeCssFastLoad()) {
			URLUtil.appendParam(sb, "minifierType", "css");
		}

		Theme theme = themeDisplay.getTheme();

		URLUtil.appendParam(
			sb, "themeId", URLCodec.encodeURL(theme.getThemeId()));
	}

}