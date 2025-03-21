/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("PaymentMethodGroupRelTerm")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"paymentMethodGroupRelId", "termId"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PaymentMethodGroupRelTerm")
public class PaymentMethodGroupRelTerm implements Serializable {

	public static PaymentMethodGroupRelTerm toDTO(String json) {
		return ObjectMapperUtil.readValue(
			PaymentMethodGroupRelTerm.class, json);
	}

	public static PaymentMethodGroupRelTerm unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PaymentMethodGroupRelTerm.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30324")
	public Long getPaymentMethodGroupRelId() {
		if (_paymentMethodGroupRelIdSupplier != null) {
			paymentMethodGroupRelId = _paymentMethodGroupRelIdSupplier.get();

			_paymentMethodGroupRelIdSupplier = null;
		}

		return paymentMethodGroupRelId;
	}

	public void setPaymentMethodGroupRelId(Long paymentMethodGroupRelId) {
		this.paymentMethodGroupRelId = paymentMethodGroupRelId;

		_paymentMethodGroupRelIdSupplier = null;
	}

	@JsonIgnore
	public void setPaymentMethodGroupRelId(
		UnsafeSupplier<Long, Exception> paymentMethodGroupRelIdUnsafeSupplier) {

		_paymentMethodGroupRelIdSupplier = () -> {
			try {
				return paymentMethodGroupRelIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long paymentMethodGroupRelId;

	@JsonIgnore
	private Supplier<Long> _paymentMethodGroupRelIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30643")
	public Long getPaymentMethodGroupRelTermId() {
		if (_paymentMethodGroupRelTermIdSupplier != null) {
			paymentMethodGroupRelTermId =
				_paymentMethodGroupRelTermIdSupplier.get();

			_paymentMethodGroupRelTermIdSupplier = null;
		}

		return paymentMethodGroupRelTermId;
	}

	public void setPaymentMethodGroupRelTermId(
		Long paymentMethodGroupRelTermId) {

		this.paymentMethodGroupRelTermId = paymentMethodGroupRelTermId;

		_paymentMethodGroupRelTermIdSupplier = null;
	}

	@JsonIgnore
	public void setPaymentMethodGroupRelTermId(
		UnsafeSupplier<Long, Exception>
			paymentMethodGroupRelTermIdUnsafeSupplier) {

		_paymentMethodGroupRelTermIdSupplier = () -> {
			try {
				return paymentMethodGroupRelTermIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long paymentMethodGroupRelTermId;

	@JsonIgnore
	private Supplier<Long> _paymentMethodGroupRelTermIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Term getTerm() {
		if (_termSupplier != null) {
			term = _termSupplier.get();

			_termSupplier = null;
		}

		return term;
	}

	public void setTerm(Term term) {
		this.term = term;

		_termSupplier = null;
	}

	@JsonIgnore
	public void setTerm(UnsafeSupplier<Term, Exception> termUnsafeSupplier) {
		_termSupplier = () -> {
			try {
				return termUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Term term;

	@JsonIgnore
	private Supplier<Term> _termSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "PAB-34098-789-N")
	public String getTermExternalReferenceCode() {
		if (_termExternalReferenceCodeSupplier != null) {
			termExternalReferenceCode =
				_termExternalReferenceCodeSupplier.get();

			_termExternalReferenceCodeSupplier = null;
		}

		return termExternalReferenceCode;
	}

	public void setTermExternalReferenceCode(String termExternalReferenceCode) {
		this.termExternalReferenceCode = termExternalReferenceCode;

		_termExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setTermExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			termExternalReferenceCodeUnsafeSupplier) {

		_termExternalReferenceCodeSupplier = () -> {
			try {
				return termExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String termExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _termExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getTermId() {
		if (_termIdSupplier != null) {
			termId = _termIdSupplier.get();

			_termIdSupplier = null;
		}

		return termId;
	}

	public void setTermId(Long termId) {
		this.termId = termId;

		_termIdSupplier = null;
	}

	@JsonIgnore
	public void setTermId(
		UnsafeSupplier<Long, Exception> termIdUnsafeSupplier) {

		_termIdSupplier = () -> {
			try {
				return termIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Long termId;

	@JsonIgnore
	private Supplier<Long> _termIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PaymentMethodGroupRelTerm)) {
			return false;
		}

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm =
			(PaymentMethodGroupRelTerm)object;

		return Objects.equals(toString(), paymentMethodGroupRelTerm.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Long paymentMethodGroupRelId = getPaymentMethodGroupRelId();

		if (paymentMethodGroupRelId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethodGroupRelId\": ");

			sb.append(paymentMethodGroupRelId);
		}

		Long paymentMethodGroupRelTermId = getPaymentMethodGroupRelTermId();

		if (paymentMethodGroupRelTermId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethodGroupRelTermId\": ");

			sb.append(paymentMethodGroupRelTermId);
		}

		Term term = getTerm();

		if (term != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"term\": ");

			sb.append(String.valueOf(term));
		}

		String termExternalReferenceCode = getTermExternalReferenceCode();

		if (termExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(termExternalReferenceCode));

			sb.append("\"");
		}

		Long termId = getTermId();

		if (termId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termId\": ");

			sb.append(termId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelTerm",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
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
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}