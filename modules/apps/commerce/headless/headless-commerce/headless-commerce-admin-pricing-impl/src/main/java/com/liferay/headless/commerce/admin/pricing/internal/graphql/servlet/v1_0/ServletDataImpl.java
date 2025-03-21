/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.pricing.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.pricing.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.DiscountAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.DiscountCategoryResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.DiscountProductResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.DiscountResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.DiscountRuleResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.PriceEntryResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.PriceListAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.PriceListResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0.TierPriceResourceImpl;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountCategoryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountProductResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountRuleResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceEntryResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceListAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.PriceListResource;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.TierPriceResource;
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
 * @author Zoltán Takács
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setDiscountResourceComponentServiceObjects(
			_discountResourceComponentServiceObjects);
		Mutation.setDiscountAccountGroupResourceComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects);
		Mutation.setDiscountCategoryResourceComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects);
		Mutation.setDiscountProductResourceComponentServiceObjects(
			_discountProductResourceComponentServiceObjects);
		Mutation.setDiscountRuleResourceComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects);
		Mutation.setPriceEntryResourceComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects);
		Mutation.setPriceListResourceComponentServiceObjects(
			_priceListResourceComponentServiceObjects);
		Mutation.setPriceListAccountGroupResourceComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects);
		Mutation.setTierPriceResourceComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects);

		Query.setDiscountResourceComponentServiceObjects(
			_discountResourceComponentServiceObjects);
		Query.setDiscountAccountGroupResourceComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects);
		Query.setDiscountCategoryResourceComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects);
		Query.setDiscountProductResourceComponentServiceObjects(
			_discountProductResourceComponentServiceObjects);
		Query.setDiscountRuleResourceComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects);
		Query.setPriceEntryResourceComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects);
		Query.setPriceListResourceComponentServiceObjects(
			_priceListResourceComponentServiceObjects);
		Query.setPriceListAccountGroupResourceComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects);
		Query.setTierPriceResourceComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Pricing";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-pricing-graphql/v1_0";
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
						"mutation#createDiscountsPageExportBatch",
						new ObjectValuePair<>(
							DiscountResourceImpl.class,
							"postDiscountsPageExportBatch"));
					put(
						"mutation#createDiscount",
						new ObjectValuePair<>(
							DiscountResourceImpl.class, "postDiscount"));
					put(
						"mutation#createDiscountBatch",
						new ObjectValuePair<>(
							DiscountResourceImpl.class, "postDiscountBatch"));
					put(
						"mutation#deleteDiscountByExternalReferenceCode",
						new ObjectValuePair<>(
							DiscountResourceImpl.class,
							"deleteDiscountByExternalReferenceCode"));
					put(
						"mutation#patchDiscountByExternalReferenceCode",
						new ObjectValuePair<>(
							DiscountResourceImpl.class,
							"patchDiscountByExternalReferenceCode"));
					put(
						"mutation#updateDiscountByExternalReferenceCode",
						new ObjectValuePair<>(
							DiscountResourceImpl.class,
							"putDiscountByExternalReferenceCode"));
					put(
						"mutation#deleteDiscount",
						new ObjectValuePair<>(
							DiscountResourceImpl.class, "deleteDiscount"));
					put(
						"mutation#deleteDiscountBatch",
						new ObjectValuePair<>(
							DiscountResourceImpl.class, "deleteDiscountBatch"));
					put(
						"mutation#patchDiscount",
						new ObjectValuePair<>(
							DiscountResourceImpl.class, "patchDiscount"));
					put(
						"mutation#deleteDiscountAccountGroup",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"deleteDiscountAccountGroup"));
					put(
						"mutation#deleteDiscountAccountGroupBatch",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"deleteDiscountAccountGroupBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountAccountGroup",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountAccountGroup"));
					put(
						"mutation#createDiscountIdDiscountAccountGroup",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"postDiscountIdDiscountAccountGroup"));
					put(
						"mutation#createDiscountIdDiscountAccountGroupBatch",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"postDiscountIdDiscountAccountGroupBatch"));
					put(
						"mutation#deleteDiscountCategory",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"deleteDiscountCategory"));
					put(
						"mutation#deleteDiscountCategoryBatch",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"deleteDiscountCategoryBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountCategory",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountCategory"));
					put(
						"mutation#createDiscountIdDiscountCategory",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"postDiscountIdDiscountCategory"));
					put(
						"mutation#createDiscountIdDiscountCategoryBatch",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"postDiscountIdDiscountCategoryBatch"));
					put(
						"mutation#deleteDiscountProduct",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"deleteDiscountProduct"));
					put(
						"mutation#deleteDiscountProductBatch",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"deleteDiscountProductBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountProduct",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountProduct"));
					put(
						"mutation#createDiscountIdDiscountProduct",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"postDiscountIdDiscountProduct"));
					put(
						"mutation#createDiscountIdDiscountProductBatch",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"postDiscountIdDiscountProductBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountRule",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountRule"));
					put(
						"mutation#deleteDiscountRule",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"deleteDiscountRule"));
					put(
						"mutation#deleteDiscountRuleBatch",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"deleteDiscountRuleBatch"));
					put(
						"mutation#patchDiscountRule",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"patchDiscountRule"));
					put(
						"mutation#createDiscountIdDiscountRule",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"postDiscountIdDiscountRule"));
					put(
						"mutation#createDiscountIdDiscountRuleBatch",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"postDiscountIdDiscountRuleBatch"));
					put(
						"mutation#deletePriceEntryByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"deletePriceEntryByExternalReferenceCode"));
					put(
						"mutation#patchPriceEntryByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"patchPriceEntryByExternalReferenceCode"));
					put(
						"mutation#deletePriceEntry",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class, "deletePriceEntry"));
					put(
						"mutation#deletePriceEntryBatch",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"deletePriceEntryBatch"));
					put(
						"mutation#patchPriceEntry",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class, "patchPriceEntry"));
					put(
						"mutation#createPriceListByExternalReferenceCodePriceEntry",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"postPriceListByExternalReferenceCodePriceEntry"));
					put(
						"mutation#createPriceListIdPriceEntry",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"postPriceListIdPriceEntry"));
					put(
						"mutation#createPriceListIdPriceEntryBatch",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"postPriceListIdPriceEntryBatch"));
					put(
						"mutation#createPriceListsPageExportBatch",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"postPriceListsPageExportBatch"));
					put(
						"mutation#createPriceList",
						new ObjectValuePair<>(
							PriceListResourceImpl.class, "postPriceList"));
					put(
						"mutation#createPriceListBatch",
						new ObjectValuePair<>(
							PriceListResourceImpl.class, "postPriceListBatch"));
					put(
						"mutation#deletePriceListByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"deletePriceListByExternalReferenceCode"));
					put(
						"mutation#patchPriceListByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"patchPriceListByExternalReferenceCode"));
					put(
						"mutation#updatePriceListByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"putPriceListByExternalReferenceCode"));
					put(
						"mutation#deletePriceList",
						new ObjectValuePair<>(
							PriceListResourceImpl.class, "deletePriceList"));
					put(
						"mutation#deletePriceListBatch",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"deletePriceListBatch"));
					put(
						"mutation#patchPriceList",
						new ObjectValuePair<>(
							PriceListResourceImpl.class, "patchPriceList"));
					put(
						"mutation#deletePriceListAccountGroup",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"deletePriceListAccountGroup"));
					put(
						"mutation#deletePriceListAccountGroupBatch",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"deletePriceListAccountGroupBatch"));
					put(
						"mutation#createPriceListByExternalReferenceCodePriceListAccountGroup",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"postPriceListByExternalReferenceCodePriceListAccountGroup"));
					put(
						"mutation#createPriceListIdPriceListAccountGroup",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"postPriceListIdPriceListAccountGroup"));
					put(
						"mutation#createPriceListIdPriceListAccountGroupBatch",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"postPriceListIdPriceListAccountGroupBatch"));
					put(
						"mutation#createPriceEntryByExternalReferenceCodeTierPrice",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"postPriceEntryByExternalReferenceCodeTierPrice"));
					put(
						"mutation#createPriceEntryIdTierPrice",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"postPriceEntryIdTierPrice"));
					put(
						"mutation#createPriceEntryIdTierPriceBatch",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"postPriceEntryIdTierPriceBatch"));
					put(
						"mutation#deleteTierPriceByExternalReferenceCode",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"deleteTierPriceByExternalReferenceCode"));
					put(
						"mutation#patchTierPriceByExternalReferenceCode",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"patchTierPriceByExternalReferenceCode"));
					put(
						"mutation#deleteTierPrice",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class, "deleteTierPrice"));
					put(
						"mutation#deleteTierPriceBatch",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"deleteTierPriceBatch"));
					put(
						"mutation#patchTierPrice",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class, "patchTierPrice"));

					put(
						"query#discounts",
						new ObjectValuePair<>(
							DiscountResourceImpl.class, "getDiscountsPage"));
					put(
						"query#discountByExternalReferenceCode",
						new ObjectValuePair<>(
							DiscountResourceImpl.class,
							"getDiscountByExternalReferenceCode"));
					put(
						"query#discount",
						new ObjectValuePair<>(
							DiscountResourceImpl.class, "getDiscount"));
					put(
						"query#discountByExternalReferenceCodeDiscountAccountGroups",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountAccountGroupsPage"));
					put(
						"query#discountIdDiscountAccountGroups",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"getDiscountIdDiscountAccountGroupsPage"));
					put(
						"query#discountByExternalReferenceCodeDiscountCategories",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountCategoriesPage"));
					put(
						"query#discountIdDiscountCategories",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"getDiscountIdDiscountCategoriesPage"));
					put(
						"query#discountByExternalReferenceCodeDiscountProducts",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountProductsPage"));
					put(
						"query#discountIdDiscountProducts",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"getDiscountIdDiscountProductsPage"));
					put(
						"query#discountByExternalReferenceCodeDiscountRules",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountRulesPage"));
					put(
						"query#discountRule",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class, "getDiscountRule"));
					put(
						"query#discountIdDiscountRules",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"getDiscountIdDiscountRulesPage"));
					put(
						"query#priceEntryByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"getPriceEntryByExternalReferenceCode"));
					put(
						"query#priceEntry",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class, "getPriceEntry"));
					put(
						"query#priceListByExternalReferenceCodePriceEntries",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceEntriesPage"));
					put(
						"query#priceListIdPriceEntries",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"getPriceListIdPriceEntriesPage"));
					put(
						"query#priceLists",
						new ObjectValuePair<>(
							PriceListResourceImpl.class, "getPriceListsPage"));
					put(
						"query#priceListByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"getPriceListByExternalReferenceCode"));
					put(
						"query#priceList",
						new ObjectValuePair<>(
							PriceListResourceImpl.class, "getPriceList"));
					put(
						"query#priceListByExternalReferenceCodePriceListAccountGroup",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListAccountGroupPage"));
					put(
						"query#priceListIdPriceListAccountGroups",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"getPriceListIdPriceListAccountGroupsPage"));
					put(
						"query#priceEntryByExternalReferenceCodeTierPrices",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getPriceEntryByExternalReferenceCodeTierPricesPage"));
					put(
						"query#priceEntryIdTierPrices",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getPriceEntryIdTierPricesPage"));
					put(
						"query#tierPriceByExternalReferenceCode",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getTierPriceByExternalReferenceCode"));
					put(
						"query#tierPrice",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class, "getTierPrice"));

					put(
						"query#Discount.byExternalReferenceCodeDiscountAccountGroups",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountAccountGroupsPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountCategories",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountCategoriesPage"));
					put(
						"query#Discount.priceEntryByExternalReferenceCodeTierPrices",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getPriceEntryByExternalReferenceCodeTierPricesPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountProducts",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountProductsPage"));
					put(
						"query#Discount.tierPriceByExternalReferenceCode",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getTierPriceByExternalReferenceCode"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceListAccountGroup",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListAccountGroupPage"));
					put(
						"query#PriceEntry.discountByExternalReferenceCode",
						new ObjectValuePair<>(
							DiscountResourceImpl.class,
							"getDiscountByExternalReferenceCode"));
					put(
						"query#Discount.priceEntryByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"getPriceEntryByExternalReferenceCode"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceEntries",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceEntriesPage"));
					put(
						"query#Discount.priceListByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"getPriceListByExternalReferenceCode"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountRules",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountRulesPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountResource>
		_discountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountAccountGroupResource>
		_discountAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountCategoryResource>
		_discountCategoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountProductResource>
		_discountProductResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountRuleResource>
		_discountRuleResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceEntryResource>
		_priceEntryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListResource>
		_priceListResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListAccountGroupResource>
		_priceListAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TierPriceResource>
		_tierPriceResourceComponentServiceObjects;

}