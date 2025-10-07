/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../../utils/getRandomInt';

export async function postListTypeDefinitionListTypeEntries({
	apiHelpers,
	listTypeEntriesLength = 4,
	locale,
}: {
	apiHelpers: DataApiHelpers;
	listTypeEntriesLength?: number;
	locale?: Locale;
}): Promise<{
	listTypeDefinition: ListTypeDefinition;
	listTypeEntries: ListTypeEntry[];
}> {
	const listTypeDefinition =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	apiHelpers.data.push({
		id: listTypeDefinition.id,
		type: 'listTypeEntries',
	});

	const listTypeEntries: LocalizedValue<string>[] = Array.from(
		{length: listTypeEntriesLength},
		() => {
			const entry: LocalizedValue<string> = {
				en_US: getRandomInt().toString(),
			};

			if (locale) {
				entry[locale] = getRandomInt().toString();
			}

			return entry;
		}
	);

	const listTypeEntry = listTypeEntries.map(
		async (listTypeDefinitionEntry) =>
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: listTypeDefinitionEntry.en_US,
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: listTypeDefinitionEntry,
			})
	);

	const promiseResolvedListTypeEntries = await Promise.all(listTypeEntry);

	return {
		listTypeDefinition,
		listTypeEntries: promiseResolvedListTypeEntries,
	};
}
