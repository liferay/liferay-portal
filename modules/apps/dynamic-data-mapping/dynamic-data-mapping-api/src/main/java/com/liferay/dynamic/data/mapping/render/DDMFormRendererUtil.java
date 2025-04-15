/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.render;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMFieldsCounter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;

/**
 * @author Marcellus Tavares
 */
public class DDMFormRendererUtil {

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public static DDMFormRenderer getDDMFormRenderer() {
		return null;
	}

	public static String render(
			DDMForm ddmForm,
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws PortalException {

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		StringBundler sb = new StringBundler(ddmFormFields.size());

		ddmFormFieldRenderingContext.setProperty(
			"fieldNamespaces", new HashSet<String>());

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (_isDDMFormFieldSkippable(
					ddmFormField, ddmFormFieldRenderingContext)) {

				continue;
			}

			DDMFormFieldRenderer ddmFormFieldRenderer =
				DDMFormFieldRendererRegistryUtil.getDDMFormFieldRenderer(
					ddmFormField.getType());

			sb.append(
				ddmFormFieldRenderer.render(
					ddmFormField, ddmFormFieldRenderingContext));
		}

		_clearDDMFieldsCounter(ddmFormFieldRenderingContext);

		return sb.toString();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public void setDDMFormRenderer(DDMFormRenderer ddmFormRenderer) {
	}

	private static void _clearDDMFieldsCounter(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		String fieldsCounterKey =
			ddmFormFieldRenderingContext.getPortletNamespace() +
				ddmFormFieldRenderingContext.getNamespace() + "fieldsCount";

		DDMFieldsCounter ddmFieldsCounter =
			(DDMFieldsCounter)httpServletRequest.getAttribute(fieldsCounterKey);

		if (ddmFieldsCounter != null) {
			ddmFieldsCounter.clear();
		}
	}

	private static boolean _isDDMFormFieldSkippable(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		if (!ddmFormFieldRenderingContext.isReadOnly() ||
			ddmFormFieldRenderingContext.isShowEmptyFieldLabel()) {

			return false;
		}

		Fields fields = ddmFormFieldRenderingContext.getFields();

		if (fields.contains(ddmFormField.getName())) {
			return false;
		}

		for (DDMFormField nestedDDMFormField :
				ddmFormField.getNestedDDMFormFields()) {

			if (!_isDDMFormFieldSkippable(
					nestedDDMFormField, ddmFormFieldRenderingContext)) {

				return false;
			}
		}

		return true;
	}

}