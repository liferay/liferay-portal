/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.feature.flag;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "feature.flag.key=LPD-70396", service = FeatureFlagListener.class
)
public class AssetCategoryFriendlyURLFeatureFlagListener
	implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!enabled) {
			return;
		}

		long classNameId = _classNameLocalService.getClassNameId(
			AssetCategory.class);

		ActionableDynamicQuery actionableDynamicQuery =
			_assetCategoryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setCompanyId(companyId);
		actionableDynamicQuery.setPerformActionMethod(
			(AssetCategory assetCategory) -> {
				try {
					_migrate(assetCategory, classNameId);
				}
				catch (PortalException portalException) {
					_log.error(
						"Unable to migrate the friendly URL entry for asset " +
							"category " + assetCategory.getCategoryId(),
						portalException);
				}
			});

		try {
			actionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private long _getParentClassPK(AssetCategory assetCategory) {
		if (assetCategory.getParentCategoryId() ==
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			return assetCategory.getVocabularyId();
		}

		return assetCategory.getParentCategoryId();
	}

	private void _migrate(AssetCategory assetCategory, long classNameId)
		throws PortalException {

		long parentClassPK = _getParentClassPK(assetCategory);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				classNameId, assetCategory.getCategoryId());

		if (friendlyURLEntry == null) {
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				assetCategory.getGroupId(), classNameId, parentClassPK,
				assetCategory.getCategoryId(),
				assetCategory.getDefaultLanguageId(),
				_friendlyURLEntryLocalService.getUniqueUrlTitleMap(
					assetCategory.getGroupId(), classNameId, parentClassPK,
					assetCategory.getCategoryId(), assetCategory.getTitleMap()),
				new ServiceContext());

			return;
		}

		if (friendlyURLEntry.getParentClassPK() == parentClassPK) {
			return;
		}

		Map<String, String> urlTitleMap = new HashMap<>();

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
					friendlyURLEntry.getFriendlyURLEntryId())) {

			urlTitleMap.put(
				friendlyURLEntryLocalization.getLanguageId(),
				friendlyURLEntryLocalization.getUrlTitle());
		}

		_friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntry.getFriendlyURLEntryId(), classNameId,
			parentClassPK, assetCategory.getCategoryId(),
			friendlyURLEntry.getDefaultLanguageId(), urlTitleMap,
			new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryFriendlyURLFeatureFlagListener.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}