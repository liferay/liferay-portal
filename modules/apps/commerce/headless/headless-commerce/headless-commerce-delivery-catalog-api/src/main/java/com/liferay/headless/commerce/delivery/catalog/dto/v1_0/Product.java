/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("Product")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Product")
public class Product implements Serializable {

	public static Product toDTO(String json) {
		return ObjectMapperUtil.readValue(Product.class, json);
	}

	public static Product unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Product.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Attachment[] getAttachments() {
		if (_attachmentsSupplier != null) {
			attachments = _attachmentsSupplier.get();

			_attachmentsSupplier = null;
		}

		return attachments;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;

		_attachmentsSupplier = null;
	}

	@JsonIgnore
	public void setAttachments(
		UnsafeSupplier<Attachment[], Exception> attachmentsUnsafeSupplier) {

		_attachmentsSupplier = () -> {
			try {
				return attachmentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Attachment[] attachments;

	@JsonIgnore
	private Supplier<Attachment[]> _attachmentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCatalogName() {
		if (_catalogNameSupplier != null) {
			catalogName = _catalogNameSupplier.get();

			_catalogNameSupplier = null;
		}

		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;

		_catalogNameSupplier = null;
	}

	@JsonIgnore
	public void setCatalogName(
		UnsafeSupplier<String, Exception> catalogNameUnsafeSupplier) {

		_catalogNameSupplier = () -> {
			try {
				return catalogNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String catalogName;

	@JsonIgnore
	private Supplier<String> _catalogNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Category[] getCategories() {
		if (_categoriesSupplier != null) {
			categories = _categoriesSupplier.get();

			_categoriesSupplier = null;
		}

		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;

		_categoriesSupplier = null;
	}

	@JsonIgnore
	public void setCategories(
		UnsafeSupplier<Category[], Exception> categoriesUnsafeSupplier) {

		_categoriesSupplier = () -> {
			try {
				return categoriesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Category[] categories;

	@JsonIgnore
	private Supplier<Category[]> _categoriesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getCreateDate() {
		if (_createDateSupplier != null) {
			createDate = _createDateSupplier.get();

			_createDateSupplier = null;
		}

		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;

		_createDateSupplier = null;
	}

	@JsonIgnore
	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		_createDateSupplier = () -> {
			try {
				return createDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public com.liferay.portal.vulcan.custom.field.CustomField[]
		getCustomFields() {

		if (_customFieldsSupplier != null) {
			customFields = _customFieldsSupplier.get();

			_customFieldsSupplier = null;
		}

		return customFields;
	}

	public void setCustomFields(
		com.liferay.portal.vulcan.custom.field.CustomField[] customFields) {

		this.customFields = customFields;

		_customFieldsSupplier = null;
	}

	@JsonIgnore
	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.portal.vulcan.custom.field.CustomField[], Exception>
				customFieldsUnsafeSupplier) {

		_customFieldsSupplier = () -> {
			try {
				return customFieldsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected com.liferay.portal.vulcan.custom.field.CustomField[] customFields;

	@JsonIgnore
	private Supplier<com.liferay.portal.vulcan.custom.field.CustomField[]>
		_customFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, ?> getExpando() {
		if (_expandoSupplier != null) {
			expando = _expandoSupplier.get();

			_expandoSupplier = null;
		}

		return expando;
	}

	public void setExpando(Map<String, ?> expando) {
		this.expando = expando;

		_expandoSupplier = null;
	}

	@JsonIgnore
	public void setExpando(
		UnsafeSupplier<Map<String, ?>, Exception> expandoUnsafeSupplier) {

		_expandoSupplier = () -> {
			try {
				return expandoUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, ?> expando;

	@JsonIgnore
	private Supplier<Map<String, ?>> _expandoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Attachment[] getImages() {
		if (_imagesSupplier != null) {
			images = _imagesSupplier.get();

			_imagesSupplier = null;
		}

		return images;
	}

	public void setImages(Attachment[] images) {
		this.images = images;

		_imagesSupplier = null;
	}

	@JsonIgnore
	public void setImages(
		UnsafeSupplier<Attachment[], Exception> imagesUnsafeSupplier) {

		_imagesSupplier = () -> {
			try {
				return imagesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Attachment[] images;

	@JsonIgnore
	private Supplier<Attachment[]> _imagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public LinkedProduct[] getLinkedProducts() {
		if (_linkedProductsSupplier != null) {
			linkedProducts = _linkedProductsSupplier.get();

			_linkedProductsSupplier = null;
		}

		return linkedProducts;
	}

	public void setLinkedProducts(LinkedProduct[] linkedProducts) {
		this.linkedProducts = linkedProducts;

		_linkedProductsSupplier = null;
	}

	@JsonIgnore
	public void setLinkedProducts(
		UnsafeSupplier<LinkedProduct[], Exception>
			linkedProductsUnsafeSupplier) {

		_linkedProductsSupplier = () -> {
			try {
				return linkedProductsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected LinkedProduct[] linkedProducts;

	@JsonIgnore
	private Supplier<LinkedProduct[]> _linkedProductsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMetaDescription() {
		if (_metaDescriptionSupplier != null) {
			metaDescription = _metaDescriptionSupplier.get();

			_metaDescriptionSupplier = null;
		}

		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;

		_metaDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setMetaDescription(
		UnsafeSupplier<String, Exception> metaDescriptionUnsafeSupplier) {

		_metaDescriptionSupplier = () -> {
			try {
				return metaDescriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String metaDescription;

	@JsonIgnore
	private Supplier<String> _metaDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMetaKeyword() {
		if (_metaKeywordSupplier != null) {
			metaKeyword = _metaKeywordSupplier.get();

			_metaKeywordSupplier = null;
		}

		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;

		_metaKeywordSupplier = null;
	}

	@JsonIgnore
	public void setMetaKeyword(
		UnsafeSupplier<String, Exception> metaKeywordUnsafeSupplier) {

		_metaKeywordSupplier = () -> {
			try {
				return metaKeywordUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String metaKeyword;

	@JsonIgnore
	private Supplier<String> _metaKeywordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMetaTitle() {
		if (_metaTitleSupplier != null) {
			metaTitle = _metaTitleSupplier.get();

			_metaTitleSupplier = null;
		}

		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;

		_metaTitleSupplier = null;
	}

	@JsonIgnore
	public void setMetaTitle(
		UnsafeSupplier<String, Exception> metaTitleUnsafeSupplier) {

		_metaTitleSupplier = () -> {
			try {
				return metaTitleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String metaTitle;

	@JsonIgnore
	private Supplier<String> _metaTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getModifiedDate() {
		if (_modifiedDateSupplier != null) {
			modifiedDate = _modifiedDateSupplier.get();

			_modifiedDateSupplier = null;
		}

		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;

		_modifiedDateSupplier = null;
	}

	@JsonIgnore
	public void setModifiedDate(
		UnsafeSupplier<Date, Exception> modifiedDateUnsafeSupplier) {

		_modifiedDateSupplier = () -> {
			try {
				return modifiedDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date modifiedDate;

	@JsonIgnore
	private Supplier<Date> _modifiedDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMultipleOrderQuantity() {
		if (_multipleOrderQuantitySupplier != null) {
			multipleOrderQuantity = _multipleOrderQuantitySupplier.get();

			_multipleOrderQuantitySupplier = null;
		}

		return multipleOrderQuantity;
	}

	public void setMultipleOrderQuantity(Integer multipleOrderQuantity) {
		this.multipleOrderQuantity = multipleOrderQuantity;

		_multipleOrderQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMultipleOrderQuantity(
		UnsafeSupplier<Integer, Exception>
			multipleOrderQuantityUnsafeSupplier) {

		_multipleOrderQuantitySupplier = () -> {
			try {
				return multipleOrderQuantityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer multipleOrderQuantity;

	@JsonIgnore
	private Supplier<Integer> _multipleOrderQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductConfiguration getProductConfiguration() {
		if (_productConfigurationSupplier != null) {
			productConfiguration = _productConfigurationSupplier.get();

			_productConfigurationSupplier = null;
		}

		return productConfiguration;
	}

	public void setProductConfiguration(
		ProductConfiguration productConfiguration) {

		this.productConfiguration = productConfiguration;

		_productConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setProductConfiguration(
		UnsafeSupplier<ProductConfiguration, Exception>
			productConfigurationUnsafeSupplier) {

		_productConfigurationSupplier = () -> {
			try {
				return productConfigurationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ProductConfiguration productConfiguration;

	@JsonIgnore
	private Supplier<ProductConfiguration> _productConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getProductId() {
		if (_productIdSupplier != null) {
			productId = _productIdSupplier.get();

			_productIdSupplier = null;
		}

		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;

		_productIdSupplier = null;
	}

	@JsonIgnore
	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		_productIdSupplier = () -> {
			try {
				return productIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductOption[] getProductOptions() {
		if (_productOptionsSupplier != null) {
			productOptions = _productOptionsSupplier.get();

			_productOptionsSupplier = null;
		}

		return productOptions;
	}

	public void setProductOptions(ProductOption[] productOptions) {
		this.productOptions = productOptions;

		_productOptionsSupplier = null;
	}

	@JsonIgnore
	public void setProductOptions(
		UnsafeSupplier<ProductOption[], Exception>
			productOptionsUnsafeSupplier) {

		_productOptionsSupplier = () -> {
			try {
				return productOptionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ProductOption[] productOptions;

	@JsonIgnore
	private Supplier<ProductOption[]> _productOptionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ProductSpecification[] getProductSpecifications() {
		if (_productSpecificationsSupplier != null) {
			productSpecifications = _productSpecificationsSupplier.get();

			_productSpecificationsSupplier = null;
		}

		return productSpecifications;
	}

	public void setProductSpecifications(
		ProductSpecification[] productSpecifications) {

		this.productSpecifications = productSpecifications;

		_productSpecificationsSupplier = null;
	}

	@JsonIgnore
	public void setProductSpecifications(
		UnsafeSupplier<ProductSpecification[], Exception>
			productSpecificationsUnsafeSupplier) {

		_productSpecificationsSupplier = () -> {
			try {
				return productSpecificationsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ProductSpecification[] productSpecifications;

	@JsonIgnore
	private Supplier<ProductSpecification[]> _productSpecificationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "simple")
	public String getProductType() {
		if (_productTypeSupplier != null) {
			productType = _productTypeSupplier.get();

			_productTypeSupplier = null;
		}

		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;

		_productTypeSupplier = null;
	}

	@JsonIgnore
	public void setProductType(
		UnsafeSupplier<String, Exception> productTypeUnsafeSupplier) {

		_productTypeSupplier = () -> {
			try {
				return productTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String productType;

	@JsonIgnore
	private Supplier<String> _productTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public RelatedProduct[] getRelatedProducts() {
		if (_relatedProductsSupplier != null) {
			relatedProducts = _relatedProductsSupplier.get();

			_relatedProductsSupplier = null;
		}

		return relatedProducts;
	}

	public void setRelatedProducts(RelatedProduct[] relatedProducts) {
		this.relatedProducts = relatedProducts;

		_relatedProductsSupplier = null;
	}

	@JsonIgnore
	public void setRelatedProducts(
		UnsafeSupplier<RelatedProduct[], Exception>
			relatedProductsUnsafeSupplier) {

		_relatedProductsSupplier = () -> {
			try {
				return relatedProductsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected RelatedProduct[] relatedProducts;

	@JsonIgnore
	private Supplier<RelatedProduct[]> _relatedProductsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShortDescription() {
		if (_shortDescriptionSupplier != null) {
			shortDescription = _shortDescriptionSupplier.get();

			_shortDescriptionSupplier = null;
		}

		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;

		_shortDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setShortDescription(
		UnsafeSupplier<String, Exception> shortDescriptionUnsafeSupplier) {

		_shortDescriptionSupplier = () -> {
			try {
				return shortDescriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String shortDescription;

	@JsonIgnore
	private Supplier<String> _shortDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Sku[] getSkus() {
		if (_skusSupplier != null) {
			skus = _skusSupplier.get();

			_skusSupplier = null;
		}

		return skus;
	}

	public void setSkus(Sku[] skus) {
		this.skus = skus;

		_skusSupplier = null;
	}

	@JsonIgnore
	public void setSkus(UnsafeSupplier<Sku[], Exception> skusUnsafeSupplier) {
		_skusSupplier = () -> {
			try {
				return skusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Sku[] skus;

	@JsonIgnore
	private Supplier<Sku[]> _skusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSlug() {
		if (_slugSupplier != null) {
			slug = _slugSupplier.get();

			_slugSupplier = null;
		}

		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;

		_slugSupplier = null;
	}

	@JsonIgnore
	public void setSlug(UnsafeSupplier<String, Exception> slugUnsafeSupplier) {
		_slugSupplier = () -> {
			try {
				return slugUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String slug;

	@JsonIgnore
	private Supplier<String> _slugSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "[tag1, tag2, tag3]")
	public String[] getTags() {
		if (_tagsSupplier != null) {
			tags = _tagsSupplier.get();

			_tagsSupplier = null;
		}

		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;

		_tagsSupplier = null;
	}

	@JsonIgnore
	public void setTags(
		UnsafeSupplier<String[], Exception> tagsUnsafeSupplier) {

		_tagsSupplier = () -> {
			try {
				return tagsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] tags;

	@JsonIgnore
	private Supplier<String[]> _tagsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getUrlImage() {
		if (_urlImageSupplier != null) {
			urlImage = _urlImageSupplier.get();

			_urlImageSupplier = null;
		}

		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;

		_urlImageSupplier = null;
	}

	@JsonIgnore
	public void setUrlImage(
		UnsafeSupplier<String, Exception> urlImageUnsafeSupplier) {

		_urlImageSupplier = () -> {
			try {
				return urlImageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String urlImage;

	@JsonIgnore
	private Supplier<String> _urlImageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=product-url-us, hr_HR=product-url-hr, hu_HU=product-url-hu}"
	)
	@Valid
	public Map<String, String> getUrls() {
		if (_urlsSupplier != null) {
			urls = _urlsSupplier.get();

			_urlsSupplier = null;
		}

		return urls;
	}

	public void setUrls(Map<String, String> urls) {
		this.urls = urls;

		_urlsSupplier = null;
	}

	@JsonIgnore
	public void setUrls(
		UnsafeSupplier<Map<String, String>, Exception> urlsUnsafeSupplier) {

		_urlsSupplier = () -> {
			try {
				return urlsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> urls;

	@JsonIgnore
	private Supplier<Map<String, String>> _urlsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Product)) {
			return false;
		}

		Product product = (Product)object;

		return Objects.equals(toString(), product.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Attachment[] attachments = getAttachments();

		if (attachments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachments\": ");

			sb.append("[");

			for (int i = 0; i < attachments.length; i++) {
				sb.append(String.valueOf(attachments[i]));

				if ((i + 1) < attachments.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String catalogName = getCatalogName();

		if (catalogName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogName\": ");

			sb.append("\"");

			sb.append(_escape(catalogName));

			sb.append("\"");
		}

		Category[] categories = getCategories();

		if (categories != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categories\": ");

			sb.append("[");

			for (int i = 0; i < categories.length; i++) {
				sb.append(String.valueOf(categories[i]));

				if ((i + 1) < categories.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createDate));

			sb.append("\"");
		}

		com.liferay.portal.vulcan.custom.field.CustomField[] customFields =
			getCustomFields();

		if (customFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < customFields.length; i++) {
				sb.append(customFields[i]);

				if ((i + 1) < customFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		Map<String, ?> expando = getExpando();

		if (expando != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expando\": ");

			sb.append(_toJSON(expando));
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Attachment[] images = getImages();

		if (images != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"images\": ");

			sb.append("[");

			for (int i = 0; i < images.length; i++) {
				sb.append(String.valueOf(images[i]));

				if ((i + 1) < images.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		LinkedProduct[] linkedProducts = getLinkedProducts();

		if (linkedProducts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"linkedProducts\": ");

			sb.append("[");

			for (int i = 0; i < linkedProducts.length; i++) {
				sb.append(String.valueOf(linkedProducts[i]));

				if ((i + 1) < linkedProducts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String metaDescription = getMetaDescription();

		if (metaDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaDescription\": ");

			sb.append("\"");

			sb.append(_escape(metaDescription));

			sb.append("\"");
		}

		String metaKeyword = getMetaKeyword();

		if (metaKeyword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaKeyword\": ");

			sb.append("\"");

			sb.append(_escape(metaKeyword));

			sb.append("\"");
		}

		String metaTitle = getMetaTitle();

		if (metaTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaTitle\": ");

			sb.append("\"");

			sb.append(_escape(metaTitle));

			sb.append("\"");
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(modifiedDate));

			sb.append("\"");
		}

		Integer multipleOrderQuantity = getMultipleOrderQuantity();

		if (multipleOrderQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multipleOrderQuantity\": ");

			sb.append(multipleOrderQuantity);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		ProductConfiguration productConfiguration = getProductConfiguration();

		if (productConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfiguration\": ");

			sb.append(String.valueOf(productConfiguration));
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		ProductOption[] productOptions = getProductOptions();

		if (productOptions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptions\": ");

			sb.append("[");

			for (int i = 0; i < productOptions.length; i++) {
				sb.append(String.valueOf(productOptions[i]));

				if ((i + 1) < productOptions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ProductSpecification[] productSpecifications =
			getProductSpecifications();

		if (productSpecifications != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productSpecifications\": ");

			sb.append("[");

			for (int i = 0; i < productSpecifications.length; i++) {
				sb.append(String.valueOf(productSpecifications[i]));

				if ((i + 1) < productSpecifications.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String productType = getProductType();

		if (productType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productType\": ");

			sb.append("\"");

			sb.append(_escape(productType));

			sb.append("\"");
		}

		RelatedProduct[] relatedProducts = getRelatedProducts();

		if (relatedProducts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedProducts\": ");

			sb.append("[");

			for (int i = 0; i < relatedProducts.length; i++) {
				sb.append(String.valueOf(relatedProducts[i]));

				if ((i + 1) < relatedProducts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String shortDescription = getShortDescription();

		if (shortDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shortDescription\": ");

			sb.append("\"");

			sb.append(_escape(shortDescription));

			sb.append("\"");
		}

		Sku[] skus = getSkus();

		if (skus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skus\": ");

			sb.append("[");

			for (int i = 0; i < skus.length; i++) {
				sb.append(String.valueOf(skus[i]));

				if ((i + 1) < skus.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String slug = getSlug();

		if (slug != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"slug\": ");

			sb.append("\"");

			sb.append(_escape(slug));

			sb.append("\"");
		}

		String[] tags = getTags();

		if (tags != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < tags.length; i++) {
				sb.append("\"");

				sb.append(_escape(tags[i]));

				sb.append("\"");

				if ((i + 1) < tags.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String urlImage = getUrlImage();

		if (urlImage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urlImage\": ");

			sb.append("\"");

			sb.append(_escape(urlImage));

			sb.append("\"");
		}

		Map<String, String> urls = getUrls();

		if (urls != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append(_toJSON(urls));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}