/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CustomizedPages;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.servlet.JSPSupportServlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.layoutconfiguration.util.velocity.ColumnProcessor;
import com.liferay.taglib.aui.InputTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.Tag;

import java.io.Writer;

import java.util.Map;

/**
 * @author Raymond Augé
 * @author Oliver Teichmann
 */
public class CustomizationSettingsProcessor implements ColumnProcessor {

	public CustomizationSettingsProcessor(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		JspFactory jspFactory = JspFactory.getDefaultFactory();

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			_pageContext = jspFactory.getPageContext(
				new JSPSupportServlet(httpServletRequest.getServletContext()),
				httpServletRequest, httpServletResponse, null, false, 0, false);
		}

		_writer = _pageContext.getOut();

		Layout selLayout = null;

		long selPlid = ParamUtil.getLong(
			httpServletRequest, "selPlid", LayoutConstants.DEFAULT_PLID);

		if (selPlid != LayoutConstants.DEFAULT_PLID) {
			selLayout = LayoutLocalServiceUtil.fetchLayout(selPlid);
		}

		_layoutTypeSettingsUnicodeProperties =
			selLayout.getTypeSettingsProperties();

		if ((selLayout instanceof VirtualLayout) ||
			!selLayout.isLayoutUpdateable() ||
			selLayout.isLayoutPrototypeLinkActive()) {

			_customizationEnabled = false;
		}
		else {
			_customizationEnabled = true;
		}
	}

	@Override
	public String processColumn(String columnId) throws Exception {
		return processColumn(columnId, StringPool.BLANK);
	}

	@Override
	public String processColumn(String columnId, String classNames)
		throws Exception {

		String customizableKey = CustomizedPages.namespaceColumnId(columnId);

		boolean customizable = false;

		if (_customizationEnabled) {
			customizable = GetterUtil.getBoolean(
				_layoutTypeSettingsUnicodeProperties.getProperty(
					customizableKey, Boolean.FALSE.toString()));
		}

		_writer.append("<div class=\"");
		_writer.append(classNames);
		_writer.append("\">");

		_writer.append("<h1>");
		_writer.append(columnId);
		_writer.append("</h1>");

		InputTag inputTag = new InputTag();

		inputTag.setDisabled(!_customizationEnabled);
		inputTag.setDynamicAttribute(
			StringPool.BLANK, "labelOff", "not-customizable");
		inputTag.setDynamicAttribute(
			StringPool.BLANK, "labelOn", "customizable");
		inputTag.setLabel(StringPool.BLANK);
		inputTag.setName(
			StringBundler.concat(
				"TypeSettingsProperties--", customizableKey, "--"));
		inputTag.setPageContext(_pageContext);
		inputTag.setType("toggle-switch");
		inputTag.setValue(customizable);

		int result = inputTag.doStartTag();

		if (result == Tag.EVAL_BODY_INCLUDE) {
			inputTag.doEndTag();
		}

		_writer.append("</div>");

		return StringPool.BLANK;
	}

	@Override
	public String processDynamicColumn(String columnId, String classNames)
		throws Exception {

		return StringPool.BLANK;
	}

	@Override
	public String processMax() throws Exception {
		return StringPool.BLANK;
	}

	@Override
	public String processPortlet(String portletId) throws Exception {
		_writer.append("<div class=\"portlet\">");
		_writer.append(portletId);
		_writer.append("</div>");

		return StringPool.BLANK;
	}

	@Override
	public String processPortlet(
			String portletId, Map<String, ?> defaultSettingsMap)
		throws Exception {

		return processPortlet(portletId);
	}

	@Override
	public String processPortlet(
			String portletProviderClassName,
			PortletProvider.Action portletProviderAction)
		throws Exception {

		return processPortlet(
			PortletProviderUtil.getPortletId(
				portletProviderClassName, portletProviderAction));
	}

	private final boolean _customizationEnabled;
	private final UnicodeProperties _layoutTypeSettingsUnicodeProperties;
	private final PageContext _pageContext;
	private final Writer _writer;

}