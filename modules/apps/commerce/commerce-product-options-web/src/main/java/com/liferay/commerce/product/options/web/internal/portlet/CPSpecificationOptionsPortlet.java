/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.portlet;

import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.options.web.internal.display.context.CPSpecificationOptionDisplayContext;
import com.liferay.commerce.product.options.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-commerce-product-specification-options",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Product Specifications",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/view_cp_specification_options.jsp",
		"jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CPSpecificationOptionsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			CPSpecificationOptionDisplayContext
				cpSpecificationOptionDisplayContext =
					new CPSpecificationOptionDisplayContext(
						_actionHelper,
						_portal.getHttpServletRequest(renderRequest),
						_cpOptionCategoryService, _cpSpecificationOptionService,
						_portletResourcePermission);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpSpecificationOptionDisplayContext);
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CPConstants.RESOURCE_NAME_PRODUCT + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}