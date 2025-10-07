/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.headless.commerce.admin.order.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderResource;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseOrderResourceImpl
	implements EntityModelResource, OrderResource,
			   VulcanBatchEngineTaskItemDelegate<Order>,
			   VulcanCRUDItemDelegate<Order> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{id}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/orders/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deleteOrder(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/orders/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteOrderBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.deleteImportTask(
				Order.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deleteOrderByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{id}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/orders/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Order getOrder(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {

		return new Order();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Order getOrderByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Order();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/orders")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Order> getOrdersPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{id}' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "advanceStatus": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "channelExternalReferenceCode": ___, "channelId": ___, "couponCode": ___, "createDate": ___, "creatorEmailAddress": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermExternalReferenceCode": ___, "deliveryTermId": ___, "externalReferenceCode": ___, "id": ___, "lastPriceUpdateDate": ___, "modifiedDate": ___, "name": ___, "orderDate": ___, "orderItems": ___, "orderStatus": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentStatus": ___, "paymentTermExternalReferenceCode": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingAmount": ___, "shippingAmountFormatted": ___, "shippingAmountValue": ___, "shippingDiscountAmount": ___, "shippingDiscountAmountFormatted": ___, "shippingDiscountAmountValue": ___, "shippingDiscountPercentageLevel1": ___, "shippingDiscountPercentageLevel1WithTaxAmount": ___, "shippingDiscountPercentageLevel2": ___, "shippingDiscountPercentageLevel2WithTaxAmount": ___, "shippingDiscountPercentageLevel3": ___, "shippingDiscountPercentageLevel3WithTaxAmount": ___, "shippingDiscountPercentageLevel4": ___, "shippingDiscountPercentageLevel4WithTaxAmount": ___, "shippingDiscountWithTaxAmount": ___, "shippingDiscountWithTaxAmountFormatted": ___, "shippingMethod": ___, "shippingOption": ___, "shippingWithTaxAmount": ___, "shippingWithTaxAmountFormatted": ___, "shippingWithTaxAmountValue": ___, "subtotal": ___, "subtotalDiscountAmount": ___, "subtotalDiscountAmountFormatted": ___, "subtotalDiscountPercentageLevel1": ___, "subtotalDiscountPercentageLevel1WithTaxAmount": ___, "subtotalDiscountPercentageLevel2": ___, "subtotalDiscountPercentageLevel2WithTaxAmount": ___, "subtotalDiscountPercentageLevel3": ___, "subtotalDiscountPercentageLevel3WithTaxAmount": ___, "subtotalDiscountPercentageLevel4": ___, "subtotalDiscountPercentageLevel4WithTaxAmount": ___, "subtotalDiscountWithTaxAmount": ___, "subtotalDiscountWithTaxAmountFormatted": ___, "subtotalFormatted": ___, "subtotalWithTaxAmount": ___, "subtotalWithTaxAmountFormatted": ___, "taxAmount": ___, "taxAmountFormatted": ___, "taxAmountValue": ___, "total": ___, "totalDiscountAmount": ___, "totalDiscountAmountFormatted": ___, "totalDiscountAmountValue": ___, "totalDiscountPercentageLevel1": ___, "totalDiscountPercentageLevel1WithTaxAmount": ___, "totalDiscountPercentageLevel2": ___, "totalDiscountPercentageLevel2WithTaxAmount": ___, "totalDiscountPercentageLevel3": ___, "totalDiscountPercentageLevel3WithTaxAmount": ___, "totalDiscountPercentageLevel4": ___, "totalDiscountPercentageLevel4WithTaxAmount": ___, "totalDiscountWithTaxAmount": ___, "totalDiscountWithTaxAmountFormatted": ___, "totalDiscountWithTaxAmountValue": ___, "totalFormatted": ___, "totalWithTaxAmount": ___, "totalWithTaxAmountFormatted": ___, "transactionId": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/orders/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Order patchOrder(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id,
			Order order)
		throws Exception {

		return new Order();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "advanceStatus": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "channelExternalReferenceCode": ___, "channelId": ___, "couponCode": ___, "createDate": ___, "creatorEmailAddress": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermExternalReferenceCode": ___, "deliveryTermId": ___, "externalReferenceCode": ___, "id": ___, "lastPriceUpdateDate": ___, "modifiedDate": ___, "name": ___, "orderDate": ___, "orderItems": ___, "orderStatus": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentStatus": ___, "paymentTermExternalReferenceCode": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingAmount": ___, "shippingAmountFormatted": ___, "shippingAmountValue": ___, "shippingDiscountAmount": ___, "shippingDiscountAmountFormatted": ___, "shippingDiscountAmountValue": ___, "shippingDiscountPercentageLevel1": ___, "shippingDiscountPercentageLevel1WithTaxAmount": ___, "shippingDiscountPercentageLevel2": ___, "shippingDiscountPercentageLevel2WithTaxAmount": ___, "shippingDiscountPercentageLevel3": ___, "shippingDiscountPercentageLevel3WithTaxAmount": ___, "shippingDiscountPercentageLevel4": ___, "shippingDiscountPercentageLevel4WithTaxAmount": ___, "shippingDiscountWithTaxAmount": ___, "shippingDiscountWithTaxAmountFormatted": ___, "shippingMethod": ___, "shippingOption": ___, "shippingWithTaxAmount": ___, "shippingWithTaxAmountFormatted": ___, "shippingWithTaxAmountValue": ___, "subtotal": ___, "subtotalDiscountAmount": ___, "subtotalDiscountAmountFormatted": ___, "subtotalDiscountPercentageLevel1": ___, "subtotalDiscountPercentageLevel1WithTaxAmount": ___, "subtotalDiscountPercentageLevel2": ___, "subtotalDiscountPercentageLevel2WithTaxAmount": ___, "subtotalDiscountPercentageLevel3": ___, "subtotalDiscountPercentageLevel3WithTaxAmount": ___, "subtotalDiscountPercentageLevel4": ___, "subtotalDiscountPercentageLevel4WithTaxAmount": ___, "subtotalDiscountWithTaxAmount": ___, "subtotalDiscountWithTaxAmountFormatted": ___, "subtotalFormatted": ___, "subtotalWithTaxAmount": ___, "subtotalWithTaxAmountFormatted": ___, "taxAmount": ___, "taxAmountFormatted": ___, "taxAmountValue": ___, "total": ___, "totalDiscountAmount": ___, "totalDiscountAmountFormatted": ___, "totalDiscountAmountValue": ___, "totalDiscountPercentageLevel1": ___, "totalDiscountPercentageLevel1WithTaxAmount": ___, "totalDiscountPercentageLevel2": ___, "totalDiscountPercentageLevel2WithTaxAmount": ___, "totalDiscountPercentageLevel3": ___, "totalDiscountPercentageLevel3WithTaxAmount": ___, "totalDiscountPercentageLevel4": ___, "totalDiscountPercentageLevel4WithTaxAmount": ___, "totalDiscountWithTaxAmount": ___, "totalDiscountWithTaxAmountFormatted": ___, "totalDiscountWithTaxAmountValue": ___, "totalFormatted": ___, "totalWithTaxAmount": ___, "totalWithTaxAmountFormatted": ___, "transactionId": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Order patchOrderByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Order order)
		throws Exception {

		Order existingOrder = getOrderByExternalReferenceCode(
			externalReferenceCode);

		if (order.getAccountExternalReferenceCode() != null) {
			existingOrder.setAccountExternalReferenceCode(
				order.getAccountExternalReferenceCode());
		}

		if (order.getAccountId() != null) {
			existingOrder.setAccountId(order.getAccountId());
		}

		if (order.getAdvanceStatus() != null) {
			existingOrder.setAdvanceStatus(order.getAdvanceStatus());
		}

		if (order.getBillingAddressExternalReferenceCode() != null) {
			existingOrder.setBillingAddressExternalReferenceCode(
				order.getBillingAddressExternalReferenceCode());
		}

		if (order.getBillingAddressId() != null) {
			existingOrder.setBillingAddressId(order.getBillingAddressId());
		}

		if (order.getChannelExternalReferenceCode() != null) {
			existingOrder.setChannelExternalReferenceCode(
				order.getChannelExternalReferenceCode());
		}

		if (order.getChannelId() != null) {
			existingOrder.setChannelId(order.getChannelId());
		}

		if (order.getCouponCode() != null) {
			existingOrder.setCouponCode(order.getCouponCode());
		}

		if (order.getCreateDate() != null) {
			existingOrder.setCreateDate(order.getCreateDate());
		}

		if (order.getCreatorEmailAddress() != null) {
			existingOrder.setCreatorEmailAddress(
				order.getCreatorEmailAddress());
		}

		if (order.getCurrencyCode() != null) {
			existingOrder.setCurrencyCode(order.getCurrencyCode());
		}

		if (order.getCurrencyExternalReferenceCode() != null) {
			existingOrder.setCurrencyExternalReferenceCode(
				order.getCurrencyExternalReferenceCode());
		}

		if (order.getCurrencyId() != null) {
			existingOrder.setCurrencyId(order.getCurrencyId());
		}

		if (order.getCustomFields() != null) {
			existingOrder.setCustomFields(order.getCustomFields());
		}

		if (order.getDeliveryTermExternalReferenceCode() != null) {
			existingOrder.setDeliveryTermExternalReferenceCode(
				order.getDeliveryTermExternalReferenceCode());
		}

		if (order.getDeliveryTermId() != null) {
			existingOrder.setDeliveryTermId(order.getDeliveryTermId());
		}

		if (order.getExternalReferenceCode() != null) {
			existingOrder.setExternalReferenceCode(
				order.getExternalReferenceCode());
		}

		if (order.getLastPriceUpdateDate() != null) {
			existingOrder.setLastPriceUpdateDate(
				order.getLastPriceUpdateDate());
		}

		if (order.getModifiedDate() != null) {
			existingOrder.setModifiedDate(order.getModifiedDate());
		}

		if (order.getName() != null) {
			existingOrder.setName(order.getName());
		}

		if (order.getOrderDate() != null) {
			existingOrder.setOrderDate(order.getOrderDate());
		}

		if (order.getOrderStatus() != null) {
			existingOrder.setOrderStatus(order.getOrderStatus());
		}

		if (order.getOrderTypeExternalReferenceCode() != null) {
			existingOrder.setOrderTypeExternalReferenceCode(
				order.getOrderTypeExternalReferenceCode());
		}

		if (order.getOrderTypeId() != null) {
			existingOrder.setOrderTypeId(order.getOrderTypeId());
		}

		if (order.getPaymentMethod() != null) {
			existingOrder.setPaymentMethod(order.getPaymentMethod());
		}

		if (order.getPaymentStatus() != null) {
			existingOrder.setPaymentStatus(order.getPaymentStatus());
		}

		if (order.getPaymentTermExternalReferenceCode() != null) {
			existingOrder.setPaymentTermExternalReferenceCode(
				order.getPaymentTermExternalReferenceCode());
		}

		if (order.getPaymentTermId() != null) {
			existingOrder.setPaymentTermId(order.getPaymentTermId());
		}

		if (order.getPrintedNote() != null) {
			existingOrder.setPrintedNote(order.getPrintedNote());
		}

		if (order.getPurchaseOrderNumber() != null) {
			existingOrder.setPurchaseOrderNumber(
				order.getPurchaseOrderNumber());
		}

		if (order.getRequestedDeliveryDate() != null) {
			existingOrder.setRequestedDeliveryDate(
				order.getRequestedDeliveryDate());
		}

		if (order.getShippingAddressExternalReferenceCode() != null) {
			existingOrder.setShippingAddressExternalReferenceCode(
				order.getShippingAddressExternalReferenceCode());
		}

		if (order.getShippingAddressId() != null) {
			existingOrder.setShippingAddressId(order.getShippingAddressId());
		}

		if (order.getShippingAmount() != null) {
			existingOrder.setShippingAmount(order.getShippingAmount());
		}

		if (order.getShippingAmountFormatted() != null) {
			existingOrder.setShippingAmountFormatted(
				order.getShippingAmountFormatted());
		}

		if (order.getShippingAmountValue() != null) {
			existingOrder.setShippingAmountValue(
				order.getShippingAmountValue());
		}

		if (order.getShippingDiscountAmount() != null) {
			existingOrder.setShippingDiscountAmount(
				order.getShippingDiscountAmount());
		}

		if (order.getShippingDiscountAmountFormatted() != null) {
			existingOrder.setShippingDiscountAmountFormatted(
				order.getShippingDiscountAmountFormatted());
		}

		if (order.getShippingDiscountAmountValue() != null) {
			existingOrder.setShippingDiscountAmountValue(
				order.getShippingDiscountAmountValue());
		}

		if (order.getShippingDiscountPercentageLevel1() != null) {
			existingOrder.setShippingDiscountPercentageLevel1(
				order.getShippingDiscountPercentageLevel1());
		}

		if (order.getShippingDiscountPercentageLevel1WithTaxAmount() != null) {
			existingOrder.setShippingDiscountPercentageLevel1WithTaxAmount(
				order.getShippingDiscountPercentageLevel1WithTaxAmount());
		}

		if (order.getShippingDiscountPercentageLevel2() != null) {
			existingOrder.setShippingDiscountPercentageLevel2(
				order.getShippingDiscountPercentageLevel2());
		}

		if (order.getShippingDiscountPercentageLevel2WithTaxAmount() != null) {
			existingOrder.setShippingDiscountPercentageLevel2WithTaxAmount(
				order.getShippingDiscountPercentageLevel2WithTaxAmount());
		}

		if (order.getShippingDiscountPercentageLevel3() != null) {
			existingOrder.setShippingDiscountPercentageLevel3(
				order.getShippingDiscountPercentageLevel3());
		}

		if (order.getShippingDiscountPercentageLevel3WithTaxAmount() != null) {
			existingOrder.setShippingDiscountPercentageLevel3WithTaxAmount(
				order.getShippingDiscountPercentageLevel3WithTaxAmount());
		}

		if (order.getShippingDiscountPercentageLevel4() != null) {
			existingOrder.setShippingDiscountPercentageLevel4(
				order.getShippingDiscountPercentageLevel4());
		}

		if (order.getShippingDiscountPercentageLevel4WithTaxAmount() != null) {
			existingOrder.setShippingDiscountPercentageLevel4WithTaxAmount(
				order.getShippingDiscountPercentageLevel4WithTaxAmount());
		}

		if (order.getShippingDiscountWithTaxAmount() != null) {
			existingOrder.setShippingDiscountWithTaxAmount(
				order.getShippingDiscountWithTaxAmount());
		}

		if (order.getShippingDiscountWithTaxAmountFormatted() != null) {
			existingOrder.setShippingDiscountWithTaxAmountFormatted(
				order.getShippingDiscountWithTaxAmountFormatted());
		}

		if (order.getShippingMethod() != null) {
			existingOrder.setShippingMethod(order.getShippingMethod());
		}

		if (order.getShippingOption() != null) {
			existingOrder.setShippingOption(order.getShippingOption());
		}

		if (order.getShippingWithTaxAmount() != null) {
			existingOrder.setShippingWithTaxAmount(
				order.getShippingWithTaxAmount());
		}

		if (order.getShippingWithTaxAmountFormatted() != null) {
			existingOrder.setShippingWithTaxAmountFormatted(
				order.getShippingWithTaxAmountFormatted());
		}

		if (order.getShippingWithTaxAmountValue() != null) {
			existingOrder.setShippingWithTaxAmountValue(
				order.getShippingWithTaxAmountValue());
		}

		if (order.getSubtotal() != null) {
			existingOrder.setSubtotal(order.getSubtotal());
		}

		if (order.getSubtotalDiscountAmount() != null) {
			existingOrder.setSubtotalDiscountAmount(
				order.getSubtotalDiscountAmount());
		}

		if (order.getSubtotalDiscountAmountFormatted() != null) {
			existingOrder.setSubtotalDiscountAmountFormatted(
				order.getSubtotalDiscountAmountFormatted());
		}

		if (order.getSubtotalDiscountPercentageLevel1() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel1(
				order.getSubtotalDiscountPercentageLevel1());
		}

		if (order.getSubtotalDiscountPercentageLevel1WithTaxAmount() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel1WithTaxAmount(
				order.getSubtotalDiscountPercentageLevel1WithTaxAmount());
		}

		if (order.getSubtotalDiscountPercentageLevel2() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel2(
				order.getSubtotalDiscountPercentageLevel2());
		}

		if (order.getSubtotalDiscountPercentageLevel2WithTaxAmount() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel2WithTaxAmount(
				order.getSubtotalDiscountPercentageLevel2WithTaxAmount());
		}

		if (order.getSubtotalDiscountPercentageLevel3() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel3(
				order.getSubtotalDiscountPercentageLevel3());
		}

		if (order.getSubtotalDiscountPercentageLevel3WithTaxAmount() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel3WithTaxAmount(
				order.getSubtotalDiscountPercentageLevel3WithTaxAmount());
		}

		if (order.getSubtotalDiscountPercentageLevel4() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel4(
				order.getSubtotalDiscountPercentageLevel4());
		}

		if (order.getSubtotalDiscountPercentageLevel4WithTaxAmount() != null) {
			existingOrder.setSubtotalDiscountPercentageLevel4WithTaxAmount(
				order.getSubtotalDiscountPercentageLevel4WithTaxAmount());
		}

		if (order.getSubtotalDiscountWithTaxAmount() != null) {
			existingOrder.setSubtotalDiscountWithTaxAmount(
				order.getSubtotalDiscountWithTaxAmount());
		}

		if (order.getSubtotalDiscountWithTaxAmountFormatted() != null) {
			existingOrder.setSubtotalDiscountWithTaxAmountFormatted(
				order.getSubtotalDiscountWithTaxAmountFormatted());
		}

		if (order.getSubtotalFormatted() != null) {
			existingOrder.setSubtotalFormatted(order.getSubtotalFormatted());
		}

		if (order.getSubtotalWithTaxAmount() != null) {
			existingOrder.setSubtotalWithTaxAmount(
				order.getSubtotalWithTaxAmount());
		}

		if (order.getSubtotalWithTaxAmountFormatted() != null) {
			existingOrder.setSubtotalWithTaxAmountFormatted(
				order.getSubtotalWithTaxAmountFormatted());
		}

		if (order.getTaxAmount() != null) {
			existingOrder.setTaxAmount(order.getTaxAmount());
		}

		if (order.getTaxAmountFormatted() != null) {
			existingOrder.setTaxAmountFormatted(order.getTaxAmountFormatted());
		}

		if (order.getTaxAmountValue() != null) {
			existingOrder.setTaxAmountValue(order.getTaxAmountValue());
		}

		if (order.getTotal() != null) {
			existingOrder.setTotal(order.getTotal());
		}

		if (order.getTotalDiscountAmount() != null) {
			existingOrder.setTotalDiscountAmount(
				order.getTotalDiscountAmount());
		}

		if (order.getTotalDiscountAmountFormatted() != null) {
			existingOrder.setTotalDiscountAmountFormatted(
				order.getTotalDiscountAmountFormatted());
		}

		if (order.getTotalDiscountAmountValue() != null) {
			existingOrder.setTotalDiscountAmountValue(
				order.getTotalDiscountAmountValue());
		}

		if (order.getTotalDiscountPercentageLevel1() != null) {
			existingOrder.setTotalDiscountPercentageLevel1(
				order.getTotalDiscountPercentageLevel1());
		}

		if (order.getTotalDiscountPercentageLevel1WithTaxAmount() != null) {
			existingOrder.setTotalDiscountPercentageLevel1WithTaxAmount(
				order.getTotalDiscountPercentageLevel1WithTaxAmount());
		}

		if (order.getTotalDiscountPercentageLevel2() != null) {
			existingOrder.setTotalDiscountPercentageLevel2(
				order.getTotalDiscountPercentageLevel2());
		}

		if (order.getTotalDiscountPercentageLevel2WithTaxAmount() != null) {
			existingOrder.setTotalDiscountPercentageLevel2WithTaxAmount(
				order.getTotalDiscountPercentageLevel2WithTaxAmount());
		}

		if (order.getTotalDiscountPercentageLevel3() != null) {
			existingOrder.setTotalDiscountPercentageLevel3(
				order.getTotalDiscountPercentageLevel3());
		}

		if (order.getTotalDiscountPercentageLevel3WithTaxAmount() != null) {
			existingOrder.setTotalDiscountPercentageLevel3WithTaxAmount(
				order.getTotalDiscountPercentageLevel3WithTaxAmount());
		}

		if (order.getTotalDiscountPercentageLevel4() != null) {
			existingOrder.setTotalDiscountPercentageLevel4(
				order.getTotalDiscountPercentageLevel4());
		}

		if (order.getTotalDiscountPercentageLevel4WithTaxAmount() != null) {
			existingOrder.setTotalDiscountPercentageLevel4WithTaxAmount(
				order.getTotalDiscountPercentageLevel4WithTaxAmount());
		}

		if (order.getTotalDiscountWithTaxAmount() != null) {
			existingOrder.setTotalDiscountWithTaxAmount(
				order.getTotalDiscountWithTaxAmount());
		}

		if (order.getTotalDiscountWithTaxAmountFormatted() != null) {
			existingOrder.setTotalDiscountWithTaxAmountFormatted(
				order.getTotalDiscountWithTaxAmountFormatted());
		}

		if (order.getTotalDiscountWithTaxAmountValue() != null) {
			existingOrder.setTotalDiscountWithTaxAmountValue(
				order.getTotalDiscountWithTaxAmountValue());
		}

		if (order.getTotalFormatted() != null) {
			existingOrder.setTotalFormatted(order.getTotalFormatted());
		}

		if (order.getTotalWithTaxAmount() != null) {
			existingOrder.setTotalWithTaxAmount(order.getTotalWithTaxAmount());
		}

		if (order.getTotalWithTaxAmountFormatted() != null) {
			existingOrder.setTotalWithTaxAmountFormatted(
				order.getTotalWithTaxAmountFormatted());
		}

		if (order.getTransactionId() != null) {
			existingOrder.setTransactionId(order.getTransactionId());
		}

		preparePatch(order, existingOrder);

		return putOrderByExternalReferenceCode(
			externalReferenceCode, existingOrder);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "advanceStatus": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "channelExternalReferenceCode": ___, "channelId": ___, "couponCode": ___, "createDate": ___, "creatorEmailAddress": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermExternalReferenceCode": ___, "deliveryTermId": ___, "externalReferenceCode": ___, "id": ___, "lastPriceUpdateDate": ___, "modifiedDate": ___, "name": ___, "orderDate": ___, "orderItems": ___, "orderStatus": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentStatus": ___, "paymentTermExternalReferenceCode": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingAmount": ___, "shippingAmountFormatted": ___, "shippingAmountValue": ___, "shippingDiscountAmount": ___, "shippingDiscountAmountFormatted": ___, "shippingDiscountAmountValue": ___, "shippingDiscountPercentageLevel1": ___, "shippingDiscountPercentageLevel1WithTaxAmount": ___, "shippingDiscountPercentageLevel2": ___, "shippingDiscountPercentageLevel2WithTaxAmount": ___, "shippingDiscountPercentageLevel3": ___, "shippingDiscountPercentageLevel3WithTaxAmount": ___, "shippingDiscountPercentageLevel4": ___, "shippingDiscountPercentageLevel4WithTaxAmount": ___, "shippingDiscountWithTaxAmount": ___, "shippingDiscountWithTaxAmountFormatted": ___, "shippingMethod": ___, "shippingOption": ___, "shippingWithTaxAmount": ___, "shippingWithTaxAmountFormatted": ___, "shippingWithTaxAmountValue": ___, "subtotal": ___, "subtotalDiscountAmount": ___, "subtotalDiscountAmountFormatted": ___, "subtotalDiscountPercentageLevel1": ___, "subtotalDiscountPercentageLevel1WithTaxAmount": ___, "subtotalDiscountPercentageLevel2": ___, "subtotalDiscountPercentageLevel2WithTaxAmount": ___, "subtotalDiscountPercentageLevel3": ___, "subtotalDiscountPercentageLevel3WithTaxAmount": ___, "subtotalDiscountPercentageLevel4": ___, "subtotalDiscountPercentageLevel4WithTaxAmount": ___, "subtotalDiscountWithTaxAmount": ___, "subtotalDiscountWithTaxAmountFormatted": ___, "subtotalFormatted": ___, "subtotalWithTaxAmount": ___, "subtotalWithTaxAmountFormatted": ___, "taxAmount": ___, "taxAmountFormatted": ___, "taxAmountValue": ___, "total": ___, "totalDiscountAmount": ___, "totalDiscountAmountFormatted": ___, "totalDiscountAmountValue": ___, "totalDiscountPercentageLevel1": ___, "totalDiscountPercentageLevel1WithTaxAmount": ___, "totalDiscountPercentageLevel2": ___, "totalDiscountPercentageLevel2WithTaxAmount": ___, "totalDiscountPercentageLevel3": ___, "totalDiscountPercentageLevel3WithTaxAmount": ___, "totalDiscountPercentageLevel4": ___, "totalDiscountPercentageLevel4WithTaxAmount": ___, "totalDiscountWithTaxAmount": ___, "totalDiscountWithTaxAmountFormatted": ___, "totalDiscountWithTaxAmountValue": ___, "totalFormatted": ___, "totalWithTaxAmount": ___, "totalWithTaxAmountFormatted": ___, "transactionId": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/orders")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Order postOrder(Order order) throws Exception {
		return new Order();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/orders/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postOrderBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.postImportTask(
				Order.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/orders/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postOrdersPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				Order.class.getName(), callbackURL, contentType, fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "advanceStatus": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "channelExternalReferenceCode": ___, "channelId": ___, "couponCode": ___, "createDate": ___, "creatorEmailAddress": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermExternalReferenceCode": ___, "deliveryTermId": ___, "externalReferenceCode": ___, "id": ___, "lastPriceUpdateDate": ___, "modifiedDate": ___, "name": ___, "orderDate": ___, "orderItems": ___, "orderStatus": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentStatus": ___, "paymentTermExternalReferenceCode": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingAmount": ___, "shippingAmountFormatted": ___, "shippingAmountValue": ___, "shippingDiscountAmount": ___, "shippingDiscountAmountFormatted": ___, "shippingDiscountAmountValue": ___, "shippingDiscountPercentageLevel1": ___, "shippingDiscountPercentageLevel1WithTaxAmount": ___, "shippingDiscountPercentageLevel2": ___, "shippingDiscountPercentageLevel2WithTaxAmount": ___, "shippingDiscountPercentageLevel3": ___, "shippingDiscountPercentageLevel3WithTaxAmount": ___, "shippingDiscountPercentageLevel4": ___, "shippingDiscountPercentageLevel4WithTaxAmount": ___, "shippingDiscountWithTaxAmount": ___, "shippingDiscountWithTaxAmountFormatted": ___, "shippingMethod": ___, "shippingOption": ___, "shippingWithTaxAmount": ___, "shippingWithTaxAmountFormatted": ___, "shippingWithTaxAmountValue": ___, "subtotal": ___, "subtotalDiscountAmount": ___, "subtotalDiscountAmountFormatted": ___, "subtotalDiscountPercentageLevel1": ___, "subtotalDiscountPercentageLevel1WithTaxAmount": ___, "subtotalDiscountPercentageLevel2": ___, "subtotalDiscountPercentageLevel2WithTaxAmount": ___, "subtotalDiscountPercentageLevel3": ___, "subtotalDiscountPercentageLevel3WithTaxAmount": ___, "subtotalDiscountPercentageLevel4": ___, "subtotalDiscountPercentageLevel4WithTaxAmount": ___, "subtotalDiscountWithTaxAmount": ___, "subtotalDiscountWithTaxAmountFormatted": ___, "subtotalFormatted": ___, "subtotalWithTaxAmount": ___, "subtotalWithTaxAmountFormatted": ___, "taxAmount": ___, "taxAmountFormatted": ___, "taxAmountValue": ___, "total": ___, "totalDiscountAmount": ___, "totalDiscountAmountFormatted": ___, "totalDiscountAmountValue": ___, "totalDiscountPercentageLevel1": ___, "totalDiscountPercentageLevel1WithTaxAmount": ___, "totalDiscountPercentageLevel2": ___, "totalDiscountPercentageLevel2WithTaxAmount": ___, "totalDiscountPercentageLevel3": ___, "totalDiscountPercentageLevel3WithTaxAmount": ___, "totalDiscountPercentageLevel4": ___, "totalDiscountPercentageLevel4WithTaxAmount": ___, "totalDiscountWithTaxAmount": ___, "totalDiscountWithTaxAmountFormatted": ___, "totalDiscountWithTaxAmountValue": ___, "totalFormatted": ___, "totalWithTaxAmount": ___, "totalWithTaxAmountFormatted": ___, "transactionId": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Order putOrderByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Order order)
		throws Exception {

		return new Order();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Order> orders, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Order, Order, Exception> orderUnsafeFunction = null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			orderUnsafeFunction = order -> postOrder(order);
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				orderUnsafeFunction = order -> {
					Order getOrder = null;
					Order persistedOrder = null;

					try {
						getOrder = getOrderByExternalReferenceCode(
							order.getExternalReferenceCode());

						persistedOrder = patchOrder(getOrder.getId(), order);
					}
					catch (NoSuchModelException noSuchModelException) {
						persistedOrder = postOrder(order);
					}

					return persistedOrder;
				};
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				orderUnsafeFunction = order -> {
					Order persistedOrder = null;

					persistedOrder = putOrderByExternalReferenceCode(
						order.getExternalReferenceCode(), order);

					return persistedOrder;
				};
			}
		}

		if (orderUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for Order");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(orders, orderUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				orders, orderUnsafeFunction::apply);
		}
		else {
			for (Order order : orders) {
				orderUnsafeFunction.apply(order);
			}
		}
	}

	@Override
	public void delete(
			Collection<Order> orders, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Order, Order, Exception> orderUnsafeFunction = order -> {
			if (order.getId() != null) {
				try {
					deleteOrder(order.getId());

					return order;
				}
				catch (Exception exception) {
					if (order.getExternalReferenceCode() != null) {
						deleteOrderByExternalReferenceCode(
							order.getExternalReferenceCode());

						return order;
					}
				}
			}
			else if (order.getExternalReferenceCode() != null) {
				deleteOrderByExternalReferenceCode(
					order.getExternalReferenceCode());

				return order;
			}

			throw new UnsupportedOperationException(
				"Unable to delete by external reference code or ID");
		};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(orders, orderUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				orders, orderUnsafeFunction::apply);
		}
		else {
			for (Order order : orders) {
				orderUnsafeFunction.apply(order);
			}
		}
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray("INSERT", "UPSERT");
	}

	public Set<String> getAvailableUpdateStrategies() {
		return SetUtil.fromArray("PARTIAL_UPDATE");
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return getEntityModel(
			new MultivaluedHashMap<String, Object>(multivaluedMap));
	}

	public String getResourceName() {
		return "Order";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Order> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getOrdersPage(search, filter, pagination, sorts);
	}

	@Override
	public void setLanguageId(String languageId) {
		this.contextAcceptLanguage = new AcceptLanguage() {

			@Override
			public List<Locale> getLocales() {
				return null;
			}

			@Override
			public String getPreferredLanguageId() {
				return languageId;
			}

			@Override
			public Locale getPreferredLocale() {
				return LocaleUtil.fromLanguageId(languageId);
			}

		};
	}

	@Override
	public void update(
			Collection<Order> orders, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Order, Order, Exception> orderUnsafeFunction = null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			orderUnsafeFunction = order -> patchOrder(order.getId(), order);
		}

		if (orderUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for Order");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(orders, orderUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				orders, orderUnsafeFunction::apply);
		}
		else {
			for (Order order : orders) {
				orderUnsafeFunction.apply(order);
			}
		}
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public Order getItem(Long id) throws Exception {
		return getOrder(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<Order>, UnsafeFunction<Order, Order, Exception>,
			 Exception> contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Order>, UnsafeConsumer<Order, Exception>, Exception>
				contextBatchUnsafeConsumer) {

		this.contextBatchUnsafeConsumer = contextBatchUnsafeConsumer;
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany) {

		this.contextCompany = contextCompany;
	}

	public void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {

		this.contextHttpServletRequest = contextHttpServletRequest;
	}

	public void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {

		this.contextHttpServletResponse = contextHttpServletResponse;
	}

	public void setContextUriInfo(UriInfo contextUriInfo) {
		this.contextUriInfo = UriInfoUtil.getVulcanUriInfo(
			getApplicationPath(), contextUriInfo);
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser) {

		this.contextUser = contextUser;
	}

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert) {

		this.expressionConvert = expressionConvert;
	}

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider) {

		this.filterParserProvider = filterParserProvider;
	}

	public void setGroupLocalService(GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService) {

		this.resourceActionLocalService = resourceActionLocalService;
	}

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService) {

		this.resourcePermissionLocalService = resourcePermissionLocalService;
	}

	public void setRoleLocalService(RoleLocalService roleLocalService) {
		this.roleLocalService = roleLocalService;
	}

	public void setSortParserProvider(SortParserProvider sortParserProvider) {
		this.sortParserProvider = sortParserProvider;
	}

	protected String getApplicationPath() {
		return "headless-commerce-admin-order";
	}

	public void setVulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource) {

		this.vulcanBatchEngineExportTaskResource =
			vulcanBatchEngineExportTaskResource;
	}

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource) {

		this.vulcanBatchEngineImportTaskResource =
			vulcanBatchEngineImportTaskResource;
	}

	@Override
	public com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		try {
			EntityModel entityModel = getEntityModel(multivaluedMap);

			FilterParser filterParser = filterParserProvider.provide(
				entityModel);

			com.liferay.portal.odata.filter.Filter oDataFilter =
				new com.liferay.portal.odata.filter.Filter(
					filterParser.parse(filterString));

			return expressionConvert.convert(
				oDataFilter.getExpression(),
				contextAcceptLanguage.getPreferredLocale(), entityModel);
		}
		catch (Exception exception) {
			_log.error("Invalid filter " + filterString, exception);

			return null;
		}
	}

	@Override
	public com.liferay.portal.kernel.search.Sort[] toSorts(String sortString) {
		if (Validator.isNull(sortString)) {
			return null;
		}

		try {
			SortParser sortParser = sortParserProvider.provide(
				getEntityModel(Collections.emptyMap()));

			if (sortParser == null) {
				return null;
			}

			com.liferay.portal.odata.sort.Sort oDataSort =
				new com.liferay.portal.odata.sort.Sort(
					sortParser.parse(sortString));

			List<SortField> sortFields = oDataSort.getSortFields();
			com.liferay.portal.kernel.search.Sort[] sorts =
				new com.liferay.portal.kernel.search.Sort[sortFields.size()];

			for (int i = 0; i < sortFields.size(); i++) {
				SortField sortField = sortFields.get(i);

				sorts[i] = new com.liferay.portal.kernel.search.Sort(
					sortField.getSortableFieldName(
						contextAcceptLanguage.getPreferredLocale()),
					!sortField.isAscending());
			}

			return sorts;
		}
		catch (Exception exception) {
			_log.error("Invalid sort " + sortString, exception);

			return new com.liferay.portal.kernel.search.Sort[0];
		}
	}

	protected Map<String, String> addAction(
		String actionName,
		com.liferay.portal.kernel.model.GroupedModel groupedModel,
		String methodName) {

		return ActionUtil.addAction(
			actionName, getClass(), groupedModel, methodName,
			contextScopeChecker, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName, Long ownerId,
		String permissionName, Long siteId) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			ownerId, permissionName, siteId, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName,
		ModelResourcePermission modelResourcePermission) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			modelResourcePermission, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, String methodName, String permissionName,
		Long siteId) {

		return addAction(
			actionName, siteId, methodName, null, permissionName, siteId);
	}

	protected void preparePatch(Order order, Order existingOrder) {
	}

	protected <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] transform(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] transform(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transform(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transformToArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		Collection<T> collection,
		UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		T[] array, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		Collection<T> collection, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		T[] array, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		Collection<T> collection, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		T[] array, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] transformToIntArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] transformToIntArray(
		T[] array, UnsafeFunction<T, Integer, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> transformToList(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] transformToLongArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] transformToLongArray(
		T[] array, UnsafeFunction<T, Long, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		Collection<T> collection, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		T[] array, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransform(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransform(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransformToArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				Collection<T> collection,
				UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			T[] array, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				Collection<T> collection,
				UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				T[] array, UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			Collection<T> collection,
			UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			T[] array, UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] unsafeTransformToIntArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] unsafeTransformToIntArray(
			T[] array, UnsafeFunction<T, Integer, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransformToList(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] unsafeTransformToLongArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] unsafeTransformToLongArray(
			T[] array, UnsafeFunction<T, Long, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			Collection<T> collection,
			UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			T[] array, UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(array, unsafeFunction);
	}

	protected AcceptLanguage contextAcceptLanguage;
	protected UnsafeBiConsumer
		<Collection<Order>, UnsafeFunction<Order, Order, Exception>, Exception>
			contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Order>, UnsafeConsumer<Order, Exception>, Exception>
			contextBatchUnsafeConsumer;
	protected com.liferay.portal.kernel.model.Company contextCompany;
	protected HttpServletRequest contextHttpServletRequest;
	protected HttpServletResponse contextHttpServletResponse;
	protected Object contextScopeChecker;
	protected UriInfo contextUriInfo;
	protected com.liferay.portal.kernel.model.User contextUser;
	protected ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
		expressionConvert;
	protected FilterParserProvider filterParserProvider;
	protected GroupLocalService groupLocalService;
	protected ResourceActionLocalService resourceActionLocalService;
	protected ResourcePermissionLocalService resourcePermissionLocalService;
	protected RoleLocalService roleLocalService;
	protected SortParserProvider sortParserProvider;
	protected VulcanBatchEngineExportTaskResource
		vulcanBatchEngineExportTaskResource;
	protected VulcanBatchEngineImportTaskResource
		vulcanBatchEngineImportTaskResource;

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseOrderResourceImpl.class);

}