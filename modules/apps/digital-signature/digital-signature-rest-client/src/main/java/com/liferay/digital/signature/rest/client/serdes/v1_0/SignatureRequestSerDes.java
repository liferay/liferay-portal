/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.serdes.v1_0;

import com.liferay.digital.signature.rest.client.dto.v1_0.FileEntryBrief;
import com.liferay.digital.signature.rest.client.dto.v1_0.SignatureRequest;
import com.liferay.digital.signature.rest.client.dto.v1_0.SignatureRequestRecipient;
import com.liferay.digital.signature.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class SignatureRequestSerDes {

	public static SignatureRequest toDTO(String json) {
		SignatureRequestJSONParser signatureRequestJSONParser =
			new SignatureRequestJSONParser();

		return signatureRequestJSONParser.parseToDTO(json);
	}

	public static SignatureRequest[] toDTOs(String json) {
		SignatureRequestJSONParser signatureRequestJSONParser =
			new SignatureRequestJSONParser();

		return signatureRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SignatureRequest signatureRequest) {
		if (signatureRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (signatureRequest.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(signatureRequest.getActions()));
		}

		if (signatureRequest.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(signatureRequest.getCreator());
		}

		if (signatureRequest.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					signatureRequest.getDateCreated()));

			sb.append("\"");
		}

		if (signatureRequest.getEmailMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailMessage\": ");

			sb.append("\"");

			sb.append(_escape(signatureRequest.getEmailMessage()));

			sb.append("\"");
		}

		if (signatureRequest.getEmailSubject() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailSubject\": ");

			sb.append("\"");

			sb.append(_escape(signatureRequest.getEmailSubject()));

			sb.append("\"");
		}

		if (signatureRequest.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					signatureRequest.getExpirationDate()));

			sb.append("\"");
		}

		if (signatureRequest.getExpireAfter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expireAfter\": ");

			sb.append(signatureRequest.getExpireAfter());
		}

		if (signatureRequest.getFileEntryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < signatureRequest.getFileEntryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(signatureRequest.getFileEntryBriefs()[i]));

				if ((i + 1) < signatureRequest.getFileEntryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (signatureRequest.getFileEntryIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryIds\": ");

			sb.append("[");

			for (int i = 0; i < signatureRequest.getFileEntryIds().length;
				 i++) {

				sb.append(signatureRequest.getFileEntryIds()[i]);

				if ((i + 1) < signatureRequest.getFileEntryIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (signatureRequest.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(signatureRequest.getId());
		}

		if (signatureRequest.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(signatureRequest.getName()));

			sb.append("\"");
		}

		if (signatureRequest.getSendNotifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sendNotifications\": ");

			sb.append(signatureRequest.getSendNotifications());
		}

		if (signatureRequest.getSignatureRequestRecipients() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signatureRequestRecipients\": ");

			sb.append("[");

			for (int i = 0;
				 i < signatureRequest.getSignatureRequestRecipients().length;
				 i++) {

				sb.append(
					String.valueOf(
						signatureRequest.getSignatureRequestRecipients()[i]));

				if ((i + 1) <
						signatureRequest.
							getSignatureRequestRecipients().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (signatureRequest.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(signatureRequest.getStatus()));

			sb.append("\"");
		}

		if (signatureRequest.getStatusDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					signatureRequest.getStatusDate()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SignatureRequestJSONParser signatureRequestJSONParser =
			new SignatureRequestJSONParser();

		return signatureRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SignatureRequest signatureRequest) {
		if (signatureRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (signatureRequest.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(signatureRequest.getActions()));
		}

		if (signatureRequest.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(signatureRequest.getCreator()));
		}

		if (signatureRequest.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					signatureRequest.getDateCreated()));
		}

		if (signatureRequest.getEmailMessage() == null) {
			map.put("emailMessage", null);
		}
		else {
			map.put(
				"emailMessage",
				String.valueOf(signatureRequest.getEmailMessage()));
		}

		if (signatureRequest.getEmailSubject() == null) {
			map.put("emailSubject", null);
		}
		else {
			map.put(
				"emailSubject",
				String.valueOf(signatureRequest.getEmailSubject()));
		}

		if (signatureRequest.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(
					signatureRequest.getExpirationDate()));
		}

		if (signatureRequest.getExpireAfter() == null) {
			map.put("expireAfter", null);
		}
		else {
			map.put(
				"expireAfter",
				String.valueOf(signatureRequest.getExpireAfter()));
		}

		if (signatureRequest.getFileEntryBriefs() == null) {
			map.put("fileEntryBriefs", null);
		}
		else {
			map.put(
				"fileEntryBriefs",
				String.valueOf(signatureRequest.getFileEntryBriefs()));
		}

		if (signatureRequest.getFileEntryIds() == null) {
			map.put("fileEntryIds", null);
		}
		else {
			map.put(
				"fileEntryIds",
				String.valueOf(signatureRequest.getFileEntryIds()));
		}

		if (signatureRequest.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(signatureRequest.getId()));
		}

		if (signatureRequest.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(signatureRequest.getName()));
		}

		if (signatureRequest.getSendNotifications() == null) {
			map.put("sendNotifications", null);
		}
		else {
			map.put(
				"sendNotifications",
				String.valueOf(signatureRequest.getSendNotifications()));
		}

		if (signatureRequest.getSignatureRequestRecipients() == null) {
			map.put("signatureRequestRecipients", null);
		}
		else {
			map.put(
				"signatureRequestRecipients",
				String.valueOf(
					signatureRequest.getSignatureRequestRecipients()));
		}

		if (signatureRequest.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(signatureRequest.getStatus()));
		}

		if (signatureRequest.getStatusDate() == null) {
			map.put("statusDate", null);
		}
		else {
			map.put(
				"statusDate",
				liferayToJSONDateFormat.format(
					signatureRequest.getStatusDate()));
		}

		return map;
	}

	public static class SignatureRequestJSONParser
		extends BaseJSONParser<SignatureRequest> {

		@Override
		protected SignatureRequest createDTO() {
			return new SignatureRequest();
		}

		@Override
		protected SignatureRequest[] createDTOArray(int size) {
			return new SignatureRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailMessage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailSubject")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expireAfter")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileEntryBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileEntryIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sendNotifications")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "signatureRequestRecipients")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "statusDate")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SignatureRequest signatureRequest, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailMessage")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setEmailMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailSubject")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setEmailSubject(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expireAfter")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setExpireAfter(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileEntryBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FileEntryBrief[] fileEntryBriefsArray =
						new FileEntryBrief[jsonParserFieldValues.length];

					for (int i = 0; i < fileEntryBriefsArray.length; i++) {
						fileEntryBriefsArray[i] = FileEntryBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					signatureRequest.setFileEntryBriefs(fileEntryBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileEntryIds")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setFileEntryIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sendNotifications")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setSendNotifications(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "signatureRequestRecipients")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					SignatureRequestRecipient[]
						signatureRequestRecipientsArray =
							new SignatureRequestRecipient
								[jsonParserFieldValues.length];

					for (int i = 0; i < signatureRequestRecipientsArray.length;
						 i++) {

						signatureRequestRecipientsArray[i] =
							SignatureRequestRecipientSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					signatureRequest.setSignatureRequestRecipients(
						signatureRequestRecipientsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "statusDate")) {
				if (jsonParserFieldValue != null) {
					signatureRequest.setStatusDate(
						toDate((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-233590803