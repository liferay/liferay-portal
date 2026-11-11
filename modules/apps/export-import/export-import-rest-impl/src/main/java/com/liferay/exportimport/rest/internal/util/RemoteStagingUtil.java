/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.rest.dto.v1_0.RemoteConnection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Raposo
 */
public class RemoteStagingUtil {

	public static Map<String, Serializable> buildPublishLayoutRemoteSettingsMap(
			Map<String, String[]> parameterMap,
			RemoteConnection remoteConnection, Group stagingGroup, User user)
		throws PortalException {

		if (remoteConnection == null) {
			remoteConnection = new RemoteConnection();
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			stagingGroup.getTypeSettingsProperties();

		String address = StagingUtil.stripProtocolFromRemoteAddress(
			GetterUtil.getString(
				remoteConnection.getAddress(),
				typeSettingsUnicodeProperties.getProperty("remoteAddress")));
		String pathContext = GetterUtil.getString(
			remoteConnection.getPathContext(),
			typeSettingsUnicodeProperties.getProperty("remotePathContext"));
		int port = GetterUtil.getInteger(
			remoteConnection.getPort(),
			GetterUtil.getInteger(
				typeSettingsUnicodeProperties.getProperty("remotePort")));
		boolean secureConnection = GetterUtil.getBoolean(
			remoteConnection.getSecureConnection(),
			GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.getProperty("secureConnection")));
		long siteId = GetterUtil.getLong(
			remoteConnection.getSiteId(),
			GetterUtil.getLong(
				typeSettingsUnicodeProperties.getProperty("remoteGroupId")));

		GroupLocalServiceUtil.validateRemote(
			stagingGroup.getGroupId(), address, port, pathContext,
			secureConnection, siteId);

		boolean privateLayout = LayoutUtil.isPrivateLayout(parameterMap);

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			stagingGroup.getGroupId(), privateLayout);

		Set<Long> layoutIds = SetUtil.fromArray(
			LayoutUtil.getLayoutIds(layouts, parameterMap));

		Map<Long, Boolean> layoutIdMap = new LinkedHashMap<>();

		for (Layout layout : layouts) {
			if (!layoutIds.contains(layout.getLayoutId())) {
				continue;
			}

			layoutIdMap.put(layout.getPlid(), false);
		}

		return ExportImportConfigurationSettingsMapFactoryUtil.
			buildPublishLayoutRemoteSettingsMap(
				user, stagingGroup.getGroupId(), privateLayout, layoutIdMap,
				parameterMap, address, port, pathContext, secureConnection,
				siteId, privateLayout);
	}

}