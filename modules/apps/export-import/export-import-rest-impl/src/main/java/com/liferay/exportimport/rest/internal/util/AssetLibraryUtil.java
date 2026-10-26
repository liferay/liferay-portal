/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.exportimport.rest.dto.v1_0.AssetLibrary;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.ws.rs.BadRequestException;

/**
 * @author Alejandro Tardín
 */
public class AssetLibraryUtil {

	public static String[] getExternalReferenceCodes(
		AssetLibrary[] assetLibraries, long companyId) {

		String[] externalReferenceCodes = TransformUtil.transform(
			assetLibraries, AssetLibrary::getExternalReferenceCode,
			String.class);

		if (ArrayUtil.isNotEmpty(externalReferenceCodes) &&
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-41307")) {

			throw new BadRequestException(
				"Asset libraries require the feature flag \"LPD-41307\"");
		}

		return externalReferenceCodes;
	}

	public static int toDepotEntryType(String value) {
		if (StringUtil.equalsIgnoreCase(
				value, AssetLibrary.Type.ASSET_LIBRARY.getValue())) {

			return DepotConstants.TYPE_ASSET_LIBRARY;
		}
		else if (StringUtil.equalsIgnoreCase(
					value, AssetLibrary.Type.DESIGN_LIBRARY.getValue())) {

			return DepotConstants.TYPE_DESIGN_LIBRARY;
		}
		else if (StringUtil.equalsIgnoreCase(
					value, AssetLibrary.Type.PROJECT.getValue())) {

			return DepotConstants.TYPE_PROJECT;
		}
		else if (StringUtil.equalsIgnoreCase(
					value, AssetLibrary.Type.SPACE.getValue())) {

			return DepotConstants.TYPE_SPACE;
		}

		return DepotConstants.TYPE_ANY;
	}

	public static AssetLibrary.Type toType(int depotEntryType) {
		if (depotEntryType == DepotConstants.TYPE_ASSET_LIBRARY) {
			return AssetLibrary.Type.ASSET_LIBRARY;
		}
		else if (depotEntryType == DepotConstants.TYPE_DESIGN_LIBRARY) {
			return AssetLibrary.Type.DESIGN_LIBRARY;
		}
		else if (depotEntryType == DepotConstants.TYPE_PROJECT) {
			return AssetLibrary.Type.PROJECT;
		}
		else if (depotEntryType == DepotConstants.TYPE_SPACE) {
			return AssetLibrary.Type.SPACE;
		}

		return null;
	}

}