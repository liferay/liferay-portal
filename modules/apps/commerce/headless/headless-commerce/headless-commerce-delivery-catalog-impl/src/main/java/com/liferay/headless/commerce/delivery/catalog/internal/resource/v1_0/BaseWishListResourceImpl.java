/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.WishListResource;
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
public abstract class BaseWishListResourceImpl
	implements EntityModelResource, VulcanBatchEngineTaskItemDelegate<WishList>,
			   VulcanCRUDItemDelegate<WishList>, WishListResource {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/wishlists/{wishListId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the wish list at /wishlists/{wishListId} via CommerceWishListService.deleteCommerceWishList. Validation -- NoSuchWishListException -> 404 when the row is missing; PrincipalException -> 403 when the caller lacks DELETE permission."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceWishList; raises 404 when missing or when the caller lacks VIEW permission.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "wishListId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/wishlists/{wishListId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteWishList(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("wishListId")
			Long wishListId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/wishlists/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/wishlists/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteWishListBatch(
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
				WishList.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{externalReferenceCode}/wishlists'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "External-reference-code variant of getChannelWishListsPage. Resolves the CommerceChannel via CommerceChannelLocalService.getCommerceChannelByExternalReferenceCode and delegates to the numeric handler. Pagination behaves identically to the numeric variant; accountId and currencyCode are accepted for parity but not used in the query. Validation -- NoSuchChannelException -> 404 when the channel ERC is missing. List query support -- pagination only; filterable fields -- none."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CommerceChannel; raises 404 when no channel with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{externalReferenceCode}/wishlists"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<WishList> getChannelByExternalReferenceCodeWishListsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
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
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/wishlists'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists CommerceWishList rows for /channels/{channelId}/wishlists. Loads the channel through fetchCommerceChannel and pages CommerceWishListService.getCommerceWishLists by the channel's site group. accountId and currencyCode are accepted on the request for parity with sibling endpoints but are not currently used in the query. Validation -- NoSuchChannelException -> 404 when the channelId does not resolve. List query support -- pagination only; filterable fields -- none."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceChannel; raises 404 when no channel with this primary key exists.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/channels/{channelId}/wishlists")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<WishList> getChannelWishListsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
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
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/wishlists/{wishListId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Returns the wish list at /wishlists/{wishListId} via CommerceWishListService.getCommerceWishList. Validation -- NoSuchWishListException -> 404 when the row is missing; PrincipalException -> 403 when the caller lacks VIEW permission."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceWishList; raises 404 when missing or when the caller lacks VIEW permission.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "wishListId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/wishlists/{wishListId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public WishList getWishList(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("wishListId")
			Long wishListId)
		throws Exception {

		return new WishList();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/wishlists/{wishListId}' -d $'{"defaultWishList": ___, "id": ___, "name": ___, "wishListItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the wish list at /wishlists/<wishListId>. Not a JSON Merge Patch -- the handler performs a manual GetterUtil.getString/getBoolean fallback merge for name and defaultWishList before calling CommerceWishListService.updateCommerceWishList. Validation -- NoSuchWishListException -> 404 when the row is missing; PrincipalException -> 403 when the caller lacks UPDATE permission. Side effects -- When the body includes wishListItems, deletes the existing items via CommerceWishListItemService.deleteCommerceWishListItems and re-adds each entry through WishListItemResource.postWishlistWishListWishListItem (upsert by (accountId, wishListId, cpInstanceUuid, productId))."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceWishList; raises 404 when missing or when the caller lacks VIEW permission.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "wishListId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/wishlists/{wishListId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public WishList patchWishList(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("wishListId")
			Long wishListId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountId")
			Long accountId,
			WishList wishList)
		throws Exception {

		return new WishList();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/by-externalReferenceCode/{externalReferenceCode}/wishlists' -d $'{"defaultWishList": ___, "id": ___, "name": ___, "wishListItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "External-reference-code variant of postChannelWishList. Resolves the CommerceChannel by ERC and delegates to the numeric handler. Not an upsert -- CommerceWishListService.addCommerceWishList is always invoked, so the channel and account can hold multiple wish lists by name. Validation -- NoSuchChannelException -> 404 when the channel ERC is missing. Side effects -- Persists a new CommerceWishList row scoped to the channel's site group and the resolved account."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code of the addressed CommerceChannel; raises 404 when no channel with this code exists in the company.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{externalReferenceCode}/wishlists"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public WishList postChannelByExternalReferenceCodeWishList(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountId")
			Long accountId,
			WishList wishList)
		throws Exception {

		return new WishList();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-delivery-catalog/v1.0/channels/{channelId}/wishlists' -d $'{"defaultWishList": ___, "id": ___, "name": ___, "wishListItems": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a new CommerceWishList under /channels/<channelId>/wishlists via CommerceWishListService.addCommerceWishList scoped to the channel's site group, using the body's name and defaultWishList flag. Not an upsert at the wish-list level -- addCommerceWishList is always invoked, so a duplicate name within the same channel and account produces a second row. Validation -- None at the wish-list level (request shape is enforced by the service). Side effects -- When the body contains wishListItems, each is forwarded to WishListItemResource.postWishlistWishListWishListItem (an upsert by (accountId, wishListId, cpInstanceUuid, productId)) so the wish list is populated atomically."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the addressed CommerceChannel; raises 404 when no channel with this primary key exists.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Reference to the AccountEntry the request is scoped to. When omitted, AccountUtil resolves the effective account from the authenticated user's commerce account assignments and channel eligibility; when the user has multiple accounts the explicit value is required (NoSuchEntryException otherwise).",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "accountId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "WishList")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/channels/{channelId}/wishlists")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public WishList postChannelWishList(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("accountId")
			Long accountId,
			WishList wishList)
		throws Exception {

		return new WishList();
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<WishList> wishLists,
			Map<String, Serializable> parameters)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Override
	public void delete(
			Collection<WishList> wishLists,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<WishList, WishList, Exception> wishListUnsafeFunction =
			wishList -> {
				deleteWishList(wishList.getId());

				return wishList;
			};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				wishLists, wishListUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				wishLists, wishListUnsafeFunction::apply);
		}
		else {
			for (WishList wishList : wishLists) {
				wishListUnsafeFunction.apply(wishList);
			}
		}
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray();
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
		return "WishList";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<WishList> read(
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
			Collection<WishList> wishLists,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<WishList, WishList, Exception> wishListUnsafeFunction =
			null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			wishListUnsafeFunction = wishList -> patchWishList(
				wishList.getId(),
				_parseLong((String)parameters.get("accountId")), wishList);
		}

		if (wishListUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for WishList");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				wishLists, wishListUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				wishLists, wishListUnsafeFunction::apply);
		}
		else {
			for (WishList wishList : wishLists) {
				wishListUnsafeFunction.apply(wishList);
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
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public WishList getItem(Long id) throws Exception {
		return getWishList(id);
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<WishList>,
			 UnsafeFunction<WishList, WishList, Exception>, Exception>
				contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<WishList>, UnsafeConsumer<WishList, Exception>,
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
		<Collection<WishList>, UnsafeFunction<WishList, WishList, Exception>,
		 Exception> contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<WishList>, UnsafeConsumer<WishList, Exception>, Exception>
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
		LogFactoryUtil.getLog(BaseWishListResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:-1743906659