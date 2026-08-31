/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getItems} from '../utils/client.es';

export async function fetchFieldSets({
	contentType,
	dataDefinitionId,
	groupId,
	keywords,
	signal,
}) {
	const siteFieldSetsPromise = groupId
		? getItems(
				`/o/data-engine/v2.0/sites/${groupId}/data-definitions/by-content-type/${contentType}`,
				keywords,
				{signal}
			)
		: Promise.resolve([]);

	const companyFieldSetsPromise =
		groupId === themeDisplay.getCompanyGroupId()
			? Promise.resolve([])
			: getItems(
					`/o/data-engine/v2.0/data-definitions/by-content-type/${contentType}`,
					keywords,
					{signal}
				);

	const [siteFieldSets, companyFieldSets] = await Promise.all([
		siteFieldSetsPromise,
		companyFieldSetsPromise,
	]);

	return [...siteFieldSets, ...companyFieldSets].filter(
		({id}) => id !== parseInt(dataDefinitionId, 10)
	);
}
