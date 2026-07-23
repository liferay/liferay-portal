/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ModelListener.class)
public class AssetVocabularyModelListener
	extends BaseModelListener<AssetVocabulary> {

	@Override
	public void onAfterCreate(AssetVocabulary assetVocabulary)
		throws ModelListenerException {

		try {
			Group group = _groupLocalService.fetchGroup(
				assetVocabulary.getGroupId());

			if ((group == null) || !group.isCMS()) {
				return;
			}

			_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
				assetVocabulary.getVocabularyId(),
				new long[] {GroupConstants.GROUP_ID_ALL},
				DepotConstants.TYPE_PROJECT);
			_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
				assetVocabulary.getVocabularyId(),
				new long[] {GroupConstants.GROUP_ID_ALL},
				DepotConstants.TYPE_SPACE);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}