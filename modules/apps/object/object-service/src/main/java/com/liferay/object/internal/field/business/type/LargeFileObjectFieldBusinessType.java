/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_LARGE_FILE,
	service = ObjectFieldBusinessType.class
)
public class LargeFileObjectFieldBusinessType
	implements ObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_BLOB;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return null;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "large-file");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_LARGE_FILE;
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.TEXT;
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
	}

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		return false;
	}

	@Reference
	private Language _language;

}