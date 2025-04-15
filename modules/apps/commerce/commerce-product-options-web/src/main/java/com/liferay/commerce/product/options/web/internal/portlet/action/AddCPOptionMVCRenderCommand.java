/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.options.web.internal.display.context.CPOptionDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_OPTIONS,
		"mvc.command.name=/cp_options/add_cp_option"
	},
	service = MVCRenderCommand.class
)
public class AddCPOptionMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CPOptionDisplayContext cpOptionDisplayContext =
				new CPOptionDisplayContext(
					_commerceOptionTypeRegistry, _configurationProvider, null,
					_portal.getHttpServletRequest(renderRequest));

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, cpOptionDisplayContext);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return "/option/add_cp_option.jsp";
	}

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

}