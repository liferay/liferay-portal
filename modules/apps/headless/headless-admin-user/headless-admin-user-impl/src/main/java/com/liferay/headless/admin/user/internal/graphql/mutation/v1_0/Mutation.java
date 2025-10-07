/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountGroup;
import com.liferay.headless.admin.user.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.headless.admin.user.dto.v1_0.Phone;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.Role;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
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
import com.liferay.headless.admin.user.resource.v1_0.SubscriptionResource;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.user.resource.v1_0.UserGroupResource;
import com.liferay.headless.admin.user.resource.v1_0.WebUrlResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Mutation {

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

	public static void setSubscriptionResourceComponentServiceObjects(
		ComponentServiceObjects<SubscriptionResource>
			subscriptionResourceComponentServiceObjects) {

		_subscriptionResourceComponentServiceObjects =
			subscriptionResourceComponentServiceObjects;
	}

	public static void setUserAccountResourceComponentServiceObjects(
		ComponentServiceObjects<UserAccountResource>
			userAccountResourceComponentServiceObjects) {

		_userAccountResourceComponentServiceObjects =
			userAccountResourceComponentServiceObjects;
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

	@GraphQLField(description = "Deletes an account.")
	public boolean deleteAccount(@GraphQLName("accountId") Long accountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.deleteAccount(accountId));

		return true;
	}

	@GraphQLField
	public Response deleteAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.deleteAccountBatch(
				callbackURL, object));
	}

	@GraphQLField(description = "Deletes an account.")
	public boolean deleteAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.deleteAccountByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public boolean deleteOrganizationAccounts(
			@GraphQLName("organizationId") Long organizationId,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.deleteOrganizationAccounts(
				organizationId, longs));

		return true;
	}

	@GraphQLField
	public boolean deleteOrganizationAccountsByExternalReferenceCode(
			@GraphQLName("organizationId") Long organizationId,
			@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.
					deleteOrganizationAccountsByExternalReferenceCode(
						organizationId, strings));

		return true;
	}

	@GraphQLField
	public boolean deleteOrganizationByExternalReferenceCodeAccounts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.
					deleteOrganizationByExternalReferenceCodeAccounts(
						externalReferenceCode, longs));

		return true;
	}

	@GraphQLField
	public boolean
			deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
				@GraphQLName("organizationExternalReferenceCode") String
					organizationExternalReferenceCode,
				@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.
					deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
						organizationExternalReferenceCode, strings));

		return true;
	}

	@GraphQLField(
		description = "Updates the account with information sent in the request body. Only the provided fields are updated."
	)
	public Account patchAccount(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("account") Account account)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.patchAccount(
				accountId, account));
	}

	@GraphQLField(
		description = "Updates the account with information sent in the request body. Only the provided fields are updated."
	)
	public Account patchAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("account") Account account)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.patchAccountByExternalReferenceCode(
					externalReferenceCode, account));
	}

	@GraphQLField
	public boolean patchOrganizationMoveAccounts(
			@GraphQLName("sourceOrganizationId") Long sourceOrganizationId,
			@GraphQLName("targetOrganizationId") Long targetOrganizationId,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.patchOrganizationMoveAccounts(
				sourceOrganizationId, targetOrganizationId, longs));

		return true;
	}

	@GraphQLField
	public boolean patchOrganizationMoveAccountsByExternalReferenceCode(
			@GraphQLName("sourceOrganizationId") Long sourceOrganizationId,
			@GraphQLName("targetOrganizationId") Long targetOrganizationId,
			@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.
					patchOrganizationMoveAccountsByExternalReferenceCode(
						sourceOrganizationId, targetOrganizationId, strings));

		return true;
	}

	@GraphQLField(description = "Creates a new account")
	public Account createAccount(@GraphQLName("account") Account account)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.postAccount(account));
	}

	@GraphQLField
	public Response createAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.postAccountBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createAccountGroupAccountsPageExportBatch(
			@GraphQLName("accountGroupId") Long accountGroupId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.postAccountGroupAccountsPageExportBatch(
					accountGroupId, search,
					_filterBiFunction.apply(accountResource, filterString),
					_sortsBiFunction.apply(accountResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createAccountsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.postAccountsPageExportBatch(
				search, _filterBiFunction.apply(accountResource, filterString),
				_sortsBiFunction.apply(accountResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean createOrganizationAccounts(
			@GraphQLName("organizationId") Long organizationId,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.postOrganizationAccounts(
				organizationId, longs));

		return true;
	}

	@GraphQLField
	public boolean createOrganizationAccountsByExternalReferenceCode(
			@GraphQLName("organizationId") Long organizationId,
			@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.postOrganizationAccountsByExternalReferenceCode(
					organizationId, strings));

		return true;
	}

	@GraphQLField
	public Response createOrganizationAccountsPageExportBatch(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.postOrganizationAccountsPageExportBatch(
					organizationId, search,
					_filterBiFunction.apply(accountResource, filterString),
					_sortsBiFunction.apply(accountResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean createOrganizationByExternalReferenceCodeAccounts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.postOrganizationByExternalReferenceCodeAccounts(
					externalReferenceCode, longs));

		return true;
	}

	@GraphQLField
	public boolean
			createOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
				@GraphQLName("organizationExternalReferenceCode") String
					organizationExternalReferenceCode,
				@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.
					postOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
						organizationExternalReferenceCode, strings));

		return true;
	}

	@GraphQLField(
		description = "Replaces the account with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public Account updateAccount(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("account") Account account)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.putAccount(accountId, account));
	}

	@GraphQLField
	public Response updateAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.putAccountBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the account with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public Account updateAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("account") Account account)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.putAccountByExternalReferenceCode(
					externalReferenceCode, account));
	}

	@GraphQLField(description = "Deletes an account group.")
	public boolean deleteAccountGroup(
			@GraphQLName("accountGroupId") Long accountGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> accountGroupResource.deleteAccountGroup(
				accountGroupId));

		return true;
	}

	@GraphQLField
	public Response deleteAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.deleteAccountGroupBatch(
					callbackURL, object));
	}

	@GraphQLField(description = "Deletes an account group.")
	public boolean deleteAccountGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.deleteAccountGroupByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Removes an account by their external reference code from an account group by external reference code"
	)
	public boolean
			deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.
					deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode(
						accountExternalReferenceCode, externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the account group with information sent in the request body. Only the provided fields are updated."
	)
	public AccountGroup patchAccountGroup(
			@GraphQLName("accountGroupId") Long accountGroupId,
			@GraphQLName("accountGroup") AccountGroup accountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> accountGroupResource.patchAccountGroup(
				accountGroupId, accountGroup));
	}

	@GraphQLField(
		description = "Updates the account with information sent in the request body. Only the provided fields are updated."
	)
	public AccountGroup patchAccountGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accountGroup") AccountGroup accountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.patchAccountGroupByExternalReferenceCode(
					externalReferenceCode, accountGroup));
	}

	@GraphQLField
	public Response createAccountAccountGroupsPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.postAccountAccountGroupsPageExportBatch(
					accountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new account group")
	public AccountGroup createAccountGroup(
			@GraphQLName("accountGroup") AccountGroup accountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> accountGroupResource.postAccountGroup(
				accountGroup));
	}

	@GraphQLField
	public Response createAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> accountGroupResource.postAccountGroupBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Assigns an account by its external reference code to an account group by external reference code"
	)
	public boolean
			createAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.
					postAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode(
						accountExternalReferenceCode, externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response createAccountGroupsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.postAccountGroupsPageExportBatch(
					search,
					_filterBiFunction.apply(accountGroupResource, filterString),
					_sortsBiFunction.apply(accountGroupResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Replaces the account group with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public AccountGroup updateAccountGroup(
			@GraphQLName("accountGroupId") Long accountGroupId,
			@GraphQLName("accountGroup") AccountGroup accountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> accountGroupResource.putAccountGroup(
				accountGroupId, accountGroup));
	}

	@GraphQLField
	public Response updateAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource -> accountGroupResource.putAccountGroupBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the account group with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public AccountGroup updateAccountGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accountGroup") AccountGroup accountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountGroupResource ->
				accountGroupResource.putAccountGroupByExternalReferenceCode(
					externalReferenceCode, accountGroup));
	}

	@GraphQLField(description = "Unassigns account users to the account role")
	public boolean deleteAccountAccountRoleUserAccountAssociation(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("accountRoleId") Long accountRoleId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					deleteAccountAccountRoleUserAccountAssociation(
						accountId, accountRoleId, userAccountId));

		return true;
	}

	@GraphQLField(
		description = "Unassigns account users by email address from the account role"
	)
	public boolean
			deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountRoleExternalReferenceCode") String
					accountRoleExternalReferenceCode,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress(
						externalReferenceCode, accountRoleExternalReferenceCode,
						emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Unassigns account users by external reference code from the account role"
	)
	public boolean
			deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("accountRoleExternalReferenceCode") String
					accountRoleExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode(
						accountExternalReferenceCode,
						accountRoleExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Unassigns account users by email address from the account role"
	)
	public boolean
			deleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountRoleId") Long accountRoleId,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					deleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
						externalReferenceCode, accountRoleId, emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Unassigns account users by external reference code from the account role"
	)
	public boolean
			deleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("accountRoleId") Long accountRoleId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					deleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
						accountExternalReferenceCode, accountRoleId,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(description = "Adds a role for the account")
	public AccountRole createAccountAccountRole(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("accountRole") AccountRole accountRole)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource -> accountRoleResource.postAccountAccountRole(
				accountId, accountRole));
	}

	@GraphQLField
	public Response createAccountAccountRoleBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.postAccountAccountRoleBatch(
					accountId, callbackURL, object));
	}

	@GraphQLField(description = "Adds a role for the account")
	public AccountRole createAccountAccountRoleByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accountRole") AccountRole accountRole)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					postAccountAccountRoleByExternalReferenceCode(
						externalReferenceCode, accountRole));
	}

	@GraphQLField(description = "Assigns account users to the account role")
	public boolean createAccountAccountRoleUserAccountAssociation(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("accountRoleId") Long accountRoleId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					postAccountAccountRoleUserAccountAssociation(
						accountId, accountRoleId, userAccountId));

		return true;
	}

	@GraphQLField
	public Response createAccountAccountRolesPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("keywords") String keywords,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.postAccountAccountRolesPageExportBatch(
					accountId, keywords,
					_filterBiFunction.apply(accountRoleResource, filterString),
					_sortsBiFunction.apply(accountRoleResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Assigns account users by email address to the account role"
	)
	public boolean
			createAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountRoleExternalReferenceCode") String
					accountRoleExternalReferenceCode,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress(
						externalReferenceCode, accountRoleExternalReferenceCode,
						emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Assigns account users by external reference code to the account role"
	)
	public boolean
			createAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("accountRoleExternalReferenceCode") String
					accountRoleExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode(
						accountExternalReferenceCode,
						accountRoleExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Assigns account users by email address to the account role"
	)
	public boolean
			createAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountRoleId") Long accountRoleId,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
						externalReferenceCode, accountRoleId, emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Assigns account users by external reference code to the account role"
	)
	public boolean
			createAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("accountRoleId") Long accountRoleId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountRoleResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountRoleResource ->
				accountRoleResource.
					postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
						accountExternalReferenceCode, accountRoleId,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(description = "Deletes an email address.")
	public boolean deleteEmailAddress(
			@GraphQLName("emailAddressId") Long emailAddressId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> emailAddressResource.deleteEmailAddress(
				emailAddressId));

		return true;
	}

	@GraphQLField
	public Response deleteEmailAddressBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource ->
				emailAddressResource.deleteEmailAddressBatch(
					callbackURL, object));
	}

	@GraphQLField(description = "Deletes an email address.")
	public boolean deleteEmailAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource ->
				emailAddressResource.deleteEmailAddressByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the email address with the information sent in the request body. Fields not present in the request body are left unchanged."
	)
	public EmailAddress patchEmailAddress(
			@GraphQLName("emailAddressId") Long emailAddressId,
			@GraphQLName("emailAddress") EmailAddress emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource -> emailAddressResource.patchEmailAddress(
				emailAddressId, emailAddress));
	}

	@GraphQLField(
		description = "Updates the email address with the information sent in the request body. Fields not present in the request body are left unchanged."
	)
	public EmailAddress patchEmailAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("emailAddress") EmailAddress emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource ->
				emailAddressResource.patchEmailAddressByExternalReferenceCode(
					externalReferenceCode, emailAddress));
	}

	@GraphQLField
	public Response createAccountEmailAddressesPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource ->
				emailAddressResource.postAccountEmailAddressesPageExportBatch(
					accountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createOrganizationEmailAddressesPageExportBatch(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource ->
				emailAddressResource.
					postOrganizationEmailAddressesPageExportBatch(
						organizationId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createUserAccountEmailAddressesPageExportBatch(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_emailAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			emailAddressResource ->
				emailAddressResource.
					postUserAccountEmailAddressesPageExportBatch(
						userAccountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean deleteAccountByExternalReferenceCodeOrganization(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.
					deleteAccountByExternalReferenceCodeOrganization(
						externalReferenceCode, organizationId));

		return true;
	}

	@GraphQLField
	public boolean deleteAccountOrganization(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.deleteAccountOrganization(
					accountId, organizationId));

		return true;
	}

	@GraphQLField(description = "Deletes an organization.")
	public boolean deleteOrganization(
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.deleteOrganization(
				organizationId));

		return true;
	}

	@GraphQLField
	public Response deleteOrganizationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.deleteOrganizationBatch(
					callbackURL, object));
	}

	@GraphQLField(description = "Deletes an organization.")
	public boolean deleteOrganizationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.deleteOrganizationByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Removes a user from an organization by their email address"
	)
	public boolean
			deleteOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.
					deleteOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
						externalReferenceCode, emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Removes users from an organization by their email addresses"
	)
	public boolean
			deleteOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.
					deleteOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
						externalReferenceCode, strings));

		return true;
	}

	@GraphQLField(
		description = "Removes a user from an organization by their email address"
	)
	public boolean deleteUserAccountByEmailAddress(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.deleteUserAccountByEmailAddress(
					organizationId, emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Removes users from an organization by their email addresses"
	)
	public boolean deleteUserAccountsByEmailAddress(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.deleteUserAccountsByEmailAddress(
					organizationId, strings));

		return true;
	}

	@GraphQLField(
		description = "Updates the organization with the information sent in the request body. Fields not present in the request body are left unchanged."
	)
	public Organization patchOrganization(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("organization") Organization organization)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.patchOrganization(
				organizationId, organization));
	}

	@GraphQLField(
		description = "Updates the organization with information sent in the request body. Only the provided fields are updated."
	)
	public Organization patchOrganizationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("organization") Organization organization)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.patchOrganizationByExternalReferenceCode(
					externalReferenceCode, organization));
	}

	@GraphQLField
	public boolean createAccountByExternalReferenceCodeOrganization(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.
					postAccountByExternalReferenceCodeOrganization(
						externalReferenceCode, organizationId));

		return true;
	}

	@GraphQLField
	public boolean createAccountOrganization(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("organizationId") String organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.postAccountOrganization(
					accountId, organizationId));

		return true;
	}

	@GraphQLField
	public Response createAccountOrganizationsPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.postAccountOrganizationsPageExportBatch(
					accountId, search,
					_filterBiFunction.apply(organizationResource, filterString),
					_sortsBiFunction.apply(organizationResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new organization")
	public Organization createOrganization(
			@GraphQLName("organization") Organization organization)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.postOrganization(
				organization));
	}

	@GraphQLField
	public Response createOrganizationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.postOrganizationBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Assigns a user to an organization by their email address"
	)
	public UserAccount
			createOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.
					postOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
						externalReferenceCode, emailAddress));
	}

	@GraphQLField(
		description = "Assigns users to an organization by their email addresses"
	)
	public java.util.Collection<UserAccount>
			createOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("organizationRoleIds") String organizationRoleIds,
				@GraphQLName("strings") String[] strings)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> {
				Page paginationPage =
					organizationResource.
						postOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
							externalReferenceCode, organizationRoleIds,
							strings);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createOrganizationsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.postOrganizationsPageExportBatch(
					search,
					_filterBiFunction.apply(organizationResource, filterString),
					_sortsBiFunction.apply(organizationResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Assigns a user to an organization by their email address"
	)
	public UserAccount createUserAccountByEmailAddress(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.postUserAccountByEmailAddress(
					organizationId, emailAddress));
	}

	@GraphQLField(
		description = "Assigns users to an organization by their email addresses"
	)
	public java.util.Collection<UserAccount> createUserAccountsByEmailAddress(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("organizationRoleIds") String organizationRoleIds,
			@GraphQLName("strings") String[] strings)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> {
				Page paginationPage =
					organizationResource.postUserAccountsByEmailAddress(
						organizationId, organizationRoleIds, strings);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Replaces the organization with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public Organization updateOrganization(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("organization") Organization organization)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.putOrganization(
				organizationId, organization));
	}

	@GraphQLField
	public Response updateOrganizationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource -> organizationResource.putOrganizationBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the organization with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public Organization updateOrganizationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("organization") Organization organization)
		throws Exception {

		return _applyComponentServiceObjects(
			_organizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			organizationResource ->
				organizationResource.putOrganizationByExternalReferenceCode(
					externalReferenceCode, organization));
	}

	@GraphQLField(description = "Deletes the phone number.")
	public boolean deletePhone(@GraphQLName("phoneId") Long phoneId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.deletePhone(phoneId));

		return true;
	}

	@GraphQLField
	public Response deletePhoneBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.deletePhoneBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the phone number by external reference code."
	)
	public boolean deletePhoneByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.deletePhoneByExternalReferenceCode(
				externalReferenceCode));

		return true;
	}

	@GraphQLField(description = "Updates the phone number.")
	public Phone patchPhone(
			@GraphQLName("phoneId") Long phoneId,
			@GraphQLName("phone") Phone phone)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.patchPhone(phoneId, phone));
	}

	@GraphQLField(
		description = "Updates the phone number by external reference code."
	)
	public Phone patchPhoneByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("phone") Phone phone)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.patchPhoneByExternalReferenceCode(
				externalReferenceCode, phone));
	}

	@GraphQLField
	public Response createAccountPhonesPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.postAccountPhonesPageExportBatch(
				accountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createOrganizationPhonesPageExportBatch(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource ->
				phoneResource.postOrganizationPhonesPageExportBatch(
					organizationId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createUserAccountPhonesPageExportBatch(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_phoneResourceComponentServiceObjects,
			this::_populateResourceContext,
			phoneResource -> phoneResource.postUserAccountPhonesPageExportBatch(
				userAccountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Deletes the postal address")
	public boolean deletePostalAddress(
			@GraphQLName("postalAddressId") Long postalAddressId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> postalAddressResource.deletePostalAddress(
				postalAddressId));

		return true;
	}

	@GraphQLField
	public Response deletePostalAddressBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.deletePostalAddressBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the postal address using external reference code."
	)
	public boolean deletePostalAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.
					deletePostalAddressByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public PostalAddress patchPostalAddress(
			@GraphQLName("postalAddressId") Long postalAddressId,
			@GraphQLName("postalAddress") PostalAddress postalAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> postalAddressResource.patchPostalAddress(
				postalAddressId, postalAddress));
	}

	@GraphQLField(
		description = "Updates the postal address using external reference code."
	)
	public PostalAddress patchPostalAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("postalAddress") PostalAddress postalAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.patchPostalAddressByExternalReferenceCode(
					externalReferenceCode, postalAddress));
	}

	@GraphQLField
	public PostalAddress createAccountPostalAddress(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("postalAddress") PostalAddress postalAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.postAccountPostalAddress(
					accountId, postalAddress));
	}

	@GraphQLField
	public Response createAccountPostalAddressBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.postAccountPostalAddressBatch(
					accountId, callbackURL, object));
	}

	@GraphQLField
	public Response createAccountPostalAddressesPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.postAccountPostalAddressesPageExportBatch(
					accountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createOrganizationPostalAddressesPageExportBatch(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.
					postOrganizationPostalAddressesPageExportBatch(
						organizationId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createUserAccountPostalAddressesPageExportBatch(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.
					postUserAccountPostalAddressesPageExportBatch(
						userAccountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public PostalAddress updatePostalAddress(
			@GraphQLName("postalAddressId") Long postalAddressId,
			@GraphQLName("postalAddress") PostalAddress postalAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource -> postalAddressResource.putPostalAddress(
				postalAddressId, postalAddress));
	}

	@GraphQLField
	public Response updatePostalAddressBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.putPostalAddressBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates the postal address using external reference code."
	)
	public PostalAddress updatePostalAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("postalAddress") PostalAddress postalAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_postalAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			postalAddressResource ->
				postalAddressResource.putPostalAddressByExternalReferenceCode(
					externalReferenceCode, postalAddress));
	}

	@GraphQLField(
		description = "Unassociates an organization role by external reference code with a user account"
	)
	public boolean
			deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("userAccountId") Long userAccountId,
				@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.
					deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
						externalReferenceCode, userAccountId, organizationId));

		return true;
	}

	@GraphQLField(
		description = "Unassociates an organization role with a user account"
	)
	public boolean deleteOrganizationRoleUserAccountAssociation(
			@GraphQLName("roleId") Long roleId,
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.deleteOrganizationRoleUserAccountAssociation(
					roleId, userAccountId, organizationId));

		return true;
	}

	@GraphQLField(description = "Deletes the role.")
	public boolean deleteRole(@GraphQLName("roleId") Long roleId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.deleteRole(roleId));

		return true;
	}

	@GraphQLField
	public Response deleteRoleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.deleteRoleBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the role by its external reference code."
	)
	public boolean deleteRoleByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.deleteRoleByExternalReferenceCode(
				externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Unassociates a role by external reference code with a user account"
	)
	public boolean deleteRoleByExternalReferenceCodeUserAccountAssociation(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.
					deleteRoleByExternalReferenceCodeUserAccountAssociation(
						externalReferenceCode, userAccountId));

		return true;
	}

	@GraphQLField(description = "Unassociates a role with a user account")
	public boolean deleteRoleUserAccountAssociation(
			@GraphQLName("roleId") Long roleId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.deleteRoleUserAccountAssociation(
				roleId, userAccountId));

		return true;
	}

	@GraphQLField(
		description = "Unassociates a site role by external reference code with a user account"
	)
	public boolean deleteSiteRoleByExternalReferenceCodeUserAccountAssociation(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.
					deleteSiteRoleByExternalReferenceCodeUserAccountAssociation(
						externalReferenceCode, userAccountId,
						Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField(description = "Unassociates a site role with a user account")
	public boolean deleteSiteRoleUserAccountAssociation(
			@GraphQLName("roleId") Long roleId,
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.deleteSiteRoleUserAccountAssociation(
				roleId, userAccountId, Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField(description = "Updates the role.")
	public Role patchRole(
			@GraphQLName("roleId") Long roleId, @GraphQLName("role") Role role)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.patchRole(roleId, role));
	}

	@GraphQLField(
		description = "Updates the role by its external reference code."
	)
	public Role patchRoleByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("role") Role role)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.patchRoleByExternalReferenceCode(
				externalReferenceCode, role));
	}

	@GraphQLField(
		description = "Associates a organization role by external reference code with a user account"
	)
	public boolean
			createOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("userAccountId") Long userAccountId,
				@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.
					postOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
						externalReferenceCode, userAccountId, organizationId));

		return true;
	}

	@GraphQLField(
		description = "Associates a organization role with a user account"
	)
	public boolean createOrganizationRoleUserAccountAssociation(
			@GraphQLName("roleId") Long roleId,
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.postOrganizationRoleUserAccountAssociation(
					roleId, userAccountId, organizationId));

		return true;
	}

	@GraphQLField(description = "Creates a new role")
	public Role createRole(@GraphQLName("role") Role role) throws Exception {
		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.postRole(role));
	}

	@GraphQLField
	public Response createRoleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.postRoleBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Associates a role by external reference code with a user account"
	)
	public boolean createRoleByExternalReferenceCodeUserAccountAssociation(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.
					postRoleByExternalReferenceCodeUserAccountAssociation(
						externalReferenceCode, userAccountId));

		return true;
	}

	@GraphQLField(description = "Associates a role with a user account")
	public boolean createRoleUserAccountAssociation(
			@GraphQLName("roleId") Long roleId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.postRoleUserAccountAssociation(
				roleId, userAccountId));

		return true;
	}

	@GraphQLField
	public Response createRolesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("types") Integer[] types,
			@GraphQLName("filter") String filterString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.postRolesPageExportBatch(
				search, types,
				_filterBiFunction.apply(roleResource, filterString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Associates a site role by external reference code with a user account"
	)
	public boolean createSiteRoleByExternalReferenceCodeUserAccountAssociation(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource ->
				roleResource.
					postSiteRoleByExternalReferenceCodeUserAccountAssociation(
						externalReferenceCode, userAccountId,
						Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField(description = "Associates a site role with a user account")
	public boolean createSiteRoleUserAccountAssociation(
			@GraphQLName("roleId") Long roleId,
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.postSiteRoleUserAccountAssociation(
				roleId, userAccountId, Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField(description = "Updates the role.")
	public Role updateRole(
			@GraphQLName("roleId") Long roleId, @GraphQLName("role") Role role)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.putRole(roleId, role));
	}

	@GraphQLField
	public Response updateRoleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.putRoleBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Updates the role by its external reference code."
	)
	public Role updateRoleByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("role") Role role)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.putRoleByExternalReferenceCode(
				externalReferenceCode, role));
	}

	@GraphQLField
	public Response createSiteSegmentsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_segmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			segmentResource -> segmentResource.postSiteSegmentsPageExportBatch(
				Long.valueOf(siteKey), callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean deleteMyUserAccountSubscription(
			@GraphQLName("subscriptionId") Long subscriptionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_subscriptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			subscriptionResource ->
				subscriptionResource.deleteMyUserAccountSubscription(
					subscriptionId));

		return true;
	}

	@GraphQLField(
		description = "Removes a user by their external reference code from an account by external reference code"
	)
	public boolean
			deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
						accountExternalReferenceCode, externalReferenceCode));

		return true;
	}

	@GraphQLField(description = "Removes a user assigned to an account")
	public boolean deleteAccountUserAccount(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.deleteAccountUserAccount(
				accountId, userAccountId));

		return true;
	}

	@GraphQLField(
		description = "Removes a user from an account by their email address"
	)
	public boolean deleteAccountUserAccountByEmailAddress(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.deleteAccountUserAccountByEmailAddress(
					accountId, emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Removes a user from an account by external reference code by their email address"
	)
	public boolean
			deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
						externalReferenceCode, emailAddress));

		return true;
	}

	@GraphQLField(
		description = "Removes users from an account by their email addresses"
	)
	public boolean deleteAccountUserAccountsByEmailAddress(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.deleteAccountUserAccountsByEmailAddress(
					accountId, strings));

		return true;
	}

	@GraphQLField(
		description = "Removes users from an account by their email addresses"
	)
	public boolean
			deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("strings") String[] strings)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress(
						externalReferenceCode, strings));

		return true;
	}

	@GraphQLField(description = "Deletes the user account")
	public boolean deleteUserAccount(
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.deleteUserAccount(
				userAccountId));

		return true;
	}

	@GraphQLField
	public Response deleteUserAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.deleteUserAccountBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteUserAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.deleteUserAccountByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the selected property of a user assigned to an account for a specific site"
	)
	public boolean patchSiteAccountUserAccountSelected(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("userAccountId") Long userAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.patchSiteAccountUserAccountSelected(
					Long.valueOf(siteKey), accountId, userAccountId));

		return true;
	}

	@GraphQLField(
		description = "Updates the selected property of a user assigned to an account for a specific site"
	)
	public boolean
			patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				@GraphQLName("friendlyUrlPath") String friendlyUrlPath,
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("userAccountExternalReferenceCode") String
					userAccountExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
						friendlyUrlPath, accountExternalReferenceCode,
						userAccountExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the user account with information sent in the request body. Only the provided fields are updated."
	)
	public UserAccount patchUserAccount(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("userAccount") UserAccount userAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.patchUserAccount(
				userAccountId, userAccount));
	}

	@GraphQLField(
		description = "Updates the user account with information sent in the request body. Only the provided fields are updated."
	)
	public UserAccount patchUserAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userAccount") UserAccount userAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.patchUserAccountByExternalReferenceCode(
					externalReferenceCode, userAccount));
	}

	@GraphQLField(
		description = "Assigns a user by their external reference code to an account by external reference code"
	)
	public boolean
			createAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				@GraphQLName("accountExternalReferenceCode") String
					accountExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					postAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
						accountExternalReferenceCode, externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Creates a user and assigns them to the account"
	)
	public UserAccount createAccountUserAccount(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("userAccount") UserAccount userAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.postAccountUserAccount(
				accountId, userAccount));
	}

	@GraphQLField
	public Response createAccountUserAccountBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.postAccountUserAccountBatch(
					accountId, callbackURL, object));
	}

	@GraphQLField(
		description = "Assigns a user to an account by their email address"
	)
	public UserAccount createAccountUserAccountByEmailAddress(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.postAccountUserAccountByEmailAddress(
					accountId, emailAddress));
	}

	@GraphQLField(
		description = "Creates a user and assigns them to the account"
	)
	public UserAccount createAccountUserAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userAccount") UserAccount userAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					postAccountUserAccountByExternalReferenceCode(
						externalReferenceCode, userAccount));
	}

	@GraphQLField(
		description = "Assigns a user to an account by external reference code by their email address"
	)
	public UserAccount
			createAccountUserAccountByExternalReferenceCodeByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("emailAddress") String emailAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.
					postAccountUserAccountByExternalReferenceCodeByEmailAddress(
						externalReferenceCode, emailAddress));
	}

	@GraphQLField(
		description = "Assigns users to an account by their email addresses"
	)
	public java.util.Collection<UserAccount>
			createAccountUserAccountsByEmailAddress(
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("accountRoleIds") String accountRoleIds,
				@GraphQLName("strings") String[] strings)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> {
				Page paginationPage =
					userAccountResource.postAccountUserAccountsByEmailAddress(
						accountId, accountRoleIds, strings);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Assigns users to an account by their email addresses"
	)
	public java.util.Collection<UserAccount>
			createAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountRoleIds") String accountRoleIds,
				@GraphQLName("strings") String[] strings)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> {
				Page paginationPage =
					userAccountResource.
						postAccountUserAccountsByExternalReferenceCodeByEmailAddress(
							externalReferenceCode, accountRoleIds, strings);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createAccountUserAccountsPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.postAccountUserAccountsPageExportBatch(
					accountId, search,
					_filterBiFunction.apply(userAccountResource, filterString),
					_sortsBiFunction.apply(userAccountResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createOrganizationUserAccountsPageExportBatch(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.postOrganizationUserAccountsPageExportBatch(
					organizationId, search,
					_filterBiFunction.apply(userAccountResource, filterString),
					_sortsBiFunction.apply(userAccountResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createSiteUserAccountsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.postSiteUserAccountsPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(userAccountResource, filterString),
					_sortsBiFunction.apply(userAccountResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new user account")
	public UserAccount createUserAccount(
			@GraphQLName("captchaAnswer") String captchaAnswer,
			@GraphQLName("captchaToken") String captchaToken,
			@GraphQLName("userAccount") UserAccount userAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.postUserAccount(
				captchaAnswer, captchaToken, userAccount));
	}

	@GraphQLField
	public Response createUserAccountBatch(
			@GraphQLName("captchaAnswer") String captchaAnswer,
			@GraphQLName("captchaToken") String captchaToken,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.postUserAccountBatch(
				captchaAnswer, captchaToken, callbackURL, object));
	}

	@GraphQLField
	@GraphQLName(
		description = "null",
		value = "postUserAccountImageUserAccountIdMultipartBody"
	)
	public Response createUserAccountImage(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.postUserAccountImage(
				userAccountId, multipartBody));
	}

	@GraphQLField
	public Response createUserAccountsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.postUserAccountsPageExportBatch(
					search,
					_filterBiFunction.apply(userAccountResource, filterString),
					_sortsBiFunction.apply(userAccountResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Replaces the user account with information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public UserAccount updateUserAccount(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("userAccount") UserAccount userAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.putUserAccount(
				userAccountId, userAccount));
	}

	@GraphQLField
	public Response updateUserAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource -> userAccountResource.putUserAccountBatch(
				callbackURL, object));
	}

	@GraphQLField
	public UserAccount updateUserAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userAccount") UserAccount userAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_userAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			userAccountResource ->
				userAccountResource.putUserAccountByExternalReferenceCode(
					externalReferenceCode, userAccount));
	}

	@GraphQLField
	public boolean deleteUserGroup(@GraphQLName("userGroupId") Long userGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.deleteUserGroup(
				userGroupId));

		return true;
	}

	@GraphQLField
	public Response deleteUserGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.deleteUserGroupBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteUserGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource ->
				userGroupResource.deleteUserGroupByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public boolean deleteUserGroupByExternalReferenceCodeUsers(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource ->
				userGroupResource.deleteUserGroupByExternalReferenceCodeUsers(
					externalReferenceCode, longs));

		return true;
	}

	@GraphQLField
	public boolean deleteUserGroupUsers(
			@GraphQLName("userGroupId") Long userGroupId,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.deleteUserGroupUsers(
				userGroupId, longs));

		return true;
	}

	@GraphQLField
	public UserGroup patchUserGroup(
			@GraphQLName("userGroupId") Long userGroupId,
			@GraphQLName("userGroup") UserGroup userGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.patchUserGroup(
				userGroupId, userGroup));
	}

	@GraphQLField
	public UserGroup patchUserGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userGroup") UserGroup userGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource ->
				userGroupResource.patchUserGroupByExternalReferenceCode(
					externalReferenceCode, userGroup));
	}

	@GraphQLField
	public UserGroup createUserGroup(
			@GraphQLName("userGroup") UserGroup userGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.postUserGroup(userGroup));
	}

	@GraphQLField
	public Response createUserGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.postUserGroupBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean createUserGroupByExternalReferenceCodeUsers(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource ->
				userGroupResource.postUserGroupByExternalReferenceCodeUsers(
					externalReferenceCode, longs));

		return true;
	}

	@GraphQLField
	public boolean createUserGroupUsers(
			@GraphQLName("userGroupId") Long userGroupId,
			@GraphQLName("longs") Long[] longs)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.postUserGroupUsers(
				userGroupId, longs));

		return true;
	}

	@GraphQLField
	public Response createUserGroupsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource ->
				userGroupResource.postUserGroupsPageExportBatch(
					search,
					_filterBiFunction.apply(userGroupResource, filterString),
					_sortsBiFunction.apply(userGroupResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public UserGroup updateUserGroup(
			@GraphQLName("userGroupId") Long userGroupId,
			@GraphQLName("userGroup") UserGroup userGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.putUserGroup(
				userGroupId, userGroup));
	}

	@GraphQLField
	public Response updateUserGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource -> userGroupResource.putUserGroupBatch(
				callbackURL, object));
	}

	@GraphQLField
	public UserGroup updateUserGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userGroup") UserGroup userGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_userGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			userGroupResource ->
				userGroupResource.putUserGroupByExternalReferenceCode(
					externalReferenceCode, userGroup));
	}

	@GraphQLField(description = "Deletes the web URL.")
	public boolean deleteWebUrl(@GraphQLName("webUrlId") Long webUrlId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> webUrlResource.deleteWebUrl(webUrlId));

		return true;
	}

	@GraphQLField
	public Response deleteWebUrlBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> webUrlResource.deleteWebUrlBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the web URL by external reference code."
	)
	public boolean deleteWebUrlByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource ->
				webUrlResource.deleteWebUrlByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(description = "Updates the web URL.")
	public WebUrl patchWebUrl(
			@GraphQLName("webUrlId") Long webUrlId,
			@GraphQLName("webUrl") WebUrl webUrl)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> webUrlResource.patchWebUrl(webUrlId, webUrl));
	}

	@GraphQLField(
		description = "Updates the web URL by external reference code."
	)
	public WebUrl patchWebUrlByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("webUrl") WebUrl webUrl)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> webUrlResource.patchWebUrlByExternalReferenceCode(
				externalReferenceCode, webUrl));
	}

	@GraphQLField
	public Response createAccountWebUrlsPageExportBatch(
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource -> webUrlResource.postAccountWebUrlsPageExportBatch(
				accountId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createOrganizationWebUrlsPageExportBatch(
			@GraphQLName("organizationId") String organizationId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource ->
				webUrlResource.postOrganizationWebUrlsPageExportBatch(
					organizationId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createUserAccountWebUrlsPageExportBatch(
			@GraphQLName("userAccountId") Long userAccountId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_webUrlResourceComponentServiceObjects,
			this::_populateResourceContext,
			webUrlResource ->
				webUrlResource.postUserAccountWebUrlsPageExportBatch(
					userAccountId, callbackURL, contentType, fieldNames));
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

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
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
		accountResource.setRoleLocalService(_roleLocalService);

		accountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		accountGroupResource.setRoleLocalService(_roleLocalService);

		accountGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		accountRoleResource.setRoleLocalService(_roleLocalService);

		accountRoleResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountRoleResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		emailAddressResource.setRoleLocalService(_roleLocalService);

		emailAddressResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		emailAddressResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		organizationResource.setRoleLocalService(_roleLocalService);

		organizationResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		organizationResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		phoneResource.setRoleLocalService(_roleLocalService);

		phoneResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		phoneResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		postalAddressResource.setRoleLocalService(_roleLocalService);

		postalAddressResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		postalAddressResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		roleResource.setRoleLocalService(_roleLocalService);

		roleResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		roleResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		segmentResource.setRoleLocalService(_roleLocalService);

		segmentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		segmentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		subscriptionResource.setRoleLocalService(_roleLocalService);

		subscriptionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		subscriptionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		userAccountResource.setRoleLocalService(_roleLocalService);

		userAccountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		userAccountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		userGroupResource.setRoleLocalService(_roleLocalService);

		userGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		userGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		webUrlResource.setRoleLocalService(_roleLocalService);

		webUrlResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		webUrlResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
	private static ComponentServiceObjects<SubscriptionResource>
		_subscriptionResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserAccountResource>
		_userAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserGroupResource>
		_userGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<WebUrlResource>
		_webUrlResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}