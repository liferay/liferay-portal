/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Category;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.LinkedProduct;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Pin;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductAccountGroup;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductChannel;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.RelatedProduct;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductSerDes {

	public static Product toDTO(String json) {
		ProductJSONParser productJSONParser = new ProductJSONParser();

		return productJSONParser.parseToDTO(json);
	}

	public static Product[] toDTOs(String json) {
		ProductJSONParser productJSONParser = new ProductJSONParser();

		return productJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Product product) {
		if (product == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (product.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(product.getActions()));
		}

		if (product.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(product.getActive());
		}

		if (product.getAttachments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachments\": ");

			sb.append("[");

			for (int i = 0; i < product.getAttachments().length; i++) {
				sb.append(String.valueOf(product.getAttachments()[i]));

				if ((i + 1) < product.getAttachments().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getCatalog() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalog\": ");

			sb.append(String.valueOf(product.getCatalog()));
		}

		if (product.getCatalogExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(product.getCatalogExternalReferenceCode()));

			sb.append("\"");
		}

		if (product.getCatalogId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(product.getCatalogId());
		}

		if (product.getCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categories\": ");

			sb.append("[");

			for (int i = 0; i < product.getCategories().length; i++) {
				sb.append(String.valueOf(product.getCategories()[i]));

				if ((i + 1) < product.getCategories().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(product.getCreateDate()));

			sb.append("\"");
		}

		if (product.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < product.getCustomFields().length; i++) {
				sb.append(product.getCustomFields()[i]);

				if ((i + 1) < product.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getDefaultSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultSku\": ");

			sb.append("\"");

			sb.append(_escape(product.getDefaultSku()));

			sb.append("\"");
		}

		if (product.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(product.getDescription()));
		}

		if (product.getDiagram() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"diagram\": ");

			sb.append(String.valueOf(product.getDiagram()));
		}

		if (product.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(product.getDisplayDate()));

			sb.append("\"");
		}

		if (product.getExpando() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expando\": ");

			sb.append(_toJSON(product.getExpando()));
		}

		if (product.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(product.getExpirationDate()));

			sb.append("\"");
		}

		if (product.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(product.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (product.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(product.getId());
		}

		if (product.getImages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"images\": ");

			sb.append("[");

			for (int i = 0; i < product.getImages().length; i++) {
				sb.append(String.valueOf(product.getImages()[i]));

				if ((i + 1) < product.getImages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getLinkedProducts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"linkedProducts\": ");

			sb.append("[");

			for (int i = 0; i < product.getLinkedProducts().length; i++) {
				sb.append(String.valueOf(product.getLinkedProducts()[i]));

				if ((i + 1) < product.getLinkedProducts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getMappedProducts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mappedProducts\": ");

			sb.append("[");

			for (int i = 0; i < product.getMappedProducts().length; i++) {
				sb.append(String.valueOf(product.getMappedProducts()[i]));

				if ((i + 1) < product.getMappedProducts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getMetaDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaDescription\": ");

			sb.append(_toJSON(product.getMetaDescription()));
		}

		if (product.getMetaKeyword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaKeyword\": ");

			sb.append(_toJSON(product.getMetaKeyword()));
		}

		if (product.getMetaTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaTitle\": ");

			sb.append(_toJSON(product.getMetaTitle()));
		}

		if (product.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(product.getModifiedDate()));

			sb.append("\"");
		}

		if (product.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(product.getName()));
		}

		if (product.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(product.getNeverExpire());
		}

		if (product.getPins() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pins\": ");

			sb.append("[");

			for (int i = 0; i < product.getPins().length; i++) {
				sb.append(String.valueOf(product.getPins()[i]));

				if ((i + 1) < product.getPins().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductAccountGroupFilter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productAccountGroupFilter\": ");

			sb.append(product.getProductAccountGroupFilter());
		}

		if (product.getProductAccountGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productAccountGroups\": ");

			sb.append("[");

			for (int i = 0; i < product.getProductAccountGroups().length; i++) {
				sb.append(String.valueOf(product.getProductAccountGroups()[i]));

				if ((i + 1) < product.getProductAccountGroups().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductChannelFilter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productChannelFilter\": ");

			sb.append(product.getProductChannelFilter());
		}

		if (product.getProductChannels() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productChannels\": ");

			sb.append("[");

			for (int i = 0; i < product.getProductChannels().length; i++) {
				sb.append(String.valueOf(product.getProductChannels()[i]));

				if ((i + 1) < product.getProductChannels().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfiguration\": ");

			sb.append(String.valueOf(product.getProductConfiguration()));
		}

		if (product.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(product.getProductId());
		}

		if (product.getProductOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptions\": ");

			sb.append("[");

			for (int i = 0; i < product.getProductOptions().length; i++) {
				sb.append(String.valueOf(product.getProductOptions()[i]));

				if ((i + 1) < product.getProductOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductSpecifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productSpecifications\": ");

			sb.append("[");

			for (int i = 0; i < product.getProductSpecifications().length;
				 i++) {

				sb.append(
					String.valueOf(product.getProductSpecifications()[i]));

				if ((i + 1) < product.getProductSpecifications().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productStatus\": ");

			sb.append(product.getProductStatus());
		}

		if (product.getProductType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productType\": ");

			sb.append("\"");

			sb.append(_escape(product.getProductType()));

			sb.append("\"");
		}

		if (product.getProductTypeI18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productTypeI18n\": ");

			sb.append("\"");

			sb.append(_escape(product.getProductTypeI18n()));

			sb.append("\"");
		}

		if (product.getProductVirtualSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productVirtualSettings\": ");

			sb.append(String.valueOf(product.getProductVirtualSettings()));
		}

		if (product.getRelatedProducts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedProducts\": ");

			sb.append("[");

			for (int i = 0; i < product.getRelatedProducts().length; i++) {
				sb.append(String.valueOf(product.getRelatedProducts()[i]));

				if ((i + 1) < product.getRelatedProducts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getShippingConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingConfiguration\": ");

			sb.append(String.valueOf(product.getShippingConfiguration()));
		}

		if (product.getShortDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shortDescription\": ");

			sb.append(_toJSON(product.getShortDescription()));
		}

		if (product.getSkuFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuFormatted\": ");

			sb.append("\"");

			sb.append(_escape(product.getSkuFormatted()));

			sb.append("\"");
		}

		if (product.getSkus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skus\": ");

			sb.append("[");

			for (int i = 0; i < product.getSkus().length; i++) {
				sb.append(String.valueOf(product.getSkus()[i]));

				if ((i + 1) < product.getSkus().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getSubscriptionConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscriptionConfiguration\": ");

			sb.append(String.valueOf(product.getSubscriptionConfiguration()));
		}

		if (product.getTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < product.getTags().length; i++) {
				sb.append(_toJSON(product.getTags()[i]));

				if ((i + 1) < product.getTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getTaxConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxConfiguration\": ");

			sb.append(String.valueOf(product.getTaxConfiguration()));
		}

		if (product.getThumbnail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append("\"");

			sb.append(_escape(product.getThumbnail()));

			sb.append("\"");
		}

		if (product.getUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append(_toJSON(product.getUrls()));
		}

		if (product.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append(product.getVersion());
		}

		if (product.getWorkflowStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowStatusInfo\": ");

			sb.append(String.valueOf(product.getWorkflowStatusInfo()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductJSONParser productJSONParser = new ProductJSONParser();

		return productJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Product product) {
		if (product == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (product.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(product.getActions()));
		}

		if (product.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(product.getActive()));
		}

		if (product.getAttachments() == null) {
			map.put("attachments", null);
		}
		else {
			map.put("attachments", String.valueOf(product.getAttachments()));
		}

		if (product.getCatalog() == null) {
			map.put("catalog", null);
		}
		else {
			map.put("catalog", String.valueOf(product.getCatalog()));
		}

		if (product.getCatalogExternalReferenceCode() == null) {
			map.put("catalogExternalReferenceCode", null);
		}
		else {
			map.put(
				"catalogExternalReferenceCode",
				String.valueOf(product.getCatalogExternalReferenceCode()));
		}

		if (product.getCatalogId() == null) {
			map.put("catalogId", null);
		}
		else {
			map.put("catalogId", String.valueOf(product.getCatalogId()));
		}

		if (product.getCategories() == null) {
			map.put("categories", null);
		}
		else {
			map.put("categories", String.valueOf(product.getCategories()));
		}

		if (product.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(product.getCreateDate()));
		}

		if (product.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(product.getCustomFields()));
		}

		if (product.getDefaultSku() == null) {
			map.put("defaultSku", null);
		}
		else {
			map.put("defaultSku", String.valueOf(product.getDefaultSku()));
		}

		if (product.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(product.getDescription()));
		}

		if (product.getDiagram() == null) {
			map.put("diagram", null);
		}
		else {
			map.put("diagram", String.valueOf(product.getDiagram()));
		}

		if (product.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(product.getDisplayDate()));
		}

		if (product.getExpando() == null) {
			map.put("expando", null);
		}
		else {
			map.put("expando", String.valueOf(product.getExpando()));
		}

		if (product.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(product.getExpirationDate()));
		}

		if (product.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(product.getExternalReferenceCode()));
		}

		if (product.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(product.getId()));
		}

		if (product.getImages() == null) {
			map.put("images", null);
		}
		else {
			map.put("images", String.valueOf(product.getImages()));
		}

		if (product.getLinkedProducts() == null) {
			map.put("linkedProducts", null);
		}
		else {
			map.put(
				"linkedProducts", String.valueOf(product.getLinkedProducts()));
		}

		if (product.getMappedProducts() == null) {
			map.put("mappedProducts", null);
		}
		else {
			map.put(
				"mappedProducts", String.valueOf(product.getMappedProducts()));
		}

		if (product.getMetaDescription() == null) {
			map.put("metaDescription", null);
		}
		else {
			map.put(
				"metaDescription",
				String.valueOf(product.getMetaDescription()));
		}

		if (product.getMetaKeyword() == null) {
			map.put("metaKeyword", null);
		}
		else {
			map.put("metaKeyword", String.valueOf(product.getMetaKeyword()));
		}

		if (product.getMetaTitle() == null) {
			map.put("metaTitle", null);
		}
		else {
			map.put("metaTitle", String.valueOf(product.getMetaTitle()));
		}

		if (product.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(product.getModifiedDate()));
		}

		if (product.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(product.getName()));
		}

		if (product.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put("neverExpire", String.valueOf(product.getNeverExpire()));
		}

		if (product.getPins() == null) {
			map.put("pins", null);
		}
		else {
			map.put("pins", String.valueOf(product.getPins()));
		}

		if (product.getProductAccountGroupFilter() == null) {
			map.put("productAccountGroupFilter", null);
		}
		else {
			map.put(
				"productAccountGroupFilter",
				String.valueOf(product.getProductAccountGroupFilter()));
		}

		if (product.getProductAccountGroups() == null) {
			map.put("productAccountGroups", null);
		}
		else {
			map.put(
				"productAccountGroups",
				String.valueOf(product.getProductAccountGroups()));
		}

		if (product.getProductChannelFilter() == null) {
			map.put("productChannelFilter", null);
		}
		else {
			map.put(
				"productChannelFilter",
				String.valueOf(product.getProductChannelFilter()));
		}

		if (product.getProductChannels() == null) {
			map.put("productChannels", null);
		}
		else {
			map.put(
				"productChannels",
				String.valueOf(product.getProductChannels()));
		}

		if (product.getProductConfiguration() == null) {
			map.put("productConfiguration", null);
		}
		else {
			map.put(
				"productConfiguration",
				String.valueOf(product.getProductConfiguration()));
		}

		if (product.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put("productId", String.valueOf(product.getProductId()));
		}

		if (product.getProductOptions() == null) {
			map.put("productOptions", null);
		}
		else {
			map.put(
				"productOptions", String.valueOf(product.getProductOptions()));
		}

		if (product.getProductSpecifications() == null) {
			map.put("productSpecifications", null);
		}
		else {
			map.put(
				"productSpecifications",
				String.valueOf(product.getProductSpecifications()));
		}

		if (product.getProductStatus() == null) {
			map.put("productStatus", null);
		}
		else {
			map.put(
				"productStatus", String.valueOf(product.getProductStatus()));
		}

		if (product.getProductType() == null) {
			map.put("productType", null);
		}
		else {
			map.put("productType", String.valueOf(product.getProductType()));
		}

		if (product.getProductTypeI18n() == null) {
			map.put("productTypeI18n", null);
		}
		else {
			map.put(
				"productTypeI18n",
				String.valueOf(product.getProductTypeI18n()));
		}

		if (product.getProductVirtualSettings() == null) {
			map.put("productVirtualSettings", null);
		}
		else {
			map.put(
				"productVirtualSettings",
				String.valueOf(product.getProductVirtualSettings()));
		}

		if (product.getRelatedProducts() == null) {
			map.put("relatedProducts", null);
		}
		else {
			map.put(
				"relatedProducts",
				String.valueOf(product.getRelatedProducts()));
		}

		if (product.getShippingConfiguration() == null) {
			map.put("shippingConfiguration", null);
		}
		else {
			map.put(
				"shippingConfiguration",
				String.valueOf(product.getShippingConfiguration()));
		}

		if (product.getShortDescription() == null) {
			map.put("shortDescription", null);
		}
		else {
			map.put(
				"shortDescription",
				String.valueOf(product.getShortDescription()));
		}

		if (product.getSkuFormatted() == null) {
			map.put("skuFormatted", null);
		}
		else {
			map.put("skuFormatted", String.valueOf(product.getSkuFormatted()));
		}

		if (product.getSkus() == null) {
			map.put("skus", null);
		}
		else {
			map.put("skus", String.valueOf(product.getSkus()));
		}

		if (product.getSubscriptionConfiguration() == null) {
			map.put("subscriptionConfiguration", null);
		}
		else {
			map.put(
				"subscriptionConfiguration",
				String.valueOf(product.getSubscriptionConfiguration()));
		}

		if (product.getTags() == null) {
			map.put("tags", null);
		}
		else {
			map.put("tags", String.valueOf(product.getTags()));
		}

		if (product.getTaxConfiguration() == null) {
			map.put("taxConfiguration", null);
		}
		else {
			map.put(
				"taxConfiguration",
				String.valueOf(product.getTaxConfiguration()));
		}

		if (product.getThumbnail() == null) {
			map.put("thumbnail", null);
		}
		else {
			map.put("thumbnail", String.valueOf(product.getThumbnail()));
		}

		if (product.getUrls() == null) {
			map.put("urls", null);
		}
		else {
			map.put("urls", String.valueOf(product.getUrls()));
		}

		if (product.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put("version", String.valueOf(product.getVersion()));
		}

		if (product.getWorkflowStatusInfo() == null) {
			map.put("workflowStatusInfo", null);
		}
		else {
			map.put(
				"workflowStatusInfo",
				String.valueOf(product.getWorkflowStatusInfo()));
		}

		return map;
	}

	public static class ProductJSONParser extends BaseJSONParser<Product> {

		@Override
		protected Product createDTO() {
			return new Product();
		}

		@Override
		protected Product[] createDTOArray(int size) {
			return new Product[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "attachments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "catalog")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "categories")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultSku")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "diagram")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expando")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "images")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "linkedProducts")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mappedProducts")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "metaDescription")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "metaKeyword")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "metaTitle")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pins")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productAccountGroupFilter")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productAccountGroups")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productChannelFilter")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productChannels")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfiguration")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productOptions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productSpecifications")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productTypeI18n")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productVirtualSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "relatedProducts")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingConfiguration")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shortDescription")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "skuFormatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skus")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subscriptionConfiguration")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxConfiguration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowStatusInfo")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Product product, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					product.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					product.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "attachments")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Attachment[] attachmentsArray =
						new Attachment[jsonParserFieldValues.length];

					for (int i = 0; i < attachmentsArray.length; i++) {
						attachmentsArray[i] = AttachmentSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setAttachments(attachmentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "catalog")) {
				if (jsonParserFieldValue != null) {
					product.setCatalog(
						CatalogSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					product.setCatalogExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				if (jsonParserFieldValue != null) {
					product.setCatalogId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "categories")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Category[] categoriesArray =
						new Category[jsonParserFieldValues.length];

					for (int i = 0; i < categoriesArray.length; i++) {
						categoriesArray[i] = CategorySerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setCategories(categoriesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					product.setCreateDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.admin.catalog.client.custom.
						field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.admin.catalog.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.admin.catalog.client.
								custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					product.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultSku")) {
				if (jsonParserFieldValue != null) {
					product.setDefaultSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					product.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "diagram")) {
				if (jsonParserFieldValue != null) {
					product.setDiagram(
						DiagramSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					product.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expando")) {
				if (jsonParserFieldValue != null) {
					product.setExpando((Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					product.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					product.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					product.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "images")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Attachment[] imagesArray =
						new Attachment[jsonParserFieldValues.length];

					for (int i = 0; i < imagesArray.length; i++) {
						imagesArray[i] = AttachmentSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setImages(imagesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "linkedProducts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					LinkedProduct[] linkedProductsArray =
						new LinkedProduct[jsonParserFieldValues.length];

					for (int i = 0; i < linkedProductsArray.length; i++) {
						linkedProductsArray[i] = LinkedProductSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setLinkedProducts(linkedProductsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mappedProducts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MappedProduct[] mappedProductsArray =
						new MappedProduct[jsonParserFieldValues.length];

					for (int i = 0; i < mappedProductsArray.length; i++) {
						mappedProductsArray[i] = MappedProductSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setMappedProducts(mappedProductsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metaDescription")) {
				if (jsonParserFieldValue != null) {
					product.setMetaDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metaKeyword")) {
				if (jsonParserFieldValue != null) {
					product.setMetaKeyword(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metaTitle")) {
				if (jsonParserFieldValue != null) {
					product.setMetaTitle(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					product.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					product.setName((Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					product.setNeverExpire((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pins")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Pin[] pinsArray = new Pin[jsonParserFieldValues.length];

					for (int i = 0; i < pinsArray.length; i++) {
						pinsArray[i] = PinSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setPins(pinsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productAccountGroupFilter")) {

				if (jsonParserFieldValue != null) {
					product.setProductAccountGroupFilter(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productAccountGroups")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductAccountGroup[] productAccountGroupsArray =
						new ProductAccountGroup[jsonParserFieldValues.length];

					for (int i = 0; i < productAccountGroupsArray.length; i++) {
						productAccountGroupsArray[i] =
							ProductAccountGroupSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					product.setProductAccountGroups(productAccountGroupsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productChannelFilter")) {

				if (jsonParserFieldValue != null) {
					product.setProductChannelFilter(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productChannels")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductChannel[] productChannelsArray =
						new ProductChannel[jsonParserFieldValues.length];

					for (int i = 0; i < productChannelsArray.length; i++) {
						productChannelsArray[i] = ProductChannelSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setProductChannels(productChannelsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfiguration")) {

				if (jsonParserFieldValue != null) {
					product.setProductConfiguration(
						ProductConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					product.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productOptions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductOption[] productOptionsArray =
						new ProductOption[jsonParserFieldValues.length];

					for (int i = 0; i < productOptionsArray.length; i++) {
						productOptionsArray[i] = ProductOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setProductOptions(productOptionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productSpecifications")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductSpecification[] productSpecificationsArray =
						new ProductSpecification[jsonParserFieldValues.length];

					for (int i = 0; i < productSpecificationsArray.length;
						 i++) {

						productSpecificationsArray[i] =
							ProductSpecificationSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					product.setProductSpecifications(
						productSpecificationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productStatus")) {
				if (jsonParserFieldValue != null) {
					product.setProductStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productType")) {
				if (jsonParserFieldValue != null) {
					product.setProductType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productTypeI18n")) {
				if (jsonParserFieldValue != null) {
					product.setProductTypeI18n((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productVirtualSettings")) {

				if (jsonParserFieldValue != null) {
					product.setProductVirtualSettings(
						ProductVirtualSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "relatedProducts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RelatedProduct[] relatedProductsArray =
						new RelatedProduct[jsonParserFieldValues.length];

					for (int i = 0; i < relatedProductsArray.length; i++) {
						relatedProductsArray[i] = RelatedProductSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setRelatedProducts(relatedProductsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingConfiguration")) {

				if (jsonParserFieldValue != null) {
					product.setShippingConfiguration(
						ProductShippingConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shortDescription")) {
				if (jsonParserFieldValue != null) {
					product.setShortDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuFormatted")) {
				if (jsonParserFieldValue != null) {
					product.setSkuFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skus")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Sku[] skusArray = new Sku[jsonParserFieldValues.length];

					for (int i = 0; i < skusArray.length; i++) {
						skusArray[i] = SkuSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setSkus(skusArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subscriptionConfiguration")) {

				if (jsonParserFieldValue != null) {
					product.setSubscriptionConfiguration(
						ProductSubscriptionConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				if (jsonParserFieldValue != null) {
					product.setTags(toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxConfiguration")) {
				if (jsonParserFieldValue != null) {
					product.setTaxConfiguration(
						ProductTaxConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				if (jsonParserFieldValue != null) {
					product.setThumbnail((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				if (jsonParserFieldValue != null) {
					product.setUrls((Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					product.setVersion(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowStatusInfo")) {

				if (jsonParserFieldValue != null) {
					product.setWorkflowStatusInfo(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
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
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}