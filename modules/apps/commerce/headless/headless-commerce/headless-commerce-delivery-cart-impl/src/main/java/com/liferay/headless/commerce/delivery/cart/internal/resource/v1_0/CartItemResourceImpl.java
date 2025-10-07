/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.exception.NoSuchOrderItemException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartItem;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.CartItemDTOConverterContext;
import com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.delivery.cart.internal.odata.entity.v1_0.CartItemEntityModel;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartItemResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/cart-item.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = CartItemResource.class
)
public class CartItemResourceImpl extends BaseCartItemResourceImpl {

	@Override
	public void deleteCartItem(Long cartItemId) throws Exception {
		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(cartItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceOrder.getCommerceAccountId(), commerceOrder.getGroupId(),
			null, commerceOrder.getCommerceOrderId(),
			contextCompany.getCompanyId());

		_commerceOrderItemService.deleteCommerceOrderItem(
			cartItemId, commerceContext);
	}

	@Override
	public void deleteCartItemByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.
				fetchCommerceOrderItemByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderItem == null) {
			throw new NoSuchOrderItemException(
				"Unable to find order item with external reference code " +
					externalReferenceCode);
		}

		deleteCartItem(commerceOrderItem.getCommerceOrderItemId());
	}

	@Override
	public Page<CartItem> getCartByExternalReferenceCodeItemsPage(
			String externalReferenceCode, String search, Long skuId,
			Pagination pagination)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), null,
			CommerceOrderItem.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"commerceOrderId", commerceOrder.getCommerceOrderId());
				searchContext.setAttribute("parentCommerceOrderItemId", 0L);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			null,
			document -> _toCartItem(
				commerceOrder.getCommerceAccountId(),
				_commerceOrderItemService.getCommerceOrderItem(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public CartItem getCartItem(Long cartItemId) throws Exception {
		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(cartItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		return _toCartItem(
			commerceOrder.getCommerceAccountId(), commerceOrderItem);
	}

	@Override
	public CartItem getCartItemByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.
				fetchCommerceOrderItemByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderItem == null) {
			throw new NoSuchOrderItemException(
				"Unable to find order item with external reference code " +
					externalReferenceCode);
		}

		return getCartItem(commerceOrderItem.getCommerceOrderItemId());
	}

	@NestedField(parentClass = Cart.class, value = "cartItems")
	@Override
	public Page<CartItem> getCartItemsPage(
			@NestedFieldId("id") Long cartId, String search, Long skuId,
			Pagination pagination)
		throws Exception {

		if (cartId == 0) {
			return Page.of(Collections.emptyList());
		}

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), null,
			CommerceOrderItem.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"commerceOrderId", commerceOrder.getCommerceOrderId());
				searchContext.setAttribute("parentCommerceOrderItemId", 0L);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			null,
			document -> _toCartItem(
				commerceOrder.getCommerceAccountId(),
				_commerceOrderItemService.getCommerceOrderItem(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public CartItem patchCartItem(Long cartItemId, CartItem cartItem)
		throws Exception {

		return super.patchCartItem(cartItemId, cartItem);
	}

	@Override
	public CartItem patchCartItemByExternalReferenceCode(
			String externalReferenceCode, CartItem cartItem)
		throws Exception {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.
				fetchCommerceOrderItemByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderItem == null) {
			throw new NoSuchOrderItemException(
				"Unable to find order item with external reference code " +
					externalReferenceCode);
		}

		return patchCartItem(
			commerceOrderItem.getCommerceOrderItemId(), cartItem);
	}

	@Override
	public CartItem postCartByExternalReferenceCodeItem(
			String externalReferenceCode, CartItem cartItem)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return _toCartItem(
			commerceOrder.getCommerceAccountId(),
			_updateCartItem(cartItem, commerceOrder));
	}

	@Override
	public CartItem postCartItem(Long cartId, CartItem cartItem)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		return _toCartItem(
			commerceOrder.getCommerceAccountId(),
			_updateCartItem(cartItem, commerceOrder));
	}

	@Override
	public CartItem putCartItem(Long cartItemId, CartItem cartItem)
		throws Exception {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(cartItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		String options = GetterUtil.getString(cartItem.getOptions());

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceOrder.getCommerceAccountId(), commerceOrder.getGroupId(),
			null, commerceOrder.getCommerceOrderId(),
			contextCompany.getCompanyId());

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commerceOrder.getGroupId());

		if (Validator.isNotNull(options)) {
			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItem(
					commerceOrderItem.getExternalReferenceCode(),
					commerceOrderItem.getCommerceOrderItemId(), options,
					cartItem.getQuantity(), commerceContext, serviceContext);
		}
		else {
			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItem(
					commerceOrderItem.getCommerceOrderItemId(),
					cartItem.getQuantity(), commerceContext, serviceContext);
		}

		long shippingAddressId = GetterUtil.getLong(
			cartItem.getShippingAddressId());

		if (shippingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						cartItem.getShippingAddressExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceAddress != null) {
				shippingAddressId = commerceAddress.getCommerceAddressId();
			}
			else {
				shippingAddressId = commerceOrderItem.getShippingAddressId();
			}
		}

		String deliveryGroupName = GetterUtil.getString(
			cartItem.getDeliveryGroupName());

		if (Validator.isNull(deliveryGroupName)) {
			deliveryGroupName = GetterUtil.getString(
				cartItem.getDeliveryGroup());
		}

		Date requestedDeliveryDate = cartItem.getRequestedDeliveryDate();

		if (requestedDeliveryDate != null) {
			Calendar requestedDeliveryDateCalendar =
				CalendarFactoryUtil.getCalendar(
					requestedDeliveryDate.getTime());

			DateConfig requestedDeliveryDateConfig = new DateConfig(
				requestedDeliveryDateCalendar);

			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItemInfo(
					commerceOrderItem.getCommerceOrderItemId(),
					shippingAddressId, deliveryGroupName,
					commerceOrderItem.getPrintedNote(),
					requestedDeliveryDateConfig.getMonth(),
					requestedDeliveryDateConfig.getDay(),
					requestedDeliveryDateConfig.getYear());
		}
		else {
			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItemInfo(
					commerceOrderItem.getCommerceOrderItemId(),
					shippingAddressId, deliveryGroupName,
					commerceOrderItem.getPrintedNote());
		}

		return _toCartItem(
			commerceOrder.getCommerceAccountId(), commerceOrderItem);
	}

	@Override
	public CartItem putCartItemByExternalReferenceCode(
			String externalReferenceCode, CartItem cartItem)
		throws Exception {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.
				fetchCommerceOrderItemByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderItem == null) {
			throw new NoSuchOrderItemException(
				"Unable to find order item with external reference code " +
					externalReferenceCode);
		}

		return putCartItem(
			commerceOrderItem.getCommerceOrderItemId(), cartItem);
	}

	private CartItem[] _getCartItems(
		long commerceAccountId, CommerceOrderItem commerceOrderItem) {

		return transformToArray(
			commerceOrderItem.getChildCommerceOrderItems(),
			cartItem -> _orderItemDTOConverter.toDTO(
				new CartItemDTOConverterContext(
					commerceAccountId, cartItem.getCommerceOrderItemId(),
					contextAcceptLanguage.getPreferredLocale())),
			CartItem.class);
	}

	private CartItem _toCartItem(
			long commerceAccountId, CommerceOrderItem commerceOrderItem)
		throws Exception {

		CartItemDTOConverterContext cartItemDTOConverterContext =
			new CartItemDTOConverterContext(
				commerceAccountId, commerceOrderItem.getCommerceOrderItemId(),
				contextAcceptLanguage.getPreferredLocale());

		cartItemDTOConverterContext.setAttribute(
			"cartItems", _getCartItems(commerceAccountId, commerceOrderItem));

		return _orderItemDTOConverter.toDTO(cartItemDTOConverterContext);
	}

	private CommerceOrderItem _updateCartItem(
			CartItem cartItem, CommerceOrder commerceOrder)
		throws Exception {

		SkuUnitOfMeasure skuUnitOfMeasure = cartItem.getSkuUnitOfMeasure();
		String skuUnitOfMeasureKey = StringPool.BLANK;

		if (skuUnitOfMeasure != null) {
			skuUnitOfMeasureKey = skuUnitOfMeasure.getKey();
		}

		long replacedSkuId = GetterUtil.getLong(cartItem.getReplacedSkuId());

		if (replacedSkuId == 0) {
			CPInstance replacedSku =
				_cpInstanceService.fetchCPInstanceByExternalReferenceCode(
					cartItem.getReplacedSkuExternalReferenceCode(),
					contextCompany.getCompanyId());

			if (replacedSku != null) {
				replacedSkuId = replacedSku.getCPInstanceId();
			}
		}

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceOrder.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		CommerceOrderItem commerceOrderItem;

		if (commerceOrderCheckoutConfiguration.showSeparateOrderItems() ||
			commerceOrderCheckoutConfiguration.multishippingEnabled()) {

			commerceOrderItem = _commerceOrderItemService.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(), cartItem.getSkuId(),
				cartItem.getOptions(),
				BigDecimalUtil.get(cartItem.getQuantity(), BigDecimal.ONE),
				GetterUtil.getLong(cartItem.getReplacedSkuId()),
				BigDecimal.ZERO, skuUnitOfMeasureKey,
				_commerceContextFactory.create(
					commerceOrder.getCommerceAccountId(),
					commerceOrder.getGroupId(), null,
					commerceOrder.getCommerceOrderId(),
					contextCompany.getCompanyId()),
				_serviceContextHelper.getServiceContext(
					commerceOrder.getGroupId()));
		}
		else {
			commerceOrderItem =
				_commerceOrderItemService.addOrUpdateCommerceOrderItem(
					commerceOrder.getCommerceOrderId(), cartItem.getSkuId(),
					cartItem.getOptions(),
					BigDecimalUtil.get(cartItem.getQuantity(), BigDecimal.ONE),
					replacedSkuId, BigDecimal.ZERO, skuUnitOfMeasureKey,
					_commerceContextFactory.create(
						commerceOrder.getCommerceAccountId(),
						commerceOrder.getGroupId(),
						commerceOrder.getCommerceCurrencyCode(),
						commerceOrder.getCommerceOrderId(),
						contextCompany.getCompanyId()),
					_serviceContextHelper.getServiceContext(
						commerceOrder.getGroupId()));
		}

		long shippingAddressId = GetterUtil.getLong(
			cartItem.getShippingAddressId());

		if (shippingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						cartItem.getShippingAddressExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceAddress != null) {
				shippingAddressId = commerceAddress.getCommerceAddressId();
			}
			else {
				shippingAddressId = commerceOrderItem.getShippingAddressId();
			}
		}

		String deliveryGroupName = GetterUtil.getString(
			cartItem.getDeliveryGroupName());

		if (Validator.isNull(deliveryGroupName)) {
			deliveryGroupName = GetterUtil.getString(
				cartItem.getDeliveryGroup());
		}

		Date requestedDeliveryDate = cartItem.getRequestedDeliveryDate();

		if (requestedDeliveryDate != null) {
			Calendar requestedDeliveryDateCalendar =
				CalendarFactoryUtil.getCalendar(
					requestedDeliveryDate.getTime());

			DateConfig requestedDeliveryDateConfig = new DateConfig(
				requestedDeliveryDateCalendar);

			return _commerceOrderItemService.updateCommerceOrderItemInfo(
				commerceOrderItem.getCommerceOrderItemId(), shippingAddressId,
				deliveryGroupName, commerceOrderItem.getPrintedNote(),
				requestedDeliveryDateConfig.getMonth(),
				requestedDeliveryDateConfig.getDay(),
				requestedDeliveryDateConfig.getYear());
		}

		return _commerceOrderItemService.updateCommerceOrderItemInfo(
			commerceOrderItem.getCommerceOrderItemId(), shippingAddressId,
			deliveryGroupName, commerceOrderItem.getPrintedNote());
	}

	private static final EntityModel _entityModel = new CartItemEntityModel();

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference(target = DTOConverterConstants.CART_ITEM_DTO_CONVERTER)
	private DTOConverter<CommerceOrderItem, CartItem> _orderItemDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}