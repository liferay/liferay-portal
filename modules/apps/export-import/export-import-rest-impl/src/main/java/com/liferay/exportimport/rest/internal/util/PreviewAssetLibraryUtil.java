/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.exportimport.rest.dto.v1_0.PreviewAssetLibrary;

/**
 * @author Alejandro Tardín
 */
public class PreviewAssetLibraryUtil {

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