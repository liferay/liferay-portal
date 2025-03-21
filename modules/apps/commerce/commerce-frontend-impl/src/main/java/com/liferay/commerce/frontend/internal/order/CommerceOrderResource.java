/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.order;

import com.liferay.commerce.frontend.internal.account.model.Order;
import com.liferay.commerce.frontend.internal.account.model.OrderList;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceOrderResource.class)
public class CommerceOrderResource {

	public OrderList getOrderList(
			long groupId, String keywords, int page, int pageSize,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long companyId = _portal.getCompanyId(httpServletRequest);

		groupId =
			_commerceChannelLocalService.getCommerceChannelGroupIdBySiteGroupId(
				groupId);

		List<Order> orders = _getOrders(
			companyId, groupId, keywords, page, pageSize, httpServletRequest);

		return new OrderList(
			orders, _getOrdersCount(companyId, groupId, keywords));
	}

	private String _getOrderLinkURL(
			long commerceOrderId, HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL editURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceOrder.class.getName(),
				PortletProvider.Action.EDIT)
		).setActionName(
			"/commerce_open_order_content/edit_commerce_order"
		).setCMD(
			"setCurrent"
		).setParameter(
			"commerceOrderId", commerceOrderId
		).buildPortletURL();

		String redirect = _portal.getCurrentURL(httpServletRequest);

		editURL.setParameter("redirect", redirect);

		return editURL.toString();
	}

	private List<Order> _getOrders(
			long companyId, long groupId, String keywords, int page,
			int pageSize, HttpServletRequest httpServletRequest)
		throws PortalException {

		int start = (page - 1) * pageSize;
		int end = page * pageSize;

		return TransformUtil.transform(
			_commerceOrderService.getUserCommerceOrders(
				companyId, groupId, keywords, start, end),
			commerceOrder -> {
				Date modifiedDate = commerceOrder.getModifiedDate();

				String modifiedDateTimeDescription =
					_language.getTimeDescription(
						httpServletRequest,
						System.currentTimeMillis() - modifiedDate.getTime(),
						true);

				return new Order(
					commerceOrder.getCommerceOrderId(),
					commerceOrder.getCommerceAccountId(),
					commerceOrder.getCommerceAccountName(),
					commerceOrder.getPurchaseOrderNumber(),
					_language.format(
						httpServletRequest, "x-ago",
						modifiedDateTimeDescription),
					WorkflowConstants.getStatusLabel(commerceOrder.getStatus()),
					_getOrderLinkURL(
						commerceOrder.getCommerceOrderId(),
						httpServletRequest));
			});
	}

	private int _getOrdersCount(long companyId, long groupId, String keywords)
		throws PortalException {

		return (int)_commerceOrderService.getUserPendingCommerceOrdersCount(
			companyId, groupId, keywords);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}