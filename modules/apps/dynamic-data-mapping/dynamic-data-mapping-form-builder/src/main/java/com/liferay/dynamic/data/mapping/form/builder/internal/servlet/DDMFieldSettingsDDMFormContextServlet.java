/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.servlet;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"dynamic.data.mapping.form.builder.servlet=true",
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-form-builder-field-settings-form-context",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMFieldSettingsDDMFormContextServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-form-builder-field-settings-form-context/*"
	},
	service = Servlet.class
)
public class DDMFieldSettingsDDMFormContextServlet
	extends BaseDDMFormBuilderServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Map<String, Object> fieldSettingsFormContext =
			_createFieldSettingsFormContext(
				httpServletRequest, httpServletResponse);

		if (fieldSettingsFormContext == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		ServletResponseUtil.write(
			httpServletResponse,
			jsonSerializer.serializeDeep(fieldSettingsFormContext));
	}

	private Map<String, Object> _createFieldSettingsFormContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			String languageId = ParamUtil.getString(
				httpServletRequest, "languageId");
			String portletNamespace = ParamUtil.getString(
				httpServletRequest, "portletNamespace");
			String type = ParamUtil.getString(httpServletRequest, "type");

			Locale locale = LocaleUtil.fromLanguageId(languageId);

			LocaleThreadLocal.setThemeDisplayLocale(locale);

			Class<?> ddmFormFieldTypeSettings = _getDDMFormFieldTypeSettings(
				type);

			DDMForm ddmFormFieldTypeSettingsDDMForm = DDMFormFactory.create(
				ddmFormFieldTypeSettings);

			DDMFormLayout ddmFormFieldTypeSettingsDDMFormLayout =
				DDMFormLayoutFactory.create(ddmFormFieldTypeSettings);

			DDMFormRenderingContext ddmFormRenderingContext =
				new DDMFormRenderingContext();

			DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(
				httpServletRequest, ddmFormFieldTypeSettingsDDMForm);

			_setTypeDDMFormFieldValue(ddmFormValues, type);

			ddmFormRenderingContext.setDDMFormValues(ddmFormValues);

			ddmFormRenderingContext.setHttpServletRequest(httpServletRequest);
			ddmFormRenderingContext.setHttpServletResponse(httpServletResponse);
			ddmFormRenderingContext.setContainerId("settings");
			ddmFormRenderingContext.setLocale(locale);
			ddmFormRenderingContext.setPortletNamespace(portletNamespace);
			ddmFormRenderingContext.setReturnFullContext(true);

			return _ddmFormTemplateContextFactory.create(
				ddmFormFieldTypeSettingsDDMForm,
				ddmFormFieldTypeSettingsDDMFormLayout, ddmFormRenderingContext);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	private Class<?> _getDDMFormFieldTypeSettings(String type) {
		DDMFormFieldType ddmFormFieldType =
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(type);

		return ddmFormFieldType.getDDMFormFieldTypeSettings();
	}

	private void _setTypeDDMFormFieldValue(
		DDMFormValues ddmFormValues, String type) {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap();

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			"type");

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		ddmFormFieldValue.setValue(new UnlocalizedValue(type));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFieldSettingsDDMFormContextServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference
	private DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private JSONFactory _jsonFactory;

}