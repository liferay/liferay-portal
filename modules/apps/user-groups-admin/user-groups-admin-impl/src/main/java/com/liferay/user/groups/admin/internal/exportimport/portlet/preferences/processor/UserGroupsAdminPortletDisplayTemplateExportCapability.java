/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateRegister;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"name=UserGroupsAdminExportCapability",
		"type=" + PortletDisplayTemplateConstants.DISPLAY_TEMPLATE_EXPORT
	},
	service = PortletDisplayTemplateRegister.class
)
public class UserGroupsAdminPortletDisplayTemplateExportCapability
	implements PortletDisplayTemplateRegister {

	@Override
	public String getDisplayStyle(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return UserGroupsAdminExportImportPortletPreferencesProcessorUtil.
			getDisplayStyle(portletPreferences);
	}

	@Override
	public long getDisplayStyleGroupId(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return UserGroupsAdminExportImportPortletPreferencesProcessorUtil.
			getDisplayStyleGroupId(portletPreferences);
	}

}