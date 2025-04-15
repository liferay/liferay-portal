/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionRelException;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionRelService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.commerce.shipping.engine.fixed.web.internal.display.context.CommerceShippingFixedOptionRelsDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionService;
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
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPPING_METHODS,
		"mvc.command.name=/commerce_shipping_methods/edit_commerce_shipping_fixed_option_rel"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceShippingFixedOptionRelMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/edit_commerce_shipping_fixed_option_rel.jsp");

			CommerceShippingFixedOptionRelsDisplayContext
				commerceShippingFixedOptionRelsDisplayContext =
					new CommerceShippingFixedOptionRelsDisplayContext(
						_commerceChannelLocalService,
						_commerceCurrencyLocalService,
						_commerceInventoryWarehouseService,
						_commerceShippingFixedOptionRelService,
						_commerceShippingFixedOptionService,
						_commerceShippingMethodService, _countryService,
						_cpMeasurementUnitLocalService, _portal, _regionService,
						renderRequest, renderResponse);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceShippingFixedOptionRelsDisplayContext);

			requestDispatcher.include(
				_portal.getHttpServletRequest(renderRequest),
				_portal.getHttpServletResponse(renderResponse));
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchShippingFixedOptionRelException ||
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
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

	@Reference
	private CommerceShippingFixedOptionRelService
		_commerceShippingFixedOptionRelService;

	@Reference
	private CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private CountryService _countryService;

	@Reference
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RegionService _regionService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.shipping.engine.fixed.web)"
	)
	private ServletContext _servletContext;

}