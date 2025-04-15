/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateRegister;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"name=CommonPortletDisplayTemplateImportCapability",
		"type=" + PortletDisplayTemplateConstants.DISPLAY_TEMPLATE_IMPORT
	},
	service = PortletDisplayTemplateRegister.class
)
public class CommonPortletDisplayTemplateImportCapability
	implements PortletDisplayTemplateRegister {

	@Override
	public String getDisplayStyle(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return ExportImportPortletPreferencesProcessorUtil.getDisplayStyle(
			_portletLocalService.getPortletById(
				portletDataContext.getCompanyId(), portletId),
			portletPreferences);
	}

	@Override
	public long getDisplayStyleGroupId(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return ExportImportPortletPreferencesProcessorUtil.
			getDisplayStyleGroupId(
				portletDataContext.getCompanyId(),
				_portletLocalService.getPortletById(
					portletDataContext.getCompanyId(), portletId),
				portletPreferences);
	}

	@Reference(unbind = "-")
	private PortletLocalService _portletLocalService;

}