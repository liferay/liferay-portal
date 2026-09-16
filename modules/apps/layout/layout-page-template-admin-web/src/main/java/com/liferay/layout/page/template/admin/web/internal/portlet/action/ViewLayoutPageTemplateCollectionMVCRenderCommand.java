/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Georgel Pop
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/view_layout_page_template_collection"
	},
	service = MVCRenderCommand.class
)
public class ViewLayoutPageTemplateCollectionMVCRenderCommand
	extends BaseLayoutPageTemplateCollectionMVCRenderCommand {

	@Override
	protected String getPath() {
		return "/view.jsp";
	}

	@Override
	protected void setAttributes(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		renderRequest.setAttribute(
			LayoutPageTemplateAdminWebKeys.SHOW_COLLECTIONS_PANEL,
			!DesignLibraryUtil.isDesignLibraryScope(
				themeDisplay.getScopeGroup()));
	}

}