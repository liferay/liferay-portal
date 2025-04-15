/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.configuration;

import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Akos Thurzo
 */
public class ExportImportConfigurationParameterMapFactoryUtil {

	public static Map<String, String[]> buildFullPublishParameterMap() {
		ExportImportConfigurationParameterMapFactory
			exportImportConfigurationParameterMapFactory =
				_exportImportConfigurationParameterMapFactorySnapshot.get();

		return exportImportConfigurationParameterMapFactory.
			buildFullPublishParameterMap();
	}

	public static Map<String, String[]> buildParameterMap() {
		ExportImportConfigurationParameterMapFactory
			exportImportConfigurationParameterMapFactory =
				_exportImportConfigurationParameterMapFactorySnapshot.get();

		return exportImportConfigurationParameterMapFactory.buildParameterMap();
	}

	public static Map<String, String[]> buildParameterMap(
		PortletRequest portletRequest) {

		ExportImportConfigurationParameterMapFactory
			exportImportConfigurationParameterMapFactory =
				_exportImportConfigurationParameterMapFactorySnapshot.get();

		return exportImportConfigurationParameterMapFactory.buildParameterMap(
			portletRequest);
	}

	public static Map<String, String[]> buildParameterMap(
		String dataStrategy, Boolean deleteMissingLayouts,
		Boolean deletePortletData, Boolean deletions,
		Boolean ignoreLastPublishDate, Boolean layoutSetPrototypeLinkEnabled,
		Boolean layoutSetSettings, Boolean logo, Boolean permissions,
		Boolean portletConfiguration, Boolean portletConfigurationAll,
		List<String> portletConfigurationPortletIds, Boolean portletData,
		Boolean portletDataAll, List<String> portletDataPortletIds,
		Boolean portletSetupAll, List<String> portletSetupPortletIds,
		String range, Boolean themeReference, Boolean updateLastPublishDate,
		String userIdStrategy) {

		ExportImportConfigurationParameterMapFactory
			exportImportConfigurationParameterMapFactory =
				_exportImportConfigurationParameterMapFactorySnapshot.get();

		return exportImportConfigurationParameterMapFactory.buildParameterMap(
			dataStrategy, deleteMissingLayouts, deletePortletData, deletions,
			ignoreLastPublishDate, layoutSetPrototypeLinkEnabled,
			layoutSetSettings, logo, permissions, portletConfiguration,
			portletConfigurationAll, portletConfigurationPortletIds,
			portletData, portletDataAll, portletDataPortletIds, portletSetupAll,
			portletSetupPortletIds, range, themeReference,
			updateLastPublishDate, userIdStrategy);
	}

	private static final Snapshot<ExportImportConfigurationParameterMapFactory>
		_exportImportConfigurationParameterMapFactorySnapshot = new Snapshot<>(
			ExportImportConfigurationParameterMapFactoryUtil.class,
			ExportImportConfigurationParameterMapFactory.class);

}