/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.Discount;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountAccount;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountAccountGroup;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountCategory;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountChannel;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountOrderType;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountProductGroup;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountRule;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class DiscountSerDes {

	public static Discount toDTO(String json) {
		DiscountJSONParser discountJSONParser = new DiscountJSONParser();

		return discountJSONParser.parseToDTO(json);
	}

	public static Discount[] toDTOs(String json) {
		DiscountJSONParser discountJSONParser = new DiscountJSONParser();

		return discountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Discount discount) {
		if (discount == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (discount.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(discount.getActions()));
		}

		if (discount.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(discount.getActive());
		}

		if (discount.getAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(discount.getAmountFormatted()));

			sb.append("\"");
		}

		if (discount.getCouponCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"couponCode\": ");

			sb.append("\"");

			sb.append(_escape(discount.getCouponCode()));

			sb.append("\"");
		}

		if (discount.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(discount.getCustomFields()));
		}

		if (discount.getDiscountAccountGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAccountGroups\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountAccountGroups().length;
				 i++) {

				sb.append(
					String.valueOf(discount.getDiscountAccountGroups()[i]));

				if ((i + 1) < discount.getDiscountAccountGroups().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDiscountAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAccounts\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountAccounts().length; i++) {
				sb.append(String.valueOf(discount.getDiscountAccounts()[i]));

				if ((i + 1) < discount.getDiscountAccounts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDiscountCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountCategories\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountCategories().length; i++) {
				sb.append(String.valueOf(discount.getDiscountCategories()[i]));

				if ((i + 1) < discount.getDiscountCategories().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDiscountChannels() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountChannels\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountChannels().length; i++) {
				sb.append(String.valueOf(discount.getDiscountChannels()[i]));

				if ((i + 1) < discount.getDiscountChannels().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDiscountOrderTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountOrderTypes\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountOrderTypes().length; i++) {
				sb.append(String.valueOf(discount.getDiscountOrderTypes()[i]));

				if ((i + 1) < discount.getDiscountOrderTypes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDiscountProductGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountProductGroups\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountProductGroups().length;
				 i++) {

				sb.append(
					String.valueOf(discount.getDiscountProductGroups()[i]));

				if ((i + 1) < discount.getDiscountProductGroups().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDiscountProducts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountProducts\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountProducts().length; i++) {
				sb.append(String.valueOf(discount.getDiscountProducts()[i]));

				if ((i + 1) < discount.getDiscountProducts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDiscountRules() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountRules\": ");

			sb.append("[");

			for (int i = 0; i < discount.getDiscountRules().length; i++) {
				sb.append(String.valueOf(discount.getDiscountRules()[i]));

				if ((i + 1) < discount.getDiscountRules().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (discount.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(discount.getDisplayDate()));

			sb.append("\"");
		}

		if (discount.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(discount.getExpirationDate()));

			sb.append("\"");
		}

		if (discount.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(discount.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (discount.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(discount.getId());
		}

		if (discount.getLevel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"level\": ");

			sb.append("\"");

			sb.append(_escape(discount.getLevel()));

			sb.append("\"");
		}

		if (discount.getLimitationTimes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"limitationTimes\": ");

			sb.append(discount.getLimitationTimes());
		}

		if (discount.getLimitationTimesPerAccount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"limitationTimesPerAccount\": ");

			sb.append(discount.getLimitationTimesPerAccount());
		}

		if (discount.getLimitationType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"limitationType\": ");

			sb.append("\"");

			sb.append(_escape(discount.getLimitationType()));

			sb.append("\"");
		}

		if (discount.getMaximumDiscountAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maximumDiscountAmount\": ");

			sb.append(discount.getMaximumDiscountAmount());
		}

		if (discount.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(discount.getModifiedDate()));

			sb.append("\"");
		}

		if (discount.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(discount.getNeverExpire());
		}

		if (discount.getNumberOfUse() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUse\": ");

			sb.append(discount.getNumberOfUse());
		}

		if (discount.getPercentageLevel1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel1\": ");

			sb.append(discount.getPercentageLevel1());
		}

		if (discount.getPercentageLevel2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel2\": ");

			sb.append(discount.getPercentageLevel2());
		}

		if (discount.getPercentageLevel3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel3\": ");

			sb.append(discount.getPercentageLevel3());
		}

		if (discount.getPercentageLevel4() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentageLevel4\": ");

			sb.append(discount.getPercentageLevel4());
		}

		if (discount.getRulesConjunction() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rulesConjunction\": ");

			sb.append(discount.getRulesConjunction());
		}

		if (discount.getTarget() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(discount.getTarget()));

			sb.append("\"");
		}

		if (discount.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(discount.getTitle()));

			sb.append("\"");
		}

		if (discount.getUseCouponCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCouponCode\": ");

			sb.append(discount.getUseCouponCode());
		}

		if (discount.getUsePercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"usePercentage\": ");

			sb.append(discount.getUsePercentage());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountJSONParser discountJSONParser = new DiscountJSONParser();

		return discountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Discount discount) {
		if (discount == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (discount.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(discount.getActions()));
		}

		if (discount.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(discount.getActive()));
		}

		if (discount.getAmountFormatted() == null) {
			map.put("amountFormatted", null);
		}
		else {
			map.put(
				"amountFormatted",
				String.valueOf(discount.getAmountFormatted()));
		}

		if (discount.getCouponCode() == null) {
			map.put("couponCode", null);
		}
		else {
			map.put("couponCode", String.valueOf(discount.getCouponCode()));
		}

		if (discount.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(discount.getCustomFields()));
		}

		if (discount.getDiscountAccountGroups() == null) {
			map.put("discountAccountGroups", null);
		}
		else {
			map.put(
				"discountAccountGroups",
				String.valueOf(discount.getDiscountAccountGroups()));
		}

		if (discount.getDiscountAccounts() == null) {
			map.put("discountAccounts", null);
		}
		else {
			map.put(
				"discountAccounts",
				String.valueOf(discount.getDiscountAccounts()));
		}

		if (discount.getDiscountCategories() == null) {
			map.put("discountCategories", null);
		}
		else {
			map.put(
				"discountCategories",
				String.valueOf(discount.getDiscountCategories()));
		}

		if (discount.getDiscountChannels() == null) {
			map.put("discountChannels", null);
		}
		else {
			map.put(
				"discountChannels",
				String.valueOf(discount.getDiscountChannels()));
		}

		if (discount.getDiscountOrderTypes() == null) {
			map.put("discountOrderTypes", null);
		}
		else {
			map.put(
				"discountOrderTypes",
				String.valueOf(discount.getDiscountOrderTypes()));
		}

		if (discount.getDiscountProductGroups() == null) {
			map.put("discountProductGroups", null);
		}
		else {
			map.put(
				"discountProductGroups",
				String.valueOf(discount.getDiscountProductGroups()));
		}

		if (discount.getDiscountProducts() == null) {
			map.put("discountProducts", null);
		}
		else {
			map.put(
				"discountProducts",
				String.valueOf(discount.getDiscountProducts()));
		}

		if (discount.getDiscountRules() == null) {
			map.put("discountRules", null);
		}
		else {
			map.put(
				"discountRules", String.valueOf(discount.getDiscountRules()));
		}

		if (discount.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(discount.getDisplayDate()));
		}

		if (discount.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(discount.getExpirationDate()));
		}

		if (discount.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(discount.getExternalReferenceCode()));
		}

		if (discount.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(discount.getId()));
		}

		if (discount.getLevel() == null) {
			map.put("level", null);
		}
		else {
			map.put("level", String.valueOf(discount.getLevel()));
		}

		if (discount.getLimitationTimes() == null) {
			map.put("limitationTimes", null);
		}
		else {
			map.put(
				"limitationTimes",
				String.valueOf(discount.getLimitationTimes()));
		}

		if (discount.getLimitationTimesPerAccount() == null) {
			map.put("limitationTimesPerAccount", null);
		}
		else {
			map.put(
				"limitationTimesPerAccount",
				String.valueOf(discount.getLimitationTimesPerAccount()));
		}

		if (discount.getLimitationType() == null) {
			map.put("limitationType", null);
		}
		else {
			map.put(
				"limitationType", String.valueOf(discount.getLimitationType()));
		}

		if (discount.getMaximumDiscountAmount() == null) {
			map.put("maximumDiscountAmount", null);
		}
		else {
			map.put(
				"maximumDiscountAmount",
				String.valueOf(discount.getMaximumDiscountAmount()));
		}

		if (discount.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(discount.getModifiedDate()));
		}

		if (discount.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put("neverExpire", String.valueOf(discount.getNeverExpire()));
		}

		if (discount.getNumberOfUse() == null) {
			map.put("numberOfUse", null);
		}
		else {
			map.put("numberOfUse", String.valueOf(discount.getNumberOfUse()));
		}

		if (discount.getPercentageLevel1() == null) {
			map.put("percentageLevel1", null);
		}
		else {
			map.put(
				"percentageLevel1",
				String.valueOf(discount.getPercentageLevel1()));
		}

		if (discount.getPercentageLevel2() == null) {
			map.put("percentageLevel2", null);
		}
		else {
			map.put(
				"percentageLevel2",
				String.valueOf(discount.getPercentageLevel2()));
		}

		if (discount.getPercentageLevel3() == null) {
			map.put("percentageLevel3", null);
		}
		else {
			map.put(
				"percentageLevel3",
				String.valueOf(discount.getPercentageLevel3()));
		}

		if (discount.getPercentageLevel4() == null) {
			map.put("percentageLevel4", null);
		}
		else {
			map.put(
				"percentageLevel4",
				String.valueOf(discount.getPercentageLevel4()));
		}

		if (discount.getRulesConjunction() == null) {
			map.put("rulesConjunction", null);
		}
		else {
			map.put(
				"rulesConjunction",
				String.valueOf(discount.getRulesConjunction()));
		}

		if (discount.getTarget() == null) {
			map.put("target", null);
		}
		else {
			map.put("target", String.valueOf(discount.getTarget()));
		}

		if (discount.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(discount.getTitle()));
		}

		if (discount.getUseCouponCode() == null) {
			map.put("useCouponCode", null);
		}
		else {
			map.put(
				"useCouponCode", String.valueOf(discount.getUseCouponCode()));
		}

		if (discount.getUsePercentage() == null) {
			map.put("usePercentage", null);
		}
		else {
			map.put(
				"usePercentage", String.valueOf(discount.getUsePercentage()));
		}

		return map;
	}

	public static class DiscountJSONParser extends BaseJSONParser<Discount> {

		@Override
		protected Discount createDTO() {
			return new Discount();
		}

		@Override
		protected Discount[] createDTOArray(int size) {
			return new Discount[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "amountFormatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "couponCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountAccountGroups")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountAccounts")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountCategories")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountChannels")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountOrderTypes")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountProductGroups")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountProducts")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountRules")) {
				return false;
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
			else if (Objects.equals(jsonParserFieldName, "level")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "limitationTimes")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "limitationTimesPerAccount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "limitationType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "maximumDiscountAmount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfUse")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel1")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel2")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel3")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel4")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rulesConjunction")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "target")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "useCouponCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "usePercentage")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Discount discount, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					discount.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					discount.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "amountFormatted")) {
				if (jsonParserFieldValue != null) {
					discount.setAmountFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "couponCode")) {
				if (jsonParserFieldValue != null) {
					discount.setCouponCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					discount.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountAccountGroups")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountAccountGroup[] discountAccountGroupsArray =
						new DiscountAccountGroup[jsonParserFieldValues.length];

					for (int i = 0; i < discountAccountGroupsArray.length;
						 i++) {

						discountAccountGroupsArray[i] =
							DiscountAccountGroupSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountAccountGroups(
						discountAccountGroupsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountAccounts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountAccount[] discountAccountsArray =
						new DiscountAccount[jsonParserFieldValues.length];

					for (int i = 0; i < discountAccountsArray.length; i++) {
						discountAccountsArray[i] = DiscountAccountSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountAccounts(discountAccountsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountCategories")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountCategory[] discountCategoriesArray =
						new DiscountCategory[jsonParserFieldValues.length];

					for (int i = 0; i < discountCategoriesArray.length; i++) {
						discountCategoriesArray[i] =
							DiscountCategorySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountCategories(discountCategoriesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountChannels")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountChannel[] discountChannelsArray =
						new DiscountChannel[jsonParserFieldValues.length];

					for (int i = 0; i < discountChannelsArray.length; i++) {
						discountChannelsArray[i] = DiscountChannelSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountChannels(discountChannelsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountOrderTypes")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountOrderType[] discountOrderTypesArray =
						new DiscountOrderType[jsonParserFieldValues.length];

					for (int i = 0; i < discountOrderTypesArray.length; i++) {
						discountOrderTypesArray[i] =
							DiscountOrderTypeSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountOrderTypes(discountOrderTypesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountProductGroups")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountProductGroup[] discountProductGroupsArray =
						new DiscountProductGroup[jsonParserFieldValues.length];

					for (int i = 0; i < discountProductGroupsArray.length;
						 i++) {

						discountProductGroupsArray[i] =
							DiscountProductGroupSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountProductGroups(
						discountProductGroupsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountProducts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountProduct[] discountProductsArray =
						new DiscountProduct[jsonParserFieldValues.length];

					for (int i = 0; i < discountProductsArray.length; i++) {
						discountProductsArray[i] = DiscountProductSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountProducts(discountProductsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountRules")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DiscountRule[] discountRulesArray =
						new DiscountRule[jsonParserFieldValues.length];

					for (int i = 0; i < discountRulesArray.length; i++) {
						discountRulesArray[i] = DiscountRuleSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					discount.setDiscountRules(discountRulesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					discount.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					discount.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discount.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					discount.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "level")) {
				if (jsonParserFieldValue != null) {
					discount.setLevel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "limitationTimes")) {
				if (jsonParserFieldValue != null) {
					discount.setLimitationTimes(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "limitationTimesPerAccount")) {

				if (jsonParserFieldValue != null) {
					discount.setLimitationTimesPerAccount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "limitationType")) {
				if (jsonParserFieldValue != null) {
					discount.setLimitationType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "maximumDiscountAmount")) {

				if (jsonParserFieldValue != null) {
					discount.setMaximumDiscountAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					discount.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					discount.setNeverExpire((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfUse")) {
				if (jsonParserFieldValue != null) {
					discount.setNumberOfUse(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel1")) {
				if (jsonParserFieldValue != null) {
					discount.setPercentageLevel1(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel2")) {
				if (jsonParserFieldValue != null) {
					discount.setPercentageLevel2(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel3")) {
				if (jsonParserFieldValue != null) {
					discount.setPercentageLevel3(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "percentageLevel4")) {
				if (jsonParserFieldValue != null) {
					discount.setPercentageLevel4(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rulesConjunction")) {
				if (jsonParserFieldValue != null) {
					discount.setRulesConjunction((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "target")) {
				if (jsonParserFieldValue != null) {
					discount.setTarget((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					discount.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "useCouponCode")) {
				if (jsonParserFieldValue != null) {
					discount.setUseCouponCode((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "usePercentage")) {
				if (jsonParserFieldValue != null) {
					discount.setUsePercentage((Boolean)jsonParserFieldValue);
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