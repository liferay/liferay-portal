/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.definitions.web.internal.display.context.CPInstanceUnitOfMeasureDisplayContext;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureService;
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
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/add_cp_instance_unit_of_measure"
	},
	service = MVCRenderCommand.class
)
public class AddCPInstanceUnitOfMeasureMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CPInstanceUnitOfMeasureDisplayContext cpDefinitionsDisplayContext =
				new CPInstanceUnitOfMeasureDisplayContext(
					_actionHelper, _portal.getHttpServletRequest(renderRequest),
					_commerceCurrencyLocalService,
					_commerceProductPriceCalculation,
					_cpInstanceUnitOfMeasureService);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, cpDefinitionsDisplayContext);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchCPInstanceUnitOfMeasureException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/unit_of_measure/add_cp_instance_unit_of_measure.jsp";
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CPInstanceUnitOfMeasureService _cpInstanceUnitOfMeasureService;

	@Reference
	private Portal _portal;

}