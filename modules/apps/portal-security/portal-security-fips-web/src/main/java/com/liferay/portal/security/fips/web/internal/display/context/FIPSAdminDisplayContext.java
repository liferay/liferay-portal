/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Manuele Castro
 */
public class FIPSAdminDisplayContext {

	public FIPSAdminDisplayContext(
		FIPSSessionConfiguration fipsSessionConfiguration,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_fipsSessionConfiguration = fipsSessionConfiguration;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public FIPSSessionConfiguration getFIPSSessionConfiguration() {
		return _fipsSessionConfiguration;
	}

	public List<NavigationItem> getNavigationItems() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);
				navigationItem.setHref(_renderResponse.createRenderURL());
				navigationItem.setLabel(
					LanguageUtil.get(
						themeDisplay.getLocale(),
						"fips-session-configuration-name"));
			}
		).build();
	}

	private final FIPSSessionConfiguration _fipsSessionConfiguration;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}