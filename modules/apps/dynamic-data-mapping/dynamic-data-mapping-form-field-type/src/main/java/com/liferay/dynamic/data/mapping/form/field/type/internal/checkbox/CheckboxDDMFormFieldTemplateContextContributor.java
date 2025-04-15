/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.checkbox;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.CHECKBOX,
	service = DDMFormFieldTemplateContextContributor.class
)
public class CheckboxDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		DDMForm ddmForm = ddmFormField.getDDMForm();

		boolean localizedObjectField = GetterUtil.getBoolean(
			ddmFormField.getProperty("localizedObjectField"));

		return HashMapBuilder.<String, Object>put(
			"localizedObjectField", localizedObjectField
		).put(
			"predefinedValue",
			GetterUtil.getBoolean(
				DDMFormFieldTypeUtil.getValue(
					DDMFormFieldTypeUtil.getPropertyValue(
						ddmFormField, ddmFormFieldRenderingContext.getLocale(),
						"predefinedValue")))
		).put(
			"showAsSwitcher",
			GetterUtil.getBoolean(ddmFormField.getProperty("showAsSwitcher"))
		).put(
			"showMaximumRepetitionsInfo",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("showMaximumRepetitionsInfo"))
		).put(
			"systemSettingsURL",
			() -> {
				if (!GetterUtil.getBoolean(
						ddmFormField.getProperty(
							"showMaximumRepetitionsInfo"))) {

					return StringPool.BLANK;
				}

				return _getSystemSettingsURL(
					ddmFormFieldRenderingContext.getHttpServletRequest());
			}
		).put(
			"tooltip",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext.getLocale(),
				"tooltip")
		).put(
			"value",
			() -> {
				if (localizedObjectField) {
					return DDMFormFieldValueUtil.getValueJSONObject(
						ddmFormFieldRenderingContext);
				}

				return GetterUtil.getBoolean(
					DDMFormFieldTypeUtil.getValue(
						ddmFormFieldRenderingContext.getValue()));
			}
		).putAll(
			DDMFormFieldTemplateContextContributorUtil.
				getLocalizationParameters(
					ddmFormField, ddmForm.getDefaultLocale())
		).build();
	}

	private String _getSystemSettingsURL(
		HttpServletRequest httpServletRequest) {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createActionURL(
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS)
		).setMVCRenderCommandName(
			"/configuration_admin/edit_configuration"
		).setParameter(
			"factoryPid",
			"com.liferay.dynamic.data.mapping.form.web.internal." +
				"configuration.DDMFormWebConfiguration"
		).buildString();
	}

}