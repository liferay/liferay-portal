/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.theme.contributor.extender.internal.events;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gabriel Lima
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class AtlasCustomPropertiesLifecycleAction implements LifecycleAction {

	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent) {
		HttpServletRequest httpServletRequest = lifecycleEvent.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((themeDisplay == null) || (themeDisplay.getTheme() == null) ||
			!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-84497")) {

			return;
		}

		Theme theme = themeDisplay.getTheme();

		String prefix = PortalUtil.getPathModule();
		String proxyPath = PortalUtil.getPathProxy();

		if (prefix.startsWith(proxyPath)) {
			prefix = prefix.substring(proxyPath.length());
		}

		String basePath = StringBundler.concat(
			prefix, StringPool.SLASH, theme.getServletContextName(),
			theme.getCssPath());

		String mainAtlasName = PortalUtil.isRightToLeft(httpServletRequest) ?
			"/main-atlas-custom-properties_rtl.css" :
				"/main-atlas-custom-properties.css";

		String mainHashedFileURI = HashedFilesRegistryUtil.getHashedFileURI(
			basePath + mainAtlasName);

		if (Validator.isNotNull(mainHashedFileURI)) {
			try {
				themeDisplay.setMainCSSURL(
					PortalUtil.getCDNHost(httpServletRequest) + proxyPath +
						mainHashedFileURI);
			}
			catch (PortalException portalException) {
				_log.error("Unable to set main CSS URL", portalException);
			}
		}

		String clayAtlasName = PortalUtil.isRightToLeft(httpServletRequest) ?
			"/clay-atlas-custom-properties_rtl.css" :
				"/clay-atlas-custom-properties.css";

		String clayHashedFileURI = HashedFilesRegistryUtil.getHashedFileURI(
			basePath + clayAtlasName);

		if (Validator.isNotNull(clayHashedFileURI)) {
			try {
				themeDisplay.setClayCSSURL(
					PortalUtil.getCDNHost(httpServletRequest) + proxyPath +
						clayHashedFileURI);
			}
			catch (PortalException portalException) {
				_log.error("Unable to set Clay CSS URL", portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AtlasCustomPropertiesLifecycleAction.class);

}