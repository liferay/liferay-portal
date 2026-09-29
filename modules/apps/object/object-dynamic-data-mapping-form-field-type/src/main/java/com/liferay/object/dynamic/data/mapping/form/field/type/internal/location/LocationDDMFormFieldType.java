/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.location;

import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Kevin Tan
 */
@Component(
	property = {
		"ddm.form.field.type.name=" + ObjectDDMFormFieldTypeConstants.LOCATION,
		"ddm.form.field.type.system=true"
	},
	service = DDMFormFieldType.class
)
public class LocationDDMFormFieldType extends BaseDDMFormFieldType {

	@Override
	public String getESModule() {
		return "{Location} from object-dynamic-data-mapping-form-field-type";
	}

	@Override
	public String getName() {
		return ObjectDDMFormFieldTypeConstants.LOCATION;
	}

	@Override
	public boolean isCustomDDMFormFieldType() {
		return true;
	}

}