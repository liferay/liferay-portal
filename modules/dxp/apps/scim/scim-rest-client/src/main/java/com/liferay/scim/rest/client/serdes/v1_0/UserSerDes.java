/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.MultiValuedAttribute;
import com.liferay.scim.rest.client.dto.v1_0.User;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class UserSerDes {

	public static User toDTO(String json) {
		UserJSONParser userJSONParser = new UserJSONParser();

		return userJSONParser.parseToDTO(json);
	}

	public static User[] toDTOs(String json) {
		UserJSONParser userJSONParser = new UserJSONParser();

		return userJSONParser.parseToDTOs(json);
	}

	public static String toJSON(User user) {
		if (user == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (user.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(user.getActive());
		}

		if (user.getAddresses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addresses\": ");

			sb.append("[");

			for (int i = 0; i < user.getAddresses().length; i++) {
				sb.append(_toJSON(user.getAddresses()[i]));

				if ((i + 1) < user.getAddresses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getDisplayName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayName\": ");

			sb.append("\"");

			sb.append(_escape(user.getDisplayName()));

			sb.append("\"");
		}

		if (user.getEmails() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emails\": ");

			sb.append("[");

			for (int i = 0; i < user.getEmails().length; i++) {
				sb.append(String.valueOf(user.getEmails()[i]));

				if ((i + 1) < user.getEmails().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getEntitlements() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entitlements\": ");

			sb.append("[");

			for (int i = 0; i < user.getEntitlements().length; i++) {
				sb.append(String.valueOf(user.getEntitlements()[i]));

				if ((i + 1) < user.getEntitlements().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getExternalId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalId\": ");

			sb.append("\"");

			sb.append(_escape(user.getExternalId()));

			sb.append("\"");
		}

		if (user.getGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groups\": ");

			sb.append("[");

			for (int i = 0; i < user.getGroups().length; i++) {
				sb.append(String.valueOf(user.getGroups()[i]));

				if ((i + 1) < user.getGroups().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(user.getId()));

			sb.append("\"");
		}

		if (user.getIms() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ims\": ");

			sb.append("[");

			for (int i = 0; i < user.getIms().length; i++) {
				sb.append(String.valueOf(user.getIms()[i]));

				if ((i + 1) < user.getIms().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getLocale() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"locale\": ");

			sb.append("\"");

			sb.append(_escape(user.getLocale()));

			sb.append("\"");
		}

		if (user.getMeta() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(user.getMeta()));
		}

		if (user.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(String.valueOf(user.getName()));
		}

		if (user.getNickName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nickName\": ");

			sb.append("\"");

			sb.append(_escape(user.getNickName()));

			sb.append("\"");
		}

		if (user.getPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"password\": ");

			sb.append("\"");

			sb.append(_escape(user.getPassword()));

			sb.append("\"");
		}

		if (user.getPhoneNumbers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumbers\": ");

			sb.append("[");

			for (int i = 0; i < user.getPhoneNumbers().length; i++) {
				sb.append(String.valueOf(user.getPhoneNumbers()[i]));

				if ((i + 1) < user.getPhoneNumbers().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getPhotos() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"photos\": ");

			sb.append("[");

			for (int i = 0; i < user.getPhotos().length; i++) {
				sb.append(String.valueOf(user.getPhotos()[i]));

				if ((i + 1) < user.getPhotos().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getPreferredLanguage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"preferredLanguage\": ");

			sb.append("\"");

			sb.append(_escape(user.getPreferredLanguage()));

			sb.append("\"");
		}

		if (user.getProfileUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"profileUrl\": ");

			sb.append("\"");

			sb.append(_escape(user.getProfileUrl()));

			sb.append("\"");
		}

		if (user.getRoles() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roles\": ");

			sb.append("[");

			for (int i = 0; i < user.getRoles().length; i++) {
				sb.append(String.valueOf(user.getRoles()[i]));

				if ((i + 1) < user.getRoles().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < user.getSchemas().length; i++) {
				sb.append(_toJSON(user.getSchemas()[i]));

				if ((i + 1) < user.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (user.getTimezone() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timezone\": ");

			sb.append("\"");

			sb.append(_escape(user.getTimezone()));

			sb.append("\"");
		}

		if (user.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(user.getTitle()));

			sb.append("\"");
		}

		if (user.getUrn_ietf_params_scim_schemas_extension_liferay_2_0_User() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"urn:ietf:params:scim:schemas:extension:liferay:2.0:User\": ");

			sb.append(
				String.valueOf(
					user.
						getUrn_ietf_params_scim_schemas_extension_liferay_2_0_User()));
		}

		if (user.getUserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(user.getUserName()));

			sb.append("\"");
		}

		if (user.getUserType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userType\": ");

			sb.append("\"");

			sb.append(_escape(user.getUserType()));

			sb.append("\"");
		}

		if (user.getX509Certificates() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"x509Certificates\": ");

			sb.append("[");

			for (int i = 0; i < user.getX509Certificates().length; i++) {
				sb.append(String.valueOf(user.getX509Certificates()[i]));

				if ((i + 1) < user.getX509Certificates().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserJSONParser userJSONParser = new UserJSONParser();

		return userJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(User user) {
		if (user == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (user.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(user.getActive()));
		}

		if (user.getAddresses() == null) {
			map.put("addresses", null);
		}
		else {
			map.put("addresses", String.valueOf(user.getAddresses()));
		}

		if (user.getDisplayName() == null) {
			map.put("displayName", null);
		}
		else {
			map.put("displayName", String.valueOf(user.getDisplayName()));
		}

		if (user.getEmails() == null) {
			map.put("emails", null);
		}
		else {
			map.put("emails", String.valueOf(user.getEmails()));
		}

		if (user.getEntitlements() == null) {
			map.put("entitlements", null);
		}
		else {
			map.put("entitlements", String.valueOf(user.getEntitlements()));
		}

		if (user.getExternalId() == null) {
			map.put("externalId", null);
		}
		else {
			map.put("externalId", String.valueOf(user.getExternalId()));
		}

		if (user.getGroups() == null) {
			map.put("groups", null);
		}
		else {
			map.put("groups", String.valueOf(user.getGroups()));
		}

		if (user.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(user.getId()));
		}

		if (user.getIms() == null) {
			map.put("ims", null);
		}
		else {
			map.put("ims", String.valueOf(user.getIms()));
		}

		if (user.getLocale() == null) {
			map.put("locale", null);
		}
		else {
			map.put("locale", String.valueOf(user.getLocale()));
		}

		if (user.getMeta() == null) {
			map.put("meta", null);
		}
		else {
			map.put("meta", String.valueOf(user.getMeta()));
		}

		if (user.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(user.getName()));
		}

		if (user.getNickName() == null) {
			map.put("nickName", null);
		}
		else {
			map.put("nickName", String.valueOf(user.getNickName()));
		}

		if (user.getPassword() == null) {
			map.put("password", null);
		}
		else {
			map.put("password", String.valueOf(user.getPassword()));
		}

		if (user.getPhoneNumbers() == null) {
			map.put("phoneNumbers", null);
		}
		else {
			map.put("phoneNumbers", String.valueOf(user.getPhoneNumbers()));
		}

		if (user.getPhotos() == null) {
			map.put("photos", null);
		}
		else {
			map.put("photos", String.valueOf(user.getPhotos()));
		}

		if (user.getPreferredLanguage() == null) {
			map.put("preferredLanguage", null);
		}
		else {
			map.put(
				"preferredLanguage",
				String.valueOf(user.getPreferredLanguage()));
		}

		if (user.getProfileUrl() == null) {
			map.put("profileUrl", null);
		}
		else {
			map.put("profileUrl", String.valueOf(user.getProfileUrl()));
		}

		if (user.getRoles() == null) {
			map.put("roles", null);
		}
		else {
			map.put("roles", String.valueOf(user.getRoles()));
		}

		if (user.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put("schemas", String.valueOf(user.getSchemas()));
		}

		if (user.getTimezone() == null) {
			map.put("timezone", null);
		}
		else {
			map.put("timezone", String.valueOf(user.getTimezone()));
		}

		if (user.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(user.getTitle()));
		}

		if (user.getUrn_ietf_params_scim_schemas_extension_liferay_2_0_User() ==
				null) {

			map.put(
				"urn:ietf:params:scim:schemas:extension:liferay:2.0:User",
				null);
		}
		else {
			map.put(
				"urn:ietf:params:scim:schemas:extension:liferay:2.0:User",
				String.valueOf(
					user.
						getUrn_ietf_params_scim_schemas_extension_liferay_2_0_User()));
		}

		if (user.getUserName() == null) {
			map.put("userName", null);
		}
		else {
			map.put("userName", String.valueOf(user.getUserName()));
		}

		if (user.getUserType() == null) {
			map.put("userType", null);
		}
		else {
			map.put("userType", String.valueOf(user.getUserType()));
		}

		if (user.getX509Certificates() == null) {
			map.put("x509Certificates", null);
		}
		else {
			map.put(
				"x509Certificates", String.valueOf(user.getX509Certificates()));
		}

		return map;
	}

	public static class UserJSONParser extends BaseJSONParser<User> {

		@Override
		protected User createDTO() {
			return new User();
		}

		@Override
		protected User[] createDTOArray(int size) {
			return new User[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "addresses")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "displayName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emails")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "entitlements")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "externalId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "groups")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ims")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "locale")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "nickName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "password")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumbers")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "photos")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "preferredLanguage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "profileUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roles")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "timezone")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"urn:ietf:params:scim:schemas:extension:liferay:2.0:User")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "x509Certificates")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			User user, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					user.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addresses")) {
				if (jsonParserFieldValue != null) {
					user.setAddresses((Object[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayName")) {
				if (jsonParserFieldValue != null) {
					user.setDisplayName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emails")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] emailsArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < emailsArray.length; i++) {
						emailsArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					user.setEmails(emailsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entitlements")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] entitlementsArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < entitlementsArray.length; i++) {
						entitlementsArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					user.setEntitlements(entitlementsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "externalId")) {
				if (jsonParserFieldValue != null) {
					user.setExternalId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "groups")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] groupsArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < groupsArray.length; i++) {
						groupsArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					user.setGroups(groupsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					user.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ims")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] imsArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < imsArray.length; i++) {
						imsArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					user.setIms(imsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "locale")) {
				if (jsonParserFieldValue != null) {
					user.setLocale((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				if (jsonParserFieldValue != null) {
					user.setMeta(
						MetaSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					user.setName(
						NameSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nickName")) {
				if (jsonParserFieldValue != null) {
					user.setNickName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "password")) {
				if (jsonParserFieldValue != null) {
					user.setPassword((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumbers")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] phoneNumbersArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < phoneNumbersArray.length; i++) {
						phoneNumbersArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					user.setPhoneNumbers(phoneNumbersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "photos")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] photosArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < photosArray.length; i++) {
						photosArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					user.setPhotos(photosArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "preferredLanguage")) {
				if (jsonParserFieldValue != null) {
					user.setPreferredLanguage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "profileUrl")) {
				if (jsonParserFieldValue != null) {
					user.setProfileUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roles")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] rolesArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < rolesArray.length; i++) {
						rolesArray[i] = MultiValuedAttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					user.setRoles(rolesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					user.setSchemas(toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "timezone")) {
				if (jsonParserFieldValue != null) {
					user.setTimezone((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					user.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"urn:ietf:params:scim:schemas:extension:liferay:2.0:User")) {

				if (jsonParserFieldValue != null) {
					user.
						setUrn_ietf_params_scim_schemas_extension_liferay_2_0_User(
							UserSchemaExtensionSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				if (jsonParserFieldValue != null) {
					user.setUserName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userType")) {
				if (jsonParserFieldValue != null) {
					user.setUserType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "x509Certificates")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MultiValuedAttribute[] x509CertificatesArray =
						new MultiValuedAttribute[jsonParserFieldValues.length];

					for (int i = 0; i < x509CertificatesArray.length; i++) {
						x509CertificatesArray[i] =
							MultiValuedAttributeSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					user.setX509Certificates(x509CertificatesArray);
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