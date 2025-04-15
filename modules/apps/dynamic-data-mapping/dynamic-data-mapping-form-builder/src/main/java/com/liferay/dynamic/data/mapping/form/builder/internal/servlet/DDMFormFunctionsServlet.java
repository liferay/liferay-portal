/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.servlet;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"dynamic.data.mapping.form.builder.servlet=true",
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-form-builder-functions",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMFormFunctionsServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-form-builder-functions/*"
	},
	service = Servlet.class
)
public class DDMFormFunctionsServlet extends BaseDDMFormBuilderServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		Map<String, DDMExpressionFunctionFactory>
			ddmExpressionFunctionFactories =
				_getDDMExpressionFunctionFactories();

		JSONArray jsonArray = _toJSONArray(
			ddmExpressionFunctionFactories.entrySet(),
			LocaleUtil.fromLanguageId(
				ParamUtil.getString(httpServletRequest, "languageId")));

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());
	}

	protected JSONObject toJSONObject(
		Map.Entry<String, DDMExpressionFunctionFactory> entry,
		ResourceBundle resourceBundle) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		String key = entry.getKey();

		String labelLanguageKey = key + "_function";

		jsonObject.put(
			"label", _language.get(resourceBundle, labelLanguageKey)
		).put(
			"value", key
		);

		String tooltipLanguageKey = key + "_tooltip";

		jsonObject.put(
			"tooltip", _language.get(resourceBundle, tooltipLanguageKey));

		return jsonObject;
	}

	private Map<String, DDMExpressionFunctionFactory>
		_getDDMExpressionFunctionFactories() {

		Set<String> functionNames = new HashSet<>();

		functionNames.add("sum");

		return _ddmExpressionFunctionRegistry.getDDMExpressionFunctionFactories(
			functionNames);
	}

	private JSONArray _toJSONArray(
		Set<Map.Entry<String, DDMExpressionFunctionFactory>> entries,
		Locale locale) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		for (Map.Entry<String, DDMExpressionFunctionFactory> entry : entries) {
			jsonArray.put(toJSONObject(entry, resourceBundle));
		}

		return jsonArray;
	}

	private static final long serialVersionUID = 1L;

	@Reference
	private DDMExpressionFunctionRegistry _ddmExpressionFunctionRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}