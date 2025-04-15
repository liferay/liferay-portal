/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.full.page.application.internal.layout.type.controller;

import com.liferay.layout.type.controller.BaseLayoutTypeControllerImpl;
import com.liferay.layout.type.controller.full.page.application.internal.constants.FullPageApplicationLayoutTypeControllerConstants;
import com.liferay.layout.type.controller.full.page.application.internal.constants.FullPageApplicationLayoutTypeControllerWebKeys;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juergen Kappler
 */
@Component(
	property = "layout.type=" + FullPageApplicationLayoutTypeControllerConstants.LAYOUT_TYPE_FULL_PAGE_APPLICATION,
	service = LayoutTypeController.class
)
public class FullPageApplicationLayoutTypeController
	extends BaseLayoutTypeControllerImpl {

	@Override
	public String getType() {
		return LayoutConstants.TYPE_PORTLET;
	}

	@Override
	public String getURL() {
		return _URL;
	}

	@Override
	public String includeEditContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout)
		throws Exception {

		httpServletRequest.setAttribute(WebKeys.SEL_LAYOUT, layout);

		return super.includeEditContent(
			httpServletRequest, httpServletResponse, layout);
	}

	@Override
	public boolean isBrowsable() {
		return true;
	}

	@Override
	public boolean isFirstPageable() {
		return true;
	}

	@Override
	public boolean isFullPageDisplayable() {
		return true;
	}

	@Override
	public boolean isParentable() {
		return true;
	}

	@Override
	public boolean isSitemapable() {
		return false;
	}

	@Override
	public boolean isURLFriendliable() {
		return true;
	}

	@Override
	protected void addAttributes(HttpServletRequest httpServletRequest) {
		super.addAttributes(httpServletRequest);

		List<Portlet> portlets = _portletLocalService.getPortlets();

		if (portlets.isEmpty()) {
			return;
		}

		portlets = ListUtil.filter(portlets, Portlet::isFullPageDisplayable);

		httpServletRequest.setAttribute(
			FullPageApplicationLayoutTypeControllerWebKeys.
				FULL_PAGE_APPLICATION_PORTLETS,
			portlets);
	}

	@Override
	protected ServletResponse createServletResponse(
		HttpServletResponse httpServletResponse,
		UnsyncStringWriter unsyncStringWriter) {

		return new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);
	}

	@Override
	protected String getEditPage() {
		return _EDIT_PAGE;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	protected String getViewPage() {
		return _VIEW_PAGE;
	}

	@Override
	protected void removeAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.removeAttribute(WebKeys.SEL_LAYOUT);

		super.removeAttributes(httpServletRequest);
	}

	private static final String _EDIT_PAGE =
		"/layout/edit/full_page_application.jsp";

	private static final String _URL =
		"${liferay:mainPath}/portal/layout?p_l_id=${liferay:plid}" +
			"&p_v_l_s_g_id=${liferay:pvlsgid}";

	private static final String _VIEW_PAGE =
		"/layout/view/full_page_application.jsp";

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.full.page.application)"
	)
	private ServletContext _servletContext;

}