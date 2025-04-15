/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.configuration;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Levente Hudák
 */
public class ExportImportConfigurationUtil {

	public static ExportImportConfiguration
			addExportLayoutExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		return addExportImportConfiguration(
			portletRequest,
			ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT);
	}

	public static ExportImportConfiguration
			addPublishLayoutLocalExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		return addExportImportConfiguration(
			portletRequest,
			ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL);
	}

	public static ExportImportConfiguration
			addPublishLayoutRemoteExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		return addExportImportConfiguration(
			portletRequest,
			ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_REMOTE);
	}

	public static String[] getExportImportConfigurationParameter(
			BackgroundTask backgroundTask, String parameterName)
		throws PortalException {

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		return getExportImportConfigurationParameter(
			ExportImportConfigurationLocalServiceUtil.
				getExportImportConfiguration(
					GetterUtil.getLong(
						taskContextMap.get("exportImportConfigurationId"))),
			parameterName);
	}

	public static String[] getExportImportConfigurationParameter(
			ExportImportConfiguration exportImportConfiguration,
			String parameterName)
		throws PortalException {

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(HashMap<String, String[]>)settingsMap.get("parameterMap");

		return parameterMap.get(parameterName);
	}

	public static ExportImportConfiguration
			updateExportLayoutExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		return updateExportImportConfiguration(
			portletRequest,
			ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT);
	}

	public static ExportImportConfiguration
			updatePublishLayoutLocalExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		return updateExportImportConfiguration(
			portletRequest,
			ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL);
	}

	public static ExportImportConfiguration
			updatePublishLayoutRemoteExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		return updateExportImportConfiguration(
			portletRequest,
			ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_REMOTE);
	}

	protected static ExportImportConfiguration addExportImportConfiguration(
			PortletRequest portletRequest, int type)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(portletRequest, "groupId");

		if (type == ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT) {
			groupId = ParamUtil.getLong(portletRequest, "liveGroupId");
		}

		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");

		Map<String, Serializable> settingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.buildSettingsMap(
				portletRequest, groupId, type);

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		if ((parameterMap != null) &&
			(type ==
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL)) {

			parameterMap.put(
				PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT,
				new String[] {Boolean.TRUE.toString()});
		}

		return ExportImportConfigurationLocalServiceUtil.
			addExportImportConfiguration(
				themeDisplay.getUserId(), groupId, name, description, type,
				settingsMap, new ServiceContext());
	}

	protected static ExportImportConfiguration updateExportImportConfiguration(
			PortletRequest portletRequest, int type)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long exportImportConfigurationId = ParamUtil.getLong(
			portletRequest, "exportImportConfigurationId");

		long groupId = ParamUtil.getLong(portletRequest, "groupId");
		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");

		Map<String, Serializable> settingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.buildSettingsMap(
				portletRequest, groupId, type);

		return ExportImportConfigurationLocalServiceUtil.
			updateExportImportConfiguration(
				themeDisplay.getUserId(), exportImportConfigurationId, name,
				description, settingsMap, new ServiceContext());
	}

}