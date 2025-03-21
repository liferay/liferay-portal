/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.rich.text;

import com.liferay.ai.creator.openai.manager.AICreatorOpenAIManager;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueUtil;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Lancha
 * @author Marko Cikos
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.RICH_TEXT,
	service = DDMFormFieldTemplateContextContributor.class
)
public class RichTextDDMFormFieldTemplateContextContributor
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
			() -> {
				if (localizedObjectField) {
					return _getPredefinedValue(
						ddmFormField, ddmFormFieldRenderingContext);
				}

				return DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, ddmFormFieldRenderingContext.getLocale(),
					"predefinedValue");
			}
		).put(
			"value",
			() -> {
				if (localizedObjectField) {
					return DDMFormFieldValueUtil.getValueJSONObject(
						ddmFormFieldRenderingContext);
				}

				return DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormFieldRenderingContext, "value");
			}
		).putAll(
			DDMFormFieldTemplateContextContributorUtil.getLocaleMap(
				ddmForm.getDefaultLocale())
		).putAll(
			_getData(ddmFormFieldRenderingContext, ddmFormField.getType())
		).build();
	}

	private Map<String, Object> _getData(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		String ddmFormFieldType) {

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				themeDisplay.getPpid(), ddmFormFieldType, "ckeditor_classic",
				HashMapBuilder.<String, Object>put(
					"liferay-ui:input-editor:allowBrowseDocuments", true
				).put(
					"liferay-ui:input-editor:name",
					ddmFormFieldRenderingContext.getName()
				).put(
					"liferay-ui:input-editor:showAICreator",
					_aiCreatorOpenAIManager.isAICreatorToolbarEnabled(
						themeDisplay.getCompanyId(),
						themeDisplay.getScopeGroupId(),
						ddmFormFieldRenderingContext.getPortletNamespace())
				).build(),
				themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest));

		return editorConfiguration.getData();
	}

	private String _getPredefinedValue(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		LocalizedValue localizedValue = ddmFormField.getPredefinedValue();

		if (localizedValue == null) {
			return null;
		}

		return localizedValue.getString(
			ddmFormFieldRenderingContext.getLocale());
	}

	@Reference
	private AICreatorOpenAIManager _aiCreatorOpenAIManager;

}