/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class OrganizationContactInformationSerDes {

	public static OrganizationContactInformation toDTO(String json) {
		OrganizationContactInformationJSONParser
			organizationContactInformationJSONParser =
				new OrganizationContactInformationJSONParser();

		return organizationContactInformationJSONParser.parseToDTO(json);
	}

	public static OrganizationContactInformation[] toDTOs(String json) {
		OrganizationContactInformationJSONParser
			organizationContactInformationJSONParser =
				new OrganizationContactInformationJSONParser();

		return organizationContactInformationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		OrganizationContactInformation organizationContactInformation) {

		if (organizationContactInformation == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (organizationContactInformation.getEmailAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddresses\": ");

			sb.append("[");

			for (int i = 0;
				 i < organizationContactInformation.getEmailAddresses().length;
				 i++) {

				sb.append(
					String.valueOf(
						organizationContactInformation.getEmailAddresses()[i]));

				if ((i + 1) <
						organizationContactInformation.
							getEmailAddresses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organizationContactInformation.getPostalAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalAddresses\": ");

			sb.append("[");

			for (int i = 0;
				 i < organizationContactInformation.getPostalAddresses().length;
				 i++) {

				sb.append(
					String.valueOf(
						organizationContactInformation.getPostalAddresses()
							[i]));

				if ((i + 1) < organizationContactInformation.
						getPostalAddresses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organizationContactInformation.getTelephones() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"telephones\": ");

			sb.append("[");

			for (int i = 0;
				 i < organizationContactInformation.getTelephones().length;
				 i++) {

				sb.append(
					String.valueOf(
						organizationContactInformation.getTelephones()[i]));

				if ((i + 1) <
						organizationContactInformation.getTelephones().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organizationContactInformation.getWebUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"webUrls\": ");

			sb.append("[");

			for (int i = 0;
				 i < organizationContactInformation.getWebUrls().length; i++) {

				sb.append(
					String.valueOf(
						organizationContactInformation.getWebUrls()[i]));

				if ((i + 1) <
						organizationContactInformation.getWebUrls().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrganizationContactInformationJSONParser
			organizationContactInformationJSONParser =
				new OrganizationContactInformationJSONParser();

		return organizationContactInformationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OrganizationContactInformation organizationContactInformation) {

		if (organizationContactInformation == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (organizationContactInformation.getEmailAddresses() == null) {
			map.put("emailAddresses", null);
		}
		else {
			map.put(
				"emailAddresses",
				String.valueOf(
					organizationContactInformation.getEmailAddresses()));
		}

		if (organizationContactInformation.getPostalAddresses() == null) {
			map.put("postalAddresses", null);
		}
		else {
			map.put(
				"postalAddresses",
				String.valueOf(
					organizationContactInformation.getPostalAddresses()));
		}

		if (organizationContactInformation.getTelephones() == null) {
			map.put("telephones", null);
		}
		else {
			map.put(
				"telephones",
				String.valueOf(organizationContactInformation.getTelephones()));
		}

		if (organizationContactInformation.getWebUrls() == null) {
			map.put("webUrls", null);
		}
		else {
			map.put(
				"webUrls",
				String.valueOf(organizationContactInformation.getWebUrls()));
		}

		return map;
	}

	public static class OrganizationContactInformationJSONParser
		extends BaseJSONParser<OrganizationContactInformation> {

		@Override
		protected OrganizationContactInformation createDTO() {
			return new OrganizationContactInformation();
		}

		@Override
		protected OrganizationContactInformation[] createDTOArray(int size) {
			return new OrganizationContactInformation[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "emailAddresses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "postalAddresses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "telephones")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "webUrls")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OrganizationContactInformation organizationContactInformation,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "emailAddresses")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					EmailAddress[] emailAddressesArray =
						new EmailAddress[jsonParserFieldValues.length];

					for (int i = 0; i < emailAddressesArray.length; i++) {
						emailAddressesArray[i] = EmailAddressSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organizationContactInformation.setEmailAddresses(
						emailAddressesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "postalAddresses")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PostalAddress[] postalAddressesArray =
						new PostalAddress[jsonParserFieldValues.length];

					for (int i = 0; i < postalAddressesArray.length; i++) {
						postalAddressesArray[i] = PostalAddressSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organizationContactInformation.setPostalAddresses(
						postalAddressesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "telephones")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Phone[] telephonesArray =
						new Phone[jsonParserFieldValues.length];

					for (int i = 0; i < telephonesArray.length; i++) {
						telephonesArray[i] = PhoneSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organizationContactInformation.setTelephones(
						telephonesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "webUrls")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WebUrl[] webUrlsArray =
						new WebUrl[jsonParserFieldValues.length];

					for (int i = 0; i < webUrlsArray.length; i++) {
						webUrlsArray[i] = WebUrlSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organizationContactInformation.setWebUrls(webUrlsArray);
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