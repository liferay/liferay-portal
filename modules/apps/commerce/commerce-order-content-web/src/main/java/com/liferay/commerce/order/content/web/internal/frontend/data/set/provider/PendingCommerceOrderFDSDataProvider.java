/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.frontend.data.set.util.CommerceOrderFDSUtil;
import com.liferay.commerce.order.content.web.internal.model.Order;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.PENDING_ORDERS,
	service = FDSDataProvider.class
)
public class PendingCommerceOrderFDSDataProvider
	implements FDSDataProvider<Order> {

	@Override
	public List<Order> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				themeDisplay.getScopeGroupId());

		if (commerceChannel == null) {
			return Collections.emptyList();
		}

		String uuid = CookiesManagerUtil.getCookieValue(
			CommerceOrder.class.getName() + StringPool.POUND +
				commerceChannel.getGroupId(),
			httpServletRequest);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
				uuid, commerceChannel.getGroupId());

		CommerceGroupThreadLocal.set(commerceChannel.getGroup());

		if ((commerceOrder != null) && commerceOrder.isGuestOrder()) {
			return CommerceOrderFDSUtil.getOrders(
				commerceChannel.getGroupId(),
				Collections.singletonList(commerceOrder),
				_commerceOrderStatusRegistry, _commerceOrderTypeService,
				_groupLocalService, commerceChannel.getPriceDisplayType(), true,
				themeDisplay);
		}

		List<CommerceOrder> commerceOrders =
			_commerceOrderService.getUserOpenCommerceOrders(
				commerceChannel.getCompanyId(), commerceChannel.getGroupId(),
				fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), sort);

		return CommerceOrderFDSUtil.getOrders(
			commerceChannel.getGroupId(), commerceOrders,
			_commerceOrderStatusRegistry, _commerceOrderTypeService,
			_groupLocalService, commerceChannel.getPriceDisplayType(), true,
			themeDisplay);
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				themeDisplay.getScopeGroupId());

		if (commerceChannel == null) {
			return 0;
		}

		String uuid = CookiesManagerUtil.getCookieValue(
			CommerceOrder.class.getName() + StringPool.POUND +
				commerceChannel.getGroupId(),
			httpServletRequest);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
				uuid, commerceChannel.getGroupId());

		if ((commerceOrder != null) && commerceOrder.isGuestOrder()) {
			return 1;
		}

		return (int)_commerceOrderService.getUserPendingCommerceOrdersCount(
			commerceChannel.getCompanyId(), commerceChannel.getGroupId(),
			fdsKeywords.getKeywords());
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderStatusRegistry _commerceOrderStatusRegistry;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private GroupLocalService _groupLocalService;

}