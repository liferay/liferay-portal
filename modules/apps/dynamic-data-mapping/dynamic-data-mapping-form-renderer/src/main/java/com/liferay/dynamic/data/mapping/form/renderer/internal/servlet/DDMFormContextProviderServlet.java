/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal.servlet;

import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.internal.DDMFormPagesTemplateContextFactory;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/dynamic-data-mapping-form-context-provider",
		"osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.renderer.internal.servlet.DDMFormContextProviderServlet",
		"osgi.http.whiteboard.servlet.pattern=/dynamic-data-mapping-form-context-provider/*"
	},
	service = Servlet.class
)
public class DDMFormContextProviderServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		UploadException uploadException =
			(UploadException)httpServletRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if ((uploadException != null) &&
			(uploadException.isExceededFileSizeLimit() ||
			 uploadException.isExceededLiferayFileItemSizeLimit() ||
			 uploadException.isExceededUploadRequestSizeLimit())) {

			httpServletResponse.sendError(
				HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
				_language.get(httpServletRequest, "upload-size-is-too-large"));

			return;
		}

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
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String portletNamespace = ParamUtil.getString(
			httpServletRequest, "portletNamespace");

		List<Object> ddmFormPagesTemplateContext =
			_createDDMFormPagesTemplateContext(
				httpServletRequest, httpServletResponse, portletNamespace);

		if (ddmFormPagesTemplateContext == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(
			httpServletResponse,
			jsonSerializer.serializeDeep(ddmFormPagesTemplateContext));
	}

	private List<Object> _createDDMFormPagesTemplateContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String portletNamespace) {

		try {
			Locale locale = LocaleUtil.fromLanguageId(
				_language.getLanguageId(httpServletRequest));

			DDMFormRenderingContext ddmFormRenderingContext =
				_createDDMFormRenderingContext(
					httpServletRequest, httpServletResponse, locale,
					portletNamespace);

			DDMFormTemplateContextProcessor ddmFormTemplateContextProcessor =
				_createDDMFormTemplateContextProcessor(httpServletRequest);

			ddmFormRenderingContext.setDDMFormInstanceId(
				ddmFormTemplateContextProcessor.getDDMFormInstanceId());
			ddmFormRenderingContext.setDDMFormValues(
				ddmFormTemplateContextProcessor.getDDMFormValues());
			ddmFormRenderingContext.setGroupId(
				ddmFormTemplateContextProcessor.getGroupId());

			_prepareThreadLocal(locale);

			DDMFormPagesTemplateContextFactory
				ddmFormPagesTemplateContextFactory =
					new DDMFormPagesTemplateContextFactory(
						ddmFormTemplateContextProcessor.getDDMForm(),
						ddmFormTemplateContextProcessor.getDDMFormLayout(),
						ddmFormRenderingContext,
						_ddmStructureLayoutLocalService,
						_ddmStructureLocalService, _groupLocalService,
						_htmlParser, _jsonFactory);

			ddmFormPagesTemplateContextFactory.setDDMFormEvaluator(
				_ddmFormEvaluator);
			ddmFormPagesTemplateContextFactory.
				setDDMFormFieldTypeServicesRegistry(
					_ddmFormFieldTypeServicesRegistry);

			return ddmFormPagesTemplateContextFactory.create();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private DDMFormRenderingContext _createDDMFormRenderingContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Locale locale,
		String portletNamespace) {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setHttpServletRequest(httpServletRequest);
		ddmFormRenderingContext.setHttpServletResponse(httpServletResponse);
		ddmFormRenderingContext.setLocale(locale);
		ddmFormRenderingContext.setPortletNamespace(portletNamespace);
		ddmFormRenderingContext.setReturnFullContext(
			ParamUtil.getBoolean(httpServletRequest, "returnFullContext"));

		return ddmFormRenderingContext;
	}

	private DDMFormTemplateContextProcessor
			_createDDMFormTemplateContextProcessor(
				HttpServletRequest httpServletRequest)
		throws Exception {

		String serializedFormContext = ParamUtil.getString(
			httpServletRequest, "serializedFormContext");

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			serializedFormContext);

		return new DDMFormTemplateContextProcessor(
			jsonObject, ParamUtil.getString(httpServletRequest, "languageId"));
	}

	private void _prepareThreadLocal(Locale locale)
		throws Exception, PortalException {

		LocaleThreadLocal.setThemeDisplayLocale(locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormContextProviderServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private DDMFormEvaluator _ddmFormEvaluator;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}