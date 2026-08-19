/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.BillingAddress;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.dto.v1_0.ShippingAddress;
import com.liferay.headless.commerce.admin.order.internal.odata.entity.v1_0.OrderEntityModel;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.BillingAddressUtil;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.OrderItemUtil;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.ShippingAddressUtil;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.ActionUtil;
import com.liferay.headless.commerce.core.util.CommerceCurrencyUtil;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.core.util.ExpandoUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/order.properties",
	scope = ServiceScope.PROTOTYPE, service = OrderResource.class
)
public class OrderResourceImpl extends BaseOrderResourceImpl {

	@Override
	public Response deleteOrder(Long id) throws Exception {
		_commerceOrderService.deleteCommerceOrder(id);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response deleteOrderByExternalReferenceCode(
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
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Order getOrder(Long id) throws Exception {
		return _toOrder(
			GetterUtil.getLong(id), contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo,
			_getActions(
				_commerceOrderService.getCommerceOrder(
					GetterUtil.getLong(id))));
	}

	@Override
	public Order getOrderByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return _toOrder(
			commerceOrder.getCommerceOrderId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commerceOrder));
	}

	@Override
	public Page<Order> getOrdersPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getOrdersPage(
			contextCompany.getCompanyId(), filter, pagination, search, sorts,
			document -> _toOrder(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)),
				contextAcceptLanguage.getPreferredLocale(),
				contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
				contextUriInfo,
				_getActions(
					_commerceOrderService.getCommerceOrder(
						GetterUtil.getLong(
							GetterUtil.getLong(
								document.get(Field.ENTRY_CLASS_PK)))))),
			true);
	}

	@Override
	public Order patchOrder(Long id, Order order) throws Exception {
		CommerceOrder commerceOrder = _updateOrder(
			_commerceOrderService.getCommerceOrder(id), order);

		return _toOrder(
			commerceOrder.getCommerceOrderId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commerceOrder));
	}

	@Override
	public Order patchOrderByExternalReferenceCode(
			String externalReferenceCode, Order order)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		commerceOrder = _updateOrder(commerceOrder, order);

		return _toOrder(
			commerceOrder.getCommerceOrderId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commerceOrder));
	}

	@Override
	public Order postOrder(Order order) throws Exception {
		CommerceOrder commerceOrder = _addOrUpdateOrder(
			order.getExternalReferenceCode(), order);

		return _toOrder(
			commerceOrder.getCommerceOrderId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commerceOrder));
	}

	@Override
	public Order putOrderByExternalReferenceCode(
			String externalReferenceCode, Order order)
		throws Exception {

		CommerceOrder commerceOrder = _addOrUpdateOrder(
			externalReferenceCode, order);

		return _toOrder(
			commerceOrder.getCommerceOrderId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commerceOrder));
	}

	private Map<String, String> _addAction(
			String actionId, long commerceOrderId, UriInfo uriInfo,
			String methodName, Class<?> clazz)
		throws Exception {

		if (!_commerceOrderModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), commerceOrderId,
				actionId)) {

			return null;
		}

		return HashMapBuilder.put(
			"href",
			() -> {
				UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();

				return uriBuilder.path(
					ActionUtil.getVersion(uriInfo)
				).path(
					clazz.getSuperclass(), methodName
				).toTemplate();
			}
		).put(
			"method",
			ActionUtil.getHttpMethodName(
				clazz, ActionUtil.getMethod(clazz, methodName))
		).build();
	}

	private CommerceOrder _addOrUpdateOrder(
			String externalReferenceCode, Order order)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				order.getChannelId());

		long billingAddressId = GetterUtil.getLong(order.getBillingAddressId());

		if (billingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						order.getBillingAddressExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceAddress != null) {
				billingAddressId = commerceAddress.getCommerceAddressId();
			}
		}

		AccountEntry accountEntry;

		if (order.getAccountId() != null) {
			accountEntry = _accountEntryService.getAccountEntry(
				order.getAccountId());
		}
		else {
			accountEntry =
				_accountEntryService.getAccountEntryByExternalReferenceCode(
					order.getAccountExternalReferenceCode(),
					commerceChannel.getCompanyId());
		}

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.getCommerceCurrency(
				commerceChannel.getCompanyId(), order.getCurrencyCode(),
				order.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(order.getCurrencyId()));

		long commerceShippingMethodId = 0;

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodService.fetchCommerceShippingMethod(
				commerceChannel.getGroupId(), order.getShippingMethod());

		if (commerceShippingMethod != null) {
			commerceShippingMethodId =
				commerceShippingMethod.getCommerceShippingMethodId();
		}

		long shippingAddressId = GetterUtil.getLong(
			order.getShippingAddressId());

		if (shippingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						order.getShippingAddressExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceAddress != null) {
				shippingAddressId = commerceAddress.getCommerceAddressId();
			}
		}

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commerceChannel.getGroupId());

		CommerceOrder commerceOrder =
			_commerceOrderService.addOrUpdateCommerceOrder(
				externalReferenceCode, commerceChannel.getGroupId(),
				billingAddressId, accountEntry.getAccountEntryId(),
				commerceCurrency.getCode(), _getCommerceOrderTypeId(order),
				commerceShippingMethodId, shippingAddressId,
				order.getAdvanceStatus(), order.getPaymentMethod(),
				GetterUtil.getString(order.getName()),
				GetterUtil.getInteger(
					order.getOrderStatus(),
					CommerceOrderConstants.ORDER_STATUS_PENDING),
				GetterUtil.getInteger(
					order.getPaymentStatus(),
					CommerceOrderPaymentConstants.STATUS_PENDING),
				order.getPurchaseOrderNumber(), order.getShippingAmount(),
				order.getShippingOption(), order.getShippingWithTaxAmount(),
				order.getSubtotal(), order.getSubtotalWithTaxAmount(),
				order.getTaxAmount(), order.getTotal(),
				order.getTotalWithTaxAmount(),
				_commerceContextFactory.create(
					accountEntry.getAccountEntryId(),
					commerceChannel.getGroupId(), null, 0,
					contextCompany.getCompanyId()),
				serviceContext);

		// Order date

		if (order.getOrderDate() != null) {
			Calendar orderDateCalendar = CalendarFactoryUtil.getCalendar(
				serviceContext.getTimeZone());

			orderDateCalendar.setTime(order.getOrderDate());

			DateConfig orderDate = new DateConfig(orderDateCalendar);

			_commerceOrderService.updateOrderDate(
				commerceOrder.getCommerceOrderId(), orderDate.getMonth(),
				orderDate.getDay(), orderDate.getYear(), orderDate.getHour(),
				orderDate.getMinute(), serviceContext);
		}

		// Requested delivery date

		if (order.getRequestedDeliveryDate() != null) {
			Calendar requestedDeliveryDateCalendar =
				CalendarFactoryUtil.getCalendar(serviceContext.getTimeZone());

			requestedDeliveryDateCalendar.setTime(
				order.getRequestedDeliveryDate());

			DateConfig requestedDeliveryDate = new DateConfig(
				requestedDeliveryDateCalendar);

			_commerceOrderService.updateInfo(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.getString(
					order.getPrintedNote(), commerceOrder.getPrintedNote()),
				requestedDeliveryDate.getMonth(),
				requestedDeliveryDate.getDay(), requestedDeliveryDate.getYear(),
				requestedDeliveryDate.getHour(),
				requestedDeliveryDate.getMinute(), serviceContext);
		}
		else {

			// Printed note

			_commerceOrderService.updatePrintedNote(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.getString(
					order.getPrintedNote(), commerceOrder.getPrintedNote()));
		}

		// Terms and conditions

		long deliveryTermId = GetterUtil.getLong(order.getDeliveryTermId());

		if (deliveryTermId == 0) {
			CommerceTermEntry commerceTermEntry =
				_commerceTermEntryLocalService.
					fetchCommerceTermEntryByExternalReferenceCode(
						order.getDeliveryTermExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceTermEntry != null) {
				deliveryTermId = commerceTermEntry.getCommerceTermEntryId();
			}
		}

		long paymentTermId = GetterUtil.getLong(order.getPaymentTermId());

		if (paymentTermId == 0) {
			CommerceTermEntry commerceTermEntry =
				_commerceTermEntryLocalService.
					fetchCommerceTermEntryByExternalReferenceCode(
						order.getPaymentTermExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceTermEntry != null) {
				paymentTermId = commerceTermEntry.getCommerceTermEntryId();
			}
		}

		if ((deliveryTermId > 0) || (paymentTermId > 0)) {
			_commerceOrderService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(), deliveryTermId,
				paymentTermId, contextAcceptLanguage.getPreferredLanguageId());
		}

		// Expando

		Map<String, ?> customFields = order.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrder.class,
				commerceOrder.getPrimaryKey(), customFields);
		}

		// Update nested resources

		return _updateNestedResources(order, commerceOrder, serviceContext);
	}

	private Map<String, Map<String, String>> _getActions(
			CommerceOrder commerceOrder)
		throws Exception {

		if (contextUriInfo == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				ActionKeys.DELETE, commerceOrder.getCommerceOrderId(),
				contextUriInfo, "deleteOrder", getClass())
		).put(
			"get",
			_addAction(
				ActionKeys.VIEW, commerceOrder.getCommerceOrderId(),
				contextUriInfo, "getOrder", getClass())
		).put(
			"update",
			_addAction(
				ActionKeys.UPDATE, commerceOrder.getCommerceOrderId(),
				contextUriInfo, "patchOrder", getClass())
		).build();
	}

	private long _getCommerceOrderTypeId(Order order) throws Exception {
		if (order.getOrderTypeId() != null) {
			return order.getOrderTypeId();
		}

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					order.getOrderTypeExternalReferenceCode(),
					contextCompany.getCompanyId());

		if (commerceOrderType == null) {
			return 0;
		}

		return commerceOrderType.getCommerceOrderTypeId();
	}

	private Map<String, Serializable> _getExpandoBridgeAttributes(
		OrderItem orderItem) {

		return CustomFieldsUtil.toMap(
			CommerceOrderItem.class.getName(), contextCompany.getCompanyId(),
			orderItem.getCustomFields(),
			contextAcceptLanguage.getPreferredLocale());
	}

	private String[] _getOrderItemExternalReferenceCodes(
		OrderItem[] orderItems) {

		Set<String> orderItemExternalReferenceCodes = new HashSet<>();

		for (OrderItem orderItem : orderItems) {
			String externalReferenceCode = orderItem.getExternalReferenceCode();

			if (Objects.nonNull(externalReferenceCode)) {
				orderItemExternalReferenceCodes.add(externalReferenceCode);
			}
		}

		if (orderItemExternalReferenceCodes.isEmpty()) {
			return null;
		}

		return transformToArray(
			orderItemExternalReferenceCodes,
			orderItemExternalReferenceCode -> orderItemExternalReferenceCode,
			String.class);
	}

	private Long[] _getOrderItemIds(OrderItem[] orderItems) {
		Set<Long> orderItemIds = new HashSet<>();

		for (OrderItem orderItem : orderItems) {
			Long id = orderItem.getId();

			if (Objects.nonNull(id)) {
				orderItemIds.add(id);
			}
		}

		if (orderItemIds.isEmpty()) {
			return new Long[] {0L};
		}

		return transformToArray(
			orderItemIds, orderItemId -> orderItemId, Long.class);
	}

	private Page<Order> _getOrdersPage(
			long companyId, Filter filter, Pagination pagination, String search,
			Sort[] sorts,
			UnsafeFunction<Document, Order, Exception> transformUnsafeFunction,
			boolean useSearchResultPermissionFilter)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceOrder.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"useSearchResultPermissionFilter",
					useSearchResultPermissionFilter);
				searchContext.setCompanyId(companyId);

				long[] commerceChannelGroupIds = transformToLongArray(
					_commerceChannelLocalService.getCommerceChannels(companyId),
					CommerceChannel::getGroupId);

				if ((commerceChannelGroupIds != null) &&
					(commerceChannelGroupIds.length > 0)) {

					searchContext.setGroupIds(commerceChannelGroupIds);
				}

				PermissionChecker permissionChecker =
					PermissionThreadLocal.getPermissionChecker();

				if (permissionChecker.isCompanyAdmin() ||
					!_portletResourcePermission.contains(
						permissionChecker, contextCompany.getGroupId(),
						ActionKeys.ACCESS_IN_CONTROL_PANEL)) {

					return;
				}

				PortletResourcePermission portletResourcePermission =
					_commerceOrderModelResourcePermission.
						getPortletResourcePermission();

				List<AccountEntry> accountEntries = transform(
					_accountEntryLocalService.getUserAccountEntries(
						contextUser.getUserId(),
						AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
						StringPool.BLANK,
						new String[] {
							AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS
						},
						QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					accountEntry -> {
						if (portletResourcePermission.contains(
								permissionChecker,
								accountEntry.getAccountEntryGroupId(),
								CommerceOrderActionKeys.
									MANAGE_ACCOUNTS_SCOPED_COMMERCE_ORDERS)) {

							return accountEntry;
						}

						return null;
					});

				if (ListUtil.isNotEmpty(accountEntries)) {
					searchContext.setAttribute(
						"commerceAccountIds",
						transformToLongArray(
							accountEntries,
							accountEntry -> accountEntry.getAccountEntryId()));
					searchContext.setUserId(0);
				}
			},
			sorts, transformUnsafeFunction);
	}

	private Order _toOrder(
			long commerceOrderId, Locale locale, boolean acceptAllLanguages,
			User contextUser, UriInfo contextUriInfo,
			Map<String, Map<String, String>> actions)
		throws Exception {

		return _orderDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				acceptAllLanguages, actions, _dtoConverterRegistry,
				commerceOrderId, locale, contextUriInfo, contextUser));
	}

	private CommerceOrder _updateNestedResources(
			Order order, CommerceOrder commerceOrder,
			ServiceContext serviceContext)
		throws Exception {

		// Order items

		OrderItem[] orderItems = order.getOrderItems();

		if (orderItems != null) {
			_commerceOrderItemService.deleteMissingCommerceOrderItems(
				commerceOrder.getCommerceOrderId(),
				_getOrderItemIds(orderItems),
				_getOrderItemExternalReferenceCodes(orderItems));

			for (OrderItem orderItem : orderItems) {
				CommerceOrderItem commerceOrderItem =
					OrderItemUtil.addOrUpdateCommerceOrderItem(
						_cpInstanceService, _cpInstanceUnitOfMeasureService,
						_commerceAddressService, _commerceOrderItemService,
						_commerceOrderModelResourcePermission, orderItem,
						commerceOrder,
						_commerceContextFactory.create(
							commerceOrder.getCommerceAccountId(),
							commerceOrder.getGroupId(), null,
							commerceOrder.getCommerceOrderId(),
							contextCompany.getCompanyId()),
						_serviceContextHelper.getServiceContext(
							commerceOrder.getGroupId()));

				Map<String, ?> expandoAttributes = _getExpandoBridgeAttributes(
					orderItem);

				if (MapUtil.isNotEmpty(expandoAttributes)) {
					ExpandoUtil.updateExpando(
						contextCompany.getCompanyId(), CommerceOrderItem.class,
						commerceOrderItem.getPrimaryKey(), expandoAttributes);
				}
			}
		}

		// Billing Address

		BillingAddress billingAddress = order.getBillingAddress();

		if (billingAddress != null) {
			commerceOrder = BillingAddressUtil.addOrUpdateBillingAddress(
				billingAddress, _commerceAddressService, commerceOrder,
				_commerceOrderService, _countryService, serviceContext);
		}

		// Shipping Address

		ShippingAddress shippingAddress = order.getShippingAddress();

		if (shippingAddress != null) {
			commerceOrder = ShippingAddressUtil.addOrUpdateShippingAddress(
				_commerceAddressService, commerceOrder, _commerceOrderService,
				_countryService, shippingAddress, serviceContext);
		}

		return commerceOrder;
	}

	private CommerceOrder _updateOrder(CommerceOrder commerceOrder, Order order)
		throws Exception {

		long billingAddressId = GetterUtil.getLong(order.getBillingAddressId());

		if (billingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						order.getBillingAddressExternalReferenceCode(),
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
			_commerceShippingMethodService.fetchCommerceShippingMethod(
				commerceOrder.getGroupId(), order.getShippingMethod());

		if (commerceShippingMethod != null) {
			commerceShippingMethodId =
				commerceShippingMethod.getCommerceShippingMethodId();
		}

		long shippingAddressId = GetterUtil.getLong(
			order.getShippingAddressId());

		if (shippingAddressId == 0) {
			CommerceAddress commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						order.getShippingAddressExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceAddress == null) {
				shippingAddressId = commerceOrder.getShippingAddressId();
			}
			else {
				shippingAddressId = commerceAddress.getCommerceAddressId();
			}
		}

		commerceOrder = _commerceOrderEngine.updateCommerceOrder(
			GetterUtil.getString(
				order.getExternalReferenceCode(),
				commerceOrder.getExternalReferenceCode()),
			commerceOrder.getCommerceOrderId(), billingAddressId,
			commerceShippingMethodId, shippingAddressId,
			GetterUtil.getString(
				order.getAdvanceStatus(), commerceOrder.getAdvanceStatus()),
			GetterUtil.getString(
				order.getPaymentMethod(),
				commerceOrder.getCommercePaymentMethodKey()),
			GetterUtil.getString(order.getName(), commerceOrder.getName()),
			GetterUtil.getString(
				order.getPurchaseOrderNumber(),
				commerceOrder.getPurchaseOrderNumber()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingAmount(), commerceOrder.getShippingAmount()),
			GetterUtil.getString(
				order.getShippingOption(),
				commerceOrder.getShippingOptionName()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingWithTaxAmount(),
				commerceOrder.getShippingWithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotal(), commerceOrder.getSubtotal()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalWithTaxAmount(),
				commerceOrder.getSubtotalWithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTaxAmount(), commerceOrder.getTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotal(), commerceOrder.getTotal()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountAmount(),
				commerceOrder.getTotalDiscountAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalWithTaxAmount(),
				commerceOrder.getTotalWithTaxAmount()),
			_commerceContextFactory.create(
				GetterUtil.getLong(
					order.getAccountId(), commerceOrder.getCommerceAccountId()),
				commerceOrder.getGroupId(), null, 0,
				contextCompany.getCompanyId()),
			false);

		_commerceOrderService.updateCommerceOrderPrices(
			commerceOrder.getCommerceOrderId(),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingAmount(), commerceOrder.getShippingAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountAmount(),
				commerceOrder.getShippingDiscountAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel1(),
				commerceOrder.getShippingDiscountPercentageLevel1()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel2(),
				commerceOrder.getShippingDiscountPercentageLevel2()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel3(),
				commerceOrder.getShippingDiscountPercentageLevel3()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel4(),
				commerceOrder.getShippingDiscountPercentageLevel4()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel1WithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel1WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel2WithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel2WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel3WithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel3WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountPercentageLevel4WithTaxAmount(),
				commerceOrder.
					getShippingDiscountPercentageLevel4WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingDiscountWithTaxAmount(),
				commerceOrder.getShippingDiscountWithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getShippingWithTaxAmount(),
				commerceOrder.getShippingWithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotal(), commerceOrder.getSubtotal()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountAmount(),
				commerceOrder.getSubtotalDiscountAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel1(),
				commerceOrder.getSubtotalDiscountPercentageLevel1()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel2(),
				commerceOrder.getSubtotalDiscountPercentageLevel2()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel3(),
				commerceOrder.getTotalDiscountPercentageLevel3()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel4(),
				commerceOrder.getSubtotalDiscountPercentageLevel4()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel1WithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel1WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel2WithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel2WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel3WithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel3WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountPercentageLevel4WithTaxAmount(),
				commerceOrder.
					getSubtotalDiscountPercentageLevel4WithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalDiscountWithTaxAmount(),
				commerceOrder.getSubtotalDiscountWithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getSubtotalWithTaxAmount(),
				commerceOrder.getSubtotalWithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTaxAmount(), commerceOrder.getTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotal(), commerceOrder.getTotal()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountAmount(),
				commerceOrder.getTotalDiscountAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel1(),
				commerceOrder.getTotalDiscountPercentageLevel1()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel2(),
				commerceOrder.getTotalDiscountPercentageLevel2()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel3(),
				commerceOrder.getTotalDiscountPercentageLevel3()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel4(),
				commerceOrder.getTotalDiscountPercentageLevel4()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel1(),
				commerceOrder.getTotalDiscountPercentageLevel1()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel2(),
				commerceOrder.getTotalDiscountPercentageLevel2()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel3(),
				commerceOrder.getTotalDiscountPercentageLevel3()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountPercentageLevel4(),
				commerceOrder.getTotalDiscountPercentageLevel4()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalDiscountWithTaxAmount(),
				commerceOrder.getTotalDiscountWithTaxAmount()),
			(BigDecimal)GetterUtil.getNumber(
				order.getTotalWithTaxAmount(),
				commerceOrder.getTotalWithTaxAmount()));

		if (order.getRequestedDeliveryDate() != null) {
			ServiceContext serviceContext =
				_serviceContextHelper.getServiceContext(
					commerceOrder.getGroupId());

			Calendar requestedDeliveryDateCalendar =
				CalendarFactoryUtil.getCalendar(serviceContext.getTimeZone());

			requestedDeliveryDateCalendar.setTime(
				order.getRequestedDeliveryDate());

			DateConfig requestedDeliveryDate = new DateConfig(
				requestedDeliveryDateCalendar);

			commerceOrder = _commerceOrderService.updateInfo(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.getString(
					order.getPrintedNote(), commerceOrder.getPrintedNote()),
				requestedDeliveryDate.getMonth(),
				requestedDeliveryDate.getDay(), requestedDeliveryDate.getYear(),
				requestedDeliveryDate.getHour(),
				requestedDeliveryDate.getMinute(), serviceContext);
		}
		else {
			commerceOrder = _commerceOrderService.updatePrintedNote(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.getString(
					order.getPrintedNote(), commerceOrder.getPrintedNote()));
		}

		if ((order.getPaymentStatus() != null) &&
			(order.getPaymentStatus() != commerceOrder.getPaymentStatus())) {

			commerceOrder = _commercePaymentEngine.updateOrderPaymentStatus(
				commerceOrder.getCommerceOrderId(),
				GetterUtil.getInteger(
					order.getPaymentStatus(), commerceOrder.getPaymentStatus()),
				commerceOrder.getTransactionId(), StringPool.BLANK);
		}

		long deliveryTermId = GetterUtil.getLong(order.getDeliveryTermId());

		if (deliveryTermId == 0) {
			CommerceTermEntry commerceTermEntry =
				_commerceTermEntryLocalService.
					fetchCommerceTermEntryByExternalReferenceCode(
						order.getDeliveryTermExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceTermEntry != null) {
				deliveryTermId = commerceTermEntry.getCommerceTermEntryId();
			}
		}

		long paymentTermId = GetterUtil.getLong(order.getPaymentTermId());

		if (paymentTermId == 0) {
			CommerceTermEntry commerceTermEntry =
				_commerceTermEntryLocalService.
					fetchCommerceTermEntryByExternalReferenceCode(
						order.getPaymentTermExternalReferenceCode(),
						contextCompany.getCompanyId());

			if (commerceTermEntry != null) {
				paymentTermId = commerceTermEntry.getCommerceTermEntryId();
			}
		}

		if ((deliveryTermId > 0) || (paymentTermId > 0)) {
			_commerceOrderService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(), deliveryTermId,
				paymentTermId, contextAcceptLanguage.getPreferredLanguageId());
		}

		Map<String, ?> customFields = order.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrder.class,
				commerceOrder.getPrimaryKey(), customFields);
		}

		commerceOrder = _updateNestedResources(
			order, commerceOrder,
			_serviceContextHelper.getServiceContext(
				commerceOrder.getGroupId()));

		if (Validator.isNotNull(order.getOrderStatus()) &&
			(commerceOrder.getOrderStatus() != order.getOrderStatus())) {

			commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
				commerceOrder, order.getOrderStatus(), contextUser.getUserId(),
				true);
		}

		return commerceOrder;
	}

	private static final EntityModel _entityModel = new OrderEntityModel();

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

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
	private CommercePaymentEngine _commercePaymentEngine;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private CountryService _countryService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private CPInstanceUnitOfMeasureService _cpInstanceUnitOfMeasureService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter.OrderDTOConverter)"
	)
	private DTOConverter<CommerceOrder, Order> _orderDTOConverter;

	@Reference(
		target = "(resource.name=" + CommercePortletKeys.COMMERCE_ORDER + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}