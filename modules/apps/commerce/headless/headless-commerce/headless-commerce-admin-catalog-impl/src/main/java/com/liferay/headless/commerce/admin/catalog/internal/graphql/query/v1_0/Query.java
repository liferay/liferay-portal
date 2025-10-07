/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.graphql.query.v1_0;

import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Category;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Currency;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Diagram;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.GroupedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.LinkedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.LowStockAction;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionCategory;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Pin;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductAccountGroup;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductChannel;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationList;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListAccount;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListAccountGroup;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListChannel;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListOrderType;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroup;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroupProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductShippingConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSubscriptionConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.RelatedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuSubscriptionConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CatalogResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CategoryResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CurrencyResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.DiagramResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.GroupedProductResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.LinkedProductResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.LowStockActionResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.MappedProductResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.OptionCategoryResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.OptionResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.OptionValueResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.PinResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductAccountGroupResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductChannelResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationListAccountGroupResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationListAccountResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationListChannelResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationListOrderTypeResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationListResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductGroupProductResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductGroupResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductOptionResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductOptionValueResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductShippingConfigurationResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductSpecificationResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductSubscriptionConfigurationResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductTaxConfigurationResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductVirtualSettingsFileEntryResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductVirtualSettingsResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.RelatedProductResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuSubscriptionConfigurationResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuUnitOfMeasureResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuVirtualSettingsFileEntryResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuVirtualSettingsResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SpecificationResource;
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

	public static void setAttachmentResourceComponentServiceObjects(
		ComponentServiceObjects<AttachmentResource>
			attachmentResourceComponentServiceObjects) {

		_attachmentResourceComponentServiceObjects =
			attachmentResourceComponentServiceObjects;
	}

	public static void setCatalogResourceComponentServiceObjects(
		ComponentServiceObjects<CatalogResource>
			catalogResourceComponentServiceObjects) {

		_catalogResourceComponentServiceObjects =
			catalogResourceComponentServiceObjects;
	}

	public static void setCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<CategoryResource>
			categoryResourceComponentServiceObjects) {

		_categoryResourceComponentServiceObjects =
			categoryResourceComponentServiceObjects;
	}

	public static void setCurrencyResourceComponentServiceObjects(
		ComponentServiceObjects<CurrencyResource>
			currencyResourceComponentServiceObjects) {

		_currencyResourceComponentServiceObjects =
			currencyResourceComponentServiceObjects;
	}

	public static void setDiagramResourceComponentServiceObjects(
		ComponentServiceObjects<DiagramResource>
			diagramResourceComponentServiceObjects) {

		_diagramResourceComponentServiceObjects =
			diagramResourceComponentServiceObjects;
	}

	public static void setGroupedProductResourceComponentServiceObjects(
		ComponentServiceObjects<GroupedProductResource>
			groupedProductResourceComponentServiceObjects) {

		_groupedProductResourceComponentServiceObjects =
			groupedProductResourceComponentServiceObjects;
	}

	public static void setLinkedProductResourceComponentServiceObjects(
		ComponentServiceObjects<LinkedProductResource>
			linkedProductResourceComponentServiceObjects) {

		_linkedProductResourceComponentServiceObjects =
			linkedProductResourceComponentServiceObjects;
	}

	public static void setListTypeDefinitionResourceComponentServiceObjects(
		ComponentServiceObjects<ListTypeDefinitionResource>
			listTypeDefinitionResourceComponentServiceObjects) {

		_listTypeDefinitionResourceComponentServiceObjects =
			listTypeDefinitionResourceComponentServiceObjects;
	}

	public static void setLowStockActionResourceComponentServiceObjects(
		ComponentServiceObjects<LowStockActionResource>
			lowStockActionResourceComponentServiceObjects) {

		_lowStockActionResourceComponentServiceObjects =
			lowStockActionResourceComponentServiceObjects;
	}

	public static void setMappedProductResourceComponentServiceObjects(
		ComponentServiceObjects<MappedProductResource>
			mappedProductResourceComponentServiceObjects) {

		_mappedProductResourceComponentServiceObjects =
			mappedProductResourceComponentServiceObjects;
	}

	public static void setOptionResourceComponentServiceObjects(
		ComponentServiceObjects<OptionResource>
			optionResourceComponentServiceObjects) {

		_optionResourceComponentServiceObjects =
			optionResourceComponentServiceObjects;
	}

	public static void setOptionCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<OptionCategoryResource>
			optionCategoryResourceComponentServiceObjects) {

		_optionCategoryResourceComponentServiceObjects =
			optionCategoryResourceComponentServiceObjects;
	}

	public static void setOptionValueResourceComponentServiceObjects(
		ComponentServiceObjects<OptionValueResource>
			optionValueResourceComponentServiceObjects) {

		_optionValueResourceComponentServiceObjects =
			optionValueResourceComponentServiceObjects;
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

	public static void setProductAccountGroupResourceComponentServiceObjects(
		ComponentServiceObjects<ProductAccountGroupResource>
			productAccountGroupResourceComponentServiceObjects) {

		_productAccountGroupResourceComponentServiceObjects =
			productAccountGroupResourceComponentServiceObjects;
	}

	public static void setProductChannelResourceComponentServiceObjects(
		ComponentServiceObjects<ProductChannelResource>
			productChannelResourceComponentServiceObjects) {

		_productChannelResourceComponentServiceObjects =
			productChannelResourceComponentServiceObjects;
	}

	public static void setProductConfigurationResourceComponentServiceObjects(
		ComponentServiceObjects<ProductConfigurationResource>
			productConfigurationResourceComponentServiceObjects) {

		_productConfigurationResourceComponentServiceObjects =
			productConfigurationResourceComponentServiceObjects;
	}

	public static void
		setProductConfigurationListResourceComponentServiceObjects(
			ComponentServiceObjects<ProductConfigurationListResource>
				productConfigurationListResourceComponentServiceObjects) {

		_productConfigurationListResourceComponentServiceObjects =
			productConfigurationListResourceComponentServiceObjects;
	}

	public static void
		setProductConfigurationListAccountResourceComponentServiceObjects(
			ComponentServiceObjects<ProductConfigurationListAccountResource>
				productConfigurationListAccountResourceComponentServiceObjects) {

		_productConfigurationListAccountResourceComponentServiceObjects =
			productConfigurationListAccountResourceComponentServiceObjects;
	}

	public static void
		setProductConfigurationListAccountGroupResourceComponentServiceObjects(
			ComponentServiceObjects
				<ProductConfigurationListAccountGroupResource>
					productConfigurationListAccountGroupResourceComponentServiceObjects) {

		_productConfigurationListAccountGroupResourceComponentServiceObjects =
			productConfigurationListAccountGroupResourceComponentServiceObjects;
	}

	public static void
		setProductConfigurationListChannelResourceComponentServiceObjects(
			ComponentServiceObjects<ProductConfigurationListChannelResource>
				productConfigurationListChannelResourceComponentServiceObjects) {

		_productConfigurationListChannelResourceComponentServiceObjects =
			productConfigurationListChannelResourceComponentServiceObjects;
	}

	public static void
		setProductConfigurationListOrderTypeResourceComponentServiceObjects(
			ComponentServiceObjects<ProductConfigurationListOrderTypeResource>
				productConfigurationListOrderTypeResourceComponentServiceObjects) {

		_productConfigurationListOrderTypeResourceComponentServiceObjects =
			productConfigurationListOrderTypeResourceComponentServiceObjects;
	}

	public static void setProductGroupResourceComponentServiceObjects(
		ComponentServiceObjects<ProductGroupResource>
			productGroupResourceComponentServiceObjects) {

		_productGroupResourceComponentServiceObjects =
			productGroupResourceComponentServiceObjects;
	}

	public static void setProductGroupProductResourceComponentServiceObjects(
		ComponentServiceObjects<ProductGroupProductResource>
			productGroupProductResourceComponentServiceObjects) {

		_productGroupProductResourceComponentServiceObjects =
			productGroupProductResourceComponentServiceObjects;
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

	public static void
		setProductShippingConfigurationResourceComponentServiceObjects(
			ComponentServiceObjects<ProductShippingConfigurationResource>
				productShippingConfigurationResourceComponentServiceObjects) {

		_productShippingConfigurationResourceComponentServiceObjects =
			productShippingConfigurationResourceComponentServiceObjects;
	}

	public static void setProductSpecificationResourceComponentServiceObjects(
		ComponentServiceObjects<ProductSpecificationResource>
			productSpecificationResourceComponentServiceObjects) {

		_productSpecificationResourceComponentServiceObjects =
			productSpecificationResourceComponentServiceObjects;
	}

	public static void
		setProductSubscriptionConfigurationResourceComponentServiceObjects(
			ComponentServiceObjects<ProductSubscriptionConfigurationResource>
				productSubscriptionConfigurationResourceComponentServiceObjects) {

		_productSubscriptionConfigurationResourceComponentServiceObjects =
			productSubscriptionConfigurationResourceComponentServiceObjects;
	}

	public static void
		setProductTaxConfigurationResourceComponentServiceObjects(
			ComponentServiceObjects<ProductTaxConfigurationResource>
				productTaxConfigurationResourceComponentServiceObjects) {

		_productTaxConfigurationResourceComponentServiceObjects =
			productTaxConfigurationResourceComponentServiceObjects;
	}

	public static void setProductVirtualSettingsResourceComponentServiceObjects(
		ComponentServiceObjects<ProductVirtualSettingsResource>
			productVirtualSettingsResourceComponentServiceObjects) {

		_productVirtualSettingsResourceComponentServiceObjects =
			productVirtualSettingsResourceComponentServiceObjects;
	}

	public static void
		setProductVirtualSettingsFileEntryResourceComponentServiceObjects(
			ComponentServiceObjects<ProductVirtualSettingsFileEntryResource>
				productVirtualSettingsFileEntryResourceComponentServiceObjects) {

		_productVirtualSettingsFileEntryResourceComponentServiceObjects =
			productVirtualSettingsFileEntryResourceComponentServiceObjects;
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

	public static void
		setSkuSubscriptionConfigurationResourceComponentServiceObjects(
			ComponentServiceObjects<SkuSubscriptionConfigurationResource>
				skuSubscriptionConfigurationResourceComponentServiceObjects) {

		_skuSubscriptionConfigurationResourceComponentServiceObjects =
			skuSubscriptionConfigurationResourceComponentServiceObjects;
	}

	public static void setSkuUnitOfMeasureResourceComponentServiceObjects(
		ComponentServiceObjects<SkuUnitOfMeasureResource>
			skuUnitOfMeasureResourceComponentServiceObjects) {

		_skuUnitOfMeasureResourceComponentServiceObjects =
			skuUnitOfMeasureResourceComponentServiceObjects;
	}

	public static void setSkuVirtualSettingsResourceComponentServiceObjects(
		ComponentServiceObjects<SkuVirtualSettingsResource>
			skuVirtualSettingsResourceComponentServiceObjects) {

		_skuVirtualSettingsResourceComponentServiceObjects =
			skuVirtualSettingsResourceComponentServiceObjects;
	}

	public static void
		setSkuVirtualSettingsFileEntryResourceComponentServiceObjects(
			ComponentServiceObjects<SkuVirtualSettingsFileEntryResource>
				skuVirtualSettingsFileEntryResourceComponentServiceObjects) {

		_skuVirtualSettingsFileEntryResourceComponentServiceObjects =
			skuVirtualSettingsFileEntryResourceComponentServiceObjects;
	}

	public static void setSpecificationResourceComponentServiceObjects(
		ComponentServiceObjects<SpecificationResource>
			specificationResourceComponentServiceObjects) {

		_specificationResourceComponentServiceObjects =
			specificationResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {attachmentByExternalReferenceCode(externalReferenceCode: ___){attachment, cdnEnabled, cdnURL, contentType, customFields, displayDate, expirationDate, externalReferenceCode, fileEntryExternalReferenceCode, fileEntryGroupExternalReferenceCode, fileEntryId, galleryEnabled, id, neverExpire, options, priority, src, tags, title, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Attachment attachmentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.getAttachmentByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeAttachments(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage productByExternalReferenceCodeAttachments(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.
					getProductByExternalReferenceCodeAttachmentsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeImages(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage productByExternalReferenceCodeImages(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.getProductByExternalReferenceCodeImagesPage(
					externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdAttachments(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage productIdAttachments(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.getProductIdAttachmentsPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdImages(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AttachmentPage productIdImages(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> new AttachmentPage(
				attachmentResource.getProductIdImagesPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {catalog(id: ___){accountId, actions, currencyCode, currencyExternalReferenceCode, currencyId, defaultLanguageId, externalReferenceCode, id, name, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Catalog catalog(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.getCatalog(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {catalogByExternalReferenceCode(externalReferenceCode: ___){accountId, actions, currencyCode, currencyExternalReferenceCode, currencyId, defaultLanguageId, externalReferenceCode, id, name, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Catalog catalogByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource ->
				catalogResource.getCatalogByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {catalogs(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CatalogPage catalogs(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> new CatalogPage(
				catalogResource.getCatalogsPage(
					search,
					_filterBiFunction.apply(catalogResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(catalogResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeCatalog(externalReferenceCode: ___, page: ___, pageSize: ___){accountId, actions, currencyCode, currencyExternalReferenceCode, currencyId, defaultLanguageId, externalReferenceCode, id, name, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Catalog productByExternalReferenceCodeCatalog(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource ->
				catalogResource.getProductByExternalReferenceCodeCatalog(
					externalReferenceCode, Pagination.of(page, pageSize)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdCatalog(id: ___, page: ___, pageSize: ___){accountId, actions, currencyCode, currencyExternalReferenceCode, currencyId, defaultLanguageId, externalReferenceCode, id, name, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Catalog productIdCatalog(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.getProductIdCatalog(
				id, Pagination.of(page, pageSize)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeCategories(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CategoryPage productByExternalReferenceCodeCategories(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryResource -> new CategoryPage(
				categoryResource.
					getProductByExternalReferenceCodeCategoriesPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdCategories(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CategoryPage productIdCategories(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryResource -> new CategoryPage(
				categoryResource.getProductIdCategoriesPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {currencies(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CurrencyPage currencies(
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
				currencyResource.getCurrenciesPage(
					search,
					_filterBiFunction.apply(currencyResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(currencyResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {currency(id: ___){active, code, externalReferenceCode, formatPattern, id, maxFractionDigits, minFractionDigits, name, primary, priority, rate, roundingMode, symbol}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Currency currency(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> currencyResource.getCurrency(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {currencyByExternalReferenceCode(externalReferenceCode: ___){active, code, externalReferenceCode, formatPattern, id, maxFractionDigits, minFractionDigits, name, primary, priority, rate, roundingMode, symbol}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Currency currencyByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource ->
				currencyResource.getCurrencyByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeDiagram(externalReferenceCode: ___){attachmentBase64, color, id, imageId, imageURL, productExternalReferenceCode, productId, radius, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Diagram productByExternalReferenceCodeDiagram(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_diagramResourceComponentServiceObjects,
			this::_populateResourceContext,
			diagramResource ->
				diagramResource.getProductByExternalReferenceCodeDiagram(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdDiagram(id: ___){attachmentBase64, color, id, imageId, imageURL, productExternalReferenceCode, productId, radius, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Diagram productIdDiagram(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_diagramResourceComponentServiceObjects,
			this::_populateResourceContext,
			diagramResource -> diagramResource.getProductIdDiagram(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeGroupedProducts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public GroupedProductPage productByExternalReferenceCodeGroupedProducts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource -> new GroupedProductPage(
				groupedProductResource.
					getProductByExternalReferenceCodeGroupedProductsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdGroupedProducts(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public GroupedProductPage productIdGroupedProducts(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource -> new GroupedProductPage(
				groupedProductResource.getProductIdGroupedProductsPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdLinkedProducts(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public LinkedProductPage productIdLinkedProducts(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_linkedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			linkedProductResource -> new LinkedProductPage(
				linkedProductResource.getProductIdLinkedProductsPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {specificationIdListTypeDefinitions(id: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ListTypeDefinitionPage specificationIdListTypeDefinitions(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource -> new ListTypeDefinitionPage(
				listTypeDefinitionResource.
					getSpecificationIdListTypeDefinitionsPage(id)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {lowStockActions{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrive low stock actions for products.")
	public LowStockActionPage lowStockActions() throws Exception {
		return _applyComponentServiceObjects(
			_lowStockActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			lowStockActionResource -> new LowStockActionPage(
				lowStockActionResource.getLowStockActionsPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeMappedProductBySequence(externalReferenceCode: ___, sequence: ___){actions, customFields, id, productExternalReferenceCode, productId, productName, quantity, sequence, sku, skuExternalReferenceCode, skuId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MappedProduct productByExternalReferenceCodeMappedProductBySequence(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("sequence") String sequence)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource ->
				mappedProductResource.
					getProductByExternalReferenceCodeMappedProductBySequence(
						externalReferenceCode, sequence));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeMappedProducts(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MappedProductPage productByExternalReferenceCodeMappedProducts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource -> new MappedProductPage(
				mappedProductResource.
					getProductByExternalReferenceCodeMappedProductsPage(
						externalReferenceCode, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							mappedProductResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdMappedProductBySequence(id: ___, sequence: ___){actions, customFields, id, productExternalReferenceCode, productId, productName, quantity, sequence, sku, skuExternalReferenceCode, skuId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MappedProduct productIdMappedProductBySequence(
			@GraphQLName("id") Long id,
			@GraphQLName("sequence") String sequence)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource ->
				mappedProductResource.getProductIdMappedProductBySequence(
					id, sequence));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdMappedProducts(id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MappedProductPage productIdMappedProducts(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource -> new MappedProductPage(
				mappedProductResource.getProductIdMappedProductsPage(
					id, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						mappedProductResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {option(id: ___){actions, catalogId, customFields, description, externalReferenceCode, facetable, fieldType, id, key, name, optionValues, priority, required, skuContributor}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Option option(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.getOption(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionByExternalReferenceCode(externalReferenceCode: ___){actions, catalogId, customFields, description, externalReferenceCode, facetable, fieldType, id, key, name, optionValues, priority, required, skuContributor}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Option optionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.getOptionByExternalReferenceCode(
				externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {options(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionPage options(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> new OptionPage(
				optionResource.getOptionsPage(
					search,
					_filterBiFunction.apply(optionResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(optionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionCategories(filter: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionCategoryPage optionCategories(
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource -> new OptionCategoryPage(
				optionCategoryResource.getOptionCategoriesPage(
					_filterBiFunction.apply(
						optionCategoryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						optionCategoryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionCategory(id: ___){description, externalReferenceCode, id, key, priority, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionCategory optionCategory(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource -> optionCategoryResource.getOptionCategory(
				id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionCategoryByExternalReferenceCode(externalReferenceCode: ___){description, externalReferenceCode, id, key, priority, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionCategory optionCategoryByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.getOptionCategoryByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionByExternalReferenceCodeOptionValues(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionValuePage optionByExternalReferenceCodeOptionValues(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource -> new OptionValuePage(
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							optionValueResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionIdOptionValues(id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionValuePage optionIdOptionValues(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource -> new OptionValuePage(
				optionValueResource.getOptionIdOptionValuesPage(
					id, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(optionValueResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionValue(id: ___){actions, customFields, externalReferenceCode, id, key, name, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionValue optionValue(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource -> optionValueResource.getOptionValue(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {optionValueByExternalReferenceCode(externalReferenceCode: ___){actions, customFields, externalReferenceCode, id, key, name, priority}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public OptionValue optionValueByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource ->
				optionValueResource.getOptionValueByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodePins(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PinPage productByExternalReferenceCodePins(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> new PinPage(
				pinResource.getProductByExternalReferenceCodePinsPage(
					externalReferenceCode, search,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(pinResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdPins(id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PinPage productIdPins(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> new PinPage(
				pinResource.getProductIdPinsPage(
					id, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(pinResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {product(id: ___){actions, active, attachments, catalog, catalogExternalReferenceCode, catalogId, categories, createDate, customFields, defaultSku, description, diagram, displayDate, expando, expirationDate, externalReferenceCode, id, images, linkedProducts, mappedProducts, metaDescription, metaKeyword, metaTitle, modifiedDate, name, neverExpire, pins, productAccountGroupFilter, productAccountGroups, productChannelFilter, productChannels, productConfiguration, productId, productOptions, productSpecifications, productStatus, productType, productTypeI18n, productVirtualSettings, relatedProducts, shippingConfiguration, shortDescription, skuFormatted, skus, subscriptionConfiguration, tags, taxConfiguration, thumbnail, urls, version, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Product product(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.getProduct(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCode(externalReferenceCode: ___){actions, active, attachments, catalog, catalogExternalReferenceCode, catalogId, categories, createDate, customFields, defaultSku, description, diagram, displayDate, expando, expirationDate, externalReferenceCode, id, images, linkedProducts, mappedProducts, metaDescription, metaKeyword, metaTitle, modifiedDate, name, neverExpire, pins, productAccountGroupFilter, productAccountGroups, productChannelFilter, productChannels, productConfiguration, productId, productOptions, productSpecifications, productStatus, productType, productTypeI18n, productVirtualSettings, relatedProducts, shippingConfiguration, shortDescription, skuFormatted, skus, subscriptionConfiguration, tags, taxConfiguration, thumbnail, urls, version, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Product productByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.getProductByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeByVersion(externalReferenceCode: ___, version: ___){actions, active, attachments, catalog, catalogExternalReferenceCode, catalogId, categories, createDate, customFields, defaultSku, description, diagram, displayDate, expando, expirationDate, externalReferenceCode, id, images, linkedProducts, mappedProducts, metaDescription, metaKeyword, metaTitle, modifiedDate, name, neverExpire, pins, productAccountGroupFilter, productAccountGroups, productChannelFilter, productChannels, productConfiguration, productId, productOptions, productSpecifications, productStatus, productType, productTypeI18n, productVirtualSettings, relatedProducts, shippingConfiguration, shortDescription, skuFormatted, skus, subscriptionConfiguration, tags, taxConfiguration, thumbnail, urls, version, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Product productByExternalReferenceCodeByVersion(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("version") Integer version)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.getProductByExternalReferenceCodeByVersion(
					externalReferenceCode, version));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByVersion(id: ___, version: ___){actions, active, attachments, catalog, catalogExternalReferenceCode, catalogId, categories, createDate, customFields, defaultSku, description, diagram, displayDate, expando, expirationDate, externalReferenceCode, id, images, linkedProducts, mappedProducts, metaDescription, metaKeyword, metaTitle, modifiedDate, name, neverExpire, pins, productAccountGroupFilter, productAccountGroups, productChannelFilter, productChannels, productConfiguration, productId, productOptions, productSpecifications, productStatus, productType, productTypeI18n, productVirtualSettings, relatedProducts, shippingConfiguration, shortDescription, skuFormatted, skus, subscriptionConfiguration, tags, taxConfiguration, thumbnail, urls, version, workflowStatusInfo}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Product productByVersion(
			@GraphQLName("id") Long id, @GraphQLName("version") Integer version)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.getProductByVersion(
				id, version));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {products(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductPage products(
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
				productResource.getProductsPage(
					search,
					_filterBiFunction.apply(productResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(productResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productAccountGroup(id: ___){accountGroupId, externalReferenceCode, id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductAccountGroup productAccountGroup(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productAccountGroupResource ->
				productAccountGroupResource.getProductAccountGroup(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeProductAccountGroups(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductAccountGroupPage
			productByExternalReferenceCodeProductAccountGroups(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productAccountGroupResource -> new ProductAccountGroupPage(
				productAccountGroupResource.
					getProductByExternalReferenceCodeProductAccountGroupsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdProductAccountGroups(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductAccountGroupPage productIdProductAccountGroups(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productAccountGroupResource -> new ProductAccountGroupPage(
				productAccountGroupResource.
					getProductIdProductAccountGroupsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeProductChannels(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductChannelPage productByExternalReferenceCodeProductChannels(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productChannelResource -> new ProductChannelPage(
				productChannelResource.
					getProductByExternalReferenceCodeProductChannelsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productChannel(id: ___){channelId, currencyCode, externalReferenceCode, id, name, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductChannel productChannel(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productChannelResource -> productChannelResource.getProductChannel(
				id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdProductChannels(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductChannelPage productIdProductChannels(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productChannelResource -> new ProductChannelPage(
				productChannelResource.getProductIdProductChannelsPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeConfiguration(externalReferenceCode: ___){actions, allowBackOrder, allowedOrderQuantities, availabilityEstimateId, availabilityEstimateName, differences, displayAvailability, displayStockQuantity, entityExternalReferenceCode, entityId, entityName, entityType, externalReferenceCode, id, inventoryEngine, lowStockAction, maxOrderQuantity, minOrderQuantity, minStockQuantity, multipleOrderQuantity, productShippingConfiguration, productTaxConfiguration, purchasable}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfiguration productByExternalReferenceCodeConfiguration(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					getProductByExternalReferenceCodeConfiguration(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfiguration(id: ___){actions, allowBackOrder, allowedOrderQuantities, availabilityEstimateId, availabilityEstimateName, differences, displayAvailability, displayStockQuantity, entityExternalReferenceCode, entityId, entityName, entityType, externalReferenceCode, id, inventoryEngine, lowStockAction, maxOrderQuantity, minOrderQuantity, minStockQuantity, multipleOrderQuantity, productShippingConfiguration, productTaxConfiguration, purchasable}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfiguration productConfiguration(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.getProductConfiguration(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationByExternalReferenceCode(externalReferenceCode: ___){actions, allowBackOrder, allowedOrderQuantities, availabilityEstimateId, availabilityEstimateName, differences, displayAvailability, displayStockQuantity, entityExternalReferenceCode, entityId, entityName, entityType, externalReferenceCode, id, inventoryEngine, lowStockAction, maxOrderQuantity, minOrderQuantity, minStockQuantity, multipleOrderQuantity, productShippingConfiguration, productTaxConfiguration, purchasable}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfiguration productConfigurationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					getProductConfigurationByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListByExternalReferenceCodeProductConfigurations(externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, showDifferences: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationPage
			productConfigurationListByExternalReferenceCodeProductConfigurations(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("showDifferences") Boolean showDifferences,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource -> new ProductConfigurationPage(
				productConfigurationResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
						externalReferenceCode, search, showDifferences,
						_filterBiFunction.apply(
							productConfigurationResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							productConfigurationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListIdProductConfigurations(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, showDifferences: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationPage
			productConfigurationListIdProductConfigurations(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("showDifferences") Boolean showDifferences,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource -> new ProductConfigurationPage(
				productConfigurationResource.
					getProductConfigurationListIdProductConfigurationsPage(
						id, search, showDifferences,
						_filterBiFunction.apply(
							productConfigurationResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							productConfigurationResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdConfiguration(id: ___){actions, allowBackOrder, allowedOrderQuantities, availabilityEstimateId, availabilityEstimateName, differences, displayAvailability, displayStockQuantity, entityExternalReferenceCode, entityId, entityName, entityType, externalReferenceCode, id, inventoryEngine, lowStockAction, maxOrderQuantity, minOrderQuantity, minStockQuantity, multipleOrderQuantity, productShippingConfiguration, productTaxConfiguration, purchasable}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfiguration productIdConfiguration(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.getProductIdConfiguration(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationList(id: ___){actions, catalogExternalReferenceCode, catalogId, createDate, customFields, displayDate, expirationDate, externalReferenceCode, id, master, name, neverExpire, parentProductConfigurationListId, priority, productConfigurations}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationList productConfigurationList(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.getProductConfigurationList(
					id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListByExternalReferenceCode(externalReferenceCode: ___){actions, catalogExternalReferenceCode, catalogId, createDate, customFields, displayDate, expirationDate, externalReferenceCode, id, master, name, neverExpire, parentProductConfigurationListId, priority, productConfigurations}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationList
			productConfigurationListByExternalReferenceCode(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.
					getProductConfigurationListByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationLists(catalogId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListPage productConfigurationLists(
			@GraphQLName("catalogId") Long catalogId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				new ProductConfigurationListPage(
					productConfigurationListResource.
						getProductConfigurationListsPage(
							catalogId, search,
							_filterBiFunction.apply(
								productConfigurationListResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productConfigurationListResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListByExternalReferenceCodeProductConfigurationListAccounts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListAccountPage
			productConfigurationListByExternalReferenceCodeProductConfigurationListAccounts(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountResource ->
				new ProductConfigurationListAccountPage(
					productConfigurationListAccountResource.
						getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
							externalReferenceCode,
							Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListIdProductConfigurationListAccounts(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListAccountPage
			productConfigurationListIdProductConfigurationListAccounts(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountResource ->
				new ProductConfigurationListAccountPage(
					productConfigurationListAccountResource.
						getProductConfigurationListIdProductConfigurationListAccountsPage(
							id, search,
							_filterBiFunction.apply(
								productConfigurationListAccountResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productConfigurationListAccountResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroups(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListAccountGroupPage
			productConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroups(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountGroupResource ->
				new ProductConfigurationListAccountGroupPage(
					productConfigurationListAccountGroupResource.
						getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
							externalReferenceCode,
							Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListIdProductConfigurationListAccountGroups(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListAccountGroupPage
			productConfigurationListIdProductConfigurationListAccountGroups(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountGroupResource ->
				new ProductConfigurationListAccountGroupPage(
					productConfigurationListAccountGroupResource.
						getProductConfigurationListIdProductConfigurationListAccountGroupsPage(
							id, search,
							_filterBiFunction.apply(
								productConfigurationListAccountGroupResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productConfigurationListAccountGroupResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListByExternalReferenceCodeProductConfigurationListChannels(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListChannelPage
			productConfigurationListByExternalReferenceCodeProductConfigurationListChannels(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListChannelResource ->
				new ProductConfigurationListChannelPage(
					productConfigurationListChannelResource.
						getProductConfigurationListByExternalReferenceCodeProductConfigurationListChannelsPage(
							externalReferenceCode,
							Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListIdProductConfigurationListChannels(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListChannelPage
			productConfigurationListIdProductConfigurationListChannels(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListChannelResource ->
				new ProductConfigurationListChannelPage(
					productConfigurationListChannelResource.
						getProductConfigurationListIdProductConfigurationListChannelsPage(
							id, search,
							_filterBiFunction.apply(
								productConfigurationListChannelResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productConfigurationListChannelResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypes(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListOrderTypePage
			productConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypes(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListOrderTypeResource ->
				new ProductConfigurationListOrderTypePage(
					productConfigurationListOrderTypeResource.
						getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
							externalReferenceCode,
							Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productConfigurationListIdProductConfigurationListOrderTypes(filter: ___, id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductConfigurationListOrderTypePage
			productConfigurationListIdProductConfigurationListOrderTypes(
				@GraphQLName("id") Long id,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListOrderTypeResource ->
				new ProductConfigurationListOrderTypePage(
					productConfigurationListOrderTypeResource.
						getProductConfigurationListIdProductConfigurationListOrderTypesPage(
							id, search,
							_filterBiFunction.apply(
								productConfigurationListOrderTypeResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productConfigurationListOrderTypeResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productGroup(id: ___){customFields, description, externalReferenceCode, id, products, productsCount, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductGroup productGroup(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource -> productGroupResource.getProductGroup(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productGroupByExternalReferenceCode(externalReferenceCode: ___){customFields, description, externalReferenceCode, id, products, productsCount, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductGroup productGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.getProductGroupByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productGroups(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductGroupPage productGroups(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource -> new ProductGroupPage(
				productGroupResource.getProductGroupsPage(
					search,
					_filterBiFunction.apply(productGroupResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						productGroupResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productGroupByExternalReferenceCodeProductGroupProducts(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductGroupProductPage
			productGroupByExternalReferenceCodeProductGroupProducts(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupProductResource -> new ProductGroupProductPage(
				productGroupProductResource.
					getProductGroupByExternalReferenceCodeProductGroupProductsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productGroupIdProductGroupProducts(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductGroupProductPage productGroupIdProductGroupProducts(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupProductResource -> new ProductGroupProductPage(
				productGroupProductResource.
					getProductGroupIdProductGroupProductsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeProductOptions(externalReferenceCode: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionPage productByExternalReferenceCodeProductOptions(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> new ProductOptionPage(
				productOptionResource.
					getProductByExternalReferenceCodeProductOptionsPage(
						externalReferenceCode, search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							productOptionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdProductOptions(id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionPage productIdProductOptions(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> new ProductOptionPage(
				productOptionResource.getProductIdProductOptionsPage(
					id, search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						productOptionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productOption(id: ___){catalogId, customFields, definedExternally, description, facetable, fieldType, id, infoItemServiceKey, key, name, optionExternalReferenceCode, optionId, priceType, priority, productOptionValues, required, skuContributor, typeSettings}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOption productOption(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> productOptionResource.getProductOption(
				id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productOptionIdProductOptionValues(id: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionValuePage productOptionIdProductOptionValues(
			@GraphQLName("id") Long id, @GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource -> new ProductOptionValuePage(
				productOptionValueResource.
					getProductOptionIdProductOptionValuesPage(
						id, search, Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							productOptionValueResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productOptionValue(id: ___){deltaPrice, id, key, name, preselected, priority, quantity, skuExternalReferenceCode, skuId, unitOfMeasureKey}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductOptionValue productOptionValue(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource ->
				productOptionValueResource.getProductOptionValue(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeShippingConfiguration(externalReferenceCode: ___){depth, freeShipping, height, shippable, shippingExtraPrice, shippingSeparately, weight, width}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductShippingConfiguration
			productByExternalReferenceCodeShippingConfiguration(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productShippingConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productShippingConfigurationResource ->
				productShippingConfigurationResource.
					getProductByExternalReferenceCodeShippingConfiguration(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdShippingConfiguration(id: ___){depth, freeShipping, height, shippable, shippingExtraPrice, shippingSeparately, weight, width}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductShippingConfiguration productIdShippingConfiguration(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productShippingConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productShippingConfigurationResource ->
				productShippingConfigurationResource.
					getProductIdShippingConfiguration(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeProductSpecifications(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSpecificationPage
			productByExternalReferenceCodeProductSpecifications(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource -> new ProductSpecificationPage(
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdProductSpecifications(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSpecificationPage productIdProductSpecifications(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource -> new ProductSpecificationPage(
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productSpecification(id: ___){externalReferenceCode, id, key, label, optionCategoryExternalReferenceCode, optionCategoryId, priority, productId, specificationExternalReferenceCode, specificationId, specificationKey, specificationPriority, value, visible}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSpecification productSpecification(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.getProductSpecification(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productSpecificationByExternalReferenceCode(externalReferenceCode: ___){externalReferenceCode, id, key, label, optionCategoryExternalReferenceCode, optionCategoryId, priority, productId, specificationExternalReferenceCode, specificationId, specificationKey, specificationPriority, value, visible}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSpecification productSpecificationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.
					getProductSpecificationByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeSubscriptionConfiguration(externalReferenceCode: ___){deliverySubscriptionEnable, deliverySubscriptionLength, deliverySubscriptionNumberOfLength, deliverySubscriptionType, deliverySubscriptionTypeSettings, enable, length, numberOfLength, subscriptionType, subscriptionTypeSettings}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSubscriptionConfiguration
			productByExternalReferenceCodeSubscriptionConfiguration(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSubscriptionConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSubscriptionConfigurationResource ->
				productSubscriptionConfigurationResource.
					getProductByExternalReferenceCodeSubscriptionConfiguration(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdSubscriptionConfiguration(id: ___){deliverySubscriptionEnable, deliverySubscriptionLength, deliverySubscriptionNumberOfLength, deliverySubscriptionType, deliverySubscriptionTypeSettings, enable, length, numberOfLength, subscriptionType, subscriptionTypeSettings}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductSubscriptionConfiguration productIdSubscriptionConfiguration(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSubscriptionConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSubscriptionConfigurationResource ->
				productSubscriptionConfigurationResource.
					getProductIdSubscriptionConfiguration(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeTaxConfiguration(externalReferenceCode: ___){id, taxCategory, taxable}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductTaxConfiguration
			productByExternalReferenceCodeTaxConfiguration(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productTaxConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productTaxConfigurationResource ->
				productTaxConfigurationResource.
					getProductByExternalReferenceCodeTaxConfiguration(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdTaxConfiguration(id: ___){id, taxCategory, taxable}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductTaxConfiguration productIdTaxConfiguration(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productTaxConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productTaxConfigurationResource ->
				productTaxConfigurationResource.getProductIdTaxConfiguration(
					id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeProductVirtualSettings(externalReferenceCode: ___){activationStatus, activationStatusInfo, attachment, duration, id, maxUsages, productVirtualSettingsFileEntries, sampleAttachment, sampleSrc, sampleURL, src, termsOfUseContent, termsOfUseJournalArticleId, termsOfUseRequired, url, useSample}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductVirtualSettings
			productByExternalReferenceCodeProductVirtualSettings(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productVirtualSettingsResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsResource ->
				productVirtualSettingsResource.
					getProductByExternalReferenceCodeProductVirtualSettings(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdProductVirtualSettings(id: ___){activationStatus, activationStatusInfo, attachment, duration, id, maxUsages, productVirtualSettingsFileEntries, sampleAttachment, sampleSrc, sampleURL, src, termsOfUseContent, termsOfUseJournalArticleId, termsOfUseRequired, url, useSample}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductVirtualSettings productIdProductVirtualSettings(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productVirtualSettingsResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsResource ->
				productVirtualSettingsResource.
					getProductIdProductVirtualSettings(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productVirtualSettingIdProductVirtualSettingsFileEntries(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductVirtualSettingsFileEntryPage
			productVirtualSettingIdProductVirtualSettingsFileEntries(
				@GraphQLName("id") Long id,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_productVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsFileEntryResource ->
				new ProductVirtualSettingsFileEntryPage(
					productVirtualSettingsFileEntryResource.
						getProductVirtualSettingIdProductVirtualSettingsFileEntriesPage(
							id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productVirtualSettingsFileEntry(id: ___){actions, attachment, id, src, url, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsFileEntryResource ->
				productVirtualSettingsFileEntryResource.
					getProductVirtualSettingsFileEntry(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeRelatedProducts(externalReferenceCode: ___, page: ___, pageSize: ___, type: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public RelatedProductPage productByExternalReferenceCodeRelatedProducts(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("type") String type,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource -> new RelatedProductPage(
				relatedProductResource.
					getProductByExternalReferenceCodeRelatedProductsPage(
						externalReferenceCode, type,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdRelatedProducts(id: ___, page: ___, pageSize: ___, type: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public RelatedProductPage productIdRelatedProducts(
			@GraphQLName("id") Long id, @GraphQLName("type") String type,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource -> new RelatedProductPage(
				relatedProductResource.getProductIdRelatedProductsPage(
					id, type, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {relatedProduct(id: ___){id, priority, productExternalReferenceCode, productId, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public RelatedProduct relatedProduct(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource -> relatedProductResource.getRelatedProduct(
				id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productByExternalReferenceCodeSkus(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuPage productByExternalReferenceCodeSkus(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> new SkuPage(
				skuResource.getProductByExternalReferenceCodeSkusPage(
					externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {productIdSkus(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuPage productIdSkus(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> new SkuPage(
				skuResource.getProductIdSkusPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sku(id: ___){cost, customFields, depth, discontinued, discontinuedDate, displayDate, expirationDate, externalReferenceCode, gtin, height, id, inventoryLevel, manufacturerPartNumber, neverExpire, price, productId, productName, promoPrice, published, purchasable, replacementSkuExternalReferenceCode, replacementSkuId, sku, skuOptions, skuSubscriptionConfiguration, skuUnitOfMeasures, skuVirtualSettings, unitOfMeasureKey, unitOfMeasureName, unitOfMeasureSkuId, unspsc, weight, width}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Sku sku(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.getSku(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuByExternalReferenceCode(externalReferenceCode: ___){cost, customFields, depth, discontinued, discontinuedDate, displayDate, expirationDate, externalReferenceCode, gtin, height, id, inventoryLevel, manufacturerPartNumber, neverExpire, price, productId, productName, promoPrice, published, purchasable, replacementSkuExternalReferenceCode, replacementSkuId, sku, skuOptions, skuSubscriptionConfiguration, skuUnitOfMeasures, skuVirtualSettings, unitOfMeasureKey, unitOfMeasureName, unitOfMeasureSkuId, unspsc, weight, width}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Sku skuByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.getSkuByExternalReferenceCode(
				externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skus(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuPage skus(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> new SkuPage(
				skuResource.getSkusPage(
					search, _filterBiFunction.apply(skuResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(skuResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {unitOfMeasureSkus(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuPage unitOfMeasureSkus(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> new SkuPage(
				skuResource.getUnitOfMeasureSkusPage(
					search, _filterBiFunction.apply(skuResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(skuResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuByExternalReferenceCodeSkuSubscriptionConfiguration(externalReferenceCode: ___){deliverySubscriptionEnable, deliverySubscriptionLength, deliverySubscriptionNumberOfLength, deliverySubscriptionType, deliverySubscriptionTypeSettings, enable, length, numberOfLength, overrideSubscriptionInfo, subscriptionType, subscriptionTypeSettings}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuSubscriptionConfiguration
			skuByExternalReferenceCodeSkuSubscriptionConfiguration(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuSubscriptionConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuSubscriptionConfigurationResource ->
				skuSubscriptionConfigurationResource.
					getSkuByExternalReferenceCodeSkuSubscriptionConfiguration(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuIdSkuSubscriptionConfiguration(id: ___){deliverySubscriptionEnable, deliverySubscriptionLength, deliverySubscriptionNumberOfLength, deliverySubscriptionType, deliverySubscriptionTypeSettings, enable, length, numberOfLength, overrideSubscriptionInfo, subscriptionType, subscriptionTypeSettings}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuSubscriptionConfiguration skuIdSkuSubscriptionConfiguration(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuSubscriptionConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuSubscriptionConfigurationResource ->
				skuSubscriptionConfigurationResource.
					getSkuIdSkuSubscriptionConfiguration(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuByExternalReferenceCodeSkuUnitOfMeasures(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuUnitOfMeasurePage skuByExternalReferenceCodeSkuUnitOfMeasures(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource -> new SkuUnitOfMeasurePage(
				skuUnitOfMeasureResource.
					getSkuByExternalReferenceCodeSkuUnitOfMeasuresPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuIdSkuUnitOfMeasures(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuUnitOfMeasurePage skuIdSkuUnitOfMeasures(
			@GraphQLName("id") Long id, @GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource -> new SkuUnitOfMeasurePage(
				skuUnitOfMeasureResource.getSkuIdSkuUnitOfMeasuresPage(
					id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuUnitOfMeasure(id: ___){actions, active, basePrice, id, incrementalOrderQuantity, key, name, precision, pricingQuantity, primary, priority, promoPrice, rate, sku, skuId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuUnitOfMeasure skuUnitOfMeasure(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource ->
				skuUnitOfMeasureResource.getSkuUnitOfMeasure(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuByExternalReferenceCodeSkuVirtualSettings(externalReferenceCode: ___){activationStatus, activationStatusInfo, attachment, duration, id, maxUsages, override, sampleAttachment, sampleSrc, sampleURL, skuVirtualSettingsFileEntries, src, termsOfUseContent, termsOfUseJournalArticleId, termsOfUseRequired, url, useSample}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuVirtualSettings skuByExternalReferenceCodeSkuVirtualSettings(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuVirtualSettingsResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsResource ->
				skuVirtualSettingsResource.
					getSkuByExternalReferenceCodeSkuVirtualSettings(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuIdSkuVirtualSettings(id: ___){activationStatus, activationStatusInfo, attachment, duration, id, maxUsages, override, sampleAttachment, sampleSrc, sampleURL, skuVirtualSettingsFileEntries, src, termsOfUseContent, termsOfUseJournalArticleId, termsOfUseRequired, url, useSample}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuVirtualSettings skuIdSkuVirtualSettings(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuVirtualSettingsResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsResource ->
				skuVirtualSettingsResource.getSkuIdSkuVirtualSettings(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuVirtualSettingIdSkuVirtualSettingsFileEntries(id: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuVirtualSettingsFileEntryPage
			skuVirtualSettingIdSkuVirtualSettingsFileEntries(
				@GraphQLName("id") Long id,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsFileEntryResource ->
				new SkuVirtualSettingsFileEntryPage(
					skuVirtualSettingsFileEntryResource.
						getSkuVirtualSettingIdSkuVirtualSettingsFileEntriesPage(
							id, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {skuVirtualSettingsFileEntry(id: ___){actions, attachment, id, src, url, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry(
			@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsFileEntryResource ->
				skuVirtualSettingsFileEntryResource.
					getSkuVirtualSettingsFileEntry(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {specification(id: ___){description, externalReferenceCode, facetable, id, key, listTypeDefinitionId, listTypeDefinitionIds, optionCategory, priority, title, visible}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Specification specification(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource -> specificationResource.getSpecification(
				id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {specificationByExternalReferenceCode(externalReferenceCode: ___){description, externalReferenceCode, facetable, id, key, listTypeDefinitionId, listTypeDefinitionIds, optionCategory, priority, title, visible}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Specification specificationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource ->
				specificationResource.getSpecificationByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {specifications(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SpecificationPage specifications(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource -> new SpecificationPage(
				specificationResource.getSpecificationsPage(
					search,
					_filterBiFunction.apply(
						specificationResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						specificationResource, sortsString))));
	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeConfigurationTypeExtension {

		public GetProductByExternalReferenceCodeConfigurationTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfiguration
				productByExternalReferenceCodeConfiguration()
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationResource ->
					productConfigurationResource.
						getProductByExternalReferenceCodeConfiguration(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeTypeExtension {

		public GetProductByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Product productByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_productResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productResource ->
					productResource.getProductByExternalReferenceCode(
						_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeCategoriesPageTypeExtension {

		public GetProductByExternalReferenceCodeCategoriesPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public CategoryPage productByExternalReferenceCodeCategories(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_categoryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				categoryResource -> new CategoryPage(
					categoryResource.
						getProductByExternalReferenceCodeCategoriesPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeGroupedProductsPageTypeExtension {

		public GetProductByExternalReferenceCodeGroupedProductsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public GroupedProductPage productByExternalReferenceCodeGroupedProducts(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_groupedProductResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				groupedProductResource -> new GroupedProductPage(
					groupedProductResource.
						getProductByExternalReferenceCodeGroupedProductsPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetCurrencyByExternalReferenceCodeTypeExtension {

		public GetCurrencyByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Currency currencyByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_currencyResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				currencyResource ->
					currencyResource.getCurrencyByExternalReferenceCode(
						_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPageTypeExtension {

		public GetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfigurationListAccountGroupPage
				productConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroups(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationListAccountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationListAccountGroupResource ->
					new ProductConfigurationListAccountGroupPage(
						productConfigurationListAccountGroupResource.
							getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroupsPage(
								_attachment.getExternalReferenceCode(),
								Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeProductChannelsPageTypeExtension {

		public GetProductByExternalReferenceCodeProductChannelsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductChannelPage productByExternalReferenceCodeProductChannels(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productChannelResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productChannelResource -> new ProductChannelPage(
					productChannelResource.
						getProductByExternalReferenceCodeProductChannelsPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetSkuByExternalReferenceCodeSkuSubscriptionConfigurationTypeExtension {

		public GetSkuByExternalReferenceCodeSkuSubscriptionConfigurationTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public SkuSubscriptionConfiguration
				skuByExternalReferenceCodeSkuSubscriptionConfiguration()
			throws Exception {

			return _applyComponentServiceObjects(
				_skuSubscriptionConfigurationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				skuSubscriptionConfigurationResource ->
					skuSubscriptionConfigurationResource.
						getSkuByExternalReferenceCodeSkuSubscriptionConfiguration(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetOptionValueByExternalReferenceCodeTypeExtension {

		public GetOptionValueByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public OptionValue optionValueByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_optionValueResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				optionValueResource ->
					optionValueResource.getOptionValueByExternalReferenceCode(
						_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeCatalogTypeExtension {

		public GetProductByExternalReferenceCodeCatalogTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Catalog productByExternalReferenceCodeCatalog(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_catalogResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				catalogResource ->
					catalogResource.getProductByExternalReferenceCodeCatalog(
						_attachment.getExternalReferenceCode(),
						Pagination.of(page, pageSize)));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetCatalogByExternalReferenceCodeTypeExtension {

		public GetCatalogByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Catalog catalogByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_catalogResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				catalogResource ->
					catalogResource.getCatalogByExternalReferenceCode(
						_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetSpecificationByExternalReferenceCodeTypeExtension {

		public GetSpecificationByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Specification specificationByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_specificationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				specificationResource ->
					specificationResource.
						getSpecificationByExternalReferenceCode(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeMappedProductsPageTypeExtension {

		public GetProductByExternalReferenceCodeMappedProductsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public MappedProductPage productByExternalReferenceCodeMappedProducts(
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_mappedProductResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				mappedProductResource -> new MappedProductPage(
					mappedProductResource.
						getProductByExternalReferenceCodeMappedProductsPage(
							_attachment.getExternalReferenceCode(), search,
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								mappedProductResource, sortsString))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetSkuByExternalReferenceCodeSkuVirtualSettingsTypeExtension {

		public GetSkuByExternalReferenceCodeSkuVirtualSettingsTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public SkuVirtualSettings skuByExternalReferenceCodeSkuVirtualSettings()
			throws Exception {

			return _applyComponentServiceObjects(
				_skuVirtualSettingsResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				skuVirtualSettingsResource ->
					skuVirtualSettingsResource.
						getSkuByExternalReferenceCodeSkuVirtualSettings(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeDiagramTypeExtension {

		public GetProductByExternalReferenceCodeDiagramTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Diagram productByExternalReferenceCodeDiagram()
			throws Exception {

			return _applyComponentServiceObjects(
				_diagramResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				diagramResource ->
					diagramResource.getProductByExternalReferenceCodeDiagram(
						_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeProductAccountGroupsPageTypeExtension {

		public GetProductByExternalReferenceCodeProductAccountGroupsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductAccountGroupPage
				productByExternalReferenceCodeProductAccountGroups(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productAccountGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productAccountGroupResource -> new ProductAccountGroupPage(
					productAccountGroupResource.
						getProductByExternalReferenceCodeProductAccountGroupsPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeSkusPageTypeExtension {

		public GetProductByExternalReferenceCodeSkusPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public SkuPage productByExternalReferenceCodeSkus(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_skuResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				skuResource -> new SkuPage(
					skuResource.getProductByExternalReferenceCodeSkusPage(
						_attachment.getExternalReferenceCode(),
						Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Catalog.class)
	public class GetAttachmentByExternalReferenceCodeTypeExtension {

		public GetAttachmentByExternalReferenceCodeTypeExtension(
			Catalog catalog) {

			_catalog = catalog;
		}

		@GraphQLField
		public Attachment attachmentByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_attachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				attachmentResource ->
					attachmentResource.getAttachmentByExternalReferenceCode(
						_catalog.getExternalReferenceCode()));
		}

		private Catalog _catalog;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageTypeExtension {

		public GetProductConfigurationListByExternalReferenceCodeProductConfigurationsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfigurationPage
				productConfigurationListByExternalReferenceCodeProductConfigurations(
					@GraphQLName("search") String search,
					@GraphQLName("showDifferences") Boolean showDifferences,
					@GraphQLName("filter") String filterString,
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page,
					@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationResource -> new ProductConfigurationPage(
					productConfigurationResource.
						getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
							_attachment.getExternalReferenceCode(), search,
							showDifferences,
							_filterBiFunction.apply(
								productConfigurationResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productConfigurationResource, sortsString))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPageTypeExtension {

		public GetProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfigurationListOrderTypePage
				productConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypes(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationListOrderTypeResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationListOrderTypeResource ->
					new ProductConfigurationListOrderTypePage(
						productConfigurationListOrderTypeResource.
							getProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderTypesPage(
								_attachment.getExternalReferenceCode(),
								Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeShippingConfigurationTypeExtension {

		public GetProductByExternalReferenceCodeShippingConfigurationTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductShippingConfiguration
				productByExternalReferenceCodeShippingConfiguration()
			throws Exception {

			return _applyComponentServiceObjects(
				_productShippingConfigurationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productShippingConfigurationResource ->
					productShippingConfigurationResource.
						getProductByExternalReferenceCodeShippingConfiguration(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductSpecificationByExternalReferenceCodeTypeExtension {

		public GetProductSpecificationByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductSpecification
				productSpecificationByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_productSpecificationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productSpecificationResource ->
					productSpecificationResource.
						getProductSpecificationByExternalReferenceCode(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetSkuByExternalReferenceCodeTypeExtension {

		public GetSkuByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Sku skuByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_skuResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				skuResource -> skuResource.getSkuByExternalReferenceCode(
					_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeByVersionTypeExtension {

		public GetProductByExternalReferenceCodeByVersionTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Product productByExternalReferenceCodeByVersion(
				@GraphQLName("version") Integer version)
			throws Exception {

			return _applyComponentServiceObjects(
				_productResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productResource ->
					productResource.getProductByExternalReferenceCodeByVersion(
						_attachment.getExternalReferenceCode(), version));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductConfigurationByExternalReferenceCodeTypeExtension {

		public GetProductConfigurationByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfiguration
				productConfigurationByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationResource ->
					productConfigurationResource.
						getProductConfigurationByExternalReferenceCode(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeAttachmentsPageTypeExtension {

		public GetProductByExternalReferenceCodeAttachmentsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public AttachmentPage productByExternalReferenceCodeAttachments(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_attachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				attachmentResource -> new AttachmentPage(
					attachmentResource.
						getProductByExternalReferenceCodeAttachmentsPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetOptionByExternalReferenceCodeOptionValuesPageTypeExtension {

		public GetOptionByExternalReferenceCodeOptionValuesPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public OptionValuePage optionByExternalReferenceCodeOptionValues(
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_optionValueResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				optionValueResource -> new OptionValuePage(
					optionValueResource.
						getOptionByExternalReferenceCodeOptionValuesPage(
							_attachment.getExternalReferenceCode(), search,
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								optionValueResource, sortsString))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductGroupByExternalReferenceCodeProductGroupProductsPageTypeExtension {

		public GetProductGroupByExternalReferenceCodeProductGroupProductsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductGroupProductPage
				productGroupByExternalReferenceCodeProductGroupProducts(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productGroupProductResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productGroupProductResource -> new ProductGroupProductPage(
					productGroupProductResource.
						getProductGroupByExternalReferenceCodeProductGroupProductsPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductConfigurationListByExternalReferenceCodeTypeExtension {

		public GetProductConfigurationListByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfigurationList
				productConfigurationListByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationListResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationListResource ->
					productConfigurationListResource.
						getProductConfigurationListByExternalReferenceCode(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeProductOptionsPageTypeExtension {

		public GetProductByExternalReferenceCodeProductOptionsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductOptionPage productByExternalReferenceCodeProductOptions(
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_productOptionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productOptionResource -> new ProductOptionPage(
					productOptionResource.
						getProductByExternalReferenceCodeProductOptionsPage(
							_attachment.getExternalReferenceCode(), search,
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								productOptionResource, sortsString))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeRelatedProductsPageTypeExtension {

		public GetProductByExternalReferenceCodeRelatedProductsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public RelatedProductPage productByExternalReferenceCodeRelatedProducts(
				@GraphQLName("type") String type,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_relatedProductResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				relatedProductResource -> new RelatedProductPage(
					relatedProductResource.
						getProductByExternalReferenceCodeRelatedProductsPage(
							_attachment.getExternalReferenceCode(), type,
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeProductSpecificationsPageTypeExtension {

		public GetProductByExternalReferenceCodeProductSpecificationsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductSpecificationPage
				productByExternalReferenceCodeProductSpecifications(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productSpecificationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productSpecificationResource -> new ProductSpecificationPage(
					productSpecificationResource.
						getProductByExternalReferenceCodeProductSpecificationsPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeMappedProductBySequenceTypeExtension {

		public GetProductByExternalReferenceCodeMappedProductBySequenceTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public MappedProduct
				productByExternalReferenceCodeMappedProductBySequence(
					@GraphQLName("sequence") String sequence)
			throws Exception {

			return _applyComponentServiceObjects(
				_mappedProductResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				mappedProductResource ->
					mappedProductResource.
						getProductByExternalReferenceCodeMappedProductBySequence(
							_attachment.getExternalReferenceCode(), sequence));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductConfigurationListByExternalReferenceCodeProductConfigurationListChannelsPageTypeExtension {

		public GetProductConfigurationListByExternalReferenceCodeProductConfigurationListChannelsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfigurationListChannelPage
				productConfigurationListByExternalReferenceCodeProductConfigurationListChannels(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationListChannelResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationListChannelResource ->
					new ProductConfigurationListChannelPage(
						productConfigurationListChannelResource.
							getProductConfigurationListByExternalReferenceCodeProductConfigurationListChannelsPage(
								_attachment.getExternalReferenceCode(),
								Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPageTypeExtension {

		public GetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductConfigurationListAccountPage
				productConfigurationListByExternalReferenceCodeProductConfigurationListAccounts(
					@GraphQLName("pageSize") int pageSize,
					@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_productConfigurationListAccountResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productConfigurationListAccountResource ->
					new ProductConfigurationListAccountPage(
						productConfigurationListAccountResource.
							getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
								_attachment.getExternalReferenceCode(),
								Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeTaxConfigurationTypeExtension {

		public GetProductByExternalReferenceCodeTaxConfigurationTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductTaxConfiguration
				productByExternalReferenceCodeTaxConfiguration()
			throws Exception {

			return _applyComponentServiceObjects(
				_productTaxConfigurationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productTaxConfigurationResource ->
					productTaxConfigurationResource.
						getProductByExternalReferenceCodeTaxConfiguration(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodeImagesPageTypeExtension {

		public GetProductByExternalReferenceCodeImagesPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public AttachmentPage productByExternalReferenceCodeImages(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_attachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				attachmentResource -> new AttachmentPage(
					attachmentResource.
						getProductByExternalReferenceCodeImagesPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductByExternalReferenceCodePinsPageTypeExtension {

		public GetProductByExternalReferenceCodePinsPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public PinPage productByExternalReferenceCodePins(
				@GraphQLName("search") String search,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_pinResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				pinResource -> new PinPage(
					pinResource.getProductByExternalReferenceCodePinsPage(
						_attachment.getExternalReferenceCode(), search,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(pinResource, sortsString))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetOptionByExternalReferenceCodeTypeExtension {

		public GetOptionByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public Option optionByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_optionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				optionResource ->
					optionResource.getOptionByExternalReferenceCode(
						_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetProductGroupByExternalReferenceCodeTypeExtension {

		public GetProductGroupByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductGroup productGroupByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_productGroupResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productGroupResource ->
					productGroupResource.getProductGroupByExternalReferenceCode(
						_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeSubscriptionConfigurationTypeExtension {

		public GetProductByExternalReferenceCodeSubscriptionConfigurationTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductSubscriptionConfiguration
				productByExternalReferenceCodeSubscriptionConfiguration()
			throws Exception {

			return _applyComponentServiceObjects(
				_productSubscriptionConfigurationResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productSubscriptionConfigurationResource ->
					productSubscriptionConfigurationResource.
						getProductByExternalReferenceCodeSubscriptionConfiguration(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetSkuByExternalReferenceCodeSkuUnitOfMeasuresPageTypeExtension {

		public GetSkuByExternalReferenceCodeSkuUnitOfMeasuresPageTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public SkuUnitOfMeasurePage skuByExternalReferenceCodeSkuUnitOfMeasures(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_skuUnitOfMeasureResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				skuUnitOfMeasureResource -> new SkuUnitOfMeasurePage(
					skuUnitOfMeasureResource.
						getSkuByExternalReferenceCodeSkuUnitOfMeasuresPage(
							_attachment.getExternalReferenceCode(),
							Pagination.of(page, pageSize))));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class
		GetProductByExternalReferenceCodeProductVirtualSettingsTypeExtension {

		public GetProductByExternalReferenceCodeProductVirtualSettingsTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public ProductVirtualSettings
				productByExternalReferenceCodeProductVirtualSettings()
			throws Exception {

			return _applyComponentServiceObjects(
				_productVirtualSettingsResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				productVirtualSettingsResource ->
					productVirtualSettingsResource.
						getProductByExternalReferenceCodeProductVirtualSettings(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

	}

	@GraphQLTypeExtension(Attachment.class)
	public class GetOptionCategoryByExternalReferenceCodeTypeExtension {

		public GetOptionCategoryByExternalReferenceCodeTypeExtension(
			Attachment attachment) {

			_attachment = attachment;
		}

		@GraphQLField
		public OptionCategory optionCategoryByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_optionCategoryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				optionCategoryResource ->
					optionCategoryResource.
						getOptionCategoryByExternalReferenceCode(
							_attachment.getExternalReferenceCode()));
		}

		private Attachment _attachment;

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

	@GraphQLName("CatalogPage")
	public class CatalogPage {

		public CatalogPage(Page catalogPage) {
			actions = catalogPage.getActions();

			items = catalogPage.getItems();
			lastPage = catalogPage.getLastPage();
			page = catalogPage.getPage();
			pageSize = catalogPage.getPageSize();
			totalCount = catalogPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Catalog> items;

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

	@GraphQLName("DiagramPage")
	public class DiagramPage {

		public DiagramPage(Page diagramPage) {
			actions = diagramPage.getActions();

			items = diagramPage.getItems();
			lastPage = diagramPage.getLastPage();
			page = diagramPage.getPage();
			pageSize = diagramPage.getPageSize();
			totalCount = diagramPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Diagram> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("GroupedProductPage")
	public class GroupedProductPage {

		public GroupedProductPage(Page groupedProductPage) {
			actions = groupedProductPage.getActions();

			items = groupedProductPage.getItems();
			lastPage = groupedProductPage.getLastPage();
			page = groupedProductPage.getPage();
			pageSize = groupedProductPage.getPageSize();
			totalCount = groupedProductPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<GroupedProduct> items;

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

	@GraphQLName("ListTypeDefinitionPage")
	public class ListTypeDefinitionPage {

		public ListTypeDefinitionPage(Page listTypeDefinitionPage) {
			actions = listTypeDefinitionPage.getActions();

			items = listTypeDefinitionPage.getItems();
			lastPage = listTypeDefinitionPage.getLastPage();
			page = listTypeDefinitionPage.getPage();
			pageSize = listTypeDefinitionPage.getPageSize();
			totalCount = listTypeDefinitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ListTypeDefinition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("LowStockActionPage")
	public class LowStockActionPage {

		public LowStockActionPage(Page lowStockActionPage) {
			actions = lowStockActionPage.getActions();

			items = lowStockActionPage.getItems();
			lastPage = lowStockActionPage.getLastPage();
			page = lowStockActionPage.getPage();
			pageSize = lowStockActionPage.getPageSize();
			totalCount = lowStockActionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<LowStockAction> items;

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

	@GraphQLName("OptionPage")
	public class OptionPage {

		public OptionPage(Page optionPage) {
			actions = optionPage.getActions();

			items = optionPage.getItems();
			lastPage = optionPage.getLastPage();
			page = optionPage.getPage();
			pageSize = optionPage.getPageSize();
			totalCount = optionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Option> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("OptionCategoryPage")
	public class OptionCategoryPage {

		public OptionCategoryPage(Page optionCategoryPage) {
			actions = optionCategoryPage.getActions();

			items = optionCategoryPage.getItems();
			lastPage = optionCategoryPage.getLastPage();
			page = optionCategoryPage.getPage();
			pageSize = optionCategoryPage.getPageSize();
			totalCount = optionCategoryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<OptionCategory> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("OptionValuePage")
	public class OptionValuePage {

		public OptionValuePage(Page optionValuePage) {
			actions = optionValuePage.getActions();

			items = optionValuePage.getItems();
			lastPage = optionValuePage.getLastPage();
			page = optionValuePage.getPage();
			pageSize = optionValuePage.getPageSize();
			totalCount = optionValuePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<OptionValue> items;

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

	@GraphQLName("ProductAccountGroupPage")
	public class ProductAccountGroupPage {

		public ProductAccountGroupPage(Page productAccountGroupPage) {
			actions = productAccountGroupPage.getActions();

			items = productAccountGroupPage.getItems();
			lastPage = productAccountGroupPage.getLastPage();
			page = productAccountGroupPage.getPage();
			pageSize = productAccountGroupPage.getPageSize();
			totalCount = productAccountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductAccountGroup> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductChannelPage")
	public class ProductChannelPage {

		public ProductChannelPage(Page productChannelPage) {
			actions = productChannelPage.getActions();

			items = productChannelPage.getItems();
			lastPage = productChannelPage.getLastPage();
			page = productChannelPage.getPage();
			pageSize = productChannelPage.getPageSize();
			totalCount = productChannelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductChannel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductConfigurationPage")
	public class ProductConfigurationPage {

		public ProductConfigurationPage(Page productConfigurationPage) {
			actions = productConfigurationPage.getActions();

			items = productConfigurationPage.getItems();
			lastPage = productConfigurationPage.getLastPage();
			page = productConfigurationPage.getPage();
			pageSize = productConfigurationPage.getPageSize();
			totalCount = productConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductConfiguration> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductConfigurationListPage")
	public class ProductConfigurationListPage {

		public ProductConfigurationListPage(Page productConfigurationListPage) {
			actions = productConfigurationListPage.getActions();

			items = productConfigurationListPage.getItems();
			lastPage = productConfigurationListPage.getLastPage();
			page = productConfigurationListPage.getPage();
			pageSize = productConfigurationListPage.getPageSize();
			totalCount = productConfigurationListPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductConfigurationList> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductConfigurationListAccountPage")
	public class ProductConfigurationListAccountPage {

		public ProductConfigurationListAccountPage(
			Page productConfigurationListAccountPage) {

			actions = productConfigurationListAccountPage.getActions();

			items = productConfigurationListAccountPage.getItems();
			lastPage = productConfigurationListAccountPage.getLastPage();
			page = productConfigurationListAccountPage.getPage();
			pageSize = productConfigurationListAccountPage.getPageSize();
			totalCount = productConfigurationListAccountPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductConfigurationListAccount> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductConfigurationListAccountGroupPage")
	public class ProductConfigurationListAccountGroupPage {

		public ProductConfigurationListAccountGroupPage(
			Page productConfigurationListAccountGroupPage) {

			actions = productConfigurationListAccountGroupPage.getActions();

			items = productConfigurationListAccountGroupPage.getItems();
			lastPage = productConfigurationListAccountGroupPage.getLastPage();
			page = productConfigurationListAccountGroupPage.getPage();
			pageSize = productConfigurationListAccountGroupPage.getPageSize();
			totalCount =
				productConfigurationListAccountGroupPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductConfigurationListAccountGroup>
			items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductConfigurationListChannelPage")
	public class ProductConfigurationListChannelPage {

		public ProductConfigurationListChannelPage(
			Page productConfigurationListChannelPage) {

			actions = productConfigurationListChannelPage.getActions();

			items = productConfigurationListChannelPage.getItems();
			lastPage = productConfigurationListChannelPage.getLastPage();
			page = productConfigurationListChannelPage.getPage();
			pageSize = productConfigurationListChannelPage.getPageSize();
			totalCount = productConfigurationListChannelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductConfigurationListChannel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductConfigurationListOrderTypePage")
	public class ProductConfigurationListOrderTypePage {

		public ProductConfigurationListOrderTypePage(
			Page productConfigurationListOrderTypePage) {

			actions = productConfigurationListOrderTypePage.getActions();

			items = productConfigurationListOrderTypePage.getItems();
			lastPage = productConfigurationListOrderTypePage.getLastPage();
			page = productConfigurationListOrderTypePage.getPage();
			pageSize = productConfigurationListOrderTypePage.getPageSize();
			totalCount = productConfigurationListOrderTypePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductConfigurationListOrderType> items;

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

	@GraphQLName("ProductGroupProductPage")
	public class ProductGroupProductPage {

		public ProductGroupProductPage(Page productGroupProductPage) {
			actions = productGroupProductPage.getActions();

			items = productGroupProductPage.getItems();
			lastPage = productGroupProductPage.getLastPage();
			page = productGroupProductPage.getPage();
			pageSize = productGroupProductPage.getPageSize();
			totalCount = productGroupProductPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductGroupProduct> items;

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

	@GraphQLName("ProductShippingConfigurationPage")
	public class ProductShippingConfigurationPage {

		public ProductShippingConfigurationPage(
			Page productShippingConfigurationPage) {

			actions = productShippingConfigurationPage.getActions();

			items = productShippingConfigurationPage.getItems();
			lastPage = productShippingConfigurationPage.getLastPage();
			page = productShippingConfigurationPage.getPage();
			pageSize = productShippingConfigurationPage.getPageSize();
			totalCount = productShippingConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductShippingConfiguration> items;

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

	@GraphQLName("ProductSubscriptionConfigurationPage")
	public class ProductSubscriptionConfigurationPage {

		public ProductSubscriptionConfigurationPage(
			Page productSubscriptionConfigurationPage) {

			actions = productSubscriptionConfigurationPage.getActions();

			items = productSubscriptionConfigurationPage.getItems();
			lastPage = productSubscriptionConfigurationPage.getLastPage();
			page = productSubscriptionConfigurationPage.getPage();
			pageSize = productSubscriptionConfigurationPage.getPageSize();
			totalCount = productSubscriptionConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductSubscriptionConfiguration> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductTaxConfigurationPage")
	public class ProductTaxConfigurationPage {

		public ProductTaxConfigurationPage(Page productTaxConfigurationPage) {
			actions = productTaxConfigurationPage.getActions();

			items = productTaxConfigurationPage.getItems();
			lastPage = productTaxConfigurationPage.getLastPage();
			page = productTaxConfigurationPage.getPage();
			pageSize = productTaxConfigurationPage.getPageSize();
			totalCount = productTaxConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductTaxConfiguration> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductVirtualSettingsPage")
	public class ProductVirtualSettingsPage {

		public ProductVirtualSettingsPage(Page productVirtualSettingsPage) {
			actions = productVirtualSettingsPage.getActions();

			items = productVirtualSettingsPage.getItems();
			lastPage = productVirtualSettingsPage.getLastPage();
			page = productVirtualSettingsPage.getPage();
			pageSize = productVirtualSettingsPage.getPageSize();
			totalCount = productVirtualSettingsPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductVirtualSettings> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProductVirtualSettingsFileEntryPage")
	public class ProductVirtualSettingsFileEntryPage {

		public ProductVirtualSettingsFileEntryPage(
			Page productVirtualSettingsFileEntryPage) {

			actions = productVirtualSettingsFileEntryPage.getActions();

			items = productVirtualSettingsFileEntryPage.getItems();
			lastPage = productVirtualSettingsFileEntryPage.getLastPage();
			page = productVirtualSettingsFileEntryPage.getPage();
			pageSize = productVirtualSettingsFileEntryPage.getPageSize();
			totalCount = productVirtualSettingsFileEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProductVirtualSettingsFileEntry> items;

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

	@GraphQLName("SkuSubscriptionConfigurationPage")
	public class SkuSubscriptionConfigurationPage {

		public SkuSubscriptionConfigurationPage(
			Page skuSubscriptionConfigurationPage) {

			actions = skuSubscriptionConfigurationPage.getActions();

			items = skuSubscriptionConfigurationPage.getItems();
			lastPage = skuSubscriptionConfigurationPage.getLastPage();
			page = skuSubscriptionConfigurationPage.getPage();
			pageSize = skuSubscriptionConfigurationPage.getPageSize();
			totalCount = skuSubscriptionConfigurationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SkuSubscriptionConfiguration> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SkuUnitOfMeasurePage")
	public class SkuUnitOfMeasurePage {

		public SkuUnitOfMeasurePage(Page skuUnitOfMeasurePage) {
			actions = skuUnitOfMeasurePage.getActions();

			items = skuUnitOfMeasurePage.getItems();
			lastPage = skuUnitOfMeasurePage.getLastPage();
			page = skuUnitOfMeasurePage.getPage();
			pageSize = skuUnitOfMeasurePage.getPageSize();
			totalCount = skuUnitOfMeasurePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SkuUnitOfMeasure> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SkuVirtualSettingsPage")
	public class SkuVirtualSettingsPage {

		public SkuVirtualSettingsPage(Page skuVirtualSettingsPage) {
			actions = skuVirtualSettingsPage.getActions();

			items = skuVirtualSettingsPage.getItems();
			lastPage = skuVirtualSettingsPage.getLastPage();
			page = skuVirtualSettingsPage.getPage();
			pageSize = skuVirtualSettingsPage.getPageSize();
			totalCount = skuVirtualSettingsPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SkuVirtualSettings> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SkuVirtualSettingsFileEntryPage")
	public class SkuVirtualSettingsFileEntryPage {

		public SkuVirtualSettingsFileEntryPage(
			Page skuVirtualSettingsFileEntryPage) {

			actions = skuVirtualSettingsFileEntryPage.getActions();

			items = skuVirtualSettingsFileEntryPage.getItems();
			lastPage = skuVirtualSettingsFileEntryPage.getLastPage();
			page = skuVirtualSettingsFileEntryPage.getPage();
			pageSize = skuVirtualSettingsFileEntryPage.getPageSize();
			totalCount = skuVirtualSettingsFileEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SkuVirtualSettingsFileEntry> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SpecificationPage")
	public class SpecificationPage {

		public SpecificationPage(Page specificationPage) {
			actions = specificationPage.getActions();

			items = specificationPage.getItems();
			lastPage = specificationPage.getLastPage();
			page = specificationPage.getPage();
			pageSize = specificationPage.getPageSize();
			totalCount = specificationPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Specification> items;

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

	private void _populateResourceContext(CatalogResource catalogResource)
		throws Exception {

		catalogResource.setContextAcceptLanguage(_acceptLanguage);
		catalogResource.setContextCompany(_company);
		catalogResource.setContextHttpServletRequest(_httpServletRequest);
		catalogResource.setContextHttpServletResponse(_httpServletResponse);
		catalogResource.setContextUriInfo(_uriInfo);
		catalogResource.setContextUser(_user);
		catalogResource.setGroupLocalService(_groupLocalService);
		catalogResource.setResourceActionLocalService(
			_resourceActionLocalService);
		catalogResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		catalogResource.setRoleLocalService(_roleLocalService);
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

	private void _populateResourceContext(DiagramResource diagramResource)
		throws Exception {

		diagramResource.setContextAcceptLanguage(_acceptLanguage);
		diagramResource.setContextCompany(_company);
		diagramResource.setContextHttpServletRequest(_httpServletRequest);
		diagramResource.setContextHttpServletResponse(_httpServletResponse);
		diagramResource.setContextUriInfo(_uriInfo);
		diagramResource.setContextUser(_user);
		diagramResource.setGroupLocalService(_groupLocalService);
		diagramResource.setResourceActionLocalService(
			_resourceActionLocalService);
		diagramResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		diagramResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			GroupedProductResource groupedProductResource)
		throws Exception {

		groupedProductResource.setContextAcceptLanguage(_acceptLanguage);
		groupedProductResource.setContextCompany(_company);
		groupedProductResource.setContextHttpServletRequest(
			_httpServletRequest);
		groupedProductResource.setContextHttpServletResponse(
			_httpServletResponse);
		groupedProductResource.setContextUriInfo(_uriInfo);
		groupedProductResource.setContextUser(_user);
		groupedProductResource.setGroupLocalService(_groupLocalService);
		groupedProductResource.setResourceActionLocalService(
			_resourceActionLocalService);
		groupedProductResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		groupedProductResource.setRoleLocalService(_roleLocalService);
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
			ListTypeDefinitionResource listTypeDefinitionResource)
		throws Exception {

		listTypeDefinitionResource.setContextAcceptLanguage(_acceptLanguage);
		listTypeDefinitionResource.setContextCompany(_company);
		listTypeDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		listTypeDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		listTypeDefinitionResource.setContextUriInfo(_uriInfo);
		listTypeDefinitionResource.setContextUser(_user);
		listTypeDefinitionResource.setGroupLocalService(_groupLocalService);
		listTypeDefinitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		listTypeDefinitionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		listTypeDefinitionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			LowStockActionResource lowStockActionResource)
		throws Exception {

		lowStockActionResource.setContextAcceptLanguage(_acceptLanguage);
		lowStockActionResource.setContextCompany(_company);
		lowStockActionResource.setContextHttpServletRequest(
			_httpServletRequest);
		lowStockActionResource.setContextHttpServletResponse(
			_httpServletResponse);
		lowStockActionResource.setContextUriInfo(_uriInfo);
		lowStockActionResource.setContextUser(_user);
		lowStockActionResource.setGroupLocalService(_groupLocalService);
		lowStockActionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		lowStockActionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		lowStockActionResource.setRoleLocalService(_roleLocalService);
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

	private void _populateResourceContext(OptionResource optionResource)
		throws Exception {

		optionResource.setContextAcceptLanguage(_acceptLanguage);
		optionResource.setContextCompany(_company);
		optionResource.setContextHttpServletRequest(_httpServletRequest);
		optionResource.setContextHttpServletResponse(_httpServletResponse);
		optionResource.setContextUriInfo(_uriInfo);
		optionResource.setContextUser(_user);
		optionResource.setGroupLocalService(_groupLocalService);
		optionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		optionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		optionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			OptionCategoryResource optionCategoryResource)
		throws Exception {

		optionCategoryResource.setContextAcceptLanguage(_acceptLanguage);
		optionCategoryResource.setContextCompany(_company);
		optionCategoryResource.setContextHttpServletRequest(
			_httpServletRequest);
		optionCategoryResource.setContextHttpServletResponse(
			_httpServletResponse);
		optionCategoryResource.setContextUriInfo(_uriInfo);
		optionCategoryResource.setContextUser(_user);
		optionCategoryResource.setGroupLocalService(_groupLocalService);
		optionCategoryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		optionCategoryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		optionCategoryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			OptionValueResource optionValueResource)
		throws Exception {

		optionValueResource.setContextAcceptLanguage(_acceptLanguage);
		optionValueResource.setContextCompany(_company);
		optionValueResource.setContextHttpServletRequest(_httpServletRequest);
		optionValueResource.setContextHttpServletResponse(_httpServletResponse);
		optionValueResource.setContextUriInfo(_uriInfo);
		optionValueResource.setContextUser(_user);
		optionValueResource.setGroupLocalService(_groupLocalService);
		optionValueResource.setResourceActionLocalService(
			_resourceActionLocalService);
		optionValueResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		optionValueResource.setRoleLocalService(_roleLocalService);
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
			ProductAccountGroupResource productAccountGroupResource)
		throws Exception {

		productAccountGroupResource.setContextAcceptLanguage(_acceptLanguage);
		productAccountGroupResource.setContextCompany(_company);
		productAccountGroupResource.setContextHttpServletRequest(
			_httpServletRequest);
		productAccountGroupResource.setContextHttpServletResponse(
			_httpServletResponse);
		productAccountGroupResource.setContextUriInfo(_uriInfo);
		productAccountGroupResource.setContextUser(_user);
		productAccountGroupResource.setGroupLocalService(_groupLocalService);
		productAccountGroupResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productAccountGroupResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productAccountGroupResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductChannelResource productChannelResource)
		throws Exception {

		productChannelResource.setContextAcceptLanguage(_acceptLanguage);
		productChannelResource.setContextCompany(_company);
		productChannelResource.setContextHttpServletRequest(
			_httpServletRequest);
		productChannelResource.setContextHttpServletResponse(
			_httpServletResponse);
		productChannelResource.setContextUriInfo(_uriInfo);
		productChannelResource.setContextUser(_user);
		productChannelResource.setGroupLocalService(_groupLocalService);
		productChannelResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productChannelResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productChannelResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductConfigurationResource productConfigurationResource)
		throws Exception {

		productConfigurationResource.setContextAcceptLanguage(_acceptLanguage);
		productConfigurationResource.setContextCompany(_company);
		productConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		productConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		productConfigurationResource.setContextUriInfo(_uriInfo);
		productConfigurationResource.setContextUser(_user);
		productConfigurationResource.setGroupLocalService(_groupLocalService);
		productConfigurationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productConfigurationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productConfigurationResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductConfigurationListResource productConfigurationListResource)
		throws Exception {

		productConfigurationListResource.setContextAcceptLanguage(
			_acceptLanguage);
		productConfigurationListResource.setContextCompany(_company);
		productConfigurationListResource.setContextHttpServletRequest(
			_httpServletRequest);
		productConfigurationListResource.setContextHttpServletResponse(
			_httpServletResponse);
		productConfigurationListResource.setContextUriInfo(_uriInfo);
		productConfigurationListResource.setContextUser(_user);
		productConfigurationListResource.setGroupLocalService(
			_groupLocalService);
		productConfigurationListResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productConfigurationListResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productConfigurationListResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductConfigurationListAccountResource
				productConfigurationListAccountResource)
		throws Exception {

		productConfigurationListAccountResource.setContextAcceptLanguage(
			_acceptLanguage);
		productConfigurationListAccountResource.setContextCompany(_company);
		productConfigurationListAccountResource.setContextHttpServletRequest(
			_httpServletRequest);
		productConfigurationListAccountResource.setContextHttpServletResponse(
			_httpServletResponse);
		productConfigurationListAccountResource.setContextUriInfo(_uriInfo);
		productConfigurationListAccountResource.setContextUser(_user);
		productConfigurationListAccountResource.setGroupLocalService(
			_groupLocalService);
		productConfigurationListAccountResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productConfigurationListAccountResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		productConfigurationListAccountResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			ProductConfigurationListAccountGroupResource
				productConfigurationListAccountGroupResource)
		throws Exception {

		productConfigurationListAccountGroupResource.setContextAcceptLanguage(
			_acceptLanguage);
		productConfigurationListAccountGroupResource.setContextCompany(
			_company);
		productConfigurationListAccountGroupResource.
			setContextHttpServletRequest(_httpServletRequest);
		productConfigurationListAccountGroupResource.
			setContextHttpServletResponse(_httpServletResponse);
		productConfigurationListAccountGroupResource.setContextUriInfo(
			_uriInfo);
		productConfigurationListAccountGroupResource.setContextUser(_user);
		productConfigurationListAccountGroupResource.setGroupLocalService(
			_groupLocalService);
		productConfigurationListAccountGroupResource.
			setResourceActionLocalService(_resourceActionLocalService);
		productConfigurationListAccountGroupResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		productConfigurationListAccountGroupResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			ProductConfigurationListChannelResource
				productConfigurationListChannelResource)
		throws Exception {

		productConfigurationListChannelResource.setContextAcceptLanguage(
			_acceptLanguage);
		productConfigurationListChannelResource.setContextCompany(_company);
		productConfigurationListChannelResource.setContextHttpServletRequest(
			_httpServletRequest);
		productConfigurationListChannelResource.setContextHttpServletResponse(
			_httpServletResponse);
		productConfigurationListChannelResource.setContextUriInfo(_uriInfo);
		productConfigurationListChannelResource.setContextUser(_user);
		productConfigurationListChannelResource.setGroupLocalService(
			_groupLocalService);
		productConfigurationListChannelResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productConfigurationListChannelResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		productConfigurationListChannelResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			ProductConfigurationListOrderTypeResource
				productConfigurationListOrderTypeResource)
		throws Exception {

		productConfigurationListOrderTypeResource.setContextAcceptLanguage(
			_acceptLanguage);
		productConfigurationListOrderTypeResource.setContextCompany(_company);
		productConfigurationListOrderTypeResource.setContextHttpServletRequest(
			_httpServletRequest);
		productConfigurationListOrderTypeResource.setContextHttpServletResponse(
			_httpServletResponse);
		productConfigurationListOrderTypeResource.setContextUriInfo(_uriInfo);
		productConfigurationListOrderTypeResource.setContextUser(_user);
		productConfigurationListOrderTypeResource.setGroupLocalService(
			_groupLocalService);
		productConfigurationListOrderTypeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productConfigurationListOrderTypeResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		productConfigurationListOrderTypeResource.setRoleLocalService(
			_roleLocalService);
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

	private void _populateResourceContext(
			ProductGroupProductResource productGroupProductResource)
		throws Exception {

		productGroupProductResource.setContextAcceptLanguage(_acceptLanguage);
		productGroupProductResource.setContextCompany(_company);
		productGroupProductResource.setContextHttpServletRequest(
			_httpServletRequest);
		productGroupProductResource.setContextHttpServletResponse(
			_httpServletResponse);
		productGroupProductResource.setContextUriInfo(_uriInfo);
		productGroupProductResource.setContextUser(_user);
		productGroupProductResource.setGroupLocalService(_groupLocalService);
		productGroupProductResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productGroupProductResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productGroupProductResource.setRoleLocalService(_roleLocalService);
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
			ProductShippingConfigurationResource
				productShippingConfigurationResource)
		throws Exception {

		productShippingConfigurationResource.setContextAcceptLanguage(
			_acceptLanguage);
		productShippingConfigurationResource.setContextCompany(_company);
		productShippingConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		productShippingConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		productShippingConfigurationResource.setContextUriInfo(_uriInfo);
		productShippingConfigurationResource.setContextUser(_user);
		productShippingConfigurationResource.setGroupLocalService(
			_groupLocalService);
		productShippingConfigurationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productShippingConfigurationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productShippingConfigurationResource.setRoleLocalService(
			_roleLocalService);
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
			ProductSubscriptionConfigurationResource
				productSubscriptionConfigurationResource)
		throws Exception {

		productSubscriptionConfigurationResource.setContextAcceptLanguage(
			_acceptLanguage);
		productSubscriptionConfigurationResource.setContextCompany(_company);
		productSubscriptionConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		productSubscriptionConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		productSubscriptionConfigurationResource.setContextUriInfo(_uriInfo);
		productSubscriptionConfigurationResource.setContextUser(_user);
		productSubscriptionConfigurationResource.setGroupLocalService(
			_groupLocalService);
		productSubscriptionConfigurationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productSubscriptionConfigurationResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		productSubscriptionConfigurationResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			ProductTaxConfigurationResource productTaxConfigurationResource)
		throws Exception {

		productTaxConfigurationResource.setContextAcceptLanguage(
			_acceptLanguage);
		productTaxConfigurationResource.setContextCompany(_company);
		productTaxConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		productTaxConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		productTaxConfigurationResource.setContextUriInfo(_uriInfo);
		productTaxConfigurationResource.setContextUser(_user);
		productTaxConfigurationResource.setGroupLocalService(
			_groupLocalService);
		productTaxConfigurationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productTaxConfigurationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productTaxConfigurationResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductVirtualSettingsResource productVirtualSettingsResource)
		throws Exception {

		productVirtualSettingsResource.setContextAcceptLanguage(
			_acceptLanguage);
		productVirtualSettingsResource.setContextCompany(_company);
		productVirtualSettingsResource.setContextHttpServletRequest(
			_httpServletRequest);
		productVirtualSettingsResource.setContextHttpServletResponse(
			_httpServletResponse);
		productVirtualSettingsResource.setContextUriInfo(_uriInfo);
		productVirtualSettingsResource.setContextUser(_user);
		productVirtualSettingsResource.setGroupLocalService(_groupLocalService);
		productVirtualSettingsResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productVirtualSettingsResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		productVirtualSettingsResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProductVirtualSettingsFileEntryResource
				productVirtualSettingsFileEntryResource)
		throws Exception {

		productVirtualSettingsFileEntryResource.setContextAcceptLanguage(
			_acceptLanguage);
		productVirtualSettingsFileEntryResource.setContextCompany(_company);
		productVirtualSettingsFileEntryResource.setContextHttpServletRequest(
			_httpServletRequest);
		productVirtualSettingsFileEntryResource.setContextHttpServletResponse(
			_httpServletResponse);
		productVirtualSettingsFileEntryResource.setContextUriInfo(_uriInfo);
		productVirtualSettingsFileEntryResource.setContextUser(_user);
		productVirtualSettingsFileEntryResource.setGroupLocalService(
			_groupLocalService);
		productVirtualSettingsFileEntryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		productVirtualSettingsFileEntryResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		productVirtualSettingsFileEntryResource.setRoleLocalService(
			_roleLocalService);
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

	private void _populateResourceContext(
			SkuSubscriptionConfigurationResource
				skuSubscriptionConfigurationResource)
		throws Exception {

		skuSubscriptionConfigurationResource.setContextAcceptLanguage(
			_acceptLanguage);
		skuSubscriptionConfigurationResource.setContextCompany(_company);
		skuSubscriptionConfigurationResource.setContextHttpServletRequest(
			_httpServletRequest);
		skuSubscriptionConfigurationResource.setContextHttpServletResponse(
			_httpServletResponse);
		skuSubscriptionConfigurationResource.setContextUriInfo(_uriInfo);
		skuSubscriptionConfigurationResource.setContextUser(_user);
		skuSubscriptionConfigurationResource.setGroupLocalService(
			_groupLocalService);
		skuSubscriptionConfigurationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		skuSubscriptionConfigurationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		skuSubscriptionConfigurationResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			SkuUnitOfMeasureResource skuUnitOfMeasureResource)
		throws Exception {

		skuUnitOfMeasureResource.setContextAcceptLanguage(_acceptLanguage);
		skuUnitOfMeasureResource.setContextCompany(_company);
		skuUnitOfMeasureResource.setContextHttpServletRequest(
			_httpServletRequest);
		skuUnitOfMeasureResource.setContextHttpServletResponse(
			_httpServletResponse);
		skuUnitOfMeasureResource.setContextUriInfo(_uriInfo);
		skuUnitOfMeasureResource.setContextUser(_user);
		skuUnitOfMeasureResource.setGroupLocalService(_groupLocalService);
		skuUnitOfMeasureResource.setResourceActionLocalService(
			_resourceActionLocalService);
		skuUnitOfMeasureResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		skuUnitOfMeasureResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SkuVirtualSettingsResource skuVirtualSettingsResource)
		throws Exception {

		skuVirtualSettingsResource.setContextAcceptLanguage(_acceptLanguage);
		skuVirtualSettingsResource.setContextCompany(_company);
		skuVirtualSettingsResource.setContextHttpServletRequest(
			_httpServletRequest);
		skuVirtualSettingsResource.setContextHttpServletResponse(
			_httpServletResponse);
		skuVirtualSettingsResource.setContextUriInfo(_uriInfo);
		skuVirtualSettingsResource.setContextUser(_user);
		skuVirtualSettingsResource.setGroupLocalService(_groupLocalService);
		skuVirtualSettingsResource.setResourceActionLocalService(
			_resourceActionLocalService);
		skuVirtualSettingsResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		skuVirtualSettingsResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SkuVirtualSettingsFileEntryResource
				skuVirtualSettingsFileEntryResource)
		throws Exception {

		skuVirtualSettingsFileEntryResource.setContextAcceptLanguage(
			_acceptLanguage);
		skuVirtualSettingsFileEntryResource.setContextCompany(_company);
		skuVirtualSettingsFileEntryResource.setContextHttpServletRequest(
			_httpServletRequest);
		skuVirtualSettingsFileEntryResource.setContextHttpServletResponse(
			_httpServletResponse);
		skuVirtualSettingsFileEntryResource.setContextUriInfo(_uriInfo);
		skuVirtualSettingsFileEntryResource.setContextUser(_user);
		skuVirtualSettingsFileEntryResource.setGroupLocalService(
			_groupLocalService);
		skuVirtualSettingsFileEntryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		skuVirtualSettingsFileEntryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		skuVirtualSettingsFileEntryResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			SpecificationResource specificationResource)
		throws Exception {

		specificationResource.setContextAcceptLanguage(_acceptLanguage);
		specificationResource.setContextCompany(_company);
		specificationResource.setContextHttpServletRequest(_httpServletRequest);
		specificationResource.setContextHttpServletResponse(
			_httpServletResponse);
		specificationResource.setContextUriInfo(_uriInfo);
		specificationResource.setContextUser(_user);
		specificationResource.setGroupLocalService(_groupLocalService);
		specificationResource.setResourceActionLocalService(
			_resourceActionLocalService);
		specificationResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		specificationResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AttachmentResource>
		_attachmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<CatalogResource>
		_catalogResourceComponentServiceObjects;
	private static ComponentServiceObjects<CategoryResource>
		_categoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<CurrencyResource>
		_currencyResourceComponentServiceObjects;
	private static ComponentServiceObjects<DiagramResource>
		_diagramResourceComponentServiceObjects;
	private static ComponentServiceObjects<GroupedProductResource>
		_groupedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<LinkedProductResource>
		_linkedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<ListTypeDefinitionResource>
		_listTypeDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<LowStockActionResource>
		_lowStockActionResourceComponentServiceObjects;
	private static ComponentServiceObjects<MappedProductResource>
		_mappedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<OptionResource>
		_optionResourceComponentServiceObjects;
	private static ComponentServiceObjects<OptionCategoryResource>
		_optionCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<OptionValueResource>
		_optionValueResourceComponentServiceObjects;
	private static ComponentServiceObjects<PinResource>
		_pinResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductResource>
		_productResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductAccountGroupResource>
		_productAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductChannelResource>
		_productChannelResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductConfigurationResource>
		_productConfigurationResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductConfigurationListResource>
		_productConfigurationListResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<ProductConfigurationListAccountResource>
			_productConfigurationListAccountResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<ProductConfigurationListAccountGroupResource>
			_productConfigurationListAccountGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<ProductConfigurationListChannelResource>
			_productConfigurationListChannelResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<ProductConfigurationListOrderTypeResource>
			_productConfigurationListOrderTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductGroupResource>
		_productGroupResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductGroupProductResource>
		_productGroupProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductOptionResource>
		_productOptionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductOptionValueResource>
		_productOptionValueResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductShippingConfigurationResource>
		_productShippingConfigurationResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductSpecificationResource>
		_productSpecificationResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<ProductSubscriptionConfigurationResource>
			_productSubscriptionConfigurationResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductTaxConfigurationResource>
		_productTaxConfigurationResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProductVirtualSettingsResource>
		_productVirtualSettingsResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<ProductVirtualSettingsFileEntryResource>
			_productVirtualSettingsFileEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<RelatedProductResource>
		_relatedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuResource>
		_skuResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuSubscriptionConfigurationResource>
		_skuSubscriptionConfigurationResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuUnitOfMeasureResource>
		_skuUnitOfMeasureResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuVirtualSettingsResource>
		_skuVirtualSettingsResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuVirtualSettingsFileEntryResource>
		_skuVirtualSettingsFileEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<SpecificationResource>
		_specificationResourceComponentServiceObjects;

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