/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.tax.category.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_TAX_CATEGORY,
		"mvc.command.name=/cp_tax_category/add_cp_tax_category"
	},
	service = MVCRenderCommand.class
)
public class AddCPTaxCategoryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		return "/cp_tax_category/add_cp_tax_category.jsp";
	}

}