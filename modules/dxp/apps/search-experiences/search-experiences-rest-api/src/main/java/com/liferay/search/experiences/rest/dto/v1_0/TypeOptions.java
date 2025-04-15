/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("TypeOptions")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TypeOptions")
public class TypeOptions implements Serializable {

	public static TypeOptions toDTO(String json) {
		return ObjectMapperUtil.readValue(TypeOptions.class, json);
	}

	public static TypeOptions unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TypeOptions.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getBoost() {
		if (_boostSupplier != null) {
			boost = _boostSupplier.get();

			_boostSupplier = null;
		}

		return boost;
	}

	public void setBoost(Boolean boost) {
		this.boost = boost;

		_boostSupplier = null;
	}

	@JsonIgnore
	public void setBoost(
		UnsafeSupplier<Boolean, Exception> boostUnsafeSupplier) {

		_boostSupplier = () -> {
			try {
				return boostUnsafeSupplier.get();
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
	protected Boolean boost;

	@JsonIgnore
	private Supplier<Boolean> _boostSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFormat() {
		if (_formatSupplier != null) {
			format = _formatSupplier.get();

			_formatSupplier = null;
		}

		return format;
	}

	public void setFormat(String format) {
		this.format = format;

		_formatSupplier = null;
	}

	@JsonIgnore
	public void setFormat(
		UnsafeSupplier<String, Exception> formatUnsafeSupplier) {

		_formatSupplier = () -> {
			try {
				return formatUnsafeSupplier.get();
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
	protected String format;

	@JsonIgnore
	private Supplier<String> _formatSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getMax() {
		if (_maxSupplier != null) {
			max = _maxSupplier.get();

			_maxSupplier = null;
		}

		return max;
	}

	public void setMax(Object max) {
		this.max = max;

		_maxSupplier = null;
	}

	@JsonIgnore
	public void setMax(UnsafeSupplier<Object, Exception> maxUnsafeSupplier) {
		_maxSupplier = () -> {
			try {
				return maxUnsafeSupplier.get();
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
	protected Object max;

	@JsonIgnore
	private Supplier<Object> _maxSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getMin() {
		if (_minSupplier != null) {
			min = _minSupplier.get();

			_minSupplier = null;
		}

		return min;
	}

	public void setMin(Object min) {
		this.min = min;

		_minSupplier = null;
	}

	@JsonIgnore
	public void setMin(UnsafeSupplier<Object, Exception> minUnsafeSupplier) {
		_minSupplier = () -> {
			try {
				return minUnsafeSupplier.get();
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
	protected Object min;

	@JsonIgnore
	private Supplier<Object> _minSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getNullable() {
		if (_nullableSupplier != null) {
			nullable = _nullableSupplier.get();

			_nullableSupplier = null;
		}

		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;

		_nullableSupplier = null;
	}

	@JsonIgnore
	public void setNullable(
		UnsafeSupplier<Boolean, Exception> nullableUnsafeSupplier) {

		_nullableSupplier = () -> {
			try {
				return nullableUnsafeSupplier.get();
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
	protected Boolean nullable;

	@JsonIgnore
	private Supplier<Boolean> _nullableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Option[] getOptions() {
		if (_optionsSupplier != null) {
			options = _optionsSupplier.get();

			_optionsSupplier = null;
		}

		return options;
	}

	public void setOptions(Option[] options) {
		this.options = options;

		_optionsSupplier = null;
	}

	@JsonIgnore
	public void setOptions(
		UnsafeSupplier<Option[], Exception> optionsUnsafeSupplier) {

		_optionsSupplier = () -> {
			try {
				return optionsUnsafeSupplier.get();
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
	protected Option[] options;

	@JsonIgnore
	private Supplier<Option[]> _optionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getRequired() {
		if (_requiredSupplier != null) {
			required = _requiredSupplier.get();

			_requiredSupplier = null;
		}

		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;

		_requiredSupplier = null;
	}

	@JsonIgnore
	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		_requiredSupplier = () -> {
			try {
				return requiredUnsafeSupplier.get();
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
	protected Boolean required;

	@JsonIgnore
	private Supplier<Boolean> _requiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getStep() {
		if (_stepSupplier != null) {
			step = _stepSupplier.get();

			_stepSupplier = null;
		}

		return step;
	}

	public void setStep(Object step) {
		this.step = step;

		_stepSupplier = null;
	}

	@JsonIgnore
	public void setStep(UnsafeSupplier<Object, Exception> stepUnsafeSupplier) {
		_stepSupplier = () -> {
			try {
				return stepUnsafeSupplier.get();
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
	protected Object step;

	@JsonIgnore
	private Supplier<Object> _stepSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getUnit() {
		if (_unitSupplier != null) {
			unit = _unitSupplier.get();

			_unitSupplier = null;
		}

		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;

		_unitSupplier = null;
	}

	@JsonIgnore
	public void setUnit(UnsafeSupplier<String, Exception> unitUnsafeSupplier) {
		_unitSupplier = () -> {
			try {
				return unitUnsafeSupplier.get();
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
	protected String unit;

	@JsonIgnore
	private Supplier<String> _unitSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getUnitSuffix() {
		if (_unitSuffixSupplier != null) {
			unitSuffix = _unitSuffixSupplier.get();

			_unitSuffixSupplier = null;
		}

		return unitSuffix;
	}

	public void setUnitSuffix(String unitSuffix) {
		this.unitSuffix = unitSuffix;

		_unitSuffixSupplier = null;
	}

	@JsonIgnore
	public void setUnitSuffix(
		UnsafeSupplier<String, Exception> unitSuffixUnsafeSupplier) {

		_unitSuffixSupplier = () -> {
			try {
				return unitSuffixUnsafeSupplier.get();
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
	protected String unitSuffix;

	@JsonIgnore
	private Supplier<String> _unitSuffixSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TypeOptions)) {
			return false;
		}

		TypeOptions typeOptions = (TypeOptions)object;

		return Objects.equals(toString(), typeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean boost = getBoost();

		if (boost != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"boost\": ");

			sb.append(boost);
		}

		String format = getFormat();

		if (format != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"format\": ");

			sb.append("\"");

			sb.append(_escape(format));

			sb.append("\"");
		}

		Object max = getMax();

		if (max != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"max\": ");

			if (max instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)max));
			}
			else if (max instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)max));
				sb.append("\"");
			}
			else {
				sb.append(max);
			}
		}

		Object min = getMin();

		if (min != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"min\": ");

			if (min instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)min));
			}
			else if (min instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)min));
				sb.append("\"");
			}
			else {
				sb.append(min);
			}
		}

		Boolean nullable = getNullable();

		if (nullable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nullable\": ");

			sb.append(nullable);
		}

		Option[] options = getOptions();

		if (options != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("[");

			for (int i = 0; i < options.length; i++) {
				sb.append(String.valueOf(options[i]));

				if ((i + 1) < options.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean required = getRequired();

		if (required != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(required);
		}

		Object step = getStep();

		if (step != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"step\": ");

			if (step instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)step));
			}
			else if (step instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)step));
				sb.append("\"");
			}
			else {
				sb.append(step);
			}
		}

		String unit = getUnit();

		if (unit != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unit\": ");

			sb.append("\"");

			sb.append(_escape(unit));

			sb.append("\"");
		}

		String unitSuffix = getUnitSuffix();

		if (unitSuffix != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitSuffix\": ");

			sb.append("\"");

			sb.append(_escape(unitSuffix));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.TypeOptions",
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