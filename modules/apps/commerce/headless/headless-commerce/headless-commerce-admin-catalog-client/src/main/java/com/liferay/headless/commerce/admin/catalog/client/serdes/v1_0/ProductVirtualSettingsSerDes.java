/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

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
public class ProductVirtualSettingsSerDes {

	public static ProductVirtualSettings toDTO(String json) {
		ProductVirtualSettingsJSONParser productVirtualSettingsJSONParser =
			new ProductVirtualSettingsJSONParser();

		return productVirtualSettingsJSONParser.parseToDTO(json);
	}

	public static ProductVirtualSettings[] toDTOs(String json) {
		ProductVirtualSettingsJSONParser productVirtualSettingsJSONParser =
			new ProductVirtualSettingsJSONParser();

		return productVirtualSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProductVirtualSettings productVirtualSettings) {
		if (productVirtualSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productVirtualSettings.getActivationStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activationStatus\": ");

			sb.append(productVirtualSettings.getActivationStatus());
		}

		if (productVirtualSettings.getActivationStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activationStatusInfo\": ");

			sb.append(
				String.valueOf(
					productVirtualSettings.getActivationStatusInfo()));
		}

		if (productVirtualSettings.getAttachment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachment\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettings.getAttachment()));

			sb.append("\"");
		}

		if (productVirtualSettings.getDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"duration\": ");

			sb.append(productVirtualSettings.getDuration());
		}

		if (productVirtualSettings.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productVirtualSettings.getId());
		}

		if (productVirtualSettings.getMaxUsages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxUsages\": ");

			sb.append(productVirtualSettings.getMaxUsages());
		}

		if (productVirtualSettings.getProductVirtualSettingsFileEntries() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productVirtualSettingsFileEntries\": ");

			sb.append("[");

			for (int i = 0;
				 i < productVirtualSettings.
					 getProductVirtualSettingsFileEntries().length;
				 i++) {

				sb.append(
					String.valueOf(
						productVirtualSettings.
							getProductVirtualSettingsFileEntries()[i]));

				if ((i + 1) < productVirtualSettings.
						getProductVirtualSettingsFileEntries().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (productVirtualSettings.getSampleAttachment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleAttachment\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettings.getSampleAttachment()));

			sb.append("\"");
		}

		if (productVirtualSettings.getSampleSrc() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleSrc\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettings.getSampleSrc()));

			sb.append("\"");
		}

		if (productVirtualSettings.getSampleURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleURL\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettings.getSampleURL()));

			sb.append("\"");
		}

		if (productVirtualSettings.getSrc() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"src\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettings.getSrc()));

			sb.append("\"");
		}

		if (productVirtualSettings.getTermsOfUseContent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termsOfUseContent\": ");

			sb.append(_toJSON(productVirtualSettings.getTermsOfUseContent()));
		}

		if (productVirtualSettings.getTermsOfUseJournalArticleId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termsOfUseJournalArticleId\": ");

			sb.append(productVirtualSettings.getTermsOfUseJournalArticleId());
		}

		if (productVirtualSettings.getTermsOfUseRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termsOfUseRequired\": ");

			sb.append(productVirtualSettings.getTermsOfUseRequired());
		}

		if (productVirtualSettings.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettings.getUrl()));

			sb.append("\"");
		}

		if (productVirtualSettings.getUseSample() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useSample\": ");

			sb.append(productVirtualSettings.getUseSample());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductVirtualSettingsJSONParser productVirtualSettingsJSONParser =
			new ProductVirtualSettingsJSONParser();

		return productVirtualSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductVirtualSettings productVirtualSettings) {

		if (productVirtualSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productVirtualSettings.getActivationStatus() == null) {
			map.put("activationStatus", null);
		}
		else {
			map.put(
				"activationStatus",
				String.valueOf(productVirtualSettings.getActivationStatus()));
		}

		if (productVirtualSettings.getActivationStatusInfo() == null) {
			map.put("activationStatusInfo", null);
		}
		else {
			map.put(
				"activationStatusInfo",
				String.valueOf(
					productVirtualSettings.getActivationStatusInfo()));
		}

		if (productVirtualSettings.getAttachment() == null) {
			map.put("attachment", null);
		}
		else {
			map.put(
				"attachment",
				String.valueOf(productVirtualSettings.getAttachment()));
		}

		if (productVirtualSettings.getDuration() == null) {
			map.put("duration", null);
		}
		else {
			map.put(
				"duration",
				String.valueOf(productVirtualSettings.getDuration()));
		}

		if (productVirtualSettings.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(productVirtualSettings.getId()));
		}

		if (productVirtualSettings.getMaxUsages() == null) {
			map.put("maxUsages", null);
		}
		else {
			map.put(
				"maxUsages",
				String.valueOf(productVirtualSettings.getMaxUsages()));
		}

		if (productVirtualSettings.getProductVirtualSettingsFileEntries() ==
				null) {

			map.put("productVirtualSettingsFileEntries", null);
		}
		else {
			map.put(
				"productVirtualSettingsFileEntries",
				String.valueOf(
					productVirtualSettings.
						getProductVirtualSettingsFileEntries()));
		}

		if (productVirtualSettings.getSampleAttachment() == null) {
			map.put("sampleAttachment", null);
		}
		else {
			map.put(
				"sampleAttachment",
				String.valueOf(productVirtualSettings.getSampleAttachment()));
		}

		if (productVirtualSettings.getSampleSrc() == null) {
			map.put("sampleSrc", null);
		}
		else {
			map.put(
				"sampleSrc",
				String.valueOf(productVirtualSettings.getSampleSrc()));
		}

		if (productVirtualSettings.getSampleURL() == null) {
			map.put("sampleURL", null);
		}
		else {
			map.put(
				"sampleURL",
				String.valueOf(productVirtualSettings.getSampleURL()));
		}

		if (productVirtualSettings.getSrc() == null) {
			map.put("src", null);
		}
		else {
			map.put("src", String.valueOf(productVirtualSettings.getSrc()));
		}

		if (productVirtualSettings.getTermsOfUseContent() == null) {
			map.put("termsOfUseContent", null);
		}
		else {
			map.put(
				"termsOfUseContent",
				String.valueOf(productVirtualSettings.getTermsOfUseContent()));
		}

		if (productVirtualSettings.getTermsOfUseJournalArticleId() == null) {
			map.put("termsOfUseJournalArticleId", null);
		}
		else {
			map.put(
				"termsOfUseJournalArticleId",
				String.valueOf(
					productVirtualSettings.getTermsOfUseJournalArticleId()));
		}

		if (productVirtualSettings.getTermsOfUseRequired() == null) {
			map.put("termsOfUseRequired", null);
		}
		else {
			map.put(
				"termsOfUseRequired",
				String.valueOf(productVirtualSettings.getTermsOfUseRequired()));
		}

		if (productVirtualSettings.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(productVirtualSettings.getUrl()));
		}

		if (productVirtualSettings.getUseSample() == null) {
			map.put("useSample", null);
		}
		else {
			map.put(
				"useSample",
				String.valueOf(productVirtualSettings.getUseSample()));
		}

		return map;
	}

	public static class ProductVirtualSettingsJSONParser
		extends BaseJSONParser<ProductVirtualSettings> {

		@Override
		protected ProductVirtualSettings createDTO() {
			return new ProductVirtualSettings();
		}

		@Override
		protected ProductVirtualSettings[] createDTOArray(int size) {
			return new ProductVirtualSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "activationStatus")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "activationStatusInfo")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "attachment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "duration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxUsages")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productVirtualSettingsFileEntries")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sampleAttachment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sampleSrc")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sampleURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "termsOfUseContent")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "termsOfUseJournalArticleId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "termsOfUseRequired")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "useSample")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductVirtualSettings productVirtualSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "activationStatus")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setActivationStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "activationStatusInfo")) {

				if (jsonParserFieldValue != null) {
					productVirtualSettings.setActivationStatusInfo(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "attachment")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setAttachment(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "duration")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setDuration(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxUsages")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setMaxUsages(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productVirtualSettingsFileEntries")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductVirtualSettingsFileEntry[]
						productVirtualSettingsFileEntriesArray =
							new ProductVirtualSettingsFileEntry
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < productVirtualSettingsFileEntriesArray.length;
						 i++) {

						productVirtualSettingsFileEntriesArray[i] =
							ProductVirtualSettingsFileEntrySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					productVirtualSettings.setProductVirtualSettingsFileEntries(
						productVirtualSettingsFileEntriesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sampleAttachment")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setSampleAttachment(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sampleSrc")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setSampleSrc(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sampleURL")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setSampleURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setSrc((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "termsOfUseContent")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setTermsOfUseContent(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "termsOfUseJournalArticleId")) {

				if (jsonParserFieldValue != null) {
					productVirtualSettings.setTermsOfUseJournalArticleId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "termsOfUseRequired")) {

				if (jsonParserFieldValue != null) {
					productVirtualSettings.setTermsOfUseRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "useSample")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettings.setUseSample(
						(Boolean)jsonParserFieldValue);
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