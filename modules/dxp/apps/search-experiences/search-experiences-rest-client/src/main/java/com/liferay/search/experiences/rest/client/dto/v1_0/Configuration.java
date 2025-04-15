/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.ConfigurationSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Configuration implements Cloneable, Serializable {

	public static Configuration toDTO(String json) {
		return ConfigurationSerDes.toDTO(json);
	}

	public AdvancedConfiguration getAdvancedConfiguration() {
		return advancedConfiguration;
	}

	public void setAdvancedConfiguration(
		AdvancedConfiguration advancedConfiguration) {

		this.advancedConfiguration = advancedConfiguration;
	}

	public void setAdvancedConfiguration(
		UnsafeSupplier<AdvancedConfiguration, Exception>
			advancedConfigurationUnsafeSupplier) {

		try {
			advancedConfiguration = advancedConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AdvancedConfiguration advancedConfiguration;

	public AggregationConfiguration getAggregationConfiguration() {
		return aggregationConfiguration;
	}

	public void setAggregationConfiguration(
		AggregationConfiguration aggregationConfiguration) {

		this.aggregationConfiguration = aggregationConfiguration;
	}

	public void setAggregationConfiguration(
		UnsafeSupplier<AggregationConfiguration, Exception>
			aggregationConfigurationUnsafeSupplier) {

		try {
			aggregationConfiguration =
				aggregationConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AggregationConfiguration aggregationConfiguration;

	public GeneralConfiguration getGeneralConfiguration() {
		return generalConfiguration;
	}

	public void setGeneralConfiguration(
		GeneralConfiguration generalConfiguration) {

		this.generalConfiguration = generalConfiguration;
	}

	public void setGeneralConfiguration(
		UnsafeSupplier<GeneralConfiguration, Exception>
			generalConfigurationUnsafeSupplier) {

		try {
			generalConfiguration = generalConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected GeneralConfiguration generalConfiguration;

	public HighlightConfiguration getHighlightConfiguration() {
		return highlightConfiguration;
	}

	public void setHighlightConfiguration(
		HighlightConfiguration highlightConfiguration) {

		this.highlightConfiguration = highlightConfiguration;
	}

	public void setHighlightConfiguration(
		UnsafeSupplier<HighlightConfiguration, Exception>
			highlightConfigurationUnsafeSupplier) {

		try {
			highlightConfiguration = highlightConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected HighlightConfiguration highlightConfiguration;

	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}

	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	public void setIndexConfiguration(
		UnsafeSupplier<IndexConfiguration, Exception>
			indexConfigurationUnsafeSupplier) {

		try {
			indexConfiguration = indexConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected IndexConfiguration indexConfiguration;

	public ParameterConfiguration getParameterConfiguration() {
		return parameterConfiguration;
	}

	public void setParameterConfiguration(
		ParameterConfiguration parameterConfiguration) {

		this.parameterConfiguration = parameterConfiguration;
	}

	public void setParameterConfiguration(
		UnsafeSupplier<ParameterConfiguration, Exception>
			parameterConfigurationUnsafeSupplier) {

		try {
			parameterConfiguration = parameterConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ParameterConfiguration parameterConfiguration;

	public QueryConfiguration getQueryConfiguration() {
		return queryConfiguration;
	}

	public void setQueryConfiguration(QueryConfiguration queryConfiguration) {
		this.queryConfiguration = queryConfiguration;
	}

	public void setQueryConfiguration(
		UnsafeSupplier<QueryConfiguration, Exception>
			queryConfigurationUnsafeSupplier) {

		try {
			queryConfiguration = queryConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected QueryConfiguration queryConfiguration;

	public Map<String, Object> getSearchContextAttributes() {
		return searchContextAttributes;
	}

	public void setSearchContextAttributes(
		Map<String, Object> searchContextAttributes) {

		this.searchContextAttributes = searchContextAttributes;
	}

	public void setSearchContextAttributes(
		UnsafeSupplier<Map<String, Object>, Exception>
			searchContextAttributesUnsafeSupplier) {

		try {
			searchContextAttributes =
				searchContextAttributesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> searchContextAttributes;

	public SortConfiguration getSortConfiguration() {
		return sortConfiguration;
	}

	public void setSortConfiguration(SortConfiguration sortConfiguration) {
		this.sortConfiguration = sortConfiguration;
	}

	public void setSortConfiguration(
		UnsafeSupplier<SortConfiguration, Exception>
			sortConfigurationUnsafeSupplier) {

		try {
			sortConfiguration = sortConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SortConfiguration sortConfiguration;

	@Override
	public Configuration clone() throws CloneNotSupportedException {
		return (Configuration)super.clone();
	}

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
		return ConfigurationSerDes.toJSON(this);
	}

}