/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntryServiceUtil;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetPublisherUtil {

	public static AssetListEntry getAssetListEntry(
			boolean checkPermissions, long companyId, long groupId,
			PortletPreferences portletPreferences)
		throws PortalException {

		String selectionStyle = GetterUtil.getString(
			portletPreferences.getValue("selectionStyle", null),
			AssetPublisherSelectionStyleConfigurationUtil.
				defaultSelectionStyle());

		if (!selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST)) {

			return null;
		}

		String assetListEntryExternalReferenceCode = GetterUtil.getString(
			portletPreferences.getValue(
				"assetListEntryExternalReferenceCode", null));

		if (Validator.isNull(assetListEntryExternalReferenceCode)) {
			return null;
		}

		String assetListEntryGroupExternalReferenceCode = GetterUtil.getString(
			portletPreferences.getValue(
				"assetListEntryGroupExternalReferenceCode", null));

		if (Validator.isNull(assetListEntryGroupExternalReferenceCode)) {
			return _fetchAssetListEntryByExternalReferenceCode(
				checkPermissions, assetListEntryExternalReferenceCode, groupId);
		}

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			assetListEntryGroupExternalReferenceCode, companyId);

		if (group == null) {
			return null;
		}

		return _fetchAssetListEntryByExternalReferenceCode(
			checkPermissions, assetListEntryExternalReferenceCode,
			group.getGroupId());
	}

	public static long getAssetListEntryId(
		long companyId, long groupId, PortletPreferences portletPreferences) {

		try {
			AssetListEntry assetListEntry = getAssetListEntry(
				false, companyId, groupId, portletPreferences);

			if (assetListEntry != null) {
				return assetListEntry.getAssetListEntryId();
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return 0;
	}

	public static long getDisplayStyleGroupId(
		long companyId, long groupId, PortletPreferences portletPreferences) {

		String displayStyleGroupExternalReferenceCode =
			portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null);

		if (Validator.isNull(displayStyleGroupExternalReferenceCode)) {
			return groupId;
		}

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			displayStyleGroupExternalReferenceCode, companyId);

		if (group != null) {
			return group.getGroupId();
		}

		return 0;
	}

	private static AssetListEntry _fetchAssetListEntryByExternalReferenceCode(
			boolean checkPermissions, String externalReferenceCode,
			long groupId)
		throws PortalException {

		if (checkPermissions) {
			return AssetListEntryServiceUtil.
				fetchAssetListEntryByExternalReferenceCode(
					externalReferenceCode, groupId);
		}

		return AssetListEntryLocalServiceUtil.
			fetchAssetListEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherUtil.class);

}