/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Caio Pinheiro
 */
public class AcquisitionParameter {

	public String getFieldName() {
		return _fieldName;
	}

	public String getName() {
		return _name;
	}

	public Boolean getStandard() {
		return _standard;
	}

	public String getType() {
		return _type;
	}

	public void setFieldName(String fieldName) {
		_fieldName = fieldName;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setStandard(Boolean standard) {
		_standard = standard;
	}

	public void setType(String type) {
		_type = type;
	}

	private String _fieldName;
	private String _name;
	private Boolean _standard;
	private String _type;

}