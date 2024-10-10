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

@Component(
	property = "fds.rest.application.key=/headless-commerce-delivery-cart/v1.0/CartItem",
	service = FDSAPIURLResolver.class
)
public class PendingCommerceOrderItemFDSAPIURLResolver
	implements FDSAPIURLResolver {

	@Override
	public String getSchema() {
		return "CartItem";
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
					"\\{cartId\\}",
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