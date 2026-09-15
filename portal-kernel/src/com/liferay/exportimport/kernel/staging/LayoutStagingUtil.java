/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.staging;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetStagingHandler;
import com.liferay.portal.kernel.model.LayoutStagingHandler;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Raymond Augé
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutStagingUtil {

	public static LayoutRevision getLayoutRevision(Layout layout) {
		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.getLayoutRevision(layout);
	}

	public static LayoutSetBranch getLayoutSetBranch(LayoutSet layoutSet) {
		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.getLayoutSetBranch(layoutSet);
	}

	public static LayoutSetStagingHandler getLayoutSetStagingHandler(
		LayoutSet layoutSet) {

		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.getLayoutSetStagingHandler(layoutSet);
	}

	public static LayoutStagingHandler getLayoutStagingHandler(Layout layout) {
		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.getLayoutStagingHandler(layout);
	}

	public static boolean isBranchingLayout(Layout layout) {
		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.isBranchingLayout(layout);
	}

	public static boolean isBranchingLayoutSet(
		Group group, boolean privateLayout) {

		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.isBranchingLayoutSet(group, privateLayout);
	}

	public static Layout mergeLayoutRevisionIntoLayout(Layout layout) {
		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.mergeLayoutRevisionIntoLayout(layout);
	}

	public static LayoutSet mergeLayoutSetRevisionIntoLayoutSet(
		LayoutSet layoutSet) {

		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.mergeLayoutSetRevisionIntoLayoutSet(layoutSet);
	}

	public static boolean prepareLayoutStagingHandler(
		PortletDataContext portletDataContext, Layout layout) {

		LayoutStaging layoutStaging = _layoutStagingSnapshot.get();

		return layoutStaging.prepareLayoutStagingHandler(
			portletDataContext, layout);
	}

	private static final Snapshot<LayoutStaging> _layoutStagingSnapshot =
		new Snapshot<>(LayoutStagingUtil.class, LayoutStaging.class);

}