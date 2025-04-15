/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.DDMOption;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.TierPrice;
import com.liferay.headless.commerce.delivery.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class SkuSerDes {

	public static Sku toDTO(String json) {
		SkuJSONParser skuJSONParser = new SkuJSONParser();

		return skuJSONParser.parseToDTO(json);
	}

	public static Sku[] toDTOs(String json) {
		SkuJSONParser skuJSONParser = new SkuJSONParser();

		return skuJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Sku sku) {
		if (sku == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sku.getDDMOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"DDMOptions\": ");

			sb.append("[");

			for (int i = 0; i < sku.getDDMOptions().length; i++) {
				sb.append(String.valueOf(sku.getDDMOptions()[i]));

				if ((i + 1) < sku.getDDMOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sku.getAllowedOrderQuantities() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowedOrderQuantities\": ");

			sb.append("[");

			for (int i = 0; i < sku.getAllowedOrderQuantities().length; i++) {
				sb.append(_toJSON(sku.getAllowedOrderQuantities()[i]));

				if ((i + 1) < sku.getAllowedOrderQuantities().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sku.getAvailability() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availability\": ");

			sb.append(String.valueOf(sku.getAvailability()));
		}

		if (sku.getBackOrderAllowed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backOrderAllowed\": ");

			sb.append(sku.getBackOrderAllowed());
		}

		if (sku.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < sku.getCustomFields().length; i++) {
				sb.append(sku.getCustomFields()[i]);

				if ((i + 1) < sku.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sku.getDepth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"depth\": ");

			sb.append(sku.getDepth());
		}

		if (sku.getDiscontinued() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discontinued\": ");

			sb.append(sku.getDiscontinued());
		}

		if (sku.getDiscontinuedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discontinuedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sku.getDiscontinuedDate()));

			sb.append("\"");
		}

		if (sku.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(sku.getDisplayDate()));

			sb.append("\"");
		}

		if (sku.getDisplayDiscountLevels() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDiscountLevels\": ");

			sb.append(sku.getDisplayDiscountLevels());
		}

		if (sku.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(sku.getExpirationDate()));

			sb.append("\"");
		}

		if (sku.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(sku.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (sku.getGtin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gtin\": ");

			sb.append("\"");

			sb.append(_escape(sku.getGtin()));

			sb.append("\"");
		}

		if (sku.getHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append(sku.getHeight());
		}

		if (sku.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(sku.getId());
		}

		if (sku.getIncomingQuantityLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"incomingQuantityLabel\": ");

			sb.append("\"");

			sb.append(_escape(sku.getIncomingQuantityLabel()));

			sb.append("\"");
		}

		if (sku.getManufacturerPartNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"manufacturerPartNumber\": ");

			sb.append("\"");

			sb.append(_escape(sku.getManufacturerPartNumber()));

			sb.append("\"");
		}

		if (sku.getMaxOrderQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxOrderQuantity\": ");

			sb.append(sku.getMaxOrderQuantity());
		}

		if (sku.getMinOrderQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minOrderQuantity\": ");

			sb.append(sku.getMinOrderQuantity());
		}

		if (sku.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(sku.getNeverExpire());
		}

		if (sku.getPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(String.valueOf(sku.getPrice()));
		}

		if (sku.getProductConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfiguration\": ");

			sb.append(String.valueOf(sku.getProductConfiguration()));
		}

		if (sku.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(sku.getProductId());
		}

		if (sku.getPublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"published\": ");

			sb.append(sku.getPublished());
		}

		if (sku.getPurchasable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchasable\": ");

			sb.append(sku.getPurchasable());
		}

		if (sku.getReplacementSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementSku\": ");

			sb.append(String.valueOf(sku.getReplacementSku()));
		}

		if (sku.getReplacementSkuExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementSkuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(sku.getReplacementSkuExternalReferenceCode()));

			sb.append("\"");
		}

		if (sku.getReplacementSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementSkuId\": ");

			sb.append(sku.getReplacementSkuId());
		}

		if (sku.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(sku.getSku()));

			sb.append("\"");
		}

		if (sku.getSkuOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptions\": ");

			sb.append("[");

			for (int i = 0; i < sku.getSkuOptions().length; i++) {
				sb.append(String.valueOf(sku.getSkuOptions()[i]));

				if ((i + 1) < sku.getSkuOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sku.getSkuUnitOfMeasures() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuUnitOfMeasures\": ");

			sb.append("[");

			for (int i = 0; i < sku.getSkuUnitOfMeasures().length; i++) {
				sb.append(String.valueOf(sku.getSkuUnitOfMeasures()[i]));

				if ((i + 1) < sku.getSkuUnitOfMeasures().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sku.getTierPrices() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tierPrices\": ");

			sb.append("[");

			for (int i = 0; i < sku.getTierPrices().length; i++) {
				sb.append(String.valueOf(sku.getTierPrices()[i]));

				if ((i + 1) < sku.getTierPrices().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sku.getWeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"weight\": ");

			sb.append(sku.getWeight());
		}

		if (sku.getWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append(sku.getWidth());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SkuJSONParser skuJSONParser = new SkuJSONParser();

		return skuJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Sku sku) {
		if (sku == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sku.getDDMOptions() == null) {
			map.put("DDMOptions", null);
		}
		else {
			map.put("DDMOptions", String.valueOf(sku.getDDMOptions()));
		}

		if (sku.getAllowedOrderQuantities() == null) {
			map.put("allowedOrderQuantities", null);
		}
		else {
			map.put(
				"allowedOrderQuantities",
				String.valueOf(sku.getAllowedOrderQuantities()));
		}

		if (sku.getAvailability() == null) {
			map.put("availability", null);
		}
		else {
			map.put("availability", String.valueOf(sku.getAvailability()));
		}

		if (sku.getBackOrderAllowed() == null) {
			map.put("backOrderAllowed", null);
		}
		else {
			map.put(
				"backOrderAllowed", String.valueOf(sku.getBackOrderAllowed()));
		}

		if (sku.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(sku.getCustomFields()));
		}

		if (sku.getDepth() == null) {
			map.put("depth", null);
		}
		else {
			map.put("depth", String.valueOf(sku.getDepth()));
		}

		if (sku.getDiscontinued() == null) {
			map.put("discontinued", null);
		}
		else {
			map.put("discontinued", String.valueOf(sku.getDiscontinued()));
		}

		if (sku.getDiscontinuedDate() == null) {
			map.put("discontinuedDate", null);
		}
		else {
			map.put(
				"discontinuedDate",
				liferayToJSONDateFormat.format(sku.getDiscontinuedDate()));
		}

		if (sku.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(sku.getDisplayDate()));
		}

		if (sku.getDisplayDiscountLevels() == null) {
			map.put("displayDiscountLevels", null);
		}
		else {
			map.put(
				"displayDiscountLevels",
				String.valueOf(sku.getDisplayDiscountLevels()));
		}

		if (sku.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(sku.getExpirationDate()));
		}

		if (sku.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(sku.getExternalReferenceCode()));
		}

		if (sku.getGtin() == null) {
			map.put("gtin", null);
		}
		else {
			map.put("gtin", String.valueOf(sku.getGtin()));
		}

		if (sku.getHeight() == null) {
			map.put("height", null);
		}
		else {
			map.put("height", String.valueOf(sku.getHeight()));
		}

		if (sku.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(sku.getId()));
		}

		if (sku.getIncomingQuantityLabel() == null) {
			map.put("incomingQuantityLabel", null);
		}
		else {
			map.put(
				"incomingQuantityLabel",
				String.valueOf(sku.getIncomingQuantityLabel()));
		}

		if (sku.getManufacturerPartNumber() == null) {
			map.put("manufacturerPartNumber", null);
		}
		else {
			map.put(
				"manufacturerPartNumber",
				String.valueOf(sku.getManufacturerPartNumber()));
		}

		if (sku.getMaxOrderQuantity() == null) {
			map.put("maxOrderQuantity", null);
		}
		else {
			map.put(
				"maxOrderQuantity", String.valueOf(sku.getMaxOrderQuantity()));
		}

		if (sku.getMinOrderQuantity() == null) {
			map.put("minOrderQuantity", null);
		}
		else {
			map.put(
				"minOrderQuantity", String.valueOf(sku.getMinOrderQuantity()));
		}

		if (sku.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put("neverExpire", String.valueOf(sku.getNeverExpire()));
		}

		if (sku.getPrice() == null) {
			map.put("price", null);
		}
		else {
			map.put("price", String.valueOf(sku.getPrice()));
		}

		if (sku.getProductConfiguration() == null) {
			map.put("productConfiguration", null);
		}
		else {
			map.put(
				"productConfiguration",
				String.valueOf(sku.getProductConfiguration()));
		}

		if (sku.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put("productId", String.valueOf(sku.getProductId()));
		}

		if (sku.getPublished() == null) {
			map.put("published", null);
		}
		else {
			map.put("published", String.valueOf(sku.getPublished()));
		}

		if (sku.getPurchasable() == null) {
			map.put("purchasable", null);
		}
		else {
			map.put("purchasable", String.valueOf(sku.getPurchasable()));
		}

		if (sku.getReplacementSku() == null) {
			map.put("replacementSku", null);
		}
		else {
			map.put("replacementSku", String.valueOf(sku.getReplacementSku()));
		}

		if (sku.getReplacementSkuExternalReferenceCode() == null) {
			map.put("replacementSkuExternalReferenceCode", null);
		}
		else {
			map.put(
				"replacementSkuExternalReferenceCode",
				String.valueOf(sku.getReplacementSkuExternalReferenceCode()));
		}

		if (sku.getReplacementSkuId() == null) {
			map.put("replacementSkuId", null);
		}
		else {
			map.put(
				"replacementSkuId", String.valueOf(sku.getReplacementSkuId()));
		}

		if (sku.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(sku.getSku()));
		}

		if (sku.getSkuOptions() == null) {
			map.put("skuOptions", null);
		}
		else {
			map.put("skuOptions", String.valueOf(sku.getSkuOptions()));
		}

		if (sku.getSkuUnitOfMeasures() == null) {
			map.put("skuUnitOfMeasures", null);
		}
		else {
			map.put(
				"skuUnitOfMeasures",
				String.valueOf(sku.getSkuUnitOfMeasures()));
		}

		if (sku.getTierPrices() == null) {
			map.put("tierPrices", null);
		}
		else {
			map.put("tierPrices", String.valueOf(sku.getTierPrices()));
		}

		if (sku.getWeight() == null) {
			map.put("weight", null);
		}
		else {
			map.put("weight", String.valueOf(sku.getWeight()));
		}

		if (sku.getWidth() == null) {
			map.put("width", null);
		}
		else {
			map.put("width", String.valueOf(sku.getWidth()));
		}

		return map;
	}

	public static class SkuJSONParser extends BaseJSONParser<Sku> {

		@Override
		protected Sku createDTO() {
			return new Sku();
		}

		@Override
		protected Sku[] createDTOArray(int size) {
			return new Sku[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "DDMOptions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "allowedOrderQuantities")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "availability")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "backOrderAllowed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "depth")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discontinued")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discontinuedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "displayDiscountLevels")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "gtin")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "height")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "incomingQuantityLabel")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "manufacturerPartNumber")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxOrderQuantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "minOrderQuantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfiguration")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "published")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "purchasable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacementSku")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"replacementSkuExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacementSkuId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuOptions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuUnitOfMeasures")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tierPrices")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "weight")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "width")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Sku sku, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "DDMOptions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DDMOption[] DDMOptionsArray =
						new DDMOption[jsonParserFieldValues.length];

					for (int i = 0; i < DDMOptionsArray.length; i++) {
						DDMOptionsArray[i] = DDMOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					sku.setDDMOptions(DDMOptionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "allowedOrderQuantities")) {

				if (jsonParserFieldValue != null) {
					sku.setAllowedOrderQuantities(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "availability")) {
				if (jsonParserFieldValue != null) {
					sku.setAvailability(
						AvailabilitySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "backOrderAllowed")) {
				if (jsonParserFieldValue != null) {
					sku.setBackOrderAllowed((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.delivery.catalog.client.
						custom.field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.delivery.catalog.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.delivery.catalog.
								client.custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					sku.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "depth")) {
				if (jsonParserFieldValue != null) {
					sku.setDepth(Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discontinued")) {
				if (jsonParserFieldValue != null) {
					sku.setDiscontinued((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discontinuedDate")) {
				if (jsonParserFieldValue != null) {
					sku.setDiscontinuedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					sku.setDisplayDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "displayDiscountLevels")) {

				if (jsonParserFieldValue != null) {
					sku.setDisplayDiscountLevels((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					sku.setExpirationDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sku.setExternalReferenceCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "gtin")) {
				if (jsonParserFieldValue != null) {
					sku.setGtin((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "height")) {
				if (jsonParserFieldValue != null) {
					sku.setHeight(Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					sku.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "incomingQuantityLabel")) {

				if (jsonParserFieldValue != null) {
					sku.setIncomingQuantityLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "manufacturerPartNumber")) {

				if (jsonParserFieldValue != null) {
					sku.setManufacturerPartNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxOrderQuantity")) {
				if (jsonParserFieldValue != null) {
					sku.setMaxOrderQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minOrderQuantity")) {
				if (jsonParserFieldValue != null) {
					sku.setMinOrderQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					sku.setNeverExpire((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				if (jsonParserFieldValue != null) {
					sku.setPrice(
						PriceSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfiguration")) {

				if (jsonParserFieldValue != null) {
					sku.setProductConfiguration(
						ProductConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					sku.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "published")) {
				if (jsonParserFieldValue != null) {
					sku.setPublished((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "purchasable")) {
				if (jsonParserFieldValue != null) {
					sku.setPurchasable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacementSku")) {
				if (jsonParserFieldValue != null) {
					sku.setReplacementSku(
						ReplacementSkuSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"replacementSkuExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sku.setReplacementSkuExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacementSkuId")) {
				if (jsonParserFieldValue != null) {
					sku.setReplacementSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					sku.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuOptions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					SkuOption[] skuOptionsArray =
						new SkuOption[jsonParserFieldValues.length];

					for (int i = 0; i < skuOptionsArray.length; i++) {
						skuOptionsArray[i] = SkuOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					sku.setSkuOptions(skuOptionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuUnitOfMeasures")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					SkuUnitOfMeasure[] skuUnitOfMeasuresArray =
						new SkuUnitOfMeasure[jsonParserFieldValues.length];

					for (int i = 0; i < skuUnitOfMeasuresArray.length; i++) {
						skuUnitOfMeasuresArray[i] =
							SkuUnitOfMeasureSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					sku.setSkuUnitOfMeasures(skuUnitOfMeasuresArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tierPrices")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TierPrice[] tierPricesArray =
						new TierPrice[jsonParserFieldValues.length];

					for (int i = 0; i < tierPricesArray.length; i++) {
						tierPricesArray[i] = TierPriceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					sku.setTierPrices(tierPricesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "weight")) {
				if (jsonParserFieldValue != null) {
					sku.setWeight(Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "width")) {
				if (jsonParserFieldValue != null) {
					sku.setWidth(Double.valueOf((String)jsonParserFieldValue));
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