/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelService;
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListChannel;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class PriceListChannelUtil {

	public static CommercePriceListChannelRel addCommercePriceListChannelRel(
			CommerceChannelService commerceChannelService,
			CommercePriceListChannelRelService
				commercePriceListChannelRelService,
			PriceListChannel priceListChannel,
			CommercePriceList commercePriceList,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext = serviceContextHelper.getServiceContext(
			commercePriceList.getGroupId());

		CommerceChannel commerceChannel = _getCommerceChannel(
			commerceChannelService, priceListChannel, serviceContext);

		CommercePriceListChannelRel commercePriceListChannelRel =
			commercePriceListChannelRelService.fetchCommercePriceListChannelRel(
				commerceChannel.getCommerceChannelId(),
				commercePriceList.getCommercePriceListId());

		if (commercePriceListChannelRel != null) {
			commercePriceListChannelRelService.
				deleteCommercePriceListChannelRel(
					commercePriceListChannelRel.
						getCommercePriceListChannelRelId());
		}

		return commercePriceListChannelRelService.
			addCommercePriceListChannelRel(
				commercePriceList.getCommercePriceListId(),
				commerceChannel.getCommerceChannelId(),
				GetterUtil.get(priceListChannel.getOrder(), 0), serviceContext);
	}

	private static CommerceChannel _getCommerceChannel(
			CommerceChannelService commerceChannelService,
			PriceListChannel priceListChannel, ServiceContext serviceContext)
		throws PortalException {

		String channelExternalReferenceCode =
			priceListChannel.getChannelExternalReferenceCode();

		if (Validator.isNull(channelExternalReferenceCode)) {
			return commerceChannelService.getCommerceChannel(
				priceListChannel.getChannelId());
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			CommerceChannel commerceChannel =
				commerceChannelService.
					fetchCommerceChannelByExternalReferenceCode(
						channelExternalReferenceCode,
						serviceContext.getCompanyId());

			if (commerceChannel != null) {
				return commerceChannel;
			}

			throw new NoSuchChannelException(
				"Unable to find channel with external reference code " +
					channelExternalReferenceCode);
		}

		return commerceChannelService.getOrAddEmptyCommerceChannel(
			channelExternalReferenceCode);
	}

}