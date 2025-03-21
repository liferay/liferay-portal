/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.taglib.internal.servlet.ServletContextUtil;
import com.liferay.dynamic.data.mapping.taglib.servlet.taglib.base.BaseTemplateSelectorTag;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.util.PortletDisplayTemplateUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Juan Fernández
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.template.taglib.servlet.taglib.TemplateSelectorTag}
 */
@Deprecated
public class TemplateSelectorTag extends BaseTemplateSelectorTag {

	@Override
	public String getDisplayStyle() {
		DDMTemplate portletDisplayDDMTemplate = getPortletDisplayDDMTemplate();

		if (portletDisplayDDMTemplate != null) {
			return PortletDisplayTemplateUtil.getDisplayStyle(
				portletDisplayDDMTemplate.getTemplateKey());
		}

		if (Validator.isNull(super.getDisplayStyle())) {
			return super.getDefaultDisplayStyle();
		}

		return super.getDisplayStyle();
	}

	@Override
	public long getDisplayStyleGroupId() {
		long displayStyleGroupId = super.getDisplayStyleGroupId();

		if (displayStyleGroupId > 0) {
			return displayStyleGroupId;
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getScopeGroupId();
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	protected DDMTemplate getPortletDisplayDDMTemplate() {
		String displayStyle = super.getDisplayStyle();

		if (Validator.isNull(displayStyle)) {
			displayStyle = super.getDefaultDisplayStyle();
		}

		return PortletDisplayTemplateUtil.getPortletDisplayTemplateDDMTemplate(
			getDisplayStyleGroupId(), PortalUtil.getClassNameId(getClassName()),
			displayStyle, true);
	}

	protected ResourceBundle getResourceBundle() {
		Locale locale = PortalUtil.getLocale(getRequest());

		Class<?> clazz = getClass();

		return ResourceBundleUtil.getBundle(
			"content.Language", locale, clazz.getClassLoader());
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		setNamespacedAttribute(
			httpServletRequest, "classNameId",
			String.valueOf(PortalUtil.getClassNameId(getClassName())));
		setNamespacedAttribute(
			httpServletRequest, "portletDisplayDDMTemplate",
			getPortletDisplayDDMTemplate());
		setNamespacedAttribute(
			httpServletRequest, "resourceBundle", getResourceBundle());
	}

}