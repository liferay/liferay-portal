/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.model.OrderItem;
import com.liferay.commerce.order.content.web.internal.util.CommerceOrderItemUtil;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CPSubscriptionInfo;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.product.util.CPSubscriptionType;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.PLACED_ORDER_ITEMS,
	service = FDSDataProvider.class
)
public class PlacedCommerceOrderItemFDSDataProvider
	implements FDSDataProvider<OrderItem> {

	@Override
	public List<OrderItem> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		BaseModelSearchResult<CommerceOrderItem> baseModelSearchResult =
			_getBaseModelSearchResult(
				fdsKeywords, fdsPagination, httpServletRequest, sort);

		try {
			return _getOrderItems(
				baseModelSearchResult.getBaseModels(), httpServletRequest);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Collections.emptyList();
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		BaseModelSearchResult<CommerceOrderItem> baseModelSearchResult =
			_getBaseModelSearchResult(
				fdsKeywords, null, httpServletRequest, null);

		return baseModelSearchResult.getLength();
	}

	private String _formatSubscriptionPeriod(
			CommerceOrderItem commerceOrderItem, Locale locale)
		throws Exception {

		CPInstance cpInstance = commerceOrderItem.fetchCPInstance();

		if ((cpInstance == null) ||
			(cpInstance.getCPSubscriptionInfo() == null)) {

			return null;
		}

		CPSubscriptionInfo cpSubscriptionInfo =
			cpInstance.getCPSubscriptionInfo();

		String period = StringPool.BLANK;

		CPSubscriptionType cpSubscriptionType =
			_cpSubscriptionTypeRegistry.getCPSubscriptionType(
				cpSubscriptionInfo.getSubscriptionType());

		if (cpSubscriptionType != null) {
			period = cpSubscriptionType.getLabel(locale);

			if (cpSubscriptionInfo.getSubscriptionLength() > 1) {
				period = _language.get(
					locale,
					StringUtil.toLowerCase(
						cpSubscriptionType.getLabel(LocaleUtil.US) +
							CharPool.LOWER_CASE_S));
			}
		}

		return _language.format(
			locale, "every-x-x",
			new Object[] {cpSubscriptionInfo.getSubscriptionLength(), period});
	}

	private BaseModelSearchResult<CommerceOrderItem> _getBaseModelSearchResult(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		int start = 0;
		int end = 0;

		if (fdsPagination != null) {
			start = fdsPagination.getStartPosition();
			end = fdsPagination.getEndPosition();
		}

		return _commerceOrderItemService.searchCommerceOrderItems(
			commerceOrderId, 0, fdsKeywords.getKeywords(), start, end, sort);
	}

	private List<OrderItem> _getChildOrderItems(
			CommerceOrderItem commerceOrderItem,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return _getOrderItems(
			_commerceOrderItemService.getChildCommerceOrderItems(
				commerceOrderItem.getCommerceOrderItemId()),
			httpServletRequest);
	}

	private List<OrderItem> _getOrderItems(
			List<CommerceOrderItem> commerceOrderItems,
			HttpServletRequest httpServletRequest)
		throws Exception {

		if (commerceOrderItems.isEmpty()) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			commerceOrderItems,
			commerceOrderItem -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				Locale locale = themeDisplay.getLocale();

				CommerceOrder commerceOrder =
					commerceOrderItem.getCommerceOrder();

				CommerceOrderItemPrice commerceOrderItemPrice =
					_commerceOrderPriceCalculation.getCommerceOrderItemPrice(
						commerceOrder.getCommerceCurrency(), commerceOrderItem);

				CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
					_cpInstanceUnitOfMeasureLocalService.
						fetchCPInstanceUnitOfMeasure(
							commerceOrderItem.getCPInstanceId(),
							commerceOrderItem.getUnitOfMeasureKey());

				return new OrderItem(
					commerceOrderItem.getCPInstanceId(),
					CommerceOrderItemUtil.formatDiscountAmount(
						commerceOrderItemPrice, locale),
					null,
					_commerceOrderItemQuantityFormatter.format(
						commerceOrderItem, cpInstanceUnitOfMeasure, locale),
					_formatSubscriptionPeriod(commerceOrderItem, locale),
					commerceOrderItem.getName(locale),
					CommerceOrderItemUtil.getOptions(
						commerceOrderItem, _cpInstanceHelper, locale),
					commerceOrderItem.getCommerceOrderId(),
					commerceOrderItem.getCommerceOrderItemId(),
					_getChildOrderItems(commerceOrderItem, httpServletRequest),
					commerceOrderItem.getParentCommerceOrderItemId(),
					CommerceOrderItemUtil.formatUnitPrice(
						commerceOrderItemPrice, _language, locale),
					CommerceOrderItemUtil.formatPromoPrice(
						commerceOrderItemPrice, locale),
					_commerceQuantityFormatter.format(
						cpInstanceUnitOfMeasure,
						commerceOrderItem.getShippedQuantity()),
					commerceOrderItem.getSku(),
					_cpInstanceHelper.getCPInstanceThumbnailSrc(
						CommerceUtil.getCommerceAccountId(
							(CommerceContext)httpServletRequest.getAttribute(
								CommerceWebKeys.COMMERCE_CONTEXT)),
						commerceOrderItem.getCPInstanceId()),
					CommerceOrderItemUtil.formatTotalPrice(
						commerceOrderItemPrice, locale),
					commerceOrderItem.getUnitOfMeasureKey());
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PlacedCommerceOrderItemFDSDataProvider.class);

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private CPSubscriptionTypeRegistry _cpSubscriptionTypeRegistry;

	@Reference
	private Language _language;

}