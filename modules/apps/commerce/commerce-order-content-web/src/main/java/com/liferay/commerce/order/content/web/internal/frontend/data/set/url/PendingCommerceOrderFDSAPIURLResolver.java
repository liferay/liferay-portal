/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.url;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = "fds.rest.application.key=/headless-commerce-delivery-cart/v1.0/Cart",
	service = FDSAPIURLResolver.class
)
public class PendingCommerceOrderFDSAPIURLResolver
	implements FDSAPIURLResolver {

	@Override
	public String getSchema() {
		return "Cart";
	}

	@Override
	public String resolve(String baseURL, HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry == null) {
			return StringPool.BLANK;
		}

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder == null) {
			return StringPool.BLANK;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				commerceContext.getCommerceChannelId());

		return StringUtil.replace(
			baseURL,
			new String[] {
				"{accountExternalReferenceCode}", "{accountId}", "{cartId}",
				"{channelExternalReferenceCode}", "{channelId}",
				"{externalReferenceCode}"
			},
			new String[] {
				accountEntry.getExternalReferenceCode(),
				String.valueOf(accountEntry.getAccountEntryId()),
				String.valueOf(commerceOrder.getCommerceOrderId()),
				commerceChannel.getExternalReferenceCode(),
				String.valueOf(commerceContext.getCommerceChannelId()),
				commerceOrder.getExternalReferenceCode()
			});
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

}