/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.locked.items.renderer;

import com.liferay.layout.locked.layouts.web.internal.display.context.LockedLayoutsDisplayContext;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.locked.items.renderer.BaseJSPLockedItemsRenderer;
import com.liferay.locked.items.renderer.LockedItemsRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(service = LockedItemsRenderer.class)
public class LayoutLockedItemsRenderer extends BaseJSPLockedItemsRenderer {

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale,
			"administrators-can-manually-unlock-pages-that-are-being-used-by-" +
				"other-users");
	}

	@Override
	public String getKey() {
		return "layouts";
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(locale, "pages");
	}

	@Override
	protected String getJspPath() {
		return "/locked-items/view.jsp";
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	protected void setAttributes(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		httpServletRequest.setAttribute(
			LockedLayoutsDisplayContext.class.getName(),
			new LockedLayoutsDisplayContext(
				_language, _layoutLocalService, _layoutLockManager,
				_portal.getLiferayPortletRequest(portletRequest),
				_portal.getLiferayPortletResponse(portletResponse), _portal));
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutLockManager _layoutLockManager;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.locked.layouts.web)"
	)
	private ServletContext _servletContext;

}