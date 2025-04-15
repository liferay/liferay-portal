/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.configuration.icon;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Correa
 */
@Component(
	property = "jakarta.portlet.name=" + ExportImportPortletKeys.COMPANY_EXPORT,
	service = PortletConfigurationIcon.class
)
public class CompanyExportTemplatesPortletConfigurationIcon
	extends BaseTemplatesPortletConfigurationIcon {
}