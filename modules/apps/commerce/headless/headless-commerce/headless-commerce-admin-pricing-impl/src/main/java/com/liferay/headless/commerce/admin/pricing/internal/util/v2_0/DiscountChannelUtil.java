/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountChannel;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class DiscountChannelUtil {

	public static CommerceChannelRel addCommerceDiscountChannelRel(
			CommerceChannelService commerceChannelService,
			CommerceChannelRelService commerceChannelRelService,
			DiscountChannel discountChannel, CommerceDiscount commerceDiscount,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		CommerceChannel commerceChannel = _getCommerceChannel(
			commerceChannelService, discountChannel, serviceContext);

		CommerceChannelRel commerceChannelRel =
			commerceChannelRelService.fetchCommerceChannelRel(
				CommerceDiscount.class.getName(),
				commerceDiscount.getCommerceDiscountId(),
				commerceChannel.getCommerceChannelId());

		if (commerceChannelRel != null) {
			return commerceChannelRel;
		}

		return commerceChannelRelService.addCommerceChannelRel(
			CommerceDiscount.class.getName(),
			commerceDiscount.getCommerceDiscountId(),
			commerceChannel.getCommerceChannelId(), serviceContext);
	}

	private static CommerceChannel _getCommerceChannel(
			CommerceChannelService commerceChannelService,
			DiscountChannel discountChannel, ServiceContext serviceContext)
		throws PortalException {

		String channelExternalReferenceCode =
			discountChannel.getChannelExternalReferenceCode();

		if (Validator.isNull(channelExternalReferenceCode)) {
			return commerceChannelService.getCommerceChannel(
				GetterUtil.getLong(discountChannel.getChannelId()));
		}

		CommerceChannel commerceChannel =
			commerceChannelService.fetchCommerceChannelByExternalReferenceCode(
				channelExternalReferenceCode, serviceContext.getCompanyId());

		if (commerceChannel != null) {
			return commerceChannel;
		}

		long channelId = GetterUtil.getLong(discountChannel.getChannelId());

		if ((channelId > 0) && !LazyReferencingThreadLocal.isEnabled()) {
			commerceChannel = commerceChannelService.fetchCommerceChannel(
				channelId);

			if ((commerceChannel != null) &&
				(commerceChannel.getCompanyId() ==
					serviceContext.getCompanyId())) {

				return commerceChannel;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchChannelException(
				"Unable to find channel with external reference code " +
					channelExternalReferenceCode);
		}

		return commerceChannelService.getOrAddEmptyCommerceChannel(
			channelExternalReferenceCode);
	}

}