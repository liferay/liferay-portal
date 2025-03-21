/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.configuration;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Levente Hudák
 */
public class ExportImportConfigurationFactory {

	public static ExportImportConfiguration
			buildDefaultLocalPublishingExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long sourceGroupId = ParamUtil.getLong(portletRequest, "sourceGroupId");
		long targetGroupId = ParamUtil.getLong(portletRequest, "targetGroupId");
		boolean privateLayout = ParamUtil.getBoolean(
			portletRequest, "privateLayout");

		return buildDefaultLocalPublishingExportImportConfiguration(
			themeDisplay.getUser(), sourceGroupId, targetGroupId, privateLayout,
			_getParameterMap(portletRequest));
	}

	public static ExportImportConfiguration
			buildDefaultLocalPublishingExportImportConfiguration(
				User user, long sourceGroupId, long targetGroupId,
				boolean privateLayout)
		throws PortalException {

		return buildDefaultLocalPublishingExportImportConfiguration(
			user, sourceGroupId, targetGroupId, privateLayout,
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap());
	}

	public static ExportImportConfiguration
			buildDefaultLocalPublishingExportImportConfiguration(
				User user, long sourceGroupId, long targetGroupId,
				boolean privateLayout, Map<String, String[]> parameterMap)
		throws PortalException {

		Map<String, Serializable> publishLayoutLocalSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutLocalSettingsMap(
					user, sourceGroupId, targetGroupId, privateLayout,
					ExportImportHelperUtil.getAllLayoutIds(
						sourceGroupId, privateLayout),
					parameterMap);

		return ExportImportConfigurationLocalServiceUtil.
			addDraftExportImportConfiguration(
				user.getUserId(),
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL,
				publishLayoutLocalSettingsMap);
	}

	public static ExportImportConfiguration
			buildDefaultRemotePublishingExportImportConfiguration(
				PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long sourceGroupId = ParamUtil.getLong(portletRequest, "sourceGroupId");
		boolean privateLayout = ParamUtil.getBoolean(
			portletRequest, "privateLayout");

		Group group = GroupLocalServiceUtil.getGroup(sourceGroupId);

		String remoteAddress = group.getTypeSettingsProperty("remoteAddress");
		int remotePort = GetterUtil.getInteger(
			group.getTypeSettingsProperty("remotePort"));

		String remotePathContext = ParamUtil.getString(
			portletRequest, "remotePathContext");
		boolean secureConnection = ParamUtil.getBoolean(
			portletRequest, "secureConnection");
		long remoteGroupId = ParamUtil.getLong(portletRequest, "remoteGroupId");

		return buildDefaultRemotePublishingExportImportConfiguration(
			themeDisplay.getUser(), sourceGroupId, privateLayout, remoteAddress,
			remotePort, remotePathContext, secureConnection, remoteGroupId,
			_getParameterMap(portletRequest));
	}

	public static ExportImportConfiguration
			buildDefaultRemotePublishingExportImportConfiguration(
				User user, long sourceGroupId, boolean privateLayout,
				String remoteAddress, int remotePort, String remotePathContext,
				boolean secureConnection, long remoteGroupId)
		throws PortalException {

		return buildDefaultRemotePublishingExportImportConfiguration(
			user, sourceGroupId, privateLayout, remoteAddress, remotePort,
			remotePathContext, secureConnection, remoteGroupId,
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap());
	}

	public static ExportImportConfiguration cloneExportImportConfiguration(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		if (exportImportConfiguration == null) {
			return null;
		}

		return ExportImportConfigurationLocalServiceUtil.
			addExportImportConfiguration(
				exportImportConfiguration.getUserId(),
				exportImportConfiguration.getGroupId(),
				exportImportConfiguration.getName(),
				exportImportConfiguration.getDescription(),
				exportImportConfiguration.getType(),
				exportImportConfiguration.getSettingsMap(),
				exportImportConfiguration.getStatus(), new ServiceContext());
	}

	protected static ExportImportConfiguration
			buildDefaultRemotePublishingExportImportConfiguration(
				User user, long sourceGroupId, boolean privateLayout,
				String remoteAddress, int remotePort, String remotePathContext,
				boolean secureConnection, long remoteGroupId,
				Map<String, String[]> parameterMap)
		throws PortalException {

		Map<String, Serializable> publishLayoutRemoteSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutRemoteSettingsMap(
					user.getUserId(), sourceGroupId, privateLayout,
					ExportImportHelperUtil.getAllLayoutIdsMap(
						sourceGroupId, privateLayout),
					parameterMap, remoteAddress, remotePort, remotePathContext,
					secureConnection, remoteGroupId, privateLayout,
					user.getLocale(), user.getTimeZone());

		return ExportImportConfigurationLocalServiceUtil.
			addDraftExportImportConfiguration(
				user.getUserId(),
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_REMOTE,
				publishLayoutRemoteSettingsMap);
	}

	private static Map<String, String[]> _getParameterMap(
		PortletRequest portletRequest) {

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		Map<String, String[]> requestParameterMap = new LinkedHashMap<>(
			portletRequest.getParameterMap());

		requestParameterMap.forEach(parameterMap::putIfAbsent);

		return parameterMap;
	}

}