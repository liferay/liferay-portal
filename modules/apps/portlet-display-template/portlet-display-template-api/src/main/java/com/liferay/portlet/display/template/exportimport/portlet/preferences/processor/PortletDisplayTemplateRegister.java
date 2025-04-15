/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;

import jakarta.portlet.PortletPreferences;

/**
 * @author Lance Ji
 */
public interface PortletDisplayTemplateRegister {

	public String getDisplayStyle(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences);

	public long getDisplayStyleGroupId(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences);

}