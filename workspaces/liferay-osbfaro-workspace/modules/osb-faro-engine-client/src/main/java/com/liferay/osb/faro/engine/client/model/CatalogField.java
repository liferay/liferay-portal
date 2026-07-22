/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Riccardo Ferrari
 */
public class CatalogField {

	public String getDataCategory() {
		return _dataCategory;
	}

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

	public String getParentField() {
		return _parentField;
	}

	public String getTableName() {
		return _tableName;
	}

	public void setDataCategory(String dataCategory) {
		_dataCategory = dataCategory;
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

	public void setParentField(String parentField) {
		_parentField = parentField;
	}

	public void setTableName(String tableName) {
		_tableName = tableName;
	}

	private String _dataCategory;
	private String _dataType;
	private String _description;
	private String _displayName;
	private String _id;
	private String _name;
	private String _parentField;
	private String _tableName;

}