/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.field.customizer;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.segments.field.Field;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides an interface for customizing a {@link Field}.
 *
 * @author Eduardo García
 */
public interface SegmentsFieldCustomizer {

	public default ClassedModel getClassedModel(String fieldValue) {
		return null;
	}

	public default String getClassName() {
		return null;
	}

	public List<String> getFieldNames();

	public default String getFieldValueName(String fieldValue, Locale locale) {
		return fieldValue;
	}

	public default String getIcon() {
		return null;
	}

	public String getKey();

	public default String getLabel(String fieldName, Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(
			resourceBundle, "field." + CamelCaseUtil.fromCamelCase(fieldName));
	}

	public default List<Field.Option> getOptions(Locale locale) {
		return Collections.emptyList();
	}

	public default Field.SelectEntity getSelectEntity(
		PortletRequest portletRequest) {

		return null;
	}

}