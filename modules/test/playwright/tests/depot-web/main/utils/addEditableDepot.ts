/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';

export default async function addEditableDepot(apiHelpers: DataApiHelpers) {

	// The General form cannot be saved for a headless asset library
	// (LPD-109404)

	const name = getRandomString();

	const depot = await apiHelpers.jsonWebServicesDepot.addDepotEntry(name);

	// Delete the depot through the fixture, also when the test times out

	const group = await apiHelpers.jsonWebServicesGroup.getGroup(
		String(depot.groupId)
	);

	apiHelpers.data.push({
		id: group.externalReferenceCode,
		type: 'assetLibrary',
	});

	return {
		...depot,
		externalReferenceCode: group.externalReferenceCode,
		name,
	};
}
