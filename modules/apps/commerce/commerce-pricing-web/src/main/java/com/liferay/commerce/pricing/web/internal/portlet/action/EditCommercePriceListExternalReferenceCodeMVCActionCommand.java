/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.price.list.exception.DuplicateCommercePriceListExternalReferenceCodeException;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICE_LIST,
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PROMOTION,
		"mvc.command.name=/commerce_price_list/edit_commerce_price_list_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommercePriceListExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommercePriceListExternalReferenceCode(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommercePriceListExternalReferenceCodeException ||
				exception instanceof NoSuchPriceListException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				_log.error(exception);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	private void _updateCommercePriceListExternalReferenceCode(
			ActionRequest actionRequest)
		throws Exception {

		long commercePriceListId = ParamUtil.getLong(
			actionRequest, "commercePriceListId");

		CommercePriceList commercePriceList =
			_commercePriceListService.getCommercePriceList(commercePriceListId);

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		_commercePriceListService.updateExternalReferenceCode(
			commercePriceList, externalReferenceCode,
			_portal.getCompanyId(actionRequest));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommercePriceListExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private Portal _portal;

}