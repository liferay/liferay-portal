/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.account.client.dto.v1_0.Account;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountAddress;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountMember;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountOrganization;
import com.liferay.headless.commerce.admin.account.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class AccountSerDes {

	public static Account toDTO(String json) {
		AccountJSONParser accountJSONParser = new AccountJSONParser();

		return accountJSONParser.parseToDTO(json);
	}

	public static Account[] toDTOs(String json) {
		AccountJSONParser accountJSONParser = new AccountJSONParser();

		return accountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Account account) {
		if (account == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (account.getAccountAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountAddresses\": ");

			sb.append("[");

			for (int i = 0; i < account.getAccountAddresses().length; i++) {
				sb.append(String.valueOf(account.getAccountAddresses()[i]));

				if ((i + 1) < account.getAccountAddresses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getAccountMembers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountMembers\": ");

			sb.append("[");

			for (int i = 0; i < account.getAccountMembers().length; i++) {
				sb.append(String.valueOf(account.getAccountMembers()[i]));

				if ((i + 1) < account.getAccountMembers().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getAccountOrganizations() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountOrganizations\": ");

			sb.append("[");

			for (int i = 0; i < account.getAccountOrganizations().length; i++) {
				sb.append(String.valueOf(account.getAccountOrganizations()[i]));

				if ((i + 1) < account.getAccountOrganizations().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(account.getActive());
		}

		if (account.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(account.getCustomFields()));
		}

		if (account.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(account.getDateCreated()));

			sb.append("\"");
		}

		if (account.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(account.getDateModified()));

			sb.append("\"");
		}

		if (account.getDefaultBillingAccountAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultBillingAccountAddressId\": ");

			sb.append(account.getDefaultBillingAccountAddressId());
		}

		if (account.getDefaultShippingAccountAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultShippingAccountAddressId\": ");

			sb.append(account.getDefaultShippingAccountAddressId());
		}

		if (account.getEmailAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddresses\": ");

			sb.append("[");

			for (int i = 0; i < account.getEmailAddresses().length; i++) {
				sb.append(_toJSON(account.getEmailAddresses()[i]));

				if ((i + 1) < account.getEmailAddresses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(account.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (account.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(account.getId());
		}

		if (account.getLogoId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoId\": ");

			sb.append(account.getLogoId());
		}

		if (account.getLogoURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoURL\": ");

			sb.append("\"");

			sb.append(_escape(account.getLogoURL()));

			sb.append("\"");
		}

		if (account.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(account.getName()));

			sb.append("\"");
		}

		if (account.getRoot() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"root\": ");

			sb.append(account.getRoot());
		}

		if (account.getTaxId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxId\": ");

			sb.append("\"");

			sb.append(_escape(account.getTaxId()));

			sb.append("\"");
		}

		if (account.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(account.getType());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountJSONParser accountJSONParser = new AccountJSONParser();

		return accountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Account account) {
		if (account == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (account.getAccountAddresses() == null) {
			map.put("accountAddresses", null);
		}
		else {
			map.put(
				"accountAddresses",
				String.valueOf(account.getAccountAddresses()));
		}

		if (account.getAccountMembers() == null) {
			map.put("accountMembers", null);
		}
		else {
			map.put(
				"accountMembers", String.valueOf(account.getAccountMembers()));
		}

		if (account.getAccountOrganizations() == null) {
			map.put("accountOrganizations", null);
		}
		else {
			map.put(
				"accountOrganizations",
				String.valueOf(account.getAccountOrganizations()));
		}

		if (account.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(account.getActive()));
		}

		if (account.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(account.getCustomFields()));
		}

		if (account.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(account.getDateCreated()));
		}

		if (account.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(account.getDateModified()));
		}

		if (account.getDefaultBillingAccountAddressId() == null) {
			map.put("defaultBillingAccountAddressId", null);
		}
		else {
			map.put(
				"defaultBillingAccountAddressId",
				String.valueOf(account.getDefaultBillingAccountAddressId()));
		}

		if (account.getDefaultShippingAccountAddressId() == null) {
			map.put("defaultShippingAccountAddressId", null);
		}
		else {
			map.put(
				"defaultShippingAccountAddressId",
				String.valueOf(account.getDefaultShippingAccountAddressId()));
		}

		if (account.getEmailAddresses() == null) {
			map.put("emailAddresses", null);
		}
		else {
			map.put(
				"emailAddresses", String.valueOf(account.getEmailAddresses()));
		}

		if (account.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(account.getExternalReferenceCode()));
		}

		if (account.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(account.getId()));
		}

		if (account.getLogoId() == null) {
			map.put("logoId", null);
		}
		else {
			map.put("logoId", String.valueOf(account.getLogoId()));
		}

		if (account.getLogoURL() == null) {
			map.put("logoURL", null);
		}
		else {
			map.put("logoURL", String.valueOf(account.getLogoURL()));
		}

		if (account.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(account.getName()));
		}

		if (account.getRoot() == null) {
			map.put("root", null);
		}
		else {
			map.put("root", String.valueOf(account.getRoot()));
		}

		if (account.getTaxId() == null) {
			map.put("taxId", null);
		}
		else {
			map.put("taxId", String.valueOf(account.getTaxId()));
		}

		if (account.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(account.getType()));
		}

		return map;
	}

	public static class AccountJSONParser extends BaseJSONParser<Account> {

		@Override
		protected Account createDTO() {
			return new Account();
		}

		@Override
		protected Account[] createDTOArray(int size) {
			return new Account[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountAddresses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountMembers")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountOrganizations")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"defaultBillingAccountAddressId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"defaultShippingAccountAddressId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddresses")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logoId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logoURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "root")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Account account, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountAddresses")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountAddress[] accountAddressesArray =
						new AccountAddress[jsonParserFieldValues.length];

					for (int i = 0; i < accountAddressesArray.length; i++) {
						accountAddressesArray[i] = AccountAddressSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					account.setAccountAddresses(accountAddressesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountMembers")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountMember[] accountMembersArray =
						new AccountMember[jsonParserFieldValues.length];

					for (int i = 0; i < accountMembersArray.length; i++) {
						accountMembersArray[i] = AccountMemberSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					account.setAccountMembers(accountMembersArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountOrganizations")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountOrganization[] accountOrganizationsArray =
						new AccountOrganization[jsonParserFieldValues.length];

					for (int i = 0; i < accountOrganizationsArray.length; i++) {
						accountOrganizationsArray[i] =
							AccountOrganizationSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					account.setAccountOrganizations(accountOrganizationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					account.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					account.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					account.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					account.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"defaultBillingAccountAddressId")) {

				if (jsonParserFieldValue != null) {
					account.setDefaultBillingAccountAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"defaultShippingAccountAddressId")) {

				if (jsonParserFieldValue != null) {
					account.setDefaultShippingAccountAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddresses")) {
				if (jsonParserFieldValue != null) {
					account.setEmailAddresses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					account.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					account.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logoId")) {
				if (jsonParserFieldValue != null) {
					account.setLogoId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logoURL")) {
				if (jsonParserFieldValue != null) {
					account.setLogoURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					account.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "root")) {
				if (jsonParserFieldValue != null) {
					account.setRoot((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxId")) {
				if (jsonParserFieldValue != null) {
					account.setTaxId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					account.setType(
						Integer.valueOf((String)jsonParserFieldValue));
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