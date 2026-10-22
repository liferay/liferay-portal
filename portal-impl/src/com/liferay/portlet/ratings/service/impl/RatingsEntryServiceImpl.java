/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.ratings.service.impl;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portlet.asset.service.permission.AssetEntryPermission;
import com.liferay.portlet.ratings.service.base.RatingsEntryServiceBaseImpl;
import com.liferay.ratings.kernel.model.RatingsEntry;

/**
 * @author Brian Wing Shun Chan
 */
public class RatingsEntryServiceImpl extends RatingsEntryServiceBaseImpl {

	@Override
	public void deleteEntry(String className, long classPK)
		throws PortalException {

		_checkViewPermission(className, classPK);

		ratingsEntryLocalService.deleteEntry(getUserId(), className, classPK);
	}

	@Override
	public RatingsEntry updateEntry(
			String className, long classPK, double score)
		throws PortalException {

		_checkViewPermission(className, classPK);

		return ratingsEntryLocalService.updateEntry(
			getUserId(), className, classPK, score, new ServiceContext());
	}

	private void _checkViewPermission(String className, long classPK)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (className.equals("com.liferay.message.boards.model.MBDiscussion")) {
			DiscussionPermission discussionPermission =
				_discussionPermissionSnapshot.get();

			if ((discussionPermission != null) &&
				discussionPermission.hasPermission(
					permissionChecker, classPK, ActionKeys.VIEW)) {

				return;
			}

			throw new PrincipalException.MustHavePermission(
				permissionChecker, className, classPK, ActionKeys.VIEW);
		}

		ModelResourcePermission<ClassedModel> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				className);

		if (modelResourcePermission != null) {
			modelResourcePermission.check(
				permissionChecker, classPK, ActionKeys.VIEW);

			return;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory != null) {
			AssetEntryPermission.check(
				permissionChecker, className, classPK, ActionKeys.VIEW);

			return;
		}

		throw new PrincipalException.MustHavePermission(
			permissionChecker, className, classPK, ActionKeys.VIEW);
	}

	private static final Snapshot<DiscussionPermission>
		_discussionPermissionSnapshot = new Snapshot<>(
			RatingsEntryServiceImpl.class, DiscussionPermission.class);

}