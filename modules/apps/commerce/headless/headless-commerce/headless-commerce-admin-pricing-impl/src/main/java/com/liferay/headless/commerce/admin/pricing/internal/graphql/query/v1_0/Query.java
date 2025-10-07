/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.admin.pricing.dto.v1_0.Discount;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.DiscountAccountGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.DiscountCategory;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.DiscountRule;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceList;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceListAccountGroup;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountCategoryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountProductResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountRuleResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceEntryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceListAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceListResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.TierPriceResource;
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

	public static void setDiscountResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountResource>
			discountResourceComponentServiceObjects) {

		_discountResourceComponentServiceObjects =
			discountResourceComponentServiceObjects;
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

	public static void setDiscountProductResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountProductResource>
			discountProductResourceComponentServiceObjects) {

		_discountProductResourceComponentServiceObjects =
			discountProductResourceComponentServiceObjects;
	}

	public static void setDiscountRuleResourceComponentServiceObjects(
		ComponentServiceObjects<DiscountRuleResource>
			discountRuleResourceComponentServiceObjects) {

		_discountRuleResourceComponentServiceObjects =
			discountRuleResourceComponentServiceObjects;
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

	public static void setPriceListAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<PriceListAccountGroupResource>
			priceListAccountGroupResourceComponentServiceObjects) {

		_priceListAccountGroupResourceComponentServiceObjects =
			priceListAccountGroupResourceComponentServiceObjects;
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discount(id: ___){active, couponCode, customFields, discountAccountGroups, discountCategories, discountProducts, discountRules, displayDate, expirationDate, externalReferenceCode, id, limitationTimes, limitationType, maximumDiscountAmount, neverExpire, numberOfUse, percentageLevel1, percentageLevel2, percentageLevel3, percentageLevel4, target, title, useCouponCode, usePercentage}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountByExternalReferenceCode(externalReferenceCode: ___){active, couponCode, customFields, discountAccountGroups, discountCategories, discountProducts, discountRules, displayDate, expirationDate, externalReferenceCode, id, limitationTimes, limitationType, maximumDiscountAmount, neverExpire, numberOfUse, percentageLevel1, percentageLevel2, percentageLevel3, percentageLevel4, target, title, useCouponCode, usePercentage}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discounts(page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountPage discounts(
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> new DiscountPage(
				discountResource.getDiscountsPage(
					Pagination.of(page, pageSize))));
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountAccountGroups(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountAccountGroupPage discountIdDiscountAccountGroups(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource -> new DiscountAccountGroupPage(
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id, Pagination.of(page, pageSize))));
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountCategories(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountCategoryPage discountIdDiscountCategories(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource -> new DiscountCategoryPage(
				discountCategoryResource.getDiscountIdDiscountCategoriesPage(
					id, Pagination.of(page, pageSize))));
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountProducts(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountProductPage discountIdDiscountProducts(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource -> new DiscountProductPage(
				discountProductResource.getDiscountIdDiscountProductsPage(
					id, Pagination.of(page, pageSize))));
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountIdDiscountRules(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DiscountRulePage discountIdDiscountRules(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource -> new DiscountRulePage(
				discountRuleResource.getDiscountIdDiscountRulesPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {discountRule(id: ___){discountId, id, type, typeSettings}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntry(id: ___){customFields, externalReferenceCode, hasTierPrice, id, price, priceListExternalReferenceCode, priceListId, promoPrice, sku, skuExternalReferenceCode, skuId, tierPrices}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceEntry priceEntry(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> priceEntryResource.getPriceEntry(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntryByExternalReferenceCode(externalReferenceCode: ___){customFields, externalReferenceCode, hasTierPrice, id, price, priceListExternalReferenceCode, priceListId, promoPrice, sku, skuExternalReferenceCode, skuId, tierPrices}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceEntries(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceEntryPage priceListByExternalReferenceCodePriceEntries(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> new PriceEntryPage(
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceEntries(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceEntryPage priceListIdPriceEntries(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> new PriceEntryPage(
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceList(id: ___){active, catalogId, currencyCode, customFields, displayDate, expirationDate, externalReferenceCode, id, name, neverExpire, priceEntries, priceListAccountGroups, priority}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCode(externalReferenceCode: ___){active, catalogId, currencyCode, customFields, displayDate, expirationDate, externalReferenceCode, id, name, neverExpire, priceEntries, priceListAccountGroups, priority}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceLists(filter: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListPage priceLists(
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
					_filterBiFunction.apply(priceListResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(priceListResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListByExternalReferenceCodePriceListAccountGroup(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListAccountGroupPage
			priceListByExternalReferenceCodePriceListAccountGroup(
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
					getPriceListByExternalReferenceCodePriceListAccountGroupPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceListIdPriceListAccountGroups(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PriceListAccountGroupPage priceListIdPriceListAccountGroups(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource -> new PriceListAccountGroupPage(
				priceListAccountGroupResource.
					getPriceListIdPriceListAccountGroupsPage(
						id, Pagination.of(page, pageSize))));
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {priceEntryIdTierPrices(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TierPricePage priceEntryIdTierPrices(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> new TierPricePage(
				tierPriceResource.getPriceEntryIdTierPricesPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {tierPrice(id: ___){customFields, externalReferenceCode, id, minimumQuantity, price, priceEntryExternalReferenceCode, priceEntryId, promoPrice}}"}' -u 'test@liferay.com:test'
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {tierPriceByExternalReferenceCode(externalReferenceCode: ___){customFields, externalReferenceCode, id, minimumQuantity, price, priceEntryExternalReferenceCode, priceEntryId, promoPrice}}"}' -u 'test@liferay.com:test'
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
		GetPriceListByExternalReferenceCodePriceListAccountGroupPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceListAccountGroupPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceListAccountGroupPage
				priceListByExternalReferenceCodePriceListAccountGroup(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceListAccountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceListAccountGroupResource -> new PriceListAccountGroupPage(
					priceListAccountGroupResource.
						getPriceListByExternalReferenceCodePriceListAccountGroupPage(
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

	@GraphQLTypeExtension(Discount.class)
	public class
		GetPriceListByExternalReferenceCodePriceEntriesPageTypeExtension {

		public GetPriceListByExternalReferenceCodePriceEntriesPageTypeExtension(
			Discount discount) {

			_discount = discount;
		}

		@GraphQLField
		public PriceEntryPage priceListByExternalReferenceCodePriceEntries(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_priceEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				priceEntryResource -> new PriceEntryPage(
					priceEntryResource.
						getPriceListByExternalReferenceCodePriceEntriesPage(
							_discount.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Discount _discount;

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

	private static ComponentServiceObjects<DiscountResource>
		_discountResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountAccountGroupResource>
		_discountAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountCategoryResource>
		_discountCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountProductResource>
		_discountProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiscountRuleResource>
		_discountRuleResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceEntryResource>
		_priceEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListResource>
		_priceListResourceComponentServiceObjects;
	private static ComponentServiceObjects<PriceListAccountGroupResource>
		_priceListAccountGroupResourceComponentServiceObjects;
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