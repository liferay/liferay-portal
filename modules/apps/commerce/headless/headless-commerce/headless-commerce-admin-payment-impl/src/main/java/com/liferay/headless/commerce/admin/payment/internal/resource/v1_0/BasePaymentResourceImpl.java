/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.admin.payment.dto.v1_0.Payment;
import com.liferay.headless.commerce.admin.payment.resource.v1_0.PaymentResource;
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
public abstract class BasePaymentResourceImpl
	implements EntityModelResource, PaymentResource,
			   VulcanBatchEngineTaskItemDelegate<Payment>,
			   VulcanCRUDItemDelegate<Payment> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/{id}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the payment identified by internal ID. Delegates directly to CommercePaymentEntryService.deleteCommercePaymentEntry. Returns 204 on success."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target payment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the payment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/payments/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deletePayment(
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
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/payments/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deletePaymentBatch(
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
				Payment.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the payment identified by external reference code. Looks up the entry via CommercePaymentEntryService.fetchCommercePaymentEntryByExternalReferenceCode for the current company and delegates to deleteCommercePaymentEntry. Returns 204 on success; raises NoSuchPaymentEntryException (404) when no record matches the supplied code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target payment on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the company scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/payments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Response deletePaymentByExternalReferenceCode(
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
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/{id}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns the payment identified by internal ID. Delegates to CommercePaymentEntryService.getCommercePaymentEntry and converts the result through PaymentDTOConverter, populating the formatted amount, the localized paymentStatusStatus envelope, the type label, and the HATEOAS actions map."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target payment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the payment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/payments/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Payment getPayment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {

		return new Payment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns the payment identified by external reference code. Looks up the entry via CommercePaymentEntryService.fetchCommercePaymentEntryByExternalReferenceCode for the current company and converts it through PaymentDTOConverter, populating the formatted amount, the localized paymentStatusStatus envelope, the type label, and the HATEOAS actions map. Raises NoSuchPaymentEntryException (404) when no record matches the supplied code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target payment on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the company scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/payments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Payment getPaymentByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Payment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists payments visible to the authenticated user as a permission-filtered page. Searches the CommercePaymentEntry index for the current company and applies the OData filter, full-text search, and sort expression. Filterable fields -- paymentStatus, type, reasonKey, relatedItemId. Sortable fields -- createDate, id, paymentStatus, reasonKey, relatedItemId, type. Search is matched against the indexed fields of the entry."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the payment result set. Filterable fields sourced from the payment entity model -- paymentStatus, type, reasonKey, and relatedItemId (the FK to the parent order or subscription, indexed as classPK). Example -- filter=paymentStatus eq 0 and type eq 1.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page index. Combined with pageSize to paginate the payment result set; defaults to 1 when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of payments per page. Defaults to the portal's configured page size when omitted; capped by the portal configuration to prevent unbounded reads.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Full-text search expression matched against the payment's indexed fields. Returns the payments whose searchable content matches the expression.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression of the form `field:asc` or `field:desc`, comma-separated for multi-field sorting. Sortable fields sourced from the payment entity model -- createDate, id, paymentStatus, reasonKey, relatedItemId, and type.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/payments")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Payment> getPaymentsPage(
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
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/{id}' -d $'{"amount": ___, "callbackURL": ___, "cancelURL": ___, "channelId": ___, "comment": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "errorMessages": ___, "externalReferenceCode": ___, "languageId": ___, "payload": ___, "paymentIntegrationKey": ___, "paymentStatus": ___, "reasonKey": ___, "redirectURL": ___, "relatedItemId": ___, "relatedItemName": ___, "relatedItemNameLabel": ___, "transactionCode": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Partially updates the payment identified by internal ID (JSON Merge Patch -- only the supplied fields are modified; unspecified fields retain their existing value). Resolves the entry via CommercePaymentEntryService.getCommercePaymentEntry and delegates to updateCommercePaymentEntry, blending the request body against the stored record. Currency lookup falls back to the stored `currencyCode` when the request does not resolve a new currency. Raises CommercePaymentEntry*Exception (400) when validation fails."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target payment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the payment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/payments/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Payment patchPayment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id,
			Payment payment)
		throws Exception {

		return new Payment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/by-externalReferenceCode/{externalReferenceCode}' -d $'{"amount": ___, "callbackURL": ___, "cancelURL": ___, "channelId": ___, "comment": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "errorMessages": ___, "externalReferenceCode": ___, "languageId": ___, "payload": ___, "paymentIntegrationKey": ___, "paymentStatus": ___, "reasonKey": ___, "redirectURL": ___, "relatedItemId": ___, "relatedItemName": ___, "relatedItemNameLabel": ___, "transactionCode": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Partially updates the payment identified by external reference code (JSON Merge Patch -- only the supplied fields are modified; unspecified fields retain their existing value). Resolves the entry via CommercePaymentEntryService.fetchCommercePaymentEntryByExternalReferenceCode and delegates to updateCommercePaymentEntry, blending the request body against the stored record. Currency lookup falls back to the stored `currencyCode` when the request does not resolve a new currency. Raises NoSuchPaymentEntryException (404) when no record matches; raises CommercePaymentEntry*Exception (400) when validation fails."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target payment on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the company scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/payments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Payment patchPaymentByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Payment payment)
		throws Exception {

		Payment existingPayment = getPaymentByExternalReferenceCode(
			externalReferenceCode);

		if (payment.getAmount() != null) {
			existingPayment.setAmount(payment.getAmount());
		}

		if (payment.getCallbackURL() != null) {
			existingPayment.setCallbackURL(payment.getCallbackURL());
		}

		if (payment.getCancelURL() != null) {
			existingPayment.setCancelURL(payment.getCancelURL());
		}

		if (payment.getChannelId() != null) {
			existingPayment.setChannelId(payment.getChannelId());
		}

		if (payment.getComment() != null) {
			existingPayment.setComment(payment.getComment());
		}

		if (payment.getCurrencyCode() != null) {
			existingPayment.setCurrencyCode(payment.getCurrencyCode());
		}

		if (payment.getCurrencyExternalReferenceCode() != null) {
			existingPayment.setCurrencyExternalReferenceCode(
				payment.getCurrencyExternalReferenceCode());
		}

		if (payment.getCurrencyId() != null) {
			existingPayment.setCurrencyId(payment.getCurrencyId());
		}

		if (payment.getErrorMessages() != null) {
			existingPayment.setErrorMessages(payment.getErrorMessages());
		}

		if (payment.getExternalReferenceCode() != null) {
			existingPayment.setExternalReferenceCode(
				payment.getExternalReferenceCode());
		}

		if (payment.getLanguageId() != null) {
			existingPayment.setLanguageId(payment.getLanguageId());
		}

		if (payment.getPayload() != null) {
			existingPayment.setPayload(payment.getPayload());
		}

		if (payment.getPaymentIntegrationKey() != null) {
			existingPayment.setPaymentIntegrationKey(
				payment.getPaymentIntegrationKey());
		}

		if (payment.getPaymentStatus() != null) {
			existingPayment.setPaymentStatus(payment.getPaymentStatus());
		}

		if (payment.getReasonKey() != null) {
			existingPayment.setReasonKey(payment.getReasonKey());
		}

		if (payment.getRedirectURL() != null) {
			existingPayment.setRedirectURL(payment.getRedirectURL());
		}

		if (payment.getRelatedItemId() != null) {
			existingPayment.setRelatedItemId(payment.getRelatedItemId());
		}

		if (payment.getRelatedItemName() != null) {
			existingPayment.setRelatedItemName(payment.getRelatedItemName());
		}

		if (payment.getRelatedItemNameLabel() != null) {
			existingPayment.setRelatedItemNameLabel(
				payment.getRelatedItemNameLabel());
		}

		if (payment.getTransactionCode() != null) {
			existingPayment.setTransactionCode(payment.getTransactionCode());
		}

		if (payment.getType() != null) {
			existingPayment.setType(payment.getType());
		}

		preparePatch(payment, existingPayment);

		return putPaymentByExternalReferenceCode(
			externalReferenceCode, existingPayment);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments' -d $'{"amount": ___, "callbackURL": ___, "cancelURL": ___, "channelId": ___, "comment": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "errorMessages": ___, "externalReferenceCode": ___, "languageId": ___, "payload": ___, "paymentIntegrationKey": ___, "paymentStatus": ___, "reasonKey": ___, "redirectURL": ___, "relatedItemId": ___, "relatedItemName": ___, "relatedItemNameLabel": ___, "transactionCode": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Upsert by external reference code. Delegates to CommercePaymentEntryService. addOrUpdateCommercePaymentEntry, resolving the currency by `currencyCode`, `currencyExternalReferenceCode`, or `currencyId` and the related entity by `relatedItemName` (resolved to a class name id) and `relatedItemId`. Creates a new payment when no record matches the supplied externalReferenceCode within the company; replaces the existing record otherwise. Validates the amount, payment integration type, payment status, and reason key; invalid input raises a CommercePaymentEntry*Exception that maps to 400."
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/payments")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Payment postPayment(Payment payment) throws Exception {
		return new Payment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/payments/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postPaymentBatch(
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
				Payment.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/by-externalReferenceCode/{externalReferenceCode}/refund'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Issues a refund against the payment identified by external reference code. Resolves the payment via CommercePaymentEntryService.fetchCommercePaymentEntryByExternalReferenceCode; the call requires the record to be of type=1 (Refund), otherwise it raises UnsupportedOperationException (500). On a valid refund record, delegates to CommercePaymentGateway.refund to drive the configured payment integration and transitions the entry to paymentStatus=17 (Refunded) on success. Raises NoSuchPaymentEntryException (404) when no record matches the supplied code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target payment on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the company scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Path(
		"/payments/by-externalReferenceCode/{externalReferenceCode}/refund"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Payment postPaymentByExternalReferenceCodeRefund(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Payment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/{id}/refund'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Issues a refund against the payment identified by internal ID. Loads the entry via CommercePaymentEntryService.getCommercePaymentEntry; the call requires the record to be of type=1 (Refund), otherwise it raises UnsupportedOperationException (500). On a valid refund record, delegates to CommercePaymentGateway.refund to drive the configured payment integration and transitions the entry to paymentStatus=17 (Refunded) on success."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target payment. Counterpart to the `by-externalReferenceCode` path variant; identifiers are server-assigned and stable across the payment's lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Path("/payments/{id}/refund")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Payment postPaymentRefund(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {

		return new Payment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the payment result set. Filterable fields sourced from the payment entity model -- paymentStatus, type, reasonKey, and relatedItemId (the FK to the parent order or subscription, indexed as classPK). Example -- filter=paymentStatus eq 0 and type eq 1.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Full-text search expression matched against the payment's indexed fields. Returns the payments whose searchable content matches the expression.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression of the form `field:asc` or `field:desc`, comma-separated for multi-field sorting. Sortable fields sourced from the payment entity model -- createDate, id, paymentStatus, reasonKey, relatedItemId, and type.",
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/payments/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postPaymentsPageExportBatch(
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
				Payment.class.getName(), callbackURL, contentType, fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-payment/v1.0/payments/by-externalReferenceCode/{externalReferenceCode}' -d $'{"amount": ___, "callbackURL": ___, "cancelURL": ___, "channelId": ___, "comment": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "errorMessages": ___, "externalReferenceCode": ___, "languageId": ___, "payload": ___, "paymentIntegrationKey": ___, "paymentStatus": ___, "reasonKey": ___, "redirectURL": ___, "relatedItemId": ___, "relatedItemName": ___, "relatedItemNameLabel": ___, "transactionCode": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Replaces the payment identified by external reference code. Delegates to CommercePaymentEntryService.addOrUpdateCommercePaymentEntry with the supplied externalReferenceCode taken from the path -- creates a new payment when no record matches in the current company, fully replaces the existing record otherwise. Unlike PATCH, missing fields take their default value (for example, type defaults to 0 (Payment), paymentStatus defaults to 1 (Pending)). Validates the amount, payment integration type, payment status, and reason key; invalid input raises a CommercePaymentEntry*Exception that maps to 400."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target payment on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the company scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Payment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/payments/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Payment putPaymentByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Payment payment)
		throws Exception {

		return new Payment();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Payment> payments, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Payment, Payment, Exception> paymentUnsafeFunction =
			null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			paymentUnsafeFunction = payment -> postPayment(payment);
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				paymentUnsafeFunction = payment -> {
					Payment getPayment = null;
					Payment persistedPayment = null;

					try {
						getPayment = getPaymentByExternalReferenceCode(
							payment.getExternalReferenceCode());

						persistedPayment = patchPayment(
							getPayment.getId(), payment);
					}
					catch (NoSuchModelException noSuchModelException) {
						persistedPayment = postPayment(payment);
					}

					return persistedPayment;
				};
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				paymentUnsafeFunction = payment -> {
					Payment persistedPayment = null;

					persistedPayment = putPaymentByExternalReferenceCode(
						payment.getExternalReferenceCode(), payment);

					return persistedPayment;
				};
			}
		}

		if (paymentUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for Payment");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				payments, paymentUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				payments, paymentUnsafeFunction::apply);
		}
		else {
			for (Payment payment : payments) {
				paymentUnsafeFunction.apply(payment);
			}
		}
	}

	@Override
	public void delete(
			Collection<Payment> payments, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Payment, Payment, Exception> paymentUnsafeFunction =
			payment -> {
				if (payment.getId() != null) {
					try {
						deletePayment(payment.getId());

						return payment;
					}
					catch (Exception exception) {
						if (payment.getExternalReferenceCode() != null) {
							deletePaymentByExternalReferenceCode(
								payment.getExternalReferenceCode());

							return payment;
						}
					}
				}
				else if (payment.getExternalReferenceCode() != null) {
					deletePaymentByExternalReferenceCode(
						payment.getExternalReferenceCode());

					return payment;
				}

				throw new UnsupportedOperationException(
					"Unable to delete by external reference code or ID");
			};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				payments, paymentUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				payments, paymentUnsafeFunction::apply);
		}
		else {
			for (Payment payment : payments) {
				paymentUnsafeFunction.apply(payment);
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
		return "Payment";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Payment> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getPaymentsPage(search, filter, pagination, sorts);
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
			Collection<Payment> payments, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Payment, Payment, Exception> paymentUnsafeFunction =
			null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			paymentUnsafeFunction = payment -> patchPayment(
				payment.getId(), payment);
		}

		if (paymentUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for Payment");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				payments, paymentUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				payments, paymentUnsafeFunction::apply);
		}
		else {
			for (Payment payment : payments) {
				paymentUnsafeFunction.apply(payment);
			}
		}
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public Payment getItem(Long id) throws Exception {
		return getPayment(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<Payment>, UnsafeFunction<Payment, Payment, Exception>,
			 Exception> contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Payment>, UnsafeConsumer<Payment, Exception>, Exception>
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
		return "headless-commerce-admin-payment";
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

	protected void preparePatch(Payment payment, Payment existingPayment) {
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
		<Collection<Payment>, UnsafeFunction<Payment, Payment, Exception>,
		 Exception> contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Payment>, UnsafeConsumer<Payment, Exception>, Exception>
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
		LogFactoryUtil.getLog(BasePaymentResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:1341743492