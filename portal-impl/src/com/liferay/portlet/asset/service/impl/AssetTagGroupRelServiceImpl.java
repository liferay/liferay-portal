/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portlet.asset.service.base.AssetTagGroupRelServiceBaseImpl;
import com.liferay.portlet.asset.service.permission.AssetTagsPermission;

import java.util.List;

/**
 * @author Gislayne Vitorino
 */
public class AssetTagGroupRelServiceImpl
	extends AssetTagGroupRelServiceBaseImpl {

	@Override
	public AssetTagGroupRel addAssetTagGroupRel(long groupId, long tagId)
		throws PortalException {

		AssetTagsPermission.check(
			getPermissionChecker(), tagId, ActionKeys.UPDATE);

		return assetTagGroupRelLocalService.addAssetTagGroupRel(groupId, tagId);
	}

	@Override
	public List<AssetTagGroupRel> getAssetTagGroupRelsByTagId(long tagId)
		throws PortalException {

		AssetTagsPermission.check(
			getPermissionChecker(), tagId, ActionKeys.VIEW);

		return assetTagGroupRelPersistence.findByTagId(tagId);
	}

	@Override
	public void setAssetTagGroupRels(long tagId, long[] groupIds)
		throws PortalException {

		AssetTagsPermission.check(
			getPermissionChecker(), tagId, ActionKeys.UPDATE);

		assetTagGroupRelLocalService.setAssetTagGroupRels(tagId, groupIds);
	}

}