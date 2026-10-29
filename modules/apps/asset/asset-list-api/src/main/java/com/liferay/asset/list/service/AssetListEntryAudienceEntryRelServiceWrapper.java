/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AssetListEntryAudienceEntryRelService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAudienceEntryRelService
 * @generated
 */
public class AssetListEntryAudienceEntryRelServiceWrapper
	implements AssetListEntryAudienceEntryRelService,
			   ServiceWrapper<AssetListEntryAudienceEntryRelService> {

	public AssetListEntryAudienceEntryRelServiceWrapper() {
		this(null);
	}

	public AssetListEntryAudienceEntryRelServiceWrapper(
		AssetListEntryAudienceEntryRelService
			assetListEntryAudienceEntryRelService) {

		_assetListEntryAudienceEntryRelService =
			assetListEntryAudienceEntryRelService;
	}

	@Override
	public void deleteAssetListEntryAudienceEntryRel(
			long assetListEntryId, String audienceEntryERC)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryAudienceEntryRelService.
			deleteAssetListEntryAudienceEntryRel(
				assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetListEntryAudienceEntryRelService.
			getOSGiServiceIdentifier();
	}

	@Override
	public void updatePriorities(long[] assetListEntryAudienceEntryRelIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryAudienceEntryRelService.updatePriorities(
			assetListEntryAudienceEntryRelIds);
	}

	@Override
	public AssetListEntryAudienceEntryRelService getWrappedService() {
		return _assetListEntryAudienceEntryRelService;
	}

	@Override
	public void setWrappedService(
		AssetListEntryAudienceEntryRelService
			assetListEntryAudienceEntryRelService) {

		_assetListEntryAudienceEntryRelService =
			assetListEntryAudienceEntryRelService;
	}

	private AssetListEntryAudienceEntryRelService
		_assetListEntryAudienceEntryRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1614755991