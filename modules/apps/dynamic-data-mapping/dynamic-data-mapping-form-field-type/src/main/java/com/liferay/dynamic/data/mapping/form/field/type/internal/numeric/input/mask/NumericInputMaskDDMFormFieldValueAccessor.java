/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.numeric.input.mask;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.NumericInputMaskDDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.string.StringPool;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.NUMERIC_INPUT_MASK,
	service = DDMFormFieldValueAccessor.class
)
public class NumericInputMaskDDMFormFieldValueAccessor
	extends DefaultDDMFormFieldValueAccessor {

	@Override
	public String getValue(DDMFormFieldValue ddmFormFieldValue, Locale locale) {
		Value value = ddmFormFieldValue.getValue();

		if (value == null) {
			return StringPool.BLANK;
		}

		return NumericInputMaskDDMFormFieldTypeUtil.getJSON(
			value.getString(locale));
	}

}