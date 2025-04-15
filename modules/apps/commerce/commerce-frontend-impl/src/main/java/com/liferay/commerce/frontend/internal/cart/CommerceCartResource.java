/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.commerce.constants.CommercePriceConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.frontend.helper.ProductHelper;
import com.liferay.commerce.frontend.internal.cart.model.Cart;
import com.liferay.commerce.frontend.internal.cart.model.Coupon;
import com.liferay.commerce.frontend.internal.cart.model.OrderStatusInfo;
import com.liferay.commerce.frontend.internal.cart.model.Product;
import com.liferay.commerce.frontend.internal.cart.model.Summary;
import com.liferay.commerce.frontend.model.PriceModel;
import com.liferay.commerce.frontend.model.ProductSettingsModel;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = CommerceCartResource.class)
public class CommerceCartResource {

	@Path("/order/{orderId}/coupon-code/{couponCode}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response applyCouponCode(
		@PathParam("orderId") long commerceOrderId,
		@PathParam("couponCode") String couponCode,
		@Context HttpServletRequest httpServletRequest) {

		Coupon coupon = null;

		try {
			CommerceOrder commerceOrder =
				_commerceOrderService.getCommerceOrder(commerceOrderId);

			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					commerceOrder.getGroupId());

			CommerceContext commerceContext = _commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(),
				commerceChannel.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				commerceOrder.getCompanyId());

			_commerceOrderService.applyCouponCode(
				commerceOrder.getCommerceOrderId(), couponCode,
				commerceContext);

			coupon = new Coupon(couponCode);
		}
		catch (Exception exception) {
			_log.error(exception);

			coupon = new Coupon(
				StringUtil.split(exception.getLocalizedMessage()));
		}

		return _getResponse(coupon);
	}

	@Path("/order/{orderId}/coupon-code")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeCouponCode(
		@PathParam("orderId") long commerceOrderId,
		@Context HttpServletRequest httpServletRequest) {

		Coupon coupon = null;

		try {
			CommerceOrder commerceOrder =
				_commerceOrderService.getCommerceOrder(commerceOrderId);

			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					commerceOrder.getGroupId());

			CommerceContext commerceContext = _commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(),
				commerceChannel.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				commerceOrder.getCompanyId());

			_commerceOrderService.applyCouponCode(
				commerceOrder.getCommerceOrderId(), null, commerceContext);

			coupon = new Coupon(StringPool.BLANK);
		}
		catch (Exception exception) {
			_log.error(exception);

			coupon = new Coupon(
				StringUtil.split(exception.getLocalizedMessage()));
		}

		return _getResponse(coupon);
	}

	@Path("/cart-item")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateOrderItem(
		@FormParam("groupId") long groupId,
		@FormParam("languageId") String languageId,
		@FormParam("commerceAccountId") long commerceAccountId,
		@FormParam("quantity") String quantity,
		@FormParam("productId") long cpInstanceId,
		@FormParam("options") String options,
		@FormParam("orderId") long orderId,
		@FormParam("unitOfMeasureKey") String unitOfMeasureKey,
		@Context HttpServletRequest httpServletRequest) {

		Cart cart = null;

		try {
			CommerceContext commerceContext = _commerceContextFactory.create(
				commerceAccountId,
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				null, orderId, _portal.getCompanyId(httpServletRequest));

			httpServletRequest.setAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);

			CommerceOrder commerceOrder =
				_commerceOrderService.fetchCommerceOrder(orderId);

			if (commerceOrder == null) {
				commerceOrder =
					_commerceOrderHttpHelper.getCurrentCommerceOrder(
						httpServletRequest);
			}

			if (commerceOrder == null) {
				commerceOrder = _commerceOrderHttpHelper.addCommerceOrder(
					httpServletRequest);
			}

			commerceContext = _commerceContextFactory.create(
				commerceAccountId, commerceOrder.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				_portal.getCompanyId(httpServletRequest));

			httpServletRequest.setAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT, commerceContext);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				CommerceOrderItem.class.getName(), httpServletRequest);

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemService.addOrUpdateCommerceOrderItem(
					commerceOrder.getCommerceOrderId(), cpInstanceId, options,
					_commerceOrderItemQuantityFormatter.parse(
						CommerceOrderItem.class.getName(), quantity,
						LocaleUtil.fromLanguageId(languageId)),
					0, BigDecimal.ZERO, unitOfMeasureKey, commerceContext,
					serviceContext);

			cart = _getCart(
				commerceOrderItem.getCommerceOrderId(),
				_getDetailsURL(commerceOrder, groupId, httpServletRequest),
				LocaleUtil.fromLanguageId(languageId), commerceContext, true);
		}
		catch (Exception exception) {
			if (exception instanceof CommerceOrderValidatorException) {
				cart = new Cart(
					_getCommerceOrderValidatorResultsMessages(
						(CommerceOrderValidatorException)exception));
			}
			else {
				cart = new Cart(
					StringUtil.split(exception.getLocalizedMessage()));
			}
		}

		return _getResponse(cart);
	}

	private Cart _getCart(
			long commerceOrderId, String detailsUrl, Locale locale,
			CommerceContext commerceContext, boolean valid)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		List<Product> product = _getProducts(
			commerceOrder, commerceContext, locale);

		if (valid && product.isEmpty()) {
			valid = false;
		}

		String orderStatusInfoLabel = WorkflowConstants.getStatusLabel(
			commerceOrder.getStatus());

		OrderStatusInfo orderStatusInfo = new OrderStatusInfo(
			commerceOrder.getOrderStatus(), orderStatusInfoLabel,
			_language.get(locale, orderStatusInfoLabel));

		return new Cart(
			detailsUrl, commerceOrderId, product,
			_getSummary(commerceOrder, locale, commerceContext), valid,
			orderStatusInfo);
	}

	private PriceModel _getCommerceOrderItemPriceModel(
			CommerceOrderItem commerceOrderItem,
			CommerceContext commerceContext, Locale locale)
		throws Exception {

		CommerceOrderItemPrice commerceOrderItemPrice =
			_commerceOrderPriceCalculation.getCommerceOrderItemPricePerUnit(
				commerceContext.getCommerceCurrency(), commerceOrderItem);

		return _getPriceModel(
			commerceOrderItemPrice.getUnitPrice(),
			commerceOrderItemPrice.getPromoPrice(),
			commerceOrderItemPrice.getDiscountAmount(),
			commerceOrderItemPrice.getDiscountPercentage(),
			commerceOrderItemPrice.getDiscountPercentageLevel1(),
			commerceOrderItemPrice.getDiscountPercentageLevel2(),
			commerceOrderItemPrice.getDiscountPercentageLevel3(),
			commerceOrderItemPrice.getDiscountPercentageLevel4(),
			commerceOrderItemPrice.getFinalPrice(), locale);
	}

	private String[] _getCommerceOrderValidatorResultsMessages(
		CommerceOrderValidatorException commerceOrderValidatorException) {

		String[] errorMessages = new String[0];

		List<CommerceOrderValidatorResult> commerceOrderValidatorResults =
			commerceOrderValidatorException.getCommerceOrderValidatorResults();

		for (CommerceOrderValidatorResult commerceOrderValidatorResult :
				commerceOrderValidatorResults) {

			if (commerceOrderValidatorResult.hasMessageResult()) {
				errorMessages = ArrayUtil.append(
					errorMessages,
					commerceOrderValidatorResult.getLocalizedMessage());
			}
		}

		return errorMessages;
	}

	private String _getDetailsURL(
			CommerceOrder commerceOrder, long siteGroupId,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		String commerceCartPortletURL =
			_commerceOrderHttpHelper.getCommerceCartPortletURL(
				siteGroupId, httpServletRequest, commerceOrder);

		if (Validator.isNull(commerceCartPortletURL)) {
			return _portal.getHomeURL(httpServletRequest);
		}

		return commerceCartPortletURL;
	}

	private String[] _getErrorMessages(
			Locale locale, CommerceOrderItem commerceOrderItem)
		throws Exception {

		String[] errorMessages = new String[0];

		List<CommerceOrderValidatorResult> commerceOrderValidatorResults =
			_commerceOrderValidatorRegistry.validate(locale, commerceOrderItem);

		for (CommerceOrderValidatorResult commerceOrderValidatorResult :
				commerceOrderValidatorResults) {

			errorMessages = ArrayUtil.append(
				errorMessages,
				commerceOrderValidatorResult.getLocalizedMessage());
		}

		return errorMessages;
	}

	private PriceModel _getPriceModel(
			CommerceMoney unitPriceCommerceMoney,
			CommerceMoney promoPriceCommerceMoney,
			CommerceMoney discountAmountCommerceMoney,
			BigDecimal discountPercentage, BigDecimal discountPercentageLevel1,
			BigDecimal discountPercentageLevel2,
			BigDecimal discountPercentageLevel3,
			BigDecimal discountPercentageLevel4,
			CommerceMoney finalPriceCommerceMoney, Locale locale)
		throws Exception {

		PriceModel priceModel = null;

		if (unitPriceCommerceMoney.isPriceOnApplication()) {
			priceModel = new PriceModel(
				CommercePriceConstants.PRICE_VALUE_PRICE_ON_APPLICATION);
		}
		else {
			priceModel = new PriceModel(unitPriceCommerceMoney.format(locale));
		}

		if (promoPriceCommerceMoney != null) {
			if (promoPriceCommerceMoney.isPriceOnApplication()) {
				priceModel.setPromoPrice(
					CommercePriceConstants.PRICE_VALUE_PRICE_ON_APPLICATION);
			}
			else {
				priceModel.setPromoPrice(
					promoPriceCommerceMoney.format(locale));
			}
		}

		if (discountAmountCommerceMoney == null) {
			return priceModel;
		}

		BigDecimal discountAmount = discountAmountCommerceMoney.getPrice();

		if ((discountAmount == null) ||
			(discountAmount.compareTo(BigDecimal.ZERO) == 0)) {

			return priceModel;
		}

		priceModel.setDiscount(discountAmountCommerceMoney.format(locale));
		priceModel.setDiscountPercentage(
			_commercePriceFormatter.format(discountPercentage, locale));

		BigDecimal level1 = BigDecimal.ZERO;
		BigDecimal level2 = BigDecimal.ZERO;
		BigDecimal level3 = BigDecimal.ZERO;
		BigDecimal level4 = BigDecimal.ZERO;

		if (discountPercentageLevel1 != null) {
			level1 = discountPercentageLevel1;
		}

		if (discountPercentageLevel2 != null) {
			level2 = discountPercentageLevel2;
		}

		if (discountPercentageLevel3 != null) {
			level3 = discountPercentageLevel3;
		}

		if (discountPercentageLevel4 != null) {
			level4 = discountPercentageLevel4;
		}

		String[] discountPercentages = {
			level1.toString(), level2.toString(), level3.toString(),
			level4.toString()
		};

		priceModel.setDiscountPercentages(discountPercentages);

		priceModel.setFinalPrice(finalPriceCommerceMoney.format(locale));

		return priceModel;
	}

	private List<Product> _getProducts(
			CommerceOrder commerceOrder, CommerceContext commerceContext,
			Locale locale)
		throws Exception {

		List<Product> products = new ArrayList<>();

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			PriceModel priceModel = _getCommerceOrderItemPriceModel(
				commerceOrderItem, commerceContext, locale);

			ProductSettingsModel productSettingsModel =
				_productHelper.getProductSettingsModel(
					commerceOrderItem.getCPDefinitionId(), commerceContext);

			BigDecimal quantity = commerceOrderItem.getQuantity();

			Product product = new Product(
				commerceOrderItem.getCommerceOrderItemId(),
				commerceOrderItem.getParentCommerceOrderItemId(),
				commerceOrderItem.getCPInstanceId(),
				commerceOrderItem.getName(locale), priceModel,
				productSettingsModel, quantity.intValue(),
				commerceOrderItem.getSku(),
				_cpInstanceHelper.getCPInstanceThumbnailSrc(
					CommerceUtil.getCommerceAccountId(commerceContext),
					commerceOrderItem.getCPInstanceId()),
				commerceOrderItem.getUnitOfMeasureKey(),
				_getErrorMessages(locale, commerceOrderItem));

			long commerceOptionValueCPDefinitionId =
				commerceOrderItem.getCPDefinitionId();

			if (commerceOrderItem.hasParentCommerceOrderItem()) {
				commerceOptionValueCPDefinitionId =
					commerceOrderItem.
						getParentCommerceOrderItemCPDefinitionId();
			}

			product.setOptions(
				_cpInstanceHelper.getKeyValuePairs(
					commerceOptionValueCPDefinitionId,
					commerceOrderItem.getJson(), locale));

			products.add(product);
		}

		return _groupProductByOrderItemId(products);
	}

	private Response _getResponse(Object object) {
		if (object == null) {
			return Response.status(
				Response.Status.NOT_FOUND
			).build();
		}

		try {
			String json = _OBJECT_MAPPER.writeValueAsString(object);

			return Response.ok(
				json, MediaType.APPLICATION_JSON
			).build();
		}
		catch (JsonProcessingException jsonProcessingException) {
			_log.error(jsonProcessingException);
		}

		return Response.status(
			Response.Status.NOT_FOUND
		).build();
	}

	private Summary _getSummary(
			CommerceOrder commerceOrder, Locale locale,
			CommerceContext commerceContext)
		throws Exception {

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, commerceContext);

		if (commerceOrderPrice == null) {
			return null;
		}

		CommerceMoney subtotalCommerceMoney = commerceOrderPrice.getSubtotal();
		CommerceMoney totalCommerceMoney = commerceOrderPrice.getTotal();

		BigDecimal itemsQuantity =
			_commerceOrderItemService.getCommerceOrderItemsQuantity(
				commerceOrder.getCommerceOrderId());

		CommerceDiscountValue totalCommerceDiscountValue =
			commerceOrderPrice.getTotalDiscountValue();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		String priceDisplayType = commerceChannel.getPriceDisplayType();

		if (priceDisplayType.equals(
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			subtotalCommerceMoney =
				commerceOrderPrice.getSubtotalWithTaxAmount();
			totalCommerceMoney = commerceOrderPrice.getTotalWithTaxAmount();
			totalCommerceDiscountValue =
				commerceOrderPrice.getTotalDiscountValueWithTaxAmount();
		}

		Summary summary = new Summary(
			subtotalCommerceMoney.format(locale),
			totalCommerceMoney.format(locale), itemsQuantity.intValue());

		if (totalCommerceDiscountValue != null) {
			CommerceMoney discountAmountCommerceMoney =
				totalCommerceDiscountValue.getDiscountAmount();

			summary.setDiscount(discountAmountCommerceMoney.format(locale));
		}

		return summary;
	}

	private List<Product> _groupProductByOrderItemId(List<Product> products) {
		Map<Long, Product> productMap = new HashMap<>();

		for (Product product : products) {
			productMap.put(product.getId(), product);
		}

		for (Product product : products) {
			long parentProductId = product.getParentProductId();

			if (parentProductId == 0) {
				continue;
			}

			Product parent = productMap.get(parentProductId);

			if (parent != null) {
				if (parent.getChildItems() == null) {
					parent.setChildItems(new ArrayList<>());
				}

				List<Product> childItems = parent.getChildItems();

				childItems.add(product);

				productMap.remove(product.getId());
			}
		}

		return new ArrayList(productMap.values());
	}

	private static final ObjectMapper _OBJECT_MAPPER = new ObjectMapper() {
		{
			configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			disable(SerializationFeature.INDENT_OUTPUT);
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCartResource.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderValidatorRegistry _commerceOrderValidatorRegistry;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ProductHelper _productHelper;

}