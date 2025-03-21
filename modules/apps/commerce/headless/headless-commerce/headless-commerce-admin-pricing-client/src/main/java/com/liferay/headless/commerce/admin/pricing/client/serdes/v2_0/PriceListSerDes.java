/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceList;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListAccount;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListAccountGroup;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListChannel;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListDiscount;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListOrderType;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class PriceListSerDes {

	public static PriceList toDTO(String json) {
		PriceListJSONParser priceListJSONParser = new PriceListJSONParser();

		return priceListJSONParser.parseToDTO(json);
	}

	public static PriceList[] toDTOs(String json) {
		PriceListJSONParser priceListJSONParser = new PriceListJSONParser();

		return priceListJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PriceList priceList) {
		if (priceList == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (priceList.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(priceList.getActions()));
		}

		if (priceList.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(priceList.getActive());
		}

		if (priceList.getAuthor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(priceList.getAuthor()));

			sb.append("\"");
		}

		if (priceList.getCatalogBasePriceList() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogBasePriceList\": ");

			sb.append(priceList.getCatalogBasePriceList());
		}

		if (priceList.getCatalogId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(priceList.getCatalogId());
		}

		if (priceList.getCatalogName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogName\": ");

			sb.append("\"");

			sb.append(_escape(priceList.getCatalogName()));

			sb.append("\"");
		}

		if (priceList.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(priceList.getCreateDate()));

			sb.append("\"");
		}

		if (priceList.getCurrencyCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(priceList.getCurrencyCode()));

			sb.append("\"");
		}

		if (priceList.getCurrencyExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceList.getCurrencyExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceList.getCurrencyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyId\": ");

			sb.append(priceList.getCurrencyId());
		}

		if (priceList.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(priceList.getCustomFields()));
		}

		if (priceList.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(priceList.getDisplayDate()));

			sb.append("\"");
		}

		if (priceList.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(priceList.getExpirationDate()));

			sb.append("\"");
		}

		if (priceList.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceList.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceList.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(priceList.getId());
		}

		if (priceList.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(priceList.getName()));

			sb.append("\"");
		}

		if (priceList.getNetPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"netPrice\": ");

			sb.append(priceList.getNetPrice());
		}

		if (priceList.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(priceList.getNeverExpire());
		}

		if (priceList.getParentPriceListId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentPriceListId\": ");

			sb.append(priceList.getParentPriceListId());
		}

		if (priceList.getPriceEntries() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceEntries\": ");

			sb.append("[");

			for (int i = 0; i < priceList.getPriceEntries().length; i++) {
				sb.append(String.valueOf(priceList.getPriceEntries()[i]));

				if ((i + 1) < priceList.getPriceEntries().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceList.getPriceListAccountGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListAccountGroups\": ");

			sb.append("[");

			for (int i = 0; i < priceList.getPriceListAccountGroups().length;
				 i++) {

				sb.append(
					String.valueOf(priceList.getPriceListAccountGroups()[i]));

				if ((i + 1) < priceList.getPriceListAccountGroups().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceList.getPriceListAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListAccounts\": ");

			sb.append("[");

			for (int i = 0; i < priceList.getPriceListAccounts().length; i++) {
				sb.append(String.valueOf(priceList.getPriceListAccounts()[i]));

				if ((i + 1) < priceList.getPriceListAccounts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceList.getPriceListChannels() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListChannels\": ");

			sb.append("[");

			for (int i = 0; i < priceList.getPriceListChannels().length; i++) {
				sb.append(String.valueOf(priceList.getPriceListChannels()[i]));

				if ((i + 1) < priceList.getPriceListChannels().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceList.getPriceListDiscounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListDiscounts\": ");

			sb.append("[");

			for (int i = 0; i < priceList.getPriceListDiscounts().length; i++) {
				sb.append(String.valueOf(priceList.getPriceListDiscounts()[i]));

				if ((i + 1) < priceList.getPriceListDiscounts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceList.getPriceListOrderTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListOrderTypes\": ");

			sb.append("[");

			for (int i = 0; i < priceList.getPriceListOrderTypes().length;
				 i++) {

				sb.append(
					String.valueOf(priceList.getPriceListOrderTypes()[i]));

				if ((i + 1) < priceList.getPriceListOrderTypes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceList.getPriceModifiers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifiers\": ");

			sb.append("[");

			for (int i = 0; i < priceList.getPriceModifiers().length; i++) {
				sb.append(String.valueOf(priceList.getPriceModifiers()[i]));

				if ((i + 1) < priceList.getPriceModifiers().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceList.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priceList.getPriority());
		}

		if (priceList.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(priceList.getType());

			sb.append("\"");
		}

		if (priceList.getWorkflowStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowStatusInfo\": ");

			sb.append(String.valueOf(priceList.getWorkflowStatusInfo()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PriceListJSONParser priceListJSONParser = new PriceListJSONParser();

		return priceListJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PriceList priceList) {
		if (priceList == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (priceList.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(priceList.getActions()));
		}

		if (priceList.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(priceList.getActive()));
		}

		if (priceList.getAuthor() == null) {
			map.put("author", null);
		}
		else {
			map.put("author", String.valueOf(priceList.getAuthor()));
		}

		if (priceList.getCatalogBasePriceList() == null) {
			map.put("catalogBasePriceList", null);
		}
		else {
			map.put(
				"catalogBasePriceList",
				String.valueOf(priceList.getCatalogBasePriceList()));
		}

		if (priceList.getCatalogId() == null) {
			map.put("catalogId", null);
		}
		else {
			map.put("catalogId", String.valueOf(priceList.getCatalogId()));
		}

		if (priceList.getCatalogName() == null) {
			map.put("catalogName", null);
		}
		else {
			map.put("catalogName", String.valueOf(priceList.getCatalogName()));
		}

		if (priceList.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(priceList.getCreateDate()));
		}

		if (priceList.getCurrencyCode() == null) {
			map.put("currencyCode", null);
		}
		else {
			map.put(
				"currencyCode", String.valueOf(priceList.getCurrencyCode()));
		}

		if (priceList.getCurrencyExternalReferenceCode() == null) {
			map.put("currencyExternalReferenceCode", null);
		}
		else {
			map.put(
				"currencyExternalReferenceCode",
				String.valueOf(priceList.getCurrencyExternalReferenceCode()));
		}

		if (priceList.getCurrencyId() == null) {
			map.put("currencyId", null);
		}
		else {
			map.put("currencyId", String.valueOf(priceList.getCurrencyId()));
		}

		if (priceList.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(priceList.getCustomFields()));
		}

		if (priceList.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(priceList.getDisplayDate()));
		}

		if (priceList.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(priceList.getExpirationDate()));
		}

		if (priceList.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(priceList.getExternalReferenceCode()));
		}

		if (priceList.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(priceList.getId()));
		}

		if (priceList.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(priceList.getName()));
		}

		if (priceList.getNetPrice() == null) {
			map.put("netPrice", null);
		}
		else {
			map.put("netPrice", String.valueOf(priceList.getNetPrice()));
		}

		if (priceList.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put("neverExpire", String.valueOf(priceList.getNeverExpire()));
		}

		if (priceList.getParentPriceListId() == null) {
			map.put("parentPriceListId", null);
		}
		else {
			map.put(
				"parentPriceListId",
				String.valueOf(priceList.getParentPriceListId()));
		}

		if (priceList.getPriceEntries() == null) {
			map.put("priceEntries", null);
		}
		else {
			map.put(
				"priceEntries", String.valueOf(priceList.getPriceEntries()));
		}

		if (priceList.getPriceListAccountGroups() == null) {
			map.put("priceListAccountGroups", null);
		}
		else {
			map.put(
				"priceListAccountGroups",
				String.valueOf(priceList.getPriceListAccountGroups()));
		}

		if (priceList.getPriceListAccounts() == null) {
			map.put("priceListAccounts", null);
		}
		else {
			map.put(
				"priceListAccounts",
				String.valueOf(priceList.getPriceListAccounts()));
		}

		if (priceList.getPriceListChannels() == null) {
			map.put("priceListChannels", null);
		}
		else {
			map.put(
				"priceListChannels",
				String.valueOf(priceList.getPriceListChannels()));
		}

		if (priceList.getPriceListDiscounts() == null) {
			map.put("priceListDiscounts", null);
		}
		else {
			map.put(
				"priceListDiscounts",
				String.valueOf(priceList.getPriceListDiscounts()));
		}

		if (priceList.getPriceListOrderTypes() == null) {
			map.put("priceListOrderTypes", null);
		}
		else {
			map.put(
				"priceListOrderTypes",
				String.valueOf(priceList.getPriceListOrderTypes()));
		}

		if (priceList.getPriceModifiers() == null) {
			map.put("priceModifiers", null);
		}
		else {
			map.put(
				"priceModifiers",
				String.valueOf(priceList.getPriceModifiers()));
		}

		if (priceList.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(priceList.getPriority()));
		}

		if (priceList.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(priceList.getType()));
		}

		if (priceList.getWorkflowStatusInfo() == null) {
			map.put("workflowStatusInfo", null);
		}
		else {
			map.put(
				"workflowStatusInfo",
				String.valueOf(priceList.getWorkflowStatusInfo()));
		}

		return map;
	}

	public static class PriceListJSONParser extends BaseJSONParser<PriceList> {

		@Override
		protected PriceList createDTO() {
			return new PriceList();
		}

		@Override
		protected PriceList[] createDTOArray(int size) {
			return new PriceList[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogBasePriceList")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "catalogName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				return false;
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
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "netPrice")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentPriceListId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceEntries")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceListAccountGroups")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceListAccounts")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceListChannels")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceListDiscounts")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceListOrderTypes")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifiers")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
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
			PriceList priceList, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					priceList.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					priceList.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				if (jsonParserFieldValue != null) {
					priceList.setAuthor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogBasePriceList")) {

				if (jsonParserFieldValue != null) {
					priceList.setCatalogBasePriceList(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				if (jsonParserFieldValue != null) {
					priceList.setCatalogId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "catalogName")) {
				if (jsonParserFieldValue != null) {
					priceList.setCatalogName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					priceList.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				if (jsonParserFieldValue != null) {
					priceList.setCurrencyCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceList.setCurrencyExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				if (jsonParserFieldValue != null) {
					priceList.setCurrencyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					priceList.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					priceList.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					priceList.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceList.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					priceList.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					priceList.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "netPrice")) {
				if (jsonParserFieldValue != null) {
					priceList.setNetPrice((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					priceList.setNeverExpire((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentPriceListId")) {
				if (jsonParserFieldValue != null) {
					priceList.setParentPriceListId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceEntries")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceEntry[] priceEntriesArray =
						new PriceEntry[jsonParserFieldValues.length];

					for (int i = 0; i < priceEntriesArray.length; i++) {
						priceEntriesArray[i] = PriceEntrySerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					priceList.setPriceEntries(priceEntriesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceListAccountGroups")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceListAccountGroup[] priceListAccountGroupsArray =
						new PriceListAccountGroup[jsonParserFieldValues.length];

					for (int i = 0; i < priceListAccountGroupsArray.length;
						 i++) {

						priceListAccountGroupsArray[i] =
							PriceListAccountGroupSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceList.setPriceListAccountGroups(
						priceListAccountGroupsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceListAccounts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceListAccount[] priceListAccountsArray =
						new PriceListAccount[jsonParserFieldValues.length];

					for (int i = 0; i < priceListAccountsArray.length; i++) {
						priceListAccountsArray[i] =
							PriceListAccountSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceList.setPriceListAccounts(priceListAccountsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceListChannels")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceListChannel[] priceListChannelsArray =
						new PriceListChannel[jsonParserFieldValues.length];

					for (int i = 0; i < priceListChannelsArray.length; i++) {
						priceListChannelsArray[i] =
							PriceListChannelSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceList.setPriceListChannels(priceListChannelsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceListDiscounts")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceListDiscount[] priceListDiscountsArray =
						new PriceListDiscount[jsonParserFieldValues.length];

					for (int i = 0; i < priceListDiscountsArray.length; i++) {
						priceListDiscountsArray[i] =
							PriceListDiscountSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceList.setPriceListDiscounts(priceListDiscountsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceListOrderTypes")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceListOrderType[] priceListOrderTypesArray =
						new PriceListOrderType[jsonParserFieldValues.length];

					for (int i = 0; i < priceListOrderTypesArray.length; i++) {
						priceListOrderTypesArray[i] =
							PriceListOrderTypeSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceList.setPriceListOrderTypes(priceListOrderTypesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifiers")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceModifier[] priceModifiersArray =
						new PriceModifier[jsonParserFieldValues.length];

					for (int i = 0; i < priceModifiersArray.length; i++) {
						priceModifiersArray[i] = PriceModifierSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					priceList.setPriceModifiers(priceModifiersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					priceList.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					priceList.setType(
						PriceList.Type.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowStatusInfo")) {

				if (jsonParserFieldValue != null) {
					priceList.setWorkflowStatusInfo(
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