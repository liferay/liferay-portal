/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exportimport.staged.model.repository;

import com.liferay.layout.set.model.adapter.StagedLayoutSet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;

/**
 * @author João Victor Alves
 */
public class StagedLayoutSetStagedModelRepositoryUtil {

	public static StagedLayoutSet fetchExistingLayoutSet(
		long groupId, boolean privateLayout) {

		StagedLayoutSet stagedLayoutSet = null;

		try {
			LayoutSetLocalService layoutSetLocalService =
				_layoutSetLocalServiceSnapshot.get();

			stagedLayoutSet = ModelAdapterUtil.adapt(
				layoutSetLocalService.getLayoutSet(groupId, privateLayout),
				LayoutSet.class, StagedLayoutSet.class);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return stagedLayoutSet;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedLayoutSetStagedModelRepositoryUtil.class);

	private static final Snapshot<LayoutSetLocalService>
		_layoutSetLocalServiceSnapshot = new Snapshot<>(
			StagedLayoutSetStagedModelRepositoryUtil.class,
			LayoutSetLocalService.class);

}