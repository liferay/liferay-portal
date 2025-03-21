/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateRegister;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

/**
 * Provides the implementation of the import capability for the Media Gallery
 * portlet. This allows the display style and display style group ID to be
 * provided based on the existence of the template handler.
 *
 * @author Laszlo Hudak
 */
@Component(
	property = {
		"name=IGDisplayImportCapability",
		"type=" + PortletDisplayTemplateConstants.DISPLAY_TEMPLATE_IMPORT
	},
	service = PortletDisplayTemplateRegister.class
)
public class IGDisplayPortletDisplayTemplateImportCapability
	implements PortletDisplayTemplateRegister {

	@Override
	public String getDisplayStyle(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return IGDisplayExportImportPortletPreferencesProcessorUtil.
			getDisplayStyle(portletPreferences);
	}

	@Override
	public long getDisplayStyleGroupId(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return IGDisplayExportImportPortletPreferencesProcessorUtil.
			getDisplayStyleGroupId(portletPreferences);
	}

}