/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.service.base.AssetListEntryAudienceEntryRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = AopService.class)
@JSONWebService(mode = JSONWebServiceMode.IGNORE)
public class AssetListEntryAudienceEntryRelServiceImpl
	extends AssetListEntryAudienceEntryRelServiceBaseImpl {

	@Override
	public void deleteAssetListEntryAudienceEntryRel(
			long assetListEntryId, String audienceEntryERC)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(), assetListEntryId, ActionKeys.UPDATE);

		assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRel(
				assetListEntryId, audienceEntryERC);
	}

	@Override
	public void updatePriorities(long[] assetListEntryAudienceEntryRelIds)
		throws PortalException {

		for (long assetListEntryAudienceEntryRelId :
				assetListEntryAudienceEntryRelIds) {

			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
				assetListEntryAudienceEntryRelPersistence.findByPrimaryKey(
					assetListEntryAudienceEntryRelId);

			_assetListEntryModelResourcePermission.check(
				getPermissionChecker(),
				assetListEntryAudienceEntryRel.getAssetListEntryId(),
				ActionKeys.UPDATE);
		}

		assetListEntryAudienceEntryRelLocalService.updatePriorities(
			assetListEntryAudienceEntryRelIds);
	}

	@Reference(
		target = "(model.class.name=com.liferay.asset.list.model.AssetListEntry)"
	)
	private ModelResourcePermission<AssetListEntry>
		_assetListEntryModelResourcePermission;

}