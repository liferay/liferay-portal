/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.model.listener;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ModelListener.class)
public class AssetCategoryFriendlyURLModelListener
	extends BaseModelListener<AssetCategory> {

	@Override
	public void onAfterCreate(AssetCategory assetCategory)
		throws ModelListenerException {

		try {
			if (ExportImportThreadLocal.isImportInProcess() ||
				ExportImportThreadLocal.isStagingInProcess()) {

				return;
			}

			long classNameId = _classNameLocalService.getClassNameId(
				AssetCategory.class);
			long parentClassPK = _getAssetCategoryParentClassPK(assetCategory);

			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				assetCategory.getGroupId(), classNameId, parentClassPK,
				assetCategory.getCategoryId(),
				assetCategory.getDefaultLanguageId(),
				_friendlyURLEntryLocalService.getUniqueUrlTitleMap(
					assetCategory.getGroupId(), classNameId, parentClassPK,
					assetCategory.getCategoryId(), assetCategory.getTitleMap()),
				new ServiceContext());
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onBeforeRemove(AssetCategory assetCategory)
		throws ModelListenerException {

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			assetCategory.getGroupId(),
			_classNameLocalService.getClassNameId(AssetCategory.class),
			assetCategory.getCategoryId());
	}

	private long _getAssetCategoryParentClassPK(AssetCategory assetCategory) {
		if (assetCategory.getParentCategoryId() ==
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			return assetCategory.getVocabularyId();
		}

		return assetCategory.getParentCategoryId();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}