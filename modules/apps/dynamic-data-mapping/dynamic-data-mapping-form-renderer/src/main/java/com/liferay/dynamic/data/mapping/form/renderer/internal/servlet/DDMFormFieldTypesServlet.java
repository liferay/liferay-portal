/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal.servlet;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.taglib.aui.ESImport;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matuzalem Teles
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-form-field-types",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.renderer.internal.servlet.DDMFormFieldTypesServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-form-field-types/*"
	},
	service = Servlet.class
)
public class DDMFormFieldTypesServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		createContext(httpServletRequest, httpServletResponse);

		super.service(httpServletRequest, httpServletResponse);
	}

	protected void createContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			EventsProcessorUtil.process(
				PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
				PropsValues.SERVLET_SERVICE_EVENTS_PRE, httpServletRequest,
				httpServletResponse);
		}
		catch (ActionException actionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(actionException);
			}
		}
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		JSONArray jsonArray = null;

		try {
			jsonArray = JSONUtil.toJSONArray(
				_ddmFormFieldTypeServicesRegistry.getDDMFormFieldTypeNames(),
				ddmFormFieldTypeName -> _getFieldTypeMetadataJSONObject(
					ddmFormFieldTypeName, Collections.emptyMap(),
					httpServletRequest));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());
	}

	@Reference
	protected NPMResolver npmResolver;

	private JSONObject _getFieldTypeMetadataJSONObject(
		String ddmFormFieldName, Map<String, Object> configuration,
		HttpServletRequest httpServletRequest) {

		JSONObject jsonObject = new JSONObjectImpl();

		return jsonObject.put(
			"configuration",
			() -> {
				if (!configuration.isEmpty()) {
					return configuration;
				}

				return null;
			}
		).put(
			"javaScriptModule",
			_resolveModuleName(
				_ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
					ddmFormFieldName),
				httpServletRequest)
		).put(
			"name", ddmFormFieldName
		);
	}

	private String _resolveModuleName(
		DDMFormFieldType ddmFormFieldType,
		HttpServletRequest httpServletRequest) {

		String esModule = ddmFormFieldType.getESModule();

		if (Validator.isNotNull(esModule)) {
			ESImport esImport = ESImportUtil.getESImport(
				_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
					httpServletRequest),
				esModule);

			return StringBundler.concat(
				"{", esImport.getSymbol(), "} from ", esImport.getModule());
		}

		if (Validator.isNull(ddmFormFieldType.getModuleName())) {
			return StringPool.BLANK;
		}

		if (ddmFormFieldType.isCustomDDMFormFieldType()) {
			return ddmFormFieldType.getModuleName();
		}

		return npmResolver.resolveModuleName(ddmFormFieldType.getModuleName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldTypesServlet.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

}