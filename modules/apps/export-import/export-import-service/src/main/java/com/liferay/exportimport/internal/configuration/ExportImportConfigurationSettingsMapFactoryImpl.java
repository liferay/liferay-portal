/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.configuration;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides a way to build a settings map for an {@link
 * com.liferay.exportimport.kernel.model.ExportImportConfiguration}, which can
 * be used to start and control an export, import, or staging process.
 *
 * @author Daniel Kocsis
 * @author Akos Thurzo
 * @since  7.0
 */
@Component(service = ExportImportConfigurationSettingsMapFactory.class)
public class ExportImportConfigurationSettingsMapFactoryImpl
	implements ExportImportConfigurationSettingsMapFactory {

	@Override
	public Map<String, Serializable> buildExportLayoutSettingsMap(
		long userId, long groupId, boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone) {

		return buildPublishLayoutLocalSettingsMap(
			userId, groupId, 0, privateLayout, layoutIds, parameterMap, locale,
			timeZone);
	}

	@Override
	public Map<String, Serializable> buildExportLayoutSettingsMap(
		User user, long groupId, boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap) {

		return buildExportLayoutSettingsMap(
			user.getUserId(), groupId, privateLayout, layoutIds, parameterMap,
			user.getLocale(), user.getTimeZone());
	}

	@Override
	public Map<String, Serializable> buildExportPortletSettingsMap(
		long userId, long sourcePlid, long sourceGroupId, String portletId,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone,
		String fileName) {

		return buildSettingsMap(
			userId, sourceGroupId, sourcePlid, 0, 0, portletId, null, null,
			null, parameterMap, StringPool.BLANK, 0, StringPool.BLANK, null,
			null, locale, timeZone, fileName);
	}

	@Override
	public Map<String, Serializable> buildExportPortletSettingsMap(
		User user, long sourcePlid, long sourceGroupId, String portletId,
		Map<String, String[]> parameterMap, String fileName) {

		return buildExportPortletSettingsMap(
			user.getUserId(), sourcePlid, sourceGroupId, portletId,
			parameterMap, user.getLocale(), user.getTimeZone(), fileName);
	}

	@Override
	public Map<String, Serializable> buildImportLayoutSettingsMap(
		long userId, long targetGroupId, boolean privateLayout,
		long[] layoutIds, Map<String, String[]> parameterMap, Locale locale,
		TimeZone timeZone) {

		return buildSettingsMap(
			userId, 0, 0, targetGroupId, 0, StringPool.BLANK, privateLayout,
			null, layoutIds, parameterMap, StringPool.BLANK, 0,
			StringPool.BLANK, null, null, locale, timeZone, StringPool.BLANK);
	}

	@Override
	public Map<String, Serializable> buildImportLayoutSettingsMap(
		User user, long targetGroupId, boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap) {

		return buildImportLayoutSettingsMap(
			user.getUserId(), targetGroupId, privateLayout, layoutIds,
			parameterMap, user.getLocale(), user.getTimeZone());
	}

	@Override
	public Map<String, Serializable> buildImportPortletSettingsMap(
		long userId, long targetPlid, long targetGroupId, String portletId,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone) {

		return buildSettingsMap(
			userId, 0, 0, targetGroupId, targetPlid, portletId, null, null,
			null, parameterMap, StringPool.BLANK, 0, StringPool.BLANK, null,
			null, locale, timeZone, StringPool.BLANK);
	}

	@Override
	public Map<String, Serializable> buildImportPortletSettingsMap(
		User user, long targetPlid, long targetGroupId, String portletId,
		Map<String, String[]> parameterMap) {

		return buildImportPortletSettingsMap(
			user.getUserId(), targetPlid, targetGroupId, portletId,
			parameterMap, user.getLocale(), user.getTimeZone());
	}

	@Override
	public Map<String, Serializable> buildPublishLayoutLocalSettingsMap(
		long userId, long sourceGroupId, long targetGroupId,
		boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap, Locale locale, TimeZone timeZone) {

		return buildSettingsMap(
			userId, sourceGroupId, 0, targetGroupId, 0, StringPool.BLANK,
			privateLayout, null, layoutIds, parameterMap, StringPool.BLANK, 0,
			StringPool.BLANK, null, null, locale, timeZone, StringPool.BLANK);
	}

	@Override
	public Map<String, Serializable> buildPublishLayoutLocalSettingsMap(
		User user, long sourceGroupId, long targetGroupId,
		boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap) {

		return buildPublishLayoutLocalSettingsMap(
			user.getUserId(), sourceGroupId, targetGroupId, privateLayout,
			layoutIds, parameterMap, user.getLocale(), user.getTimeZone());
	}

	@Override
	public Map<String, Serializable> buildPublishLayoutRemoteSettingsMap(
		long userId, long sourceGroupId, boolean privateLayout,
		Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
		String remoteAddress, int remotePort, String remotePathContext,
		boolean secureConnection, long remoteGroupId,
		boolean remotePrivateLayout, Locale locale, TimeZone timeZone) {

		return buildSettingsMap(
			userId, sourceGroupId, 0, remoteGroupId, 0, StringPool.BLANK,
			privateLayout, layoutIdMap, null, parameterMap, remoteAddress,
			remotePort, remotePathContext, secureConnection,
			remotePrivateLayout, locale, timeZone, StringPool.BLANK);
	}

	@Override
	public Map<String, Serializable> buildPublishLayoutRemoteSettingsMap(
		User user, long sourceGroupId, boolean privateLayout,
		Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
		String remoteAddress, int remotePort, String remotePathContext,
		boolean secureConnection, long remoteGroupId,
		boolean remotePrivateLayout) {

		return buildPublishLayoutRemoteSettingsMap(
			user.getUserId(), sourceGroupId, privateLayout, layoutIdMap,
			parameterMap, remoteAddress, remotePort, remotePathContext,
			secureConnection, remoteGroupId, remotePrivateLayout,
			user.getLocale(), user.getTimeZone());
	}

	@Override
	public Map<String, Serializable> buildPublishPortletSettingsMap(
		long userId, long sourceGroupId, long sourcePlid, long targetGroupId,
		long targetPlid, String portletId, Map<String, String[]> parameterMap,
		Locale locale, TimeZone timeZone) {

		return buildSettingsMap(
			userId, sourceGroupId, sourcePlid, targetGroupId, targetPlid,
			portletId, null, null, null, parameterMap, StringPool.BLANK, 0,
			StringPool.BLANK, null, null, locale, timeZone, null);
	}

	@Override
	public Map<String, Serializable> buildPublishPortletSettingsMap(
		User user, long sourceGroupId, long sourcePlid, long targetGroupId,
		long targetPlid, String portletId, Map<String, String[]> parameterMap) {

		return buildPublishPortletSettingsMap(
			user.getUserId(), sourceGroupId, sourcePlid, targetGroupId,
			targetPlid, portletId, parameterMap, user.getLocale(),
			user.getTimeZone());
	}

	/**
	 * Returns an export layout settings map if the type is {@link
	 * ExportImportConfigurationConstants#TYPE_EXPORT_LAYOUT}; otherwise,
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
	@Override
	public Map<String, Serializable> buildSettingsMap(
			PortletRequest portletRequest, long groupId, int type)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean privateLayout = ParamUtil.getBoolean(
			portletRequest, "privateLayout");

		Map<Long, Boolean> layoutIdMap = _exportImportHelper.getLayoutIdMap(
			portletRequest);

		if (type == ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT) {
			return buildExportLayoutSettingsMap(
				themeDisplay.getUserId(), groupId, privateLayout,
				_exportImportHelper.getLayoutIds(layoutIdMap),
				portletRequest.getParameterMap(), themeDisplay.getLocale(),
				themeDisplay.getTimeZone());
		}

		Group stagingGroup = _groupLocalService.getGroup(groupId);

		Group liveGroup = stagingGroup.getLiveGroup();

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.buildParameterMap(
				portletRequest);

		if (liveGroup != null) {
			return buildPublishLayoutLocalSettingsMap(
				themeDisplay.getUserId(), stagingGroup.getGroupId(),
				liveGroup.getGroupId(), privateLayout,
				_exportImportHelper.getLayoutIds(
					layoutIdMap, liveGroup.getGroupId()),
				parameterMap, themeDisplay.getLocale(),
				themeDisplay.getTimeZone());
		}

		UnicodeProperties groupTypeSettingsUnicodeProperties =
			stagingGroup.getTypeSettingsProperties();

		String remoteAddress = ParamUtil.getString(
			portletRequest, "remoteAddress",
			groupTypeSettingsUnicodeProperties.getProperty("remoteAddress"));

		remoteAddress = HttpComponentsUtil.removeProtocol(remoteAddress);

		int remotePort = ParamUtil.getInteger(
			portletRequest, "remotePort",
			GetterUtil.getInteger(
				groupTypeSettingsUnicodeProperties.getProperty("remotePort")));
		String remotePathContext = ParamUtil.getString(
			portletRequest, "remotePathContext",
			groupTypeSettingsUnicodeProperties.getProperty(
				"remotePathContext"));
		boolean secureConnection = ParamUtil.getBoolean(
			portletRequest, "secureConnection",
			GetterUtil.getBoolean(
				groupTypeSettingsUnicodeProperties.getProperty(
					"secureConnection")));
		long remoteGroupId = ParamUtil.getLong(
			portletRequest, "remoteGroupId",
			GetterUtil.getLong(
				groupTypeSettingsUnicodeProperties.getProperty(
					"remoteGroupId")));
		boolean remotePrivateLayout = ParamUtil.getBoolean(
			portletRequest, "remotePrivateLayout");

		_groupLocalService.validateRemote(
			groupId, remoteAddress, remotePort, remotePathContext,
			secureConnection, remoteGroupId);

		return buildPublishLayoutRemoteSettingsMap(
			themeDisplay.getUserId(), groupId, privateLayout, layoutIdMap,
			parameterMap, remoteAddress, remotePort, remotePathContext,
			secureConnection, remoteGroupId, remotePrivateLayout,
			themeDisplay.getLocale(), themeDisplay.getTimeZone());
	}

	protected Map<String, Serializable> buildSettingsMap(
		long userId, long sourceGroupId, long sourcePlid, long targetGroupId,
		long targetPlid, String portletId, Boolean privateLayout,
		Map<Long, Boolean> layoutIdMap, long[] layoutIds,
		Map<String, String[]> parameterMap, String remoteAddress,
		int remotePort, String remotePathContext, Boolean secureConnection,
		Boolean remotePrivateLayout, Locale locale, TimeZone timeZone,
		String fileName) {

		Map<String, Serializable> settingsMap = new HashMap<>();

		if (Validator.isNotNull(fileName)) {
			settingsMap.put("fileName", fileName);
		}

		if (MapUtil.isNotEmpty(layoutIdMap)) {
			HashMap<Long, Boolean> serializableLayoutIdMap = new HashMap<>(
				layoutIdMap);

			settingsMap.put("layoutIdMap", serializableLayoutIdMap);
		}

		if (ArrayUtil.isNotEmpty(layoutIds)) {
			settingsMap.put("layoutIds", layoutIds);
		}

		if (locale != null) {
			settingsMap.put("locale", locale);
		}

		if (parameterMap != null) {
			HashMap<String, String[]> serializableParameterMap = new HashMap<>(
				parameterMap);

			if (layoutIds != null) {
				serializableParameterMap.remove("layoutIds");
			}

			settingsMap.put("parameterMap", serializableParameterMap);
		}

		if (Validator.isNotNull(portletId)) {
			settingsMap.put("portletId", portletId);
		}

		if (privateLayout != null) {
			settingsMap.put("privateLayout", privateLayout);
		}

		if (Validator.isNotNull(remoteAddress)) {
			settingsMap.put("remoteAddress", remoteAddress);
		}

		if (Validator.isNotNull(remotePathContext)) {
			settingsMap.put("remotePathContext", remotePathContext);
		}

		if (remotePort > 0) {
			settingsMap.put("remotePort", remotePort);
		}

		if (remotePrivateLayout != null) {
			settingsMap.put("remotePrivateLayout", remotePrivateLayout);
		}

		if (secureConnection != null) {
			settingsMap.put("secureConnection", secureConnection);
		}

		if (sourceGroupId > 0) {
			settingsMap.put("sourceGroupId", sourceGroupId);
		}

		if (sourcePlid > 0) {
			settingsMap.put("sourcePlid", sourcePlid);
		}

		if (targetGroupId > 0) {
			settingsMap.put("targetGroupId", targetGroupId);
		}

		if (targetPlid > 0) {
			settingsMap.put("targetPlid", targetPlid);
		}

		if (timeZone != null) {
			settingsMap.put("timezone", timeZone);
		}

		settingsMap.put("userId", userId);

		return settingsMap;
	}

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

}