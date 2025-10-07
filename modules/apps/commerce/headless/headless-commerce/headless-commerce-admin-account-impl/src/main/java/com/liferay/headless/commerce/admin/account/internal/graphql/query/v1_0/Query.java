/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.graphql.query.v1_0;

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
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alessio Antonio Rendina
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

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {account(id: ___){accountAddresses, accountMembers, accountOrganizations, active, customFields, dateCreated, dateModified, defaultBillingAccountAddressId, defaultShippingAccountAddressId, emailAddresses, externalReferenceCode, id, logoId, logoURL, name, root, taxId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Account account(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.getAccount(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCode(externalReferenceCode: ___){accountAddresses, accountMembers, accountOrganizations, active, customFields, dateCreated, dateModified, defaultBillingAccountAddressId, defaultShippingAccountAddressId, emailAddresses, externalReferenceCode, id, logoId, logoURL, name, root, taxId, type}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accounts(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAddress(id: ___){city, countryISOCode, defaultBilling, defaultShipping, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, regionISOCode, street1, street2, street3, type, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountAddress accountAddress(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource -> accountAddressResource.getAccountAddress(
				id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAddressByExternalReferenceCode(externalReferenceCode: ___){city, countryISOCode, defaultBilling, defaultShipping, description, externalReferenceCode, id, latitude, longitude, name, phoneNumber, regionISOCode, street1, street2, street3, type, zip}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountAddress accountAddressByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource ->
				accountAddressResource.getAccountAddressByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountAddresses(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountAddressPage accountByExternalReferenceCodeAccountAddresses(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource -> new AccountAddressPage(
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountAddresses(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountAddressPage accountIdAccountAddresses(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressResource -> new AccountAddressPage(
				accountAddressResource.getAccountIdAccountAddressesPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelBillingAddresses(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelBillingAddresses(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelBillingAddressesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelCurrencies(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelCurrencies(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelCurrenciesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelDeliveryTerms(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelDeliveryTerms(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelDeliveryTermsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelDiscounts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelDiscounts(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelDiscountsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelPaymentMethods(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelPaymentMethods(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelPaymentMethodsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelPaymentTerms(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelPaymentTerms(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelPaymentTermsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelPriceLists(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelPriceLists(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelPriceListsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelShippingAddresses(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelShippingAddresses(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelShippingAddressesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelUsers(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage
			accountByExternalReferenceCodeAccountChannelUsers(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountByExternalReferenceCodeAccountChannelUsersPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelBillingAddressId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelBillingAddressId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelBillingAddressId(
					id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelCurrencyId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelCurrencyId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelCurrencyId(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelDeliveryTermId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelDeliveryTermId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelDeliveryTermId(
					id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelDiscountId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelDiscountId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelDiscountId(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelPaymentMethodId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelPaymentMethodId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelPaymentMethodId(
					id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelPaymentTermId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelPaymentTermId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelPaymentTermId(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelPriceListId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelPriceListId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelPriceListId(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelShippingAddressId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelShippingAddressId(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelShippingAddressId(
					id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelUserId(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, classExternalReferenceCode, classPK, id, overrideEligibility, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntry accountChannelUserId(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource ->
				accountChannelEntryResource.getAccountChannelUserId(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelBillingAddresses(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelBillingAddresses(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelBillingAddressesPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelCurrencies(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelCurrencies(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelCurrenciesPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelDeliveryTerms(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelDeliveryTerms(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelDeliveryTermsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelDiscounts(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelDiscounts(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelDiscountsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelPaymentMethods(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelPaymentMethods(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelPaymentMethodsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelPaymentTerms(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelPaymentTerms(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelPaymentTermsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelPriceLists(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelPriceLists(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelPriceListsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelShippingAddresses(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelShippingAddresses(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.
					getAccountIdAccountChannelShippingAddressesPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelUsers(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelEntryPage accountIdAccountChannelUsers(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelEntryResource -> new AccountChannelEntryPage(
				accountChannelEntryResource.getAccountIdAccountChannelUsersPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountChannelShippingOption(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelShippingOptionPage
			accountByExternalReferenceCodeAccountChannelShippingOption(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				new AccountChannelShippingOptionPage(
					accountChannelShippingOptionResource.
						getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
							externalReferenceCode,
							Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountChannelShippingOption(id: ___){accountExternalReferenceCode, accountId, actions, channelExternalReferenceCode, channelId, id, shippingMethodId, shippingMethodKey, shippingOptionId, shippingOptionKey}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelShippingOption accountChannelShippingOption(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				accountChannelShippingOptionResource.
					getAccountChannelShippingOption(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountChannelShippingOption(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountChannelShippingOptionPage
			accountIdAccountChannelShippingOption(
				@GraphQLName("id") Long id,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountChannelShippingOptionResource ->
				new AccountChannelShippingOptionPage(
					accountChannelShippingOptionResource.
						getAccountIdAccountChannelShippingOptionPage(
							id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountMember(externalReferenceCode: ___, userId: ___){accountId, accountRoles, email, externalReferenceCode, name, userExternalReferenceCode, userId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountMember accountByExternalReferenceCodeAccountMember(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("userId") Long userId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMember(
						externalReferenceCode, userId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountMembers(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountMemberPage accountByExternalReferenceCodeAccountMembers(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource -> new AccountMemberPage(
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountMember(id: ___, userId: ___){accountId, accountRoles, email, externalReferenceCode, name, userExternalReferenceCode, userId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountMember accountIdAccountMember(
			@GraphQLName("id") Long id, @GraphQLName("userId") Long userId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource ->
				accountMemberResource.getAccountIdAccountMember(id, userId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountMembers(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountMemberPage accountIdAccountMembers(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountMemberResource -> new AccountMemberPage(
				accountMemberResource.getAccountIdAccountMembersPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountOrganization(externalReferenceCode: ___, organizationId: ___){accountId, name, organizationExternalReferenceCode, organizationId, treePath}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountOrganization
			accountByExternalReferenceCodeAccountOrganization(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource ->
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganization(
						externalReferenceCode, organizationId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountOrganizations(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountOrganizationPage
			accountByExternalReferenceCodeAccountOrganizations(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource -> new AccountOrganizationPage(
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountOrganization(id: ___, organizationId: ___){accountId, name, organizationExternalReferenceCode, organizationId, treePath}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountOrganization accountIdAccountOrganization(
			@GraphQLName("id") Long id,
			@GraphQLName("organizationId") Long organizationId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource ->
				accountOrganizationResource.getAccountIdAccountOrganization(
					id, organizationId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountOrganizations(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountOrganizationPage accountIdAccountOrganizations(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountOrganizationResource -> new AccountOrganizationPage(
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountByExternalReferenceCodeAccountGroups(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AdminAccountGroupPage accountByExternalReferenceCodeAccountGroups(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource -> new AdminAccountGroupPage(
				adminAccountGroupResource.
					getAccountByExternalReferenceCodeAccountGroupsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroup(id: ___){customFields, description, externalReferenceCode, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AdminAccountGroup accountGroup(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource ->
				adminAccountGroupResource.getAccountGroup(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroupByExternalReferenceCode(externalReferenceCode: ___){customFields, description, externalReferenceCode, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AdminAccountGroup accountGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource ->
				adminAccountGroupResource.
					getAccountGroupByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountGroups(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AdminAccountGroupPage accountGroups(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource -> new AdminAccountGroupPage(
				adminAccountGroupResource.getAccountGroupsPage(
					search,
					_filterBiFunction.apply(
						adminAccountGroupResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						adminAccountGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountIdAccountGroups(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AdminAccountGroupPage accountIdAccountGroups(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			adminAccountGroupResource -> new AdminAccountGroupPage(
				adminAccountGroupResource.getAccountIdAccountGroupsPage(
					id, Pagination.of(page, pageSize))));
	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelShippingOptionPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelShippingOptionPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelShippingOptionPage
				byExternalReferenceCodeAccountChannelShippingOption(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelShippingOptionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelShippingOptionResource ->
					new AccountChannelShippingOptionPage(
						accountChannelShippingOptionResource.
							getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
								_account.getExternalReferenceCode(),
								Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelPaymentTermsPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelPaymentTermsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelPaymentTerms(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelPaymentTermsPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountMembersPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountMembersPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountMemberPage byExternalReferenceCodeAccountMembers(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountMemberResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountMemberResource -> new AccountMemberPage(
					accountMemberResource.
						getAccountByExternalReferenceCodeAccountMembersPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountAddressesPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountAddressPage byExternalReferenceCodeAccountAddresses(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountAddressResource -> new AccountAddressPage(
					accountAddressResource.
						getAccountByExternalReferenceCodeAccountAddressesPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(AccountAddress.class)
	public class GetAccountByExternalReferenceCodeTypeExtension {

		public GetAccountByExternalReferenceCodeTypeExtension(
			AccountAddress accountAddress) {

			_accountAddress = accountAddress;
		}

		@GraphQLField
		public Account accountByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_accountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountResource ->
					accountResource.getAccountByExternalReferenceCode(
						_accountAddress.getExternalReferenceCode()));
		}

		private AccountAddress _accountAddress;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelShippingAddressesPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelShippingAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelShippingAddresses(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelShippingAddressesPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
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
		public AdminAccountGroup groupByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_adminAccountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				adminAccountGroupResource ->
					adminAccountGroupResource.
						getAccountGroupByExternalReferenceCode(
							_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelBillingAddressesPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelBillingAddressesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelBillingAddresses(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelBillingAddressesPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelUsersPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelUsersPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelUsers(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelUsersPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountByExternalReferenceCodeAccountMemberTypeExtension {

		public GetAccountByExternalReferenceCodeAccountMemberTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountMember byExternalReferenceCodeAccountMember(
				@GraphQLName("userId") Long userId)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountMemberResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountMemberResource ->
					accountMemberResource.
						getAccountByExternalReferenceCodeAccountMember(
							_account.getExternalReferenceCode(), userId));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountOrganizationTypeExtension {

		public GetAccountByExternalReferenceCodeAccountOrganizationTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountOrganization byExternalReferenceCodeAccountOrganization(
				@GraphQLName("organizationId") Long organizationId)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountOrganizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountOrganizationResource ->
					accountOrganizationResource.
						getAccountByExternalReferenceCodeAccountOrganization(
							_account.getExternalReferenceCode(),
							organizationId));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelPriceListsPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelPriceListsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelPriceLists(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelPriceListsPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelCurrenciesPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelCurrenciesPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelCurrencies(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelCurrenciesPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountGroupsPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountGroupsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AdminAccountGroupPage byExternalReferenceCodeAccountGroups(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_adminAccountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				adminAccountGroupResource -> new AdminAccountGroupPage(
					adminAccountGroupResource.
						getAccountByExternalReferenceCodeAccountGroupsPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelPaymentMethodsPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelPaymentMethodsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelPaymentMethods(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelPaymentMethodsPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountOrganizationsPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountOrganizationsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountOrganizationPage
				byExternalReferenceCodeAccountOrganizations(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountOrganizationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountOrganizationResource -> new AccountOrganizationPage(
					accountOrganizationResource.
						getAccountByExternalReferenceCodeAccountOrganizationsPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelDiscountsPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelDiscountsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelDiscounts(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelDiscountsPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class
		GetAccountByExternalReferenceCodeAccountChannelDeliveryTermsPageTypeExtension {

		public GetAccountByExternalReferenceCodeAccountChannelDeliveryTermsPageTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountChannelEntryPage
				byExternalReferenceCodeAccountChannelDeliveryTerms(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountChannelEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountChannelEntryResource -> new AccountChannelEntryPage(
					accountChannelEntryResource.
						getAccountByExternalReferenceCodeAccountChannelDeliveryTermsPage(
							_account.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Account _account;

	}

	@GraphQLTypeExtension(Account.class)
	public class GetAccountAddressByExternalReferenceCodeTypeExtension {

		public GetAccountAddressByExternalReferenceCodeTypeExtension(
			Account account) {

			_account = account;
		}

		@GraphQLField
		public AccountAddress addressByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_accountAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountAddressResource ->
					accountAddressResource.
						getAccountAddressByExternalReferenceCode(
							_account.getExternalReferenceCode()));
		}

		private Account _account;

	}

	@GraphQLName("AccountPage")
	public class AccountPage {

		public AccountPage(Page accountPage) {
			actions = accountPage.getActions();

			items = accountPage.getItems();
			lastPage = accountPage.getLastPage();
			page = accountPage.getPage();
			pageSize = accountPage.getPageSize();
			totalCount = accountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

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

	@GraphQLName("AccountAddressPage")
	public class AccountAddressPage {

		public AccountAddressPage(Page accountAddressPage) {
			actions = accountAddressPage.getActions();

			items = accountAddressPage.getItems();
			lastPage = accountAddressPage.getLastPage();
			page = accountAddressPage.getPage();
			pageSize = accountAddressPage.getPageSize();
			totalCount = accountAddressPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountAddress> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AccountChannelEntryPage")
	public class AccountChannelEntryPage {

		public AccountChannelEntryPage(Page accountChannelEntryPage) {
			actions = accountChannelEntryPage.getActions();

			items = accountChannelEntryPage.getItems();
			lastPage = accountChannelEntryPage.getLastPage();
			page = accountChannelEntryPage.getPage();
			pageSize = accountChannelEntryPage.getPageSize();
			totalCount = accountChannelEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountChannelEntry> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AccountChannelShippingOptionPage")
	public class AccountChannelShippingOptionPage {

		public AccountChannelShippingOptionPage(
			Page accountChannelShippingOptionPage) {

			actions = accountChannelShippingOptionPage.getActions();

			items = accountChannelShippingOptionPage.getItems();
			lastPage = accountChannelShippingOptionPage.getLastPage();
			page = accountChannelShippingOptionPage.getPage();
			pageSize = accountChannelShippingOptionPage.getPageSize();
			totalCount = accountChannelShippingOptionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountChannelShippingOption> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AccountMemberPage")
	public class AccountMemberPage {

		public AccountMemberPage(Page accountMemberPage) {
			actions = accountMemberPage.getActions();

			items = accountMemberPage.getItems();
			lastPage = accountMemberPage.getLastPage();
			page = accountMemberPage.getPage();
			pageSize = accountMemberPage.getPageSize();
			totalCount = accountMemberPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountMember> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AccountOrganizationPage")
	public class AccountOrganizationPage {

		public AccountOrganizationPage(Page accountOrganizationPage) {
			actions = accountOrganizationPage.getActions();

			items = accountOrganizationPage.getItems();
			lastPage = accountOrganizationPage.getLastPage();
			page = accountOrganizationPage.getPage();
			pageSize = accountOrganizationPage.getPageSize();
			totalCount = accountOrganizationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountOrganization> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("AdminAccountGroupPage")
	public class AdminAccountGroupPage {

		public AdminAccountGroupPage(Page adminAccountGroupPage) {
			actions = adminAccountGroupPage.getActions();

			items = adminAccountGroupPage.getItems();
			lastPage = adminAccountGroupPage.getLastPage();
			page = adminAccountGroupPage.getPage();
			pageSize = adminAccountGroupPage.getPageSize();
			totalCount = adminAccountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AdminAccountGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

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
		accountAddressResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountAddressResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountAddressResource.setRoleLocalService(_roleLocalService);
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
		accountChannelEntryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountChannelEntryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountChannelEntryResource.setRoleLocalService(_roleLocalService);
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
		accountChannelShippingOptionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountChannelShippingOptionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountChannelShippingOptionResource.setRoleLocalService(
			_roleLocalService);
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
		accountMemberResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountMemberResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountMemberResource.setRoleLocalService(_roleLocalService);
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
		accountOrganizationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountOrganizationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountOrganizationResource.setRoleLocalService(_roleLocalService);
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
		adminAccountGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		adminAccountGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		adminAccountGroupResource.setRoleLocalService(_roleLocalService);
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

	private AcceptLanguage _acceptLanguage;
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