/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.change.tracking.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutPageTemplateCollectionCTDisplayRenderer
	extends BaseCTDisplayRenderer<LayoutPageTemplateCollection> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_page_template_admin/edit_layout_page_template_collection"
		).setParameter(
			"layoutPageTemplateCollectionId",
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).buildString();
	}

	@Override
	public Class<LayoutPageTemplateCollection> getModelClass() {
		return LayoutPageTemplateCollection.class;
	}

	@Override
	public String getTitle(
			Locale locale,
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws PortalException {

		return layoutPageTemplateCollection.getName();
	}

	@Reference
	private Portal _portal;

}