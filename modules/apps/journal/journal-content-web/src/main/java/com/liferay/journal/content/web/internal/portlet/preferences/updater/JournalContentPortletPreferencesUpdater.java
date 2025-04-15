/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.portlet.preferences.updater;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.portlet.preferences.updater.PortletPreferencesUpdater;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = PortletPreferencesUpdater.class
)
public class JournalContentPortletPreferencesUpdater
	implements PortletPreferencesUpdater {

	@Override
	public void updatePortletPreferences(
			String className, long classPK, String portletId,
			PortletPreferences portletPreferences, ThemeDisplay themeDisplay)
		throws Exception {

		AssetRendererFactory<JournalArticle> articleAssetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			className, classPK);

		AssetRenderer<JournalArticle> articleAssetRenderer =
			articleAssetRendererFactory.getAssetRenderer(
				assetEntry.getClassPK());

		JournalArticle article = articleAssetRenderer.getAssetObject();

		portletPreferences.setValue(
			"articleExternalReferenceCode", article.getExternalReferenceCode());

		Group group = _groupLocalService.fetchGroup(article.getGroupId());

		if (group != null) {
			portletPreferences.setValue(
				"groupExternalReferenceCode", group.getExternalReferenceCode());
		}

		_addLayoutClassedModelUsage(
			themeDisplay.getLayout(), portletId, article);
	}

	private void _addLayoutClassedModelUsage(
		Layout layout, String portletId, JournalArticle article) {

		LayoutClassedModelUsage layoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				layout.getGroupId(),
				_portal.getClassNameId(JournalArticle.class),
				article.getResourcePrimKey(), StringPool.BLANK, portletId,
				_portal.getClassNameId(Portlet.class), layout.getPlid());

		if (layoutClassedModelUsage != null) {
			return;
		}

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			layout.getGroupId(), _portal.getClassNameId(JournalArticle.class),
			article.getResourcePrimKey(), StringPool.BLANK, portletId,
			_portal.getClassNameId(Portlet.class), layout.getPlid(),
			ServiceContextThreadLocal.getServiceContext());
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private Portal _portal;

}