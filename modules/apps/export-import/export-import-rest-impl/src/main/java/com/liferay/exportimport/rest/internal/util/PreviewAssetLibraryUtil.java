/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.exportimport.rest.dto.v1_0.PreviewAssetLibrary;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Alejandro Tardín
 */
public class PreviewAssetLibraryUtil {

	public static int toDepotEntryType(String value) {
		if (StringUtil.equalsIgnoreCase(
				value, PreviewAssetLibrary.Type.ASSET_LIBRARY.getValue())) {

			return DepotConstants.TYPE_ASSET_LIBRARY;
		}
		else if (StringUtil.equalsIgnoreCase(
					value,
					PreviewAssetLibrary.Type.DESIGN_LIBRARY.getValue())) {

			return DepotConstants.TYPE_DESIGN_LIBRARY;
		}
		else if (StringUtil.equalsIgnoreCase(
					value, PreviewAssetLibrary.Type.PROJECT.getValue())) {

			return DepotConstants.TYPE_PROJECT;
		}
		else if (StringUtil.equalsIgnoreCase(
					value, PreviewAssetLibrary.Type.SPACE.getValue())) {

			return DepotConstants.TYPE_SPACE;
		}

		return DepotConstants.TYPE_ANY;
	}

	public static PreviewAssetLibrary.Type toType(int depotEntryType) {
		if (depotEntryType == DepotConstants.TYPE_ASSET_LIBRARY) {
			return PreviewAssetLibrary.Type.ASSET_LIBRARY;
		}
		else if (depotEntryType == DepotConstants.TYPE_DESIGN_LIBRARY) {
			return PreviewAssetLibrary.Type.DESIGN_LIBRARY;
		}
		else if (depotEntryType == DepotConstants.TYPE_PROJECT) {
			return PreviewAssetLibrary.Type.PROJECT;
		}
		else if (depotEntryType == DepotConstants.TYPE_SPACE) {
			return PreviewAssetLibrary.Type.SPACE;
		}

		return null;
	}

}