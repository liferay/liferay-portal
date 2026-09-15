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

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
@ProviderType
public interface LayoutStaging {

	public LayoutRevision getLayoutRevision(Layout layout);

	public LayoutSetBranch getLayoutSetBranch(LayoutSet layoutSet);

	public LayoutSetStagingHandler getLayoutSetStagingHandler(
		LayoutSet layoutSet);

	public LayoutStagingHandler getLayoutStagingHandler(Layout layout);

	public boolean isBranchingLayout(Layout layout);

	public boolean isBranchingLayoutSet(Group group, boolean privateLayout);

	public Layout mergeLayoutRevisionIntoLayout(Layout layout);

	public LayoutSet mergeLayoutSetRevisionIntoLayoutSet(LayoutSet layoutSet);

	public boolean prepareLayoutStagingHandler(
		PortletDataContext portletDataContext, Layout layout);

}