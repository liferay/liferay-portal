/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.admin.channel.dto.v1_0.Account;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.AccountAddressChannel;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.CategoryDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ChannelAccount;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.DefaultCategoryDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.DefaultProductDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.OrderType;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelOrderType;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelTerm;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ProductDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionOrderType;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionTerm;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingMethod;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.Term;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.AccountAddressChannelResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.AccountResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.CategoryDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ChannelAccountResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.DefaultCategoryDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.DefaultProductDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.OrderTypeResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.PaymentMethodGroupRelOrderTypeResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.PaymentMethodGroupRelTermResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ProductDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ShippingFixedOptionOrderTypeResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ShippingFixedOptionTermResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ShippingMethodResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.TaxCategoryResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.TermResource;
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
 * @author Andrea Sbarra
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

	public static void setAccountAddressChannelResourceComponentServiceObjects(
		ComponentServiceObjects<AccountAddressChannelResource>
			accountAddressChannelResourceComponentServiceObjects) {

		_accountAddressChannelResourceComponentServiceObjects =
			accountAddressChannelResourceComponentServiceObjects;
	}

	public static void setCategoryDisplayPageResourceComponentServiceObjects(
		ComponentServiceObjects<CategoryDisplayPageResource>
			categoryDisplayPageResourceComponentServiceObjects) {

		_categoryDisplayPageResourceComponentServiceObjects =
			categoryDisplayPageResourceComponentServiceObjects;
	}

	public static void setChannelResourceComponentServiceObjects(
		ComponentServiceObjects<ChannelResource>
			channelResourceComponentServiceObjects) {

		_channelResourceComponentServiceObjects =
			channelResourceComponentServiceObjects;
	}

	public static void setChannelAccountResourceComponentServiceObjects(
		ComponentServiceObjects<ChannelAccountResource>
			channelAccountResourceComponentServiceObjects) {

		_channelAccountResourceComponentServiceObjects =
			channelAccountResourceComponentServiceObjects;
	}

	public static void
		setDefaultCategoryDisplayPageResourceComponentServiceObjects(
			ComponentServiceObjects<DefaultCategoryDisplayPageResource>
				defaultCategoryDisplayPageResourceComponentServiceObjects) {

		_defaultCategoryDisplayPageResourceComponentServiceObjects =
			defaultCategoryDisplayPageResourceComponentServiceObjects;
	}

	public static void
		setDefaultProductDisplayPageResourceComponentServiceObjects(
			ComponentServiceObjects<DefaultProductDisplayPageResource>
				defaultProductDisplayPageResourceComponentServiceObjects) {

		_defaultProductDisplayPageResourceComponentServiceObjects =
			defaultProductDisplayPageResourceComponentServiceObjects;
	}

	public static void setOrderTypeResourceComponentServiceObjects(
		ComponentServiceObjects<OrderTypeResource>
			orderTypeResourceComponentServiceObjects) {

		_orderTypeResourceComponentServiceObjects =
			orderTypeResourceComponentServiceObjects;
	}

	public static void
		setPaymentMethodGroupRelOrderTypeResourceComponentServiceObjects(
			ComponentServiceObjects<PaymentMethodGroupRelOrderTypeResource>
				paymentMethodGroupRelOrderTypeResourceComponentServiceObjects) {

		_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects =
			paymentMethodGroupRelOrderTypeResourceComponentServiceObjects;
	}

	public static void
		setPaymentMethodGroupRelTermResourceComponentServiceObjects(
			ComponentServiceObjects<PaymentMethodGroupRelTermResource>
				paymentMethodGroupRelTermResourceComponentServiceObjects) {

		_paymentMethodGroupRelTermResourceComponentServiceObjects =
			paymentMethodGroupRelTermResourceComponentServiceObjects;
	}

	public static void setProductDisplayPageResourceComponentServiceObjects(
		ComponentServiceObjects<ProductDisplayPageResource>
			productDisplayPageResourceComponentServiceObjects) {

		_productDisplayPageResourceComponentServiceObjects =
			productDisplayPageResourceComponentServiceObjects;
	}

	public static void
		setShippingFixedOptionOrderTypeResourceComponentServiceObjects(
			ComponentServiceObjects<ShippingFixedOptionOrderTypeResource>
				shippingFixedOptionOrderTypeResourceComponentServiceObjects) {

		_shippingFixedOptionOrderTypeResourceComponentServiceObjects =
			shippingFixedOptionOrderTypeResourceComponentServiceObjects;
	}

	public static void
		setShippingFixedOptionTermResourceComponentServiceObjects(
			ComponentServiceObjects<ShippingFixedOptionTermResource>
				shippingFixedOptionTermResourceComponentServiceObjects) {

		_shippingFixedOptionTermResourceComponentServiceObjects =
			shippingFixedOptionTermResourceComponentServiceObjects;
	}

	public static void setShippingMethodResourceComponentServiceObjects(
		ComponentServiceObjects<ShippingMethodResource>
			shippingMethodResourceComponentServiceObjects) {

		_shippingMethodResourceComponentServiceObjects =
			shippingMethodResourceComponentServiceObjects;
	}

	public static void setTaxCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<TaxCategoryResource>
			taxCategoryResourceComponentServiceObjects) {

		_taxCategoryResourceComponentServiceObjects =
			taxCategoryResourceComponentServiceObjects;
	}

	public static void setTermResourceComponentServiceObjects(
		ComponentServiceObjects<TermResource>
			termResourceComponentServiceObjects) {

		_termResourceComponentServiceObjects =
			termResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelAccountAccount(channelAccountId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Account channelAccountAccount(
			@GraphQLName("channelAccountId") Long channelAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.getChannelAccountAccount(
				channelAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAddressByExternalReferenceCodeAccountAddressChannels(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountAddressChannelPage
			accountAddressByExternalReferenceCodeAccountAddressChannels(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressChannelResource -> new AccountAddressChannelPage(
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAddressIdAccountAddressChannels(addressId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountAddressChannelPage accountAddressIdAccountAddressChannels(
			@GraphQLName("addressId") Long addressId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressChannelResource -> new AccountAddressChannelPage(
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, search,
						_filterBiFunction.apply(
							accountAddressChannelResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							accountAddressChannelResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {categoryDisplayPage(id: ___){actions, categoryExternalReferenceCode, categoryId, groupExternalReferenceCode, id, pageUuid}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CategoryDisplayPage categoryDisplayPage(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource ->
				categoryDisplayPageResource.getCategoryDisplayPage(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeCategoryDisplayPages(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CategoryDisplayPagePage
			channelByExternalReferenceCodeCategoryDisplayPages(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource -> new CategoryDisplayPagePage(
				categoryDisplayPageResource.
					getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							categoryDisplayPageResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							categoryDisplayPageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelIdCategoryDisplayPages(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CategoryDisplayPagePage channelIdCategoryDisplayPages(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource -> new CategoryDisplayPagePage(
				categoryDisplayPageResource.
					getChannelIdCategoryDisplayPagesPage(
						id, search,
						_filterBiFunction.apply(
							categoryDisplayPageResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							categoryDisplayPageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {accountAddressChannelChannel(accountAddressChannelId: ___){accountExternalReferenceCode, accountId, currencyCode, currencyExternalReferenceCode, currencyId, externalReferenceCode, id, name, siteGroupId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Channel accountAddressChannelChannel(
			@GraphQLName("accountAddressChannelId") Long
				accountAddressChannelId)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.getAccountAddressChannelChannel(
				accountAddressChannelId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channel(channelId: ___){accountExternalReferenceCode, accountId, currencyCode, currencyExternalReferenceCode, currencyId, externalReferenceCode, id, name, siteGroupId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrive information of the given Channel.")
	public Channel channel(@GraphQLName("channelId") Long channelId)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.getChannel(channelId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCode(externalReferenceCode: ___){accountExternalReferenceCode, accountId, currencyCode, currencyExternalReferenceCode, currencyId, externalReferenceCode, id, name, siteGroupId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrive information of the given Channel.")
	public Channel channelByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource ->
				channelResource.getChannelByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channels(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves channels.")
	public ChannelPage channels(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> new ChannelPage(
				channelResource.getChannelsPage(
					search,
					_filterBiFunction.apply(channelResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(channelResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelAccounts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ChannelAccountPage channelByExternalReferenceCodeChannelAccounts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelAccountResource -> new ChannelAccountPage(
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelIdChannelAccounts(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ChannelAccountPage channelIdChannelAccounts(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelAccountResource -> new ChannelAccountPage(
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, search,
					_filterBiFunction.apply(
						channelAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						channelAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeDefaultCategoryDisplayPage(externalReferenceCode: ___){actions, pageUuid}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DefaultCategoryDisplayPage
			channelByExternalReferenceCodeDefaultCategoryDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultCategoryDisplayPageResource ->
				defaultCategoryDisplayPageResource.
					getChannelByExternalReferenceCodeDefaultCategoryDisplayPage(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelIdDefaultCategoryDisplayPage(id: ___){actions, pageUuid}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DefaultCategoryDisplayPage channelIdDefaultCategoryDisplayPage(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultCategoryDisplayPageResource ->
				defaultCategoryDisplayPageResource.
					getChannelIdDefaultCategoryDisplayPage(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeDefaultProductDisplayPage(externalReferenceCode: ___){actions, pageUuid}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DefaultProductDisplayPage
			channelByExternalReferenceCodeDefaultProductDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultProductDisplayPageResource ->
				defaultProductDisplayPageResource.
					getChannelByExternalReferenceCodeDefaultProductDisplayPage(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelIdDefaultProductDisplayPage(id: ___){actions, pageUuid}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DefaultProductDisplayPage channelIdDefaultProductDisplayPage(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultProductDisplayPageResource ->
				defaultProductDisplayPageResource.
					getChannelIdDefaultProductDisplayPage(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {paymentMethodGroupRelOrderTypeOrderType(paymentMethodGroupRelOrderTypeId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OrderType paymentMethodGroupRelOrderTypeOrderType(
			@GraphQLName("paymentMethodGroupRelOrderTypeId") Long
				paymentMethodGroupRelOrderTypeId)
		throws Exception {

		return _applyComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			orderTypeResource ->
				orderTypeResource.getPaymentMethodGroupRelOrderTypeOrderType(
					paymentMethodGroupRelOrderTypeId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shippingFixedOptionOrderTypeOrderType(shippingFixedOptionOrderTypeId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OrderType shippingFixedOptionOrderTypeOrderType(
			@GraphQLName("shippingFixedOptionOrderTypeId") Long
				shippingFixedOptionOrderTypeId)
		throws Exception {

		return _applyComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			orderTypeResource ->
				orderTypeResource.getShippingFixedOptionOrderTypeOrderType(
					shippingFixedOptionOrderTypeId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {paymentMethodGroupRelIdPaymentMethodGroupRelOrderTypes(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PaymentMethodGroupRelOrderTypePage
			paymentMethodGroupRelIdPaymentMethodGroupRelOrderTypes(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelOrderTypeResource ->
				new PaymentMethodGroupRelOrderTypePage(
					paymentMethodGroupRelOrderTypeResource.
						getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
							id, search,
							_filterBiFunction.apply(
								paymentMethodGroupRelOrderTypeResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								paymentMethodGroupRelOrderTypeResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {paymentMethodGroupRelIdPaymentMethodGroupRelTerms(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PaymentMethodGroupRelTermPage
			paymentMethodGroupRelIdPaymentMethodGroupRelTerms(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodGroupRelTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelTermResource ->
				new PaymentMethodGroupRelTermPage(
					paymentMethodGroupRelTermResource.
						getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
							id, search,
							_filterBiFunction.apply(
								paymentMethodGroupRelTermResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								paymentMethodGroupRelTermResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeProductDisplayPages(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductDisplayPagePage
			channelByExternalReferenceCodeProductDisplayPages(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource -> new ProductDisplayPagePage(
				productDisplayPageResource.
					getChannelByExternalReferenceCodeProductDisplayPagesPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							productDisplayPageResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							productDisplayPageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelIdProductDisplayPages(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductDisplayPagePage channelIdProductDisplayPages(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource -> new ProductDisplayPagePage(
				productDisplayPageResource.getChannelIdProductDisplayPagesPage(
					id, search,
					_filterBiFunction.apply(
						productDisplayPageResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						productDisplayPageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productDisplayPage(id: ___){actions, id, pageTemplateUuid, pageUuid, productExternalReferenceCode, productId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductDisplayPage productDisplayPage(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource ->
				productDisplayPageResource.getProductDisplayPage(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shippingFixedOptionIdShippingFixedOptionOrderTypes(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShippingFixedOptionOrderTypePage
			shippingFixedOptionIdShippingFixedOptionOrderTypes(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingFixedOptionOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionOrderTypeResource ->
				new ShippingFixedOptionOrderTypePage(
					shippingFixedOptionOrderTypeResource.
						getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
							id, search,
							_filterBiFunction.apply(
								shippingFixedOptionOrderTypeResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								shippingFixedOptionOrderTypeResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shippingFixedOptionIdShippingFixedOptionTerms(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ShippingFixedOptionTermPage
			shippingFixedOptionIdShippingFixedOptionTerms(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingFixedOptionTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionTermResource -> new ShippingFixedOptionTermPage(
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, search,
						_filterBiFunction.apply(
							shippingFixedOptionTermResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							shippingFixedOptionTermResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelShippingMethods(channelId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves channel shipping methods.")
	public ShippingMethodPage channelShippingMethods(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingMethodResource -> new ShippingMethodPage(
				shippingMethodResource.getChannelShippingMethodsPage(
					channelId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxCategories(page: ___, pageSize: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxCategoryPage taxCategories(
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource -> new TaxCategoryPage(
				taxCategoryResource.getTaxCategoriesPage(
					search, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxCategory(id: ___){description, groupId, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxCategory taxCategory(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource -> taxCategoryResource.getTaxCategory(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {paymentMethodGroupRelTermTerm(paymentMethodGroupRelTermId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Term paymentMethodGroupRelTermTerm(
			@GraphQLName("paymentMethodGroupRelTermId") Long
				paymentMethodGroupRelTermId)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> termResource.getPaymentMethodGroupRelTermTerm(
				paymentMethodGroupRelTermId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {shippingFixedOptionTermTerm(shippingFixedOptionTermId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Term shippingFixedOptionTermTerm(
			@GraphQLName("shippingFixedOptionTermId") Long
				shippingFixedOptionTermId)
		throws Exception {

		return _applyComponentServiceObjects(
			_termResourceComponentServiceObjects,
			this::_populateResourceContext,
			termResource -> termResource.getShippingFixedOptionTermTerm(
				shippingFixedOptionTermId));
	}

	@GraphQLTypeExtension(Channel.class)
	public class
		GetAccountAddressByExternalReferenceCodeAccountAddressChannelsPageTypeExtension {

		public GetAccountAddressByExternalReferenceCodeAccountAddressChannelsPageTypeExtension(
			Channel channel) {

			_channel = channel;
		}

		@GraphQLField
		public AccountAddressChannelPage
				accountAddressByExternalReferenceCodeAccountAddressChannels(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_accountAddressChannelResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				accountAddressChannelResource -> new AccountAddressChannelPage(
					accountAddressChannelResource.
						getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
							_channel.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Channel _channel;

	}

	@GraphQLTypeExtension(Channel.class)
	public class GetChannelShippingMethodsPageTypeExtension {

		public GetChannelShippingMethodsPageTypeExtension(Channel channel) {
			_channel = channel;
		}

		@GraphQLField(description = "Retrieves channel shipping methods.")
		public ShippingMethodPage shippingMethods(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_shippingMethodResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				shippingMethodResource -> new ShippingMethodPage(
					shippingMethodResource.getChannelShippingMethodsPage(
						_channel.getId(), Pagination.of(page, pageSize))));
		}

		private Channel _channel;

	}

	@GraphQLTypeExtension(ChannelAccount.class)
	public class GetChannelTypeExtension {

		public GetChannelTypeExtension(ChannelAccount channelAccount) {
			_channelAccount = channelAccount;
		}

		@GraphQLField(description = "Retrive information of the given Channel.")
		public Channel channel() throws Exception {
			return _applyComponentServiceObjects(
				_channelResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				channelResource -> channelResource.getChannel(
					_channelAccount.getChannelId()));
		}

		private ChannelAccount _channelAccount;

	}

	@GraphQLTypeExtension(Channel.class)
	public class
		GetChannelByExternalReferenceCodeChannelAccountsPageTypeExtension {

		public GetChannelByExternalReferenceCodeChannelAccountsPageTypeExtension(
			Channel channel) {

			_channel = channel;
		}

		@GraphQLField
		public ChannelAccountPage byExternalReferenceCodeChannelAccounts(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_channelAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				channelAccountResource -> new ChannelAccountPage(
					channelAccountResource.
						getChannelByExternalReferenceCodeChannelAccountsPage(
							_channel.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Channel _channel;

	}

	@GraphQLTypeExtension(Channel.class)
	public class
		GetChannelByExternalReferenceCodeProductDisplayPagesPageTypeExtension {

		public GetChannelByExternalReferenceCodeProductDisplayPagesPageTypeExtension(
			Channel channel) {

			_channel = channel;
		}

		@GraphQLField
		public ProductDisplayPagePage
				byExternalReferenceCodeProductDisplayPages(
					@GraphQLName("search") String search,
					@GraphQLName("filter") String filterString,
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page,
					@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_productDisplayPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productDisplayPageResource -> new ProductDisplayPagePage(
					productDisplayPageResource.
						getChannelByExternalReferenceCodeProductDisplayPagesPage(
							_channel.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								productDisplayPageResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productDisplayPageResource, sortsString))));
		}

		private Channel _channel;

	}

	@GraphQLTypeExtension(Channel.class)
	public class
		GetChannelByExternalReferenceCodeDefaultCategoryDisplayPageTypeExtension {

		public GetChannelByExternalReferenceCodeDefaultCategoryDisplayPageTypeExtension(
			Channel channel) {

			_channel = channel;
		}

		@GraphQLField
		public DefaultCategoryDisplayPage
				byExternalReferenceCodeDefaultCategoryDisplayPage()
			throws Exception {

			return _applyComponentServiceObjects(
				_defaultCategoryDisplayPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				defaultCategoryDisplayPageResource ->
					defaultCategoryDisplayPageResource.
						getChannelByExternalReferenceCodeDefaultCategoryDisplayPage(
							_channel.getExternalReferenceCode()));
		}

		private Channel _channel;

	}

	@GraphQLTypeExtension(Channel.class)
	public class
		GetChannelByExternalReferenceCodeCategoryDisplayPagesPageTypeExtension {

		public GetChannelByExternalReferenceCodeCategoryDisplayPagesPageTypeExtension(
			Channel channel) {

			_channel = channel;
		}

		@GraphQLField
		public CategoryDisplayPagePage
				byExternalReferenceCodeCategoryDisplayPages(
					@GraphQLName("search") String search,
					@GraphQLName("filter") String filterString,
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page,
					@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_categoryDisplayPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				categoryDisplayPageResource -> new CategoryDisplayPagePage(
					categoryDisplayPageResource.
						getChannelByExternalReferenceCodeCategoryDisplayPagesPage(
							_channel.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								categoryDisplayPageResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								categoryDisplayPageResource, sortsString))));
		}

		private Channel _channel;

	}

	@GraphQLTypeExtension(Channel.class)
	public class
		GetChannelByExternalReferenceCodeDefaultProductDisplayPageTypeExtension {

		public GetChannelByExternalReferenceCodeDefaultProductDisplayPageTypeExtension(
			Channel channel) {

			_channel = channel;
		}

		@GraphQLField
		public DefaultProductDisplayPage
				byExternalReferenceCodeDefaultProductDisplayPage()
			throws Exception {

			return _applyComponentServiceObjects(
				_defaultProductDisplayPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				defaultProductDisplayPageResource ->
					defaultProductDisplayPageResource.
						getChannelByExternalReferenceCodeDefaultProductDisplayPage(
							_channel.getExternalReferenceCode()));
		}

		private Channel _channel;

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

	@GraphQLName("AccountAddressChannelPage")
	public class AccountAddressChannelPage {

		public AccountAddressChannelPage(Page accountAddressChannelPage) {
			actions = accountAddressChannelPage.getActions();

			items = accountAddressChannelPage.getItems();
			lastPage = accountAddressChannelPage.getLastPage();
			page = accountAddressChannelPage.getPage();
			pageSize = accountAddressChannelPage.getPageSize();
			totalCount = accountAddressChannelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AccountAddressChannel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CategoryDisplayPagePage")
	public class CategoryDisplayPagePage {

		public CategoryDisplayPagePage(Page categoryDisplayPagePage) {
			actions = categoryDisplayPagePage.getActions();

			items = categoryDisplayPagePage.getItems();
			lastPage = categoryDisplayPagePage.getLastPage();
			page = categoryDisplayPagePage.getPage();
			pageSize = categoryDisplayPagePage.getPageSize();
			totalCount = categoryDisplayPagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CategoryDisplayPage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ChannelPage")
	public class ChannelPage {

		public ChannelPage(Page channelPage) {
			actions = channelPage.getActions();

			items = channelPage.getItems();
			lastPage = channelPage.getLastPage();
			page = channelPage.getPage();
			pageSize = channelPage.getPageSize();
			totalCount = channelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Channel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ChannelAccountPage")
	public class ChannelAccountPage {

		public ChannelAccountPage(Page channelAccountPage) {
			actions = channelAccountPage.getActions();

			items = channelAccountPage.getItems();
			lastPage = channelAccountPage.getLastPage();
			page = channelAccountPage.getPage();
			pageSize = channelAccountPage.getPageSize();
			totalCount = channelAccountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ChannelAccount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DefaultCategoryDisplayPagePage")
	public class DefaultCategoryDisplayPagePage {

		public DefaultCategoryDisplayPagePage(
			Page defaultCategoryDisplayPagePage) {

			actions = defaultCategoryDisplayPagePage.getActions();

			items = defaultCategoryDisplayPagePage.getItems();
			lastPage = defaultCategoryDisplayPagePage.getLastPage();
			page = defaultCategoryDisplayPagePage.getPage();
			pageSize = defaultCategoryDisplayPagePage.getPageSize();
			totalCount = defaultCategoryDisplayPagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DefaultCategoryDisplayPage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DefaultProductDisplayPagePage")
	public class DefaultProductDisplayPagePage {

		public DefaultProductDisplayPagePage(
			Page defaultProductDisplayPagePage) {

			actions = defaultProductDisplayPagePage.getActions();

			items = defaultProductDisplayPagePage.getItems();
			lastPage = defaultProductDisplayPagePage.getLastPage();
			page = defaultProductDisplayPagePage.getPage();
			pageSize = defaultProductDisplayPagePage.getPageSize();
			totalCount = defaultProductDisplayPagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DefaultProductDisplayPage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("OrderTypePage")
	public class OrderTypePage {

		public OrderTypePage(Page orderTypePage) {
			actions = orderTypePage.getActions();

			items = orderTypePage.getItems();
			lastPage = orderTypePage.getLastPage();
			page = orderTypePage.getPage();
			pageSize = orderTypePage.getPageSize();
			totalCount = orderTypePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<OrderType> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PaymentMethodGroupRelOrderTypePage")
	public class PaymentMethodGroupRelOrderTypePage {

		public PaymentMethodGroupRelOrderTypePage(
			Page paymentMethodGroupRelOrderTypePage) {

			actions = paymentMethodGroupRelOrderTypePage.getActions();

			items = paymentMethodGroupRelOrderTypePage.getItems();
			lastPage = paymentMethodGroupRelOrderTypePage.getLastPage();
			page = paymentMethodGroupRelOrderTypePage.getPage();
			pageSize = paymentMethodGroupRelOrderTypePage.getPageSize();
			totalCount = paymentMethodGroupRelOrderTypePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PaymentMethodGroupRelOrderType> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PaymentMethodGroupRelTermPage")
	public class PaymentMethodGroupRelTermPage {

		public PaymentMethodGroupRelTermPage(
			Page paymentMethodGroupRelTermPage) {

			actions = paymentMethodGroupRelTermPage.getActions();

			items = paymentMethodGroupRelTermPage.getItems();
			lastPage = paymentMethodGroupRelTermPage.getLastPage();
			page = paymentMethodGroupRelTermPage.getPage();
			pageSize = paymentMethodGroupRelTermPage.getPageSize();
			totalCount = paymentMethodGroupRelTermPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PaymentMethodGroupRelTerm> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductDisplayPagePage")
	public class ProductDisplayPagePage {

		public ProductDisplayPagePage(Page productDisplayPagePage) {
			actions = productDisplayPagePage.getActions();

			items = productDisplayPagePage.getItems();
			lastPage = productDisplayPagePage.getLastPage();
			page = productDisplayPagePage.getPage();
			pageSize = productDisplayPagePage.getPageSize();
			totalCount = productDisplayPagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductDisplayPage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ShippingFixedOptionOrderTypePage")
	public class ShippingFixedOptionOrderTypePage {

		public ShippingFixedOptionOrderTypePage(
			Page shippingFixedOptionOrderTypePage) {

			actions = shippingFixedOptionOrderTypePage.getActions();

			items = shippingFixedOptionOrderTypePage.getItems();
			lastPage = shippingFixedOptionOrderTypePage.getLastPage();
			page = shippingFixedOptionOrderTypePage.getPage();
			pageSize = shippingFixedOptionOrderTypePage.getPageSize();
			totalCount = shippingFixedOptionOrderTypePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ShippingFixedOptionOrderType> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ShippingFixedOptionTermPage")
	public class ShippingFixedOptionTermPage {

		public ShippingFixedOptionTermPage(Page shippingFixedOptionTermPage) {
			actions = shippingFixedOptionTermPage.getActions();

			items = shippingFixedOptionTermPage.getItems();
			lastPage = shippingFixedOptionTermPage.getLastPage();
			page = shippingFixedOptionTermPage.getPage();
			pageSize = shippingFixedOptionTermPage.getPageSize();
			totalCount = shippingFixedOptionTermPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ShippingFixedOptionTerm> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ShippingMethodPage")
	public class ShippingMethodPage {

		public ShippingMethodPage(Page shippingMethodPage) {
			actions = shippingMethodPage.getActions();

			items = shippingMethodPage.getItems();
			lastPage = shippingMethodPage.getLastPage();
			page = shippingMethodPage.getPage();
			pageSize = shippingMethodPage.getPageSize();
			totalCount = shippingMethodPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ShippingMethod> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TaxCategoryPage")
	public class TaxCategoryPage {

		public TaxCategoryPage(Page taxCategoryPage) {
			actions = taxCategoryPage.getActions();

			items = taxCategoryPage.getItems();
			lastPage = taxCategoryPage.getLastPage();
			page = taxCategoryPage.getPage();
			pageSize = taxCategoryPage.getPageSize();
			totalCount = taxCategoryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TaxCategory> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TermPage")
	public class TermPage {

		public TermPage(Page termPage) {
			actions = termPage.getActions();

			items = termPage.getItems();
			lastPage = termPage.getLastPage();
			page = termPage.getPage();
			pageSize = termPage.getPageSize();
			totalCount = termPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Term> items;

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
			AccountAddressChannelResource accountAddressChannelResource)
		throws Exception {

		accountAddressChannelResource.setContextAcceptLanguage(_acceptLanguage);
		accountAddressChannelResource.setContextCompany(_company);
		accountAddressChannelResource.setContextHttpServletRequest(
			_httpServletRequest);
		accountAddressChannelResource.setContextHttpServletResponse(
			_httpServletResponse);
		accountAddressChannelResource.setContextUriInfo(_uriInfo);
		accountAddressChannelResource.setContextUser(_user);
		accountAddressChannelResource.setGroupLocalService(_groupLocalService);
		accountAddressChannelResource.setResourceActionLocalService(
			_resourceActionLocalService);
		accountAddressChannelResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		accountAddressChannelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			CategoryDisplayPageResource categoryDisplayPageResource)
		throws Exception {

		categoryDisplayPageResource.setContextAcceptLanguage(_acceptLanguage);
		categoryDisplayPageResource.setContextCompany(_company);
		categoryDisplayPageResource.setContextHttpServletRequest(
			_httpServletRequest);
		categoryDisplayPageResource.setContextHttpServletResponse(
			_httpServletResponse);
		categoryDisplayPageResource.setContextUriInfo(_uriInfo);
		categoryDisplayPageResource.setContextUser(_user);
		categoryDisplayPageResource.setGroupLocalService(_groupLocalService);
		categoryDisplayPageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		categoryDisplayPageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		categoryDisplayPageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(ChannelResource channelResource)
		throws Exception {

		channelResource.setContextAcceptLanguage(_acceptLanguage);
		channelResource.setContextCompany(_company);
		channelResource.setContextHttpServletRequest(_httpServletRequest);
		channelResource.setContextHttpServletResponse(_httpServletResponse);
		channelResource.setContextUriInfo(_uriInfo);
		channelResource.setContextUser(_user);
		channelResource.setGroupLocalService(_groupLocalService);
		channelResource.setResourceActionLocalService(
			_resourceActionLocalService);
		channelResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		channelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ChannelAccountResource channelAccountResource)
		throws Exception {

		channelAccountResource.setContextAcceptLanguage(_acceptLanguage);
		channelAccountResource.setContextCompany(_company);
		channelAccountResource.setContextHttpServletRequest(
			_httpServletRequest);
		channelAccountResource.setContextHttpServletResponse(
			_httpServletResponse);
		channelAccountResource.setContextUriInfo(_uriInfo);
		channelAccountResource.setContextUser(_user);
		channelAccountResource.setGroupLocalService(_groupLocalService);
		channelAccountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		channelAccountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		channelAccountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DefaultCategoryDisplayPageResource
				defaultCategoryDisplayPageResource)
		throws Exception {

		defaultCategoryDisplayPageResource.setContextAcceptLanguage(
			_acceptLanguage);
		defaultCategoryDisplayPageResource.setContextCompany(_company);
		defaultCategoryDisplayPageResource.setContextHttpServletRequest(
			_httpServletRequest);
		defaultCategoryDisplayPageResource.setContextHttpServletResponse(
			_httpServletResponse);
		defaultCategoryDisplayPageResource.setContextUriInfo(_uriInfo);
		defaultCategoryDisplayPageResource.setContextUser(_user);
		defaultCategoryDisplayPageResource.setGroupLocalService(
			_groupLocalService);
		defaultCategoryDisplayPageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		defaultCategoryDisplayPageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		defaultCategoryDisplayPageResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			DefaultProductDisplayPageResource defaultProductDisplayPageResource)
		throws Exception {

		defaultProductDisplayPageResource.setContextAcceptLanguage(
			_acceptLanguage);
		defaultProductDisplayPageResource.setContextCompany(_company);
		defaultProductDisplayPageResource.setContextHttpServletRequest(
			_httpServletRequest);
		defaultProductDisplayPageResource.setContextHttpServletResponse(
			_httpServletResponse);
		defaultProductDisplayPageResource.setContextUriInfo(_uriInfo);
		defaultProductDisplayPageResource.setContextUser(_user);
		defaultProductDisplayPageResource.setGroupLocalService(
			_groupLocalService);
		defaultProductDisplayPageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		defaultProductDisplayPageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		defaultProductDisplayPageResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(OrderTypeResource orderTypeResource)
		throws Exception {

		orderTypeResource.setContextAcceptLanguage(_acceptLanguage);
		orderTypeResource.setContextCompany(_company);
		orderTypeResource.setContextHttpServletRequest(_httpServletRequest);
		orderTypeResource.setContextHttpServletResponse(_httpServletResponse);
		orderTypeResource.setContextUriInfo(_uriInfo);
		orderTypeResource.setContextUser(_user);
		orderTypeResource.setGroupLocalService(_groupLocalService);
		orderTypeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		orderTypeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		orderTypeResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PaymentMethodGroupRelOrderTypeResource
				paymentMethodGroupRelOrderTypeResource)
		throws Exception {

		paymentMethodGroupRelOrderTypeResource.setContextAcceptLanguage(
			_acceptLanguage);
		paymentMethodGroupRelOrderTypeResource.setContextCompany(_company);
		paymentMethodGroupRelOrderTypeResource.setContextHttpServletRequest(
			_httpServletRequest);
		paymentMethodGroupRelOrderTypeResource.setContextHttpServletResponse(
			_httpServletResponse);
		paymentMethodGroupRelOrderTypeResource.setContextUriInfo(_uriInfo);
		paymentMethodGroupRelOrderTypeResource.setContextUser(_user);
		paymentMethodGroupRelOrderTypeResource.setGroupLocalService(
			_groupLocalService);
		paymentMethodGroupRelOrderTypeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		paymentMethodGroupRelOrderTypeResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		paymentMethodGroupRelOrderTypeResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			PaymentMethodGroupRelTermResource paymentMethodGroupRelTermResource)
		throws Exception {

		paymentMethodGroupRelTermResource.setContextAcceptLanguage(
			_acceptLanguage);
		paymentMethodGroupRelTermResource.setContextCompany(_company);
		paymentMethodGroupRelTermResource.setContextHttpServletRequest(
			_httpServletRequest);
		paymentMethodGroupRelTermResource.setContextHttpServletResponse(
			_httpServletResponse);
		paymentMethodGroupRelTermResource.setContextUriInfo(_uriInfo);
		paymentMethodGroupRelTermResource.setContextUser(_user);
		paymentMethodGroupRelTermResource.setGroupLocalService(
			_groupLocalService);
		paymentMethodGroupRelTermResource.setResourceActionLocalService(
			_resourceActionLocalService);
		paymentMethodGroupRelTermResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		paymentMethodGroupRelTermResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			ProductDisplayPageResource productDisplayPageResource)
		throws Exception {

		productDisplayPageResource.setContextAcceptLanguage(_acceptLanguage);
		productDisplayPageResource.setContextCompany(_company);
		productDisplayPageResource.setContextHttpServletRequest(
			_httpServletRequest);
		productDisplayPageResource.setContextHttpServletResponse(
			_httpServletResponse);
		productDisplayPageResource.setContextUriInfo(_uriInfo);
		productDisplayPageResource.setContextUser(_user);
		productDisplayPageResource.setGroupLocalService(_groupLocalService);
		productDisplayPageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productDisplayPageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productDisplayPageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ShippingFixedOptionOrderTypeResource
				shippingFixedOptionOrderTypeResource)
		throws Exception {

		shippingFixedOptionOrderTypeResource.setContextAcceptLanguage(
			_acceptLanguage);
		shippingFixedOptionOrderTypeResource.setContextCompany(_company);
		shippingFixedOptionOrderTypeResource.setContextHttpServletRequest(
			_httpServletRequest);
		shippingFixedOptionOrderTypeResource.setContextHttpServletResponse(
			_httpServletResponse);
		shippingFixedOptionOrderTypeResource.setContextUriInfo(_uriInfo);
		shippingFixedOptionOrderTypeResource.setContextUser(_user);
		shippingFixedOptionOrderTypeResource.setGroupLocalService(
			_groupLocalService);
		shippingFixedOptionOrderTypeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shippingFixedOptionOrderTypeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shippingFixedOptionOrderTypeResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			ShippingFixedOptionTermResource shippingFixedOptionTermResource)
		throws Exception {

		shippingFixedOptionTermResource.setContextAcceptLanguage(
			_acceptLanguage);
		shippingFixedOptionTermResource.setContextCompany(_company);
		shippingFixedOptionTermResource.setContextHttpServletRequest(
			_httpServletRequest);
		shippingFixedOptionTermResource.setContextHttpServletResponse(
			_httpServletResponse);
		shippingFixedOptionTermResource.setContextUriInfo(_uriInfo);
		shippingFixedOptionTermResource.setContextUser(_user);
		shippingFixedOptionTermResource.setGroupLocalService(
			_groupLocalService);
		shippingFixedOptionTermResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shippingFixedOptionTermResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shippingFixedOptionTermResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ShippingMethodResource shippingMethodResource)
		throws Exception {

		shippingMethodResource.setContextAcceptLanguage(_acceptLanguage);
		shippingMethodResource.setContextCompany(_company);
		shippingMethodResource.setContextHttpServletRequest(
			_httpServletRequest);
		shippingMethodResource.setContextHttpServletResponse(
			_httpServletResponse);
		shippingMethodResource.setContextUriInfo(_uriInfo);
		shippingMethodResource.setContextUser(_user);
		shippingMethodResource.setGroupLocalService(_groupLocalService);
		shippingMethodResource.setResourceActionLocalService(
			_resourceActionLocalService);
		shippingMethodResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		shippingMethodResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TaxCategoryResource taxCategoryResource)
		throws Exception {

		taxCategoryResource.setContextAcceptLanguage(_acceptLanguage);
		taxCategoryResource.setContextCompany(_company);
		taxCategoryResource.setContextHttpServletRequest(_httpServletRequest);
		taxCategoryResource.setContextHttpServletResponse(_httpServletResponse);
		taxCategoryResource.setContextUriInfo(_uriInfo);
		taxCategoryResource.setContextUser(_user);
		taxCategoryResource.setGroupLocalService(_groupLocalService);
		taxCategoryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		taxCategoryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		taxCategoryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(TermResource termResource)
		throws Exception {

		termResource.setContextAcceptLanguage(_acceptLanguage);
		termResource.setContextCompany(_company);
		termResource.setContextHttpServletRequest(_httpServletRequest);
		termResource.setContextHttpServletResponse(_httpServletResponse);
		termResource.setContextUriInfo(_uriInfo);
		termResource.setContextUser(_user);
		termResource.setGroupLocalService(_groupLocalService);
		termResource.setResourceActionLocalService(_resourceActionLocalService);
		termResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		termResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;
	private static ComponentServiceObjects<AccountAddressChannelResource>
		_accountAddressChannelResourceComponentServiceObjects;
	private static ComponentServiceObjects<CategoryDisplayPageResource>
		_categoryDisplayPageResourceComponentServiceObjects;
	private static ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;
	private static ComponentServiceObjects<ChannelAccountResource>
		_channelAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects<DefaultCategoryDisplayPageResource>
		_defaultCategoryDisplayPageResourceComponentServiceObjects;
	private static ComponentServiceObjects<DefaultProductDisplayPageResource>
		_defaultProductDisplayPageResourceComponentServiceObjects;
	private static ComponentServiceObjects<OrderTypeResource>
		_orderTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<PaymentMethodGroupRelOrderTypeResource>
			_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects<PaymentMethodGroupRelTermResource>
		_paymentMethodGroupRelTermResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductDisplayPageResource>
		_productDisplayPageResourceComponentServiceObjects;
	private static ComponentServiceObjects<ShippingFixedOptionOrderTypeResource>
		_shippingFixedOptionOrderTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects<ShippingFixedOptionTermResource>
		_shippingFixedOptionTermResourceComponentServiceObjects;
	private static ComponentServiceObjects<ShippingMethodResource>
		_shippingMethodResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxCategoryResource>
		_taxCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<TermResource>
		_termResourceComponentServiceObjects;

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