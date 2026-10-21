/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.yaml.openapi;

/**
 * @author Peter Shin
 */
public class Info {

	public String getDescription() {
		return _description;
	}

	public String getFeatureFlag() {
		return _featureFlag;
	}

	public License getLicense() {
		return _license;
	}

	public String getTitle() {
		return _title;
	}

	public String getVersion() {
		return _version;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setFeatureFlag(String featureFlag) {
		_featureFlag = featureFlag;
	}

	public void setLicense(License license) {
		_license = license;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setVersion(String version) {
		_version = version;
	}

	private String _description;
	private String _featureFlag;
	private License _license;
	private String _title;
	private String _version;

}