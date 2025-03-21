/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.BundleScriptAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.internal.util.CacheHelper;
import com.liferay.portal.url.builder.internal.util.URLUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;

/**
 * @author Iván Zaera Avellón
 */
public class BundleScriptAbsolutePortalURLBuilderImpl
	extends BaseBundleResourceAbsolutePortalURLBuilderImpl
		<BundleScriptAbsolutePortalURLBuilder>
	implements BundleScriptAbsolutePortalURLBuilder {

	public BundleScriptAbsolutePortalURLBuilderImpl(
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

		if (themeDisplay.isThemeJsFastLoad()) {
			URLUtil.appendParam(sb, "minifierType", "js");
		}
	}

}