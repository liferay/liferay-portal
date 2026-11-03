/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.exception.CommerceOrderBillingAddressException;
import com.liferay.commerce.exception.CommerceOrderShippingAddressException;
import com.liferay.commerce.exception.CommerceOrderShippingAndBillingException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;

import java.util.Objects;

/**
 * @author Luca Pellizzon
 */
public class AddressCommerceCheckoutStepDisplayContext {

	public AddressCommerceCheckoutStepDisplayContext(
		AccountEntryLocalService accountEntryLocalService,
		int commerceAddressType, CommerceOrderService commerceOrderService,
		CommerceAddressService commerceAddressService,
		CountryLocalService countryLocalService,
		ModelResourcePermission<CommerceOrder>
			commerceOrderModelResourcePermission) {

		_accountEntryLocalService = accountEntryLocalService;
		_commerceAddressType = commerceAddressType;
		_commerceOrderService = commerceOrderService;
		_commerceAddressService = commerceAddressService;
		_countryLocalService = countryLocalService;
		_commerceOrderModelResourcePermission =
			commerceOrderModelResourcePermission;
	}

	public CommerceOrder updateCommerceOrderAddress(
			ActionRequest actionRequest, String paramName)
		throws Exception {

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceOrder commerceOrder = _getCommerceOrder(
			actionRequest, commerceContext.getCommerceChannelGroupId());

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_commerceOrderModelResourcePermission.check(
			themeDisplay.getPermissionChecker(), commerceOrder,
			CommerceOrderActionKeys.CHECKOUT_COMMERCE_ORDER);

		CommerceAddress commerceAddress = _getCommerceAddress(
			commerceOrder, actionRequest, paramName);

		long commerceAddressId = commerceAddress.getCommerceAddressId();

		if (Objects.equals(
				CommerceCheckoutWebKeys.BILLING_ADDRESS_PARAM_NAME,
				paramName)) {

			return _updateCommerceOrderAddress(
				commerceOrder, commerceAddressId,
				commerceOrder.getShippingAddressId());
		}

		boolean useAsBilling = ParamUtil.getBoolean(
			actionRequest, "use-as-billing");

		if (useAsBilling) {
			Country country = commerceAddress.getCountry();

			if (!country.isBillingAllowed()) {
				throw new CommerceOrderShippingAndBillingException();
			}

			_commerceAddressType =
				CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING;

			_commerceAddressService.updateCommerceAddress(
				commerceAddress.getExternalReferenceCode(), commerceAddressId,
				commerceAddress.getCountryId(), commerceAddress.getRegionId(),
				commerceAddress.getCity(), commerceAddress.getDescription(),
				commerceAddress.getName(), commerceAddress.getPhoneNumber(),
				commerceAddress.getStreet1(), commerceAddress.getStreet2(),
				commerceAddress.getStreet3(),
				ParamUtil.getString(
					actionRequest, "subtype", commerceAddress.getSubtype()),
				_commerceAddressType, commerceAddress.getZip(), null);

			commerceOrder = _commerceOrderService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

			commerceOrder = _updateCommerceOrderAddress(
				commerceOrder, commerceAddressId, commerceAddressId);
		}
		else {
			if (Objects.equals(
					commerceOrder.getShippingAddressId(),
					commerceOrder.getBillingAddressId())) {

				commerceOrder = _updateCommerceOrderAddress(
					commerceOrder, 0, commerceAddressId);
			}
			else {
				commerceOrder = _updateCommerceOrderAddress(
					commerceOrder, commerceOrder.getBillingAddressId(),
					commerceAddressId);
			}
		}

		actionRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		return commerceOrder;
	}

	private CommerceAddress _addCommerceAddress(
			CommerceOrder commerceOrder, ActionRequest actionRequest)
		throws Exception {

		long countryId = ParamUtil.getLong(actionRequest, "countryId");

		boolean useAsBilling = ParamUtil.getBoolean(
			actionRequest, "use-as-billing");

		if (useAsBilling) {
			Country country = _countryLocalService.getCountry(countryId);

			if (!country.isBillingAllowed()) {
				throw new CommerceOrderShippingAndBillingException();
			}

			_commerceAddressType =
				CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING;
		}

		String name = ParamUtil.getString(actionRequest, "name");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceAddress.class.getName(), actionRequest);

		serviceContext.setScopeGroupId(commerceOrder.getGroupId());

		if (commerceOrder.isGuestOrder()) {
			String email = ParamUtil.getString(actionRequest, "email");

			AccountEntry accountEntry =
				_accountEntryLocalService.addAccountEntry(
					StringPool.BLANK, serviceContext.getUserId(),
					AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, name,
					null, null, email, null, null,
					AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
					WorkflowConstants.STATUS_APPROVED, serviceContext);

			commerceOrder.setCommerceAccountId(
				accountEntry.getAccountEntryId());

			commerceOrder = _commerceOrderService.updateCommerceOrder(
				commerceOrder);
		}

		return _commerceAddressService.addCommerceAddress(
			StringPool.BLANK, AccountEntry.class.getName(),
			commerceOrder.getCommerceAccountId(), countryId,
			ParamUtil.getLong(actionRequest, "regionId"),
			ParamUtil.getString(actionRequest, "city"),
			ParamUtil.getString(actionRequest, "description"), name,
			ParamUtil.getString(actionRequest, "phoneNumber"),
			ParamUtil.getString(actionRequest, "street1"),
			ParamUtil.getString(actionRequest, "street2"),
			ParamUtil.getString(actionRequest, "street3"),
			ParamUtil.getString(actionRequest, "subtype"), _commerceAddressType,
			ParamUtil.getString(actionRequest, "zip"), serviceContext);
	}

	private CommerceAddress _getCommerceAddress(
			CommerceOrder commerceOrder, ActionRequest actionRequest,
			String paramName)
		throws Exception {

		if (ParamUtil.getBoolean(actionRequest, "newAddress")) {
			return _addCommerceAddress(commerceOrder, actionRequest);
		}

		long commerceAddressId = ParamUtil.getLong(actionRequest, paramName);

		if (commerceAddressId < 1) {
			if (Objects.equals(
					CommerceCheckoutWebKeys.BILLING_ADDRESS_PARAM_NAME,
					paramName)) {

				throw new CommerceOrderBillingAddressException();
			}

			throw new CommerceOrderShippingAddressException();
		}

		return _commerceAddressService.getCommerceAddress(commerceAddressId);
	}

	private CommerceOrder _getCommerceOrder(
			ActionRequest actionRequest, long groupId)
		throws Exception {

		CommerceOrder commerceOrder = (CommerceOrder)actionRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (commerceOrder != null) {
			return commerceOrder;
		}

		commerceOrder = _commerceOrderService.getCommerceOrderByUuidAndGroupId(
			ParamUtil.getString(actionRequest, "commerceOrderUuid"), groupId);

		actionRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		return commerceOrder;
	}

	private CommerceOrder _updateCommerceOrderAddress(
			CommerceOrder commerceOrder, long billingAddressId,
			long shippingAddressId)
		throws Exception {

		return _commerceOrderService.updateCommerceOrder(
			commerceOrder.getExternalReferenceCode(),
			commerceOrder.getCommerceOrderId(), billingAddressId,
			commerceOrder.getCommerceShippingMethodId(), shippingAddressId,
			commerceOrder.getAdvanceStatus(),
			commerceOrder.getCommercePaymentMethodKey(), null,
			commerceOrder.getPurchaseOrderNumber(),
			commerceOrder.getShippingAmount(),
			commerceOrder.getShippingOptionName(), commerceOrder.getSubtotal(),
			commerceOrder.getTotal());
	}

	private final AccountEntryLocalService _accountEntryLocalService;
	private final CommerceAddressService _commerceAddressService;
	private int _commerceAddressType;
	private final ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;
	private final CommerceOrderService _commerceOrderService;
	private final CountryLocalService _countryLocalService;

}