/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.engine.fixed.exception.NoSuchTaxFixedRateException;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateService;
import com.liferay.commerce.tax.engine.fixed.web.internal.display.context.CommerceTaxFixedRatesDisplayContext;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=/commerce_tax_methods/edit_commerce_tax_fixed_rate"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceTaxFixedRateMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/edit_commerce_tax_fixed_rate.jsp");

			CommerceTaxFixedRatesDisplayContext
				commerceTaxFixedRatesDisplayContext =
					new CommerceTaxFixedRatesDisplayContext(
						_commerceChannelLocalService,
						_commerceChannelModelResourcePermission,
						_commerceCurrencyLocalService,
						_commerceTaxFixedRateService, _commerceTaxMethodService,
						_cpTaxCategoryService, _percentageFormatter,
						renderRequest);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceTaxFixedRatesDisplayContext);

			requestDispatcher.include(
				_portal.getHttpServletRequest(renderRequest),
				_portal.getHttpServletResponse(renderResponse));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchTaxFixedRateException ||
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
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceTaxFixedRateService _commerceTaxFixedRateService;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private CPTaxCategoryService _cpTaxCategoryService;

	@Reference
	private PercentageFormatter _percentageFormatter;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.tax.engine.fixed.web)"
	)
	private ServletContext _servletContext;

}