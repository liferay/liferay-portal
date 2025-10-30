/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountGroupBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

		if (account.getAccountContactInformation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountContactInformation\": ");

			sb.append(String.valueOf(account.getAccountContactInformation()));
		}

		if (account.getAccountGroupBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupBriefs\": ");

			sb.append("[");

			for (int i = 0; i < account.getAccountGroupBriefs().length; i++) {
				sb.append(String.valueOf(account.getAccountGroupBriefs()[i]));

				if ((i + 1) < account.getAccountGroupBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getAccountRoles() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountRoles\": ");

			sb.append("[");

			for (int i = 0; i < account.getAccountRoles().length; i++) {
				sb.append(String.valueOf(account.getAccountRoles()[i]));

				if ((i + 1) < account.getAccountRoles().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getAccountUserAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountUserAccounts\": ");

			sb.append("[");

			for (int i = 0; i < account.getAccountUserAccounts().length; i++) {
				sb.append(String.valueOf(account.getAccountUserAccounts()[i]));

				if ((i + 1) < account.getAccountUserAccounts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(account.getActions()));
		}

		if (account.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(account.getCreator()));
		}

		if (account.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < account.getCustomFields().length; i++) {
				sb.append(account.getCustomFields()[i]);

				if ((i + 1) < account.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		if (account.getDefaultBillingAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultBillingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					account.getDefaultBillingAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (account.getDefaultBillingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultBillingAddressId\": ");

			sb.append(account.getDefaultBillingAddressId());
		}

		if (account.getDefaultShippingAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultShippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					account.getDefaultShippingAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (account.getDefaultShippingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultShippingAddressId\": ");

			sb.append(account.getDefaultShippingAddressId());
		}

		if (account.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(account.getDescription()));

			sb.append("\"");
		}

		if (account.getDomains() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"domains\": ");

			sb.append("[");

			for (int i = 0; i < account.getDomains().length; i++) {
				sb.append(_toJSON(account.getDomains()[i]));

				if ((i + 1) < account.getDomains().length) {
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

		if (account.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < account.getKeywords().length; i++) {
				sb.append(_toJSON(account.getKeywords()[i]));

				if ((i + 1) < account.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getLogoBase64() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoBase64\": ");

			sb.append("\"");

			sb.append(_escape(account.getLogoBase64()));

			sb.append("\"");
		}

		if (account.getLogoExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(account.getLogoExternalReferenceCode()));

			sb.append("\"");
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

		if (account.getNumberOfUsers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUsers\": ");

			sb.append(account.getNumberOfUsers());
		}

		if (account.getOrganizationExternalReferenceCodes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < account.getOrganizationExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						account.getOrganizationExternalReferenceCodes()[i]));

				if ((i + 1) <
						account.
							getOrganizationExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getOrganizationIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationIds\": ");

			sb.append("[");

			for (int i = 0; i < account.getOrganizationIds().length; i++) {
				sb.append(account.getOrganizationIds()[i]);

				if ((i + 1) < account.getOrganizationIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getParentAccountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentAccountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(account.getParentAccountExternalReferenceCode()));

			sb.append("\"");
		}

		if (account.getParentAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentAccountId\": ");

			sb.append(account.getParentAccountId());
		}

		if (account.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < account.getPermissions().length; i++) {
				sb.append(account.getPermissions()[i]);

				if ((i + 1) < account.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getPostalAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalAddresses\": ");

			sb.append("[");

			for (int i = 0; i < account.getPostalAddresses().length; i++) {
				sb.append(String.valueOf(account.getPostalAddresses()[i]));

				if ((i + 1) < account.getPostalAddresses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(account.getStatus());
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

		if (account.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < account.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(account.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) < account.getTaxonomyCategoryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (account.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(account.getType());
			sb.append("\"");
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

		if (account.getAccountContactInformation() == null) {
			map.put("accountContactInformation", null);
		}
		else {
			map.put(
				"accountContactInformation",
				String.valueOf(account.getAccountContactInformation()));
		}

		if (account.getAccountGroupBriefs() == null) {
			map.put("accountGroupBriefs", null);
		}
		else {
			map.put(
				"accountGroupBriefs",
				String.valueOf(account.getAccountGroupBriefs()));
		}

		if (account.getAccountRoles() == null) {
			map.put("accountRoles", null);
		}
		else {
			map.put("accountRoles", String.valueOf(account.getAccountRoles()));
		}

		if (account.getAccountUserAccounts() == null) {
			map.put("accountUserAccounts", null);
		}
		else {
			map.put(
				"accountUserAccounts",
				String.valueOf(account.getAccountUserAccounts()));
		}

		if (account.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(account.getActions()));
		}

		if (account.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(account.getCreator()));
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

		if (account.getDefaultBillingAddressExternalReferenceCode() == null) {
			map.put("defaultBillingAddressExternalReferenceCode", null);
		}
		else {
			map.put(
				"defaultBillingAddressExternalReferenceCode",
				String.valueOf(
					account.getDefaultBillingAddressExternalReferenceCode()));
		}

		if (account.getDefaultBillingAddressId() == null) {
			map.put("defaultBillingAddressId", null);
		}
		else {
			map.put(
				"defaultBillingAddressId",
				String.valueOf(account.getDefaultBillingAddressId()));
		}

		if (account.getDefaultShippingAddressExternalReferenceCode() == null) {
			map.put("defaultShippingAddressExternalReferenceCode", null);
		}
		else {
			map.put(
				"defaultShippingAddressExternalReferenceCode",
				String.valueOf(
					account.getDefaultShippingAddressExternalReferenceCode()));
		}

		if (account.getDefaultShippingAddressId() == null) {
			map.put("defaultShippingAddressId", null);
		}
		else {
			map.put(
				"defaultShippingAddressId",
				String.valueOf(account.getDefaultShippingAddressId()));
		}

		if (account.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(account.getDescription()));
		}

		if (account.getDomains() == null) {
			map.put("domains", null);
		}
		else {
			map.put("domains", String.valueOf(account.getDomains()));
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

		if (account.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(account.getKeywords()));
		}

		if (account.getLogoBase64() == null) {
			map.put("logoBase64", null);
		}
		else {
			map.put("logoBase64", String.valueOf(account.getLogoBase64()));
		}

		if (account.getLogoExternalReferenceCode() == null) {
			map.put("logoExternalReferenceCode", null);
		}
		else {
			map.put(
				"logoExternalReferenceCode",
				String.valueOf(account.getLogoExternalReferenceCode()));
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

		if (account.getNumberOfUsers() == null) {
			map.put("numberOfUsers", null);
		}
		else {
			map.put(
				"numberOfUsers", String.valueOf(account.getNumberOfUsers()));
		}

		if (account.getOrganizationExternalReferenceCodes() == null) {
			map.put("organizationExternalReferenceCodes", null);
		}
		else {
			map.put(
				"organizationExternalReferenceCodes",
				String.valueOf(
					account.getOrganizationExternalReferenceCodes()));
		}

		if (account.getOrganizationIds() == null) {
			map.put("organizationIds", null);
		}
		else {
			map.put(
				"organizationIds",
				String.valueOf(account.getOrganizationIds()));
		}

		if (account.getParentAccountExternalReferenceCode() == null) {
			map.put("parentAccountExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentAccountExternalReferenceCode",
				String.valueOf(
					account.getParentAccountExternalReferenceCode()));
		}

		if (account.getParentAccountId() == null) {
			map.put("parentAccountId", null);
		}
		else {
			map.put(
				"parentAccountId",
				String.valueOf(account.getParentAccountId()));
		}

		if (account.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put("permissions", String.valueOf(account.getPermissions()));
		}

		if (account.getPostalAddresses() == null) {
			map.put("postalAddresses", null);
		}
		else {
			map.put(
				"postalAddresses",
				String.valueOf(account.getPostalAddresses()));
		}

		if (account.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(account.getStatus()));
		}

		if (account.getTaxId() == null) {
			map.put("taxId", null);
		}
		else {
			map.put("taxId", String.valueOf(account.getTaxId()));
		}

		if (account.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(account.getTaxonomyCategoryBriefs()));
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
			if (Objects.equals(
					jsonParserFieldName, "accountContactInformation")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountGroupBriefs")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountRoles")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountUserAccounts")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"defaultBillingAddressExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultBillingAddressId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"defaultShippingAddressExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultShippingAddressId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "domains")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logoBase64")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "logoExternalReferenceCode")) {

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
			else if (Objects.equals(jsonParserFieldName, "numberOfUsers")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"organizationExternalReferenceCodes")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "organizationIds")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentAccountExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentAccountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "postalAddresses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

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

			if (Objects.equals(
					jsonParserFieldName, "accountContactInformation")) {

				if (jsonParserFieldValue != null) {
					account.setAccountContactInformation(
						AccountContactInformationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountGroupBriefs")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountGroupBrief[] accountGroupBriefsArray =
						new AccountGroupBrief[jsonParserFieldValues.length];

					for (int i = 0; i < accountGroupBriefsArray.length; i++) {
						accountGroupBriefsArray[i] =
							AccountGroupBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					account.setAccountGroupBriefs(accountGroupBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountRoles")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountRole[] accountRolesArray =
						new AccountRole[jsonParserFieldValues.length];

					for (int i = 0; i < accountRolesArray.length; i++) {
						accountRolesArray[i] = AccountRoleSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					account.setAccountRoles(accountRolesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountUserAccounts")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserAccount[] accountUserAccountsArray =
						new UserAccount[jsonParserFieldValues.length];

					for (int i = 0; i < accountUserAccountsArray.length; i++) {
						accountUserAccountsArray[i] = UserAccountSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					account.setAccountUserAccounts(accountUserAccountsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					account.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					account.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.user.client.custom.field.
						CustomField[] customFieldsArray = new
						com.liferay.headless.admin.user.client.custom.field.
							CustomField[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.admin.user.client.custom.field.
								CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					account.setCustomFields(customFieldsArray);
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
						"defaultBillingAddressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					account.setDefaultBillingAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultBillingAddressId")) {

				if (jsonParserFieldValue != null) {
					account.setDefaultBillingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"defaultShippingAddressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					account.setDefaultShippingAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultShippingAddressId")) {

				if (jsonParserFieldValue != null) {
					account.setDefaultShippingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					account.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "domains")) {
				if (jsonParserFieldValue != null) {
					account.setDomains(
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
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					account.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logoBase64")) {
				if (jsonParserFieldValue != null) {
					account.setLogoBase64((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "logoExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					account.setLogoExternalReferenceCode(
						(String)jsonParserFieldValue);
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
			else if (Objects.equals(jsonParserFieldName, "numberOfUsers")) {
				if (jsonParserFieldValue != null) {
					account.setNumberOfUsers(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"organizationExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					account.setOrganizationExternalReferenceCodes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "organizationIds")) {
				if (jsonParserFieldValue != null) {
					account.setOrganizationIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentAccountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					account.setParentAccountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentAccountId")) {
				if (jsonParserFieldValue != null) {
					account.setParentAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.user.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.admin.user.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.admin.user.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					account.setPermissions(permissionsArray);
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

					account.setPostalAddresses(postalAddressesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					account.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxId")) {
				if (jsonParserFieldValue != null) {
					account.setTaxId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TaxonomyCategoryBrief[] taxonomyCategoryBriefsArray =
						new TaxonomyCategoryBrief[jsonParserFieldValues.length];

					for (int i = 0; i < taxonomyCategoryBriefsArray.length;
						 i++) {

						taxonomyCategoryBriefsArray[i] =
							TaxonomyCategoryBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					account.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					account.setType(
						Account.Type.create((String)jsonParserFieldValue));
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