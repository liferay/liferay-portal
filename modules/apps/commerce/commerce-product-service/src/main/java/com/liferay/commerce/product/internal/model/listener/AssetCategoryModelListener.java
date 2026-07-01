/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.model.listener;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDisplayLayoutLocalService;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = ModelListener.class)
public class AssetCategoryModelListener
	extends BaseModelListener<AssetCategory> {

	@Override
	public void onAfterCreate(AssetCategory assetCategory)
		throws ModelListenerException {

		if (FeatureFlagManagerUtil.isEnabled(
				assetCategory.getCompanyId(), "LPD-70396")) {

			return;
		}

		try {
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				assetCategory.getGroupId(),
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(),
				_getUniqueUrlTitles(assetCategory), new ServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	@Override
	public void onBeforeRemove(AssetCategory assetCategory) {
		try {
			_cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntries(
				AssetCategory.class.getName(), assetCategory.getCategoryId());

			_cpDisplayLayoutLocalService.deleteCPDisplayLayouts(
				AssetCategory.class, assetCategory.getCategoryId());

			if (!FeatureFlagManagerUtil.isEnabled(
					assetCategory.getCompanyId(), "LPD-70396")) {

				_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
					assetCategory.getGroupId(),
					_portal.getClassNameId(AssetCategory.class),
					assetCategory.getCategoryId());
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private Map<String, String> _getUniqueUrlTitles(AssetCategory assetCategory)
		throws PortalException {

		Map<String, String> urlTitleMap = new HashMap<>();

		Map<Locale, String> titleMap = assetCategory.getTitleMap();

		for (Map.Entry<Locale, String> titleEntry : titleMap.entrySet()) {
			String urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
				assetCategory.getGroupId(),
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(), titleEntry.getValue(), null);

			urlTitleMap.put(
				LocaleUtil.toLanguageId(titleEntry.getKey()), urlTitle);
		}

		return urlTitleMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryModelListener.class);

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Portal _portal;

}