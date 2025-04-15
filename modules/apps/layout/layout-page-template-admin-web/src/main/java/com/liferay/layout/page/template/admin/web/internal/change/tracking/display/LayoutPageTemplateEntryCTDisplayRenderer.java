/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.change.tracking.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutPageTemplateEntryCTDisplayRenderer
	extends BaseCTDisplayRenderer<LayoutPageTemplateEntry> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_page_template_admin/edit_layout_page_template_entry"
		).setTabs1(
			() -> {
				if ((layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.BASIC) ||
					(layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

					return "page-templates";
				}

				if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

					return "display-page-templates";
				}

				if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

					return "master-layouts";
				}

				return null;
			}
		).setParameter(
			"layoutPageTemplateEntryId",
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).buildString();
	}

	@Override
	public Class<LayoutPageTemplateEntry> getModelClass() {
		return LayoutPageTemplateEntry.class;
	}

	@Override
	public String getTitle(
			Locale locale, LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		return layoutPageTemplateEntry.getName();
	}

	@Reference
	private Portal _portal;

}