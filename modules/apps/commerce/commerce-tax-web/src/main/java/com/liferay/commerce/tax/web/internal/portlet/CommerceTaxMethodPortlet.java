/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.web.internal.portlet;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.commerce.tax.web.internal.display.context.CommerceTaxMethodsDisplayContext;
import com.liferay.commerce.util.CommerceTaxEngineRegistry;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=false",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Tax Methods",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/edit_commerce_tax_method.jsp",
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommerceTaxMethodPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CommerceTaxMethodsDisplayContext commerceTaxMethodsDisplayContext =
			new CommerceTaxMethodsDisplayContext(
				_commerceChannelLocalService,
				_commerceChannelModelResourcePermission,
				_commerceTaxEngineRegistry, _commerceTaxMethodService,
				renderRequest, renderResponse);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, commerceTaxMethodsDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommerceTaxEngineRegistry _commerceTaxEngineRegistry;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

}