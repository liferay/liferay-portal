/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Péter Alius
 */
public class StagingProcessesWebDisplayContext {

	public StagingProcessesWebDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				String activeTab = ParamUtil.getString(
					_httpServletRequest, "tabs1", "processes");

				navigationItem.setActive(activeTab.equals("processes"));

				navigationItem.setHref(
					_renderResponse.createRenderURL(), "tabs1", "processes");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "processes"));
			}
		).add(
			navigationItem -> {
				String activeTab = ParamUtil.getString(
					_httpServletRequest, "tabs1", "processes");

				navigationItem.setActive(activeTab.equals("scheduled"));

				navigationItem.setHref(
					_renderResponse.createRenderURL(), "tabs1", "scheduled");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "scheduled"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}