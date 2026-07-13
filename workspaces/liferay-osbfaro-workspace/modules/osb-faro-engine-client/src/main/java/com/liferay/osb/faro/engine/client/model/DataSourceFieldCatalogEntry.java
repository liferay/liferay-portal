/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Riccardo Ferrari
 */
public class DataSourceFieldCatalogEntry {

	public DataSourceFieldCatalogEntry() {
	}

	public DataSourceFieldCatalogEntry(
		String name, boolean required, boolean selected, String type) {

		_name = name;
		_required = required;
		_selected = selected;
		_type = type;
	}

	public String getName() {
		return _name;
	}

	public String getType() {
		return _type;
	}

	public boolean isRequired() {
		return _required;
	}

	public boolean isSelected() {
		return _selected;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	public void setSelected(boolean selected) {
		_selected = selected;
	}

	public void setType(String type) {
		_type = type;
	}

	private String _name;
	private boolean _required;
	private boolean _selected;
	private String _type;

}