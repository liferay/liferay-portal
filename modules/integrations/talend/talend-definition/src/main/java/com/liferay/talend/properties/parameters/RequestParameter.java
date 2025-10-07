/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.properties.parameters;

import java.util.Objects;

import javax.ws.rs.core.UriBuilder;

/**
 * @author Igor Beslic
 */
public class RequestParameter {

	public RequestParameter(String location, String name, String value) {
		_location = location;
		_name = name;
		_value = value;
	}

	public void apply(UriBuilder uriBuilder) {
		if (Objects.equals(_location, "query")) {
			uriBuilder.queryParam(getName(), getValue());
		}
		else if (Objects.equals(_location, "path")) {
			uriBuilder.resolveTemplate(getName(), getValue());
		}
	}

	public String getLocation() {
		return _location;
	}

	public String getName() {
		if (_name.endsWith("*")) {
			return _name.substring(0, _name.length() - 1);
		}

		return _name;
	}

	public String getValue() {
		return _value;
	}

	public boolean isPathLocation() {
		return Objects.equals(_location, "path");
	}

	private final String _location;
	private final String _name;
	private final String _value;

}