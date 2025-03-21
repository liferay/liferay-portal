/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.graphql.servlet.v1_0;

import com.liferay.headless.commerce.admin.channel.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.commerce.admin.channel.internal.graphql.query.v1_0.Query;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.AccountAddressChannelResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.AccountResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.CategoryDisplayPageResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.ChannelAccountResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.ChannelResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.DefaultCategoryDisplayPageResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.DefaultProductDisplayPageResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.OrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.PaymentMethodGroupRelOrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.PaymentMethodGroupRelTermResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.ProductDisplayPageResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.ShippingFixedOptionOrderTypeResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.ShippingFixedOptionTermResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.ShippingMethodResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.TaxCategoryResourceImpl;
import com.liferay.headless.commerce.admin.channel.internal.resource.v1_0.TermResourceImpl;
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
 * @author Andrea Sbarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setAccountAddressChannelResourceComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects);
		Mutation.setCategoryDisplayPageResourceComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects);
		Mutation.setChannelResourceComponentServiceObjects(
			_channelResourceComponentServiceObjects);
		Mutation.setChannelAccountResourceComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects);
		Mutation.setDefaultCategoryDisplayPageResourceComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects);
		Mutation.setDefaultProductDisplayPageResourceComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects);
		Mutation.
			setPaymentMethodGroupRelOrderTypeResourceComponentServiceObjects(
				_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects);
		Mutation.setPaymentMethodGroupRelTermResourceComponentServiceObjects(
			_paymentMethodGroupRelTermResourceComponentServiceObjects);
		Mutation.setProductDisplayPageResourceComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects);
		Mutation.setShippingFixedOptionOrderTypeResourceComponentServiceObjects(
			_shippingFixedOptionOrderTypeResourceComponentServiceObjects);
		Mutation.setShippingFixedOptionTermResourceComponentServiceObjects(
			_shippingFixedOptionTermResourceComponentServiceObjects);
		Mutation.setShippingMethodResourceComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects);
		Mutation.setTaxCategoryResourceComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects);

		Query.setAccountResourceComponentServiceObjects(
			_accountResourceComponentServiceObjects);
		Query.setAccountAddressChannelResourceComponentServiceObjects(
			_accountAddressChannelResourceComponentServiceObjects);
		Query.setCategoryDisplayPageResourceComponentServiceObjects(
			_categoryDisplayPageResourceComponentServiceObjects);
		Query.setChannelResourceComponentServiceObjects(
			_channelResourceComponentServiceObjects);
		Query.setChannelAccountResourceComponentServiceObjects(
			_channelAccountResourceComponentServiceObjects);
		Query.setDefaultCategoryDisplayPageResourceComponentServiceObjects(
			_defaultCategoryDisplayPageResourceComponentServiceObjects);
		Query.setDefaultProductDisplayPageResourceComponentServiceObjects(
			_defaultProductDisplayPageResourceComponentServiceObjects);
		Query.setOrderTypeResourceComponentServiceObjects(
			_orderTypeResourceComponentServiceObjects);
		Query.setPaymentMethodGroupRelOrderTypeResourceComponentServiceObjects(
			_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects);
		Query.setPaymentMethodGroupRelTermResourceComponentServiceObjects(
			_paymentMethodGroupRelTermResourceComponentServiceObjects);
		Query.setProductDisplayPageResourceComponentServiceObjects(
			_productDisplayPageResourceComponentServiceObjects);
		Query.setShippingFixedOptionOrderTypeResourceComponentServiceObjects(
			_shippingFixedOptionOrderTypeResourceComponentServiceObjects);
		Query.setShippingFixedOptionTermResourceComponentServiceObjects(
			_shippingFixedOptionTermResourceComponentServiceObjects);
		Query.setShippingMethodResourceComponentServiceObjects(
			_shippingMethodResourceComponentServiceObjects);
		Query.setTaxCategoryResourceComponentServiceObjects(
			_taxCategoryResourceComponentServiceObjects);
		Query.setTermResourceComponentServiceObjects(
			_termResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Commerce.Admin.Channel";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-commerce-admin-channel-graphql/v1_0";
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
						"mutation#deleteAccountAddressChannel",
						new ObjectValuePair<>(
							AccountAddressChannelResourceImpl.class,
							"deleteAccountAddressChannel"));
					put(
						"mutation#deleteAccountAddressChannelBatch",
						new ObjectValuePair<>(
							AccountAddressChannelResourceImpl.class,
							"deleteAccountAddressChannelBatch"));
					put(
						"mutation#createAccountAddressByExternalReferenceCodeAccountAddressChannel",
						new ObjectValuePair<>(
							AccountAddressChannelResourceImpl.class,
							"postAccountAddressByExternalReferenceCodeAccountAddressChannel"));
					put(
						"mutation#createAccountAddressIdAccountAddressChannel",
						new ObjectValuePair<>(
							AccountAddressChannelResourceImpl.class,
							"postAccountAddressIdAccountAddressChannel"));
					put(
						"mutation#deleteCategoryDisplayPage",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"deleteCategoryDisplayPage"));
					put(
						"mutation#deleteCategoryDisplayPageBatch",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"deleteCategoryDisplayPageBatch"));
					put(
						"mutation#patchCategoryDisplayPage",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"patchCategoryDisplayPage"));
					put(
						"mutation#createChannelByExternalReferenceCodeCategoryDisplayPage",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"postChannelByExternalReferenceCodeCategoryDisplayPage"));
					put(
						"mutation#createChannelIdCategoryDisplayPage",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"postChannelIdCategoryDisplayPage"));
					put(
						"mutation#createChannelsPageExportBatch",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"postChannelsPageExportBatch"));
					put(
						"mutation#createChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "postChannel"));
					put(
						"mutation#createChannelBatch",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "postChannelBatch"));
					put(
						"mutation#deleteChannelByExternalReferenceCode",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"deleteChannelByExternalReferenceCode"));
					put(
						"mutation#patchChannelByExternalReferenceCode",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"patchChannelByExternalReferenceCode"));
					put(
						"mutation#updateChannelByExternalReferenceCode",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"putChannelByExternalReferenceCode"));
					put(
						"mutation#deleteChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "deleteChannel"));
					put(
						"mutation#deleteChannelBatch",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "deleteChannelBatch"));
					put(
						"mutation#patchChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "patchChannel"));
					put(
						"mutation#updateChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "putChannel"));
					put(
						"mutation#updateChannelBatch",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "putChannelBatch"));
					put(
						"mutation#deleteChannelAccount",
						new ObjectValuePair<>(
							ChannelAccountResourceImpl.class,
							"deleteChannelAccount"));
					put(
						"mutation#deleteChannelAccountBatch",
						new ObjectValuePair<>(
							ChannelAccountResourceImpl.class,
							"deleteChannelAccountBatch"));
					put(
						"mutation#createChannelByExternalReferenceCodeChannelAccount",
						new ObjectValuePair<>(
							ChannelAccountResourceImpl.class,
							"postChannelByExternalReferenceCodeChannelAccount"));
					put(
						"mutation#createChannelIdChannelAccount",
						new ObjectValuePair<>(
							ChannelAccountResourceImpl.class,
							"postChannelIdChannelAccount"));
					put(
						"mutation#deleteChannelByExternalReferenceCodeDefaultCategoryDisplayPage",
						new ObjectValuePair<>(
							DefaultCategoryDisplayPageResourceImpl.class,
							"deleteChannelByExternalReferenceCodeDefaultCategoryDisplayPage"));
					put(
						"mutation#createChannelByExternalReferenceCodeDefaultCategoryDisplayPage",
						new ObjectValuePair<>(
							DefaultCategoryDisplayPageResourceImpl.class,
							"postChannelByExternalReferenceCodeDefaultCategoryDisplayPage"));
					put(
						"mutation#deleteChannelIdDefaultCategoryDisplayPage",
						new ObjectValuePair<>(
							DefaultCategoryDisplayPageResourceImpl.class,
							"deleteChannelIdDefaultCategoryDisplayPage"));
					put(
						"mutation#createChannelIdDefaultCategoryDisplayPage",
						new ObjectValuePair<>(
							DefaultCategoryDisplayPageResourceImpl.class,
							"postChannelIdDefaultCategoryDisplayPage"));
					put(
						"mutation#deleteChannelByExternalReferenceCodeDefaultProductDisplayPage",
						new ObjectValuePair<>(
							DefaultProductDisplayPageResourceImpl.class,
							"deleteChannelByExternalReferenceCodeDefaultProductDisplayPage"));
					put(
						"mutation#createChannelByExternalReferenceCodeDefaultProductDisplayPage",
						new ObjectValuePair<>(
							DefaultProductDisplayPageResourceImpl.class,
							"postChannelByExternalReferenceCodeDefaultProductDisplayPage"));
					put(
						"mutation#deleteChannelIdDefaultProductDisplayPage",
						new ObjectValuePair<>(
							DefaultProductDisplayPageResourceImpl.class,
							"deleteChannelIdDefaultProductDisplayPage"));
					put(
						"mutation#createChannelIdDefaultProductDisplayPage",
						new ObjectValuePair<>(
							DefaultProductDisplayPageResourceImpl.class,
							"postChannelIdDefaultProductDisplayPage"));
					put(
						"mutation#deletePaymentMethodGroupRelOrderType",
						new ObjectValuePair<>(
							PaymentMethodGroupRelOrderTypeResourceImpl.class,
							"deletePaymentMethodGroupRelOrderType"));
					put(
						"mutation#deletePaymentMethodGroupRelOrderTypeBatch",
						new ObjectValuePair<>(
							PaymentMethodGroupRelOrderTypeResourceImpl.class,
							"deletePaymentMethodGroupRelOrderTypeBatch"));
					put(
						"mutation#createPaymentMethodGroupRelIdPaymentMethodGroupRelOrderType",
						new ObjectValuePair<>(
							PaymentMethodGroupRelOrderTypeResourceImpl.class,
							"postPaymentMethodGroupRelIdPaymentMethodGroupRelOrderType"));
					put(
						"mutation#deletePaymentMethodGroupRelTerm",
						new ObjectValuePair<>(
							PaymentMethodGroupRelTermResourceImpl.class,
							"deletePaymentMethodGroupRelTerm"));
					put(
						"mutation#deletePaymentMethodGroupRelTermBatch",
						new ObjectValuePair<>(
							PaymentMethodGroupRelTermResourceImpl.class,
							"deletePaymentMethodGroupRelTermBatch"));
					put(
						"mutation#createPaymentMethodGroupRelIdPaymentMethodGroupRelTerm",
						new ObjectValuePair<>(
							PaymentMethodGroupRelTermResourceImpl.class,
							"postPaymentMethodGroupRelIdPaymentMethodGroupRelTerm"));
					put(
						"mutation#createChannelByExternalReferenceCodeProductDisplayPage",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"postChannelByExternalReferenceCodeProductDisplayPage"));
					put(
						"mutation#createChannelIdProductDisplayPage",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"postChannelIdProductDisplayPage"));
					put(
						"mutation#deleteProductDisplayPage",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"deleteProductDisplayPage"));
					put(
						"mutation#deleteProductDisplayPageBatch",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"deleteProductDisplayPageBatch"));
					put(
						"mutation#patchProductDisplayPage",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"patchProductDisplayPage"));
					put(
						"mutation#deleteShippingFixedOptionOrderType",
						new ObjectValuePair<>(
							ShippingFixedOptionOrderTypeResourceImpl.class,
							"deleteShippingFixedOptionOrderType"));
					put(
						"mutation#deleteShippingFixedOptionOrderTypeBatch",
						new ObjectValuePair<>(
							ShippingFixedOptionOrderTypeResourceImpl.class,
							"deleteShippingFixedOptionOrderTypeBatch"));
					put(
						"mutation#createShippingFixedOptionIdShippingFixedOptionOrderType",
						new ObjectValuePair<>(
							ShippingFixedOptionOrderTypeResourceImpl.class,
							"postShippingFixedOptionIdShippingFixedOptionOrderType"));
					put(
						"mutation#deleteShippingFixedOptionTerm",
						new ObjectValuePair<>(
							ShippingFixedOptionTermResourceImpl.class,
							"deleteShippingFixedOptionTerm"));
					put(
						"mutation#deleteShippingFixedOptionTermBatch",
						new ObjectValuePair<>(
							ShippingFixedOptionTermResourceImpl.class,
							"deleteShippingFixedOptionTermBatch"));
					put(
						"mutation#createShippingFixedOptionIdShippingFixedOptionTerm",
						new ObjectValuePair<>(
							ShippingFixedOptionTermResourceImpl.class,
							"postShippingFixedOptionIdShippingFixedOptionTerm"));
					put(
						"mutation#createChannelShippingMethodsPageExportBatch",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"postChannelShippingMethodsPageExportBatch"));
					put(
						"mutation#createTaxCategoriesPageExportBatch",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class,
							"postTaxCategoriesPageExportBatch"));

					put(
						"query#channelAccountAccount",
						new ObjectValuePair<>(
							AccountResourceImpl.class,
							"getChannelAccountAccount"));
					put(
						"query#accountAddressByExternalReferenceCodeAccountAddressChannels",
						new ObjectValuePair<>(
							AccountAddressChannelResourceImpl.class,
							"getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage"));
					put(
						"query#accountAddressIdAccountAddressChannels",
						new ObjectValuePair<>(
							AccountAddressChannelResourceImpl.class,
							"getAccountAddressIdAccountAddressChannelsPage"));
					put(
						"query#categoryDisplayPage",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"getCategoryDisplayPage"));
					put(
						"query#channelByExternalReferenceCodeCategoryDisplayPages",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeCategoryDisplayPagesPage"));
					put(
						"query#channelIdCategoryDisplayPages",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"getChannelIdCategoryDisplayPagesPage"));
					put(
						"query#accountAddressChannelChannel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getAccountAddressChannelChannel"));
					put(
						"query#channels",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "getChannelsPage"));
					put(
						"query#channelByExternalReferenceCode",
						new ObjectValuePair<>(
							ChannelResourceImpl.class,
							"getChannelByExternalReferenceCode"));
					put(
						"query#channel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "getChannel"));
					put(
						"query#channelByExternalReferenceCodeChannelAccounts",
						new ObjectValuePair<>(
							ChannelAccountResourceImpl.class,
							"getChannelByExternalReferenceCodeChannelAccountsPage"));
					put(
						"query#channelIdChannelAccounts",
						new ObjectValuePair<>(
							ChannelAccountResourceImpl.class,
							"getChannelIdChannelAccountsPage"));
					put(
						"query#channelByExternalReferenceCodeDefaultCategoryDisplayPage",
						new ObjectValuePair<>(
							DefaultCategoryDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeDefaultCategoryDisplayPage"));
					put(
						"query#channelIdDefaultCategoryDisplayPage",
						new ObjectValuePair<>(
							DefaultCategoryDisplayPageResourceImpl.class,
							"getChannelIdDefaultCategoryDisplayPage"));
					put(
						"query#channelByExternalReferenceCodeDefaultProductDisplayPage",
						new ObjectValuePair<>(
							DefaultProductDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeDefaultProductDisplayPage"));
					put(
						"query#channelIdDefaultProductDisplayPage",
						new ObjectValuePair<>(
							DefaultProductDisplayPageResourceImpl.class,
							"getChannelIdDefaultProductDisplayPage"));
					put(
						"query#paymentMethodGroupRelOrderTypeOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getPaymentMethodGroupRelOrderTypeOrderType"));
					put(
						"query#shippingFixedOptionOrderTypeOrderType",
						new ObjectValuePair<>(
							OrderTypeResourceImpl.class,
							"getShippingFixedOptionOrderTypeOrderType"));
					put(
						"query#paymentMethodGroupRelIdPaymentMethodGroupRelOrderTypes",
						new ObjectValuePair<>(
							PaymentMethodGroupRelOrderTypeResourceImpl.class,
							"getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage"));
					put(
						"query#paymentMethodGroupRelIdPaymentMethodGroupRelTerms",
						new ObjectValuePair<>(
							PaymentMethodGroupRelTermResourceImpl.class,
							"getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage"));
					put(
						"query#channelByExternalReferenceCodeProductDisplayPages",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeProductDisplayPagesPage"));
					put(
						"query#channelIdProductDisplayPages",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"getChannelIdProductDisplayPagesPage"));
					put(
						"query#productDisplayPage",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"getProductDisplayPage"));
					put(
						"query#shippingFixedOptionIdShippingFixedOptionOrderTypes",
						new ObjectValuePair<>(
							ShippingFixedOptionOrderTypeResourceImpl.class,
							"getShippingFixedOptionIdShippingFixedOptionOrderTypesPage"));
					put(
						"query#shippingFixedOptionIdShippingFixedOptionTerms",
						new ObjectValuePair<>(
							ShippingFixedOptionTermResourceImpl.class,
							"getShippingFixedOptionIdShippingFixedOptionTermsPage"));
					put(
						"query#channelShippingMethods",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"getChannelShippingMethodsPage"));
					put(
						"query#taxCategories",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class,
							"getTaxCategoriesPage"));
					put(
						"query#taxCategory",
						new ObjectValuePair<>(
							TaxCategoryResourceImpl.class, "getTaxCategory"));
					put(
						"query#paymentMethodGroupRelTermTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getPaymentMethodGroupRelTermTerm"));
					put(
						"query#shippingFixedOptionTermTerm",
						new ObjectValuePair<>(
							TermResourceImpl.class,
							"getShippingFixedOptionTermTerm"));

					put(
						"query#Channel.accountAddressByExternalReferenceCodeAccountAddressChannels",
						new ObjectValuePair<>(
							AccountAddressChannelResourceImpl.class,
							"getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage"));
					put(
						"query#Channel.shippingMethods",
						new ObjectValuePair<>(
							ShippingMethodResourceImpl.class,
							"getChannelShippingMethodsPage"));
					put(
						"query#ChannelAccount.channel",
						new ObjectValuePair<>(
							ChannelResourceImpl.class, "getChannel"));
					put(
						"query#Channel.byExternalReferenceCodeChannelAccounts",
						new ObjectValuePair<>(
							ChannelAccountResourceImpl.class,
							"getChannelByExternalReferenceCodeChannelAccountsPage"));
					put(
						"query#Channel.byExternalReferenceCodeProductDisplayPages",
						new ObjectValuePair<>(
							ProductDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeProductDisplayPagesPage"));
					put(
						"query#Channel.byExternalReferenceCodeDefaultCategoryDisplayPage",
						new ObjectValuePair<>(
							DefaultCategoryDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeDefaultCategoryDisplayPage"));
					put(
						"query#Channel.byExternalReferenceCodeCategoryDisplayPages",
						new ObjectValuePair<>(
							CategoryDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeCategoryDisplayPagesPage"));
					put(
						"query#Channel.byExternalReferenceCodeDefaultProductDisplayPage",
						new ObjectValuePair<>(
							DefaultProductDisplayPageResourceImpl.class,
							"getChannelByExternalReferenceCodeDefaultProductDisplayPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountAddressChannelResource>
		_accountAddressChannelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CategoryDisplayPageResource>
		_categoryDisplayPageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ChannelAccountResource>
		_channelAccountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DefaultCategoryDisplayPageResource>
		_defaultCategoryDisplayPageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DefaultProductDisplayPageResource>
		_defaultProductDisplayPageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PaymentMethodGroupRelOrderTypeResource>
		_paymentMethodGroupRelOrderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PaymentMethodGroupRelTermResource>
		_paymentMethodGroupRelTermResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ProductDisplayPageResource>
		_productDisplayPageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShippingFixedOptionOrderTypeResource>
		_shippingFixedOptionOrderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShippingFixedOptionTermResource>
		_shippingFixedOptionTermResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ShippingMethodResource>
		_shippingMethodResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaxCategoryResource>
		_taxCategoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OrderTypeResource>
		_orderTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TermResource>
		_termResourceComponentServiceObjects;

}