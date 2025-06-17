/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.util;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.importer.type.CSVCommerceOrderImporterTypeImpl;
import com.liferay.commerce.order.content.web.internal.importer.type.CommerceOrdersCommerceOrderImporterTypeImpl;
import com.liferay.commerce.order.content.web.internal.importer.type.CommerceWishListsCommerceOrderImporterTypeImpl;
import com.liferay.commerce.order.content.web.internal.model.Order;
import com.liferay.commerce.order.content.web.internal.model.WishList;
import com.liferay.commerce.order.status.CommerceOrderStatus;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceOrderFDSUtil {

	public static String getCSVCommerceOrderPreviewURL(
		long fileEntryId, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				PortalUtil.getOriginalServletRequest(httpServletRequest),
				portletDisplay.getId(), themeDisplay.getPlid(),
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_open_order_content/view_commerce_order_importer_type"
		).setParameter(
			"commerceOrderId",
			ParamUtil.getLong(httpServletRequest, "commerceOrderId")
		).setParameter(
			"commerceOrderImporterTypeKey", CSVCommerceOrderImporterTypeImpl.KEY
		).setParameter(
			"fileEntryId", fileEntryId
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public static String getEditOrderURL(
			long commerceOrderId, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				PortalUtil.getOriginalServletRequest(httpServletRequest),
				portletDisplay.getId(), themeDisplay.getPlid(),
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_open_order_content/edit_commerce_order"
		).setCMD(
			"setCurrent"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				PortalUtil.getCurrentURL(httpServletRequest))
		).setParameter(
			"commerceOrderId", commerceOrderId
		).buildString();
	}

	public static String getOrderCommerceOrderPreviewURL(
		Order order, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				PortalUtil.getOriginalServletRequest(httpServletRequest),
				portletDisplay.getId(), themeDisplay.getPlid(),
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_open_order_content/view_commerce_order_importer_type"
		).setParameter(
			"commerceOrderId",
			ParamUtil.getLong(httpServletRequest, "commerceOrderId")
		).setParameter(
			"commerceOrderImporterTypeKey",
			CommerceOrdersCommerceOrderImporterTypeImpl.KEY
		).setParameter(
			"orderDetailURL",
			ParamUtil.getString(httpServletRequest, "orderDetailURL")
		).setParameter(
			"selectedCommerceOrderId", order.getOrderId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public static List<Order> getOrders(
			long commerceChannelGroupId, List<CommerceOrder> commerceOrders,
			CommerceOrderStatusRegistry commerceOrderStatusRegistry,
			CommerceOrderTypeService commerceOrderTypeService,
			GroupLocalService groupLocalService, String priceDisplayType,
			boolean showCommerceOrderCreateTime, ThemeDisplay themeDisplay)
		throws PortalException {

		CommerceGroupThreadLocal.set(
			groupLocalService.fetchGroup(commerceChannelGroupId));

		return TransformUtil.transform(
			commerceOrders,
			commerceOrder -> {
				String amount = StringPool.BLANK;

				CommerceMoney totalCommerceMoney =
					commerceOrder.getTotalMoney();

				if (priceDisplayType.equals(
						CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

					totalCommerceMoney =
						commerceOrder.getTotalWithTaxAmountMoney();
				}

				if (totalCommerceMoney != null) {
					amount = totalCommerceMoney.format(
						themeDisplay.getLocale());
				}

				ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
					"content.Language", themeDisplay.getLocale(),
					CommerceOrderFDSUtil.class);

				String commerceOrderStatusLabel = LanguageUtil.get(
					resourceBundle,
					CommerceOrderConstants.getOrderStatusLabel(
						commerceOrder.getOrderStatus()));

				if (commerceOrderStatusLabel == null) {
					CommerceOrderStatus commerceOrderStatus =
						commerceOrderStatusRegistry.getCommerceOrderStatus(
							commerceOrder.getOrderStatus());

					commerceOrderStatusLabel = commerceOrderStatus.getLabel(
						themeDisplay.getLocale());
				}

				String workflowStatusLabel = LanguageUtil.get(
					resourceBundle,
					WorkflowConstants.getStatusLabel(
						commerceOrder.getStatus()));

				Date orderDate = commerceOrder.getCreateDate();

				if (commerceOrder.getOrderDate() != null) {
					orderDate = commerceOrder.getOrderDate();
				}

				String commerceOrderTypeName = StringPool.BLANK;

				CommerceOrderType commerceOrderType =
					commerceOrderTypeService.fetchCommerceOrderType(
						commerceOrder.getCommerceOrderTypeId());

				if (commerceOrderType != null) {
					commerceOrderTypeName = commerceOrderType.getName(
						themeDisplay.getLocale());
				}

				return new Order(
					commerceOrder.getExternalReferenceCode(),
					commerceOrder.getCommerceOrderId(),
					commerceOrder.getCommerceAccountName(), amount,
					commerceOrder.getUserName(),
					_formatCommerceOrderCreateDate(
						themeDisplay.getLocale(), orderDate,
						showCommerceOrderCreateTime,
						themeDisplay.getTimeZone()),
					commerceOrder.getName(), commerceOrderStatusLabel,
					commerceOrderTypeName,
					commerceOrder.getPurchaseOrderNumber(),
					workflowStatusLabel);
			});
	}

	public static String getOrderViewDetailURL(
			long commerceOrderId, ThemeDisplay themeDisplay)
		throws PortalException {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLFactoryUtil.create(
			themeDisplay.getRequest(), portletDisplay.getId(),
			themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"backURL",
			PortletURLBuilder.create(
				portletURL
			).setParameter(
				"itemsPerPage",
				ParamUtil.getString(themeDisplay.getRequest(), "pageSize")
			).setParameter(
				"pageNumber",
				ParamUtil.getString(themeDisplay.getRequest(), "page")
			).setParameter(
				"tableName", CommerceOrderFDSNames.PLACED_ORDERS
			).buildString());
		portletURL.setParameter(
			"mvcRenderCommandName",
			"/commerce_order_content/view_commerce_order_details");
		portletURL.setParameter(
			"commerceOrderId", String.valueOf(commerceOrderId));

		return portletURL.toString();
	}

	public static String getViewShipmentURL(
		long commerceOrderItemId, ThemeDisplay themeDisplay) {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				themeDisplay.getRequest(), portletDisplay.getId(),
				themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_order_content/view_commerce_order_shipments"
		).setParameter(
			"commerceOrderItemId", commerceOrderItemId
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		portletURL.setParameter("backURL", portletURL.toString());

		return portletURL.toString();
	}

	public static String getWishListCommerceOrderPreviewURL(
		WishList wishList, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				PortalUtil.getOriginalServletRequest(httpServletRequest),
				portletDisplay.getId(), themeDisplay.getPlid(),
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_open_order_content/view_commerce_order_importer_type"
		).setParameter(
			"commerceOrderId",
			ParamUtil.getLong(httpServletRequest, "commerceOrderId")
		).setParameter(
			"commerceOrderImporterTypeKey",
			CommerceWishListsCommerceOrderImporterTypeImpl.KEY
		).setParameter(
			"commerceWishListId", wishList.getWishListId()
		).setParameter(
			"orderDetailURL",
			ParamUtil.getString(httpServletRequest, "orderDetailURL")
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private static String _formatCommerceOrderCreateDate(
		Locale locale, Date orderDate, boolean showCommerceOrderCreateTime,
		TimeZone timeZone) {

		Format commerceOrderDateFormat = FastDateFormatFactoryUtil.getDate(
			DateFormat.MEDIUM, locale, timeZone);

		if (showCommerceOrderCreateTime) {
			Format commerceOrderTimeFormat = FastDateFormatFactoryUtil.getTime(
				DateFormat.MEDIUM, locale, timeZone);

			return commerceOrderDateFormat.format(orderDate) + " " +
				commerceOrderTimeFormat.format(orderDate);
		}

		return commerceOrderDateFormat.format(orderDate);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderFDSUtil.class);

}