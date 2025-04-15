/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.resource.v1_0.OrderItemResource;
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

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseOrderItemResourceImpl
	implements EntityModelResource, OrderItemResource,
			   VulcanBatchEngineTaskItemDelegate<OrderItem>,
			   VulcanCRUDItemDelegate<OrderItem> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/orderItems")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<OrderItem> getOrderItemsPage(
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
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/export-batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/orderItems/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postOrderItemsPageExportBatch(
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
				OrderItem.class.getName(), callbackURL, contentType, fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/orderItems/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deleteOrderItemByExternalReferenceCode(
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
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/orderItems/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public OrderItem getOrderItemByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new OrderItem();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/by-externalReferenceCode/{externalReferenceCode}' -d $'{"bookedQuantityId": ___, "customFields": ___, "decimalQuantity": ___, "deliveryGroup": ___, "deliveryGroupName": ___, "discountAmount": ___, "discountPercentageLevel1": ___, "discountPercentageLevel1WithTaxAmount": ___, "discountPercentageLevel2": ___, "discountPercentageLevel2WithTaxAmount": ___, "discountPercentageLevel3": ___, "discountPercentageLevel3WithTaxAmount": ___, "discountPercentageLevel4": ___, "discountPercentageLevel4WithTaxAmount": ___, "discountWithTaxAmount": ___, "externalReferenceCode": ___, "finalPrice": ___, "finalPriceWithTaxAmount": ___, "id": ___, "name": ___, "options": ___, "orderExternalReferenceCode": ___, "orderId": ___, "printedNote": ___, "promoPrice": ___, "promoPriceWithTaxAmount": ___, "quantity": ___, "replacedSkuExternalReferenceCode": ___, "replacedSkuId": ___, "requestedDeliveryDate": ___, "shippedQuantity": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "sku": ___, "skuExternalReferenceCode": ___, "skuId": ___, "subscription": ___, "unitOfMeasure": ___, "unitOfMeasureKey": ___, "unitPrice": ___, "unitPriceWithTaxAmount": ___, "virtualItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/orderItems/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response patchOrderItemByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			OrderItem orderItem)
		throws Exception {

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/by-externalReferenceCode/{externalReferenceCode}' -d $'{"bookedQuantityId": ___, "customFields": ___, "decimalQuantity": ___, "deliveryGroup": ___, "deliveryGroupName": ___, "discountAmount": ___, "discountPercentageLevel1": ___, "discountPercentageLevel1WithTaxAmount": ___, "discountPercentageLevel2": ___, "discountPercentageLevel2WithTaxAmount": ___, "discountPercentageLevel3": ___, "discountPercentageLevel3WithTaxAmount": ___, "discountPercentageLevel4": ___, "discountPercentageLevel4WithTaxAmount": ___, "discountWithTaxAmount": ___, "externalReferenceCode": ___, "finalPrice": ___, "finalPriceWithTaxAmount": ___, "id": ___, "name": ___, "options": ___, "orderExternalReferenceCode": ___, "orderId": ___, "printedNote": ___, "promoPrice": ___, "promoPriceWithTaxAmount": ___, "quantity": ___, "replacedSkuExternalReferenceCode": ___, "replacedSkuId": ___, "requestedDeliveryDate": ___, "shippedQuantity": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "sku": ___, "skuExternalReferenceCode": ___, "skuId": ___, "subscription": ___, "unitOfMeasure": ___, "unitOfMeasureKey": ___, "unitPrice": ___, "unitPriceWithTaxAmount": ___, "virtualItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/orderItems/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public OrderItem putOrderItemByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			OrderItem orderItem)
		throws Exception {

		return new OrderItem();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/{id}'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/orderItems/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deleteOrderItem(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull @jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/orderItems/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteOrderItemBatch(
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
				OrderItem.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/{id}'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/orderItems/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public OrderItem getOrderItem(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull @jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {

		return new OrderItem();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/{id}' -d $'{"bookedQuantityId": ___, "customFields": ___, "decimalQuantity": ___, "deliveryGroup": ___, "deliveryGroupName": ___, "discountAmount": ___, "discountPercentageLevel1": ___, "discountPercentageLevel1WithTaxAmount": ___, "discountPercentageLevel2": ___, "discountPercentageLevel2WithTaxAmount": ___, "discountPercentageLevel3": ___, "discountPercentageLevel3WithTaxAmount": ___, "discountPercentageLevel4": ___, "discountPercentageLevel4WithTaxAmount": ___, "discountWithTaxAmount": ___, "externalReferenceCode": ___, "finalPrice": ___, "finalPriceWithTaxAmount": ___, "id": ___, "name": ___, "options": ___, "orderExternalReferenceCode": ___, "orderId": ___, "printedNote": ___, "promoPrice": ___, "promoPriceWithTaxAmount": ___, "quantity": ___, "replacedSkuExternalReferenceCode": ___, "replacedSkuId": ___, "requestedDeliveryDate": ___, "shippedQuantity": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "sku": ___, "skuExternalReferenceCode": ___, "skuId": ___, "subscription": ___, "unitOfMeasure": ___, "unitOfMeasureKey": ___, "unitPrice": ___, "unitPriceWithTaxAmount": ___, "virtualItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/orderItems/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response patchOrderItem(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull @jakarta.ws.rs.PathParam("id")
			Long id,
			OrderItem orderItem)
		throws Exception {

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/{id}' -d $'{"bookedQuantityId": ___, "customFields": ___, "decimalQuantity": ___, "deliveryGroup": ___, "deliveryGroupName": ___, "discountAmount": ___, "discountPercentageLevel1": ___, "discountPercentageLevel1WithTaxAmount": ___, "discountPercentageLevel2": ___, "discountPercentageLevel2WithTaxAmount": ___, "discountPercentageLevel3": ___, "discountPercentageLevel3WithTaxAmount": ___, "discountPercentageLevel4": ___, "discountPercentageLevel4WithTaxAmount": ___, "discountWithTaxAmount": ___, "externalReferenceCode": ___, "finalPrice": ___, "finalPriceWithTaxAmount": ___, "id": ___, "name": ___, "options": ___, "orderExternalReferenceCode": ___, "orderId": ___, "printedNote": ___, "promoPrice": ___, "promoPriceWithTaxAmount": ___, "quantity": ___, "replacedSkuExternalReferenceCode": ___, "replacedSkuId": ___, "requestedDeliveryDate": ___, "shippedQuantity": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "sku": ___, "skuExternalReferenceCode": ___, "skuId": ___, "subscription": ___, "unitOfMeasure": ___, "unitOfMeasureKey": ___, "unitPrice": ___, "unitPriceWithTaxAmount": ___, "virtualItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/orderItems/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public OrderItem putOrderItem(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull @jakarta.ws.rs.PathParam("id")
			Long id,
			OrderItem orderItem)
		throws Exception {

		return new OrderItem();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orderItems/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/orderItems/batch")
	@jakarta.ws.rs.Produces("application/json")
	@jakarta.ws.rs.PUT
	@Override
	public Response putOrderItemBatch(
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
			vulcanBatchEngineImportTaskResource.putImportTask(
				OrderItem.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}/orderItems'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}/orderItems"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<OrderItem> getOrderByExternalReferenceCodeOrderItemsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			@jakarta.ws.rs.core.Context Pagination pagination)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}/orderItems' -d $'{"bookedQuantityId": ___, "customFields": ___, "decimalQuantity": ___, "deliveryGroup": ___, "deliveryGroupName": ___, "discountAmount": ___, "discountPercentageLevel1": ___, "discountPercentageLevel1WithTaxAmount": ___, "discountPercentageLevel2": ___, "discountPercentageLevel2WithTaxAmount": ___, "discountPercentageLevel3": ___, "discountPercentageLevel3WithTaxAmount": ___, "discountPercentageLevel4": ___, "discountPercentageLevel4WithTaxAmount": ___, "discountWithTaxAmount": ___, "externalReferenceCode": ___, "finalPrice": ___, "finalPriceWithTaxAmount": ___, "id": ___, "name": ___, "options": ___, "orderExternalReferenceCode": ___, "orderId": ___, "printedNote": ___, "promoPrice": ___, "promoPriceWithTaxAmount": ___, "quantity": ___, "replacedSkuExternalReferenceCode": ___, "replacedSkuId": ___, "requestedDeliveryDate": ___, "shippedQuantity": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "sku": ___, "skuExternalReferenceCode": ___, "skuId": ___, "subscription": ___, "unitOfMeasure": ___, "unitOfMeasureKey": ___, "unitPrice": ___, "unitPriceWithTaxAmount": ___, "virtualItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}/orderItems"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public OrderItem postOrderByExternalReferenceCodeOrderItem(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			OrderItem orderItem)
		throws Exception {

		return new OrderItem();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{id}/orderItems'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/orders/{id}/orderItems")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<OrderItem> getOrderIdOrderItemsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull @jakarta.ws.rs.PathParam("id")
			Long id,
			@jakarta.ws.rs.core.Context Pagination pagination)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{id}/orderItems' -d $'{"bookedQuantityId": ___, "customFields": ___, "decimalQuantity": ___, "deliveryGroup": ___, "deliveryGroupName": ___, "discountAmount": ___, "discountPercentageLevel1": ___, "discountPercentageLevel1WithTaxAmount": ___, "discountPercentageLevel2": ___, "discountPercentageLevel2WithTaxAmount": ___, "discountPercentageLevel3": ___, "discountPercentageLevel3WithTaxAmount": ___, "discountPercentageLevel4": ___, "discountPercentageLevel4WithTaxAmount": ___, "discountWithTaxAmount": ___, "externalReferenceCode": ___, "finalPrice": ___, "finalPriceWithTaxAmount": ___, "id": ___, "name": ___, "options": ___, "orderExternalReferenceCode": ___, "orderId": ___, "printedNote": ___, "promoPrice": ___, "promoPriceWithTaxAmount": ___, "quantity": ___, "replacedSkuExternalReferenceCode": ___, "replacedSkuId": ___, "requestedDeliveryDate": ___, "shippedQuantity": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "sku": ___, "skuExternalReferenceCode": ___, "skuId": ___, "subscription": ___, "unitOfMeasure": ___, "unitOfMeasureKey": ___, "unitPrice": ___, "unitPriceWithTaxAmount": ___, "virtualItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/orders/{id}/orderItems")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public OrderItem postOrderIdOrderItem(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull @jakarta.ws.rs.PathParam("id")
			Long id,
			OrderItem orderItem)
		throws Exception {

		return new OrderItem();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/orderItems/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "OrderItem")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/orders/orderItems/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postOrderIdOrderItemBatch(
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
				OrderItem.class.getName(), callbackURL, null, object)
		).build();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<OrderItem> orderItems,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<OrderItem, OrderItem, Exception>
			orderItemUnsafeFunction = null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			if (parameters.containsKey("externalReferenceCode")) {
				orderItemUnsafeFunction =
					orderItem -> postOrderByExternalReferenceCodeOrderItem(
						(String)parameters.get("externalReferenceCode"),
						orderItem);
			}
			else {
				throw new NotSupportedException(
					"One of the following parameters must be specified: [externalReferenceCode]");
			}
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				orderItemUnsafeFunction =
					orderItem -> putOrderItemByExternalReferenceCode(
						orderItem.getExternalReferenceCode(), orderItem);
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				orderItemUnsafeFunction = orderItem -> {
					OrderItem persistedOrderItem = null;

					try {
						OrderItem getOrderItem =
							getOrderItemByExternalReferenceCode(
								orderItem.getExternalReferenceCode());

						patchOrderItem(
							getOrderItem.getId() != null ?
								getOrderItem.getId() :
									_parseLong(
										(String)parameters.get("orderItemId")),
							orderItem);
					}
					catch (NoSuchModelException noSuchModelException) {
						if (parameters.containsKey("externalReferenceCode")) {
							persistedOrderItem =
								postOrderByExternalReferenceCodeOrderItem(
									(String)parameters.get(
										"externalReferenceCode"),
									orderItem);
						}
						else {
							throw new NotSupportedException(
								"One of the following parameters must be specified: [externalReferenceCode]");
						}
					}

					return persistedOrderItem;
				};
			}
		}

		if (orderItemUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for OrderItem");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				orderItems, orderItemUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				orderItems, orderItemUnsafeFunction::apply);
		}
		else {
			for (OrderItem orderItem : orderItems) {
				orderItemUnsafeFunction.apply(orderItem);
			}
		}
	}

	@Override
	public void delete(
			Collection<OrderItem> orderItems,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<OrderItem, OrderItem, Exception>
			orderItemUnsafeFunction = orderItem -> {
				if (orderItem.getId() != null) {
					try {
						deleteOrderItem(orderItem.getId());

						return orderItem;
					}
					catch (Exception exception) {
						if (orderItem.getExternalReferenceCode() != null) {
							deleteOrderItemByExternalReferenceCode(
								orderItem.getExternalReferenceCode());

							return orderItem;
						}
					}
				}
				else if (orderItem.getExternalReferenceCode() != null) {
					deleteOrderItemByExternalReferenceCode(
						orderItem.getExternalReferenceCode());

					return orderItem;
				}

				throw new UnsupportedOperationException(
					"Unable to delete by external reference code or ID");
			};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				orderItems, orderItemUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				orderItems, orderItemUnsafeFunction::apply);
		}
		else {
			for (OrderItem orderItem : orderItems) {
				orderItemUnsafeFunction.apply(orderItem);
			}
		}
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray("INSERT", "UPSERT");
	}

	public Set<String> getAvailableUpdateStrategies() {
		return SetUtil.fromArray("PARTIAL_UPDATE", "UPDATE");
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return getEntityModel(
			new MultivaluedHashMap<String, Object>(multivaluedMap));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	public String getResourceName() {
		return "OrderItem";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<OrderItem> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getOrderItemsPage(search, filter, pagination, sorts);
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
			Collection<OrderItem> orderItems,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<OrderItem, OrderItem, Exception>
			orderItemUnsafeFunction = null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			orderItemUnsafeFunction = orderItem -> {
				patchOrderItem(
					orderItem.getId() != null ? orderItem.getId() :
						_parseLong((String)parameters.get("orderItemId")),
					orderItem);

				return null;
			};
		}

		if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
			orderItemUnsafeFunction = orderItem -> putOrderItem(
				orderItem.getId() != null ? orderItem.getId() :
					_parseLong((String)parameters.get("orderItemId")),
				orderItem);
		}

		if (orderItemUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for OrderItem");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				orderItems, orderItemUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				orderItems, orderItemUnsafeFunction::apply);
		}
		else {
			for (OrderItem orderItem : orderItems) {
				orderItemUnsafeFunction.apply(orderItem);
			}
		}
	}

	private Long _parseLong(String value) {
		if (value != null) {
			return Long.parseLong(value);
		}

		return null;
	}

	@Override
	public OrderItem getItem(Long id) throws Exception {
		return getOrderItem(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<OrderItem>,
			 UnsafeFunction<OrderItem, OrderItem, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<OrderItem>, UnsafeConsumer<OrderItem, Exception>,
			 Exception> contextBatchUnsafeConsumer) {

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

	protected <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transform(collection, unsafeFunction);
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

	protected <T, R, E extends Throwable> List<R> transformToList(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] transformToLongArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(collection, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransform(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransform(collection, unsafeFunction);
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

	protected AcceptLanguage contextAcceptLanguage;
	protected UnsafeBiConsumer
		<Collection<OrderItem>, UnsafeFunction<OrderItem, OrderItem, Exception>,
		 Exception> contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<OrderItem>, UnsafeConsumer<OrderItem, Exception>, Exception>
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
		LogFactoryUtil.getLog(BaseOrderItemResourceImpl.class);

}