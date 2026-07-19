/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutStagingHandler;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutRevisionPersistence;
import com.liferay.portlet.exportimport.staging.StagingAdvicesThreadLocal;

/**
 * @author Shuyang Zhou
 */
public class PortletPreferencesPlidUtil {

	public static LayoutRevision getLayoutRevision(
		long plid, LayoutRevisionPersistence layoutRevisionPersistence,
		LayoutPersistence layoutPersistence) {

		if (plid <= 0) {
			return null;
		}

		LayoutRevision layoutRevision =
			layoutRevisionPersistence.fetchByPrimaryKey(plid);

		if (layoutRevision != null) {
			return layoutRevision;
		}

		Layout layout = layoutPersistence.fetchByPrimaryKey(plid);

		if (layout == null) {
			return null;
		}

		if (LayoutStagingUtil.isBranchingLayout(layout)) {
			LayoutStagingHandler layoutStagingHandler =
				new LayoutStagingHandler(layout);

			return layoutStagingHandler.getLayoutRevision();
		}

		return null;
	}

	public static long swapPlidForPortletPreferences(
		long plid, LayoutRevisionPersistence layoutRevisionPersistence,
		LayoutPersistence layoutPersistence) {

		if (!StagingAdvicesThreadLocal.isEnabled()) {
			return plid;
		}

		LayoutRevision layoutRevision = getLayoutRevision(
			plid, layoutRevisionPersistence, layoutPersistence);

		if (layoutRevision == null) {
			return plid;
		}

		return layoutRevision.getLayoutRevisionId();
	}

}