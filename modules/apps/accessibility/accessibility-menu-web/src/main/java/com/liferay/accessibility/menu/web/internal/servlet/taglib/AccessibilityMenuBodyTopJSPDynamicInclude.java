/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.accessibility.menu.web.internal.servlet.taglib;

import com.liferay.accessibility.menu.web.internal.constants.AccessibilityMenuPortletKeys;
import com.liferay.accessibility.menu.web.internal.util.AccessibilitySettingsUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.taglib.portletext.RuntimeTag;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Evan Thibodeau
 */
@Component(service = DynamicInclude.class)
public class AccessibilityMenuBodyTopJSPDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!AccessibilitySettingsUtil.isAccessibilityMenuEnabled(
				httpServletRequest, _configurationProvider)) {

			return;
		}

		PageContext pageContext = PageContextFactoryUtil.create(
			httpServletRequest, httpServletResponse);

		try {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(
				"<div class=\"accessibility-menu\" style=\"display: none;\">");

			RuntimeTag runtimeTag = new RuntimeTag();

			runtimeTag.setPortletName(
				AccessibilityMenuPortletKeys.ACCESSIBILITY_MENU);

			runtimeTag.doTag(pageContext);

			jspWriter.write("</div>");
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/body_top.jsp#post");
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}