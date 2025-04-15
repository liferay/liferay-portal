/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.AccountContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class AccountContactInformationSerDes {

	public static AccountContactInformation toDTO(String json) {
		AccountContactInformationJSONParser
			accountContactInformationJSONParser =
				new AccountContactInformationJSONParser();

		return accountContactInformationJSONParser.parseToDTO(json);
	}

	public static AccountContactInformation[] toDTOs(String json) {
		AccountContactInformationJSONParser
			accountContactInformationJSONParser =
				new AccountContactInformationJSONParser();

		return accountContactInformationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AccountContactInformation accountContactInformation) {

		if (accountContactInformation == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountContactInformation.getEmailAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddresses\": ");

			sb.append("[");

			for (int i = 0;
				 i < accountContactInformation.getEmailAddresses().length;
				 i++) {

				sb.append(
					String.valueOf(
						accountContactInformation.getEmailAddresses()[i]));

				if ((i + 1) <
						accountContactInformation.getEmailAddresses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountContactInformation.getFacebook() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facebook\": ");

			sb.append("\"");

			sb.append(_escape(accountContactInformation.getFacebook()));

			sb.append("\"");
		}

		if (accountContactInformation.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(accountContactInformation.getId());
		}

		if (accountContactInformation.getJabber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jabber\": ");

			sb.append("\"");

			sb.append(_escape(accountContactInformation.getJabber()));

			sb.append("\"");
		}

		if (accountContactInformation.getPostalAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalAddresses\": ");

			sb.append("[");

			for (int i = 0;
				 i < accountContactInformation.getPostalAddresses().length;
				 i++) {

				sb.append(
					String.valueOf(
						accountContactInformation.getPostalAddresses()[i]));

				if ((i + 1) <
						accountContactInformation.getPostalAddresses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountContactInformation.getSkype() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skype\": ");

			sb.append("\"");

			sb.append(_escape(accountContactInformation.getSkype()));

			sb.append("\"");
		}

		if (accountContactInformation.getSms() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sms\": ");

			sb.append("\"");

			sb.append(_escape(accountContactInformation.getSms()));

			sb.append("\"");
		}

		if (accountContactInformation.getTelephones() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"telephones\": ");

			sb.append("[");

			for (int i = 0;
				 i < accountContactInformation.getTelephones().length; i++) {

				sb.append(
					String.valueOf(
						accountContactInformation.getTelephones()[i]));

				if ((i + 1) <
						accountContactInformation.getTelephones().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountContactInformation.getTwitter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"twitter\": ");

			sb.append("\"");

			sb.append(_escape(accountContactInformation.getTwitter()));

			sb.append("\"");
		}

		if (accountContactInformation.getWebUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"webUrls\": ");

			sb.append("[");

			for (int i = 0; i < accountContactInformation.getWebUrls().length;
				 i++) {

				sb.append(
					String.valueOf(accountContactInformation.getWebUrls()[i]));

				if ((i + 1) < accountContactInformation.getWebUrls().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountContactInformationJSONParser
			accountContactInformationJSONParser =
				new AccountContactInformationJSONParser();

		return accountContactInformationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountContactInformation accountContactInformation) {

		if (accountContactInformation == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountContactInformation.getEmailAddresses() == null) {
			map.put("emailAddresses", null);
		}
		else {
			map.put(
				"emailAddresses",
				String.valueOf(accountContactInformation.getEmailAddresses()));
		}

		if (accountContactInformation.getFacebook() == null) {
			map.put("facebook", null);
		}
		else {
			map.put(
				"facebook",
				String.valueOf(accountContactInformation.getFacebook()));
		}

		if (accountContactInformation.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountContactInformation.getId()));
		}

		if (accountContactInformation.getJabber() == null) {
			map.put("jabber", null);
		}
		else {
			map.put(
				"jabber",
				String.valueOf(accountContactInformation.getJabber()));
		}

		if (accountContactInformation.getPostalAddresses() == null) {
			map.put("postalAddresses", null);
		}
		else {
			map.put(
				"postalAddresses",
				String.valueOf(accountContactInformation.getPostalAddresses()));
		}

		if (accountContactInformation.getSkype() == null) {
			map.put("skype", null);
		}
		else {
			map.put(
				"skype", String.valueOf(accountContactInformation.getSkype()));
		}

		if (accountContactInformation.getSms() == null) {
			map.put("sms", null);
		}
		else {
			map.put("sms", String.valueOf(accountContactInformation.getSms()));
		}

		if (accountContactInformation.getTelephones() == null) {
			map.put("telephones", null);
		}
		else {
			map.put(
				"telephones",
				String.valueOf(accountContactInformation.getTelephones()));
		}

		if (accountContactInformation.getTwitter() == null) {
			map.put("twitter", null);
		}
		else {
			map.put(
				"twitter",
				String.valueOf(accountContactInformation.getTwitter()));
		}

		if (accountContactInformation.getWebUrls() == null) {
			map.put("webUrls", null);
		}
		else {
			map.put(
				"webUrls",
				String.valueOf(accountContactInformation.getWebUrls()));
		}

		return map;
	}

	public static class AccountContactInformationJSONParser
		extends BaseJSONParser<AccountContactInformation> {

		@Override
		protected AccountContactInformation createDTO() {
			return new AccountContactInformation();
		}

		@Override
		protected AccountContactInformation[] createDTOArray(int size) {
			return new AccountContactInformation[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "emailAddresses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "facebook")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "jabber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "postalAddresses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skype")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sms")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "telephones")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "twitter")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "webUrls")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountContactInformation accountContactInformation,
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

					accountContactInformation.setEmailAddresses(
						emailAddressesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "facebook")) {
				if (jsonParserFieldValue != null) {
					accountContactInformation.setFacebook(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountContactInformation.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "jabber")) {
				if (jsonParserFieldValue != null) {
					accountContactInformation.setJabber(
						(String)jsonParserFieldValue);
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

					accountContactInformation.setPostalAddresses(
						postalAddressesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skype")) {
				if (jsonParserFieldValue != null) {
					accountContactInformation.setSkype(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sms")) {
				if (jsonParserFieldValue != null) {
					accountContactInformation.setSms(
						(String)jsonParserFieldValue);
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

					accountContactInformation.setTelephones(telephonesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "twitter")) {
				if (jsonParserFieldValue != null) {
					accountContactInformation.setTwitter(
						(String)jsonParserFieldValue);
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

					accountContactInformation.setWebUrls(webUrlsArray);
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