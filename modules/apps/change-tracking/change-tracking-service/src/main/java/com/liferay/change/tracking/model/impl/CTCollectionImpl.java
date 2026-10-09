/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.model.impl;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.internal.score.CTScoreCache;
import com.liferay.change.tracking.internal.score.CTScoreSizeClassificationCache;
import com.liferay.change.tracking.mapping.CTMappingTableInfo;
import com.liferay.change.tracking.service.CTCollectionLocalServiceUtil;
import com.liferay.change.tracking.service.CTEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Daniel Kocsis
 */
public class CTCollectionImpl extends CTCollectionBaseImpl {

	@Override
	public int getScore() {
		CTScoreCache ctScoreCache = _ctScoreCacheSnapshot.get();

		return ctScoreCache.getScore(getCtCollectionId());
	}

	@Override
	public String getScoreSizeClassification() {
		CTScoreSizeClassificationCache ctScoreSizeClassificationCache =
			_ctScoreSizeClassificationCacheSnapshot.get();

		return ctScoreSizeClassificationCache.getScoreSizeClassification(
			getCtCollectionId());
	}

	@Override
	public String getUserName() {
		User user = UserLocalServiceUtil.fetchUser(getUserId());

		if (user == null) {
			return StringPool.BLANK;
		}

		return user.getFullName();
	}

	@Override
	public boolean isEmpty() {
		long ctCollectionId = getCtCollectionId();

		int ctEntriesCount =
			CTEntryLocalServiceUtil.getCTCollectionCTEntriesCount(
				ctCollectionId);

		if (ctEntriesCount != 0) {
			return false;
		}

		List<CTMappingTableInfo> ctMappingTableInfos =
			CTCollectionLocalServiceUtil.getCTMappingTableInfos(ctCollectionId);

		return ctMappingTableInfos.isEmpty();
	}

	@Override
	public boolean isInProgress() {
		if ((getStatus() == WorkflowConstants.STATUS_DRAFT) ||
			(getStatus() == WorkflowConstants.STATUS_INCOMPLETE)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isProduction() {
		if (CTConstants.CT_COLLECTION_ID_PRODUCTION == getCtCollectionId()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isReadOnly() {
		if (!isInProgress() &&
			(getStatus() != WorkflowConstants.STATUS_PENDING)) {

			return true;
		}

		return false;
	}

	private static final Snapshot<CTScoreCache> _ctScoreCacheSnapshot =
		new Snapshot<>(CTCollectionImpl.class, CTScoreCache.class);
	private static final Snapshot<CTScoreSizeClassificationCache>
		_ctScoreSizeClassificationCacheSnapshot = new Snapshot<>(
			CTCollectionImpl.class, CTScoreSizeClassificationCache.class);

}