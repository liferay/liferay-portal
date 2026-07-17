/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.commerce.configuration.CommerceAccountEntryValidationConfiguration;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceAccountEntryValidationConstants;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.exception.CommerceOrderBillingAddressException;
import com.liferay.commerce.exception.CommerceOrderGuestCheckoutException;
import com.liferay.commerce.exception.CommerceOrderPriceException;
import com.liferay.commerce.exception.CommerceOrderShippingAddressException;
import com.liferay.commerce.exception.CommerceOrderShippingMethodException;
import com.liferay.commerce.exception.CommerceOrderStatusException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceChannelConfigurationUtil;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.CommerceCurrencyUtil;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.ExpandoUtil;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Address;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartItem;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CouponCode;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.CartItemDTOConverterContext;
import com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.delivery.cart.internal.odata.entity.v1_0.CartEntityModel;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartResource;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

import java.security.Key;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/cart.properties",
	scope = ServiceScope.PROTOTYPE, service = CartResource.class
)
public class CartResourceImpl extends BaseCartResourceImpl {

	@Override
	public Response deleteCart(Long cartId) throws Exception {
		_commerceOrderService.deleteCommerceOrder(cartId);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response deleteCartByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		_commerceOrderService.deleteCommerceOrder(
			commerceOrder.getCommerceOrderId());

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Cart getCart(Long cartId) throws Exception {
		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			GetterUtil.getLong(cartId));

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public Cart getCartByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public String getCartByExternalReferenceCodePaymentUrl(
			String externalReferenceCode, String callbackURL)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return _getPaymentURL(callbackURL, commerceOrder);
	}

	@Override
	public String getCartPaymentURL(Long cartId, String callbackURL)
		throws Exception {

		return _getPaymentURL(
			callbackURL, _commerceOrderService.getCommerceOrder(cartId));
	}

	@Override
	public Page<Cart> getChannelAccountCartsPage(
			Long accountId, Long channelId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		return SearchUtil.search(
			null,
			booleanQuery -> {
			},
			filter, CommerceOrder.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"commerceAccountIds", new long[] {accountId});
				searchContext.setAttribute(
					"orderStatuses",
					new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN});
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(
					new long[] {commerceChannel.getGroupId()});

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}

				searchContext.setUserId(0);
			},
			sorts,
			document -> _toCart(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)), false));
	}

	@Override
	public Page<Cart>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode, String search,
				Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				accountExternalReferenceCode, contextCompany.getCompanyId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					channelExternalReferenceCode,
					contextCompany.getCompanyId());

		return getChannelAccountCartsPage(
			accountEntry.getAccountEntryId(),
			commerceChannel.getCommerceChannelId(), search, filter, pagination,
			sorts);
	}

	@Override
	public Page<Cart> getChannelCartsPage(
			Long channelId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		return SearchUtil.search(
			null,
			booleanQuery -> {
			},
			filter, CommerceOrder.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"commerceAccountIds",
					_getCommerceAccountIds(commerceChannel.getGroupId()));
				searchContext.setAttribute(
					"orderStatuses",
					new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN});
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(
					new long[] {commerceChannel.getGroupId()});

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}

				searchContext.setUserId(0);
			},
			sorts,
			document -> _toCart(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)), false));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Cart patchCart(Long cartId, Cart cart) throws Exception {
		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to patch a placed order");
		}

		_updateOrder(commerceOrder, cart);

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public Cart patchCartByExternalReferenceCode(
			String externalReferenceCode, Cart cart)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to patch a placed order");
		}

		_updateOrder(commerceOrder, cart);

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public Cart postCartByExternalReferenceCodeCheckout(
			String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return _checkoutOrder(commerceOrder);
	}

	@Override
	public Cart postCartByExternalReferenceCodeCouponCode(
			String externalReferenceCode, CouponCode couponCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		commerceOrder = _commerceOrderService.applyCouponCode(
			commerceOrder.getCommerceOrderId(), couponCode.getCode(),
			_commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(),
				commerceOrder.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				contextCompany.getCompanyId()));

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public Cart postCartCheckout(Long cartId) throws Exception {
		return _checkoutOrder(_commerceOrderService.getCommerceOrder(cartId));
	}

	@Override
	public Cart postCartCouponCode(Long cartId, CouponCode couponCode)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to patch a placed order");
		}

		commerceOrder = _commerceOrderService.applyCouponCode(
			cartId, couponCode.getCode(),
			_commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(),
				commerceOrder.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				contextCompany.getCompanyId()));

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public Cart postChannelCart(Long channelId, Cart cart) throws Exception {
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		CommerceOrder commerceOrder = _addCommerceOrder(
			cart, commerceChannel.getGroupId());

		CommerceChannelConfigurationUtil.validateGuestCheckout(
			commerceOrder.getCommerceOrderId());

		_updateOrder(commerceOrder, cart);

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public Cart postChannelCartByExternalReferenceCode(
			String externalReferenceCode, Cart cart)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return postChannelCart(commerceChannel.getCommerceChannelId(), cart);
	}

	@Override
	public Cart putCart(Long cartId, Cart cart) throws Exception {
		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to put a placed order");
		}

		_updateOrder(commerceOrder, cart);

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	@Override
	public Cart putCartByExternalReferenceCode(
			String externalReferenceCode, Cart cart)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to put a placed order");
		}

		_updateOrder(commerceOrder, cart);

		return _toCart(commerceOrder.getCommerceOrderId(), true);
	}

	private CommerceAddress _addCommerceAddress(
			CommerceOrder commerceOrder, Address address, int type,
			ServiceContext serviceContext)
		throws Exception {

		Country country = _countryService.getCountryByA2(
			commerceOrder.getCompanyId(), address.getCountryISOCode());

		return _commerceAddressService.addCommerceAddress(
			StringPool.BLANK, commerceOrder.getModelClassName(),
			commerceOrder.getCommerceOrderId(), country.getCountryId(),
			_getRegionId(null, country, address), address.getCity(),
			address.getDescription(), address.getName(),
			address.getPhoneNumber(), address.getStreet1(),
			address.getStreet2(), address.getStreet3(), address.getSubtype(),
			type, address.getZip(), serviceContext);
	}

	private CommerceOrder _addCommerceOrder(
			Cart cart, long commerceChannelGroupId)
		throws Exception {

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), cart.getCurrencyCode(),
				cart.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(cart.getCurrencyId()));

		AccountEntry accountEntry = null;

		if (cart.getAccountId() == AccountConstants.ACCOUNT_ENTRY_ID_GUEST) {
			accountEntry = _accountEntryLocalService.getGuestAccountEntry(
				contextCompany.getCompanyId());
		}
		else {
			accountEntry = _accountEntryLocalService.getAccountEntry(
				cart.getAccountId());
		}

		return _commerceOrderService.addCommerceOrder(
			commerceChannelGroupId, accountEntry.getAccountEntryId(),
			commerceCurrency.getCode(), _getCommerceOrderTypeId(cart));
	}

	private void _addOrUpdateBillingAddress(
			CommerceOrder commerceOrder, Address address, int type,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws Exception {

		if (commerceOrder.getBillingAddressId() > 0) {
			_updateCommerceOrderAddress(
				commerceOrder, address, type, serviceContext);
		}
		else {
			CommerceAddress commerceAddress = _addCommerceAddress(
				commerceOrder, address, type, serviceContext);

			commerceOrder.setBillingAddressId(
				commerceAddress.getCommerceAddressId());
		}

		_commerceOrderEngine.updateCommerceOrder(
			commerceOrder.getExternalReferenceCode(),
			commerceOrder.getCommerceOrderId(),
			commerceOrder.getBillingAddressId(),
			commerceOrder.getCommerceShippingMethodId(),
			commerceOrder.getShippingAddressId(),
			commerceOrder.getAdvanceStatus(),
			commerceOrder.getCommercePaymentMethodKey(),
			commerceOrder.getName(), commerceOrder.getPurchaseOrderNumber(),
			commerceOrder.getShippingAmount(),
			commerceOrder.getShippingOptionName(),
			commerceOrder.getShippingWithTaxAmount(),
			commerceOrder.getSubtotal(),
			commerceOrder.getSubtotalWithTaxAmount(),
			commerceOrder.getTaxAmount(), commerceOrder.getTotal(),
			commerceOrder.getTotalDiscountAmount(),
			commerceOrder.getTotalWithTaxAmount(), commerceContext, true);
	}

	private void _addOrUpdateCommerceOrderItem(
			CartItem cartItem, CommerceOrder commerceOrder,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws Exception {

		CPInstance cpInstance = null;

		if (cartItem.getSkuId() != null) {
			cpInstance = _cpInstanceLocalService.getCPInstance(
				cartItem.getSkuId());
		}

		SkuUnitOfMeasure skuUnitOfMeasure = cartItem.getSkuUnitOfMeasure();
		String skuUnitOfMeasureKey = StringPool.BLANK;

		if (skuUnitOfMeasure != null) {
			skuUnitOfMeasureKey = skuUnitOfMeasure.getKey();
		}

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceOrder.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		CommerceOrderItem commerceOrderItem = null;

		if (commerceOrderCheckoutConfiguration.showSeparateOrderItems() ||
			commerceOrderCheckoutConfiguration.multishippingEnabled()) {

			commerceOrderItem = _commerceOrderItemService.addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), cartItem.getOptions(),
				BigDecimalUtil.get(cartItem.getQuantity(), BigDecimal.ONE),
				GetterUtil.getLong(cartItem.getReplacedSkuId()),
				BigDecimal.ZERO, skuUnitOfMeasureKey, commerceContext,
				serviceContext);
		}
		else {
			commerceOrderItem =
				_commerceOrderItemService.addOrUpdateCommerceOrderItem(
					commerceOrder.getCommerceOrderId(),
					cpInstance.getCPInstanceId(), cartItem.getOptions(),
					BigDecimalUtil.get(cartItem.getQuantity(), BigDecimal.ONE),
					GetterUtil.getLong(cartItem.getReplacedSkuId()),
					BigDecimal.ZERO, skuUnitOfMeasureKey, commerceContext,
					serviceContext);
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

			_commerceOrderItemService.updateCommerceOrderItemInfo(
				commerceOrderItem.getCommerceOrderItemId(), shippingAddressId,
				deliveryGroupName, commerceOrderItem.getPrintedNote(),
				requestedDeliveryDateConfig.getMonth(),
				requestedDeliveryDateConfig.getDay(),
				requestedDeliveryDateConfig.getYear());
		}
		else {
			_commerceOrderItemService.updateCommerceOrderItemInfo(
				commerceOrderItem.getCommerceOrderItemId(), shippingAddressId,
				deliveryGroupName, commerceOrderItem.getPrintedNote());
		}
	}

	private void _addOrUpdateNestedResources(
			Cart cart, CommerceOrder commerceOrder,
			CommerceContext commerceContext)
		throws Exception {

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commerceOrder.getGroupId());

		// Order items

		CartItem[] orderItems = cart.getCartItems();

		if (orderItems != null) {
			CommerceChannelConfigurationUtil.validateGuestCheckout(
				commerceOrder.getCommerceOrderId());

			_commerceOrderItemService.deleteCommerceOrderItems(
				commerceOrder.getCommerceOrderId());

			for (CartItem cartItem : orderItems) {
				_addOrUpdateCommerceOrderItem(
					cartItem, commerceOrder, commerceContext, serviceContext);
			}

			commerceOrder = _commerceOrderService.recalculatePrice(
				commerceOrder.getCommerceOrderId(), commerceContext);
		}

		commerceOrder.setBillingAddressId(
			GetterUtil.get(cart.getBillingAddressId(), 0));
		commerceOrder.setShippingAddressId(
			GetterUtil.get(cart.getShippingAddressId(), 0));

		boolean useAsBilling = GetterUtil.get(cart.getUseAsBilling(), false);
		int type = CommerceAddressConstants.ADDRESS_TYPE_SHIPPING;

		if (useAsBilling) {
			type = CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING;
		}

		// Shipping Address

		Address shippingAddress = cart.getShippingAddress();

		if (shippingAddress != null) {
			commerceOrder = _addOrUpdateShippingAddress(
				commerceOrder, shippingAddress, type, commerceContext,
				serviceContext);
		}

		if (useAsBilling) {
			_commerceOrderEngine.updateCommerceOrder(
				commerceOrder.getExternalReferenceCode(),
				commerceOrder.getCommerceOrderId(),
				commerceOrder.getBillingAddressId(),
				commerceOrder.getCommerceShippingMethodId(),
				commerceOrder.getShippingAddressId(),
				commerceOrder.getAdvanceStatus(),
				commerceOrder.getCommercePaymentMethodKey(),
				commerceOrder.getName(), commerceOrder.getPurchaseOrderNumber(),
				commerceOrder.getShippingAmount(),
				commerceOrder.getShippingOptionName(),
				commerceOrder.getShippingWithTaxAmount(),
				commerceOrder.getSubtotal(),
				commerceOrder.getSubtotalWithTaxAmount(),
				commerceOrder.getTaxAmount(), commerceOrder.getTotal(),
				commerceOrder.getTotalDiscountAmount(),
				commerceOrder.getTotalWithTaxAmount(), commerceContext, true);
		}
		else {

			// Billing Address

			Address billingAddress = cart.getBillingAddress();

			if (billingAddress != null) {
				_addOrUpdateBillingAddress(
					commerceOrder, billingAddress,
					CommerceAddressConstants.ADDRESS_TYPE_BILLING,
					commerceContext, serviceContext);
			}
		}
	}

	private CommerceOrder _addOrUpdateShippingAddress(
			CommerceOrder commerceOrder, Address address, int type,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws Exception {

		if (commerceOrder.getShippingAddressId() > 0) {
			_updateCommerceOrderAddress(
				commerceOrder, address, type, serviceContext);
		}
		else {
			CommerceAddress commerceAddress = _addCommerceAddress(
				commerceOrder, address, type, serviceContext);

			commerceOrder.setShippingAddressId(
				commerceAddress.getCommerceAddressId());
		}

		return _commerceOrderEngine.updateCommerceOrder(
			commerceOrder.getExternalReferenceCode(),
			commerceOrder.getCommerceOrderId(),
			commerceOrder.getBillingAddressId(),
			commerceOrder.getCommerceShippingMethodId(),
			commerceOrder.getShippingAddressId(),
			commerceOrder.getAdvanceStatus(),
			commerceOrder.getCommercePaymentMethodKey(),
			commerceOrder.getName(), commerceOrder.getPurchaseOrderNumber(),
			commerceOrder.getShippingAmount(),
			commerceOrder.getShippingOptionName(),
			commerceOrder.getShippingWithTaxAmount(),
			commerceOrder.getSubtotal(),
			commerceOrder.getSubtotalWithTaxAmount(),
			commerceOrder.getTaxAmount(), commerceOrder.getTotal(),
			commerceOrder.getTotalDiscountAmount(),
			commerceOrder.getTotalWithTaxAmount(), commerceContext, true);
	}

	private Cart _checkoutOrder(CommerceOrder commerceOrder) throws Exception {
		Cart cart = _toCart(commerceOrder.getCommerceOrderId(), false);

		CommerceOrder finalCommerceOrder = commerceOrder;
		Cart finalCart = cart;

		cart.setCartItems(
			() -> _getValidatedCommerceOrderItems(
				finalCommerceOrder, finalCart));

		cart.setValid(() -> true);

		if (FeatureFlagManagerUtil.isEnabled(
				commerceOrder.getCompanyId(), "LPD-89850")) {

			CommerceAccountEntryValidationConfiguration
				commerceAccountEntryValidationConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAccountEntryValidationConfiguration.class,
						new GroupServiceSettingsLocator(
							commerceOrder.getGroupId(),
							CommerceConstants.
								SERVICE_NAME_COMMERCE_ACCOUNT_ENTRY_VALIDATION));

			String validationMode =
				commerceAccountEntryValidationConfiguration.validationMode();

			if (!Objects.equals(
					validationMode,
					CommerceAccountEntryValidationConstants.
						VALIDATION_MODE_DISABLED)) {

				for (AccountEntryValidatorResult accountEntryValidatorResult :
						_accountEntryValidatorRegistry.validate(
							_accountEntryLocalService.fetchAccountEntry(
								commerceOrder.getCommerceAccountId()),
							JSONUtil.put(
								"billingAddressId",
								commerceOrder.getBillingAddressId()
							).put(
								"commerceOrderId",
								commerceOrder.getCommerceOrderId()
							).put(
								"shippingAddressId",
								commerceOrder.getShippingAddressId()
							))) {

					if (_isAccountValidationResultValid(
							accountEntryValidatorResult, validationMode)) {

						continue;
					}

					cart.setValid(() -> false);

					if (Validator.isNotNull(
							accountEntryValidatorResult.getResultMessage())) {

						String errorMessage = _language.get(
							contextAcceptLanguage.getPreferredLocale(),
							accountEntryValidatorResult.getResultMessage());

						cart.setErrorMessages(
							() -> new String[] {errorMessage});
					}

					return cart;
				}
			}
		}

		try {
			commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
				commerceOrder, contextUser.getUserId());

			cart = _toCart(commerceOrder.getCommerceOrderId(), false);
		}
		catch (Exception exception) {
			cart.setValid(() -> false);

			if (exception.getCause() instanceof
					CommerceOrderBillingAddressException) {

				cart.setErrorMessages(
					() -> new String[] {"Invalid billing address"});
			}

			if (exception.getCause() instanceof
					CommerceOrderGuestCheckoutException) {

				cart.setErrorMessages(
					() -> new String[] {"Invalid guest checkout"});
			}

			if (exception.getCause() instanceof CommerceOrderPriceException) {
				cart.setErrorMessages(() -> new String[] {"Invalid price"});
			}

			if (exception.getCause() instanceof
					CommerceOrderShippingAddressException) {

				cart.setErrorMessages(
					() -> new String[] {"Invalid shipping address"});
			}

			if (exception.getCause() instanceof
					CommerceOrderShippingMethodException) {

				cart.setErrorMessages(
					() -> new String[] {"Invalid shipping method"});
			}

			if (exception.getCause() instanceof CommerceOrderStatusException) {
				cart.setErrorMessages(
					() -> new String[] {"Invalid cart status"});
			}
		}

		return cart;
	}

	private long[] _getCommerceAccountIds(long groupId) throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_commerceOrderModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), groupId,
				CommerceOrderActionKeys.MANAGE_ALL_ACCOUNTS)) {

			return null;
		}

		return _commerceAccountHelper.getUserCommerceAccountIds(
			contextUser.getUserId(), groupId);
	}

	private long _getCommerceOrderTypeId(Cart cart) throws Exception {
		if (cart.getOrderTypeId() != null) {
			return cart.getOrderTypeId();
		}

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					cart.getOrderTypeExternalReferenceCode(),
					contextCompany.getCompanyId());

		if (commerceOrderType == null) {
			return 0;
		}

		return commerceOrderType.getCommerceOrderTypeId();
	}

	private CommerceShippingFixedOption _getCommerceShippingFixedOption(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			String commerceShippingMethodEngineKey)
		throws Exception {

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethodEngineKey);

		List<CommerceShippingOption> commerceShippingOptions =
			commerceShippingEngine.getEnabledCommerceShippingOptions(
				commerceContext, commerceOrder,
				contextAcceptLanguage.getPreferredLocale());

		for (CommerceShippingOption commerceShippingOption :
				commerceShippingOptions) {

			if (StringUtil.equals(
					commerceOrder.getShippingOptionName(),
					commerceShippingOption.getKey())) {

				return _commerceShippingFixedOptionLocalService.
					fetchCommerceShippingFixedOption(
						commerceOrder.getCompanyId(),
						commerceShippingOption.getKey());
			}
		}

		return null;
	}

	private String _getOrderConfirmationCheckoutStepURL(
			CommerceOrder commerceOrder)
		throws Exception {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				contextHttpServletRequest,
				CommercePortletKeys.COMMERCE_CHECKOUT,
				PortletProvider.Action.VIEW)
		).setParameter(
			"checkoutStepName",
			() -> {
				CommerceCheckoutStep commerceCheckoutStep =
					_commerceCheckoutStepRegistry.getCommerceCheckoutStep(
						"order-confirmation");

				return commerceCheckoutStep.getName();
			}
		).setParameter(
			"commerceOrderUuid", commerceOrder.getUuid()
		).buildString();
	}

	private String _getPaymentURL(
			String callbackURL, CommerceOrder commerceOrder)
		throws Exception {

		_initThemeDisplay(commerceOrder);

		StringBundler sb = new StringBundler(14);

		sb.append(_portal.getPortalURL(contextHttpServletRequest));
		sb.append(_portal.getPathModule());
		sb.append(CharPool.SLASH);
		sb.append(CommercePaymentMethodConstants.SERVLET_PATH);
		sb.append("?groupId=");
		sb.append(commerceOrder.getGroupId());
		sb.append(StringPool.AMPERSAND);

		if (commerceOrder.isGuestOrder()) {
			sb.append("guestToken=");

			Key key = contextCompany.getKeyObj();

			sb.append(
				URLCodec.encodeURL(
					_encryptor.encrypt(
						key,
						String.valueOf(commerceOrder.getCommerceOrderId()))));

			sb.append(StringPool.AMPERSAND);
		}

		sb.append("nextStep=");

		if (Validator.isNotNull(callbackURL)) {
			sb.append(callbackURL);
		}
		else {
			sb.append(
				URLCodec.encodeURL(
					_getOrderConfirmationCheckoutStepURL(commerceOrder)));
		}

		sb.append("&uuid=");
		sb.append(commerceOrder.getUuid());

		return sb.toString();
	}

	private long _getRegionId(
			CommerceAddress commerceAddress, Country country, Address address)
		throws Exception {

		if (Validator.isNull(address.getRegionISOCode()) &&
			(commerceAddress != null)) {

			return commerceAddress.getRegionId();
		}

		if (Validator.isNull(address.getRegionISOCode()) || (country == null)) {
			return 0;
		}

		Region region = _regionLocalService.getRegion(
			country.getCountryId(), address.getRegionISOCode());

		return region.getRegionId();
	}

	private CartItem[] _getValidatedCommerceOrderItems(
			CommerceOrder commerceOrder, Cart cart)
		throws Exception {

		List<CartItem> cartItems = new ArrayList<>();

		Map<Long, List<CommerceOrderValidatorResult>>
			commerceOrderValidatorResults =
				_commerceOrderValidatorRegistry.
					getCommerceOrderValidatorResults(null, commerceOrder);

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			CartItem cartItem = _cartItemDTOConverter.toDTO(
				new CartItemDTOConverterContext(
					commerceOrder.getCommerceAccountId(),
					commerceOrderItem.getCommerceOrderItemId(),
					contextAcceptLanguage.getPreferredLocale()));

			List<CommerceOrderValidatorResult>
				commerceOrderItemValidatorResults =
					commerceOrderValidatorResults.get(
						commerceOrderItem.getCommerceOrderItemId());

			if (commerceOrderItemValidatorResults != null) {
				boolean cartItemValid = true;

				for (CommerceOrderValidatorResult commerceOrderValidatorResult :
						commerceOrderItemValidatorResults) {

					if (!commerceOrderValidatorResult.isValid()) {
						cartItemValid = false;

						break;
					}
				}

				boolean finalCartItemValid = cartItemValid;

				cart.setValid(() -> finalCartItemValid);

				cartItem.setErrorMessages(
					() -> transformToArray(
						commerceOrderItemValidatorResults,
						CommerceOrderValidatorResult::getLocalizedMessage,
						String.class));
				cartItem.setValid(() -> finalCartItemValid);
			}

			cartItems.add(cartItem);
		}

		return cartItems.toArray(new CartItem[0]);
	}

	private void _initThemeDisplay(CommerceOrder commerceOrder)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return;
		}

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		themeDisplay = (ThemeDisplay)contextHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		themeDisplay.setScopeGroupId(commerceChannel.getSiteGroupId());
	}

	private boolean _isAccountValidationResultValid(
		AccountEntryValidatorResult accountEntryValidatorResult,
		String validationMode) {

		if (Objects.equals(
				validationMode,
				CommerceAccountEntryValidationConstants.
					VALIDATION_MODE_ALLOW_ALL)) {

			return true;
		}

		if (!accountEntryValidatorResult.isValid() ||
			(Objects.equals(
				validationMode,
				CommerceAccountEntryValidationConstants.
					VALIDATION_MODE_ALLOW_SUCCESSES_ONLY) &&
			 AccountEntryValidatorConstants.RESULT_WARNING.equals(
				 accountEntryValidatorResult.getResultStatus()))) {

			return false;
		}

		return true;
	}

	private boolean _isValidDeliveryTerm(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			CommerceShippingMethod commerceShippingMethod, long deliveryTermId)
		throws Exception {

		if ((commerceShippingMethod == null) || (deliveryTermId == 0) ||
			!commerceOrder.isOpen()) {

			return true;
		}

		CommerceShippingFixedOption commerceShippingFixedOption =
			_getCommerceShippingFixedOption(
				commerceContext, commerceOrder,
				commerceShippingMethod.getEngineKey());

		if (commerceShippingFixedOption == null) {
			return true;
		}

		List<CommerceTermEntry> deliveryCommerceTermEntries =
			_commerceTermEntryLocalService.getDeliveryCommerceTermEntries(
				commerceOrder.getCompanyId(),
				commerceOrder.getCommerceOrderTypeId(),
				commerceShippingFixedOption.getCommerceShippingFixedOptionId());

		for (CommerceTermEntry commerceTermEntry :
				deliveryCommerceTermEntries) {

			if (commerceTermEntry.getCommerceTermEntryId() == deliveryTermId) {
				return true;
			}
		}

		return false;
	}

	private boolean _isValidPaymentTerm(
			CommerceOrder commerceOrder, long paymentTermId)
		throws Exception {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(),
					commerceOrder.getCommercePaymentMethodKey());

		if (commercePaymentMethodGroupRel == null) {
			return true;
		}

		List<CommerceTermEntry> paymentCommerceTermEntries =
			_commerceTermEntryLocalService.getPaymentCommerceTermEntries(
				commerceOrder.getCompanyId(),
				commerceOrder.getCommerceOrderTypeId(),
				commercePaymentMethodGroupRel.
					getCommercePaymentMethodGroupRelId());

		for (CommerceTermEntry commerceTermEntry : paymentCommerceTermEntries) {
			if (commerceTermEntry.getCommerceTermEntryId() == paymentTermId) {
				return true;
			}
		}

		return false;
	}

	private Cart _toCart(long commerceOrderId, boolean checkOrderErrors)
		throws Exception {

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				commerceOrderId, contextAcceptLanguage.getPreferredLocale());

		defaultDTOConverterContext.setAttribute(
			"checkOrderErrors", checkOrderErrors);

		return _cartDTOConverter.toDTO(defaultDTOConverterContext);
	}

	private void _updateCommerceOrderAddress(
			CommerceOrder commerceOrder, Address address, int type,
			ServiceContext serviceContext)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.getCommerceAddress(
				commerceOrder.getShippingAddressId());

		Country country = commerceAddress.getCountry();

		_commerceAddressService.updateCommerceAddress(
			commerceAddress.getExternalReferenceCode(),
			commerceAddress.getCommerceAddressId(), country.getCountryId(),
			_getRegionId(commerceAddress, country, address), address.getCity(),
			GetterUtil.get(
				address.getDescription(), commerceAddress.getDescription()),
			address.getName(),
			GetterUtil.get(
				address.getPhoneNumber(), commerceAddress.getPhoneNumber()),
			address.getStreet1(),
			GetterUtil.get(address.getStreet2(), commerceAddress.getStreet2()),
			GetterUtil.get(address.getStreet3(), commerceAddress.getStreet3()),
			GetterUtil.get(address.getSubtype(), commerceAddress.getSubtype()),
			type, GetterUtil.get(address.getZip(), commerceAddress.getZip()),
			serviceContext);
	}

	private void _updateOrder(CommerceOrder commerceOrder, Cart cart)
		throws Exception {

		long billingAddressId = GetterUtil.getLong(cart.getBillingAddressId());

		if (billingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						cart.getBillingAddressExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceAddress == null) {
				billingAddressId = commerceOrder.getBillingAddressId();
			}
			else {
				billingAddressId = commerceAddress.getCommerceAddressId();
			}
		}

		long commerceShippingMethodId =
			commerceOrder.getCommerceShippingMethodId();

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.fetchCommerceShippingMethod(
				commerceOrder.getGroupId(), cart.getShippingMethod());

		if (commerceShippingMethod != null) {
			commerceShippingMethodId =
				commerceShippingMethod.getCommerceShippingMethodId();
		}

		long shippingAddressId = GetterUtil.getLong(
			cart.getShippingAddressId());

		if (shippingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						cart.getShippingAddressExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceAddress == null) {
				shippingAddressId = commerceOrder.getShippingAddressId();
			}
			else {
				shippingAddressId = commerceAddress.getCommerceAddressId();
			}
		}

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceOrder.getCommerceAccountId(), commerceOrder.getGroupId(),
			cart.getCurrencyCode(), commerceOrder.getCommerceOrderId(),
			contextCompany.getCompanyId());

		_commerceOrderEngine.updateCommerceOrder(
			commerceOrder.getExternalReferenceCode(),
			commerceOrder.getCommerceOrderId(), billingAddressId,
			commerceShippingMethodId, shippingAddressId,
			commerceOrder.getAdvanceStatus(),
			GetterUtil.get(
				cart.getPaymentMethod(),
				commerceOrder.getCommercePaymentMethodKey()),
			GetterUtil.getString(cart.getName(), commerceOrder.getName()),
			GetterUtil.get(
				cart.getPurchaseOrderNumber(),
				commerceOrder.getPurchaseOrderNumber()),
			commerceOrder.getShippingAmount(),
			GetterUtil.get(
				cart.getShippingOption(),
				commerceOrder.getShippingOptionName()),
			commerceOrder.getShippingWithTaxAmount(),
			commerceOrder.getSubtotal(),
			commerceOrder.getSubtotalWithTaxAmount(),
			commerceOrder.getTaxAmount(), commerceOrder.getTotal(),
			commerceOrder.getTotalDiscountAmount(),
			commerceOrder.getTotalWithTaxAmount(), commerceContext, true);

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commerceOrder.getGroupId());

		if (cart.getRequestedDeliveryDate() != null) {
			Calendar requestedDeliveryDateCalendar =
				CalendarFactoryUtil.getCalendar(serviceContext.getTimeZone());

			requestedDeliveryDateCalendar.setTime(
				cart.getRequestedDeliveryDate());

			DateConfig requestedDeliveryDateConfig = new DateConfig(
				requestedDeliveryDateCalendar);

			_commerceOrderService.updateInfo(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.getString(
					cart.getPrintedNote(), commerceOrder.getPrintedNote()),
				requestedDeliveryDateConfig.getMonth(),
				requestedDeliveryDateConfig.getDay(),
				requestedDeliveryDateConfig.getYear(),
				requestedDeliveryDateConfig.getHour(),
				requestedDeliveryDateConfig.getMinute(), serviceContext);
		}
		else {
			_commerceOrderService.updatePrintedNote(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.getString(
					cart.getPrintedNote(), commerceOrder.getPrintedNote()));
		}

		Map<String, ?> customFields = cart.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrder.class,
				commerceOrder.getPrimaryKey(), customFields);
		}

		long deliveryTermId = GetterUtil.getLong(cart.getDeliveryTermId());

		if (!_isValidDeliveryTerm(
				commerceContext, commerceOrder, commerceShippingMethod,
				deliveryTermId)) {

			deliveryTermId = GetterUtil.getLong(
				commerceOrder.getDeliveryCommerceTermEntryId());
		}

		long paymentTermId = GetterUtil.getLong(cart.getPaymentTermId());

		if (!_isValidPaymentTerm(commerceOrder, paymentTermId)) {
			paymentTermId = GetterUtil.getLong(
				commerceOrder.getPaymentCommerceTermEntryId());
		}

		commerceOrder = _commerceOrderService.updateTermsAndConditions(
			commerceOrder.getCommerceOrderId(), deliveryTermId, paymentTermId,
			contextAcceptLanguage.getPreferredLanguageId());

		_addOrUpdateNestedResources(cart, commerceOrder, commerceContext);
	}

	private static final EntityModel _entityModel = new CartEntityModel();

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryValidatorRegistry _accountEntryValidatorRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.CartDTOConverter)"
	)
	private DTOConverter<CommerceOrder, Cart> _cartDTOConverter;

	@Reference(target = DTOConverterConstants.CART_ITEM_DTO_CONVERTER)
	private DTOConverter<CommerceOrderItem, CartItem> _cartItemDTOConverter;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCheckoutStepRegistry _commerceCheckoutStepRegistry;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommerceOrderValidatorRegistry _commerceOrderValidatorRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CountryService _countryService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Encryptor _encryptor;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}