/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal;

import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingException;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Writer;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = DDMFormRenderer.class)
public class DDMFormRendererImpl implements DDMFormRenderer {

	@Override
	public Map<String, Object> getDDMFormTemplateContext(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			DDMFormRenderingContext ddmFormRenderingContext)
		throws Exception {

		Map<String, Object> ddmFormTemplateContext =
			_ddmFormTemplateContextFactory.create(
				ddmForm, ddmFormLayout, ddmFormRenderingContext);

		ddmFormTemplateContext.put("editable", false);

		ddmFormTemplateContext.remove("fieldTypes");

		HttpServletRequest httpServletRequest =
			ddmFormRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ddmFormTemplateContext.put(
			"spritemap", themeDisplay.getPathThemeSpritemap());

		return ddmFormTemplateContext;
	}

	@Override
	public String render(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			DDMFormRenderingContext ddmFormRenderingContext)
		throws DDMFormRenderingException {

		try {
			return _render(ddmForm, ddmFormLayout, ddmFormRenderingContext);
		}
		catch (DDMFormRenderingException ddmFormRenderingException) {
			throw ddmFormRenderingException;
		}
		catch (Exception exception) {
			throw new DDMFormRenderingException(exception);
		}
	}

	@Override
	public String render(
			DDMForm ddmForm, DDMFormRenderingContext ddmFormRenderingContext)
		throws DDMFormRenderingException {

		try {
			return _render(
				ddmForm, _ddm.getDefaultDDMFormLayout(ddmForm),
				ddmFormRenderingContext);
		}
		catch (DDMFormRenderingException ddmFormRenderingException) {
			throw ddmFormRenderingException;
		}
		catch (Exception exception) {
			throw new DDMFormRenderingException(exception);
		}
	}

	private String _render(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			DDMFormRenderingContext ddmFormRenderingContext)
		throws Exception {

		Writer writer = new UnsyncStringWriter();

		writer.append("<div id=\"");
		writer.append(ddmFormRenderingContext.getContainerId());
		writer.append("\">");

		_reactRenderer.renderReact(
			new ComponentDescriptor(
				"{FormView} from data-engine-js-components-web"),
			getDDMFormTemplateContext(
				ddmForm, ddmFormLayout, ddmFormRenderingContext),
			ddmFormRenderingContext.getHttpServletRequest(), writer);

		writer.append("</div>");

		return writer.toString();
	}

	@Reference
	private DDM _ddm;

	@Reference
	private DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

	@Reference
	private ReactRenderer _reactRenderer;

}