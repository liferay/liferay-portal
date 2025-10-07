/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Options, Picklist} from '../../common/types/Picklist';
import ApiHelper from './ApiHelper';

async function createPicklist({
	erc,
	name: name_i18n,
	options,
}: {
	erc: string;
	name: Liferay.Language.LocalizedValue<string>;
	options?: Options;
}) {
	return await ApiHelper.post<Picklist>(
		`/o/headless-admin-list-type/v1.0/list-type-definitions`,
		{
			externalReferenceCode: erc,
			name_i18n,
			...(options && {
				listTypeEntries: normalizeOptions(options),
			}),
		}
	);
}

async function getPicklists(): Promise<Picklist[]> {
	const {data, error} = await ApiHelper.get<{items: Picklist[]}>(
		'/o/headless-admin-list-type/v1.0/list-type-definitions'
	);

	if (data) {
		return data.items;
	}

	throw new Error(error);
}

async function updatePicklist({
	erc,
	id,
	name: name_i18n,
	options,
}: {
	erc?: string;
	id: number;
	name?: Liferay.Language.LocalizedValue<string>;
	options?: Options;
}) {
	return await ApiHelper.put(
		`/o/headless-admin-list-type/v1.0/list-type-definitions/${id}`,
		{
			externalReferenceCode: erc,
			name_i18n,
			...(options && {
				listTypeEntries: normalizeOptions(options),
			}),
		}
	);
}

function normalizeOptions(options: Options) {
	return [...options].map(([erc, value]) => ({
		externalReferenceCode: erc,
		key: value.key,
		name_i18n: value.name,
	}));
}

export default {
	createPicklist,
	getPicklists,
	updatePicklist,
};
