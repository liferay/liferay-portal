/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionOptionRelDisplayContext;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionRelException;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_option_rel"
	},
	service = MVCRenderCommand.class
)
public class EditCPDefinitionOptionRelMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CPDefinitionOptionRelDisplayContext
				cpDefinitionOptionRelDisplayContext =
					new CPDefinitionOptionRelDisplayContext(
						_actionHelper,
						_portal.getHttpServletRequest(renderRequest),
						_commerceOptionTypeRegistry, _configurationProvider,
						_infoItemServiceRegistry, _itemSelector);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpDefinitionOptionRelDisplayContext);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPDefinitionOptionRelException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/edit_cp_definition_option_rel.jsp";
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}