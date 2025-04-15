/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.url;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.rest.application.key=/headless-commerce-delivery-order/v1.0/Shipment",
	service = FDSAPIURLResolver.class
)
public class PlacedCommerceOrderShipmentsFDSAPIURLResolver
	implements FDSAPIURLResolver {

	@Override
	public String getSchema() {
		return "Shipment";
	}

	@Override
	public String resolve(String baseURL, HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceOrder commerceOrder =
			CommerceOrderInfoItemUtil.getCommerceOrder(
				_commerceOrderService, httpServletRequest);

		if (commerceOrder == null) {
			return StringPool.BLANK;
		}

		return StringUtil.replace(
			baseURL,
			new String[] {"{externalReferenceCode}", "{placedOrderId}"},
			new String[] {
				String.valueOf(commerceOrder.getExternalReferenceCode()),
				String.valueOf(commerceOrder.getCommerceOrderId())
			});
	}

	@Reference
	private CommerceOrderService _commerceOrderService;

}