/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CouponCode;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartResource;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseCartResourceImpl
	implements CartResource, EntityModelResource,
			   VulcanBatchEngineTaskItemDelegate<Cart>,
			   VulcanCRUDItemDelegate<Cart> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/{cartId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the cart addressed by its internal identifier. The cart must be in the Open state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of a cart. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the cart's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "cartId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/carts/{cartId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deleteCart(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("cartId")
			Long cartId)
		throws Exception {

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/carts/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteCartBatch(
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
				Cart.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the cart addressed by external reference code. The cart must be in the Open state; deleting a placed order raises a status conflict."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/carts/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deleteCartByExternalReferenceCode(
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
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/{cartId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the cart addressed by ID with its items, addresses, summary, and status."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of a cart. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the cart's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "cartId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/carts/{cartId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart getCart(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("cartId")
			Long cartId)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the cart addressed by external reference code with its items, addresses, summary, and status."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/carts/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart getCartByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/by-externalReferenceCode/{externalReferenceCode}/payment-url'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns the encrypted payment-gateway redirect URL for the cart addressed by external reference code. The optional callbackURL is encoded into the encrypted token; the response is a plain-text URL."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Optional URL the payment gateway redirects the buyer to after the payment flow completes or is abandoned. When omitted, the gateway-default callback configured on the channel is used.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/carts/by-externalReferenceCode/{externalReferenceCode}/payment-url"
	)
	@jakarta.ws.rs.Produces(
		{"application/json", "application/xml", "text/plain"}
	)
	@Override
	public String getCartByExternalReferenceCodePaymentUrl(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL)
		throws Exception {

		return StringPool.BLANK;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/{cartId}/payment-url'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns the encrypted payment-gateway redirect URL for the cart addressed by ID. The optional callbackURL is encoded into the encrypted token; the response is a plain-text URL."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of a cart. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the cart's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "cartId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Optional URL the payment gateway redirects the buyer to after the payment flow completes or is abandoned. When omitted, the gateway-default callback configured on the channel is used.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/carts/{cartId}/payment-url")
	@jakarta.ws.rs.Produces(
		{"application/json", "application/xml", "text/plain"}
	)
	@Override
	public String getCartPaymentURL(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("cartId")
			Long cartId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL)
		throws Exception {

		return StringPool.BLANK;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/channels/{channelId}/account/{accountId}/carts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists open carts (the Open state carts) for the account, scoped to the channel, both addressed by ID. Filterable by accountId, orderStatus, createDate, modifiedDate, orderDate, id, orderId, account, author, externalReferenceCode, name, orderType, and purchaseOrderNumber via CartEntityModel; searchable through the cart index; pageable."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the account whose carts are being queried in the given channel. Counterpart to the by-externalReferenceCode variant.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the CommerceChannel that scopes the carts. Counterpart to the by-externalReferenceCode variant.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and come from the matching entity model. For /carts list endpoints, supported fields include accountId, orderStatus, createDate, modifiedDate, orderDate, id, orderId, account, author, externalReferenceCode, name, orderType, and purchaseOrderNumber. For /carts/<cartId>/attachments, restricted, dateCreated, dateModified, priority, commerceOrderId, externalReferenceCode, title, and type are supported. For /carts/<cartId>/items, quantity, name, sku, and unitOfMeasure are supported. Example -- filter=accountId eq 12345.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page index. Combined with pageSize to paginate the result set; defaults to 1 when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items per page. Defaults to the portal's configured page size when omitted; capped by the portal configuration to prevent unbounded reads.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Full-text search query. For /carts list endpoints the search corpus is the cart index (matches against account, author, name, and other indexed fields). For nested /items and /attachments endpoints the corpus is the corresponding cart item or attachment index.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression. Comma-separated `field:direction` pairs (direction `asc` or `desc`). Supported sort fields are the same as the filterable fields documented on filter for the same endpoint.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/channels/{channelId}/account/{accountId}/carts")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Cart> getChannelAccountCartsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
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
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/account/by-externalReferenceCode/{accountExternalReferenceCode}/carts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists open carts (the Open state carts) for the account, scoped to the channel, both addressed by external reference code. Filterable by accountId, orderStatus, createDate, modifiedDate, orderDate, id, orderId, account, author, externalReferenceCode, name, orderType, and purchaseOrderNumber via CartEntityModel; searchable through the cart index; pageable."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code identifying the account whose carts are being queried in the given channel. Resolved against the company scope before filtering carts.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code identifying the CommerceChannel that scopes the carts. The channel determines currency, available payment methods, available shipping methods, and visibility rules.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and come from the matching entity model. For /carts list endpoints, supported fields include accountId, orderStatus, createDate, modifiedDate, orderDate, id, orderId, account, author, externalReferenceCode, name, orderType, and purchaseOrderNumber. For /carts/<cartId>/attachments, restricted, dateCreated, dateModified, priority, commerceOrderId, externalReferenceCode, title, and type are supported. For /carts/<cartId>/items, quantity, name, sku, and unitOfMeasure are supported. Example -- filter=accountId eq 12345.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page index. Combined with pageSize to paginate the result set; defaults to 1 when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items per page. Defaults to the portal's configured page size when omitted; capped by the portal configuration to prevent unbounded reads.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Full-text search query. For /carts list endpoints the search corpus is the cart index (matches against account, author, name, and other indexed fields). For nested /items and /attachments endpoints the corpus is the corresponding cart item or attachment index.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression. Comma-separated `field:direction` pairs (direction `asc` or `desc`). Supported sort fields are the same as the filterable fields documented on filter for the same endpoint.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{channelExternalReferenceCode}/account/by-externalReferenceCode/{accountExternalReferenceCode}/carts"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Cart>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("accountExternalReferenceCode")
				String accountExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("channelExternalReferenceCode")
				String channelExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("search")
				String search,
				@jakarta.ws.rs.core.Context
					com.liferay.portal.kernel.search.filter.Filter filter,
				@jakarta.ws.rs.core.Context Pagination pagination,
				@jakarta.ws.rs.core.Context
					com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/channels/{channelId}/carts'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists open carts (the Open state carts) scoped to the channel addressed by ID. Filterable, searchable, and pageable through CartEntityModel."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the CommerceChannel that scopes the carts. Counterpart to the by-externalReferenceCode variant.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and come from the matching entity model. For /carts list endpoints, supported fields include accountId, orderStatus, createDate, modifiedDate, orderDate, id, orderId, account, author, externalReferenceCode, name, orderType, and purchaseOrderNumber. For /carts/<cartId>/attachments, restricted, dateCreated, dateModified, priority, commerceOrderId, externalReferenceCode, title, and type are supported. For /carts/<cartId>/items, quantity, name, sku, and unitOfMeasure are supported. Example -- filter=accountId eq 12345.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page index. Combined with pageSize to paginate the result set; defaults to 1 when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items per page. Defaults to the portal's configured page size when omitted; capped by the portal configuration to prevent unbounded reads.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Full-text search query. For /carts list endpoints the search corpus is the cart index (matches against account, author, name, and other indexed fields). For nested /items and /attachments endpoints the corpus is the corresponding cart item or attachment index.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression. Comma-separated `field:direction` pairs (direction `asc` or `desc`). Supported sort fields are the same as the filterable fields documented on filter for the same endpoint.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/channels/{channelId}/carts")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Cart> getChannelCartsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
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
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/{cartId}' -d $'{"accountId": ___, "attachments": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "cartItems": ___, "couponCode": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermId": ___, "errorMessages": ___, "name": ___, "notes": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingMethod": ___, "shippingOption": ___, "steps": ___, "summary": ___, "useAsBilling": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the cart addressed by ID with JSON Merge Patch semantics (only supplied fields are modified). Mutates shipping/billing addresses, delivery/payment terms, order type, coupon, shipping method, and payment method. Requires the cart to be in the Open state; raises a status conflict if the cart has been placed."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of a cart. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the cart's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "cartId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/carts/{cartId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart patchCart(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("cartId")
			Long cartId,
			Cart cart)
		throws Exception {

		Cart existingCart = getCart(cartId);

		if (cart.getAccountId() != null) {
			existingCart.setAccountId(cart.getAccountId());
		}

		if (cart.getBillingAddressExternalReferenceCode() != null) {
			existingCart.setBillingAddressExternalReferenceCode(
				cart.getBillingAddressExternalReferenceCode());
		}

		if (cart.getBillingAddressId() != null) {
			existingCart.setBillingAddressId(cart.getBillingAddressId());
		}

		if (cart.getCouponCode() != null) {
			existingCart.setCouponCode(cart.getCouponCode());
		}

		if (cart.getCurrencyCode() != null) {
			existingCart.setCurrencyCode(cart.getCurrencyCode());
		}

		if (cart.getCurrencyExternalReferenceCode() != null) {
			existingCart.setCurrencyExternalReferenceCode(
				cart.getCurrencyExternalReferenceCode());
		}

		if (cart.getCurrencyId() != null) {
			existingCart.setCurrencyId(cart.getCurrencyId());
		}

		if (cart.getCustomFields() != null) {
			existingCart.setCustomFields(cart.getCustomFields());
		}

		if (cart.getDeliveryTermId() != null) {
			existingCart.setDeliveryTermId(cart.getDeliveryTermId());
		}

		if (cart.getErrorMessages() != null) {
			existingCart.setErrorMessages(cart.getErrorMessages());
		}

		if (cart.getName() != null) {
			existingCart.setName(cart.getName());
		}

		if (cart.getOrderTypeExternalReferenceCode() != null) {
			existingCart.setOrderTypeExternalReferenceCode(
				cart.getOrderTypeExternalReferenceCode());
		}

		if (cart.getOrderTypeId() != null) {
			existingCart.setOrderTypeId(cart.getOrderTypeId());
		}

		if (cart.getPaymentMethod() != null) {
			existingCart.setPaymentMethod(cart.getPaymentMethod());
		}

		if (cart.getPaymentTermId() != null) {
			existingCart.setPaymentTermId(cart.getPaymentTermId());
		}

		if (cart.getPrintedNote() != null) {
			existingCart.setPrintedNote(cart.getPrintedNote());
		}

		if (cart.getPurchaseOrderNumber() != null) {
			existingCart.setPurchaseOrderNumber(cart.getPurchaseOrderNumber());
		}

		if (cart.getRequestedDeliveryDate() != null) {
			existingCart.setRequestedDeliveryDate(
				cart.getRequestedDeliveryDate());
		}

		if (cart.getShippingAddressExternalReferenceCode() != null) {
			existingCart.setShippingAddressExternalReferenceCode(
				cart.getShippingAddressExternalReferenceCode());
		}

		if (cart.getShippingAddressId() != null) {
			existingCart.setShippingAddressId(cart.getShippingAddressId());
		}

		if (cart.getShippingMethod() != null) {
			existingCart.setShippingMethod(cart.getShippingMethod());
		}

		if (cart.getShippingOption() != null) {
			existingCart.setShippingOption(cart.getShippingOption());
		}

		if (cart.getUseAsBilling() != null) {
			existingCart.setUseAsBilling(cart.getUseAsBilling());
		}

		preparePatch(cart, existingCart);

		return putCart(cartId, existingCart);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/by-externalReferenceCode/{externalReferenceCode}' -d $'{"accountId": ___, "attachments": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "cartItems": ___, "couponCode": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermId": ___, "errorMessages": ___, "name": ___, "notes": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingMethod": ___, "shippingOption": ___, "steps": ___, "summary": ___, "useAsBilling": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the cart addressed by external reference code with JSON Merge Patch semantics (only supplied fields are modified). Mutates shipping/billing addresses (by ID or ERC), delivery/payment terms, order type, coupon, shipping method, and payment method. Requires the cart to be in the Open state; raises a status conflict if the cart has been placed."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/carts/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart patchCartByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Cart cart)
		throws Exception {

		Cart existingCart = getCartByExternalReferenceCode(
			externalReferenceCode);

		if (cart.getAccountId() != null) {
			existingCart.setAccountId(cart.getAccountId());
		}

		if (cart.getBillingAddressExternalReferenceCode() != null) {
			existingCart.setBillingAddressExternalReferenceCode(
				cart.getBillingAddressExternalReferenceCode());
		}

		if (cart.getBillingAddressId() != null) {
			existingCart.setBillingAddressId(cart.getBillingAddressId());
		}

		if (cart.getCouponCode() != null) {
			existingCart.setCouponCode(cart.getCouponCode());
		}

		if (cart.getCurrencyCode() != null) {
			existingCart.setCurrencyCode(cart.getCurrencyCode());
		}

		if (cart.getCurrencyExternalReferenceCode() != null) {
			existingCart.setCurrencyExternalReferenceCode(
				cart.getCurrencyExternalReferenceCode());
		}

		if (cart.getCurrencyId() != null) {
			existingCart.setCurrencyId(cart.getCurrencyId());
		}

		if (cart.getCustomFields() != null) {
			existingCart.setCustomFields(cart.getCustomFields());
		}

		if (cart.getDeliveryTermId() != null) {
			existingCart.setDeliveryTermId(cart.getDeliveryTermId());
		}

		if (cart.getErrorMessages() != null) {
			existingCart.setErrorMessages(cart.getErrorMessages());
		}

		if (cart.getName() != null) {
			existingCart.setName(cart.getName());
		}

		if (cart.getOrderTypeExternalReferenceCode() != null) {
			existingCart.setOrderTypeExternalReferenceCode(
				cart.getOrderTypeExternalReferenceCode());
		}

		if (cart.getOrderTypeId() != null) {
			existingCart.setOrderTypeId(cart.getOrderTypeId());
		}

		if (cart.getPaymentMethod() != null) {
			existingCart.setPaymentMethod(cart.getPaymentMethod());
		}

		if (cart.getPaymentTermId() != null) {
			existingCart.setPaymentTermId(cart.getPaymentTermId());
		}

		if (cart.getPrintedNote() != null) {
			existingCart.setPrintedNote(cart.getPrintedNote());
		}

		if (cart.getPurchaseOrderNumber() != null) {
			existingCart.setPurchaseOrderNumber(cart.getPurchaseOrderNumber());
		}

		if (cart.getRequestedDeliveryDate() != null) {
			existingCart.setRequestedDeliveryDate(
				cart.getRequestedDeliveryDate());
		}

		if (cart.getShippingAddressExternalReferenceCode() != null) {
			existingCart.setShippingAddressExternalReferenceCode(
				cart.getShippingAddressExternalReferenceCode());
		}

		if (cart.getShippingAddressId() != null) {
			existingCart.setShippingAddressId(cart.getShippingAddressId());
		}

		if (cart.getShippingMethod() != null) {
			existingCart.setShippingMethod(cart.getShippingMethod());
		}

		if (cart.getShippingOption() != null) {
			existingCart.setShippingOption(cart.getShippingOption());
		}

		if (cart.getUseAsBilling() != null) {
			existingCart.setUseAsBilling(cart.getUseAsBilling());
		}

		preparePatch(cart, existingCart);

		return putCartByExternalReferenceCode(
			externalReferenceCode, existingCart);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/by-externalReferenceCode/{externalReferenceCode}/checkout'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Runs the checkout validator chain against the cart addressed by external reference code (billing address, shipping method, payment method, items) through the checkout engine and transitions the cart from the Open state to the In Progress state. Synchronous; returns the updated cart on success."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Path(
		"/carts/by-externalReferenceCode/{externalReferenceCode}/checkout"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart postCartByExternalReferenceCodeCheckout(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/by-externalReferenceCode/{externalReferenceCode}/coupon-code' -d $'{"code": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Applies a coupon code (CouponCode.code) to the cart addressed by external reference code and returns the updated cart with the recalculated Summary. Requires the cart to be in the Open state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/carts/by-externalReferenceCode/{externalReferenceCode}/coupon-code"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart postCartByExternalReferenceCodeCouponCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			CouponCode couponCode)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/{cartId}/checkout'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Runs the checkout validator chain against the cart addressed by ID (billing address, shipping method, payment method, items) through the checkout engine and transitions the cart from the Open state to the In Progress state. Synchronous; returns the updated cart on success."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of a cart. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the cart's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "cartId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Path("/carts/{cartId}/checkout")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart postCartCheckout(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("cartId")
			Long cartId)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/{cartId}/coupon-code' -d $'{"code": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Applies a coupon code (CouponCode.code) to the cart addressed by ID and returns the updated cart with the recalculated Summary. Requires the cart to be in the Open state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of a cart. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the cart's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "cartId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/carts/{cartId}/coupon-code")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart postCartCouponCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("cartId")
			Long cartId,
			CouponCode couponCode)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/channels/{channelId}/carts' -d $'{"accountId": ___, "attachments": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "cartItems": ___, "couponCode": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermId": ___, "errorMessages": ___, "name": ___, "notes": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingMethod": ___, "shippingOption": ___, "steps": ___, "summary": ___, "useAsBilling": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a new cart under the channel addressed by ID, then applies the provided Cart fields (account, currency, addresses, terms, items)."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the CommerceChannel that scopes the carts. Counterpart to the by-externalReferenceCode variant.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/channels/{channelId}/carts")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart postChannelCart(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			Cart cart)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/channels/by-externalReferenceCode/{externalReferenceCode}/carts' -d $'{"accountId": ___, "attachments": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "cartItems": ___, "couponCode": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermId": ___, "errorMessages": ___, "name": ___, "notes": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingMethod": ___, "shippingOption": ___, "steps": ___, "summary": ___, "useAsBilling": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a new cart under the channel addressed by external reference code, then applies the provided Cart fields (account, currency, addresses, terms, items)."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{externalReferenceCode}/carts"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Cart postChannelCartByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Cart cart)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/{cartId}' -d $'{"accountId": ___, "attachments": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "cartItems": ___, "couponCode": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermId": ___, "errorMessages": ___, "name": ___, "notes": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingMethod": ___, "shippingOption": ___, "steps": ___, "summary": ___, "useAsBilling": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Replaces the cart addressed by ID; the cart must be in the Open state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of a cart. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the cart's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "cartId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/carts/{cartId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Cart putCart(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("cartId")
			Long cartId,
			Cart cart)
		throws Exception {

		return new Cart();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/carts/batch")
	@jakarta.ws.rs.Produces("application/json")
	@jakarta.ws.rs.PUT
	@Override
	public Response putCartBatch(
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
				Cart.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-delivery-cart/v1.0/carts/by-externalReferenceCode/{externalReferenceCode}' -d $'{"accountId": ___, "attachments": ___, "billingAddress": ___, "billingAddressExternalReferenceCode": ___, "billingAddressId": ___, "cartItems": ___, "couponCode": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "customFields": ___, "deliveryTermId": ___, "errorMessages": ___, "name": ___, "notes": ___, "orderTypeExternalReferenceCode": ___, "orderTypeId": ___, "paymentMethod": ___, "paymentTermId": ___, "printedNote": ___, "purchaseOrderNumber": ___, "requestedDeliveryDate": ___, "shippingAddress": ___, "shippingAddressExternalReferenceCode": ___, "shippingAddressId": ___, "shippingMethod": ___, "shippingOption": ___, "steps": ___, "summary": ___, "useAsBilling": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Replaces the cart addressed by external reference code. PUT is upsert when the externalReferenceCode is unknown; the cart must be (or become) in the Open state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Cart")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/carts/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Cart putCartByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Cart cart)
		throws Exception {

		return new Cart();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Cart> carts, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Cart, Cart, Exception> cartUnsafeFunction = null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				cartUnsafeFunction = cart -> {
					Cart persistedCart = null;

					persistedCart = putCartByExternalReferenceCode(
						cart.getExternalReferenceCode(), cart);

					return persistedCart;
				};
			}
		}

		if (cartUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for Cart");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(carts, cartUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(carts, cartUnsafeFunction::apply);
		}
		else {
			for (Cart cart : carts) {
				cartUnsafeFunction.apply(cart);
			}
		}
	}

	@Override
	public void delete(
			Collection<Cart> carts, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Cart, Cart, Exception> cartUnsafeFunction = cart -> {
			if (cart.getId() != null) {
				try {
					deleteCart(cart.getId());

					return cart;
				}
				catch (Exception exception) {
					if (cart.getExternalReferenceCode() != null) {
						deleteCartByExternalReferenceCode(
							cart.getExternalReferenceCode());

						return cart;
					}
				}
			}
			else if (cart.getExternalReferenceCode() != null) {
				deleteCartByExternalReferenceCode(
					cart.getExternalReferenceCode());

				return cart;
			}

			throw new UnsupportedOperationException(
				"Unable to delete by external reference code or ID");
		};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(carts, cartUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(carts, cartUnsafeFunction::apply);
		}
		else {
			for (Cart cart : carts) {
				cartUnsafeFunction.apply(cart);
			}
		}
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray("UPSERT");
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

	public String getResourceName() {
		return "Cart";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Cart> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

			@Override
			public boolean isAcceptAllLanguages() {
				if (ExportImportThreadLocal.isExportInProcess()) {
					return true;
				}

				return AcceptLanguage.super.isAcceptAllLanguages();
			}

		};
	}

	@Override
	public void update(
			Collection<Cart> carts, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Cart, Cart, Exception> cartUnsafeFunction = null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			cartUnsafeFunction = cart -> patchCart(cart.getId(), cart);
		}

		if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
			cartUnsafeFunction = cart -> putCart(cart.getId(), cart);
		}

		if (cartUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for Cart");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(carts, cartUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(carts, cartUnsafeFunction::apply);
		}
		else {
			for (Cart cart : carts) {
				cartUnsafeFunction.apply(cart);
			}
		}
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public Cart getItem(Long id) throws Exception {
		return getCart(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<Cart>, UnsafeFunction<Cart, Cart, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Cart>, UnsafeConsumer<Cart, Exception>, Exception>
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
		return "headless-commerce-delivery-cart";
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

	protected void preparePatch(Cart cart, Cart existingCart) {
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
		<Collection<Cart>, UnsafeFunction<Cart, Cart, Exception>, Exception>
			contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Cart>, UnsafeConsumer<Cart, Exception>, Exception>
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
		LogFactoryUtil.getLog(BaseCartResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:561245825