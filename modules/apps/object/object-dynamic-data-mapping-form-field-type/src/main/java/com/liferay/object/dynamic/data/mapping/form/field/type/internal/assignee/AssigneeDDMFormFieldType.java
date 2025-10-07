/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.assignee;

import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Igor Franca
 */
@Component(
	property = {
		"ddm.form.field.type.name=" + ObjectDDMFormFieldTypeConstants.ASSIGNEE,
		"ddm.form.field.type.system=true"
	},
	service = DDMFormFieldType.class
)
public class AssigneeDDMFormFieldType extends BaseDDMFormFieldType {

	@Override
	public String getESModule() {
		return "{Assignee} from object-dynamic-data-mapping-form-field-type";
	}

	@Override
	public String getName() {
		return ObjectDDMFormFieldTypeConstants.ASSIGNEE;
	}

	@Override
	public boolean isCustomDDMFormFieldType() {
		return true;
	}

}