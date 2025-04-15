/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Luca Pellizzon
 */
public class DeliveryGroupDisplayContext {

	public DeliveryGroupDisplayContext(
		CommerceAddressService commerceAddressService) {

		_commerceAddressService = commerceAddressService;
	}

	public CommerceAddress getCommerceAddress(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return _commerceAddressService.fetchCommerceAddress(
			ParamUtil.getLong(httpServletRequest, "addressId"));
	}

	public Date getDeliveryGroupDate(HttpServletRequest httpServletRequest) {
		return new Date(ParamUtil.getLong(httpServletRequest, "deliveryDate"));
	}

	private final CommerceAddressService _commerceAddressService;

}