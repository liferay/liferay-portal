/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.upgrade.v1_0_6;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfiguration;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author Lourdes Fernández Besada
 */
public class UpgradePortletPreferences extends BaseUpgradePortletPreferences {

	public UpgradePortletPreferences(
		AssetListEntryLocalService assetListEntryLocalService,
		ConfigurationProvider configurationProvider) {

		_assetListEntryLocalService = assetListEntryLocalService;

		_defaultSelectionStyle = _getDefaultSelectionStyle(
			configurationProvider);
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			AssetPublisherPortletKeys.ASSET_PUBLISHER + "_INSTANCE_%",
			AssetPublisherPortletKeys.HIGHEST_RATED_ASSETS + "_INSTANCE_%",
			AssetPublisherPortletKeys.MOST_VIEWED_ASSETS + "_INSTANCE_%",
			AssetPublisherPortletKeys.RECENT_CONTENT + "_INSTANCE_%",
			AssetPublisherPortletKeys.RELATED_ASSETS + "_INSTANCE_%"
		};
	}

	@Override
	protected void upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, PortletPreferences portletPreferences)
		throws Exception {

		if (!portletId.startsWith(AssetPublisherPortletKeys.ASSET_PUBLISHER) &&
			!portletId.startsWith(AssetPublisherPortletKeys.RECENT_CONTENT)) {

			return;
		}

		String selectionStyle = GetterUtil.getString(
			portletPreferences.getValue("selectionStyle", null),
			_defaultSelectionStyle);

		if (!selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST)) {

			return;
		}

		long assetListEntryId = GetterUtil.getLong(
			portletPreferences.getValue("assetListEntryId", null));

		if (assetListEntryId == 0) {
			return;
		}

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(assetListEntryId);

		if (assetListEntry == null) {
			return;
		}

		portletPreferences.setValue(
			"assetListEntryExternalReferenceCode",
			assetListEntry.getExternalReferenceCode());

		String scopeExternalReferenceCode = getScopeExternalReferenceCode(
			companyId, ownerId, ownerType, plid, assetListEntry.getGroupId());

		if (Validator.isNotNull(scopeExternalReferenceCode)) {
			portletPreferences.setValue(
				"assetListEntryGroupExternalReferenceCode",
				scopeExternalReferenceCode);
		}
	}

	private String _getDefaultSelectionStyle(
		ConfigurationProvider configurationProvider) {

		try {
			AssetPublisherSelectionStyleConfiguration
				assetPublisherSelectionStyleConfiguration =
					configurationProvider.getSystemConfiguration(
						AssetPublisherSelectionStyleConfiguration.class);

			return assetPublisherSelectionStyleConfiguration.
				defaultSelectionStyle();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(configurationException);
			}
		}

		return AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletPreferences.class);

	private final AssetListEntryLocalService _assetListEntryLocalService;
	private final String _defaultSelectionStyle;

}