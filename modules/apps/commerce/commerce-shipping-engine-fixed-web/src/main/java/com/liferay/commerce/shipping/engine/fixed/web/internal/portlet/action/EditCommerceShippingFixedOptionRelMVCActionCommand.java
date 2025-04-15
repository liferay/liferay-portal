/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionRelPriceException;
import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionRelException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionRelService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

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
	service = MVCActionCommand.class
)
public class EditCommerceShippingFixedOptionRelMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceShippingFixedOptionRel(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceShippingFixedOptionRels(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceShippingFixedOptionRelPriceException ||
				exception instanceof NoSuchShippingFixedOptionRelException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceShippingFixedOptionRels(
			ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceShippingFixedOptionRelIds = null;

		long commerceShippingFixedOptionRelId = ParamUtil.getLong(
			actionRequest, "commerceShippingFixedOptionRelId");

		if (commerceShippingFixedOptionRelId > 0) {
			deleteCommerceShippingFixedOptionRelIds = new long[] {
				commerceShippingFixedOptionRelId
			};
		}
		else {
			deleteCommerceShippingFixedOptionRelIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceShippingFixedOptionRelIds"),
				0L);
		}

		for (long deleteCommerceShippingFixedOptionRelId :
				deleteCommerceShippingFixedOptionRelIds) {

			_commerceShippingFixedOptionRelService.
				deleteCommerceShippingFixedOptionRel(
					deleteCommerceShippingFixedOptionRelId);
		}
	}

	private void _updateCommerceShippingFixedOptionRel(
			ActionRequest actionRequest)
		throws Exception {

		long commerceShippingFixedOptionRelId = ParamUtil.getLong(
			actionRequest, "commerceShippingFixedOptionRelId");

		long commerceInventoryWarehouseId = ParamUtil.getLong(
			actionRequest, "commerceInventoryWarehouseId");
		long countryId = ParamUtil.getLong(actionRequest, "countryId");
		long regionId = ParamUtil.getLong(actionRequest, "regionId");
		String zip = ParamUtil.getString(actionRequest, "zip");
		double weightFrom = ParamUtil.getDouble(actionRequest, "weightFrom");
		double weightTo = ParamUtil.getDouble(actionRequest, "weightTo");
		BigDecimal fixedPrice = _commercePriceFormatter.parse(
			actionRequest, false,
			CommerceShippingFixedOptionRel.class.getName(), "fixedPrice");
		BigDecimal rateUnitWeightPrice = _commercePriceFormatter.parse(
			actionRequest, false,
			CommerceShippingFixedOptionRel.class.getName(),
			"rateUnitWeightPrice");
		double ratePercentage = ParamUtil.getDouble(
			actionRequest, "ratePercentage");

		if (commerceShippingFixedOptionRelId > 0) {
			_commerceShippingFixedOptionRelService.
				updateCommerceShippingFixedOptionRel(
					commerceShippingFixedOptionRelId,
					commerceInventoryWarehouseId, countryId, regionId, zip,
					weightFrom, weightTo, fixedPrice, rateUnitWeightPrice,
					ratePercentage);
		}
		else {
			long commerceShippingMethodId = ParamUtil.getLong(
				actionRequest, "commerceShippingMethodId");

			CommerceShippingMethod commerceShippingMethod =
				_commerceShippingMethodService.getCommerceShippingMethod(
					commerceShippingMethodId);

			_commerceShippingFixedOptionRelService.
				addCommerceShippingFixedOptionRel(
					commerceShippingMethod.getGroupId(),
					commerceShippingMethod.getCommerceShippingMethodId(),
					ParamUtil.getLong(
						actionRequest, "commerceShippingFixedOptionId"),
					commerceInventoryWarehouseId, countryId, regionId, zip,
					weightFrom, weightTo, fixedPrice, rateUnitWeightPrice,
					ratePercentage);
		}
	}

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceShippingFixedOptionRelService
		_commerceShippingFixedOptionRelService;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

}