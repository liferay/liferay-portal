/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.change.tracking.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutPrototype;
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
public class LayoutPrototypeCTDisplayRenderer
	extends BaseCTDisplayRenderer<LayoutPrototype> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			LayoutPrototype layoutPrototype)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_layout_prototype.jsp"
		).setParameter(
			"layoutPrototypeId", layoutPrototype.getLayoutPrototypeId()
		).buildString();
	}

	@Override
	public Class<LayoutPrototype> getModelClass() {
		return LayoutPrototype.class;
	}

	@Override
	public String getTitle(Locale locale, LayoutPrototype layoutPrototype)
		throws PortalException {

		return layoutPrototype.getName(locale);
	}

	@Reference
	private Portal _portal;

}