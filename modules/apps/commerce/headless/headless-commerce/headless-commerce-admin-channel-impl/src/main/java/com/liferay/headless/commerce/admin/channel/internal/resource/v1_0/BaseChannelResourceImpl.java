/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ChannelResource;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PermissionServiceUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;
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
import java.util.HashMap;
import java.util.HashSet;
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
public abstract class BaseChannelResourceImpl
	implements ChannelResource, EntityModelResource,
			   VulcanBatchEngineTaskItemDelegate<Channel>,
			   VulcanCRUDItemDelegate<Channel> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/{channelId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the commerce channel by its internal ID. Idempotent. A follow-up call on an entity that has already been deleted returns 404. Calls CommerceChannelService.deleteCommerceChannel. Validation -- NoSuchChannelException -> 404 when channel ID not found. Side effects -- cascade delete of the channel and its dependent commerce data."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Identifier of a commerce channel. Addresses a single storefront bound to a Liferay site, with its own currency, account visibility, and display pages.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/channels/{channelId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteChannel(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/channels/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteChannelBatch(
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
				Channel.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the commerce channel by its external reference code (ERC). Idempotent. A follow-up call on an entity that has already been deleted returns 404. Calls CommerceChannelService.fetchCommerceChannelByExternalReferenceCode + deleteCommerceChannel. Validation -- NoSuchChannelException -> 404 when channel ERC not found. Side effects -- cascade delete of the channel and its dependent commerce data."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; it is assigned by the client rather than the server.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteChannelByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/account-address-channels/{accountAddressChannelId}/channel'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the commerce channel associated with the parent AccountAddressChannel, addressed by internal ID. Calls CommerceChannelRelService.getCommerceChannelRel. Validation -- NoSuchChannelRelException -> 404 when channel rel ID not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Identifier of an account-address-channel binding. Addresses a single mapping that scopes an account address to a specific channel for shipping or billing eligibility.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "accountAddressChannelId", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/account-address-channels/{accountAddressChannelId}/channel"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Channel getAccountAddressChannelChannel(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("accountAddressChannelId")
			Long accountAddressChannelId)
		throws Exception {

		return new Channel();
	}

	protected abstract Channel doGetChannel(Long channelId) throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/{channelId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the commerce channel by its internal ID. Calls CommerceChannelService.fetchCommerceChannel. Validation -- NoSuchChannelException -> 404 when channel ID not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Identifier of a commerce channel. Addresses a single storefront bound to a Liferay site, with its own currency, account visibility, and display pages.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/channels/{channelId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final Channel getChannel(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId)
		throws Exception {

		Channel getChannel = doGetChannel(channelId);

		getChannel.setPermissions(
			() -> NestedFieldsSupplier.supply(
				"permissions",
				nestedField -> {
					Page<Permission> permissionsPage =
						getChannelPermissionsPage(getChannel.getId(), null);

					Collection<Permission> permissions =
						permissionsPage.getItems();

					return permissions.toArray(
						new Permission[permissions.size()]);
				}));

		return getChannel;
	}

	protected abstract Channel doGetChannelByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/by-externalReferenceCode/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the commerce channel by its external reference code (ERC). Calls CommerceChannelService.fetchCommerceChannelByExternalReferenceCode. Validation -- NoSuchChannelException -> 404 when channel ERC not found."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; it is assigned by the client rather than the server.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final Channel getChannelByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {

		Channel getChannel = doGetChannelByExternalReferenceCode(
			externalReferenceCode);

		getChannel.setPermissions(
			() -> NestedFieldsSupplier.supply(
				"permissions",
				nestedField -> {
					Page<Permission> permissionsPage =
						getChannelPermissionsPage(getChannel.getId(), null);

					Collection<Permission> permissions =
						permissionsPage.getItems();

					return permissions.toArray(
						new Permission[permissions.size()]);
				}));

		return getChannel;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/{channelId}/permissions'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists the role permissions granted on the channel identified by ID. Requires the PERMISSIONS action on the channel."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Identifier of a commerce channel. Addresses a single storefront bound to a Liferay site, with its own currency, account visibility, and display pages.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId", required = true
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "roleNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/channels/{channelId}/permissions")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Permission> getChannelPermissionsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("roleNames")
			String roleNames)
		throws Exception {

		Long groupId = getPermissionCheckerGroupId(channelId);
		Long resourceId = getPermissionCheckerResourceId(channelId);
		String resourceName = getPermissionCheckerResourceName(channelId);

		PermissionServiceUtil.checkPermission(
			groupId, resourceName, resourceId);

		return toPermissionPage(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"getChannelPermissionsPage", null, resourceName, groupId)
			).put(
				"replace",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"putChannelPermissionsPage", null, resourceName, groupId)
			).build(),
			resourceId, resourceName, roleNames);
	}

	protected abstract Page<Channel> doGetChannelsPage(
			String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Lists every commerce channel visible to the caller. Calls SearchUtil.search (index CommerceChannel). Validation -- None (returns empty page when no matches). List query support — filterable fields -- dateCreated, dateModified, externalReferenceCode, name, siteGroupId; sortable fields -- dateCreated, dateModified, externalReferenceCode, name, siteGroupId."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and are sourced from the matching entity model; see the per-resource list operation description for specifics. For example, filter=externalReferenceCode eq 'AB-34098-789-N'.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "1-based page number for paginated responses. Defaults to 1.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Number of items per page. Defaults to the portal's configured page size when omitted; capped by the portal configuration to prevent unbounded reads.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Free-text search applied to the resource's full-text indexed fields. Multiple terms are AND-combined.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression of the form `field:asc` or `field:desc`, comma-separated for multi-field sorting (for example, name:asc,createDate:desc). Supported sort fields depend on the endpoint and are sourced from the matching entity model; see the per-resource list operation description for specifics.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/channels")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final Page<Channel> getChannelsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		Page<Channel> channelsPage = doGetChannelsPage(
			search, filter, pagination, sorts);

		for (Channel channel : channelsPage.getItems()) {
			channel.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Page<Permission> permissionsPage =
							getChannelPermissionsPage(channel.getId(), null);

						Collection<Permission> permissions =
							permissionsPage.getItems();

						return permissions.toArray(
							new Permission[permissions.size()]);
					}));
		}

		return channelsPage;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/{channelId}' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "externalReferenceCode": ___, "name": ___, "permissions": ___, "siteGroupId": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Partially updates the commerce channel by its internal ID — only the fields supplied in the body are replaced; omitted fields are left unchanged. A resolvable currencyCode (or matching currencyId / currencyExternalReferenceCode) is applied; an unresolved code is silently ignored and the channel's current currency is preserved. Calls CommerceChannelService.getCommerceChannel + CommerceCurrencyUtil.getCommerceCurrency + CommerceChannelService.updateCommerceChannel. Validation -- NoSuchChannelException -> 404 when channel ID not found; CommerceChannelNameException -> 400 when invalid name; CommerceChannelTypeException -> 400 when invalid type; CommerceChannelSiteGroupIdException -> 400 when siteGroupId matches no site of the instance. Side effects -- an unresolvable currency code is swallowed and the existing currency kept; the channel is reindexed on update."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Identifier of a commerce channel. Addresses a single storefront bound to a Liferay site, with its own currency, account visibility, and display pages.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/channels/{channelId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Channel patchChannel(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			Channel channel)
		throws Exception {

		Channel existingChannel = getChannel(channelId);

		if (channel.getAccountExternalReferenceCode() != null) {
			existingChannel.setAccountExternalReferenceCode(
				channel.getAccountExternalReferenceCode());
		}

		if (channel.getAccountId() != null) {
			existingChannel.setAccountId(channel.getAccountId());
		}

		if (channel.getCurrencyCode() != null) {
			existingChannel.setCurrencyCode(channel.getCurrencyCode());
		}

		if (channel.getCurrencyExternalReferenceCode() != null) {
			existingChannel.setCurrencyExternalReferenceCode(
				channel.getCurrencyExternalReferenceCode());
		}

		if (channel.getCurrencyId() != null) {
			existingChannel.setCurrencyId(channel.getCurrencyId());
		}

		if (channel.getExternalReferenceCode() != null) {
			existingChannel.setExternalReferenceCode(
				channel.getExternalReferenceCode());
		}

		if (channel.getName() != null) {
			existingChannel.setName(channel.getName());
		}

		if (channel.getPermissions() != null) {
			existingChannel.setPermissions(channel.getPermissions());
		}

		if (channel.getSiteGroupId() != null) {
			existingChannel.setSiteGroupId(channel.getSiteGroupId());
		}

		if (channel.getType() != null) {
			existingChannel.setType(channel.getType());
		}

		preparePatch(channel, existingChannel);

		return putChannel(channelId, existingChannel);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/by-externalReferenceCode/{externalReferenceCode}' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "externalReferenceCode": ___, "name": ___, "permissions": ___, "siteGroupId": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Partially updates the commerce channel by its external reference code (ERC) — only the fields supplied in the body are replaced; omitted fields are left unchanged. A resolvable currencyCode (or matching currencyId / currencyExternalReferenceCode) is applied; an unresolved code is silently ignored and the channel's current currency is preserved. Calls CommerceChannelService.fetchCommerceChannelByExternalReferenceCode + CommerceCurrencyUtil.getCommerceCurrency + CommerceChannelService.updateCommerceChannel. Validation -- NoSuchChannelException -> 404 when channel ERC not found; CommerceChannelNameException -> 400 when invalid name; CommerceChannelTypeException -> 400 when invalid type; CommerceChannelSiteGroupIdException -> 400 when siteGroupId matches no site of the instance. Side effects -- an unresolvable currency code is swallowed and the existing currency kept; the channel is reindexed on update."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; it is assigned by the client rather than the server.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Channel patchChannelByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Channel channel)
		throws Exception {

		Channel existingChannel = getChannelByExternalReferenceCode(
			externalReferenceCode);

		if (channel.getAccountExternalReferenceCode() != null) {
			existingChannel.setAccountExternalReferenceCode(
				channel.getAccountExternalReferenceCode());
		}

		if (channel.getAccountId() != null) {
			existingChannel.setAccountId(channel.getAccountId());
		}

		if (channel.getCurrencyCode() != null) {
			existingChannel.setCurrencyCode(channel.getCurrencyCode());
		}

		if (channel.getCurrencyExternalReferenceCode() != null) {
			existingChannel.setCurrencyExternalReferenceCode(
				channel.getCurrencyExternalReferenceCode());
		}

		if (channel.getCurrencyId() != null) {
			existingChannel.setCurrencyId(channel.getCurrencyId());
		}

		if (channel.getExternalReferenceCode() != null) {
			existingChannel.setExternalReferenceCode(
				channel.getExternalReferenceCode());
		}

		if (channel.getName() != null) {
			existingChannel.setName(channel.getName());
		}

		if (channel.getPermissions() != null) {
			existingChannel.setPermissions(channel.getPermissions());
		}

		if (channel.getSiteGroupId() != null) {
			existingChannel.setSiteGroupId(channel.getSiteGroupId());
		}

		if (channel.getType() != null) {
			existingChannel.setType(channel.getType());
		}

		preparePatch(channel, existingChannel);

		return putChannelByExternalReferenceCode(
			externalReferenceCode, existingChannel);
	}

	protected abstract Channel doPostChannel(Channel channel) throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "externalReferenceCode": ___, "name": ___, "permissions": ___, "siteGroupId": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Creates a new commerce channel and allocates a fresh internal ID via the portal counter. Calls CommerceCurrencyUtil.getCommerceCurrency + AccountEntryService.fetchAccountEntryByExternalReferenceCode + CommerceChannelService.addCommerceChannel. Validation -- NoSuchCurrencyException -> 404 when currency not resolvable; DuplicateCommerceChannelException -> 409 when channel already exists; CommerceChannelNameException -> 400 when invalid name; DuplicateExternalReferenceCodeException -> 400 when duplicate ERC; CommerceChannelSiteGroupIdException -> 400 when siteGroupId matches no site of the instance. Side effects -- provisions the channel site group and reindexes the channel."
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/channels")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final Channel postChannel(Channel channel) throws Exception {
		Permission[] permissions = channel.getPermissions();

		Channel postChannel = doPostChannel(channel);

		if (permissions != null) {
			Page<Permission> permissionsPage = putChannelPermissionsPage(
				postChannel.getId(), permissions);

			postChannel.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return postChannel;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/channels/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postChannelBatch(
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
				Channel.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "OData v4 filter expression that narrows the result set. Supported fields depend on the endpoint and are sourced from the matching entity model; see the per-resource list operation description for specifics. For example, filter=externalReferenceCode eq 'AB-34098-789-N'.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Free-text search applied to the resource's full-text indexed fields. Multiple terms are AND-combined.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Sort expression of the form `field:asc` or `field:desc`, comma-separated for multi-field sorting (for example, name:asc,createDate:desc). Supported sort fields depend on the endpoint and are sourced from the matching entity model; see the per-resource list operation description for specifics.",
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/channels/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postChannelsPageExportBatch(
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
				Channel.class.getName(), callbackURL, contentType, fieldNames)
		).build();
	}

	protected abstract Channel doPutChannel(Long channelId, Channel channel)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/{channelId}' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "externalReferenceCode": ___, "name": ___, "permissions": ___, "siteGroupId": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Fully replaces the commerce channel by its internal ID — every mutable field is set from the body, and omitted optional fields are reset to their default. Falls back to a new-channel create when the supplied ID does not match an existing channel. The supplied currencyCode (or matching currencyId / currencyExternalReferenceCode) must resolve to an existing CommerceCurrency; an unresolved code returns 404. Calls CommerceChannelService.fetchCommerceChannel + CommerceCurrencyUtil.getCommerceCurrency + CommerceChannelService.updateCommerceChannel | addCommerceChannel. Validation -- NoSuchCurrencyException -> 404 when currency not resolvable; CommerceChannelNameException -> 400 when invalid name; CommerceChannelTypeException -> 400 when invalid type; CommerceChannelSiteGroupIdException -> 400 when siteGroupId matches no site of the instance. Side effects -- creates the channel and provisions its site group when the ID is unknown; reindexes on write."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Identifier of a commerce channel. Addresses a single storefront bound to a Liferay site, with its own currency, account visibility, and display pages.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/channels/{channelId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public final Channel putChannel(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			Channel channel)
		throws Exception {

		Permission[] permissions = channel.getPermissions();

		Channel putChannel = doPutChannel(channelId, channel);

		if (permissions != null) {
			Page<Permission> permissionsPage = putChannelPermissionsPage(
				putChannel.getId(), permissions);

			putChannel.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return putChannel;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/batch'  -u 'test@liferay.com:test'
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
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/channels/batch")
	@jakarta.ws.rs.Produces("application/json")
	@jakarta.ws.rs.PUT
	@Override
	public Response putChannelBatch(
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
				Channel.class.getName(), callbackURL, object)
		).build();
	}

	protected abstract Channel doPutChannelByExternalReferenceCode(
			String externalReferenceCode, Channel channel)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/by-externalReferenceCode/{externalReferenceCode}' -d $'{"accountExternalReferenceCode": ___, "accountId": ___, "currencyCode": ___, "currencyExternalReferenceCode": ___, "currencyId": ___, "externalReferenceCode": ___, "name": ___, "permissions": ___, "siteGroupId": ___, "type": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Fully replaces the commerce channel by its external reference code (ERC) — every mutable field is set from the body, and omitted optional fields are reset to their default. Upserts when the ERC does not match an existing channel. The supplied currencyCode (or matching currencyId / currencyExternalReferenceCode) must resolve to an existing CommerceCurrency; an unresolved code returns 404. Calls CommerceCurrencyUtil.getCommerceCurrency + AccountEntryService.fetchAccountEntryByExternalReferenceCode + CommerceChannelService.addOrUpdateCommerceChannel. Validation -- NoSuchCurrencyException -> 404 when currency not resolvable; CommerceChannelNameException -> 400 when invalid name; CommerceChannelTypeException -> 400 when invalid type; CommerceChannelSiteGroupIdException -> 400 when siteGroupId matches no site of the instance. Side effects -- creates the channel and provisions its site group when the ERC is unknown; reindexes on write."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "External reference code that addresses the target resource on the `by-externalReferenceCode` paths. The code is the integration-supplied idempotency key, unique within the resource scope; it is assigned by the client rather than the server.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/channels/by-externalReferenceCode/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public final Channel putChannelByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode,
			Channel channel)
		throws Exception {

		Permission[] permissions = channel.getPermissions();

		Channel putChannel = doPutChannelByExternalReferenceCode(
			externalReferenceCode, channel);

		if (permissions != null) {
			Page<Permission> permissionsPage = putChannelPermissionsPage(
				putChannel.getId(), permissions);

			putChannel.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return putChannel;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-commerce-admin-channel/v1.0/channels/{channelId}/permissions'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Replaces the role permissions granted on the channel identified by ID. Roles missing from the request lose their individual permissions. Requires the PERMISSIONS action on the channel."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				description = "Identifier of a commerce channel. Addresses a single storefront bound to a Liferay site, with its own currency, account visibility, and display pages.",
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "channelId", required = true
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Channel")}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/channels/{channelId}/permissions")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Page<Permission> putChannelPermissionsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("channelId")
			Long channelId,
			Permission[] permissions)
		throws Exception {

		Long groupId = getPermissionCheckerGroupId(channelId);
		Long resourceId = getPermissionCheckerResourceId(channelId);
		String resourceName = getPermissionCheckerResourceName(channelId);

		PermissionServiceUtil.checkPermission(
			groupId, resourceName, resourceId);

		ModelPermissions modelPermissions =
			ModelPermissionsUtil.toModelPermissions(
				contextCompany.getCompanyId(), permissions, resourceId,
				resourceName, resourceActionLocalService,
				resourcePermissionLocalService, roleLocalService);

		Collection<String> roleNames = modelPermissions.getRoleNames();

		for (ResourcePermission resourcePermission :
				resourcePermissionLocalService.getResourcePermissions(
					contextCompany.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(resourceId))) {

			com.liferay.portal.kernel.model.Role role =
				roleLocalService.fetchRole(resourcePermission.getRoleId());

			if ((role == null) || roleNames.contains(role.getName())) {
				continue;
			}

			for (ResourceAction resourceAction :
					resourceActionLocalService.getResourceActions(
						resourceName)) {

				resourcePermissionLocalService.removeResourcePermission(
					contextCompany.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(resourceId), role.getRoleId(),
					resourceAction.getActionId());
			}
		}

		resourcePermissionLocalService.updateResourcePermissions(
			contextCompany.getCompanyId(), groupId, resourceName,
			String.valueOf(resourceId), modelPermissions);

		return toPermissionPage(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"getChannelPermissionsPage", null, resourceName, groupId)
			).put(
				"replace",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"putChannelPermissionsPage", null, resourceName, groupId)
			).build(),
			resourceId, resourceName, null);
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<Channel> channels, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Channel, Channel, Exception> channelUnsafeFunction =
			null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			channelUnsafeFunction = channel -> postChannel(channel);
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				channelUnsafeFunction = channel -> {
					Channel getChannel = null;
					Channel persistedChannel = null;

					try {
						getChannel = getChannelByExternalReferenceCode(
							channel.getExternalReferenceCode());

						persistedChannel = patchChannel(
							getChannel.getId(), channel);
					}
					catch (NoSuchModelException noSuchModelException) {
						persistedChannel = postChannel(channel);
					}

					return persistedChannel;
				};
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				channelUnsafeFunction = channel -> {
					Channel persistedChannel = null;

					persistedChannel = putChannelByExternalReferenceCode(
						channel.getExternalReferenceCode(), channel);

					return persistedChannel;
				};
			}
		}

		if (channelUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for Channel");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				channels, channelUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				channels, channelUnsafeFunction::apply);
		}
		else {
			for (Channel channel : channels) {
				channelUnsafeFunction.apply(channel);
			}
		}
	}

	@Override
	public void delete(
			Collection<Channel> channels, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Channel, Channel, Exception> channelUnsafeFunction =
			channel -> {
				if (channel.getId() != null) {
					try {
						deleteChannel(channel.getId());

						return channel;
					}
					catch (Exception exception) {
						if (channel.getExternalReferenceCode() != null) {
							deleteChannelByExternalReferenceCode(
								channel.getExternalReferenceCode());

							return channel;
						}
					}
				}
				else if (channel.getExternalReferenceCode() != null) {
					deleteChannelByExternalReferenceCode(
						channel.getExternalReferenceCode());

					return channel;
				}

				throw new UnsupportedOperationException(
					"Unable to delete by external reference code or ID");
			};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				channels, channelUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				channels, channelUnsafeFunction::apply);
		}
		else {
			for (Channel channel : channels) {
				channelUnsafeFunction.apply(channel);
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

	public String getResourceName() {
		return "Channel";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<Channel> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		return getChannelsPage(search, filter, pagination, sorts);
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
			Collection<Channel> channels, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Channel, Channel, Exception> channelUnsafeFunction =
			null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			channelUnsafeFunction = channel -> patchChannel(
				channel.getId(), channel);
		}

		if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
			channelUnsafeFunction = channel -> putChannel(
				channel.getId(), channel);
		}

		if (channelUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for Channel");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				channels, channelUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				channels, channelUnsafeFunction::apply);
		}
		else {
			for (Channel channel : channels) {
				channelUnsafeFunction.apply(channel);
			}
		}
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public Channel getItem(Long id) throws Exception {
		return getChannel(id);
	}

	protected String getPermissionCheckerActionsResourceName(Object id)
		throws Exception {

		return getPermissionCheckerResourceName(id);
	}

	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String getPermissionCheckerPortletName(Object id)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long getPermissionCheckerResourceId(Object id) throws Exception {
		return GetterUtil.getLong(id);
	}

	protected String getPermissionCheckerResourceName(Object id)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Page<Permission> toPermissionPage(
			Map<String, Map<String, String>> actions, long id,
			String resourceName, String roleNames)
		throws Exception {

		List<ResourceAction> resourceActions =
			resourceActionLocalService.getResourceActions(resourceName);

		if (Validator.isNotNull(roleNames)) {
			return Page.of(
				actions,
				_getPermissions(
					contextCompany.getCompanyId(), resourceActions, id,
					resourceName, StringUtil.split(roleNames)));
		}

		return Page.of(
			actions,
			_getPermissions(
				contextCompany.getCompanyId(), resourceActions, id,
				resourceName, null));
	}

	/**
	 * @see com.liferay.portal.vulcan.permission.PermissionUtil#getPermissions(long, List, long, String, String[])
	 */
	private Collection<Permission> _getPermissions(
			long companyId, List<ResourceAction> resourceActions,
			long resourceId, String resourceName, String[] roleNames)
		throws Exception {

		Map<String, Permission> permissions = new HashMap<>();

		int count = resourcePermissionLocalService.getResourcePermissionsCount(
			companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(resourceId));

		if (count == 0) {
			ResourceLocalServiceUtil.addResources(
				companyId, resourceId, 0, resourceName,
				String.valueOf(resourceId), false, true, true);
		}

		List<String> actionIds = transform(
			resourceActions, resourceAction -> resourceAction.getActionId());

		Set<ResourcePermission> resourcePermissions = new HashSet<>();

		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId)));
		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_GROUP,
				String.valueOf(GroupThreadLocal.getGroupId())));
		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_GROUP_TEMPLATE,
				"0"));
		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(resourceId)));

		List<Resource> resources = transform(
			resourcePermissions,
			resourcePermission -> ResourceLocalServiceUtil.getResource(
				resourcePermission.getCompanyId(), resourcePermission.getName(),
				resourcePermission.getScope(),
				resourcePermission.getPrimKey()));

		Set<com.liferay.portal.kernel.model.Role> roles = new HashSet<>();

		if (roleNames != null) {
			for (String roleName : roleNames) {
				roles.add(roleLocalService.getRole(companyId, roleName));
			}
		}
		else {
			for (ResourcePermission resourcePermission : resourcePermissions) {
				com.liferay.portal.kernel.model.Role role =
					roleLocalService.getRole(resourcePermission.getRoleId());

				roles.add(role);
			}
		}

		for (com.liferay.portal.kernel.model.Role role : roles) {
			Set<String> actionsIdsSet = new HashSet<>();

			for (Resource resource : resources) {
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_COMPANY,
							String.valueOf(resource.getCompanyId()),
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_GROUP,
							String.valueOf(GroupThreadLocal.getGroupId()),
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							resource.getScope(), resource.getPrimKey(),
							role.getRoleId(), actionIds));
			}

			if (actionsIdsSet.isEmpty()) {
				continue;
			}

			Permission permission = new Permission() {
				{
					actionIds = actionsIdsSet.toArray(new String[0]);

					roleExternalReferenceCode = role.getExternalReferenceCode();

					roleName = role.getName();
				}
			};

			permissions.put(role.getName(), permission);
		}

		return permissions.values();
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<Channel>, UnsafeFunction<Channel, Channel, Exception>,
			 Exception> contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<Channel>, UnsafeConsumer<Channel, Exception>, Exception>
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
		return "headless-commerce-admin-channel";
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

	protected void preparePatch(Channel channel, Channel existingChannel) {
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
		<Collection<Channel>, UnsafeFunction<Channel, Channel, Exception>,
		 Exception> contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<Channel>, UnsafeConsumer<Channel, Exception>, Exception>
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
		LogFactoryUtil.getLog(BaseChannelResourceImpl.class);

}
// LIFERAY-REST-BUILDER-HASH:-1318324600