/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.exception.NoSuchOrderItemException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.admin.order.internal.dto.v1_0.util.CustomFieldsUtil;
import com.liferay.headless.commerce.admin.order.internal.odata.entity.v1_0.OrderItemEntityModel;
import com.liferay.headless.commerce.admin.order.internal.util.v1_0.OrderItemUtil;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderItemResource;
import com.liferay.headless.commerce.core.util.ExpandoUtil;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/order-item.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = OrderItemResource.class
)
public class OrderItemResourceImpl extends BaseOrderItemResourceImpl {

	@Override
	public Response deleteOrderItem(Long id) throws Exception {
		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(id);

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderItem.getCommerceOrderId());

		_commerceOrderItemService.deleteCommerceOrderItem(
			commerceOrderItem.getCommerceOrderItemId(),
			_commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(),
				commerceOrder.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				contextCompany.getCompanyId()));

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response deleteOrderItemByExternalReferenceCode(
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

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderItem.getCommerceOrderId());

		_commerceOrderItemService.deleteCommerceOrderItem(
			commerceOrderItem.getCommerceOrderItemId(),
			_commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(),
				commerceOrder.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				contextCompany.getCompanyId()));

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return new OrderItemEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(CommerceOrderItem.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public Page<OrderItem> getOrderByExternalReferenceCodeOrderItemsPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrderItemService.getCommerceOrderItems(
				commerceOrder.getCommerceOrderId(),
				pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount = _commerceOrderItemService.getCommerceOrderItemsCount(
			commerceOrder.getCommerceOrderId());

		return Page.of(
			_toOrderItems(
				commerceOrderItems, contextAcceptLanguage.getPreferredLocale()),
			pagination, totalCount);
	}

	@NestedField(parentClass = Order.class, value = "orderItems")
	@Override
	public Page<OrderItem> getOrderIdOrderItemsPage(
			Long id, Pagination pagination)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.fetchCommerceOrder(
			id);

		if (commerceOrder == null) {
			return Page.of(Collections.emptyList());
		}

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrderItemService.getCommerceOrderItems(
				id, pagination.getStartPosition(), pagination.getEndPosition());

		int totalCount = _commerceOrderItemService.getCommerceOrderItemsCount(
			id);

		return Page.of(
			_toOrderItems(
				commerceOrderItems, contextAcceptLanguage.getPreferredLocale()),
			pagination, totalCount);
	}

	@Override
	public OrderItem getOrderItem(Long id) throws Exception {
		return _toOrderItem(GetterUtil.getLong(id));
	}

	@Override
	public OrderItem getOrderItemByExternalReferenceCode(
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

		return _toOrderItem(commerceOrderItem.getCommerceOrderItemId());
	}

	@Override
	public Page<OrderItem> getOrderItemsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceOrderItem.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toOrderItem(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Response patchOrderItem(Long id, OrderItem orderItem)
		throws Exception {

		_updateOrderItem(
			_commerceOrderItemService.getCommerceOrderItem(id), orderItem);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchOrderItemByExternalReferenceCode(
			String externalReferenceCode, OrderItem orderItem)
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

		_updateOrderItem(commerceOrderItem, orderItem);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public OrderItem postOrderByExternalReferenceCodeOrderItem(
			String externalReferenceCode, OrderItem orderItem)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return _addOrderItem(commerceOrder, orderItem);
	}

	@Override
	public OrderItem postOrderIdOrderItem(Long id, OrderItem orderItem)
		throws Exception {

		return _addOrderItem(
			_commerceOrderService.getCommerceOrder(id), orderItem);
	}

	@Override
	public OrderItem putOrderItem(Long id, OrderItem orderItem)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			orderItem.getOrderId());

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.updateCommerceOrderItem(
				null, id, GetterUtil.getString(orderItem.getOptions(), "[]"),
				BigDecimal.valueOf(
					GetterUtil.getInteger(orderItem.getQuantity())),
				_commerceContextFactory.create(
					commerceOrder.getCommerceAccountId(),
					commerceOrder.getGroupId(), null,
					commerceOrder.getCommerceOrderId(),
					contextCompany.getCompanyId()),
				_serviceContextHelper.getServiceContext(
					commerceOrder.getScopeGroupId()));

		// Pricing

		PortletResourcePermission portletResourcePermission =
			_commerceOrderModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commerceOrder.getGroupId(),
				CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES)) {

			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItemPrices(
					commerceOrderItem.getCommerceOrderItemId(),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountAmount(), BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountWithTaxAmount(), BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel1(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel1WithTaxAmount(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel2(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel2WithTaxAmount(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel3(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel3WithTaxAmount(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel4(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getDiscountPercentageLevel4WithTaxAmount(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getFinalPrice(), BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getFinalPriceWithTaxAmount(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getPromoPrice(), BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getPromoPriceWithTaxAmount(),
						BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getUnitPrice(), BigDecimal.ZERO),
					(BigDecimal)GetterUtil.getNumber(
						orderItem.getUnitPriceWithTaxAmount(),
						BigDecimal.ZERO));
		}

		// Expando

		Map<String, ?> customFields = _getExpandoBridgeAttributes(orderItem);

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrderItem.class,
				commerceOrderItem.getPrimaryKey(), customFields);
		}

		return _toOrderItem(commerceOrderItem.getCommerceOrderItemId());
	}

	@Override
	public OrderItem putOrderItemByExternalReferenceCode(
			String externalReferenceCode, OrderItem orderItem)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			GetterUtil.getLong(orderItem.getOrderId()));

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.
				fetchCommerceOrderItemByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrderItem == null) {
			commerceOrderItem = OrderItemUtil.addCommerceOrderItem(
				_cpInstanceService, _commerceAddressService,
				_commerceOrderItemService,
				_commerceOrderModelResourcePermission, orderItem, commerceOrder,
				_commerceContextFactory.create(
					commerceOrder.getCommerceAccountId(),
					commerceOrder.getGroupId(), null,
					commerceOrder.getCommerceOrderId(),
					contextCompany.getCompanyId()),
				_serviceContextHelper.getServiceContext(
					commerceOrder.getGroupId()));

			commerceOrderItem =
				_commerceOrderItemService.updateExternalReferenceCode(
					commerceOrderItem.getCommerceOrderItemId(),
					externalReferenceCode);
		}
		else {
			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItem(
					null, commerceOrderItem.getCommerceOrderItemId(),
					GetterUtil.getString(orderItem.getOptions(), "[]"),
					BigDecimal.valueOf(
						GetterUtil.getInteger(orderItem.getQuantity())),
					_commerceContextFactory.create(
						commerceOrder.getCommerceAccountId(),
						commerceOrder.getGroupId(), null,
						commerceOrder.getCommerceOrderId(),
						contextCompany.getCompanyId()),
					_serviceContextHelper.getServiceContext(
						commerceOrder.getGroupId()));

			// Pricing

			PortletResourcePermission portletResourcePermission =
				_commerceOrderModelResourcePermission.
					getPortletResourcePermission();

			if (portletResourcePermission.contains(
					PermissionThreadLocal.getPermissionChecker(),
					commerceOrder.getGroupId(),
					CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES)) {

				commerceOrderItem =
					_commerceOrderItemService.updateCommerceOrderItemPrices(
						commerceOrderItem.getCommerceOrderItemId(),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getDiscountAmount(), BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getDiscountWithTaxAmount(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getDiscountPercentageLevel1(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.
								getDiscountPercentageLevel1WithTaxAmount(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getDiscountPercentageLevel2(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.
								getDiscountPercentageLevel2WithTaxAmount(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getDiscountPercentageLevel3(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.
								getDiscountPercentageLevel3WithTaxAmount(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getDiscountPercentageLevel4(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.
								getDiscountPercentageLevel4WithTaxAmount(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getFinalPrice(), BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getFinalPriceWithTaxAmount(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getPromoPrice(), BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getPromoPriceWithTaxAmount(),
							BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getUnitPrice(), BigDecimal.ZERO),
						(BigDecimal)GetterUtil.getNumber(
							orderItem.getUnitPriceWithTaxAmount(),
							BigDecimal.ZERO));
			}
		}

		// Expando

		Map<String, ?> customFields = _getExpandoBridgeAttributes(orderItem);

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrderItem.class,
				commerceOrderItem.getPrimaryKey(), customFields);
		}

		return _toOrderItem(commerceOrderItem.getCommerceOrderItemId());
	}

	private OrderItem _addOrderItem(
			CommerceOrder commerceOrder, OrderItem orderItem)
		throws Exception {

		CommerceOrderItem commerceOrderItem =
			OrderItemUtil.addCommerceOrderItem(
				_cpInstanceService, _commerceAddressService,
				_commerceOrderItemService,
				_commerceOrderModelResourcePermission, orderItem, commerceOrder,
				_commerceContextFactory.create(
					commerceOrder.getCommerceAccountId(),
					commerceOrder.getGroupId(), null,
					commerceOrder.getCommerceOrderId(),
					contextCompany.getCompanyId()),
				_serviceContextHelper.getServiceContext(
					commerceOrder.getGroupId()));

		// Pricing

		PortletResourcePermission portletResourcePermission =
			_commerceOrderModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commerceOrder.getGroupId(),
				CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES)) {

			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItemPrices(
					commerceOrderItem.getCommerceOrderItemId(),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountAmount(),
						commerceOrderItem.getDiscountAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountWithTaxAmount(),
						commerceOrderItem.getDiscountWithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel1(),
						commerceOrderItem.getDiscountPercentageLevel1()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel1WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel1WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel2(),
						commerceOrderItem.getDiscountPercentageLevel2()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel2WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel2WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel3(),
						commerceOrderItem.getDiscountPercentageLevel3()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel3WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel3WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel4(),
						commerceOrderItem.getDiscountPercentageLevel4()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel4WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel4WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getFinalPrice(),
						commerceOrderItem.getFinalPrice()),
					(BigDecimal)GetterUtil.get(
						orderItem.getFinalPriceWithTaxAmount(),
						commerceOrderItem.getFinalPriceWithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getPromoPrice(),
						commerceOrderItem.getPromoPrice()),
					(BigDecimal)GetterUtil.get(
						orderItem.getPromoPriceWithTaxAmount(),
						commerceOrderItem.getPromoPriceWithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getUnitPrice(),
						commerceOrderItem.getUnitPrice()),
					(BigDecimal)GetterUtil.get(
						orderItem.getUnitPriceWithTaxAmount(),
						commerceOrderItem.getUnitPriceWithTaxAmount()));
		}

		// Expando

		Map<String, ?> customFields = _getExpandoBridgeAttributes(orderItem);

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrderItem.class,
				commerceOrderItem.getPrimaryKey(), customFields);
		}

		return _toOrderItem(commerceOrderItem.getCommerceOrderItemId());
	}

	private Map<String, Serializable> _getExpandoBridgeAttributes(
		OrderItem orderItem) {

		return CustomFieldsUtil.toMap(
			CommerceOrderItem.class.getName(), contextCompany.getCompanyId(),
			orderItem.getCustomFields(),
			contextAcceptLanguage.getPreferredLocale());
	}

	private OrderItem _toOrderItem(long commerceOrderItemId) throws Exception {
		return _orderItemDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceOrderItemId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<OrderItem> _toOrderItems(
			List<CommerceOrderItem> commerceOrderItems, Locale locale)
		throws Exception {

		return transform(
			commerceOrderItems,
			commerceOrderItem -> _orderItemDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					commerceOrderItem.getCommerceOrderItemId(), locale)));
	}

	private OrderItem _updateOrderItem(
			CommerceOrderItem commerceOrderItem, OrderItem orderItem)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderItem.getCommerceOrderId());

		BigDecimal quantity = commerceOrderItem.getQuantity();

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			commerceOrderItem.getGroupId());

		serviceContext.setAttribute("validateOrder", Boolean.FALSE);

		commerceOrderItem = _commerceOrderItemService.updateCommerceOrderItem(
			orderItem.getExternalReferenceCode(),
			commerceOrderItem.getCommerceOrderItemId(),
			GetterUtil.getString(
				orderItem.getOptions(), commerceOrderItem.getJson()),
			BigDecimal.valueOf(
				GetterUtil.get(orderItem.getQuantity(), quantity.intValue())),
			_commerceContextFactory.create(
				commerceOrder.getCommerceAccountId(),
				commerceOrder.getGroupId(), null,
				commerceOrder.getCommerceOrderId(),
				contextCompany.getCompanyId()),
			serviceContext);

		// Pricing

		PortletResourcePermission portletResourcePermission =
			_commerceOrderModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commerceOrder.getGroupId(),
				CommerceActionKeys.MANAGE_COMMERCE_ORDER_PRICES)) {

			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItemPrices(
					commerceOrderItem.getCommerceOrderItemId(),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountAmount(),
						commerceOrderItem.getDiscountAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountWithTaxAmount(),
						commerceOrderItem.getDiscountWithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel1(),
						commerceOrderItem.getDiscountPercentageLevel1()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel1WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel1WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel2(),
						commerceOrderItem.getDiscountPercentageLevel2()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel2WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel2WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel3(),
						commerceOrderItem.getDiscountPercentageLevel3()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel3WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel3WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel4(),
						commerceOrderItem.getDiscountPercentageLevel4()),
					(BigDecimal)GetterUtil.get(
						orderItem.getDiscountPercentageLevel4WithTaxAmount(),
						commerceOrderItem.
							getDiscountPercentageLevel4WithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getFinalPrice(),
						commerceOrderItem.getFinalPrice()),
					(BigDecimal)GetterUtil.get(
						orderItem.getFinalPriceWithTaxAmount(),
						commerceOrderItem.getFinalPriceWithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getPromoPrice(),
						commerceOrderItem.getPromoPrice()),
					(BigDecimal)GetterUtil.get(
						orderItem.getPromoPriceWithTaxAmount(),
						commerceOrderItem.getPromoPriceWithTaxAmount()),
					(BigDecimal)GetterUtil.get(
						orderItem.getUnitPrice(),
						commerceOrderItem.getUnitPrice()),
					(BigDecimal)GetterUtil.get(
						orderItem.getUnitPriceWithTaxAmount(),
						commerceOrderItem.getUnitPriceWithTaxAmount()));
		}

		// Expando

		Map<String, ?> customFields = _getExpandoBridgeAttributes(orderItem);

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrderItem.class,
				commerceOrderItem.getPrimaryKey(), customFields);
		}

		return _toOrderItem(commerceOrderItem.getCommerceOrderItemId());
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

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
	private CPInstanceService _cpInstanceService;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference(target = DTOConverterConstants.ORDER_ITEM_DTO_CONVERTER)
	private DTOConverter<CommerceOrderItem, OrderItem> _orderItemDTOConverter;

	@Reference
	private Portal _portal;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}