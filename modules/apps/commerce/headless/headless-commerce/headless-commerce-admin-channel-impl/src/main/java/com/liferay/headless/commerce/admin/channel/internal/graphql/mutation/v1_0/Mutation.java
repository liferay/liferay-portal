/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.graphql.mutation.v1_0;

import com.liferay.headless.commerce.admin.channel.dto.v1_0.AccountAddressChannel;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.CategoryDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ChannelAccount;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.DefaultCategoryDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.DefaultProductDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelOrderType;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelTerm;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ProductDisplayPage;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionOrderType;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionTerm;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.AccountAddressChannelResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.CategoryDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ChannelAccountResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.DefaultCategoryDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.DefaultProductDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.PaymentMethodGroupRelOrderTypeResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.PaymentMethodGroupRelTermResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ProductDisplayPageResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ShippingFixedOptionOrderTypeResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ShippingFixedOptionTermResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.ShippingMethodResource;
import com.liferay.headless.commerce.admin.channel.resource.v1_0.TaxCategoryResource;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Mutation {

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

	@GraphQLField
	public boolean deleteAccountAddressChannel(
			@GraphQLName("accountAddressChannelId") Long
				accountAddressChannelId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressChannelResource ->
				accountAddressChannelResource.deleteAccountAddressChannel(
					accountAddressChannelId));

		return true;
	}

	@GraphQLField
	public Response deleteAccountAddressChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressChannelResource ->
				accountAddressChannelResource.deleteAccountAddressChannelBatch(
					callbackURL, object));
	}

	@GraphQLField
	public AccountAddressChannel
			createAccountAddressByExternalReferenceCodeAccountAddressChannel(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("accountAddressChannel") AccountAddressChannel
					accountAddressChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressChannelResource ->
				accountAddressChannelResource.
					postAccountAddressByExternalReferenceCodeAccountAddressChannel(
						externalReferenceCode, accountAddressChannel));
	}

	@GraphQLField
	public AccountAddressChannel createAccountAddressIdAccountAddressChannel(
			@GraphQLName("addressId") Long addressId,
			@GraphQLName("accountAddressChannel") AccountAddressChannel
				accountAddressChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			accountAddressChannelResource ->
				accountAddressChannelResource.
					postAccountAddressIdAccountAddressChannel(
						addressId, accountAddressChannel));
	}

	@GraphQLField
	public boolean deleteCategoryDisplayPage(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource ->
				categoryDisplayPageResource.deleteCategoryDisplayPage(id));

		return true;
	}

	@GraphQLField
	public Response deleteCategoryDisplayPageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource ->
				categoryDisplayPageResource.deleteCategoryDisplayPageBatch(
					callbackURL, object));
	}

	@GraphQLField
	public CategoryDisplayPage patchCategoryDisplayPage(
			@GraphQLName("id") Long id,
			@GraphQLName("categoryDisplayPage") CategoryDisplayPage
				categoryDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource ->
				categoryDisplayPageResource.patchCategoryDisplayPage(
					id, categoryDisplayPage));
	}

	@GraphQLField
	public CategoryDisplayPage
			createChannelByExternalReferenceCodeCategoryDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("categoryDisplayPage") CategoryDisplayPage
					categoryDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource ->
				categoryDisplayPageResource.
					postChannelByExternalReferenceCodeCategoryDisplayPage(
						externalReferenceCode, categoryDisplayPage));
	}

	@GraphQLField
	public CategoryDisplayPage createChannelIdCategoryDisplayPage(
			@GraphQLName("id") Long id,
			@GraphQLName("categoryDisplayPage") CategoryDisplayPage
				categoryDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryDisplayPageResource ->
				categoryDisplayPageResource.postChannelIdCategoryDisplayPage(
					id, categoryDisplayPage));
	}

	@GraphQLField
	public Response createChannelsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.postChannelsPageExportBatch(
				search, _filterBiFunction.apply(channelResource, filterString),
				_sortsBiFunction.apply(channelResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Channel createChannel(@GraphQLName("channel") Channel channel)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.postChannel(channel));
	}

	@GraphQLField
	public Response createChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.postChannelBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteChannelByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource ->
				channelResource.deleteChannelByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Channel patchChannelByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("channel") Channel channel)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource ->
				channelResource.patchChannelByExternalReferenceCode(
					externalReferenceCode, channel));
	}

	@GraphQLField
	public Channel updateChannelByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("channel") Channel channel)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource ->
				channelResource.putChannelByExternalReferenceCode(
					externalReferenceCode, channel));
	}

	@GraphQLField
	public boolean deleteChannel(@GraphQLName("channelId") Long channelId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.deleteChannel(channelId));

		return true;
	}

	@GraphQLField
	public Response deleteChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.deleteChannelBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Channel patchChannel(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("channel") Channel channel)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.patchChannel(
				channelId, channel));
	}

	@GraphQLField
	public Channel updateChannel(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("channel") Channel channel)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.putChannel(channelId, channel));
	}

	@GraphQLField
	public Response updateChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelResource -> channelResource.putChannelBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteChannelAccount(
			@GraphQLName("channelAccountId") Long channelAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelAccountResource ->
				channelAccountResource.deleteChannelAccount(channelAccountId));

		return true;
	}

	@GraphQLField
	public Response deleteChannelAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelAccountResource ->
				channelAccountResource.deleteChannelAccountBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ChannelAccount createChannelByExternalReferenceCodeChannelAccount(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("channelAccount") ChannelAccount channelAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelAccountResource ->
				channelAccountResource.
					postChannelByExternalReferenceCodeChannelAccount(
						externalReferenceCode, channelAccount));
	}

	@GraphQLField
	public ChannelAccount createChannelIdChannelAccount(
			@GraphQLName("id") Long id,
			@GraphQLName("channelAccount") ChannelAccount channelAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			channelAccountResource ->
				channelAccountResource.postChannelIdChannelAccount(
					id, channelAccount));
	}

	@GraphQLField
	public boolean
			deleteChannelByExternalReferenceCodeDefaultCategoryDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultCategoryDisplayPageResource ->
				defaultCategoryDisplayPageResource.
					deleteChannelByExternalReferenceCodeDefaultCategoryDisplayPage(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public DefaultCategoryDisplayPage
			createChannelByExternalReferenceCodeDefaultCategoryDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("defaultCategoryDisplayPage")
					DefaultCategoryDisplayPage defaultCategoryDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultCategoryDisplayPageResource ->
				defaultCategoryDisplayPageResource.
					postChannelByExternalReferenceCodeDefaultCategoryDisplayPage(
						externalReferenceCode, defaultCategoryDisplayPage));
	}

	@GraphQLField
	public boolean deleteChannelIdDefaultCategoryDisplayPage(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultCategoryDisplayPageResource ->
				defaultCategoryDisplayPageResource.
					deleteChannelIdDefaultCategoryDisplayPage(id));

		return true;
	}

	@GraphQLField
	public DefaultCategoryDisplayPage createChannelIdDefaultCategoryDisplayPage(
			@GraphQLName("id") Long id,
			@GraphQLName("defaultCategoryDisplayPage")
				DefaultCategoryDisplayPage defaultCategoryDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultCategoryDisplayPageResource ->
				defaultCategoryDisplayPageResource.
					postChannelIdDefaultCategoryDisplayPage(
						id, defaultCategoryDisplayPage));
	}

	@GraphQLField
	public boolean
			deleteChannelByExternalReferenceCodeDefaultProductDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultProductDisplayPageResource ->
				defaultProductDisplayPageResource.
					deleteChannelByExternalReferenceCodeDefaultProductDisplayPage(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public DefaultProductDisplayPage
			createChannelByExternalReferenceCodeDefaultProductDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("defaultProductDisplayPage")
					DefaultProductDisplayPage defaultProductDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultProductDisplayPageResource ->
				defaultProductDisplayPageResource.
					postChannelByExternalReferenceCodeDefaultProductDisplayPage(
						externalReferenceCode, defaultProductDisplayPage));
	}

	@GraphQLField
	public boolean deleteChannelIdDefaultProductDisplayPage(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultProductDisplayPageResource ->
				defaultProductDisplayPageResource.
					deleteChannelIdDefaultProductDisplayPage(id));

		return true;
	}

	@GraphQLField
	public DefaultProductDisplayPage createChannelIdDefaultProductDisplayPage(
			@GraphQLName("id") Long id,
			@GraphQLName("defaultProductDisplayPage") DefaultProductDisplayPage
				defaultProductDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			defaultProductDisplayPageResource ->
				defaultProductDisplayPageResource.
					postChannelIdDefaultProductDisplayPage(
						id, defaultProductDisplayPage));
	}

	@GraphQLField
	public boolean deletePaymentMethodGroupRelOrderType(
			@GraphQLName("paymentMethodGroupRelOrderTypeId") Long
				paymentMethodGroupRelOrderTypeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelOrderTypeResource ->
				paymentMethodGroupRelOrderTypeResource.
					deletePaymentMethodGroupRelOrderType(
						paymentMethodGroupRelOrderTypeId));

		return true;
	}

	@GraphQLField
	public Response deletePaymentMethodGroupRelOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelOrderTypeResource ->
				paymentMethodGroupRelOrderTypeResource.
					deletePaymentMethodGroupRelOrderTypeBatch(
						callbackURL, object));
	}

	@GraphQLField
	public PaymentMethodGroupRelOrderType
			createPaymentMethodGroupRelIdPaymentMethodGroupRelOrderType(
				@GraphQLName("id") Long id,
				@GraphQLName("paymentMethodGroupRelOrderType")
					PaymentMethodGroupRelOrderType
						paymentMethodGroupRelOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelOrderTypeResource ->
				paymentMethodGroupRelOrderTypeResource.
					postPaymentMethodGroupRelIdPaymentMethodGroupRelOrderType(
						id, paymentMethodGroupRelOrderType));
	}

	@GraphQLField
	public boolean deletePaymentMethodGroupRelTerm(
			@GraphQLName("paymentMethodGroupRelTermId") Long
				paymentMethodGroupRelTermId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_paymentMethodGroupRelTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelTermResource ->
				paymentMethodGroupRelTermResource.
					deletePaymentMethodGroupRelTerm(
						paymentMethodGroupRelTermId));

		return true;
	}

	@GraphQLField
	public Response deletePaymentMethodGroupRelTermBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodGroupRelTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelTermResource ->
				paymentMethodGroupRelTermResource.
					deletePaymentMethodGroupRelTermBatch(callbackURL, object));
	}

	@GraphQLField
	public PaymentMethodGroupRelTerm
			createPaymentMethodGroupRelIdPaymentMethodGroupRelTerm(
				@GraphQLName("id") Long id,
				@GraphQLName("paymentMethodGroupRelTerm")
					PaymentMethodGroupRelTerm paymentMethodGroupRelTerm)
		throws Exception {

		return _applyComponentServiceObjects(
			_paymentMethodGroupRelTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			paymentMethodGroupRelTermResource ->
				paymentMethodGroupRelTermResource.
					postPaymentMethodGroupRelIdPaymentMethodGroupRelTerm(
						id, paymentMethodGroupRelTerm));
	}

	@GraphQLField
	public ProductDisplayPage
			createChannelByExternalReferenceCodeProductDisplayPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productDisplayPage") ProductDisplayPage
					productDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource ->
				productDisplayPageResource.
					postChannelByExternalReferenceCodeProductDisplayPage(
						externalReferenceCode, productDisplayPage));
	}

	@GraphQLField
	public ProductDisplayPage createChannelIdProductDisplayPage(
			@GraphQLName("id") Long id,
			@GraphQLName("productDisplayPage") ProductDisplayPage
				productDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource ->
				productDisplayPageResource.postChannelIdProductDisplayPage(
					id, productDisplayPage));
	}

	@GraphQLField
	public boolean deleteProductDisplayPage(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource ->
				productDisplayPageResource.deleteProductDisplayPage(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductDisplayPageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource ->
				productDisplayPageResource.deleteProductDisplayPageBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ProductDisplayPage patchProductDisplayPage(
			@GraphQLName("id") Long id,
			@GraphQLName("productDisplayPage") ProductDisplayPage
				productDisplayPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			productDisplayPageResource ->
				productDisplayPageResource.patchProductDisplayPage(
					id, productDisplayPage));
	}

	@GraphQLField
	public boolean deleteShippingFixedOptionOrderType(
			@GraphQLName("shippingFixedOptionOrderTypeId") Long
				shippingFixedOptionOrderTypeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_shippingFixedOptionOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionOrderTypeResource ->
				shippingFixedOptionOrderTypeResource.
					deleteShippingFixedOptionOrderType(
						shippingFixedOptionOrderTypeId));

		return true;
	}

	@GraphQLField
	public Response deleteShippingFixedOptionOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingFixedOptionOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionOrderTypeResource ->
				shippingFixedOptionOrderTypeResource.
					deleteShippingFixedOptionOrderTypeBatch(
						callbackURL, object));
	}

	@GraphQLField
	public ShippingFixedOptionOrderType
			createShippingFixedOptionIdShippingFixedOptionOrderType(
				@GraphQLName("id") Long id,
				@GraphQLName("shippingFixedOptionOrderType")
					ShippingFixedOptionOrderType shippingFixedOptionOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingFixedOptionOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionOrderTypeResource ->
				shippingFixedOptionOrderTypeResource.
					postShippingFixedOptionIdShippingFixedOptionOrderType(
						id, shippingFixedOptionOrderType));
	}

	@GraphQLField
	public boolean deleteShippingFixedOptionTerm(
			@GraphQLName("shippingFixedOptionTermId") Long
				shippingFixedOptionTermId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_shippingFixedOptionTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionTermResource ->
				shippingFixedOptionTermResource.deleteShippingFixedOptionTerm(
					shippingFixedOptionTermId));

		return true;
	}

	@GraphQLField
	public Response deleteShippingFixedOptionTermBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingFixedOptionTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionTermResource ->
				shippingFixedOptionTermResource.
					deleteShippingFixedOptionTermBatch(callbackURL, object));
	}

	@GraphQLField
	public ShippingFixedOptionTerm
			createShippingFixedOptionIdShippingFixedOptionTerm(
				@GraphQLName("id") Long id,
				@GraphQLName("shippingFixedOptionTerm") ShippingFixedOptionTerm
					shippingFixedOptionTerm)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingFixedOptionTermResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingFixedOptionTermResource ->
				shippingFixedOptionTermResource.
					postShippingFixedOptionIdShippingFixedOptionTerm(
						id, shippingFixedOptionTerm));
	}

	@GraphQLField
	public Response createChannelShippingMethodsPageExportBatch(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects,
			this::_populateResourceContext,
			shippingMethodResource ->
				shippingMethodResource.
					postChannelShippingMethodsPageExportBatch(
						channelId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createTaxCategoriesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxCategoryResource ->
				taxCategoryResource.postTaxCategoriesPageExportBatch(
					search, callbackURL, contentType, fieldNames));
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
		accountAddressChannelResource.setRoleLocalService(_roleLocalService);

		accountAddressChannelResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		accountAddressChannelResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		categoryDisplayPageResource.setRoleLocalService(_roleLocalService);

		categoryDisplayPageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		categoryDisplayPageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		channelResource.setRoleLocalService(_roleLocalService);

		channelResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		channelResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		channelAccountResource.setRoleLocalService(_roleLocalService);

		channelAccountResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		channelAccountResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		defaultProductDisplayPageResource.setRoleLocalService(
			_roleLocalService);
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
		paymentMethodGroupRelOrderTypeResource.setRoleLocalService(
			_roleLocalService);

		paymentMethodGroupRelOrderTypeResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		paymentMethodGroupRelOrderTypeResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		paymentMethodGroupRelTermResource.setRoleLocalService(
			_roleLocalService);

		paymentMethodGroupRelTermResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		paymentMethodGroupRelTermResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		productDisplayPageResource.setRoleLocalService(_roleLocalService);

		productDisplayPageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productDisplayPageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		shippingFixedOptionOrderTypeResource.setRoleLocalService(
			_roleLocalService);

		shippingFixedOptionOrderTypeResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		shippingFixedOptionOrderTypeResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		shippingFixedOptionTermResource.setRoleLocalService(_roleLocalService);

		shippingFixedOptionTermResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		shippingFixedOptionTermResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		shippingMethodResource.setRoleLocalService(_roleLocalService);

		shippingMethodResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		shippingMethodResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		taxCategoryResource.setRoleLocalService(_roleLocalService);

		taxCategoryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		taxCategoryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

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