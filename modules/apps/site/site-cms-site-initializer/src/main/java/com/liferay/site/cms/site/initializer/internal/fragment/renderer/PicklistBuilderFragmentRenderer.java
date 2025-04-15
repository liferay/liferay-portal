/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.site.cms.site.initializer.internal.display.context.PicklistBuilderDisplayContext;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Verónica Gonzaléz
 */
@Component(service = FragmentRenderer.class)
public class PicklistBuilderFragmentRenderer
	extends BaseSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "picklist-builder";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "picklist-builder");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<div><span aria-hidden=\"true\" class=\"");
			printWriter.write("loading-animation\"></span>");

			ComponentTag componentTag = new ComponentTag();

			componentTag.setModule(
				"{PicklistBuilder} from site-cms-site-initializer");
			componentTag.setPageContext(
				PageContextFactoryUtil.create(
					httpServletRequest, httpServletResponse));

			PicklistBuilderDisplayContext picklistBuilderDisplayContext =
				new PicklistBuilderDisplayContext(
					httpServletRequest, _jsonFactory,
					_listTypeDefinitionResourceFactory);

			componentTag.setProps(picklistBuilderDisplayContext.getProps());

			componentTag.setServletContext(_servletContext);

			componentTag.doStartTag();

			componentTag.doEndTag();

			printWriter.write("</div>");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PicklistBuilderFragmentRenderer.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.cms.site.initializer)"
	)
	private ServletContext _servletContext;

}