/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

export interface ObjectField {
	businessType: string;
	name: string;
	readOnly: string;
	required: boolean;
}

async function getObjectFields(
	externalReferenceCode: string
): Promise<{items: ObjectField[]}> {
	const response = await fetch(
		`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${externalReferenceCode}/object-fields?fields=businessType,name,readOnly,required&pageSize=100`
	);

	if (!response.ok) {
		throw new Error('Failed to fetch object fields.');
	}

	return response.json();
}

export {getObjectFields};
