/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.admin.web.internal.util.PageTemplateSetsUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

/**
 * @author Georgel Pop
 */
public class SelectLayoutPageTemplateEntryMasterLayoutDisplayContext {

	public SelectLayoutPageTemplateEntryMasterLayoutDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getPageTemplateSetsProps() {
		long layoutPageTemplateCollectionId = ParamUtil.getLong(
			_httpServletRequest, "layoutPageTemplateCollectionId");

		if ((layoutPageTemplateCollectionId > 0) ||
			!DesignLibraryUtil.isDesignLibraryScope(
				_themeDisplay.getScopeGroup())) {

			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"addPageTemplateSetURL",
			() -> {
				LiferayPortletURL addLayoutPageTemplateCollectionPortletURL =
					(LiferayPortletURL)ResourceURLBuilder.createResourceURL(
						_renderResponse
					).setResourceID(
						"/layout_page_template_admin" +
							"/add_layout_page_template_collection"
					).buildResourceURL();

				addLayoutPageTemplateCollectionPortletURL.
					setCopyCurrentRenderParameters(false);

				return addLayoutPageTemplateCollectionPortletURL.toString();
			}
		).put(
			"pageTemplateSets",
			PageTemplateSetsUtil.getPageTemplateSetsJSONArray(
				_themeDisplay.getScopeGroupId(),
				LayoutPageTemplateCollectionLocalServiceUtil.getService())
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}