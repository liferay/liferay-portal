/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';

export default async function addConnectedDepot(
	apiHelpers: DataApiHelpers,
	site: Site,
	{ddmStructuresAvailable = false}: {ddmStructuresAvailable?: boolean} = {}
) {
	const assetLibrary =
		await apiHelpers.headlessAssetLibrary.createAssetLibrary();

	await apiHelpers.headlessAssetLibrary.connectSite(
		assetLibrary.externalReferenceCode,
		site.externalReferenceCode
	);

	if (ddmStructuresAvailable) {
		const depotEntryGroupRel =
			await apiHelpers.jsonWebServicesDepotGroupRel.getDepotEntryGroupRelByDepotEntryIdToGroupId(
				String(assetLibrary.id),
				String(site.id)
			);

		await apiHelpers.jsonWebServicesDepotGroupRel.updateDDMStructuresAvailable(
			String(depotEntryGroupRel.depotEntryGroupRelId),
			true
		);
	}

	return assetLibrary;
}
