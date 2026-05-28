/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.shipment.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.admin.shipment.dto.v1_0.Shipment;
import com.liferay.headless.commerce.admin.shipment.resource.v1_0.ShipmentResource;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseShipmentResourceImpl
	implements EntityModelResource, ShipmentResource,
			   VulcanBatchEngineTaskItemDelegate<Shipment>,
			   VulcanCRUDItemDelegate<Shipment> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/{shipmentId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the shipment identified by shipmentId. Calls the service with restoreStockQuantity=false; the row is removed without re-crediting the shipped quantity back to the order items."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target shipment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the shipment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "shipmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/shipments/{shipmentId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteShipment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("shipmentId")
			Long shipmentId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/shipments/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteShipmentBatch(
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
				Shipment.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the shipment identified by external reference code. Resolves the row against the current company scope and calls deleteCommerceShipment with restoreStockQuantity=false; raises a not-found error (404) when the ERC is unknown."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; PUT against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/shipments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteShipmentByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/{shipmentId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns the shipment identified by shipmentId. Read-only fetch; the response includes the actions HATEOAS map for the current user."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target shipment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the shipment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "shipmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/shipments/{shipmentId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment getShipment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("shipmentId")
			Long shipmentId)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns the shipment identified by external reference code. Resolves the row against the current company scope; raises a not-found error (404) when the ERC is unknown."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; PUT against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/shipments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment getShipmentByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists shipment rows for the current company through Liferay's SearchUtil with status set to STATUS_ANY (all workflow states returned). Supports OData v4 filter, full-text search across the shipment's indexed fields, sort, and one-based page/pageSize pagination; the filter and sort fields are sourced from shipment's entity model."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. The supported fields are those exposed by the matching Liferay entity model for shipment (typically createDate, modifiedDate, status, and the shipment identifiers). Example -- filter=status eq 2.",
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
				description = "Full-text search expression matched against the resource's indexed fields. Returns the items whose searchable content matches the expression.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression of the form `field:asc` or `field:desc`, comma-separated for multi-field sorting. Supported sort fields depend on the endpoint and are sourced from the matching entity model (typically createDate, modifiedDate, and the status field).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/shipments")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Shipment> getShipmentsPage(
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
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/{shipmentId}' -d $'{"carrier": ___, "customFields": ___, "expectedDate": ___, "externalReferenceCode": ___, "orderExternalReferenceCode": ___, "orderId": ___, "shipmentItems": ___, "shippingAddress": ___, "shippingAddressId": ___, "shippingDate": ___, "shippingMethodId": ___, "trackingNumber": ___, "trackingURL": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the shipment identified by shipmentId using JSON Merge Patch (only the supplied fields are modified). Resolves the row, applies carrier, trackingNumber, trackingURL, expectedDate, shippingDate, and shippingMethodId via updateCommerceShipment, replays the nested shippingAddress and shipmentItems collections when supplied, and optionally rewrites externalReferenceCode. Domain validation (inactive warehouse, malformed address) maps to 422."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target shipment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the shipment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "shipmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/shipments/{shipmentId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment patchShipment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("shipmentId")
			Long shipmentId,
			Shipment shipment)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/by-externalReferenceCode/{externalReferenceCode}' -d $'{"carrier": ___, "customFields": ___, "expectedDate": ___, "externalReferenceCode": ___, "orderExternalReferenceCode": ___, "orderId": ___, "shipmentItems": ___, "shippingAddress": ___, "shippingAddressId": ___, "shippingDate": ___, "shippingMethodId": ___, "trackingNumber": ___, "trackingURL": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the shipment identified by external reference code using JSON Merge Patch (only the supplied fields are modified). Resolves the row, applies carrier, trackingNumber, trackingURL, expectedDate, shippingDate, and shippingMethodId, and replays the nested shippingAddress and shipmentItems collections when supplied. Raises a not-found error (404) when the ERC is unknown; domain validation (inactive warehouse, malformed address) maps to 422."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; PUT against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/shipments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment patchShipmentByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Shipment shipment)
		throws Exception {

		Shipment existingShipment = getShipmentByExternalReferenceCode(
			externalReferenceCode);

		if (shipment.getCarrier() != null) {
			existingShipment.setCarrier(shipment.getCarrier());
		}

		if (shipment.getCustomFields() != null) {
			existingShipment.setCustomFields(shipment.getCustomFields());
		}

		if (shipment.getExpectedDate() != null) {
			existingShipment.setExpectedDate(shipment.getExpectedDate());
		}

		if (shipment.getExternalReferenceCode() != null) {
			existingShipment.setExternalReferenceCode(
				shipment.getExternalReferenceCode());
		}

		if (shipment.getOrderExternalReferenceCode() != null) {
			existingShipment.setOrderExternalReferenceCode(
				shipment.getOrderExternalReferenceCode());
		}

		if (shipment.getOrderId() != null) {
			existingShipment.setOrderId(shipment.getOrderId());
		}

		if (shipment.getShippingAddressId() != null) {
			existingShipment.setShippingAddressId(
				shipment.getShippingAddressId());
		}

		if (shipment.getShippingDate() != null) {
			existingShipment.setShippingDate(shipment.getShippingDate());
		}

		if (shipment.getShippingMethodId() != null) {
			existingShipment.setShippingMethodId(
				shipment.getShippingMethodId());
		}

		if (shipment.getTrackingNumber() != null) {
			existingShipment.setTrackingNumber(shipment.getTrackingNumber());
		}

		if (shipment.getTrackingURL() != null) {
			existingShipment.setTrackingURL(shipment.getTrackingURL());
		}

		preparePatch(shipment, existingShipment);

		return putShipmentByExternalReferenceCode(
			externalReferenceCode, existingShipment);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments' -d $'{"carrier": ___, "customFields": ___, "expectedDate": ___, "externalReferenceCode": ___, "orderExternalReferenceCode": ___, "orderId": ___, "shipmentItems": ___, "shippingAddress": ___, "shippingAddressId": ___, "shippingDate": ___, "shippingMethodId": ___, "trackingNumber": ___, "trackingURL": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a shipment for an existing order identified by orderId (preferred) or orderExternalReferenceCode. Resolves the order, then calls the service cloning the order's groupId, accountId, shippingAddressId, shippingMethodId, and shippingOptionName. Applies the optional carrier, trackingNumber, trackingURL, expectedDate, and shippingDate via updateCommerceShipment; when shippingAddress is supplied the linked address is updated in place, and when shipmentItems is supplied every existing item is replaced. Raises a not-found error (404) when orderExternalReferenceCode is unknown; domain validation (inactive warehouse, invalid shipping address) maps to 422."
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/shipments")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment postShipment(Shipment shipment) throws Exception {
		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/shipments/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postShipmentBatch(
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
				Shipment.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/by-externalReferenceCode/{externalReferenceCode}/status-delivered'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Transitions the shipment identified by external reference code to the DELIVERED workflow state (code 3). Resolves the parent shipment via fetchCommerceShipmentByExternalReferenceCode and calls the service with SHIPMENT_STATUS_DELIVERED; raises a not-found error (404) when the ERC is unknown and a status conflict (400) when the transition is not allowed from the current state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; PUT against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Path(
		"/shipments/by-externalReferenceCode/{externalReferenceCode}/status-delivered"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment postShipmentByExternalReferenceCodeStatusDelivered(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/by-externalReferenceCode/{externalReferenceCode}/status-finish-processing'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Transitions the shipment identified by external reference code to the READY_TO_BE_SHIPPED workflow state (code 1). Resolves the parent shipment via fetchCommerceShipmentByExternalReferenceCode and calls the service with SHIPMENT_STATUS_READY_TO_BE_SHIPPED; raises a not-found error (404) when the ERC is unknown and a status conflict (400) when the transition is not allowed from the current state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; PUT against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Path(
		"/shipments/by-externalReferenceCode/{externalReferenceCode}/status-finish-processing"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment postShipmentByExternalReferenceCodeStatusFinishProcessing(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/by-externalReferenceCode/{externalReferenceCode}/status-shipped'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Transitions the shipment identified by external reference code to the SHIPPED workflow state (code 2). Resolves the parent shipment via fetchCommerceShipmentByExternalReferenceCode and calls the service with SHIPMENT_STATUS_SHIPPED; raises a not-found error (404) when the ERC is unknown and a status conflict (400) when the transition is not allowed from the current state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; PUT against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Path(
		"/shipments/by-externalReferenceCode/{externalReferenceCode}/status-shipped"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment postShipmentByExternalReferenceCodeStatusShipped(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/{shipmentId}/status-delivered'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Transitions the shipment identified by shipmentId to the DELIVERED workflow state (code 3). Calls the service with SHIPMENT_STATUS_DELIVERED; raises a status conflict (400) when the transition is not allowed from the current state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target shipment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the shipment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "shipmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Path("/shipments/{shipmentId}/status-delivered")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment postShipmentStatusDelivered(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("shipmentId")
			Long shipmentId)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/{shipmentId}/status-finish-processing'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Transitions the shipment identified by shipmentId to the READY_TO_BE_SHIPPED workflow state (code 1). Calls the service with SHIPMENT_STATUS_READY_TO_BE_SHIPPED; raises a status conflict (400) when the transition is not allowed from the current state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target shipment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the shipment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "shipmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Path("/shipments/{shipmentId}/status-finish-processing")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment postShipmentStatusFinishProcessing(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("shipmentId")
			Long shipmentId)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/{shipmentId}/status-shipped'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Transitions the shipment identified by shipmentId to the SHIPPED workflow state (code 2). Calls the service with SHIPMENT_STATUS_SHIPPED; raises a status conflict (400) when the transition is not allowed from the current state."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target shipment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the shipment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "shipmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Path("/shipments/{shipmentId}/status-shipped")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Shipment postShipmentStatusShipped(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("shipmentId")
			Long shipmentId)
		throws Exception {

		return new Shipment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. The supported fields are those exposed by the matching Liferay entity model for shipment (typically createDate, modifiedDate, status, and the shipment identifiers). Example -- filter=status eq 2.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Full-text search expression matched against the resource's indexed fields. Returns the items whose searchable content matches the expression.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression of the form `field:asc` or `field:desc`, comma-separated for multi-field sorting. Supported sort fields depend on the endpoint and are sourced from the matching entity model (typically createDate, modifiedDate, and the status field).",
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/shipments/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postShipmentsPageExportBatch(
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
				Shipment.class.getName(), callbackURL, contentType, fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-shipment/v1.0/shipments/by-externalReferenceCode/{externalReferenceCode}' -d $'{"carrier": ___, "customFields": ___, "expectedDate": ___, "externalReferenceCode": ___, "orderExternalReferenceCode": ___, "orderId": ___, "shipmentItems": ___, "shippingAddress": ___, "shippingAddressId": ___, "shippingDate": ___, "shippingMethodId": ___, "trackingNumber": ___, "trackingURL": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Upserts the shipment identified by external reference code. When the ERC resolves, the row is updated and the nested shippingAddress and shipmentItems collections are replayed; when the ERC is unknown, the request body's orderId is used to resolve the source order and addCommerceShipment is invoked to seed a new shipment from the order's groupId, accountId, shippingAddressId, shippingMethodId, and shippingOptionName. Domain validation maps to 422."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; PUT against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Shipment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/shipments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Shipment putShipmentByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Shipment shipment)
		throws Exception {

		return new Shipment();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Shipment> shipments,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Shipment, Shipment, Exception> shipmentUnsafeFunction =
			null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			shipmentUnsafeFunction = shipment -> postShipment(shipment);
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				shipmentUnsafeFunction = shipment -> {
					Shipment getShipment = null;
					Shipment persistedShipment = null;

					try {
						getShipment = getShipmentByExternalReferenceCode(
							shipment.getExternalReferenceCode());

						persistedShipment = patchShipment(
							getShipment.getId(), shipment);
					}
					catch (NoSuchModelException noSuchModelException) {
						persistedShipment = postShipment(shipment);
					}

					return persistedShipment;
				};
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				shipmentUnsafeFunction = shipment -> {
					Shipment persistedShipment = null;

					persistedShipment = putShipmentByExternalReferenceCode(
						shipment.getExternalReferenceCode(), shipment);

					return persistedShipment;
				};
			}
		}

		if (shipmentUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for Shipment");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				shipments, shipmentUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				shipments, shipmentUnsafeFunction::apply);
		}
		else {
			for (Shipment shipment : shipments) {
				shipmentUnsafeFunction.apply(shipment);
			}
		}
	}

	@Override
	public void delete(
			Collection<Shipment> shipments,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Shipment, Shipment, Exception> shipmentUnsafeFunction =
			shipment -> {
				if (shipment.getId() != null) {
					try {
						deleteShipment(shipment.getId());

						return shipment;
					}
					catch (Exception exception) {
						if (shipment.getExternalReferenceCode() != null) {
							deleteShipmentByExternalReferenceCode(
								shipment.getExternalReferenceCode());

							return shipment;
						}
					}
				}
				else if (shipment.getExternalReferenceCode() != null) {
					deleteShipmentByExternalReferenceCode(
						shipment.getExternalReferenceCode());

					return shipment;
				}

				throw new UnsupportedOperationException(
					"Unable to delete by external reference code or ID");
			};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				shipments, shipmentUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				shipments, shipmentUnsafeFunction::apply);
		}
		else {
			for (Shipment shipment : shipments) {
				shipmentUnsafeFunction.apply(shipment);
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
		return "Shipment";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Shipment> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getShipmentsPage(search, filter, pagination, sorts);
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
			Collection<Shipment> shipments,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Shipment, Shipment, Exception> shipmentUnsafeFunction =
			null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			shipmentUnsafeFunction = shipment -> patchShipment(
				shipment.getId(), shipment);
		}

		if (shipmentUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for Shipment");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				shipments, shipmentUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				shipments, shipmentUnsafeFunction::apply);
		}
		else {
			for (Shipment shipment : shipments) {
				shipmentUnsafeFunction.apply(shipment);
			}
		}
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public Shipment getItem(Long id) throws Exception {
		return getShipment(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<Shipment>,
			 UnsafeFunction<Shipment, Shipment, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Shipment>, UnsafeConsumer<Shipment, Exception>,
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
		return "headless-commerce-admin-shipment";
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

	protected void preparePatch(Shipment shipment, Shipment existingShipment) {
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
		<Collection<Shipment>, UnsafeFunction<Shipment, Shipment, Exception>,
		 Exception> contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Shipment>, UnsafeConsumer<Shipment, Exception>, Exception>
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
		LogFactoryUtil.getLog(BaseShipmentResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:-212032286