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
	const requests = [];

	if (groupId) {
		requests.push(
			getItems(
				`/o/data-engine/v2.0/sites/${groupId}/data-definitions/by-content-type/${contentType}`,
				keywords,
				{signal}
			)
		);
	}

	if (groupId !== themeDisplay.getCompanyGroupId()) {
		requests.push(
			getItems(
				`/o/data-engine/v2.0/data-definitions/by-content-type/${contentType}`,
				keywords,
				{signal}
			)
		);
	}

	const results = await Promise.allSettled(requests);

	const rejectedResults = results.filter(
		({status}) => status === 'rejected'
	);

	if (rejectedResults.length === results.length) {
		throw rejectedResults[0].reason;
	}

	const items = [];

	results.forEach((result) => {
		if (result.status === 'fulfilled') {
			items.push(...result.value);
		}
	});

	return items.filter(({id}) => id !== parseInt(dataDefinitionId, 10));
}
