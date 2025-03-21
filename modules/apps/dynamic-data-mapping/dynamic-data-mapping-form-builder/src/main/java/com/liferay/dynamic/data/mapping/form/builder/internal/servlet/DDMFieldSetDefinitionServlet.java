/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.servlet;

import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextFactory;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextRequest;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextResponse;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
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

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"dynamic.data.mapping.form.builder.servlet=true",
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-form-builder-fieldset-definition",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.builder.internal.servlet.DDMFieldSetDefinitionServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-form-builder-fieldset-definition/*"
	},
	service = Servlet.class
)
public class DDMFieldSetDefinitionServlet extends BaseDDMFormBuilderServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		long ddmStructureId = ParamUtil.getLong(
			httpServletRequest, "ddmStructureId");

		if (ddmStructureId == 0) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		String languageId = ParamUtil.getString(
			httpServletRequest, "languageId");

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		LocaleThreadLocal.setThemeDisplayLocale(locale);

		DDMFormBuilderContextRequest ddmFormBuilderContextRequest =
			DDMFormBuilderContextRequest.with(
				_getDDMStructure(ddmStructureId), httpServletRequest,
				httpServletResponse, locale, true);

		String portletNamespace = ParamUtil.getString(
			httpServletRequest, "portletNamespace");

		ddmFormBuilderContextRequest.addProperty(
			"portletNamespace", portletNamespace);

		DDMFormBuilderContextResponse fieldContext =
			_ddmFormBuilderContextFactory.create(ddmFormBuilderContextRequest);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		ServletResponseUtil.write(
			httpServletResponse,
			jsonSerializer.serializeDeep(fieldContext.getContext()));
	}

	private DDMStructure _getDDMStructure(long ddmStructureId) {
		try {
			return _ddmStructureService.getStructure(ddmStructureId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFieldSetDefinitionServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private DDMFormBuilderContextFactory _ddmFormBuilderContextFactory;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private JSONFactory _jsonFactory;

}