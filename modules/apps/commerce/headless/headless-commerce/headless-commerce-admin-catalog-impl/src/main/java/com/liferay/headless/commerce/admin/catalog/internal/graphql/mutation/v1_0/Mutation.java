/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.graphql.mutation.v1_0;

import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentUrl;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Category;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Currency;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Diagram;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.GroupedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Option;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionCategory;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Pin;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
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
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.RelatedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CatalogResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CategoryResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CurrencyResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.DiagramResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.GroupedProductResource;
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
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.RelatedProductResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuUnitOfMeasureResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuVirtualSettingsFileEntryResource;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.SpecificationResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class Mutation {

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

	public static void setSkuUnitOfMeasureResourceComponentServiceObjects(
		ComponentServiceObjects<SkuUnitOfMeasureResource>
			skuUnitOfMeasureResourceComponentServiceObjects) {

		_skuUnitOfMeasureResourceComponentServiceObjects =
			skuUnitOfMeasureResourceComponentServiceObjects;
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

	@GraphQLField(
		description = "Deletes the attachment identified by its internal attachment ID. Returns 404 when the ID is not found. Side effects -- Removes the underlying DL file entry association; cascades through attachment delete listeners."
	)
	public boolean deleteAttachment(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> attachmentResource.deleteAttachment(id));

		return true;
	}

	@GraphQLField
	public Response deleteAttachmentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> attachmentResource.deleteAttachmentBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the attachment identified by its external reference code. Returns 404 when the external reference code is not found. Side effects -- Removes the underlying DL file entry association via the attachment delete cascade."
	)
	public boolean deleteAttachmentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.deleteAttachmentByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the attachment identified by its external reference code. Returns 404 when the external reference code is not found. Side effects -- Updates DL file entry metadata and asset tags as side effects of attachment update."
	)
	public Attachment patchAttachmentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachment") Attachment attachment)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.patchAttachmentByExternalReferenceCode(
					externalReferenceCode, attachment));
	}

	@GraphQLField(
		description = "Creates or updates an attachment (TYPE_OTHER) under the product identified by external reference code, using an inline file payload. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found. Side effects -- Creates or updates a DL file entry under the product group; custom field storage attributes and asset tags applied."
	)
	public Attachment createProductByExternalReferenceCodeAttachment(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachment") Attachment attachment)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.postProductByExternalReferenceCodeAttachment(
					externalReferenceCode, attachment));
	}

	@GraphQLField(
		description = "Creates or updates an attachment (TYPE_OTHER) under the product identified by external reference code, supplied as a base64-encoded payload. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found. Side effects -- Decodes the base64 file and creates a DL file entry under the product group; asset tags and custom field storage attributes applied."
	)
	public Attachment createProductByExternalReferenceCodeAttachmentByBase64(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachmentBase64") AttachmentBase64 attachmentBase64)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.
					postProductByExternalReferenceCodeAttachmentByBase64(
						externalReferenceCode, attachmentBase64));
	}

	@GraphQLField(
		description = "Creates or updates an attachment (TYPE_OTHER) under the product identified by external reference code, referenced by URL. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found. Side effects -- Stores the URL reference; DL file entry is not necessarily downloaded."
	)
	public Attachment createProductByExternalReferenceCodeAttachmentByUrl(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachmentUrl") AttachmentUrl attachmentUrl)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.
					postProductByExternalReferenceCodeAttachmentByUrl(
						externalReferenceCode, attachmentUrl));
	}

	@GraphQLField(
		description = "Creates or updates an image attachment (TYPE_IMAGE) under the product identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found. Side effects -- Creates or updates a DL file entry under the product group; asset tags and custom field storage attributes applied."
	)
	public Attachment createProductByExternalReferenceCodeImage(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachment") Attachment attachment)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.postProductByExternalReferenceCodeImage(
					externalReferenceCode, attachment));
	}

	@GraphQLField(
		description = "Creates or updates an image attachment (TYPE_IMAGE) under the product identified by external reference code, supplied as a base64-encoded payload. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found. Side effects -- Decodes the base64 file and creates a DL file entry under the product group."
	)
	public Attachment createProductByExternalReferenceCodeImageByBase64(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachmentBase64") AttachmentBase64 attachmentBase64)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.
					postProductByExternalReferenceCodeImageByBase64(
						externalReferenceCode, attachmentBase64));
	}

	@GraphQLField(
		description = "Creates or updates an image attachment (TYPE_IMAGE) under the product identified by external reference code, referenced by URL. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found. Side effects -- Stores the URL reference."
	)
	public Attachment createProductByExternalReferenceCodeImageByUrl(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachmentUrl") AttachmentUrl attachmentUrl)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.postProductByExternalReferenceCodeImageByUrl(
					externalReferenceCode, attachmentUrl));
	}

	@GraphQLField(
		description = "Creates or updates an attachment (TYPE_OTHER) under the product identified by product ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found. Side effects -- Creates or updates a DL file entry under the product group."
	)
	public Attachment createProductIdAttachment(
			@GraphQLName("id") Long id,
			@GraphQLName("attachment") Attachment attachment)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> attachmentResource.postProductIdAttachment(
				id, attachment));
	}

	@GraphQLField
	public Response createProductIdAttachmentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.postProductIdAttachmentBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Creates or updates an attachment (TYPE_OTHER) under the product identified by product ID, supplied as a base64-encoded payload. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found. Side effects -- Decodes the base64 file and creates a DL file entry under the product group."
	)
	public Attachment createProductIdAttachmentByBase64(
			@GraphQLName("id") Long id,
			@GraphQLName("attachmentBase64") AttachmentBase64 attachmentBase64)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.postProductIdAttachmentByBase64(
					id, attachmentBase64));
	}

	@GraphQLField(
		description = "Creates or updates an attachment (TYPE_OTHER) under the product identified by product ID, referenced by URL. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found. Side effects -- Stores the URL reference."
	)
	public Attachment createProductIdAttachmentByUrl(
			@GraphQLName("id") Long id,
			@GraphQLName("attachmentUrl") AttachmentUrl attachmentUrl)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.postProductIdAttachmentByUrl(
					id, attachmentUrl));
	}

	@GraphQLField(
		description = "Creates or updates an image attachment (TYPE_IMAGE) under the product identified by product ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found. Side effects -- Creates or updates a DL file entry under the product group."
	)
	public Attachment createProductIdImage(
			@GraphQLName("id") Long id,
			@GraphQLName("attachment") Attachment attachment)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> attachmentResource.postProductIdImage(
				id, attachment));
	}

	@GraphQLField(
		description = "Creates or updates an image attachment (TYPE_IMAGE) under the product identified by product ID, supplied as a base64-encoded payload. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found. Side effects -- Decodes the base64 file and creates a DL file entry."
	)
	public Attachment createProductIdImageByBase64(
			@GraphQLName("id") Long id,
			@GraphQLName("attachmentBase64") AttachmentBase64 attachmentBase64)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> attachmentResource.postProductIdImageByBase64(
				id, attachmentBase64));
	}

	@GraphQLField(
		description = "Creates or updates an image attachment (TYPE_IMAGE) under the product identified by product ID, referenced by URL. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found. Side effects -- Stores the URL reference."
	)
	public Attachment createProductIdImageByUrl(
			@GraphQLName("id") Long id,
			@GraphQLName("attachmentUrl") AttachmentUrl attachmentUrl)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource -> attachmentResource.postProductIdImageByUrl(
				id, attachmentUrl));
	}

	@GraphQLField(
		description = "Replaces the attachment identified by external reference code with the supplied representation. Returns 404 when the external reference code is not found. Side effects -- Updates DL file entry metadata, asset tags, and custom field storage attributes."
	)
	public Attachment updateAttachmentByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("attachment") Attachment attachment)
		throws Exception {

		return _applyComponentServiceObjects(
			_attachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			attachmentResource ->
				attachmentResource.putAttachmentByExternalReferenceCode(
					externalReferenceCode, attachment));
	}

	@GraphQLField(
		description = "Deletes the commerce catalog identified by ID. Returns 404 when the ID is missing (service-level). Side effects -- Cascades through catalog delete listeners (group, virtual instance teardown)."
	)
	public Response deleteCatalog(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.deleteCatalog(id));
	}

	@GraphQLField
	public Response deleteCatalogBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.deleteCatalogBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the commerce catalog identified by its external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades through catalog delete listeners."
	)
	public Response deleteCatalogByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource ->
				catalogResource.deleteCatalogByExternalReferenceCode(
					externalReferenceCode));
	}

	@GraphQLField(
		description = "Partially updates the commerce catalog identified by ID. Returns 404 when the ID is not found, and tolerated (debug log). Side effects -- Reindexes the catalog; touches the underlying group."
	)
	public Response patchCatalog(
			@GraphQLName("id") Long id, @GraphQLName("catalog") Catalog catalog)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.patchCatalog(id, catalog));
	}

	@GraphQLField(
		description = "Partially updates the commerce catalog identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes the catalog."
	)
	public Response patchCatalogByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("catalog") Catalog catalog)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource ->
				catalogResource.patchCatalogByExternalReferenceCode(
					externalReferenceCode, catalog));
	}

	@GraphQLField(
		description = "Creates a new commerce catalog, or updates the existing catalog when the external reference code already matches one. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns tolerated during update path (debug log), and 404 when the currency lookup fails. Side effects -- Provisions a new group, default catalog roles, and indexes the catalog."
	)
	public Catalog createCatalog(@GraphQLName("catalog") Catalog catalog)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.postCatalog(catalog));
	}

	@GraphQLField
	public Response createCatalogBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.postCatalogBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createCatalogsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource -> catalogResource.postCatalogsPageExportBatch(
				search, _filterBiFunction.apply(catalogResource, filterString),
				_sortsBiFunction.apply(catalogResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates or replaces the commerce catalog identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the currency lookup fails. Side effects -- Provisions a new group on add; reindex on update."
	)
	public Catalog updateCatalogByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("catalog") Catalog catalog)
		throws Exception {

		return _applyComponentServiceObjects(
			_catalogResourceComponentServiceObjects,
			this::_populateResourceContext,
			catalogResource ->
				catalogResource.putCatalogByExternalReferenceCode(
					externalReferenceCode, catalog));
	}

	@GraphQLField(
		description = "Replaces the asset category assignments of the product identified by external reference code. Returns 404 when the product external reference code is not found, and 404 when any supplied category ID is missing. Side effects -- Replaces the product's asset category assignment set (overwrites prior categorization)."
	)
	public Response patchProductByExternalReferenceCodeCategory(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("categories") Category[] categories)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryResource ->
				categoryResource.patchProductByExternalReferenceCodeCategory(
					externalReferenceCode, categories));
	}

	@GraphQLField(
		description = "Replaces the asset category assignments of the product identified by product ID. Returns 404 when the product ID is not found, and 404 when any supplied category ID is missing. Side effects -- Replaces the product's asset category assignment set."
	)
	public Response patchProductIdCategory(
			@GraphQLName("id") Long id,
			@GraphQLName("categories") Category[] categories)
		throws Exception {

		return _applyComponentServiceObjects(
			_categoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			categoryResource -> categoryResource.patchProductIdCategory(
				id, categories));
	}

	@GraphQLField(
		description = "Deletes the commerce currency identified by ID. Returns 404 when the ID is not found. Side effects -- Cascades through commerce currency delete listeners."
	)
	public boolean deleteCurrency(@GraphQLName("id") Long id) throws Exception {
		_applyVoidComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> currencyResource.deleteCurrency(id));

		return true;
	}

	@GraphQLField
	public Response deleteCurrencyBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> currencyResource.deleteCurrencyBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the commerce currency identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades through currency delete listeners."
	)
	public boolean deleteCurrencyByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource ->
				currencyResource.deleteCurrencyByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the commerce currency identified by ID. Returns 404 when the ID is not found. Side effects -- Reindexes the currency."
	)
	public Currency patchCurrency(
			@GraphQLName("id") Long id,
			@GraphQLName("currency") Currency currency)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> currencyResource.patchCurrency(id, currency));
	}

	@GraphQLField(
		description = "Partially updates the commerce currency identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes the currency."
	)
	public Currency patchCurrencyByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("currency") Currency currency)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource ->
				currencyResource.patchCurrencyByExternalReferenceCode(
					externalReferenceCode, currency));
	}

	@GraphQLField
	public Response createCurrenciesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> currencyResource.postCurrenciesPageExportBatch(
				search, _filterBiFunction.apply(currencyResource, filterString),
				_sortsBiFunction.apply(currencyResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new commerce currency. Returns 409 when the record is not found. Side effects -- Indexes the new currency; localized format pattern populated from default if missing."
	)
	public Currency createCurrency(@GraphQLName("currency") Currency currency)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> currencyResource.postCurrency(currency));
	}

	@GraphQLField
	public Response createCurrencyBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_currencyResourceComponentServiceObjects,
			this::_populateResourceContext,
			currencyResource -> currencyResource.postCurrencyBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the diagram setting identified by diagramId. Returns 404 when `diagramId` is not found. Side effects -- Updates the diagram's image attachment and may replace the linked DL file entry."
	)
	public Diagram patchDiagram(
			@GraphQLName("diagramId") Long diagramId,
			@GraphQLName("diagram") Diagram diagram)
		throws Exception {

		return _applyComponentServiceObjects(
			_diagramResourceComponentServiceObjects,
			this::_populateResourceContext,
			diagramResource -> diagramResource.patchDiagram(
				diagramId, diagram));
	}

	@GraphQLField(
		description = "Creates a diagram setting under the product identified by external reference code. Returns 404 when the product external reference code is not found. Side effects -- Creates the diagram setting and links an attachment as the diagram image."
	)
	public Diagram createProductByExternalReferenceCodeDiagram(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("diagram") Diagram diagram)
		throws Exception {

		return _applyComponentServiceObjects(
			_diagramResourceComponentServiceObjects,
			this::_populateResourceContext,
			diagramResource ->
				diagramResource.postProductByExternalReferenceCodeDiagram(
					externalReferenceCode, diagram));
	}

	@GraphQLField(
		description = "Creates a diagram setting under the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Creates the diagram setting and links an attachment as the diagram image."
	)
	public Diagram createProductIdDiagram(
			@GraphQLName("id") Long id, @GraphQLName("diagram") Diagram diagram)
		throws Exception {

		return _applyComponentServiceObjects(
			_diagramResourceComponentServiceObjects,
			this::_populateResourceContext,
			diagramResource -> diagramResource.postProductIdDiagram(
				id, diagram));
	}

	@GraphQLField
	public Response createProductIdDiagramBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_diagramResourceComponentServiceObjects,
			this::_populateResourceContext,
			diagramResource -> diagramResource.postProductIdDiagramBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Removes the grouped product entry identified by groupedProductId. Returns 404 when the record is not found. Side effects -- Removes the grouped product link from the parent product definition."
	)
	public boolean deleteGroupedProduct(
			@GraphQLName("groupedProductId") Long groupedProductId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource ->
				groupedProductResource.deleteGroupedProduct(groupedProductId));

		return true;
	}

	@GraphQLField
	public Response deleteGroupedProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource ->
				groupedProductResource.deleteGroupedProductBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates priority and quantity of the grouped product entry identified by groupedProductId. Returns 404 when the ID is not found. Side effects -- None (updates priority and quantity)."
	)
	public GroupedProduct patchGroupedProduct(
			@GraphQLName("groupedProductId") Long groupedProductId,
			@GraphQLName("groupedProduct") GroupedProduct groupedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource ->
				groupedProductResource.patchGroupedProduct(
					groupedProductId, groupedProduct));
	}

	@GraphQLField(
		description = "Adds a grouped product entry to the parent product identified by external reference code. Returns 404 when the either the parent product ERC or the entry product is missing. Side effects -- Links the entry product to the parent grouped product."
	)
	public GroupedProduct createProductByExternalReferenceCodeGroupedProduct(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("groupedProduct") GroupedProduct groupedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource ->
				groupedProductResource.
					postProductByExternalReferenceCodeGroupedProduct(
						externalReferenceCode, groupedProduct));
	}

	@GraphQLField(
		description = "Adds a grouped product entry to the parent product identified by product ID. Returns 404 when the parent product ID or entry product is missing. Side effects -- Links the entry product to the parent grouped product."
	)
	public GroupedProduct createProductIdGroupedProduct(
			@GraphQLName("id") Long id,
			@GraphQLName("groupedProduct") GroupedProduct groupedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource ->
				groupedProductResource.postProductIdGroupedProduct(
					id, groupedProduct));
	}

	@GraphQLField
	public Response createProductIdGroupedProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_groupedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			groupedProductResource ->
				groupedProductResource.postProductIdGroupedProductBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Detaches a list type definition from a specification option. Returns 404 when `specificationId` is not found. Side effects -- Detaches the list type definition from the specification option."
	)
	public boolean deleteSpecificationListTypeDefinition(
			@GraphQLName("specificationId") Long specificationId,
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.
					deleteSpecificationListTypeDefinition(
						specificationId, listTypeDefinitionId));

		return true;
	}

	@GraphQLField(
		description = "Creates a list type definition and attaches it to the specification option identified by ID. Returns 404 when the specification ID is not found. Side effects -- Creates a new list type definition and attaches it to the specification option."
	)
	public ListTypeDefinition createSpecificationIdListTypeDefinition(
			@GraphQLName("id") Long id,
			@GraphQLName("listTypeDefinition") ListTypeDefinition
				listTypeDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.
					postSpecificationIdListTypeDefinition(
						id, listTypeDefinition));
	}

	@GraphQLField
	public Response createSpecificationIdListTypeDefinitionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.
					postSpecificationIdListTypeDefinitionBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Attaches an existing list type definition to the specification option. Returns 404 when `specificationId` is not found. Side effects -- Creates the relation row between the specification option and the list type definition."
	)
	public boolean createSpecificationListTypeDefinition(
			@GraphQLName("specificationId") Long specificationId,
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.postSpecificationListTypeDefinition(
					specificationId, listTypeDefinitionId));

		return true;
	}

	@GraphQLField
	public Response createLowStockActionsPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_lowStockActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			lowStockActionResource ->
				lowStockActionResource.postLowStockActionsPageExportBatch(
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Deletes the mapped (diagram) product identified by mappedProductId. Returns 404 when the ID is not found. Side effects -- Removes the diagram entry; orphaned pins may remain unless cleaned separately."
	)
	public boolean deleteMappedProduct(
			@GraphQLName("mappedProductId") Long mappedProductId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource -> mappedProductResource.deleteMappedProduct(
				mappedProductId));

		return true;
	}

	@GraphQLField
	public Response deleteMappedProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource ->
				mappedProductResource.deleteMappedProductBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the mapped (diagram) product identified by mappedProductId. Returns 404 when the ID is not found. Side effects -- May relink the diagram entry to a different SKU/product."
	)
	public MappedProduct patchMappedProduct(
			@GraphQLName("mappedProductId") Long mappedProductId,
			@GraphQLName("mappedProduct") MappedProduct mappedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource -> mappedProductResource.patchMappedProduct(
				mappedProductId, mappedProduct));
	}

	@GraphQLField(
		description = "Creates a mapped (diagram) product under the parent product identified by external reference code. Returns 404 when the product external reference code is not found. Side effects -- Creates a diagram entry referencing the supplied SKU/product."
	)
	public MappedProduct createProductByExternalReferenceCodeMappedProduct(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("mappedProduct") MappedProduct mappedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource ->
				mappedProductResource.
					postProductByExternalReferenceCodeMappedProduct(
						externalReferenceCode, mappedProduct));
	}

	@GraphQLField(
		description = "Creates a mapped (diagram) product under the parent product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Creates a diagram entry referencing the supplied SKU/product."
	)
	public MappedProduct createProductIdMappedProduct(
			@GraphQLName("id") Long id,
			@GraphQLName("mappedProduct") MappedProduct mappedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource ->
				mappedProductResource.postProductIdMappedProduct(
					id, mappedProduct));
	}

	@GraphQLField
	public Response createProductIdMappedProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_mappedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			mappedProductResource ->
				mappedProductResource.postProductIdMappedProductBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the option identified by ID. Returns 404 when the ID is not found. Side effects -- Cascades through product option listeners; reindexes affected products."
	)
	public Response deleteOption(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.deleteOption(id));
	}

	@GraphQLField
	public Response deleteOptionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.deleteOptionBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the option identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades through product option listeners."
	)
	public Response deleteOptionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource ->
				optionResource.deleteOptionByExternalReferenceCode(
					externalReferenceCode));
	}

	@GraphQLField(
		description = "Partially updates the option identified by ID. Returns 404 when the ID is not found. Side effects -- Reindexes the option."
	)
	public Response patchOption(
			@GraphQLName("id") Long id, @GraphQLName("option") Option option)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.patchOption(id, option));
	}

	@GraphQLField(
		description = "Partially updates the option identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes the option."
	)
	public Response patchOptionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("option") Option option)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.patchOptionByExternalReferenceCode(
				externalReferenceCode, option));
	}

	@GraphQLField(
		description = "Creates or updates an option (with optional nested option values) using the supplied external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 400 when the key is invalid, and 409 when the eRC collides. Side effects -- Creates nested option values via option value endpoint; reindexes the option."
	)
	public Option createOption(@GraphQLName("option") Option option)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.postOption(option));
	}

	@GraphQLField
	public Response createOptionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.postOptionBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createOptionsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.postOptionsPageExportBatch(
				search, _filterBiFunction.apply(optionResource, filterString),
				_sortsBiFunction.apply(optionResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates or replaces the option identified by external reference code, including optional nested option values. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 400 when the record is not found. Side effects -- Creates nested option values; reindexes the option."
	)
	public Option updateOptionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("option") Option option)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionResource -> optionResource.putOptionByExternalReferenceCode(
				externalReferenceCode, option));
	}

	@GraphQLField(
		description = "Deletes the option category identified by ID. Returns 404 when the record is not found. Side effects -- Cascades through option category delete listeners; reindexes affected specifications."
	)
	public Response deleteOptionCategory(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.deleteOptionCategory(id));
	}

	@GraphQLField
	public Response deleteOptionCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.deleteOptionCategoryBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the option category identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades through option category delete listeners."
	)
	public boolean deleteOptionCategoryByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.
					deleteOptionCategoryByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the option category identified by ID. Returns 404 when the ID is not found. Side effects -- Reindexes the option category."
	)
	public Response patchOptionCategory(
			@GraphQLName("id") Long id,
			@GraphQLName("optionCategory") OptionCategory optionCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.patchOptionCategory(id, optionCategory));
	}

	@GraphQLField(
		description = "Partially updates the option category identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes the option category."
	)
	public OptionCategory patchOptionCategoryByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("optionCategory") OptionCategory optionCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.
					patchOptionCategoryByExternalReferenceCode(
						externalReferenceCode, optionCategory));
	}

	@GraphQLField
	public Response createOptionCategoriesPageExportBatch(
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.postOptionCategoriesPageExportBatch(
					_filterBiFunction.apply(
						optionCategoryResource, filterString),
					_sortsBiFunction.apply(optionCategoryResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates or updates an option category using the supplied external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 409 when the key is already taken. Side effects -- Reindexes the option category."
	)
	public OptionCategory createOptionCategory(
			@GraphQLName("optionCategory") OptionCategory optionCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource -> optionCategoryResource.postOptionCategory(
				optionCategory));
	}

	@GraphQLField
	public Response createOptionCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.postOptionCategoryBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Creates or replaces the option category identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 409 when the key is already taken. Side effects -- Reindexes the option category."
	)
	public OptionCategory updateOptionCategoryByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("optionCategory") OptionCategory optionCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionCategoryResource ->
				optionCategoryResource.putOptionCategoryByExternalReferenceCode(
					externalReferenceCode, optionCategory));
	}

	@GraphQLField(
		description = "Deletes the option value identified by ID. Returns 404 when the record is not found. Side effects -- Cascades through product option value listeners."
	)
	public Response deleteOptionValue(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource -> optionValueResource.deleteOptionValue(id));
	}

	@GraphQLField
	public Response deleteOptionValueBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource -> optionValueResource.deleteOptionValueBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the option value identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades through product option value listeners."
	)
	public Response deleteOptionValueByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource ->
				optionValueResource.deleteOptionValueByExternalReferenceCode(
					externalReferenceCode));
	}

	@GraphQLField(
		description = "Partially updates the option value identified by ID. Returns 404 when the ID is not found. Side effects -- Reindexes the option value."
	)
	public Response patchOptionValue(
			@GraphQLName("id") Long id,
			@GraphQLName("optionValue") OptionValue optionValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource -> optionValueResource.patchOptionValue(
				id, optionValue));
	}

	@GraphQLField(
		description = "Partially updates the option value identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes the option value."
	)
	public Response patchOptionValueByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("optionValue") OptionValue optionValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource ->
				optionValueResource.patchOptionValueByExternalReferenceCode(
					externalReferenceCode, optionValue));
	}

	@GraphQLField(
		description = "Creates or updates an option value under the parent option identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the parent option external reference code is not found. Side effects -- Reindexes the option value."
	)
	public OptionValue createOptionByExternalReferenceCodeOptionValue(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("optionValue") OptionValue optionValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource ->
				optionValueResource.
					postOptionByExternalReferenceCodeOptionValue(
						externalReferenceCode, optionValue));
	}

	@GraphQLField(
		description = "Creates or updates an option value under the parent option identified by option ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the parent option ID is not found. Side effects -- Reindexes the option value."
	)
	public OptionValue createOptionIdOptionValue(
			@GraphQLName("id") Long id,
			@GraphQLName("optionValue") OptionValue optionValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource -> optionValueResource.postOptionIdOptionValue(
				id, optionValue));
	}

	@GraphQLField
	public Response createOptionIdOptionValueBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_optionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			optionValueResource ->
				optionValueResource.postOptionIdOptionValueBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the pin identified by pinId and removes its orphan diagram entry when no other pin references it. Returns 404 when the ID is not found. Side effects -- Cascades the matching diagram entry delete when no other pin shares the sequence."
	)
	public boolean deletePin(@GraphQLName("pinId") Long pinId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> pinResource.deletePin(pinId));

		return true;
	}

	@GraphQLField
	public Response deletePinBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> pinResource.deletePinBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the pin identified by pinId, optionally creating or updating its mapped product. Returns 404 when the ID is not found. Side effects -- When mappedProduct is included, may add or update the linked diagram entry (SKU/product mapping)."
	)
	public Pin patchPin(
			@GraphQLName("pinId") Long pinId, @GraphQLName("pin") Pin pin)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> pinResource.patchPin(pinId, pin));
	}

	@GraphQLField(
		description = "Creates a diagram pin on the product identified by external reference code. Returns 404 when the product external reference code is not found. Side effects -- Creates the pin and optionally creates/updates a mapped diagram entry."
	)
	public Pin createProductByExternalReferenceCodePin(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("pin") Pin pin)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> pinResource.postProductByExternalReferenceCodePin(
				externalReferenceCode, pin));
	}

	@GraphQLField(
		description = "Creates a diagram pin on the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Creates the pin and optionally creates/updates a mapped diagram entry."
	)
	public Pin createProductIdPin(
			@GraphQLName("id") Long id, @GraphQLName("pin") Pin pin)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> pinResource.postProductIdPin(id, pin));
	}

	@GraphQLField
	public Response createProductIdPinBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_pinResourceComponentServiceObjects, this::_populateResourceContext,
			pinResource -> pinResource.postProductIdPinBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Cascades product definition delete (skus, options, attachments, categorization)."
	)
	public boolean deleteProduct(@GraphQLName("id") Long id) throws Exception {
		_applyVoidComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.deleteProduct(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.deleteProductBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades product definition delete."
	)
	public boolean deleteProductByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.deleteProductByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Deletes a specific historical version of the product identified by external reference code. Returns 404 when the external reference code or version is not found. Side effects -- Cascades product definition delete for the targeted version only."
	)
	public boolean deleteProductByExternalReferenceCodeByVersion(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("version") Integer version)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.deleteProductByExternalReferenceCodeByVersion(
					externalReferenceCode, version));

		return true;
	}

	@GraphQLField(
		description = "Deletes a specific historical version of the product identified by product ID. Returns 404 when the product ID or version is not found. Side effects -- Cascades product definition delete for the targeted version only."
	)
	public boolean deleteProductByVersion(
			@GraphQLName("id") Long id, @GraphQLName("version") Integer version)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.deleteProductByVersion(
				id, version));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Updates custom field storage, asset categorization, asset tags, and may trigger workflow draft transition."
	)
	public Product patchProduct(
			@GraphQLName("id") Long id, @GraphQLName("product") Product product)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.patchProduct(id, product));
	}

	@GraphQLField(
		description = "Partially updates the product identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Updates custom field storage, asset categorization, asset tags; may set workflowAction=SAVE_DRAFT when productStatus=DRAFT."
	)
	public Product patchProductByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("product") Product product)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.patchProductByExternalReferenceCode(
					externalReferenceCode, product));
	}

	@GraphQLField(
		description = "Creates or updates a product using the supplied external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the supplied catalog cannot be resolved, and workflow exceptions propagate. Side effects -- Creates the product version chain, asset entry, default SKU when productType has one; triggers workflow on add."
	)
	public Product createProduct(@GraphQLName("product") Product product)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.postProduct(product));
	}

	@GraphQLField
	public Response createProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.postProductBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Clones the product identified by external reference code, optionally into a different catalog. Returns 404 when the source product external reference code is not found, and 404 when the target catalog external reference code is not found. Side effects -- Creates a brand new product definition with cloned SKUs, options, attachments, etc.."
	)
	public Product createProductByExternalReferenceCodeClone(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("catalogExternalReferenceCode") String
				catalogExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.postProductByExternalReferenceCodeClone(
					externalReferenceCode, catalogExternalReferenceCode));
	}

	@GraphQLField(
		description = "Clones the product identified by product ID, optionally into a different catalog. Returns 404 when the source product ID is not found. Side effects -- Creates a brand new product definition with cloned SKUs, options, attachments, etc.."
	)
	public Product createProductClone(
			@GraphQLName("id") Long id,
			@GraphQLName("catalogId") Long catalogId)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.postProductClone(id, catalogId));
	}

	@GraphQLField
	public Response createProductsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource -> productResource.postProductsPageExportBatch(
				search, _filterBiFunction.apply(productResource, filterString),
				_sortsBiFunction.apply(productResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates or replaces the product identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the supplied catalog cannot be resolved. Side effects -- Creates or updates the full product version chain."
	)
	public Product updateProductByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("product") Product product)
		throws Exception {

		return _applyComponentServiceObjects(
			_productResourceComponentServiceObjects,
			this::_populateResourceContext,
			productResource ->
				productResource.putProductByExternalReferenceCode(
					externalReferenceCode, product));
	}

	@GraphQLField(
		description = "Removes the account group assignment identified by ID from its product. Returns 404 when the record is not found. Side effects -- Removes the account group <-> product association."
	)
	public boolean deleteProductAccountGroup(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productAccountGroupResource ->
				productAccountGroupResource.deleteProductAccountGroup(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productAccountGroupResource ->
				productAccountGroupResource.deleteProductAccountGroupBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Removes the channel assignment identified by ID from its product. Returns 404 when the record is not found. Side effects -- Removes the channel <-> product association."
	)
	public boolean deleteProductChannel(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productChannelResource ->
				productChannelResource.deleteProductChannel(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productChannelResource ->
				productChannelResource.deleteProductChannelBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product configuration entry identified by ID. Returns 404 when the record is not found. Side effects -- Removes the configuration override (master entries are protected by model permission check)."
	)
	public boolean deleteProductConfiguration(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.deleteProductConfiguration(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductConfigurationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.deleteProductConfigurationBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product configuration entry identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Removes the configuration override."
	)
	public boolean deleteProductConfigurationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					deleteProductConfigurationByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the consolidated product configuration of the product identified by external reference code. Resolves the addressed record by external reference code, then applies the same behavior as the by-ID variant. Returns 404 when the product external reference code is not found. Side effects -- Updates the master configuration entry plus product inventory configuration and per-product availability estimate."
	)
	public Response patchProductByExternalReferenceCodeConfiguration(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("productConfiguration") ProductConfiguration
				productConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					patchProductByExternalReferenceCodeConfiguration(
						externalReferenceCode, productConfiguration));
	}

	@GraphQLField(
		description = "Partially updates the product configuration entry identified by ID. Returns 404 when the ID is not found. Side effects -- Updates inventory, shipping, and tax fields on the entry."
	)
	public ProductConfiguration patchProductConfiguration(
			@GraphQLName("id") Long id,
			@GraphQLName("productConfiguration") ProductConfiguration
				productConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.patchProductConfiguration(
					id, productConfiguration));
	}

	@GraphQLField(
		description = "Partially updates the product configuration entry identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Updates inventory, shipping, and tax fields on the entry."
	)
	public ProductConfiguration
			patchProductConfigurationByExternalReferenceCode(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productConfiguration") ProductConfiguration
					productConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					patchProductConfigurationByExternalReferenceCode(
						externalReferenceCode, productConfiguration));
	}

	@GraphQLField(
		description = "Partially updates the consolidated product configuration of the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Updates the master configuration entry plus product inventory configuration and per-product availability estimate."
	)
	public Response patchProductIdConfiguration(
			@GraphQLName("id") Long id,
			@GraphQLName("productConfiguration") ProductConfiguration
				productConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.patchProductIdConfiguration(
					id, productConfiguration));
	}

	@GraphQLField(
		description = "Creates a product configuration entry under the product configuration list identified by external reference code. Resolves the addressed record by external reference code, then applies the same behavior as the by-ID variant. Returns 404 when the external reference code is not found, and 404 when entityType=product and the referenced product is missing. Side effects -- Creates a configuration entry under the configuration list, scoped to a product or to the list as a template."
	)
	public ProductConfiguration
			createProductConfigurationListByExternalReferenceCodeProductConfiguration(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productConfiguration") ProductConfiguration
					productConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					postProductConfigurationListByExternalReferenceCodeProductConfiguration(
						externalReferenceCode, productConfiguration));
	}

	@GraphQLField(
		description = "Creates a product configuration entry under the product configuration list identified by ID. Returns 404 when the ID is not found, and 404 when entityType=product and the referenced product is missing. Side effects -- Creates a configuration entry under the configuration list."
	)
	public ProductConfiguration
			createProductConfigurationListIdProductConfiguration(
				@GraphQLName("id") Long id,
				@GraphQLName("productConfiguration") ProductConfiguration
					productConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					postProductConfigurationListIdProductConfiguration(
						id, productConfiguration));
	}

	@GraphQLField
	public Response createProductConfigurationListIdProductConfigurationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationResource ->
				productConfigurationResource.
					postProductConfigurationListIdProductConfigurationBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product configuration list identified by ID. Returns 404 when the ID is not found. Side effects -- Cascades deletion of configuration list assignment rows and configuration entry rows attached to the list."
	)
	public boolean deleteProductConfigurationList(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.deleteProductConfigurationList(
					id));

		return true;
	}

	@GraphQLField
	public Response deleteProductConfigurationListBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.
					deleteProductConfigurationListBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product configuration list identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades deletion of rels and entries."
	)
	public boolean deleteProductConfigurationListByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.
					deleteProductConfigurationListByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the product configuration list identified by ID, applying any nested product configurations. Returns 404 when the ID is not found. Side effects -- Reindexes; updates display/expiration date and custom field storage; cascades into nested product configurations."
	)
	public ProductConfigurationList patchProductConfigurationList(
			@GraphQLName("id") Long id,
			@GraphQLName("productConfigurationList") ProductConfigurationList
				productConfigurationList)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.patchProductConfigurationList(
					id, productConfigurationList));
	}

	@GraphQLField(
		description = "Partially updates the product configuration list identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes; updates display/expiration date and custom field storage; cascades into nested product configurations."
	)
	public ProductConfigurationList
			patchProductConfigurationListByExternalReferenceCode(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productConfigurationList")
					ProductConfigurationList productConfigurationList)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.
					patchProductConfigurationListByExternalReferenceCode(
						externalReferenceCode, productConfigurationList));
	}

	@GraphQLField(
		description = "Creates or updates a product configuration list under the supplied catalog, optionally with nested product configurations. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when no catalog can be resolved. Side effects -- Reindexes; creates nested product configurations when supplied."
	)
	public ProductConfigurationList createProductConfigurationList(
			@GraphQLName("productConfigurationList") ProductConfigurationList
				productConfigurationList)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.postProductConfigurationList(
					productConfigurationList));
	}

	@GraphQLField
	public Response createProductConfigurationListBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.
					postProductConfigurationListBatch(callbackURL, object));
	}

	@GraphQLField
	public Response createProductConfigurationListsPageExportBatch(
			@GraphQLName("catalogId") Long catalogId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListResource ->
				productConfigurationListResource.
					postProductConfigurationListsPageExportBatch(
						catalogId, search,
						_filterBiFunction.apply(
							productConfigurationListResource, filterString),
						_sortsBiFunction.apply(
							productConfigurationListResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Removes the account binding identified by ID from its product configuration list. Returns 404 when the record is not found. Side effects -- Removes the account <-> configuration list association."
	)
	public boolean deleteProductConfigurationListAccount(
			@GraphQLName("productConfigurationListAccountId") Long
				productConfigurationListAccountId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountResource ->
				productConfigurationListAccountResource.
					deleteProductConfigurationListAccount(
						productConfigurationListAccountId));

		return true;
	}

	@GraphQLField
	public Response deleteProductConfigurationListAccountBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountResource ->
				productConfigurationListAccountResource.
					deleteProductConfigurationListAccountBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Binds an account to the product configuration list identified by external reference code. Resolves the addressed record by external reference code, then applies the same behavior as the by-ID variant. Returns 404 when the external reference code is not found, and 404 when the account lookup fails. Side effects -- Creates a configuration list assignment binding the account to the configuration list."
	)
	public ProductConfigurationListAccount
			createProductConfigurationListByExternalReferenceCodeProductConfigurationListAccount(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productConfigurationListAccount")
					ProductConfigurationListAccount
						productConfigurationListAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountResource ->
				productConfigurationListAccountResource.
					postProductConfigurationListByExternalReferenceCodeProductConfigurationListAccount(
						externalReferenceCode,
						productConfigurationListAccount));
	}

	@GraphQLField(
		description = "Binds an account to the product configuration list identified by ID. Returns 404 when the ID is not found, and 404 when the account lookup fails. Side effects -- Creates a configuration list assignment binding the account to the configuration list."
	)
	public ProductConfigurationListAccount
			createProductConfigurationListIdProductConfigurationListAccount(
				@GraphQLName("id") Long id,
				@GraphQLName("productConfigurationListAccount")
					ProductConfigurationListAccount
						productConfigurationListAccount)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountResource ->
				productConfigurationListAccountResource.
					postProductConfigurationListIdProductConfigurationListAccount(
						id, productConfigurationListAccount));
	}

	@GraphQLField
	public Response
			createProductConfigurationListIdProductConfigurationListAccountBatch(
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountResource ->
				productConfigurationListAccountResource.
					postProductConfigurationListIdProductConfigurationListAccountBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Removes the account group binding identified by ID from its product configuration list. Returns 404 when the record is not found. Side effects -- Removes the account group <-> configuration list association."
	)
	public boolean deleteProductConfigurationListAccountGroup(
			@GraphQLName("productConfigurationListAccountGroupId") Long
				productConfigurationListAccountGroupId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountGroupResource ->
				productConfigurationListAccountGroupResource.
					deleteProductConfigurationListAccountGroup(
						productConfigurationListAccountGroupId));

		return true;
	}

	@GraphQLField
	public Response deleteProductConfigurationListAccountGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountGroupResource ->
				productConfigurationListAccountGroupResource.
					deleteProductConfigurationListAccountGroupBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Binds an account group to the product configuration list identified by external reference code. Resolves the addressed record by external reference code, then applies the same behavior as the by-ID variant. Returns 404 when the external reference code is not found, and 404 when the account group lookup fails. Side effects -- Creates a configuration list assignment binding the account group to the configuration list."
	)
	public ProductConfigurationListAccountGroup
			createProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroup(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productConfigurationListAccountGroup")
					ProductConfigurationListAccountGroup
						productConfigurationListAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountGroupResource ->
				productConfigurationListAccountGroupResource.
					postProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountGroup(
						externalReferenceCode,
						productConfigurationListAccountGroup));
	}

	@GraphQLField(
		description = "Binds an account group to the product configuration list identified by ID. Returns 404 when the ID is not found, and 404 when the account group lookup fails. Side effects -- Creates a configuration list assignment binding the account group to the configuration list."
	)
	public ProductConfigurationListAccountGroup
			createProductConfigurationListIdProductConfigurationListAccountGroup(
				@GraphQLName("id") Long id,
				@GraphQLName("productConfigurationListAccountGroup")
					ProductConfigurationListAccountGroup
						productConfigurationListAccountGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountGroupResource ->
				productConfigurationListAccountGroupResource.
					postProductConfigurationListIdProductConfigurationListAccountGroup(
						id, productConfigurationListAccountGroup));
	}

	@GraphQLField
	public Response
			createProductConfigurationListIdProductConfigurationListAccountGroupBatch(
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListAccountGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListAccountGroupResource ->
				productConfigurationListAccountGroupResource.
					postProductConfigurationListIdProductConfigurationListAccountGroupBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Removes the channel binding identified by ID from its product configuration list. Returns 404 when the record is not found. Side effects -- Removes the channel <-> configuration list association."
	)
	public boolean deleteProductConfigurationListChannel(
			@GraphQLName("productConfigurationListChannelId") Long
				productConfigurationListChannelId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListChannelResource ->
				productConfigurationListChannelResource.
					deleteProductConfigurationListChannel(
						productConfigurationListChannelId));

		return true;
	}

	@GraphQLField
	public Response deleteProductConfigurationListChannelBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListChannelResource ->
				productConfigurationListChannelResource.
					deleteProductConfigurationListChannelBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Binds a commerce channel to the product configuration list identified by external reference code. Resolves the addressed record by external reference code, then applies the same behavior as the by-ID variant. Returns 404 when the external reference code is not found, and 404 when the channel lookup fails. Side effects -- Creates a channel assignment binding the channel to the configuration list."
	)
	public ProductConfigurationListChannel
			createProductConfigurationListByExternalReferenceCodeProductConfigurationListChannel(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productConfigurationListChannel")
					ProductConfigurationListChannel
						productConfigurationListChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListChannelResource ->
				productConfigurationListChannelResource.
					postProductConfigurationListByExternalReferenceCodeProductConfigurationListChannel(
						externalReferenceCode,
						productConfigurationListChannel));
	}

	@GraphQLField(
		description = "Binds a commerce channel to the product configuration list identified by ID. Returns 404 when the ID is not found, and 404 when the channel lookup fails. Side effects -- Creates a channel assignment binding the channel to the configuration list."
	)
	public ProductConfigurationListChannel
			createProductConfigurationListIdProductConfigurationListChannel(
				@GraphQLName("id") Long id,
				@GraphQLName("productConfigurationListChannel")
					ProductConfigurationListChannel
						productConfigurationListChannel)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListChannelResource ->
				productConfigurationListChannelResource.
					postProductConfigurationListIdProductConfigurationListChannel(
						id, productConfigurationListChannel));
	}

	@GraphQLField
	public Response
			createProductConfigurationListIdProductConfigurationListChannelBatch(
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListChannelResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListChannelResource ->
				productConfigurationListChannelResource.
					postProductConfigurationListIdProductConfigurationListChannelBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Removes the order type binding identified by ID from its product configuration list. Returns 404 when the record is not found. Side effects -- Removes the order type <-> configuration list association."
	)
	public boolean deleteProductConfigurationListOrderType(
			@GraphQLName("productConfigurationListOrderTypeId") Long
				productConfigurationListOrderTypeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productConfigurationListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListOrderTypeResource ->
				productConfigurationListOrderTypeResource.
					deleteProductConfigurationListOrderType(
						productConfigurationListOrderTypeId));

		return true;
	}

	@GraphQLField
	public Response deleteProductConfigurationListOrderTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListOrderTypeResource ->
				productConfigurationListOrderTypeResource.
					deleteProductConfigurationListOrderTypeBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Binds a commerce order type to the product configuration list identified by external reference code. Resolves the addressed record by external reference code, then applies the same behavior as the by-ID variant. Returns 404 when the external reference code is not found, and 404 when the order type lookup fails. Side effects -- Creates a configuration list assignment binding the order type to the configuration list."
	)
	public ProductConfigurationListOrderType
			createProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderType(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productConfigurationListOrderType")
					ProductConfigurationListOrderType
						productConfigurationListOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListOrderTypeResource ->
				productConfigurationListOrderTypeResource.
					postProductConfigurationListByExternalReferenceCodeProductConfigurationListOrderType(
						externalReferenceCode,
						productConfigurationListOrderType));
	}

	@GraphQLField(
		description = "Binds a commerce order type to the product configuration list identified by ID. Returns 404 when the ID is not found, and 404 when the order type lookup fails. Side effects -- Creates a configuration list assignment binding the order type to the configuration list."
	)
	public ProductConfigurationListOrderType
			createProductConfigurationListIdProductConfigurationListOrderType(
				@GraphQLName("id") Long id,
				@GraphQLName("productConfigurationListOrderType")
					ProductConfigurationListOrderType
						productConfigurationListOrderType)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListOrderTypeResource ->
				productConfigurationListOrderTypeResource.
					postProductConfigurationListIdProductConfigurationListOrderType(
						id, productConfigurationListOrderType));
	}

	@GraphQLField
	public Response
			createProductConfigurationListIdProductConfigurationListOrderTypeBatch(
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productConfigurationListOrderTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			productConfigurationListOrderTypeResource ->
				productConfigurationListOrderTypeResource.
					postProductConfigurationListIdProductConfigurationListOrderTypeBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product group identified by ID. Returns 404 when the record is not found. Side effects -- Cascades deletion of product group assignment rows and associated pricing rules."
	)
	public boolean deleteProductGroup(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource -> productGroupResource.deleteProductGroup(
				id));

		return true;
	}

	@GraphQLField
	public Response deleteProductGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.deleteProductGroupBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the product group identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades deletion of rels and rules."
	)
	public boolean deleteProductGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.deleteProductGroupByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the product group identified by ID, optionally appending new product bindings. Returns 404 when the ID is not found, and 404 when nested products fail to resolve. Side effects -- Reindexes; updates custom field storage; appends new product bindings without removing existing ones."
	)
	public Response patchProductGroup(
			@GraphQLName("id") Long id,
			@GraphQLName("productGroup") ProductGroup productGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource -> productGroupResource.patchProductGroup(
				id, productGroup));
	}

	@GraphQLField(
		description = "Partially updates the product group identified by external reference code. Returns 404 when the external reference code is not found, and 404 when nested products fail to resolve. Side effects -- Reindexes; updates custom field storage; appends new product bindings."
	)
	public Response patchProductGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("productGroup") ProductGroup productGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.patchProductGroupByExternalReferenceCode(
					externalReferenceCode, productGroup));
	}

	@GraphQLField(
		description = "Creates or updates a product group, optionally including product bindings. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when nested products fail to resolve. Side effects -- Reindexes; creates the rel rows for any supplied products."
	)
	public ProductGroup createProductGroup(
			@GraphQLName("productGroup") ProductGroup productGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource -> productGroupResource.postProductGroup(
				productGroup));
	}

	@GraphQLField
	public Response createProductGroupBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource -> productGroupResource.postProductGroupBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createProductGroupsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.postProductGroupsPageExportBatch(
					search,
					_filterBiFunction.apply(productGroupResource, filterString),
					_sortsBiFunction.apply(productGroupResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates or replaces the product group identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when nested products fail to resolve. Side effects -- Reindexes; appends new product bindings."
	)
	public ProductGroup updateProductGroupByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("productGroup") ProductGroup productGroup)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupResource ->
				productGroupResource.putProductGroupByExternalReferenceCode(
					externalReferenceCode, productGroup));
	}

	@GraphQLField(
		description = "Removes the product binding identified by ID from its product group. Returns 404 when the record is not found. Side effects -- Removes the product <-> product group association."
	)
	public boolean deleteProductGroupProduct(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productGroupProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupProductResource ->
				productGroupProductResource.deleteProductGroupProduct(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductGroupProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupProductResource ->
				productGroupProductResource.deleteProductGroupProductBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Binds a product to the product group identified by external reference code. Returns 404 when the product group external reference code is not found, and 404 when the product lookup fails. Side effects -- Creates a product group assignment binding the product to the group."
	)
	public ProductGroupProduct
			createProductGroupByExternalReferenceCodeProductGroupProduct(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productGroupProduct") ProductGroupProduct
					productGroupProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupProductResource ->
				productGroupProductResource.
					postProductGroupByExternalReferenceCodeProductGroupProduct(
						externalReferenceCode, productGroupProduct));
	}

	@GraphQLField(
		description = "Binds a product to the product group identified by ID. Returns 404 when the product group ID is not found, and 404 when the product lookup fails. Side effects -- Creates a product group assignment binding the product to the group."
	)
	public ProductGroupProduct createProductGroupIdProductGroupProduct(
			@GraphQLName("id") Long id,
			@GraphQLName("productGroupProduct") ProductGroupProduct
				productGroupProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupProductResource ->
				productGroupProductResource.
					postProductGroupIdProductGroupProduct(
						id, productGroupProduct));
	}

	@GraphQLField
	public Response createProductGroupIdProductGroupProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productGroupProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			productGroupProductResource ->
				productGroupProductResource.
					postProductGroupIdProductGroupProductBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Removes the product option relation identified by ID from its product. Returns 404 when the ID is not found. Side effects -- Cascades deletion of associated product option value rows."
	)
	public Response deleteProductOption(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> productOptionResource.deleteProductOption(
				id));
	}

	@GraphQLField
	public Response deleteProductOptionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource ->
				productOptionResource.deleteProductOptionBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the product option relation identified by ID, including any nested product option values. Returns 404 when the ID is not found. Side effects -- Reindexes the parent product; cascades into nested product option values."
	)
	public Response patchProductOption(
			@GraphQLName("id") Long id,
			@GraphQLName("productOption") ProductOption productOption)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> productOptionResource.patchProductOption(
				id, productOption));
	}

	@GraphQLField(
		description = "Creates or updates a batch of product option relations on the product identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found. Side effects -- Adds or updates each product option and cascades into nested product option values."
	)
	public java.util.Collection<ProductOption>
			createProductByExternalReferenceCodeProductOptionsPage(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productOptions") ProductOption[] productOptions)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> {
				Page paginationPage =
					productOptionResource.
						postProductByExternalReferenceCodeProductOptionsPage(
							externalReferenceCode, productOptions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Creates or updates a batch of product option relations on the product identified by product ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found. Side effects -- Adds or updates each product option and cascades into nested product option values."
	)
	public java.util.Collection<ProductOption>
			createProductIdProductOptionsPage(
				@GraphQLName("id") Long id,
				@GraphQLName("productOptions") ProductOption[] productOptions)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionResource -> {
				Page paginationPage =
					productOptionResource.postProductIdProductOptionsPage(
						id, productOptions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Removes the product option value relation identified by ID. Returns 404 when the record is not found. Side effects -- Cascades through SKU option value listeners; reindexes affected SKUs."
	)
	public boolean deleteProductOptionValue(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource ->
				productOptionValueResource.deleteProductOptionValue(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductOptionValueBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource ->
				productOptionValueResource.deleteProductOptionValueBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the product option value relation identified by ID. Returns 404 when the ID is not found. Side effects -- May link the value to a specific SKU (SKU); reindexes affected SKUs."
	)
	public ProductOptionValue patchProductOptionValue(
			@GraphQLName("id") Long id,
			@GraphQLName("productOptionValue") ProductOptionValue
				productOptionValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource ->
				productOptionValueResource.patchProductOptionValue(
					id, productOptionValue));
	}

	@GraphQLField(
		description = "Creates or updates a product option value relation under the product option identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Falls back to the value key within the parent product option when the external reference code is omitted. Returns 404 when the product option external reference code is not found. Side effects -- Links the value to a SKU when one is supplied; reindexes the affected SKUs."
	)
	public ProductOptionValue
			createProductOptionByExternalReferenceCodeProductOptionValue(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productOptionValue") ProductOptionValue
					productOptionValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource ->
				productOptionValueResource.
					postProductOptionByExternalReferenceCodeProductOptionValue(
						externalReferenceCode, productOptionValue));
	}

	@GraphQLField(
		description = "Creates or updates a product option value relation under the product option identified by ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the parent option relation ID is not found. Side effects -- May link the value to a specific SKU (SKU); reindexes affected SKUs."
	)
	public ProductOptionValue createProductOptionIdProductOptionValue(
			@GraphQLName("id") Long id,
			@GraphQLName("productOptionValue") ProductOptionValue
				productOptionValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource ->
				productOptionValueResource.
					postProductOptionIdProductOptionValue(
						id, productOptionValue));
	}

	@GraphQLField
	public Response createProductOptionIdProductOptionValueBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productOptionValueResourceComponentServiceObjects,
			this::_populateResourceContext,
			productOptionValueResource ->
				productOptionValueResource.
					postProductOptionIdProductOptionValueBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the shipping configuration of the product identified by external reference code. Returns 404 when the product external reference code is not found. Side effects -- Updates the product definition shipping fields (weight, width, height, depth, etc.)."
	)
	public Response patchProductByExternalReferenceCodeShippingConfiguration(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("productShippingConfiguration")
				ProductShippingConfiguration productShippingConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productShippingConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productShippingConfigurationResource ->
				productShippingConfigurationResource.
					patchProductByExternalReferenceCodeShippingConfiguration(
						externalReferenceCode, productShippingConfiguration));
	}

	@GraphQLField(
		description = "Partially updates the shipping configuration of the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Updates the product definition shipping fields."
	)
	public Response patchProductIdShippingConfiguration(
			@GraphQLName("id") Long id,
			@GraphQLName("productShippingConfiguration")
				ProductShippingConfiguration productShippingConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productShippingConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productShippingConfigurationResource ->
				productShippingConfigurationResource.
					patchProductIdShippingConfiguration(
						id, productShippingConfiguration));
	}

	@GraphQLField(
		description = "Removes the product specification value identified by ID from its product. Returns 404 when the ID is not found. Side effects -- Reindexes the parent product."
	)
	public boolean deleteProductSpecification(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.deleteProductSpecification(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductSpecificationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.deleteProductSpecificationBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Removes the product specification value identified by external reference code from its product. Returns 404 when the external reference code is not found. Side effects -- Reindexes the parent product."
	)
	public boolean deleteProductSpecificationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.
					deleteProductSpecificationByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the product specification value identified by ID. Returns 404 when the ID is not found, and 404 when nested specification cannot be resolved. Side effects -- Reindexes the parent product."
	)
	public ProductSpecification patchProductSpecification(
			@GraphQLName("id") Long id,
			@GraphQLName("productSpecification") ProductSpecification
				productSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.patchProductSpecification(
					id, productSpecification));
	}

	@GraphQLField(
		description = "Partially updates the product specification value identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes the parent product."
	)
	public ProductSpecification
			patchProductSpecificationByExternalReferenceCode(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productSpecification") ProductSpecification
					productSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.
					patchProductSpecificationByExternalReferenceCode(
						externalReferenceCode, productSpecification));
	}

	@GraphQLField(
		description = "Creates or updates a specification value on the product identified by external reference code. Resolves the addressed record by external reference code, then applies the same behavior as the by-ID variant. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found, and 404 when the specification cannot be resolved. Side effects -- Adds or updates the specification value; reindexes the parent product."
	)
	public ProductSpecification
			createProductByExternalReferenceCodeProductSpecification(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productSpecification") ProductSpecification
					productSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.
					postProductByExternalReferenceCodeProductSpecification(
						externalReferenceCode, productSpecification));
	}

	@GraphQLField(
		description = "Creates or updates a specification value on the product identified by product ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found, and 404 when the specification cannot be resolved. Side effects -- Adds or updates the specification value; reindexes the parent product."
	)
	public ProductSpecification createProductIdProductSpecification(
			@GraphQLName("id") Long id,
			@GraphQLName("productSpecification") ProductSpecification
				productSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.postProductIdProductSpecification(
					id, productSpecification));
	}

	@GraphQLField
	public Response createProductIdProductSpecificationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSpecificationResource ->
				productSpecificationResource.
					postProductIdProductSpecificationBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the subscription configuration of the product identified by external reference code. Returns 404 when the product external reference code is not found. Side effects -- Updates the product definition subscription fields (length, cycles, type settings)."
	)
	public Response
			patchProductByExternalReferenceCodeSubscriptionConfiguration(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("productSubscriptionConfiguration")
					ProductSubscriptionConfiguration
						productSubscriptionConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSubscriptionConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSubscriptionConfigurationResource ->
				productSubscriptionConfigurationResource.
					patchProductByExternalReferenceCodeSubscriptionConfiguration(
						externalReferenceCode,
						productSubscriptionConfiguration));
	}

	@GraphQLField(
		description = "Partially updates the subscription configuration of the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Updates the product definition subscription fields."
	)
	public Response patchProductIdSubscriptionConfiguration(
			@GraphQLName("id") Long id,
			@GraphQLName("productSubscriptionConfiguration")
				ProductSubscriptionConfiguration
					productSubscriptionConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productSubscriptionConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productSubscriptionConfigurationResource ->
				productSubscriptionConfigurationResource.
					patchProductIdSubscriptionConfiguration(
						id, productSubscriptionConfiguration));
	}

	@GraphQLField(
		description = "Partially updates the tax configuration of the product identified by external reference code. Returns 404 when the product external reference code is not found. Side effects -- Updates the product definition tax category and taxExempt flag."
	)
	public Response patchProductByExternalReferenceCodeTaxConfiguration(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("productTaxConfiguration") ProductTaxConfiguration
				productTaxConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productTaxConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productTaxConfigurationResource ->
				productTaxConfigurationResource.
					patchProductByExternalReferenceCodeTaxConfiguration(
						externalReferenceCode, productTaxConfiguration));
	}

	@GraphQLField(
		description = "Partially updates the tax configuration of the product identified by product ID. Returns 404 when the product ID is not found. Side effects -- Updates the product definition tax category and taxExempt flag."
	)
	public Response patchProductIdTaxConfiguration(
			@GraphQLName("id") Long id,
			@GraphQLName("productTaxConfiguration") ProductTaxConfiguration
				productTaxConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_productTaxConfigurationResourceComponentServiceObjects,
			this::_populateResourceContext,
			productTaxConfigurationResource ->
				productTaxConfigurationResource.patchProductIdTaxConfiguration(
					id, productTaxConfiguration));
	}

	@GraphQLField(
		description = "Deletes the product virtual settings file entry identified by ID. Returns 404 when the record is not found. Side effects -- Removes the linked DL file entry reference (file is not deleted)."
	)
	public boolean deleteProductVirtualSettingsFileEntry(
			@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_productVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsFileEntryResource ->
				productVirtualSettingsFileEntryResource.
					deleteProductVirtualSettingsFileEntry(id));

		return true;
	}

	@GraphQLField
	public Response deleteProductVirtualSettingsFileEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_productVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsFileEntryResource ->
				productVirtualSettingsFileEntryResource.
					deleteProductVirtualSettingsFileEntryBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the product virtual settings file entry identified by ID, optionally replacing the underlying file. Returns 404 when the ID is not found. Side effects -- May create a new DL file entry under the product group when a binary file or attachment is supplied."
	)
	@GraphQLName(
		description = "Partially updates the product virtual settings file entry identified by ID, optionally replacing the underlying file. Returns 404 when the ID is not found. Side effects -- May create a new DL file entry under the product group when a binary file or attachment is supplied.",
		value = "patchProductVirtualSettingsFileEntryIdMultipartBody"
	)
	public ProductVirtualSettingsFileEntry patchProductVirtualSettingsFileEntry(
			@GraphQLName("id") Long id,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_productVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsFileEntryResource ->
				productVirtualSettingsFileEntryResource.
					patchProductVirtualSettingsFileEntry(id, multipartBody));
	}

	@GraphQLField(
		description = "Adds a file entry under the product virtual setting identified by ID, accepting a binary upload or an existing attachment reference. Returns 400 when neither binary file nor attachment is provided, and 404 when the parent ID is not found. Side effects -- Creates a DL file entry under the product group and records a new virtual settings file entry."
	)
	@GraphQLName(
		description = "Adds a file entry under the product virtual setting identified by ID, accepting a binary upload or an existing attachment reference. Returns 400 when neither binary file nor attachment is provided, and 404 when the parent ID is not found. Side effects -- Creates a DL file entry under the product group and records a new virtual settings file entry.",
		value = "postProductVirtualSettingIdProductVirtualSettingsFileEntryIdMultipartBody"
	)
	public ProductVirtualSettingsFileEntry
			createProductVirtualSettingIdProductVirtualSettingsFileEntry(
				@GraphQLName("id") Long id,
				@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_productVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			productVirtualSettingsFileEntryResource ->
				productVirtualSettingsFileEntryResource.
					postProductVirtualSettingIdProductVirtualSettingsFileEntry(
						id, multipartBody));
	}

	@GraphQLField(
		description = "Removes the related product link identified by ID. Returns 404 when the record is not found. Side effects -- Removes the related product link."
	)
	public Response deleteRelatedProduct(@GraphQLName("id") Long id)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource ->
				relatedProductResource.deleteRelatedProduct(id));
	}

	@GraphQLField
	public Response deleteRelatedProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource ->
				relatedProductResource.deleteRelatedProductBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Creates or updates a related product link on the product identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the parent product ERC not found or related product cannot be resolved. Side effects -- Creates or updates the related product link."
	)
	public RelatedProduct createProductByExternalReferenceCodeRelatedProduct(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("relatedProduct") RelatedProduct relatedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource ->
				relatedProductResource.
					postProductByExternalReferenceCodeRelatedProduct(
						externalReferenceCode, relatedProduct));
	}

	@GraphQLField(
		description = "Creates or updates a related product link on the product identified by product ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the parent product ID not found or related product cannot be resolved. Side effects -- Creates or updates the related product link."
	)
	public RelatedProduct createProductIdRelatedProduct(
			@GraphQLName("id") Long id,
			@GraphQLName("relatedProduct") RelatedProduct relatedProduct)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource ->
				relatedProductResource.postProductIdRelatedProduct(
					id, relatedProduct));
	}

	@GraphQLField
	public Response createProductIdRelatedProductBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_relatedProductResourceComponentServiceObjects,
			this::_populateResourceContext,
			relatedProductResource ->
				relatedProductResource.postProductIdRelatedProductBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the SKU identified by ID. Returns 404 when the record is not found. Side effects -- Cascades through SKU delete listeners (inventory, price entries, units of measure)."
	)
	public Response deleteSku(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.deleteSku(id));
	}

	@GraphQLField
	public Response deleteSkuBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.deleteSkuBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the SKU identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades through SKU delete listeners."
	)
	public Response deleteSkuByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.deleteSkuByExternalReferenceCode(
				externalReferenceCode));
	}

	@GraphQLField(
		description = "Partially updates the SKU identified by ID. Returns 404 when the ID is not found. Side effects -- Updates subscription, virtual settings, prices, units of measure, discontinued replacement."
	)
	public Sku patchSku(@GraphQLName("id") Long id, @GraphQLName("sku") Sku sku)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.patchSku(id, sku));
	}

	@GraphQLField(
		description = "Partially updates the SKU identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Updates subscription, virtual settings, prices, units of measure, discontinued replacement."
	)
	public Sku patchSkuByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("sku") Sku sku)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.patchSkuByExternalReferenceCode(
				externalReferenceCode, sku));
	}

	@GraphQLField(
		description = "Creates or updates a SKU under the product identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product external reference code is not found, and 400 when the product type forbids SKUs. Side effects -- Creates the SKU, default price entries, units of measure, and virtual settings."
	)
	public Sku createProductByExternalReferenceCodeSku(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("sku") Sku sku)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.postProductByExternalReferenceCodeSku(
				externalReferenceCode, sku));
	}

	@GraphQLField(
		description = "Creates or updates a SKU under the product identified by product ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the product ID is not found, and 400 when the product type forbids SKUs. Side effects -- Creates the SKU, default price entries, units of measure, and virtual settings."
	)
	public Sku createProductIdSku(
			@GraphQLName("id") Long id, @GraphQLName("sku") Sku sku)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.postProductIdSku(id, sku));
	}

	@GraphQLField
	public Response createProductIdSkuBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.postProductIdSkuBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createSkusPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.postSkusPageExportBatch(
				search, _filterBiFunction.apply(skuResource, filterString),
				_sortsBiFunction.apply(skuResource, sortsString), callbackURL,
				contentType, fieldNames));
	}

	@GraphQLField(
		description = "Replaces the SKU identified by external reference code with the supplied representation. Returns 404 when the external reference code is not found. Side effects -- Updates display/expiration/discontinued dates, subscription fields, custom field storage; resolves replacement SKU when discontinued."
	)
	public Sku updateSkuByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("sku") Sku sku)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuResourceComponentServiceObjects, this::_populateResourceContext,
			skuResource -> skuResource.putSkuByExternalReferenceCode(
				externalReferenceCode, sku));
	}

	@GraphQLField(
		description = "Deletes the SKU unit of measure identified by ID. Returns 404 when the record is not found. Side effects -- Removes the unit of measure and its associated price entries."
	)
	public boolean deleteSkuUnitOfMeasure(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource ->
				skuUnitOfMeasureResource.deleteSkuUnitOfMeasure(id));

		return true;
	}

	@GraphQLField
	public Response deleteSkuUnitOfMeasureBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource ->
				skuUnitOfMeasureResource.deleteSkuUnitOfMeasureBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the SKU unit of measure identified by ID, including base and promo prices when supplied. Returns 404 when the ID is not found. Side effects -- Updates the unit of measure record and may update base/promo price entries on the parent SKU."
	)
	public SkuUnitOfMeasure patchSkuUnitOfMeasure(
			@GraphQLName("id") Long id,
			@GraphQLName("skuUnitOfMeasure") SkuUnitOfMeasure skuUnitOfMeasure)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource ->
				skuUnitOfMeasureResource.patchSkuUnitOfMeasure(
					id, skuUnitOfMeasure));
	}

	@GraphQLField(
		description = "Creates or updates a unit of measure under the SKU identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the SKU external reference code is not found. Side effects -- Creates or updates the unit of measure, plus any supplied price entries."
	)
	public SkuUnitOfMeasure createSkuByExternalReferenceCodeSkuUnitOfMeasure(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("skuUnitOfMeasure") SkuUnitOfMeasure skuUnitOfMeasure)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource ->
				skuUnitOfMeasureResource.
					postSkuByExternalReferenceCodeSkuUnitOfMeasure(
						externalReferenceCode, skuUnitOfMeasure));
	}

	@GraphQLField(
		description = "Creates or updates a unit of measure under the SKU identified by ID. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 404 when the SKU ID is not found. Side effects -- Creates or updates the unit of measure, plus any supplied price entries."
	)
	public SkuUnitOfMeasure createSkuIdSkuUnitOfMeasure(
			@GraphQLName("id") Long id,
			@GraphQLName("skuUnitOfMeasure") SkuUnitOfMeasure skuUnitOfMeasure)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource ->
				skuUnitOfMeasureResource.postSkuIdSkuUnitOfMeasure(
					id, skuUnitOfMeasure));
	}

	@GraphQLField
	public Response createSkuIdSkuUnitOfMeasureBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuUnitOfMeasureResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuUnitOfMeasureResource ->
				skuUnitOfMeasureResource.postSkuIdSkuUnitOfMeasureBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the SKU virtual settings file entry identified by ID. Returns 404 when the record is not found. Side effects -- Removes the linked DL file entry reference (file is not deleted)."
	)
	public boolean deleteSkuVirtualSettingsFileEntry(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_skuVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsFileEntryResource ->
				skuVirtualSettingsFileEntryResource.
					deleteSkuVirtualSettingsFileEntry(id));

		return true;
	}

	@GraphQLField
	public Response deleteSkuVirtualSettingsFileEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsFileEntryResource ->
				skuVirtualSettingsFileEntryResource.
					deleteSkuVirtualSettingsFileEntryBatch(
						callbackURL, object));
	}

	@GraphQLField(
		description = "Partially updates the SKU virtual settings file entry identified by ID, optionally replacing the underlying file. Returns 404 when the ID is not found. Side effects -- May create a new DL file entry under the SKU group when a binary file or attachment is supplied."
	)
	@GraphQLName(
		description = "Partially updates the SKU virtual settings file entry identified by ID, optionally replacing the underlying file. Returns 404 when the ID is not found. Side effects -- May create a new DL file entry under the SKU group when a binary file or attachment is supplied.",
		value = "patchSkuVirtualSettingsFileEntryIdMultipartBody"
	)
	public SkuVirtualSettingsFileEntry patchSkuVirtualSettingsFileEntry(
			@GraphQLName("id") Long id,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsFileEntryResource ->
				skuVirtualSettingsFileEntryResource.
					patchSkuVirtualSettingsFileEntry(id, multipartBody));
	}

	@GraphQLField(
		description = "Adds a file entry under the SKU virtual setting identified by ID, accepting a binary upload or an existing attachment reference. Returns 400 when neither binary file nor attachment is provided, and 404 when the parent ID is not found. Side effects -- Creates a DL file entry under the SKU group and records a new virtual settings file entry."
	)
	@GraphQLName(
		description = "Adds a file entry under the SKU virtual setting identified by ID, accepting a binary upload or an existing attachment reference. Returns 400 when neither binary file nor attachment is provided, and 404 when the parent ID is not found. Side effects -- Creates a DL file entry under the SKU group and records a new virtual settings file entry.",
		value = "postSkuVirtualSettingIdSkuVirtualSettingsFileEntryIdMultipartBody"
	)
	public SkuVirtualSettingsFileEntry
			createSkuVirtualSettingIdSkuVirtualSettingsFileEntry(
				@GraphQLName("id") Long id,
				@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_skuVirtualSettingsFileEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			skuVirtualSettingsFileEntryResource ->
				skuVirtualSettingsFileEntryResource.
					postSkuVirtualSettingIdSkuVirtualSettingsFileEntry(
						id, multipartBody));
	}

	@GraphQLField(
		description = "Deletes the specification identified by ID. Returns 404 when the record is not found. Side effects -- Cascades through product specification value listeners; reindexes affected products."
	)
	public boolean deleteSpecification(@GraphQLName("id") Long id)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource -> specificationResource.deleteSpecification(
				id));

		return true;
	}

	@GraphQLField
	public Response deleteSpecificationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource ->
				specificationResource.deleteSpecificationBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the specification identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Cascades through specification value listeners."
	)
	public boolean deleteSpecificationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource ->
				specificationResource.
					deleteSpecificationByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Partially updates the specification identified by ID. Returns 404 when the ID is not found. Side effects -- Reindexes the specification and any products that reference it."
	)
	public Specification patchSpecification(
			@GraphQLName("id") Long id,
			@GraphQLName("specification") Specification specification)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource -> specificationResource.patchSpecification(
				id, specification));
	}

	@GraphQLField(
		description = "Partially updates the specification identified by external reference code. Returns 404 when the external reference code is not found. Side effects -- Reindexes the specification and dependent products."
	)
	public Specification patchSpecificationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("specification") Specification specification)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource ->
				specificationResource.patchSpecificationByExternalReferenceCode(
					externalReferenceCode, specification));
	}

	@GraphQLField(
		description = "Creates or updates a specification by trying ID, external reference code, then key for resolution. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 400 when the key is invalid. Side effects -- Reindexes the specification; lookups by ID/ERC/key fall through with debug logging."
	)
	public Specification createSpecification(
			@GraphQLName("specification") Specification specification)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource -> specificationResource.postSpecification(
				specification));
	}

	@GraphQLField
	public Response createSpecificationBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource ->
				specificationResource.postSpecificationBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response createSpecificationsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource ->
				specificationResource.postSpecificationsPageExportBatch(
					search,
					_filterBiFunction.apply(
						specificationResource, filterString),
					_sortsBiFunction.apply(specificationResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates or replaces the specification identified by external reference code. POST is upsert by external reference code -- creates a new entity when the ERC is unknown, otherwise updates the existing one. Returns 400 when the key is invalid. Side effects -- Reindexes the specification."
	)
	public Specification updateSpecificationByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("specification") Specification specification)
		throws Exception {

		return _applyComponentServiceObjects(
			_specificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			specificationResource ->
				specificationResource.putSpecificationByExternalReferenceCode(
					externalReferenceCode, specification));
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

	private void _populateResourceContext(AttachmentResource attachmentResource)
		throws Exception {

		attachmentResource.setContextAcceptLanguage(_acceptLanguage);
		attachmentResource.setContextCompany(_company);
		attachmentResource.setContextHttpServletRequest(_httpServletRequest);
		attachmentResource.setContextHttpServletResponse(_httpServletResponse);
		attachmentResource.setContextUriInfo(_uriInfo);
		attachmentResource.setContextUser(_user);
		attachmentResource.setGroupLocalService(_groupLocalService);
		attachmentResource.setRoleLocalService(_roleLocalService);

		attachmentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		attachmentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		catalogResource.setRoleLocalService(_roleLocalService);

		catalogResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		catalogResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		categoryResource.setRoleLocalService(_roleLocalService);

		categoryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		categoryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		currencyResource.setRoleLocalService(_roleLocalService);

		currencyResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		currencyResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		diagramResource.setRoleLocalService(_roleLocalService);

		diagramResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		diagramResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		groupedProductResource.setRoleLocalService(_roleLocalService);

		groupedProductResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		groupedProductResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		listTypeDefinitionResource.setRoleLocalService(_roleLocalService);

		listTypeDefinitionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		listTypeDefinitionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		lowStockActionResource.setRoleLocalService(_roleLocalService);

		lowStockActionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		lowStockActionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		mappedProductResource.setRoleLocalService(_roleLocalService);

		mappedProductResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		mappedProductResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		optionResource.setRoleLocalService(_roleLocalService);

		optionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		optionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		optionCategoryResource.setRoleLocalService(_roleLocalService);

		optionCategoryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		optionCategoryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		optionValueResource.setRoleLocalService(_roleLocalService);

		optionValueResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		optionValueResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		pinResource.setRoleLocalService(_roleLocalService);

		pinResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pinResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productResource.setRoleLocalService(_roleLocalService);

		productResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productAccountGroupResource.setRoleLocalService(_roleLocalService);

		productAccountGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productAccountGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productChannelResource.setRoleLocalService(_roleLocalService);

		productChannelResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productChannelResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productConfigurationResource.setRoleLocalService(_roleLocalService);

		productConfigurationResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productConfigurationResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productConfigurationListResource.setRoleLocalService(_roleLocalService);

		productConfigurationListResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productConfigurationListResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productConfigurationListAccountResource.setRoleLocalService(
			_roleLocalService);

		productConfigurationListAccountResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		productConfigurationListAccountResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		productConfigurationListAccountGroupResource.setRoleLocalService(
			_roleLocalService);

		productConfigurationListAccountGroupResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		productConfigurationListAccountGroupResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		productConfigurationListChannelResource.setRoleLocalService(
			_roleLocalService);

		productConfigurationListChannelResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		productConfigurationListChannelResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		productConfigurationListOrderTypeResource.setRoleLocalService(
			_roleLocalService);

		productConfigurationListOrderTypeResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		productConfigurationListOrderTypeResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		productGroupResource.setRoleLocalService(_roleLocalService);

		productGroupResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productGroupResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productGroupProductResource.setRoleLocalService(_roleLocalService);

		productGroupProductResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productGroupProductResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productOptionResource.setRoleLocalService(_roleLocalService);

		productOptionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productOptionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productOptionValueResource.setRoleLocalService(_roleLocalService);

		productOptionValueResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productOptionValueResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productSpecificationResource.setRoleLocalService(_roleLocalService);

		productSpecificationResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		productSpecificationResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		productTaxConfigurationResource.setRoleLocalService(_roleLocalService);
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
		productVirtualSettingsFileEntryResource.setRoleLocalService(
			_roleLocalService);

		productVirtualSettingsFileEntryResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		productVirtualSettingsFileEntryResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		relatedProductResource.setRoleLocalService(_roleLocalService);

		relatedProductResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		relatedProductResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		skuResource.setRoleLocalService(_roleLocalService);

		skuResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		skuResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		skuUnitOfMeasureResource.setRoleLocalService(_roleLocalService);

		skuUnitOfMeasureResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		skuUnitOfMeasureResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		skuVirtualSettingsFileEntryResource.setRoleLocalService(
			_roleLocalService);

		skuVirtualSettingsFileEntryResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		skuVirtualSettingsFileEntryResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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
		specificationResource.setRoleLocalService(_roleLocalService);

		specificationResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		specificationResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
	private static ComponentServiceObjects
		<ProductVirtualSettingsFileEntryResource>
			_productVirtualSettingsFileEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<RelatedProductResource>
		_relatedProductResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuResource>
		_skuResourceComponentServiceObjects;
	private static ComponentServiceObjects<SkuUnitOfMeasureResource>
		_skuUnitOfMeasureResourceComponentServiceObjects;
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
// LIFERAY-REST-BUILDER-HASH:745278027