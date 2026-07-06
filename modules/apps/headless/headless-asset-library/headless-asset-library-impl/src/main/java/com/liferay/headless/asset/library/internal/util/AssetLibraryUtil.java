/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class AssetLibraryUtil {

	public static AssetLibrary.Type getAssetLibraryType(int depotEntryType) {
		if (depotEntryType == DepotConstants.TYPE_ASSET_LIBRARY) {
			return AssetLibrary.Type.ASSET_LIBRARY;
		}
		else if (depotEntryType == DepotConstants.TYPE_DESIGN_LIBRARY) {
			return AssetLibrary.Type.DESIGN_LIBRARY;
		}
		else if (depotEntryType == DepotConstants.TYPE_PROJECT) {
			return AssetLibrary.Type.PROJECT;
		}

		return AssetLibrary.Type.SPACE;
	}

	public static int getDepotEntryType(AssetLibrary.Type assetLibraryType) {
		if (Objects.equals(assetLibraryType, AssetLibrary.Type.ASSET_LIBRARY)) {
			return DepotConstants.TYPE_ASSET_LIBRARY;
		}
		else if (Objects.equals(
					assetLibraryType, AssetLibrary.Type.DESIGN_LIBRARY)) {

			return DepotConstants.TYPE_DESIGN_LIBRARY;
		}
		else if (Objects.equals(assetLibraryType, AssetLibrary.Type.PROJECT)) {
			return DepotConstants.TYPE_PROJECT;
		}

		return DepotConstants.TYPE_SPACE;
	}

	public static int getDepotEntryType(String assetLibraryTypeString) {
		return _depotEntryTypes.get(
			StringUtil.toLowerCase(assetLibraryTypeString));
	}

	private static final Map<String, Integer> _depotEntryTypes =
		HashMapBuilder.put(
			StringUtil.toLowerCase(AssetLibrary.Type.ASSET_LIBRARY.getValue()),
			getDepotEntryType(AssetLibrary.Type.ASSET_LIBRARY)
		).put(
			StringUtil.toLowerCase(AssetLibrary.Type.DESIGN_LIBRARY.getValue()),
			getDepotEntryType(AssetLibrary.Type.DESIGN_LIBRARY)
		).put(
			StringUtil.toLowerCase(AssetLibrary.Type.PROJECT.getValue()),
			getDepotEntryType(AssetLibrary.Type.PROJECT)
		).put(
			StringUtil.toLowerCase(AssetLibrary.Type.SPACE.getValue()),
			getDepotEntryType(AssetLibrary.Type.SPACE)
		).build();

}