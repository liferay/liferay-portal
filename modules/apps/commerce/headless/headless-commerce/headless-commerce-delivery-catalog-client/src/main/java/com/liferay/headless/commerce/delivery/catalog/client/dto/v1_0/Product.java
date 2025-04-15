/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.ProductSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Product implements Cloneable, Serializable {

	public static Product toDTO(String json) {
		return ProductSerDes.toDTO(json);
	}

	public Attachment[] getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;
	}

	public void setAttachments(
		UnsafeSupplier<Attachment[], Exception> attachmentsUnsafeSupplier) {

		try {
			attachments = attachmentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Attachment[] attachments;

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public void setCatalogName(
		UnsafeSupplier<String, Exception> catalogNameUnsafeSupplier) {

		try {
			catalogName = catalogNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String catalogName;

	public Category[] getCategories() {
		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;
	}

	public void setCategories(
		UnsafeSupplier<Category[], Exception> categoriesUnsafeSupplier) {

		try {
			categories = categoriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Category[] categories;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date createDate;

	public com.liferay.headless.commerce.delivery.catalog.client.custom.field.
		CustomField[] getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.commerce.delivery.catalog.client.custom.field.
			CustomField[] customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.commerce.delivery.catalog.client.custom.field.
				CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected
		com.liferay.headless.commerce.delivery.catalog.client.custom.field.
			CustomField[] customFields;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public Map<String, ?> getExpando() {
		return expando;
	}

	public void setExpando(Map<String, ?> expando) {
		this.expando = expando;
	}

	public void setExpando(
		UnsafeSupplier<Map<String, ?>, Exception> expandoUnsafeSupplier) {

		try {
			expando = expandoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> expando;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Attachment[] getImages() {
		return images;
	}

	public void setImages(Attachment[] images) {
		this.images = images;
	}

	public void setImages(
		UnsafeSupplier<Attachment[], Exception> imagesUnsafeSupplier) {

		try {
			images = imagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Attachment[] images;

	public LinkedProduct[] getLinkedProducts() {
		return linkedProducts;
	}

	public void setLinkedProducts(LinkedProduct[] linkedProducts) {
		this.linkedProducts = linkedProducts;
	}

	public void setLinkedProducts(
		UnsafeSupplier<LinkedProduct[], Exception>
			linkedProductsUnsafeSupplier) {

		try {
			linkedProducts = linkedProductsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected LinkedProduct[] linkedProducts;

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public void setMetaDescription(
		UnsafeSupplier<String, Exception> metaDescriptionUnsafeSupplier) {

		try {
			metaDescription = metaDescriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String metaDescription;

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public void setMetaKeyword(
		UnsafeSupplier<String, Exception> metaKeywordUnsafeSupplier) {

		try {
			metaKeyword = metaKeywordUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String metaKeyword;

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public void setMetaTitle(
		UnsafeSupplier<String, Exception> metaTitleUnsafeSupplier) {

		try {
			metaTitle = metaTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String metaTitle;

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setModifiedDate(
		UnsafeSupplier<Date, Exception> modifiedDateUnsafeSupplier) {

		try {
			modifiedDate = modifiedDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date modifiedDate;

	public Integer getMultipleOrderQuantity() {
		return multipleOrderQuantity;
	}

	public void setMultipleOrderQuantity(Integer multipleOrderQuantity) {
		this.multipleOrderQuantity = multipleOrderQuantity;
	}

	public void setMultipleOrderQuantity(
		UnsafeSupplier<Integer, Exception>
			multipleOrderQuantityUnsafeSupplier) {

		try {
			multipleOrderQuantity = multipleOrderQuantityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer multipleOrderQuantity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public ProductConfiguration getProductConfiguration() {
		return productConfiguration;
	}

	public void setProductConfiguration(
		ProductConfiguration productConfiguration) {

		this.productConfiguration = productConfiguration;
	}

	public void setProductConfiguration(
		UnsafeSupplier<ProductConfiguration, Exception>
			productConfigurationUnsafeSupplier) {

		try {
			productConfiguration = productConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductConfiguration productConfiguration;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		try {
			productId = productIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long productId;

	public ProductOption[] getProductOptions() {
		return productOptions;
	}

	public void setProductOptions(ProductOption[] productOptions) {
		this.productOptions = productOptions;
	}

	public void setProductOptions(
		UnsafeSupplier<ProductOption[], Exception>
			productOptionsUnsafeSupplier) {

		try {
			productOptions = productOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductOption[] productOptions;

	public ProductSpecification[] getProductSpecifications() {
		return productSpecifications;
	}

	public void setProductSpecifications(
		ProductSpecification[] productSpecifications) {

		this.productSpecifications = productSpecifications;
	}

	public void setProductSpecifications(
		UnsafeSupplier<ProductSpecification[], Exception>
			productSpecificationsUnsafeSupplier) {

		try {
			productSpecifications = productSpecificationsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ProductSpecification[] productSpecifications;

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public void setProductType(
		UnsafeSupplier<String, Exception> productTypeUnsafeSupplier) {

		try {
			productType = productTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String productType;

	public RelatedProduct[] getRelatedProducts() {
		return relatedProducts;
	}

	public void setRelatedProducts(RelatedProduct[] relatedProducts) {
		this.relatedProducts = relatedProducts;
	}

	public void setRelatedProducts(
		UnsafeSupplier<RelatedProduct[], Exception>
			relatedProductsUnsafeSupplier) {

		try {
			relatedProducts = relatedProductsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RelatedProduct[] relatedProducts;

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setShortDescription(
		UnsafeSupplier<String, Exception> shortDescriptionUnsafeSupplier) {

		try {
			shortDescription = shortDescriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String shortDescription;

	public Sku[] getSkus() {
		return skus;
	}

	public void setSkus(Sku[] skus) {
		this.skus = skus;
	}

	public void setSkus(UnsafeSupplier<Sku[], Exception> skusUnsafeSupplier) {
		try {
			skus = skusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Sku[] skus;

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public void setSlug(UnsafeSupplier<String, Exception> slugUnsafeSupplier) {
		try {
			slug = slugUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String slug;

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public void setTags(
		UnsafeSupplier<String[], Exception> tagsUnsafeSupplier) {

		try {
			tags = tagsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] tags;

	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}

	public void setUrlImage(
		UnsafeSupplier<String, Exception> urlImageUnsafeSupplier) {

		try {
			urlImage = urlImageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String urlImage;

	public Map<String, String> getUrls() {
		return urls;
	}

	public void setUrls(Map<String, String> urls) {
		this.urls = urls;
	}

	public void setUrls(
		UnsafeSupplier<Map<String, String>, Exception> urlsUnsafeSupplier) {

		try {
			urls = urlsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> urls;

	@Override
	public Product clone() throws CloneNotSupportedException {
		return (Product)super.clone();
	}

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
		return ProductSerDes.toJSON(this);
	}

}