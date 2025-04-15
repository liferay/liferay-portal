/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.portlet.internal.layout.type.controller;

import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.type.controller.BaseLayoutTypeControllerImpl;
import com.liferay.layout.type.controller.portlet.internal.constants.PortletLayoutTypeControllerWebKeys;
import com.liferay.layout.type.controller.portlet.internal.display.context.PortletLayoutDisplayContext;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.TransferHeadersHelperUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.DynamicServletRequestUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "layout.type=" + LayoutConstants.TYPE_PORTLET,
	service = LayoutTypeController.class
)
public class PortletLayoutTypeController extends BaseLayoutTypeControllerImpl {

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

		RequestDispatcher requestDispatcher =
			TransferHeadersHelperUtil.getTransferHeadersRequestDispatcher(
				DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
					_servletContext, getEditPage()));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		requestDispatcher.include(httpServletRequest, pipingServletResponse);

		return unsyncStringWriter.toString();
	}

	@Override
	public boolean includeLayoutContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout)
		throws Exception {

		RequestDispatcher requestDispatcher =
			TransferHeadersHelperUtil.getTransferHeadersRequestDispatcher(
				DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
					_servletContext, getViewPage()));

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		if (Validator.isNotNull(portletId)) {
			Portlet portlet = _portletLocalService.getPortletById(portletId);

			if (portlet != null) {
				originalHttpServletRequest =
					DynamicServletRequestUtil.createDynamicServletRequest(
						originalHttpServletRequest, portlet,
						httpServletRequest.getParameterMap(), false);
			}
		}

		httpServletRequest.setAttribute(
			PortletLayoutTypeControllerWebKeys.ORIGINAL_HTTP_SERVLET_REQUEST,
			originalHttpServletRequest);
		httpServletRequest.setAttribute(
			PortletLayoutTypeControllerWebKeys.PORTLET_LAYOUT_DISPLAY_CONTEXT,
			new PortletLayoutDisplayContext(
				_layoutPageTemplateEntryLocalService,
				_layoutPageTemplateStructureLocalService));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		String contentType = pipingServletResponse.getContentType();

		requestDispatcher.include(httpServletRequest, pipingServletResponse);

		if (contentType != null) {
			httpServletResponse.setContentType(contentType);
		}

		httpServletRequest.setAttribute(
			WebKeys.LAYOUT_CONTENT, unsyncStringWriter.getStringBundler());

		return false;
	}

	@Override
	public boolean isFirstPageable() {
		return true;
	}

	@Override
	public boolean isFullPageDisplayable() {
		return false;
	}

	@Override
	public boolean isParentable() {
		return true;
	}

	@Override
	public boolean isSitemapable() {
		return true;
	}

	@Override
	public boolean isURLFriendliable() {
		return true;
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

	private static final String _EDIT_PAGE = "/layout/edit/portlet.jsp";

	private static final String _URL =
		"${liferay:mainPath}/portal/layout?p_l_id=${liferay:plid}&" +
			"p_v_l_s_g_id=${liferay:pvlsgid}";

	private static final String _VIEW_PAGE = "/layout/view/portlet.jsp";

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.portlet)"
	)
	private ServletContext _servletContext;

}