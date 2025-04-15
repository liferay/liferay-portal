/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.definitions.web.internal.display.context.CPInstanceDisplayContext;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
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
		"mvc.command.name=/cp_definitions/edit_cp_instance"
	},
	service = MVCRenderCommand.class
)
public class EditCPInstanceMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CPInstanceDisplayContext cpInstanceDisplayContext =
				new CPInstanceDisplayContext(
					_actionHelper, _portal.getHttpServletRequest(renderRequest),
					_commerceCurrencyLocalService, _commerceOptionTypeRegistry,
					_commercePriceEntryService,
					_commerceProductPriceCalculation,
					_cpDefinitionOptionRelService, _cpInstanceHelper,
					_cpInstanceUnitOfMeasureService,
					_cpMeasurementUnitLocalService);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, cpInstanceDisplayContext);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPInstanceException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/edit_cp_instance.jsp";
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceUnitOfMeasureService _cpInstanceUnitOfMeasureService;

	@Reference
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@Reference
	private Portal _portal;

}