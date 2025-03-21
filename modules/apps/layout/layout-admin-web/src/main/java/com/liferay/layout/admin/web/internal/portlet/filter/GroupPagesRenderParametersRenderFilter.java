/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.filter;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.RenderParametersPool;
import com.liferay.site.display.context.GroupDisplayContextHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.filter.FilterChain;
import jakarta.portlet.filter.FilterConfig;
import jakarta.portlet.filter.PortletFilter;
import jakarta.portlet.filter.RenderFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Attila Bakay
 */
@Component(
	property = "jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
	service = PortletFilter.class
)
public class GroupPagesRenderParametersRenderFilter implements RenderFilter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			RenderRequest renderRequest, RenderResponse renderResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		long selPlid = ParamUtil.getLong(
			renderRequest, "selPlid", LayoutConstants.DEFAULT_PLID);

		if (selPlid == LayoutConstants.DEFAULT_PLID) {
			filterChain.doFilter(renderRequest, renderResponse);

			return;
		}

		GroupDisplayContextHelper groupDisplayContextHelper =
			new GroupDisplayContextHelper(
				_portal.getHttpServletRequest(renderRequest));

		Group selGroup = groupDisplayContextHelper.getSelGroup();

		Layout selLayout = _layoutLocalService.fetchLayout(selPlid);

		try {
			if ((selLayout == null) ||
				!_layoutLocalService.hasLayout(
					selLayout.getUuid(), selGroup.getGroupId(),
					selLayout.isPrivateLayout())) {

				_clearRenderRequestParameters(
					_portal.getHttpServletRequest(renderRequest),
					renderRequest);

				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				httpServletResponse.sendRedirect(
					PortletURLBuilder.create(
						_portal.getControlPanelPortletURL(
							renderRequest, LayoutAdminPortletKeys.GROUP_PAGES,
							PortletRequest.RENDER_PHASE)
					).setParameter(
						"p_v_l_s_g_id", selGroup.getGroupId()
					).buildString());

				return;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		filterChain.doFilter(renderRequest, renderResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	private void _clearRenderRequestParameters(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		RenderParametersPool.clear(
			httpServletRequest, themeDisplay.getPlid(),
			WebKeys.PUBLIC_RENDER_PARAMETERS);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupPagesRenderParametersRenderFilter.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}