/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.definitions.web.internal.display.context.CPDefinitionSpecificationOptionValueDisplayContext;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionSpecificationOptionValueException;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.list.type.service.ListTypeEntryService;
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
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_specification_option_value"
	},
	service = MVCRenderCommand.class
)
public class EditCPDefinitionSpecificationOptionValueMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CPDefinitionSpecificationOptionValueDisplayContext
				cpDefinitionSpecificationOptionValueDisplayContext =
					new CPDefinitionSpecificationOptionValueDisplayContext(
						_actionHelper,
						_portal.getHttpServletRequest(renderRequest),
						_cpOptionCategoryService, _itemSelector,
						_listTypeEntryService);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpDefinitionSpecificationOptionValueDisplayContext);
		}
		catch (Exception exception) {
			if (exception instanceof
					NoSuchCPDefinitionSpecificationOptionValueException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/edit_cp_definition_specification_option_value.jsp";
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private ListTypeEntryService _listTypeEntryService;

	@Reference
	private Portal _portal;

}