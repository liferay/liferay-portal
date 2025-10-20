/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.web.internal.model.display.main.EntityDisplay;
import com.liferay.osb.faro.web.internal.util.FieldMappingUtil;
import com.liferay.osb.faro.web.internal.util.SchemaOrgUtil;

import java.util.Map;

/**
 * @author Matthew Kong
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class FieldMappingDisplay extends EntityDisplay {

	public FieldMappingDisplay() {
	}

	public FieldMappingDisplay(FieldMapping fieldMapping) {
		setId(fieldMapping.getFieldName());

		_context = fieldMapping.getContext();
		_dataSourceFieldNames = fieldMapping.getDataSourceFieldNames();
		_displayName = FieldMappingUtil.getDisplayName(fieldMapping);
		_displayType = fieldMapping.getDisplayType();
		_name = fieldMapping.getDisplayName();
		_ownerType = fieldMapping.getOwnerType();
		_rawType = SchemaOrgUtil.getRawType(
			fieldMapping.getDisplayType(), fieldMapping.getFieldType());
		_type = fieldMapping.getFieldType();
	}

	public String getName() {
		return _name;
	}

	public String getType() {
		return _type;
	}

	private String _context;
	private Map<String, String> _dataSourceFieldNames;
	private String _displayName;
	private String _displayType;
	private String _name;
	private String _ownerType;
	private String _rawType;
	private String _type;

}