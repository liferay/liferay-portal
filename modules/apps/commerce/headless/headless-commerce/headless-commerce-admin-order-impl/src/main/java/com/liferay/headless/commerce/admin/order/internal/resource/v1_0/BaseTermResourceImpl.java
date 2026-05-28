/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Term;
import com.liferay.headless.commerce.admin.order.resource.v1_0.TermResource;
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
public abstract class BaseTermResourceImpl
	implements EntityModelResource, TermResource,
			   VulcanBatchEngineTaskItemDelegate<Term>,
			   VulcanCRUDItemDelegate<Term> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/{id}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes a Term by ID. Backed by the matching commerce service method on the target resource."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target resource. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the resource lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/terms/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteTerm(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/terms/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteTermBatch(
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
				Term.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes a commerce term by externalReferenceCode followed by deletion. Throws NoSuchTermEntryException (404) if not found."
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/terms/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteTermByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/{id}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Gets a Term by ID. Backed by the matching commerce service method on the target resource."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target resource. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the resource lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/terms/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Term getTerm(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id)
		throws Exception {

		return new Term();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves a term by externalReferenceCode the service. Identical response to getTerm. Throws NoSuchTermEntryException (404) if not found."
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/terms/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Term getTermByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		return new Term();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "List endpoint for all commerce terms in the system. Supports pagination, filtering, and sorting SearchUtil.search(). Status is set to Any."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/terms")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Term> getTermsPage(
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
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/{id}' -d $'{"active": ___, "createDate": ___, "description": ___, "displayDate": ___, "expirationDate": ___, "externalReferenceCode": ___, "id": ___, "label": ___, "name": ___, "neverExpire": ___, "priority": ___, "termOrderType": ___, "type": ___, "typeLocalized": ___, "typeSettings": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates a Term by ID. Backed by the matching commerce service method on the target resource."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Internal numeric identifier of the target resource. Counterpart to the by-externalReferenceCode path variant; identifiers are server-assigned and stable across the resource lifetime.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "id"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/terms/{id}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Term patchTerm(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("id")
			Long id,
			Term term)
		throws Exception {

		return new Term();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/by-externalReferenceCode/{externalReferenceCode}' -d $'{"active": ___, "createDate": ___, "description": ___, "displayDate": ___, "expirationDate": ___, "externalReferenceCode": ___, "id": ___, "label": ___, "name": ___, "neverExpire": ___, "priority": ___, "termOrderType": ___, "type": ___, "typeLocalized": ___, "typeSettings": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Partial update of a term by externalReferenceCode via _updateTerm(). Updates active, description, displayDate, expirationDate, label, name, neverExpire, priority, typeSettings, and calls _updateNestedResources() to sync linked order types. Returns updated Term DTO. Throws NoSuchTermEntryException (404) if not found."
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/terms/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Term patchTermByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Term term)
		throws Exception {

		Term existingTerm = getTermByExternalReferenceCode(
			externalReferenceCode);

		if (term.getActive() != null) {
			existingTerm.setActive(term.getActive());
		}

		if (term.getCreateDate() != null) {
			existingTerm.setCreateDate(term.getCreateDate());
		}

		if (term.getDescription() != null) {
			existingTerm.setDescription(term.getDescription());
		}

		if (term.getDisplayDate() != null) {
			existingTerm.setDisplayDate(term.getDisplayDate());
		}

		if (term.getExpirationDate() != null) {
			existingTerm.setExpirationDate(term.getExpirationDate());
		}

		if (term.getExternalReferenceCode() != null) {
			existingTerm.setExternalReferenceCode(
				term.getExternalReferenceCode());
		}

		if (term.getLabel() != null) {
			existingTerm.setLabel(term.getLabel());
		}

		if (term.getName() != null) {
			existingTerm.setName(term.getName());
		}

		if (term.getNeverExpire() != null) {
			existingTerm.setNeverExpire(term.getNeverExpire());
		}

		if (term.getPriority() != null) {
			existingTerm.setPriority(term.getPriority());
		}

		if (term.getType() != null) {
			existingTerm.setType(term.getType());
		}

		if (term.getTypeLocalized() != null) {
			existingTerm.setTypeLocalized(term.getTypeLocalized());
		}

		if (term.getTypeSettings() != null) {
			existingTerm.setTypeSettings(term.getTypeSettings());
		}

		preparePatch(term, existingTerm);

		return putTermByExternalReferenceCode(
			externalReferenceCode, existingTerm);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms' -d $'{"active": ___, "createDate": ___, "description": ___, "displayDate": ___, "expirationDate": ___, "externalReferenceCode": ___, "id": ___, "label": ___, "name": ___, "neverExpire": ___, "priority": ___, "termOrderType": ___, "type": ___, "typeLocalized": ___, "typeSettings": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Create a new commerce term. Calls _addCommerceTermEntry() which uses the service with externalReferenceCode, localized description/label, active, dates, name, priority, type, typeSettings. Also calls _updateNestedResources() to add linked order types if provided. Returns created Term DTO."
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/terms")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Term postTerm(Term term) throws Exception {
		return new Term();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/terms/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postTermBatch(
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
				Term.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and are sourced from the matching entity model -- typically accountId, channelId, orderStatus, orderTypeId, paymentStatus, sku, name, and createDate. Example -- filter=orderStatus eq 10.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/terms/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postTermsPageExportBatch(
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
				Term.class.getName(), callbackURL, contentType, fieldNames)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-order/v1.0/terms/by-externalReferenceCode/{externalReferenceCode}' -d $'{"active": ___, "createDate": ___, "description": ___, "displayDate": ___, "expirationDate": ___, "externalReferenceCode": ___, "id": ___, "label": ___, "name": ___, "neverExpire": ___, "priority": ___, "termOrderType": ___, "type": ___, "typeLocalized": ___, "typeSettings": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Full replace of a term by externalReferenceCode (upsert semantics). If term exists, updates all fields and calls _updateNestedResources(); if not, calls postTerm(). Returns Term DTO."
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Term")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/terms/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Term putTermByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Term term)
		throws Exception {

		return new Term();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Term> terms, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Term, Term, Exception> termUnsafeFunction = null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			termUnsafeFunction = term -> postTerm(term);
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				termUnsafeFunction = term -> {
					Term getTerm = null;
					Term persistedTerm = null;

					try {
						getTerm = getTermByExternalReferenceCode(
							term.getExternalReferenceCode());

						persistedTerm = patchTerm(getTerm.getId(), term);
					}
					catch (NoSuchModelException noSuchModelException) {
						persistedTerm = postTerm(term);
					}

					return persistedTerm;
				};
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				termUnsafeFunction = term -> {
					Term persistedTerm = null;

					persistedTerm = putTermByExternalReferenceCode(
						term.getExternalReferenceCode(), term);

					return persistedTerm;
				};
			}
		}

		if (termUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for Term");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(terms, termUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(terms, termUnsafeFunction::apply);
		}
		else {
			for (Term term : terms) {
				termUnsafeFunction.apply(term);
			}
		}
	}

	@Override
	public void delete(
			Collection<Term> terms, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Term, Term, Exception> termUnsafeFunction = term -> {
			if (term.getId() != null) {
				try {
					deleteTerm(term.getId());

					return term;
				}
				catch (Exception exception) {
					if (term.getExternalReferenceCode() != null) {
						deleteTermByExternalReferenceCode(
							term.getExternalReferenceCode());

						return term;
					}
				}
			}
			else if (term.getExternalReferenceCode() != null) {
				deleteTermByExternalReferenceCode(
					term.getExternalReferenceCode());

				return term;
			}

			throw new UnsupportedOperationException(
				"Unable to delete by external reference code or ID");
		};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(terms, termUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(terms, termUnsafeFunction::apply);
		}
		else {
			for (Term term : terms) {
				termUnsafeFunction.apply(term);
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
		return "Term";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Term> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getTermsPage(search, filter, pagination, sorts);
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
			Collection<Term> terms, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Term, Term, Exception> termUnsafeFunction = null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			termUnsafeFunction = term -> patchTerm(term.getId(), term);
		}

		if (termUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for Term");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(terms, termUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(terms, termUnsafeFunction::apply);
		}
		else {
			for (Term term : terms) {
				termUnsafeFunction.apply(term);
			}
		}
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public Term getItem(Long id) throws Exception {
		return getTerm(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<Term>, UnsafeFunction<Term, Term, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Term>, UnsafeConsumer<Term, Exception>, Exception>
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

	protected void preparePatch(Term term, Term existingTerm) {
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
		<Collection<Term>, UnsafeFunction<Term, Term, Exception>, Exception>
			contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Term>, UnsafeConsumer<Term, Exception>, Exception>
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
		LogFactoryUtil.getLog(BaseTermResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:1756661714