/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.frontend.data.set.provider;

import com.liferay.commerce.checkout.web.internal.constants.CommerceCheckoutFDSNames;
import com.liferay.commerce.checkout.web.internal.model.DeliveryGroup;
import com.liferay.commerce.checkout.web.internal.util.CommerceOrderUtil;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "fds.data.provider.key=" + CommerceCheckoutFDSNames.DELIVERY_GROUPS,
	service = FDSDataProvider.class
)
public class CommerceCheckoutDeliveryGroupsFDSDataProvider
	implements FDSDataProvider<DeliveryGroup> {

	@Override
	public List<DeliveryGroup> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		Map<String, DeliveryGroup> deliveryGroups = new HashMap<>();

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			if (deliveryGroups.containsKey(
					commerceOrderItem.getDeliveryGroupName())) {

				continue;
			}

			CommerceAddress commerceAddress =
				_commerceAddressLocalService.getCommerceAddress(
					commerceOrderItem.getShippingAddressId());

			Country country = commerceAddress.getCountry();

			DeliveryGroup deliveryGroup = new DeliveryGroup(
				commerceAddress.getCommerceAddressId(),
				StringBundler.concat(
					commerceAddress.getStreet1(), StringPool.COMMA_AND_SPACE,
					commerceAddress.getCity(), StringPool.COMMA_AND_SPACE,
					country.getName(_portal.getLocale(httpServletRequest))),
				commerceOrderItem.getRequestedDeliveryDate(),
				commerceOrderItem.getDeliveryGroupName());

			deliveryGroups.put(deliveryGroup.getName(), deliveryGroup);
		}

		return new ArrayList<>(deliveryGroups.values());
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		return CommerceOrderUtil.getCommerceOrderDeliveryGroupNamesCount(
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId));
	}

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private Portal _portal;

}