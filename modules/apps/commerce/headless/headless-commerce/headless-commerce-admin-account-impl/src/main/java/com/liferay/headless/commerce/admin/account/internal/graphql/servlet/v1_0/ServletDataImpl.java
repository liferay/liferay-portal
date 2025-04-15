/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.account.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.account.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.AccountAddressResourceImpl;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.AccountChannelEntryResourceImpl;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.AccountChannelShippingOptionResourceImpl;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.AccountMemberResourceImpl;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.AccountOrganizationResourceImpl;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.AccountResourceImpl;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.AdminAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.account.internal.resource.v1_0.UserResourceImpl;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountAddressResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountChannelEntryResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountChannelShippingOptionResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountMemberResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountOrganizationResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AdminAccountGroupResource;
import com.liferay.headless.commerce.admin.account.resource.v1_0.UserResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setAccountResourceComponentServiceObjects(
			_accountResourceComponentServiceObjects);
		Mutation.setAccountAddressResourceComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects);
		Mutation.setAccountChannelEntryResourceComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects);
		Mutation.setAccountChannelShippingOptionResourceComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects);
		Mutation.setAccountMemberResourceComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects);
		Mutation.setAccountOrganizationResourceComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects);
		Mutation.setAdminAccountGroupResourceComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects);
		Mutation.setUserResourceComponentServiceObjects(
			_userResourceComponentServiceObjects);

		Query.setAccountResourceComponentServiceObjects(
			_accountResourceComponentServiceObjects);
		Query.setAccountAddressResourceComponentServiceObjects(
			_accountAddressResourceComponentServiceObjects);
		Query.setAccountChannelEntryResourceComponentServiceObjects(
			_accountChannelEntryResourceComponentServiceObjects);
		Query.setAccountChannelShippingOptionResourceComponentServiceObjects(
			_accountChannelShippingOptionResourceComponentServiceObjects);
		Query.setAccountMemberResourceComponentServiceObjects(
			_accountMemberResourceComponentServiceObjects);
		Query.setAccountOrganizationResourceComponentServiceObjects(
			_accountOrganizationResourceComponentServiceObjects);
		Query.setAdminAccountGroupResourceComponentServiceObjects(
			_adminAccountGroupResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Account";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-account-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createAccountGroupByExternalReferenceCodeAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"postAccountGroupByExternalReferenceCodeAccount"));
					put(
						"mutation#deleteAccountGroupByExternalReferenceCodeAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"deleteAccountGroupByExternalReferenceCodeAccount"));
					put(
						"mutation#createAccountsPageExportBatch",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"postAccountsPageExportBatch"));
					put(
						"mutation#createAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "postAccount"));
					put(
						"mutation#createAccountBatch",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "postAccountBatch"));
					put(
						"mutation#deleteAccountByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"deleteAccountByExternalReferenceCode"));
					put(
						"mutation#patchAccountByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"patchAccountByExternalReferenceCode"));
					put(
						"mutation#createAccountByExternalReferenceCodeLogo",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"postAccountByExternalReferenceCodeLogo"));
					put(
						"mutation#deleteAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "deleteAccount"));
					put(
						"mutation#deleteAccountBatch",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "deleteAccountBatch"));
					put(
						"mutation#patchAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "patchAccount"));
					put(
						"mutation#createAccountLogo",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "postAccountLogo"));
					put(
						"mutation#deleteAccountAddressByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"deleteAccountAddressByExternalReferenceCode"));
					put(
						"mutation#patchAccountAddressByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"patchAccountAddressByExternalReferenceCode"));
					put(
						"mutation#deleteAccountAddress",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"deleteAccountAddress"));
					put(
						"mutation#deleteAccountAddressBatch",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"deleteAccountAddressBatch"));
					put(
						"mutation#patchAccountAddress",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"patchAccountAddress"));
					put(
						"mutation#updateAccountAddress",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"putAccountAddress"));
					put(
						"mutation#updateAccountAddressBatch",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"putAccountAddressBatch"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountAddress",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountAddress"));
					put(
						"mutation#createAccountIdAccountAddress",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"postAccountIdAccountAddress"));
					put(
						"mutation#createAccountIdAccountAddressBatch",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"postAccountIdAccountAddressBatch"));
					put(
						"mutation#deleteAccountChannelBillingAddressId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelBillingAddressId"));
					put(
						"mutation#patchAccountChannelBillingAddressId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelBillingAddressId"));
					put(
						"mutation#deleteAccountChannelCurrencyId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelCurrencyId"));
					put(
						"mutation#patchAccountChannelCurrencyId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelCurrencyId"));
					put(
						"mutation#deleteAccountChannelDeliveryTermId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelDeliveryTermId"));
					put(
						"mutation#patchAccountChannelDeliveryTermId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelDeliveryTermId"));
					put(
						"mutation#deleteAccountChannelDiscountId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelDiscountId"));
					put(
						"mutation#patchAccountChannelDiscountId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelDiscountId"));
					put(
						"mutation#deleteAccountChannelPaymentMethodId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelPaymentMethodId"));
					put(
						"mutation#patchAccountChannelPaymentMethodId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelPaymentMethodId"));
					put(
						"mutation#deleteAccountChannelPaymentTermId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelPaymentTermId"));
					put(
						"mutation#patchAccountChannelPaymentTermId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelPaymentTermId"));
					put(
						"mutation#deleteAccountChannelPriceListId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelPriceListId"));
					put(
						"mutation#patchAccountChannelPriceListId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelPriceListId"));
					put(
						"mutation#deleteAccountChannelShippingAddressId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelShippingAddressId"));
					put(
						"mutation#patchAccountChannelShippingAddressId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelShippingAddressId"));
					put(
						"mutation#deleteAccountChannelUserId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"deleteAccountChannelUserId"));
					put(
						"mutation#patchAccountChannelUserId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"patchAccountChannelUserId"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelBillingAddress",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelBillingAddress"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelCurrency",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelCurrency"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelDeliveryTerm",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelDeliveryTerm"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelDiscount",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelDiscount"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelPaymentMethod",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelPaymentMethod"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelPaymentTerm",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelPaymentTerm"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelPriceList",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelPriceList"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelShippingAddress",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelShippingAddress"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelUser",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelUser"));
					put(
						"mutation#createAccountIdAccountChannelBillingAddress",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelBillingAddress"));
					put(
						"mutation#createAccountIdAccountChannelCurrency",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelCurrency"));
					put(
						"mutation#createAccountIdAccountChannelDeliveryTerm",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelDeliveryTerm"));
					put(
						"mutation#createAccountIdAccountChannelDiscount",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelDiscount"));
					put(
						"mutation#createAccountIdAccountChannelPaymentMethod",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelPaymentMethod"));
					put(
						"mutation#createAccountIdAccountChannelPaymentTerm",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelPaymentTerm"));
					put(
						"mutation#createAccountIdAccountChannelPriceList",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelPriceList"));
					put(
						"mutation#createAccountIdAccountChannelShippingAddress",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelShippingAddress"));
					put(
						"mutation#createAccountIdAccountChannelUser",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"postAccountIdAccountChannelUser"));
					put(
						"mutation#deleteAccountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"deleteAccountChannelShippingOption"));
					put(
						"mutation#deleteAccountChannelShippingOptionBatch",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"deleteAccountChannelShippingOptionBatch"));
					put(
						"mutation#patchAccountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"patchAccountChannelShippingOption"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountChannelShippingOption"));
					put(
						"mutation#createAccountIdAccountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"postAccountIdAccountChannelShippingOption"));
					put(
						"mutation#createAccountIdAccountChannelShippingOptionBatch",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"postAccountIdAccountChannelShippingOptionBatch"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountMember"));
					put(
						"mutation#deleteAccountByExternalReferenceCodeAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"deleteAccountByExternalReferenceCodeAccountMember"));
					put(
						"mutation#patchAccountByExternalReferenceCodeAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"patchAccountByExternalReferenceCodeAccountMember"));
					put(
						"mutation#createAccountIdAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"postAccountIdAccountMember"));
					put(
						"mutation#createAccountIdAccountMemberBatch",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"postAccountIdAccountMemberBatch"));
					put(
						"mutation#deleteAccountIdAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"deleteAccountIdAccountMember"));
					put(
						"mutation#patchAccountIdAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"patchAccountIdAccountMember"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountOrganization",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountOrganization"));
					put(
						"mutation#deleteAccountByExternalReferenceCodeAccountOrganization",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"deleteAccountByExternalReferenceCodeAccountOrganization"));
					put(
						"mutation#createAccountIdAccountOrganization",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"postAccountIdAccountOrganization"));
					put(
						"mutation#createAccountIdAccountOrganizationBatch",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"postAccountIdAccountOrganizationBatch"));
					put(
						"mutation#deleteAccountIdAccountOrganization",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"deleteAccountIdAccountOrganization"));
					put(
						"mutation#createAccountGroup",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"postAccountGroup"));
					put(
						"mutation#deleteAccountGroupByExternalReferenceCode",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"deleteAccountGroupByExternalReferenceCode"));
					put(
						"mutation#patchAccountGroupByExternalReferenceCode",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"patchAccountGroupByExternalReferenceCode"));
					put(
						"mutation#deleteAccountGroup",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"deleteAccountGroup"));
					put(
						"mutation#patchAccountGroup",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"patchAccountGroup"));
					put(
						"mutation#createAccountByExternalReferenceCodeAccountMemberCreateUser",
						new ObjectValuePair<>(
							UserResourceImpl.class,
							"postAccountByExternalReferenceCodeAccountMemberCreateUser"));

					put(
						"query#accounts",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "getAccountsPage"));
					put(
						"query#accountByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getAccountByExternalReferenceCode"));
					put(
						"query#account",
						new ObjectValuePair<>(
							AccountResourceImpl.class, "getAccount"));
					put(
						"query#accountAddressByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"getAccountAddressByExternalReferenceCode"));
					put(
						"query#accountAddress",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"getAccountAddress"));
					put(
						"query#accountByExternalReferenceCodeAccountAddresses",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountAddressesPage"));
					put(
						"query#accountIdAccountAddresses",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"getAccountIdAccountAddressesPage"));
					put(
						"query#accountChannelBillingAddressId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelBillingAddressId"));
					put(
						"query#accountChannelCurrencyId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelCurrencyId"));
					put(
						"query#accountChannelDeliveryTermId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelDeliveryTermId"));
					put(
						"query#accountChannelDiscountId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelDiscountId"));
					put(
						"query#accountChannelPaymentMethodId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelPaymentMethodId"));
					put(
						"query#accountChannelPaymentTermId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelPaymentTermId"));
					put(
						"query#accountChannelPriceListId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelPriceListId"));
					put(
						"query#accountChannelShippingAddressId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelShippingAddressId"));
					put(
						"query#accountChannelUserId",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountChannelUserId"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelBillingAddresses",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelBillingAddressesPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelCurrencies",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelCurrenciesPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelDeliveryTerms",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelDeliveryTermsPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelDiscounts",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelDiscountsPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelPaymentMethods",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelPaymentMethodsPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelPaymentTerms",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelPaymentTermsPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelPriceLists",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelPriceListsPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelShippingAddresses",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelShippingAddressesPage"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelUsers",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelUsersPage"));
					put(
						"query#accountIdAccountChannelBillingAddresses",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelBillingAddressesPage"));
					put(
						"query#accountIdAccountChannelCurrencies",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelCurrenciesPage"));
					put(
						"query#accountIdAccountChannelDeliveryTerms",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelDeliveryTermsPage"));
					put(
						"query#accountIdAccountChannelDiscounts",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelDiscountsPage"));
					put(
						"query#accountIdAccountChannelPaymentMethods",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelPaymentMethodsPage"));
					put(
						"query#accountIdAccountChannelPaymentTerms",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelPaymentTermsPage"));
					put(
						"query#accountIdAccountChannelPriceLists",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelPriceListsPage"));
					put(
						"query#accountIdAccountChannelShippingAddresses",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelShippingAddressesPage"));
					put(
						"query#accountIdAccountChannelUsers",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountIdAccountChannelUsersPage"));
					put(
						"query#accountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"getAccountChannelShippingOption"));
					put(
						"query#accountByExternalReferenceCodeAccountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelShippingOptionPage"));
					put(
						"query#accountIdAccountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"getAccountIdAccountChannelShippingOptionPage"));
					put(
						"query#accountByExternalReferenceCodeAccountMembers",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountMembersPage"));
					put(
						"query#accountByExternalReferenceCodeAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountMember"));
					put(
						"query#accountIdAccountMembers",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"getAccountIdAccountMembersPage"));
					put(
						"query#accountIdAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"getAccountIdAccountMember"));
					put(
						"query#accountByExternalReferenceCodeAccountOrganizations",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountOrganizationsPage"));
					put(
						"query#accountByExternalReferenceCodeAccountOrganization",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountOrganization"));
					put(
						"query#accountIdAccountOrganizations",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"getAccountIdAccountOrganizationsPage"));
					put(
						"query#accountIdAccountOrganization",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"getAccountIdAccountOrganization"));
					put(
						"query#accountGroups",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"getAccountGroupsPage"));
					put(
						"query#accountGroupByExternalReferenceCode",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"getAccountGroupByExternalReferenceCode"));
					put(
						"query#accountGroup",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"getAccountGroup"));
					put(
						"query#accountByExternalReferenceCodeAccountGroups",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountGroupsPage"));
					put(
						"query#accountIdAccountGroups",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"getAccountIdAccountGroupsPage"));

					put(
						"query#Account.byExternalReferenceCodeAccountChannelShippingOption",
						new ObjectValuePair<>(
							AccountChannelShippingOptionResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelShippingOptionPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelPaymentTerms",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelPaymentTermsPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountMembers",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountMembersPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountAddresses",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountAddressesPage"));
					put(
						"query#AccountAddress.accountByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getAccountByExternalReferenceCode"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelShippingAddresses",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelShippingAddressesPage"));
					put(
						"query#Account.groupByExternalReferenceCode",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"getAccountGroupByExternalReferenceCode"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelBillingAddresses",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelBillingAddressesPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelUsers",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelUsersPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountMember",
						new ObjectValuePair<>(
							AccountMemberResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountMember"));
					put(
						"query#Account.byExternalReferenceCodeAccountOrganization",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountOrganization"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelPriceLists",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelPriceListsPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelCurrencies",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelCurrenciesPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountGroups",
						new ObjectValuePair<>(
							AdminAccountGroupResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountGroupsPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelPaymentMethods",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelPaymentMethodsPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountOrganizations",
						new ObjectValuePair<>(
							AccountOrganizationResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountOrganizationsPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelDiscounts",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelDiscountsPage"));
					put(
						"query#Account.byExternalReferenceCodeAccountChannelDeliveryTerms",
						new ObjectValuePair<>(
							AccountChannelEntryResourceImpl.class,
							"getAccountByExternalReferenceCodeAccountChannelDeliveryTermsPage"));
					put(
						"query#Account.addressByExternalReferenceCode",
						new ObjectValuePair<>(
							AccountAddressResourceImpl.class,
							"getAccountAddressByExternalReferenceCode"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountAddressResource>
		_accountAddressResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountChannelEntryResource>
		_accountChannelEntryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountChannelShippingOptionResource>
		_accountChannelShippingOptionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountMemberResource>
		_accountMemberResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountOrganizationResource>
		_accountOrganizationResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AdminAccountGroupResource>
		_adminAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<UserResource>
		_userResourceComponentServiceObjects;

}