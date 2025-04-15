/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.model.Address;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.SHIPPING_ADDRESSES,
	service = FDSDataProvider.class
)
public class CommerceShippingAddressFDSDataProvider
	implements FDSDataProvider<Address> {

	@Override
	public List<Address> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		return TransformUtil.transform(
			_commerceAddressService.getShippingCommerceAddresses(
				commerceOrder.getCompanyId(), AccountEntry.class.getName(),
				commerceOrder.getCommerceAccountId(),
				commerceChannel.getCommerceChannelId(),
				fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), sort),
			commerceAddress -> new Address(
				commerceAddress.getCommerceAddressId(),
				commerceAddress.getName(),
				_getDescriptiveCommerceAddress(commerceAddress)));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		return _commerceAddressService.getShippingCommerceAddressesCount(
			commerceOrder.getCompanyId(), AccountEntry.class.getName(),
			commerceOrder.getCommerceAccountId(),
			commerceChannel.getCommerceChannelId(), fdsKeywords.getKeywords());
	}

	private String _getDescriptiveCommerceAddress(
			CommerceAddress commerceAddress)
		throws PortalException {

		if (commerceAddress == null) {
			return StringPool.BLANK;
		}

		Region region = commerceAddress.getRegion();

		StringBundler sb = new StringBundler((region == null) ? 5 : 7);

		sb.append(commerceAddress.getStreet1());
		sb.append(StringPool.SPACE);
		sb.append(commerceAddress.getCity());
		sb.append(StringPool.NEW_LINE);

		if (region != null) {
			sb.append(region.getRegionCode());
			sb.append(StringPool.SPACE);
		}

		sb.append(commerceAddress.getZip());

		return sb.toString();
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

}