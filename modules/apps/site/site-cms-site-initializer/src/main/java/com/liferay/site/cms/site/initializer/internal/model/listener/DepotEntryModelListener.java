/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelListener;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ModelListener.class)
public class DepotEntryModelListener extends BaseModelListener<DepotEntry> {

	@Override
	public void onBeforeRemove(DepotEntry depotEntry)
		throws ModelListenerException {

		try {
			if (!(depotEntry.getType() == DepotConstants.TYPE_SPACE)) {
				return;
			}

			List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
				_assetVocabularyGroupRelLocalService.
					getAssetVocabularyGroupRelsByGroupId(
						depotEntry.getGroupId());

			for (AssetVocabularyGroupRel assetVocabularyGroupRel :
					assetVocabularyGroupRels) {

				long count =
					_assetVocabularyGroupRelLocalService.
						getAssetVocabularyGroupRelsCount(
							assetVocabularyGroupRel.getVocabularyId());

				if (count == 1) {
					_assetVocabularyGroupRelLocalService.
						addAssetVocabularyGroupRel(
							GroupConstants.ANY_PARENT_GROUP_ID,
							assetVocabularyGroupRel.getVocabularyId());
				}
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

}