/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.graphql.servlet.v2_0;

import com.liferay.headless.commerce.admin.pricing.internal.graphql.mutation.v2_0.Mutation;
import com.liferay.headless.commerce.admin.pricing.internal.graphql.query.v2_0.Query;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.AccountResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.CategoryResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.ChannelResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountAccountResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountCategoryResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountChannelResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountOrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountProductGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountProductResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountRuleResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.DiscountSkuResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.OrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceEntryResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceListAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceListAccountResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceListChannelResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceListDiscountResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceListOrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceListResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceModifierCategoryResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceModifierProductGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceModifierProductResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PriceModifierResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.PricingAccountGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.ProductGroupResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.ProductResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.SkuResourceImpl;
import com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0.TierPriceResourceImpl;
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
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

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
		Mutation.setDiscountAccountResourceComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects);
		Mutation.setDiscountAccountGroupResourceComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects);
		Mutation.setDiscountCategoryResourceComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects);
		Mutation.setDiscountChannelResourceComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects);
		Mutation.setDiscountOrderTypeResourceComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects);
		Mutation.setDiscountProductResourceComponentServiceObjects(
			_discountProductResourceComponentServiceObjects);
		Mutation.setDiscountProductGroupResourceComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects);
		Mutation.setDiscountRuleResourceComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects);
		Mutation.setDiscountSkuResourceComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects);
		Mutation.setPriceEntryResourceComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects);
		Mutation.setPriceListResourceComponentServiceObjects(
			_priceListResourceComponentServiceObjects);
		Mutation.setPriceListAccountResourceComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects);
		Mutation.setPriceListAccountGroupResourceComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects);
		Mutation.setPriceListChannelResourceComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects);
		Mutation.setPriceListDiscountResourceComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects);
		Mutation.setPriceListOrderTypeResourceComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects);
		Mutation.setPriceModifierResourceComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects);
		Mutation.setPriceModifierCategoryResourceComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects);
		Mutation.setPriceModifierProductResourceComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects);
		Mutation.setPriceModifierProductGroupResourceComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects);
		Mutation.setTierPriceResourceComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects);

		Query.setAccountResourceComponentServiceObjects(
			_accountResourceComponentServiceObjects);
		Query.setCategoryResourceComponentServiceObjects(
			_categoryResourceComponentServiceObjects);
		Query.setChannelResourceComponentServiceObjects(
			_channelResourceComponentServiceObjects);
		Query.setDiscountResourceComponentServiceObjects(
			_discountResourceComponentServiceObjects);
		Query.setDiscountAccountResourceComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects);
		Query.setDiscountAccountGroupResourceComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects);
		Query.setDiscountCategoryResourceComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects);
		Query.setDiscountChannelResourceComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects);
		Query.setDiscountOrderTypeResourceComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects);
		Query.setDiscountProductResourceComponentServiceObjects(
			_discountProductResourceComponentServiceObjects);
		Query.setDiscountProductGroupResourceComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects);
		Query.setDiscountRuleResourceComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects);
		Query.setDiscountSkuResourceComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects);
		Query.setOrderTypeResourceComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects);
		Query.setPriceEntryResourceComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects);
		Query.setPriceListResourceComponentServiceObjects(
			_priceListResourceComponentServiceObjects);
		Query.setPriceListAccountResourceComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects);
		Query.setPriceListAccountGroupResourceComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects);
		Query.setPriceListChannelResourceComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects);
		Query.setPriceListDiscountResourceComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects);
		Query.setPriceListOrderTypeResourceComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects);
		Query.setPriceModifierResourceComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects);
		Query.setPriceModifierCategoryResourceComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects);
		Query.setPriceModifierProductResourceComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects);
		Query.setPriceModifierProductGroupResourceComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects);
		Query.setPricingAccountGroupResourceComponentServiceObjects(
			_pricingAccountGroupResourceComponentServiceObjects);
		Query.setProductResourceComponentServiceObjects(
			_productResourceComponentServiceObjects);
		Query.setProductGroupResourceComponentServiceObjects(
			_productGroupResourceComponentServiceObjects);
		Query.setSkuResourceComponentServiceObjects(
			_skuResourceComponentServiceObjects);
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
		return "/headless-commerce-admin-pricing-graphql/v2_0";
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
						"mutation#deleteDiscountAccount",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"deleteDiscountAccount"));
					put(
						"mutation#deleteDiscountAccountBatch",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"deleteDiscountAccountBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountAccount",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountAccount"));
					put(
						"mutation#createDiscountIdDiscountAccount",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"postDiscountIdDiscountAccount"));
					put(
						"mutation#createDiscountIdDiscountAccountBatch",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"postDiscountIdDiscountAccountBatch"));
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
						"mutation#deleteDiscountChannel",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"deleteDiscountChannel"));
					put(
						"mutation#deleteDiscountChannelBatch",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"deleteDiscountChannelBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountChannel",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountChannel"));
					put(
						"mutation#createDiscountIdDiscountChannel",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"postDiscountIdDiscountChannel"));
					put(
						"mutation#createDiscountIdDiscountChannelBatch",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"postDiscountIdDiscountChannelBatch"));
					put(
						"mutation#deleteDiscountOrderType",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"deleteDiscountOrderType"));
					put(
						"mutation#deleteDiscountOrderTypeBatch",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"deleteDiscountOrderTypeBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountOrderType",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountOrderType"));
					put(
						"mutation#createDiscountIdDiscountOrderType",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"postDiscountIdDiscountOrderType"));
					put(
						"mutation#createDiscountIdDiscountOrderTypeBatch",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"postDiscountIdDiscountOrderTypeBatch"));
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
						"mutation#deleteDiscountProductGroup",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"deleteDiscountProductGroup"));
					put(
						"mutation#deleteDiscountProductGroupBatch",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"deleteDiscountProductGroupBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountProductGroup",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountProductGroup"));
					put(
						"mutation#createDiscountIdDiscountProductGroup",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"postDiscountIdDiscountProductGroup"));
					put(
						"mutation#createDiscountIdDiscountProductGroupBatch",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"postDiscountIdDiscountProductGroupBatch"));
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
						"mutation#createDiscountByExternalReferenceCodeDiscountRule",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountRule"));
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
						"mutation#deleteDiscountSku",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"deleteDiscountSku"));
					put(
						"mutation#deleteDiscountSkuBatch",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"deleteDiscountSkuBatch"));
					put(
						"mutation#createDiscountByExternalReferenceCodeDiscountSku",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"postDiscountByExternalReferenceCodeDiscountSku"));
					put(
						"mutation#createDiscountIdDiscountSku",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"postDiscountIdDiscountSku"));
					put(
						"mutation#createDiscountIdDiscountSkuBatch",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"postDiscountIdDiscountSkuBatch"));
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
						"mutation#deletePriceListAccount",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"deletePriceListAccount"));
					put(
						"mutation#deletePriceListAccountBatch",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"deletePriceListAccountBatch"));
					put(
						"mutation#createPriceListByExternalReferenceCodePriceListAccount",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"postPriceListByExternalReferenceCodePriceListAccount"));
					put(
						"mutation#createPriceListIdPriceListAccount",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"postPriceListIdPriceListAccount"));
					put(
						"mutation#createPriceListIdPriceListAccountBatch",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"postPriceListIdPriceListAccountBatch"));
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
						"mutation#deletePriceListChannel",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"deletePriceListChannel"));
					put(
						"mutation#deletePriceListChannelBatch",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"deletePriceListChannelBatch"));
					put(
						"mutation#createPriceListByExternalReferenceCodePriceListChannel",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"postPriceListByExternalReferenceCodePriceListChannel"));
					put(
						"mutation#createPriceListIdPriceListChannel",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"postPriceListIdPriceListChannel"));
					put(
						"mutation#createPriceListIdPriceListChannelBatch",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"postPriceListIdPriceListChannelBatch"));
					put(
						"mutation#deletePriceListDiscount",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"deletePriceListDiscount"));
					put(
						"mutation#deletePriceListDiscountBatch",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"deletePriceListDiscountBatch"));
					put(
						"mutation#createPriceListByExternalReferenceCodePriceListDiscount",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"postPriceListByExternalReferenceCodePriceListDiscount"));
					put(
						"mutation#createPriceListIdPriceListDiscount",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"postPriceListIdPriceListDiscount"));
					put(
						"mutation#createPriceListIdPriceListDiscountBatch",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"postPriceListIdPriceListDiscountBatch"));
					put(
						"mutation#deletePriceListOrderType",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"deletePriceListOrderType"));
					put(
						"mutation#deletePriceListOrderTypeBatch",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"deletePriceListOrderTypeBatch"));
					put(
						"mutation#createPriceListByExternalReferenceCodePriceListOrderType",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"postPriceListByExternalReferenceCodePriceListOrderType"));
					put(
						"mutation#createPriceListIdPriceListOrderType",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"postPriceListIdPriceListOrderType"));
					put(
						"mutation#createPriceListIdPriceListOrderTypeBatch",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"postPriceListIdPriceListOrderTypeBatch"));
					put(
						"mutation#createPriceListByExternalReferenceCodePriceModifier",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"postPriceListByExternalReferenceCodePriceModifier"));
					put(
						"mutation#createPriceListIdPriceModifier",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"postPriceListIdPriceModifier"));
					put(
						"mutation#createPriceListIdPriceModifierBatch",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"postPriceListIdPriceModifierBatch"));
					put(
						"mutation#deletePriceModifierByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"deletePriceModifierByExternalReferenceCode"));
					put(
						"mutation#patchPriceModifierByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"patchPriceModifierByExternalReferenceCode"));
					put(
						"mutation#deletePriceModifier",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"deletePriceModifier"));
					put(
						"mutation#deletePriceModifierBatch",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"deletePriceModifierBatch"));
					put(
						"mutation#patchPriceModifier",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"patchPriceModifier"));
					put(
						"mutation#deletePriceModifierCategory",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"deletePriceModifierCategory"));
					put(
						"mutation#deletePriceModifierCategoryBatch",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"deletePriceModifierCategoryBatch"));
					put(
						"mutation#createPriceModifierByExternalReferenceCodePriceModifierCategory",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"postPriceModifierByExternalReferenceCodePriceModifierCategory"));
					put(
						"mutation#createPriceModifierIdPriceModifierCategory",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"postPriceModifierIdPriceModifierCategory"));
					put(
						"mutation#createPriceModifierIdPriceModifierCategoryBatch",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"postPriceModifierIdPriceModifierCategoryBatch"));
					put(
						"mutation#deletePriceModifierProduct",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"deletePriceModifierProduct"));
					put(
						"mutation#deletePriceModifierProductBatch",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"deletePriceModifierProductBatch"));
					put(
						"mutation#createPriceModifierByExternalReferenceCodePriceModifierProduct",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"postPriceModifierByExternalReferenceCodePriceModifierProduct"));
					put(
						"mutation#createPriceModifierIdPriceModifierProduct",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"postPriceModifierIdPriceModifierProduct"));
					put(
						"mutation#createPriceModifierIdPriceModifierProductBatch",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"postPriceModifierIdPriceModifierProductBatch"));
					put(
						"mutation#deletePriceModifierProductGroup",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"deletePriceModifierProductGroup"));
					put(
						"mutation#deletePriceModifierProductGroupBatch",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"deletePriceModifierProductGroupBatch"));
					put(
						"mutation#createPriceModifierByExternalReferenceCodePriceModifierProductGroup",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"postPriceModifierByExternalReferenceCodePriceModifierProductGroup"));
					put(
						"mutation#createPriceModifierIdPriceModifierProductGroup",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"postPriceModifierIdPriceModifierProductGroup"));
					put(
						"mutation#createPriceModifierIdPriceModifierProductGroupBatch",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"postPriceModifierIdPriceModifierProductGroupBatch"));
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
						"query#discountAccountAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getDiscountAccountAccount"));
					put(
						"query#priceListAccountAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getPriceListAccountAccount"));
					put(
						"query#discountCategoryCategory",
						new ObjectValuePair<>(
							CategoryResourceImpl.class,
							"getDiscountCategoryCategory"));
					put(
						"query#priceModifierCategoryCategory",
						new ObjectValuePair<>(
							CategoryResourceImpl.class,
							"getPriceModifierCategoryCategory"));
					put(
						"query#discountChannelChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getDiscountChannelChannel"));
					put(
						"query#priceListChannelChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getPriceListChannelChannel"));
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
						"query#discountByExternalReferenceCodeDiscountAccounts",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountAccountsPage"));
					put(
						"query#discountIdDiscountAccounts",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"getDiscountIdDiscountAccountsPage"));
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
						"query#discountByExternalReferenceCodeDiscountChannels",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountChannelsPage"));
					put(
						"query#discountIdDiscountChannels",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"getDiscountIdDiscountChannelsPage"));
					put(
						"query#discountByExternalReferenceCodeDiscountOrderTypes",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountOrderTypesPage"));
					put(
						"query#discountIdDiscountOrderTypes",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"getDiscountIdDiscountOrderTypesPage"));
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
						"query#discountByExternalReferenceCodeDiscountProductGroups",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountProductGroupsPage"));
					put(
						"query#discountIdDiscountProductGroups",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"getDiscountIdDiscountProductGroupsPage"));
					put(
						"query#discountRule",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class, "getDiscountRule"));
					put(
						"query#discountByExternalReferenceCodeDiscountRules",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountRulesPage"));
					put(
						"query#discountIdDiscountRules",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"getDiscountIdDiscountRulesPage"));
					put(
						"query#discountByExternalReferenceCodeDiscountSkus",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountSkusPage"));
					put(
						"query#discountIdDiscountSkus",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"getDiscountIdDiscountSkusPage"));
					put(
						"query#discountOrderTypeOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getDiscountOrderTypeOrderType"));
					put(
						"query#priceListOrderTypeOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getPriceListOrderTypeOrderType"));
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
						"query#priceListByExternalReferenceCodePriceListAccounts",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListAccountsPage"));
					put(
						"query#priceListIdPriceListAccounts",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"getPriceListIdPriceListAccountsPage"));
					put(
						"query#priceListByExternalReferenceCodePriceListAccountGroups",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListAccountGroupsPage"));
					put(
						"query#priceListIdPriceListAccountGroups",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"getPriceListIdPriceListAccountGroupsPage"));
					put(
						"query#priceListByExternalReferenceCodePriceListChannels",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListChannelsPage"));
					put(
						"query#priceListIdPriceListChannels",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"getPriceListIdPriceListChannelsPage"));
					put(
						"query#priceListByExternalReferenceCodePriceListDiscounts",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListDiscountsPage"));
					put(
						"query#priceListIdPriceListDiscounts",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"getPriceListIdPriceListDiscountsPage"));
					put(
						"query#priceListByExternalReferenceCodePriceListOrderTypes",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListOrderTypesPage"));
					put(
						"query#priceListIdPriceListOrderTypes",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"getPriceListIdPriceListOrderTypesPage"));
					put(
						"query#priceListByExternalReferenceCodePriceModifiers",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceModifiersPage"));
					put(
						"query#priceListIdPriceModifiers",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"getPriceListIdPriceModifiersPage"));
					put(
						"query#priceModifierByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"getPriceModifierByExternalReferenceCode"));
					put(
						"query#priceModifier",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"getPriceModifier"));
					put(
						"query#priceModifierByExternalReferenceCodePriceModifierCategories",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage"));
					put(
						"query#priceModifierIdPriceModifierCategories",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"getPriceModifierIdPriceModifierCategoriesPage"));
					put(
						"query#priceModifierByExternalReferenceCodePriceModifierProducts",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"getPriceModifierByExternalReferenceCodePriceModifierProductsPage"));
					put(
						"query#priceModifierIdPriceModifierProducts",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"getPriceModifierIdPriceModifierProductsPage"));
					put(
						"query#priceModifierByExternalReferenceCodePriceModifierProductGroups",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"getPriceModifierByExternalReferenceCodePriceModifierProductGroupsPage"));
					put(
						"query#priceModifierIdPriceModifierProductGroups",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"getPriceModifierIdPriceModifierProductGroupsPage"));
					put(
						"query#discountAccountGroupAccountGroup",
						new ObjectValuePair<>(
							PricingAccountGroupResourceImpl.class,
							"getDiscountAccountGroupAccountGroup"));
					put(
						"query#priceListAccountGroupAccountGroup",
						new ObjectValuePair<>(
							PricingAccountGroupResourceImpl.class,
							"getPriceListAccountGroupAccountGroup"));
					put(
						"query#discountProductProduct",
						new ObjectValuePair<>(
							ProductResourceImpl.class,
							"getDiscountProductProduct"));
					put(
						"query#priceEntryIdProduct",
						new ObjectValuePair<>(
							ProductResourceImpl.class,
							"getPriceEntryIdProduct"));
					put(
						"query#priceModifierProductProduct",
						new ObjectValuePair<>(
							ProductResourceImpl.class,
							"getPriceModifierProductProduct"));
					put(
						"query#discountProductGroupProductGroup",
						new ObjectValuePair<>(
							ProductGroupResourceImpl.class,
							"getDiscountProductGroupProductGroup"));
					put(
						"query#priceModifierProductGroupProductGroup",
						new ObjectValuePair<>(
							ProductGroupResourceImpl.class,
							"getPriceModifierProductGroupProductGroup"));
					put(
						"query#discountSkuSku",
						new ObjectValuePair<>(
							SkuResourceImpl.class, "getDiscountSkuSku"));
					put(
						"query#priceEntryIdSku",
						new ObjectValuePair<>(
							SkuResourceImpl.class, "getPriceEntryIdSku"));
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
						"query#PriceEntry.idProduct",
						new ObjectValuePair<>(
							ProductResourceImpl.class,
							"getPriceEntryIdProduct"));
					put(
						"query#PriceEntry.idTierPrices",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getPriceEntryIdTierPricesPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountCategories",
						new ObjectValuePair<>(
							DiscountCategoryResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountCategoriesPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountProductGroups",
						new ObjectValuePair<>(
							DiscountProductGroupResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountProductGroupsPage"));
					put(
						"query#TierPrice.priceEntry",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class, "getPriceEntry"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountProducts",
						new ObjectValuePair<>(
							DiscountProductResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountProductsPage"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceListAccounts",
						new ObjectValuePair<>(
							PriceListAccountResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListAccountsPage"));
					put(
						"query#PriceEntry.discountByExternalReferenceCode",
						new ObjectValuePair<>(
							DiscountResourceImpl.class,
							"getDiscountByExternalReferenceCode"));
					put(
						"query#Discount.priceListByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceListResourceImpl.class,
							"getPriceListByExternalReferenceCode"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceListOrderTypes",
						new ObjectValuePair<>(
							PriceListOrderTypeResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListOrderTypesPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountOrderTypes",
						new ObjectValuePair<>(
							DiscountOrderTypeResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountOrderTypesPage"));
					put(
						"query#Discount.tierPriceByExternalReferenceCode",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getTierPriceByExternalReferenceCode"));
					put(
						"query#Discount.priceModifierByExternalReferenceCodePriceModifierProducts",
						new ObjectValuePair<>(
							PriceModifierProductResourceImpl.class,
							"getPriceModifierByExternalReferenceCodePriceModifierProductsPage"));
					put(
						"query#Discount.priceEntryByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"getPriceEntryByExternalReferenceCode"));
					put(
						"query#PriceEntry.idSku",
						new ObjectValuePair<>(
							SkuResourceImpl.class, "getPriceEntryIdSku"));
					put(
						"query#Discount.priceModifierByExternalReferenceCodePriceModifierProductGroups",
						new ObjectValuePair<>(
							PriceModifierProductGroupResourceImpl.class,
							"getPriceModifierByExternalReferenceCodePriceModifierProductGroupsPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountAccountGroups",
						new ObjectValuePair<>(
							DiscountAccountGroupResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountAccountGroupsPage"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceListAccountGroups",
						new ObjectValuePair<>(
							PriceListAccountGroupResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListAccountGroupsPage"));
					put(
						"query#Discount.priceEntryByExternalReferenceCodeTierPrices",
						new ObjectValuePair<>(
							TierPriceResourceImpl.class,
							"getPriceEntryByExternalReferenceCodeTierPricesPage"));
					put(
						"query#Discount.priceModifierByExternalReferenceCodePriceModifierCategories",
						new ObjectValuePair<>(
							PriceModifierCategoryResourceImpl.class,
							"getPriceModifierByExternalReferenceCodePriceModifierCategoriesPage"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceModifiers",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceModifiersPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountChannels",
						new ObjectValuePair<>(
							DiscountChannelResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountChannelsPage"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceListDiscounts",
						new ObjectValuePair<>(
							PriceListDiscountResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListDiscountsPage"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceListChannels",
						new ObjectValuePair<>(
							PriceListChannelResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceListChannelsPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountSkus",
						new ObjectValuePair<>(
							DiscountSkuResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountSkusPage"));
					put(
						"query#Discount.priceListByExternalReferenceCodePriceEntries",
						new ObjectValuePair<>(
							PriceEntryResourceImpl.class,
							"getPriceListByExternalReferenceCodePriceEntriesPage"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountRules",
						new ObjectValuePair<>(
							DiscountRuleResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountRulesPage"));
					put(
						"query#Discount.priceModifierByExternalReferenceCode",
						new ObjectValuePair<>(
							PriceModifierResourceImpl.class,
							"getPriceModifierByExternalReferenceCode"));
					put(
						"query#Discount.byExternalReferenceCodeDiscountAccounts",
						new ObjectValuePair<>(
							DiscountAccountResourceImpl.class,
							"getDiscountByExternalReferenceCodeDiscountAccountsPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountResource>
		_discountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountAccountResource>
		_discountAccountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountAccountGroupResource>
		_discountAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountCategoryResource>
		_discountCategoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountChannelResource>
		_discountChannelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountOrderTypeResource>
		_discountOrderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountProductResource>
		_discountProductResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountProductGroupResource>
		_discountProductGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountRuleResource>
		_discountRuleResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DiscountSkuResource>
		_discountSkuResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceEntryResource>
		_priceEntryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListResource>
		_priceListResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListAccountResource>
		_priceListAccountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListAccountGroupResource>
		_priceListAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListChannelResource>
		_priceListChannelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListDiscountResource>
		_priceListDiscountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceListOrderTypeResource>
		_priceListOrderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceModifierResource>
		_priceModifierResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceModifierCategoryResource>
		_priceModifierCategoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceModifierProductResource>
		_priceModifierProductResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PriceModifierProductGroupResource>
		_priceModifierProductGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TierPriceResource>
		_tierPriceResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CategoryResource>
		_categoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderTypeResource>
		_orderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PricingAccountGroupResource>
		_pricingAccountGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ProductResource>
		_productResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ProductGroupResource>
		_productGroupResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SkuResource>
		_skuResourceComponentServiceObjects;

}