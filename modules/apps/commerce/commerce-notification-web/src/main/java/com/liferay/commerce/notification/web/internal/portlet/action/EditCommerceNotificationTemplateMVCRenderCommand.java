/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.portlet.action;

import com.liferay.commerce.notification.exception.NoSuchNotificationTemplateException;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateService;
import com.liferay.commerce.notification.type.CommerceNotificationTypeRegistry;
import com.liferay.commerce.notification.web.internal.display.context.CommerceNotificationTemplatesDisplayContext;
import com.liferay.commerce.order.CommerceDefinitionTermContributorRegistry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CHANNELS,
		"mvc.command.name=/commerce_channels/edit_commerce_notification_template"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceNotificationTemplateMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/edit_commerce_notification_template.jsp");

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			CommerceNotificationTemplatesDisplayContext
				commerceNotificationTemplatesDisplayContext =
					new CommerceNotificationTemplatesDisplayContext(
						_commerceChannelLocalService,
						_commerceDefinitionTermContributorRegistry,
						_commerceNotificationTemplateService,
						_commerceNotificationTypeRegistry, httpServletRequest);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceNotificationTemplatesDisplayContext);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNotificationTemplateException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceDefinitionTermContributorRegistry
		_commerceDefinitionTermContributorRegistry;

	@Reference
	private CommerceNotificationTemplateService
		_commerceNotificationTemplateService;

	@Reference
	private CommerceNotificationTypeRegistry _commerceNotificationTypeRegistry;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.notification.web)"
	)
	private ServletContext _servletContext;

}