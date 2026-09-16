/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Georgel Pop
 */
public abstract class BaseLayoutPageTemplateCollectionMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
				portal.getHttpServletRequest(renderRequest),
				themeDisplay.getScopeGroupId());

		if (layoutPageTemplateCollection != null) {
			renderRequest.setAttribute(
				LayoutPageTemplateAdminWebKeys.
					LAYOUT_PAGE_TEMPLATE_COLLECTION_ID,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());
		}

		setAttributes(renderRequest, renderResponse);

		return getPath();
	}

	protected abstract String getPath();

	protected void setAttributes(
		RenderRequest renderRequest, RenderResponse renderResponse) {
	}

	@Reference
	protected Portal portal;

}