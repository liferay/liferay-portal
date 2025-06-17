/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.frontend.data.set.util.CommerceOrderFDSUtil;
import com.liferay.commerce.order.content.web.internal.model.Order;
import com.liferay.commerce.order.content.web.internal.portlet.configuration.CommerceOrderContentPortletInstanceConfiguration;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.PLACED_ORDERS,
	service = FDSDataProvider.class
)
public class PlacedCommerceOrderFDSDataProvider
	implements FDSDataProvider<Order> {

	@Override
	public List<Order> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(
			WebKeys.VISITED_GROUP_ID_RECENT, themeDisplay.getScopeGroupId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				themeDisplay.getScopeGroupId());

		if (commerceChannel == null) {
			return Collections.emptyList();
		}

		CommerceGroupThreadLocal.set(commerceChannel.getGroup());

		List<CommerceOrder> commerceOrders =
			_commerceOrderService.getUserPlacedCommerceOrders(
				commerceChannel.getCompanyId(), commerceChannel.getGroupId(),
				fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), sort);

		CPRequestHelper cpRequestHelper = new CPRequestHelper(
			httpServletRequest);

		CommerceOrderContentPortletInstanceConfiguration
			commerceOrderContentPortletInstanceConfiguration =
				_configurationProvider.getPortletInstanceConfiguration(
					CommerceOrderContentPortletInstanceConfiguration.class,
					cpRequestHelper.getThemeDisplay());

		return CommerceOrderFDSUtil.getOrders(
			commerceChannel.getGroupId(), commerceOrders,
			_commerceOrderStatusRegistry, _commerceOrderTypeService,
			_groupLocalService, commerceChannel.getPriceDisplayType(),
			commerceOrderContentPortletInstanceConfiguration.
				showCommerceOrderCreateTime(),
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

		return (int)_commerceOrderService.getUserPlacedCommerceOrdersCount(
			commerceChannel.getCompanyId(), commerceChannel.getGroupId(),
			fdsKeywords.getKeywords());
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderStatusRegistry _commerceOrderStatusRegistry;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

}