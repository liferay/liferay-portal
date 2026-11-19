/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author Caio Pinheiro
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetWorkspaceGroupEventAttributeDefinitionsPageResponse {

	public EventAttributeDefinitionBag getEventAttributeDefinitionBag() {
		return _eventAttributeDefinitionBag;
	}

	public void setEventAttributeDefinitionBag(
		EventAttributeDefinitionBag eventAttributeDefinitionBag) {

		_eventAttributeDefinitionBag = eventAttributeDefinitionBag;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EventAttributeDefinition {

		public String getDataType() {
			return _dataType;
		}

		public String getDescription() {
			return _description;
		}

		public String getDisplayName() {
			return _displayName;
		}

		public String getId() {
			return _id;
		}

		public String getName() {
			return _name;
		}

		public String getSampleValue() {
			return _sampleValue;
		}

		public String getType() {
			return _type;
		}

		public void setDataType(String dataType) {
			_dataType = dataType;
		}

		public void setDescription(String description) {
			_description = description;
		}

		public void setDisplayName(String displayName) {
			_displayName = displayName;
		}

		public void setId(String id) {
			_id = id;
		}

		public void setName(String name) {
			_name = name;
		}

		public void setSampleValue(String sampleValue) {
			_sampleValue = sampleValue;
		}

		public void setType(String type) {
			_type = type;
		}

		private String _dataType;
		private String _description;
		private String _displayName;
		private String _id;
		private String _name;
		private String _sampleValue;
		private String _type;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EventAttributeDefinitionBag {

		public List<EventAttributeDefinition> getEventAttributeDefinitions() {
			return _eventAttributeDefinitions;
		}

		public Integer getTotal() {
			return _total;
		}

		public void setEventAttributeDefinitions(
			List<EventAttributeDefinition> eventAttributeDefinitions) {

			_eventAttributeDefinitions = eventAttributeDefinitions;
		}

		public void setTotal(Integer total) {
			_total = total;
		}

		private List<EventAttributeDefinition> _eventAttributeDefinitions;
		private Integer _total;

	}

	private EventAttributeDefinitionBag _eventAttributeDefinitionBag;

}