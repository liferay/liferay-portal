/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Account;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Category;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Channel;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Currency;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.LinkedProduct;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Pin;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.RelatedProduct;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishListItem;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.AccountResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.CategoryResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.CurrencyResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.LinkedProductResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.MappedProductResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.PinResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductOptionResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductOptionValueResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductSpecificationResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.RelatedProductResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.WishListItemResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.WishListResource;
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

	public static void setAttachmentResourceComponentServiceObjects(
		ComponentServiceObjects<AttachmentResource>
			attachmentResourceComponentServiceObjects) {

		_attachmentResourceComponentServiceObjects =
			attachmentResourceComponentServiceObjects;
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

	public static void setCurrencyResourceComponentServiceObjects(
		ComponentServiceObjects<CurrencyResource>
			currencyResourceComponentServiceObjects) {

		_currencyResourceComponentServiceObjects =
			currencyResourceComponentServiceObjects;
	}

	public static void setLinkedProductResourceComponentServiceObjects(
		ComponentServiceObjects<LinkedProductResource>
			linkedProductResourceComponentServiceObjects) {

		_linkedProductResourceComponentServiceObjects =
			linkedProductResourceComponentServiceObjects;
	}

	public static void setMappedProductResourceComponentServiceObjects(
		ComponentServiceObjects<MappedProductResource>
			mappedProductResourceComponentServiceObjects) {

		_mappedProductResourceComponentServiceObjects =
			mappedProductResourceComponentServiceObjects;
	}

	public static void setPinResourceComponentServiceObjects(
		ComponentServiceObjects<PinResource>
			pinResourceComponentServiceObjects) {

		_pinResourceComponentServiceObjects =
			pinResourceComponentServiceObjects;
	}

	public static void setProductResourceComponentServiceObjects(
		ComponentServiceObjects<ProductResource>
			productResourceComponentServiceObjects) {

		_productResourceComponentServiceObjects =
			productResourceComponentServiceObjects;
	}

	public static void setProductOptionResourceComponentServiceObjects(
		ComponentServiceObjects<ProductOptionResource>
			productOptionResourceComponentServiceObjects) {

		_productOptionResourceComponentServiceObjects =
			productOptionResourceComponentServiceObjects;
	}

	public static void setProductOptionValueResourceComponentServiceObjects(
		ComponentServiceObjects<ProductOptionValueResource>
			productOptionValueResourceComponentServiceObjects) {

		_productOptionValueResourceComponentServiceObjects =
			productOptionValueResourceComponentServiceObjects;
	}

	public static void setProductSpecificationResourceComponentServiceObjects(
		ComponentServiceObjects<ProductSpecificationResource>
			productSpecificationResourceComponentServiceObjects) {

		_productSpecificationResourceComponentServiceObjects =
			productSpecificationResourceComponentServiceObjects;
	}

	public static void setRelatedProductResourceComponentServiceObjects(
		ComponentServiceObjects<RelatedProductResource>
			relatedProductResourceComponentServiceObjects) {

		_relatedProductResourceComponentServiceObjects =
			relatedProductResourceComponentServiceObjects;
	}

	public static void setSkuResourceComponentServiceObjects(
		ComponentServiceObjects<SkuResource>
			skuResourceComponentServiceObjects) {

		_skuResourceComponentServiceObjects =
			skuResourceComponentServiceObjects;
	}

	public static void setWishListResourceComponentServiceObjects(
		ComponentServiceObjects<WishListResource>
			wishListResourceComponentServiceObjects) {

		_wishListResourceComponentServiceObjects =
			wishListResourceComponentServiceObjects;
	}

	public static void setWishListItemResourceComponentServiceObjects(
		ComponentServiceObjects<WishListItemResource>
			wishListItemResourceComponentServiceObjects) {

		_wishListItemResourceComponentServiceObjects =
			wishListItemResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelAccounts(channelId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AccountPage channelAccounts(
			@GraphQLName("channelId") Long channelId,
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
				accountResource.getChannelAccountsPage(
					channelId, search,
					_filterBiFunction.apply(accountResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(accountResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductAttachments(accountId: ___, channelId: ___, page: ___, pageSize: ___, productId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage channelProductAttachments(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.getChannelProductAttachmentsPage(
					channelId, productId, accountId,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductImages(accountId: ___, channelId: ___, page: ___, pageSize: ___, productId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage channelProductImages(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.getChannelProductImagesPage(
					channelId, productId, accountId,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductCategories(channelId: ___, page: ___, pageSize: ___, productId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets a list of Category related to a Product.")
	public CategoryPage channelProductCategories(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryResource -> new CategoryPage(
				categoryResource.getChannelProductCategoriesPage(
					channelId, productId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channels(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
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
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeCurrencies(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves currencies from selected channel.")
	public CurrencyPage channelByExternalReferenceCodeCurrencies(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> new CurrencyPage(
				currencyResource.
					getChannelByExternalReferenceCodeCurrenciesPage(
						externalReferenceCode, search,
						_filterBiFunction.apply(currencyResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							currencyResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelCurrencies(channelId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves currencies from selected channel.")
	public CurrencyPage channelCurrencies(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> new CurrencyPage(
				currencyResource.getChannelCurrenciesPage(
					channelId, search,
					_filterBiFunction.apply(currencyResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(currencyResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductLinkedProducts(accountId: ___, channelId: ___, page: ___, pageSize: ___, productId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public LinkedProductPage channelProductLinkedProducts(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_linkedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			linkedProductResource -> new LinkedProductPage(
				linkedProductResource.getChannelProductLinkedProductsPage(
					channelId, productId, accountId,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductMappedProducts(accountId: ___, channelId: ___, currencyCode: ___, page: ___, pageSize: ___, productId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MappedProductPage channelProductMappedProducts(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("currencyCode") String currencyCode,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource -> new MappedProductPage(
				mappedProductResource.getChannelProductMappedProductsPage(
					channelId, productId, accountId, currencyCode, search,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						mappedProductResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePins(accountId: ___, channelExternalReferenceCode: ___, page: ___, pageSize: ___, productExternalReferenceCode: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PinPage
			channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePins(
				@GraphQLName("channelExternalReferenceCode") String
					channelExternalReferenceCode,
				@GraphQLName("productExternalReferenceCode") String
					productExternalReferenceCode,
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> new PinPage(
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, accountId, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(pinResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductPins(accountId: ___, channelId: ___, page: ___, pageSize: ___, productId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PinPage channelProductPins(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> new PinPage(
				pinResource.getChannelProductPinsPage(
					channelId, productId, accountId, search,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(pinResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProduct(accountId: ___, channelId: ___, productId: ___){attachments, catalogName, categories, createDate, customFields, description, expando, externalReferenceCode, id, images, linkedProducts, metaDescription, metaKeyword, metaTitle, modifiedDate, multipleOrderQuantity, name, productConfiguration, productId, productOptions, productSpecifications, productType, relatedProducts, shortDescription, skus, slug, tags, urlImage, urls}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves products from selected channel.")
	public Product channelProduct(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("accountId") Long accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.getChannelProduct(
				channelId, productId, accountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductByFriendlyUrlPath(accountId: ___, channelId: ___, friendlyUrlPath: ___){attachments, catalogName, categories, createDate, customFields, description, expando, externalReferenceCode, id, images, linkedProducts, metaDescription, metaKeyword, metaTitle, modifiedDate, multipleOrderQuantity, name, productConfiguration, productId, productOptions, productSpecifications, productType, relatedProducts, shortDescription, skus, slug, tags, urlImage, urls}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves products from selected channel.")
	public Product channelProductByFriendlyUrlPath(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath,
			@GraphQLName("accountId") Long accountId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.getChannelProductByFriendlyUrlPath(
					channelId, friendlyUrlPath, accountId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProducts(accountId: ___, channelId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves products from selected channel.")
	public ProductPage channelProducts(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> new ProductPage(
				productResource.getChannelProductsPage(
					channelId, accountId, search,
					_filterBiFunction.apply(productResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(productResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptions(channelExternalReferenceCode: ___, page: ___, pageSize: ___, productExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionPage
			channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptions(
				@GraphQLName("channelExternalReferenceCode") String
					channelExternalReferenceCode,
				@GraphQLName("productExternalReferenceCode") String
					productExternalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> new ProductOptionPage(
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductProductOptions(channelId: ___, page: ___, pageSize: ___, productId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionPage channelProductProductOptions(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> new ProductOptionPage(
				productOptionResource.getChannelProductProductOptionsPage(
					channelId, productId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValues(accountId: ___, channelExternalReferenceCode: ___, currencyCode: ___, page: ___, pageSize: ___, productExternalReferenceCode: ___, productOptionExternalReferenceCode: ___, productOptionValueId: ___, skuId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionValuePage
			channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValues(
				@GraphQLName("channelExternalReferenceCode") String
					channelExternalReferenceCode,
				@GraphQLName("productExternalReferenceCode") String
					productExternalReferenceCode,
				@GraphQLName("productOptionExternalReferenceCode") String
					productOptionExternalReferenceCode,
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("currencyCode") String currencyCode,
				@GraphQLName("productOptionValueId") Long productOptionValueId,
				@GraphQLName("skuId") Long skuId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource -> new ProductOptionValuePage(
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						productOptionExternalReferenceCode, accountId,
						currencyCode, productOptionValueId, skuId,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductProductOptionProductOptionValues(accountId: ___, channelId: ___, currencyCode: ___, page: ___, pageSize: ___, productId: ___, productOptionId: ___, productOptionValueId: ___, skuId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionValuePage
			channelProductProductOptionProductOptionValues(
				@GraphQLName("channelId") Long channelId,
				@GraphQLName("productId") Long productId,
				@GraphQLName("productOptionId") Long productOptionId,
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("currencyCode") String currencyCode,
				@GraphQLName("productOptionValueId") Long productOptionValueId,
				@GraphQLName("skuId") Long skuId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource -> new ProductOptionValuePage(
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						channelId, productId, productOptionId, accountId,
						currencyCode, productOptionValueId, skuId,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductSpecifications(channelExternalReferenceCode: ___, page: ___, pageSize: ___, productExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSpecificationPage
			channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductSpecifications(
				@GraphQLName("channelExternalReferenceCode") String
					channelExternalReferenceCode,
				@GraphQLName("productExternalReferenceCode") String
					productExternalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource -> new ProductSpecificationPage(
				productSpecificationResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductSpecificationsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductProductSpecifications(channelId: ___, page: ___, pageSize: ___, productId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSpecificationPage channelProductProductSpecifications(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource -> new ProductSpecificationPage(
				productSpecificationResource.
					getChannelProductProductSpecificationsPage(
						channelId, productId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductRelatedProducts(channelId: ___, page: ___, pageSize: ___, productId: ___, type: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Gets a list of Related Products of a Product.")
	public RelatedProductPage channelProductRelatedProducts(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("type") String type,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource -> new RelatedProductPage(
				relatedProductResource.getChannelProductRelatedProductsPage(
					channelId, productId, type,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode(accountId: ___, channelExternalReferenceCode: ___, currencyCode: ___, productExternalReferenceCode: ___, skuExternalReferenceCode: ___){DDMOptions, allowedOrderQuantities, availability, backOrderAllowed, customFields, depth, discontinued, discontinuedDate, displayDate, displayDiscountLevels, expirationDate, externalReferenceCode, gtin, height, id, incomingQuantityLabel, manufacturerPartNumber, maxOrderQuantity, minOrderQuantity, neverExpire, price, productConfiguration, productId, published, purchasable, replacementSku, replacementSkuExternalReferenceCode, replacementSkuId, sku, skuOptions, skuUnitOfMeasures, tierPrices, weight, width}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a product from selected channel.")
	public Sku
			channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode(
				@GraphQLName("channelExternalReferenceCode") String
					channelExternalReferenceCode,
				@GraphQLName("productExternalReferenceCode") String
					productExternalReferenceCode,
				@GraphQLName("skuExternalReferenceCode") String
					skuExternalReferenceCode,
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("currencyCode") String currencyCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource ->
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode(
						channelExternalReferenceCode,
						productExternalReferenceCode, skuExternalReferenceCode,
						accountId, currencyCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkus(accountId: ___, channelExternalReferenceCode: ___, currencyCode: ___, page: ___, pageSize: ___, productExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves products from selected channel.")
	public SkuPage
			channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkus(
				@GraphQLName("channelExternalReferenceCode") String
					channelExternalReferenceCode,
				@GraphQLName("productExternalReferenceCode") String
					productExternalReferenceCode,
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("currencyCode") String currencyCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> new SkuPage(
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, accountId, currencyCode,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductSku(accountId: ___, channelId: ___, currencyCode: ___, productId: ___, skuId: ___){DDMOptions, allowedOrderQuantities, availability, backOrderAllowed, customFields, depth, discontinued, discontinuedDate, displayDate, displayDiscountLevels, expirationDate, externalReferenceCode, gtin, height, id, incomingQuantityLabel, manufacturerPartNumber, maxOrderQuantity, minOrderQuantity, neverExpire, price, productConfiguration, productId, published, purchasable, replacementSku, replacementSkuExternalReferenceCode, replacementSkuId, sku, skuOptions, skuUnitOfMeasures, tierPrices, weight, width}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a product from selected channel.")
	public Sku channelProductSku(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("skuId") Long skuId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("currencyCode") String currencyCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.getChannelProductSku(
				channelId, productId, skuId, accountId, currencyCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelProductSkus(accountId: ___, channelId: ___, currencyCode: ___, page: ___, pageSize: ___, productId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves products from selected channel.")
	public SkuPage channelProductSkus(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("productId") Long productId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("currencyCode") String currencyCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> new SkuPage(
				skuResource.getChannelProductSkusPage(
					channelId, productId, accountId, currencyCode,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelByExternalReferenceCodeWishLists(accountId: ___, currencyCode: ___, externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves wishlists for a given channel.")
	public WishListPage channelByExternalReferenceCodeWishLists(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("currencyCode") String currencyCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListResource -> new WishListPage(
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					externalReferenceCode, accountId, currencyCode,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {channelWishLists(accountId: ___, channelId: ___, currencyCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves wishlists for a given channel.")
	public WishListPage channelWishLists(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("currencyCode") String currencyCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListResource -> new WishListPage(
				wishListResource.getChannelWishListsPage(
					channelId, accountId, currencyCode,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wishList(wishListId: ___){defaultWishList, id, name, wishListItems}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a wishlist by wishListId.")
	public WishList wishList(@GraphQLName("wishListId") Long wishListId)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListResource -> wishListResource.getWishList(wishListId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wishListItem(accountId: ___, currencyCode: ___, wishListItemId: ___){finalPrice, friendlyURL, icon, id, productId, productName, skuId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves wishlist item by wishListItemId for a specific channel and account"
	)
	public WishListItem wishListItem(
			@GraphQLName("wishListItemId") Long wishListItemId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("currencyCode") String currencyCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListItemResource -> wishListItemResource.getWishListItem(
				wishListItemId, accountId, currencyCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wishlistWishListWishListItems(accountId: ___, currencyCode: ___, page: ___, pageSize: ___, wishListId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves wishlist items by wishListId for a specific channel and account"
	)
	public WishListItemPage wishlistWishListWishListItems(
			@GraphQLName("wishListId") Long wishListId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("currencyCode") String currencyCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListItemResource -> new WishListItemPage(
				wishListItemResource.getWishlistWishListWishListItemsPage(
					wishListId, accountId, currencyCode,
					Pagination.of(page, pageSize))));
	}

	@GraphQLTypeExtension(WishList.class)
	public class GetWishlistWishListWishListItemsPageTypeExtension {

		public GetWishlistWishListWishListItemsPageTypeExtension(
			WishList wishList) {

			_wishList = wishList;
		}

		@GraphQLField(
			description = "Retrieves wishlist items by wishListId for a specific channel and account"
		)
		public WishListItemPage wishlistWishListWishListItems(
				@GraphQLName("accountId") Long accountId,
				@GraphQLName("currencyCode") String currencyCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_wishListItemResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wishListItemResource -> new WishListItemPage(
					wishListItemResource.getWishlistWishListWishListItemsPage(
						_wishList.getId(), accountId, currencyCode,
						Pagination.of(page, pageSize))));
		}

		private WishList _wishList;

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

	@GraphQLName("AttachmentPage")
	public class AttachmentPage {

		public AttachmentPage(Page attachmentPage) {
			actions = attachmentPage.getActions();

			items = attachmentPage.getItems();
			lastPage = attachmentPage.getLastPage();
			page = attachmentPage.getPage();
			pageSize = attachmentPage.getPageSize();
			totalCount = attachmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Attachment> items;

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

	@GraphQLName("CurrencyPage")
	public class CurrencyPage {

		public CurrencyPage(Page currencyPage) {
			actions = currencyPage.getActions();

			items = currencyPage.getItems();
			lastPage = currencyPage.getLastPage();
			page = currencyPage.getPage();
			pageSize = currencyPage.getPageSize();
			totalCount = currencyPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Currency> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("LinkedProductPage")
	public class LinkedProductPage {

		public LinkedProductPage(Page linkedProductPage) {
			actions = linkedProductPage.getActions();

			items = linkedProductPage.getItems();
			lastPage = linkedProductPage.getLastPage();
			page = linkedProductPage.getPage();
			pageSize = linkedProductPage.getPageSize();
			totalCount = linkedProductPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<LinkedProduct> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("MappedProductPage")
	public class MappedProductPage {

		public MappedProductPage(Page mappedProductPage) {
			actions = mappedProductPage.getActions();

			items = mappedProductPage.getItems();
			lastPage = mappedProductPage.getLastPage();
			page = mappedProductPage.getPage();
			pageSize = mappedProductPage.getPageSize();
			totalCount = mappedProductPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<MappedProduct> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PinPage")
	public class PinPage {

		public PinPage(Page pinPage) {
			actions = pinPage.getActions();

			items = pinPage.getItems();
			lastPage = pinPage.getLastPage();
			page = pinPage.getPage();
			pageSize = pinPage.getPageSize();
			totalCount = pinPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Pin> items;

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

	@GraphQLName("ProductOptionPage")
	public class ProductOptionPage {

		public ProductOptionPage(Page productOptionPage) {
			actions = productOptionPage.getActions();

			items = productOptionPage.getItems();
			lastPage = productOptionPage.getLastPage();
			page = productOptionPage.getPage();
			pageSize = productOptionPage.getPageSize();
			totalCount = productOptionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductOption> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductOptionValuePage")
	public class ProductOptionValuePage {

		public ProductOptionValuePage(Page productOptionValuePage) {
			actions = productOptionValuePage.getActions();

			items = productOptionValuePage.getItems();
			lastPage = productOptionValuePage.getLastPage();
			page = productOptionValuePage.getPage();
			pageSize = productOptionValuePage.getPageSize();
			totalCount = productOptionValuePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductOptionValue> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductSpecificationPage")
	public class ProductSpecificationPage {

		public ProductSpecificationPage(Page productSpecificationPage) {
			actions = productSpecificationPage.getActions();

			items = productSpecificationPage.getItems();
			lastPage = productSpecificationPage.getLastPage();
			page = productSpecificationPage.getPage();
			pageSize = productSpecificationPage.getPageSize();
			totalCount = productSpecificationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductSpecification> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("RelatedProductPage")
	public class RelatedProductPage {

		public RelatedProductPage(Page relatedProductPage) {
			actions = relatedProductPage.getActions();

			items = relatedProductPage.getItems();
			lastPage = relatedProductPage.getLastPage();
			page = relatedProductPage.getPage();
			pageSize = relatedProductPage.getPageSize();
			totalCount = relatedProductPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<RelatedProduct> items;

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

	@GraphQLName("WishListPage")
	public class WishListPage {

		public WishListPage(Page wishListPage) {
			actions = wishListPage.getActions();

			items = wishListPage.getItems();
			lastPage = wishListPage.getLastPage();
			page = wishListPage.getPage();
			pageSize = wishListPage.getPageSize();
			totalCount = wishListPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<WishList> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WishListItemPage")
	public class WishListItemPage {

		public WishListItemPage(Page wishListItemPage) {
			actions = wishListItemPage.getActions();

			items = wishListItemPage.getItems();
			lastPage = wishListItemPage.getLastPage();
			page = wishListItemPage.getPage();
			pageSize = wishListItemPage.getPageSize();
			totalCount = wishListItemPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<WishListItem> items;

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

	private void _populateResourceContext(AttachmentResource attachmentResource)
		throws Exception {

		attachmentResource.setContextAcceptLanguage(_acceptLanguage);
		attachmentResource.setContextCompany(_company);
		attachmentResource.setContextHttpServletRequest(_httpServletRequest);
		attachmentResource.setContextHttpServletResponse(_httpServletResponse);
		attachmentResource.setContextUriInfo(_uriInfo);
		attachmentResource.setContextUser(_user);
		attachmentResource.setGroupLocalService(_groupLocalService);
		attachmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		attachmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		attachmentResource.setRoleLocalService(_roleLocalService);
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

	private void _populateResourceContext(CurrencyResource currencyResource)
		throws Exception {

		currencyResource.setContextAcceptLanguage(_acceptLanguage);
		currencyResource.setContextCompany(_company);
		currencyResource.setContextHttpServletRequest(_httpServletRequest);
		currencyResource.setContextHttpServletResponse(_httpServletResponse);
		currencyResource.setContextUriInfo(_uriInfo);
		currencyResource.setContextUser(_user);
		currencyResource.setGroupLocalService(_groupLocalService);
		currencyResource.setResourceActionLocalService(
			_resourceActionLocalService);
		currencyResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		currencyResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			LinkedProductResource linkedProductResource)
		throws Exception {

		linkedProductResource.setContextAcceptLanguage(_acceptLanguage);
		linkedProductResource.setContextCompany(_company);
		linkedProductResource.setContextHttpServletRequest(_httpServletRequest);
		linkedProductResource.setContextHttpServletResponse(
			_httpServletResponse);
		linkedProductResource.setContextUriInfo(_uriInfo);
		linkedProductResource.setContextUser(_user);
		linkedProductResource.setGroupLocalService(_groupLocalService);
		linkedProductResource.setResourceActionLocalService(
			_resourceActionLocalService);
		linkedProductResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		linkedProductResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			MappedProductResource mappedProductResource)
		throws Exception {

		mappedProductResource.setContextAcceptLanguage(_acceptLanguage);
		mappedProductResource.setContextCompany(_company);
		mappedProductResource.setContextHttpServletRequest(_httpServletRequest);
		mappedProductResource.setContextHttpServletResponse(
			_httpServletResponse);
		mappedProductResource.setContextUriInfo(_uriInfo);
		mappedProductResource.setContextUser(_user);
		mappedProductResource.setGroupLocalService(_groupLocalService);
		mappedProductResource.setResourceActionLocalService(
			_resourceActionLocalService);
		mappedProductResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		mappedProductResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(PinResource pinResource)
		throws Exception {

		pinResource.setContextAcceptLanguage(_acceptLanguage);
		pinResource.setContextCompany(_company);
		pinResource.setContextHttpServletRequest(_httpServletRequest);
		pinResource.setContextHttpServletResponse(_httpServletResponse);
		pinResource.setContextUriInfo(_uriInfo);
		pinResource.setContextUser(_user);
		pinResource.setGroupLocalService(_groupLocalService);
		pinResource.setResourceActionLocalService(_resourceActionLocalService);
		pinResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		pinResource.setRoleLocalService(_roleLocalService);
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
			ProductOptionResource productOptionResource)
		throws Exception {

		productOptionResource.setContextAcceptLanguage(_acceptLanguage);
		productOptionResource.setContextCompany(_company);
		productOptionResource.setContextHttpServletRequest(_httpServletRequest);
		productOptionResource.setContextHttpServletResponse(
			_httpServletResponse);
		productOptionResource.setContextUriInfo(_uriInfo);
		productOptionResource.setContextUser(_user);
		productOptionResource.setGroupLocalService(_groupLocalService);
		productOptionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productOptionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productOptionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductOptionValueResource productOptionValueResource)
		throws Exception {

		productOptionValueResource.setContextAcceptLanguage(_acceptLanguage);
		productOptionValueResource.setContextCompany(_company);
		productOptionValueResource.setContextHttpServletRequest(
			_httpServletRequest);
		productOptionValueResource.setContextHttpServletResponse(
			_httpServletResponse);
		productOptionValueResource.setContextUriInfo(_uriInfo);
		productOptionValueResource.setContextUser(_user);
		productOptionValueResource.setGroupLocalService(_groupLocalService);
		productOptionValueResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productOptionValueResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productOptionValueResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductSpecificationResource productSpecificationResource)
		throws Exception {

		productSpecificationResource.setContextAcceptLanguage(_acceptLanguage);
		productSpecificationResource.setContextCompany(_company);
		productSpecificationResource.setContextHttpServletRequest(
			_httpServletRequest);
		productSpecificationResource.setContextHttpServletResponse(
			_httpServletResponse);
		productSpecificationResource.setContextUriInfo(_uriInfo);
		productSpecificationResource.setContextUser(_user);
		productSpecificationResource.setGroupLocalService(_groupLocalService);
		productSpecificationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productSpecificationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productSpecificationResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			RelatedProductResource relatedProductResource)
		throws Exception {

		relatedProductResource.setContextAcceptLanguage(_acceptLanguage);
		relatedProductResource.setContextCompany(_company);
		relatedProductResource.setContextHttpServletRequest(
			_httpServletRequest);
		relatedProductResource.setContextHttpServletResponse(
			_httpServletResponse);
		relatedProductResource.setContextUriInfo(_uriInfo);
		relatedProductResource.setContextUser(_user);
		relatedProductResource.setGroupLocalService(_groupLocalService);
		relatedProductResource.setResourceActionLocalService(
			_resourceActionLocalService);
		relatedProductResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		relatedProductResource.setRoleLocalService(_roleLocalService);
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

	private void _populateResourceContext(WishListResource wishListResource)
		throws Exception {

		wishListResource.setContextAcceptLanguage(_acceptLanguage);
		wishListResource.setContextCompany(_company);
		wishListResource.setContextHttpServletRequest(_httpServletRequest);
		wishListResource.setContextHttpServletResponse(_httpServletResponse);
		wishListResource.setContextUriInfo(_uriInfo);
		wishListResource.setContextUser(_user);
		wishListResource.setGroupLocalService(_groupLocalService);
		wishListResource.setResourceActionLocalService(
			_resourceActionLocalService);
		wishListResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		wishListResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			WishListItemResource wishListItemResource)
		throws Exception {

		wishListItemResource.setContextAcceptLanguage(_acceptLanguage);
		wishListItemResource.setContextCompany(_company);
		wishListItemResource.setContextHttpServletRequest(_httpServletRequest);
		wishListItemResource.setContextHttpServletResponse(
			_httpServletResponse);
		wishListItemResource.setContextUriInfo(_uriInfo);
		wishListItemResource.setContextUser(_user);
		wishListItemResource.setGroupLocalService(_groupLocalService);
		wishListItemResource.setResourceActionLocalService(
			_resourceActionLocalService);
		wishListItemResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		wishListItemResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AccountResource>
		_accountResourceComponentServiceObjects;
	private static ComponentServiceObjects<AttachmentResource>
		_attachmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<CategoryResource>
		_categoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<ChannelResource>
		_channelResourceComponentServiceObjects;
	private static ComponentServiceObjects<CurrencyResource>
		_currencyResourceComponentServiceObjects;
	private static ComponentServiceObjects<LinkedProductResource>
		_linkedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<MappedProductResource>
		_mappedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<PinResource>
		_pinResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductResource>
		_productResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductOptionResource>
		_productOptionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductOptionValueResource>
		_productOptionValueResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductSpecificationResource>
		_productSpecificationResourceComponentServiceObjects;
	private static ComponentServiceObjects<RelatedProductResource>
		_relatedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuResource>
		_skuResourceComponentServiceObjects;
	private static ComponentServiceObjects<WishListResource>
		_wishListResourceComponentServiceObjects;
	private static ComponentServiceObjects<WishListItemResource>
		_wishListItemResourceComponentServiceObjects;

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