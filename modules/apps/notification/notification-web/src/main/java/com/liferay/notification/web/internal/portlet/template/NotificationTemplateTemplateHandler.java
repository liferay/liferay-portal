/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.template;

import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.BaseTemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Victor Kammerer
 */
@Component(
	property = "jakarta.portlet.name=" + NotificationPortletKeys.NOTIFICATION_TEMPLATES,
	service = TemplateHandler.class
)
public class NotificationTemplateTemplateHandler extends BaseTemplateHandler {

	@Override
	public String getClassName() {
		return NotificationTemplate.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			NotificationPortletKeys.NOTIFICATION_TEMPLATES, locale);

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return NotificationConstants.RESOURCE_NAME_NOTIFICATION_TEMPLATE;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		return LinkedHashMapBuilder.<String, TemplateVariableGroup>put(
			"general-variables",
			() -> {
				TemplateVariableGroup templateVariableGroup =
					new TemplateVariableGroup("general-variables");

				templateVariableGroup.addVariable(
					"locale", Locale.class, "locale");
				templateVariableGroup.addVariable(
					"portal-url", TemplateNode.class, "portalURL");

				return templateVariableGroup;
			}
		).build();
	}

	@Override
	public boolean isDisplayTemplateHandler() {
		return false;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}