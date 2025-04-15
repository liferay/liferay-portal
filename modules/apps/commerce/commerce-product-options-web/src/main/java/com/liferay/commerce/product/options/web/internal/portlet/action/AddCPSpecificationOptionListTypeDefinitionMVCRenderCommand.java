/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS,
		"mvc.command.name=/cp_specification_options/add_cp_specification_option_list_type_definition"
	},
	service = MVCRenderCommand.class
)
public class AddCPSpecificationOptionListTypeDefinitionMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);

		if (cmd.equals(Constants.ADD)) {
			return "/specification_option/create_list_type_definition.jsp";
		}

		return "/specification_option/add_existing_list_type_definition.jsp";
	}

}