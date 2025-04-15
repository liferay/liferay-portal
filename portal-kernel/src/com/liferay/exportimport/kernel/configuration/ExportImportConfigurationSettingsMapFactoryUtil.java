/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.configuration;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Provides a way to build a settings map for an {@link
 * com.liferay.exportimport.kernel.model.ExportImportConfiguration}, which can
 * be used to start and control an export, import, or staging process.
 *
 * @author Daniel Kocsis
 * @author Akos Thurzo
 * @since  7.0
 */
public class ExportImportConfigurationSettingsMapFactoryUtil {

	public static Map<String, Serializable> buildExportLayoutSettingsMap(
		long userId, long groupId, boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildExportLayoutSettingsMap(
				userId, groupId, privateLayout, layoutIds, parameterMap, locale,
				timeZone);
	}

	public static Map<String, Serializable> buildExportLayoutSettingsMap(
		User user, long groupId, boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildExportLayoutSettingsMap(
				user, groupId, privateLayout, layoutIds, parameterMap);
	}

	public static Map<String, Serializable> buildExportPortletSettingsMap(
		long userId, long sourcePlid, long sourceGroupId, String portletId,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone,
		String fileName) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildExportPortletSettingsMap(
				userId, sourcePlid, sourceGroupId, portletId, parameterMap,
				locale, timeZone, fileName);
	}

	public static Map<String, Serializable> buildExportPortletSettingsMap(
		User user, long sourcePlid, long sourceGroupId, String portletId,
		Map<String, String[]> parameterMap, String fileName) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildExportPortletSettingsMap(
				user, sourcePlid, sourceGroupId, portletId, parameterMap,
				fileName);
	}

	public static Map<String, Serializable> buildImportLayoutSettingsMap(
		long userId, long targetGroupId, boolean privateLayout,
		long[] layoutIds, Map<String, String[]> parameterMap, Locale locale,
		TimeZone timeZone) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildImportLayoutSettingsMap(
				userId, targetGroupId, privateLayout, layoutIds, parameterMap,
				locale, timeZone);
	}

	public static Map<String, Serializable> buildImportLayoutSettingsMap(
		User user, long targetGroupId, boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildImportLayoutSettingsMap(
				user, targetGroupId, privateLayout, layoutIds, parameterMap);
	}

	public static Map<String, Serializable> buildImportPortletSettingsMap(
		long userId, long targetPlid, long targetGroupId, String portletId,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildImportPortletSettingsMap(
				userId, targetPlid, targetGroupId, portletId, parameterMap,
				locale, timeZone);
	}

	public static Map<String, Serializable> buildImportPortletSettingsMap(
		User user, long targetPlid, long targetGroupId, String portletId,
		Map<String, String[]> parameterMap) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildImportPortletSettingsMap(
				user, targetPlid, targetGroupId, portletId, parameterMap);
	}

	public static Map<String, Serializable> buildPublishLayoutLocalSettingsMap(
		long userId, long sourceGroupId, long targetGroupId,
		boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildPublishLayoutLocalSettingsMap(
				userId, sourceGroupId, targetGroupId, privateLayout, layoutIds,
				parameterMap, locale, timeZone);
	}

	public static Map<String, Serializable> buildPublishLayoutLocalSettingsMap(
		User user, long sourceGroupId, long targetGroupId,
		boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildPublishLayoutLocalSettingsMap(
				user, sourceGroupId, targetGroupId, privateLayout, layoutIds,
				parameterMap);
	}

	public static Map<String, Serializable> buildPublishLayoutRemoteSettingsMap(
		long userId, long sourceGroupId, boolean privateLayout,
		Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
		String remoteAddress, int remotePort, String remotePathContext,
		boolean secureConnection, long remoteGroupId,
		boolean remotePrivateLayout, Locale locale, TimeZone timeZone) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildPublishLayoutRemoteSettingsMap(
				userId, sourceGroupId, privateLayout, layoutIdMap, parameterMap,
				remoteAddress, remotePort, remotePathContext, secureConnection,
				remoteGroupId, remotePrivateLayout, locale, timeZone);
	}

	public static Map<String, Serializable> buildPublishLayoutRemoteSettingsMap(
		User user, long sourceGroupId, boolean privateLayout,
		Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
		String remoteAddress, int remotePort, String remotePathContext,
		boolean secureConnection, long remoteGroupId,
		boolean remotePrivateLayout) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildPublishLayoutRemoteSettingsMap(
				user, sourceGroupId, privateLayout, layoutIdMap, parameterMap,
				remoteAddress, remotePort, remotePathContext, secureConnection,
				remoteGroupId, remotePrivateLayout);
	}

	public static Map<String, Serializable> buildPublishPortletSettingsMap(
		long userId, long sourceGroupId, long sourcePlid, long targetGroupId,
		long targetPlid, String portletId, Map<String, String[]> parameterMap,
		Locale locale, TimeZone timeZone) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildPublishPortletSettingsMap(
				userId, sourceGroupId, sourcePlid, targetGroupId, targetPlid,
				portletId, parameterMap, locale, timeZone);
	}

	public static Map<String, Serializable> buildPublishPortletSettingsMap(
		User user, long sourceGroupId, long sourcePlid, long targetGroupId,
		long targetPlid, String portletId, Map<String, String[]> parameterMap) {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.
			buildPublishPortletSettingsMap(
				user, sourceGroupId, sourcePlid, targetGroupId, targetPlid,
				portletId, parameterMap);
	}

	/**
	 * Returns an export layout settings map if the type is {@link
	 * com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants#TYPE_EXPORT_LAYOUT}; otherwise,
	 * returns either a local or remote publish layout settings map, depending
	 * on the staging type.
	 *
	 * @param  portletRequest the portlet request
	 * @param  groupId the primary key of the group
	 * @param  type the export/import option type
	 * @return an export layout settings map if the type is an export layout;
	 *         otherwise, returns either a local or remote publish layout
	 *         settings map, depending on the staging type
	 */
	public static Map<String, Serializable> buildSettingsMap(
			PortletRequest portletRequest, long groupId, int type)
		throws PortalException {

		ExportImportConfigurationSettingsMapFactory
			exportImportConfigurationSettingsMapFactory =
				_exportImportConfigurationSettingsMapFactorySnapshot.get();

		return exportImportConfigurationSettingsMapFactory.buildSettingsMap(
			portletRequest, groupId, type);
	}

	private static final Snapshot<ExportImportConfigurationSettingsMapFactory>
		_exportImportConfigurationSettingsMapFactorySnapshot = new Snapshot<>(
			ExportImportConfigurationSettingsMapFactoryUtil.class,
			ExportImportConfigurationSettingsMapFactory.class);

}