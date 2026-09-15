/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchPersistence;
import com.liferay.portal.service.base.LayoutRevisionServiceBaseImpl;

/**
 * @author Raymond Augé
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutRevisionServiceImpl extends LayoutRevisionServiceBaseImpl {

	@Override
	public LayoutRevision addLayoutRevision(
			long userId, long layoutSetBranchId, long layoutBranchId,
			long parentLayoutRevisionId, boolean head, long plid,
			long portletPreferencesPlid, boolean privateLayout, String name,
			String title, String description, String keywords, String robots,
			String typeSettings, boolean iconImage, long iconImageId,
			String themeId, String colorSchemeId, String css,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchPersistence.findByPrimaryKey(layoutSetBranchId);

		GroupPermissionUtil.check(
			getPermissionChecker(), layoutSetBranch.getGroupId(),
			ActionKeys.ADD_LAYOUT_BRANCH);

		return layoutRevisionLocalService.addLayoutRevision(
			userId, layoutSetBranchId, layoutBranchId, parentLayoutRevisionId,
			head, plid, portletPreferencesPlid, privateLayout, name, title,
			description, keywords, robots, typeSettings, iconImage, iconImageId,
			themeId, colorSchemeId, css, serviceContext);
	}

	@BeanReference(type = LayoutSetBranchPersistence.class)
	private LayoutSetBranchPersistence _layoutSetBranchPersistence;

}