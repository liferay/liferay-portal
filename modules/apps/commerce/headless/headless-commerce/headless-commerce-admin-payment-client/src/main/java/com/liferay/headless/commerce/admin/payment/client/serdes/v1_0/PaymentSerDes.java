/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.payment.client.dto.v1_0.Payment;
import com.liferay.headless.commerce.admin.payment.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class PaymentSerDes {

	public static Payment toDTO(String json) {
		PaymentJSONParser paymentJSONParser = new PaymentJSONParser();

		return paymentJSONParser.parseToDTO(json);
	}

	public static Payment[] toDTOs(String json) {
		PaymentJSONParser paymentJSONParser = new PaymentJSONParser();

		return paymentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Payment payment) {
		if (payment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (payment.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(payment.getActions()));
		}

		if (payment.getAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amount\": ");

			sb.append(payment.getAmount());
		}

		if (payment.getAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(payment.getAmountFormatted()));

			sb.append("\"");
		}

		if (payment.getAuthor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(payment.getAuthor()));

			sb.append("\"");
		}

		if (payment.getCallbackURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"callbackURL\": ");

			sb.append("\"");

			sb.append(_escape(payment.getCallbackURL()));

			sb.append("\"");
		}

		if (payment.getCancelURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cancelURL\": ");

			sb.append("\"");

			sb.append(_escape(payment.getCancelURL()));

			sb.append("\"");
		}

		if (payment.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(payment.getChannelId());
		}

		if (payment.getComment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(payment.getComment()));

			sb.append("\"");
		}

		if (payment.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(payment.getCreateDate()));

			sb.append("\"");
		}

		if (payment.getCurrencyCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(payment.getCurrencyCode()));

			sb.append("\"");
		}

		if (payment.getCurrencyExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(payment.getCurrencyExternalReferenceCode()));

			sb.append("\"");
		}

		if (payment.getCurrencyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyId\": ");

			sb.append(payment.getCurrencyId());
		}

		if (payment.getErrorMessages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessages\": ");

			sb.append("\"");

			sb.append(_escape(payment.getErrorMessages()));

			sb.append("\"");
		}

		if (payment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(payment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (payment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(payment.getId());
		}

		if (payment.getLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageId\": ");

			sb.append("\"");

			sb.append(_escape(payment.getLanguageId()));

			sb.append("\"");
		}

		if (payment.getPayload() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"payload\": ");

			sb.append("\"");

			sb.append(_escape(payment.getPayload()));

			sb.append("\"");
		}

		if (payment.getPaymentIntegrationKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentIntegrationKey\": ");

			sb.append("\"");

			sb.append(_escape(payment.getPaymentIntegrationKey()));

			sb.append("\"");
		}

		if (payment.getPaymentIntegrationType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentIntegrationType\": ");

			sb.append(payment.getPaymentIntegrationType());
		}

		if (payment.getPaymentStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatus\": ");

			sb.append(payment.getPaymentStatus());
		}

		if (payment.getPaymentStatusStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatusStatus\": ");

			sb.append(String.valueOf(payment.getPaymentStatusStatus()));
		}

		if (payment.getReasonKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reasonKey\": ");

			sb.append("\"");

			sb.append(_escape(payment.getReasonKey()));

			sb.append("\"");
		}

		if (payment.getReasonName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reasonName\": ");

			sb.append(_toJSON(payment.getReasonName()));
		}

		if (payment.getRedirectURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"redirectURL\": ");

			sb.append("\"");

			sb.append(_escape(payment.getRedirectURL()));

			sb.append("\"");
		}

		if (payment.getRelatedItemId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedItemId\": ");

			sb.append(payment.getRelatedItemId());
		}

		if (payment.getRelatedItemName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedItemName\": ");

			sb.append("\"");

			sb.append(_escape(payment.getRelatedItemName()));

			sb.append("\"");
		}

		if (payment.getRelatedItemNameLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedItemNameLabel\": ");

			sb.append("\"");

			sb.append(_escape(payment.getRelatedItemNameLabel()));

			sb.append("\"");
		}

		if (payment.getTransactionCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transactionCode\": ");

			sb.append("\"");

			sb.append(_escape(payment.getTransactionCode()));

			sb.append("\"");
		}

		if (payment.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(payment.getType());
		}

		if (payment.getTypeLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeLabel\": ");

			sb.append("\"");

			sb.append(_escape(payment.getTypeLabel()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PaymentJSONParser paymentJSONParser = new PaymentJSONParser();

		return paymentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Payment payment) {
		if (payment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (payment.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(payment.getActions()));
		}

		if (payment.getAmount() == null) {
			map.put("amount", null);
		}
		else {
			map.put("amount", String.valueOf(payment.getAmount()));
		}

		if (payment.getAmountFormatted() == null) {
			map.put("amountFormatted", null);
		}
		else {
			map.put(
				"amountFormatted",
				String.valueOf(payment.getAmountFormatted()));
		}

		if (payment.getAuthor() == null) {
			map.put("author", null);
		}
		else {
			map.put("author", String.valueOf(payment.getAuthor()));
		}

		if (payment.getCallbackURL() == null) {
			map.put("callbackURL", null);
		}
		else {
			map.put("callbackURL", String.valueOf(payment.getCallbackURL()));
		}

		if (payment.getCancelURL() == null) {
			map.put("cancelURL", null);
		}
		else {
			map.put("cancelURL", String.valueOf(payment.getCancelURL()));
		}

		if (payment.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put("channelId", String.valueOf(payment.getChannelId()));
		}

		if (payment.getComment() == null) {
			map.put("comment", null);
		}
		else {
			map.put("comment", String.valueOf(payment.getComment()));
		}

		if (payment.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(payment.getCreateDate()));
		}

		if (payment.getCurrencyCode() == null) {
			map.put("currencyCode", null);
		}
		else {
			map.put("currencyCode", String.valueOf(payment.getCurrencyCode()));
		}

		if (payment.getCurrencyExternalReferenceCode() == null) {
			map.put("currencyExternalReferenceCode", null);
		}
		else {
			map.put(
				"currencyExternalReferenceCode",
				String.valueOf(payment.getCurrencyExternalReferenceCode()));
		}

		if (payment.getCurrencyId() == null) {
			map.put("currencyId", null);
		}
		else {
			map.put("currencyId", String.valueOf(payment.getCurrencyId()));
		}

		if (payment.getErrorMessages() == null) {
			map.put("errorMessages", null);
		}
		else {
			map.put(
				"errorMessages", String.valueOf(payment.getErrorMessages()));
		}

		if (payment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(payment.getExternalReferenceCode()));
		}

		if (payment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(payment.getId()));
		}

		if (payment.getLanguageId() == null) {
			map.put("languageId", null);
		}
		else {
			map.put("languageId", String.valueOf(payment.getLanguageId()));
		}

		if (payment.getPayload() == null) {
			map.put("payload", null);
		}
		else {
			map.put("payload", String.valueOf(payment.getPayload()));
		}

		if (payment.getPaymentIntegrationKey() == null) {
			map.put("paymentIntegrationKey", null);
		}
		else {
			map.put(
				"paymentIntegrationKey",
				String.valueOf(payment.getPaymentIntegrationKey()));
		}

		if (payment.getPaymentIntegrationType() == null) {
			map.put("paymentIntegrationType", null);
		}
		else {
			map.put(
				"paymentIntegrationType",
				String.valueOf(payment.getPaymentIntegrationType()));
		}

		if (payment.getPaymentStatus() == null) {
			map.put("paymentStatus", null);
		}
		else {
			map.put(
				"paymentStatus", String.valueOf(payment.getPaymentStatus()));
		}

		if (payment.getPaymentStatusStatus() == null) {
			map.put("paymentStatusStatus", null);
		}
		else {
			map.put(
				"paymentStatusStatus",
				String.valueOf(payment.getPaymentStatusStatus()));
		}

		if (payment.getReasonKey() == null) {
			map.put("reasonKey", null);
		}
		else {
			map.put("reasonKey", String.valueOf(payment.getReasonKey()));
		}

		if (payment.getReasonName() == null) {
			map.put("reasonName", null);
		}
		else {
			map.put("reasonName", String.valueOf(payment.getReasonName()));
		}

		if (payment.getRedirectURL() == null) {
			map.put("redirectURL", null);
		}
		else {
			map.put("redirectURL", String.valueOf(payment.getRedirectURL()));
		}

		if (payment.getRelatedItemId() == null) {
			map.put("relatedItemId", null);
		}
		else {
			map.put(
				"relatedItemId", String.valueOf(payment.getRelatedItemId()));
		}

		if (payment.getRelatedItemName() == null) {
			map.put("relatedItemName", null);
		}
		else {
			map.put(
				"relatedItemName",
				String.valueOf(payment.getRelatedItemName()));
		}

		if (payment.getRelatedItemNameLabel() == null) {
			map.put("relatedItemNameLabel", null);
		}
		else {
			map.put(
				"relatedItemNameLabel",
				String.valueOf(payment.getRelatedItemNameLabel()));
		}

		if (payment.getTransactionCode() == null) {
			map.put("transactionCode", null);
		}
		else {
			map.put(
				"transactionCode",
				String.valueOf(payment.getTransactionCode()));
		}

		if (payment.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(payment.getType()));
		}

		if (payment.getTypeLabel() == null) {
			map.put("typeLabel", null);
		}
		else {
			map.put("typeLabel", String.valueOf(payment.getTypeLabel()));
		}

		return map;
	}

	public static class PaymentJSONParser extends BaseJSONParser<Payment> {

		@Override
		protected Payment createDTO() {
			return new Payment();
		}

		@Override
		protected Payment[] createDTOArray(int size) {
			return new Payment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "amount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "amountFormatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "callbackURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cancelURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "languageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "payload")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentIntegrationKey")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentIntegrationType")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentStatusStatus")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reasonKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reasonName")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "redirectURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "relatedItemId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "relatedItemName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "relatedItemNameLabel")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "transactionCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeLabel")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Payment payment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					payment.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "amount")) {
				if (jsonParserFieldValue != null) {
					payment.setAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "amountFormatted")) {
				if (jsonParserFieldValue != null) {
					payment.setAmountFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				if (jsonParserFieldValue != null) {
					payment.setAuthor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "callbackURL")) {
				if (jsonParserFieldValue != null) {
					payment.setCallbackURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cancelURL")) {
				if (jsonParserFieldValue != null) {
					payment.setCancelURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					payment.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				if (jsonParserFieldValue != null) {
					payment.setComment((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					payment.setCreateDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				if (jsonParserFieldValue != null) {
					payment.setCurrencyCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					payment.setCurrencyExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				if (jsonParserFieldValue != null) {
					payment.setCurrencyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				if (jsonParserFieldValue != null) {
					payment.setErrorMessages((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					payment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					payment.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "languageId")) {
				if (jsonParserFieldValue != null) {
					payment.setLanguageId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "payload")) {
				if (jsonParserFieldValue != null) {
					payment.setPayload((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentIntegrationKey")) {

				if (jsonParserFieldValue != null) {
					payment.setPaymentIntegrationKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentIntegrationType")) {

				if (jsonParserFieldValue != null) {
					payment.setPaymentIntegrationType(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				if (jsonParserFieldValue != null) {
					payment.setPaymentStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentStatusStatus")) {

				if (jsonParserFieldValue != null) {
					payment.setPaymentStatusStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reasonKey")) {
				if (jsonParserFieldValue != null) {
					payment.setReasonKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reasonName")) {
				if (jsonParserFieldValue != null) {
					payment.setReasonName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "redirectURL")) {
				if (jsonParserFieldValue != null) {
					payment.setRedirectURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "relatedItemId")) {
				if (jsonParserFieldValue != null) {
					payment.setRelatedItemId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "relatedItemName")) {
				if (jsonParserFieldValue != null) {
					payment.setRelatedItemName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "relatedItemNameLabel")) {

				if (jsonParserFieldValue != null) {
					payment.setRelatedItemNameLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "transactionCode")) {
				if (jsonParserFieldValue != null) {
					payment.setTransactionCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					payment.setType(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeLabel")) {
				if (jsonParserFieldValue != null) {
					payment.setTypeLabel((String)jsonParserFieldValue);
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