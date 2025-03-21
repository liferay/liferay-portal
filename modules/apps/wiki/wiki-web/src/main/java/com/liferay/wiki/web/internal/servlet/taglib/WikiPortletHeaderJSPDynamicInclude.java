/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.model.WikiNode;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(service = DynamicInclude.class)
public class WikiPortletHeaderJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		String mvcRenderCommandName = ParamUtil.getString(
			portletRequest, "mvcRenderCommandName");

		if (Validator.isNotNull(mvcRenderCommandName) &&
			!mvcRenderCommandName.equals("/wiki/view") &&
			!mvcRenderCommandName.equals("/wiki/view_page") &&
			!mvcRenderCommandName.equals("/wiki/view_pages") &&
			!mvcRenderCommandName.equals("/wiki/view_categorized_pages") &&
			!mvcRenderCommandName.equals("/wiki/view_draft_pages") &&
			!mvcRenderCommandName.equals("/wiki/view_orphan_pages") &&
			!mvcRenderCommandName.equals("/wiki/view_page_attachments") &&
			!mvcRenderCommandName.equals("/wiki/view_page_details") &&
			!mvcRenderCommandName.equals("/wiki/view_page_history") &&
			!mvcRenderCommandName.equals("/wiki/view_page_incoming_links") &&
			!mvcRenderCommandName.equals("/wiki/view_page_outgoing_links") &&
			!mvcRenderCommandName.equals("/wiki/view_recent_changes") &&
			!mvcRenderCommandName.equals("/wiki/view_tagged_pages") &&
			!mvcRenderCommandName.equals("/wiki/view_page_activities")) {

			return;
		}

		WikiNode node = (WikiNode)httpServletRequest.getAttribute(
			WikiWebKeys.WIKI_NODE);

		if (node == null) {
			return;
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"portlet_header_" + WikiPortletKeys.WIKI);
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/portlet_header.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPortletHeaderJSPDynamicInclude.class);

	@Reference(target = "(osgi.web.symbolicname=com.liferay.wiki.web)")
	private ServletContext _servletContext;

}