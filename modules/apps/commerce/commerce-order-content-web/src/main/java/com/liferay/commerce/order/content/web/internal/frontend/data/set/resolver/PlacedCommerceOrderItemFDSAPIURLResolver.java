/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.resolver;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.frontend.data.set.resolver.FDSAPIURLResolver;
import com.liferay.portal.kernel.util.Validator;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = "fds.rest.application.key=/headless-commerce-delivery-order/v1.0/PlacedOrderItem",
	service = FDSAPIURLResolver.class
)
public class PlacedCommerceOrderItemFDSAPIURLResolver
	implements FDSAPIURLResolver {

	@Override
	public String getSchema() {
		return "PlacedOrderItem";
	}

	@Override
	public String resolve(
		String baseURL, HttpServletRequest httpServletRequest) {

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (Validator.isNotNull(commerceContext)) {
			CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

			if (Validator.isNotNull(commerceOrder)) {
				baseURL = baseURL.replaceAll(
					"\\{placedOrderId\\}",
					String.valueOf(commerceOrder.getCommerceOrderId())
				).replaceAll(
					"\\{externalReferenceCode\\}",
					commerceOrder.getExternalReferenceCode()
				);
			}
		}

		return baseURL;
	}

}