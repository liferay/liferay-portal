/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from '../../../helpers/ApiHelpers';

export default async function deleteSiteAndLayoutSetPrototypes(
	apiHelpers: ApiHelpers,
	siteId: string,
	...layoutSetPrototypeIds: string[]
) {
	let response = await apiHelpers.headlessSite.deleteSite(siteId);
	if (!response.ok()) {
		response = await apiHelpers.headlessSite.deleteSite(siteId);
	}
	expect(response.ok()).toBe(true);
	for (const prototypeId of layoutSetPrototypeIds) {
		await apiHelpers.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
			prototypeId
		);
	}
}
