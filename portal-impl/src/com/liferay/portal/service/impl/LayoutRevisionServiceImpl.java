/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.NoSuchLayoutRevisionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
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

		FeatureFlagManagerUtil.checkEnabled(
			serviceContext.getCompanyId(), "LPD-105778");

		_checkPermissions(layoutSetBranchId, plid);

		_checkLayoutRevision(
			parentLayoutRevisionId, plid, portletPreferencesPlid);

		return layoutRevisionLocalService.addLayoutRevision(
			getUserId(), layoutSetBranchId, layoutBranchId,
			parentLayoutRevisionId, head, plid, portletPreferencesPlid,
			privateLayout, name, title, description, keywords, robots,
			typeSettings, iconImage, iconImageId, themeId, colorSchemeId, css,
			serviceContext);
	}

	private void _checkLayoutRevision(long layoutRevisionId, long plid)
		throws NoSuchLayoutRevisionException {

		LayoutRevision layoutRevision =
			layoutRevisionPersistence.fetchByPrimaryKey(layoutRevisionId);

		if ((layoutRevision == null) || (layoutRevision.getPlid() != plid)) {
			throw new NoSuchLayoutRevisionException(
				StringBundler.concat(
					"No layout revision exists with the primary key ",
					layoutRevisionId, " for layout ", plid));
		}
	}

	private void _checkLayoutRevision(
			long parentLayoutRevisionId, long plid, long portletPreferencesPlid)
		throws PortalException {

		if (parentLayoutRevisionId > 0) {
			_checkLayoutRevision(parentLayoutRevisionId, plid);
		}

		if ((portletPreferencesPlid == LayoutConstants.DEFAULT_PLID) ||
			(portletPreferencesPlid == plid)) {

			return;
		}

		_checkLayoutRevision(portletPreferencesPlid, plid);
	}

	private void _checkPermissions(long layoutSetBranchId, long plid)
		throws PortalException {

		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchPersistence.findByPrimaryKey(layoutSetBranchId);

		GroupPermissionUtil.check(
			getPermissionChecker(), layoutSetBranch.getGroupId(),
			ActionKeys.ADD_LAYOUT_BRANCH);

		Layout layout = _layoutPersistence.findByPrimaryKey(plid);

		if ((layout.getGroupId() != layoutSetBranch.getGroupId()) ||
			(layout.isPrivateLayout() != layoutSetBranch.isPrivateLayout())) {

			throw new PrincipalException.MustHavePermission(
				getPermissionChecker(), Layout.class.getName(), plid,
				ActionKeys.UPDATE);
		}

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.UPDATE);
	}

	@BeanReference(type = LayoutPersistence.class)
	private LayoutPersistence _layoutPersistence;

	@BeanReference(type = LayoutSetBranchPersistence.class)
	private LayoutSetBranchPersistence _layoutSetBranchPersistence;

}