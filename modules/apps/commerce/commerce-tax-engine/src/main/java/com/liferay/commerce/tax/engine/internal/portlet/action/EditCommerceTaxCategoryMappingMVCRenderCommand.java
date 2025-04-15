/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.engine.internal.display.context.CommerceTaxCategoryMappingsDisplayContext;
import com.liferay.commerce.tax.exception.NoSuchTaxCategoryMappingException;
import com.liferay.commerce.tax.service.CommerceTaxCategoryMappingService;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=/commerce_tax_methods/edit_commerce_tax_category_mapping"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceTaxCategoryMappingMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/edit_commerce_tax_category_mapping.jsp");

			CommerceTaxCategoryMappingsDisplayContext
				commerceTaxCategoryMappingsDisplayContext =
					new CommerceTaxCategoryMappingsDisplayContext(
						_commerceChannelLocalService,
						_commerceChannelModelResourcePermission,
						_commerceTaxCategoryMappingService,
						_commerceTaxMethodService, _cpTaxCategoryService,
						renderRequest);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceTaxCategoryMappingsDisplayContext);

			requestDispatcher.include(
				_portal.getHttpServletRequest(renderRequest),
				_portal.getHttpServletResponse(renderResponse));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchTaxCategoryMappingException ||
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

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommerceTaxCategoryMappingService
		_commerceTaxCategoryMappingService;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private CPTaxCategoryService _cpTaxCategoryService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.tax.engine)"
	)
	private ServletContext _servletContext;

}