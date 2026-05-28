/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.DDMOption;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.SkuResource;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseSkuResourceImpl
	implements EntityModelResource, SkuResource,
			   VulcanBatchEngineTaskItemDelegate<Sku> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus/by-externalReferenceCode/{skuExternalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "External-reference-code variant of getChannelProductSku addressed at /channels/by-externalReferenceCode/{...}/products/by-externalReferenceCode/{...}/skus/by-externalReferenceCode/{skuExternalReferenceCode}. Resolves CommerceChannel, CProduct and CPInstance by ERC and delegates to getChannelProductSku, applying CommerceProductViewPermission and the standard CommerceContext for pricing and availability. Validation -- NoSuchModelException -> 404 when any ERC is missing; PrincipalException -> 403 when the caller lacks VIEW permission on the product."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CommerceChannel; raises 404 when no channel with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CProduct; raises 404 when no product with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CPInstance; raises 404 when missing.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "skuExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "ISO 4217 currency code applied to the request's CommerceContext for price resolution. When omitted, the channel's default currency is used; a non-active currency raises 422.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "currencyCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus/by-externalReferenceCode/{skuExternalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Sku
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("channelExternalReferenceCode")
				String channelExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("productExternalReferenceCode")
				String productExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("skuExternalReferenceCode")
				String skuExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("accountId")
				Long accountId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("currencyCode")
				String currencyCode)
		throws Exception {

		return new Sku();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "External-reference-code variant of getChannelProductSkusPage. Resolves the CommerceChannel and CProduct by ERC and delegates to the numeric handler. Validation -- NoSuchModelException -> 404 when either ERC is missing. List query support -- pagination only; filterable fields -- none."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CommerceChannel; raises 404 when no channel with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CProduct; raises 404 when no product with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "ISO 4217 currency code applied to the request's CommerceContext for price resolution. When omitted, the channel's default currency is used; a non-active currency raises 422.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "currencyCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page index for paginated results. Combine with pageSize to walk pages; when omitted the server returns page 1.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items per page. When omitted the server applies the configured default page size; the maximum page size is bounded by the portal's PortalUtil.PROPS_REST_MAX_RETURN_SIZE.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Sku>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("channelExternalReferenceCode")
				String channelExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("productExternalReferenceCode")
				String productExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("accountId")
				Long accountId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("currencyCode")
				String currencyCode,
				@jakarta.ws.rs.core.Context Pagination pagination)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus/{skuId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns a single Sku under /channels/{channelId}/products/{productId}/skus/{skuId}. Resolves the CPDefinition and the channel; builds a CommerceContext that picks an AccountEntry from the explicit accountId, the user's eligible commerce accounts, or the guest account. Enforces CommerceProductViewPermission, fetches the CPInstance, resolves the default unit-of-measure key and converts via SkuDTOConverter. Validation -- NoSuchCProductException -> 404 when the productId does not resolve; NoSuchCPInstanceException -> 404 when the skuId does not resolve; NoSuchEntryException -> 404 when the user has multiple eligible accounts and supplied none; PrincipalException -> 403 when the caller lacks VIEW permission on the product."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceChannel; raises 404 when no channel with this primary key exists.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CProduct (product head). The resource resolves the active CPDefinition through fetchCPDefinitionByCProductId; raises 404 when missing.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CPInstance (SKU); raises 404 when missing.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "skuId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "ISO 4217 currency code applied to the request's CommerceContext for price resolution. When omitted, the channel's default currency is used; a non-active currency raises 422.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "currencyCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/channels/{channelId}/products/{productId}/skus/{skuId}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Sku getChannelProductSku(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("productId")
			Long productId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("skuId")
			Long skuId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("currencyCode")
			String currencyCode)
		throws Exception {

		return new Sku();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists CPInstance rows (status APPROVED) for /channels/{channelId}/products/{productId}/skus. Resolves the CPDefinition, the channel, the account via AccountUtil, and enforces CommerceProductViewPermission. Pages CPInstanceLocalService.getCPDefinitionInstances and converts each entry via SkuDTOConverter using a per-row CommerceContext. Exposed as the `skus` field of the Product DTO. Validation -- NoSuchCProductException -> 404 when the productId does not resolve; PrincipalException -> 403 when the caller lacks VIEW permission on the product. List query support -- pagination only; filterable fields -- none."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceChannel; raises 404 when no channel with this primary key exists.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CProduct (product head). The resource resolves the active CPDefinition through fetchCPDefinitionByCProductId; raises 404 when missing.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "ISO 4217 currency code applied to the request's CommerceContext for price resolution. When omitted, the channel's default currency is used; a non-active currency raises 422.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "currencyCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "One-based page index for paginated results. Combine with pageSize to walk pages; when omitted the server returns page 1.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items per page. When omitted the server applies the configured default page size; the maximum page size is bounded by the portal's PortalUtil.PROPS_REST_MAX_RETURN_SIZE.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/channels/{channelId}/products/{productId}/skus")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Sku> getChannelProductSkusPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("productId")
			Long productId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("currencyCode")
			String currencyCode,
			@jakarta.ws.rs.core.Context Pagination pagination)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "External-reference-code variant of postChannelProductSku. Resolves the CommerceChannel and CProduct by ERC and delegates. Not an upsert -- the underlying method throws UnsupportedOperationException for the DDMOption[] body shape, so callers must use the by-sku-option endpoint instead. Validation -- NoSuchModelException -> 404 when either ERC is missing; UnsupportedOperationException -> 500 for any request body."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CommerceChannel; raises 404 when no channel with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CProduct; raises 404 when no product with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Requested order quantity expressed in base units. Used by the pricing engine to evaluate tier pricing and unit-of-measure conversions; defaults to BigDecimal.ONE or the unit-of-measure incremental order quantity when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "quantity"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Sku
			postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSku(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("channelExternalReferenceCode")
				String channelExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("productExternalReferenceCode")
				String productExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("accountId")
				Long accountId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("quantity")
				java.math.BigDecimal quantity,
				DDMOption[] ddmOptions)
		throws Exception {

		return new Sku();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus/by-sku-option'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "External-reference-code variant of postChannelProductSkuBySkuOption. Resolves channel and product by ERC and delegates. The endpoint is a SKU lookup (not a create) -- it serializes the SkuOption[] body and calls CPInstanceHelper.fetchCPInstance to locate the matching CPInstance. Not an upsert -- nothing is persisted. Validation -- NoSuchModelException -> 404 when either ERC is missing; NoSuchCPInstanceException -> 404 when no SKU matches the option selection."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CommerceChannel; raises 404 when no channel with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CProduct; raises 404 when no product with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productExternalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "ISO 4217 currency code applied to the request's CommerceContext for price resolution. When omitted, the channel's default currency is used; a non-active currency raises 422.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "currencyCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Requested order quantity expressed in base units. Used by the pricing engine to evaluate tier pricing and unit-of-measure conversions; defaults to BigDecimal.ONE or the unit-of-measure incremental order quantity when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "quantity"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Key of the SkuUnitOfMeasure the buyer is ordering in. When omitted the SKU's primary unit of measure is used; ignored when the SKU is single-unit.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "skuUnitOfMeasureKey"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{channelExternalReferenceCode}/products/by-externalReferenceCode/{productExternalReferenceCode}/skus/by-sku-option"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Sku
			postChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOption(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("channelExternalReferenceCode")
				String channelExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("productExternalReferenceCode")
				String productExternalReferenceCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("accountId")
				Long accountId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("currencyCode")
				String currencyCode,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("quantity")
				java.math.BigDecimal quantity,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("skuUnitOfMeasureKey")
				String skuUnitOfMeasureKey,
				SkuOption[] skuOptions)
		throws Exception {

		return new Sku();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Endpoint declared on the base resource accepting a DDMOption[] body. Not an upsert -- the current implementation throws UnsupportedOperationException, so callers must use the by-sku-option endpoint to look up a SKU instead. Validation -- UnsupportedOperationException -> 500 for any request body."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceChannel; raises 404 when no channel with this primary key exists.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CProduct (product head). The resource resolves the active CPDefinition through fetchCPDefinitionByCProductId; raises 404 when missing.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Requested order quantity expressed in base units. Used by the pricing engine to evaluate tier pricing and unit-of-measure conversions; defaults to BigDecimal.ONE or the unit-of-measure incremental order quantity when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "quantity"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/channels/{channelId}/products/{productId}/skus")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Sku postChannelProductSku(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("productId")
			Long productId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("quantity")
			java.math.BigDecimal quantity,
			DDMOption[] ddmOptions)
		throws Exception {

		return new Sku();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/products/{productId}/skus/by-sku-option'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Looks up the SKU that matches a SkuOption[] selection under /channels/{channelId}/products/{productId}/skus/by-sku-option -- it is a search call, not a create. Resolves the CPDefinition, the channel, builds a CommerceContext and enforces CommerceProductViewPermission. Serializes the body to a JSONArray and calls CPInstanceHelper.fetchCPInstance to locate the matching CPInstance. When skuUnitOfMeasureKey is omitted the SKU's default unit of measure is used; quantity defaults to BigDecimal.ONE or the unit of measure's incremental order quantity. Not an upsert -- nothing is persisted. Validation -- NoSuchCProductException -> 404 when the productId does not resolve; NoSuchCPInstanceException -> 404 when no SKU matches the option selection; PrincipalException -> 403 when the caller lacks VIEW permission on the product."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceChannel; raises 404 when no channel with this primary key exists.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CProduct (product head). The resource resolves the active CPDefinition through fetchCPDefinitionByCProductId; raises 404 when missing.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "productId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "ISO 4217 currency code applied to the request's CommerceContext for price resolution. When omitted, the channel's default currency is used; a non-active currency raises 422.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "currencyCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Requested order quantity expressed in base units. Used by the pricing engine to evaluate tier pricing and unit-of-measure conversions; defaults to BigDecimal.ONE or the unit-of-measure incremental order quantity when omitted.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "quantity"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Key of the SkuUnitOfMeasure the buyer is ordering in. When omitted the SKU's primary unit of measure is used; ignored when the SKU is single-unit.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "skuUnitOfMeasureKey"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Sku")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/channels/{channelId}/products/{productId}/skus/by-sku-option"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Sku postChannelProductSkuBySkuOption(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("productId")
			Long productId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountId")
			Long accountId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("currencyCode")
			String currencyCode,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("quantity")
			java.math.BigDecimal quantity,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("skuUnitOfMeasureKey")
			String skuUnitOfMeasureKey,
			SkuOption[] skuOptions)
		throws Exception {

		return new Sku();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Sku> skus, Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Override
	public void delete(
			Collection<Sku> skus, Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray();
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
		return "Sku";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Sku> read(
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
			Collection<Sku> skus, Map<String, Serializable> parameters)
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
			<Collection<Sku>, UnsafeFunction<Sku, Sku, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Sku>, UnsafeConsumer<Sku, Exception>, Exception>
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
		return "headless-commerce-delivery-catalog";
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
		<Collection<Sku>, UnsafeFunction<Sku, Sku, Exception>, Exception>
			contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Sku>, UnsafeConsumer<Sku, Exception>, Exception>
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
		LogFactoryUtil.getLog(BaseSkuResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:1037452556