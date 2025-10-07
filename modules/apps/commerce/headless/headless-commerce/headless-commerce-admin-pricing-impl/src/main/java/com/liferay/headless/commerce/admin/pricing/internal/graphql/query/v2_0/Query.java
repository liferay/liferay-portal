/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.graphql.query.v2_0;

import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Account;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Category;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Channel;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountAccount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountAccountGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountCategory;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountChannel;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountOrderType;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProductGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountRule;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountSku;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.OrderType;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceList;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListAccount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListAccountGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListChannel;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListDiscount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListOrderType;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierCategory;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProduct;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProductGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PricingAccountGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Product;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.ProductGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Sku;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.AccountResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.CategoryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.ChannelResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountAccountResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountCategoryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountChannelResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountOrderTypeResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountProductGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountProductResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountRuleResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountSkuResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.OrderTypeResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceEntryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceListAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceListAccountResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceListChannelResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceListDiscountResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceListOrderTypeResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceListResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceModifierCategoryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceModifierProductGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceModifierProductResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PriceModifierResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.PricingAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.ProductGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.ProductResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.SkuResource;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.TierPriceResource;
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
 * @author Zoltán Takács
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

	public static void setCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<CategoryResource>
			categoryResourceComponentServiceObjects) {

		_categoryResourceComponentServiceObjects =
			categoryResourceComponentServiceObjects;
	}

	public static void setChannelResourceComponentServiceObjects(
		ComponentServiceObjects<ChannelResource>
			channelResourceComponentServiceObjects) {

		_channelResourceComponentServiceObjects =
			channelResourceComponentServiceObjects;
	}

	public static void setDiscountResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountResource>
			discountResourceComponentServiceObjects) {

		_discountResourceComponentServiceObjects =
			discountResourceComponentServiceObjects;
	}

	public static void setDiscountAccountResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountAccountResource>
			discountAccountResourceComponentServiceObjects) {

		_discountAccountResourceComponentServiceObjects =
			discountAccountResourceComponentServiceObjects;
	}

	public static void setDiscountAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountAccountGroupResource>
			discountAccountGroupResourceComponentServiceObjects) {

		_discountAccountGroupResourceComponentServiceObjects =
			discountAccountGroupResourceComponentServiceObjects;
	}

	public static void setDiscountCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountCategoryResource>
			discountCategoryResourceComponentServiceObjects) {

		_discountCategoryResourceComponentServiceObjects =
			discountCategoryResourceComponentServiceObjects;
	}

	public static void setDiscountChannelResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountChannelResource>
			discountChannelResourceComponentServiceObjects) {

		_discountChannelResourceComponentServiceObjects =
			discountChannelResourceComponentServiceObjects;
	}

	public static void setDiscountOrderTypeResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountOrderTypeResource>
			discountOrderTypeResourceComponentServiceObjects) {

		_discountOrderTypeResourceComponentServiceObjects =
			discountOrderTypeResourceComponentServiceObjects;
	}

	public static void setDiscountProductResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountProductResource>
			discountProductResourceComponentServiceObjects) {

		_discountProductResourceComponentServiceObjects =
			discountProductResourceComponentServiceObjects;
	}

	public static void setDiscountProductGroupResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountProductGroupResource>
			discountProductGroupResourceComponentServiceObjects) {

		_discountProductGroupResourceComponentServiceObjects =
			discountProductGroupResourceComponentServiceObjects;
	}

	public static void setDiscountRuleResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountRuleResource>
			discountRuleResourceComponentServiceObjects) {

		_discountRuleResourceComponentServiceObjects =
			discountRuleResourceComponentServiceObjects;
	}

	public static void setDiscountSkuResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountSkuResource>
			discountSkuResourceComponentServiceObjects) {

		_discountSkuResourceComponentServiceObjects =
			discountSkuResourceComponentServiceObjects;
	}

	public static void setOrderTypeResourceComponentServiceObjects(
		ComponentServiceObjects<OrderTypeResource>
			orderTypeResourceComponentServiceObjects) {

		_orderTypeResourceComponentServiceObjects =
			orderTypeResourceComponentServiceObjects;
	}

	public static void setPriceEntryResourceComponentServiceObjects(
		ComponentServiceObjects<PriceEntryResource>
			priceEntryResourceComponentServiceObjects) {

		_priceEntryResourceComponentServiceObjects =
			priceEntryResourceComponentServiceObjects;
	}

	public static void setPriceListResourceComponentServiceObjects(
		ComponentServiceObjects<PriceListResource>
			priceListResourceComponentServiceObjects) {

		_priceListResourceComponentServiceObjects =
			priceListResourceComponentServiceObjects;
	}

	public static void setPriceListAccountResourceComponentServiceObjects(
		ComponentServiceObjects<PriceListAccountResource>
			priceListAccountResourceComponentServiceObjects) {

		_priceListAccountResourceComponentServiceObjects =
			priceListAccountResourceComponentServiceObjects;
	}

	public static void setPriceListAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<PriceListAccountGroupResource>
			priceListAccountGroupResourceComponentServiceObjects) {

		_priceListAccountGroupResourceComponentServiceObjects =
			priceListAccountGroupResourceComponentServiceObjects;
	}

	public static void setPriceListChannelResourceComponentServiceObjects(
		ComponentServiceObjects<PriceListChannelResource>
			priceListChannelResourceComponentServiceObjects) {

		_priceListChannelResourceComponentServiceObjects =
			priceListChannelResourceComponentServiceObjects;
	}

	public static void setPriceListDiscountResourceComponentServiceObjects(
		ComponentServiceObjects<PriceListDiscountResource>
			priceListDiscountResourceComponentServiceObjects) {

		_priceListDiscountResourceComponentServiceObjects =
			priceListDiscountResourceComponentServiceObjects;
	}

	public static void setPriceListOrderTypeResourceComponentServiceObjects(
		ComponentServiceObjects<PriceListOrderTypeResource>
			priceListOrderTypeResourceComponentServiceObjects) {

		_priceListOrderTypeResourceComponentServiceObjects =
			priceListOrderTypeResourceComponentServiceObjects;
	}

	public static void setPriceModifierResourceComponentServiceObjects(
		ComponentServiceObjects<PriceModifierResource>
			priceModifierResourceComponentServiceObjects) {

		_priceModifierResourceComponentServiceObjects =
			priceModifierResourceComponentServiceObjects;
	}

	public static void setPriceModifierCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<PriceModifierCategoryResource>
			priceModifierCategoryResourceComponentServiceObjects) {

		_priceModifierCategoryResourceComponentServiceObjects =
			priceModifierCategoryResourceComponentServiceObjects;
	}

	public static void setPriceModifierProductResourceComponentServiceObjects(
		ComponentServiceObjects<PriceModifierProductResource>
			priceModifierProductResourceComponentServiceObjects) {

		_priceModifierProductResourceComponentServiceObjects =
			priceModifierProductResourceComponentServiceObjects;
	}

	public static void
		setPriceModifierProductGroupResourceComponentServiceObjects(
			ComponentServiceObjects<PriceModifierProductGroupResource>
				priceModifierProductGroupResourceComponentServiceObjects) {

		_priceModifierProductGroupResourceComponentServiceObjects =
			priceModifierProductGroupResourceComponentServiceObjects;
	}

	public static void setPricingAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<PricingAccountGroupResource>
			pricingAccountGroupResourceComponentServiceObjects) {

		_pricingAccountGroupResourceComponentServiceObjects =
			pricingAccountGroupResourceComponentServiceObjects;
	}

	public static void setProductResourceComponentServiceObjects(
		ComponentServiceObjects<ProductResource>
			productResourceComponentServiceObjects) {

		_productResourceComponentServiceObjects =
			productResourceComponentServiceObjects;
	}

	public static void setProductGroupResourceComponentServiceObjects(
		ComponentServiceObjects<ProductGroupResource>
			productGroupResourceComponentServiceObjects) {

		_productGroupResourceComponentServiceObjects =
			productGroupResourceComponentServiceObjects;
	}

	public static void setSkuResourceComponentServiceObjects(
		ComponentServiceObjects<SkuResource>
			skuResourceComponentServiceObjects) {

		_skuResourceComponentServiceObjects =
			skuResourceComponentServiceObjects;
	}

	public static void setTierPriceResourceComponentServiceObjects(
		ComponentServiceObjects<TierPriceResource>
			tierPriceResourceComponentServiceObjects) {

		_tierPriceResourceComponentServiceObjects =
			tierPriceResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountAccountAccount(discountAccountId: ___){id, logoId, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Account discountAccountAccount(
			@GraphQLName("discountAccountId") Long discountAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.getDiscountAccountAccount(
				discountAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListAccountAccount(priceListAccountId: ___){id, logoId, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Account priceListAccountAccount(
			@GraphQLName("priceListAccountId") Long priceListAccountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountResource -> accountResource.getPriceListAccountAccount(
				priceListAccountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountCategoryCategory(discountCategoryId: ___){id, name, path, vocabulary}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Category discountCategoryCategory(
			@GraphQLName("discountCategoryId") Long discountCategoryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryResource -> categoryResource.getDiscountCategoryCategory(
				discountCategoryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierCategoryCategory(priceModifierCategoryId: ___){id, name, path, vocabulary}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Category priceModifierCategoryCategory(
			@GraphQLName("priceModifierCategoryId") Long
				priceModifierCategoryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryResource ->
				categoryResource.getPriceModifierCategoryCategory(
					priceModifierCategoryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountChannelChannel(discountChannelId: ___){currencyCode, externalReferenceCode, id, name, siteGroupId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Channel discountChannelChannel(
			@GraphQLName("discountChannelId") Long discountChannelId)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.getDiscountChannelChannel(
				discountChannelId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListChannelChannel(priceListChannelId: ___){currencyCode, externalReferenceCode, id, name, siteGroupId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Channel priceListChannelChannel(
			@GraphQLName("priceListChannelId") Long priceListChannelId)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.getPriceListChannelChannel(
				priceListChannelId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discount(id: ___){actions, active, amountFormatted, couponCode, customFields, discountAccountGroups, discountAccounts, discountCategories, discountChannels, discountOrderTypes, discountProductGroups, discountProducts, discountRules, displayDate, expirationDate, externalReferenceCode, id, level, limitationTimes, limitationTimesPerAccount, limitationType, maximumDiscountAmount, modifiedDate, neverExpire, numberOfUse, percentageLevel1, percentageLevel2, percentageLevel3, percentageLevel4, rulesConjunction, target, title, useCouponCode, usePercentage}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Discount discount(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> discountResource.getDiscount(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCode(externalReferenceCode: ___){actions, active, amountFormatted, couponCode, customFields, discountAccountGroups, discountAccounts, discountCategories, discountChannels, discountOrderTypes, discountProductGroups, discountProducts, discountRules, displayDate, expirationDate, externalReferenceCode, id, level, limitationTimes, limitationTimesPerAccount, limitationType, maximumDiscountAmount, modifiedDate, neverExpire, numberOfUse, percentageLevel1, percentageLevel2, percentageLevel3, percentageLevel4, rulesConjunction, target, title, useCouponCode, usePercentage}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Discount discountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource ->
				discountResource.getDiscountByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discounts(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountPage discounts(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> new DiscountPage(
				discountResource.getDiscountsPage(
					search,
					_filterBiFunction.apply(discountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(discountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountAccounts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountAccountPage discountByExternalReferenceCodeDiscountAccounts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountResource -> new DiscountAccountPage(
				discountAccountResource.
					getDiscountByExternalReferenceCodeDiscountAccountsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountAccounts(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountAccountPage discountIdDiscountAccounts(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountResource -> new DiscountAccountPage(
				discountAccountResource.getDiscountIdDiscountAccountsPage(
					id, search,
					_filterBiFunction.apply(
						discountAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						discountAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountAccountGroups(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountAccountGroupPage
			discountByExternalReferenceCodeDiscountAccountGroups(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource -> new DiscountAccountGroupPage(
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountAccountGroups(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountAccountGroupPage discountIdDiscountAccountGroups(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource -> new DiscountAccountGroupPage(
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id, search,
						_filterBiFunction.apply(
							discountAccountGroupResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							discountAccountGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountCategories(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountCategoryPage
			discountByExternalReferenceCodeDiscountCategories(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource -> new DiscountCategoryPage(
				discountCategoryResource.
					getDiscountByExternalReferenceCodeDiscountCategoriesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountCategories(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountCategoryPage discountIdDiscountCategories(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource -> new DiscountCategoryPage(
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, search,
					_filterBiFunction.apply(
						discountCategoryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						discountCategoryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountChannels(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountChannelPage discountByExternalReferenceCodeDiscountChannels(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountChannelResource -> new DiscountChannelPage(
				discountChannelResource.
					getDiscountByExternalReferenceCodeDiscountChannelsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountChannels(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountChannelPage discountIdDiscountChannels(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountChannelResource -> new DiscountChannelPage(
				discountChannelResource.getDiscountIdDiscountChannelsPage(
					id, search,
					_filterBiFunction.apply(
						discountChannelResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						discountChannelResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountOrderTypes(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountOrderTypePage
			discountByExternalReferenceCodeDiscountOrderTypes(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountOrderTypeResource -> new DiscountOrderTypePage(
				discountOrderTypeResource.
					getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountOrderTypes(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountOrderTypePage discountIdDiscountOrderTypes(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountOrderTypeResource -> new DiscountOrderTypePage(
				discountOrderTypeResource.getDiscountIdDiscountOrderTypesPage(
					id, search,
					_filterBiFunction.apply(
						discountOrderTypeResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						discountOrderTypeResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountProducts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountProductPage discountByExternalReferenceCodeDiscountProducts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource -> new DiscountProductPage(
				discountProductResource.
					getDiscountByExternalReferenceCodeDiscountProductsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountProducts(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountProductPage discountIdDiscountProducts(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource -> new DiscountProductPage(
				discountProductResource.getDiscountIdDiscountProductsPage(
					id, search,
					_filterBiFunction.apply(
						discountProductResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						discountProductResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountProductGroups(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountProductGroupPage
			discountByExternalReferenceCodeDiscountProductGroups(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductGroupResource -> new DiscountProductGroupPage(
				discountProductGroupResource.
					getDiscountByExternalReferenceCodeDiscountProductGroupsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountProductGroups(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountProductGroupPage discountIdDiscountProductGroups(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductGroupResource -> new DiscountProductGroupPage(
				discountProductGroupResource.
					getDiscountIdDiscountProductGroupsPage(
						id, search,
						_filterBiFunction.apply(
							discountProductGroupResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							discountProductGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountRules(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountRulePage discountByExternalReferenceCodeDiscountRules(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource -> new DiscountRulePage(
				discountRuleResource.
					getDiscountByExternalReferenceCodeDiscountRulesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountRules(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountRulePage discountIdDiscountRules(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource -> new DiscountRulePage(
				discountRuleResource.getDiscountIdDiscountRulesPage(
					id, search,
					_filterBiFunction.apply(discountRuleResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						discountRuleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountRule(id: ___){actions, discountId, id, name, type, typeSettings}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountRule discountRule(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource -> discountRuleResource.getDiscountRule(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCodeDiscountSkus(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountSkuPage discountByExternalReferenceCodeDiscountSkus(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountSkuResource -> new DiscountSkuPage(
				discountSkuResource.
					getDiscountByExternalReferenceCodeDiscountSkusPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountSkus(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountSkuPage discountIdDiscountSkus(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountSkuResource -> new DiscountSkuPage(
				discountSkuResource.getDiscountIdDiscountSkusPage(
					id, search,
					_filterBiFunction.apply(discountSkuResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(discountSkuResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountOrderTypeOrderType(discountOrderTypeId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OrderType discountOrderTypeOrderType(
			@GraphQLName("discountOrderTypeId") Long discountOrderTypeId)
		throws Exception {

		return _applyComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			orderTypeResource ->
				orderTypeResource.getDiscountOrderTypeOrderType(
					discountOrderTypeId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListOrderTypeOrderType(priceListOrderTypeId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OrderType priceListOrderTypeOrderType(
			@GraphQLName("priceListOrderTypeId") Long priceListOrderTypeId)
		throws Exception {

		return _applyComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			orderTypeResource ->
				orderTypeResource.getPriceListOrderTypeOrderType(
					priceListOrderTypeId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntry(priceEntryId: ___){actions, active, bulkPricing, customFields, discountDiscovery, discountLevel1, discountLevel2, discountLevel3, discountLevel4, discountLevelsFormatted, displayDate, expirationDate, externalReferenceCode, hasTierPrice, neverExpire, price, priceEntryId, priceFormatted, priceListExternalReferenceCode, priceListId, priceOnApplication, product, quantity, sku, skuExternalReferenceCode, skuId, tierPrices, unitOfMeasureKey}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceEntry priceEntry(@GraphQLName("priceEntryId") Long priceEntryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> priceEntryResource.getPriceEntry(
				priceEntryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntryByExternalReferenceCode(externalReferenceCode: ___){actions, active, bulkPricing, customFields, discountDiscovery, discountLevel1, discountLevel2, discountLevel3, discountLevel4, discountLevelsFormatted, displayDate, expirationDate, externalReferenceCode, hasTierPrice, neverExpire, price, priceEntryId, priceFormatted, priceListExternalReferenceCode, priceListId, priceOnApplication, product, quantity, sku, skuExternalReferenceCode, skuId, tierPrices, unitOfMeasureKey}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceEntry priceEntryByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource ->
				priceEntryResource.getPriceEntryByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceEntries(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceEntryPage priceListByExternalReferenceCodePriceEntries(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> new PriceEntryPage(
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(
							priceEntryResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							priceEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceEntries(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceEntryPage priceListIdPriceEntries(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> new PriceEntryPage(
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, search,
					_filterBiFunction.apply(priceEntryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(priceEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceList(id: ___){actions, active, author, catalogBasePriceList, catalogId, catalogName, createDate, currencyCode, currencyExternalReferenceCode, currencyId, customFields, displayDate, expirationDate, externalReferenceCode, id, name, netPrice, neverExpire, parentPriceListId, priceEntries, priceListAccountGroups, priceListAccounts, priceListChannels, priceListDiscounts, priceListOrderTypes, priceModifiers, priority, type, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceList priceList(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource -> priceListResource.getPriceList(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCode(externalReferenceCode: ___){actions, active, author, catalogBasePriceList, catalogId, catalogName, createDate, currencyCode, currencyExternalReferenceCode, currencyId, customFields, displayDate, expirationDate, externalReferenceCode, id, name, netPrice, neverExpire, parentPriceListId, priceEntries, priceListAccountGroups, priceListAccounts, priceListChannels, priceListDiscounts, priceListOrderTypes, priceModifiers, priority, type, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceList priceListByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource ->
				priceListResource.getPriceListByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceLists(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListPage priceLists(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource -> new PriceListPage(
				priceListResource.getPriceListsPage(
					search,
					_filterBiFunction.apply(priceListResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(priceListResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceListAccounts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListAccountPage
			priceListByExternalReferenceCodePriceListAccounts(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountResource -> new PriceListAccountPage(
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceListAccounts(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListAccountPage priceListIdPriceListAccounts(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountResource -> new PriceListAccountPage(
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, search,
					_filterBiFunction.apply(
						priceListAccountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						priceListAccountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceListAccountGroups(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListAccountGroupPage
			priceListByExternalReferenceCodePriceListAccountGroups(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource -> new PriceListAccountGroupPage(
				priceListAccountGroupResource.
					getPriceListByExternalReferenceCodePriceListAccountGroupsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceListAccountGroups(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListAccountGroupPage priceListIdPriceListAccountGroups(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource -> new PriceListAccountGroupPage(
				priceListAccountGroupResource.
					getPriceListIdPriceListAccountGroupsPage(
						id, search,
						_filterBiFunction.apply(
							priceListAccountGroupResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							priceListAccountGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceListChannels(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListChannelPage
			priceListByExternalReferenceCodePriceListChannels(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListChannelResource -> new PriceListChannelPage(
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceListChannels(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListChannelPage priceListIdPriceListChannels(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListChannelResource -> new PriceListChannelPage(
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, search,
					_filterBiFunction.apply(
						priceListChannelResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						priceListChannelResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceListDiscounts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListDiscountPage
			priceListByExternalReferenceCodePriceListDiscounts(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListDiscountResource -> new PriceListDiscountPage(
				priceListDiscountResource.
					getPriceListByExternalReferenceCodePriceListDiscountsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceListDiscounts(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListDiscountPage priceListIdPriceListDiscounts(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListDiscountResource -> new PriceListDiscountPage(
				priceListDiscountResource.getPriceListIdPriceListDiscountsPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceListOrderTypes(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListOrderTypePage
			priceListByExternalReferenceCodePriceListOrderTypes(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListOrderTypeResource -> new PriceListOrderTypePage(
				priceListOrderTypeResource.
					getPriceListByExternalReferenceCodePriceListOrderTypesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceListOrderTypes(id: ___, page: ___, pageSize: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListOrderTypePage priceListIdPriceListOrderTypes(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListOrderTypeResource -> new PriceListOrderTypePage(
				priceListOrderTypeResource.
					getPriceListIdPriceListOrderTypesPage(
						id, search, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceModifiers(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierPage priceListByExternalReferenceCodePriceModifiers(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource -> new PriceModifierPage(
				priceModifierResource.
					getPriceListByExternalReferenceCodePriceModifiersPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceModifiers(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierPage priceListIdPriceModifiers(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource -> new PriceModifierPage(
				priceModifierResource.getPriceListIdPriceModifiersPage(
					id, search,
					_filterBiFunction.apply(
						priceModifierResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						priceModifierResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifier(id: ___){actions, active, displayDate, expirationDate, externalReferenceCode, id, modifierAmount, modifierType, neverExpire, priceListExternalReferenceCode, priceListId, priceModifierCategories, priceModifierProductGroups, priceModifierProducts, priority, target, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifier priceModifier(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource -> priceModifierResource.getPriceModifier(
				id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierByExternalReferenceCode(externalReferenceCode: ___){actions, active, displayDate, expirationDate, externalReferenceCode, id, modifierAmount, modifierType, neverExpire, priceListExternalReferenceCode, priceListId, priceModifierCategories, priceModifierProductGroups, priceModifierProducts, priority, target, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifier priceModifierByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource ->
				priceModifierResource.getPriceModifierByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierByExternalReferenceCodePriceModifierCategories(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierCategoryPage
			priceModifierByExternalReferenceCodePriceModifierCategories(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierCategoryResource -> new PriceModifierCategoryPage(
				priceModifierCategoryResource.
					getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierIdPriceModifierCategories(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierCategoryPage priceModifierIdPriceModifierCategories(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierCategoryResource -> new PriceModifierCategoryPage(
				priceModifierCategoryResource.
					getPriceModifierIdPriceModifierCategoriesPage(
						id, search,
						_filterBiFunction.apply(
							priceModifierCategoryResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							priceModifierCategoryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierByExternalReferenceCodePriceModifierProducts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierProductPage
			priceModifierByExternalReferenceCodePriceModifierProducts(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductResource -> new PriceModifierProductPage(
				priceModifierProductResource.
					getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierIdPriceModifierProducts(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierProductPage priceModifierIdPriceModifierProducts(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductResource -> new PriceModifierProductPage(
				priceModifierProductResource.
					getPriceModifierIdPriceModifierProductsPage(
						id, search,
						_filterBiFunction.apply(
							priceModifierProductResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							priceModifierProductResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierByExternalReferenceCodePriceModifierProductGroups(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierProductGroupPage
			priceModifierByExternalReferenceCodePriceModifierProductGroups(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductGroupResource ->
				new PriceModifierProductGroupPage(
					priceModifierProductGroupResource.
						getPriceModifierByExternalReferenceCodePriceModifierProductGroupsPage(
							externalReferenceCode,
							Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierIdPriceModifierProductGroups(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceModifierProductGroupPage
			priceModifierIdPriceModifierProductGroups(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductGroupResource ->
				new PriceModifierProductGroupPage(
					priceModifierProductGroupResource.
						getPriceModifierIdPriceModifierProductGroupsPage(
							id, search,
							_filterBiFunction.apply(
								priceModifierProductGroupResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								priceModifierProductGroupResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountAccountGroupAccountGroup(discountAccountGroupId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PricingAccountGroup discountAccountGroupAccountGroup(
			@GraphQLName("discountAccountGroupId") Long discountAccountGroupId)
		throws Exception {

		return _applyComponentServiceObjects(
			_pricingAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			pricingAccountGroupResource ->
				pricingAccountGroupResource.getDiscountAccountGroupAccountGroup(
					discountAccountGroupId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListAccountGroupAccountGroup(priceListAccountGroupId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PricingAccountGroup priceListAccountGroupAccountGroup(
			@GraphQLName("priceListAccountGroupId") Long
				priceListAccountGroupId)
		throws Exception {

		return _applyComponentServiceObjects(
			_pricingAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			pricingAccountGroupResource ->
				pricingAccountGroupResource.
					getPriceListAccountGroupAccountGroup(
						priceListAccountGroupId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountProductProduct(discountProductId: ___){id, name, sku, thumbnail}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Product discountProductProduct(
			@GraphQLName("discountProductId") Long discountProductId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.getDiscountProductProduct(
				discountProductId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntryIdProduct(priceEntryId: ___){id, name, sku, thumbnail}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Product priceEntryIdProduct(
			@GraphQLName("priceEntryId") Long priceEntryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.getPriceEntryIdProduct(
				priceEntryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierProductProduct(priceModifierProductId: ___){id, name, sku, thumbnail}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Product priceModifierProductProduct(
			@GraphQLName("priceModifierProductId") Long priceModifierProductId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.getPriceModifierProductProduct(
				priceModifierProductId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountProductGroupProductGroup(discountProductGroupId: ___){id, productsCount, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductGroup discountProductGroupProductGroup(
			@GraphQLName("discountProductGroupId") Long discountProductGroupId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.getDiscountProductGroupProductGroup(
					discountProductGroupId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceModifierProductGroupProductGroup(priceModifierProductGroupId: ___){id, productsCount, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductGroup priceModifierProductGroupProductGroup(
			@GraphQLName("priceModifierProductGroupId") Long
				priceModifierProductGroupId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.getPriceModifierProductGroupProductGroup(
					priceModifierProductGroupId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountSkuSku(discountSkuId: ___){basePrice, basePriceFormatted, basePromoPrice, basePromoPriceFormatted, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Sku discountSkuSku(@GraphQLName("discountSkuId") Long discountSkuId)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.getDiscountSkuSku(discountSkuId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntryIdSku(priceEntryId: ___){basePrice, basePriceFormatted, basePromoPrice, basePromoPriceFormatted, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Sku priceEntryIdSku(@GraphQLName("priceEntryId") Long priceEntryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.getPriceEntryIdSku(priceEntryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntryByExternalReferenceCodeTierPrices(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TierPricePage priceEntryByExternalReferenceCodeTierPrices(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> new TierPricePage(
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntryIdTierPrices(page: ___, pageSize: ___, priceEntryId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TierPricePage priceEntryIdTierPrices(
			@GraphQLName("priceEntryId") Long priceEntryId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> new TierPricePage(
				tierPriceResource.getPriceEntryIdTierPricesPage(
					priceEntryId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {tierPrice(id: ___){actions, active, customFields, discountDiscovery, discountLevel1, discountLevel2, discountLevel3, discountLevel4, displayDate, expirationDate, externalReferenceCode, id, minimumQuantity, neverExpire, price, priceEntryExternalReferenceCode, priceEntryId, priceFormatted, unitOfMeasureKey}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TierPrice tierPrice(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> tierPriceResource.getTierPrice(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {tierPriceByExternalReferenceCode(externalReferenceCode: ___){actions, active, customFields, discountDiscovery, discountLevel1, discountLevel2, discountLevel3, discountLevel4, displayDate, expirationDate, externalReferenceCode, id, minimumQuantity, neverExpire, price, priceEntryExternalReferenceCode, priceEntryId, priceFormatted, unitOfMeasureKey}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TierPrice tierPriceByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource ->
				tierPriceResource.getTierPriceByExternalReferenceCode(
					externalReferenceCode));
	}

	@GraphQLTypeExtension(PriceEntry.class)
	public class GetPriceEntryIdProductTypeExtension {

		public GetPriceEntryIdProductTypeExtension(PriceEntry priceEntry) {
			_priceEntry = priceEntry;
		}

		@GraphQLField
		public Product idProduct() throws Exception {
			return _applyComponentServiceObjects(
				_productResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productResource -> productResource.getPriceEntryIdProduct(
					_priceEntry.getPriceEntryId()));
		}

		private PriceEntry _priceEntry;

	}

	@GraphQLTypeExtension(PriceEntry.class)
	public class GetPriceEntryIdTierPricesPageTypeExtension {

		public GetPriceEntryIdTierPricesPageTypeExtension(
			PriceEntry priceEntry) {

			_priceEntry = priceEntry;
		}

		@GraphQLField
		public TierPricePage idTierPrices(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_tierPriceResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				tierPriceResource -> new TierPricePage(
					tierPriceResource.getPriceEntryIdTierPricesPage(
						_priceEntry.getPriceEntryId(),
						Pagination.of(page, pageSize))));
		}

		private PriceEntry _priceEntry;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountCategoriesPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountCategoriesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountCategoryPage byExternalReferenceCodeDiscountCategories(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountCategoryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountCategoryResource -> new DiscountCategoryPage(
					discountCategoryResource.
						getDiscountByExternalReferenceCodeDiscountCategoriesPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountProductGroupsPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountProductGroupsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountProductGroupPage
				byExternalReferenceCodeDiscountProductGroups(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountProductGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountProductGroupResource -> new DiscountProductGroupPage(
					discountProductGroupResource.
						getDiscountByExternalReferenceCodeDiscountProductGroupsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(TierPrice.class)
	public class GetPriceEntryTypeExtension {

		public GetPriceEntryTypeExtension(TierPrice tierPrice) {
			_tierPrice = tierPrice;
		}

		@GraphQLField
		public PriceEntry priceEntry() throws Exception {
			return _applyComponentServiceObjects(
				_priceEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceEntryResource -> priceEntryResource.getPriceEntry(
					_tierPrice.getPriceEntryId()));
		}

		private TierPrice _tierPrice;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountProductsPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountProductsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountProductPage byExternalReferenceCodeDiscountProducts(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountProductResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountProductResource -> new DiscountProductPage(
					discountProductResource.
						getDiscountByExternalReferenceCodeDiscountProductsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceListAccountsPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceListAccountsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceListAccountPage
				priceListByExternalReferenceCodePriceListAccounts(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceListAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceListAccountResource -> new PriceListAccountPage(
					priceListAccountResource.
						getPriceListByExternalReferenceCodePriceListAccountsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(PriceEntry.class)
	public class GetDiscountByExternalReferenceCodeTypeExtension {

		public GetDiscountByExternalReferenceCodeTypeExtension(
			PriceEntry priceEntry) {

			_priceEntry = priceEntry;
		}

		@GraphQLField
		public Discount discountByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_discountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountResource ->
					discountResource.getDiscountByExternalReferenceCode(
						_priceEntry.getExternalReferenceCode()));
		}

		private PriceEntry _priceEntry;

	}

	@GraphQLTypeExtension(Discount.class)
	public class GetPriceListByExternalReferenceCodeTypeExtension {

		public GetPriceListByExternalReferenceCodeTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceList priceListByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_priceListResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceListResource ->
					priceListResource.getPriceListByExternalReferenceCode(
						_discount.getExternalReferenceCode()));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceListOrderTypesPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceListOrderTypesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceListOrderTypePage
				priceListByExternalReferenceCodePriceListOrderTypes(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceListOrderTypeResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceListOrderTypeResource -> new PriceListOrderTypePage(
					priceListOrderTypeResource.
						getPriceListByExternalReferenceCodePriceListOrderTypesPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountOrderTypesPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountOrderTypesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountOrderTypePage byExternalReferenceCodeDiscountOrderTypes(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountOrderTypeResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountOrderTypeResource -> new DiscountOrderTypePage(
					discountOrderTypeResource.
						getDiscountByExternalReferenceCodeDiscountOrderTypesPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class GetTierPriceByExternalReferenceCodeTypeExtension {

		public GetTierPriceByExternalReferenceCodeTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public TierPrice tierPriceByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_tierPriceResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				tierPriceResource ->
					tierPriceResource.getTierPriceByExternalReferenceCode(
						_discount.getExternalReferenceCode()));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceModifierByExternalReferenceCodePriceModifierProductsPageTypeExtension {

		public GetPriceModifierByExternalReferenceCodePriceModifierProductsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceModifierProductPage
				priceModifierByExternalReferenceCodePriceModifierProducts(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceModifierProductResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceModifierProductResource -> new PriceModifierProductPage(
					priceModifierProductResource.
						getPriceModifierByExternalReferenceCodePriceModifierProductsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class GetPriceEntryByExternalReferenceCodeTypeExtension {

		public GetPriceEntryByExternalReferenceCodeTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceEntry priceEntryByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_priceEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceEntryResource ->
					priceEntryResource.getPriceEntryByExternalReferenceCode(
						_discount.getExternalReferenceCode()));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(PriceEntry.class)
	public class GetPriceEntryIdSkuTypeExtension {

		public GetPriceEntryIdSkuTypeExtension(PriceEntry priceEntry) {
			_priceEntry = priceEntry;
		}

		@GraphQLField
		public Sku idSku() throws Exception {
			return _applyComponentServiceObjects(
				_skuResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				skuResource -> skuResource.getPriceEntryIdSku(
					_priceEntry.getPriceEntryId()));
		}

		private PriceEntry _priceEntry;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceModifierByExternalReferenceCodePriceModifierProductGroupsPageTypeExtension {

		public GetPriceModifierByExternalReferenceCodePriceModifierProductGroupsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceModifierProductGroupPage
				priceModifierByExternalReferenceCodePriceModifierProductGroups(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceModifierProductGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceModifierProductGroupResource ->
					new PriceModifierProductGroupPage(
						priceModifierProductGroupResource.
							getPriceModifierByExternalReferenceCodePriceModifierProductGroupsPage(
								_discount.getExternalReferenceCode(),
								Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountAccountGroupsPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountAccountGroupsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountAccountGroupPage
				byExternalReferenceCodeDiscountAccountGroups(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountAccountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountAccountGroupResource -> new DiscountAccountGroupPage(
					discountAccountGroupResource.
						getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceListAccountGroupsPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceListAccountGroupsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceListAccountGroupPage
				priceListByExternalReferenceCodePriceListAccountGroups(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceListAccountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceListAccountGroupResource -> new PriceListAccountGroupPage(
					priceListAccountGroupResource.
						getPriceListByExternalReferenceCodePriceListAccountGroupsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceEntryByExternalReferenceCodeTierPricesPageTypeExtension {

		public GetPriceEntryByExternalReferenceCodeTierPricesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public TierPricePage priceEntryByExternalReferenceCodeTierPrices(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_tierPriceResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				tierPriceResource -> new TierPricePage(
					tierPriceResource.
						getPriceEntryByExternalReferenceCodeTierPricesPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceModifierByExternalReferenceCodePriceModifierCategoriesPageTypeExtension {

		public GetPriceModifierByExternalReferenceCodePriceModifierCategoriesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceModifierCategoryPage
				priceModifierByExternalReferenceCodePriceModifierCategories(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceModifierCategoryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceModifierCategoryResource -> new PriceModifierCategoryPage(
					priceModifierCategoryResource.
						getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceModifiersPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceModifiersPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceModifierPage priceListByExternalReferenceCodePriceModifiers(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceModifierResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceModifierResource -> new PriceModifierPage(
					priceModifierResource.
						getPriceListByExternalReferenceCodePriceModifiersPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountChannelsPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountChannelsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountChannelPage byExternalReferenceCodeDiscountChannels(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountChannelResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountChannelResource -> new DiscountChannelPage(
					discountChannelResource.
						getDiscountByExternalReferenceCodeDiscountChannelsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceListDiscountsPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceListDiscountsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceListDiscountPage
				priceListByExternalReferenceCodePriceListDiscounts(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceListDiscountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceListDiscountResource -> new PriceListDiscountPage(
					priceListDiscountResource.
						getPriceListByExternalReferenceCodePriceListDiscountsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceListChannelsPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceListChannelsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceListChannelPage
				priceListByExternalReferenceCodePriceListChannels(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceListChannelResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceListChannelResource -> new PriceListChannelPage(
					priceListChannelResource.
						getPriceListByExternalReferenceCodePriceListChannelsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountSkusPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountSkusPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountSkuPage byExternalReferenceCodeDiscountSkus(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountSkuResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountSkuResource -> new DiscountSkuPage(
					discountSkuResource.
						getDiscountByExternalReferenceCodeDiscountSkusPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceEntriesPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceEntriesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceEntryPage priceListByExternalReferenceCodePriceEntries(
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceEntryResource -> new PriceEntryPage(
					priceEntryResource.
						getPriceListByExternalReferenceCodePriceEntriesPage(
							_discount.getExternalReferenceCode(), search,
							_filterBiFunction.apply(
								priceEntryResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								priceEntryResource, sortsString))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountRulesPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountRulesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountRulePage byExternalReferenceCodeDiscountRules(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountRuleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountRuleResource -> new DiscountRulePage(
					discountRuleResource.
						getDiscountByExternalReferenceCodeDiscountRulesPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class GetPriceModifierByExternalReferenceCodeTypeExtension {

		public GetPriceModifierByExternalReferenceCodeTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceModifier priceModifierByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_priceModifierResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceModifierResource ->
					priceModifierResource.
						getPriceModifierByExternalReferenceCode(
							_discount.getExternalReferenceCode()));
		}

		private Discount _discount;

	}

	@GraphQLTypeExtension(Discount.class)
	public class
		GetDiscountByExternalReferenceCodeDiscountAccountsPageTypeExtension {

		public GetDiscountByExternalReferenceCodeDiscountAccountsPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public DiscountAccountPage byExternalReferenceCodeDiscountAccounts(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_discountAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				discountAccountResource -> new DiscountAccountPage(
					discountAccountResource.
						getDiscountByExternalReferenceCodeDiscountAccountsPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

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

	@GraphQLName("CategoryPage")
	public class CategoryPage {

		public CategoryPage(Page categoryPage) {
			actions = categoryPage.getActions();

			items = categoryPage.getItems();
			lastPage = categoryPage.getLastPage();
			page = categoryPage.getPage();
			pageSize = categoryPage.getPageSize();
			totalCount = categoryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Category> items;

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

	@GraphQLName("DiscountPage")
	public class DiscountPage {

		public DiscountPage(Page discountPage) {
			actions = discountPage.getActions();

			items = discountPage.getItems();
			lastPage = discountPage.getLastPage();
			page = discountPage.getPage();
			pageSize = discountPage.getPageSize();
			totalCount = discountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Discount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountAccountPage")
	public class DiscountAccountPage {

		public DiscountAccountPage(Page discountAccountPage) {
			actions = discountAccountPage.getActions();

			items = discountAccountPage.getItems();
			lastPage = discountAccountPage.getLastPage();
			page = discountAccountPage.getPage();
			pageSize = discountAccountPage.getPageSize();
			totalCount = discountAccountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountAccount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountAccountGroupPage")
	public class DiscountAccountGroupPage {

		public DiscountAccountGroupPage(Page discountAccountGroupPage) {
			actions = discountAccountGroupPage.getActions();

			items = discountAccountGroupPage.getItems();
			lastPage = discountAccountGroupPage.getLastPage();
			page = discountAccountGroupPage.getPage();
			pageSize = discountAccountGroupPage.getPageSize();
			totalCount = discountAccountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountAccountGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountCategoryPage")
	public class DiscountCategoryPage {

		public DiscountCategoryPage(Page discountCategoryPage) {
			actions = discountCategoryPage.getActions();

			items = discountCategoryPage.getItems();
			lastPage = discountCategoryPage.getLastPage();
			page = discountCategoryPage.getPage();
			pageSize = discountCategoryPage.getPageSize();
			totalCount = discountCategoryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountCategory> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountChannelPage")
	public class DiscountChannelPage {

		public DiscountChannelPage(Page discountChannelPage) {
			actions = discountChannelPage.getActions();

			items = discountChannelPage.getItems();
			lastPage = discountChannelPage.getLastPage();
			page = discountChannelPage.getPage();
			pageSize = discountChannelPage.getPageSize();
			totalCount = discountChannelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountChannel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountOrderTypePage")
	public class DiscountOrderTypePage {

		public DiscountOrderTypePage(Page discountOrderTypePage) {
			actions = discountOrderTypePage.getActions();

			items = discountOrderTypePage.getItems();
			lastPage = discountOrderTypePage.getLastPage();
			page = discountOrderTypePage.getPage();
			pageSize = discountOrderTypePage.getPageSize();
			totalCount = discountOrderTypePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountOrderType> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountProductPage")
	public class DiscountProductPage {

		public DiscountProductPage(Page discountProductPage) {
			actions = discountProductPage.getActions();

			items = discountProductPage.getItems();
			lastPage = discountProductPage.getLastPage();
			page = discountProductPage.getPage();
			pageSize = discountProductPage.getPageSize();
			totalCount = discountProductPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountProduct> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountProductGroupPage")
	public class DiscountProductGroupPage {

		public DiscountProductGroupPage(Page discountProductGroupPage) {
			actions = discountProductGroupPage.getActions();

			items = discountProductGroupPage.getItems();
			lastPage = discountProductGroupPage.getLastPage();
			page = discountProductGroupPage.getPage();
			pageSize = discountProductGroupPage.getPageSize();
			totalCount = discountProductGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountProductGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountRulePage")
	public class DiscountRulePage {

		public DiscountRulePage(Page discountRulePage) {
			actions = discountRulePage.getActions();

			items = discountRulePage.getItems();
			lastPage = discountRulePage.getLastPage();
			page = discountRulePage.getPage();
			pageSize = discountRulePage.getPageSize();
			totalCount = discountRulePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountRule> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DiscountSkuPage")
	public class DiscountSkuPage {

		public DiscountSkuPage(Page discountSkuPage) {
			actions = discountSkuPage.getActions();

			items = discountSkuPage.getItems();
			lastPage = discountSkuPage.getLastPage();
			page = discountSkuPage.getPage();
			pageSize = discountSkuPage.getPageSize();
			totalCount = discountSkuPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DiscountSku> items;

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

	@GraphQLName("PriceEntryPage")
	public class PriceEntryPage {

		public PriceEntryPage(Page priceEntryPage) {
			actions = priceEntryPage.getActions();

			items = priceEntryPage.getItems();
			lastPage = priceEntryPage.getLastPage();
			page = priceEntryPage.getPage();
			pageSize = priceEntryPage.getPageSize();
			totalCount = priceEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceEntry> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceListPage")
	public class PriceListPage {

		public PriceListPage(Page priceListPage) {
			actions = priceListPage.getActions();

			items = priceListPage.getItems();
			lastPage = priceListPage.getLastPage();
			page = priceListPage.getPage();
			pageSize = priceListPage.getPageSize();
			totalCount = priceListPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceList> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceListAccountPage")
	public class PriceListAccountPage {

		public PriceListAccountPage(Page priceListAccountPage) {
			actions = priceListAccountPage.getActions();

			items = priceListAccountPage.getItems();
			lastPage = priceListAccountPage.getLastPage();
			page = priceListAccountPage.getPage();
			pageSize = priceListAccountPage.getPageSize();
			totalCount = priceListAccountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceListAccount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceListAccountGroupPage")
	public class PriceListAccountGroupPage {

		public PriceListAccountGroupPage(Page priceListAccountGroupPage) {
			actions = priceListAccountGroupPage.getActions();

			items = priceListAccountGroupPage.getItems();
			lastPage = priceListAccountGroupPage.getLastPage();
			page = priceListAccountGroupPage.getPage();
			pageSize = priceListAccountGroupPage.getPageSize();
			totalCount = priceListAccountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceListAccountGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceListChannelPage")
	public class PriceListChannelPage {

		public PriceListChannelPage(Page priceListChannelPage) {
			actions = priceListChannelPage.getActions();

			items = priceListChannelPage.getItems();
			lastPage = priceListChannelPage.getLastPage();
			page = priceListChannelPage.getPage();
			pageSize = priceListChannelPage.getPageSize();
			totalCount = priceListChannelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceListChannel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceListDiscountPage")
	public class PriceListDiscountPage {

		public PriceListDiscountPage(Page priceListDiscountPage) {
			actions = priceListDiscountPage.getActions();

			items = priceListDiscountPage.getItems();
			lastPage = priceListDiscountPage.getLastPage();
			page = priceListDiscountPage.getPage();
			pageSize = priceListDiscountPage.getPageSize();
			totalCount = priceListDiscountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceListDiscount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceListOrderTypePage")
	public class PriceListOrderTypePage {

		public PriceListOrderTypePage(Page priceListOrderTypePage) {
			actions = priceListOrderTypePage.getActions();

			items = priceListOrderTypePage.getItems();
			lastPage = priceListOrderTypePage.getLastPage();
			page = priceListOrderTypePage.getPage();
			pageSize = priceListOrderTypePage.getPageSize();
			totalCount = priceListOrderTypePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceListOrderType> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceModifierPage")
	public class PriceModifierPage {

		public PriceModifierPage(Page priceModifierPage) {
			actions = priceModifierPage.getActions();

			items = priceModifierPage.getItems();
			lastPage = priceModifierPage.getLastPage();
			page = priceModifierPage.getPage();
			pageSize = priceModifierPage.getPageSize();
			totalCount = priceModifierPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceModifier> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceModifierCategoryPage")
	public class PriceModifierCategoryPage {

		public PriceModifierCategoryPage(Page priceModifierCategoryPage) {
			actions = priceModifierCategoryPage.getActions();

			items = priceModifierCategoryPage.getItems();
			lastPage = priceModifierCategoryPage.getLastPage();
			page = priceModifierCategoryPage.getPage();
			pageSize = priceModifierCategoryPage.getPageSize();
			totalCount = priceModifierCategoryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceModifierCategory> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceModifierProductPage")
	public class PriceModifierProductPage {

		public PriceModifierProductPage(Page priceModifierProductPage) {
			actions = priceModifierProductPage.getActions();

			items = priceModifierProductPage.getItems();
			lastPage = priceModifierProductPage.getLastPage();
			page = priceModifierProductPage.getPage();
			pageSize = priceModifierProductPage.getPageSize();
			totalCount = priceModifierProductPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceModifierProduct> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PriceModifierProductGroupPage")
	public class PriceModifierProductGroupPage {

		public PriceModifierProductGroupPage(
			Page priceModifierProductGroupPage) {

			actions = priceModifierProductGroupPage.getActions();

			items = priceModifierProductGroupPage.getItems();
			lastPage = priceModifierProductGroupPage.getLastPage();
			page = priceModifierProductGroupPage.getPage();
			pageSize = priceModifierProductGroupPage.getPageSize();
			totalCount = priceModifierProductGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PriceModifierProductGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PricingAccountGroupPage")
	public class PricingAccountGroupPage {

		public PricingAccountGroupPage(Page pricingAccountGroupPage) {
			actions = pricingAccountGroupPage.getActions();

			items = pricingAccountGroupPage.getItems();
			lastPage = pricingAccountGroupPage.getLastPage();
			page = pricingAccountGroupPage.getPage();
			pageSize = pricingAccountGroupPage.getPageSize();
			totalCount = pricingAccountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<PricingAccountGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductPage")
	public class ProductPage {

		public ProductPage(Page productPage) {
			actions = productPage.getActions();

			items = productPage.getItems();
			lastPage = productPage.getLastPage();
			page = productPage.getPage();
			pageSize = productPage.getPageSize();
			totalCount = productPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Product> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductGroupPage")
	public class ProductGroupPage {

		public ProductGroupPage(Page productGroupPage) {
			actions = productGroupPage.getActions();

			items = productGroupPage.getItems();
			lastPage = productGroupPage.getLastPage();
			page = productGroupPage.getPage();
			pageSize = productGroupPage.getPageSize();
			totalCount = productGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SkuPage")
	public class SkuPage {

		public SkuPage(Page skuPage) {
			actions = skuPage.getActions();

			items = skuPage.getItems();
			lastPage = skuPage.getLastPage();
			page = skuPage.getPage();
			pageSize = skuPage.getPageSize();
			totalCount = skuPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Sku> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TierPricePage")
	public class TierPricePage {

		public TierPricePage(Page tierPricePage) {
			actions = tierPricePage.getActions();

			items = tierPricePage.getItems();
			lastPage = tierPricePage.getLastPage();
			page = tierPricePage.getPage();
			pageSize = tierPricePage.getPageSize();
			totalCount = tierPricePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TierPrice> items;

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

	private void _populateResourceContext(CategoryResource categoryResource)
		throws Exception {

		categoryResource.setContextAcceptLanguage(_acceptLanguage);
		categoryResource.setContextCompany(_company);
		categoryResource.setContextHttpServletRequest(_httpServletRequest);
		categoryResource.setContextHttpServletResponse(_httpServletResponse);
		categoryResource.setContextUriInfo(_uriInfo);
		categoryResource.setContextUser(_user);
		categoryResource.setGroupLocalService(_groupLocalService);
		categoryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		categoryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		categoryResource.setRoleLocalService(_roleLocalService);
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

	private void _populateResourceContext(DiscountResource discountResource)
		throws Exception {

		discountResource.setContextAcceptLanguage(_acceptLanguage);
		discountResource.setContextCompany(_company);
		discountResource.setContextHttpServletRequest(_httpServletRequest);
		discountResource.setContextHttpServletResponse(_httpServletResponse);
		discountResource.setContextUriInfo(_uriInfo);
		discountResource.setContextUser(_user);
		discountResource.setGroupLocalService(_groupLocalService);
		discountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountAccountResource discountAccountResource)
		throws Exception {

		discountAccountResource.setContextAcceptLanguage(_acceptLanguage);
		discountAccountResource.setContextCompany(_company);
		discountAccountResource.setContextHttpServletRequest(
			_httpServletRequest);
		discountAccountResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountAccountResource.setContextUriInfo(_uriInfo);
		discountAccountResource.setContextUser(_user);
		discountAccountResource.setGroupLocalService(_groupLocalService);
		discountAccountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountAccountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountAccountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountAccountGroupResource discountAccountGroupResource)
		throws Exception {

		discountAccountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		discountAccountGroupResource.setContextCompany(_company);
		discountAccountGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		discountAccountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountAccountGroupResource.setContextUriInfo(_uriInfo);
		discountAccountGroupResource.setContextUser(_user);
		discountAccountGroupResource.setGroupLocalService(_groupLocalService);
		discountAccountGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountAccountGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountAccountGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountCategoryResource discountCategoryResource)
		throws Exception {

		discountCategoryResource.setContextAcceptLanguage(_acceptLanguage);
		discountCategoryResource.setContextCompany(_company);
		discountCategoryResource.setContextHttpServletRequest(
			_httpServletRequest);
		discountCategoryResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountCategoryResource.setContextUriInfo(_uriInfo);
		discountCategoryResource.setContextUser(_user);
		discountCategoryResource.setGroupLocalService(_groupLocalService);
		discountCategoryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountCategoryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountCategoryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountChannelResource discountChannelResource)
		throws Exception {

		discountChannelResource.setContextAcceptLanguage(_acceptLanguage);
		discountChannelResource.setContextCompany(_company);
		discountChannelResource.setContextHttpServletRequest(
			_httpServletRequest);
		discountChannelResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountChannelResource.setContextUriInfo(_uriInfo);
		discountChannelResource.setContextUser(_user);
		discountChannelResource.setGroupLocalService(_groupLocalService);
		discountChannelResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountChannelResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountChannelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountOrderTypeResource discountOrderTypeResource)
		throws Exception {

		discountOrderTypeResource.setContextAcceptLanguage(_acceptLanguage);
		discountOrderTypeResource.setContextCompany(_company);
		discountOrderTypeResource.setContextHttpServletRequest(
			_httpServletRequest);
		discountOrderTypeResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountOrderTypeResource.setContextUriInfo(_uriInfo);
		discountOrderTypeResource.setContextUser(_user);
		discountOrderTypeResource.setGroupLocalService(_groupLocalService);
		discountOrderTypeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountOrderTypeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountOrderTypeResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountProductResource discountProductResource)
		throws Exception {

		discountProductResource.setContextAcceptLanguage(_acceptLanguage);
		discountProductResource.setContextCompany(_company);
		discountProductResource.setContextHttpServletRequest(
			_httpServletRequest);
		discountProductResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountProductResource.setContextUriInfo(_uriInfo);
		discountProductResource.setContextUser(_user);
		discountProductResource.setGroupLocalService(_groupLocalService);
		discountProductResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountProductResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountProductResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountProductGroupResource discountProductGroupResource)
		throws Exception {

		discountProductGroupResource.setContextAcceptLanguage(_acceptLanguage);
		discountProductGroupResource.setContextCompany(_company);
		discountProductGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		discountProductGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountProductGroupResource.setContextUriInfo(_uriInfo);
		discountProductGroupResource.setContextUser(_user);
		discountProductGroupResource.setGroupLocalService(_groupLocalService);
		discountProductGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountProductGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountProductGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountRuleResource discountRuleResource)
		throws Exception {

		discountRuleResource.setContextAcceptLanguage(_acceptLanguage);
		discountRuleResource.setContextCompany(_company);
		discountRuleResource.setContextHttpServletRequest(_httpServletRequest);
		discountRuleResource.setContextHttpServletResponse(
			_httpServletResponse);
		discountRuleResource.setContextUriInfo(_uriInfo);
		discountRuleResource.setContextUser(_user);
		discountRuleResource.setGroupLocalService(_groupLocalService);
		discountRuleResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountRuleResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountRuleResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DiscountSkuResource discountSkuResource)
		throws Exception {

		discountSkuResource.setContextAcceptLanguage(_acceptLanguage);
		discountSkuResource.setContextCompany(_company);
		discountSkuResource.setContextHttpServletRequest(_httpServletRequest);
		discountSkuResource.setContextHttpServletResponse(_httpServletResponse);
		discountSkuResource.setContextUriInfo(_uriInfo);
		discountSkuResource.setContextUser(_user);
		discountSkuResource.setGroupLocalService(_groupLocalService);
		discountSkuResource.setResourceActionLocalService(
			_resourceActionLocalService);
		discountSkuResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		discountSkuResource.setRoleLocalService(_roleLocalService);
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

	private void _populateResourceContext(PriceEntryResource priceEntryResource)
		throws Exception {

		priceEntryResource.setContextAcceptLanguage(_acceptLanguage);
		priceEntryResource.setContextCompany(_company);
		priceEntryResource.setContextHttpServletRequest(_httpServletRequest);
		priceEntryResource.setContextHttpServletResponse(_httpServletResponse);
		priceEntryResource.setContextUriInfo(_uriInfo);
		priceEntryResource.setContextUser(_user);
		priceEntryResource.setGroupLocalService(_groupLocalService);
		priceEntryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceEntryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceEntryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(PriceListResource priceListResource)
		throws Exception {

		priceListResource.setContextAcceptLanguage(_acceptLanguage);
		priceListResource.setContextCompany(_company);
		priceListResource.setContextHttpServletRequest(_httpServletRequest);
		priceListResource.setContextHttpServletResponse(_httpServletResponse);
		priceListResource.setContextUriInfo(_uriInfo);
		priceListResource.setContextUser(_user);
		priceListResource.setGroupLocalService(_groupLocalService);
		priceListResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceListResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceListResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceListAccountResource priceListAccountResource)
		throws Exception {

		priceListAccountResource.setContextAcceptLanguage(_acceptLanguage);
		priceListAccountResource.setContextCompany(_company);
		priceListAccountResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceListAccountResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceListAccountResource.setContextUriInfo(_uriInfo);
		priceListAccountResource.setContextUser(_user);
		priceListAccountResource.setGroupLocalService(_groupLocalService);
		priceListAccountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceListAccountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceListAccountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceListAccountGroupResource priceListAccountGroupResource)
		throws Exception {

		priceListAccountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		priceListAccountGroupResource.setContextCompany(_company);
		priceListAccountGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceListAccountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceListAccountGroupResource.setContextUriInfo(_uriInfo);
		priceListAccountGroupResource.setContextUser(_user);
		priceListAccountGroupResource.setGroupLocalService(_groupLocalService);
		priceListAccountGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceListAccountGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceListAccountGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceListChannelResource priceListChannelResource)
		throws Exception {

		priceListChannelResource.setContextAcceptLanguage(_acceptLanguage);
		priceListChannelResource.setContextCompany(_company);
		priceListChannelResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceListChannelResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceListChannelResource.setContextUriInfo(_uriInfo);
		priceListChannelResource.setContextUser(_user);
		priceListChannelResource.setGroupLocalService(_groupLocalService);
		priceListChannelResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceListChannelResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceListChannelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceListDiscountResource priceListDiscountResource)
		throws Exception {

		priceListDiscountResource.setContextAcceptLanguage(_acceptLanguage);
		priceListDiscountResource.setContextCompany(_company);
		priceListDiscountResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceListDiscountResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceListDiscountResource.setContextUriInfo(_uriInfo);
		priceListDiscountResource.setContextUser(_user);
		priceListDiscountResource.setGroupLocalService(_groupLocalService);
		priceListDiscountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceListDiscountResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceListDiscountResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceListOrderTypeResource priceListOrderTypeResource)
		throws Exception {

		priceListOrderTypeResource.setContextAcceptLanguage(_acceptLanguage);
		priceListOrderTypeResource.setContextCompany(_company);
		priceListOrderTypeResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceListOrderTypeResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceListOrderTypeResource.setContextUriInfo(_uriInfo);
		priceListOrderTypeResource.setContextUser(_user);
		priceListOrderTypeResource.setGroupLocalService(_groupLocalService);
		priceListOrderTypeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceListOrderTypeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceListOrderTypeResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceModifierResource priceModifierResource)
		throws Exception {

		priceModifierResource.setContextAcceptLanguage(_acceptLanguage);
		priceModifierResource.setContextCompany(_company);
		priceModifierResource.setContextHttpServletRequest(_httpServletRequest);
		priceModifierResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceModifierResource.setContextUriInfo(_uriInfo);
		priceModifierResource.setContextUser(_user);
		priceModifierResource.setGroupLocalService(_groupLocalService);
		priceModifierResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceModifierResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceModifierResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceModifierCategoryResource priceModifierCategoryResource)
		throws Exception {

		priceModifierCategoryResource.setContextAcceptLanguage(_acceptLanguage);
		priceModifierCategoryResource.setContextCompany(_company);
		priceModifierCategoryResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceModifierCategoryResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceModifierCategoryResource.setContextUriInfo(_uriInfo);
		priceModifierCategoryResource.setContextUser(_user);
		priceModifierCategoryResource.setGroupLocalService(_groupLocalService);
		priceModifierCategoryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceModifierCategoryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceModifierCategoryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceModifierProductResource priceModifierProductResource)
		throws Exception {

		priceModifierProductResource.setContextAcceptLanguage(_acceptLanguage);
		priceModifierProductResource.setContextCompany(_company);
		priceModifierProductResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceModifierProductResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceModifierProductResource.setContextUriInfo(_uriInfo);
		priceModifierProductResource.setContextUser(_user);
		priceModifierProductResource.setGroupLocalService(_groupLocalService);
		priceModifierProductResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceModifierProductResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceModifierProductResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			PriceModifierProductGroupResource priceModifierProductGroupResource)
		throws Exception {

		priceModifierProductGroupResource.setContextAcceptLanguage(
			_acceptLanguage);
		priceModifierProductGroupResource.setContextCompany(_company);
		priceModifierProductGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		priceModifierProductGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		priceModifierProductGroupResource.setContextUriInfo(_uriInfo);
		priceModifierProductGroupResource.setContextUser(_user);
		priceModifierProductGroupResource.setGroupLocalService(
			_groupLocalService);
		priceModifierProductGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		priceModifierProductGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		priceModifierProductGroupResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			PricingAccountGroupResource pricingAccountGroupResource)
		throws Exception {

		pricingAccountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		pricingAccountGroupResource.setContextCompany(_company);
		pricingAccountGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		pricingAccountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		pricingAccountGroupResource.setContextUriInfo(_uriInfo);
		pricingAccountGroupResource.setContextUser(_user);
		pricingAccountGroupResource.setGroupLocalService(_groupLocalService);
		pricingAccountGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		pricingAccountGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		pricingAccountGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(ProductResource productResource)
		throws Exception {

		productResource.setContextAcceptLanguage(_acceptLanguage);
		productResource.setContextCompany(_company);
		productResource.setContextHttpServletRequest(_httpServletRequest);
		productResource.setContextHttpServletResponse(_httpServletResponse);
		productResource.setContextUriInfo(_uriInfo);
		productResource.setContextUser(_user);
		productResource.setGroupLocalService(_groupLocalService);
		productResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductGroupResource productGroupResource)
		throws Exception {

		productGroupResource.setContextAcceptLanguage(_acceptLanguage);
		productGroupResource.setContextCompany(_company);
		productGroupResource.setContextHttpServletRequest(_httpServletRequest);
		productGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		productGroupResource.setContextUriInfo(_uriInfo);
		productGroupResource.setContextUser(_user);
		productGroupResource.setGroupLocalService(_groupLocalService);
		productGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SkuResource skuResource)
		throws Exception {

		skuResource.setContextAcceptLanguage(_acceptLanguage);
		skuResource.setContextCompany(_company);
		skuResource.setContextHttpServletRequest(_httpServletRequest);
		skuResource.setContextHttpServletResponse(_httpServletResponse);
		skuResource.setContextUriInfo(_uriInfo);
		skuResource.setContextUser(_user);
		skuResource.setGroupLocalService(_groupLocalService);
		skuResource.setResourceActionLocalService(_resourceActionLocalService);
		skuResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		skuResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(TierPriceResource tierPriceResource)
		throws Exception {

		tierPriceResource.setContextAcceptLanguage(_acceptLanguage);
		tierPriceResource.setContextCompany(_company);
		tierPriceResource.setContextHttpServletRequest(_httpServletRequest);
		tierPriceResource.setContextHttpServletResponse(_httpServletResponse);
		tierPriceResource.setContextUriInfo(_uriInfo);
		tierPriceResource.setContextUser(_user);
		tierPriceResource.setGroupLocalService(_groupLocalService);
		tierPriceResource.setResourceActionLocalService(
			_resourceActionLocalService);
		tierPriceResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		tierPriceResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;
	private static ComponentServiceObjects<CategoryResource>
		_categoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountResource>
		_discountResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountAccountResource>
		_discountAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountAccountGroupResource>
		_discountAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountCategoryResource>
		_discountCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountChannelResource>
		_discountChannelResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountOrderTypeResource>
		_discountOrderTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountProductResource>
		_discountProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountProductGroupResource>
		_discountProductGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountRuleResource>
		_discountRuleResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountSkuResource>
		_discountSkuResourceComponentServiceObjects;
	private static ComponentServiceObjects<OrderTypeResource>
		_orderTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceEntryResource>
		_priceEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListResource>
		_priceListResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListAccountResource>
		_priceListAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListAccountGroupResource>
		_priceListAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListChannelResource>
		_priceListChannelResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListDiscountResource>
		_priceListDiscountResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListOrderTypeResource>
		_priceListOrderTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceModifierResource>
		_priceModifierResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceModifierCategoryResource>
		_priceModifierCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceModifierProductResource>
		_priceModifierProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceModifierProductGroupResource>
		_priceModifierProductGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<PricingAccountGroupResource>
		_pricingAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductResource>
		_productResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductGroupResource>
		_productGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuResource>
		_skuResourceComponentServiceObjects;
	private static ComponentServiceObjects<TierPriceResource>
		_tierPriceResourceComponentServiceObjects;

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