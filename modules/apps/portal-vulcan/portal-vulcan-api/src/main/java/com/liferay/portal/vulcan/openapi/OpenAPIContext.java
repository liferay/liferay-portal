/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.openapi;

import jakarta.ws.rs.core.UriInfo;

/**
 * @author Carlos Correa
 */
public class OpenAPIContext {

	public String getBaseURL() {
		return _baseURL;
	}

	public String getPath() {
		return _path;
	}

	public UriInfo getUriInfo() {
		return _uriInfo;
	}

	public String getVersion() {
		return _version;
	}

	public void setBaseURL(String baseURL) {
		_baseURL = baseURL;
	}

	public void setPath(String path) {
		_path = path;
	}

	public void setUriInfo(UriInfo uriInfo) {
		_uriInfo = uriInfo;
	}

	public void setVersion(String version) {
		_version = version;
	}

	private String _baseURL;
	private String _path;
	private UriInfo _uriInfo;
	private String _version;

}