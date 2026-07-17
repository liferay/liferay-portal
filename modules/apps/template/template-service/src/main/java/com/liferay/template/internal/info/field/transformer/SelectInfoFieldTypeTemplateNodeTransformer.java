/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.internal.info.field.transformer;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.template.info.field.transformer.TemplateNodeTransformer;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "info.field.type.class.name=com.liferay.info.field.type.SelectInfoFieldType",
	service = TemplateNodeTransformer.class
)
public class SelectInfoFieldTypeTemplateNodeTransformer
	extends BaseSelectInfoFieldTypeTemplateNodeTransformer {

	@Override
	protected Map<String, String> getAttributes() {
		return HashMapBuilder.put(
			"multiple", Boolean.FALSE.toString()
		).build();
	}

	@Override
	protected String getKey(JSONArray selectedOptionValuesJSONArray) {
		if (!JSONUtil.isEmpty(selectedOptionValuesJSONArray)) {
			return selectedOptionValuesJSONArray.getString(0);
		}

		return StringPool.BLANK;
	}

	@Override
	protected String getLabel(
		Map<String, String> optionsMap,
		JSONArray selectedOptionValuesJSONArray) {

		if (JSONUtil.isEmpty(selectedOptionValuesJSONArray)) {
			return StringPool.BLANK;
		}

		String key = selectedOptionValuesJSONArray.getString(0);

		return optionsMap.getOrDefault(key, StringPool.BLANK);
	}

	@Override
	protected List<OptionInfoFieldType> getOptionInfoFieldTypes(
		InfoField infoField) {

		return (List<OptionInfoFieldType>)infoField.getAttribute(
			SelectInfoFieldType.OPTIONS);
	}

}