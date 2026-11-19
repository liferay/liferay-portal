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
public class GetWorkspaceGroupEventDefinitionsPageResponse {

	public EventDefinitionBag getEventDefinitionBag() {
		return _eventDefinitionBag;
	}

	public void setEventDefinitionBag(EventDefinitionBag eventDefinitionBag) {
		_eventDefinitionBag = eventDefinitionBag;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EventDefinition {

		public String getDescription() {
			return _description;
		}

		public String getDisplayName() {
			return _displayName;
		}

		public Boolean getHidden() {
			return _hidden;
		}

		public String getId() {
			return _id;
		}

		public String getName() {
			return _name;
		}

		public String getType() {
			return _type;
		}

		public void setDescription(String description) {
			_description = description;
		}

		public void setDisplayName(String displayName) {
			_displayName = displayName;
		}

		public void setHidden(Boolean hidden) {
			_hidden = hidden;
		}

		public void setId(String id) {
			_id = id;
		}

		public void setName(String name) {
			_name = name;
		}

		public void setType(String type) {
			_type = type;
		}

		private String _description;
		private String _displayName;
		private Boolean _hidden;
		private String _id;
		private String _name;
		private String _type;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EventDefinitionBag {

		public List<EventDefinition> getEventDefinitions() {
			return _eventDefinitions;
		}

		public Integer getTotal() {
			return _total;
		}

		public void setEventDefinitions(
			List<EventDefinition> eventDefinitions) {

			_eventDefinitions = eventDefinitions;
		}

		public void setTotal(Integer total) {
			_total = total;
		}

		private List<EventDefinition> _eventDefinitions;
		private Integer _total;

	}

	private EventDefinitionBag _eventDefinitionBag;

}