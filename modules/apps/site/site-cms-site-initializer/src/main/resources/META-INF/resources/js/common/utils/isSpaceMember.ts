/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AssetLibrary} from '../types/AssetLibrary';
import {getScopeExternalReferenceCode} from './getScopeExternalReferenceCode';

/**
 * Checks whether the current user is a member of the item's Space. The asset
 * libraries list holds the Spaces the user is a member of (every Space for
 * CMS administrators), so an item scoped to a Space outside the list fails
 * the check. An item without Space data passes.
 */
export function isSpaceMember(
	assetLibraries: AssetLibrary[] | undefined,
	itemData: any
): boolean {
	const scopeExternalReferenceCode = getScopeExternalReferenceCode(itemData);

	if (!assetLibraries || !scopeExternalReferenceCode) {
		return true;
	}

	return assetLibraries.some(
		(assetLibrary) =>
			assetLibrary.externalReferenceCode === scopeExternalReferenceCode
	);
}
