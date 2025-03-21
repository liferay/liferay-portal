/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.layout.listener;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryUsage;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletLayoutListenerException;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the implementation of <code>PortletLayoutListener</code> (in
 * <code>com.liferay.portal.kernel</code>) for the Asset Publisher portlet so
 * email subscriptions can be removed when the Asset Publisher is removed from
 * the page.
 *
 * @author Zsolt Berentey
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
	service = PortletLayoutListener.class
)
public class AssetPublisherPortletLayoutListener
	implements PortletLayoutListener {

	@Override
	public void onAddToLayout(String portletId, long plid) {
	}

	@Override
	public void onMoveInLayout(String portletId, long plid) {
	}

	@Override
	public void onRemoveFromLayout(String portletId, long plid)
		throws PortletLayoutListenerException {

		try {
			Layout layout = _layoutLocalService.getLayout(plid);

			if (_assetPublisherWebHelper.isDefaultAssetPublisher(
					layout, portletId, StringPool.BLANK)) {

				_journalArticleLocalService.deleteLayoutArticleReferences(
					layout.getGroupId(), layout.getUuid());
			}

			_deleteLayoutClassedModelUsages(layout, portletId);

			_deleteAssetListEntryUsage(plid, portletId);
		}
		catch (Exception exception) {
			throw new PortletLayoutListenerException(exception);
		}
	}

	@Override
	public void onSetup(String portletId, long plid) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return;
		}

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				layout, portletId);

		String selectionStyle = portletPreferences.getValue(
			"selectionStyle",
			AssetPublisherSelectionStyleConfigurationUtil.
				defaultSelectionStyle());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		long assetListEntryId = AssetPublisherUtil.getAssetListEntryId(
			serviceContext.getCompanyId(), serviceContext.getScopeGroupId(),
			portletPreferences);

		String infoListProviderKey = portletPreferences.getValue(
			"infoListProviderKey", StringPool.BLANK);

		if (Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST) &&
			(assetListEntryId > 0)) {

			_addAssetListEntryUsage(
				_portal.getClassNameId(AssetListEntry.class),
				String.valueOf(assetListEntryId), plid, portletId);
		}
		else if (Objects.equals(
					selectionStyle,
					AssetPublisherSelectionStyleConstants.
						TYPE_ASSET_LIST_PROVIDER) &&
				 Validator.isNotNull(infoListProviderKey)) {

			_addAssetListEntryUsage(
				_portal.getClassNameId(InfoCollectionProvider.class),
				infoListProviderKey, plid, portletId);
		}
		else if (Objects.equals(
					selectionStyle,
					AssetPublisherSelectionStyleConstants.TYPE_MANUAL)) {

			_deleteAssetListEntryUsage(plid, portletId);
			_deleteLayoutClassedModelUsages(layout, portletId);

			_addLayoutClassedModelUsages(plid, portletId, portletPreferences);
		}
		else {
			_deleteAssetListEntryUsage(plid, portletId);
		}

		if (!Objects.equals(
				selectionStyle,
				AssetPublisherSelectionStyleConstants.TYPE_MANUAL)) {

			_deleteLayoutClassedModelUsages(layout, portletId);
		}
	}

	@Override
	public void updatePropertiesOnRemoveFromLayout(
			String portletId, UnicodeProperties typeSettingsUnicodeProperties)
		throws PortletLayoutListenerException {

		String defaultAssetPublisherPortletId =
			typeSettingsUnicodeProperties.getProperty(
				LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID);

		if (portletId.equals(defaultAssetPublisherPortletId)) {
			typeSettingsUnicodeProperties.setProperty(
				LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID,
				StringPool.BLANK);
		}
	}

	private void _addAssetListEntryUsage(
		long classNameId, String key, long plid, String portletId) {

		List<AssetListEntryUsage> assetListEntryUsages =
			_assetListEntryUsageLocalService.getAssetListEntryUsages(
				portletId, _portal.getClassNameId(Portlet.class), plid);

		if (ListUtil.isNotEmpty(assetListEntryUsages)) {
			for (AssetListEntryUsage assetListEntryUsage :
					assetListEntryUsages) {

				if (Objects.equals(key, assetListEntryUsage.getKey())) {
					continue;
				}

				assetListEntryUsage.setClassNameId(classNameId);
				assetListEntryUsage.setKey(key);

				_assetListEntryUsageLocalService.updateAssetListEntryUsage(
					assetListEntryUsage);
			}

			return;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			_assetListEntryUsageLocalService.addAssetListEntryUsage(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				classNameId, portletId, _portal.getClassNameId(Portlet.class),
				key, plid, serviceContext);
		}
		catch (PortalException portalException) {
			_log.error("Unable to add asset list entry usage", portalException);
		}
	}

	private void _addLayoutClassedModelUsages(
			long plid, String portletId, PortletPreferences portletPreferences)
		throws PortletLayoutListenerException {

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			PortletRequest portletRequest =
				serviceContext.getLiferayPortletRequest();

			long[] groupIds = _assetPublisherHelper.getGroupIds(
				portletPreferences, themeDisplay.getScopeGroupId(),
				themeDisplay.getLayout());

			List<AssetEntry> assetEntries =
				_assetPublisherHelper.getAssetEntries(
					portletRequest, portletPreferences,
					themeDisplay.getPermissionChecker(), groupIds, false, true);

			for (AssetEntry assetEntry : assetEntries) {
				long classNameId = assetEntry.getClassNameId();

				if (Objects.equals(
						assetEntry.getClassName(),
						DLFileEntry.class.getName())) {

					classNameId = _portal.getClassNameId(
						FileEntry.class.getName());
				}

				_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
					themeDisplay.getScopeGroupId(), classNameId,
					assetEntry.getClassPK(), StringPool.BLANK, portletId,
					_portal.getClassNameId(Portlet.class), plid,
					serviceContext);
			}
		}
		catch (Exception exception) {
			throw new PortletLayoutListenerException(exception);
		}
	}

	private void _deleteAssetListEntryUsage(long plid, String portletId) {
		_assetListEntryUsageLocalService.deleteAssetListEntryUsages(
			portletId, _portal.getClassNameId(Portlet.class), plid);
	}

	private void _deleteLayoutClassedModelUsages(
		Layout layout, String portletId) {

		_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsages(
			portletId, _portal.getClassNameId(Portlet.class), layout.getPlid());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherPortletLayoutListener.class);

	@Reference
	private AssetListEntryUsageLocalService _assetListEntryUsageLocalService;

	@Reference
	private AssetPublisherHelper _assetPublisherHelper;

	@Reference
	private AssetPublisherWebHelper _assetPublisherWebHelper;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}