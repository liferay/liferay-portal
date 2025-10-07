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

		if (commerceContext == null) {
			return StringPool.BLANK;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannel(
				commerceContext.getCommerceChannelId());

		if (commerceChannel == null) {
			return StringPool.BLANK;
		}

		String accountExternalReferenceCode = StringPool.BLANK;
		long accountId = 0;

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry != null) {
			accountExternalReferenceCode =
				accountEntry.getExternalReferenceCode();
			accountId = accountEntry.getAccountEntryId();
		}

		long cartId = 0;
		String externalReferenceCode = StringPool.BLANK;

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder != null) {
			cartId = commerceOrder.getCommerceOrderId();
			externalReferenceCode = commerceOrder.getExternalReferenceCode();
		}

		return StringUtil.replace(
			baseURL,
			new String[] {
				"{accountExternalReferenceCode}", "{accountId}", "{cartId}",
				"{channelExternalReferenceCode}", "{channelId}",
				"{externalReferenceCode}"
			},
			new String[] {
				accountExternalReferenceCode, String.valueOf(accountId),
				String.valueOf(cartId),
				commerceChannel.getExternalReferenceCode(),
				String.valueOf(commerceChannel.getCommerceChannelId()),
				externalReferenceCode
			});
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

}