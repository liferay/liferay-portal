/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;

import jakarta.portlet.PortletPreferences;

import java.util.List;

/**
 * @author Máté Thurzó
 */
public interface ExportImportPortletPreferencesProcessor {

	public List<Capability> getExportCapabilities();

	public List<Capability> getImportCapabilities();

	public default boolean isPublishDisplayedContent() {
		return true;
	}

	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException;

	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException;

}