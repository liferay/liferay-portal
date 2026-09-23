/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.util;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class GroupExportImportParameterUtil {

	public static String getCurrentGroupExternalReferenceCode(
		Map<String, String[]> parameterMap) {

		String currentGroupExternalReferenceCode = MapUtil.getString(
			parameterMap, _CURRENT_GROUP_EXTERNAL_REFERENCE_CODE);

		if (Validator.isNull(currentGroupExternalReferenceCode)) {
			return null;
		}

		return currentGroupExternalReferenceCode;
	}

	public static Map<String, String[]> getGroupExportParameterMap(
		String externalReferenceCode, Map<String, String[]> parameterMap) {

		return _getGroupParameterMap(externalReferenceCode, parameterMap);
	}

	public static Map<String, String[]> getGroupImportParameterMap(
		String externalReferenceCode, Map<String, String[]> parameterMap) {

		return HashMapBuilder.putAll(
			_getGroupParameterMap(externalReferenceCode, parameterMap)
		).put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
		).put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
			new String[] {
				PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_UUID
			}
		).build();
	}

	public static String[] getSelectedGroupExternalReferenceCodes(
		Map<String, String[]> parameterMap) {

		String[] groupExternalReferenceCodes = parameterMap.get(
			PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES);

		if (groupExternalReferenceCodes == null) {
			return new String[0];
		}

		return groupExternalReferenceCodes;
	}

	public static boolean isGroupExportImportEnabled(long companyId) {
		return FeatureFlagManagerUtil.isEnabled(companyId, "LPD-85946");
	}

	public static boolean isGroupScoped(Map<String, String[]> parameterMap) {
		if (getCurrentGroupExternalReferenceCode(parameterMap) != null) {
			return true;
		}

		return false;
	}

	public static boolean isGroupScoped(PortletDataContext portletDataContext) {
		return isGroupScoped(portletDataContext.getParameterMap());
	}

	private static Map<String, String[]> _getGroupParameterMap(
		String externalReferenceCode, Map<String, String[]> parameterMap) {

		Map<String, String[]> groupParameterMap = HashMapBuilder.putAll(
			parameterMap
		).put(
			_CURRENT_GROUP_EXTERNAL_REFERENCE_CODE,
			new String[] {externalReferenceCode}
		).put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_PRIVATE_LAYOUT,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_PROTOTYPE_SETTINGS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LOGO, new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {Boolean.TRUE.toString()}
		).build();

		groupParameterMap.remove(
			PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES);

		return groupParameterMap;
	}

	private static final String _CURRENT_GROUP_EXTERNAL_REFERENCE_CODE =
		"CURRENT_GROUP_EXTERNAL_REFERENCE_CODE";

}