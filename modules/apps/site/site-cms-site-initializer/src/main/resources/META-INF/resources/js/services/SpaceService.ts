/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Space} from '../types/Space';
import ApiHelper from './ApiHelper';

async function addSpace({description, name}: {description?: string; name: string}) {
	return await ApiHelper.post<{id: number}>(
		'/o/headless-asset-library/v1.0/asset-libraries',
		{
			description,
			name,
		}
	);
}

async function getSpace(externalReferenceCode: string): Promise<Space> {
	const {data, error} = await ApiHelper.get<Space>(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function getSpaces(): Promise<Space[]> {
	const {data, error} = await ApiHelper.get<{items: Space[]}>(
		'/o/headless-asset-library/v1.0/asset-libraries'
	);

	if (data) {
		return data.items;
	}

	throw new Error(error);
}

export default {
	addSpace,
	getSpace,
	getSpaces,
};
