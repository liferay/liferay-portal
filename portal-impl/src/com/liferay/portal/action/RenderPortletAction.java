/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class RenderPortletAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setAjax(true);

		String ajaxId = httpServletRequest.getParameter("ajax_id");

		User user = PortalUtil.getUser(httpServletRequest);
		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			PortalUtil.getCompanyId(httpServletRequest), portletId);

		String columnId = ParamUtil.getString(httpServletRequest, "p_p_col_id");
		int columnPos = ParamUtil.getInteger(httpServletRequest, "p_p_col_pos");
		int columnCount = ParamUtil.getInteger(
			httpServletRequest, "p_p_col_count");

		Boolean boundary = null;

		String boundaryParam = ParamUtil.getString(
			httpServletRequest, "p_p_boundary", null);

		if (boundaryParam != null) {
			boundary = GetterUtil.getBoolean(boundaryParam);
		}

		Boolean decorate = null;

		String decorateParam = ParamUtil.getString(
			httpServletRequest, "p_p_decorate", null);

		if (decorateParam != null) {
			decorate = GetterUtil.getBoolean(decorateParam);
		}

		boolean staticPortlet = ParamUtil.getBoolean(
			httpServletRequest, "p_p_static");

		if (staticPortlet) {
			portlet.setStatic(true);

			boolean staticStartPortlet = ParamUtil.getBoolean(
				httpServletRequest, "p_p_static_start");

			portlet.setStaticStart(staticStartPortlet);
		}

		if (ajaxId != null) {
			httpServletResponse.setHeader("Ajax-ID", ajaxId);
		}

		PortalUtil.updateWindowState(
			portletId, user, layout,
			WindowStateFactory.getWindowState(
				ParamUtil.getString(httpServletRequest, "p_p_state")),
			httpServletRequest);

		httpServletRequest = PortletContainerUtil.setupOptionalRenderParameters(
			httpServletRequest, null, columnId, columnPos, columnCount,
			boundary, decorate);

		PortletContainerUtil.processPublicRenderParameters(
			httpServletRequest, themeDisplay.getLayout());

		PortletContainerUtil.render(
			httpServletRequest, httpServletResponse, portlet);

		return null;
	}

}