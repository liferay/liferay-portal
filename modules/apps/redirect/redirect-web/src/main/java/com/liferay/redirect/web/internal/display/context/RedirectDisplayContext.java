/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.redirect.configuration.RedirectConfiguration;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class RedirectDisplayContext {

	public RedirectDisplayContext(
		HttpServletRequest httpServletRequest,
		RedirectConfiguration redirectConfiguration,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_redirectConfiguration = redirectConfiguration;
		_renderResponse = renderResponse;
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(isShowRedirectEntries());
				navigationItem.setHref(_renderResponse.createRenderURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "aliases"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(_isShowRedirectPatterns());
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "navigation",
					"patterns");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "patterns"));
			}
		).add(
			_redirectConfiguration::isRedirectNotFoundEnabled,
			navigationItem -> {
				navigationItem.setActive(isShowRedirectNotFoundEntries());
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "navigation",
					"404-urls");
				navigationItem.setLabel(
					LanguageUtil.format(
						_httpServletRequest, "x-urls",
						HttpServletResponse.SC_NOT_FOUND, false));
			}
		).build();
	}

	public boolean isShowRedirectEntries() {
		return _isShowNavigationPanel("aliases");
	}

	public boolean isShowRedirectNotFoundEntries() {
		return _isShowNavigationPanel("404-urls");
	}

	private boolean _isShowNavigationPanel(String name) {
		String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "aliases");

		return navigation.equals(name);
	}

	private boolean _isShowRedirectPatterns() {
		return _isShowNavigationPanel("patterns");
	}

	private final HttpServletRequest _httpServletRequest;
	private final RedirectConfiguration _redirectConfiguration;
	private final RenderResponse _renderResponse;

}