/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.portlet.preferences.updater;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.layout.portlet.preferences.updater.PortletPreferencesUpdater;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "model.class.name=model.class.name=com.liferay.dynamic.data.lists.model.DDLRecord",
	service = PortletPreferencesUpdater.class
)
public class DDLDisplayPortletPreferencesUpdater
	implements PortletPreferencesUpdater {

	@Override
	public void updatePortletPreferences(
			String className, long classPK, String portletId,
			PortletPreferences portletPreferences, ThemeDisplay themeDisplay)
		throws Exception {

		AssetRendererFactory<DDLRecord> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				DDLRecord.class);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			className, classPK);

		AssetRenderer<DDLRecord> assetRenderer =
			assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());

		DDLRecord record = assetRenderer.getAssetObject();

		portletPreferences.setValue(
			"recordSetId", String.valueOf(record.getRecordSetId()));
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

}