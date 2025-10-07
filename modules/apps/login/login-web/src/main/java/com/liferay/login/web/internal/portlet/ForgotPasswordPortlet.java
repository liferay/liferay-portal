/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.login.web.internal.portlet.util.LoginUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Istvan Sajtos
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-login",
		"com.liferay.portlet.display-category=category.tools",
		"com.liferay.portlet.icon=/icons/login.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.single-page-application=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Forgot Password",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.add-process-action-success-action=false",
		"jakarta.portlet.init-param.config-template=/forgot_password/configuration.jsp",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/forgot_password.jsp",
		"jakarta.portlet.name=" + LoginPortletKeys.FORGOT_PASSWORD,
		"jakarta.portlet.portlet-mode=text/html;config",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ForgotPasswordPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (!LoginUtil.isAllowedToRenderView(
				"/forgot_password.jsp", "/login/forgot_password",
				renderRequest)) {

			renderRequest.setAttribute(
				getMVCPathAttributeName(renderResponse.getNamespace()),
				"/login.jsp");
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(renderRequest));

		String currentURL = URLCodec.decodeURL(
			(String)httpServletRequest.getAttribute("CURRENT_URL"));

		if (currentURL.contains("/login/update_password")) {
			Ticket ticket = _ticketLocalService.fetchTicket(
				ParamUtil.getLong(httpServletRequest, "ticketId"));

			if (ticket != null) {
				httpServletRequest.setAttribute(WebKeys.TICKET, ticket);
			}
		}

		if (currentURL.contains("/login/update_password") ||
			currentURL.contains("/portal/update_password")) {

			renderRequest.setAttribute(
				getMVCPathAttributeName(renderResponse.getNamespace()),
				"/update_password.jsp");
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.login.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private TicketLocalService _ticketLocalService;

}