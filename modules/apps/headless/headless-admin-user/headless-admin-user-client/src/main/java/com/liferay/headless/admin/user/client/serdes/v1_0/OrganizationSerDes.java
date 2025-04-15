/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Organization;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Service;
import com.liferay.headless.admin.user.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountBrief;
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
public class OrganizationSerDes {

	public static Organization toDTO(String json) {
		OrganizationJSONParser organizationJSONParser =
			new OrganizationJSONParser();

		return organizationJSONParser.parseToDTO(json);
	}

	public static Organization[] toDTOs(String json) {
		OrganizationJSONParser organizationJSONParser =
			new OrganizationJSONParser();

		return organizationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Organization organization) {
		if (organization == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (organization.getAccountBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountBriefs\": ");

			sb.append("[");

			for (int i = 0; i < organization.getAccountBriefs().length; i++) {
				sb.append(String.valueOf(organization.getAccountBriefs()[i]));

				if ((i + 1) < organization.getAccountBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(organization.getActions()));
		}

		if (organization.getChildOrganizations() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"childOrganizations\": ");

			sb.append("[");

			for (int i = 0; i < organization.getChildOrganizations().length;
				 i++) {

				sb.append(
					String.valueOf(organization.getChildOrganizations()[i]));

				if ((i + 1) < organization.getChildOrganizations().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getComment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(organization.getComment()));

			sb.append("\"");
		}

		if (organization.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(organization.getCreator()));
		}

		if (organization.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < organization.getCustomFields().length; i++) {
				sb.append(organization.getCustomFields()[i]);

				if ((i + 1) < organization.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(organization.getDateCreated()));

			sb.append("\"");
		}

		if (organization.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(organization.getDateModified()));

			sb.append("\"");
		}

		if (organization.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(organization.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (organization.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(organization.getId()));

			sb.append("\"");
		}

		if (organization.getImage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append("\"");

			sb.append(_escape(organization.getImage()));

			sb.append("\"");
		}

		if (organization.getImageExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(organization.getImageExternalReferenceCode()));

			sb.append("\"");
		}

		if (organization.getImageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageId\": ");

			sb.append(organization.getImageId());
		}

		if (organization.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < organization.getKeywords().length; i++) {
				sb.append(_toJSON(organization.getKeywords()[i]));

				if ((i + 1) < organization.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getLocation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"location\": ");

			sb.append(String.valueOf(organization.getLocation()));
		}

		if (organization.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(organization.getName()));

			sb.append("\"");
		}

		if (organization.getNumberOfAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfAccounts\": ");

			sb.append(organization.getNumberOfAccounts());
		}

		if (organization.getNumberOfOrganizations() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfOrganizations\": ");

			sb.append(organization.getNumberOfOrganizations());
		}

		if (organization.getNumberOfUsers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUsers\": ");

			sb.append(organization.getNumberOfUsers());
		}

		if (organization.getOrganizationAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationAccounts\": ");

			sb.append("[");

			for (int i = 0; i < organization.getOrganizationAccounts().length;
				 i++) {

				sb.append(
					String.valueOf(organization.getOrganizationAccounts()[i]));

				if ((i + 1) < organization.getOrganizationAccounts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getOrganizationContactInformation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationContactInformation\": ");

			sb.append(
				String.valueOf(
					organization.getOrganizationContactInformation()));
		}

		if (organization.getParentOrganization() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOrganization\": ");

			sb.append(String.valueOf(organization.getParentOrganization()));
		}

		if (organization.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < organization.getPermissions().length; i++) {
				sb.append(organization.getPermissions()[i]);

				if ((i + 1) < organization.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getRoleBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < organization.getRoleBriefs().length; i++) {
				sb.append(String.valueOf(organization.getRoleBriefs()[i]));

				if ((i + 1) < organization.getRoleBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getServices() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"services\": ");

			sb.append("[");

			for (int i = 0; i < organization.getServices().length; i++) {
				sb.append(String.valueOf(organization.getServices()[i]));

				if ((i + 1) < organization.getServices().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < organization.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(
						organization.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) < organization.getTaxonomyCategoryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getTreePath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"treePath\": ");

			sb.append("\"");

			sb.append(_escape(organization.getTreePath()));

			sb.append("\"");
		}

		if (organization.getUserAccountBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccountBriefs\": ");

			sb.append("[");

			for (int i = 0; i < organization.getUserAccountBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(organization.getUserAccountBriefs()[i]));

				if ((i + 1) < organization.getUserAccountBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (organization.getUserAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccounts\": ");

			sb.append("[");

			for (int i = 0; i < organization.getUserAccounts().length; i++) {
				sb.append(String.valueOf(organization.getUserAccounts()[i]));

				if ((i + 1) < organization.getUserAccounts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrganizationJSONParser organizationJSONParser =
			new OrganizationJSONParser();

		return organizationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Organization organization) {
		if (organization == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (organization.getAccountBriefs() == null) {
			map.put("accountBriefs", null);
		}
		else {
			map.put(
				"accountBriefs",
				String.valueOf(organization.getAccountBriefs()));
		}

		if (organization.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(organization.getActions()));
		}

		if (organization.getChildOrganizations() == null) {
			map.put("childOrganizations", null);
		}
		else {
			map.put(
				"childOrganizations",
				String.valueOf(organization.getChildOrganizations()));
		}

		if (organization.getComment() == null) {
			map.put("comment", null);
		}
		else {
			map.put("comment", String.valueOf(organization.getComment()));
		}

		if (organization.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(organization.getCreator()));
		}

		if (organization.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(organization.getCustomFields()));
		}

		if (organization.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(organization.getDateCreated()));
		}

		if (organization.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(organization.getDateModified()));
		}

		if (organization.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(organization.getExternalReferenceCode()));
		}

		if (organization.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(organization.getId()));
		}

		if (organization.getImage() == null) {
			map.put("image", null);
		}
		else {
			map.put("image", String.valueOf(organization.getImage()));
		}

		if (organization.getImageExternalReferenceCode() == null) {
			map.put("imageExternalReferenceCode", null);
		}
		else {
			map.put(
				"imageExternalReferenceCode",
				String.valueOf(organization.getImageExternalReferenceCode()));
		}

		if (organization.getImageId() == null) {
			map.put("imageId", null);
		}
		else {
			map.put("imageId", String.valueOf(organization.getImageId()));
		}

		if (organization.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(organization.getKeywords()));
		}

		if (organization.getLocation() == null) {
			map.put("location", null);
		}
		else {
			map.put("location", String.valueOf(organization.getLocation()));
		}

		if (organization.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(organization.getName()));
		}

		if (organization.getNumberOfAccounts() == null) {
			map.put("numberOfAccounts", null);
		}
		else {
			map.put(
				"numberOfAccounts",
				String.valueOf(organization.getNumberOfAccounts()));
		}

		if (organization.getNumberOfOrganizations() == null) {
			map.put("numberOfOrganizations", null);
		}
		else {
			map.put(
				"numberOfOrganizations",
				String.valueOf(organization.getNumberOfOrganizations()));
		}

		if (organization.getNumberOfUsers() == null) {
			map.put("numberOfUsers", null);
		}
		else {
			map.put(
				"numberOfUsers",
				String.valueOf(organization.getNumberOfUsers()));
		}

		if (organization.getOrganizationAccounts() == null) {
			map.put("organizationAccounts", null);
		}
		else {
			map.put(
				"organizationAccounts",
				String.valueOf(organization.getOrganizationAccounts()));
		}

		if (organization.getOrganizationContactInformation() == null) {
			map.put("organizationContactInformation", null);
		}
		else {
			map.put(
				"organizationContactInformation",
				String.valueOf(
					organization.getOrganizationContactInformation()));
		}

		if (organization.getParentOrganization() == null) {
			map.put("parentOrganization", null);
		}
		else {
			map.put(
				"parentOrganization",
				String.valueOf(organization.getParentOrganization()));
		}

		if (organization.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions", String.valueOf(organization.getPermissions()));
		}

		if (organization.getRoleBriefs() == null) {
			map.put("roleBriefs", null);
		}
		else {
			map.put("roleBriefs", String.valueOf(organization.getRoleBriefs()));
		}

		if (organization.getServices() == null) {
			map.put("services", null);
		}
		else {
			map.put("services", String.valueOf(organization.getServices()));
		}

		if (organization.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(organization.getTaxonomyCategoryBriefs()));
		}

		if (organization.getTreePath() == null) {
			map.put("treePath", null);
		}
		else {
			map.put("treePath", String.valueOf(organization.getTreePath()));
		}

		if (organization.getUserAccountBriefs() == null) {
			map.put("userAccountBriefs", null);
		}
		else {
			map.put(
				"userAccountBriefs",
				String.valueOf(organization.getUserAccountBriefs()));
		}

		if (organization.getUserAccounts() == null) {
			map.put("userAccounts", null);
		}
		else {
			map.put(
				"userAccounts", String.valueOf(organization.getUserAccounts()));
		}

		return map;
	}

	public static class OrganizationJSONParser
		extends BaseJSONParser<Organization> {

		@Override
		protected Organization createDTO() {
			return new Organization();
		}

		@Override
		protected Organization[] createDTOArray(int size) {
			return new Organization[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "childOrganizations")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				return false;
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
						jsonParserFieldName, "externalReferenceCode")) {

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
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "location")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfAccounts")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfOrganizations")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfUsers")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "organizationAccounts")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"organizationContactInformation")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentOrganization")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "services")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userAccountBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userAccounts")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Organization organization, String jsonParserFieldName,
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

					organization.setAccountBriefs(accountBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					organization.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "childOrganizations")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Organization[] childOrganizationsArray =
						new Organization[jsonParserFieldValues.length];

					for (int i = 0; i < childOrganizationsArray.length; i++) {
						childOrganizationsArray[i] = OrganizationSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organization.setChildOrganizations(childOrganizationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				if (jsonParserFieldValue != null) {
					organization.setComment((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					organization.setCreator(
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

					organization.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					organization.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					organization.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					organization.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					organization.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "image")) {
				if (jsonParserFieldValue != null) {
					organization.setImage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "imageExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					organization.setImageExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "imageId")) {
				if (jsonParserFieldValue != null) {
					organization.setImageId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					organization.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "location")) {
				if (jsonParserFieldValue != null) {
					organization.setLocation(
						LocationSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					organization.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfAccounts")) {
				if (jsonParserFieldValue != null) {
					organization.setNumberOfAccounts(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfOrganizations")) {

				if (jsonParserFieldValue != null) {
					organization.setNumberOfOrganizations(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfUsers")) {
				if (jsonParserFieldValue != null) {
					organization.setNumberOfUsers(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "organizationAccounts")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Account[] organizationAccountsArray =
						new Account[jsonParserFieldValues.length];

					for (int i = 0; i < organizationAccountsArray.length; i++) {
						organizationAccountsArray[i] = AccountSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organization.setOrganizationAccounts(
						organizationAccountsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"organizationContactInformation")) {

				if (jsonParserFieldValue != null) {
					organization.setOrganizationContactInformation(
						OrganizationContactInformationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentOrganization")) {

				if (jsonParserFieldValue != null) {
					organization.setParentOrganization(
						OrganizationSerDes.toDTO((String)jsonParserFieldValue));
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

					organization.setPermissions(permissionsArray);
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

					organization.setRoleBriefs(roleBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "services")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Service[] servicesArray =
						new Service[jsonParserFieldValues.length];

					for (int i = 0; i < servicesArray.length; i++) {
						servicesArray[i] = ServiceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organization.setServices(servicesArray);
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

					organization.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
				if (jsonParserFieldValue != null) {
					organization.setTreePath((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userAccountBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserAccountBrief[] userAccountBriefsArray =
						new UserAccountBrief[jsonParserFieldValues.length];

					for (int i = 0; i < userAccountBriefsArray.length; i++) {
						userAccountBriefsArray[i] =
							UserAccountBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					organization.setUserAccountBriefs(userAccountBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userAccounts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserAccount[] userAccountsArray =
						new UserAccount[jsonParserFieldValues.length];

					for (int i = 0; i < userAccountsArray.length; i++) {
						userAccountsArray[i] = UserAccountSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organization.setUserAccounts(userAccountsArray);
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