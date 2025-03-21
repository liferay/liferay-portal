/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("Currency")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"code", "name"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Currency")
public class Currency implements Serializable {

	public static Currency toDTO(String json) {
		return ObjectMapperUtil.readValue(Currency.class, json);
	}

	public static Currency unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Currency.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "USD")
	public String getCode() {
		if (_codeSupplier != null) {
			code = _codeSupplier.get();

			_codeSupplier = null;
		}

		return code;
	}

	public void setCode(String code) {
		this.code = code;

		_codeSupplier = null;
	}

	@JsonIgnore
	public void setCode(UnsafeSupplier<String, Exception> codeUnsafeSupplier) {
		_codeSupplier = () -> {
			try {
				return codeUnsafeSupplier.get();
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
	@NotEmpty
	protected String code;

	@JsonIgnore
	private Supplier<String> _codeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "AB-34098-789-N")
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Hand Saw, hr_HR=Product Name HR, hu_HU=Product Name HU}"
	)
	@Valid
	public Map<String, String> getFormatPattern() {
		if (_formatPatternSupplier != null) {
			formatPattern = _formatPatternSupplier.get();

			_formatPatternSupplier = null;
		}

		return formatPattern;
	}

	public void setFormatPattern(Map<String, String> formatPattern) {
		this.formatPattern = formatPattern;

		_formatPatternSupplier = null;
	}

	@JsonIgnore
	public void setFormatPattern(
		UnsafeSupplier<Map<String, String>, Exception>
			formatPatternUnsafeSupplier) {

		_formatPatternSupplier = () -> {
			try {
				return formatPatternUnsafeSupplier.get();
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
	protected Map<String, String> formatPattern;

	@JsonIgnore
	private Supplier<Map<String, String>> _formatPatternSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30130")
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMaxFractionDigits() {
		if (_maxFractionDigitsSupplier != null) {
			maxFractionDigits = _maxFractionDigitsSupplier.get();

			_maxFractionDigitsSupplier = null;
		}

		return maxFractionDigits;
	}

	public void setMaxFractionDigits(Integer maxFractionDigits) {
		this.maxFractionDigits = maxFractionDigits;

		_maxFractionDigitsSupplier = null;
	}

	@JsonIgnore
	public void setMaxFractionDigits(
		UnsafeSupplier<Integer, Exception> maxFractionDigitsUnsafeSupplier) {

		_maxFractionDigitsSupplier = () -> {
			try {
				return maxFractionDigitsUnsafeSupplier.get();
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
	protected Integer maxFractionDigits;

	@JsonIgnore
	private Supplier<Integer> _maxFractionDigitsSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMinFractionDigits() {
		if (_minFractionDigitsSupplier != null) {
			minFractionDigits = _minFractionDigitsSupplier.get();

			_minFractionDigitsSupplier = null;
		}

		return minFractionDigits;
	}

	public void setMinFractionDigits(Integer minFractionDigits) {
		this.minFractionDigits = minFractionDigits;

		_minFractionDigitsSupplier = null;
	}

	@JsonIgnore
	public void setMinFractionDigits(
		UnsafeSupplier<Integer, Exception> minFractionDigitsUnsafeSupplier) {

		_minFractionDigitsSupplier = () -> {
			try {
				return minFractionDigitsUnsafeSupplier.get();
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
	protected Integer minFractionDigits;

	@JsonIgnore
	private Supplier<Integer> _minFractionDigitsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "{en_US=Hand Saw, hr_HR=Product Name HR, hu_HU=Product Name HU}"
	)
	@Valid
	public Map<String, String> getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected Map<String, String> name;

	@JsonIgnore
	private Supplier<Map<String, String>> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getPrimary() {
		if (_primarySupplier != null) {
			primary = _primarySupplier.get();

			_primarySupplier = null;
		}

		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;

		_primarySupplier = null;
	}

	@JsonIgnore
	public void setPrimary(
		UnsafeSupplier<Boolean, Exception> primaryUnsafeSupplier) {

		_primarySupplier = () -> {
			try {
				return primaryUnsafeSupplier.get();
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
	protected Boolean primary;

	@JsonIgnore
	private Supplier<Boolean> _primarySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "1.2")
	public Double getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
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
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "33.54")
	@Valid
	public BigDecimal getRate() {
		if (_rateSupplier != null) {
			rate = _rateSupplier.get();

			_rateSupplier = null;
		}

		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;

		_rateSupplier = null;
	}

	@JsonIgnore
	public void setRate(
		UnsafeSupplier<BigDecimal, Exception> rateUnsafeSupplier) {

		_rateSupplier = () -> {
			try {
				return rateUnsafeSupplier.get();
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
	protected BigDecimal rate;

	@JsonIgnore
	private Supplier<BigDecimal> _rateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "HALF_EVEN")
	@JsonGetter("roundingMode")
	@Valid
	public RoundingMode getRoundingMode() {
		if (_roundingModeSupplier != null) {
			roundingMode = _roundingModeSupplier.get();

			_roundingModeSupplier = null;
		}

		return roundingMode;
	}

	@JsonIgnore
	public String getRoundingModeAsString() {
		RoundingMode roundingMode = getRoundingMode();

		if (roundingMode == null) {
			return null;
		}

		return roundingMode.toString();
	}

	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;

		_roundingModeSupplier = null;
	}

	@JsonIgnore
	public void setRoundingMode(
		UnsafeSupplier<RoundingMode, Exception> roundingModeUnsafeSupplier) {

		_roundingModeSupplier = () -> {
			try {
				return roundingModeUnsafeSupplier.get();
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
	protected RoundingMode roundingMode;

	@JsonIgnore
	private Supplier<RoundingMode> _roundingModeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "$")
	public String getSymbol() {
		if (_symbolSupplier != null) {
			symbol = _symbolSupplier.get();

			_symbolSupplier = null;
		}

		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;

		_symbolSupplier = null;
	}

	@JsonIgnore
	public void setSymbol(
		UnsafeSupplier<String, Exception> symbolUnsafeSupplier) {

		_symbolSupplier = () -> {
			try {
				return symbolUnsafeSupplier.get();
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
	protected String symbol;

	@JsonIgnore
	private Supplier<String> _symbolSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Currency)) {
			return false;
		}

		Currency currency = (Currency)object;

		return Objects.equals(toString(), currency.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

		String code = getCode();

		if (code != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"code\": ");

			sb.append("\"");

			sb.append(_escape(code));

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Map<String, String> formatPattern = getFormatPattern();

		if (formatPattern != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formatPattern\": ");

			sb.append(_toJSON(formatPattern));
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Integer maxFractionDigits = getMaxFractionDigits();

		if (maxFractionDigits != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxFractionDigits\": ");

			sb.append(maxFractionDigits);
		}

		Integer minFractionDigits = getMinFractionDigits();

		if (minFractionDigits != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minFractionDigits\": ");

			sb.append(minFractionDigits);
		}

		Map<String, String> name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(name));
		}

		Boolean primary = getPrimary();

		if (primary != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(primary);
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		BigDecimal rate = getRate();

		if (rate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rate\": ");

			sb.append(rate);
		}

		RoundingMode roundingMode = getRoundingMode();

		if (roundingMode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roundingMode\": ");

			sb.append("\"");

			sb.append(roundingMode);

			sb.append("\"");
		}

		String symbol = getSymbol();

		if (symbol != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"symbol\": ");

			sb.append("\"");

			sb.append(_escape(symbol));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Currency",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("RoundingMode")
	public static enum RoundingMode {

		UP("UP"), DOWN("DOWN"), CEILING("CEILING"), FLOOR("FLOOR"),
		HALF_UP("HALF_UP"), HALF_DOWN("HALF_DOWN"), HALF_EVEN("HALF_EVEN"),
		UNNECESSARY("UNNECESSARY");

		@JsonCreator
		public static RoundingMode create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (RoundingMode roundingMode : values()) {
				if (Objects.equals(roundingMode.getValue(), value)) {
					return roundingMode;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private RoundingMode(String value) {
			_value = value;
		}

		private final String _value;

	}

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