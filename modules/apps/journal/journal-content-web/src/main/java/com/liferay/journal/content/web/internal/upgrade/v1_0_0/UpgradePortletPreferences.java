/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.upgrade.v1_0_0;

import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

/**
 * @author Julio Camarero
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			StringPool.PERCENT + JournalContentPortletKeys.JOURNAL_CONTENT +
				StringPool.PERCENT
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		_upgradeContentMetadataAssetAddonEntryKeys(portletPreferences);
		_upgradeUserToolAssetAddonEntryKeys(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private String[] _upgradeBooleanAssetAddonEntry(
			String[] assetAddonEntryKeys, PortletPreferences portletPreferences,
			String preferenceKey)
		throws Exception {

		boolean preferenceValue = GetterUtil.getBoolean(
			portletPreferences.getValue(preferenceKey, null));

		if (preferenceValue) {
			assetAddonEntryKeys = ArrayUtil.append(
				assetAddonEntryKeys, preferenceKey);
		}

		portletPreferences.reset(preferenceKey);

		return assetAddonEntryKeys;
	}

	private void _upgradeContentMetadataAssetAddonEntryKeys(
			PortletPreferences portletPreferences)
		throws Exception {

		String[] contentMetadataAssetAddonEntryKeys = new String[0];

		contentMetadataAssetAddonEntryKeys = _upgradeBooleanAssetAddonEntry(
			contentMetadataAssetAddonEntryKeys, portletPreferences,
			"enableCommentRatings");
		contentMetadataAssetAddonEntryKeys = _upgradeBooleanAssetAddonEntry(
			contentMetadataAssetAddonEntryKeys, portletPreferences,
			"enableComments");
		contentMetadataAssetAddonEntryKeys = _upgradeBooleanAssetAddonEntry(
			contentMetadataAssetAddonEntryKeys, portletPreferences,
			"enableRatings");
		contentMetadataAssetAddonEntryKeys = _upgradeBooleanAssetAddonEntry(
			contentMetadataAssetAddonEntryKeys, portletPreferences,
			"enableRelatedAssets");

		portletPreferences.setValue(
			"contentMetadataAssetAddonEntryKeys",
			StringUtil.merge(contentMetadataAssetAddonEntryKeys));
	}

	private String[] _upgradeMultiValueAssetAddonEntryKeys(
			String[] assetAddonEntryKeys, PortletPreferences portletPreferences,
			String preferenceKey, Map<String, String> newPreferenceValues)
		throws Exception {

		String[] preferenceValues = portletPreferences.getValues(
			preferenceKey, null);

		if (preferenceValues != null) {
			for (String preferenceValue : preferenceValues) {
				String newPreferenceValue = newPreferenceValues.get(
					preferenceValue);

				if (Validator.isNotNull(newPreferenceValue)) {
					assetAddonEntryKeys = ArrayUtil.append(
						assetAddonEntryKeys, preferenceKey);
				}
			}
		}

		portletPreferences.reset(preferenceKey);

		return assetAddonEntryKeys;
	}

	private void _upgradeUserToolAssetAddonEntryKeys(
			PortletPreferences portletPreferences)
		throws Exception {

		String[] userToolAssetAddonEntryKeys = new String[0];

		userToolAssetAddonEntryKeys = _upgradeBooleanAssetAddonEntry(
			userToolAssetAddonEntryKeys, portletPreferences, "enablePrint");

		userToolAssetAddonEntryKeys = _upgradeMultiValueAssetAddonEntryKeys(
			userToolAssetAddonEntryKeys, portletPreferences, "extensions",
			HashMapBuilder.put(
				"doc", "enableDOC"
			).put(
				"odt", "enableODT"
			).put(
				"pdf", "enablePDF"
			).put(
				"txt", "enableTXT"
			).build());

		userToolAssetAddonEntryKeys = _upgradeBooleanAssetAddonEntry(
			userToolAssetAddonEntryKeys, portletPreferences,
			"showAvailableLocales");

		portletPreferences.setValue(
			"userToolAssetAddonEntryKeys",
			StringUtil.merge(userToolAssetAddonEntryKeys));
	}

}