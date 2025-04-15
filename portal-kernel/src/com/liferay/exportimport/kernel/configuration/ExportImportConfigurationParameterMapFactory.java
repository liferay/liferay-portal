/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.configuration;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Akos Thurzo
 */
@ProviderType
public interface ExportImportConfigurationParameterMapFactory {

	public Map<String, String[]> buildFullPublishParameterMap();

	public Map<String, String[]> buildParameterMap();

	public Map<String, String[]> buildParameterMap(
		PortletRequest portletRequest);

	public Map<String, String[]> buildParameterMap(
		String dataStrategy, Boolean deleteLayouts,
		Boolean deleteMissingLayouts, Boolean deletePortletData,
		Boolean deletions, Boolean ignoreLastPublishDate,
		Boolean layoutSetPrototypeLinkEnabled, Boolean layoutSetSettings,
		Boolean logo, Boolean permissions, Boolean portletConfiguration,
		Boolean portletConfigurationAll,
		List<String> portletConfigurationPortletIds, Boolean portletData,
		Boolean portletDataAll, List<String> portletDataPortletIds,
		Boolean portletSetupAll, List<String> portletSetupPortletIds,
		String range, Boolean themeReference, Boolean updateLastPublishDate,
		String userIdStrategy);

	public Map<String, String[]> buildParameterMap(
		String dataStrategy, Boolean deleteMissingLayouts,
		Boolean deletePortletData, Boolean deletions,
		Boolean ignoreLastPublishDate, Boolean layoutSetPrototypeLinkEnabled,
		Boolean layoutSetSettings, Boolean logo, Boolean permissions,
		Boolean portletConfiguration, Boolean portletConfigurationAll,
		List<String> portletConfigurationPortletIds, Boolean portletData,
		Boolean portletDataAll, List<String> portletDataPortletIds,
		Boolean portletSetupAll, List<String> portletSetupPortletIds,
		String range, Boolean themeReference, Boolean updateLastPublishDate,
		String userIdStrategy);

}