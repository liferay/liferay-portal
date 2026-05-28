/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.order.resource.v1_0.AttachmentResource;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
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
public abstract class BaseAttachmentResourceImpl
	implements AttachmentResource, EntityModelResource,
			   VulcanBatchEngineTaskItemDelegate<Attachment> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{orderId}/attachments/{attachmentId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes a file attachment from an order. Requires order existence verification. Returns 204 No Content."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order. Used as the parent context when accessing nested resources (items, notes, attachments, addresses).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "orderId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order attachment.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "attachmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/orders/{orderId}/attachments/{attachmentId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteOrderAttachment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("orderId")
			Long orderId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("attachmentId")
			Long attachmentId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}/attachments/by-externalReferenceCode/{attachmentExternalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes an attachment from an order using external reference codes for both. Validates both order and attachment existence. Returns 204 No Content."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses an attachment under an order. The code is the integration-supplied idempotency key, unique per attachment within the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "attachmentExternalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}/attachments/by-externalReferenceCode/{attachmentExternalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void
			deleteOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("attachmentExternalReferenceCode")
				String attachmentExternalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{orderId}/attachments/{attachmentId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves a single attachment from an order by attachment ID the service. Returns populated Attachment DTO with file metadata (extension, fileName, url) and actions. Throws NoSuchOrderAttachmentException (404) if not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order. Used as the parent context when accessing nested resources (items, notes, attachments, addresses).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "orderId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order attachment.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "attachmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/orders/{orderId}/attachments/{attachmentId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Attachment getOrderAttachment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("orderId")
			Long orderId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("attachmentId")
			Long attachmentId)
		throws Exception {

		return new Attachment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{orderId}/attachments'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "List endpoint for order attachments; supports pagination, filtering, and sorting. Delegates to the service scoped to a single order. Returns Page<Attachment> with total count. Searchable/filterable by attachment properties stored in attachment model."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order. Used as the parent context when accessing nested resources (items, notes, attachments, addresses).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "orderId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and are sourced from the matching entity model -- typically accountId, channelId, orderStatus, orderTypeId, paymentStatus, sku, name, and createDate. Example -- filter=orderStatus eq 10.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page number used together with pageSize to navigate paginated result sets. Defaults to 1 when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items to return per page in paginated result sets. Defaults to the resource pagination configuration when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Free-text search expression applied to the indexed search corpus of the target resource (for example, the creator email for orders, the SKU for order items, the name for order rules).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Comma-separated list of fields to sort by. Each field accepts the suffix :asc or :desc; sortable fields depend on the endpoint and are sourced from the matching entity model.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/orders/{orderId}/attachments")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Attachment> getOrderAttachmentsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("orderId")
			Long orderId,
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
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}/attachments/by-externalReferenceCode/{attachmentExternalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves a single attachment via external reference code pair; resolves both order and attachment, then delegates to getOrderAttachment(). Throws NoSuchOrderException (404) or NoSuchOrderAttachmentException (404) if either not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses an attachment under an order. The code is the integration-supplied idempotency key, unique per attachment within the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "attachmentExternalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}/attachments/by-externalReferenceCode/{attachmentExternalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Attachment
			getOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("attachmentExternalReferenceCode")
				String attachmentExternalReferenceCode)
		throws Exception {

		return new Attachment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}/attachments'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Identical to getOrderAttachmentsPage but order is resolved by externalReferenceCode first. Throws NoSuchOrderException (404) if order not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and are sourced from the matching entity model -- typically accountId, channelId, orderStatus, orderTypeId, paymentStatus, sku, name, and createDate. Example -- filter=orderStatus eq 10.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page number used together with pageSize to navigate paginated result sets. Defaults to 1 when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items to return per page in paginated result sets. Defaults to the resource pagination configuration when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Free-text search expression applied to the indexed search corpus of the target resource (for example, the creator email for orders, the SKU for order items, the name for order rules).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Comma-separated list of fields to sort by. Each field accepts the suffix :asc or :desc; sortable fields depend on the endpoint and are sourced from the matching entity model.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}/attachments"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Attachment> getOrderByExternalReferenceCodeAttachmentsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
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
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{orderId}/attachments/{attachmentId}' -d $'{"attachment": ___, "externalReferenceCode": ___, "fileName": ___, "priority": ___, "restricted": ___, "title": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Partial update of an order attachment the service. Updates priority, restricted flag, title, and type. Returns updated Attachment DTO. Throws NoSuchOrderAttachmentException (404) if not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order. Used as the parent context when accessing nested resources (items, notes, attachments, addresses).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "orderId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order attachment.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "attachmentId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/orders/{orderId}/attachments/{attachmentId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Attachment patchOrderAttachment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("orderId")
			Long orderId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("attachmentId")
			Long attachmentId,
			Attachment attachment)
		throws Exception {

		return new Attachment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}/attachments/by-externalReferenceCode/{attachmentExternalReferenceCode}' -d $'{"attachment": ___, "externalReferenceCode": ___, "fileName": ___, "priority": ___, "restricted": ___, "title": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Partial update of an attachment via external reference codes for both order and attachment. Throws NoSuchOrderException (404) or NoSuchOrderAttachmentException (404) if either not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the by-externalReferenceCode paths. The code is the integration-supplied idempotency key, unique within the resource scope; POST against this path is upsert (create when absent, replace when present).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses an attachment under an order. The code is the integration-supplied idempotency key, unique per attachment within the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "attachmentExternalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}/attachments/by-externalReferenceCode/{attachmentExternalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Attachment
			patchOrderByExternalReferenceCodeAttachmentByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("attachmentExternalReferenceCode")
				String attachmentExternalReferenceCode,
				Attachment attachment)
		throws Exception {

		return new Attachment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/{orderId}/attachments' -d $'{"attachment": ___, "externalReferenceCode": ___, "fileName": ___, "priority": ___, "restricted": ___, "title": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Create a file attachment for an order. Base64-decodes the attachment field and calls the service with title, type, restricted flag, and priority. Returns created Attachment DTO."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target order. Used as the parent context when accessing nested resources (items, notes, attachments, addresses).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "orderId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/orders/{orderId}/attachments")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Attachment postOrderAttachment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("orderId")
			Long orderId,
			Attachment attachment)
		throws Exception {

		return new Attachment();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/{externalReferenceCode}/attachments' -d $'{"attachment": ___, "externalReferenceCode": ___, "fileName": ___, "priority": ___, "restricted": ___, "title": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Create an attachment for an order accessed by externalReferenceCode. Resolves order first, then delegates to postOrderAttachment(). Throws NoSuchOrderException (404) if order not found."
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Attachment")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/orders/by-externalReferenceCode/{externalReferenceCode}/attachments"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Attachment postOrderByExternalReferenceCodeAttachment(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Attachment attachment)
		throws Exception {

		return new Attachment();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Attachment> attachments,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Attachment, Attachment, Exception>
			attachmentUnsafeFunction = null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			if (parameters.containsKey("externalReferenceCode")) {
				attachmentUnsafeFunction =
					attachment -> postOrderByExternalReferenceCodeAttachment(
						(String)parameters.get("externalReferenceCode"),
						attachment);
			}
			else {
				throw new NotSupportedException(
					"One of the following parameters must be specified: [externalReferenceCode]");
			}
		}

		if (attachmentUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for Attachment");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				attachments, attachmentUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				attachments, attachmentUnsafeFunction::apply);
		}
		else {
			for (Attachment attachment : attachments) {
				attachmentUnsafeFunction.apply(attachment);
			}
		}
	}

	@Override
	public void delete(
			Collection<Attachment> attachments,
			Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray("INSERT");
	}

	public Set<String> getAvailableUpdateStrategies() {
		return SetUtil.fromArray();
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return getEntityModel(
			new MultivaluedHashMap<String, Object>(multivaluedMap));
	}

	public String getResourceName() {
		return "Attachment";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Attachment> read(
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
			Collection<Attachment> attachments,
			Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<Attachment>,
			 UnsafeFunction<Attachment, Attachment, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Attachment>, UnsafeConsumer<Attachment, Exception>,
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
		<Collection<Attachment>,
		 UnsafeFunction<Attachment, Attachment, Exception>, Exception>
			contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Attachment>, UnsafeConsumer<Attachment, Exception>,
		 Exception> contextBatchUnsafeConsumer;
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
		LogFactoryUtil.getLog(BaseAttachmentResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:178123424