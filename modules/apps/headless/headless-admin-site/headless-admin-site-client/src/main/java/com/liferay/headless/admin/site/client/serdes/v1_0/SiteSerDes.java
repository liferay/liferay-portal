/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.Site;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class SiteSerDes {

	public static Site toDTO(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToDTO(json);
	}

	public static Site[] toDTOs(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Site site) {
		if (site == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (site.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(site.getActive());
		}

		if (site.getAnalyticsConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"analyticsConfiguration\": ");

			sb.append(String.valueOf(site.getAnalyticsConfiguration()));
		}

		if (site.getAssetAutoTaggingEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetAutoTaggingEnabled\": ");

			sb.append(site.getAssetAutoTaggingEnabled());
		}

		if (site.getContentSharingWithChildrenEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentSharingWithChildrenEnabled\": ");

			sb.append(site.getContentSharingWithChildrenEnabled());
		}

		if (site.getDefaultLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(site.getDefaultLanguageId()));

			sb.append("\"");
		}

		if (site.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(site.getDescription()));

			sb.append("\"");
		}

		if (site.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(site.getDescription_i18n()));
		}

		if (site.getDescriptiveName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(site.getDescriptiveName()));

			sb.append("\"");
		}

		if (site.getDescriptiveName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName_i18n\": ");

			sb.append(_toJSON(site.getDescriptiveName_i18n()));
		}

		if (site.getDirectoryIndexingEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"directoryIndexingEnabled\": ");

			sb.append(site.getDirectoryIndexingEnabled());
		}

		if (site.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(site.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (site.getFriendlyUrlPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(site.getFriendlyUrlPath()));

			sb.append("\"");
		}

		if (site.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(site.getId());
		}

		if (site.getInheritLocales() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inheritLocales\": ");

			sb.append(site.getInheritLocales());
		}

		if (site.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(site.getKey()));

			sb.append("\"");
		}

		if (site.getLocales() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"locales\": ");

			sb.append("[");

			for (int i = 0; i < site.getLocales().length; i++) {
				sb.append(_toJSON(site.getLocales()[i]));

				if ((i + 1) < site.getLocales().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (site.getLogo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append("\"");

			sb.append(_escape(site.getLogo()));

			sb.append("\"");
		}

		if (site.getManualMembership() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"manualMembership\": ");

			sb.append(site.getManualMembership());
		}

		if (site.getMapProviderKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mapProviderKey\": ");

			sb.append("\"");
			sb.append(site.getMapProviderKey());
			sb.append("\"");
		}

		if (site.getMembershipRestriction() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"membershipRestriction\": ");

			sb.append(site.getMembershipRestriction());
		}

		if (site.getMembershipType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"membershipType\": ");

			sb.append("\"");
			sb.append(site.getMembershipType());
			sb.append("\"");
		}

		if (site.getMentionsEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mentionsEnabled\": ");

			sb.append(site.getMentionsEnabled());
		}

		if (site.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(site.getName()));

			sb.append("\"");
		}

		if (site.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(site.getName_i18n()));
		}

		if (site.getParentSiteExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSiteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(site.getParentSiteExternalReferenceCode()));

			sb.append("\"");
		}

		if (site.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < site.getPermissions().length; i++) {
				sb.append(site.getPermissions()[i]);

				if ((i + 1) < site.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (site.getRatingsTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingsTypes\": ");

			sb.append(String.valueOf(site.getRatingsTypes()));
		}

		if (site.getSharingEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sharingEnabled\": ");

			sb.append(site.getSharingEnabled());
		}

		if (site.getTemplateKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateKey\": ");

			sb.append("\"");

			sb.append(_escape(site.getTemplateKey()));

			sb.append("\"");
		}

		if (site.getTemplateType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateType\": ");

			sb.append("\"");
			sb.append(site.getTemplateType());
			sb.append("\"");
		}

		if (site.getTrashEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trashEnabled\": ");

			sb.append(site.getTrashEnabled());
		}

		if (site.getTrashEntriesMaxAge() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trashEntriesMaxAge\": ");

			sb.append(site.getTrashEntriesMaxAge());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Site site) {
		if (site == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (site.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(site.getActive()));
		}

		if (site.getAnalyticsConfiguration() == null) {
			map.put("analyticsConfiguration", null);
		}
		else {
			map.put(
				"analyticsConfiguration",
				String.valueOf(site.getAnalyticsConfiguration()));
		}

		if (site.getAssetAutoTaggingEnabled() == null) {
			map.put("assetAutoTaggingEnabled", null);
		}
		else {
			map.put(
				"assetAutoTaggingEnabled",
				String.valueOf(site.getAssetAutoTaggingEnabled()));
		}

		if (site.getContentSharingWithChildrenEnabled() == null) {
			map.put("contentSharingWithChildrenEnabled", null);
		}
		else {
			map.put(
				"contentSharingWithChildrenEnabled",
				String.valueOf(site.getContentSharingWithChildrenEnabled()));
		}

		if (site.getDefaultLanguageId() == null) {
			map.put("defaultLanguageId", null);
		}
		else {
			map.put(
				"defaultLanguageId",
				String.valueOf(site.getDefaultLanguageId()));
		}

		if (site.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(site.getDescription()));
		}

		if (site.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n", String.valueOf(site.getDescription_i18n()));
		}

		if (site.getDescriptiveName() == null) {
			map.put("descriptiveName", null);
		}
		else {
			map.put(
				"descriptiveName", String.valueOf(site.getDescriptiveName()));
		}

		if (site.getDescriptiveName_i18n() == null) {
			map.put("descriptiveName_i18n", null);
		}
		else {
			map.put(
				"descriptiveName_i18n",
				String.valueOf(site.getDescriptiveName_i18n()));
		}

		if (site.getDirectoryIndexingEnabled() == null) {
			map.put("directoryIndexingEnabled", null);
		}
		else {
			map.put(
				"directoryIndexingEnabled",
				String.valueOf(site.getDirectoryIndexingEnabled()));
		}

		if (site.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(site.getExternalReferenceCode()));
		}

		if (site.getFriendlyUrlPath() == null) {
			map.put("friendlyUrlPath", null);
		}
		else {
			map.put(
				"friendlyUrlPath", String.valueOf(site.getFriendlyUrlPath()));
		}

		if (site.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(site.getId()));
		}

		if (site.getInheritLocales() == null) {
			map.put("inheritLocales", null);
		}
		else {
			map.put("inheritLocales", String.valueOf(site.getInheritLocales()));
		}

		if (site.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(site.getKey()));
		}

		if (site.getLocales() == null) {
			map.put("locales", null);
		}
		else {
			map.put("locales", String.valueOf(site.getLocales()));
		}

		if (site.getLogo() == null) {
			map.put("logo", null);
		}
		else {
			map.put("logo", String.valueOf(site.getLogo()));
		}

		if (site.getManualMembership() == null) {
			map.put("manualMembership", null);
		}
		else {
			map.put(
				"manualMembership", String.valueOf(site.getManualMembership()));
		}

		if (site.getMapProviderKey() == null) {
			map.put("mapProviderKey", null);
		}
		else {
			map.put("mapProviderKey", String.valueOf(site.getMapProviderKey()));
		}

		if (site.getMembershipRestriction() == null) {
			map.put("membershipRestriction", null);
		}
		else {
			map.put(
				"membershipRestriction",
				String.valueOf(site.getMembershipRestriction()));
		}

		if (site.getMembershipType() == null) {
			map.put("membershipType", null);
		}
		else {
			map.put("membershipType", String.valueOf(site.getMembershipType()));
		}

		if (site.getMentionsEnabled() == null) {
			map.put("mentionsEnabled", null);
		}
		else {
			map.put(
				"mentionsEnabled", String.valueOf(site.getMentionsEnabled()));
		}

		if (site.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(site.getName()));
		}

		if (site.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(site.getName_i18n()));
		}

		if (site.getParentSiteExternalReferenceCode() == null) {
			map.put("parentSiteExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentSiteExternalReferenceCode",
				String.valueOf(site.getParentSiteExternalReferenceCode()));
		}

		if (site.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put("permissions", String.valueOf(site.getPermissions()));
		}

		if (site.getRatingsTypes() == null) {
			map.put("ratingsTypes", null);
		}
		else {
			map.put("ratingsTypes", String.valueOf(site.getRatingsTypes()));
		}

		if (site.getSharingEnabled() == null) {
			map.put("sharingEnabled", null);
		}
		else {
			map.put("sharingEnabled", String.valueOf(site.getSharingEnabled()));
		}

		if (site.getTemplateKey() == null) {
			map.put("templateKey", null);
		}
		else {
			map.put("templateKey", String.valueOf(site.getTemplateKey()));
		}

		if (site.getTemplateType() == null) {
			map.put("templateType", null);
		}
		else {
			map.put("templateType", String.valueOf(site.getTemplateType()));
		}

		if (site.getTrashEnabled() == null) {
			map.put("trashEnabled", null);
		}
		else {
			map.put("trashEnabled", String.valueOf(site.getTrashEnabled()));
		}

		if (site.getTrashEntriesMaxAge() == null) {
			map.put("trashEntriesMaxAge", null);
		}
		else {
			map.put(
				"trashEntriesMaxAge",
				String.valueOf(site.getTrashEntriesMaxAge()));
		}

		return map;
	}

	public static class SiteJSONParser extends BaseJSONParser<Site> {

		@Override
		protected Site createDTO() {
			return new Site();
		}

		@Override
		protected Site[] createDTOArray(int size) {
			return new Site[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "analyticsConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "assetAutoTaggingEnabled")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"contentSharingWithChildrenEnabled")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "descriptiveName_i18n")) {

				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "directoryIndexingEnabled")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "inheritLocales")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "locales")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "manualMembership")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mapProviderKey")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "membershipRestriction")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "membershipType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mentionsEnabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentSiteExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ratingsTypes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sharingEnabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "templateType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "trashEnabled")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "trashEntriesMaxAge")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Site site, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					site.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "analyticsConfiguration")) {

				if (jsonParserFieldValue != null) {
					site.setAnalyticsConfiguration(
						AnalyticsConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "assetAutoTaggingEnabled")) {

				if (jsonParserFieldValue != null) {
					site.setAssetAutoTaggingEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"contentSharingWithChildrenEnabled")) {

				if (jsonParserFieldValue != null) {
					site.setContentSharingWithChildrenEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				if (jsonParserFieldValue != null) {
					site.setDefaultLanguageId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					site.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					site.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				if (jsonParserFieldValue != null) {
					site.setDescriptiveName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "descriptiveName_i18n")) {

				if (jsonParserFieldValue != null) {
					site.setDescriptiveName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "directoryIndexingEnabled")) {

				if (jsonParserFieldValue != null) {
					site.setDirectoryIndexingEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					site.setExternalReferenceCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				if (jsonParserFieldValue != null) {
					site.setFriendlyUrlPath((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					site.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "inheritLocales")) {
				if (jsonParserFieldValue != null) {
					site.setInheritLocales((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					site.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "locales")) {
				if (jsonParserFieldValue != null) {
					site.setLocales(toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				if (jsonParserFieldValue != null) {
					site.setLogo((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "manualMembership")) {
				if (jsonParserFieldValue != null) {
					site.setManualMembership((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mapProviderKey")) {
				if (jsonParserFieldValue != null) {
					site.setMapProviderKey(
						Site.MapProviderKey.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "membershipRestriction")) {

				if (jsonParserFieldValue != null) {
					site.setMembershipRestriction(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "membershipType")) {
				if (jsonParserFieldValue != null) {
					site.setMembershipType(
						Site.MembershipType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mentionsEnabled")) {
				if (jsonParserFieldValue != null) {
					site.setMentionsEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					site.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					site.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentSiteExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					site.setParentSiteExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.site.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.admin.site.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.admin.site.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					site.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratingsTypes")) {
				if (jsonParserFieldValue != null) {
					site.setRatingsTypes(
						RatingsTypesSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sharingEnabled")) {
				if (jsonParserFieldValue != null) {
					site.setSharingEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				if (jsonParserFieldValue != null) {
					site.setTemplateKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "templateType")) {
				if (jsonParserFieldValue != null) {
					site.setTemplateType(
						Site.TemplateType.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "trashEnabled")) {
				if (jsonParserFieldValue != null) {
					site.setTrashEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "trashEntriesMaxAge")) {

				if (jsonParserFieldValue != null) {
					site.setTrashEntriesMaxAge(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:504885770