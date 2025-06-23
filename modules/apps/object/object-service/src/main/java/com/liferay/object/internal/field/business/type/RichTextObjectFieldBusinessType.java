/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
	service = ObjectFieldBusinessType.class
)
public class RichTextObjectFieldBusinessType
	implements ObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_CLOB;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.RICH_TEXT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(locale, "create-rich-text-content");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "rich-text");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		Object value = ObjectFieldBusinessType.super.getValue(
			groupId, objectField, userId, values);

		if (Validator.isNull(value)) {
			return value;
		}

		if (!FeatureFlagManagerUtil.isEnabled("LPD-31212")) {
			ObjectDefinition objectDefinition =
				objectField.getObjectDefinition();

			value = SanitizerUtil.sanitize(
				objectField.getCompanyId(), 0, objectField.getUserId(),
				objectDefinition.getClassName(), 0, ContentTypes.TEXT_HTML,
				Sanitizer.MODE_ALL, String.valueOf(value), null);
		}
		else {
			value = _htmlParser.extractText(
				HtmlUtil.escape(String.valueOf(value)));
		}

		return value;
	}

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Language _language;

}