/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("FacetConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FacetConfiguration")
public class FacetConfiguration implements Serializable {

	public static FacetConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(FacetConfiguration.class, json);
	}

	public static FacetConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FacetConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the aggregation."
	)
	public String getAggregationName() {
		if (_aggregationNameSupplier != null) {
			aggregationName = _aggregationNameSupplier.get();

			_aggregationNameSupplier = null;
		}

		return aggregationName;
	}

	public void setAggregationName(String aggregationName) {
		this.aggregationName = aggregationName;

		_aggregationNameSupplier = null;
	}

	@JsonIgnore
	public void setAggregationName(
		UnsafeSupplier<String, Exception> aggregationNameUnsafeSupplier) {

		_aggregationNameSupplier = () -> {
			try {
				return aggregationNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The name of the aggregation.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String aggregationName;

	@JsonIgnore
	private Supplier<String> _aggregationNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Additional attributes for the facet."
	)
	@Valid
	public Map<String, Object> getAttributes() {
		if (_attributesSupplier != null) {
			attributes = _attributesSupplier.get();

			_attributesSupplier = null;
		}

		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;

		_attributesSupplier = null;
	}

	@JsonIgnore
	public void setAttributes(
		UnsafeSupplier<Map<String, Object>, Exception>
			attributesUnsafeSupplier) {

		_attributesSupplier = () -> {
			try {
				return attributesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Additional attributes for the facet.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Object> attributes;

	@JsonIgnore
	private Supplier<Map<String, Object>> _attributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Frequency threshold for showing the terms."
	)
	public Integer getFrequencyThreshold() {
		if (_frequencyThresholdSupplier != null) {
			frequencyThreshold = _frequencyThresholdSupplier.get();

			_frequencyThresholdSupplier = null;
		}

		return frequencyThreshold;
	}

	public void setFrequencyThreshold(Integer frequencyThreshold) {
		this.frequencyThreshold = frequencyThreshold;

		_frequencyThresholdSupplier = null;
	}

	@JsonIgnore
	public void setFrequencyThreshold(
		UnsafeSupplier<Integer, Exception> frequencyThresholdUnsafeSupplier) {

		_frequencyThresholdSupplier = () -> {
			try {
				return frequencyThresholdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Frequency threshold for showing the terms.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer frequencyThreshold;

	@JsonIgnore
	private Supplier<Integer> _frequencyThresholdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Maximum number of terms to be shown."
	)
	public Integer getMaxTerms() {
		if (_maxTermsSupplier != null) {
			maxTerms = _maxTermsSupplier.get();

			_maxTermsSupplier = null;
		}

		return maxTerms;
	}

	public void setMaxTerms(Integer maxTerms) {
		this.maxTerms = maxTerms;

		_maxTermsSupplier = null;
	}

	@JsonIgnore
	public void setMaxTerms(
		UnsafeSupplier<Integer, Exception> maxTermsUnsafeSupplier) {

		_maxTermsSupplier = () -> {
			try {
				return maxTermsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Maximum number of terms to be shown.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer maxTerms;

	@JsonIgnore
	private Supplier<Integer> _maxTermsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the facet."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
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

	@GraphQLField(description = "The name of the facet.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The values / selections to be filtered by."
	)
	@Valid
	public Object[] getValues() {
		if (_valuesSupplier != null) {
			values = _valuesSupplier.get();

			_valuesSupplier = null;
		}

		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;

		_valuesSupplier = null;
	}

	@JsonIgnore
	public void setValues(
		UnsafeSupplier<Object[], Exception> valuesUnsafeSupplier) {

		_valuesSupplier = () -> {
			try {
				return valuesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The values / selections to be filtered by.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object[] values;

	@JsonIgnore
	private Supplier<Object[]> _valuesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FacetConfiguration)) {
			return false;
		}

		FacetConfiguration facetConfiguration = (FacetConfiguration)object;

		return Objects.equals(toString(), facetConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String aggregationName = getAggregationName();

		if (aggregationName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregationName\": ");

			sb.append("\"");

			sb.append(_escape(aggregationName));

			sb.append("\"");
		}

		Map<String, Object> attributes = getAttributes();

		if (attributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append(_toJSON(attributes));
		}

		Integer frequencyThreshold = getFrequencyThreshold();

		if (frequencyThreshold != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"frequencyThreshold\": ");

			sb.append(frequencyThreshold);
		}

		Integer maxTerms = getMaxTerms();

		if (maxTerms != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxTerms\": ");

			sb.append(maxTerms);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Object[] values = getValues();

		if (values != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"values\": ");

			sb.append("[");

			for (int i = 0; i < values.length; i++) {
				sb.append("\"");

				sb.append(_escape(values[i]));

				sb.append("\"");

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.search.rest.dto.v1_0.FacetConfiguration",
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