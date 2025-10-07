/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.graphql.query.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountGroup;
import com.liferay.headless.admin.user.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.headless.admin.user.dto.v1_0.Phone;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.Role;
import com.liferay.headless.admin.user.dto.v1_0.RolePermission;
import com.liferay.headless.admin.user.dto.v1_0.Segment;
import com.liferay.headless.admin.user.dto.v1_0.SegmentUser;
import com.liferay.headless.admin.user.dto.v1_0.SharedAsset;
import com.liferay.headless.admin.user.dto.v1_0.Site;
import com.liferay.headless.admin.user.dto.v1_0.Subscription;
import com.liferay.headless.admin.user.dto.v1_0.Ticket;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.dto.v1_0.UserAccountFullNameDefinition;
import com.liferay.headless.admin.user.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.resource.v1_0.AccountGroupResource;
import com.liferay.headless.admin.user.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.resource.v1_0.AccountRoleResource;
import com.liferay.headless.admin.user.resource.v1_0.EmailAddressResource;
import com.liferay.headless.admin.user.resource.v1_0.OrganizationResource;
import com.liferay.headless.admin.user.resource.v1_0.PhoneResource;
import com.liferay.headless.admin.user.resource.v1_0.PostalAddressResource;
import com.liferay.headless.admin.user.resource.v1_0.RoleResource;
import com.liferay.headless.admin.user.resource.v1_0.SegmentResource;
import com.liferay.headless.admin.user.resource.v1_0.SegmentUserResource;
import com.liferay.headless.admin.user.resource.v1_0.SharedAssetResource;
import com.liferay.headless.admin.user.resource.v1_0.SiteResource;
import com.liferay.headless.admin.user.resource.v1_0.SubscriptionResource;
import com.liferay.headless.admin.user.resource.v1_0.TicketResource;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountFullNameDefinitionResource;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.user.resource.v1_0.UserGroupResource;
import com.liferay.headless.admin.user.resource.v1_0.WebUrlResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setAccountResourceComponentServiceObjects(
		ComponentServiceObjects<AccountResource>
			accountResourceComponentServiceObjects) {

		_accountResourceComponentServiceObjects =
			accountResourceComponentServiceObjects;
	}

	public static void setAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<AccountGroupResource>
			accountGroupResourceComponentServiceObjects) {

		_accountGroupResourceComponentServiceObjects =
			accountGroupResourceComponentServiceObjects;
	}

	public static void setAccountRoleResourceComponentServiceObjects(
		ComponentServiceObjects<AccountRoleResource>
			accountRoleResourceComponentServiceObjects) {

		_accountRoleResourceComponentServiceObjects =
			accountRoleResourceComponentServiceObjects;
	}

	public static void setEmailAddressResourceComponentServiceObjects(
		ComponentServiceObjects<EmailAddressResource>
			emailAddressResourceComponentServiceObjects) {

		_emailAddressResourceComponentServiceObjects =
			emailAddressResourceComponentServiceObjects;
	}

	public static void setOrganizationResourceComponentServiceObjects(
		ComponentServiceObjects<OrganizationResource>
			organizationResourceComponentServiceObjects) {

		_organizationResourceComponentServiceObjects =
			organizationResourceComponentServiceObjects;
	}

	public static void setPhoneResourceComponentServiceObjects(
		ComponentServiceObjects<PhoneResource>
			phoneResourceComponentServiceObjects) {

		_phoneResourceComponentServiceObjects =
			phoneResourceComponentServiceObjects;
	}

	public static void setPostalAddressResourceComponentServiceObjects(
		ComponentServiceObjects<PostalAddressResource>
			postalAddressResourceComponentServiceObjects) {

		_postalAddressResourceComponentServiceObjects =
			postalAddressResourceComponentServiceObjects;
	}

	public static void setRoleResourceComponentServiceObjects(
		ComponentServiceObjects<RoleResource>
			roleResourceComponentServiceObjects) {

		_roleResourceComponentServiceObjects =
			roleResourceComponentServiceObjects;
	}

	public static void setSegmentResourceComponentServiceObjects(
		ComponentServiceObjects<SegmentResource>
			segmentResourceComponentServiceObjects) {

		_segmentResourceComponentServiceObjects =
			segmentResourceComponentServiceObjects;
	}

	public static void setSegmentUserResourceComponentServiceObjects(
		ComponentServiceObjects<SegmentUserResource>
			segmentUserResourceComponentServiceObjects) {

		_segmentUserResourceComponentServiceObjects =
			segmentUserResourceComponentServiceObjects;
	}

	public static void setSharedAssetResourceComponentServiceObjects(
		ComponentServiceObjects<SharedAssetResource>
			sharedAssetResourceComponentServiceObjects) {

		_sharedAssetResourceComponentServiceObjects =
			sharedAssetResourceComponentServiceObjects;
	}

	public static void setSiteResourceComponentServiceObjects(
		ComponentServiceObjects<SiteResource>
			siteResourceComponentServiceObjects) {

		_siteResourceComponentServiceObjects =
			siteResourceComponentServiceObjects;
	}

	public static void setSubscriptionResourceComponentServiceObjects(
		ComponentServiceObjects<SubscriptionResource>
			subscriptionResourceComponentServiceObjects) {

		_subscriptionResourceComponentServiceObjects =
			subscriptionResourceComponentServiceObjects;
	}

	public static void setTicketResourceComponentServiceObjects(
		ComponentServiceObjects<TicketResource>
			ticketResourceComponentServiceObjects) {

		_ticketResourceComponentServiceObjects =
			ticketResourceComponentServiceObjects;
	}

	public static void setUserAccountResourceComponentServiceObjects(
		ComponentServiceObjects<UserAccountResource>
			userAccountResourceComponentServiceObjects) {

		_userAccountResourceComponentServiceObjects =
			userAccountResourceComponentServiceObjects;
	}

	public static void
		setUserAccountFullNameDefinitionResourceComponentServiceObjects(
			ComponentServiceObjects<UserAccountFullNameDefinitionResource>
				userAccountFullNameDefinitionResourceComponentServiceObjects) {

		_userAccountFullNameDefinitionResourceComponentServiceObjects =
			userAccountFullNameDefinitionResourceComponentServiceObjects;
	}

	public static void setUserGroupResourceComponentServiceObjects(
		ComponentServiceObjects<UserGroupResource>
			userGroupResourceComponentServiceObjects) {

		_userGroupResourceComponentServiceObjects =
			userGroupResourceComponentServiceObjects;
	}

	public static void setWebUrlResourceComponentServiceObjects(
		ComponentServiceObjects<WebUrlResource>
			webUrlResourceComponentServiceObjects) {

		_webUrlResourceComponentServiceObjects =
			webUrlResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {account(accountId: ___){accountContactInformation, accountGroupBriefs, accountRoles, accountUserAccounts, actions, creator, customFields, dateCreated, dateModified, defaultBillingAddressExternalReferenceCode, defaultBillingAddressId, defaultShippingAddressExternalReferenceCode, defaultShippingAddressId, description, domains, externalReferenceCode, id, keywords, logoBase64, logoExternalReferenceCode, logoId, logoURL, name, numberOfUsers, organizationExternalReferenceCodes, organizationIds, parentAccountExternalReferenceCode, parentAccountId, permissions, postalAddresses, status, taxId, taxonomyCategoryBriefs, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Account account(@GraphQLName("accountId") Long accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.getAccount(accountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCode(externalReferenceCode: ___){accountContactInformation, accountGroupBriefs, accountRoles, accountUserAccounts, actions, creator, customFields, dateCreated, dateModified, defaultBillingAddressExternalReferenceCode, defaultBillingAddressId, defaultShippingAddressExternalReferenceCode, defaultShippingAddressId, description, domains, externalReferenceCode, id, keywords, logoBase64, logoExternalReferenceCode, logoId, logoURL, name, numberOfUsers, organizationExternalReferenceCodes, organizationIds, parentAccountExternalReferenceCode, parentAccountId, permissions, postalAddresses, status, taxId, taxonomyCategoryBriefs, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Account accountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.getAccountByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroupAccounts(accountGroupId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the list of accounts in an account group."
	)
	public AccountPage accountGroupAccounts(
			@GraphQLName("accountGroupId") Long accountGroupId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> new AccountPage(
				accountResource.getAccountGroupAccountsPage(
					accountGroupId, search,
					_filterBiFunction.apply(accountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroupByExternalReferenceCodeAccounts(accountGroupExternalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the list of accounts in an account group."
	)
	public AccountPage accountGroupByExternalReferenceCodeAccounts(
			@GraphQLName("accountGroupExternalReferenceCode") String
				accountGroupExternalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> new AccountPage(
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, search,
						_filterBiFunction.apply(accountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accounts(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the accounts. Results can be paginated, filtered, searched, and sorted."
	)
	public AccountPage accounts(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> new AccountPage(
				accountResource.getAccountsPage(
					search,
					_filterBiFunction.apply(accountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationAccounts(filter: ___, organizationId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organization's members (accounts). Results can be paginated, filtered, searched, and sorted."
	)
	public AccountPage organizationAccounts(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> new AccountPage(
				accountResource.getOrganizationAccountsPage(
					organizationId, search,
					_filterBiFunction.apply(accountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodeAccounts(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organization's members (accounts). Results can be paginated, filtered, searched, and sorted."
	)
	public AccountPage organizationByExternalReferenceCodeAccounts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> new AccountPage(
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(accountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCode(filter: ___, organizationExternalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organization's members (accounts) by external reference code. Results can be paginated, filtered, searched, and sorted."
	)
	public AccountPage
			organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCode(
				@GraphQLName("organizationExternalReferenceCode") String
					organizationExternalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> new AccountPage(
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, search,
						_filterBiFunction.apply(accountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAccountGroups(accountId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountGroupPage accountAccountGroups(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> new AccountGroupPage(
				accountGroupResource.getAccountAccountGroupsPage(
					accountId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroups(accountExternalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountGroupPage
			accountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroups(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> new AccountGroupPage(
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						accountExternalReferenceCode,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroup(accountGroupId: ___){accountBriefs, actions, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, permissions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountGroup accountGroup(
			@GraphQLName("accountGroupId") Long accountGroupId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> accountGroupResource.getAccountGroup(
				accountGroupId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroupByExternalReferenceCode(externalReferenceCode: ___){accountBriefs, actions, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, permissions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountGroup accountGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.getAccountGroupByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroups(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the account groups. Results can be paginated, filtered, searched, and sorted."
	)
	public AccountGroupPage accountGroups(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> new AccountGroupPage(
				accountGroupResource.getAccountGroupsPage(
					search,
					_filterBiFunction.apply(accountGroupResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						accountGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAccountRolesByExternalReferenceCode(externalReferenceCode: ___, filter: ___, keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets the account's roles")
	public AccountRolePage accountAccountRolesByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("keywords") String keywords,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource -> new AccountRolePage(
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, keywords,
						_filterBiFunction.apply(
							accountRoleResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							accountRoleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAccountRoles(accountId: ___, filter: ___, keywords: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets the account's roles")
	public AccountRolePage accountAccountRoles(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("keywords") String keywords,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource -> new AccountRolePage(
				accountRoleResource.getAccountAccountRolesPage(
					accountId, keywords,
					_filterBiFunction.apply(accountRoleResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(accountRoleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeUserAccountByEmailAddressAccountRoles(emailAddress: ___, externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Gets a user's account roles by their email address from an account by external reference code"
	)
	public AccountRolePage
			accountByExternalReferenceCodeUserAccountByEmailAddressAccountRoles(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource -> new AccountRolePage(
				accountRoleResource.
					getAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage(
						externalReferenceCode, emailAddress)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRoles(accountExternalReferenceCode: ___, externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Gets a user's account roles by their external reference code from an account by external reference code"
	)
	public AccountRolePage
			accountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRoles(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource -> new AccountRolePage(
				accountRoleResource.
					getAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage(
						accountExternalReferenceCode, externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeEmailAddresses(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's email addresses.")
	public EmailAddressPage accountByExternalReferenceCodeEmailAddresses(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> new EmailAddressPage(
				emailAddressResource.
					getAccountByExternalReferenceCodeEmailAddressesPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountEmailAddresses(accountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's email addresses.")
	public EmailAddressPage accountEmailAddresses(
			@GraphQLName("accountId") Long accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> new EmailAddressPage(
				emailAddressResource.getAccountEmailAddressesPage(accountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {emailAddress(emailAddressId: ___){emailAddress, externalReferenceCode, id, primary, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the email address.")
	public EmailAddress emailAddress(
			@GraphQLName("emailAddressId") Long emailAddressId)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> emailAddressResource.getEmailAddress(
				emailAddressId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {emailAddressByExternalReferenceCode(externalReferenceCode: ___){emailAddress, externalReferenceCode, id, primary, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the email address.")
	public EmailAddress emailAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource ->
				emailAddressResource.getEmailAddressByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodeEmailAddresses(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the organization's email addresses.")
	public EmailAddressPage organizationByExternalReferenceCodeEmailAddresses(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> new EmailAddressPage(
				emailAddressResource.
					getOrganizationByExternalReferenceCodeEmailAddressesPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationEmailAddresses(organizationId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the organization's email addresses.")
	public EmailAddressPage organizationEmailAddresses(
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> new EmailAddressPage(
				emailAddressResource.getOrganizationEmailAddressesPage(
					organizationId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountByExternalReferenceCodeEmailAddresses(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's email addresses.")
	public EmailAddressPage userAccountByExternalReferenceCodeEmailAddresses(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> new EmailAddressPage(
				emailAddressResource.
					getUserAccountByExternalReferenceCodeEmailAddressesPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountEmailAddresses(userAccountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's email addresses.")
	public EmailAddressPage userAccountEmailAddresses(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> new EmailAddressPage(
				emailAddressResource.getUserAccountEmailAddressesPage(
					userAccountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeOrganization(externalReferenceCode: ___, organizationId: ___){accountBriefs, actions, childOrganizations, comment, creator, customFields, dateCreated, dateModified, externalReferenceCode, id, image, imageBase64, imageExternalReferenceCode, imageId, keywords, location, name, numberOfAccounts, numberOfOrganizations, numberOfUsers, organizationAccounts, organizationContactInformation, parentOrganization, permissions, roleBriefs, services, taxonomyCategoryBriefs, treePath, userAccountBriefs, userAccounts}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Organization accountByExternalReferenceCodeOrganization(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.
					getAccountByExternalReferenceCodeOrganization(
						externalReferenceCode, organizationId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeOrganizations(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the account's organizations. Results can be paginated, filtered, searched, and sorted."
	)
	public OrganizationPage accountByExternalReferenceCodeOrganizations(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> new OrganizationPage(
				organizationResource.
					getAccountByExternalReferenceCodeOrganizationsPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							organizationResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							organizationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountOrganization(accountId: ___, organizationId: ___){accountBriefs, actions, childOrganizations, comment, creator, customFields, dateCreated, dateModified, externalReferenceCode, id, image, imageBase64, imageExternalReferenceCode, imageId, keywords, location, name, numberOfAccounts, numberOfOrganizations, numberOfUsers, organizationAccounts, organizationContactInformation, parentOrganization, permissions, roleBriefs, services, taxonomyCategoryBriefs, treePath, userAccountBriefs, userAccounts}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Organization accountOrganization(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.getAccountOrganization(
				accountId, organizationId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountOrganizations(accountId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the account's organizations. Results can be paginated, filtered, searched, and sorted."
	)
	public OrganizationPage accountOrganizations(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> new OrganizationPage(
				organizationResource.getAccountOrganizationsPage(
					accountId, search,
					_filterBiFunction.apply(organizationResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						organizationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organization(organizationId: ___){accountBriefs, actions, childOrganizations, comment, creator, customFields, dateCreated, dateModified, externalReferenceCode, id, image, imageBase64, imageExternalReferenceCode, imageId, keywords, location, name, numberOfAccounts, numberOfOrganizations, numberOfUsers, organizationAccounts, organizationContactInformation, parentOrganization, permissions, roleBriefs, services, taxonomyCategoryBriefs, treePath, userAccountBriefs, userAccounts}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the organization.")
	public Organization organization(
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.getOrganization(
				organizationId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCode(externalReferenceCode: ___){accountBriefs, actions, childOrganizations, comment, creator, customFields, dateCreated, dateModified, externalReferenceCode, id, image, imageBase64, imageExternalReferenceCode, imageId, keywords, location, name, numberOfAccounts, numberOfOrganizations, numberOfUsers, organizationAccounts, organizationContactInformation, parentOrganization, permissions, roleBriefs, services, taxonomyCategoryBriefs, treePath, userAccountBriefs, userAccounts}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Organization organizationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.getOrganizationByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodeChildOrganizations(externalReferenceCode: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent organization's child organizations. Results can be paginated, filtered, searched, and sorted."
	)
	public OrganizationPage
			organizationByExternalReferenceCodeChildOrganizations(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> new OrganizationPage(
				organizationResource.
					getOrganizationByExternalReferenceCodeChildOrganizationsPage(
						externalReferenceCode, flatten, search,
						_filterBiFunction.apply(
							organizationResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							organizationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationChildOrganizations(filter: ___, flatten: ___, organizationId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent organization's child organizations. Results can be paginated, filtered, searched, and sorted."
	)
	public OrganizationPage organizationChildOrganizations(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> new OrganizationPage(
				organizationResource.getOrganizationChildOrganizationsPage(
					organizationId, flatten, search,
					_filterBiFunction.apply(organizationResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						organizationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationOrganizations(filter: ___, flatten: ___, page: ___, pageSize: ___, parentOrganizationId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent organization's child organizations. Results can be paginated, filtered, searched, and sorted."
	)
	public OrganizationPage organizationOrganizations(
			@GraphQLName("parentOrganizationId") String parentOrganizationId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> new OrganizationPage(
				organizationResource.getOrganizationOrganizationsPage(
					parentOrganizationId, flatten, search,
					_filterBiFunction.apply(organizationResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						organizationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizations(filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organizations. Results can be paginated, filtered, searched, and sorted."
	)
	public OrganizationPage organizations(
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> new OrganizationPage(
				organizationResource.getOrganizationsPage(
					flatten, search,
					_filterBiFunction.apply(organizationResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						organizationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodePhones(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's phone numbers.")
	public PhonePage accountByExternalReferenceCodePhones(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> new PhonePage(
				phoneResource.getAccountByExternalReferenceCodePhonesPage(
					externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountPhones(accountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's phone numbers.")
	public PhonePage accountPhones(@GraphQLName("accountId") Long accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> new PhonePage(
				phoneResource.getAccountPhonesPage(accountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodePhones(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the organization's phone numbers.")
	public PhonePage organizationByExternalReferenceCodePhones(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> new PhonePage(
				phoneResource.getOrganizationByExternalReferenceCodePhonesPage(
					externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationPhones(organizationId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the organization's phone numbers.")
	public PhonePage organizationPhones(
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> new PhonePage(
				phoneResource.getOrganizationPhonesPage(organizationId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {phone(phoneId: ___){extension, externalReferenceCode, id, phoneNumber, phoneType, primary}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the phone number.")
	public Phone phone(@GraphQLName("phoneId") Long phoneId) throws Exception {
		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.getPhone(phoneId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {phoneByExternalReferenceCode(externalReferenceCode: ___){extension, externalReferenceCode, id, phoneNumber, phoneType, primary}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the phone number by external reference code."
	)
	public Phone phoneByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.getPhoneByExternalReferenceCode(
				externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountByExternalReferenceCodePhones(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's phone numbers.")
	public PhonePage userAccountByExternalReferenceCodePhones(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> new PhonePage(
				phoneResource.getUserAccountByExternalReferenceCodePhonesPage(
					externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountPhones(userAccountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's phone numbers.")
	public PhonePage userAccountPhones(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> new PhonePage(
				phoneResource.getUserAccountPhonesPage(userAccountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodePostalAddresses(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's postal addresses.")
	public PostalAddressPage accountByExternalReferenceCodePostalAddresses(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> new PostalAddressPage(
				postalAddressResource.
					getAccountByExternalReferenceCodePostalAddressesPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountPostalAddresses(accountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's postal addresses.")
	public PostalAddressPage accountPostalAddresses(
			@GraphQLName("accountId") Long accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> new PostalAddressPage(
				postalAddressResource.getAccountPostalAddressesPage(
					accountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodePostalAddresses(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organization's postal addresses."
	)
	public PostalAddressPage organizationByExternalReferenceCodePostalAddresses(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> new PostalAddressPage(
				postalAddressResource.
					getOrganizationByExternalReferenceCodePostalAddressesPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationPostalAddresses(organizationId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organization's postal addresses."
	)
	public PostalAddressPage organizationPostalAddresses(
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> new PostalAddressPage(
				postalAddressResource.getOrganizationPostalAddressesPage(
					organizationId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {postalAddress(postalAddressId: ___){addressCountry, addressCountry_i18n, addressLocality, addressRegion, addressSubtype, addressType, externalReferenceCode, id, name, phoneNumber, postalCode, primary, streetAddressLine1, streetAddressLine2, streetAddressLine3}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the postal address.")
	public PostalAddress postalAddress(
			@GraphQLName("postalAddressId") Long postalAddressId)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> postalAddressResource.getPostalAddress(
				postalAddressId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {postalAddressByExternalReferenceCode(externalReferenceCode: ___){addressCountry, addressCountry_i18n, addressLocality, addressRegion, addressSubtype, addressType, externalReferenceCode, id, name, phoneNumber, postalCode, primary, streetAddressLine1, streetAddressLine2, streetAddressLine3}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the postal address using external reference code."
	)
	public PostalAddress postalAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.getPostalAddressByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountByExternalReferenceCodePostalAddresses(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's postal addresses.")
	public PostalAddressPage userAccountByExternalReferenceCodePostalAddresses(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> new PostalAddressPage(
				postalAddressResource.
					getUserAccountByExternalReferenceCodePostalAddressesPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountPostalAddresses(userAccountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's postal addresses.")
	public PostalAddressPage userAccountPostalAddresses(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> new PostalAddressPage(
				postalAddressResource.getUserAccountPostalAddressesPage(
					userAccountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {role(roleId: ___){actions, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, permissions, rolePermissions, roleType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the role.")
	public Role role(@GraphQLName("roleId") Long roleId) throws Exception {
		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.getRole(roleId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {roleByExternalReferenceCode(externalReferenceCode: ___){actions, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, permissions, rolePermissions, roleType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the role by its external reference code."
	)
	public Role roleByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.getRoleByExternalReferenceCode(
				externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {roles(filter: ___, page: ___, pageSize: ___, search: ___, types: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the portal instance's roles. Results can be paginated."
	)
	public RolePage roles(
			@GraphQLName("search") String search,
			@GraphQLName("types") Integer[] types,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> new RolePage(
				roleResource.getRolesPage(
					search, types,
					_filterBiFunction.apply(roleResource, filterString),
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {segments(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets a site's segments.")
	public SegmentPage segments(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_segmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			segmentResource -> new SegmentPage(
				segmentResource.getSiteSegmentsPage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountSegments(siteKey: ___, userAccountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Gets a user's segments. The set of available headers are: Accept-Language (string), Host (string), User-Agent (string), X-Browser (string), X-Cookies (collection string), X-Device-Brand (string), X-Device-Model (string), X-Device-Screen-Resolution-Height (double), X-Device-Screen-Resolution-Width (double), X-Last-Sign-In-Date-Time (date time) and X-Signed-In (boolean). Local date will be always present in the request."
	)
	public SegmentPage userAccountSegments(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_segmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			segmentResource -> new SegmentPage(
				segmentResource.getSiteUserAccountSegmentsPage(
					Long.valueOf(siteKey), userAccountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {segmentUserAccounts(page: ___, pageSize: ___, segmentId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets a segment's users.")
	public SegmentUserPage segmentUserAccounts(
			@GraphQLName("segmentId") Long segmentId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_segmentUserResourceComponentServiceObjects,
			this::_populateResourceContext,
			segmentUserResource -> new SegmentUserPage(
				segmentUserResource.getSegmentUserAccountsPage(
					segmentId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {myUserAccountSharedAssetsSharedByMe(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SharedAssetPage myUserAccountSharedAssetsSharedByMe(
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_sharedAssetResourceComponentServiceObjects,
			this::_populateResourceContext,
			sharedAssetResource -> new SharedAssetPage(
				sharedAssetResource.getMyUserAccountSharedAssetsSharedByMePage(
					search,
					_aggregationBiFunction.apply(
						sharedAssetResource, aggregations),
					_filterBiFunction.apply(sharedAssetResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(sharedAssetResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {myUserAccountSharedAssetsSharedWithMe(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SharedAssetPage myUserAccountSharedAssetsSharedWithMe(
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_sharedAssetResourceComponentServiceObjects,
			this::_populateResourceContext,
			sharedAssetResource -> new SharedAssetPage(
				sharedAssetResource.
					getMyUserAccountSharedAssetsSharedWithMePage(
						search,
						_aggregationBiFunction.apply(
							sharedAssetResource, aggregations),
						_filterBiFunction.apply(
							sharedAssetResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							sharedAssetResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {myUserAccountSites(page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SitePage myUserAccountSites(
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteResource -> new SitePage(
				siteResource.getMyUserAccountSitesPage(
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {site(siteKey: ___){availableLanguages, creator, description, description_i18n, descriptiveName, descriptiveName_i18n, friendlyUrlPath, id, key, membershipType, name, name_i18n, parentSiteId, sites}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Site site(@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteResource -> siteResource.getSite(Long.valueOf(siteKey)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {byFriendlyUrlPath(friendlyUrlPath: ___){availableLanguages, creator, description, description_i18n, descriptiveName, descriptiveName_i18n, friendlyUrlPath, id, key, membershipType, name, name_i18n, parentSiteId, sites}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Site byFriendlyUrlPath(
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteResource -> siteResource.getSiteByFriendlyUrlPath(
				friendlyUrlPath));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {myUserAccountSubscription(subscriptionId: ___){contentId, contentType, dateCreated, dateModified, frequency, id, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Subscription myUserAccountSubscription(
			@GraphQLName("subscriptionId") Long subscriptionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_subscriptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			subscriptionResource ->
				subscriptionResource.getMyUserAccountSubscription(
					subscriptionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {myUserAccountSubscriptions(contentType: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SubscriptionPage myUserAccountSubscriptions(
			@GraphQLName("contentType") String contentType,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_subscriptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			subscriptionResource -> new SubscriptionPage(
				subscriptionResource.getMyUserAccountSubscriptionsPage(
					contentType, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountEmailVerificationTicket(userAccountId: ___){expirationDate, extraInfo, id, key}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the user's email verification ticket."
	)
	public Ticket userAccountEmailVerificationTicket(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_ticketResourceComponentServiceObjects,
			this::_populateResourceContext,
			ticketResource ->
				ticketResource.getUserAccountEmailVerificationTicket(
					userAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountPasswordResetTicket(userAccountId: ___){expirationDate, extraInfo, id, key}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's password reset ticket.")
	public Ticket userAccountPasswordResetTicket(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_ticketResourceComponentServiceObjects,
			this::_populateResourceContext,
			ticketResource -> ticketResource.getUserAccountPasswordResetTicket(
				userAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeUserAccountByExternalReferenceCode(accountExternalReferenceCode: ___, externalReferenceCode: ___){accountBriefs, actions, additionalName, alternateName, assetLibraryBriefs, birthDate, creator, currentPassword, customFields, dashboardURL, dateCreated, dateModified, emailAddress, externalReferenceCode, familyName, gender, givenName, hasLoginDate, honorificPrefix, honorificSuffix, id, image, imageExternalReferenceCode, imageId, jobTitle, keywords, languageDisplayName, languageId, lastLoginDate, name, organizationBriefs, password, permissions, profileURL, roleBriefs, siteBriefs, status, taxonomyCategoryBriefs, userAccountContactInformation, userGroupBriefs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Gets a user by their external reference code to an account by external reference code"
	)
	public UserAccount
			accountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					getAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
						accountExternalReferenceCode, externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountUserAccount(accountId: ___, userAccountId: ___){accountBriefs, actions, additionalName, alternateName, assetLibraryBriefs, birthDate, creator, currentPassword, customFields, dashboardURL, dateCreated, dateModified, emailAddress, externalReferenceCode, familyName, gender, givenName, hasLoginDate, honorificPrefix, honorificSuffix, id, image, imageExternalReferenceCode, imageId, jobTitle, keywords, languageDisplayName, languageId, lastLoginDate, name, organizationBriefs, password, permissions, profileURL, roleBriefs, siteBriefs, status, taxonomyCategoryBriefs, userAccountContactInformation, userGroupBriefs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets a user assigned to an account")
	public UserAccount accountUserAccount(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.getAccountUserAccount(
				accountId, userAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountUserAccountsByExternalReferenceCode(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets the users assigned to an account")
	public UserAccountPage accountUserAccountsByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							userAccountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountUserAccounts(accountId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets the users assigned to an account")
	public UserAccountPage accountUserAccounts(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.getAccountUserAccountsPage(
					accountId, search,
					_filterBiFunction.apply(userAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {myUserAccount{accountBriefs, actions, additionalName, alternateName, assetLibraryBriefs, birthDate, creator, currentPassword, customFields, dashboardURL, dateCreated, dateModified, emailAddress, externalReferenceCode, familyName, gender, givenName, hasLoginDate, honorificPrefix, honorificSuffix, id, image, imageExternalReferenceCode, imageId, jobTitle, keywords, languageDisplayName, languageId, lastLoginDate, name, organizationBriefs, password, permissions, profileURL, roleBriefs, siteBriefs, status, taxonomyCategoryBriefs, userAccountContactInformation, userGroupBriefs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves information about the user who made the request."
	)
	public UserAccount myUserAccount() throws Exception {
		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.getMyUserAccount());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodeUserAccounts(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organization's members (users). Results can be paginated, filtered, searched, and sorted."
	)
	public UserAccountPage organizationByExternalReferenceCodeUserAccounts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							userAccountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationUserAccounts(filter: ___, organizationId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the organization's members (users). Results can be paginated, filtered, searched, and sorted."
	)
	public UserAccountPage organizationUserAccounts(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, search,
					_filterBiFunction.apply(userAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountUserAccountSelected(accountId: ___, siteKey: ___, userAccountId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Gets the selected property of a user assigned to an account for a specific site"
	)
	public Boolean accountUserAccountSelected(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.getSiteAccountUserAccountSelected(
					Long.valueOf(siteKey), accountId, userAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {byFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(accountExternalReferenceCode: ___, friendlyUrlPath: ___, userAccountExternalReferenceCode: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Gets the selected property of a user assigned to an account for a specific site"
	)
	public Boolean
			byFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				@GraphQLName("friendlyUrlPath") String friendlyUrlPath,
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("userAccountExternalReferenceCode") String
					userAccountExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
						friendlyUrlPath, accountExternalReferenceCode,
						userAccountExternalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteUserAccounts(filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site members' user accounts. Results can be paginated, filtered, searched, and sorted."
	)
	public UserAccountPage siteUserAccounts(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.getSiteUserAccountsPage(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(userAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccount(userAccountId: ___){accountBriefs, actions, additionalName, alternateName, assetLibraryBriefs, birthDate, creator, currentPassword, customFields, dashboardURL, dateCreated, dateModified, emailAddress, externalReferenceCode, familyName, gender, givenName, hasLoginDate, honorificPrefix, honorificSuffix, id, image, imageExternalReferenceCode, imageId, jobTitle, keywords, languageDisplayName, languageId, lastLoginDate, name, organizationBriefs, password, permissions, profileURL, roleBriefs, siteBriefs, status, taxonomyCategoryBriefs, userAccountContactInformation, userGroupBriefs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user account.")
	public UserAccount userAccount(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.getUserAccount(
				userAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountByEmailAddress(emailAddress: ___){accountBriefs, actions, additionalName, alternateName, assetLibraryBriefs, birthDate, creator, currentPassword, customFields, dashboardURL, dateCreated, dateModified, emailAddress, externalReferenceCode, familyName, gender, givenName, hasLoginDate, honorificPrefix, honorificSuffix, id, image, imageExternalReferenceCode, imageId, jobTitle, keywords, languageDisplayName, languageId, lastLoginDate, name, organizationBriefs, password, permissions, profileURL, roleBriefs, siteBriefs, status, taxonomyCategoryBriefs, userAccountContactInformation, userGroupBriefs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserAccount userAccountByEmailAddress(
			@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.getUserAccountByEmailAddress(emailAddress));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountByExternalReferenceCode(externalReferenceCode: ___){accountBriefs, actions, additionalName, alternateName, assetLibraryBriefs, birthDate, creator, currentPassword, customFields, dashboardURL, dateCreated, dateModified, emailAddress, externalReferenceCode, familyName, gender, givenName, hasLoginDate, honorificPrefix, honorificSuffix, id, image, imageExternalReferenceCode, imageId, jobTitle, keywords, languageDisplayName, languageId, lastLoginDate, name, organizationBriefs, password, permissions, profileURL, roleBriefs, siteBriefs, status, taxonomyCategoryBriefs, userAccountContactInformation, userGroupBriefs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserAccount userAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.getUserAccountByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountsByStatus(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___, status: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserAccountPage userAccountsByStatus(
			@GraphQLName("status") String status,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.getUserAccountsByStatusPage(
					status, search,
					_filterBiFunction.apply(userAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccounts(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the user accounts. Results can be paginated, filtered, searched, and sorted."
	)
	public UserAccountPage userAccounts(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.getUserAccountsPage(
					search,
					_filterBiFunction.apply(userAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userGroupByExternalReferenceCodeUsers(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the list of users in a user group.")
	public UserAccountPage userGroupByExternalReferenceCodeUsers(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							userAccountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userGroupUsers(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___, userGroupId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the list of users in a user group.")
	public UserAccountPage userGroupUsers(
			@GraphQLName("userGroupId") Long userGroupId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> new UserAccountPage(
				userAccountResource.getUserGroupUsersPage(
					userGroupId, search,
					_filterBiFunction.apply(userAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(userAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountFullNameDefinition(languageId: ___){userAccountFullNameDefinitionFields}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the user account full name definition."
	)
	public UserAccountFullNameDefinition userAccountFullNameDefinition(
			@GraphQLName("languageId") String languageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountFullNameDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountFullNameDefinitionResource ->
				userAccountFullNameDefinitionResource.
					getUserAccountFullNameDefinition(languageId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userGroup(userGroupId: ___){actions, creator, dateCreated, dateModified, description, externalReferenceCode, id, name, permissions, roleBriefs, userAccountBriefs, usersCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserGroup userGroup(@GraphQLName("userGroupId") Long userGroupId)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.getUserGroup(userGroupId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userGroupByExternalReferenceCode(externalReferenceCode: ___){actions, creator, dateCreated, dateModified, description, externalReferenceCode, id, name, permissions, roleBriefs, userAccountBriefs, usersCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserGroup userGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource ->
				userGroupResource.getUserGroupByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userGroups(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public UserGroupPage userGroups(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> new UserGroupPage(
				userGroupResource.getUserGroupsPage(
					search,
					_filterBiFunction.apply(userGroupResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(userGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userUserGroups(userAccountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's user groups.")
	public UserGroupPage userUserGroups(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> new UserGroupPage(
				userGroupResource.getUserUserGroups(userAccountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeWebUrls(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's web URLs.")
	public WebUrlPage accountByExternalReferenceCodeWebUrls(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> new WebUrlPage(
				webUrlResource.getAccountByExternalReferenceCodeWebUrlsPage(
					externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountWebUrls(accountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the account's web URLs.")
	public WebUrlPage accountWebUrls(@GraphQLName("accountId") Long accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> new WebUrlPage(
				webUrlResource.getAccountWebUrlsPage(accountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationByExternalReferenceCodeWebUrls(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the organization's web URLs.")
	public WebUrlPage organizationByExternalReferenceCodeWebUrls(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> new WebUrlPage(
				webUrlResource.
					getOrganizationByExternalReferenceCodeWebUrlsPage(
						externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {organizationWebUrls(organizationId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the organization's URLs.")
	public WebUrlPage organizationWebUrls(
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> new WebUrlPage(
				webUrlResource.getOrganizationWebUrlsPage(organizationId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountByExternalReferenceCodeWebUrls(externalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's web URLs.")
	public WebUrlPage userAccountByExternalReferenceCodeWebUrls(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> new WebUrlPage(
				webUrlResource.getUserAccountByExternalReferenceCodeWebUrlsPage(
					externalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userAccountWebUrls(userAccountId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the user's URLs.")
	public WebUrlPage userAccountWebUrls(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> new WebUrlPage(
				webUrlResource.getUserAccountWebUrlsPage(userAccountId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {webUrl(webUrlId: ___){externalReferenceCode, id, primary, url, urlType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the web URL.")
	public WebUrl webUrl(@GraphQLName("webUrlId") Long webUrlId)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> webUrlResource.getWebUrl(webUrlId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {webUrlByExternalReferenceCode(externalReferenceCode: ___){externalReferenceCode, id, primary, url, urlType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the web URL by external reference code."
	)
	public WebUrl webUrlByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> webUrlResource.getWebUrlByExternalReferenceCode(
				externalReferenceCode));
	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountUserAccountsPageTypeExtension {

		public GetAccountUserAccountsPageTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(description = "Gets the users assigned to an account")
		public UserAccountPage userAccounts(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource -> new UserAccountPage(
					userAccountResource.getAccountUserAccountsPage(
						_account.getId(), search,
						_filterBiFunction.apply(
							userAccountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							userAccountResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountAccountRolesByExternalReferenceCodePageTypeExtension {

		public GetAccountAccountRolesByExternalReferenceCodePageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Gets the account's roles")
		public AccountRolePage accountRolesByExternalReferenceCode(
				@GraphQLName("keywords") String keywords,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountRoleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountRoleResource -> new AccountRolePage(
					accountRoleResource.
						getAccountAccountRolesByExternalReferenceCodePage(
							_account.getExternalReferenceCode(), keywords,
							_filterBiFunction.apply(
								accountRoleResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								accountRoleResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(AccountGroup.class)
	public class GetAccountByExternalReferenceCodeTypeExtension {

		public GetAccountByExternalReferenceCodeTypeExtension(
			AccountGroup accountGroup) {

			_accountGroup = accountGroup;
		}

		@GraphQLField
		public Account accountByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_accountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountResource ->
					accountResource.getAccountByExternalReferenceCode(
						_accountGroup.getExternalReferenceCode()));
		}

		private AccountGroup _accountGroup;

	}

	@GraphQLTypeExtension(Organization.class)
	public class GetOrganizationAccountsPageTypeExtension {

		public GetOrganizationAccountsPageTypeExtension(
			Organization organization) {

			_organization = organization;
		}

		@GraphQLField(
			description = "Retrieves the organization's members (accounts). Results can be paginated, filtered, searched, and sorted."
		)
		public AccountPage accounts(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountResource -> new AccountPage(
					accountResource.getOrganizationAccountsPage(
						_organization.getId(), search,
						_filterBiFunction.apply(accountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(accountResource, sortsString))));
		}

		private Organization _organization;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountByExternalReferenceCodePhonesPageTypeExtension {

		public GetAccountByExternalReferenceCodePhonesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the account's phone numbers.")
		public PhonePage byExternalReferenceCodePhones() throws Exception {
			return _applyComponentServiceObjects(
				_phoneResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				phoneResource -> new PhonePage(
					phoneResource.getAccountByExternalReferenceCodePhonesPage(
						_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Site.class)
	public class GetSiteUserAccountSegmentsPageTypeExtension {

		public GetSiteUserAccountSegmentsPageTypeExtension(Site site) {
			_site = site;
		}

		@GraphQLField(
			description = "Gets a user's segments. The set of available headers are: Accept-Language (string), Host (string), User-Agent (string), X-Browser (string), X-Cookies (collection string), X-Device-Brand (string), X-Device-Model (string), X-Device-Screen-Resolution-Height (double), X-Device-Screen-Resolution-Width (double), X-Last-Sign-In-Date-Time (date time) and X-Signed-In (boolean). Local date will be always present in the request."
		)
		public SegmentPage userAccountSegments(
				@GraphQLName("userAccountId") Long userAccountId)
			throws Exception {

			return _applyComponentServiceObjects(
				_segmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				segmentResource -> new SegmentPage(
					segmentResource.getSiteUserAccountSegmentsPage(
						_site.getId(), userAccountId)));
		}

		private Site _site;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountAccountGroupsPageTypeExtension {

		public GetAccountAccountGroupsPageTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField
		public AccountGroupPage accountGroups(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountGroupResource -> new AccountGroupPage(
					accountGroupResource.getAccountAccountGroupsPage(
						_account.getId(), Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(UserAccount.class)
	public class GetUserAccountPostalAddressesPageTypeExtension {

		public GetUserAccountPostalAddressesPageTypeExtension(
			UserAccount userAccount) {

			_userAccount = userAccount;
		}

		@GraphQLField(description = "Retrieves the user's postal addresses.")
		public PostalAddressPage postalAddresses() throws Exception {
			return _applyComponentServiceObjects(
				_postalAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				postalAddressResource -> new PostalAddressPage(
					postalAddressResource.getUserAccountPostalAddressesPage(
						_userAccount.getId())));
		}

		private UserAccount _userAccount;

	}

	@GraphQLTypeExtension(UserAccount.class)
	public class GetUserAccountEmailAddressesPageTypeExtension {

		public GetUserAccountEmailAddressesPageTypeExtension(
			UserAccount userAccount) {

			_userAccount = userAccount;
		}

		@GraphQLField(description = "Retrieves the user's email addresses.")
		public EmailAddressPage emailAddresses() throws Exception {
			return _applyComponentServiceObjects(
				_emailAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				emailAddressResource -> new EmailAddressPage(
					emailAddressResource.getUserAccountEmailAddressesPage(
						_userAccount.getId())));
		}

		private UserAccount _userAccount;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetOrganizationByExternalReferenceCodeUserAccountsPageTypeExtension {

		public GetOrganizationByExternalReferenceCodeUserAccountsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the organization's members (users). Results can be paginated, filtered, searched, and sorted."
		)
		public UserAccountPage organizationByExternalReferenceCodeUserAccounts(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource -> new UserAccountPage(
					userAccountResource.
						getOrganizationByExternalReferenceCodeUserAccountsPage(
							_account.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								userAccountResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								userAccountResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Organization.class)
	public class GetOrganizationPostalAddressesPageTypeExtension {

		public GetOrganizationPostalAddressesPageTypeExtension(
			Organization organization) {

			_organization = organization;
		}

		@GraphQLField(
			description = "Retrieves the organization's postal addresses."
		)
		public PostalAddressPage postalAddresses() throws Exception {
			return _applyComponentServiceObjects(
				_postalAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				postalAddressResource -> new PostalAddressPage(
					postalAddressResource.getOrganizationPostalAddressesPage(
						_organization.getId())));
		}

		private Organization _organization;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeOrganizationsPageTypeExtension {

		public GetAccountByExternalReferenceCodeOrganizationsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the account's organizations. Results can be paginated, filtered, searched, and sorted."
		)
		public OrganizationPage byExternalReferenceCodeOrganizations(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_organizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				organizationResource -> new OrganizationPage(
					organizationResource.
						getAccountByExternalReferenceCodeOrganizationsPage(
							_account.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								organizationResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								organizationResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(AccountGroup.class)
	public class GetAccountGroupAccountsPageTypeExtension {

		public GetAccountGroupAccountsPageTypeExtension(
			AccountGroup accountGroup) {

			_accountGroup = accountGroup;
		}

		@GraphQLField(
			description = "Retrieves the list of accounts in an account group."
		)
		public AccountPage accounts(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountResource -> new AccountPage(
					accountResource.getAccountGroupAccountsPage(
						_accountGroup.getId(), search,
						_filterBiFunction.apply(accountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(accountResource, sortsString))));
		}

		private AccountGroup _accountGroup;

	}

	@GraphQLTypeExtension(Organization.class)
	public class GetOrganizationOrganizationsPageTypeExtension {

		public GetOrganizationOrganizationsPageTypeExtension(
			Organization organization) {

			_organization = organization;
		}

		@GraphQLField(
			description = "Retrieves the parent organization's child organizations. Results can be paginated, filtered, searched, and sorted."
		)
		public OrganizationPage organizations(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_organizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				organizationResource -> new OrganizationPage(
					organizationResource.getOrganizationOrganizationsPage(
						_organization.getId(), flatten, search,
						_filterBiFunction.apply(
							organizationResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							organizationResource, sortsString))));
		}

		private Organization _organization;

	}

	@GraphQLTypeExtension(Organization.class)
	public class GetOrganizationEmailAddressesPageTypeExtension {

		public GetOrganizationEmailAddressesPageTypeExtension(
			Organization organization) {

			_organization = organization;
		}

		@GraphQLField(
			description = "Retrieves the organization's email addresses."
		)
		public EmailAddressPage emailAddresses() throws Exception {
			return _applyComponentServiceObjects(
				_emailAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				emailAddressResource -> new EmailAddressPage(
					emailAddressResource.getOrganizationEmailAddressesPage(
						_organization.getId())));
		}

		private Organization _organization;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeEmailAddressesPageTypeExtension {

		public GetAccountByExternalReferenceCodeEmailAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the account's email addresses.")
		public EmailAddressPage byExternalReferenceCodeEmailAddresses()
			throws Exception {

			return _applyComponentServiceObjects(
				_emailAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				emailAddressResource -> new EmailAddressPage(
					emailAddressResource.
						getAccountByExternalReferenceCodeEmailAddressesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetUserGroupByExternalReferenceCodeTypeExtension {

		public GetUserGroupByExternalReferenceCodeTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public UserGroup userGroupByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_userGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userGroupResource ->
					userGroupResource.getUserGroupByExternalReferenceCode(
						_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountOrganizationsPageTypeExtension {

		public GetAccountOrganizationsPageTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the account's organizations. Results can be paginated, filtered, searched, and sorted."
		)
		public OrganizationPage organizations(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_organizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				organizationResource -> new OrganizationPage(
					organizationResource.getAccountOrganizationsPage(
						_account.getId(), search,
						_filterBiFunction.apply(
							organizationResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							organizationResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountByExternalReferenceCodeOrganizationTypeExtension {

		public GetAccountByExternalReferenceCodeOrganizationTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public Organization byExternalReferenceCodeOrganization(
				@GraphQLName("organizationId") String organizationId)
			throws Exception {

			return _applyComponentServiceObjects(
				_organizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				organizationResource ->
					organizationResource.
						getAccountByExternalReferenceCodeOrganization(
							_account.getExternalReferenceCode(),
							organizationId));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(AccountRole.class)
	public class GetAccountUserAccountTypeExtension {

		public GetAccountUserAccountTypeExtension(AccountRole accountRole) {
			_accountRole = accountRole;
		}

		@GraphQLField(description = "Gets a user assigned to an account")
		public UserAccount accountUserAccount(
				@GraphQLName("userAccountId") Long userAccountId)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource ->
					userAccountResource.getAccountUserAccount(
						_accountRole.getAccountId(), userAccountId));
		}

		private AccountRole _accountRole;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetUserAccountByExternalReferenceCodePostalAddressesPageTypeExtension {

		public GetUserAccountByExternalReferenceCodePostalAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the user's postal addresses.")
		public PostalAddressPage
				userAccountByExternalReferenceCodePostalAddresses()
			throws Exception {

			return _applyComponentServiceObjects(
				_postalAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				postalAddressResource -> new PostalAddressPage(
					postalAddressResource.
						getUserAccountByExternalReferenceCodePostalAddressesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountGroupByExternalReferenceCodeTypeExtension {

		public GetAccountGroupByExternalReferenceCodeTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountGroup groupByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_accountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountGroupResource ->
					accountGroupResource.getAccountGroupByExternalReferenceCode(
						_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountPhonesPageTypeExtension {

		public GetAccountPhonesPageTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(description = "Retrieves the account's phone numbers.")
		public PhonePage phones() throws Exception {
			return _applyComponentServiceObjects(
				_phoneResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				phoneResource -> new PhonePage(
					phoneResource.getAccountPhonesPage(_account.getId())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Organization.class)
	public class GetOrganizationWebUrlsPageTypeExtension {

		public GetOrganizationWebUrlsPageTypeExtension(
			Organization organization) {

			_organization = organization;
		}

		@GraphQLField(description = "Retrieves the organization's URLs.")
		public WebUrlPage webUrls() throws Exception {
			return _applyComponentServiceObjects(
				_webUrlResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				webUrlResource -> new WebUrlPage(
					webUrlResource.getOrganizationWebUrlsPage(
						_organization.getId())));
		}

		private Organization _organization;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetOrganizationByExternalReferenceCodePostalAddressesPageTypeExtension {

		public GetOrganizationByExternalReferenceCodePostalAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the organization's postal addresses."
		)
		public PostalAddressPage
				organizationByExternalReferenceCodePostalAddresses()
			throws Exception {

			return _applyComponentServiceObjects(
				_postalAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				postalAddressResource -> new PostalAddressPage(
					postalAddressResource.
						getOrganizationByExternalReferenceCodePostalAddressesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetEmailAddressByExternalReferenceCodeTypeExtension {

		public GetEmailAddressByExternalReferenceCodeTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the email address.")
		public EmailAddress emailAddressByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_emailAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				emailAddressResource ->
					emailAddressResource.getEmailAddressByExternalReferenceCode(
						_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Subscription.class)
	public class GetSiteTypeExtension {

		public GetSiteTypeExtension(Subscription subscription) {
			_subscription = subscription;
		}

		@GraphQLField
		public Site site() throws Exception {
			return _applyComponentServiceObjects(
				_siteResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				siteResource -> siteResource.getSite(
					_subscription.getSiteId()));
		}

		private Subscription _subscription;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetOrganizationByExternalReferenceCodePhonesPageTypeExtension {

		public GetOrganizationByExternalReferenceCodePhonesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the organization's phone numbers."
		)
		public PhonePage organizationByExternalReferenceCodePhones()
			throws Exception {

			return _applyComponentServiceObjects(
				_phoneResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				phoneResource -> new PhonePage(
					phoneResource.
						getOrganizationByExternalReferenceCodePhonesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(UserAccount.class)
	public class GetUserAccountPasswordResetTicketTypeExtension {

		public GetUserAccountPasswordResetTicketTypeExtension(
			UserAccount userAccount) {

			_userAccount = userAccount;
		}

		@GraphQLField(
			description = "Retrieves the user's password reset ticket."
		)
		public Ticket passwordResetTicket() throws Exception {
			return _applyComponentServiceObjects(
				_ticketResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				ticketResource ->
					ticketResource.getUserAccountPasswordResetTicket(
						_userAccount.getId()));
		}

		private UserAccount _userAccount;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountEmailAddressesPageTypeExtension {

		public GetAccountEmailAddressesPageTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(description = "Retrieves the account's email addresses.")
		public EmailAddressPage emailAddresses() throws Exception {
			return _applyComponentServiceObjects(
				_emailAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				emailAddressResource -> new EmailAddressPage(
					emailAddressResource.getAccountEmailAddressesPage(
						_account.getId())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Organization.class)
	public class GetOrganizationPhonesPageTypeExtension {

		public GetOrganizationPhonesPageTypeExtension(
			Organization organization) {

			_organization = organization;
		}

		@GraphQLField(
			description = "Retrieves the organization's phone numbers."
		)
		public PhonePage phones() throws Exception {
			return _applyComponentServiceObjects(
				_phoneResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				phoneResource -> new PhonePage(
					phoneResource.getOrganizationPhonesPage(
						_organization.getId())));
		}

		private Organization _organization;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetOrganizationByExternalReferenceCodeChildOrganizationsPageTypeExtension {

		public GetOrganizationByExternalReferenceCodeChildOrganizationsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the parent organization's child organizations. Results can be paginated, filtered, searched, and sorted."
		)
		public OrganizationPage
				organizationByExternalReferenceCodeChildOrganizations(
					@GraphQLName("flatten") Boolean flatten,
					@GraphQLName("search") String search,
					@GraphQLName("filter") String filterString,
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page,
					@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_organizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				organizationResource -> new OrganizationPage(
					organizationResource.
						getOrganizationByExternalReferenceCodeChildOrganizationsPage(
							_account.getExternalReferenceCode(), flatten,
							search,
							_filterBiFunction.apply(
								organizationResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								organizationResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(UserAccount.class)
	public class GetUserAccountPhonesPageTypeExtension {

		public GetUserAccountPhonesPageTypeExtension(UserAccount userAccount) {
			_userAccount = userAccount;
		}

		@GraphQLField(description = "Retrieves the user's phone numbers.")
		public PhonePage phones() throws Exception {
			return _applyComponentServiceObjects(
				_phoneResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				phoneResource -> new PhonePage(
					phoneResource.getUserAccountPhonesPage(
						_userAccount.getId())));
		}

		private UserAccount _userAccount;

	}

	@GraphQLTypeExtension(UserGroup.class)
	public class GetUserGroupUsersPageTypeExtension {

		public GetUserGroupUsersPageTypeExtension(UserGroup userGroup) {
			_userGroup = userGroup;
		}

		@GraphQLField(
			description = "Retrieves the list of users in a user group."
		)
		public UserAccountPage users(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource -> new UserAccountPage(
					userAccountResource.getUserGroupUsersPage(
						_userGroup.getId(), search,
						_filterBiFunction.apply(
							userAccountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							userAccountResource, sortsString))));
		}

		private UserGroup _userGroup;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetUserAccountByExternalReferenceCodeTypeExtension {

		public GetUserAccountByExternalReferenceCodeTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public UserAccount userAccountByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource ->
					userAccountResource.getUserAccountByExternalReferenceCode(
						_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountOrganizationTypeExtension {

		public GetAccountOrganizationTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField
		public Organization organization(
				@GraphQLName("organizationId") String organizationId)
			throws Exception {

			return _applyComponentServiceObjects(
				_organizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				organizationResource ->
					organizationResource.getAccountOrganization(
						_account.getId(), organizationId));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(AccountRole.class)
	public class GetAccountTypeExtension {

		public GetAccountTypeExtension(AccountRole accountRole) {
			_accountRole = accountRole;
		}

		@GraphQLField
		public Account account() throws Exception {
			return _applyComponentServiceObjects(
				_accountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountResource -> accountResource.getAccount(
					_accountRole.getAccountId()));
		}

		private AccountRole _accountRole;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetOrganizationByExternalReferenceCodeEmailAddressesPageTypeExtension {

		public GetOrganizationByExternalReferenceCodeEmailAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the organization's email addresses."
		)
		public EmailAddressPage
				organizationByExternalReferenceCodeEmailAddresses()
			throws Exception {

			return _applyComponentServiceObjects(
				_emailAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				emailAddressResource -> new EmailAddressPage(
					emailAddressResource.
						getOrganizationByExternalReferenceCodeEmailAddressesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetPostalAddressByExternalReferenceCodeTypeExtension {

		public GetPostalAddressByExternalReferenceCodeTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the postal address using external reference code."
		)
		public PostalAddress postalAddressByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_postalAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				postalAddressResource ->
					postalAddressResource.
						getPostalAddressByExternalReferenceCode(
							_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetWebUrlByExternalReferenceCodeTypeExtension {

		public GetWebUrlByExternalReferenceCodeTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the web URL by external reference code."
		)
		public WebUrl webUrlByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_webUrlResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				webUrlResource ->
					webUrlResource.getWebUrlByExternalReferenceCode(
						_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetUserGroupByExternalReferenceCodeUsersPageTypeExtension {

		public GetUserGroupByExternalReferenceCodeUsersPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the list of users in a user group."
		)
		public UserAccountPage userGroupByExternalReferenceCodeUsers(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource -> new UserAccountPage(
					userAccountResource.
						getUserGroupByExternalReferenceCodeUsersPage(
							_account.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								userAccountResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								userAccountResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetRoleByExternalReferenceCodeTypeExtension {

		public GetRoleByExternalReferenceCodeTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the role by its external reference code."
		)
		public Role roleByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_roleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				roleResource -> roleResource.getRoleByExternalReferenceCode(
					_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetPhoneByExternalReferenceCodeTypeExtension {

		public GetPhoneByExternalReferenceCodeTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the phone number by external reference code."
		)
		public Phone phoneByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_phoneResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				phoneResource -> phoneResource.getPhoneByExternalReferenceCode(
					_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Site.class)
	public class GetSiteUserAccountsPageTypeExtension {

		public GetSiteUserAccountsPageTypeExtension(Site site) {
			_site = site;
		}

		@GraphQLField(
			description = "Retrieves the site members' user accounts. Results can be paginated, filtered, searched, and sorted."
		)
		public UserAccountPage userAccounts(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource -> new UserAccountPage(
					userAccountResource.getSiteUserAccountsPage(
						_site.getId(), search,
						_filterBiFunction.apply(
							userAccountResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							userAccountResource, sortsString))));
		}

		private Site _site;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountUserAccountsByExternalReferenceCodePageTypeExtension {

		public GetAccountUserAccountsByExternalReferenceCodePageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Gets the users assigned to an account")
		public UserAccountPage userAccountsByExternalReferenceCode(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource -> new UserAccountPage(
					userAccountResource.
						getAccountUserAccountsByExternalReferenceCodePage(
							_account.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								userAccountResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								userAccountResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(RolePermission.class)
	public class GetRoleTypeExtension {

		public GetRoleTypeExtension(RolePermission rolePermission) {
			_rolePermission = rolePermission;
		}

		@GraphQLField(description = "Retrieves the role.")
		public Role role() throws Exception {
			return _applyComponentServiceObjects(
				_roleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				roleResource -> roleResource.getRole(
					_rolePermission.getRoleId()));
		}

		private RolePermission _rolePermission;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetUserAccountByExternalReferenceCodePhonesPageTypeExtension {

		public GetUserAccountByExternalReferenceCodePhonesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the user's phone numbers.")
		public PhonePage userAccountByExternalReferenceCodePhones()
			throws Exception {

			return _applyComponentServiceObjects(
				_phoneResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				phoneResource -> new PhonePage(
					phoneResource.
						getUserAccountByExternalReferenceCodePhonesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetOrganizationByExternalReferenceCodeAccountsPageTypeExtension {

		public GetOrganizationByExternalReferenceCodeAccountsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Retrieves the organization's members (accounts). Results can be paginated, filtered, searched, and sorted."
		)
		public AccountPage organizationByExternalReferenceCodeAccounts(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountResource -> new AccountPage(
					accountResource.
						getOrganizationByExternalReferenceCodeAccountsPage(
							_account.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								accountResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								accountResource, sortsString))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodePostalAddressesPageTypeExtension {

		public GetAccountByExternalReferenceCodePostalAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the account's postal addresses.")
		public PostalAddressPage byExternalReferenceCodePostalAddresses()
			throws Exception {

			return _applyComponentServiceObjects(
				_postalAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				postalAddressResource -> new PostalAddressPage(
					postalAddressResource.
						getAccountByExternalReferenceCodePostalAddressesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetOrganizationByExternalReferenceCodeWebUrlsPageTypeExtension {

		public GetOrganizationByExternalReferenceCodeWebUrlsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the organization's web URLs.")
		public WebUrlPage organizationByExternalReferenceCodeWebUrls()
			throws Exception {

			return _applyComponentServiceObjects(
				_webUrlResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				webUrlResource -> new WebUrlPage(
					webUrlResource.
						getOrganizationByExternalReferenceCodeWebUrlsPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountWebUrlsPageTypeExtension {

		public GetAccountWebUrlsPageTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField(description = "Retrieves the account's web URLs.")
		public WebUrlPage webUrls() throws Exception {
			return _applyComponentServiceObjects(
				_webUrlResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				webUrlResource -> new WebUrlPage(
					webUrlResource.getAccountWebUrlsPage(_account.getId())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPageTypeExtension {

		public GetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(
			description = "Gets a user's account roles by their email address from an account by external reference code"
		)
		public AccountRolePage
				byExternalReferenceCodeUserAccountByEmailAddressAccountRoles(
					@GraphQLName("emailAddress") String emailAddress)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountRoleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountRoleResource -> new AccountRolePage(
					accountRoleResource.
						getAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage(
							_account.getExternalReferenceCode(),
							emailAddress)));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Site.class)
	public class GetSiteAccountUserAccountSelectedTypeExtension {

		public GetSiteAccountUserAccountSelectedTypeExtension(Site site) {
			_site = site;
		}

		@GraphQLField(
			description = "Gets the selected property of a user assigned to an account for a specific site"
		)
		public Boolean accountUserAccountSelected(
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("userAccountId") Long userAccountId)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource ->
					userAccountResource.getSiteAccountUserAccountSelected(
						_site.getId(), accountId, userAccountId));
		}

		private Site _site;

	}

	@GraphQLTypeExtension(Site.class)
	public class GetSiteSegmentsPageTypeExtension {

		public GetSiteSegmentsPageTypeExtension(Site site) {
			_site = site;
		}

		@GraphQLField(description = "Gets a site's segments.")
		public SegmentPage segments(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_segmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				segmentResource -> new SegmentPage(
					segmentResource.getSiteSegmentsPage(
						_site.getId(), Pagination.of(page, pageSize))));
		}

		private Site _site;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountByExternalReferenceCodeWebUrlsPageTypeExtension {

		public GetAccountByExternalReferenceCodeWebUrlsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the account's web URLs.")
		public WebUrlPage byExternalReferenceCodeWebUrls() throws Exception {
			return _applyComponentServiceObjects(
				_webUrlResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				webUrlResource -> new WebUrlPage(
					webUrlResource.getAccountByExternalReferenceCodeWebUrlsPage(
						_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(UserAccount.class)
	public class GetUserAccountEmailVerificationTicketTypeExtension {

		public GetUserAccountEmailVerificationTicketTypeExtension(
			UserAccount userAccount) {

			_userAccount = userAccount;
		}

		@GraphQLField(
			description = "Retrieves the user's email verification ticket."
		)
		public Ticket emailVerificationTicket() throws Exception {
			return _applyComponentServiceObjects(
				_ticketResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				ticketResource ->
					ticketResource.getUserAccountEmailVerificationTicket(
						_userAccount.getId()));
		}

		private UserAccount _userAccount;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetUserAccountByExternalReferenceCodeEmailAddressesPageTypeExtension {

		public GetUserAccountByExternalReferenceCodeEmailAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the user's email addresses.")
		public EmailAddressPage
				userAccountByExternalReferenceCodeEmailAddresses()
			throws Exception {

			return _applyComponentServiceObjects(
				_emailAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				emailAddressResource -> new EmailAddressPage(
					emailAddressResource.
						getUserAccountByExternalReferenceCodeEmailAddressesPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(UserAccount.class)
	public class GetUserAccountWebUrlsPageTypeExtension {

		public GetUserAccountWebUrlsPageTypeExtension(UserAccount userAccount) {
			_userAccount = userAccount;
		}

		@GraphQLField(description = "Retrieves the user's URLs.")
		public WebUrlPage webUrls() throws Exception {
			return _applyComponentServiceObjects(
				_webUrlResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				webUrlResource -> new WebUrlPage(
					webUrlResource.getUserAccountWebUrlsPage(
						_userAccount.getId())));
		}

		private UserAccount _userAccount;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetUserAccountByExternalReferenceCodeWebUrlsPageTypeExtension {

		public GetUserAccountByExternalReferenceCodeWebUrlsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField(description = "Retrieves the user's web URLs.")
		public WebUrlPage userAccountByExternalReferenceCodeWebUrls()
			throws Exception {

			return _applyComponentServiceObjects(
				_webUrlResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				webUrlResource -> new WebUrlPage(
					webUrlResource.
						getUserAccountByExternalReferenceCodeWebUrlsPage(
							_account.getExternalReferenceCode())));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Site.class)
	public class
		GetSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelectedTypeExtension {

		public GetSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelectedTypeExtension(
			Site site) {

			_site = site;
		}

		@GraphQLField(
			description = "Gets the selected property of a user assigned to an account for a specific site"
		)
		public Boolean
				byFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					@GraphQLName("accountExternalReferenceCode") String
						accountExternalReferenceCode,
					@GraphQLName("userAccountExternalReferenceCode") String
						userAccountExternalReferenceCode)
			throws Exception {

			return _applyComponentServiceObjects(
				_userAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userAccountResource ->
					userAccountResource.
						getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
							_site.getFriendlyUrlPath(),
							accountExternalReferenceCode,
							userAccountExternalReferenceCode));
		}

		private Site _site;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetOrganizationByExternalReferenceCodeTypeExtension {

		public GetOrganizationByExternalReferenceCodeTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public Organization organizationByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_organizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				organizationResource ->
					organizationResource.getOrganizationByExternalReferenceCode(
						_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(UserAccount.class)
	public class GetUserUserGroupsTypeExtension {

		public GetUserUserGroupsTypeExtension(UserAccount userAccount) {
			_userAccount = userAccount;
		}

		@GraphQLField(description = "Retrieves the user's user groups.")
		public UserGroupPage userUserGroups() throws Exception {
			return _applyComponentServiceObjects(
				_userGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				userGroupResource -> new UserGroupPage(
					userGroupResource.getUserUserGroups(_userAccount.getId())));
		}

		private UserAccount _userAccount;

	}

	@GraphQLName("AccountPage")
	public class AccountPage {

		public AccountPage(Page accountPage) {
			actions = accountPage.getActions();

			facets = accountPage.getFacets();

			items = accountPage.getItems();
			lastPage = accountPage.getLastPage();
			page = accountPage.getPage();
			pageSize = accountPage.getPageSize();
			totalCount = accountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Account> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AccountGroupPage")
	public class AccountGroupPage {

		public AccountGroupPage(Page accountGroupPage) {
			actions = accountGroupPage.getActions();

			facets = accountGroupPage.getFacets();

			items = accountGroupPage.getItems();
			lastPage = accountGroupPage.getLastPage();
			page = accountGroupPage.getPage();
			pageSize = accountGroupPage.getPageSize();
			totalCount = accountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<AccountGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AccountRolePage")
	public class AccountRolePage {

		public AccountRolePage(Page accountRolePage) {
			actions = accountRolePage.getActions();

			facets = accountRolePage.getFacets();

			items = accountRolePage.getItems();
			lastPage = accountRolePage.getLastPage();
			page = accountRolePage.getPage();
			pageSize = accountRolePage.getPageSize();
			totalCount = accountRolePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<AccountRole> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("EmailAddressPage")
	public class EmailAddressPage {

		public EmailAddressPage(Page emailAddressPage) {
			actions = emailAddressPage.getActions();

			facets = emailAddressPage.getFacets();

			items = emailAddressPage.getItems();
			lastPage = emailAddressPage.getLastPage();
			page = emailAddressPage.getPage();
			pageSize = emailAddressPage.getPageSize();
			totalCount = emailAddressPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<EmailAddress> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("OrganizationPage")
	public class OrganizationPage {

		public OrganizationPage(Page organizationPage) {
			actions = organizationPage.getActions();

			facets = organizationPage.getFacets();

			items = organizationPage.getItems();
			lastPage = organizationPage.getLastPage();
			page = organizationPage.getPage();
			pageSize = organizationPage.getPageSize();
			totalCount = organizationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Organization> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PhonePage")
	public class PhonePage {

		public PhonePage(Page phonePage) {
			actions = phonePage.getActions();

			facets = phonePage.getFacets();

			items = phonePage.getItems();
			lastPage = phonePage.getLastPage();
			page = phonePage.getPage();
			pageSize = phonePage.getPageSize();
			totalCount = phonePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Phone> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PostalAddressPage")
	public class PostalAddressPage {

		public PostalAddressPage(Page postalAddressPage) {
			actions = postalAddressPage.getActions();

			facets = postalAddressPage.getFacets();

			items = postalAddressPage.getItems();
			lastPage = postalAddressPage.getLastPage();
			page = postalAddressPage.getPage();
			pageSize = postalAddressPage.getPageSize();
			totalCount = postalAddressPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<PostalAddress> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("RolePage")
	public class RolePage {

		public RolePage(Page rolePage) {
			actions = rolePage.getActions();

			facets = rolePage.getFacets();

			items = rolePage.getItems();
			lastPage = rolePage.getLastPage();
			page = rolePage.getPage();
			pageSize = rolePage.getPageSize();
			totalCount = rolePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Role> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SegmentPage")
	public class SegmentPage {

		public SegmentPage(Page segmentPage) {
			actions = segmentPage.getActions();

			facets = segmentPage.getFacets();

			items = segmentPage.getItems();
			lastPage = segmentPage.getLastPage();
			page = segmentPage.getPage();
			pageSize = segmentPage.getPageSize();
			totalCount = segmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Segment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SegmentUserPage")
	public class SegmentUserPage {

		public SegmentUserPage(Page segmentUserPage) {
			actions = segmentUserPage.getActions();

			facets = segmentUserPage.getFacets();

			items = segmentUserPage.getItems();
			lastPage = segmentUserPage.getLastPage();
			page = segmentUserPage.getPage();
			pageSize = segmentUserPage.getPageSize();
			totalCount = segmentUserPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<SegmentUser> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SharedAssetPage")
	public class SharedAssetPage {

		public SharedAssetPage(Page sharedAssetPage) {
			actions = sharedAssetPage.getActions();

			facets = sharedAssetPage.getFacets();

			items = sharedAssetPage.getItems();
			lastPage = sharedAssetPage.getLastPage();
			page = sharedAssetPage.getPage();
			pageSize = sharedAssetPage.getPageSize();
			totalCount = sharedAssetPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<SharedAsset> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SitePage")
	public class SitePage {

		public SitePage(Page sitePage) {
			actions = sitePage.getActions();

			facets = sitePage.getFacets();

			items = sitePage.getItems();
			lastPage = sitePage.getLastPage();
			page = sitePage.getPage();
			pageSize = sitePage.getPageSize();
			totalCount = sitePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Site> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SubscriptionPage")
	public class SubscriptionPage {

		public SubscriptionPage(Page subscriptionPage) {
			actions = subscriptionPage.getActions();

			facets = subscriptionPage.getFacets();

			items = subscriptionPage.getItems();
			lastPage = subscriptionPage.getLastPage();
			page = subscriptionPage.getPage();
			pageSize = subscriptionPage.getPageSize();
			totalCount = subscriptionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Subscription> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TicketPage")
	public class TicketPage {

		public TicketPage(Page ticketPage) {
			actions = ticketPage.getActions();

			facets = ticketPage.getFacets();

			items = ticketPage.getItems();
			lastPage = ticketPage.getLastPage();
			page = ticketPage.getPage();
			pageSize = ticketPage.getPageSize();
			totalCount = ticketPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Ticket> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("UserAccountPage")
	public class UserAccountPage {

		public UserAccountPage(Page userAccountPage) {
			actions = userAccountPage.getActions();

			facets = userAccountPage.getFacets();

			items = userAccountPage.getItems();
			lastPage = userAccountPage.getLastPage();
			page = userAccountPage.getPage();
			pageSize = userAccountPage.getPageSize();
			totalCount = userAccountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<UserAccount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("UserAccountFullNameDefinitionPage")
	public class UserAccountFullNameDefinitionPage {

		public UserAccountFullNameDefinitionPage(
			Page userAccountFullNameDefinitionPage) {

			actions = userAccountFullNameDefinitionPage.getActions();

			facets = userAccountFullNameDefinitionPage.getFacets();

			items = userAccountFullNameDefinitionPage.getItems();
			lastPage = userAccountFullNameDefinitionPage.getLastPage();
			page = userAccountFullNameDefinitionPage.getPage();
			pageSize = userAccountFullNameDefinitionPage.getPageSize();
			totalCount = userAccountFullNameDefinitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<UserAccountFullNameDefinition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("UserGroupPage")
	public class UserGroupPage {

		public UserGroupPage(Page userGroupPage) {
			actions = userGroupPage.getActions();

			facets = userGroupPage.getFacets();

			items = userGroupPage.getItems();
			lastPage = userGroupPage.getLastPage();
			page = userGroupPage.getPage();
			pageSize = userGroupPage.getPageSize();
			totalCount = userGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<UserGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WebUrlPage")
	public class WebUrlPage {

		public WebUrlPage(Page webUrlPage) {
			actions = webUrlPage.getActions();

			facets = webUrlPage.getFacets();

			items = webUrlPage.getItems();
			lastPage = webUrlPage.getLastPage();
			page = webUrlPage.getPage();
			pageSize = webUrlPage.getPageSize();
			totalCount = webUrlPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<WebUrl> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLTypeExtension(Account.class)
	public class ParentAccountAccountIdTypeExtension {

		public ParentAccountAccountIdTypeExtension(Account account) {
			_account = account;
		}

		@GraphQLField
		public Account parentAccount() throws Exception {
			if (_account.getParentAccountId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_accountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountResource -> accountResource.getAccount(
					_account.getParentAccountId()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Site.class)
	public class ParentSiteSiteIdTypeExtension {

		public ParentSiteSiteIdTypeExtension(Site site) {
			_site = site;
		}

		@GraphQLField
		public Site parentSite() throws Exception {
			if (_site.getParentSiteId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_siteResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				siteResource -> siteResource.getSite(_site.getParentSiteId()));
		}

		private Site _site;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(AccountResource accountResource)
		throws Exception {

		accountResource.setContextAcceptLanguage(_acceptLanguage);
		accountResource.setContextCompany(_company);
		accountResource.setContextHttpServletRequest(_httpServletRequest);
		accountResource.setContextHttpServletResponse(_httpServletResponse);
		accountResource.setContextUriInfo(_uriInfo);
		accountResource.setContextUser(_user);
		accountResource.setGroupLocalService(_groupLocalService);
		accountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			AccountGroupResource accountGroupResource)
		throws Exception {

		accountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		accountGroupResource.setContextCompany(_company);
		accountGroupResource.setContextHttpServletRequest(_httpServletRequest);
		accountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountGroupResource.setContextUriInfo(_uriInfo);
		accountGroupResource.setContextUser(_user);
		accountGroupResource.setGroupLocalService(_groupLocalService);
		accountGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			AccountRoleResource accountRoleResource)
		throws Exception {

		accountRoleResource.setContextAcceptLanguage(_acceptLanguage);
		accountRoleResource.setContextCompany(_company);
		accountRoleResource.setContextHttpServletRequest(_httpServletRequest);
		accountRoleResource.setContextHttpServletResponse(_httpServletResponse);
		accountRoleResource.setContextUriInfo(_uriInfo);
		accountRoleResource.setContextUser(_user);
		accountRoleResource.setGroupLocalService(_groupLocalService);
		accountRoleResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountRoleResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountRoleResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			EmailAddressResource emailAddressResource)
		throws Exception {

		emailAddressResource.setContextAcceptLanguage(_acceptLanguage);
		emailAddressResource.setContextCompany(_company);
		emailAddressResource.setContextHttpServletRequest(_httpServletRequest);
		emailAddressResource.setContextHttpServletResponse(
			_httpServletResponse);
		emailAddressResource.setContextUriInfo(_uriInfo);
		emailAddressResource.setContextUser(_user);
		emailAddressResource.setGroupLocalService(_groupLocalService);
		emailAddressResource.setResourceActionLocalService(
			_resourceActionLocalService);
		emailAddressResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		emailAddressResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			OrganizationResource organizationResource)
		throws Exception {

		organizationResource.setContextAcceptLanguage(_acceptLanguage);
		organizationResource.setContextCompany(_company);
		organizationResource.setContextHttpServletRequest(_httpServletRequest);
		organizationResource.setContextHttpServletResponse(
			_httpServletResponse);
		organizationResource.setContextUriInfo(_uriInfo);
		organizationResource.setContextUser(_user);
		organizationResource.setGroupLocalService(_groupLocalService);
		organizationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		organizationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		organizationResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(PhoneResource phoneResource)
		throws Exception {

		phoneResource.setContextAcceptLanguage(_acceptLanguage);
		phoneResource.setContextCompany(_company);
		phoneResource.setContextHttpServletRequest(_httpServletRequest);
		phoneResource.setContextHttpServletResponse(_httpServletResponse);
		phoneResource.setContextUriInfo(_uriInfo);
		phoneResource.setContextUser(_user);
		phoneResource.setGroupLocalService(_groupLocalService);
		phoneResource.setResourceActionLocalService(
			_resourceActionLocalService);
		phoneResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		phoneResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PostalAddressResource postalAddressResource)
		throws Exception {

		postalAddressResource.setContextAcceptLanguage(_acceptLanguage);
		postalAddressResource.setContextCompany(_company);
		postalAddressResource.setContextHttpServletRequest(_httpServletRequest);
		postalAddressResource.setContextHttpServletResponse(
			_httpServletResponse);
		postalAddressResource.setContextUriInfo(_uriInfo);
		postalAddressResource.setContextUser(_user);
		postalAddressResource.setGroupLocalService(_groupLocalService);
		postalAddressResource.setResourceActionLocalService(
			_resourceActionLocalService);
		postalAddressResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		postalAddressResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(RoleResource roleResource)
		throws Exception {

		roleResource.setContextAcceptLanguage(_acceptLanguage);
		roleResource.setContextCompany(_company);
		roleResource.setContextHttpServletRequest(_httpServletRequest);
		roleResource.setContextHttpServletResponse(_httpServletResponse);
		roleResource.setContextUriInfo(_uriInfo);
		roleResource.setContextUser(_user);
		roleResource.setGroupLocalService(_groupLocalService);
		roleResource.setResourceActionLocalService(_resourceActionLocalService);
		roleResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		roleResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SegmentResource segmentResource)
		throws Exception {

		segmentResource.setContextAcceptLanguage(_acceptLanguage);
		segmentResource.setContextCompany(_company);
		segmentResource.setContextHttpServletRequest(_httpServletRequest);
		segmentResource.setContextHttpServletResponse(_httpServletResponse);
		segmentResource.setContextUriInfo(_uriInfo);
		segmentResource.setContextUser(_user);
		segmentResource.setGroupLocalService(_groupLocalService);
		segmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		segmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		segmentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SegmentUserResource segmentUserResource)
		throws Exception {

		segmentUserResource.setContextAcceptLanguage(_acceptLanguage);
		segmentUserResource.setContextCompany(_company);
		segmentUserResource.setContextHttpServletRequest(_httpServletRequest);
		segmentUserResource.setContextHttpServletResponse(_httpServletResponse);
		segmentUserResource.setContextUriInfo(_uriInfo);
		segmentUserResource.setContextUser(_user);
		segmentUserResource.setGroupLocalService(_groupLocalService);
		segmentUserResource.setResourceActionLocalService(
			_resourceActionLocalService);
		segmentUserResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		segmentUserResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SharedAssetResource sharedAssetResource)
		throws Exception {

		sharedAssetResource.setContextAcceptLanguage(_acceptLanguage);
		sharedAssetResource.setContextCompany(_company);
		sharedAssetResource.setContextHttpServletRequest(_httpServletRequest);
		sharedAssetResource.setContextHttpServletResponse(_httpServletResponse);
		sharedAssetResource.setContextUriInfo(_uriInfo);
		sharedAssetResource.setContextUser(_user);
		sharedAssetResource.setGroupLocalService(_groupLocalService);
		sharedAssetResource.setResourceActionLocalService(
			_resourceActionLocalService);
		sharedAssetResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		sharedAssetResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SiteResource siteResource)
		throws Exception {

		siteResource.setContextAcceptLanguage(_acceptLanguage);
		siteResource.setContextCompany(_company);
		siteResource.setContextHttpServletRequest(_httpServletRequest);
		siteResource.setContextHttpServletResponse(_httpServletResponse);
		siteResource.setContextUriInfo(_uriInfo);
		siteResource.setContextUser(_user);
		siteResource.setGroupLocalService(_groupLocalService);
		siteResource.setResourceActionLocalService(_resourceActionLocalService);
		siteResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		siteResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SubscriptionResource subscriptionResource)
		throws Exception {

		subscriptionResource.setContextAcceptLanguage(_acceptLanguage);
		subscriptionResource.setContextCompany(_company);
		subscriptionResource.setContextHttpServletRequest(_httpServletRequest);
		subscriptionResource.setContextHttpServletResponse(
			_httpServletResponse);
		subscriptionResource.setContextUriInfo(_uriInfo);
		subscriptionResource.setContextUser(_user);
		subscriptionResource.setGroupLocalService(_groupLocalService);
		subscriptionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		subscriptionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		subscriptionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(TicketResource ticketResource)
		throws Exception {

		ticketResource.setContextAcceptLanguage(_acceptLanguage);
		ticketResource.setContextCompany(_company);
		ticketResource.setContextHttpServletRequest(_httpServletRequest);
		ticketResource.setContextHttpServletResponse(_httpServletResponse);
		ticketResource.setContextUriInfo(_uriInfo);
		ticketResource.setContextUser(_user);
		ticketResource.setGroupLocalService(_groupLocalService);
		ticketResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ticketResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ticketResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			UserAccountResource userAccountResource)
		throws Exception {

		userAccountResource.setContextAcceptLanguage(_acceptLanguage);
		userAccountResource.setContextCompany(_company);
		userAccountResource.setContextHttpServletRequest(_httpServletRequest);
		userAccountResource.setContextHttpServletResponse(_httpServletResponse);
		userAccountResource.setContextUriInfo(_uriInfo);
		userAccountResource.setContextUser(_user);
		userAccountResource.setGroupLocalService(_groupLocalService);
		userAccountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		userAccountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		userAccountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			UserAccountFullNameDefinitionResource
				userAccountFullNameDefinitionResource)
		throws Exception {

		userAccountFullNameDefinitionResource.setContextAcceptLanguage(
			_acceptLanguage);
		userAccountFullNameDefinitionResource.setContextCompany(_company);
		userAccountFullNameDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		userAccountFullNameDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		userAccountFullNameDefinitionResource.setContextUriInfo(_uriInfo);
		userAccountFullNameDefinitionResource.setContextUser(_user);
		userAccountFullNameDefinitionResource.setGroupLocalService(
			_groupLocalService);
		userAccountFullNameDefinitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		userAccountFullNameDefinitionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		userAccountFullNameDefinitionResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(UserGroupResource userGroupResource)
		throws Exception {

		userGroupResource.setContextAcceptLanguage(_acceptLanguage);
		userGroupResource.setContextCompany(_company);
		userGroupResource.setContextHttpServletRequest(_httpServletRequest);
		userGroupResource.setContextHttpServletResponse(_httpServletResponse);
		userGroupResource.setContextUriInfo(_uriInfo);
		userGroupResource.setContextUser(_user);
		userGroupResource.setGroupLocalService(_groupLocalService);
		userGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		userGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		userGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(WebUrlResource webUrlResource)
		throws Exception {

		webUrlResource.setContextAcceptLanguage(_acceptLanguage);
		webUrlResource.setContextCompany(_company);
		webUrlResource.setContextHttpServletRequest(_httpServletRequest);
		webUrlResource.setContextHttpServletResponse(_httpServletResponse);
		webUrlResource.setContextUriInfo(_uriInfo);
		webUrlResource.setContextUser(_user);
		webUrlResource.setGroupLocalService(_groupLocalService);
		webUrlResource.setResourceActionLocalService(
			_resourceActionLocalService);
		webUrlResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		webUrlResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountGroupResource>
		_accountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountRoleResource>
		_accountRoleResourceComponentServiceObjects;
	private static ComponentServiceObjects<EmailAddressResource>
		_emailAddressResourceComponentServiceObjects;
	private static ComponentServiceObjects<OrganizationResource>
		_organizationResourceComponentServiceObjects;
	private static ComponentServiceObjects<PhoneResource>
		_phoneResourceComponentServiceObjects;
	private static ComponentServiceObjects<PostalAddressResource>
		_postalAddressResourceComponentServiceObjects;
	private static ComponentServiceObjects<RoleResource>
		_roleResourceComponentServiceObjects;
	private static ComponentServiceObjects<SegmentResource>
		_segmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<SegmentUserResource>
		_segmentUserResourceComponentServiceObjects;
	private static ComponentServiceObjects<SharedAssetResource>
		_sharedAssetResourceComponentServiceObjects;
	private static ComponentServiceObjects<SiteResource>
		_siteResourceComponentServiceObjects;
	private static ComponentServiceObjects<SubscriptionResource>
		_subscriptionResourceComponentServiceObjects;
	private static ComponentServiceObjects<TicketResource>
		_ticketResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserAccountResource>
		_userAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<UserAccountFullNameDefinitionResource>
			_userAccountFullNameDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserGroupResource>
		_userGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<WebUrlResource>
		_webUrlResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}