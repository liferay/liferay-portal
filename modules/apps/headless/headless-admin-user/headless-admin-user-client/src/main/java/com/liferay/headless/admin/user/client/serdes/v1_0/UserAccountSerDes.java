/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.AssetLibraryBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.SiteBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserGroupBrief;
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
public class UserAccountSerDes {

	public static UserAccount toDTO(String json) {
		UserAccountJSONParser userAccountJSONParser =
			new UserAccountJSONParser();

		return userAccountJSONParser.parseToDTO(json);
	}

	public static UserAccount[] toDTOs(String json) {
		UserAccountJSONParser userAccountJSONParser =
			new UserAccountJSONParser();

		return userAccountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserAccount userAccount) {
		if (userAccount == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userAccount.getAccountBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getAccountBriefs().length; i++) {
				sb.append(String.valueOf(userAccount.getAccountBriefs()[i]));

				if ((i + 1) < userAccount.getAccountBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(userAccount.getActions()));
		}

		if (userAccount.getAdditionalName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionalName\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getAdditionalName()));

			sb.append("\"");
		}

		if (userAccount.getAlternateName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"alternateName\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getAlternateName()));

			sb.append("\"");
		}

		if (userAccount.getAssetLibraryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getAssetLibraryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(userAccount.getAssetLibraryBriefs()[i]));

				if ((i + 1) < userAccount.getAssetLibraryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getBirthDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"birthDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userAccount.getBirthDate()));

			sb.append("\"");
		}

		if (userAccount.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(userAccount.getCreator()));
		}

		if (userAccount.getCurrentPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currentPassword\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getCurrentPassword()));

			sb.append("\"");
		}

		if (userAccount.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getCustomFields().length; i++) {
				sb.append(userAccount.getCustomFields()[i]);

				if ((i + 1) < userAccount.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getDashboardURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dashboardURL\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getDashboardURL()));

			sb.append("\"");
		}

		if (userAccount.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userAccount.getDateCreated()));

			sb.append("\"");
		}

		if (userAccount.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userAccount.getDateModified()));

			sb.append("\"");
		}

		if (userAccount.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getEmailAddress()));

			sb.append("\"");
		}

		if (userAccount.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (userAccount.getFamilyName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"familyName\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getFamilyName()));

			sb.append("\"");
		}

		if (userAccount.getGender() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gender\": ");

			sb.append("\"");

			sb.append(userAccount.getGender());

			sb.append("\"");
		}

		if (userAccount.getGivenName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"givenName\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getGivenName()));

			sb.append("\"");
		}

		if (userAccount.getHasLoginDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasLoginDate\": ");

			sb.append(userAccount.getHasLoginDate());
		}

		if (userAccount.getHonorificPrefix() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificPrefix\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getHonorificPrefix()));

			sb.append("\"");
		}

		if (userAccount.getHonorificSuffix() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificSuffix\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getHonorificSuffix()));

			sb.append("\"");
		}

		if (userAccount.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(userAccount.getId());
		}

		if (userAccount.getImage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getImage()));

			sb.append("\"");
		}

		if (userAccount.getImageExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getImageExternalReferenceCode()));

			sb.append("\"");
		}

		if (userAccount.getImageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageId\": ");

			sb.append(userAccount.getImageId());
		}

		if (userAccount.getJobTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jobTitle\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getJobTitle()));

			sb.append("\"");
		}

		if (userAccount.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getKeywords().length; i++) {
				sb.append(_toJSON(userAccount.getKeywords()[i]));

				if ((i + 1) < userAccount.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getLanguageDisplayName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageDisplayName\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getLanguageDisplayName()));

			sb.append("\"");
		}

		if (userAccount.getLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageId\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getLanguageId()));

			sb.append("\"");
		}

		if (userAccount.getLastLoginDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastLoginDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userAccount.getLastLoginDate()));

			sb.append("\"");
		}

		if (userAccount.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getName()));

			sb.append("\"");
		}

		if (userAccount.getOrganizationBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getOrganizationBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(userAccount.getOrganizationBriefs()[i]));

				if ((i + 1) < userAccount.getOrganizationBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"password\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getPassword()));

			sb.append("\"");
		}

		if (userAccount.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getPermissions().length; i++) {
				sb.append(userAccount.getPermissions()[i]);

				if ((i + 1) < userAccount.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getProfileURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"profileURL\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getProfileURL()));

			sb.append("\"");
		}

		if (userAccount.getRoleBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getRoleBriefs().length; i++) {
				sb.append(String.valueOf(userAccount.getRoleBriefs()[i]));

				if ((i + 1) < userAccount.getRoleBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getSiteBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getSiteBriefs().length; i++) {
				sb.append(String.valueOf(userAccount.getSiteBriefs()[i]));

				if ((i + 1) < userAccount.getSiteBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(userAccount.getStatus());

			sb.append("\"");
		}

		if (userAccount.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(userAccount.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) < userAccount.getTaxonomyCategoryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userAccount.getUserAccountContactInformation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccountContactInformation\": ");

			sb.append(
				String.valueOf(userAccount.getUserAccountContactInformation()));
		}

		if (userAccount.getUserGroupBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userGroupBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userAccount.getUserGroupBriefs().length; i++) {
				sb.append(String.valueOf(userAccount.getUserGroupBriefs()[i]));

				if ((i + 1) < userAccount.getUserGroupBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserAccountJSONParser userAccountJSONParser =
			new UserAccountJSONParser();

		return userAccountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userAccount.getAccountBriefs() == null) {
			map.put("accountBriefs", null);
		}
		else {
			map.put(
				"accountBriefs",
				String.valueOf(userAccount.getAccountBriefs()));
		}

		if (userAccount.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(userAccount.getActions()));
		}

		if (userAccount.getAdditionalName() == null) {
			map.put("additionalName", null);
		}
		else {
			map.put(
				"additionalName",
				String.valueOf(userAccount.getAdditionalName()));
		}

		if (userAccount.getAlternateName() == null) {
			map.put("alternateName", null);
		}
		else {
			map.put(
				"alternateName",
				String.valueOf(userAccount.getAlternateName()));
		}

		if (userAccount.getAssetLibraryBriefs() == null) {
			map.put("assetLibraryBriefs", null);
		}
		else {
			map.put(
				"assetLibraryBriefs",
				String.valueOf(userAccount.getAssetLibraryBriefs()));
		}

		if (userAccount.getBirthDate() == null) {
			map.put("birthDate", null);
		}
		else {
			map.put(
				"birthDate",
				liferayToJSONDateFormat.format(userAccount.getBirthDate()));
		}

		if (userAccount.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(userAccount.getCreator()));
		}

		if (userAccount.getCurrentPassword() == null) {
			map.put("currentPassword", null);
		}
		else {
			map.put(
				"currentPassword",
				String.valueOf(userAccount.getCurrentPassword()));
		}

		if (userAccount.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(userAccount.getCustomFields()));
		}

		if (userAccount.getDashboardURL() == null) {
			map.put("dashboardURL", null);
		}
		else {
			map.put(
				"dashboardURL", String.valueOf(userAccount.getDashboardURL()));
		}

		if (userAccount.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(userAccount.getDateCreated()));
		}

		if (userAccount.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(userAccount.getDateModified()));
		}

		if (userAccount.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress", String.valueOf(userAccount.getEmailAddress()));
		}

		if (userAccount.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(userAccount.getExternalReferenceCode()));
		}

		if (userAccount.getFamilyName() == null) {
			map.put("familyName", null);
		}
		else {
			map.put("familyName", String.valueOf(userAccount.getFamilyName()));
		}

		if (userAccount.getGender() == null) {
			map.put("gender", null);
		}
		else {
			map.put("gender", String.valueOf(userAccount.getGender()));
		}

		if (userAccount.getGivenName() == null) {
			map.put("givenName", null);
		}
		else {
			map.put("givenName", String.valueOf(userAccount.getGivenName()));
		}

		if (userAccount.getHasLoginDate() == null) {
			map.put("hasLoginDate", null);
		}
		else {
			map.put(
				"hasLoginDate", String.valueOf(userAccount.getHasLoginDate()));
		}

		if (userAccount.getHonorificPrefix() == null) {
			map.put("honorificPrefix", null);
		}
		else {
			map.put(
				"honorificPrefix",
				String.valueOf(userAccount.getHonorificPrefix()));
		}

		if (userAccount.getHonorificSuffix() == null) {
			map.put("honorificSuffix", null);
		}
		else {
			map.put(
				"honorificSuffix",
				String.valueOf(userAccount.getHonorificSuffix()));
		}

		if (userAccount.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(userAccount.getId()));
		}

		if (userAccount.getImage() == null) {
			map.put("image", null);
		}
		else {
			map.put("image", String.valueOf(userAccount.getImage()));
		}

		if (userAccount.getImageExternalReferenceCode() == null) {
			map.put("imageExternalReferenceCode", null);
		}
		else {
			map.put(
				"imageExternalReferenceCode",
				String.valueOf(userAccount.getImageExternalReferenceCode()));
		}

		if (userAccount.getImageId() == null) {
			map.put("imageId", null);
		}
		else {
			map.put("imageId", String.valueOf(userAccount.getImageId()));
		}

		if (userAccount.getJobTitle() == null) {
			map.put("jobTitle", null);
		}
		else {
			map.put("jobTitle", String.valueOf(userAccount.getJobTitle()));
		}

		if (userAccount.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(userAccount.getKeywords()));
		}

		if (userAccount.getLanguageDisplayName() == null) {
			map.put("languageDisplayName", null);
		}
		else {
			map.put(
				"languageDisplayName",
				String.valueOf(userAccount.getLanguageDisplayName()));
		}

		if (userAccount.getLanguageId() == null) {
			map.put("languageId", null);
		}
		else {
			map.put("languageId", String.valueOf(userAccount.getLanguageId()));
		}

		if (userAccount.getLastLoginDate() == null) {
			map.put("lastLoginDate", null);
		}
		else {
			map.put(
				"lastLoginDate",
				liferayToJSONDateFormat.format(userAccount.getLastLoginDate()));
		}

		if (userAccount.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(userAccount.getName()));
		}

		if (userAccount.getOrganizationBriefs() == null) {
			map.put("organizationBriefs", null);
		}
		else {
			map.put(
				"organizationBriefs",
				String.valueOf(userAccount.getOrganizationBriefs()));
		}

		if (userAccount.getPassword() == null) {
			map.put("password", null);
		}
		else {
			map.put("password", String.valueOf(userAccount.getPassword()));
		}

		if (userAccount.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions", String.valueOf(userAccount.getPermissions()));
		}

		if (userAccount.getProfileURL() == null) {
			map.put("profileURL", null);
		}
		else {
			map.put("profileURL", String.valueOf(userAccount.getProfileURL()));
		}

		if (userAccount.getRoleBriefs() == null) {
			map.put("roleBriefs", null);
		}
		else {
			map.put("roleBriefs", String.valueOf(userAccount.getRoleBriefs()));
		}

		if (userAccount.getSiteBriefs() == null) {
			map.put("siteBriefs", null);
		}
		else {
			map.put("siteBriefs", String.valueOf(userAccount.getSiteBriefs()));
		}

		if (userAccount.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(userAccount.getStatus()));
		}

		if (userAccount.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(userAccount.getTaxonomyCategoryBriefs()));
		}

		if (userAccount.getUserAccountContactInformation() == null) {
			map.put("userAccountContactInformation", null);
		}
		else {
			map.put(
				"userAccountContactInformation",
				String.valueOf(userAccount.getUserAccountContactInformation()));
		}

		if (userAccount.getUserGroupBriefs() == null) {
			map.put("userGroupBriefs", null);
		}
		else {
			map.put(
				"userGroupBriefs",
				String.valueOf(userAccount.getUserGroupBriefs()));
		}

		return map;
	}

	public static class UserAccountJSONParser
		extends BaseJSONParser<UserAccount> {

		@Override
		protected UserAccount createDTO() {
			return new UserAccount();
		}

		@Override
		protected UserAccount[] createDTOArray(int size) {
			return new UserAccount[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "additionalName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "alternateName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "assetLibraryBriefs")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "birthDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currentPassword")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dashboardURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "familyName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "gender")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "givenName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hasLoginDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "honorificPrefix")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "honorificSuffix")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "image")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "imageExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "imageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "jobTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "languageDisplayName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "languageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastLoginDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "organizationBriefs")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "password")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "profileURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "userAccountContactInformation")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userGroupBriefs")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserAccount userAccount, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountBrief[] accountBriefsArray =
						new AccountBrief[jsonParserFieldValues.length];

					for (int i = 0; i < accountBriefsArray.length; i++) {
						accountBriefsArray[i] = AccountBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					userAccount.setAccountBriefs(accountBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					userAccount.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "additionalName")) {
				if (jsonParserFieldValue != null) {
					userAccount.setAdditionalName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "alternateName")) {
				if (jsonParserFieldValue != null) {
					userAccount.setAlternateName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "assetLibraryBriefs")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AssetLibraryBrief[] assetLibraryBriefsArray =
						new AssetLibraryBrief[jsonParserFieldValues.length];

					for (int i = 0; i < assetLibraryBriefsArray.length; i++) {
						assetLibraryBriefsArray[i] =
							AssetLibraryBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					userAccount.setAssetLibraryBriefs(assetLibraryBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "birthDate")) {
				if (jsonParserFieldValue != null) {
					userAccount.setBirthDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					userAccount.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currentPassword")) {
				if (jsonParserFieldValue != null) {
					userAccount.setCurrentPassword(
						(String)jsonParserFieldValue);
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

					userAccount.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dashboardURL")) {
				if (jsonParserFieldValue != null) {
					userAccount.setDashboardURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					userAccount.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					userAccount.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					userAccount.setEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					userAccount.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "familyName")) {
				if (jsonParserFieldValue != null) {
					userAccount.setFamilyName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "gender")) {
				if (jsonParserFieldValue != null) {
					userAccount.setGender(
						UserAccount.Gender.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "givenName")) {
				if (jsonParserFieldValue != null) {
					userAccount.setGivenName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hasLoginDate")) {
				if (jsonParserFieldValue != null) {
					userAccount.setHasLoginDate((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "honorificPrefix")) {
				if (jsonParserFieldValue != null) {
					userAccount.setHonorificPrefix(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "honorificSuffix")) {
				if (jsonParserFieldValue != null) {
					userAccount.setHonorificSuffix(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					userAccount.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "image")) {
				if (jsonParserFieldValue != null) {
					userAccount.setImage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "imageExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					userAccount.setImageExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "imageId")) {
				if (jsonParserFieldValue != null) {
					userAccount.setImageId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "jobTitle")) {
				if (jsonParserFieldValue != null) {
					userAccount.setJobTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					userAccount.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "languageDisplayName")) {

				if (jsonParserFieldValue != null) {
					userAccount.setLanguageDisplayName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "languageId")) {
				if (jsonParserFieldValue != null) {
					userAccount.setLanguageId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastLoginDate")) {
				if (jsonParserFieldValue != null) {
					userAccount.setLastLoginDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					userAccount.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "organizationBriefs")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					OrganizationBrief[] organizationBriefsArray =
						new OrganizationBrief[jsonParserFieldValues.length];

					for (int i = 0; i < organizationBriefsArray.length; i++) {
						organizationBriefsArray[i] =
							OrganizationBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					userAccount.setOrganizationBriefs(organizationBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "password")) {
				if (jsonParserFieldValue != null) {
					userAccount.setPassword((String)jsonParserFieldValue);
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

					userAccount.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "profileURL")) {
				if (jsonParserFieldValue != null) {
					userAccount.setProfileURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RoleBrief[] roleBriefsArray =
						new RoleBrief[jsonParserFieldValues.length];

					for (int i = 0; i < roleBriefsArray.length; i++) {
						roleBriefsArray[i] = RoleBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					userAccount.setRoleBriefs(roleBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					SiteBrief[] siteBriefsArray =
						new SiteBrief[jsonParserFieldValues.length];

					for (int i = 0; i < siteBriefsArray.length; i++) {
						siteBriefsArray[i] = SiteBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					userAccount.setSiteBriefs(siteBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					userAccount.setStatus(
						UserAccount.Status.create(
							(String)jsonParserFieldValue));
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

					userAccount.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "userAccountContactInformation")) {

				if (jsonParserFieldValue != null) {
					userAccount.setUserAccountContactInformation(
						UserAccountContactInformationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userGroupBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserGroupBrief[] userGroupBriefsArray =
						new UserGroupBrief[jsonParserFieldValues.length];

					for (int i = 0; i < userGroupBriefsArray.length; i++) {
						userGroupBriefsArray[i] = UserGroupBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					userAccount.setUserGroupBriefs(userGroupBriefsArray);
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