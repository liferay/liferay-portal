/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_FORMULA,
	service = ObjectFieldBusinessType.class
)
public class FormulaObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public Set<String> getAllowedObjectFieldSettingsNames() {
		return SetUtil.fromArray("output", "script");
	}

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_STRING;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return DDMFormFieldTypeConstants.TEXT;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			locale,
			"add-an-algorithm-that-derives-its-value-from-other-fields");
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "formula");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_FORMULA;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public Set<String> getRequiredObjectFieldSettingsNames(
		ObjectField objectField) {

		return SetUtil.fromArray("output");
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
	}

	@Override
	public void validateObjectFieldSettings(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		super.validateObjectFieldSettings(objectField, objectFieldSettings);

		Map<String, String> objectFieldSettingsValues =
			getObjectFieldSettingsValues(objectFieldSettings);

		String script = objectFieldSettingsValues.get("script");

		if (Validator.isNull(script)) {
			return;
		}

		try {
			_ddmExpressionFactory.createExpression(
				CreateExpressionRequest.Builder.newBuilder(
					script
				).build());
		}
		catch (DDMExpressionException ddmExpressionException) {
			throw new ScriptingException(ddmExpressionException);
		}
	}

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private Language _language;

}