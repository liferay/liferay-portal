/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the remote service utility for AssetListEntryAudienceEntryRel. This utility wraps
 * <code>com.liferay.asset.list.service.impl.AssetListEntryAudienceEntryRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAudienceEntryRelService
 * @generated
 */
public class AssetListEntryAudienceEntryRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.list.service.impl.AssetListEntryAudienceEntryRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void deleteAssetListEntryAudienceEntryRel(
			long assetListEntryId, String audienceEntryERC)
		throws PortalException {

		getService().deleteAssetListEntryAudienceEntryRel(
			assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static void updatePriorities(
			long[] assetListEntryAudienceEntryRelIds)
		throws PortalException {

		getService().updatePriorities(assetListEntryAudienceEntryRelIds);
	}

	public static AssetListEntryAudienceEntryRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetListEntryAudienceEntryRelService>
		_serviceSnapshot = new Snapshot<>(
			AssetListEntryAudienceEntryRelServiceUtil.class,
			AssetListEntryAudienceEntryRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-901801813