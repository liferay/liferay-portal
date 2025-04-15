/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.graphql.mutation.v2_0;

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
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.TierPrice;
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
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.TierPriceResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Mutation {

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

	public static void setTierPriceResourceComponentServiceObjects(
		ComponentServiceObjects<TierPriceResource>
			tierPriceResourceComponentServiceObjects) {

		_tierPriceResourceComponentServiceObjects =
			tierPriceResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createDiscountsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> discountResource.postDiscountsPageExportBatch(
				search, _filterBiFunction.apply(discountResource, filterString),
				_sortsBiFunction.apply(discountResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Discount createDiscount(@GraphQLName("discount") Discount discount)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> discountResource.postDiscount(discount));
	}

	@GraphQLField
	public Response createDiscountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> discountResource.postDiscountBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource ->
				discountResource.deleteDiscountByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Discount patchDiscountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("discount") Discount discount)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource ->
				discountResource.patchDiscountByExternalReferenceCode(
					externalReferenceCode, discount));
	}

	@GraphQLField
	public Discount updateDiscountByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("discount") Discount discount)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource ->
				discountResource.putDiscountByExternalReferenceCode(
					externalReferenceCode, discount));
	}

	@GraphQLField
	public boolean deleteDiscount(@GraphQLName("id") Long id) throws Exception {
		_applyVoidComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> discountResource.deleteDiscount(id));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> discountResource.deleteDiscountBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Discount patchDiscount(
			@GraphQLName("id") Long id,
			@GraphQLName("discount") Discount discount)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountResource -> discountResource.patchDiscount(id, discount));
	}

	@GraphQLField
	public boolean deleteDiscountAccount(
			@GraphQLName("discountAccountId") Long discountAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountResource ->
				discountAccountResource.deleteDiscountAccount(
					discountAccountId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountResource ->
				discountAccountResource.deleteDiscountAccountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountAccount createDiscountByExternalReferenceCodeDiscountAccount(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("discountAccount") DiscountAccount discountAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountResource ->
				discountAccountResource.
					postDiscountByExternalReferenceCodeDiscountAccount(
						externalReferenceCode, discountAccount));
	}

	@GraphQLField
	public DiscountAccount createDiscountIdDiscountAccount(
			@GraphQLName("id") Long id,
			@GraphQLName("discountAccount") DiscountAccount discountAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountResource ->
				discountAccountResource.postDiscountIdDiscountAccount(
					id, discountAccount));
	}

	@GraphQLField
	public Response createDiscountIdDiscountAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountResource ->
				discountAccountResource.postDiscountIdDiscountAccountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountAccountGroup(
			@GraphQLName("discountAccountGroupId") Long discountAccountGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource ->
				discountAccountGroupResource.deleteDiscountAccountGroup(
					discountAccountGroupId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource ->
				discountAccountGroupResource.deleteDiscountAccountGroupBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountAccountGroup
			createDiscountByExternalReferenceCodeDiscountAccountGroup(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("discountAccountGroup") DiscountAccountGroup
					discountAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource ->
				discountAccountGroupResource.
					postDiscountByExternalReferenceCodeDiscountAccountGroup(
						externalReferenceCode, discountAccountGroup));
	}

	@GraphQLField
	public DiscountAccountGroup createDiscountIdDiscountAccountGroup(
			@GraphQLName("id") Long id,
			@GraphQLName("discountAccountGroup") DiscountAccountGroup
				discountAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource ->
				discountAccountGroupResource.postDiscountIdDiscountAccountGroup(
					id, discountAccountGroup));
	}

	@GraphQLField
	public Response createDiscountIdDiscountAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountAccountGroupResource ->
				discountAccountGroupResource.
					postDiscountIdDiscountAccountGroupBatch(
						callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountCategory(
			@GraphQLName("discountCategoryId") Long discountCategoryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource ->
				discountCategoryResource.deleteDiscountCategory(
					discountCategoryId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource ->
				discountCategoryResource.deleteDiscountCategoryBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountCategory
			createDiscountByExternalReferenceCodeDiscountCategory(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("discountCategory") DiscountCategory
					discountCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource ->
				discountCategoryResource.
					postDiscountByExternalReferenceCodeDiscountCategory(
						externalReferenceCode, discountCategory));
	}

	@GraphQLField
	public DiscountCategory createDiscountIdDiscountCategory(
			@GraphQLName("id") Long id,
			@GraphQLName("discountCategory") DiscountCategory discountCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource ->
				discountCategoryResource.postDiscountIdDiscountCategory(
					id, discountCategory));
	}

	@GraphQLField
	public Response createDiscountIdDiscountCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountCategoryResource ->
				discountCategoryResource.postDiscountIdDiscountCategoryBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountChannel(
			@GraphQLName("discountChannelId") Long discountChannelId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountChannelResource ->
				discountChannelResource.deleteDiscountChannel(
					discountChannelId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountChannelResource ->
				discountChannelResource.deleteDiscountChannelBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountChannel createDiscountByExternalReferenceCodeDiscountChannel(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("discountChannel") DiscountChannel discountChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountChannelResource ->
				discountChannelResource.
					postDiscountByExternalReferenceCodeDiscountChannel(
						externalReferenceCode, discountChannel));
	}

	@GraphQLField
	public DiscountChannel createDiscountIdDiscountChannel(
			@GraphQLName("id") Long id,
			@GraphQLName("discountChannel") DiscountChannel discountChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountChannelResource ->
				discountChannelResource.postDiscountIdDiscountChannel(
					id, discountChannel));
	}

	@GraphQLField
	public Response createDiscountIdDiscountChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountChannelResource ->
				discountChannelResource.postDiscountIdDiscountChannelBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountOrderType(
			@GraphQLName("discountOrderTypeId") Long discountOrderTypeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountOrderTypeResource ->
				discountOrderTypeResource.deleteDiscountOrderType(
					discountOrderTypeId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountOrderTypeResource ->
				discountOrderTypeResource.deleteDiscountOrderTypeBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountOrderType
			createDiscountByExternalReferenceCodeDiscountOrderType(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("discountOrderType") DiscountOrderType
					discountOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountOrderTypeResource ->
				discountOrderTypeResource.
					postDiscountByExternalReferenceCodeDiscountOrderType(
						externalReferenceCode, discountOrderType));
	}

	@GraphQLField
	public DiscountOrderType createDiscountIdDiscountOrderType(
			@GraphQLName("id") Long id,
			@GraphQLName("discountOrderType") DiscountOrderType
				discountOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountOrderTypeResource ->
				discountOrderTypeResource.postDiscountIdDiscountOrderType(
					id, discountOrderType));
	}

	@GraphQLField
	public Response createDiscountIdDiscountOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountOrderTypeResource ->
				discountOrderTypeResource.postDiscountIdDiscountOrderTypeBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountProduct(
			@GraphQLName("discountProductId") Long discountProductId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource ->
				discountProductResource.deleteDiscountProduct(
					discountProductId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource ->
				discountProductResource.deleteDiscountProductBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountProduct createDiscountByExternalReferenceCodeDiscountProduct(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("discountProduct") DiscountProduct discountProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource ->
				discountProductResource.
					postDiscountByExternalReferenceCodeDiscountProduct(
						externalReferenceCode, discountProduct));
	}

	@GraphQLField
	public DiscountProduct createDiscountIdDiscountProduct(
			@GraphQLName("id") Long id,
			@GraphQLName("discountProduct") DiscountProduct discountProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource ->
				discountProductResource.postDiscountIdDiscountProduct(
					id, discountProduct));
	}

	@GraphQLField
	public Response createDiscountIdDiscountProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductResource ->
				discountProductResource.postDiscountIdDiscountProductBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountProductGroup(
			@GraphQLName("discountProductGroupId") Long discountProductGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductGroupResource ->
				discountProductGroupResource.deleteDiscountProductGroup(
					discountProductGroupId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountProductGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductGroupResource ->
				discountProductGroupResource.deleteDiscountProductGroupBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountProductGroup
			createDiscountByExternalReferenceCodeDiscountProductGroup(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("discountProductGroup") DiscountProductGroup
					discountProductGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductGroupResource ->
				discountProductGroupResource.
					postDiscountByExternalReferenceCodeDiscountProductGroup(
						externalReferenceCode, discountProductGroup));
	}

	@GraphQLField
	public DiscountProductGroup createDiscountIdDiscountProductGroup(
			@GraphQLName("id") Long id,
			@GraphQLName("discountProductGroup") DiscountProductGroup
				discountProductGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductGroupResource ->
				discountProductGroupResource.postDiscountIdDiscountProductGroup(
					id, discountProductGroup));
	}

	@GraphQLField
	public Response createDiscountIdDiscountProductGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountProductGroupResource ->
				discountProductGroupResource.
					postDiscountIdDiscountProductGroupBatch(
						callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountRule(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource -> discountRuleResource.deleteDiscountRule(
				id));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountRuleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource ->
				discountRuleResource.deleteDiscountRuleBatch(
					callbackURL, object));
	}

	@GraphQLField
	public DiscountRule patchDiscountRule(
			@GraphQLName("id") Long id,
			@GraphQLName("discountRule") DiscountRule discountRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource -> discountRuleResource.patchDiscountRule(
				id, discountRule));
	}

	@GraphQLField
	public DiscountRule createDiscountByExternalReferenceCodeDiscountRule(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("discountRule") DiscountRule discountRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource ->
				discountRuleResource.
					postDiscountByExternalReferenceCodeDiscountRule(
						externalReferenceCode, discountRule));
	}

	@GraphQLField
	public DiscountRule createDiscountIdDiscountRule(
			@GraphQLName("id") Long id,
			@GraphQLName("discountRule") DiscountRule discountRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource ->
				discountRuleResource.postDiscountIdDiscountRule(
					id, discountRule));
	}

	@GraphQLField
	public Response createDiscountIdDiscountRuleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountRuleResource ->
				discountRuleResource.postDiscountIdDiscountRuleBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDiscountSku(
			@GraphQLName("discountSkuId") Long discountSkuId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountSkuResource -> discountSkuResource.deleteDiscountSku(
				discountSkuId));

		return true;
	}

	@GraphQLField
	public Response deleteDiscountSkuBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountSkuResource -> discountSkuResource.deleteDiscountSkuBatch(
				callbackURL, object));
	}

	@GraphQLField
	public DiscountSku createDiscountByExternalReferenceCodeDiscountSku(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("discountSku") DiscountSku discountSku)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountSkuResource ->
				discountSkuResource.
					postDiscountByExternalReferenceCodeDiscountSku(
						externalReferenceCode, discountSku));
	}

	@GraphQLField
	public DiscountSku createDiscountIdDiscountSku(
			@GraphQLName("id") Long id,
			@GraphQLName("discountSku") DiscountSku discountSku)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountSkuResource ->
				discountSkuResource.postDiscountIdDiscountSku(id, discountSku));
	}

	@GraphQLField
	public Response createDiscountIdDiscountSkuBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_discountSkuResourceComponentServiceObjects,
			this::_populateResourceContext,
			discountSkuResource ->
				discountSkuResource.postDiscountIdDiscountSkuBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceEntryByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource ->
				priceEntryResource.deletePriceEntryByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public PriceEntry patchPriceEntryByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("priceEntry") PriceEntry priceEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource ->
				priceEntryResource.patchPriceEntryByExternalReferenceCode(
					externalReferenceCode, priceEntry));
	}

	@GraphQLField
	public boolean deletePriceEntry(
			@GraphQLName("priceEntryId") Long priceEntryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> priceEntryResource.deletePriceEntry(
				priceEntryId));

		return true;
	}

	@GraphQLField
	public Response deletePriceEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> priceEntryResource.deletePriceEntryBatch(
				callbackURL, object));
	}

	@GraphQLField
	public PriceEntry patchPriceEntry(
			@GraphQLName("priceEntryId") Long priceEntryId,
			@GraphQLName("priceEntry") PriceEntry priceEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> priceEntryResource.patchPriceEntry(
				priceEntryId, priceEntry));
	}

	@GraphQLField
	public PriceEntry createPriceListByExternalReferenceCodePriceEntry(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("priceEntry") PriceEntry priceEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource ->
				priceEntryResource.
					postPriceListByExternalReferenceCodePriceEntry(
						externalReferenceCode, priceEntry));
	}

	@GraphQLField
	public PriceEntry createPriceListIdPriceEntry(
			@GraphQLName("id") Long id,
			@GraphQLName("priceEntry") PriceEntry priceEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource -> priceEntryResource.postPriceListIdPriceEntry(
				id, priceEntry));
	}

	@GraphQLField
	public Response createPriceListIdPriceEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceEntryResource ->
				priceEntryResource.postPriceListIdPriceEntryBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response createPriceListsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource ->
				priceListResource.postPriceListsPageExportBatch(
					search,
					_filterBiFunction.apply(priceListResource, filterString),
					_sortsBiFunction.apply(priceListResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public PriceList createPriceList(
			@GraphQLName("priceList") PriceList priceList)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource -> priceListResource.postPriceList(priceList));
	}

	@GraphQLField
	public Response createPriceListBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource -> priceListResource.postPriceListBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceListByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource ->
				priceListResource.deletePriceListByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public PriceList patchPriceListByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("priceList") PriceList priceList)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource ->
				priceListResource.patchPriceListByExternalReferenceCode(
					externalReferenceCode, priceList));
	}

	@GraphQLField
	public PriceList updatePriceListByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("priceList") PriceList priceList)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource ->
				priceListResource.putPriceListByExternalReferenceCode(
					externalReferenceCode, priceList));
	}

	@GraphQLField
	public boolean deletePriceList(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource -> priceListResource.deletePriceList(id));

		return true;
	}

	@GraphQLField
	public Response deletePriceListBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource -> priceListResource.deletePriceListBatch(
				callbackURL, object));
	}

	@GraphQLField
	public PriceList patchPriceList(
			@GraphQLName("id") Long id,
			@GraphQLName("priceList") PriceList priceList)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListResource -> priceListResource.patchPriceList(
				id, priceList));
	}

	@GraphQLField
	public boolean deletePriceListAccount(
			@GraphQLName("priceListAccountId") Long priceListAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountResource ->
				priceListAccountResource.deletePriceListAccount(
					priceListAccountId));

		return true;
	}

	@GraphQLField
	public Response deletePriceListAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountResource ->
				priceListAccountResource.deletePriceListAccountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PriceListAccount
			createPriceListByExternalReferenceCodePriceListAccount(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceListAccount") PriceListAccount
					priceListAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountResource ->
				priceListAccountResource.
					postPriceListByExternalReferenceCodePriceListAccount(
						externalReferenceCode, priceListAccount));
	}

	@GraphQLField
	public PriceListAccount createPriceListIdPriceListAccount(
			@GraphQLName("id") Long id,
			@GraphQLName("priceListAccount") PriceListAccount priceListAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountResource ->
				priceListAccountResource.postPriceListIdPriceListAccount(
					id, priceListAccount));
	}

	@GraphQLField
	public Response createPriceListIdPriceListAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountResource ->
				priceListAccountResource.postPriceListIdPriceListAccountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceListAccountGroup(
			@GraphQLName("priceListAccountGroupId") Long
				priceListAccountGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource ->
				priceListAccountGroupResource.deletePriceListAccountGroup(
					priceListAccountGroupId));

		return true;
	}

	@GraphQLField
	public Response deletePriceListAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource ->
				priceListAccountGroupResource.deletePriceListAccountGroupBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PriceListAccountGroup
			createPriceListByExternalReferenceCodePriceListAccountGroup(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceListAccountGroup") PriceListAccountGroup
					priceListAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource ->
				priceListAccountGroupResource.
					postPriceListByExternalReferenceCodePriceListAccountGroup(
						externalReferenceCode, priceListAccountGroup));
	}

	@GraphQLField
	public PriceListAccountGroup createPriceListIdPriceListAccountGroup(
			@GraphQLName("id") Long id,
			@GraphQLName("priceListAccountGroup") PriceListAccountGroup
				priceListAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource ->
				priceListAccountGroupResource.
					postPriceListIdPriceListAccountGroup(
						id, priceListAccountGroup));
	}

	@GraphQLField
	public Response createPriceListIdPriceListAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListAccountGroupResource ->
				priceListAccountGroupResource.
					postPriceListIdPriceListAccountGroupBatch(
						callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceListChannel(
			@GraphQLName("priceListChannelId") Long priceListChannelId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListChannelResource ->
				priceListChannelResource.deletePriceListChannel(
					priceListChannelId));

		return true;
	}

	@GraphQLField
	public Response deletePriceListChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListChannelResource ->
				priceListChannelResource.deletePriceListChannelBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PriceListChannel
			createPriceListByExternalReferenceCodePriceListChannel(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceListChannel") PriceListChannel
					priceListChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListChannelResource ->
				priceListChannelResource.
					postPriceListByExternalReferenceCodePriceListChannel(
						externalReferenceCode, priceListChannel));
	}

	@GraphQLField
	public PriceListChannel createPriceListIdPriceListChannel(
			@GraphQLName("id") Long id,
			@GraphQLName("priceListChannel") PriceListChannel priceListChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListChannelResource ->
				priceListChannelResource.postPriceListIdPriceListChannel(
					id, priceListChannel));
	}

	@GraphQLField
	public Response createPriceListIdPriceListChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListChannelResource ->
				priceListChannelResource.postPriceListIdPriceListChannelBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceListDiscount(
			@GraphQLName("priceListDiscountId") Long priceListDiscountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListDiscountResource ->
				priceListDiscountResource.deletePriceListDiscount(
					priceListDiscountId));

		return true;
	}

	@GraphQLField
	public Response deletePriceListDiscountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListDiscountResource ->
				priceListDiscountResource.deletePriceListDiscountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PriceListDiscount
			createPriceListByExternalReferenceCodePriceListDiscount(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceListDiscount") PriceListDiscount
					priceListDiscount)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListDiscountResource ->
				priceListDiscountResource.
					postPriceListByExternalReferenceCodePriceListDiscount(
						externalReferenceCode, priceListDiscount));
	}

	@GraphQLField
	public PriceListDiscount createPriceListIdPriceListDiscount(
			@GraphQLName("id") Long id,
			@GraphQLName("priceListDiscount") PriceListDiscount
				priceListDiscount)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListDiscountResource ->
				priceListDiscountResource.postPriceListIdPriceListDiscount(
					id, priceListDiscount));
	}

	@GraphQLField
	public Response createPriceListIdPriceListDiscountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListDiscountResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListDiscountResource ->
				priceListDiscountResource.postPriceListIdPriceListDiscountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceListOrderType(
			@GraphQLName("priceListOrderTypeId") Long priceListOrderTypeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListOrderTypeResource ->
				priceListOrderTypeResource.deletePriceListOrderType(
					priceListOrderTypeId));

		return true;
	}

	@GraphQLField
	public Response deletePriceListOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListOrderTypeResource ->
				priceListOrderTypeResource.deletePriceListOrderTypeBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PriceListOrderType
			createPriceListByExternalReferenceCodePriceListOrderType(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceListOrderType") PriceListOrderType
					priceListOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListOrderTypeResource ->
				priceListOrderTypeResource.
					postPriceListByExternalReferenceCodePriceListOrderType(
						externalReferenceCode, priceListOrderType));
	}

	@GraphQLField
	public PriceListOrderType createPriceListIdPriceListOrderType(
			@GraphQLName("id") Long id,
			@GraphQLName("priceListOrderType") PriceListOrderType
				priceListOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListOrderTypeResource ->
				priceListOrderTypeResource.postPriceListIdPriceListOrderType(
					id, priceListOrderType));
	}

	@GraphQLField
	public Response createPriceListIdPriceListOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceListOrderTypeResource ->
				priceListOrderTypeResource.
					postPriceListIdPriceListOrderTypeBatch(
						callbackURL, object));
	}

	@GraphQLField
	public PriceModifier createPriceListByExternalReferenceCodePriceModifier(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("priceModifier") PriceModifier priceModifier)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource ->
				priceModifierResource.
					postPriceListByExternalReferenceCodePriceModifier(
						externalReferenceCode, priceModifier));
	}

	@GraphQLField
	public PriceModifier createPriceListIdPriceModifier(
			@GraphQLName("id") Long id,
			@GraphQLName("priceModifier") PriceModifier priceModifier)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource ->
				priceModifierResource.postPriceListIdPriceModifier(
					id, priceModifier));
	}

	@GraphQLField
	public Response createPriceListIdPriceModifierBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource ->
				priceModifierResource.postPriceListIdPriceModifierBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceModifierByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource ->
				priceModifierResource.
					deletePriceModifierByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response patchPriceModifierByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("priceModifier") PriceModifier priceModifier)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource ->
				priceModifierResource.patchPriceModifierByExternalReferenceCode(
					externalReferenceCode, priceModifier));
	}

	@GraphQLField
	public boolean deletePriceModifier(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource -> priceModifierResource.deletePriceModifier(
				id));

		return true;
	}

	@GraphQLField
	public Response deletePriceModifierBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource ->
				priceModifierResource.deletePriceModifierBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response patchPriceModifier(
			@GraphQLName("id") Long id,
			@GraphQLName("priceModifier") PriceModifier priceModifier)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierResource -> priceModifierResource.patchPriceModifier(
				id, priceModifier));
	}

	@GraphQLField
	public boolean deletePriceModifierCategory(
			@GraphQLName("priceModifierCategoryId") Long
				priceModifierCategoryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierCategoryResource ->
				priceModifierCategoryResource.deletePriceModifierCategory(
					priceModifierCategoryId));

		return true;
	}

	@GraphQLField
	public Response deletePriceModifierCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierCategoryResource ->
				priceModifierCategoryResource.deletePriceModifierCategoryBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PriceModifierCategory
			createPriceModifierByExternalReferenceCodePriceModifierCategory(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceModifierCategory") PriceModifierCategory
					priceModifierCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierCategoryResource ->
				priceModifierCategoryResource.
					postPriceModifierByExternalReferenceCodePriceModifierCategory(
						externalReferenceCode, priceModifierCategory));
	}

	@GraphQLField
	public PriceModifierCategory createPriceModifierIdPriceModifierCategory(
			@GraphQLName("id") Long id,
			@GraphQLName("priceModifierCategory") PriceModifierCategory
				priceModifierCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierCategoryResource ->
				priceModifierCategoryResource.
					postPriceModifierIdPriceModifierCategory(
						id, priceModifierCategory));
	}

	@GraphQLField
	public Response createPriceModifierIdPriceModifierCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierCategoryResource ->
				priceModifierCategoryResource.
					postPriceModifierIdPriceModifierCategoryBatch(
						callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceModifierProduct(
			@GraphQLName("priceModifierProductId") Long priceModifierProductId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductResource ->
				priceModifierProductResource.deletePriceModifierProduct(
					priceModifierProductId));

		return true;
	}

	@GraphQLField
	public Response deletePriceModifierProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductResource ->
				priceModifierProductResource.deletePriceModifierProductBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PriceModifierProduct
			createPriceModifierByExternalReferenceCodePriceModifierProduct(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceModifierProduct") PriceModifierProduct
					priceModifierProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductResource ->
				priceModifierProductResource.
					postPriceModifierByExternalReferenceCodePriceModifierProduct(
						externalReferenceCode, priceModifierProduct));
	}

	@GraphQLField
	public PriceModifierProduct createPriceModifierIdPriceModifierProduct(
			@GraphQLName("id") Long id,
			@GraphQLName("priceModifierProduct") PriceModifierProduct
				priceModifierProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductResource ->
				priceModifierProductResource.
					postPriceModifierIdPriceModifierProduct(
						id, priceModifierProduct));
	}

	@GraphQLField
	public Response createPriceModifierIdPriceModifierProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductResource ->
				priceModifierProductResource.
					postPriceModifierIdPriceModifierProductBatch(
						callbackURL, object));
	}

	@GraphQLField
	public boolean deletePriceModifierProductGroup(
			@GraphQLName("priceModifierProductGroupId") Long
				priceModifierProductGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductGroupResource ->
				priceModifierProductGroupResource.
					deletePriceModifierProductGroup(
						priceModifierProductGroupId));

		return true;
	}

	@GraphQLField
	public Response deletePriceModifierProductGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductGroupResource ->
				priceModifierProductGroupResource.
					deletePriceModifierProductGroupBatch(callbackURL, object));
	}

	@GraphQLField
	public PriceModifierProductGroup
			createPriceModifierByExternalReferenceCodePriceModifierProductGroup(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("priceModifierProductGroup")
					PriceModifierProductGroup priceModifierProductGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductGroupResource ->
				priceModifierProductGroupResource.
					postPriceModifierByExternalReferenceCodePriceModifierProductGroup(
						externalReferenceCode, priceModifierProductGroup));
	}

	@GraphQLField
	public PriceModifierProductGroup
			createPriceModifierIdPriceModifierProductGroup(
				@GraphQLName("id") Long id,
				@GraphQLName("priceModifierProductGroup")
					PriceModifierProductGroup priceModifierProductGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductGroupResource ->
				priceModifierProductGroupResource.
					postPriceModifierIdPriceModifierProductGroup(
						id, priceModifierProductGroup));
	}

	@GraphQLField
	public Response createPriceModifierIdPriceModifierProductGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_priceModifierProductGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			priceModifierProductGroupResource ->
				priceModifierProductGroupResource.
					postPriceModifierIdPriceModifierProductGroupBatch(
						callbackURL, object));
	}

	@GraphQLField
	public TierPrice createPriceEntryByExternalReferenceCodeTierPrice(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("tierPrice") TierPrice tierPrice)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource ->
				tierPriceResource.
					postPriceEntryByExternalReferenceCodeTierPrice(
						externalReferenceCode, tierPrice));
	}

	@GraphQLField
	public TierPrice createPriceEntryIdTierPrice(
			@GraphQLName("priceEntryId") Long priceEntryId,
			@GraphQLName("tierPrice") TierPrice tierPrice)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> tierPriceResource.postPriceEntryIdTierPrice(
				priceEntryId, tierPrice));
	}

	@GraphQLField
	public Response createPriceEntryIdTierPriceBatch(
			@GraphQLName("priceEntryId") Long priceEntryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource ->
				tierPriceResource.postPriceEntryIdTierPriceBatch(
					priceEntryId, callbackURL, object));
	}

	@GraphQLField
	public boolean deleteTierPriceByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource ->
				tierPriceResource.deleteTierPriceByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response patchTierPriceByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("tierPrice") TierPrice tierPrice)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource ->
				tierPriceResource.patchTierPriceByExternalReferenceCode(
					externalReferenceCode, tierPrice));
	}

	@GraphQLField
	public boolean deleteTierPrice(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> tierPriceResource.deleteTierPrice(id));

		return true;
	}

	@GraphQLField
	public Response deleteTierPriceBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> tierPriceResource.deleteTierPriceBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response patchTierPrice(
			@GraphQLName("id") Long id,
			@GraphQLName("tierPrice") TierPrice tierPrice)
		throws Exception {

		return _applyComponentServiceObjects(
			_tierPriceResourceComponentServiceObjects,
			this::_populateResourceContext,
			tierPriceResource -> tierPriceResource.patchTierPrice(
				id, tierPrice));
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

	private void _populateResourceContext(DiscountResource discountResource)
		throws Exception {

		discountResource.setContextAcceptLanguage(_acceptLanguage);
		discountResource.setContextCompany(_company);
		discountResource.setContextHttpServletRequest(_httpServletRequest);
		discountResource.setContextHttpServletResponse(_httpServletResponse);
		discountResource.setContextUriInfo(_uriInfo);
		discountResource.setContextUser(_user);
		discountResource.setGroupLocalService(_groupLocalService);
		discountResource.setRoleLocalService(_roleLocalService);

		discountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountAccountResource.setRoleLocalService(_roleLocalService);

		discountAccountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountAccountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountAccountGroupResource.setRoleLocalService(_roleLocalService);

		discountAccountGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountAccountGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountCategoryResource.setRoleLocalService(_roleLocalService);

		discountCategoryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountCategoryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountChannelResource.setRoleLocalService(_roleLocalService);

		discountChannelResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountChannelResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountOrderTypeResource.setRoleLocalService(_roleLocalService);

		discountOrderTypeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountOrderTypeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountProductResource.setRoleLocalService(_roleLocalService);

		discountProductResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountProductResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountProductGroupResource.setRoleLocalService(_roleLocalService);

		discountProductGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountProductGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountRuleResource.setRoleLocalService(_roleLocalService);

		discountRuleResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountRuleResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		discountSkuResource.setRoleLocalService(_roleLocalService);

		discountSkuResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		discountSkuResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceEntryResource.setRoleLocalService(_roleLocalService);

		priceEntryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceEntryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceListResource.setRoleLocalService(_roleLocalService);

		priceListResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceListResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceListAccountResource.setRoleLocalService(_roleLocalService);

		priceListAccountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceListAccountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceListAccountGroupResource.setRoleLocalService(_roleLocalService);

		priceListAccountGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceListAccountGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceListChannelResource.setRoleLocalService(_roleLocalService);

		priceListChannelResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceListChannelResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceListDiscountResource.setRoleLocalService(_roleLocalService);

		priceListDiscountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceListDiscountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceListOrderTypeResource.setRoleLocalService(_roleLocalService);

		priceListOrderTypeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceListOrderTypeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceModifierResource.setRoleLocalService(_roleLocalService);

		priceModifierResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceModifierResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceModifierCategoryResource.setRoleLocalService(_roleLocalService);

		priceModifierCategoryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceModifierCategoryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceModifierProductResource.setRoleLocalService(_roleLocalService);

		priceModifierProductResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		priceModifierProductResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		priceModifierProductGroupResource.setRoleLocalService(
			_roleLocalService);

		priceModifierProductGroupResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		priceModifierProductGroupResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		tierPriceResource.setRoleLocalService(_roleLocalService);

		tierPriceResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		tierPriceResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

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