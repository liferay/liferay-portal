/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.graphql.mutation.v1_0;

import com.liferay.headless.commerce.admin.account.dto.v1_0.Account;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountAddress;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountChannelEntry;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountChannelShippingOption;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountMember;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountOrganization;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AdminAccountGroup;
import com.liferay.headless.commerce.admin.account.dto.v1_0.User;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountAddressResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountChannelEntryResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountChannelShippingOptionResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountMemberResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountOrganizationResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AdminAccountGroupResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.UserResource;
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

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alessio Antonio Rendina
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

	public static void setAccountAddressResourceComponentServiceObjects(
		ComponentServiceObjects<AccountAddressResource>
			accountAddressResourceComponentServiceObjects) {

		_accountAddressResourceComponentServiceObjects =
			accountAddressResourceComponentServiceObjects;
	}

	public static void setAccountChannelEntryResourceComponentServiceObjects(
		ComponentServiceObjects<AccountChannelEntryResource>
			accountChannelEntryResourceComponentServiceObjects) {

		_accountChannelEntryResourceComponentServiceObjects =
			accountChannelEntryResourceComponentServiceObjects;
	}

	public static void
		setAccountChannelShippingOptionResourceComponentServiceObjects(
			ComponentServiceObjects<AccountChannelShippingOptionResource>
				accountChannelShippingOptionResourceComponentServiceObjects) {

		_accountChannelShippingOptionResourceComponentServiceObjects =
			accountChannelShippingOptionResourceComponentServiceObjects;
	}

	public static void setAccountMemberResourceComponentServiceObjects(
		ComponentServiceObjects<AccountMemberResource>
			accountMemberResourceComponentServiceObjects) {

		_accountMemberResourceComponentServiceObjects =
			accountMemberResourceComponentServiceObjects;
	}

	public static void setAccountOrganizationResourceComponentServiceObjects(
		ComponentServiceObjects<AccountOrganizationResource>
			accountOrganizationResourceComponentServiceObjects) {

		_accountOrganizationResourceComponentServiceObjects =
			accountOrganizationResourceComponentServiceObjects;
	}

	public static void setAdminAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<AdminAccountGroupResource>
			adminAccountGroupResourceComponentServiceObjects) {

		_adminAccountGroupResourceComponentServiceObjects =
			adminAccountGroupResourceComponentServiceObjects;
	}

	public static void setUserResourceComponentServiceObjects(
		ComponentServiceObjects<UserResource>
			userResourceComponentServiceObjects) {

		_userResourceComponentServiceObjects =
			userResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createAccountGroupByExternalReferenceCodeAccount(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("account") Account account)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.postAccountGroupByExternalReferenceCodeAccount(
					externalReferenceCode, account));
	}

	@GraphQLField
	public Response deleteAccountGroupByExternalReferenceCodeAccount(
			@GraphQLName("accountExternalReferenceCode") String
				accountExternalReferenceCode,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.
					deleteAccountGroupByExternalReferenceCodeAccount(
						accountExternalReferenceCode, externalReferenceCode));
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
	public Response deleteAccountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.deleteAccountByExternalReferenceCode(
					externalReferenceCode));
	}

	@GraphQLField
	public Response patchAccountByExternalReferenceCode(
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
	@GraphQLName(
		description = "null",
		value = "postAccountByExternalReferenceCodeLogoExternalReferenceCodeMultipartBody"
	)
	public Response createAccountByExternalReferenceCodeLogo(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource ->
				accountResource.postAccountByExternalReferenceCodeLogo(
					externalReferenceCode, multipartBody));
	}

	@GraphQLField
	public Response deleteAccount(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.deleteAccount(id));
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

	@GraphQLField
	public Response patchAccount(
			@GraphQLName("id") Long id, @GraphQLName("account") Account account)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.patchAccount(id, account));
	}

	@GraphQLField
	@GraphQLName(description = "null", value = "postAccountLogoIdMultipartBody")
	public Response createAccountLogo(
			@GraphQLName("id") Long id,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.postAccountLogo(
				id, multipartBody));
	}

	@GraphQLField
	public Response deleteAccountAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.
					deleteAccountAddressByExternalReferenceCode(
						externalReferenceCode));
	}

	@GraphQLField
	public Response patchAccountAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accountAddress") AccountAddress accountAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.
					patchAccountAddressByExternalReferenceCode(
						externalReferenceCode, accountAddress));
	}

	@GraphQLField
	public Response deleteAccountAddress(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.deleteAccountAddress(id));
	}

	@GraphQLField
	public Response deleteAccountAddressBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.deleteAccountAddressBatch(
					callbackURL, object));
	}

	@GraphQLField
	public AccountAddress patchAccountAddress(
			@GraphQLName("id") Long id,
			@GraphQLName("accountAddress") AccountAddress accountAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.patchAccountAddress(id, accountAddress));
	}

	@GraphQLField
	public AccountAddress updateAccountAddress(
			@GraphQLName("id") Long id,
			@GraphQLName("accountAddress") AccountAddress accountAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource -> accountAddressResource.putAccountAddress(
				id, accountAddress));
	}

	@GraphQLField
	public Response updateAccountAddressBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.putAccountAddressBatch(
					callbackURL, object));
	}

	@GraphQLField
	public AccountAddress createAccountByExternalReferenceCodeAccountAddress(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accountAddress") AccountAddress accountAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.
					postAccountByExternalReferenceCodeAccountAddress(
						externalReferenceCode, accountAddress));
	}

	@GraphQLField
	public AccountAddress createAccountIdAccountAddress(
			@GraphQLName("id") Long id,
			@GraphQLName("accountAddress") AccountAddress accountAddress)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.postAccountIdAccountAddress(
					id, accountAddress));
	}

	@GraphQLField
	public Response createAccountIdAccountAddressBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.postAccountIdAccountAddressBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteAccountChannelBillingAddressId(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					deleteAccountChannelBillingAddressId(id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelBillingAddressId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelBillingAddressId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelCurrencyId(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.deleteAccountChannelCurrencyId(id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelCurrencyId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelCurrencyId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelDeliveryTermId(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.deleteAccountChannelDeliveryTermId(
					id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelDeliveryTermId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelDeliveryTermId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelDiscountId(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.deleteAccountChannelDiscountId(id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelDiscountId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelDiscountId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelPaymentMethodId(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.deleteAccountChannelPaymentMethodId(
					id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelPaymentMethodId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelPaymentMethodId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelPaymentTermId(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.deleteAccountChannelPaymentTermId(
					id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelPaymentTermId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelPaymentTermId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelPriceListId(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.deleteAccountChannelPriceListId(
					id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelPriceListId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelPriceListId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelShippingAddressId(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					deleteAccountChannelShippingAddressId(id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelShippingAddressId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					patchAccountChannelShippingAddressId(
						id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelUserId(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.deleteAccountChannelUserId(id));

		return true;
	}

	@GraphQLField
	public AccountChannelEntry patchAccountChannelUserId(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.patchAccountChannelUserId(
					id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelBillingAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelBillingAddress(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelCurrency(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelCurrency(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelDeliveryTerm(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelDeliveryTerm(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelDiscount(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelDiscount(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelPaymentMethod(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelPaymentMethod(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelPaymentTerm(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelPaymentTerm(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelPriceList(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelPriceList(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelShippingAddress(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelShippingAddress(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry
			createAccountByExternalReferenceCodeAccountChannelUser(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelEntry") AccountChannelEntry
					accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountByExternalReferenceCodeAccountChannelUser(
						externalReferenceCode, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelBillingAddress(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountIdAccountChannelBillingAddress(
						id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelCurrency(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.postAccountIdAccountChannelCurrency(
					id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelDeliveryTerm(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountIdAccountChannelDeliveryTerm(
						id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelDiscount(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.postAccountIdAccountChannelDiscount(
					id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelPaymentMethod(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountIdAccountChannelPaymentMethod(
						id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelPaymentTerm(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountIdAccountChannelPaymentTerm(
						id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelPriceList(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountIdAccountChannelPriceList(
						id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelShippingAddress(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.
					postAccountIdAccountChannelShippingAddress(
						id, accountChannelEntry));
	}

	@GraphQLField
	public AccountChannelEntry createAccountIdAccountChannelUser(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelEntry") AccountChannelEntry
				accountChannelEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.postAccountIdAccountChannelUser(
					id, accountChannelEntry));
	}

	@GraphQLField
	public boolean deleteAccountChannelShippingOption(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				accountChannelShippingOptionResource.
					deleteAccountChannelShippingOption(id));

		return true;
	}

	@GraphQLField
	public Response deleteAccountChannelShippingOptionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				accountChannelShippingOptionResource.
					deleteAccountChannelShippingOptionBatch(
						callbackURL, object));
	}

	@GraphQLField
	public AccountChannelShippingOption patchAccountChannelShippingOption(
			@GraphQLName("id") Long id,
			@GraphQLName("accountChannelShippingOption")
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				accountChannelShippingOptionResource.
					patchAccountChannelShippingOption(
						id, accountChannelShippingOption));
	}

	@GraphQLField
	public AccountChannelShippingOption
			createAccountByExternalReferenceCodeAccountChannelShippingOption(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountChannelShippingOption")
					AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				accountChannelShippingOptionResource.
					postAccountByExternalReferenceCodeAccountChannelShippingOption(
						externalReferenceCode, accountChannelShippingOption));
	}

	@GraphQLField
	public AccountChannelShippingOption
			createAccountIdAccountChannelShippingOption(
				@GraphQLName("id") Long id,
				@GraphQLName("accountChannelShippingOption")
					AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				accountChannelShippingOptionResource.
					postAccountIdAccountChannelShippingOption(
						id, accountChannelShippingOption));
	}

	@GraphQLField
	public Response createAccountIdAccountChannelShippingOptionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				accountChannelShippingOptionResource.
					postAccountIdAccountChannelShippingOptionBatch(
						callbackURL, object));
	}

	@GraphQLField
	public AccountMember createAccountByExternalReferenceCodeAccountMember(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accountMember") AccountMember accountMember)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.
					postAccountByExternalReferenceCodeAccountMember(
						externalReferenceCode, accountMember));
	}

	@GraphQLField
	public Response deleteAccountByExternalReferenceCodeAccountMember(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userId") Long userId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.
					deleteAccountByExternalReferenceCodeAccountMember(
						externalReferenceCode, userId));
	}

	@GraphQLField
	public Response patchAccountByExternalReferenceCodeAccountMember(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userId") Long userId,
			@GraphQLName("accountMember") AccountMember accountMember)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.
					patchAccountByExternalReferenceCodeAccountMember(
						externalReferenceCode, userId, accountMember));
	}

	@GraphQLField
	public AccountMember createAccountIdAccountMember(
			@GraphQLName("id") Long id,
			@GraphQLName("accountMember") AccountMember accountMember)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.postAccountIdAccountMember(
					id, accountMember));
	}

	@GraphQLField
	public Response createAccountIdAccountMemberBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.postAccountIdAccountMemberBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response deleteAccountIdAccountMember(
			@GraphQLName("id") Long id, @GraphQLName("userId") Long userId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.deleteAccountIdAccountMember(id, userId));
	}

	@GraphQLField
	public Response patchAccountIdAccountMember(
			@GraphQLName("id") Long id, @GraphQLName("userId") Long userId,
			@GraphQLName("accountMember") AccountMember accountMember)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.patchAccountIdAccountMember(
					id, userId, accountMember));
	}

	@GraphQLField
	public AccountOrganization
			createAccountByExternalReferenceCodeAccountOrganization(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountOrganization") AccountOrganization
					accountOrganization)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource ->
				accountOrganizationResource.
					postAccountByExternalReferenceCodeAccountOrganization(
						externalReferenceCode, accountOrganization));
	}

	@GraphQLField
	public Response deleteAccountByExternalReferenceCodeAccountOrganization(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource ->
				accountOrganizationResource.
					deleteAccountByExternalReferenceCodeAccountOrganization(
						externalReferenceCode, organizationId));
	}

	@GraphQLField
	public AccountOrganization createAccountIdAccountOrganization(
			@GraphQLName("id") Long id,
			@GraphQLName("accountOrganization") AccountOrganization
				accountOrganization)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource ->
				accountOrganizationResource.postAccountIdAccountOrganization(
					id, accountOrganization));
	}

	@GraphQLField
	public Response createAccountIdAccountOrganizationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource ->
				accountOrganizationResource.
					postAccountIdAccountOrganizationBatch(callbackURL, object));
	}

	@GraphQLField
	public Response deleteAccountIdAccountOrganization(
			@GraphQLName("id") Long id,
			@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource ->
				accountOrganizationResource.deleteAccountIdAccountOrganization(
					id, organizationId));
	}

	@GraphQLField
	public AdminAccountGroup createAccountGroup(
			@GraphQLName("adminAccountGroup") AdminAccountGroup
				adminAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource ->
				adminAccountGroupResource.postAccountGroup(adminAccountGroup));
	}

	@GraphQLField
	public Response deleteAccountGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource ->
				adminAccountGroupResource.
					deleteAccountGroupByExternalReferenceCode(
						externalReferenceCode));
	}

	@GraphQLField
	public Response patchAccountGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("adminAccountGroup") AdminAccountGroup
				adminAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource ->
				adminAccountGroupResource.
					patchAccountGroupByExternalReferenceCode(
						externalReferenceCode, adminAccountGroup));
	}

	@GraphQLField
	public Response deleteAccountGroup(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource ->
				adminAccountGroupResource.deleteAccountGroup(id));
	}

	@GraphQLField
	public Response patchAccountGroup(
			@GraphQLName("id") Long id,
			@GraphQLName("adminAccountGroup") AdminAccountGroup
				adminAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource ->
				adminAccountGroupResource.patchAccountGroup(
					id, adminAccountGroup));
	}

	@GraphQLField
	public User createAccountByExternalReferenceCodeAccountMemberCreateUser(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("user") User user)
		throws Exception {

		return _applyComponentServiceObjects(
			_userResourceComponentServiceObjects,
			this::_populateResourceContext,
			userResource ->
				userResource.
					postAccountByExternalReferenceCodeAccountMemberCreateUser(
						externalReferenceCode, user));
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
			AccountAddressResource accountAddressResource)
		throws Exception {

		accountAddressResource.setContextAcceptLanguage(_acceptLanguage);
		accountAddressResource.setContextCompany(_company);
		accountAddressResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountAddressResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountAddressResource.setContextUriInfo(_uriInfo);
		accountAddressResource.setContextUser(_user);
		accountAddressResource.setGroupLocalService(_groupLocalService);
		accountAddressResource.setRoleLocalService(_roleLocalService);

		accountAddressResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountAddressResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			AccountChannelEntryResource accountChannelEntryResource)
		throws Exception {

		accountChannelEntryResource.setContextAcceptLanguage(_acceptLanguage);
		accountChannelEntryResource.setContextCompany(_company);
		accountChannelEntryResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountChannelEntryResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountChannelEntryResource.setContextUriInfo(_uriInfo);
		accountChannelEntryResource.setContextUser(_user);
		accountChannelEntryResource.setGroupLocalService(_groupLocalService);
		accountChannelEntryResource.setRoleLocalService(_roleLocalService);

		accountChannelEntryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountChannelEntryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			AccountChannelShippingOptionResource
				accountChannelShippingOptionResource)
		throws Exception {

		accountChannelShippingOptionResource.setContextAcceptLanguage(
			_acceptLanguage);
		accountChannelShippingOptionResource.setContextCompany(_company);
		accountChannelShippingOptionResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountChannelShippingOptionResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountChannelShippingOptionResource.setContextUriInfo(_uriInfo);
		accountChannelShippingOptionResource.setContextUser(_user);
		accountChannelShippingOptionResource.setGroupLocalService(
			_groupLocalService);
		accountChannelShippingOptionResource.setRoleLocalService(
			_roleLocalService);

		accountChannelShippingOptionResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		accountChannelShippingOptionResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			AccountMemberResource accountMemberResource)
		throws Exception {

		accountMemberResource.setContextAcceptLanguage(_acceptLanguage);
		accountMemberResource.setContextCompany(_company);
		accountMemberResource.setContextHttpServletRequest(_httpServletRequest);
		accountMemberResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountMemberResource.setContextUriInfo(_uriInfo);
		accountMemberResource.setContextUser(_user);
		accountMemberResource.setGroupLocalService(_groupLocalService);
		accountMemberResource.setRoleLocalService(_roleLocalService);

		accountMemberResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountMemberResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			AccountOrganizationResource accountOrganizationResource)
		throws Exception {

		accountOrganizationResource.setContextAcceptLanguage(_acceptLanguage);
		accountOrganizationResource.setContextCompany(_company);
		accountOrganizationResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountOrganizationResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountOrganizationResource.setContextUriInfo(_uriInfo);
		accountOrganizationResource.setContextUser(_user);
		accountOrganizationResource.setGroupLocalService(_groupLocalService);
		accountOrganizationResource.setRoleLocalService(_roleLocalService);

		accountOrganizationResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountOrganizationResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			AdminAccountGroupResource adminAccountGroupResource)
		throws Exception {

		adminAccountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		adminAccountGroupResource.setContextCompany(_company);
		adminAccountGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		adminAccountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		adminAccountGroupResource.setContextUriInfo(_uriInfo);
		adminAccountGroupResource.setContextUser(_user);
		adminAccountGroupResource.setGroupLocalService(_groupLocalService);
		adminAccountGroupResource.setRoleLocalService(_roleLocalService);

		adminAccountGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		adminAccountGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(UserResource userResource)
		throws Exception {

		userResource.setContextAcceptLanguage(_acceptLanguage);
		userResource.setContextCompany(_company);
		userResource.setContextHttpServletRequest(_httpServletRequest);
		userResource.setContextHttpServletResponse(_httpServletResponse);
		userResource.setContextUriInfo(_uriInfo);
		userResource.setContextUser(_user);
		userResource.setGroupLocalService(_groupLocalService);
		userResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountAddressResource>
		_accountAddressResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountChannelEntryResource>
		_accountChannelEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountChannelShippingOptionResource>
		_accountChannelShippingOptionResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountMemberResource>
		_accountMemberResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountOrganizationResource>
		_accountOrganizationResourceComponentServiceObjects;
	private static ComponentServiceObjects<AdminAccountGroupResource>
		_adminAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<UserResource>
		_userResourceComponentServiceObjects;

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