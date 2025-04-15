/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInvoker;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = DDMFormFieldOptionsFactory.class)
public class DDMFormFieldOptionsFactoryImpl
	implements DDMFormFieldOptionsFactory {

	@Override
	public DDMFormFieldOptions create(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		String dataSourceType = ddmFormField.getDataSourceType();

		if (Objects.equals(dataSourceType, "data-provider")) {
			return _createDDMFormFieldOptionsFromDataProvider(
				ddmFormField, ddmFormFieldRenderingContext);
		}

		return _createDDMFormFieldOptions(
			ddmFormField, ddmFormFieldRenderingContext, dataSourceType);
	}

	@Reference
	protected DDMDataProviderInvoker ddmDataProviderInvoker;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Portal portal;

	private DDMFormFieldOptions _createDDMFormFieldOptions(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		String dataSourceType) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.setDefaultLocale(
			ddmFormFieldRenderingContext.getLocale());

		List<Map<String, String>> options =
			(List<Map<String, String>>)ddmFormFieldRenderingContext.getProperty(
				"options");

		if (ListUtil.isEmpty(options)) {
			if (dataSourceType.equals("from-autofill")) {
				return ddmFormFieldOptions;
			}

			Map<String, Object> changedProperties =
				(Map<String, Object>)ddmFormFieldRenderingContext.getProperty(
					"changedProperties");

			if (MapUtil.isNotEmpty(changedProperties) &&
				changedProperties.containsKey("options")) {

				return ddmFormFieldOptions;
			}

			return ddmFormField.getDDMFormFieldOptions();
		}

		for (Map<String, String> option : options) {
			ddmFormFieldOptions.addOptionLabel(
				option.get("value"), ddmFormFieldRenderingContext.getLocale(),
				option.get("label"));
			ddmFormFieldOptions.addOptionReference(
				option.get("value"), option.get("reference"));
		}

		return ddmFormFieldOptions;
	}

	private DDMFormFieldOptions _createDDMFormFieldOptionsFromDataProvider(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.setDefaultLocale(
			ddmFormFieldRenderingContext.getLocale());

		try {
			String ddmDataProviderInstanceId = _getJSONArrayFirstValue(
				GetterUtil.getString(
					ddmFormField.getProperty("ddmDataProviderInstanceId")));

			HttpServletRequest httpServletRequest =
				ddmFormFieldRenderingContext.getHttpServletRequest();

			DDMDataProviderRequest.Builder builder =
				DDMDataProviderRequest.Builder.newBuilder();

			DDMDataProviderRequest ddmDataProviderRequest =
				builder.withDDMDataProviderId(
					ddmDataProviderInstanceId
				).withCompanyId(
					portal.getCompanyId(httpServletRequest)
				).withGroupId(
					_getGroupId(httpServletRequest)
				).withLocale(
					ddmFormFieldRenderingContext.getLocale()
				).withParameter(
					"filterParameterValue",
					HtmlUtil.escapeURL(
						String.valueOf(ddmFormFieldRenderingContext.getValue()))
				).withParameter(
					"httpServletRequest", httpServletRequest
				).build();

			DDMDataProviderResponse ddmDataProviderResponse =
				ddmDataProviderInvoker.invoke(ddmDataProviderRequest);

			String ddmDataProviderInstanceOutput = _getJSONArrayFirstValue(
				GetterUtil.getString(
					ddmFormField.getProperty("ddmDataProviderInstanceOutput"),
					"Default-Output"));

			List<KeyValuePair> keyValuesPairs =
				ddmDataProviderResponse.getOutput(
					ddmDataProviderInstanceOutput, List.class);

			if (keyValuesPairs == null) {
				return ddmFormFieldOptions;
			}

			for (KeyValuePair keyValuePair : keyValuesPairs) {
				ddmFormFieldOptions.addOptionLabel(
					keyValuePair.getKey(),
					ddmFormFieldRenderingContext.getLocale(),
					keyValuePair.getValue());
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return ddmFormFieldOptions;
	}

	private long _getGroupId(HttpServletRequest httpServletRequest) {
		long scopeGroupId = ParamUtil.getLong(
			httpServletRequest, "scopeGroupId");

		if (scopeGroupId == 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			scopeGroupId = themeDisplay.getScopeGroupId();
		}

		return scopeGroupId;
	}

	private String _getJSONArrayFirstValue(String value) {
		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(value);

			return jsonArray.getString(0);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return value;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldOptionsFactoryImpl.class);

}