/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderer;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRendererRegistry;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
		"mvc.command.name=/dynamic_data_mapping/render_structure_field"
	},
	service = MVCResourceCommand.class
)
public class RenderStructureFieldMVCResourceCommand
	extends BaseMVCResourceCommand {

	protected DDMFormFieldRenderingContext createDDMFormFieldRenderingContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		String portletId = ParamUtil.getString(httpServletRequest, "portletId");

		if (Validator.isNotNull(portletId)) {
			httpServletRequest.setAttribute(WebKeys.PORTLET_ID, portletId);
		}

		String portletNamespace = HtmlUtil.escapeAttribute(
			ParamUtil.getString(httpServletRequest, "portletNamespace"));

		httpServletRequest.setAttribute(
			"aui:form:portletNamespace", portletNamespace);

		ddmFormFieldRenderingContext.setHttpServletRequest(
			_portal.getOriginalServletRequest(httpServletRequest));
		ddmFormFieldRenderingContext.setHttpServletResponse(
			httpServletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ddmFormFieldRenderingContext.setLocale(themeDisplay.getLocale());

		ddmFormFieldRenderingContext.setMode(
			ParamUtil.getString(httpServletRequest, "mode"));
		ddmFormFieldRenderingContext.setNamespace(
			HtmlUtil.escapeAttribute(
				ParamUtil.getString(httpServletRequest, "namespace")));
		ddmFormFieldRenderingContext.setPortletNamespace(portletNamespace);
		ddmFormFieldRenderingContext.setReadOnly(
			ParamUtil.getBoolean(httpServletRequest, "readOnly"));

		return ddmFormFieldRenderingContext;
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(resourceResponse);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		DDMFormField ddmFormField = getDDMFormField(httpServletRequest);

		DDMFormFieldRenderer ddmFormFieldRenderer =
			_ddmFormFieldRendererRegistry.getDDMFormFieldRenderer(
				ddmFormField.getType());

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			createDDMFormFieldRenderingContext(
				httpServletRequest, httpServletResponse);

		String ddmFormFieldHTML = HtmlUtil.escapeAttribute(
			ddmFormFieldRenderer.render(
				ddmFormField, ddmFormFieldRenderingContext));

		httpServletResponse.setContentType(ContentTypes.TEXT_HTML);

		ServletResponseUtil.write(httpServletResponse, ddmFormFieldHTML);
	}

	protected DDMFormField getDDMFormField(
		HttpServletRequest httpServletRequest) {

		String definition = ParamUtil.getString(
			httpServletRequest, "definition");
		String fieldName = ParamUtil.getString(httpServletRequest, "fieldName");

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				definition);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		DDMForm ddmForm = ddmFormDeserializerDeserializeResponse.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		return ddmFormFieldsMap.get(fieldName);
	}

	@Reference
	private DDMFormFieldRendererRegistry _ddmFormFieldRendererRegistry;

	@Reference(target = "(ddm.form.deserializer.type=json)")
	private DDMFormDeserializer _jsonDDMFormDeserializer;

	@Reference
	private Portal _portal;

}