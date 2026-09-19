/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.openapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Javier Gamarra
 */
public class DTOProperty {

	public DTOProperty(
		Map<String, Object> extensions, String name, String type) {

		_extensions = extensions;
		_name = name;
		_type = type;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public DTOProperty(String name, String type) {
		this(new HashMap<>(), name, type);
	}

	public List<DTOProperty> getDTOProperties() {
		return _dtoProperties;
	}

	public String getDescription() {
		return _description;
	}

	public Map<String, Object> getExtensions() {
		return _extensions;
	}

	public String getName() {
		return _name;
	}

	public Boolean getReadOnly() {
		return _readOnly;
	}

	public String getType() {
		return _type;
	}

	public boolean isRequired() {
		return _required;
	}

	public void setDTOProperties(List<DTOProperty> dtoProperties) {
		_dtoProperties = dtoProperties;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setReadOnly(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	public void setType(String type) {
		_type = type;
	}

	private String _description;
	private List<DTOProperty> _dtoProperties = new ArrayList<>();
	private final Map<String, Object> _extensions;
	private String _name;
	private Boolean _readOnly;
	private boolean _required;
	private String _type;

}