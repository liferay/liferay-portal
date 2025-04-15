/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.web.internal.portlet.action;

import com.liferay.commerce.exception.NoSuchAddressRestrictionException;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.util.comparator.CommerceShippingMethodPriorityComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CHANNELS,
		"mvc.command.name=/commerce_channels/edit_commerce_shipping_method_address_restriction"
	},
	service = MVCActionCommand.class
)
public class EditCommerceShippingMethodAddressRestrictionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			Callable<Object> commerceAddressRestrictionsCallable =
				new CommerceAddressRestrictionsCallable(actionRequest);

			TransactionInvokerUtil.invoke(
				_transactionConfig, commerceAddressRestrictionsCallable);
		}
		catch (Throwable throwable) {
			if (throwable instanceof NoSuchAddressRestrictionException ||
				throwable instanceof PrincipalException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, throwable.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				_log.error(throwable, throwable);
			}
		}
	}

	private void _updateCommerceAddressRestrictions(ActionRequest actionRequest)
		throws Exception {

		long commerceChannelId = ParamUtil.getLong(
			actionRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		List<CommerceShippingMethod> commerceShippingMethods =
			_commerceShippingMethodService.getCommerceShippingMethods(
				commerceChannel.getGroupId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				CommerceShippingMethodPriorityComparator.getInstance(false));

		for (CommerceShippingMethod commerceShippingMethod :
				commerceShippingMethods) {

			_commerceShippingMethodService.deleteCommerceAddressRestrictions(
				commerceShippingMethod.getCommerceShippingMethodId());
		}

		List<Country> commerceCountries = _countryService.getCompanyCountries(
			_portal.getCompanyId(actionRequest), true);

		for (Country country : commerceCountries) {
			long[] commercePaymentMethodGroupRelIds = ParamUtil.getLongValues(
				actionRequest, String.valueOf(country.getCountryId()));

			if (ArrayUtil.isEmpty(commercePaymentMethodGroupRelIds)) {
				continue;
			}

			for (long commercePaymentMethodGroupRelId :
					commercePaymentMethodGroupRelIds) {

				if (commercePaymentMethodGroupRelId <= 0) {
					continue;
				}

				_commerceShippingMethodService.addCommerceAddressRestriction(
					commerceChannel.getGroupId(),
					commercePaymentMethodGroupRelId, country.getCountryId());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceShippingMethodAddressRestrictionMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private CountryService _countryService;

	@Reference
	private Portal _portal;

	private class CommerceAddressRestrictionsCallable
		implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			_updateCommerceAddressRestrictions(_actionRequest);

			return null;
		}

		private CommerceAddressRestrictionsCallable(
			ActionRequest actionRequest) {

			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}