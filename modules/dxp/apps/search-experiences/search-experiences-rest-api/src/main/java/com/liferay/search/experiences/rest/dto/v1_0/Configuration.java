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
@GraphQLName("Configuration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Configuration")
public class Configuration implements Serializable {

	public static Configuration toDTO(String json) {
		return ObjectMapperUtil.readValue(Configuration.class, json);
	}

	public static Configuration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Configuration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public AdvancedConfiguration getAdvancedConfiguration() {
		if (_advancedConfigurationSupplier != null) {
			advancedConfiguration = _advancedConfigurationSupplier.get();

			_advancedConfigurationSupplier = null;
		}

		return advancedConfiguration;
	}

	public void setAdvancedConfiguration(
		AdvancedConfiguration advancedConfiguration) {

		this.advancedConfiguration = advancedConfiguration;

		_advancedConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setAdvancedConfiguration(
		UnsafeSupplier<AdvancedConfiguration, Exception>
			advancedConfigurationUnsafeSupplier) {

		_advancedConfigurationSupplier = () -> {
			try {
				return advancedConfigurationUnsafeSupplier.get();
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
	protected AdvancedConfiguration advancedConfiguration;

	@JsonIgnore
	private Supplier<AdvancedConfiguration> _advancedConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public AggregationConfiguration getAggregationConfiguration() {
		if (_aggregationConfigurationSupplier != null) {
			aggregationConfiguration = _aggregationConfigurationSupplier.get();

			_aggregationConfigurationSupplier = null;
		}

		return aggregationConfiguration;
	}

	public void setAggregationConfiguration(
		AggregationConfiguration aggregationConfiguration) {

		this.aggregationConfiguration = aggregationConfiguration;

		_aggregationConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setAggregationConfiguration(
		UnsafeSupplier<AggregationConfiguration, Exception>
			aggregationConfigurationUnsafeSupplier) {

		_aggregationConfigurationSupplier = () -> {
			try {
				return aggregationConfigurationUnsafeSupplier.get();
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
	protected AggregationConfiguration aggregationConfiguration;

	@JsonIgnore
	private Supplier<AggregationConfiguration>
		_aggregationConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public GeneralConfiguration getGeneralConfiguration() {
		if (_generalConfigurationSupplier != null) {
			generalConfiguration = _generalConfigurationSupplier.get();

			_generalConfigurationSupplier = null;
		}

		return generalConfiguration;
	}

	public void setGeneralConfiguration(
		GeneralConfiguration generalConfiguration) {

		this.generalConfiguration = generalConfiguration;

		_generalConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setGeneralConfiguration(
		UnsafeSupplier<GeneralConfiguration, Exception>
			generalConfigurationUnsafeSupplier) {

		_generalConfigurationSupplier = () -> {
			try {
				return generalConfigurationUnsafeSupplier.get();
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
	protected GeneralConfiguration generalConfiguration;

	@JsonIgnore
	private Supplier<GeneralConfiguration> _generalConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public HighlightConfiguration getHighlightConfiguration() {
		if (_highlightConfigurationSupplier != null) {
			highlightConfiguration = _highlightConfigurationSupplier.get();

			_highlightConfigurationSupplier = null;
		}

		return highlightConfiguration;
	}

	public void setHighlightConfiguration(
		HighlightConfiguration highlightConfiguration) {

		this.highlightConfiguration = highlightConfiguration;

		_highlightConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setHighlightConfiguration(
		UnsafeSupplier<HighlightConfiguration, Exception>
			highlightConfigurationUnsafeSupplier) {

		_highlightConfigurationSupplier = () -> {
			try {
				return highlightConfigurationUnsafeSupplier.get();
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
	protected HighlightConfiguration highlightConfiguration;

	@JsonIgnore
	private Supplier<HighlightConfiguration> _highlightConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public IndexConfiguration getIndexConfiguration() {
		if (_indexConfigurationSupplier != null) {
			indexConfiguration = _indexConfigurationSupplier.get();

			_indexConfigurationSupplier = null;
		}

		return indexConfiguration;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;

		_indexConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setIndexConfiguration(
		UnsafeSupplier<IndexConfiguration, Exception>
			indexConfigurationUnsafeSupplier) {

		_indexConfigurationSupplier = () -> {
			try {
				return indexConfigurationUnsafeSupplier.get();
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
	protected IndexConfiguration indexConfiguration;

	@JsonIgnore
	private Supplier<IndexConfiguration> _indexConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ParameterConfiguration getParameterConfiguration() {
		if (_parameterConfigurationSupplier != null) {
			parameterConfiguration = _parameterConfigurationSupplier.get();

			_parameterConfigurationSupplier = null;
		}

		return parameterConfiguration;
	}

	public void setParameterConfiguration(
		ParameterConfiguration parameterConfiguration) {

		this.parameterConfiguration = parameterConfiguration;

		_parameterConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setParameterConfiguration(
		UnsafeSupplier<ParameterConfiguration, Exception>
			parameterConfigurationUnsafeSupplier) {

		_parameterConfigurationSupplier = () -> {
			try {
				return parameterConfigurationUnsafeSupplier.get();
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
	protected ParameterConfiguration parameterConfiguration;

	@JsonIgnore
	private Supplier<ParameterConfiguration> _parameterConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public QueryConfiguration getQueryConfiguration() {
		if (_queryConfigurationSupplier != null) {
			queryConfiguration = _queryConfigurationSupplier.get();

			_queryConfigurationSupplier = null;
		}

		return queryConfiguration;
	}

	public void setQueryConfiguration(QueryConfiguration queryConfiguration) {
		this.queryConfiguration = queryConfiguration;

		_queryConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setQueryConfiguration(
		UnsafeSupplier<QueryConfiguration, Exception>
			queryConfigurationUnsafeSupplier) {

		_queryConfigurationSupplier = () -> {
			try {
				return queryConfigurationUnsafeSupplier.get();
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
	protected QueryConfiguration queryConfiguration;

	@JsonIgnore
	private Supplier<QueryConfiguration> _queryConfigurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getSearchContextAttributes() {
		if (_searchContextAttributesSupplier != null) {
			searchContextAttributes = _searchContextAttributesSupplier.get();

			_searchContextAttributesSupplier = null;
		}

		return searchContextAttributes;
	}

	public void setSearchContextAttributes(
		Map<String, Object> searchContextAttributes) {

		this.searchContextAttributes = searchContextAttributes;

		_searchContextAttributesSupplier = null;
	}

	@JsonIgnore
	public void setSearchContextAttributes(
		UnsafeSupplier<Map<String, Object>, Exception>
			searchContextAttributesUnsafeSupplier) {

		_searchContextAttributesSupplier = () -> {
			try {
				return searchContextAttributesUnsafeSupplier.get();
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
	protected Map<String, Object> searchContextAttributes;

	@JsonIgnore
	private Supplier<Map<String, Object>> _searchContextAttributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SortConfiguration getSortConfiguration() {
		if (_sortConfigurationSupplier != null) {
			sortConfiguration = _sortConfigurationSupplier.get();

			_sortConfigurationSupplier = null;
		}

		return sortConfiguration;
	}

	public void setSortConfiguration(SortConfiguration sortConfiguration) {
		this.sortConfiguration = sortConfiguration;

		_sortConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setSortConfiguration(
		UnsafeSupplier<SortConfiguration, Exception>
			sortConfigurationUnsafeSupplier) {

		_sortConfigurationSupplier = () -> {
			try {
				return sortConfigurationUnsafeSupplier.get();
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
	protected SortConfiguration sortConfiguration;

	@JsonIgnore
	private Supplier<SortConfiguration> _sortConfigurationSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Configuration)) {
			return false;
		}

		Configuration configuration = (Configuration)object;

		return Objects.equals(toString(), configuration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		AdvancedConfiguration advancedConfiguration =
			getAdvancedConfiguration();

		if (advancedConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"advancedConfiguration\": ");

			sb.append(String.valueOf(advancedConfiguration));
		}

		AggregationConfiguration aggregationConfiguration =
			getAggregationConfiguration();

		if (aggregationConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregationConfiguration\": ");

			sb.append(String.valueOf(aggregationConfiguration));
		}

		GeneralConfiguration generalConfiguration = getGeneralConfiguration();

		if (generalConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"generalConfiguration\": ");

			sb.append(String.valueOf(generalConfiguration));
		}

		HighlightConfiguration highlightConfiguration =
			getHighlightConfiguration();

		if (highlightConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"highlightConfiguration\": ");

			sb.append(String.valueOf(highlightConfiguration));
		}

		IndexConfiguration indexConfiguration = getIndexConfiguration();

		if (indexConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexConfiguration\": ");

			sb.append(String.valueOf(indexConfiguration));
		}

		ParameterConfiguration parameterConfiguration =
			getParameterConfiguration();

		if (parameterConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterConfiguration\": ");

			sb.append(String.valueOf(parameterConfiguration));
		}

		QueryConfiguration queryConfiguration = getQueryConfiguration();

		if (queryConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryConfiguration\": ");

			sb.append(String.valueOf(queryConfiguration));
		}

		Map<String, Object> searchContextAttributes =
			getSearchContextAttributes();

		if (searchContextAttributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchContextAttributes\": ");

			sb.append(_toJSON(searchContextAttributes));
		}

		SortConfiguration sortConfiguration = getSortConfiguration();

		if (sortConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortConfiguration\": ");

			sb.append(String.valueOf(sortConfiguration));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Configuration",
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